# Use Case - FU02 - Deploy Cloud Application to get messages from a Cloud Queue and save into a Database

## Description

This Use Case will show how read messages from a Cloud Queue and save the data
into a database. This application could be used to test the use cases
[AU01](../au01-amq-persistence), [AU03](../au03-amq-master-slave) and/or [AU05](../au05-amq-messages).

It will define a new application using the ​*fis-java-openshift​* image to implement it.

This application will have:

* ActiveMQ bean definition to connect with the Messaging System.
* Bean definition to connect with Database.
* Camel Route to process each message from queue and store into a table in the database.
* Optional: The application could manage the configuration using Environment Variables or Config Maps.

## Maven Project

### Configure A-MQ Broker Url

The use cases [AU01](../au01-amq-persistence), [AU03](../au03-amq-master-slave) and/or
[AU05](../au05-amq-messages) deploy different A-MQ brokers. This
use case will connect to someone but it is needed to get the service name used by OpenShift.

This command shows the services of some project:

```
$ oc get service -n au01-amq-persistence
NAME                        CLUSTER-IP       EXTERNAL-IP   PORT(S)     AGE
au01-amq-broker-amq-amqp    172.30.50.128    <none>        5672/TCP    16m
au01-amq-broker-amq-mqtt    172.30.51.69     <none>        1883/TCP    16m
au01-amq-broker-amq-stomp   172.30.118.253   <none>        61613/TCP   16m
au01-amq-broker-amq-tcp     172.30.95.70     <none>        61616/TCP   16m
```

The value of NAME column will be used to connect from this application.

Change the value of variable *activemq.service.host* in *src/main/resources/application.properties* file as:

```
activemq.service.host=au01-amq-broker-amq-tcp.au01-amq-persistence
```

**NOTE:** It is needed to include as suffix the name of the namespace where is
running the A-MQ broker.

### Configure Couchbase Url

TODO

### Building

The use case can be built with

```
mvn clean install
```    

### Running the example locally

The use case can be run locally using the following Maven goal:

```
mvn spring-boot:run
```

## OpenShift Application

Login into OpenShift with a granted user

```
$ oc login -u openshift-dev 10.1.2.2:8443
Authentication required for https://10.1.2.2:8443 (openshift)
Username: openshift-dev
Password:
Login successful.

You have access to the following projects and can switch between them with 'oc project <projectname>':

  * sample-project

Using project "sample-project".
```

Create a new namespace for this use case.

```
$ oc new-project fu02-amq-db --display-name="FU02 - AMQ and Database"
Now using project "fu02-amq-db" on server "https://10.1.2.2:8443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-22-centos7~https://github.com/openshift/ruby-ex.git

to build a new example application in Ruby.
```

## Deploy Couchbase


```
[rmarting@rhel7-localdomain ~/RH/Cloud/cdk/components/rhel/rhel-ose] oc new-app arungupta/couchbase
--> Found Docker image 70eafed (5 days old) from Docker Hub for "arungupta/couchbase"

    * An image stream will be created as "couchbase:latest" that will track this image
    * This image will be deployed in deployment config "couchbase"
    * Ports 11207/tcp, 11210/tcp, 11211/tcp, 18091/tcp, 18092/tcp, 18093/tcp, 8091/tcp, 8092/tcp, 8093/tcp, 8094/tcp will be load balanced by service "couchbase"
      * Other containers can access this service through the hostname "couchbase"
    * This image declares volumes and will default to use non-persistent, host-local storage.
      You can add persistent volumes later by running 'volume dc/couchbase --add ...'
    * WARNING: Image "arungupta/couchbase" runs as the 'root' user which may not be permitted by your cluster administrator

--> Creating resources with label app=couchbase ...
    imagestream "couchbase" created
    deploymentconfig "couchbase" created
    service "couchbase" created
--> Success
    Run 'oc status' to view your app.
```

Create route if it is needed

Default users:
  Administrator/password  



### Running the use case on OpenShift

It is assumed a running OpenShift platform is already running. Assuming your current
shell is connected to OpenShift so that you can type a command like

```
oc get pods
```

Then the following command will package your app and run it on OpenShift:

```
mvn fabric8:run
```

To list all the running pods:

```
oc get pods
```

Then find the name of the pod that runs this use case, and output the logs from the running pods with:

```
oc logs <name of pod>
```

### Deploy into OpenShift

```
mvn fabric8:deploy
```

# References

* [OpenShift Ecosystem: Couchbase and OpenShift for Your NoSQL Applications](https://blog.openshift.com/openshift-ecosystem-couchbase-openshift-nosql-applications/)
* [Camel Couchbase](http://camel.apache.org/couchbase.html)
* [Couchbase on OpenShift 3](https://blog.couchbase.com/couchbase-on-openshift-3/)





curl -H "Content-Type: application/json" -X POST -d '{"number":"1234","description":"Description","cuantity":1,"price":30,"shop":"Shop"}'  http://fu01-amq-rest-queue-fu01-amq-rest-queue.axdesocp1.central.inditex.grp/services/helloservice/queue/add


http://fu01-amq-rest-queue-fu01-amq-rest-queue.axdesocp1.central.inditex.grp/services/helloservice/queue

http://localhost:8080/fu01/services/queue/add

curl -H "Content-Type: application/json" -X POST -d '{"number":"1234","description":"Description","cuantity":1,"price":30,"shop":"Shop"}' http://localhost:8080/fu01/services/queue/add

* http://couchbase-fu02-amq-db.axdesocp1.central.inditex.grp/ui/index.html#/buckets



curl http://couchbase-fu02-amq-db.axdesocp1.central.inditex.grp/pools/default/buckets

curl -u Administrator:password http://couchbase-fu02-amq-db.axdesocp1.central.inditex.grp/pools/default/buckets/default

curl -X POST -u Administrator:password -d name=default -d ramQuotaMB=200 -d authType=none -d replicaNumber=1 http://couchbase-fu02-amq-db.axdesocp1.central.inditex.grp/pools/default/buckets




Logs from

    11:00:13.487 [Camel (fu02-amq-db) thread #10 - timer://amq-db] INFO  putMessageToQueue - Sending Message '#2868' to queue 'fu02-queue'
    11:00:13.490 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:13.497 [Camel (fu02-amq-db) thread #8 - JmsConsumer[fu02-queue]] INFO  insertDB - Saving '#2868' into DB
    2017-03-27 11:00:13.501 INFO com.couchbase.client.vbucket.provider.BucketConfigurationProvider:  Could bootstrap through carrier publication.
    2017-03-27 11:00:13.502 INFO com.couchbase.client.CouchbaseConnection:  Added {QA sa=couchbase/10.255.195.181:11210, #Rops=0, #Wops=0, #iq=0, topRop=null, topWop=null, toWrite=0, interested=0} to connect queue
    2017-03-27 11:00:13.502 INFO com.couchbase.client.CouchbaseClient:  CouchbaseConnectionFactory{bucket='default', nodes=[http://couchbase:8091/pools], order=RANDOM, opTimeout=2500, opQueue=16384, opQueueBlockTime=10000, obsPollInt=10, obsPollMax=500, obsTimeout=5000, viewConns=10, viewTimeout=75000, viewWorkers=1, configCheck=10, reconnectInt=1100, failureMode=Redistribute, hashAlgo=NATIVE_HASH, authWaitTime=2500}
    2017-03-27 11:00:13.504 INFO com.couchbase.client.CouchbaseClient:  viewmode property isn't defined. Setting viewmode to production mode
    11:00:13.506 [Camel (fu02-amq-db) thread #8 - JmsConsumer[fu02-queue]] INFO  e.i.p.f.f.p.CouchbaseProcessor - Added new Documento into DB {"id":"2868","number":"2868","description":"Description","cuantity":2868,"price":2868000,"shop":"Shop-2868","shopped":1490612413504,"registered":1490612413504,"processed":1490612413504}
    11:00:13.507 [Camel (fu02-amq-db) thread #8 - JmsConsumer[fu02-queue]] INFO  e.i.p.f.f.p.CouchbaseProcessor - Flushed data to DB
    2017-03-27 11:00:13.508 INFO com.couchbase.client.CouchbaseConnection:  Shut down Couchbase client
    2017-03-27 11:00:13.511 INFO com.couchbase.client.ViewConnection:  I/O reactor terminated
    11:00:19.086 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:29.083 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:29.084 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:29.090 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:29.092 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:39.083 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:39.089 [ActiveMQ Task-1] INFO  o.a.a.t.failover.FailoverTransport - Successfully connected to tcp://au01-amq-broker-amq-tcp.au01-amq-persistence:61616
    11:00:43.486 [Camel (fu02-amq-db) thread #10 - timer://amq-db] INFO  putMessageToQueue - Sending Message '#4884' to queue 'fu02-queue'
    11:00:43.494 [Camel (fu02-amq-db) thread #4 - JmsConsumer[fu02-queue]] INFO  insertDB - Saving '#4884' into DB
    2017-03-27 11:00:43.505 INFO com.couchbase.client.vbucket.provider.BucketConfigurationProvider:  Could bootstrap through carrier publication.
    2017-03-27 11:00:43.506 INFO com.couchbase.client.CouchbaseConnection:  Added {QA sa=couchbase/10.255.195.181:11210, #Rops=0, #Wops=0, #iq=0, topRop=null, topWop=null, toWrite=0, interested=0} to connect queue
    2017-03-27 11:00:43.506 INFO com.couchbase.client.CouchbaseClient:  CouchbaseConnectionFactory{bucket='default', nodes=[http://couchbase:8091/pools], order=RANDOM, opTimeout=2500, opQueue=16384, opQueueBlockTime=10000, obsPollInt=10, obsPollMax=500, obsTimeout=5000, viewConns=10, viewTimeout=75000, viewWorkers=1, configCheck=10, reconnectInt=1100, failureMode=Redistribute, hashAlgo=NATIVE_HASH, authWaitTime=2500}
    2017-03-27 11:00:43.507 INFO com.couchbase.client.CouchbaseClient:  viewmode property isn't defined. Setting viewmode to production mode
    11:00:43.511 [Camel (fu02-amq-db) thread #4 - JmsConsumer[fu02-queue]] INFO  e.i.p.f.f.p.CouchbaseProcessor - Added new Documento into DB {"id":"4884","number":"4884","description":"Description","cuantity":4884,"price":4884000,"shop":"Shop-4884","shopped":1490612443507,"registered":1490612443507,"processed":1490612443507}
    11:00:43.511 [Camel (fu02-amq-db) thread #4 - JmsConsumer[fu02-queue]] INFO  e.i.p.f.f.p.CouchbaseProcessor - Flushed data to DB
    2017-03-27 11:00:43.512 INFO com.couchbase.client.CouchbaseConnection:  Shut down Couchbase client
    2017-03-27 11:00:43.513 INFO com.couchbase.client.ViewConnection:  I/O reactor terminated





References:

* [Getting any Docker image running in your own OpenShift cluster](https://blog.openshift.com/getting-any-docker-image-running-in-your-own-openshift-cluster/)
* [Couchbase - Creating and editing buckets](http://docs.couchbase.com/admin/admin/REST/rest-bucket-create.html)
* [Couchbase - Getting single bucket information](http://docs.couchbase.com/admin/admin/REST/rest-bucket-info.html)
