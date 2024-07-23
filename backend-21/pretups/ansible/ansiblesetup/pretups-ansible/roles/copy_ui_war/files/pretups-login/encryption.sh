#!/bin/bash



export JAVA_HOME=EXPORTJAVAHOME
export PATH=EXPORTPATH


cd CHANGEDIR
echo "Encrypting" 
java -jar utilities-keystore-1.0.8-SNAPSHOT.jar  CHANGEDIR 1 ENC_DB_PASS  > enc.log

