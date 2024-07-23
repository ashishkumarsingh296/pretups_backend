source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh


#Language setting for the SMS without this setting users will get the invalid Pos key
#LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-server -Xss2m -Xms512m -Xmx512m -Xmn256m"
export JAVA_OPTS;

export BASH_ENV PATH CLASSPATH

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

echo "O2CSendEmail Process Started......................................................................"

#java com/btsl/pretups/processes/O2CSendEmailProcess $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props

java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/pretups/processes/O2CSendEmailProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

echo "O2CSendEmail Process Completed...................................................................."

