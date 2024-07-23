#********************************************************************************************
# This script is used to run the RP2P request send process.
# The script takes four(4) parameters as input
#       path of propertiesfile for RP2P requets that contain required parameter
#       number of requests per second
#       service keyword
#       total number of requests
# Created By                                 Date
# Ankit Singhal                         09/03/2006
#
#********************************************************************************************
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms128m -Xmx128m"; export JAVA_OPTS;

#CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5_10g.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crystal.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/j-ftp.jar:.

export BASH_ENV PATH CLASSPATH
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
echo CP2P for 10 request
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/HourlyC2SDWHProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props > $CATALINA_HOME/logs/hourlyC2SDWHprocess.log