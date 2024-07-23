echo " "
echo "**************************************************************************************"
echo " Your are restarting the PreTUPS HONDURAS SMSR2 Server"
echo ""
echo ""

HOME=<HOME_DIR>; export HOME
JAVA_HOME=$HOME/<JAVA_INSTALLED_LINK>; export JAVA_HOME
CATALINA_HOME=$HOME/<TOMCAT_INSTALLED_DIR>; export CATALINA_HOME

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
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-server -Xss2m -Xms512m -Xmx512m -Xmn256m  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:+PrintGCDetails -Xloggc:gc_ptups_txn.log -Djava.library.path=$CATALINA_HOME/lib";
JAVA_OPTS=" $JAVA_OPTS -Dorg.owasp.esapi.resources=$CATALINA_HOME/bin/.esapi" ;
export JAVA_OPTS;

CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crystal.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversestub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.7.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-codec-1.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis-schema.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-1.0.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/serializer.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xalan.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesImpl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesSamples.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xml-apis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlParserAPIs.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlsec-1.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversetgstub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crimson.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/bcprov-ext-jdk15-144.jar:.

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
#./shutdown.sh
./pretupsStop.sh
abc=`ps -aef| grep -a "PRETUPS_601" |awk  '{ print $2 }'|cut -f1`
#sleep 10
echo $abc
kill -9 $abc
echo ""
clear
echo "**************************************************************************************"
echo ""
echo " Starting the PreTUPs HONDURAS SMSR2 Server, Please wait ..........."
sleep 10
./startup.sh
echo ""
echo ""
echo ""
echo "PreTUPS HONDURAS SMSR2 Server is started on the port 8866"
echo ""
echo ""
echo ""
echo " ******************************** Powered by Comviva *********************************"

