###################################################################
#                Cron Script  - By Rajeev Gosain                  #
#                Bharti Telesoft LTD.                             #
#                Dated :27/10/2007                                #
###################################################################
curl 'http://127.0.0.1:5555/pretups/test.html'
ret1=`echo $?`
if [ $ret1 = 0 ] ; then
	echo "`uname -n` is active"
	declare -i ret
	curl 'http://127.0.0.1:13000/status' > stat
	ret=`grep -c "re-connecting" stat`
	if [ $ret != 0 ]; then
	# check for Services 
	mon=`date +%m`
	case $mon in
	1) mon1=Jan;;
	2) mon1=Feb;;
	3) mon1=Mar;;
	4) mon1=Apr;;
	5) mon1=May;;
	6) mon1=Jun;;
	7) mon1=Jul;;
	8) mon1=Aug;;
	9) mon1=Sep;;
	10)mon1=Oct;;
	11)mon1=Nov;;
	12)mon1=Dec;;
	esac
	yr1=`date +%Y`
	echo $yr1
	echo $mon1
	dir1=$mon1"-"$yr1"-LOG"
	cd /pretupsvar/LogServer/OAM
	cd $dir1
	day1=`date +%d`
	echo $day1
	filen2="OAM"$mon1"-"$day1"-"$yr1".log"
	#echo $filen2
	echo "OAM : `date +%H:%M:%S` :CRITICAL :#3#System#MAJOR#    SmscGateway[process]                  PreTUPS cannot able to connect SMSC from `uname -n`.        MAJOR           #System Information"   >> $filen2
	sh /pretupshome/tomcat5_web/webapps/pretups/pretups_scripts/logServerStart.sh
	sh /pretupshome/tomcat5_web/webapps/pretups/pretups_scripts/AlarmManagerStart.sh
	else
	echo "working fine"
	fi
else
	echo "`uname -n` is not active"
fi

############## By Rajeev Gosain ######################################

