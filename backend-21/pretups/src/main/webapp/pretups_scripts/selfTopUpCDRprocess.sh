cd ~
source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/classes:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/selftopup/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:.
export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/selftopup/WEB-INF/classes/

java com/selftopup/pretups/processes/SelfTopUpCDRCreation  $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/webapps/selftopup/WEB-INF/classes/configfiles/selftopup/ProcessLogConfig.props
