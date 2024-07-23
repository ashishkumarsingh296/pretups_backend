###################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
#echo "`uname -n` is active"
# Rotatelogs.sh
#echo " *************** Rotation of sms Logs********************************"
 
TOMLOG=/pretupsvar/LogServer/OAM
OLDLOG=/pretupsvar/LogServer/OAM/oldlogs
cd $TOMLOG
mon=`date +%m`
month=`expr $mon - 1`
echo $month
#datenow=$day"_"$datenow
#cd $TOMLOG
#mv -r $OLDLOG/

#cd $OLDLOG
#find *.log.* -ctime +90 -exec rm -f {} \;

#echo "[`date`] Truncate sms Logs from Node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi
