echo " "
echo "**************************************************************************************"
echo " You are Stopping the PreTUPs WEB SERVER"
echo ""
echo ""
#HOME=/home/vastrix; export HOME
cd ~
HOME=`pwd`; export HOME
JAVA_HOME=$HOME/<JAVA_HOME>; export JAVA_HOME
CATALINA_HOME=$HOME/<TOMCAT_HOME>; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms512m -Xmx512m"; export JAVA_OPTS;
##CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:
##$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/alepokenyastub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mobinilpoststub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ClearReports.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ReportViewer.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/milton-servlet-1.4.jar:.

##CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload-1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-io-2.0.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang-2.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang3-3.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-1.1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-api-1.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/freemarker-2.3.19.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/javassist-3.11.0.GA.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jpa.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.17.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ognl-3.0.6.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/sitemesh-2.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-core-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-sitemesh-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-tiles3-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-taglib-1.3.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xwork-core-2.3.15.1.jar:.
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload-1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-io-2.0.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang-2.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang3-3.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-1.1.3.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-api-1.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/freemarker-2.3.19.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/javassist-3.11.0.GA.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jpa.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.17.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ognl-3.0.6.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/sitemesh-2.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-core-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-sitemesh-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts2-tiles3-plugin-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-taglib-1.3.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xwork-core-2.3.15.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/c3p0-0.9.1.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc6.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversestub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.7.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-codec-1.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis-schema.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xalan.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesImpl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesSamples.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/serializer.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xml-apis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlParserAPIs.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlsec-1.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversetgstub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crimson.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/bcprov-ext-jdk15-144.jar:.

export BASH_ENV PATH CLASSPATH

cd  $HOME/<TOMCAT_HOME>/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
./shutdown.sh
abc=`ps -aef | grep java | grep "<TOMCAT_HOME>" | awk -F " " '{print $2}'`
echo $abc
#kill -9 $abc
#killall java
clear
echo "***************************************************************************************"
echo ""
echo ""
echo " Stopping the PreTUPs WEB Server of PreTUPS 6.3 , Please wait ..........."
sleep 5
#./startup.sh
echo ""
echo ""
echo ""
echo ""
echo " ******************************** Powered by Telesoft *********************************"
