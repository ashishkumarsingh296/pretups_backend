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
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
#first argument Constants.props
#second argument ProcessLogConfig.props
#Third argument 0 (Locale)
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/csvgenerator/CSVFileGenerator4NewUserApprovedProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/csvConfigFile4NewUsers.props


echo "[`date`]: CSVFileGenerator4NewUserApprovedProcess.sh (CSVFileGenerator4NewUserApprovedProcess) Processing  from node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi

#Go to the path as per configured value for <NEW_USER_APPROVED_DIR> into the csvConfigFile4NewUsers.props
#NEW_USER_APPROVED_DIR=/home/pretups_oci/tomcat7_web/logs/newUserApprovedCsvreports/
cd $CATALINA_HOME/logs/newUserApprovedCsvreports/
find ./ -name *.csv -ctime +7 -exec rm -f {} \;
echo "Files older than 7 days has been deleted."

