source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/client/pretups/processes/clientprocesses/OffLineCSVFileGeneratorProcess  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/OffLineOnerWiseSalesReport_withAllDaily.props N
