oc new-app --template=amq63-basic \
	-p APPLICATION_NAME=amq-broker \
	-p MQ_USERNAME=admin \
	-p MQ_PASSWORD=admin \
	-p IMAGE_STREAM_NAMESPACE=openshift
