source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 argument Constants.props
#2 argument ProcessLogConfig.props
#3 QueryFile.props
#4 process interval (24 daily) 24
#java com/btsl/pretups/processes/ExternalUserDataFile $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/QueryFile.props 24

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ExternalUserDataFile $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/QueryFile.props 24

