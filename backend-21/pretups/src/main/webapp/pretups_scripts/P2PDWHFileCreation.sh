source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/P2PDWHFileCreation $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 



cd $CATALINA_HOME/logs/P2PDWHDataFinal
find ./ -name *.csv -ctime +7 -exec rm -f {} \;
echo "Files older than 7 days has been deleted."