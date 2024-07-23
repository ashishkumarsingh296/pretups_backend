####################################################################
#                Script to Send Disk Space Alert - via SMS         #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
#                By Rajeev Gosain                                  #
####################################################################
HOME=<Tomcat-Path>/webapps/pretups/pretups_scripts
nodename=`uname -n`
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
clear
if [ $ret = 0 ] ; then
echo "`uname -n` is active"
echo
############# The Numbers on which the alert will be sent###############

#alertnumber=20123942594
alertnumber1=20123317354

############## Disk Partitioning    ##########################

a1="/dev/cciss/c0d0p2"
a2="/dev/cciss/c0d0p1"
a3="/dev/cciss/c0d0p5"
a4="/dev/cciss/c0d0p3"
a5="/dev/cciss/c0d1p1"
a6="/dev/cciss/c1d0p3"

declare -i count
declare -i totused
declare -i used
declare -i fmsg1
declare -i flag1
flag1=1
count=1
until [ $count -gt 6 ]
do
case $count in
        1)a1=$a1;;
        2)a1=$a2;;
        3)a1=$a3;;
        4)a1=$a4;;
        5)a1=$a5;;
        6)a1=$a6;;
esac
a=$a1
############### Fetching information about the partitioning###############

used=`df -kh | grep "$a" | awk '{ print $5  }' | cut -f1 -d'%'`
ava=`df -kh | grep "$a" | awk '{ print $4  }'`
toa=`df -kh | grep "$a" | awk '{ print $2  }'`
fsa=`df -kh | grep "$a" | awk '{ print $6  }'`
echo
case $count in
        1)fmsg1=0
	  if [ $used -gt 75 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
        2)fmsg1=0
	  if [ $used -gt 75 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
        3)fmsg1=0
	  if [ $used -gt 75 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
        4)fmsg1=0
	  if [ $used -gt 75 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
        5)fmsg1=0
          if [ $used -gt 80 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
        6)fmsg1=0
	  if [ $used -gt 90 ]; then
          msg1="$fsa:Available=$ava+Used=$used+percent+of+Total+Disk=$toa"
          echo $fmsg1
          else
          fmsg1=1
          fi;;
esac
#echo "      Mobinil PreTups Server Disk of $fsa : Total Disk =$toa  Used percent = $used % of Available=$ava"

if [ $fmsg1 != 1 ] ; then
msg7="$msg7+$msg1"
flag1=0
fi

totused=$totused+$used
count=$count+1
done


#echo $flag1
############### Sending Alert check
if [ $flag1 != "1" ]; then

#curl http://127.0.0.1:13014/cgi-bin/sendsms?user=test\&pass=test\&to=$alertnumber\&text=MSG:"MobiNil+Server+Disk+Status+"$nodename+"$msg7".\&smsc=Smsc7682\&from=7682;

curl http://127.0.0.1:13014/cgi-bin/sendsms?user=test\&pass=test\&to=$alertnumber1\&text=MSG:"MobiNil+Server+Disk+Status+"$nodename+"$msg7".\&smsc=Smsc7682\&from=7682;

echo "Message Sent - $msg7"

else

echo " All the Disks have Enough Space - So No. Message Send for any Disk"

fi

#echo "--------------DONE-------------------------------------------------"
echo "[`date`]: Diskspace.sh from Node [`uname -n`]" >> /pretupsvar/pretups_cronLogs/scripts.log

###########end of Script
else
echo "`uname -n` is not active"
fi


