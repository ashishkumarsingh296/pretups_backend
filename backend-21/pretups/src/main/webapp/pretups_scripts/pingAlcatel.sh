#**********************************
#*        Bharti TeleSoft         *
#*        Ping Test with IN       *
#*        Date :10.03.2006        *
#**********************************


IN1=10.6.6.127
IN2=10.6.6.128
IN3=130.0.1.60
SMSC1=10.7.6.23
declare -i count
count=1
until [ $count -gt 3 ]
do
echo "                  Testing Alcatel IN - $IN3 from `uname -n`"
echo 
echo "                  System will do the Ping Test with Ferma IN - $IN3"
echo
ping -w20 $IN3 > resultIN3.txt
echo "                    `cat result.txt| grep "transmitted" | awk '{ print $1  }' | cut -f1 -d'%'` Packets Transmitted"
echo "                    `cat result.txt| grep "transmitted" | awk '{ print $4  }' | cut -f1 -d'%'` Packets received"
echo "                    `cat result.txt| grep "transmitted" | awk '{ print $6  }' | cut -f1 -d'%'` % Packet lost"
sleep 5
clear 
date >> result$IN3.txt
cat resultIN3.txt >> result$IN3.txt
count=$count+1
done

