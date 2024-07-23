HOME=/home/safaricom_dev; export HOME
JAVA_HOME=/home/safaricom_dev/SAFARICOM_DEV; export JAVA_HOME
CATALINA_HOME=$HOME/tomcat5; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
#JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
#CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/Oranxo.jar:$CATALINA_HOME/webapps/crystal/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/crystal/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/safaricomstub.jar:.
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc14.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/rsaexternal.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/edtftpj-1.5.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/safaricomstub.jar:.

export BASH_ENV PATH CLASSPATH


cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
JAVA_OPTS="-Xms512m -Xmx512m"; export JAVA_OPTS;
java -Xms512m -Xmx512m com/btsl/loadtest/loadgenerator/LoadTest /home/safaricom_dev/tomcat5/webapps/pretups/WEB-INF/classes/com/btsl/loadtest/loadgenerator/LoadTest.properties /home/safaricom_dev/tomcat5/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props


