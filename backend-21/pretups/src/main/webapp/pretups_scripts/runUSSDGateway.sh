source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH ussd/USSDTestProgram $CATALINA_HOME/webapps/pretups/WEB-INF/pretups_scripts/ConfigfileUSSD.txt
