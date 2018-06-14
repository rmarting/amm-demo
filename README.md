# Introduction

This repository includes a set of applications to demostrate how it could be migrated and modernizate
a JEE application into OpenShift.

The main topics implemented are:

* Use of [thorntail.io](http://thorntail.io) to package an JEE as an Uber JAR
* Use of [Maven Fabric8 Plug-In](https://maven.fabric8.io/) to build and deploy an image into OpenShift
* Use of ActiveMQ Artemis to deploy a Message Broker on OpenShift
* Use of Apache Camel and Spring Boot to create integration between different applications

# Deploy Minishift

[Minishift](https://docs.openshift.org/latest/minishift/index.html) is a tool that helps you run OpenShift locally
by running a single-node OpenShift cluster inside a VM.

Follow the [Installation instructions](https://docs.openshift.org/latest/minishift/getting-started/installing.html) for you local OS.

There are several options to usage Minishift described [here](https://docs.openshift.org/latest/minishift/using/basic-usage.html). Take a moment to review it.

[Minishift Profiles](https://docs.openshift.org/latest/minishift/using/profiles.html) is good choice when you
want to isolate different OpenShift clusters. A profile is an instance of the Minishift VM along with all of its configuration and state. 

Each Minishift profile is created with its own configuration (memory, CPU, disk size, add-ons, and so on) and is independent of other profiles. 

To deploy this AMM Demo it is recommended to use a new profile, other wise to could do with the default profile.

	$ minishift profile set amm-demo
	Profile 'amm-demo' set as active profile.
	$ minishift config set cpus 2
	No Minishift instance exists. New 'cpus' setting will be applied on next 'minishift start'
	$ minishift config set memory 6GB 
	No Minishift instance exists. New 'memory' setting will be applied on next 'minishift start'
	$ minishift start
	-- Starting profile 'amm-demo'
	-- Checking if https://github.com is reachable ... OK
	-- Checking if requested OpenShift version 'v3.9.0' is valid ... OK
	-- Checking if requested OpenShift version 'v3.9.0' is supported ... OK
	-- Checking if requested hypervisor 'kvm' is supported on this platform ... OK
	-- Checking if KVM driver is installed ... 
	   Driver is available at /usr/local/bin/docker-machine-driver-kvm ... 
	   Checking driver binary is executable ... OK
	-- Checking if Libvirt is installed ... OK
	-- Checking if Libvirt default network is present ... OK
	-- Checking if Libvirt default network is active ... OK
	-- Checking the ISO URL ... OK
	-- Checking if provided oc flags are supported ... OK
	-- Starting local OpenShift cluster using 'kvm' hypervisor ...
	-- Minishift VM will be configured with ...
	   Memory:    6 GB
	   vCPUs :    2
	   Disk size: 20 GB
	-- Starting Minishift VM .......................... OK
	-- Checking for IP address ... OK
	-- Checking for nameservers ... OK
	-- Checking if external host is reachable from the Minishift VM ... 
	   Pinging 8.8.8.8 ... OK
	-- Checking HTTP connectivity from the VM ... 
	   Retrieving http://minishift.io/index.html ... OK
	-- Checking if persistent storage volume is mounted ... OK
	-- Checking available disk space ... 0% used OK
	   Importing 'openshift/origin:v3.9.0' .............................. OK
	   Importing 'openshift/origin-docker-registry:v3.9.0' ..... OK
	   Importing 'openshift/origin-haproxy-router:v3.9.0' ........ OK
	-- OpenShift cluster will be configured with ...
	   Version: v3.9.0
	-- Copying oc binary from the OpenShift container image to VM . OK
	-- Starting OpenShift cluster .........................................................................
	Using Docker shared volumes for OpenShift volumes
	Using public hostname IP 192.168.42.47 as the host IP
	Using 192.168.42.112 as the server IP
	Starting OpenShift using openshift/origin:v3.9.0 ...
	OpenShift server started.
	
	The server is accessible via web console at:
	    https://192.168.42.47:8443
	
	You are logged in as:
	    User:     developer
	    Password: <any value>
	
	To login as administrator:
	    oc login -u system:admin
	
After Minishift is ready we will apply some [addons](https://docs.openshift.org/latest/minishift/using/addons.html) to improve some capabilites in our profile

	$ minishift addons apply xpaas --profile amm-demo
	-- Applying addon 'xpaas':....................................................................................................
	XPaaS OpenShift imagestream and templates installed
	See https://github.com/openshift/openshift-ansible/tree/master/roles/openshift_examples/files/examples/v3.7

Also we will apply *admin-user* addon to allow the user became cluster admin and deploy more images in *openshift* namespace 

	$ minishift addons apply admin-user --profile amm-demo
	-- Applying addon 'admin-user':..
	$ oc adm policy add-cluster-role-to-user cluster-admin developer --as system:admin
	cluster role "cluster-admin" added: "developer"

Update some of the Red Hat Middeware images loaded with *xpaas* addon to the latest version:

	$ oc replace -f https://raw.githubusercontent.com/jboss-fuse/application-templates/master/fis-image-streams.json -n openshift
	imagestream "jboss-fuse70-java-openshift" created
	imagestream "jboss-fuse70-karaf-openshift" created
	imagestream "jboss-fuse70-eap-openshift" created
	imagestream "jboss-fuse70-console" created
	Error from server (AlreadyExists): imagestreams "fis-java-openshift" already exists
	Error from server (AlreadyExists): imagestreams "fis-karaf-openshift" already exists

Now our Minishift is ready to deploy our AMM Demo. 

# Prepare the Environment

## Login and create AMM Demo Project

	$ oc login -u developer -p developer https://$(minishift ip):8443
	Login successful.
	
	You have one project on this server: "myproject"
	
	Using project "myproject".

Create the project to deploy the applications
	
	$ oc new-project amm-demo
	Now using project "amm-demo" on server "https://192.168.42.47:8443".
	
	You can add applications to this project with the 'new-app' command. For example, try:
	
	    oc new-app centos/ruby-22-centos7~https://github.com/openshift/ruby-ex.git
	
	to build a new example application in Ruby.

## Deploy MySQL

The JEE applications uses a MySQL database to storage some information. This database will be deployed in Minishift
using a template with a hook to execute some commands after the database is deployed. These hooks allow us to create the
database and load some initial values. 

### Create MySQL Template and Deploy a new Database

There is a application template in **openshift** folder:

	$ oc create -f openshift/01-template-mysql-persistent-with-init.json

Using this template we could deploy a new database instance to be used by the application:

	$ oc new-app --template=mysql-persistent-with-init -p MYSQL_USER=mysql 	-p MYSQL_PASSWORD=mysql -p MYSQL_DATABASE=catalogdb
	--> Deploying template "amm-demo/mysql-persistent-with-init" to project amm-demo
	
	     MySQL (Persistent)
	     ---------
	     MySQL database service, with persistent storage. For more information about using this template, including OpenShift considerations, see https://github.com/sclorg/mysql-container/blob/master/5.7/README.md.
	     
	     NOTE: Scaling to more than one replica is not supported. You must have persistent volumes available in your cluster to use this template.
	
	     The following service(s) have been created in your project: mysql.
	     
	            Username: mysql
	            Password: mysql
	       Database Name: catalogdb
	      Connection URL: mysql://mysql:3306/
	     
	     For more information about using this template, including OpenShift considerations, see https://github.com/sclorg/mysql-container/blob/master/5.7/README.md.
	
	     * With parameters:
	        * Memory Limit=512Mi
	        * Namespace=openshift
	        * Database Service Name=mysql
	        * MySQL Connection Username=mysql
	        * MySQL Connection Password=mysql
	        * MySQL root user Password=rn2CAsjmm16V7YkX # generated
	        * MySQL Database Name=catalogdb
	        * Volume Capacity=1Gi
	        * Version of MySQL Image=5.7
	
	--> Creating resources ...
	    secret "mysql" created
	    service "mysql" created
	    persistentvolumeclaim "mysql" created
	    deploymentconfig "mysql" created
	--> Success
	    Run 'oc status' to view your app.

Also there is a **deploy-mysql.sh** script in **openshift** folder to deploy it.

### Test MySQL Database

To test if the database is deployed and running successfully, the next commands will help you:

	$ export pod=$(oc get pod | grep mysql | awk '{print $1}')
	$ oc rsh $pod
	$ mysql -u $MYSQL_USER -p$MYSQL_PASSWORD -h $HOSTNAME $MYSQL_DATABASE

	mysql> connect catalogdb;
	Connection id:    1628
	Current database: catalogdb

	mysql> SELECT t.* FROM catalogdb.Catalog t;
	+------+-----------+---------------------------+-------+------------------+-----------------+---------+
	| id   | artist    | description               | price | publication_date | title           | version |
	+------+-----------+---------------------------+-------+------------------+-----------------+---------+
	| 1001 | ACDC      | Australian hard rock band |    15 | 1980-07-25       | Back in Black   |       1 |
	| 1002 | Abba      | Swedish pop music group   |    12 | 1976-10-11       | Arrival         |       1 |
	| 1003 | Coldplay  | British rock band         |    17 | 2008-07-12       | Viva la Vida    |       1 |
	| 1004 | U2        | Irish rock band           |    18 | 1987-03-09       | The Joshua Tree |       1 |
	| 1005 | Metallica | Heavy metal band          |    15 | 1991-08-12       | Black           |       1 |
	+------+-----------+---------------------------+-------+------------------+-----------------+---------+
	5 rows in set (0.00 sec)

# Migration Phase

## Deploy jee-web-application-wildfly-swarm

	$ cd jee-web-application-wildfly-swarm
	$ mvn clean fabric8:deploy -Popenshift

The original application implemented the next technologies:

* JPA: Manage entities from an external database
* MDB: Consume messages from message brokers
* Timer: Schedule some tasks

### Health & Monitoring

OpenShift monitors the applications using the Liveness Probe and Readiness Probe tests.

This dependency activate some url tests to identify the health of Wilfly.

	<!-- Liveness Probe and Readiness Probe -->
	<dependency>
		<groupId>org.wildfly.swarm</groupId>
		<artifactId>monitor</artifactId>
	</dependency>

To monitor the application we will use an url and we will include in **src/main/fabric8/deployment.yml** descriptor as:

	apiVersion: v1
	kind: Deployment
	metadata:
	  name: ${project.artifactId}
	spec:
	  template:
	    spec:
	      containers:
	        - readinessProbe:
	            httpGet:
	              path: /rest/catalogs
	              port: 8080
	              scheme: HTTP
	            failureThreshold: 3
	            initialDelaySeconds: 90
	            periodSeconds: 10
	            successThreshold: 1
	            timeoutSeconds: 1
	          livenessProbe:
	            httpGet:
	              path: /rest/catalogs
	              port: 8080
	              scheme: HTTP
	            failureThreshold: 2
	            initialDelaySeconds: 90
	            periodSeconds: 10
	            successThreshold: 1
	            timeoutSeconds: 1

These components will help us to define a zero-downtime rolling process.

# Modernization

To avoid that this monolithic application manages all the resources we will deploy: 

* New Messaging Services external to the application
* New Cloud Applications to manage the Messaging services and the Database
* Application will remove any action about manage the Messaging System and only consume data from database

## Deploy Messaging Systems

	$ oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default
	role "view" added: "system:serviceaccount:amm-demo:default"

	$ oc new-app --template=amq63-basic -p APPLICATION_NAME=amq-broker -p MQ_USERNAME=admin -p MQ_PASSWORD=admin -p MQ_PROTOCOL=openwire,amqp
	--> Deploying template "openshift/amq63-basic" to project amm-demo
	
	     Red Hat JBoss A-MQ 6.3 (Ephemeral, no SSL)
	     ---------
	     Application template for JBoss A-MQ brokers. These can be deployed as standalone or in a mesh. This template doesn't feature SSL support.
	
	     A new messaging service has been created in your project. It will handle the protocol(s) "openwire,amqp". The username/password for accessing the service is admin/admin.
	
	     * With parameters:
	        * Application Name=amq-broker
	        * A-MQ Protocols=openwire,amqp
	        * Queues=
	        * Topics=
	        * A-MQ Serializable Packages=
	        * A-MQ Username=admin
	        * A-MQ Password=admin
	        * A-MQ Mesh Discovery Type=dns
	        * A-MQ Storage Limit=100 gb
	        * Queue Memory Limit=
	        * ImageStream Namespace=openshift
	
	--> Creating resources ...
	    service "amq-broker-amq-amqp" created
	    service "amq-broker-amq-mqtt" created
	    service "amq-broker-amq-stomp" created
	    service "amq-broker-amq-tcp" created
	    service "amq-broker-amq-mesh" created
	    deploymentconfig "amq-broker-amq" created
	--> Success
	    Run 'oc status' to view your app.

### Modify Application

Develop the next changes to remove the old functions:

* Remove single task to send messages: Comment timer components in **org.rmarting.jee.bean.CatalogMessageTimer** class.
* Remove MDBs: Comment MDB annotations and interfaces in **org.rmarting.jee.ejb.CatalogMDB** class
* Remove Messaging Dependies in **pom.xml** file: resource-adapters, activemq-rar

## Build and deploy new applications

Develop and deploy new applications:

* **jms-producer-batch**: Application to create data and store into a queue in the Messaging Service
* **jms-consumer-(fuse7|apache-camel)**: Application to consume messages from the queue in the Messaging Service and insert as data in the database

### jms-producer-batch application

This application will produce a new message to be loaded into database using the Messaging Services

This application is developed as Java class.
	
	$ cd jms-batch-producer
	$ mvn clean package fabric8:build

Deploy CronJob to send messages

	$ oc create -f openshift/04-cronjob-jms-producer-batch.yaml
	cronjob "jms-producer-batch-cron-job" created

### jms-consumer application

This application will consume messages from the Messaging System and insert the message into the database

There are two different implementations:

* Spring Boot + Apache Camel + ActiveMQ: [jms-consumer-apache-camel](./jms-consumer-apache-camel) folder
* Fuse 7 + Apache Camel + Artemis: [jms-consumer-fuse7](./jms-consumer-fuse7) folder

In both cases the deployment is the same:

	$ cd jms-consumer-fuse7
	$ mvn clean fabric8:deploy -Popenshift -DskipTests

