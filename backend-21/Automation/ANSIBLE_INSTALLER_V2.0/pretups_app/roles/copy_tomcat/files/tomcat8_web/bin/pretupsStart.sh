echo " "
echo "**************************************************************************************"
echo " Your are restarting the PreTUPS 6.5.0 RoadMap Server"
echo ""
echo ""
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#### Below section for integrating APR with Tomcat
if [ "X$LD_LIBRARY_PATH" != "X" ]
then
        LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${CATALINE_HOME}/lib
else
        LD_LIBRARY_PATH=${CATALINE_HOME}/lib
fi

export LD_LIBRARY_PATH
#### END of APR integration

#Language setting for the SMS without this setting users will get the invalid Pos key
JAVA_OPTS="-server  -Xss40m -Xms256m -Xmx256m -Xmn128m  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:+PrintGCDetails -Xloggc:gc_ptups_txn.log -Djava.library.path=$CATALINA_HOME/lib"
#JAVA_OPTS=" $JAVA_OPTS -Dorg.owasp.esapi.resources=$CATALINA_HOME/bin/.esapi" ;
export JAVA_OPTS;


cd $CATALINA_HOME/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
#./shutdown.sh
./pretupsStop.sh
abc=`ps -aef| grep -a /home/pretups/tomcat8_duplicate |awk  '{ print $2 }'|cut -f1`
#sleep 10
echo $abc
kill -15 $abc
echo ""
clear
echo "**************************************************************************************"
echo ""
echo " Starting the PreTUPS 6.5.0 RoadMap Server, Please wait ..........."
sleep 10
./startup.sh
echo ""
echo ""
echo ""
echo " PreTUPS 6.5.0 RoadMap Server is started on port 7070 ..........."
echo ""
echo ""
echo ""
echo " ******************************** Powered by Mahindra Comviva *********************************"



