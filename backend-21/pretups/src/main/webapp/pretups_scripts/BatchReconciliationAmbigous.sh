source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 argument Constants.props
#2 argument ProcessLogConfig.props
#3 argument file name with path (if file name start with RP2P means process will execute for C2S reconciliation and 
#           if file name start with CP2P means process will execute for P2P reconciliation.) 
#java com/btsl/pretups/processes/ReconcileUnsettledAmbiguousCases $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props /pretupshome/ambigousCase/RP2PFile1.txt

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ReconcileUnsettledAmbiguousCases $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props <Tomcat-Path>/ambigousCase/<Text-file>



echo "Finish"
