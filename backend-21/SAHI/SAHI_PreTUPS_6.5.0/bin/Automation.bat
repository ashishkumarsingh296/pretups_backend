:: This file is been used for running all the pre-requisities required for running Sahi Pro
:: as it helps to run SahiPro perfectly without doing any changes in the file manually in Pretups Application
:: /Y tells XCOPY to overwrite files without asking for confirmation
:: @author - Vishal Agarwal

@ECHO OFF

echo ==================================================================
echo 1) PretupsTA folder with its contents is going to get copied into SAHI scripts folder.
echo 2) After it has been copied, please open the SAHI Dashboard. Click on "scripts" link to go to the SAHI scripts folder.
echo 3) Then scripts can be executed using "testrunner" OR through Web link present in the SAHI Dashboard.
echo -------
echo Example:
echo -------
echo testrunner PretupsTA\scripts\GUI\MasterSuite_GUI.csv http://172.16.1.121:9922/pretups firefox "Y"
echo ==================================================================
echo.

set /p SAHIPATH="Enter the path where SAHI is installed(e.g. F:\SahiPro\): " %=%

:: This file is being copied to help the user to set the logs path, no of threads, run multiple/single session in testrunner.bat file as testrunner is been used to run Sahi scripts, suite and csv files
XCOPY testrunner.bat %SAHIPATH%\userdata\bin\ /Y

:: This jar file is being copied to establish ojdbc connection with the DB that will fetch the data from DB and provide those data in GUI
XCOPY ..\lib\ojdbc14.jar %SAHIPATH%\extlib\db\ /Y

:: This file is being copied to set ojdbc path in the Sahi Pro to establish database connection
XCOPY start_dashboard.bat %SAHIPATH%\userdata\bin\ /Y

:: This file is being copied to help the user to set the logs path, no of threads, run multiple/single session in testrunner.bat file as testrunner is been used to run Sahi scripts, suite and csv files
XCOPY PretupsRun.bat %SAHIPATH%\userdata\bin\ /Y

:: PretupsTA folder will automatically move into SAHI scripts folder
XCOPY "..\..\PretupsTA" "%SAHIPATH%\userdata\scripts\PretupsTA" /D /E /C /R /I /K /Y