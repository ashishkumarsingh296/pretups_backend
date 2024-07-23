source <tomcat-path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/univocity-parsers-2.2.3.jar:$CLASSPATH com/btsl/voms/vomsprocesses/businesslogic/VoucherFileProcessRM  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props