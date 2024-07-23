source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
start=`TZ=EST+24 date +%d/%m/%Y_00:00:00`
end=`TZ=EST+24 date +%d/%m/%Y_23:59:59`
java -classpath pretupsCore.jar:$CLASSPATH com/client/pretups/processes/clientprocesses/vietnam/ErpViewReader $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $start $end