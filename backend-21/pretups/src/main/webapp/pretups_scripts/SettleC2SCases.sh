source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 arguement Constants.props
#2 arguement LogConfig.props
#3 arguement file name with path - in input file there are two parameters  - (transaction_id,S/F) S for success and F for fail
#4 arguement sleep time 100


java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/channel/user/businesslogic/HandleUnsettledCases $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props  <input_File_Name_with_Path> 100