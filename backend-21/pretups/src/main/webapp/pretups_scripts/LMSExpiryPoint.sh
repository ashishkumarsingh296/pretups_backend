source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#java com/btsl/pretups/processes/LMSExpiryPointsReturn $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/LMSExpiryPointsReturn $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

