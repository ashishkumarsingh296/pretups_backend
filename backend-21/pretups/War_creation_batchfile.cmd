set starttime=%time%
ren pom.xml pom_bk.xml
ren pom_pretupsCore_jar.xml pom.xml
echo Building pretupsCore.jar
call mvn clean compile > pretupsCoreJarBuildLogs.txt && call mvn package
set ErrorCode = %ERRORLEVEL%
echo Exit Code = %ERRORLEVEL%
ren pom.xml pom_pretupsCore_jar.xml
ren pom_bk.xml pom.xml
copy /y target\pretupsCore.jar src\main\webapp\WEB-INF\lib
rmdir /s /q src\main\java\com\btsl
rmdir /s /q src\main\java\com\selftopup
rmdir /s /q src\main\java\com\txn
rmdir /s /q src\main\java\com\web
ren pom.xml pom_bk.xml
ren pom_inter_jar.xml pom.xml
call mvn clean compile > interJarBuildLogs.txt && call mvn package
copy /y target\inter.jar src\main\webapp\WEB-INF\lib
ren pom.xml pom_inter_jar.xml
ren pom_bk.xml pom.xml
rmdir /s /q src\main\java\com\inter
ren pom.xml pom_bk.xml
ren pom_client_jar.xml pom.xml
call mvn clean compile > clientJarBuildLogs.txt && call mvn package
copy /y target\client.jar src\main\webapp\WEB-INF\lib
ren pom.xml pom_client_jar.xml
ren pom_bk.xml pom.xml
rmdir /s /q src\main\java\com\client
ren pom.xml pom_bk.xml
ren pom_pretups.xml pom.xml
call mvn clean install > pretupsWarBuildLogs.txt
ren pom.xml pom_pretups.xml
ren pom_bk.xml pom.xml
set finishtime=%time%
echo -----------------------------------------------
echo -----------------------------------------------
echo Start time = %starttime%
echo Finish time = %finishtime%
echo -----------------------------------------------
echo -----------------------------------------------
echo You can see build logs of pretupsCore.jar in pretupsCoreJarBuildLogs.txt
echo You can see build logs of inter.jar in interJarBuildLogs.txt
echo You can see build logs of client.jar in clientJarBuildLogs.txt
echo You can see build logs of pretups.war in pretupsWarBuildLogs.txt
echo -----------------------------------------------
echo -----------------------------------------------
echo Press any key to exit this window . . . 
pause >nul