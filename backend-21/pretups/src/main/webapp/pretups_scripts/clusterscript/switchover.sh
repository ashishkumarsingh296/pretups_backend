###################################################################
#                Cron Script  - By Rajeev Gosain                  #
#                Bharti Telesoft LTD.                             #
#                Dated :27/10/2007                                #
###################################################################
# check for Services Switch Over On which node Services are running
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
filen1="OAM"$mon1"-"$day1"-"$yr1".log"
echo $filen1
echo "OAM : `date +%H:%M:%S` :CRITICAL :#3#System#MAJOR#    SwitchOver[process]                  SwitchOver of Application is done successfully on `uname -n`.        MAJOR           #System Information"   >> $filen1
#cat $filen1
############## By Rajeev Gosain ######################################
