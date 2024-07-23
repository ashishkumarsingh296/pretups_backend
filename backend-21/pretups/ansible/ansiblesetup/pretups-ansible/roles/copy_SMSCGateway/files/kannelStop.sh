echo "Stopping SMSC Gateway"
ps ax | grep bearerbox | awk '{print "kill -9 "$1}'|sh
ps ax | grep smsbox | awk '{print "kill -9 "$1}'|sh
echo "Stopped SMSC Gateway"
