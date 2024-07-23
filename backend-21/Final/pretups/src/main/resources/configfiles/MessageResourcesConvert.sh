JAVA_HOME=/usr/java/jdk1.5.0_02/
PATH=$JAVA_HOME/bin:$PATH:.
cd /pretupshome/tomcat5_web/webapps/pretups/WEB-INF/classes/configfiles/
native2ascii MessageResources_ar_EG.properties >abc.txt
cp abc.txt MessageResources_ar_EG.properties
