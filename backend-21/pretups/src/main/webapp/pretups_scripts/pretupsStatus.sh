#Bharti TeleSoft 
#New Delhi
#Dated: 19/02/2006 
#Script ------ for Pretups Health Check
while true
do
clear
echo
echo
echo
echo
## Menu - check Health
echo
echo "                      *************************************************"
echo "                      *    Pretups and SMSC Gateway Health Cleck      *"
echo "                      *************************************************"
echo "                      *				 	              *"
echo "                      *       1.      Check Pretups(SMS SERVER)       *"
echo "                      *					              *"
echo "                      *       2.      Check Pretups(WEB SERVER)       *"
echo "                      *					              *"
echo "                      *       3.      Check SMSC Gateway              *"
echo "                      *					              *"
echo "                      *       4.      Check SERVER SPACE              *"
echo "                      *					              *"
echo "                      *	      5.      Exit		              *"
echo "                      *					              *"
echo "                      *************************************************"
echo
echo "                           Choice Option( 1 - 5 ):"; read opt 
echo "                           Thanks for choosing  Option $opt"
echo
echo
# Switch Case Options
case $opt in
        1) clear
           echo
           echo
           echo
	   echo "       Please Wait System is checking the Service Status of SMS SERVER..........."
           sleep 3
           clear
	   # checking the SMS Application Server status
           curl 'http://127.0.0.1:9898/pretups/test.html'
           ret=`echo $?`
           clear
           echo
           echo
           echo
	   echo
           echo
           echo "                  ***********************************************************************"
           echo "                  *                                                                     *"
           if [ $ret != 0  ] ;then
           echo "                  *        Pretups SMS Application Server is not running.               *"
           else 

           echo "                  *        Pretups SMS Application Server is running                    *"
           echo "                  *                                                                     *"
           # checking the number of threads running of SMS Application Server
           echo "                  *             SMS Application Server has `ps -e fname | grep -a "PRETUPS_SMS_JAVA" | grep -c "pretups"` Java Threads              * "
 
           fi
           echo "                  *                                                                     *"
           echo "                  ***********************************************************************"
           sleep 5 
           clear;;
        2) clear
           echo
	   echo "       Please Wait System is checking the Service Status of WEB Application SERVER..........."
           sleep 3
           clear
           # checking the Web Application Server Status
           curl 'http://127.0.0.1:5555/pretups/test.html'
           ret=`echo $?`
           clear
           echo
           echo
           echo
	   echo
           echo
           echo "                  ***********************************************************************"
           echo "                  *                                                                     *"
           if [ $ret != 0  ] ;then
           echo "                  *        Pretups WEB Application Server is not running.               *"
           else 

           echo "                  *        Pretups WEB Application Server is running                    *"
           echo "                  *                                                                     *"
           # checking  the number of threads running of Web Application Server
           echo "                  *             WEB Application Server has `ps -e fname | grep -a "PRETUPS_WEB_JAVA" | grep -c "pretups"` Java Threads              * "
 
           fi
           echo "                  *                                                                     *"
           echo "                  ***********************************************************************"
           sleep 5 
           clear;;
        3) clear
           echo
           echo
           echo 
           echo "                   Please Wait... System is Checking the Status of SMSC Gateways......................"
           echo
           sleep 3
           clear
           # Checking the SMSC Gateways Status
           curl 'http://127.0.0.1:13001/status?'
           ret=`echo $?`
           clear
           echo
           echo
           echo
	   echo
           echo
           echo "                  ***********************************************************************"
           echo "                  *                                                                     *"
           if [ $ret != 0  ] ;then
           echo "                  *        SMSC Gateway is not running.                                 *"
           else 

           echo "                  *        SMSC Gateway is running                                      *"
           echo "                  *                                                                     *"
           # checking the number of process running for SMSC Gateway
           echo "                  *        SMSC Gateway Server Number of Process on Server are `ps -e fname | grep -a "box"| grep -c "pretups"`       * "

 
           fi
           echo "                  *                                                                     *"
           echo "                  ***********************************************************************"
           sleep 5 
           clear ;;
        4) clear
           # calling the script to check the disk space available 
           sh <Tomcat-Path>/webapps/pretups/pretups_scripts/Spacecheck.sh
           sleep 5;;
	5) exit ;;
esac
done

