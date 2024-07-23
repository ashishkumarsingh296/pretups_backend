source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#parameters => Constants.props LogConfig.props FWD/BWD no_of_days
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/util/FindMyRetailerBatch  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props  BWD 3