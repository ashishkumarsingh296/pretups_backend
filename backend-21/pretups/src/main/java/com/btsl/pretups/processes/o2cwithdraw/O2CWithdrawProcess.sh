###################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################


HOME=/pretupshome ; export HOME
JAVA_HOME=$HOME/PRETUPS_WEB_JAVA; export JAVA_HOME
PRETUPS_HOME=$HOME/tomcat5_web/webapps/pretups; export PRETUPS_HOME
CATALINA_HOME=$HOME/tomcat5_web; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;

CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:.
export BASH_ENV PATH CLASSPATH
cd $CATALINA_HOME/webapps/pretups/WEB-INF/src/
echo "Your CLASSPATH is "$CLASSPATH

cd $CATALINA_HOME/webapps/pretups/pretups_scripts/O2CWithdrawDeleteProcess3/
#javac O2CWithdrawUserDelProcess.java O2CWdhUserDelProcessLog.java 

java -Xms512m -Xmx512m O2CWithdrawUserDelProcess $CATALINA_HOME/webapps/pretups/pretups_scripts/O2CWithdrawDeleteProcess3/O2cWdhConstants.props $CATALINA_HOME/webapps/pretups/pretups_scripts/O2CWithdrawDeleteProcess3/O2cWdhLogConfig.props $CATALINA_HOME/webapps/pretups/pretups_scripts/O2CWithdrawDeleteProcess3/userLists.txt OB


