echo " "
echo "**************************************************************************************"
echo " You are Stopping the PreTUPs WEB SERVER"
echo ""
echo ""
HOME=<HOME_DIR>; export HOME
JAVA_HOME=$HOME/<JAVA_INSTALLED_LINK>; export JAVA_HOME
CATALINA_HOME=$HOME/<TOMCAT_INSTALLED_DIR>; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms512m -Xmx512m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:
$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crystal.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/alepokenyastub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mobinilpoststub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:.

export BASH_ENV PATH CLASSPATH

cd  $HOME/tomcat5_601/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
./shutdown.sh
abc=`ps -aef | grep java | grep "PRETUPS_601" | awk -F " " '{print $2}'`
echo $abc
#kill -9 $abc
#killall java
clear
echo "***************************************************************************************"
echo ""
echo ""
echo " Stopping the PreTUPs WEB Server of TIGO Guatemala , Please wait ..........."
sleep 5
#./startup.sh
echo ""
echo ""
echo ""
echo ""
echo " ******************************** Powered by Telesoft *********************************"

