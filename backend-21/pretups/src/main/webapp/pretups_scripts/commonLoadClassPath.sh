HOME=<HOME_DIR>; export HOME
JAVA_HOME=$HOME/<JAVA_INSTALLED_LINK>; export JAVA_HOME
CATALINA_HOME=$HOME/<TOMCAT_INSTALLED_DIR>; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

APPPATH=$CATALINA_HOME/webapps/pretups/WEB-INF;

CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$APPPATH/classes;

for file in $(find $APPPATH/lib/ -name "*.jar" -print); do
CLASSPATH=${CLASSPATH}":"${file}
done;

JAVA_OPTS="-Xms512m -Xmx512m"; export JAVA_OPTS;
export BASH_ENV PATH CLASSPATH



