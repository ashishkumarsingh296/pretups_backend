####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
# check for Services On which node Services are running
#curl 'http://172.16.1.175:5030/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
echo "`uname -n` is active"

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/AdditionalCommisionDeduction $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props


echo "[`date`]: Addition commission Deduction Processing  from node [`uname -n`]" >> pretupsvar/pretups_cronLogs/additionCommissionProfileLog.log
else
echo "`uname -n` is not active"
fi