#cd sbin/
HOME=<Tomcat-Path>/webapps/pretups/pretups_scripts
echo "-----------------------------------------------------------------------------------------"
echo "			This will stop the Log Server and Alarm maneger Server if running"
echo "-------------------------------------------------------------------------------------------"
echo ""
echo ""
su - pretups -c $HOME/logServerStop.sh
echo "Stopped  LogServer"
su - pretups -c $HOME/AlarmManagerStop.sh 
echo "Stopped  AlarmManager"
su -c $HOME/SNMPDStop.sh
echo "Stopped  SNMP"
echo ""
echo "----------------------------------------------------------------------------"
echo "           Stopped"
echo "--------------------------------------------------------------------------"
