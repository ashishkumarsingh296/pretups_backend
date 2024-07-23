HOME1=/pretupshome;export HOME1
HOME2=/pretupshome;export HOME2
HOME=/pretupshome;export HOME
JAVA_HOME=/usr/java/jdk1.5.0_02; export JAVA_HOME
PRETUPS_HOME=/pretupshome/tomcat5_web/webapps/pretups; export PRETUPS_HOME
CATALINA_HOME=$HOME/tomcat5_web; export CATALINA_HOME
#Language setting for the SMS without this setting users will get the invalid Pos key
LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc

echo $PATH

JAVA_OPTS="-Xms32m -Xmx32m"; export JAVA_OPTS;
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jxl.jar:.

export BASH_ENV PATH CLASSPATH

cd /pretupshome/tomcat5_web/webapps/pretups/WEB-INF/src/
echo "Your CLASSPATH is "$CLASSPATH

echo compileing btsl.common

javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/util/Test.java
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/util/LoadTest1.java
javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/util/LoadTestThreaded.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/gateway/businesslogic/PushMessage.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/common/PretupsI.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/web/FOCBatchEnquiryAction.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/businesslogic/FOCBatchMasterVO.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/businesslogic/FOCBatchTransferDAO.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/processes/BalanceMismatchAlert.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/common/ListValueVO.java
#javac -d $PRETUPS_HOME/WEB-INF/classes/ com/btsl/pretups/channel/transfer/web/FOCBatchTransferApprovalAction.java













