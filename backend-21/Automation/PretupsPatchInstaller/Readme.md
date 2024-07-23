#####################################################################################################################
#
#            ----------------------PATCH INSTALLER Read Me-----------------------------
#
# Author: PreTUPS_RoadMap
# Date: 25/07/2016
#
######################################################################################################################

Ansible Role for Patch Installation:
-------------------------------------
	This Playbook installs Patch over different hosts according to the configuration given.
	
	Types of Patch supported by this playbook:
		-'WAR'
		-'JAR'
		-'JSP'
		-'JAVA'
		-'CLASSFILE'
		-'PROP-NEWLINE'
		-'PROP-MODIFY'
		-'PROP-REMOVE'
		-'FILE-SYNC'
        	-'DB-SCRIPTS'
		
WAR:
---- 
	The patch type 'WAR' is responsible for taking the backup of existing WAR Instance to the backup path mentioned and copies the new war to the destination hosts and extracts it.
	
JAR:
----
	The patch type 'JAR' is responsible for taking the backup of old jar with a backup extension  mentioned by user  to the location where the file resides and copies the new jar to the that location

JSP:
----
	The patch type 'JSP' is responsible for taking the backup of existing jsp file with a backup extension mentioned by user to the location where the file resides and copies the new jsp file to that location.

JAVA:
-----
	The patch type 'JAVA' is responsible for taking the backup of existing class file with a backup extension mentioned by user to the location where the file resides and copies the new java file to that location.
	
CLASSFILE:
----------
	The patch type 'CLASSFILE' is responsible for taking the backup of existing class file with a backup extension  mentioned by user  to the location where the file resides and copies the new classfile file to that location.

PROP-NEWLINE:	
-------------
	The patch type 'PROP-NEWLINE' is responsible to Add  new lines to the property file which is mentioned by the user.
	
PROP-MODIFY:
-------------
	The patch type 'PROP-MODIFY' is responsible to modify the existing line in the property file by key value pair.
	

PROP-REMOVE:
-------------
	The patch type 'PROP-REMOVE' is responsible to remove lines from the property file which is mentioned by the user.

FILE-SYNC:
----------
	The patch type 'FILE-SYNC' is responsible for if any file from backup location in the same server to be syncedUp with the current instance file , this patch type will do the same.
	
DB-SCRIPTS:
-----------
        The patch type 'DB-SCRIPTS' is responsible for shipping and executing the sql files(DDL/DML Scripts).

REQUIREMENTS:
-------------
	* Ansible 1.9+ should be installed in the master server.
	* Tomcat home path should be same in all the servers

HOSTS FILE:
-----------	
	EXAMPLE: 172.19.2.48 ansible_ssh_user=root ansible_ssh_pass=root123 
	
	Please enter  IP of the server in first Column,columns are separated by a space
	Please enter  User name(ansible_ssh_user) of the server in second column.
	Please enter  Password (ansible_ssh_pass) of the server in third column.
	
Give the same configuration for n rows for n servers.
	
Variables:
-----------
host_vars/WEB.yml

	*  isTomcatStartStopRequired: true
		
		Enter 'true' or 'false' for whether tomcat start and stop is required or not during patch installation.

	*  isSMSCStartStopRequired: false
	   SMSCPath:

	   	Enter 'true' or 'false' for whether SMSC start and stop is required or not during patch installation.
           	If 'true', provide complete path of SMSC Gateway	

	*  isOAMStartStopRequired: false
	   OAMPath:

	   	Enter 'true' or 'false' for whether OAM start and stop is required or not during patch installation.
                If 'true', provide complete path of OAM Server
	
	*  isWarPatch: true
		
		Enter 'true' or 'false' for whether WAR patch is involved or not in this patch installation.
		
	*  isFilePatch: true
		
		Enter 'true' or 'false' for whether patch is other than 'WAR' in this patch installation.

      	*  isJAVAPatch: true
		
		Enter 'true' or 'false' for whether patch is of 'JAVA' in this patch installation.

	*  isComPatch: true
		
		Enter 'true' or 'false' for whether COM directory Structure is available for Patch Deployment.
	
	*  BackupWarInstancePath: /root/Desktop/TestPatch/Backup
		
		Enter the Backup path where the entire tomcat instance will be backed up.

	*  BackupExtn: _BKP{{ lookup('pipe', 'date +%d-%m-%Y') }}
		
		Do not change this field.

	*  isConstant_Props: true
		Enter 'true' if Constants.props is be patched else 'false'.

	*  isLog_Config: true
		Enter 'true' if logConfig.props is be patched else 'false'.

	*  isLinkBasedPatch: true
		Enter 'true' if Link Based patch is to be deployed else 'false'.


Below are the Templates for Each Patch type that needs to be filled and explanation for the same.
--------------------------------------------------------------------------------------------------	
WAR:
---
	- patchType: WAR
	  fileName: Connect-1.2.0.war
      	  oldWarName: Connect-1.1.0.war
      	  path: "/root/Desktop/TestPatch/Coretomcat/webapps/"

* Enter patchType as WAR 	
* Enter fileName(new War file Name which needed to be patched) with extension
* Enter oldWarName( the name of the existing instance in tomcat webapps/ folder) with extension.
* Enter the Path of instance location.

FILE-SYNC:
----------
     - patchType: FILE-SYNC
	   fileName: mconnect.properties #Enter the  File name with extension
	   backupFilePath: "{{ BackupWarInstancePath }}/Connect-1.1.0/WEB-INF/classes/configfiles/"
	   path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/configfiles/" 
	 
* Enter patchType as FILE-SYNC 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter the backupFilePath (the path where backup of the file resides in remote server) ,the content of this file will be synced to the new instance file.
* Enter the path of the file residing in the remote server that needs to be synced.

JSP:
---
	- patchType: JSP
      fileName: recharge.jsp
      path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/com/comviva/mconnect/webservices/"

* Enter patchType as JSP 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter the path of the file residing in the remote server that needs to be patched.

JAR
----
	- patchType: JAR
      fileName: validate.jar
      path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/libs/"

* Enter patchType as JAR 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter the path of the file residing in the remote server that needs to be patched.

CLASSFILE:
----------
	- patchType: CLASSFILE
	  fileName: IMConnectWebUiServices.class 
	  path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/com/comviva/mconnect/webservices/" 
	  
* Enter patchType as JAR 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter the path of the file residing in the remote server that needs to be patched.

PROP-NEWLINE:
--------------	
	- patchType: PROP-NEWLINE
	  fileName: mconnect.properties
	  line: "key=value\nkey2=value3\nline=removed"
	  path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/configfiles/"

* Enter patchType as PROP-NEWLINE 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter line that need to be added in the property file. if multiple lines to be added ,add '\n' at the end of line and give next line in a single row without space.
* Enter the path of the file residing in the remote server that needs to be patched.

PROP-MODIFY:
------------
	- patchType: PROP-MODIFY
	  fileName: mconnect.properties
	  key: EMAILVAL=
	  value: true
	  path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/configfiles/"

* Enter patchType as PROP-MODIFY	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter key that needed to be modified,please enter the key including '='. eg:EMAILVAL=
* Enter the value to be modified for that particular key you entered.(for multiple key/value to be modified copy and paste whole above set for each key/value set)
* Enter the path of the file residing in the remote server that needs to be patched.

PROP-REMOVE:
-------------
	- patchType: PROP-REMOVE
	  fileName: mconnect.properties #Enter the  File name with extension
      	  line: "line=removed"
      	  path: "/root/Desktop/TestPatch/Coretomcat/webapps/Connect-1.2.0/WEB-INF/classes/configfiles/" #Enter the path of the file residing in the remote server.

* Enter patchType as PROP-REMOVE 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter line that need to be removed in the property file(for multiple lines to be removed copy and paste whole above set for each line)
* Enter the path of the file residing in the remote server that needs to be patched.

DB-SCRIPTS:
-------------
	- patchType: DB-SCRIPTS
	  fileName: dbScripts.sql #Enter the  File name with extension
      	  path: "/root/Desktop/TestPatch/" #Enter the path in the remote server where the Scripts need to be shipped and executed.

* Enter patchType as DB-SCRIPTS 	
* Enter fileName( file Name which needed to be patched) with extension.
* Enter the path in the remote server where the Scripts need to be shipped and executed.		