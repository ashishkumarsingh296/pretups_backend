source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/

#java com/btsl/pretups/processes/CDRGeneratorProcess $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props INTID00007 CDR

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/CDRGeneratorProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props INTID00007 CDR
#java com/btsl/pretups/inter/post/TestDate 


