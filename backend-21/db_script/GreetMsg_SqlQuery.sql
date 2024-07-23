-- FOR NEW PAGE ENTRIES

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CUSERGT01A', 'CUSERS', '/channeluser/greetMsg.form', 'Send Greeting Messages', 'N', 
    516, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CUSERGT001', 'CUSERS', '/channeluser/greetMsg.form', 'Send Greeting Messages', 'Y', 
    516, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CUSERGTDMM', 'CUSERS', '/channeluser/greetMsg.form', 'Send Greeting Messages', 'Y', 
    516, '1', '1');
	
	
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('GREETMSG', 'CUSERGT001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('GREETMSG', 'CUSERGT01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('GREETMSG', 'CUSERGTDMM', '1');
   
  
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
Values
   ('OPERATOR', 'GREETMSG', 'Greeting Messages Mgt', 'Channel Users', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
Values
   ('BCU', 'GREETMSG', '1');
   
-- WEB SERIVCE TYPE

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('GREETMSGCAT', 'Load Category Data', 'GreetingMsgRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-category-data', 'N', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('GREETMSGDOMAIN', 'Load Domain Data', 'GreetingMsgRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-domain-data', 'N', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('GREETUSRDNLD', 'Download User List', 'GreetingMsgRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/download/user-list', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('GREETUSRUPLD', 'Upload User List', 'GreetingMsgRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/upload/user-list', 'Y', 'Y');
	
	
-- ROLES FOR REST SERVICE

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'GREETUSRUPLD', 'Upload Greeting Msg List', 'Upload Files', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'GREETUSRDNLD', 'Download Greeting Msg User List', 'Download Files', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
	
-- GREETMSG PROCESS

Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('GREETMSG', TO_DATE('08/03/2016 09:55:35', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('07/30/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('07/31/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Greet Msg Process', 'BL', NULL);
	


	
