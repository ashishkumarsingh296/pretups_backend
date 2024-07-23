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
echo ""
echo "----------------------------------------------------------------------------"
echo "           Stopped"
echo "--------------------------------------------------------------------------"

