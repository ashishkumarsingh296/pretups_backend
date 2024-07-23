source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH EXTGW/EXTGWTestProgram $CATALINA_HOME/webapps/pretups/WEB-INF/pretups_scripts/ConfigfileEXTGW.txt
