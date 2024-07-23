>myprofile.sh
cp ./bash_profile myprofile.sh
chmod +x myprofile.sh

#For getting the latest partition name for channel_transfer and channel_transfer_items.
if test $# -eq 2  ; then
{
echo "./userHierarcyMovementDB/get_Partition_Name.sh $1 $2" >>myprofile.sh 
./myprofile.sh
       if [ $?  -ne 0 ] ; then
                echo "ERROR - $1 failed with $?"
                exit 1
        fi
}
fi

#For taking backup of the data before migration process.
if test $# -eq 5  ; then
{
echo "./userHierarcyMovementDB/UserMigrationBackUpDB.sh $1 $2 $3 $4 $5" >> myprofile.sh
./myprofile.sh

 if [ $?  -ne 0 ] ; then
                echo "ERROR - $1 failed with $?"
                exit 1
 fi
}
fi

#For rolling back the migration process.
if  test $# -eq 4  ; then 
{
echo "./userHierarcyMovementDB/UserMigrationRollBackDB.sh $1 $2 $3 $4" >> myprofile.sh
./myprofile.sh

 if [ $?  -ne 0 ] ; then
                echo "ERROR - $1 failed with $?"
                exit 1
        fi
}
fi


rm myprofile.sh
