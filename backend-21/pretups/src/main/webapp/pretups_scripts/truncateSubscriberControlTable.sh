#truncateSubscriberControlTable.sh
#---------------------------------------------------------------
# Calls the query to truncate Subscriber Control Table
# Gurjeet               28/09/2006
#---------------------------------------------------------------
export LD_ASSUME_KERNEL=2.4.19
export ORACLE_BASE=/oracle
export ORACLE_HOME=/oracle/ora9i
export ORACLE_SID=prtp
export LD_LIBRARY_PATH=$ORACLE_HOME/lib:/usr/lib:/usr/local/lib
export PATH=$ORACLE_HOME/bin:$PATH
export ORACLE_TERM=xterm
export EDITOR=vi

cd /data/backup_scripts
#echo "Reached After cd"
echo "Starting [`date`]"
sqlplus pretups_live/pretups_live @truncateSubscriberControlTable.sql
echo "Ending [`date`]"
exit;
