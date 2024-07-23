#FILE:- UserMigrationBackUpDB.sh
#PURPOSE:- TO TAKE BACKUP OF THE DATABASE AT THE START OF THE USER MIGRATION PROCESS.

extension=.dmp;
backupfile="$5$extension"

logExtension=_Log.log
BackUpFileLog="$5$logExtension"

echo "*****************************************START TAKING BACKUP********************************************"

#Taking the Full Back Up of the Database before droping the tables...
logExtension=_Full_BackUp_Before_Migration_Log.log
extension=_Full_BackUp_Before_Migration.dmp;
fullBackUpFile="$1$extension"
fullBackUpFileLog="$1$logExtension"
#uncomment this.
#exp system/manager file=$fullBackUpFile owner=$1 consistent=y statistics=none buffer=100000 log=$fullBackUpFileLog
exp $1/$2 file=$fullBackUpFile owner=$1 consistent=y statistics=none buffer=100000 log=$fullBackUpFileLog


#working.
#exp $1/$2  file=$backupfile tables=USERS,CHANNEL_USERS,USER_PHONES,USER_GEOGRAPHIES,USER_ROLES,USER_SERVICES,USER_BALANCES,USER_DAILY_BALANCES,CHANNEL_TRANSFERS:$3,CHANNEL_TRANSFERS_ITEMS:$4 statistics=none log=$BackUpFileLog

#exp $1/$2  file=$backupfile tables=CHANNEL_TRANSFERS:$3,CHANNEL_TRANSFERS_ITEMS:$4,CHANNEL_TRANSFERS,CHANNEL_TRANSFERS_ITEMS statistics=none log=$BackUpFileLog

#For Testing Purpose Only..
#exp $1/$2  file=$backupfile tables=NAME log=$fullBackUpFileLog
#exp pretups_ocm/pretups_ocm  file=ocm_pretups.dmp tables=NAME log=$BackUpFileLog


#For taking the backup of the login_id column in the users table.
#echo -n "DO YOU WANT TO TAKE BACKUP OF LOGIN_ID's.. ? (YES/NO)"
#read confirmstatus
#if [ $confirmstatus == "yes"  -o  $confirmstatus == "YES"  -o  $confirmstatus == "y"  -o $confirmstatus == "Y" ] ; then
#{
>USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "ALTER TABLE USERS ADD OLD_LOGIN_ID VARCHAR2(20);" >> USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "COMMIT;" >> USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "UPDATE USERS SET OLD_LOGIN_ID=LOGIN_ID;" >> USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "COMMIT;" >> USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "QUIT;"  >> USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
echo "Please Wait a While..."
sqlplus $1/$2 @USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
rm USER_MIGRATION_FILE_BACKUP_ALTER_USERS_TABLE.sql
#}
#fi

echo""
echo""
echo "****************************** BACKUP PROCESS COMPLETED ************************************************"
echo ""
echo""
echo "********************************* POWERED BY COMVIVA ***************************************************"
echo ""
echo""
