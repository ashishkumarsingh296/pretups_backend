source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#first argument Constants.props
#second argument ProcessLogConfig.props
#Third argument 0 (Locale)
#Fourth argument ALL (Network Code)
#Fifth argument date (format hard code dd/MM/yy) (Which date you want to generate report, if its blank then its take current date)
#java com/btsl/pretups/processes/DailyReportAnalysis $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props 0 ALL

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/DailyReportAnalysis $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 0 ALL