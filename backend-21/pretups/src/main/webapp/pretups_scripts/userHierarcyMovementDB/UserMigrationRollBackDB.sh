#UserMigrationRollBackDB.sh
#USER MIGRATION ROLLBACK SCRIPT..


#Taking the Full Back Up of the Database before droping the tables...
#logExtension=_Full_BackUp__After_Migration_Log.log
#extension=_Full_BackUp_After_Migration.dmp;
#fullBackUpFile="$1$extension"
#fullBackUpFileLog="$1$logExtension"
#exp system/manager file=$fullBackUpFile owner=$1 consistent=y statistics=none buffer=100000 log=$fullBackUpFileLog

clear
echo""
echo""
echo""
echo "DETAILS OF THE ROLLBACK DATABASE ARE AS FOLLOWS"
echo -n "TO DATABASE USER:-"
echo $1
echo -n "FROM DATABASE USER:-"
echo $3
echo -n "DATABASE BACKUP FILE NAME:-"
echo $4

echo""
echo""
echo "THIS WILL DROP SOME TABLES FROM THE DATABASE.."
echo -n "ARE YOU SURE TO CONTINUE...? (yes/no): "
read confirmstatus
if [ $confirmstatus == "yes"  -o  $confirmstatus == "YES"  -o  $confirmstatus == "y"  -o $confirmstatus == "Y" ] ; then
{

#to get the latest partition for tables CHANNEL_TRANSFERS and CHANNEL_TRANSFERS_ITEMS.
./userHierarcyMovementDB/get_Partition_Name.sh $1 $2
echo -n "ENTER LATEST PARTITION FOR CHANNEL_TRANSFERS: "
read CHANNEL_TRANSFERS_partt
echo -n "ENTER LATEST PARTITION FOR CHANNEL_TRANSFERS_ITEMS: "
read CHANNEL_TRANSFERS_ITEMS_partt

>USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USERS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table CHANNEL_USERS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_PHONES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_GEOGRAPHIES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_ROLES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_SERVICES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_BALANCES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "truncate table USER_DAILY_BALANCES;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql

echo "alter table CHANNEL_TRANSFERS_ITEMS DISABLE constraint FK_CHNL_TRANSFER_ITEMS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "alter table CHANNEL_TRANSFERS DISABLE constraint PK_CHNL_TRANSFERS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql

echo "Alter table CHANNEL_TRANSFERS_ITEMS truncate partition" $CHANNEL_TRANSFERS_ITEMS_partt";" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql

echo "Alter table CHANNEL_TRANSFERS truncate partition" $CHANNEL_TRANSFERS_partt";" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql

echo "Alter index PK_CHNL_TRANSFER_ITEMS rebuild;"  >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "Alter index UK_CHNL_TRANSFER_ITEMS rebuild;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "Alter index PK_CHNL_TRANSFERS rebuild;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "alter table CHANNEL_TRANSFERS ENABLE constraint PK_CHNL_TRANSFERS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "alter table CHANNEL_TRANSFERS_ITEMS ENABLE constraint FK_CHNL_TRANSFER_ITEMS;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "commit;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
echo "QUIT;" >> USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql

echo "Please Wait a While..."
sqlplus $1/$2  @USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql
rm USER_MIGRATION_FILE_DELETE_TABLE_LIST.sql


echo "*****************************************START IMPORTING********************************************"
rollBacklogExtension=_RollBack_Log.log
rollBacklogFile="$1$rollBacklogExtension"
imp $1/$2 fromuser=$3  touser=$1  file=$4  statistics=none commit=y ignore=Y 
#log=$rollBacklogFile
echo "******************************IMPORTED SUCCESSFULLY************************************************"
echo""
echo""
echo "******************************* POWERED BY COMVIVA *************************************************"
}
else
{
 echo "ONLY FULL DATABASE DUMP IS TAKEN, NO ROLLBACK ACTION PERFORMED."
}
fi
