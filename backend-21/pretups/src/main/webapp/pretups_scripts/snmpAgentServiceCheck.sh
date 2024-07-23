#cd sbin/
HOME=<Tomcat-Path>/webapps/pretups/pretups_scripts
echo "-----------------------------------------------------------------------------------------"
echo "			This will start the Log Server and Alarm maneger Server if not running"
echo "-------------------------------------------------------------------------------------------"
echo ""
echo ""
ret=`ps -aef |grep -c "logServer"`
if [ $ret -eq 1 ]; then
	su - pretups -c $HOME/logServerStart.sh
	echo "Started  LogServer"
fi
ret=`ps -aef | grep -c "AlarmManager"`
if [ $ret -eq 1 ] ;then
	su - pretups -c $HOME/AlarmManagerStart.sh 
	echo "Started  AlarmManager"
fi
ret=`ps -aef | grep -c "snmpd"`
if [ $ret -eq 1 ] ;then
	 su -c $HOME/SNMPDStart.sh
fi
echo "Started  SNMP"
echo ""
echo ""
echo "----------------------------------------------------------------------------"
echo "           Started"
echo "--------------------------------------------------------------------------"
