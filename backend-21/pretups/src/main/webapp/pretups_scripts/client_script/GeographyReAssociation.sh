
############## Geography Re-Association ################
source <tomcat>/conf/pretups/commonLoadClassPath.sh
cd $CATALINA_HOME/webapps/pretups/WEB-INF/lib/

#1 Argument Constants.props
#2 Argument LogConfig.props
#3 Argument MessageResources.props
java -classpath pretupsCore.jar:$CLASSPATH com/client/pretups/processes/clientprocesses/GeographyReAssociation $CATALINA_HOME/conf/pretups/Constants.props $CATALINA_HOME/conf/pretups/LogConfig.props $CATALINA_HOME/webapps/pretups/WEB-INF/classes/configfiles/MessageResources.properties