source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
PRETUPS_HOME=$CATALINA_HOME/webapps/pretups; export PRETUPS_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;

CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5_10g.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl_1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-spi.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jsr173_api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-api.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/FastInfoset.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc-impl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crystal.jar:.

export BASH_ENV PATH CLASSPATH

cd $CATALINA_HOME/webapps/pretups/WEB-INF/src/
echo "Your CLASSPATH is "$CLASSPATH;

find com -name \*.java >java_files;

for i in `cat java_files`
do
	echo "compiling $i ";
	javac -d $PRETUPS_HOME/WEB-INF/classes/ $i;
done

\rm java_files;
