#Bharti TeleSoft 
#New Delhi
#Dated: 23-Feb-2006
#Script ------ for  Starting n Stopping Pretups
HOME=/pretupshome
case $1 in
	1) echo "Script will start all the Services"
	   echo "You must be logged on AS SUPER USER to Start all the Services "
           echo 
           USER="`id -un`"
           LOGNAME=$USER
           if [ $USER = "root" ] ; then
           cp $HOME/cluster/clusterScript.sh_vir $HOME/cluster/clusterScript.sh
           echo "Starting Tomcat SMS"
	   su - pretups -c $HOME/tomcat5_sms/bin/pretupsstart.sh
           echo "Started Tomcat SMS"
           echo "Starting Tomcat WEB"
           su - pretups -c $HOME/tomcat5_web/bin/pretupsstart.sh
           echo "Started Tomcat WEB"
           echo "Starting SMSC Gateway"
           su - pretups -c $HOME/SMSCGateway/kannelStart.sh
           echo "Started SMSC Gateway"
           echo "Starting AlarmManager"
           su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStart.sh
           echo "Started AlarmManager"
           echo "Starting Log Server"
           su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/logServerStart.sh
           echo "Started Log Server"
           echo "Starting SNMP Agent"
           su -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/SNMPDStart.sh
           echo "Started SNMP Agent"
           echo 
           else
 	   echo " You are not Authorised to Start the Service, Only Super User can Start the Services."
           fi
           sleep 5
           clear;;
	2) echo "Script will stop all the Services"
           echo "You must be logged on AS SUPER USER to Stop all the Services "
           echo
           USER="`id -un`"
           LOGNAME=$USER
           if [ $USER = "root" ] ; then
           cp $HOME/cluster/clusterScript.sh $HOME/cluster/clusterScript.sh_vir
           cp $HOME/cluster/NetClosecluster.sh $HOME/cluster/clusterScript.sh
           echo "Stopping Tomcat SMS"
           su - pretups -c $HOME/tomcat5_sms/bin/pretupsstop.sh
           echo "Stopped Tomcat SMS"
           echo "Stopping Tomcat WEB"
           su - pretups -c $HOME/tomcat5_web/bin/pretupsstop.sh
           echo "Stopped Tomcat WEB"
           echo "Stopping SMSC Gateway"
           su - pretups -c $HOME/SMSCGateway/kannelStop.sh
           echo "Stopped SMSC Gateway"
           echo "Stopping AlarmManager"
           su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStop.sh
           echo "Stopped AlarmManager"
           echo "Stopping Log Server"
           su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/logServerStop.sh
           echo "Stopped Log Server"
           echo "Stopping SNMP Agent"
           su -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/SNMPDStop.sh
           echo "Stopped SNMP Agent"
           echo dd
           else 
	   echo "You are not Authorised to Stop the Services. Only SuperUser can Stop the Services."
           fi
           sleep 5
           clear;;
        3) echo "wait"
           sleep 3
	   exit;;
esac
#done

