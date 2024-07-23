alter table channel_users add  LR_ALLOWED varchar(1);
alter table channel_users add  LR_MAX_AMOUNT varchar(10);
alter table user_transfer_counts add LAST_LR_STATUS varchar(20);
alter table user_transfer_counts add LAST_LR_TXNID varchar(20);

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LR_ENABLED', 'Last Recharge enabled', 'NETWORKPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Last Recharge enabled or not', 'N', 
    'Y', 'C2S', 'Last Recharge enabled or not', TO_DATE('05/22/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/22/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'true/false', 'Y');
COMMIT;



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TARGET_BASED_COMMISSION', 'Target based commission applicable', 'NETWORKPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Target based commission applicable', 'N', 
    'N', 'C2S', 'Target based commission applicable', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


alter table channel_users alter column LR_ALLOWED set DEFAULT 'N';


--Db script for 7.0

SET DEFINE OFF;
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('ACTYP', 'Amount/Count Type(Amount or Percentage)', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIn', 'N');
COMMIT;


SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CNT', 'Count', 'ACTYP', 'Y', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;
SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('AMT', 'Amt', 'ACTYP', 'Y', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;




alter table addnl_comm_profile_details add OTF_APPLICABLE_FROM date;
alter table addnl_comm_profile_details add OTF_APPLICABLE_TO date;
alter table addnl_comm_profile_details add OTF_TYPE varchar(10);



alter table adjustments add adnl_com_prfle_otf_detail_id varchar(10);
alter table adjustments add otf_type varchar(10);
alter table adjustments add otf_rate varchar(10);
alter table adjustments add otf_AMOUNT varchar(10);


create table addnl_comm_profile_otf_details 
(
adnl_com_prfle_otf_detail_id varchar(10) Primary key,
addnl_comm_profile_detail_id varchar(10),
otf_value varchar(10),
otf_type varchar(10),
otf_rate varchar(10)

);
commit;
create table user_transfer_otf_count
 (
user_id varchar(10) Primary key,
adnl_com_prfle_otf_detail_id  varchar(10),
OTF_COUNT varchar(10),
OTF_Value varchar(10)
);
commit;



alter table channel_transfers alter column transaction_mode type varchar(2);



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TARGET_BASED_COMMISSION_SLABS', 'Target based commission maximum slabs', 'NETWORKPRF', 'INT', '5', 
    1, 10, 50,'Target based commission maximum slabs', 'N', 
    'N', 'C2S', 'Target based commission applicable', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;



SET DEFINE OFF;
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('ALL', 'ADDCOMMOTF', 'ALL', 6, TO_DATE('02/20/2017 15:16:56', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);
COMMIT;

alter table user_transfer_otf_count alter column user_id type varchar(20); 

alter table user_transfer_otf_count drop constraint user_transfer_otf_count_pkey;

alter table user_transfer_otf_count add constraint pkc_key  primary key (user_id,adnl_com_prfle_otf_detail_id);



alter table addnl_comm_profile_details add OTF_TIME_SLAB varchar(35);

---for message master table for LR Settlement
SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '303135', 'mclass^2&pid^61:303135:Withdrawal request, against pending Last Recharge loan, of product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against Last Recharge Loan is {2}.','NG', 
    'mclass^2&pid^61:303135:Withdrawal request, against pending Last Recharge loan, of product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against Last Recharge Loan is {2}.', 'mclass^2&pid^61:303135:Withdrawal request, against pending Last Recharge loan, of product(s) {0} is successful,  your new balance of product(s) {1}. Transfer ID against Last Recharge Loan is {2}.',NULL,'Y',NULL,NULL,NULL);

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('303135', '0', 'LR pending Amount (productcode:Amount)');

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('303135','1','User Balances (productcode:Balance)');

Insert into MESSAGE_ARGUMENT
   (MESSAGE_CODE, ARGUMENT, ARGUMENT_DESCRIPTION)
 Values
   ('303135', '2', 'Transaction ID');
COMMIT;

Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('2017', 'BATCH_ID', 'PB', 411, TO_DATE('03/31/2008 10:01:14', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);
   
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
    Values
   ('2017', 'BATCH_ID', 'NG', 411, TO_DATE('03/31/2008 10:01:14', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);
COMMIT;




--added by kashish
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DECRYPT_KEY_VISIBLE', 'Decrypt key visible', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Decrypt key visible in voucher Download', 'N', 
    'Y', 'O2C', 'Decrypt key vivible in voucher Download', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
		
	Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('O2CDT', 'O2C Distribution Type', CURRENT_TIMESTAMP, 'ADMIN', CURRENT_TIMESTAMP,'ADMIN', 'N');
   
   Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('STOCK', 'Stock', 'O2CDT', 'N', CURRENT_TIMESTAMP,'ADMIN', CURRENT_TIMESTAMP, 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('VOUCHTRACK', 'Voucher', 'O2CDT', 'Y', CURRENT_TIMESTAMP, 
    'ADMIN', CURRENT_TIMESTAMP , 'ADMIN');
	
	
alter table voms_products add EXPIRY_DATE DATE ;


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('THIRD_PARTY_VISIBLE', 'third party visible', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Third party visible in voucher Download', 'N', 
    'Y', 'O2C', 'Decrypt key vivible in voucher Download', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
	
	Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('WH', 'WareHouse', 'RSTAT', 'N', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PE', 'Print Enabled', 'RSTAT', 'N', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('S', 'Suspended', 'RSTAT', 'N', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	
	Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('VOMS_PROFILE_ACTIVATION_REQ', 'VOMS PROFILE ACTIVATION REQ', 'SYSTEMPRF', 'boolean', 'false', 
    NULL, NULL, 50, 'Voucher profile activation required', 'N', 
    'N', 'C2S', 'false if profile activation not required , true if required ', TO_DATE('06/13/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/13/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
	
	
	
	

	Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('VB', 'C2S', 'PRE', 'TYPE SUBID PIN', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherEnqConHandler', 
    'Voucher RollBack Service', 'Voucher RollBack Service', 'Y', TO_DATE('02/11/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/11/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Voucher RollBack Service', 'Y', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,PIN,SUBID,LOGINID,PASSWORD,EXTREFNUM,EXTNWCODE,EXTCODE,LANGUAGE1,LANGUAGE2', 
    'Y');

SET DEFINE OFF;
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('BULKUSRSUSPN', TO_DATE('06/08/2017 16:29:56', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('06/08/2017 16:29:56', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('06/08/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Bulk User Suspension', 'NG', '0');
COMMIT;


SET DEFINE OFF;
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BLKAMBS001', 'RECONCIL', '/bulkAmbiguousSettlement.do?method=loadBulkCaseSettlement', 'Bulk Ambiguous Settlement', 'Y', 
    580, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BLKAMBS002', 'RECONCIL', '/jsp/c2sreconciliation/bulkAmbiguousSettlement.jsp', 'Bulk Ambiguous Settlement', 'N', 
    580, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BLKAMBS003', 'RECONCIL', '/jsp/c2sreconciliation/bulkAmbiguousSettlement.jsp', 'Bulk Ambiguous Settlement', 'N', 
    580, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BLKAMBS01A', 'RECONCIL', '/bulkAmbiguousSettlement.do?method=loadBulkCaseSettlement', 'Bulk Ambiguous Settlement', 'N', 
    580, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BLKAMBSDMM', 'RECONCIL', '/bulkAmbiguousSettlement.do?method=loadBulkCaseSettlement', 'Bulk Ambiguous Settlement', 'Y', 
    580, '1', '1');
COMMIT;

SET DEFINE OFF;
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BLKAMBSETLMNT', 'BLKAMBS001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BLKAMBSETLMNT', 'BLKAMBS002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BLKAMBSETLMNT', 'BLKAMBS003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BLKAMBSETLMNT', 'BLKAMBS01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BLKAMBSETLMNT', 'BLKAMBSDMM', '1');
COMMIT;

SET DEFINE OFF;
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'BLKAMBSETLMNT', '1');
COMMIT;

SET DEFINE OFF;
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'BLKAMBSETLMNT', 'Bulk Ambiguous Settlement', 'Reconciliation', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;



update SYSTEM_PREFERENCES set modified_allowed='Y' where preference_code='TARGET_BASED_COMMISSION_SLABS';
update SYSTEM_PREFERENCES set display='Y' where preference_code='TARGET_BASED_COMMISSION_SLABS';
update SYSTEM_PREFERENCES set modified_allowed='Y' where preference_code='TARGET_BASED_COMMISSION';
update SYSTEM_PREFERENCES set display='Y' where preference_code='TARGET_BASED_COMMISSION';
commit;

Alter table addnl_comm_profile_otf_details alter column otf_value type varchar(16);
Alter table addnl_comm_profile_otf_details alter column otf_rate type varchar(16);

commit;
--file name column size increse to 40 char ref from Oracle DB
ALTER TABLE batches ALTER COLUMN file_name TYPE character varying(40);

alter table c2s_transfers add column stock_updated varchar(2)  default 'Y';

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('OFFLINE_SETTLE_EXTUSR', 'Offline Stock Settlement', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Offline Recharge Stock Settlement flag allows to external system users stock deduction at real time as a part of recharge if flag is set to false. If flag is set to true, deduction will happen offline', 'Y', 
    'Y', 'C2S', 'Offline Recharge Stock Settlement flag allows to external system users stock deduction at real time as a part of recharge if flag is set to false. If flag is set to true, deduction will happen offline', TO_DATE('01/09/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/09/2017 16:10:58', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'true/false', 'Y');

Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('C2STRFSTDD', TO_DATE('01/23/2017 21:21:21', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('01/22/2017 02:53:36', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/23/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Offline Recharge txn Stock Deduction  process', 'NG', 10);

COMMIT; 
DROP TABLE ADDNL_COMM_PROFILE_OTF_DETAILS;


commit;



                create table profile_otf_details 
                (
                prfle_otf_detail_id varchar(10) Primary key,
                profile_detail_id varchar(10),
                otf_value varchar(10),
                otf_type varchar(10),
                otf_rate varchar(10),
                comm_type varchar(6)
);


alter table COMMISSION_PROFILE_DETAILS add OTF_APPLICABLE_FROM date;
alter table COMMISSION_PROFILE_DETAILS add OTF_APPLICABLE_TO date;
alter table COMMISSION_PROFILE_DETAILS add OTF_TIME_SLAB varchar(40);

update ids set id_type='COMMOTF' where id_type='ADDCOMMOTF';
commit;

SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TARGET_BASED_BASE_COMMISSION_SLABS', 'Targetbased base commission maximumslabs', 'NETWORKPRF', 'INT', '5', 
    1, 10, 50, 'Target based base commission maximum slabs', 'Y', 
    'Y', 'C2S', 'Target based commission applicable', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TARGET_BASED_BASE_COMMISSION', 'Target based base commission', 'NETWORKPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Target based base commission', 'Y', 
    'Y', 'C2S', 'Target based commission applicable', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


ALTER TABLE USERS ADD ALLOWD_USR_TYP_CREATION VARCHAR(10);
update USERS set ALLOWD_USR_TYP_CREATION='ALL';

SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('ALLOWD_USR_TYP_CREATION', 'Allowed User Type Creation', 'SYSTEMPRF', 'STRING', 'Y', 
    NULL, NULL, 50, 'Allowed user type creation for Network Admin(if Y then display otherwise not)', 'N', 
    'N', 'C2S', 'Allowed user type creation for Network Admin(if Y then display otherwise not)', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


   SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SYSTEM_ROLE_ALLOWED', 'System role allowed', 'SYSTEMPRF', 'STRING', 'N', 
    NULL, NULL, 50, 'System role option is dispalyed or not(if Y then display otherwise not)', 'N', 
    'N', 'C2S', 'System role option is dispalyed or not(if Y then display otherwise not)', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


SET DEFINE OFF;
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('TRFRUL013', 'TRFRULES', '/modifyC2STransferRule.do?method=loadOrderByC2STransferRulesList', 'Modify C2S Transfer Rules', 'Y', 
    6, '2', '1');



Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODC2STRFRULES', 'TRFRUL013', '1');



update PAGES set MENU_ITEM= 'N' where PAGE_CODE= 'TRFRUL008';


SET DEFINE OFF;
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('ORDBY', 'Order By list', TO_DATE('07/04/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('07/04/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;



SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('RG', 'Request Gateway', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CD', 'Channel Domain', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');


Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('TYP', 'Type', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');	
	
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SC', 'Service Class', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');	
		
	
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('ST', 'Service Type', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SS', 'Sub Service', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CGS', 'Card Group Set', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('STATUS', 'Status', 'ORDBY', 'Y', TO_DATE('03/19/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('07/05/2013 13:46:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;	


	
Alter table profile_otf_details alter column otf_value varchar(16);
Alter table profile_otf_details alter column otf_rate varchar(16);




Insert into MESSAGE_GATEWAY
   (GATEWAY_CODE, GATEWAY_NAME, GATEWAY_TYPE, GATEWAY_SUBTYPE, PROTOCOL, 
    HANDLER_CLASS, NETWORK_CODE, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, HOST, STATUS, REQ_PASSWORD_PLAIN)
 Values
   ('REST', 'REST', 'REST', 'REST', 'HTTP', 
    'com.btsl.pretups.gateway.parsers.RestParser', 'NG', TO_DATE('12/27/2010 14:22:42', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('01/27/2017 16:31:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'SU0001', '127.0.0.1', 'Y', 'Y');
COMMIT;





Insert into REQ_MESSAGE_GATEWAY
   (GATEWAY_CODE, PORT, SERVICE_PORT, LOGIN_ID, PASSWORD, 
    ENCRYPTION_LEVEL, ENCRYPTION_KEY, CONTENT_TYPE, AUTH_TYPE, STATUS, 
    MODIFIED_ON, MODIFIED_BY, CREATED_ON, CREATED_BY, UNDERPROCESS_CHECK_REQD)
 Values
   ('REST', '100', '190', 'pretups', '5359410680b3a555', 
    'NA', NULL, 'JSON', 'LOGIN', 'Y', 
    TO_DATE('01/27/2017 16:31:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/27/2010 14:22:42', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'N');
COMMIT;

Insert into RES_MESSAGE_GATEWAY
   (GATEWAY_CODE, PORT, SERVICE_PORT, LOGIN_ID, PASSWORD, 
    DEST_NO, STATUS, MODIFIED_ON, MODIFIED_BY, CREATED_ON, 
    CREATED_BY, PATH, TIMEOUT)
 Values
   ('REST', '8007', '190', 'test', '5359410680b3a555', 
    '190', 'Y', TO_DATE('01/27/2017 16:31:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('11/28/2009 14:15:42', 'MM/DD/YYYY HH24:MI:SS'), 
    'SU0001', 'pretups/test.html1?', 10000);
COMMIT;



Insert into MESSAGE_GATEWAY_TYPES
   (GATEWAY_TYPE, GATEWAY_TYPE_NAME, ACCESS_FROM, PLAIN_MSG_ALLOWED, BINARY_MSG_ALLOWED, 
    FLOW_TYPE, RESPONSE_TYPE, TIMEOUT_VALUE, DISPLAY_ALLOWED, MODIFY_ALLOWED, 
    USER_AUTHORIZATION_REQD)
 Values
   ('REST', 'REST', 'LOGIN', 'Y', 'Y', 
    'C', 'RESPONSE', 10000, 'Y', 'Y', 
    'Y');
COMMIT;



Insert into MESSAGE_GATEWAY_SUBTYPES
   (GATEWAY_SUBTYPE, GATEWAY_TYPE, GATEWAY_SUBTYPE_NAME)
 Values
   ('REST', 'REST', 'REST');
COMMIT;



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALLOW_TRANSACTION_IF_SOS_SETTLEMENT_FAIL', 'Allow Transaction if SOS settelment fail', 'SYSTEMPRF', 'STRING', 'FOC, DP, C2CREV', 
    NULL, NULL, 50, 'This flag allow transaction to complete if SOS settelment condition gets failed', 'N', 
    'Y', 'C2S', 'This flag allow transaction to complete if SOS settelment condition gets failed', TO_DATE('05/15/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'FOC, O2C, C2C, DP, C2CREV', 'Y');

COMMIT;


truncate table user_transfer_otf_count;

alter table user_transfer_otf_count add prfle_otf_detail_id varchar(10);

alter table user_transfer_otf_count add comm_type varchar(6);

alter table user_transfer_otf_count drop constraint PKC_KEY;

alter table user_transfer_otf_count drop column adnl_com_prfle_otf_detail_id;

alter table user_transfer_otf_count add constraint pkc_key  primary key (user_id,prfle_otf_detail_id,comm_type);

alter table channel_transfers_items add otf_type varchar(10);
alter table channel_transfers_items add otf_rate varchar(10);
alter table channel_transfers_items add otf_amount varchar(10);

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUNADM', 'BLKAMBSETLMNT', '1');


update SYSTEM_PREFERENCES set PREFERENCE_CODE='TARGET_BASED_ADDNL_COMMISSION_SLABS' where PREFERENCE_CODE='TARGET_BASED_COMMISSION_SLABS';
commit;

update SYSTEM_PREFERENCES set PREFERENCE_CODE='TARGET_BASED_ADDNL_COMMISSION' where PREFERENCE_CODE='TARGET_BASED_COMMISSION';
commit;

update SYSTEM_PREFERENCES set NAME='Targetbased ADDNLCommission maximumslabs',DESCRIPTION ='Target based Additional commission maximum slabs', REMARKS = 'Target based Additional commission applicable'  where PREFERENCE_CODE='TARGET_BASED_ADDNL_COMMISSION_SLABS';
commit;

update SYSTEM_PREFERENCES set NAME='Target based ADDNL_commission applicable',DESCRIPTION ='Target based Additional commission applicable', REMARKS = 'Target based Additional commission applicable'  where PREFERENCE_CODE='TARGET_BASED_ADDNL_COMMISSION';


alter table user_transfer_counts add c2sbalance_settled_date date;


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
Values
   ('C2SBALSETLMNT', TO_DATE('02/07/2017 19:31:34', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('02/06/2017 02:53:36', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('02/07/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'c2s balance settlement', 'NG', 10);
COMMIT

alter table c2s_transfers drop column stock_updated; 



alter table c2s_transfers drop column stock_updated; 



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALIAS_TO_BE_ENCRYPTED', 'Alias to be encryped', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 5, 'Alias to be encryped ,true or false', 'Y', 
    'N', 'C2S', 'Alias to be encryped', TO_DATE('07/25/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/25/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

ALTER TABLE USER_PHONES ADD STATUS_AUTO_C2C varchar(1); 

ALTER TABLE ONLY USER_PHONES ALTER COLUMN status_auto_c2c SET DEFAULT 'C';

update user_phones set STATUS_AUTO_C2C='C';



ALTER TABLE Profile_otf_details DROP CONSTRAINT Profile_otf_details_pkey;


ALTER TABLE Profile_otf_details
ADD PRIMARY KEY (prfle_otf_detail_id,profile_detail_id);

-- CP2P DATA SQL QUERY START 

Insert into SERVICE_TYPE(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER,  ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED,  STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE,  FILE_PARSER, RECEIVER_USER_SERVICE_CHECK, ERP_HANDLER)
 Values('CDATA', 'P2P', 'BOTH', 'KEYWORD PIN', 'com.btsl.pretups.p2p.transfer.requesthandler.PrepaidController',  'P2P Data Recharge', 'P2P Data Recharge', 'Y', CURRENT_TIMESTAMP, 'ADMIN',  CURRENT_TIMESTAMP, 'ADMIN', 'CP2P Data Transfer', 'Y', 'Y',  'Y', NULL, 'N', 'NA', 'N',  NULL, 'Y', 'NA');
	
	
insert into service_payment_mapping values('CDATA','PRE','PRE','PRE',CURRENT_TIMESTAMP);
-- change network code accordingly
insert into network_services values ('P2P' , 'CDATA' , 'NG','NG','Y','','','SU0001',CURRENT_TIMESTAMP,'SU0001',CURRENT_TIMESTAMP);
insert into product_service_type_mapping values ('P2P','CDATA','ADMIN',CURRENT_TIMESTAMP,'ADMIN',CURRENT_TIMESTAMP,'N','N','DEF');
insert into INTERFACE_TYPES values('CDATAZTE','CDATAZTE','PRE','com.inter.zteocm.zteocmdata.CP2PDataSimulatorINHandler','N',1,'N');
Alter table Transfer_Items alter column interface_reference_id type varchar(21);


Insert into LOOKUPS (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('CDATAZTE', 'CP2P Data Interface', 'INTCT', 'Y', CURRENT_TIMESTAMP, 'ADMIN', CURRENT_TIMESTAMP, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values('CDATAZTE', 'CP2P Data Interface', 'INCAT', 'Y', CURRENT_TIMESTAMP, 'ADMIN', CURRENT_TIMESTAMP, 'ADMIN');

 -- execute this query after creating Selector Mapping for CDATA
update service_type_selector_mapping set receiver_bundle_id='37' where service_type='CDATA';
-- update according to current Configuration , please append CDATA to the current value
update system_preferences set default_value='RC,DTH,PIN,DC,CE,CBP,PMD,CCN,RPB,MRC,CDATA' where  preference_code = 'SRVC_PROD_INTFC_MAPPING_ALLOWED';
-- CP2P DATA SQL QUERY END=======

------------------ Committed by Anjali ----------------
ALTER TABLE CHANNEL_TRANSFERS
ADD column INFO1 VARCHAR(100),
ADD	column INFO2 VARCHAR(100);

Insert into SYSTEM_PREFERENCES
  (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
   	 MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
  	  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    	MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
('CHANNEL_TRANSFERS_INFO_REQUIRED', 'Chnnl Transfers Info Required', 'SYSTEMPRF', 'BOOLEAN', 'true', 
   	 NULL, NULL, 5, 'O2C C2C INFO Required in Channel Transfers', 'N', 
   	 'N', 'C2S', 'O2C C2C INFO Required in Channel Transfers', current_timestamp, 'ADMIN', 
  	  current_timestamp, 'ADMIN', NULL, 'Y');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DOWNLOAD_CSV_REPORT_REQUIRED', 'Download Reports in csv ', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Download Reports in csv required', 'N', 
    'N', 'C2S', 'Download Reports in csv required', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CHNL_USR_LAST_ACTIVE_TXN', 'Channel user last days ative', 'SYSTEMPRF', 'INT', '5', 
    NULL, NULL, 5, 'Channel user last actiive days', 'N', 
    'N', 'C2S', 'Channel user last acive days', TO_DATE('06/23/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/23/2017 21:41:03', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('BATO2CINITN', TO_DATE('06/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('06/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('06/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Batch O2C Initiation', 'IN', 0);
	
ALTER TABLE PROFILE_OTF_DETAILS ALTER COLUMN OTF_VALUE TYPE varchar(15);


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('TARGETBASECOMMISSION', TO_DATE('07/24/2017 14:52:49', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('07/24/2017 14:42:34', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('07/24/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Offline Messages for OTF slabs', 'NG', 0);
COMMIT;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('REALTIME_OTF_MESSAGES', 'Messages in Target based commission ', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Messages in Target based commission ', 'Y', 
    'Y', 'C2S', 'Target based commission applicable', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;
ALTER TABLE VOMS_VOUCHERS add OTHER_INFO varchar(200);
ALTER TABLE VOMS_TYPES alter column NAME type varchar(50);


alter table c2s_transfers alter subs_sid type varchar(50);


Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '8116', 'mclass^2&pid^61:8116:FOC transfer request of product(s) {1} has been approved successfully, your new balance of product(s) {2} INR. Transaction ID is {0}. Remarks is:{3}.', 'ALL', 'mclass^2&pid^61:8116:FOC transfer request of product(s) {1} has been approved successfully, your new balance of product(s) {2} INR. Transaction ID is {0}. Remarks is:{3}.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
	

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('POST_SERVICE_CLASS', 'Postpaid Subs service class', 'SYSTEMPRF', 'INT', '1000', 
    NULL, NULL, 50, 'postpaid subscriber service class .', 'Y', 
    'Y', 'C2S', 'postpaid subscriber service class .', TO_DATE('03/22/2016 11:43:12', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('03/22/2016 11:43:22', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;	

-- GP Code Merge for 3.1 AIR IP MANAGEMENT - ENHANCEMENT (START) =======
------------------ Committed by Sanjay ----------------
CREATE TABLE CONFIGURATIONS
(
  INSTANCE_ID       VARCHAR(10)           NOT NULL,
  INTERFACE_ID      VARCHAR(20)           NOT NULL,  
  TYPE              VARCHAR(20)           NOT NULL,
  KEY               VARCHAR(75)           NOT NULL,
  VALUE             VARCHAR(200),
  DESCRIPTION       VARCHAR(300),
  MODIFIED_ALLOWED  VARCHAR(1)            NOT NULL,
  DISPLAY_ALLOWED   VARCHAR(1)            NOT NULL,
  CREATED_ON        TIMESTAMP(0)                        NOT NULL,
  CREATED_BY        VARCHAR(20)           NOT NULL,
  MODIFIED_ON       TIMESTAMP(0)                        NOT NULL,
  MODIFIED_BY       VARCHAR(20)           NOT NULL
);

COMMENT ON COLUMN CONFIGURATIONS.INSTANCE_ID IS 'Instance ID';

COMMENT ON COLUMN CONFIGURATIONS.INTERFACE_ID IS 'Interface ID';

COMMENT ON COLUMN CONFIGURATIONS.TYPE IS 'Type of Configuration';

COMMENT ON COLUMN CONFIGURATIONS.KEY IS 'Key';

COMMENT ON COLUMN CONFIGURATIONS.VALUE IS 'Value of key';

COMMENT ON COLUMN CONFIGURATIONS.DESCRIPTION IS 'Description of key';

COMMENT ON COLUMN CONFIGURATIONS.MODIFIED_ALLOWED IS 'modified allowed Y/N, if Y then it can be modified';

COMMENT ON COLUMN CONFIGURATIONS.DISPLAY_ALLOWED IS 'display allowed Y/N, if Y then it can be modified';

COMMENT ON COLUMN CONFIGURATIONS.CREATED_ON IS 'key created on';

COMMENT ON COLUMN CONFIGURATIONS.CREATED_BY IS 'key created by';

COMMENT ON COLUMN CONFIGURATIONS.MODIFIED_ON IS 'key modified on';

COMMENT ON COLUMN CONFIGURATIONS.MODIFIED_BY IS 'key modified by';

CREATE INDEX INST_INTER_TYPE_KEY ON CONFIGURATIONS
(INSTANCE_ID,INTERFACE_ID,TYPE,KEY);

CREATE INDEX INST_INTER_TYPE ON CONFIGURATIONS
(INSTANCE_ID,INTERFACE_ID,TYPE);

CREATE TABLE CONFIGURATIONS_HISTORY
(
  INSTANCE_ID           VARCHAR(10)           NOT NULL,
  INTERFACE_ID          VARCHAR(20)           NOT NULL,  
  TYPE                  VARCHAR(20)           NOT NULL,
  KEY                   VARCHAR(75)           NOT NULL,
  VALUE                 VARCHAR(200),
  DESCRIPTION           VARCHAR(300),
  MODIFIED_ALLOWED      VARCHAR(1)            NOT NULL,
  DISPLAY_ALLOWED       VARCHAR(1)            NOT NULL,
  CREATED_ON            TIMESTAMP(0)                        NOT NULL,
  CREATED_BY            VARCHAR(20)           NOT NULL,
  MODIFIED_ON           TIMESTAMP(0)                        NOT NULL,
  MODIFIED_BY           VARCHAR(20)           NOT NULL,
  ENTRY_DATE            TIMESTAMP(0)                        NOT NULL,
  OPERATION_PERFORMED   VARCHAR(1)            NOT NULL  
);

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.INSTANCE_ID IS 'Instance ID';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.INTERFACE_ID IS 'Interface ID';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.TYPE IS 'Type of Configuration';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.KEY IS 'Key';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.VALUE IS 'Value of key';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.DESCRIPTION IS 'Description of key';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.MODIFIED_ALLOWED IS 'modified allowed Y/N, if Y then it can be modified';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.DISPLAY_ALLOWED IS 'display allowed Y/N, if Y then it can be modified';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.CREATED_ON IS 'key created on';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.CREATED_BY IS 'key created by';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.MODIFIED_ON IS 'key modified on';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.MODIFIED_BY IS 'key modified by';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.ENTRY_DATE IS 'modification date of key in Configurations table';

COMMENT ON COLUMN CONFIGURATIONS_HISTORY.OPERATION_PERFORMED IS 'Type of operation performed on key';

-- Function: trig_configurations_history()
CREATE OR REPLACE FUNCTION trig_configurations_history()
  RETURNS trigger AS
$BODY$  
BEGIN

IF (TG_OP = 'INSERT') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(NEW.INSTANCE_ID, NEW.INTERFACE_ID,
NEW.TYPE, NEW.KEY, NEW.VALUE, NEW.DESCRIPTION, NEW.MODIFIED_ALLOWED,
NEW.DISPLAY_ALLOWED, NEW.CREATED_ON, NEW.CREATED_BY, NEW.MODIFIED_ON, NEW.MODIFIED_BY, sysdate,'I');

ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(NEW.INSTANCE_ID, NEW.INTERFACE_ID,
NEW.TYPE, NEW.KEY, NEW.VALUE, NEW.DESCRIPTION, NEW.MODIFIED_ALLOWED,
NEW.DISPLAY_ALLOWED, NEW.CREATED_ON, NEW.CREATED_BY, NEW.MODIFIED_ON, NEW.MODIFIED_BY, sysdate,'U');

ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(OLD.INSTANCE_ID, OLD.INTERFACE_ID,
OLD.TYPE, OLD.KEY, OLD.VALUE, OLD.DESCRIPTION, OLD.MODIFIED_ALLOWED,
OLD.DISPLAY_ALLOWED, OLD.CREATED_ON, OLD.CREATED_BY, OLD.MODIFIED_ON, OLD.MODIFIED_BY, sysdate,'D');
END IF;
RETURN NULL;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION trig_configurations_history()
  OWNER TO pgdb;

ALTER TABLE CONFIGURATIONS ADD 
  CONSTRAINT PK_CONFIGURATIONS
 PRIMARY KEY
 (INSTANCE_ID,INTERFACE_ID,KEY);
 
 Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'MODCONFIGFILE', 'File Cache', 'Preferences', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF013', 'PREFERENCE', '/selectConfigurationType.do?method=loadConfigurationType', 'File Cache', 'Y', 
    1, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF001', 'PREFERENCE', '/jsp/configuration/selectConfigurationType.jsp', 'File Cache', 'N', 
    1, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF002', 'PREFERENCE', '/configuration/displayINTIDConfigurationDetail.jsp', 'File Cache', 'N', 
    1, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF003', 'PREFERENCE', '/configuration/viewINTIDConfigurationData.jsp', 'File Cache', 'N', 
    1, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF1DMM', 'PREFERENCE', '/selectConfigurationType.do?method=loadConfigurationType', 'File Cache', 'Y', 
    1, '1', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('CNF001A', 'PREFERENCE', '/selectConfigurationType.do?method=loadConfigurationType', 'File Cache', 'N', 
    1, '2', '1');
COMMIT;

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF001A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF013', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODCONFIGFILE', 'CNF1DMM', '1');
COMMIT;

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'MODCONFIGFILE', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SSADM', 'MODCONFIGFILE', '1');
COMMIT;

Insert into GROUP_ROLES
   (GROUP_ROLE_CODE, ROLE_CODE)
 Values
   ('SSADM', 'MODCONFIGFILE');
COMMIT;

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('COTYP', 'Type', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('11/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('INTIDF', 'Interface', 'COTYP', 'Y', TO_DATE('08/18/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/18/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

CREATE TRIGGER trig_configurations_history
 AFTER INSERT OR UPDATE OR DELETE ON CONFIGURATIONS FOR EACH ROW
  EXECUTE PROCEDURE trig_configurations_history();
-- GP Code Merge for 3.1 AIR IP MANAGEMENT - ENHANCEMENT (END) =======

-- GP Code Merge for 3.1 AIR IP MANAGEMENT - ENHANCEMENT (END) =======

-- FOR VOMS VOUCHER API  (START)==

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SUBS_BLK_AFT_X_CONS_FAIL', 'Subsc Consecutive Fail Count', 'SYSTEMPRF', 'INT', '4', 
    0, 50, 9, 'Subscriber Consecutive Fail Count before Bar (Not being used)', 'N', 
    'Y', 'C2S', 'Subscriber Consecutive Fail Count', TO_DATE('06/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('03/17/2006 17:51:58', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SUBS_UNBLK_AFT_X_TIME', 'Unblock Subsc After X Time', 'SYSTEMPRF', 'INT', '90', 
    0, 1000, 9, 'Unblock Subsc After X Time (Not being used)', 'N', 
    'Y', 'C2S', 'Unblock Subsc After X Time', TO_DATE('06/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('03/17/2006 17:51:58', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, REQUEST_PARAM, RESPONSE_PARAM)
Values
   ('VCNVAS', 'P2P', 'PRE', 'TYPE MSISDN VOUCHER_CODE LANGUAGE', 'com.btsl.pretups.channel.transfer.requesthandler.VASVoucherConsController', 
    'VAS Voucher Consumption', 'PreTUPS VAS Voucher Consumption', 'Y', TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'PreTUPS VAS Voucher Consumption', 'Y', 'Y', 
    'Y', NULL, 'N', 'NA', 'N', 
    'com.btsl.pretups.gateway.parsers.IVRPlainStringParser.java', NULL, NULL, 'TYPE,MSISDN,VOUCHER_CODE,LANGUAGE', 'TYPE,TXNSTATUS,MESSAGE');

COMMIT;

-- FOR VOMS VOUCHER API  (END)==


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ADD_COMM_SEPARATE_MSG', 'Additional commission seprate message', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Additional commission seprate message', 'N', 
    'Y', 'C2S', 'Additional commission seprate message', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;

UPDATE SYSTEM_PREFERENCES SET DEFAULT_VALUE='true' WHERE PREFERENCE_CODE ='ENQ_POSTBAL_ALLOW';
COMMIT;

update system_preferences set modified_allowed ='Y',display='Y' where preference_code='MIN_VALIDITY_DAYS';
COMMIT;

--voucher change status service type entry start

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('VSCH', 'C2S', 'PRE', 'TYPE FROM_SERIALNO TO_SERIALNO STATUS', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherStatusChangeHandler', 
    'Voucher Status Change Service', 'VOMS Status Changes', 'Y', TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/01/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Voucher Status Change Service', 'Y', 'Y', 
    'Y', NULL, 'N', 'NA', 'N', 
    'com.btsl.pretups.gateway.parsers.IVRPlainStringParser.java', NULL, NULL, 'TYPE,FROM_SERIALNO,TO_SERIALNO,PRE_STATUS,REQ_STATUS,TXNSTATUS,MESSAGE', 'TYPE,FROM_SERIALNO,TO_SERIALNO,STATUS', 
    'Y');
--voucher change status service type entry END

--voucher change status mapping for NG and PB network--
CREATE TABLE VOMS_VOUCHERS_STATUS_MAPPING
(
  STATUS         character varying(15)          NOT NULL,
  MAPPED_STATUS  character varying(15)              NOT NULL,
  NETWORK_CODE   character varying(2)               NOT NULL,
  CREATED_DATE   DATE,
  CREATED_BY     character varying(20),
  MODIFIED_DATE  DATE,
  MODIFIED_BY    character varying(20)
)

Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('GE', 'PA', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('GE', 'EN', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PE', 'WH', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'PA', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'EN', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'S', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'S', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'EN', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'S', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'S', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'ST', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'OH', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'EX', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('S', 'EN', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('OH', 'EN', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('OH', 'ST', 'NG', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
	
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('GE', 'PA', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('GE', 'EN', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PE', 'WH', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'PA', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'EN', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('WH', 'S', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'S', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'EN', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'S', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'S', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'ST', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'OH', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'EX', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('S', 'EN', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('OH', 'EN', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('OH', 'ST', 'PB', TO_DATE('08/12/2015 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    NULL, NULL);

commit;


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMENQ001', 'C2SENQ', '/commissionProfileEnquiry.do?method=loadGeoDomainList', 'Commission Profile Enquiry', 'Y', 
    7, '2', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMENQ00A', 'C2SENQ', '/commissionProfileEnquiry.do?method=loadGeoDomainList', 'Commission Profile Enquiry', 'N', 
    7, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMENQDMM', 'C2SENQ', '/commissionProfileEnquiry.do?method=loadGeoDomainList', 'Commission Profile Enquiry', 'Y', 
    7, '1', '1');

COMMIT;


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMENQ002', 'C2SENQ', '/jsp/profile/enqViewCommDetail.jsp', 'Commission Profile Enquiry', 'N', 
    7, '2', '1');



Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('COMMENQ', 'COMENQ001', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('COMMENQ', 'COMENQ00A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('COMMENQ', 'COMENQDMM', '1');
   
   Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('COMMENQ', 'COMENQ002', '1');
   

COMMIT;

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'COMMENQ', 'Commission Profile Enquiry', 'Channel Enquiry', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;



Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('CCE', 'COMMENQ', '1');
COMMIT;

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUCCE', 'COMMENQ', '1');
COMMIT;


Update system_preferences set default_value='False' where preference_code='OWNER_COMMISION_ALLOWED'
commit;


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMSLF001', 'C2SENQ', '/selfCommissionEnquiry.do?method=viewSelfCommDetail', 'Self Commission Enquiry', 'Y', 
    21, '2', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMSLF00A', 'C2SENQ', '/selfCommissionEnquiry.do?method=viewSelfCommDetail', 'Self Commission Enquiry', 'N', 
    21, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('COMSLFDMM', 'C2SENQ', '/selfCommissionEnquiry.do?method=viewSelfCommDetail', 'Self Commission Enquiry', 'Y', 
    21, '1', '1');

COMMIT;





Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('SLFCOMMENQ', 'COMSLF001', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('SLFCOMMENQ', 'COMSLF00A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('SLFCOMMENQ', 'COMSLFDMM', '1');

 Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('SLFCOMMENQ', 'COMENQ002', '1');

COMMIT;

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('DISTB_CHAN', 'SLFCOMMENQ', 'Self Commission Enquiry', 'Channel Enquiry', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;



Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('DIST', 'SLFCOMMENQ', '1');
COMMIT;

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SE', 'SLFCOMMENQ', '1');
COMMIT;

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('RET', 'SLFCOMMENQ', '1');
COMMIT;

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('AG', 'SLFCOMMENQ', '1');
COMMIT;

alter table channel_transfers_items add  OTF_APPLICABLE boolean;
alter table c2s_transfers add  OTF_APPLICABLE boolean;



-- CP2P Data Reports Two more for P2P

--MONTHLY WISE FOR RECEIVER
Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values('OPERATOR', 'MONTHWISETXNPERRECV', 'Month wise Txn per Receiver', 'P2P Reports', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTMTPR001', 'P2PREPORT', '/p2pMonthwiseTxnperRecvr.do?method=loadp2pMonthWiseRecvTxn', 'Month wise Txn per Receiver', 'Y', 378, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTMTPR01A', 'P2PREPORT', '/p2pMonthwiseTxnperRecvr.do?method=loadp2pMonthWiseRecvTxn', 'Month wise Txn per Receiver', 'N', 378, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTMTPRDMM', 'P2PREPORT', '/p2pMonthwiseTxnperRecvr.do?method=loadp2pMonthWiseRecvTxn', 'Month wise Txn per Receiver', 'Y', 378, '1', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('MONTHWISETXNPERRECV', 'RPTMTPR001', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('MONTHWISETXNPERRECV', 'RPTMTPR01A', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('MONTHWISETXNPERRECV', 'RPTMTPRDMM', '1');
Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values('BCU', 'MONTHWISETXNPERRECV', '1');

--HISTORY OF SUBSCRIBER 

Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values('OPERATOR', 'P2PSBSCBRHSTRYRPT', 'Subscriber History Report', 'P2P Reports', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTSUBH001', 'P2PREPORT', '/p2pSubscriberHistoryReport.do?method=loadp2pSubscriberHistory', 'Subscriber History Report', 'Y', 379, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTSUBH01A', 'P2PREPORT', '/p2pSubscriberHistoryReport.do?method=loadp2pSubscriberHistory', 'Subscriber History Report', 'N', 379, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values('RPTSUBHDMM', 'P2PREPORT', '/p2pSubscriberHistoryReport.do?method=loadp2pSubscriberHistory', 'Subscriber History Report', 'Y', 378, '1', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('P2PSBSCBRHSTRYRPT', 'RPTSUBH001', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('P2PSBSCBRHSTRYRPT', 'RPTSUBH01A', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values('P2PSBSCBRHSTRYRPT', 'RPTSUBHDMM', '1');
Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values('BCU', 'P2PSBSCBRHSTRYRPT', '1');

-- END Cp2p Data Reports

COMMIT;

--23-aug-2017 for p2pmis
ALTER TABLE daily_transaction_summary  DROP CONSTRAINT pk_dly_trans_summ;
ALTER TABLE daily_transaction_summary ADD CONSTRAINT pk_dly_trans_summ  UNIQUE (TRANS_DATE,SENDER_SERVICE_CLASS,RECEIVER_SERVICE_CLASS,SERVICE,SUB_SERVICE,SENDER_NETWORK_CODE,RECEIVER_NETWORK_CODE,TRANSFER_CATEGORY);


alter table voms_VOUCHERS_TEMP rename TO VOMS_VOUCHERS_SNIFFER;
Alter table voms_vouchers_sniffer add sequence_id numeric(3) default 0;

ALTER TABLE VOMS_VOUCHERS_sniffer add OTHER_INFO varchar(200);

--DEF2830 Fixing
INSERT INTO lookups
(lookup_code, lookup_name, lookup_type, status, created_on, created_by, modified_on, modified_by)
VALUES('VOMS', 'Voucher Management', 'MOTYP', 'Y', '2017-08-28 00:00:00.000', 'ADMIN', '2017-08-28 00:00:00.000', 'ADMIN');
COMMIT;

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SSADM', 'OPTUSRAPRROLES', '1');


alter table channel_transfers_items drop column OTF_APPLICABLE;

alter table channel_transfers_items add OTF_APPLICABLE varchar(2);

alter table channel_transfers_items alter column  OTF_APPLICABLE SET DEFAULT 'N';


alter table c2s_transfers drop column OTF_APPLICABLE;

alter table c2s_transfers add OTF_APPLICABLE varchar(2);

alter table c2s_transfers alter column  OTF_APPLICABLE SET DEFAULT 'N';
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('TRWD', 'Sale', 'TRFTY', 'Y', SYSDATE, 
    'ADMIN', SYSDATE, 'ADMIN');
COMMIT;