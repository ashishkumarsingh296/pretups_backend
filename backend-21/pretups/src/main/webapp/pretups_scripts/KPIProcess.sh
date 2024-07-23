source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

PRETUPS_HOME=$CATALINA_HOME/webapps/pretups; export PRETUPS_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
#PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
#BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
#CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/rsaexternal.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/edtftpj-1.5.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/safaricomstub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/sxfwcipher1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cipherdecrypt1.jar:.


#export BASH_ENV PATH CLASSPATH

#cd $PRETUPS_HOME/WEB-INF/src/
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/processes/KPIProcess.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/processes/KPIReportWriteInXLS.java
#cd $PRETUPS_HOME/WEB-INF/classes/
cd $PRETUPS_HOME/WEB-INF/lib/

#first argument Constants.props
#second argument ProcessLogConfig.props
#Third argument KPIConfig.props
#Fourth argument Date (dd/MM/yyyy). if want to run for paticular date. if blank then default will be current date
#java com/btsl/pretups/processes/KPIProcess $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/ProcessLogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/KPIConfig.props

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/KPIProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/KPIConfig.props






