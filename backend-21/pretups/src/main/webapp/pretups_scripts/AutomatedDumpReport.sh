source <TOMCAT-PATH>/conf/pretups/commonLoadClassPath.sh

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/




java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/csvgenerator/clientcsvgenerator/CSVMinutelyFileGeneratorProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/csvConfigFileMinutely.props


#cd $CATALINA_HOME/logs/ADR_Final_Data/
##find ./ -name *.csv -ctime +7 -exec rm -f {} \;
##echo "Files older than 7 days has been deleted."
echo "Script executed"
