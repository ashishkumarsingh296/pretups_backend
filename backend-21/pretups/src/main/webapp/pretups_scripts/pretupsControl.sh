#Bharti TeleSoft 
#New Delhi
#Dated:21/02/06
#Script ------ for  Starting n Stopping Pretups
while true
do
clear
echo
echo
echo
echo
echo
echo "              ********************************************"
echo "              *       Start Or Stop Pretups              *"
echo "              ********************************************"
echo "              *				 	         *"
echo "              *	      1.      Start Pretup               *"
echo "              *					         *"
echo "              *	      2.      Stop  Pretups              *"
echo "              *                                          *"
echo "              *	      3.      Check Pretups Services     *"
echo "              *					         *"
echo "              *	      4.      Exit		         *"
echo "              *					         *"
echo "              ********************************************"

echo "                           Choice Option:"
read opt
echo "Thanx for choosing  Option $opt"
case $opt in
	1) echo 
           echo
           USER="`id -un`"
           LOGNAME=$USER
           if [ $USER = "root" ] ; then
                echo
                echo "***********************************************************************"
	        echo "*     You must be logged on AS SUPER USER to Start the Services       *"
                echo "*         You have Logged on as $LOGNAME                              *"
           	echo "*         Please Wait Services are starting  on `uname -n`.....       *"
                echo "*                                                                     *"
                echo "***********************************************************************"
                sleep 3
                # Starting the Cluster Manager
#                service clumanager start
#               sh /<Tomcat-Path>/webapps/pretups/pretups_scripts/appControl.sh 1
                clear
                echo "***********************************************************************"
	        echo "*     You must be logged on AS SUPER USER to Start the Services       *"
                echo "*                                                                     *"
                echo "*         Services are started on `uname -n`                          *"                               
                echo "*                                                                     *"
                echo "***********************************************************************"
           else
                echo "***********************************************************************"
	        echo "*     You must be logged on AS SUPER USER to Start the Services       *"
                echo "*                                                                     *"
                echo "*         You have Logged on as $LOGNAME                               *"
                echo "*                                                                     *"
		echo "*    Sorry You are not authorised to Start the Server Services        *"
                echo "*                                                                     *"
                echo "***********************************************************************"
           fi
           echo 
           sleep 5
           clear;;
	2) echo    
           echo
           USER="`id -un`"
           LOGNAME=$USER
           if [ $USER = "root" ] ; then
                echo
                echo "***********************************************************************"
                echo "*     You must be logged on AS SUPER USER to Stop the Services        *"
                echo "*         You have Logged on as $LOGNAME                              *"
                echo "*         Please Wait Services are stopping  on `uname -n`.....       *"
                echo "*                                                                     *"
                echo "***********************************************************************"
                sleep 3
                # Stopping the Cluster Manager
#               service clumanager stop
#               sh /<Tomcat-Path>/webapps/pretups/pretups_scripts/appControl.sh 2

                clear
                echo "***********************************************************************"
                echo "*     You must be logged on AS SUPER USER to stop the Services        *"
                echo "*                                                                     *"
                echo "*         Services are stopped on `uname -n`                          *"
                echo "*                                                                     *"
                echo "***********************************************************************"
           else
                echo "***********************************************************************"
                echo "*     You must be logged on AS SUPER USER to Start the Services       *"
                echo "*                                                                     *"
                echo "*         You have Logged on as $LOGNAME                               *"
                echo "*                                                                     *"
                echo "*    Sorry You are not authorised to Start the Server Services        *"
                echo "*                                                                     *"
                echo "***********************************************************************"
           fi
           echo
           sleep 5
	   clear;;
        3) clear
	 echo
           echo "       Please Wait System is checking the Service Status of SERVER..........."
           sleep 3
           clear
           # checking the Web Application Server Status
           curl 'http://10.7.92.226:5555/pretups/test.html'
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
echo "                  *                `uname -n` is not Active Cluster Node              *"
           else

           echo "                  *                `uname -n` is Active Cluster Node                  *"
           fi
           echo "                  *                                                                     *"
           echo "                  ***********************************************************************"
           sleep 5
           clear;;
	4) exit;;
esac
done

