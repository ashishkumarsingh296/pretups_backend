source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/

#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
#java -Xms64m -Xmx64m com.btsl.voms.vomsprocesses.businesslogic/VoucherFileProcessor  $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/VomsFileUploadLog.props

cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

JAVA_OPTS="-Xms64m -Xmx64m"; export JAVA_OPTS;
java -classpath pretupsCore.jar:$CLASSPATH com.btsl.voms.vomsprocesses.businesslogic/VoucherFileProcessor  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/VomsFileUploadLog.props

