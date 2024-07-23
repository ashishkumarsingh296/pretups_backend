clear

#echo "Please Wait a While ......"
#This java class will create temporary column to manage the login_id of the migrated user while rollback process.
#(uncomment)(no need to uncomment because this is handeled in shell script.)
#java com/migrationtool/UserMigBackUp  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props
echo "####################### MIGRATION BACKUP PROCESS ###########################################"
echo ""
echo ""
echo -n "ENTER THE ORACLE DATABASE SEREVR USER: "
read oracleuserid
echo -n "ENTER THE ORALCE DATABASE SERVER IP: "
read oracleserverip
echo -n "ENTER THE DATABASE USER: "
read databaseuser
echo -n "ENTER THE DATABASE PASSWORD: "
stty -echo
read databasepassword
stty echo

if  [ -n "${oracleuserid}" ] &&  [ -n "${oracleserverip}" ] && [ -n "${databaseuser}" ] && [ -n "${databasepassword}" ] ;  then
{
        clear
        echo "ENTER THE DATABASE SERVER PASSWORD:"
        ssh $oracleuserid@$oracleserverip  ./userHierarcyMovementDB/userMigrationCommon.sh $databaseuser $databasepassword
       	       if [ $?  -ne 0 ] ; then
                echo "ERROR - $1 failed with $?"
                exit 1
	        fi
 
	echo -n "ENTER LATEST PARTITION FOR CHANNEL_TRANSFERS: "
        read CHANNEL_TRANSFERS_part
        echo -n "ENTER LATEST PARTITION FOR CHANNEL_TRANSFERS_ITEMS: "
        read CHANNEL_TRANSFERS_ITEMS_part
       if [ -n "${CHANNEL_TRANSFERS_part}" ] &&  [ -n "${CHANNEL_TRANSFERS_ITEMS_part}" ] ; then
        {
         clear
		echo -n "ENTER THE BACKUP FILE NAME(without extension): "
		read backupfile
		
		#dumpdate=`date "+%d%m%Y"`
		#backupfilename="MASTERBACKUP_BEFORE_MIGRATION_"
		#backupfile=$backupfilename$dumpdate
		if [ -n "${oracleuserid}" ] ; then
		{
		echo ""
		echo ""
                echo "FOR SECURITY REASONS, PLEASE ENTER THE DATABASE SERVER PASSWORD AGAIN: "
	ssh $oracleuserid@$oracleserverip  ./userHierarcyMovementDB/userMigrationCommon.sh $databaseuser $databasepassword $CHANNEL_TRANSFERS_part $CHANNEL_TRANSFERS_ITEMS_part $backupfile

			 if [ $?  -ne 0 ] ; then
        		        echo "ERROR - $1 failed with $?"
		                exit 1
	        	 fi

		}
		else
		{
			echo "ERROR:YOU HAVE MISSED BACKUP FILE NAME,SO PLEASE TRY AGAIN....!"
		}
		fi
        }
        else
        {
         echo "ERROR:YOU HAVE MISSED ANY OF THE PARTITION NAME, SO PLEASE TRY AGAIN....!"
        }
        fi
 
}
else
{
        echo "ERROR:YOU HAVE MISSED ANY INPUT FIELD,SO PLEASE TRY AGAIN....!"
}
fi
