-- For Struts to Spring Conversion
-- Module : View Schedule Topup

Update PAGES set
   PAGE_URL ='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise'
Where PAGE_CODE='VIEWSCH001';
COMMIT;

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWSUBSSCHEDULE', 'View Schedule Topup', 'ViewScheduleTopupRestService', 'configfiles/restrictedsubs/restricted-subs-validator.xml', 'com.btsl.pretups.restrictedsubs.web.RestrictedSubscriberModel', 
    'configfiles/restservice', '/rest/view-schedule/view-schedule-topup', 'Y', 'Y', 'VIEWSUBSSCHEDULE');
COMMIT;


---Module : View Schedule Recharge in Batch
Update PAGES set
   PAGE_URL ='/restrictedsubs/view_schedule_rc_batch.form'
Where PAGE_CODE='VWSCHTR01';
COMMIT;

Update PAGES set
   PAGE_URL ='/restrictedsubs/view_schedule_rc_batch.form'
Where PAGE_CODE='VWSCHTR1A';
COMMIT;

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWSCHRCBATCH', 'View Schedule Recharge Batch', 'ViewSchRCBatchRestService', 'configfiles/restrictedsubs/validation-restrictedsubs.xml', 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/schedulerc/view-schedulerc', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWSCHRCBATCH2', 'View Schedule Recharge Batch', 'ViewSchRCBatchRestService', 'configfiles/restrictedsubs/validation-restrictedsubs.xml', 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/schedulerc/view-schedulerc-link', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWSCHRCBATCH3', 'View Schedule Recharge Batch', 'ViewSchRCBatchRestService', 'configfiles/restrictedsubs/validation-restrictedsubs.xml', 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/newuser/view-schedulerc-link-detail', 'Y', 'Y', NULL);
COMMIT;



update pages set Page_url='/restrictedsubs/cancel_schedule_recharge.form' where page_code='CNCLSCH001';

update pages set Page_url='/restrictedsubs/cancel_schedule_recharge.form' where page_code='CNCLSCH004';

update pages set Page_url='/restrictedsubs/cancel_schedule_recharge_details.form' where page_code='CNCLSCH002';

update pages set Page_url='/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form' where page_code='CNCLSCH003';




Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('CANCELMSISDN', 'Cancel Schedule Rechrage for msisdns', 'cancelScheduleRechrageForMsisdns', NULL, NULL, 
    'configfiles/restservice', '/rest/ScheduleRecharge/delete-Details-For-Selected', 'Y', 'Y', NULL);
COMMIT;

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWCANCEL', 'View Batch Details', 'viewBatchDetails', NULL, NULL, 
    'configfiles/restservice', '/rest/ScheduleRecharge/view-Cancel-ScehduleSubs', 'Y', 'Y', NULL);
COMMIT;


Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWCANCELMSISDN', 'Load Details for msisdns', 'loadDetailsForMsisdns', NULL, NULL, 
    'configfiles/restservice', '/rest/ScheduleRecharge/load-Details-For-Single', 'Y', 'Y', NULL);
COMMIT;





Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADUSERSBATCHRECHARGE', 'Load User Details for msisdns', 'loadUserDetailsForMsisdns', NULL, NULL, 
    'configfiles/restservice', '/rest/ScheduleRecharge/load-users-batch-recharge', 'Y', 'Y', NULL);
COMMIT;


--Added for Schedule Batch Recharge ===Lalit=====
SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADSERVICE', 'Load Services', 'ServiceRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/service/load-services', 'N', 'N', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('SCTPTEMPL', 'Schedule Topup Template', 'RestrictedSubscriberService', NULL, NULL, 
    NULL, '/rest/file-processor/schedule-recharge-template', 'N', 'N', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADGEODOMAIN', 'Load Geographycal Domain', 'GeographicalDomainRestService', NULL, 'com.btsl.user.businesslogic.UserGeographiesVO', 
    'configfiles/restservice', '/rest/geo-domain/load-geo-domain-list', 'N', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADCATEGORY', 'Load Category', 'CategoryRestService', NULL, NULL, 
    NULL, '/rest/category/load-category-details', 'N', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('SCHTOUPFLUP', 'Schedule Topup Process', 'RestrictedSubscriberService', NULL, NULL, 
    NULL, '/rest/file-processor/process-schedule-recharge-uploded-file', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADDOMAIN', 'Load Domain', 'DomainRestService', NULL, 'com.btsl.pretups.domain.businesslogic.DomainVO', 
    'configfiles/restservice', '/rest/domain/load-domain-details', 'N', 'N', NULL);
COMMIT;


SET DEFINE OFF;
UPDATE PAGES SET PAGE_URL = '/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE PAGE_CODE IN ('SCHTOPUP1A', 'SCHTOPUPDM', 'SCHTOPUP01');
COMMIT;

--Added for Re-schedule Batch Recharge === REST URLs
SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('DWNLDBATCHFILE', 'Download Batch File To Reschedule', 'BatchRechageRescheduleService', NULL, 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/batch-reschedule/download-batch-file', 'N', 'Y', 'RESCHEDULETOPUP');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADBATCHLIST', 'Load Scheduled Batch List', 'BatchRechageRescheduleRestService', NULL, 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/batch-reschedule/load-batch-list', 'Y', 'Y', 'RESCHEDULETOPUP');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('PROCESSRESCHDL', 'Download Batch File To Reschedule', 'BatchRechageRescheduleService', NULL, 'com.btsl.pretups.restrictedsubs.web.RestrictedTopUpForm', 
    'configfiles/restservice', '/rest/batch-reschedule/process-reschedule', 'N', 'Y', 'RESCHEDULETOPUP');
COMMIT;

ALTER TABLE SCHEDULED_BATCH_DETAIL ADD EXECUTED_ITERATIONS NUMBER(2);
ALTER TABLE SCHEDULED_BATCH_MASTER ADD FREQUENCY VARCHAR2(10);
ALTER TABLE SCHEDULED_BATCH_MASTER ADD ITERATION NUMBER(10);
ALTER TABLE SCHEDULED_BATCH_MASTER ADD PROCESSED_ON DATE;
ALTER TABLE SCHEDULED_BATCH_MASTER ADD EXECUTED_ITERATIONS NUMBER(10);

=======
--Added for Re-schedule Batch Recharge === Controller URLs
SET DEFINE OFF;
UPDATE PAGES SET PAGE_URL = '/batch-reschedule/rescheduleTopUp.form' WHERE PAGE_CODE IN ('RSHTOPUP01', 'RSHTOPUP1A', 'RSHTOPUPDM');
COMMIT;
--===Lalit=====


--Added for modifying column

ALTER TABLE SCHEDULED_BATCH_MASTER MODIFY FREQUENCY DEFAULT 'DAILY';
ALTER TABLE SCHEDULED_BATCH_MASTER MODIFY ITERATION DEFAULT 1;
ALTER TABLE SCHEDULED_BATCH_MASTER MODIFY EXECUTED_ITERATIONS DEFAULT 0;
ALTER TABLE SCHEDULED_BATCH_DETAIL MODIFY EXECUTED_ITERATIONS DEFAULT 0;

--==============



--Added for lookups of frequency

SET DEFINE OFF;
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('FREQ', 'Batch Schedule Recharge Frequence', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/01/2011 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;



SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('DAILY', 'Daily', 'FREQ', 'Y', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMin', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('MONTHLY', 'Monthly', 'FREQ', 'Y', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('WEEKLY', 'Weekly', 'FREQ', 'Y', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('12/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

--Updated validator name for batch rechedule rest services
SET DEFINE OFF;
UPDATE WEB_SERVICES_TYPES SET VALIDATOR_NAME = 'configfiles/restrictedsubs/restricted-subs-validator.xml' WHERE WEB_SERVICE_TYPE IN ('DWNLDBATCHFILE', 'LOADBATCHLIST', 'PROCESSRESCHDL');
COMMIT;



---update scripts for Module : View Schedule Recharge in Batch

UPDATE WEB_SERVICES_TYPES SET VALIDATOR_NAME = 'configfiles/restrictedsubs/restricted-subs-validator.xml' , FORMBEAN_NAME='com.btsl.pretups.restrictedsubs.web.RestrictedSubscriberModel'
WHERE WEB_SERVICE_TYPE IN ('VIEWSCHRCBATCH', 'VIEWSCHRCBATCH2', 'VIEWSCHRCBATCH3');
COMMIT;

--Updated script for validation xml and Role code to process batch schedule recharge


UPDATE WEB_SERVICES_TYPES SET VALIDATOR_NAME = 'configfiles/restrictedsubs/restricted-subs-validator.xml', ROLE_CODE = 'SCHEDULETOPUP', RESOURCE_NAME = 'BatchScheduleRechargeRestService', FORMBEAN_NAME = 'RestrictedSubscriberModel', IS_RBA_REQUIRE='Y', IS_DATA_VALIDATION_REQUIRE = 'Y' WHERE WEB_SERVICE_TYPE = 'SCTPTEMPL';

COMMIT;



UPDATE WEB_SERVICES_TYPES SET VALIDATOR_NAME = 'configfiles/restrictedsubs/restricted-subs-validator.xml', ROLE_CODE = 'SCHEDULETOPUP', RESOURCE_NAME = 'BatchScheduleRechargeRestService', FORMBEAN_NAME = 'RestrictedSubscriberModel' WHERE WEB_SERVICE_TYPE = 'SCHTOUPFLUP';


update Web_services_types Set Validator_name ='configfiles/restrictedsubs/restricted-subs-validator.xml' where web_service_type='VIEWCANCELMSISDN';
commit;

---update scripts for Module : View Schedule Recharge in Batch

UPDATE WEB_SERVICES_TYPES SET ROLE_CODE='VIEWSCHEDULED'
WHERE WEB_SERVICE_TYPE IN ('VIEWSCHRCBATCH', 'VIEWSCHRCBATCH2', 'VIEWSCHRCBATCH3');
COMMIT;



Update Web_services_types SET role_code='CANCELSCHEDULE' where WEB_SERVICE_TYPE='CANCELMSISDN';
commit;

update Web_services_types Set Validator_name ='configfiles/restrictedsubs/restricted-subs-validator.xml' where web_service_type='CANCELMSISDN';
commit;

Update Web_services_types SET role_code='CANCELSCHEDULE' where WEB_SERVICE_TYPE='VIEWCANCELMSISDN';
commit;

update Web_services_types Set Validator_name ='configfiles/restrictedsubs/restricted-subs-validator.xml' where web_service_type='VIEWCANCEL';
commit;

Update Web_services_types SET role_code='CANCELSCHEDULE' where WEB_SERVICE_TYPE='VIEWCANCEL';
commit;

--Insert script for Module : View Network 

SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWNETWORKDETAIL', 'VIEW NETWORK', 'ViewNetworkRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/view-network', 'Y', 'Y', 'VIEWNETWORKDETAIL');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('SHOWNETWORKDETAIL', 'SHOW NETWORK', 'ShowNetworkRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/show-network', 'Y', 'Y', 'VIEWNETWORKDETAIL');
COMMIT;

update PAGES SET PAGE_URL='/network/network_view_action.form' where PAGE_CODE='NW3001';
   COMMIT;

   update PAGES SET PAGE_URL='/network/viewNetworkListSpring.form' where PAGE_CODE='NW3002';
   COMMIT;

   
   --Insert script for Module :  Network Status 
   
SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('SAVENETWORKSTATUS', 'SAVE NETWORK STATUS', 'NetworkStatusRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/save-network-status', 'Y', 'Y', 'NETWORKSTATUS');
COMMIT;


SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('NETWORKSTATUS', 'NETWORK STATUS', 'NetworkStatusRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/fetch-network-status', 'Y', 'Y', 'NETWORKSTATUS');
COMMIT;


update PAGES SET PAGE_URL='/network/network_Status.form' where PAGE_CODE='NS001';
   COMMIT;

   update PAGES SET PAGE_URL='/network/save-network-status.form' where PAGE_CODE='NS002';
   COMMIT;

--Added script for adding Low Base roles to Super Channel Amdin

SET DEFINE OFF;
Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('SUBCU', 'LBSEENQ', '1');
Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('SUBCU', 'LOWBALRCH', '1');
COMMIT;

 --Insert script for Module :  Change Network  

 update PAGES SET PAGE_URL='/network/change-network.form' where MODULE_CODE='CHANGENET';
   COMMIT;
   
   Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('CHANGENETWORK', 'CHANGE NETWORK', 'ChangeNetworkRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/Change-Network', 'Y', 'Y', 'CHANGENETWORK');
COMMIT;

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('SUBMITCHANGENETWORK', 'SUBMIT CHANGE NETWORK', 'ChangeNetworkRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/network/Submit-Change-Network', 'Y', 'Y', 'CHANGENETWORK');
COMMIT;

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('CANCELBATCH', 'Cancel Schedule Rechrage for Batch', 'cancelScheduleRechrageForMsisdns', NULL, NULL, 
    'configfiles/restservice', '/rest/ScheduleRecharge/cancel-batch/', 'Y', 'Y', NULL);
COMMIT;


update pages set page_url='/restrictedsubs/cancel_batch_schedule_recharge.form' where Page_code='CNSCHTR01';
commit;


update pages set page_url='/restrictedsubs/cancel_schedule_recharge_batch.form' where Page_code='CNSCHTR02';
commit;


--scripts for auto network stock creation
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('AUTOCREATE', 'Auto Creation', 'STTYP', 'Y', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('AUTO_NWSTK_CRTN_ALWD', 'AUTO_NWSTK_CRTN_ALWD', 'NETWORKPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'AUTO_NWSTK_CRTN_ALWD', 'Y', 
    'Y', 'C2S', 'AUTO_NWSTK_CRTN_ALWD', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;
	
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('AUTO_NWSTK_CRTN_THRESHOLD', 'AUTO_NWSTK_CRTN_THRESHOLD', 'NETWORKPRF', 'STRING', 'SAL:ETOPUP:2000:300000,INC:ETOPUP:400000:500000,FOC:ETOPUP:600000:700000', 
    NULL, NULL, 50, 'AUTO_NWSTK_CRTN_THRESHOLD', 'Y', 
    'Y', 'C2S', 'AUTO_NWSTK_CRTN_THRESHOLD', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/01/2017 18:57:18', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;

--Added sql for Mobile APP 09/01/17

ALTER TABLE USER_PHONES
ADD (MHASH VARCHAR2(50 BYTE));

ALTER TABLE USER_PHONES
ADD (TOKEN VARCHAR2(100 BYTE));

ALTER TABLE USER_PHONES
ADD (TOKEN_LASTUSED_DATE DATE);

ALTER TABLE USER_OTP
ADD (SERVICE_TYPES VARCHAR2(10 BYTE));

ALTER TABLE USER_OTP
ADD (invalid_counts NUMBER);

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
Values
   ('VASSERVICE', 'C2S', 'ALL', 'TYPE IMEI', 'com.btsl.pretups.user.requesthandler.VASServiceAPPController', 
    'Vas Service', 'Vas Service', 'Y', TO_DATE('12/30/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/30/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Vas Service', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,IMEI,MSISDN', 
    'Y');
	
	Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TOKEN_EXPIRY_IN_MINTS', 'Token expiry in minutes', 'SYSTEMPRF', 'NUMBER', '60', 
    1, 100, 50, 'TokenExpiry in minutes', 'N', 
    'Y', 'C2S', 'TokenExpiry in minutes', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/12/2005 03:39:55', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');




Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VASSERVICE', 'MAPPGW', '190', 'VASSERVICE', 'Vas Service', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('12/30/2015 15:52:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/30/2015 15:53:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100986', 
    NULL, 'TYPE,IMEI,MSISDN');
    

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
Values
   ('VASENQUIRY', 'C2S', 'ALL', 'TYPE EXTNW IMEI', 'com.btsl.pretups.user.requesthandler.VASServiceEnquiryAPPController', 
    'Vas Enquiry', 'Vas Enquiry', 'Y', TO_DATE('12/30/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/30/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Vas Enquiry', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,EXTNW,IMEI', 
    'Y');


    
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VASENQUIRY', 'MAPPGW', '190', 'VASENQUIRY', 'Vas Enquiry', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('12/30/2015 15:57:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/30/2015 15:57:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100987', 
    NULL, 'TYPE,EXTNW,IMEI');
    
    
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VAS', 'MAPPGW', '190', 'VAS', 'VAS Recharge', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('12/30/2015 16:05:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/30/2015 16:05:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100988', 
    NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,IMEI,PIN,LANGUAGE1,LANGUAGE2');

Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VOURETREQ', 'MAPPGW', '190', 'VR', 'Voucher retrieval', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('07/26/2012 19:30:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/26/2012 19:30:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100900', 
    NULL, 'TYPE,SUBID,MRP,SERVICE,SELECTOR,TXNID');
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('RCREV', 'MAPPGW', '190', 'RCREV', 'Revarsal', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('07/26/2012 19:30:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/26/2012 19:30:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK4100901', 
    NULL, 'TYPE,MSISDN,IMEI,PIN,MSISDN2,TXNID');

	

update service_type set MESSAGE_FORMAT='TYPE IMEI PIN OTP' where service_type='USERREG';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,MHASH,OTP,TOKEN' where keyword='USRAUTH' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,MHASH' where keyword='USRREGREQ' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,IMEI,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='RC' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN' where keyword='C2SLASTTRF' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN' where keyword='C2SLASXTRF' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,PIN,MHASH,TOKEN' where keyword='C2SDAILYTR' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN' where keyword='C2SBAL' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,PIN,MSISDN2,AMOUNT,SELECTOR,IMEI,LANGUAGE1,MHASH,TOKEN' where keyword='PPB' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,MSISDN2,AMOUNT,GIFTER_MSISDN,GIFTER_NAME,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='GRC' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='RET' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='EVD' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,TXNID,IDENT,LANGUAGE1' where keyword='RS' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MODULE,AMOUNT,IMEI,MHASH,TOKEN' where keyword='COMMCALC' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1,MHASH,TOKEN' where keyword='TRF' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='WD' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,EXTNW,IMEI,PRODUCT,SERVICE,GATEWAY,MHASH,TOKEN' where keyword='PRGWC2S2' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,PIN,IMEI,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='OTHERBALAN' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,OLDPIN,NEWPIN,CONFIRMPIN,LANGUAGE1,MHASH,TOKEN' where keyword='C2SCPN' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,IMEI,MSISDN,MHASH,TOKEN' where keyword='VASSERVICE' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,EXTNW,IMEI,MHASH,TOKEN' where keyword='VASENQUIRY' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,IMEI,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN' where keyword='VAS' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,SUBID,MRP,SERVICE,SELECTOR,TXNID,MHASH,TOKEN' where keyword='VOURETREQ' and REQ_INTERFACE_TYPE='MAPPGW';
update service_keywords set REQUEST_PARAM='TYPE,MSISDN,IMEI,PIN,MSISDN2,TXNID,MHASH,TOKEN' where keyword='RCREV' and REQ_INTERFACE_TYPE='MAPPGW';


--added for o2c direct transfer enhancement
SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('O2C_DIRECT_TRANSFER', 'O2C Direct Transfer', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'O2C Direct Transfer is true then network stock is part of transcations', 'Y', 
    'Y', 'C2S', 'O2C Direct Transfer', TO_DATE('01/09/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/09/2017 16:10:58', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;

ALTER  TABLE CHANNEL_TRANSFERS  ADD (STOCK_UPDATED VARCHAR2(1 BYTE) DEFAULT 'Y');
COMMIT;



update web_services_types set validator_name='configfiles/restrictedsubs/restricted-subs-validator.xml' where web_service_type='CANCELBATCH';

update web_services_types set role_code='CANCELSCHEDULE' where web_service_type='CANCELBATCH';

Commit;

--added for View Self Details
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VIEWSELFDETAILS', 'View Self Details', 'ViewSelfDetailsRestService', ' ', ' ', 
    'configfiles/restservice', '/rest/user/view-selfdetails', 'Y', 'Y', 'VIEWUSERSELF');
COMMIT;


 update PAGES SET PAGE_URL='/user/user_Operator_View_Action.form' where PAGE_CODE='VIEWUSRS01'; 
   COMMIT;



--Added for DEF2638 fixes

UPDATE LOOKUPS SET STATUS = 'N' WHERE LOOKUP_CODE = 'POST' AND LOOKUP_TYPE = 'SUBTP';

--- Added for bulk voucher resend pin

SET DEFINE OFF;

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BVPINRS01', 'VCHRENQ', '/voucherenquiry/vomsBulkVoucherResendPin.form', 'Voucher enquiry', 'Y', 
    (select max(SEQUENCE_NO)+1 from PAGES), '2', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BVPINRS0A', 'VCHRENQ', '/voucherenquiry/vomsBulkVoucherResendPin.form', 'Voucher enquiry', 'N', 
    (select max(SEQUENCE_NO) from PAGES), '2', '1');	
	
	
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BVPINRSDDM', 'VCHRENQ', '/voucherenquiry/vomsBulkVoucherResendPin.form', 'Voucher enquiry', 'Y', 
    (select max(SEQUENCE_NO) from PAGES), '1', '1');
	
	
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BULKVCPINRS', 'BVPINRS01', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BULKVCPINRS', 'BVPINRS0A', '1');
   
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BULKVCPINRS', 'BVPINRSDDM', '1');
   
   
INSERT INTO ROLES ( DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR,
TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES ) VALUES ( 
'OPERATOR', 'BULKVCPINRS', 'Bulk pin resend for vouchers', 'Voucher enquiry', 'Y', 'A', NULL, NULL
, 'N', '1', 'WEB');

INSERT INTO CATEGORY_ROLES ( CATEGORY_CODE, ROLE_CODE, APPLICATION_ID ) VALUES ( 
'CCE', 'BULKVCPINRS', '1');

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('BULKVOUCHERRESENDPIN', 'bulk voucher resend pin', 'BulkVoucherResendPinService', NULL, NULL, 
    'configfiles/restservice', '/rest/bulk-voucher-resend-pin/upload-bulkVoucherResendPin', 'N', 'Y', NULL);
COMMIT;

SET DEFINE OFF;
TRUNCATE TABLE MESSAGE_ARGUMENT;
COMMIT;

--Added for Message_Arguments
SET DEFINE OFF;
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('242', '0', 'Transaction id');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('242', '3', 'Sender Mobile');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('242', '4', 'Requested amount');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105001', '3', 'Total Penalty');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105001', '2', 'Owner Roam Penalty');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105001', '1', 'Transaction id
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105001', '0', 'User  Roam Penality');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105003', '3', 'Total Penalty');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105003', '2', 'User  Name');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105003', '1', 'Transaction id');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('105003', '0', 'Owner Roam Penalty');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('2500003', '3', 'Current Balance
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('2500003', '2', 'User  Name
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('2500003', '1', 'Transaction id
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('2500003', '0', 'Owner Roam Penalty
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('216', '2', 'Recharge Amount');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('216', '1', 'Transaction Number');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('216', '0', 'User Number');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('230', '2', 'Sender MSISDN');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('230', '1', 'Recharge Amount');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('230', '0', 'Transaction ID
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('231', '2', 'Sender  MSISDN
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('231', '1', 'Recharge Amount
');
Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('231', '0', 'Transaction ID
');
COMMIT;
--Added for Message_Master
SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '105000', 'mclass^2&pid^61:105000:You do not have enough balance to pay penalty charged for this roam transaction.
', 'ALL', 'mclass^2&pid^61:105000:You do not have enough balance to pay penalty charged for this roam transaction.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '105001', 'mclass^2&pid^61:105001:You have been charged a roam penalty of {0} INR on transaction {1} as your daily roam recharge amount has exceeded the maximum threshold for the day.Your owner has been charged a roam penalty of {2} INR and total penalty is {3} INR.
', 'ALL', 'mclass^2&pid^61:105001:You have been charged a roam penalty of {0} INR on transaction {1} as your daily roam recharge amount has exceeded the maximum threshold for the day.Your owner has been charged a roam penalty of {2} INR and total penalty is {3} INR.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '105002', 'mclass^2&pid^61:105002: Your owner does not have enough balance to pay penalty charged for this roam transaction.
', 'ALL', 'mclass^2&pid^61:105002: Your owner does not have enough balance to pay penalty charged for this roam transaction.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '105003', 'mclass^2&pid^61:105003:You have been charged a roam penalty of {0} INR on transaction {1} of your user {2} as his/her daily roam recharge amount has exceeded the maximum threshold for the day.The total penlaty is {3} INR.
', 'ALL', 'mclass^2&pid^61:105003:You have been charged a roam penalty of {0} INR on transaction {1} of your user {2} as his/her daily roam recharge amount has exceeded the maximum threshold for the day.The total penlaty is {3} INR.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '2500003', 'mclass^2&pid^61:2500003: You have been charged a roam penalty Rs.{0} on transaction {1} of your user {2} as his/her daily roam recharge amount has exceeded the maximum threshold for the day.Your Current Balance is INR {3}.
', 'ALL', 'mclass^2&pid^61:2500003: You have been charged a roam penalty Rs.{0} on transaction {1} of your user {2} as his/her daily roam recharge amount has exceeded the maximum threshold for the day.Your Current Balance is INR {3}.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '216', 'mclass^2&pid^61:216:Your request to recharge {2} INR to {0} will be processed in a short time. Transaction number is {1}.
', 'ALL', 'mclass^2&pid^61:216:Your request to recharge {2} INR to {0} will be processed in a short time. Transaction number is {1}.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '230', 'mclass^2&pid^61:230:Please confirm your transaction status from customer care, transaction from {2}, transaction ID is {0}, amount is {1}.
', 'ALL', 'mclass^2&pid^61:230:Please confirm your transaction status from customer care, transaction from {2}, transaction ID is {0}, amount is {1}.
', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '250', 'mclass^2&pid^61:250:Your request cannot be processed at this time, please try again later.
', 'ALL', NULL, 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '231', 'mclass^2&pid^61:231:Your transfer request from {2} for transaction ID {0} of amount {1} can not be processed.
', 'ALL', 'mclass^2&pid^61:231:Your transfer request from {2} for transaction ID {0} of amount {1} can not be processed.
.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '200', '200:Successful.', 'ALL', '200:Successful', 
    '200:Successful', NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '206', '206:Fail', 'ALL', '206:Fail', 
    '206:Fail', NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '242', 'mclass^2&pid^61:242:Dear customer, your request for {4} INR from {3} is successful, transaction ID is {0}. Please check your balance.
', 'ALL', NULL, 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
	
--added for O2C network stock deduction process
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('O2CTRFDDT', TO_DATE('01/16/2017 10:14:18', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('01/15/2017 02:53:36', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/16/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'network stock deduction process', 'NG', 10);

COMMIT;

--Update for Auto network stock

UPDATE SYSTEM_PREFERENCES SET MAX_SIZE = 100 WHERE PREFERENCE_CODE = 'AUTO_NWSTK_CRTN_THRESHOLD';

COMMIT;

/*Added for O2C transfer details remark report*/
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('O2CRPT001', 'CHRPTO2C', '/reports/loadreportsform.form', 'O2C reports details', 'Y', 
    579, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('O2CRPT001A', 'CHRPTO2C', '/reports/loadreportsform.form', 'O2C reports details', 'N', 
    579, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('O2CRPTDMM', 'CHRPTO2C', '/reports/loadreportsform.form', 'O2C reports details', 'Y', 
    579, '1', '1');




Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('O2CRPTNEW', 'O2CRPT001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('O2CRPTNEW', 'O2CRPT001A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('O2CRPTNEW', 'O2CRPTDMM', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('DISTB_CHAN', 'O2CRPTNEW', 'O2C transfer details New', 'Channel Reports-O2C', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'O2CRPTNEW', 'O2C transfer details New', 'Channel Reports-O2C', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'O2CRPTNEW', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DIST', 'O2CRPTNEW', '1');


Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADCHNUSRLIST', 'Load Channel User List', 'ChannelUserReportRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/reports/load-user-list', 'N', 'N', NULL);
ALTER TABLE PROFILE_SET_VERSION drop column PRODUCT_CODE ;
ALTER TABLE PROFILE_DETAILS DROP CONSTRAINT PK_PD;
DROP INDEX PK_PD;
ALTER TABLE PROFILE_DETAILS add  PRODUCT_CODE VARCHAR2(10 BYTE) NOT NULL;
CREATE UNIQUE INDEX PK_PD ON PROFILE_DETAILS (SET_ID, VERSION, DETAIL_ID,PRODUCT_CODE);
ALTER TABLE USER_OTH_PROFILES ADD  PRODUCT_CODE  VARCHAR2(10 BYTE);
CREATE INDEX LMS_PRO_USER_ID ON DAILY_C2S_LMS_SUMMARY ("LMS_PROFILE", "USER_ID") TABLESPACE PRTP_DATA;
ALTER TABLE DAILY_C2S_LMS_SUMMARY add version VARCHAR2(20 BYTE) default 1;
ALTER TABLE DAILY_C2S_LMS_SUMMARY DROP CONSTRAINT DAILY_C2S_LMS_SUMMARY_PK;
ALTER TABLE DAILY_C2S_LMS_SUMMARY ADD (CONSTRAINT DAILY_C2S_LMS_SUMMARY_PK PRIMARY KEY (trans_date, user_id, PRODUCT_CODE, LMS_PROFILE,VERSION));
CREATE OR REPLACE PROCEDURE update_accpnt_dly_c2s_lms_smry (
							aiv_Date        	   IN  	VARCHAR2,
	   	  		  			rtn_message           OUT   VARCHAR2,
							rtn_messageforlog     OUT   VARCHAR2,
							rtn_sqlerrmsgforlog   OUT   VARCHAR2
	   	  		  			)
IS
p_trans_date			DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id				DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code			DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile			DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_accumulated_points	DAILY_C2S_LMS_SUMMARY.accumulated_points%type;
p_version				DAILY_C2S_LMS_SUMMARY.version%type;
p_count NUMBER;

sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR update_cur is
		SELECT USER_ID_OR_MSISDN, PRODUCT_CODE, PROFILE_ID, ACCUMULATED_POINTS,POINTS_DATE,VERSION from  BONUS where PROFILE_TYPE='LMS' and POINTS_DATE=to_date(aiv_Date,'dd/mm/yy');
	BEGIN
	  p_count:=0;
	  FOR user_records IN update_cur
			 LOOP
			     	p_user_id:=user_records.USER_ID_OR_MSISDN;
					p_product_code:=user_records.product_code;
					p_lms_profile:=user_records.PROFILE_ID;
					p_accumulated_points:=user_records.accumulated_points;
					p_version:=user_records.version;
				 BEGIN
					p_count:=p_count+1;
					UPDATE DAILY_C2S_LMS_SUMMARY SET  accumulated_points=p_accumulated_points
					WHERE user_id=p_user_id
					AND product_code=p_product_code
					AND trans_date=to_date(aiv_Date,'dd/mm/yy')
					AND lms_profile=p_lms_profile
					AND version=p_version;
					EXCEPTION
						WHEN OTHERS	   THEN
								  DBMS_OUTPUT.PUT_LINE ('Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
								  rtn_messageforlog := 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
								  rtn_sqlerrmsgforlog := SQLERRM;
								  RAISE sqlexception;
				 END;
			 END LOOP;
			 	rtn_message:='SUCCESS';
				rtn_messageForLog :='PreTUPS update_acc_pnt_daily_c2s_lms_summary successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
				rtn_sqlerrMsgForLog :=' ';
		
		EXCEPTION --Exception Handling of main procedure
		 WHEN sqlexception THEN
		 	  ROLLBACK;
			  DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
			  rtn_message :='FAILED';
			  RAISE sqlexception;

		 WHEN OTHERS THEN
		 	  ROLLBACK;
	     	  DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in update_acc_pnt_daily_c2s_lms_summary procedure:='||SQLERRM);
		      rtn_message :='FAILED';
			  RAISE sqlexception;

	END;
/
SHOW ERRORS; 
CREATE OR REPLACE PROCEDURE insert_dly_no_c2s_lms_smry (
							aiv_Date        	   IN  	VARCHAR2,
	   	  		  			rtn_message           OUT   VARCHAR2,
							rtn_messageforlog     OUT   VARCHAR2,
							rtn_sqlerrmsgforlog   OUT   VARCHAR2
	   	  		  			)
IS
p_trans_date			DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id				DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code			DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile			DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_txn_amount			DAILY_C2S_LMS_SUMMARY.TRANSACTION_AMOUNT%type;
p_txn_count				DAILY_C2S_LMS_SUMMARY.TRANSACTION_COUNT%type;
p_accumulatepoint		DAILY_C2S_LMS_SUMMARY.ACCUMULATED_POINTS%type;
p_version				DAILY_C2S_LMS_SUMMARY.version%type;
p_count NUMBER;

sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

CURSOR insert_cur is
		select distinct cu.user_id,cu.LMS_PROFILE, ps.PRODUCT_CODE,ps.version  from channel_users cu,  users U, PROFILE_DETAILS ps
		where cu.LMS_PROFILE is not null and u.USER_ID=cu.USER_ID and u.status not in ('N','C') and cu.LMS_PROFILE=ps.SET_ID
		minus 
		select ds.user_id,ds.LMS_PROFILE, ds.PRODUCT_CODE,ds.version from DAILY_C2S_LMS_SUMMARY ds where ds.TRANS_DATE=to_date(aiv_Date,'dd/mm/yy');
		
	BEGIN
	  p_count:=0;
	  p_trans_date:=to_date(aiv_Date,'dd/mm/yy');	
	  p_txn_amount:=0;
	  p_txn_count:=0;
	  p_accumulatepoint:=0;	 
	  p_version:=1;	
	  FOR user_records IN insert_cur
			 LOOP
			     	p_user_id:=user_records.USER_ID;
					p_product_code:=user_records.product_code;
					p_lms_profile:=user_records.LMS_PROFILE;
					p_version:=user_records.version;	
				BEGIN
					p_count:=p_count+1;
					insert into DAILY_C2S_LMS_SUMMARY(trans_date,user_id,product_code,lms_profile,TRANSACTION_AMOUNT,TRANSACTION_COUNT,ACCUMULATED_POINTS,VERSION) VALUES  (p_trans_date,p_user_id,p_product_code,p_lms_profile,p_txn_amount,p_txn_count,p_accumulatepoint,p_version);

					EXCEPTION
						WHEN OTHERS	   THEN
								  DBMS_OUTPUT.PUT_LINE ('Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM );
								  rtn_messageforlog := 'Exception in insert_dly_no_c2s_lms_smry Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
								  rtn_sqlerrmsgforlog := SQLERRM;
								  RAISE sqlexception;
				 END;
			 END LOOP;
			 	rtn_message:='SUCCESS';
				rtn_messageForLog :='PreTUPS insert_dly_no_c2s_lms_smry successfully executed, Excuted Date Time:'||SYSDATE||' For date:'||p_trans_date||' Number updates:'||p_count;
				rtn_sqlerrMsgForLog :=' ';
		
		EXCEPTION --Exception Handling of main procedure
		 WHEN sqlexception THEN
		 	  ROLLBACK;
			  DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
			  rtn_messageForLog :='insert_dly_no_c2s_lms_smry sqlException Caught='||SQLERRM;
			  rtn_message :='FAILED';
			  RAISE sqlexception;

		 WHEN OTHERS THEN
		 	  ROLLBACK;
	     	  DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure:='||SQLERRM);
			  rtn_messageForLog :='OTHERS ERROR in insert_dly_no_c2s_lms_smry procedure'||SQLERRM;
		      rtn_message :='FAILED';
			  RAISE sqlexception;

	END;
/
SHOW ERRORS;


/*VOUCHTRACK disable script*/

update PRODUCTS set STATUS='N' where PRODUCT_CODE='VOUCHTRACK'
update NETWORK_PRODUCT_MAPPING set STATUS='N' where PRODUCT_CODE='VOUCHTRACK' 

--Fixed for Batch Bar User Deletion



ALTER TABLE BATCHES ADD APPROVED_RECORDS NUMBER (10);

ALTER TABLE BATCHES ADD REJECTED_RECORDS NUMBER (10);

COMMIT;