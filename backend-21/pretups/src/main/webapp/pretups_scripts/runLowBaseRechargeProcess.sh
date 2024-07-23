###################################################################
#                Cron Script - runLowBaseRechargeProcess                                      #
#                Bharti Telesoft LTD.                              #
#                Dated :19/09/2016                               #
####################################################################


source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/pretups/processes/LowBasedRechargeReportProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props NG

