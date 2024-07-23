source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/


JAVA_OPTS="-Xms256m -Xmx256m"; export JAVA_OPTS;
java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com.btsl.voms.vomsprocesses.businesslogic/CummulativeInventoryAmtReport  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props 0
