source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh


#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
#java com/btsl/pretups/whitelist/process/FileProcess  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/ProcessLogConfig.props

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/whitelist/process/FileProcess  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props
