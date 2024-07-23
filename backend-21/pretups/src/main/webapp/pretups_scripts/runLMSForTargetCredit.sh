###################################################################
#                Cron Script- runLMSForTargetCredit                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :15/01/2014                                 #
####################################################################


source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#Language setting for the SMS without this setting users will get the invalid Pos key

LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-Xms256m -Xmx256m"; export JAVA_OPTS;

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/


java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/pretups/processes/RunLMSForTargetCreditNew $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props


