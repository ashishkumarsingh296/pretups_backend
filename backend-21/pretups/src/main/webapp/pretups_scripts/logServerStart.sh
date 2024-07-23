#cd sbin/
echo "-------------------------------------------------------------------------"
echo "			This will stop and start Log Server"
echo "-------------------------------------------------------------------------"
echo ""
echo ""
echo "Stopping please wait ............"
abc=`ps -e| grep -a "LogServer"|awk  '{ print $1 }'|cut -f1`
echo $abc
kill -9 $abc
sleep 2
cd /pretupshome/OAM_30mar/LogServer/
#./start.sh > /dev/null 2>&1 &
./start.sh > /pretupsvar/pretups_oam/logserver.log 2>&1 &
echo ""
echo ""
echo "----------------------------------------------------------------------------"
echo "           Started"
echo "--------------------------------------------------------------------------"

