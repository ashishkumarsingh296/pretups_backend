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
# Rotatelogs.sh
#echo " *************** Rotation of Web Logs********************************"
 
TOMLOG=/pretupsvar/pretups_weblogs/
#TOMOLDLOG=/pretupsvar/pretups_weblogs/backup_logs
OLDLOG=/pretupsvar/pretups_weblogs/oldlogs

cd $TOMLOG
datenow=`date +%m_%Y`
day=`date +%d`
day=`expr $day - 1`
datenow=$day"_"$datenow
cp catalina.out $OLDLOG/catalina.out.$datenow 
> catalina.out
cp reports.log $OLDLOG/reports.log.$datenow 
> reports.log
#cd $TOMOLDLOG
#find *.out* -ctime +5 -exec rm -f {} \;
 
cd $TOMLOG
mv *.log.* $OLDLOG/
cd $OLDLOG
find *.log.* -ctime +90 -exec rm -f {} \;
find catalina.out* -ctime +7 -exec rm -f {} \;
find PreTUPs_out.log.* -ctime +7 -exec rm -f {} \;

 
#echo " *********** End of Pretups Web Logs ****************************"
#echo " ********** Powered by Telesoft *********************************"
echo "[`date`] Truncate Web Logs from Node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi
