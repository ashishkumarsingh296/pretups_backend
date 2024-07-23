source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 argument Constants.props
#2 argument ProcessLogConfig.props
#3 QueryFile.props 
#4 process interval (24 daily) 24
#5 LANGUAGE_CODE 0 (From locale_master table)
#java com/btsl/pretups/processes/ExternalFile $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/ProcessLogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/QueryFile.props 24 0
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ExternalFile $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/QueryFile.props 24 0

