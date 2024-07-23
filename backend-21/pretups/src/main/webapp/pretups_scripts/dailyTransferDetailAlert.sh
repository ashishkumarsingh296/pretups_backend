#This script should be executed after UserDailyBalance script
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/

#java com/btsl/pretups/processes/DailyTransferDetailAlert  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/DailyTransferDetailAlert  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

