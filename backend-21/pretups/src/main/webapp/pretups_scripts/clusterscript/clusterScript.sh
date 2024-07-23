HOME=/pretupshome
case "$1" in
    start)
        echo "Change the premission"
        su -c /root/premission.sh
        echo " check-----> $1"
        echo "Starting Tomcat SMSR"
        su - pretups -c $HOME/tomcat5_smsr/bin/pretupsstart.sh
        echo "Started Tomcat SMSR"
        echo "[`date`]:Started Tomcat SMSR [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log

        echo "Starting Tomcat WEB"
        su - pretups -c $HOME/tomcat5_web/bin/pretupsstart.sh
        echo "Started Tomcat WEB"
        echo "[`date`]:Started Tomcat Web [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log

        echo "Starting SMSC Gateway"
        su - pretups -c $HOME/SMSCGateway/kannelStart.sh
        echo "Started SMSC Gateway"
        echo "[`date`]:Started SMSC Gateway [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
	su - pretups -c $HOME/cluster/switchover.sh

        echo "Starting AlarmManager"
        su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStart.sh
        echo "Started AlarmManager"
        echo "[`date`]:Started AlarmManager [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log

        echo "Starting Log Server"
        su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/logServerStart.sh
        echo "Started Log Server"
        echo "[`date`]:Started Log Server [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log
        ;;
 stop)
        su -c /root/premission.sh
        echo "check-----> $1"
        echo "PreTUPS SMSR App stopping"
        su - pretups -c $HOME/tomcat5_smsr/bin/pretupsstop.sh
        echo "PreTUPS SMSR App stopped"
        echo "[`date`]:Stopped Tomcat SMSR [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log

        echo "PreTUPS WEB App stopping"
        su - pretups -c $HOME/tomcat5_web/bin/pretupsstop.sh
        echo "PreTUPS WEB App stopped"
        echo "[`date`]:Stopped Tomcat Web [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log

        echo "SMSC Gateways Stopping"
        su - pretups -c $HOME/SMSCGateway/kannelStop.sh
        echo "SMSC Gateways Stopped"
        echo "[`date`]:Stopped SMSC Gateway [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log

        echo "Stopping AlarmManager"
        su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStop.sh
        echo "Stopped AlarmManager"
        echo "[`date`]:Stopped AlarmManager [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log

        echo "Stopping Log Server"
        su - pretups -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/logServerStop.sh
        echo "Stopped Log Server"
        echo "[`date`]:Stopped Log Server [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log
        ;;
   status)
        echo " check-----> $1"
# check for SMSR Services
        curl 'http://127.0.0.1:9898/pretups/test.html'
        ret=`echo $?`
        if [ $ret !=  0  ] ;then
                  echo "[`date`]:PreTUPS SMSR App is Not Running [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
                  su - pretups -c $HOME/tomcat5_smsr/bin/pretupsstart.sh
                  sleep 10
                  echo "[`date`]:Restarted Tomcat SMSR [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
                  curl 'http://127.0.0.1:9898/pretups/test.html'
                  ret=`echo $?`
                  if [ $ret != 0 ]; then
                      echo "PreTUPS SMSR App is Not Running"
                      exit $ret;
                  fi
        fi

# check for Web Services
        curl 'http://127.0.0.1:5555/pretups/test.html'
        ret=`echo $?`
        if [ $ret != 0  ] ;then
              echo "[`date`]:PreTUPS WEB App is Not Running [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              su - pretups -c $HOME/tomcat5_web/bin/pretupsstart.sh
        echo "[`date`]:Restarted Tomcat Web [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log
              sleep 10
              curl 'http://127.0.0.1:5555/pretups/test.html'
              ret=`echo $?`
              if [ $ret != 0 ]; then
                  echo "PreTUPS WEB App is Not Running"
                  exit $ret;
              fi
        fi
# check for Alarm Manager  Services
        ret=`ps -e|grep -c "AlarmManager"`
        if [ $ret != 1  ] ;then
              echo "[`date`]:Alarm Manager is Not Running [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              su -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStart.sh
              sleep 10
              echo "[`date`]:Restarted Alarm Manager [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              ret=`ps -e|grep -c "AlarmManager"`
              if [ $ret != 1 ]; then
                  echo "Alarm Manger is Not Running"
                  exit 0;
              fi
        fi

# check for LogServer Services
        ret=`ps -e|grep -c "LogServer"`
        if [ $ret != 1  ] ;then
              echo "[`date`]:Log Server is Not Running [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              su -c $HOME/tomcat5_web/webapps/pretups/pretups_scripts/logServerStart.sh
              sleep 10
              echo "[`date`]:Restarted Log Server [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              ret=`ps -e|grep -c "LogServer"`
              if [ $ret != 1 ]; then
                  echo "Log Server is Not Running"
                  exit 0;
              fi
        fi

# check for SMSC Gateways
        curl 'http://127.0.0.1:13000/status?' > stat
        ret=`echo $?`
        if [ $ret != 0 ] ;then
              echo "[`date`]:SMSC Gateways is not Running [cluster Script]" >> /pretupsvar/pretups_cronLogs/scripts.log
              su - pretups -c $HOME/SMSCGateway/kannelStart.sh
              sleep 10
              echo "[`date`]:Restarted SMSC Gateways [cluster Script]" >>  /pretupsvar/pretups_cronLogs/scripts.log
              curl 'http://127.0.0.1:13000/status?' > stat
              ret=`echo $?`
              if [ $ret != 0 ] ;then
                  echo "SMSC Gateways is not Running"
                  exit $ret;
              fi
        fi
        #if [ $ret1 = 0 ]; then
            #echo " SMS Box is not working"
            #su - pretups -c $HOME/SMSCGateway/smsBoxStart.sh
        #fi
        echo "SMSCGateway status"$ret
# finally Returning the value to Qurum Manager
        exit $ret;
        ;;
    *)
    echo "Usage: $0 {start|stop|status}"
    exit 1
esac
exit 0
