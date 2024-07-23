#cd sbin/
echo "-------------------------------------------------------------------------"
echo "			This will stop and start the SMSCGateway for E-Topup"
echo "-------------------------------------------------------------------------"
echo ""
echo ""
cd <SMSC-Path>
echo "Stopping SMSC Gateway"
ps ax | grep bearerbox | awk '{print "kill -9 "$1}'|sh
ps ax | grep smsbox | awk '{print "kill -9 "$1}'|sh
echo "Stopped SMSC Gateway"

echo "Starting SMSCGateway for E-Topup Please wait ............"
#./sbin/bearerbox &
./sbin/bearerbox -v 5 > /dev/null 2>&1 &

sleep 2
#./sbin/smsbox &
./sbin/smsbox -v 5 > /dev/null 2>&1 &
echo ""
echo ""
echo "----------------------------------------------------------------------------"
echo "           SMSCGateway Started,  Thanks for Using SMSCGateway"
echo "--------------------------------------------------------------------------"
