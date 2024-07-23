##################### testing check #################
# Created by PreTUPS Team  dated : 19/10/2011       #
# By Rajeev Gosain                                  #
#####################################################

############# Construct the URL  ###############
###### IP of the server defined to be checked ###############

IP1=10.201.0.38
IP2=10.201.0.39
IP3=10.201.0.40
IP4=10.201.0.41
IP5=10.201.0.42
IP6=10.201.0.36
IP7=10.201.0.37

##########Port of the server defined to be checked ###############

prt1=9898
prt2=9898
prt3=9898
prt4=9898
prt5=9898
prt6=5555
prt7=5555


###### MSISDN ###############

MSISDN1=8801610005070
MSISDN2=8801610005010
MSISDN3=8801610005444
MSISDN4=8801610005678

######### Name of Running Instance in Instance Load Table ###############
InsName1="Pretups Application server1"
InsName2="Pretups Application server2"
InsName3="Pretups Application server3"
InsName4="Pretups Application server4"
InsName5="Pretups Application server5"
InsName6="WebServer1"
InsName7="WebServer2"

echo `date`
echo "_____________________________"
echo "Checking the Instance        "
echo "_____________________________"

#########  Variable Definition ####################
declare -i n1
declare -i c
declare -i var
declare -i MSISDN

############# Assigning the value to Variable Definition
##prt=$prt1
##InsName=$InsName1
##IP=$IP1

c=1
while [[ $c -le 7 ]];
do

########## Construction of URL ##############################
case "$c" in
        1)
        IP=$IP1
        prt=$prt1
        InsName=$InsName1;;
        2)
        IP=$IP2
        prt=$prt2
        InsName=$InsName2;;
        3)
        IP=$IP3
        prt=$prt3
        InsName=$InsName3;;
        4)
        IP=$IP4
        prt=$prt4
        InsName=$InsName4;;
        5)
        IP=$IP5
        prt=$prt5
        InsName=$InsName5;;
        6)
        IP=$IP6
        prt=$prt6
        InsName=$InsName6;;
        7)
        IP=$IP7
        prt=$prt7
        InsName=$InsName7;;
esac

msg="http://$IP:$prt/pretups/test.html"

echo $msg
echo $InsName

# check for Web Report Services
        curl $msg
        ret=`echo $?`
        if [ $ret != 0  ] ;then
              echo "[`date`]: PreTUPS Server $InsName is Not Running [Server Check Script]" >>/<Tomcat-Path>/logs/ServerCheck.log
              curl 'http://10.201.0.38:13013/cgi-bin/sendsms?user=test&pass=test&to=$MSISDN1&text=Service+on+$InsName+is+not+running+kindly+check.&smsc=SMSC9702&from=3281'
        else
              echo "Services on $InsName are running fine"
        fi
c=`expr $c + 1`
done
