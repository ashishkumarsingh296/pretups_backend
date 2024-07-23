source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-Xms128m -Xmx128m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:.
export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
#To Drop the table uncomment in live env.. 
#Now no need to uncomment this bcoz this is handled in shell script not in java code..
#java com/migrationtool/UserMigRollBack  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props

clear
echo "################### MIGRATION FULL ROLLBACK PROCESS ###############################################"
echo -n "ENTER THE ORALCE USER: "
read oracleuserid
echo -n "ENTER THE ORALCE SERVER IP: "
read oracleserverip
echo -n "ENTER THE DATABASE USER TO WHERE YOU WANT TO IMPORT: "
read todatabaseuser
echo -n "ENTER THE DATABASE USER PASSWORD TO WHERE YOU WANT TO IMPORT: "
stty -echo
read todatabasepassword
stty echo
echo ""
echo -n "ENTER THE DATABASE USER FROM WHERE YOU HAVE IMPORTED THE DATABASE DUMP: "
read fromdatabaseuser
echo -n "ENTER THE BACKUP FILE NAME(with out extension): "
read backupfile

if  [ -n "${oracleuserid}" ] &&  [ -n "${oracleserverip}" ] && [ -n "${todatabaseuser}" ] && [ -n "${backupfile}" ] && [ -n "${fromdatabaseuser}" ] &&[ -n "${todatabasepassword}" ];  then
{
extension=.dmp;
backupfile="$backupfile$extension"

ssh $oracleuserid@$oracleserverip  ./userHierarcyMovementDB/userMigrationCommon.sh $todatabaseuser $todatabasepassword $fromdatabaseuser  $backupfile
	 if [ $?  -ne 0 ] ; then
                echo "ERROR - $1 failed with $?"
                exit 1
        fi
}
else
{
	echo "ERROR:YOU HAVE MISSED ANY INPUT FIELD,SO PLEASE TRY AGAIN....!"
}
fi
