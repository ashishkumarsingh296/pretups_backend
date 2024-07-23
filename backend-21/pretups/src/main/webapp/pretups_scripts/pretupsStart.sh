echo " "
echo "**************************************************************************************"
echo " Your are restarting the PreTUPS 6.3.0 Server"
echo ""
echo ""

#HOME=/home/vastrix; export HOME
cd ~
HOME=`pwd`; export HOME
JAVA_HOME=$HOME/<JAVA_HOME>; export JAVA_HOME
CATALINA_HOME=$HOME/<TOMCAT_HOME>;export CATALINA_HOME

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
JAVA_OPTS="-server -Xss40m -Xms256m -Xmx256m -Xmn128m  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -XX:+PrintGCDetails -Xloggc:gc_ptups_txn.log -Djava.library.path=$CATALINA_HOME/lib";
export JAVA_OPTS;

CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload-1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-io-2.0.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang-2.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang3-3.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-1.1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-api-1.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/freemarker-2.3.19.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/javassist-3.11.0.GA.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jpa.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.17.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ognl-3.0.6.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/sitemesh-2.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-core-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-sitemesh-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-tiles3-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-taglib-1.3.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xwork-core-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/c3p0-0.9.1.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc6.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversestub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.7.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-codec-1.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis-schema.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xalan.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesImpl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesSamples.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/serializer.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xml-apis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlParserAPIs.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlsec-1.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversetgstub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/bcprov-ext-jdk15-144.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes/hdiv-tags.tld:.

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
#./shutdown.sh
./pretupsStop.sh
abc=`ps -aef| grep -a "<TOMCAT_HOME>" |awk  '{ print $2 }'|cut -f1`
#sleep 10
echo $abc
kill -9 $abc
echo ""
clear
echo "**************************************************************************************"
echo ""
echo " Starting the PreTUPs6.3.0 Server, Please wait ..........."
sleep 10
./startup.sh
echo ""
echo ""
echo ""
echo "PreTUPs6.3.0 Server is started "
echo ""
echo ""
echo ""
echo " ******************************** Powered by Mahindra Comviva *********************************"
