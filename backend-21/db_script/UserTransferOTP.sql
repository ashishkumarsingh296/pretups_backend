

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('USRTRF001', 'USERTRF', '/usertransfer/usertransfer.form', 'Channel User Transfer Initiate', 'Y', 
    5, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('USRTRFDMM', 'USERTRF', '/usertransfer/usertransfer.form', 'Channel User Transfer Initiate', 'Y', 
    5, '1', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('USRTRF01A', 'USERTRF', '/usertransfer/usertransfer.form', 'Channel User Transfer Initiate', 'N', 
    4, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('USRTRF002', 'USERTRF', '/usertransfer/usertransfer.form', 'Channel User Transfer Initiate', 'N', 
    4, '2', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('DISTB_CHAN', 'USERTRFOTP', 'Channel User Transfer Initiate', 'Channel user trf', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('USERTRFOTP', 'USRTRF001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('USERTRFOTP', 'USRTRF002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('USERTRFOTP', 'USRTRF01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('USERTRFOTP', 'USRTRFDMM', '1');




Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DIST', 'USERTRFOTP', '1');


   
SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOADINITUSRTRFLIST', 'load initiated user transfer details', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/load-initiated-user-transfer-details', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOADUSRLIST', 'Load User List', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/load-user-list', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOADUSRDETAILS', 'confirm  user details', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/confirm-user-details', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOADCHNNLUSRLIST', 'Load Channel User List', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/load-channel-user-list', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('CONFIRMUSRTRANSFER', 'Confirm User Transfer', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/confirm-user-transfer', 'Y', 'Y');

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOADINITUSRBYMSISDN', 'Load Initiated User by msisdn', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/load-initiated-user-with-msisdn', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('USERTRFCAT', 'Load Category Data', 'UserTransferRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/usertransfer/load-category-data', 'N', 'Y');
COMMIT;



CREATE TABLE USER_MIGRATION_REQUEST
(
  FROM_USER_ID  VARCHAR2(20 BYTE),
  TO_USER_ID    VARCHAR2(20 BYTE),
  OTP           VARCHAR2(32 BYTE),
  STATUS        VARCHAR2(1 BYTE),
  CREATED_ON    DATE,
  CREATED_BY    VARCHAR2(20 BYTE),
  MODIFIED_ON   DATE,
  MODIFIED_BY   VARCHAR2(20 BYTE)
);


CREATE UNIQUE INDEX USER_MIGRATION_REQUEST_PK ON USER_MIGRATION_REQUEST
(TO_USER_ID, STATUS);


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('UTRFCNF01A', 'USERTRF', '/usertransfer/userTransferConfirm.form', 'User Transfer Confirmation', 'N', 
    6, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('UTRFCNF002', 'USERTRF', '/usertransfer/userTransferConfirm.form', 'User Transfer Confirmation', 'N', 
    6, '2', '1');
	Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('UTRFCNF001', 'USERTRF', '/usertransfer/userTransferConfirm.form', 'User Transfer Confirmation', 'Y', 
    6, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('UTRFCNFDMM', 'USERTRF', '/usertransfer/userTransferConfirm.form', 'User Transfer Confirmation', 'Y', 
    6, '1', '1');

COMMIT;


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('DISTB_CHAN', 'UTRFCNFOTP', 'Channel User Transfer Confirmation', 'Channel user trf', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;
;

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('UTRFCNFOTP', 'UTRFCNF001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('UTRFCNFOTP', 'UTRFCNF002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('UTRFCNFOTP', 'UTRFCNF01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('UTRFCNFOTP', 'UTRFCNFDMM', '1');
COMMIT;


SET DEFINE OFF;
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DIST', 'UTRFCNFOTP', '1');


alter table user_otp add SERVICE_TYPES  VARCHAR2(20 BYTE);
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('OTP_ALLOWED_LENGTH', 'Allowed length for OTP', 'SYSTEMPRF', 'INT', '6', 
    NULL, NULL, 50, 'Allowed length for OTP', 'Y', 
    'N', 'C2S', 'Allowed length for OTP', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
	
	SET DEFINE OFF;
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DPOS', 'UTRFCNFOTP', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DPOS', 'USERTRFOTP', '1');
COMMIT;


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SDIST', 'UTRFCNFOTP', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SDIST', 'USERTRFOTP', '1');
COMMIT;
alter table USER_MIGRATION_REQUEST add INVALID_OTP_COUNT NUMBER(3) default 0 not null;
drop index USER_MIGRATION_REQUEST_PK;

update WEB_SERVICES_TYPES set WEB_SERVICE_TYPE='LOADUSRLISTTRF' where  WEB_SERVICE_TYPE='LOADUSRLIST' and RESOURCE_NAME='UserTransferRestService';

update WEB_SERVICES_TYPES set WEB_SERVICE_TYPE='LOADUSRDETAILSTRF' where  WEB_SERVICE_TYPE='LOADUSRDETAILS' and RESOURCE_NAME='UserTransferRestService';

update WEB_SERVICES_TYPES set WEB_SERVICE_TYPE='LOADCHNNLUSRLISTTRF' where  WEB_SERVICE_TYPE='LOADCHNNLUSRLIST' and RESOURCE_NAME='UserTransferRestService';