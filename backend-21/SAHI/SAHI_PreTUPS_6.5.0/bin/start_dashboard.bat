@ECHO ON
SET SAHI_HOME=..\..
SET SAHI_USERDATA_DIR=..
SET SAHI_EXT_CLASS_PATH="../extlib/*";%SAHI_HOME%/extlib/db/ojdbc14.jar;
CALL setjava.bat
CALL %SAHI_HOME%\bin\dashboard.bat