rsync -rtv Final/pretups/src pretups/

chmod -R 777 pretups/src/main/java/com/btsl/security/
chmod -R 777 pretups/src/main/java/com/btsl/pretups/util/
chmod -R 777 pretups/src/main/java/com/restapi/simulator/

cp pretups/src/main/resources/application_AWS.properties pretups/src/main/resources/application.properties

chmod pretups/src/main/java/com/btsl/db/pool/
cp /home/jenkinshome/jenkins/sonartesting/C3p0PoolManager.java pretups/src/main/java/com/btsl/db/pool/C3p0PoolManager.java

cd pretups/;rm -rf target;mv pom.xml pom_bk.xml;mv pom_pretupsCore_jar.xml pom.xml;mvn compile && mvn -U package

rm -rf src/main/webapp/WEB-INF/lib/pretupsCore.jar;rsync -rv target/pretupsCore.jar src/main/webapp/WEB-INF/lib;mv pom.xml pom_pretupsCore_jar.xml;mv pom_bk.xml pom.xml;rm -rf target;rm -rf src/main/java/com/btsl;rm -rf src/main/java/com/selftopup;rm -rf src/main/java/com/txn;rm -rf src/main/java/com/web;rm -rf src/main/java/com/restapi;rm -rf src/main/java/EXTGW;rm -rf src/main/java/loadtest;rm -rf src/main/java/ussd;mv pom.xml pom_bk.xml;mv pom_inter_jar.xml pom.xml;mvn compile && mvn package

rm -rf src/main/webapp/WEB-INF/lib/inter.jar;rsync -rv target/inter.jar src/main/webapp/WEB-INF/lib;mv pom.xml pom_inter_jar.xml;mv pom_bk.xml pom.xml;rm -rf src/main/java/com/inter;mvn clean compile && mvn package

mv pom_pretups.xml pom.xml;

mvn clean install

mv pom.xml pom_pretups.xml;

zip -d target/pretups.war WEB-INF/lib/log4j-to-slf4j-2.10.0.jar;
zip -d target/pretups.war WEB-INF/lib/slf4j-api-1.7.25.jar;

cd ..