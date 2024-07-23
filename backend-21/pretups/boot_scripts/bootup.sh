#!/bin/bash

#setting java path
JAVA_HOME=/data1/pretupsapp/jdk-21.0.1; export JAVA_HOME
PATH=$JAVA_HOME/bin:$PATH:.
nohup java -jar pretups.jar > log.out 2>&1 &

#storing the pid in current directory
echo $! > pid.file
