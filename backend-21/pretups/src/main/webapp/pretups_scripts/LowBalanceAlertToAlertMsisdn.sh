###################################################################
#                Low Balance Alert to Alert MSISDN                 #
#                Comviva Technologies Pvt.LTD.                     #
#                Dated :08/07/2011                                 #
####################################################################
# check for Services On which node Services are running
curl 'http://127.0.0.1:5555/pretups/test.html'
ret=`echo $?`
if [ $ret = 0 ] ; then
        echo "`uname -n` is active"


source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

export BASH_ENV PATH CLASSPATH


cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
java -classpath pretupsCore.jar:$CLASSPATH com/btsl/pretups/processes/LowBalanceAlertToAlertMsisdn $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

else
        echo "`uname -n` is not active"
fi
