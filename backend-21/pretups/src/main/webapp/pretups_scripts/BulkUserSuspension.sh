source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 Argument Constants.props
#2 Argument LogConfig.props
#3 Network Code
#4 Arguement of the file Data Type (MSISDN)
## Parameter value=(MSISDN).
## MSISDN is for MSISDN Id of the user

java -classpath pretupsCore.jar:$CLASSPATH com/client/pretups/processes/clientprocesses/BulkUserSuspension $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props NG MSISDN