# There are 3 arguements of the process 1. Constant.props, 2. LogConfig  and
#third arguement of the file is batch type
#parameter value =(CORPORATE/NORMAL/ALL/BOTH). CORPORATE is for restricted msisdn recharge
#NORMAL is for normal msisdn recharge 
#ALL/BOTH will recharge both type of files 
#if no value is given then it will take BOTH as default value

source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/scheduletopup/process/RestrictedSubscriberTopUp $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props BOTH