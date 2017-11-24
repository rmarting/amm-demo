
# Create OCP Project

	$ oc new-project amm-demo

# Deploy MySQL

## Create MySQL Template

	$ oc create -f openshift/01-template-mysql-persistent-with-init.json

## Deploy new MySQL App

	$ oc new-app --template=mysql-persistent-with-init \
		-p MYSQL_USER=mysql \
    	-p MYSQL_PASSWORD=mysql \
    	-p MYSQL_DATABASE=catalogdb

## Test Deploy

	$ export pod=$(oc get pod | grep mysql | awk '{print $1}')
	$ oc rsh $pod
	$ mysql -u $MYSQL_USER -p$MYSQL_PASSWORD -h $HOSTNAME $MYSQL_DATABASE

	mysql> connect catalogdb;
	Connection id:    1628
	Current database: catalogdb

	mysql> SELECT t.* FROM catalogdb.Catalog t;
	ERROR 1146 (42S02): Table 'catalogdb.Catalog' doesn't exist

*NOTE*: As we haven't yet deployed the service, the Catalog DB hasn't been yet created by the Hibernate framework so this
      message is expected.
      Note also that there shouldn't be any spaces between the `-p` option and the password you provide to the `mysql` command as otherwise, this will fail.

# Deploy jee-web-application-wildfly-swarm

	$ cd jee-web-application-wildfly-swarm
	$ mvn clean package fabric8:deploy -Popenshift

# Deploy AMQ

	$ oc create -f openshift/02-jboss-images-streams.json -n openshift

	$ oc create -f openshift/03-template-amq63-basic.json

	$ oc new-app --template=amq63-basic \
		-p APPLICATION_NAME=amq-broker \
		-p MQ_USERNAME=admin \
		-p MQ_PASSWORD=admin \
		-p IMAGE_STREAM_NAMESPACE=openshift

# Modify App

* Remove single task to send messages
* Connect to the new service

# Build and deploy new applications

## jms-consumer-spring-boot
	
	$ cd jms-consumer-spring-boot
	$ mvn clean package fabric8:deploy -Popenshift -DskipTests
		
## jms-batch-producer
	
	$ cd jms-batch-producer
	$ mvn clean package fabric8:build

Deploy CronJob to send messages

	$ oc create -f openshift/04-cronjob-jms-producer-batch.yaml

TODO: Create a diagram with the final version of this application
