source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/LMBMisDataProcessing $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 




