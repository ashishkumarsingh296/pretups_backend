--Low base Recharge and Zero Base Recharge and Fnf

CREATE TABLE FNF_ZERO_BASE_CUSTOMER
(
MSISDN1    VARCHAR2(20 BYTE)    NOT NULL,
MSISDN2    VARCHAR2(20 BYTE)    NOT NULL,
RECORD_TYPE  VARCHAR2(10 BYTE) NOT NULL,
EXPIRY_DATE     DATE,
CREATED_ON DATE
);


CREATE UNIQUE INDEX indxZB01 ON FNF_ZERO_BASE_CUSTOMER(MSISDN1, RECORD_TYPE);

CREATE UNIQUE INDEX indxzb002 ON FNF_ZERO_BASE_CUSTOMER(MSISDN2, RECORD_TYPE);



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('FNF_ZB_ALLOWED', 'FNF and ZB Allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Direct Voucher Enable', 'N', 
    'Y', 'C2S', 'FNF_ZB_ALLOWED', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2005 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LOW_BASED_ALLOWED', 'Low based Allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Direct Voucher Enable', 'N', 
    'Y', 'C2S', 'LOW_BASED_ALLOWED', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2005 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
	
CREATE TABLE LOW_BASE_CUSTOMER
(
  CUSTOMER_MSISDN        VARCHAR2(20 BYTE)    NOT NULL,
  MIN_RECH_AMOUNT        NUMBER(12,2),
  MAX_RECH_AMOUNT        NUMBER(12,2),
  CREATED_ON             DATE            NOT NULL,
  COMMISSION             NUMBER,
  EXPIRY_DATE            DATE
);


CREATE TABLE CUST_RET_COUNT
(
  CUSTOMER_MSISDN                  VARCHAR2(20 BYTE)    NOT NULL,
  RETAILER_MSISDN                  VARCHAR2(20 BYTE)    NOT NULL,
  COUNT            NUMBER(12,2),
  AMOUNT            NUMBER(12,2),
  FIRST_TXN_DATE    DATE            NOT NULL,
  LAST_TXN_DATE    DATE            NOT NULL,
  STATUS            VARCHAR2(5 BYTE)    NOT NULL
);
ALTER TABLE C2S_TRANSFERS
ADD (LOW_BASED_RECHARGE VARCHAR2(1 BYTE));



------------------------------------------------------------------------------------------

--Added for Low Base Recharge Enquiry/ Eligibility Enquiry Date:13/09/16

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM,SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LOBRC001', 'LOWBALRCH', '/lowbase/low_base_transaction_enquiry.form', 'Low Base Transaction Enquiry', 'Y', 1, '2', '1');


Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LOBRC00A', 'LOWBALRCH', '/lowbase/low_base_transaction_enquiry.form', 'Low Base Transaction Enquiry', 'N', 1, '2', '1');

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM,SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LOBRCDMM', 'LOWBALRCH', '/lowbase/low_base_transaction_enquiry.form', 'Low Base Transaction Enquiry', 'Y',  1, '1', '1');
COMMIT;

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LOWBALRCH', 'LOBRC001', '1');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LOWBALRCH', 'LOBRC00A', '1');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LOWBALRCH', 'LOBRCDMM', '1');



Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE) values('OPERATOR', 'LOWBALRCH', 'Low Base Transaction Enquiry', 'Low Base Transaction Enquiry', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N');

Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) values('BCU', 'LOWBALRCH', '1');

Insert into MODULES(MODULE_CODE, MODULE_NAME,SEQUENCE_NO, APPLICATION_ID) Values('LOWBALRCH', 'Low Base Transaction Enquiry', '1', '1');

COMMIT;


Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM,SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LBSEE001', 'LOWBALRCH', '/lowbase/low_base_subscriber_eligibility_enquiry.form', 'Low Base Sub. Eligibility Enq.', 'Y', 1, '2', '1');

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LBSEE00A', 'LOWBALRCH', '/lowbase/low_base_subscriber_eligibility_enquiry.form', 'Low Base Sub. Eligibility Enq.', 'N', 1, '2', '1');

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) values('LBSEEDMM', 'LOWBALRCH', '/lowbase/low_base_subscriber_eligibility_enquiry.form', 'Low Base Sub. Eligibility Enq.', 'Y', 1, '1', '1');

COMMIT;

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LBSEENQ', 'LBSEE001', '1');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LBSEENQ', 'LBSEE00A', '1');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) values('LBSEENQ', 'LBSEEDMM', '1');

Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID,GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE) values('OPERATOR', 'LBSEENQ', 'Low Base Subscriber Eligibility Enquiry', 'Low Base Transaction Enquiry', 'Y','A', NULL, NULL, 'N', '1','WEB', 'B', 'N', 'N');

Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) values('BCU', 'LBSEENQ', '1');

COMMIT;

Insert into WEB_SERVICES_TYPES (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE) values('LOWBALRCH', 'Low Balance Recharge', 'LowBaseTxnEnqRestService', 'configfiles/lowbase/validator-lowbase.xml', 'com.btsl.pretups.lowbase.businesslogic.LowBasedRechargeVO', 'configfiles/restservice', '/rest/low-base/load-transaction-details', 'Y', 'Y');

Insert into WEB_SERVICES_TYPES (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME,CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE) values('LBSEENQ', 'Low Balance Recharge', 'LowBaseTxnEnqRestService', 'configfiles/lowbase/validator-lowbase.xml', 'com.btsl.pretups.lowbase.businesslogic.LowBasedRechargeVO', 'configfiles/restservice', '/rest/low-base/load-eligibility-details', 'Y', 'Y');
COMMIT;

update SERVICE_TYPE set MESSAGE_FORMAT='KEYWORD NAME PIN' where SERVICE_TYPE ='PDEL';
update SERVICE_TYPE set MESSAGE_FORMAT='Keyword Name MSISDN Preferred Amount PIN' where SERVICE_TYPE ='PADD';
COMMIT;

ALTER TABLE WEB_SERVICES_TYPES ADD ROLE_CODE VARCHAR2(25);

-------------------------------------------------------------------------------------------------------------

--ADDED FOR JMETER TESTING OF REST SERVICE FOR LOW BASE MODULE DATE:14-09-16

UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'LOWBALRCH' WHERE WEB_SERVICE_TYPE = 'LOWBALRCH';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'LBSEENQ' WHERE WEB_SERVICE_TYPE = 'LBSEENQ';
COMMIT;
-----------------------------------------------------------------------------------------------------------



-- change the value of Description of SYSTEM_PREFERENCES

update System_preferences set Description='Low Base Customer Allowed'  where PREFERENCE_CODE = 'LOW_BASED_ALLOWED';
update System_preferences set Description='FnF and Zero Base Customer Allowed'  where PREFERENCE_CODE = 'FNF_ZB_ALLOWED';

update System_preferences set Modified_allowed='Y' where PREFERENCE_CODE = 'LOW_BASED_ALLOWED';
update System_preferences set Modified_allowed='Y' where PREFERENCE_CODE = 'FNF_ZB_ALLOWED';
COMMIT;


--Added for REST Service Role Validation:15-09-16

UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'BARUSER' WHERE WEB_SERVICE_TYPE = 'BARUSER';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'UNBARUSER' WHERE WEB_SERVICE_TYPE = 'UNBARUSER';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'UNBARUSER' WHERE WEB_SERVICE_TYPE = 'CONUNBARUSER';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'VIEWBARREDLIST' WHERE WEB_SERVICE_TYPE = 'VIEWBARREDLIST';
COMMIT;

--Added for low base customer upload
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LB_SYSTEMLEVEL_LIMIT', 'Low base upload', 'SYSTEMPRF', 'INT', '100000', 
    NULL, NULL, 50, 'Low base MSISDN Upload', 'N', 
    'Y', 'C2S', 'Max Limit for uploading the msisdn for low base', TO_DATE('09/10/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/10/2016 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
	
CREATE UNIQUE INDEX LOWBaseUnique ON LOW_BASE_CUSTOMER (CUSTOMER_MSISDN);



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('MSISDN_LENGTH', 'length of Mobile Number', 'SYSTEMPRF', 'INT', '10', 
    6, 15, 50, 'fixed length of Mobile Number', 'N', 
    'Y', 'C2S', 'fixed length of Mobile Number', TO_DATE('06/07/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/07/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('LBFILEUPLOAD', TO_DATE('09/10/2016 09:27:14', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/29/2016 09:27:14', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('07/31/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Low Base user upload', 'NG', 0);
COMMIT;


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('ZBFNFUPLOAD', TO_DATE('09/10/2016 09:27:14', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/29/2016 09:27:14', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('07/31/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Zb Fnf user upload', 'NG', 0);
----Added for Commmission Profile Role Code Authentication in WEB SERVICE TTYPE

UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'SUSCOMMPROFILE' WHERE WEB_SERVICE_TYPE = 'COMMPS';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'SUSCOMMPROFILE' WHERE WEB_SERVICE_TYPE = 'COMMPSL';
UPDATE WEB_SERVICES_TYPES SET ROLE_CODE = 'SUSCOMMPROFILE' WHERE WEB_SERVICE_TYPE = 'COMMPLSS';

----Added for Low Base Report Process
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('LBREPORT', TO_DATE('09/16/2016 16:40:00', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/16/2016 16:40:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/16/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    1440, 1440, 'Low Base Report Process', 'NG', 0);

----Added for Low Base Report Download
Insert into DOWNLOAD_SCHEDULED_REPORTS
   (REPORT_CODE, REPORT_NAME, TYPE, STATUS, PATH_KEY, 
    PREFIX, DATE_FORMAT, MODULE, DISPLAY_SEQ)
Values
   ('RPT018', 'Low Base Recharge Report', 'csv', 'Y', 'LB_TXN_REPORT_SCHEDULE', 
    'LBTransactionReport', 'ddMMyy', 'C2S', NULL);


--- Added for WEB_SERVICES_TYPES to make IS_RBA_REQUIRE='Y' for those who has role_code
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='COMMPS';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='COMMPSL';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='COMMPLSS';

update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='C2SREV';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='C2SDOREV';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='C2SLOADTXN';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='C2SREVSTAT';

update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='UNBARUSER';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='CONUNBARUSER';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='BARUSER';
update WEB_SERVICES_TYPES set IS_RBA_REQUIRE='Y' where WEB_SERVICE_TYPE='VIEWBARREDLIST';

COMMIT;

--DB scripts for GMB

--Create table P2P_sUBCRIBERS_COUNTERS


CREATE TABLE P2P_SUBSCRIBERS_COUNTERS
(
  USER_ID                       VARCHAR2(20 BYTE) NOT NULL,
  MSISDN                        VARCHAR2(15 BYTE) NOT NULL,
SERVICE_TYPE             VARCHAR2(10 BYTE) NOT NULL,
  DAILY_TRANSFER_COUNT          NUMBER(16)      DEFAULT 0,
  MONTHLY_TRANSFER_COUNT        NUMBER(16)      DEFAULT 0,
  WEEKLY_TRANSFER_COUNT         NUMBER(16)      DEFAULT 0,
  PREV_DAILY_TRANSFER_COUNT     NUMBER(16)      DEFAULT 0,
  PREV_WEEKLY_TRANSFER_COUNT    NUMBER(16)      DEFAULT 0,
  PREV_MONTHLY_TRANSFER_COUNT   NUMBER(16)      DEFAULT 0,
  DAILY_TRANSFER_AMOUNT         NUMBER(20)      DEFAULT 0,
  WEEKLY_TRANSFER_AMOUNT        NUMBER(20)      DEFAULT 0,
  MONTHLY_TRANSFER_AMOUNT       NUMBER(20)      DEFAULT 0,
  PREV_DAILY_TRANSFER_AMOUNT    NUMBER(20)      DEFAULT 0,
  PREV_WEEKLY_TRANSFER_AMOUNT   NUMBER(20)      DEFAULT 0,
  PREV_MONTHLY_TRANSFER_AMOUNT  NUMBER(20)      DEFAULT 0,
  PREV_TRANSFER_DATE            DATE,
  PREV_TRANSFER_WEEK_DATE       DATE,
  PREV_TRANSFER_MONTH_DATE      DATE,

CONSTRAINT Pk_userID_msisdn_sevctype PRIMARY KEY (USER_ID,MSISDN,SERVICE_TYPE)

);

commit;


--Alter P2P_SUBSCRIBERS


Alter table p2p_subscribers 
Drop (Daily_Transfer_amount,
Daily_Transfer_Count,
Weekly_transfer_count,
Weekly_transfer_amount,
Monthly_transfer_amount,
Monthly_transfer_count,
Prev_daily_transfer_amount,
Prev_daily_transfer_count,
prev_monthly_transfer_amount,
prev_monthly_transfer_count,
prev_transfer_date,	
prev_transfer_month_date,
prev_transfer_week_date,
prev_weekly_transfer_amount,
prev_weekly_transfer_count
);

commit;
--Alter P2P_SUBSCRIBERS History

Alter table p2p_subscribers_history 
Drop (Daily_Transfer_amount,
Daily_Transfer_Count,
Weekly_transfer_count,
Weekly_transfer_amount,
Monthly_transfer_amount,
Monthly_transfer_count,
Prev_daily_transfer_amount,
Prev_daily_transfer_count,
prev_monthly_transfer_amount,
prev_monthly_transfer_count,
prev_transfer_date,	
prev_transfer_month_date,
prev_transfer_week_date,
prev_weekly_transfer_amount,
prev_weekly_transfer_count
);


commit;

--update System_preferences table



UPDATE System_preferences 
SET type='SERTYPPREF' WHERE
PREFERENCE_CODE  IN ('DAY_SDR_MX_TRANS_AMT',
'DAY_SDR_MX_TRANS_NUM',
'MO_SUCTRAN_ALLWD_P2P',
 'MO_REC_AMT_ALLWD_P2P',
 'WE_SUCTRAN_ALLWD_P2P',
 'WE_REC_AMT_ALLWD_P2P',
'WK_SDR_MX_TRANS_NUM',
'WK_SDR_MX_TRANS_AMT',
'MON_SDR_MX_TRANS_AMT',
'MON_SDR_MX_TRANS_NUM',
'DA_REC_AMT_ALLWD_P2P',
'DA_SUCTRAN_ALLWD_P2P'
);
commit;




Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('P2P_SERVICES_TYPE_SERVICECLASS', 'P2P Services Preference type ', 'SYSTEMPRF', 'boolean', 'false', 
    NULL, NULL, 50, 'P2P Services Preference type as serviceType or service Class type', 'N', 
    'N', 'P2P', 'False for SERVICE TYPE and true for SERVICECLASS TYPE ', TO_DATE('09/21/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/21/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


Alter Table Barred_msisdns
 Add  for_msisdn varchar2(15);
 
 alter table barred_msisdns Modify for_msisdn   DEFAULT 'N.A';
 Commit;
 
 
 Alter table barred_msisdns drop CONSTRAINT Pk_barred_msisdns;
Commit;

update barred_msisdns Set for_msisdn= 'N.A.' where msisdn is NOT NULL;
Commit;


Alter Table Barred_msisdns modify (for_msisdn  NOT NULL);
Commit;

Alter Table Barred_msisdns add CONSTRAINT Pk_barred_msisdns_new PRIMARY KEY (MODULE,NETWORK_CODE,MSISDN,USER_TYPE,BARRED_TYPE,FOR_MSISDN);

Commit;



Drop Index pk_barred_msisdns;
commit;

UPDATE System_preferences 
SET type='SVCCLSPRF' WHERE
PREFERENCE_CODE  IN ('DA_REC_AMT_ALLWD_P2P',
'DA_SUCTRAN_ALLWD_P2P',
'WE_REC_AMT_ALLWD_P2P',
'WE_SUCTRAN_ALLWD_P2P',
'MO_SUCTRAN_ALLWD_P2P',
'MO_REC_AMT_ALLWD_P2P',
'MAX_ALLD_BALANCE_P2P'
);
commit;

 Insert into SUB_LOOKUPS
   (SUB_LOOKUP_CODE, LOOKUP_CODE, SUB_LOOKUP_NAME, LOOKUP_TYPE, STATUS, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, DELETE_ALLOWED)
 Values
   ('GMBBAR', 'P2PBARTYPE', 'Self Barred for PreTUPs', 'BARTP', 'Y', 
    TO_DATE('10/21/2005 16:50:44', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('10/26/2005 11:44:31', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'N');
COMMIT;



Update lookups Set Status = 'Y' where Lookup_code = 'SERTYPPREF';
commit;
 
 
 
update service_type set request_handler='com.btsl.pretups.requesthandler.GiveMeBalanceHandler' where service_type='CGMBALREQ';
Commit;





Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('GMBBAR', 'P2P', 'PRE', 'KEYWORD', 'com.btsl.pretups.p2p.subscriber.requesthandler.BarredSubscriberController', 
    'BARRED USER', 'BARRED USER For GMB Service', 'Y', TO_DATE('07/12/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/09/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'P2P Barred for GMB', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2', 
    'Y');
COMMIT;
 
 
 
 --Messages added For LOWBase and Zerobase FNF
 
 
SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '2600001', 'mclass^2&pid^61:2600001:You are zero based subscriber please recharge more .', 'ALL', 'mclass^2&pid^61:2600001:You are zero based subscriber please recharge more .', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '2600002', 'mclass^2&pid^61:2600002:You are Registered as FNF based subscriber.', 'ALL', 'mclass^2&pid^61:2600002:You are Registered as FNF based subscriber.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '2600004', 'mclass^2&pid^61:2600004:Subscriber {0} is low base subscriber with minimum amount as {}.', 'ALL', 'mclass^2&pid^61:2600004:Subscriber {0} is low base subscriber with minimum amount as {}.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;


update Service_type Set unregistered_access_allowed = 'Y' where SERVICE_TYPE = 'PBAR';
COMMIT;

--pvg defect 1092 entries for internet recharge
SET DEFINE OFF;
Insert into INTERFACE_TYPES
   (INTERFACE_TYPE_ID, INTERFACE_NAME, INTERFACE_CATEGORY, HANDLER_CLASS, UNDERPROCESS_MSG_REQD, 
    MAX_NODES, URI_REQ)
 Values
   ('INTRRC', 'INTRRC', 'PRE', 'com.inter.comverse.ComverseINHandler', 'N', 
    1, 'Y');
COMMIT;

SET DEFINE OFF;
Insert into INTF_NTWRK_PRFX_MAPPING
   (NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'INTID00015', '24', 'U', 'INTRRC', 
    TO_DATE('09/08/2016 18:13:24', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407', TO_DATE('09/08/2016 18:13:24', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407');
Insert into INTF_NTWRK_PRFX_MAPPING
   (NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'INTID00015', '24', 'V', 'INTRRC', 
    TO_DATE('09/08/2016 18:13:24', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407', TO_DATE('09/08/2016 18:13:24', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000011407');
COMMIT;


----------------------Correction Check For Network Summary report Ambiguous------

CREATE OR REPLACE PROCEDURE dump_trans_summary (
   start_date_time   IN   VARCHAR2
)
AS
   err_val                VARCHAR2 (2000);
   check_flag             NUMBER (2);
   check_flag2              NUMBER (2);
   alreadydoneexception   EXCEPTION;
      setelmentnotdoneexception   EXCEPTION;
-- Thrown if the Proc has already been executed already, and the process termnates
BEGIN
   SELECT COUNT (*)
     INTO check_flag
     FROM process_status p
    WHERE executed_upto = start_date_time
      AND  p.PROCESS_ID = 'RUNTRNSUM'
      AND SCHEDULER_STATUS = 'C';
    
     SELECT COUNT (*)
     INTO check_flag2
     from c2s_transfers c
     WHERE transfer_date = start_date_time
     and transfer_status in ('250','205');
    
   IF check_flag <> 0 
   THEN
                  INSERT INTO proc_error_log
                              (desc1
                              )
                       VALUES (   'RUNTRNSUM'
                               || 'RUN_DUPLICATE  At Time :-  '
                               || TO_CHAR (SYSDATE, 'DDMMRRRRHHMMSS')
                               || start_date_time
                               || SYSDATE
                               || 'F'
                              );

                  DBMS_OUTPUT.put_line
                              ('The Procedure is executed several times for the same date');
                  RAISE alreadydoneexception;
                  COMMIT;
                 
               
           ELSIF check_flag2 <>0
               THEN
                  INSERT INTO proc_error_log
                              (desc1
                              )
                       VALUES (   'RUNTRNSUM'
                               || 'The settelment process not run yet'
                               || TO_CHAR (SYSDATE, 'DDMMRRRRHHMMSS')
                               || start_date_time
                               || SYSDATE
                               || 'F'
                              );

                  DBMS_OUTPUT.put_line
                              ('The settelment process not run yet');
                  RAISE setelmentnotdoneexception;
                  COMMIT;
                  
                  
               
    ELSE 
 
      
      INSERT INTO transaction_summary
         SELECT transsumm_id.NEXTVAL, x.*
           FROM (SELECT   transfer_date, TO_CHAR (transfer_date_time, 'HH24'),
                          c.network_code, c.interface_id, c.sender_category,
                          c.service_type, c.sub_service,
                          request_gateway_code g_c,
                          SUM (DECODE (transfer_status, 200, 1, 0)
                              ) success_count,
                          SUM (DECODE (transfer_status, 200, 0, 250, 0, 1)
                              ) error_count,
                          SUM (DECODE (transfer_status,
                                       200, c.transfer_value,
                                       0
                                      )
                              ) success_amt,
                          SUM (DECODE (transfer_status,
                                       200, 0,
                                       250, 0,
                                       c.transfer_value
                                      )
                              ) error_amt,
                          SUM (receiver_tax1_value + receiver_tax2_value
                              ) tax_amt,
                          SUM (c.receiver_access_fee) access_fee,
                          SUM (c.receiver_transfer_value) as REC
                     FROM c2s_transfers c
                    WHERE transfer_date = start_date_time
                 GROUP BY transfer_date,
                          c.network_code,
                          c.interface_id,
                          c.service_type,
                          c.sub_service,
                          TO_CHAR (transfer_date_time, 'HH24'),
                          request_gateway_code,
                          c.sender_category) x;

      COMMIT;
   END IF;
EXCEPTION
   WHEN NO_DATA_FOUND
   THEN
      err_val := SQLERRM;

      INSERT INTO proc_error_log
           VALUES ('INSERT_INTO_TRANS-EXCEPTION NO_DATA_FOUND' || err_val);

      COMMIT;
   WHEN OTHERS
   THEN
      err_val := SQLERRM;

      INSERT INTO proc_error_log
           VALUES ('INSERT_INTO_TRANS-EXCEPTION in  OTHERS' || err_val);

      COMMIT;
END;
/


------------- Deletion of role from nadm and addition to BCU
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'NETWORKSUMRPT', '1');
   
   delete from CATEGORY_ROLES where CATEGORY_CODE='NWADM' and ROLE_CODE= 'NETWORKSUMRPT';
   
   update lookups set status='N' where lookup_type='VSTAT' and lookup_code='PE';
   COMMIT;
	

-- to assign user deletion service to channel admin for NG network, Kindly execute as per required network code
Insert into CATEGORY_SERVICE_TYPE(CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE) Values('BCU', 'USERDEL', 'NG');
COMMIT;

-- Login password minimum length should be 3
update SYSTEM_PREFERENCES set DEFAULT_VALUE='3' where PREFERENCE_CODE='MIN_LOGIN_PWD_LENGTH';
COMMIT;

update roles set status='N' where role_code='ASSOPTTRFRULE';
commit;