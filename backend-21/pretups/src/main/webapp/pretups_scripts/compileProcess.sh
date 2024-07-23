


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
PRETUPS_HOME=<Tomcat-Path>/webapps/pretups; export PRETUPS_HOME


echo compileing btsl.pretups.processes
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/processes/*.java

echo "[`date`]: Compile Processes from node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi


