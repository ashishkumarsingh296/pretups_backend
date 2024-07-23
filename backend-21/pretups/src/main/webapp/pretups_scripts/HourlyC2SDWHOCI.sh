####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
# check for Services On which node Services are running
#curl 'http://127.0.0.1:9090/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
echo "`uname -n` is active"

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#first argument Constants.props
#second argument ProcessLogConfig.props
#Third argument 0 (Locale)
#java com/btsl/pretups/processes/HourlyC2SDWHProcessOCI $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/csvConfigFile4HourlyC2SDWH.props
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/HourlyC2SDWHProcessOCI $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/csvConfigFile4HourlyC2SDWH.props
echo "[`date`]: HourlyC2SDWHOCI.sh (HourlyC2SDWHOCI) Processing  from node [`uname -n`]"
else
echo "`uname -n` is not active"
fi

