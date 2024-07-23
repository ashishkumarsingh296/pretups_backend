####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################


# RotateCron.sh : To be Run after 7 days
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
echo "`uname -n` is active"
#echo " **************** Rotation of Cron Logs********************************"
TOMLOG=/<Tomcat-Path>/pretups_cronLogs/
TOMOLDLOG=/<Tomcat-Path>/pretups_cronLogs/backup_logs
cd $TOMLOG
datenow=`date +%m_%Y`
day=`date +%d`
#day=`expr $day - 1`
datenow=$day"_"$datenow
cp networkDailyClosingBalance.out $TOMOLDLOG/networkDailyClosingBalance.out.$datenow 
> networkDailyClosingBalance.out
cp networkDailyClosingBalance.err $TOMOLDLOG/networkDailyClosingBalance.err.$datenow 
> networkDailyClosingBalance.err
cp lowBalanceAlertSender.out $TOMOLDLOG/lowBalanceAlertSender.out.$datenow 
> lowBalanceAlertSender.out
cp lowBalanceAlertSender.err $TOMOLDLOG/lowBalanceAlertSender.err.$datenow 
> lowBalanceAlertSender.err
cp channelTransferPurging.out $TOMOLDLOG/channelTransferPurging.out.$datenow 
> channelTransferPurging.out
cp channelTransferPurging.err $TOMOLDLOG/channelTransferPurging.err.$datenow 
> channelTransferPurging.err
cp channelTransferArchiving.out $TOMOLDLOG/channelTransferArchiving.out.$datenow 
> channelTransferArchiving.out
cp channelTransferArchiving.err $TOMOLDLOG/channelTransferArchiving.err.$datenow 
> channelTransferArchiving.err
cp c2sTransferPurging.out $TOMOLDLOG/c2sTransferPurging.out.$datenow 
> c2sTransferPurging.out
cp c2sTransferPurging.err $TOMOLDLOG/c2sTransferPurging.err.$datenow 
> c2sTransferPurging.err
cp userDailyClosingBalance.out $TOMOLDLOG/userDailyClosingBalance.out.$datenow 
> userDailyClosingBalance.out
cp userDailyClosingBalance.err $TOMOLDLOG/userDailyClosingBalance.err.$datenow 
> userDailyClosingBalance.err
cp pinPasswordAlertP2pPin.out $TOMOLDLOG/pinPasswordAlertP2pPin.out.$datenow 
> pinPasswordAlertP2pPin.out
cp pinPasswordAlertP2pPin.err $TOMOLDLOG/pinPasswordAlertP2pPin.err.$datenow 
> pinPasswordAlertP2pPin.err
cp pinPasswordAlertOptPwd.out $TOMOLDLOG/pinPasswordAlertOptPwd.out.$datenow 
> pinPasswordAlertOptPwd.out
cp pinPasswordAlertOptPwd.err $TOMOLDLOG/pinPasswordAlertOptPwd.err.$datenow 
> pinPasswordAlertOptPwd.err
cp pinPasswordAlertChnlPwd.out $TOMOLDLOG/pinPasswordAlertChnlPwd.out.$datenow 
> pinPasswordAlertChnlPwd.out
cp pinPasswordAlertChnlPwd.err $TOMOLDLOG/pinPasswordAlertChnlPwd.err.$datenow 
> pinPasswordAlertChnlPwd.err
cp pinPasswordAlertC2sPin.out $TOMOLDLOG/pinPasswordAlertC2sPin.out.$datenow 
> pinPasswordAlertC2sPin.out
cp pinPasswordAlertC2sPin.err $TOMOLDLOG/pinPasswordAlertC2sPin.err.$datenow 
> pinPasswordAlertC2sPin.err
cp networkStockTxnPurging.out $TOMOLDLOG/networkStockTxnPurging.out.$datenow 
> networkStockTxnPurging.out
cp networkStockTxnPurging.err $TOMOLDLOG/networkStockTxnPurging.err.$datenow 
> networkStockTxnPurging.err
cp networkStockTxnArchiving.out $TOMOLDLOG/networkStockTxnArchiving.out.$datenow 
> networkStockTxnArchiving.out
cp networkStockTxnArchiving.err $TOMOLDLOG/networkStockTxnArchiving.err.$datenow 
> networkStockTxnArchiving.err
cp P2PMisProcessing.out $TOMOLDLOG/P2PMisProcessing.out.$datenow 
> P2PMisProcessing.out
cp P2PMisProcessing.err $TOMOLDLOG/P2PMisProcessing.err.$datenow 
> P2PMisProcessing.err
cp C2SMisProcessing.out $TOMOLDLOG/C2SMisProcessing.out.$datenow 
> C2SMisProcessing.out
cp C2SMisProcessing.err $TOMOLDLOG/C2SMisProcessing.err.$datenow 
> C2SMisProcessing.err
cd $TOMOLDLOG
find *.out.* -ctime +30 -exec rm -f {} \;
find *.err.* -ctime +30 -exec rm -f {} \;
#echo " **********************************************************************"
#echo " **************** Powered by Telesoft *********************************"
echo "[`date`]: Trancate CronLogs from [`uname -n`]" >> /<Tomcat-Path>/pretups_cronLogs/scripts.log
else
echo "`uname -n` is not active"
fi
