####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2010                                 #
####################################################################

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
PRETUPS_HOME=$CATALINA_HOME/webapps/pretups; export PRETUPS_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;


export BASH_ENV PATH CLASSPATH


cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/pretups/processes/RP2PTransactionDataProcessFile $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props
