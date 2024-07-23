source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 argument Constants.props
#2 argument LogConfig.props
#3 QueryFile.props 
#4 argument BOTH/P2P/C2S
#5 process interval (24 daily) 24
#6 LANGUAGE_CODE 0 (From locale_master table)

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ReconciliationReport $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props <PATH of queryfile.props> BOTH 24 0