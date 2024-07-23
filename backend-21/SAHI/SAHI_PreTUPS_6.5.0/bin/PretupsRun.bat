:: This file is been used for running the master suite file where all the test cases are kept
:: @author - Vishal Agarwal

@ECHO OFF

:: Setting the application URL value
set /p APP_URL="Enter the application URL else it will take the URL from PretupsConfigInputs.xlsx(e.g. http://172.16.1.121:9922/pretups/): " %=%

IF "%APP_URL%"=="" (
	:: Command to run test cases present in master suite
	testrunner PretupsTA\scripts\GUI\Pretups_MasterSuite_GUI.csv http://172.16.1.121:9922/pretups/ firefox "Y"
) ELSE (
	:: Command to run test cases present in master suite
    testrunner PretupsTA\scripts\GUI\Pretups_MasterSuite_GUI.csv %APP_URL% firefox "Y"
)