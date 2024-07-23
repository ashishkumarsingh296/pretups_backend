declare -i count

count=1
until [ $count -gt 160 ]
do
clear
echo 
echo
echo "*************************************************"
echo "*   SMS Application Server has `ps -e fname | grep -a "PRETUPS_SMS_JAVA" | grep -c "pretups"` Java Threads *  "
echo "*                                               *" 
echo "*   WEB Application Server has `ps -e fname | grep -a "PRETUPS_WEB_JAVA" | grep -c "pretups"` Java Threads  * "
echo "*                                               *"
echo "*   Total connection with DB Server are `netstat -o|grep -c 10.7.92.230:1521`     *"
echo "*************************************************"
echo
sleep 5
clear
count=$count+1
done

