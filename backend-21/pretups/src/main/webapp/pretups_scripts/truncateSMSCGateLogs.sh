####################################################################
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
#                RotateLogs of SMSCGateways                        #
####################################################################
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
echo "`uname -n` is active"
#echo " ************* Rotation of Gateway Logs********************************"
TOMLOG=/pretupsvar/SMSCGatewayLogs/
#TOMOLDLOG=/pretupsvar/SMSCGatewayLogs/backup_logs
OLDLOG=/pretupsvar/SMSCGatewayLogs/oldlogs
cd $TOMLOG
datenow=`date +%m_%Y`
day=`date +%d`
#day=`expr $day - 1`
datenow=$day"_"$datenow
cp SmscGateway.log $OLDLOG/SmscGateway.log.$datenow 
> SmscGateway.log
cp smsbox.log $OLDLOG/smsbox.log.$datenow 
> smsbox.log
cp sms_access.log $OLDLOG/sms_access.log.$datenow 
> sms_access.log
cp access.log $OLDLOG/access.log.$datenow 
#cp access.log $OLDLOG/access.log.$datenow 
> access.log
#cd $TOMOLDLOG
#find *.log.* -ctime +5 -exec rm -f {} \;
 
cd $OLDLOG
find *.log.* -ctime +90 -exec rm -f {} \;

#echo "***********************************************************************"
#echo "**************** Powered by Telesoft *********************************"

echo "[`date`]: Truncate SMSC Gateways Logs from node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi
