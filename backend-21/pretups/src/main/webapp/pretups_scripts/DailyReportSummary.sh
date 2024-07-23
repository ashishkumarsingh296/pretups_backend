source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
#CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:.
export BASH_ENV PATH CLASSPATH

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#first argument Constants.props
#second argument ProcessLogConfig.props
#Third argument 0 (Locale)
#Fourth argument ALL (Network Code)
#Fifth argument date (format hard code dd/MM/yy) (Which date you want to generate report, if its blank then its take current date)
#java com/btsl/pretups/processes/DailyReportSummary $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props 0 ALL

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/DailyReportSummary $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 0 ALL
