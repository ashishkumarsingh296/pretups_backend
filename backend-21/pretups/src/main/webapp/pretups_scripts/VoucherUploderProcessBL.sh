source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh


cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

JAVA_OPTS="-Xms64m -Xmx64m"; export JAVA_OPTS;


java -classpath pretupsCore.jar:$CLASSPATH com/client/voms/vomsprocesses/businesslogic/VoucherLoaderProcessBL $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props superadmin 1357

