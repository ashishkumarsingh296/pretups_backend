#!/bin/bash



export JAVA_HOME=EXPORTJAVAHOME
export PATH=EXPORTPATH


cd CHANGEDIR
java -jar vouchermgmt-0.0.1-SNAPSHOT.jar  -Dspring.config.location=application.properties  >> CHANGEDIR/pretups-login-success.log