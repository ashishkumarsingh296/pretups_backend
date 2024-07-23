source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
# This Script Insert Subscriber Retailer Mapping for Activation Bonus
#java com/btsl/pretups/processes/ActivationSubscriberRetailerMapping $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/Constants.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/LogConfig.props

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/ActivationSubscriberRetailerMapping $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props
