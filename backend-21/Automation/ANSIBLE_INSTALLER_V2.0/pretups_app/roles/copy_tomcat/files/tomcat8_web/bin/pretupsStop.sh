echo " "
echo "**************************************************************************************"
echo " Stopping the PreTUPS 6.5.0 RoadMap Server, Please wait....."
echo ""
echo ""
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/bin/
echo "Your CLASSPATH is "$CLASSPATH
echo "Your Path is "$PATH
./shutdown.sh
abc=`ps -aef| grep -a /home/pretups/tomcat8_duplicate |awk  '{ print $2 }'|cut -f1`
echo $abc
kill -15 $abc
#killall java
clear
echo "***************************************************************************************"
echo ""
echo ""
echo " Stopping the PreTUPS 6.5.0 RoadMap Server, Please wait....."
sleep 5
#./startup.sh
echo ""
echo ""
echo ""
echo ""
echo " ******************************** Powered by Mahindra Comviva *********************************"


