###################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/o2cwithdraw/O2CWithdrawUserDelProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/pretups_scripts/O2CWithdrawDeleteProcess3/userLists.txt ML
