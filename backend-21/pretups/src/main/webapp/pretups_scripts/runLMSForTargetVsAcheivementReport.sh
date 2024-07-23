source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
JAVA_OPTS="-Xms64m -Xmx64m"; export JAVA_OPTS;

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/LMSTargetVsAchievementReport $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 0
