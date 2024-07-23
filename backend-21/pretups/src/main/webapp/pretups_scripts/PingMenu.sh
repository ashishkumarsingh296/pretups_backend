#**********************************
#*        Bharti TeleSoft         *
#*        Ping Test with IN       *
#*        Date :10.03.2006        *
#**********************************


IN1=10.6.6.127
IN2=10.6.6.128
IN3=130.0.1.60
SMSC1=10.7.6.23
while true
do
clear
echo "              ********************************************"
echo "              *      Pretups IN and SMSC Ping Test       *"
echo "              ********************************************"
echo "              *                                          *"
echo "              *         1.      Test Ferma1              *"
echo "              *                                          *"
echo "              *         2.      Test Ferma2              *"
echo "              *                                          *"
echo "              *         3.      Test Alcatel             *"
echo "              *                                          *"
echo "              *         4.      Test SMSCGateway         *"
echo "              *                                          *"
echo "              *         5.      Exit                     *"
echo "              *                                          *"
echo "              ********************************************"

echo "                           Choice Option:"
read opt
echo $opt

echo "Thanx for choosing  Option $opt"
case $opt in
	1) clear 
           declare -i count
           count=1
           until [ $count -gt 6 ]
           do
 	   echo "                  Testing Ferma IN - $IN1 from `uname -n`"
           echo 
           echo "                  System will do the Ping Test with Ferma IN - $IN1"
           echo
           ping -w20 $IN1 > result.txt
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $1  }' | cut -f1 -d'%'` Packets Transmitted"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $4  }' | cut -f1 -d'%'` Packets received"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $6  }' | cut -f1 -d'%'` % Packet lost"
           sleep 10
           clear 
           cat result.txt >> result$IN1.txt
           count=$count+1
           done
           clear;;
       2)  clear
           declare -i count
           count=1
           until [ $count -gt 6 ]
           do
 	   echo "                  Testing Ferma IN - $IN2 from `uname -n`"
           echo 
           echo "                  System will do the Ping Test with Ferma IN - $IN2"
           echo
           ping -w20 $IN2 > result.txt
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $1  }' | cut -f1 -d'%'` Packets Transmitted"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $4  }' | cut -f1 -d'%'` Packets received"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $6  }' | cut -f1 -d'%'` % Packet lost"
           sleep 10
           cat result.txt >> result$IN2.txt
           count=$count+1
           done
	clear;;
       3)  clear
           declare -i count
           count=1
           until [ $count -gt 6 ]
           do
 	   echo "                  Testing ALCATEL IN - $IN3 from `uname -n`"
           echo 
           echo "                  System will do the Ping Test with Ferma IN - $IN3"
           echo
           ping -w20 $IN3 > result.txt
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $1  }' | cut -f1 -d'%'` Packets Transmitted"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $4  }' | cut -f1 -d'%'` Packets received"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $6  }' | cut -f1 -d'%'` % Packet lost"
           sleep 10
           cat result.txt >> result$IN3.txt
           count=$count+1
           done
           clear;;
       4)  clear
           declare -i count
           count=1
           until [ $count -gt 6 ]
           do
 	   echo "                  Testing SMSCGateway - $SMSC1 from `uname -n`"
           echo 
           echo "                  System will do the Ping Test with Ferma IN - $SMSC1"
           echo
           ping -w20 $SMSC1 > result.txt
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $1  }' | cut -f1 -d'%'` Packets Transmitted"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $4  }' | cut -f1 -d'%'` Packets received"
           echo "                    `cat result.txt| grep "transmitted" | awk '{ print $6  }' | cut -f1 -d'%'` % Packet lost"
           sleep 10
           cat result.txt >> result$SMSC1.txt
           count=$count+1
           done
           clear;;
       5)  exit;;
esac
done

