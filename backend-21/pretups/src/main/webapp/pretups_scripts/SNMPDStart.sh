#cd sbin/
echo "-------------------------------------------------------------------------"
echo "			This will stop and start SNMPD"
echo "-------------------------------------------------------------------------"
echo ""
echo ""
echo "Stopping please wait ............"
abc=`ps -e| grep -a "snmpd"|awk  '{ print $1 }'|cut -f1`
echo $abc
kill -9 $abc
cd /pretupshome/OAM_30mar/SNMP/net-snmp-5.1/agent/
#./snmpd -L -d -f > /dev/null 2>&1 &
./snmpd -L -d -f > /<Tomcat-Path>/logs/snmpd.log 2>&1 &
echo ""
echo ""
echo "----------------------------------------------------------------------------"
echo "           Started"
echo "--------------------------------------------------------------------------"
