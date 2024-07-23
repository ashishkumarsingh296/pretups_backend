source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH java com/btsl/pretups/processes/clientprocesses/AmbiguousServerP2PUpdateProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props Y

