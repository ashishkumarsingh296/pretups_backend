###################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
# check for Services On which node Services are running
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh


###Changes made by Inderpreet Singh for Grameenphone###
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
file_path=<Tomcat-Path>/logs/Ambigous
process_file_path=<Tomcat-Path>/webapps/pretups/WEB-INF/classes
back_up_path=<Tomcat-Path>/logs/BulkAmbiBackup/




cd $file_path
file_list=$(ls RP2P*.txt CP2P*.txt)
echo "File_List="$file_list
total=$(echo $file_list| wc -w)
echo "Total Number of Files="$total



for file in $file_list
do
cd $process_file_path
process_file=<Tomcat-Path>/logs/Ambigous/$file
echo "Processing File:"$process_file
java -classpath $CATALINA_HOME/webapps/pretups/WEB-INF/lib/pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ReconcileUnsettledAmbiguousCases $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $process_file
mv <Tomcat-Path>/logs/Ambigous/$file $back_up_path
done
