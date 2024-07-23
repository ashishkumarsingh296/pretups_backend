####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
echo "`uname -n` is active"

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/voms/vomsprocesses/businesslogic/MobinilVoucherChangeOnHoldProcesses $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

echo "[`date`]: MobinilVoucherFileProcessor [`uname -n`]" >> /mmoneyvar/pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi
