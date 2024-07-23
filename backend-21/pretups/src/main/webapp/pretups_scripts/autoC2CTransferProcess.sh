source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

echo "Auto C2C Transfer Process Started......................................................................"

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/AutoC2CTransferProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

echo "Auto C2C Transfer Process Completed...................................................................."

