#********************************************************************************************
# This script is used to run the RP2P/CP2P request send process.
# The script takes four(4) parameters as input
#	path of propertiesfile for RP2P requets that contain required parameter
#	number of requests per second
#	service keyword
#	total number of requests
# Created By                    	     Date
# Sanjeev Sharma				13/03/2008
#
#********************************************************************************************
echo " "
echo "**************************************************************************************"
echo " Start Sending USSDLoad Test Request "
echo ""
echo ""
HOME=/home/pretups513_dev; export HOME
JAVA_HOME=$HOME/PRETUPS513_DEV; export JAVA_HOME
CATALINA_HOME=$HOME/tomcat5; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms64m -Xmx64m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/crystal/crystalclear/CC-Viewer.jar:.

export BASH_ENV PATH CLASSPATH

##For USSD Request of CP2P Service use this
java com/btsl/loadtest/ussdloadtest/USSDLoadTestSimulator $CATALINA_HOME/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/ussdloadtest/UssdConfigfile.properties 2 CCRCREQ 2 > $CATALINA_HOME/logs/USSDCP2P_RC.txt 2>&1 &

##For USSD Request of RP2P Service use this
#java com/btsl/loadtest/ussdloadtest/USSDLoadTestSimulator $CATALINA_HOME/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/ussdloadtest/UssdConfigfile.properties 2 RCTRFREQ 2> $CATALINA_HOME/logs/USSDRP2P_RC.txt 2>&1 &

echo "*************** Request Complete ***************"


