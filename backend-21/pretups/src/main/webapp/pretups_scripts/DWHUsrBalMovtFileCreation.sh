####################################################################
#                Cron Script                                       #
#                Comviva Technologies LTD.                              #
#                Dated :06/09/2009                                 #
####################################################################
# check for Services On which node Services are running


source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/DWHUserBalanceMovement $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props


