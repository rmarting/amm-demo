oc new-app --template=mysql-persistent-with-init \
	-p MYSQL_USER=mysql \
	-p MYSQL_PASSWORD=mysql \
	-p MYSQL_DATABASE=catalogdb
