###################################################################
#                Cron Script- LMSOptInOutPromotionProcess                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :15/01/2014                                 #
####################################################################


source <Tomcat-Path>/conf/pretups/commonLoadClassPath.sh

#Language setting for the SMS without this setting users will get the invalid Pos key

LANG=en_US; export LANG
PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH:.
BASH_ENV=$HOME/.bashrc
JAVA_OPTS="-Xms256m -Xmx256m"; export JAVA_OPTS;
#CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/classes:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/activation.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/mail.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/ojdbc5.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/servlet.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.9.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/cos.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-beanutils.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-collections.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-digester.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-fileupload.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-lang.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-validator.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/struts-legacy.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crystal.jar:$CATALINA_HOME/webapps/pretups/crystalclear/JavaClient.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversestub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.7.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-codec-1.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/axis-schema.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-discovery-0.2.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/commons-logging-1.0.4.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/jaxrpc.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/log4j-1.2.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/saaj.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/serializer.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wsdl4j-1.5.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xalan.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesImpl.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xercesSamples.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xml-apis.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlParserAPIs.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/xmlsec-1.2.1.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/wss4j-1.5.8.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/comversetgstub.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/crimson.jar:$CATALINA_HOME/webapps/pretups/WEB-INF/lib/bcprov-ext-jdk15-144.jar:.
export BASH_ENV PATH CLASSPATH
#cd $CATALINA_HOME/webapps/pretups/WEB-INF/classes/
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/
#java -Xms256m -Xmx256m com/btsl/pretups/processes/RunLMSForTargetCredit $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props

java -classpath pretupsCore.jar:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:$CLASSPATH com/btsl/pretups/processes/LMSOptInOutPromotionProcess $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props


