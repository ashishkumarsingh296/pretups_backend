#!/bin/bash



#export JAVA_HOME=/home/pretupsRev_demo/jdk1.8.0_74
#export PATH=/home/pretupsRev_demo/jdk1.8.0_74/bin



echo "Starting the job......"
echo $PWD
ls -ltr
cd pretups_login
ls -ltr

java -jar vouchermgmt-0.0.1-SNAPSHOT.jar  -Dspring.config.location=application.properties  –server.port=LOGIN_MODULE_CONTAINER_PORT > loginstart.log
