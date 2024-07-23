#!/bin/sh
rm -rf target;mv pom.xml pom_bk.xml;mv pom_pretupsCore_jar.xml pom.xml;mvn compile && mvn -U package

rm -rf src/main/webapp/WEB-INF/lib/pretupsCore.jar;rsync -rv target/pretupsCore.jar src/main/webapp/WEB-INF/lib;mv pom.xml pom_pretupsCore_jar.xml;mv pom_bk.xml pom.xml;rm -rf target;rm -rf src/main/java/com/selftopup;rm -rf src/main/java/com/client;rm -rf src/main/java/com/inter;rm -d;mv pom.xml pom_bk.xml;mv pom_inter_jar.xml pom.xml;mvn compile && mvn package

rm -rf src/main/webapp/WEB-INF/lib/inter.jar;rsync -rv target/inter.jar src/main/webapp/WEB-INF/lib;mv pom.xml pom_inter_jar.xml;mv pom_bk.xml pom.xml;rm -rf src/main/java/com/inter;mvn clean compile && mvn package

rm -rf src/main/java/com/client;mv pom_pretups.xml pom.xml;

mvn clean install

mv pom.xml pom_pretups.xml;
