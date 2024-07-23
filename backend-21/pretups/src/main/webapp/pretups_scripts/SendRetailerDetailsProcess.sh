source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

JAVA_OPTS="-Xms512m -Xmx512m"; export JAVA_OPTS;
#Parameter => Constants.props LogConfig.props BatchNo_separated_by_comma No_of_retailer_to_send_message
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/SendRetailerDetailsProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 1,2 3