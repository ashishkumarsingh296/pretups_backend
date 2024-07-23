source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/

#java com/btsl/pretups/p2p/reconciliation/businesslogic/HandleUnsettledP2PCases $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/ProcessLogConfig.props $CATALINA_HOME/webapps/pretups/pretups_scripts/abc.txt 100

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/p2p/reconciliation/businesslogic/HandleUnsettledP2PCases $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/pretups_scripts/abc.txt 100
