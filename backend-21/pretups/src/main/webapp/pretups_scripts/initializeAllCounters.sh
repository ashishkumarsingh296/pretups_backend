echo "Initializing all counters by zero.................";
sleep 5 ;
 curl 'http://10.7.92.4:9898/pretups/monitorserver/initialiseCountersAll.jsp?instanceID=2';
clear;
echo "Counters are initialized successfully..................."
