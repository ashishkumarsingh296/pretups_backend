source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-Xms256m -Xmx256m"; export JAVA_OPTS;

export BASH_ENV PATH CLASSPATH

#cd ussd/
#javac USSDTestProgram.java
java ussd.USSDTestProgram /pretupshome/pretups58_dev/tomcat5_web/ussd/Configfile_GMB.txt XML
