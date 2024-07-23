#********************************************************************************************
# This script is used to run the CP2P request send process.
# The script takes four(4) parameters as input
#	path of propertiesfile for CP2P requets that contain required parameter
#	number of requests per second
#	service keyword
#	total number of requests
# Created By                    	     Date
# Ankit Singhal				09/03/2006
#
#********************************************************************************************
HOME=/home/pretups_com; export HOME
JAVA_HOME=/usr/java/jdk1.5.0_02; export JAVA_HOME
PRETUPS_HOME=/home/pretups_com/tomcat5/webapps/pretups; export PRETUPS_HOME
CATALINA_HOME=$HOME/tomcat5; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:.

export BASH_ENV PATH CLASSPATH

echo CP2P for 10 request
java com/btsl/loadtest/P2PLoadTester $CATALINA_HOME/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/P2PProperties.properties 2 PRC 2 > $CATALINA_HOME/logs/CP2P_10_PRC.txt 2>&1 &
java com/btsl/loadtest/P2PLoadTester $CATALINA_HOME/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/P2PProperties.properties 1 CPN 1 > $CATALINA_HOME/logs/CP2P_10_CPN.txt 2>&1 &
java com/btsl/loadtest/P2PLoadTester $CATALINA_HOME/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/P2PProperties.properties 1 PCHLAN 1 > $CATALINA_HOME/logs/CP2P_10_PCHLAN.txt 2>&1 &
