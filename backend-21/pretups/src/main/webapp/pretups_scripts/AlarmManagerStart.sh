#cd sbin/
echo "-------------------------------------------------------------------------"
echo "			This will stop and start Alarm Server"
echo "-------------------------------------------------------------------------"
echo ""
echo ""
echo "Stopping Log Server please wait ............"
abc=`ps -e| grep -a "AlarmManager"|awk  '{ print $1 }'|cut -f1`
echo $abc
kill -9 $abc
cd /pretupshome/OAM_30mar/AlarmManager/
#./start.sh > /dev/null 2>&1 &
./start.sh > /pretupsvar/pretups_oam/alarm.log 2>&1 &
echo ""
echo ""
echo "----------------------------------------------------------------------------"
echo "           Started"
echo "--------------------------------------------------------------------------"

