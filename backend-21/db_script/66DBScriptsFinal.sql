SET DEFINE OFF;
--Update URL for MASTER menu link
update pages set page_url='/baruser/barreduser.form' where page_code='BAR01' and module_code='MASTER' and menu_item='Y' and menu_level='2';
update pages set page_url='/baruser/barreduser.form?method=loadBarredUser' where page_code='BAR1Dmm' and module_code='MASTER' and menu_item='Y' and menu_level='1';



--Added columns in WEB_SERVICES_TYPES table for webservice url and isrba required
ALTER TABLE WEB_SERVICES_TYPES ADD (WEB_SERVICE_URL  VARCHAR2(250 BYTE));
ALTER TABLE WEB_SERVICES_TYPES ADD (IS_RBA_REQUIRE  VARCHAR2(5 BYTE));
ALTER TABLE WEB_SERVICES_TYPES ADD IS_DATA_VALIDATION_REQUIRE varchar2(5);

--for incresing Length of WEB_SERVICE_TYPE columns

ALTER TABLE WEB_SERVICES_TYPES MODIFY(WEB_SERVICE_TYPE VARCHAR2(25 BYTE));

--Added a system prefernce for choice recharge
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CHOICE_RECHARGE_APPLICABLE', 'CHOICE RECHARGE APPLICABLE', 'SYSTEMPRF', 'BOOLEAN', 'True', 
    NULL, NULL, 50, 'CHOICE RECHARGE APPLICABLE ', 'Y', 
    'Y', 'C2S', 'CHOICE RECHARGE', TO_DATE('02/29/2016 15:25:20', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/20/2016 11:36:34', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'true,false', 'Y');
COMMIT;

--Note: This script is only for one network, need to be added for rest of the networks as well
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('BATCARDGROUP', TO_DATE('05/20/2016 10:21:42', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('02/22/2007 17:58:49', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('04/03/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'C2S batch Cardgroup modification', 'NG', '0');
COMMIT;


--Update pages for Commission profile status left link
Update pages set page_url='/commission-profile/status.form' where page_code='COMMPS001' and module_code='PROFILES';

--Insert to WEB_SERVICES_TYPES for Commission profile status web services
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('COMMPS', 'Commission Profile Status', 'CommissionProfileService', NULL, 'com.btsl.pretups.channel.profile.web.CommissionProfileModel', 
    'configfiles/restservice', 'N', 'commission-profile/load-commission-status', 'N');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('COMMPSL', 'Commission Profile Status List', 'CommissionProfileService', 'configfiles/profile/validation-commission-status-list.xml', 'com.btsl.pretups.channel.profile.web.CommissionProfileModel', 
    'configfiles/restservice', 'Y', 'commission-profile/load-commission-set-list', 'N');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('COMMPLSS', 'Commission Profile Status List SUSPEND', 'CommissionProfileService', NULL, 'com.btsl.pretups.channel.profile.web.CommissionProfileModel', 
    'configfiles/restservice', 'Y', 'commission-profile/save-suspend', 'N');
	
--Added script for Bar User module_code
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('UNBARUSER', 'Un Bar User', 'BarredUserRestService', 'configfiles/subscriber/validator-baruser.xml', 'com.btsl.pretups.subscriber.businesslogic.BarredUserVO', 
    'configfiles/restservice', 'barred-user/unbar-user', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('BARUSER', 'Bar User', 'BarredUserRestService', 'configfiles/subscriber/validator-baruser.xml', 'com.btsl.pretups.subscriber.businesslogic.BarredUserVO', 
    'configfiles/restservice', 'barred-user/add-barred-user', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('VIEWBARREDLIST', 'View Barred User', 'BarredUserRestService', 'configfiles/subscriber/validator-baruser.xml', 'com.btsl.pretups.subscriber.businesslogic.BarredUserVO', 
    'configfiles/restservice', 'barred-user/fetch-barred-user-list', 'Y', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('LOOKUP', 'Lookup Cache', 'LookupsRestService', NULL, NULL, 
    'configfiles/restservice', 'lookups/lookups-dropdown', 'N', 'Y');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('SUBLOOKUP', 'Sub Lookup Cache', 'LookupsRestService', NULL, NULL, 
    'configfiles/restservice', 'lookups/sub-lookups-dropdown', 'N', 'Y');
	
--Added to update url for unbar user module
update pages set page_url='/baruser/unbaruser.form' where page_code='UNBAR01' and module_code='MASTER' and menu_item='Y' and menu_level='2';
update pages set page_url='/baruser/unbaruser.form' where page_code='UNBAR1Dmm' and module_code='MASTER' and menu_item='Y' and menu_level='1';
commit;

update pages set page_url='/baruser/viewBarredUserAction.form' where page_code='VIEWBAR01' and module_code='MASTER' and menu_item='Y' and menu_level='2';
update pages set page_url='/baruser/viewBarredUserAction.form' where page_code='VIEWBARDmm' and module_code='MASTER' and menu_item='Y' and menu_level='1';
commit;

--Added for COnfirm unbar user
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('CONUNBARUSER', 'Confirm Unbar User', 'BarredUserRestService', 'configfiles/subscriber/validator-baruser.xml', 'com.btsl.pretups.subscriber.businesslogic.BarredUserVO', 
    'configfiles/restservice', 'barred-user/unbarred-barred-user-list', 'Y', 'Y');


--direct_payout_dbscript
--Direct payout For supporting 10 Lakh users:

alter table network_stock_transactions modify txn_no varchar(25);
alter table network_stock_trans_items modify txn_no varchar(25);
alter table network_stocks_history modify last_txn_no varchar(25);

--adding indexes:

CREATE INDEX COMM_PROFILE_SET_ID ON COMMISSION_PROFILE_PRODUCTS
(COMM_PROFILE_SET_ID)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX COMM_PROFILE_SET_VERSION ON COMMISSION_PROFILE_PRODUCTS
(COMM_PROFILE_SET_VERSION)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_APPFROM ON COMMISSION_PROFILE_SET_VERSION
(APPLICABLE_FROM)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_CAT ON CHNL_TRANSFER_RULES
(FROM_CATEGORY, TO_CATEGORY)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_CATCODE ON CHANNEL_GRADES
(CATEGORY_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_COMM_PRO_SET_ID ON CHANNEL_USERS
(COMM_PROFILE_SET_ID)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_DAILYUPDATED ON USER_BALANCES
(DAILY_BALANCE_UPDATED_ON)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_DOMAIN_CODE ON CATEGORIES
(DOMAIN_CODE, STATUS)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_DOMAINCODE ON CHNL_TRANSFER_RULES
(DOMAIN_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_NTWCODE ON CHNL_TRANSFER_RULES
(NETWORK_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_NWCODE1 ON USERS
(NETWORK_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_NWCODE ON GEOGRAPHICAL_DOMAINS
(NETWORK_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_NW ON NETWORK_STOCKS
(NETWORK_CODE, NETWORK_CODE_FOR, WALLET_TYPE, DAILY_STOCK_UPDATED_ON)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_STA ON GEOGRAPHICAL_DOMAINS
(STATUS)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_STATUS ON USERS
(STATUS)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_STATUS1 ON NETWORK_PRODUCT_MAPPING
(STATUS)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_TRANSFER_PROFILE_ID ON CHANNEL_USERS
(TRANSFER_PROFILE_ID)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX IND_TYPE ON CHNL_TRANSFER_RULES
(STATUS, TYPE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX IND_USERGRADE ON CHANNEL_USERS
(USER_GRADE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX INDEX_CATEGORY_CODE ON TRANSFER_PROFILE
(CATEGORY_CODE)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX INDEX_PRNT_PROFILE_ID ON TRANSFER_PROFILE
(PARENT_PROFILE_ID)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX INDEX_NETWORK_CODE ON TRANSFER_PROFILE
(NETWORK_CODE)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;
CREATE INDEX INDEX_STATUS ON TRANSFER_PROFILE
(STATUS)
NOLOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;

CREATE INDEX INDX_COMMISSION_PROFILE_DETAILS ON COMMISSION_PROFILE_DETAILS
(END_RANGE)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          16M
            NEXT             16M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


--Direct payout For wirting failed entry in database:

ALTER TABLE foc_batch_items ADD ERROR varchar(1000);

--For negative balance handling
CREATE TABLE RECON_MISTMP
(
  TRANSFER_VALUE         NUMBER(20)             NOT NULL,
  TRANSFER_STATUS        NUMBER(20),
  RECONCILIATION_DATE    DATE,
  RECONCILIATION_FLAG    VARCHAR2(2 BYTE),
  SENDER_ID              VARCHAR2(200 BYTE),
  NETWORK_CODE           VARCHAR2(5 BYTE),
  RECEIVER_NETWORK_CODE  VARCHAR2(5 BYTE),
  PRODUCT_CODE           VARCHAR2(10 BYTE)
)

CREATE INDEX RECON_MISTEMP_SENDERID ON RECON_MISTMP
(SENDER_ID)
LOGGING

CREATE INDEX RECON_DATE ON C2S_TRANSFERS
(RECONCILIATION_DATE)

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TIME_FOR_REVERSAL', 'Time for Reversal in hours', 'SYSTEMPRF', 'STRING', '48', 
    0, 1000000, 50, 'Time for Reversal in hours', 'N', 
    'Y', 'C2S', 'Time for Reversal in hours', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TIME_FOR_REVERSAL_CCE', 'Time for Reversal for CCE', 'SYSTEMPRF', 'STRING', '48', 
    0, 1000000, 50, 'Time for Reversal for CCE', 'N', 
    'Y', 'C2S', 'Time for Reversal for CCE', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALLOWED_DAYS_FOR_REVERSAL', 'Days for batch reversal', 'SYSTEMPRF', 'STRING', '2', 
    0, 1000000, 50, 'Days for batch reversal', 'N', 
    'Y', 'C2S', 'Days for batch reversal', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALLOWED_SERVICES_FOR_REVERSAL', 'Allowed services for reversal', 'SYSTEMPRF', 'STRING', 'RC,GRC', 
    NULL, NULL, 50, 'Allowed services for reversal', 'N', 
    'Y', 'C2S', 'Allowed services for reversal', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALLOW_BULK_C2S_REVERSAL_MESSAGE', 'Allow bulk reversal message', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 1, 'Allow bulk reversal message', 'N', 
    'Y', 'C2S', 'Allow bulk reversal message', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ALLOWED_GATEWAY_FOR_BULK_REVERSAL', 'Gateway for Bulk Reversal', 'SYSTEMPRF', 'STRING', 'WEB', 
    NULL, NULL, 50, 'Gateway for Bulk Reversal', 'N', 
    'Y', 'C2S', 'Gateway for Bulk Reversal', TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/15/2016 10:00:56', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
COMMIT;


--Added new column for Rest INSTANCES
ALTER TABLE NETWORK_LOAD
 ADD (RST_INSTANCE_ID  VARCHAR2(5 BYTE) DEFAULT 1 NOT NULL);
 

update WEB_SERVICES_TYPES set WEB_SERVICE_URL = '/rest/' || WEB_SERVICE_URL where instr(WEB_SERVICE_URL ,'/rest/')=0
commit;
 
 
--Update pages for C2S Reversal left link
Update pages set page_url='/c2srecharge/reversal.form' where page_code='C2SREV001';
Update pages set page_url='/c2srecharge/reversal.form' where page_code='C2SREVDMM';
Update pages set page_url='/c2srecharge/reversal.form' where page_code='C2SREV002';



--Insert to WEB_SERVICES_TYPES for C2S Reversal web services
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('C2SREV', 'C2S Reversal', 'C2SReversalService', NULL, 'com.btsl.pretups.channel.transfer.web.C2SReversalModel', 
    'configfiles/restservice', 'N', '/rest/cs/reversal', 'N');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('C2SDOREV', 'C2S Do Reverse', 'C2SReversalRestService', NULL, 'com.btsl.pretups.channel.transfer.web.C2SReversalModel', 
    'configfiles/restservice', 'Y', '/rest/cs/do-reverse', 'N');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('C2SREVCONF', 'C2S Reversal Confirm', 'C2SReversalRestService', 'configfiles/channeluser/validation-c2s-reversal.xml', 'com.btsl.pretups.channel.transfer.web.C2SReversalModel', 
    'configfiles/restservice', 'Y', '/rest/cs/reversal-confirm', 'N');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, IS_DATA_VALIDATION_REQUIRE, WEB_SERVICE_URL, IS_RBA_REQUIRE)
 Values
   ('C2SREVSTAT', 'C2S Reversal Status', 'C2SReversalRestService', NULL, 'com.btsl.pretups.channel.transfer.web.C2SReversalModel', 
    'configfiles/restservice', 'Y', '/rest/cs/reverse-status', 'N');

--Added to update UNBAR USER url

update WEB_SERVICES_TYPES set WEB_SERVICE_URL = '/rest/barred-user/unbarred-user-list' where WEB_SERVICE_TYPE = 'UNBARUSER';


--Added for Card group Enquiry API

Insert into PRODUCT_SERVICE_TYPE_MAPPING
   (PRODUCT_TYPE, SERVICE_TYPE, CREATED_BY, CREATED_ON, MODIFIED_BY, 
    MODIFIED_ON, GIVE_ONLINE_DIFFERENTIAL, DIFFERENTIAL_APPLICABLE, SUB_SERVICE)
 Values
   ('PREPROD', 'CGENQREQ', 'ADMIN', TO_DATE('05/30/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/30/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'Y', 'Y', 'DEF');

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('CGENQREQ', 'C2S', 'PRE', 'TYPE MSISDN1 PIN MSISDN2 SERVICETYPE AMOUNT', 'com.btsl.pretups.requesthandler.CardGroupEnquiryRequestHandler', 
    'c2s.cardgroupenquiry', 'Card Group Enquiry', 'Y', TO_DATE('12/30/2015 18:19:39', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/30/2015 18:19:39', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'CARD GROUP ENQUIRY', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE,SERVICECLASS,MSISDN2,SLABAMT,CGDESC,SUBSERVICE', 'TYPE,MSISDN1,MSISDN2,SERVICETYPE,AMOUNT', 
    'Y');

Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('CGENQREQ', 'USSD', '190', 'CGENQREQ', 'Card Group Enquiry', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('12/30/2015 18:19:50', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/30/2015 18:19:50', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0000229', 
    NULL, 'TYPE,MSISDN1,MSISDN2,SERVICETYPE,AMOUNT');
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('CGENQREQ', 'EXTGW', '190', 'CGENQREQ', 'Card Group Enquiry', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('12/30/2015 18:19:50', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('12/30/2015 18:19:50', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0000299', 
    NULL, 'TYPE,MSISDN1,EXTNWCODE,MSISDN2,SERVICETYPE,AMOUNT');

    Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('DP_ONLINE_LIMIT', 'direct payout online limit', 'SYSTEMPRF', 'INT', '5', 
    NULL, NULL, 10000, 'direct payout online limit', 'N', 
    'Y', 'C2S', 'direct payout limit for online to offline process', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

ALTER TABLE CHANNEL_GRADES MODIFY IS_2FA_ALLOWED DEFAULT 'N';

--Added column For marking Grades allowed for two factor Authentication
ALTER TABLE CHANNEL_GRADES ADD IS_2FA_ALLOWED varchar2(1);
--ADDED TO set existing GRADE IS_2FA_ALLOWED value to be not null
UPDATE CHANNEL_GRADES SET IS_2FA_ALLOWED='N' WHERE IS_2FA_ALLOWED IS NULL;

--Added a system preferences for Two factor Authentication
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('TWO_FA_REQ', 'TWO Factor Authentication Req', 'CATPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'two factor Authentication Required', 'Y', 
    'Y', 'C2S', '2 factor Authentication Required', TO_DATE('07/04/2013 18:58:53', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/23/2016 09:33:01', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'true,false', 'Y');
COMMIT;

ALTER TABLE TRANSFER_RULES MODIFY  CATEGORY_CODE  DEFAULT 'ALL';
ALTER TABLE TRANSFER_RULES MODIFY  GRADE_CODE  DEFAULT 'ALL';
COMMIT;





-- For c2s Bulk Reversal 


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BC2SREV01', 'TXNREVSE', '/batchC2SReversalAction.do?method=loadTempelate', 'Batch C2S Transaction reverse', 'Y', 
    '550', '2', '1');
    
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BC2SREV01A', 'TXNREVSE', '/batchC2SReversalAction.do?method=loadTempelate', 'Batch C2S Transaction reverse', 'N', 
    '550', '2', '1');
    
 Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BC2SREVDMM', 'TXNREVSE', '/batchC2SReversalAction.do?method=loadTempelate', 'Batch C2S Transaction reverse', 'Y', 
    '550', '1', '1');   



Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHC2SREV', 'BC2SREV01', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHC2SREV', 'BC2SREV01A', '1');
   
   
   Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHC2SREV', 'BC2SREVDMM', '1');
   
   
  
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('BRCREV', 'WEB', '7070', 'BRCREV', 'Recharge REV', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('04/16/2015 11:00:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/24/2015 10:48:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0001234', 
    NULL, NULL);


update service_type set EXTERNAL_INTERFACE='N' where service_type='BRCREV';
 
   
   
   
   
  
   
   
   
   
   
   
   
   
   
   Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BC2SREV02', 'TXNREVSE', '/jsp/userreturn/batchC2SRevUploadFile.jsp', 'Batch C2S Transaction reverse', 'N', 
    '560', '2', '1');
   
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHC2SREV', 'BC2SREV02', '1');
   
   
   
   Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BC2SREV03', 'TXNREVSE', '/jsp/userreturn/showResultC2SReversal.jsp', 'Batch C2S Transaction reverse', 'N', 
    '570', '2', '1');
   
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHC2SREV', 'BC2SREV03', '1');




Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'BATCHC2SREV', 'Batch C2S Reversal', 'Transaction reverse', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;



Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'BATCHC2SREV', '1');
COMMIT;




Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('BRCREV', 'C2S', 'PRE', 'TYPE MSISDN2 AMOUNT SELECTOR LANGUAGE1 PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2SBulkReversalController', 
    'C2S bulk rev', 'C2S bulk rev', 'Y', TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Customer Recharge rev', 'Y', 'N', 
    'Y', NULL, 'Y', 'NA', 'N', 
    'com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser', NULL, NULL, 'TYPE,TXNSTATUS,TXNID', 'TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,LANGUAGE1,LANGUAGE2,SELECTOR,SERVICECLASS', 
    'Y');
    
    
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('BRCREV', 'SMSC', '190', 'BRCREV', 'Recharge REV', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('04/16/2015 11:00:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/24/2015 10:48:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0000908', 
    NULL, NULL);
	
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('BC2STREVERSAL', TO_DATE('06/10/2015 18:23:53', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('02/22/2007 17:58:49', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('04/03/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    60, 1440, 'Batch C2S Reversal', 'MU', '0');
	
	COMMIT;
	
	
	
	Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('BRCREV', 'WEB', '7070', 'BRCREV', 'Recharge REV', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('04/16/2015 11:00:07', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('07/24/2015 10:48:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0001234', 
    NULL, NULL);


update service_type set EXTERNAL_INTERFACE='N' where service_type='BRCREV';


update system_preferences set default_value='RC,GRC,PPB' where preference_code='DECIML_ALOW_SERVICES';




update SERVICE_KEYWORDS SET REQUEST_PARAM='TYPE,MSISDN1,PIN,MSISDN2,AMOUNT,LANGUAGE1,LANGUAGE2,SELECTOR' where keyword='RCTRFSERREQ' and req_interface_type='EXTGW';


COMMIT;
		
		
		
update SERVICE_TYPE_SELECTOR_MAPPING set SELECTOR_CODE='3' where service_type='RCREV' and selector_name='VG';

update SERVICE_TYPE_SELECTOR_MAPPING set SELECTOR_CODE='1' where service_type='RCREV' and selector_name='CVG';

COMMIT;

Alter table network_stocks  MODIFY LAST_TXN_NO varchar2(25);	
	
Alter table network_daily_stocks  MODIFY LAST_TXN_NO varchar2(25); 
Alter table network_stocks  MODIFY LAST_TXN_NO varchar2(25);
	
	
Insert into IDS 
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION) 
 Values 
   ('2016', 'OX', 'NG', 1, TO_DATE('04/24/2014 18:00:16', 'MM/DD/YYYY HH24:MI:SS'), 
    'MINUTES', NULL);
	
COMMIT;	

-- added for GP  defects (DEF 415,472 on 02-Aug-2016)
UPDATE LOOKUPS SET STATUS ='N' WHERE LOOKUP_TYPE='WLTYP' AND LOOKUP_CODE='LMS';
COMMIT;

DELETE NETWORK_STOCKS WHERE WALLET_TYPE='LMS';
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('COMMISSION', 'Commission', 'STTYP', 'Y', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('DEDUCT', 'Deduct', 'STTYP', 'Y', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;


--Added For Fixing DEF457 for GP6.6.1 
--Date:04-08-2016
ALTER TABLE NETWORK_STOCK_TRANSACTIONS MODIFY REF_TXN_ID DEFAULT NULL;

-- Inserting values in KEY_VALUES table (From 6.5 to 6.6 on 05-aug-2016)
insert into key_values (key,value,type) values ('23070','Your request cannot be processed at this time, please try again later.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('127','Txn Already Reconciled','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041051','Reversal for your Prepaid TopUp is already done.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('6027','You have reached the daily maximum number of  allowed subscriber count.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('252','Your request cannot be processed at this time, please try again later.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041052','Entered transaction id for prepaid reversal is invalid or minimum time for reversal has been elapsed.','C2S_ERR_CD');

insert into key_values (key,value,type) values ('1044572','You have entered invalid receiver msisdn.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1017117','Subscriber is not active.' ,'C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041047','Your Request cannot be processed at this time.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('3006201','The mobile number cannot use the same recharge service within defined minutes of last successful transaction as last transfer amount is same as current requested amount.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041048','Your Request can not be processed due to insufficient balance in Wrong Customer mobile no.','C2S_ERR_CD');

insert into key_values (key,value,type) values ('251','Your request cannot be processed at this time, please try again later.' ,'C2S_ERR_CD');
insert into key_values (key,value,type) values ('121','Reference Txn Id Not Exists.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('2076','The card group or default card group does not exist in the system.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041049','Reversal failed.Kindly retry.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041049','Reversal failed.Kindly retry.','C2S_STATUS');
insert into key_values (key,value,type) values ('1044571','You are not authorized to do prepaid Reversal.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041040','Sorry, you are not allowed for Prepaid Reversal Request.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041141','Dear Customer, The amount is debited which was wrongly credited in your account.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041142','Transaction for Prepaid Reversal request is accepted for processing, your balance is updated','C2S_ERR_CD');

insert into key_values (key,value,type) values ('1041143','Your Prepaid Reversal request is under process.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041144','Prepaid Reversal request will be processed in a short time.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041045','Prepaid Reversal can not be processed.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041046','Please confirm your Prepaid Reversal request status  from customer care.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1041050','Reversal failed.Kindly try again.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1044574','Your Prepaid reversal request can not be processed. Please contact customer care.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1044568','Your Prepaid Reversal Request has been Timed Out.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('7705','Transaction ID in this date for this customer of specified amount is made successful, reversal is done , your account is credited back and your balance is updated','C2S_ERR_CD');
insert into key_values (key,value,type) values ('7706','Specified Transaction ID in this date for specified customer for given amount is made failed, reversal unsucessful.','C2S_ERR_CD');



insert into key_values (key,value,type) values ('1007_R','Service keyword was not found.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('206_R','Transaction failed.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('1031090','The operator does not allow to reverse this transaction.','C2S_ERR_CD');
insert into key_values (key,value,type) values ('7065','Before doing any transaction, please change your new reset PIN first.','C2S_ERR_CD');
COMMIT;

update system_preferences
set default_value='XML' where preference_code='SMS_PIN_BYPASS_GATEWAY';
commit;

--added for geography api (DEF 51 in GP on 08-aug-2016)
Update SERVICE_TYPE SET STATUS='Y' where SERVICE_TYPE='EXTGRPH';
commit;





--added some key values for Error Codes






Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('14001_S', 'No voucher exists for the requested amount', 'C2S_ERR_CD', NULL);
COMMIT;


Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('205', 'UNDER PROCESS', 'C2S_ERR_CD', NULL);
COMMIT;

--added for view transaction api (DEF 55 in GP on 09-aug-2016)
Update SERVICE_TYPE SET STATUS='Y' where SERVICE_TYPE='C2CTRFENQ';
commit;

--added for batch O2C withdraw push message
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('O2C_BATCH_WITHDRAW_MESSAGE_ALLOWED', 'O2C Withdraw Message allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'To allow message pushing at the time od batch O2C withdraw or not', 'N', 
    'Y', 'C2S', 'message O2C withdraw', TO_DATE('12/16/2015 15:40:40', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/16/2015 15:40:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
COMMIT;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ADMIN_MESSAGE_REQD', 'O2C Withdraw Message for admin', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'To allow message pushing at the time od batch O2C withdraw or not', 'N', 
    'Y', 'C2S', 'message O2C withdraw', TO_DATE('12/16/2015 15:40:40', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/16/2015 15:40:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'N');
COMMIT;

--C2S Reversal load transactions request type name changed 
update WEB_SERVICES_TYPES set WEB_SERVICE_TYPE='C2SLOADTXN',WEB_SERVICE_URL='/rest/cs/reversal-load-txn' where WEB_SERVICE_TYPE='C2SREVCONF';

--FOR DEF 51 CLARO
update service_type set external_interface='N' where service_type='EXTGRPH';
--FOR DEF 53 CLARO
update service_type set external_interface='N' where service_type='VIEWCUSER';


--DEF 76 fixed for Claro issue 2
update lookups set status='N' where lookup_code='PRX' and lookup_type='PROMO';




--DEF 1072 Fixed


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('LMSPROGRPT', 'LMSPRGAU01', '1');
COMMIT;


--DEF 972 Fixed for PreTUPS650 ITR1

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('COMMISSION', 'Commission', 'STTYP', 'Y', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('DEDUCT', 'Deduct', 'STTYP', 'Y', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/22/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

update SERVICE_TYPE set MESSAGE_FORMAT='KEYWORD NAME PIN' where SERVICE_TYPE ='PDEL';
update SERVICE_TYPE set MESSAGE_FORMAT='Keyword Name MSISDN Preferred Amount PIN' where SERVICE_TYPE ='PADD';
commit;




--function DEF 948 FIXED

CREATE OR REPLACE FUNCTION "GETUSERROLES" 
--This function is used for generate report for assigned roles
--(p_userId  VARCHAR2) RETURN CLOB
--p_roleName(if Y then role_name else role_code)
(p_userId  VARCHAR2,p_roleName VARCHAR2) RETURN VARCHAR
IS
--p_userRoles CLOB;
p_userRoles VARCHAR(32767) DEFAULT '';
role_name_code VARCHAR2(100) DEFAULT '';
role_type VARCHAR2(1);
group_role VARCHAR2(1) DEFAULT '';
oldGroupName VARCHAR2(100)DEFAULT '';
newGroupName VARCHAR2(100)DEFAULT '';
var NUMBER(5) DEFAULT 0;
CURSOR c_userRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE r.role_code=ur.role_code
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
CURSOR c_userGroupRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name ,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,GROUP_ROLES gr,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE ur.ROLE_CODE=gr.GROUP_ROLE_CODE
            AND gr.ROLE_CODE=r.ROLE_CODE
            AND ur.user_id=p_userId
            AND r.status='Y'
            --AND cr.CATEGORY_CODE=u.CATEGORY_CODE
            --AND cr.ROLE_CODE=r.ROLE_CODE
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;

CURSOR c_userFixRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,TRIM(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USERS ur,CATEGORIES C,CATEGORY_ROLES CR ,ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,DOMAINS D
            WHERE ur.user_id=p_userId
            AND ur.CATEGORY_CODE=C.CATEGORY_CODE
            AND C.FIXED_ROLES='Y'
            AND C.CATEGORY_CODE=CR.CATEGORY_CODE
            AND CR.ROLE_CODE=R.ROLE_CODE
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MODULE_CODE=m.MODULE_CODE
            AND p.MENU_LEVEL=1
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
BEGIN
       oldGroupName:='###';
       FOR tr IN c_userRoles(p_userId)
       LOOP
               role_type:=tr.GROUP_ROLE;
             IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr.role_name;
            ELSE
               role_name_code:=tr.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
                  newGroupName:=UPPER(trim(tr.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
               IF newGroupName <> oldGroupName THEN
                     oldGroupName:=newGroupName;
                  IF p_roleName LIKE 'Y' THEN
                          p_userRoles:=p_userRoles||'<b>'||'('||tr.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                  ELSE
                       p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
               ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
               END IF;
            END IF;

        END LOOP;
        FOR tr1 IN c_userGroupRoles(p_userId)
        LOOP
            role_type:=tr1.GROUP_ROLE;
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr1.role_name;
            ELSE
               role_name_code:=tr1.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
               --p_userRoles:=p_userRoles||to_clob(tr.role_name)||',';
                newGroupName:=UPPER(trim(tr1.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
                IF newGroupName <> oldGroupName THEN
                   oldGroupName:=newGroupName;
                   IF p_roleName LIKE 'Y' THEN
                         p_userRoles:=p_userRoles||'<b>'||'('||tr1.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                   ELSE
                         p_userRoles:=p_userRoles||role_name_code||', ';
                   END IF;
                ELSE
                    p_userRoles:=p_userRoles||role_name_code||', ';
                END IF;
              END IF;
        END LOOP;
        FOR tr2 IN c_userFixRoles(p_userId)
        LOOP
            role_type:='F';
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr2.role_name;
            ELSE
               role_name_code:=tr2.ROLE_CODE;
            END IF;
            newGroupName:=UPPER(trim(tr2.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
            IF newGroupName <> oldGroupName THEN
               oldGroupName:=newGroupName;
               IF p_roleName LIKE 'Y' THEN
                     p_userRoles:=p_userRoles||'<b>'||'('||tr2.MODULE_NAME||')'||'</b>'||role_name_code||', ';
               ELSE
                          p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
            ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
            END IF;

          END LOOP;

      IF LENGTH(p_userRoles) > 0 THEN
         p_userRoles:=SUBSTR(p_userRoles,0,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;


--DEF 1073 fix
update service_keywords set REQUEST_PARAM='TYPE,EXTNWCODE,MSISDN,PIN,DATE,LANGUAGE1' WHERE SERVICE_TYPE='LMSPTENQ' AND REQ_INTERFACE_TYPE='USSD';
update service_keywords set REQUEST_PARAM='TYPE,EXTNWCODE,LOGINID,PASSWORD,DATE,LANGUAGE1' WHERE SERVICE_TYPE='LMSPTENQ' AND REQ_INTERFACE_TYPE='EXTGW';
update service_keywords set REQUEST_PARAM='TYPE,EXTNWCODE,MSISDN,PIN,POINTS,DATE,LANGUAGE1' WHERE SERVICE_TYPE='LMSPTRED' AND REQ_INTERFACE_TYPE='USSD';
update service_keywords set REQUEST_PARAM='TYPE,EXTNWCODE,MSISDN,PIN,POINTS,DATE,LANGUAGE1' WHERE SERVICE_TYPE='LMSPTRED' AND REQ_INTERFACE_TYPE='EXTGW';

--by default set CONTROL_GROUP = 'N' for existing users who has CONTROL_GROUP = null
update CHANNEL_USERS set CONTROL_GROUP = 'N';

update lookups set status='N' where lookup_code='CEL'and lookup_type='PROMO';
commit;

update roles set status='N' where role_code='BONUSENQY';
commit;

Insert into NETWORK_SERVICES
   (MODULE_CODE, SERVICE_TYPE, SENDER_NETWORK, RECEIVER_NETWORK, STATUS, 
    LANGUAGE1_MESSAGE, LANGUAGE2_MESSAGE, CREATED_BY, CREATED_ON, MODIFIED_BY, 
    MODIFIED_ON)
 Values
   ('OPT', 'CPREG', 'PB', 'PB', 'Y', 
    'Service is suspended for some time, Please try after some time or contact customer care.', 'Service is suspended for some time, Please try after some time or contact customer care.', 'ADMIN', TO_DATE('10/16/2012 14:42:49', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000014828', 
    TO_DATE('10/16/2012 14:42:49', 'MM/DD/YYYY HH24:MI:SS'));

Insert into CATEGORY_SERVICE_TYPE
   (CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE)
 Values
   ('BCU', 'CPREG', 'PB');
COMMIT;


--scripts added for GP defects for PreTUPS 662


UPDATE GEOGRAPHICAL_DOMAINS SET GRPH_DOMAIN_NAME='Vodafone Punjab' where GRPH_DOMAIN_CODE='PB' and NETWORK_CODE='PB'; 

update service_type set description = 'Customer Recharge' where service_type='RC';
update service_type set error_key = 'Customer Recharge' where service_type='RC';

update service_type_selector_mapping set is_default_code='Y' where service_type='RC';
commit;


--scripts added for GP defects for PreTUPS 662

Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('C2SLASTTRF', 'USSD', '190', 'C2SLASTTRF', 'LAST X TRANSFER MAPPGW', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('29/08/2016 09:29:57', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('29/08/2010 09:29:57', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK1003031', 
    NULL, NULL);
COMMIT;


CREATE TABLE  USER_OTP
(
  USER_ID       VARCHAR2(15 BYTE)  NOT NULL,
  MSISDN        VARCHAR2(15 BYTE)  NOT NULL,
  SERVICE_TYPES VARCHAR2(10) DEFAULT 'OTP',
  OTP_PIN       VARCHAR2(32 BYTE)  NOT NULL,
  STATUS        VARCHAR2(2 BYTE),
  GENERATED_ON  DATE   NOT NULL,
  CREATED_BY    VARCHAR2(15 BYTE),
  CREATED_ON    DATE  NOT NULL,
  MODIFIED_ON   DATE  ,
  MODIFIED_BY   VARCHAR2(15 BYTE),
  CONSUMED_ON   DATE
);

CREATE INDEX INDEX_USERID1 ON USER_OTP
(USER_ID, MSISDN, SERVICE_TYPES);
COMMIT;

--added for 2 services where external_inteface is 'A'
update service_type set external_interface='A' where service_type='LMSPTRED';
update service_type set external_interface='A' where service_type='LMSPTENQ'; 

--merged from 6.5 db scripts
update SYSTEM_PREFERENCES set default_value='EX:Y,CH:Y,PA:Y'  where preference_code='TXN_SENDER_USER_STATUS_CHANG';



--Added for fixing DEF144 O2C Transaction Reversal Date: 06-09-2016
ALTER TABLE CHANNEL_TRANSFERS DROP CONSTRAINT UNQ_EXT_NO

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) Values ('DUPLICATE_CARDGROUP_CODE_ALLOW', 'DUPLICATE_CARDGROUP_CODE_ALLOW', 'SYSTEMPRF', 'BOOLEAN', 'true',10, 10, 50, 'DUPLICATE_CARDGROUP_CODE_ALLOW Applicable', 'N', 'N', 'C2S', 'DUPLICATE_CARDGROUP_CODE_ALLOW Applicable', sysdate, 'ADMIN', sysdate, 'ADMIN', 'true,false', 'Y');

-- def 2098
update service_type set response_param=TYPE,TXNSTATUS,MESSAGE,EXTTXNNUMBER where service_type='C2SENQ';

-- to make IS_RBA_REQUIRE = N
UPDATE WEB_SERVICES_TYPES SET IS_RBA_REQUIRE = 'N'

-- for VOMS performance related
ALTER  TABLE VOMS_CATEGORIES ADD (IS_RUNNING VARCHAR2(2 BYTE) DEFAULT 'N');

CREATE TABLE VOMS_VOUCHERS_TEMP
(
  SERIAL_NO                VARCHAR2(15 BYTE),
  PRODUCT_ID               VARCHAR2(15 BYTE),
  PIN_NO                   VARCHAR2(128 BYTE),
  GENERATION_BATCH_NO      VARCHAR2(20 BYTE),
  ENABLE_BATCH_NO          VARCHAR2(20 BYTE),
  SALE_BATCH_NO            VARCHAR2(20 BYTE),
  ATTEMPT_USED             NUMBER,
  CURRENT_STATUS           VARCHAR2(2 BYTE),
  EXPIRY_DATE              DATE,
  CONSUME_BEFORE           DATE,
  TOTAL_VALUE_USED         NUMBER,
  LAST_CONSUMED_BY         VARCHAR2(20 BYTE),
  LAST_CONSUMED_ON         DATE,
  PRODUCTION_NETWORK_CODE  VARCHAR2(2 BYTE),
  USER_NETWORK_CODE        VARCHAR2(2 BYTE),
  MODIFIED_BY              VARCHAR2(20 BYTE),
  LAST_BATCH_NO            VARCHAR2(20 BYTE),
  MODIFIED_ON              DATE,
  CREATED_ON               DATE,
  CON_SUMMARY_UPDATE       VARCHAR2(1 BYTE)     DEFAULT 'N',
  PREVIOUS_STATUS          VARCHAR2(2 BYTE),
  LAST_CONSUMED_OPTION     VARCHAR2(10 BYTE),
  OPTION_SUMMARY_UPDATED   VARCHAR2(1 BYTE)     DEFAULT 'N',
  LAST_ATTEMPT_NO          NUMBER,
  ATTEMPT_TYPE             VARCHAR2(5 BYTE)     DEFAULT 'N',
  FIRST_CONSUMED_BY        VARCHAR2(20 BYTE),
  FIRST_CONSUMED_ON        DATE,
  LAST_TRANSACTION_ID      VARCHAR2(20 BYTE),
  NO_OF_REQUESTS           NUMBER,
  STATUS                   VARCHAR2(2 BYTE),
  LAST_REQUEST_ATTEMPT_NO  NUMBER,
  ONE_TIME_USAGE           VARCHAR2(5 BYTE)     DEFAULT 'N',
  LAST_ATTEMPT_VALUE       NUMBER(10),
  SEQ_NO                   NUMBER,
  CREATED_DATE             DATE,
  TALKTIME                 NUMBER(10),
  VALIDITY                 NUMBER(5),
  MRP                      NUMBER(10),
  USER_ID                  VARCHAR2(15 BYTE),
  SUBSCRIBER_ID            VARCHAR2(30 BYTE),
  EXT_TRANSACTION_ID       VARCHAR2(20 BYTE)
);
ALTER TABLE VOMS_VOUCHERS_TEMP ADD CONSTRAINT PK_VOMS_VOUCHERS_TEMP PRIMARY KEY( SERIAL_NO) ;
CREATE INDEX IND_VOMS_VOUCHERS_TEMP ON VOMS_VOUCHERS_TEMP(PRODUCT_ID, CURRENT_STATUS);
CREATE INDEX IND_VOUCHER_TEMP_PIN ON VOMS_VOUCHERS_TEMP(PIN_NO);

DROP INDEX PK_VOMS_VOUCHERS;
ALTER TABLE VOMS_VOUCHERS ADD CONSTRAINT PK_VOMS_VOUCHERS  PRIMARY KEY( SERIAL_NO) ;

CREATE INDEX INDEX_PROFILE_ID ON TRANSFER_PROFILE (PROFILE_ID);
ALTER TABLE WEB_SERVICES_TYPES ADD ROLE_CODE VARCHAR2(25);
--Owner Additional Commission feature development
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_ADDNL_COMM_TYPE           VARCHAR2(5 BYTE); 
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_ADDNL_COMM_RATE           NUMBER(16,4);    
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_TAX1_TYPE                 VARCHAR2(3 BYTE); 
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_TAX1_RATE                 NUMBER(16,4);                  
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_TAX2_TYPE                 VARCHAR2(3 BYTE);
ALTER TABLE ADDNL_COMM_PROFILE_DETAILS ADD OWN_TAX2_RATE                 NUMBER(16,4);
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('OWNER_COMMISION_ALLOWED', 'Flag For OWNER COMMISION', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 5, 'OWNER COMMISION Allowed', 'N', 
    'N', 'C2S', 'OWNER COMMISION ALLOWED STATUS', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
COMMIT;

INSERT INTO service_type
            (service_type, module, TYPE,
             message_format,
             request_handler,
             error_key, description, flexible,
             created_on,
             created_by,
             modified_on,
             modified_by, NAME, external_interface,
             unregistered_access_allowed, status, seq_no,
             use_interface_language, group_type, sub_keyword_applicable,
             file_parser, erp_handler, receiver_user_service_check,
             response_param,
             request_param,
             underprocess_check_reqd
            )
     VALUES ('USRMOVEMNT', 'OPT', 'ALL',
             'TYPE,EMPCODE,FROMUSERMSISDN,TOPARENTMSISDN,TOUSERGEOCODE,TOUSERCATCODE',
             'com.btsl.pretups.channel.transfer.requesthandler.UserTransferController',
             'User Movement', 'User Movement', 'Y',
             TO_DATE ('07/12/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
             'ADMIN',
             TO_DATE ('07/12/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),
             'ADMIN', 'User Movement', 'Y',
             'N', 'Y', NULL,
             'N', 'NA', 'N',
             NULL, NULL, NULL,
             'TYPE,TXNSTATUS,MESSAGE',
             'TYPE,NETWORKCODE,DATE,EMPCODE,EXTCODE,EXTREFNO,FROMUSERMSISDN,FROMUSERUNIQID,FROMUSEREXTCODE,TOPARENTMSISDN,TOPARENTUNIQID,TOPARENTEXTCODE,TOUSERGEOCODE,TOUSERCATCODE',
             'Y'
            );
			
ALTER TABLE SUBSCRIBER_MSISDN_ALIAS MODIFY USER_SID VARCHAR(45);
ALTER TABLE SUBSCRIBER_MSISDN_HISTORY MODIFY USER_SID VARCHAR(45);
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SID_ENCRYPTION_ALLOWED', 'Sid Encryption allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'To store SID in encrypted format,if default value is true', 'N', 
    'Y', 'C2S', 'Sid encryption allowed', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
--Added for LMS Target VS Achievement Process
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('LMSTARGETVSACHIEVE', sysdate, 'C',sysdate-1, sysdate, 
    60, 1440, 'LMS Target VS Acheivement Process', 'BL', 0);
COMMIT ;



Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('TDSRD', TO_DATE('09/20/2016 13:12:34', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 13:12:34', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Daily at 5 am Summary Report', 'BL', 0);
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('TDSRDH', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0);
COMMIT;

Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('SCDRD', TO_DATE('09/20/2016 13:12:34', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 13:12:34', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Daily at 5 am Summary Report', 'BL', 0);
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('SCDRDH', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0);


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('OF_FOCR', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0);
    
   Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('OF_O2CR', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0);
    
       Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('OF_OWASR', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0); 

    
       Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('OF_OWPPSR', TO_DATE('09/20/2016 15:39:38', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/20/2016 15:39:37', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/20/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'Top Up Houlry Summary Report', 'BL', 0);


--DB Script for C2C Withdraw via Channel Admin
 SET DEFINE OFF;
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('C2CWCU001', 'WITHDRAW', '/master/C2CWithdrawFromBCU.form', 'C2C Withdraw', 'Y', 
    518, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('C2CWCU001A', 'WITHDRAW', '/master/C2CWithdrawFromBCU.form', 'C2C Withdraw', 'N', 
    518, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('C2CWCUDMM', 'WITHDRAW', '/master/C2CWithdrawFromBCU.form', 'C2C Withdraw', 'Y', 
    518, '1', '1');
COMMIT;

--DB Script for C2C Withdraw via Channel Admin
SET DEFINE OFF;
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'C2CWCU', 'C2C Withdraw ', 'C2C Withdraw', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;

--DB Script for C2C Withdraw via Channel Admin
SET DEFINE OFF;
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('C2CWCU', 'C2CWCU001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('C2CWCU', 'C2CWCU001A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('C2CWCU', 'C2CWCUDMM', '1');
COMMIT;

SET DEFINE OFF;
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'C2CWCU', '1');
COMMIT;

--DB Script for C2C Withdraw via Channel Admin Web Service types

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADUSRLIST', 'Load User List', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-user-list', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADUSRDETAILS', 'Load User List', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-user-details', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADUSRLISTSNDR', 'Load Channel User List Sender', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-channel-user-list-sender', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('CONFIRMWITHDRAW', 'Load User List', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/confirm-withdraw', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('VALIDATECHNNLUSER', 'Validate Channel User', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/validate-channel-user', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADCHNNLUSRLIST', 'Load Channel User List', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-channel-user-list', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADCATLISTBTTRF', 'Load Cat List by Transfer Rule', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-category-list-by-transfer-rule', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('CONFIRMUSER', 'Confirm C2C withdraw via Channel Admin', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/confirm-transaction', 'Y', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('LOADCATBYDOMAIN', 'Load Category List By Domain', 'C2CWithdrawRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/greetings/load-cat-list-by-domain', 'Y', 'Y', NULL);
COMMIT;






-- ADDED FOR NETWORK SUMMARY REPORT

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR001', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'Y', 
    517, '2', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR01A', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'N', 
    517, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR1DMM', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'Y', 
    517, '1', '1');



Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'NETWORKSUMRPT', 'Network Summary Report', 'Masters', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'NETWORKSUMRPT', '1');
   
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR01A', '1');




Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR1DMM', '1');   









Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('NETSUMMDNLD', 'Download User List', 'NetworkSummaryReportRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/networkSummary/download-monthly', 'N', 'N');

 


CREATE TABLE PROC_ERROR_LOG
(
  DESC1  VARCHAR2(2000 BYTE)
)
TABLESPACE PRTP_DATA
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


CREATE SEQUENCE TRANSSUMM_ID
  START WITH 1
  MAXVALUE 999999999999
  MINVALUE 0
  CYCLE
  NOCACHE
  NOORDER;

  
  CREATE TABLE TRANSACTION_SUMMARY
(
  TRANS_SUMM_ID            NUMBER(12),
  TRANS_DATE               DATE,
  TRANS_TIME               NUMBER(2),
  NETWORK_CODE             VARCHAR2(10 BYTE)    NOT NULL,
  INTERFACE_ID             VARCHAR2(10 BYTE)    DEFAULT 0,
  CATEGORY                 VARCHAR2(10 BYTE)    NOT NULL,
  SERVICE_TYPE             VARCHAR2(10 BYTE)    NOT NULL,
  SUB_SERVICE              VARCHAR2(10 BYTE)    NOT NULL,
  GATEWAYCODE              VARCHAR2(10 BYTE)    NOT NULL,
  SUCCESS_COUNT            NUMBER(10)           DEFAULT 0,
  ERROR_COUNT              NUMBER(10)           DEFAULT 0,
  SUCCESS_AMT              NUMBER(15)           DEFAULT 0,
  ERROR_AMT                NUMBER(12)           DEFAULT 0,
  TAX_AMOUNT               NUMBER(12)           DEFAULT NULL,
  ACCESS_FEE               NUMBER(12)           DEFAULT NULL,
  RECEIVER_TRANSFER_VALUE  NUMBER(12)           DEFAULT NULL
)
TABLESPACE PRTP_DATA
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;



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
     and transfer_status in ('206','205');
    
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


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('RUNTRNSUM', TO_DATE('02/02/2016 14:54:27', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('02/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('02/02/2016 14:54:27', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'RUN_PROCEDURE_TRANS_SUMMARY', 'BD', '0');

	
	
	
	
-- ADDED FOR GREET MSG 

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

	
-- ROLES FOR REST SERVICE

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

	

--GREETMSG ENTRY DELETION

DELETE FROM WEB_SERVICES_TYPES WHERE WEB_SERVICE_TYPE='GREETUSRUPLD';
DELETE FROM ROLES WHERE ROLE_CODE='GREETUSRUPLD';
COMMIT;
	
	
--CHANGE SELF PIN ROLE  DISBALE
update roles set status='N' where role_code = 'CHANGESELFPIN' and domain_type='OPERATOR';
--MODIFY P2P CARD GROUP ENABLE 
update roles set status='Y' where role_code = 'EDITCARDGRP' and domain_type='OPERATOR';
--DEFAULT CARDGROUP ENABLE
update roles set status='Y' where role_code = 'DFLTC2SCARDGRP' and domain_type='OPERATOR';
update roles set status='Y' where role_code = 'DFLTP2PCARDGRP' and domain_type='OPERATOR';
--BATCH MODIFY CARD GROUP ENABLE
update roles set status='Y' where role_code='BATCGMOD' and domain_type='OPERATOR';
update roles set status='Y' where role_code='BATCGMODP2P' and domain_type='OPERATOR';
COMMIT;


------------- Deletion of role from nadm and addition to BCU
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'NETWORKSUMRPT', '1');
   
   delete from CATEGORY_ROLES where CATEGORY_CODE='NWADM' and ROLE_CODE= 'NETWORKSUMRPT';
   
   
 ----------- Approval Suspend/Delete Batch User -----------------------
 
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'APPROVSRBDRB', 'Approve Delete/Suspend User Batch', 'Channel Users', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('APPUDSDMM', 'CUSERS', '/channeluser/approvalBatchUserDeleteSuspend.form', 'Approval Batch D/S  User', 'Y', 
    578, '1', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('APPUDS001', 'CUSERS', '/channeluser/approvalBatchUserDeleteSuspend.form', 'Approval Batch D/S  User', 'Y', 
    578, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('APPUDS01A', 'CUSERS', '/channeluser/approvalBatchUserDeleteSuspend.form', 'Approval Batch D/S  User', 'N', 
    578, '2', '1');
	
	
	
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APPROVSRBDRB', 'APPUDS001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APPROVSRBDRB', 'APPUDS01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APPROVSRBDRB', 'APPUDSDMM', '1');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'APPROVSRBDRB', '1');



-- WEBSERVUICE TYPE FOR REST SERVICE

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('DNLDUSRDLTSPNDLIST', 'Download Delete/Suspend USer List', 'ApprovalUserDeleteSuspendService', NULL, NULL, 
    'configfiles/restservice', '/rest/approve-user-delete-suspend/download-user-list', 'Y', 'Y', 'DNLDUSRDLTSPNDLIST');



Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('APRVLBATCHDLTSPNDUSR', 'Download Delete/Suspend USer List', 'ApprovalUserDeleteSuspendService', NULL, NULL, 
    'configfiles/restservice', '/rest/approve-user-delete-suspend/approval-batch', 'Y', 'Y', 'APRVLBATCHDLTSPNDUSR');

 ---- ROLES
 
 Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'DNLDUSRDLTSPNDLIST', 'Download Approval Sus/Dlt User List', 'Channel Users', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'APRVLBATCHDLTSPNDUSR', 'Approve Delete/Suspend User Batch Rest', 'Channel Users', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
	

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'APRVLBATCHDLTSPNDUSR', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'DNLDUSRDLTSPNDLIST', '1');
 
 
 ---SYstem Preference 
 
 Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USR_BTCH_SUS_DEL_APRVL', 'Batch User Sus/Del Aprroval', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Batch User Sus/Del Aprroval will allow the approval of suspension and deletion of users in batch. Until then users will be in status SRB and DRB', 'N', 
    'Y', 'C2S', 'Batch User Sus/Del Aprroval', TO_DATE('09/21/2016 10:41:49', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/21/2016 10:41:49', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

COMMIT;
-- Greeting Message Download UserList Category Roles 

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'GREETUSRDNLD', '1');
   
 update WEB_SERVICES_TYPES set role_code ='GREETUSRDNLD' where web_service_type='GREETUSRDNLD';
COMMIT;

--Approval for Suspend Delete Lookup Type for status

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SRB', 'Approve Batch Suspend', 'URTYP', 'Y', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('DRB', 'Approve Batch Delete', 'URTYP', 'Y', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	
--Defect 2371 fix for removing o2c from superadmin

delete from page_roles where role_code='BTO2CAPR2' and page_code='BATO2C0002';
delete from category_roles where role_code='BTO2CAPR2' and category_code='SUADM';
COMMIT;



--function DEF 948 FIXED

CREATE OR REPLACE FUNCTION "GETUSERROLES" 
--This function is used for generate report for assigned roles
--(p_userId  VARCHAR2) RETURN CLOB
--p_roleName(if Y then role_name else role_code)
(p_userId  VARCHAR2,p_roleName VARCHAR2) RETURN VARCHAR
IS
--p_userRoles CLOB;
p_userRoles VARCHAR(32767) DEFAULT '';
role_name_code VARCHAR2(100) DEFAULT '';
role_type VARCHAR2(1);
group_role VARCHAR2(1) DEFAULT '';
oldGroupName VARCHAR2(100)DEFAULT '';
newGroupName VARCHAR2(100)DEFAULT '';
var NUMBER(5) DEFAULT 0;
CURSOR c_userRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE r.role_code=ur.role_code
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
CURSOR c_userGroupRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name ,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,GROUP_ROLES gr,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE ur.ROLE_CODE=gr.GROUP_ROLE_CODE
            AND gr.ROLE_CODE=r.ROLE_CODE
            AND ur.user_id=p_userId
            AND r.status='Y'
            --AND cr.CATEGORY_CODE=u.CATEGORY_CODE
            --AND cr.ROLE_CODE=r.ROLE_CODE
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;

CURSOR c_userFixRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,TRIM(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USERS ur,CATEGORIES C,CATEGORY_ROLES CR ,ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,DOMAINS D
            WHERE ur.user_id=p_userId
            AND ur.CATEGORY_CODE=C.CATEGORY_CODE
            AND C.FIXED_ROLES='Y'
            AND C.CATEGORY_CODE=CR.CATEGORY_CODE
            AND CR.ROLE_CODE=R.ROLE_CODE
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MODULE_CODE=m.MODULE_CODE
            AND p.MENU_LEVEL=1
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
BEGIN
       oldGroupName:='###';
       FOR tr IN c_userRoles(p_userId)
       LOOP
               role_type:=tr.GROUP_ROLE;
             IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr.role_name;
            ELSE
               role_name_code:=tr.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
                  newGroupName:=UPPER(trim(tr.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
               IF newGroupName <> oldGroupName THEN
                     oldGroupName:=newGroupName;
                  IF p_roleName LIKE 'Y' THEN
                          p_userRoles:=p_userRoles||'<b>'||'('||tr.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                  ELSE
                       p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
               ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
               END IF;
            END IF;

        END LOOP;
        FOR tr1 IN c_userGroupRoles(p_userId)
        LOOP
            role_type:=tr1.GROUP_ROLE;
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr1.role_name;
            ELSE
               role_name_code:=tr1.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
               --p_userRoles:=p_userRoles||to_clob(tr.role_name)||',';
                newGroupName:=UPPER(trim(tr1.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
                IF newGroupName <> oldGroupName THEN
                   oldGroupName:=newGroupName;
                   IF p_roleName LIKE 'Y' THEN
                         p_userRoles:=p_userRoles||'<b>'||'('||tr1.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                   ELSE
                         p_userRoles:=p_userRoles||role_name_code||', ';
                   END IF;
                ELSE
                    p_userRoles:=p_userRoles||role_name_code||', ';
                END IF;
              END IF;
        END LOOP;
        FOR tr2 IN c_userFixRoles(p_userId)
        LOOP
            role_type:='F';
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr2.role_name;
            ELSE
               role_name_code:=tr2.ROLE_CODE;
            END IF;
            newGroupName:=UPPER(trim(tr2.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
            IF newGroupName <> oldGroupName THEN
               oldGroupName:=newGroupName;
               IF p_roleName LIKE 'Y' THEN
                     p_userRoles:=p_userRoles||'<b>'||'('||tr2.MODULE_NAME||')'||'</b>'||role_name_code||', ';
               ELSE
                          p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
            ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
            END IF;

          END LOOP;

      IF LENGTH(p_userRoles) > 0 THEN
         p_userRoles:=SUBSTR(p_userRoles,0,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;

update lookups set status='N' where lookup_type='VSTAT' and lookup_code='PE';
COMMIT;


-- to assign user deletion service to channel admin for NG network, Kindly execute as per required network code
Insert into CATEGORY_SERVICE_TYPE(CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE) Values('BCU', 'USERDEL', 'NG');

update pages set menu_name='C2S transfers Enquiry' where page_code='C2STENQ01A';
update pages set menu_name='C2S transfers Enquiry' where page_code='C2STENQ001';
update pages set menu_name='C2S transfers Enquiry' where page_code='C2STENQDMM';
update pages set menu_name='C2S transfers Enquiry' where page_code='C2STENQ002';

update pages set menu_name='Batch FOC transfer' where menu_name='FOC batch transfer enquiry';


update pages set menu_name='Bulk commission payout' where menu_name='DP bulk transfer enquiry';



update pages set menu_name='Bulk commission payout' where menu_name='DP Bulk transfer enquiry';

update pages set menu_name='Batch O2C withdraw' where menu_name='Batch O2C Withdraw Enquiry';


update pages set menu_name='Batch O2C transfer Summary' where menu_name='Batch O2C Summary report';

UPDATE ROLES SET ROLE_NAME='Upload & process ICCID/IMSI key' WHERE ROLE_CODE='ICCIDKEYMGMT';


UPDATE ROLES SET group_name='ICCID/IMSI Key Management' WHERE group_name='ICCID Key Management';

UPDATE ROLES SET ROLE_NAME='Upload & process ICCID/IMSI MSISDN' WHERE ROLE_CODE='ICCIDMSISDNMGMT';

UPDATE ROLES SET ROLE_NAME='Associate MSISDN with ICCID/IMSI' WHERE ROLE_CODE='ASSMSISDNICCID';

UPDATE ROLES SET ROLE_NAME='Correct MSISDN ICCID/IMSI mapping' WHERE ROLE_CODE='CORRMSISDNICCIDMAP';

UPDATE ROLES SET ROLE_NAME='ICCID/IMSI MSISDN enquiry' WHERE ROLE_CODE='ICCIDMSISDNENQ';

UPDATE ROLES SET ROLE_NAME='ICCID/IMSI MSISDN history' WHERE ROLE_CODE='ICCIDMSISDNHIS';

UPDATE ROLES SET ROLE_NAME='ICCID/IMSI MSISDN association' WHERE ROLE_CODE='ICCIDMSISDNRPT';

UPDATE ROLES SET ROLE_NAME='ICCID/IMSI delete' WHERE ROLE_CODE='ICCIDDELETE';

update USER_ALLOWED_STATUS set USER_SENDER_SUSPENDED='S', USER_RECEIVER_SUSPENDED='S';


ALTER TABLE USER_DAILY_BALANCES MODIFY(LAST_TRANSFER_NO VARCHAR2(25 BYTE));

COMMIT;

SET DEFINE OFF;
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOM001', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'Y', 
    378, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOM002', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'N', 
    378, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOM003', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'N', 
    378, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOM004', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'N', 
    378, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOM01A', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'N', 
    378, '1', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('BGEDOMDMM', 'MASTER', '/master/batchGeographicalDomain.form', 'Batch Geographical Domain', 'N', 
    378, '2', '1');
COMMIT;

SET DEFINE OFF;
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOM001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOM002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOM003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOM004', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOM01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('BATCHGRPHDMN', 'BGEDOMDMM', '1');
COMMIT;

SET DEFINE OFF;
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'BATCHGRPHDMN', '1');
COMMIT;

SET DEFINE OFF;
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'BATCHGRPHDMN', 'Batch Geographical Domain', 'Masters', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');
COMMIT;

SET DEFINE OFF;
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('2016', 'BGRPHDMN', 'BL', 43, TO_DATE('11/07/2016 13:59:09', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', 'Batch ID for Batch Geography Domain Creation');
COMMIT;

SET DEFINE OFF;
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('DNLDGRPHDMNLIST', 'Download Geaographical Domain Template', 'BatchGeographicalDomainService', NULL, NULL, 
    'configfiles/restservice', '/rest/batch-geographical-domain/download-template', 'N', 'Y', NULL);
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('INITIATEGRPHDMNBATCH', 'Initiate Batch Geaographical Domain Creation', 'BatchGeographicalDomainService', NULL, NULL, 
    'configfiles/restservice', '/rest/batch-geographical-domain/initiate-batch', 'N', 'Y', NULL);
COMMIT;

update roles set status='N' where role_code='ASSOPTTRFRULE';
commit;

SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '80823', 'mclass^2&pid^61:80823:No records exist for MSISDN:{0}.', 'ALL', 'mclass^2&pid^61:80823:No records exist for MSISDN:{0}.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '80824', 'mclass^2&pid^61:80824:Records fetched successfully for MSISDN:{0}.', 'ALL', 'mclass^2&pid^61:80824:Records fetched successfully for MSISDN:{0}.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '80825', 'mclass^2&pid^61:80825:The entered Child MSISDN:{0} is not in your hierarchy.', 'ALL', 'mclass^2&pid^61:80825:The entered Child MSISDN:{0} is not in your hierarchy.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

SET DEFINE OFF;
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('DCSRREQ', 'C2S', 'BOTH', 'TYPE MSISDN1', 'com.btsl.pretups.user.requesthandler.DailySelfChildTransferRequestHandler', 
    'c2s.transferreport', 'Daily Self and Child status Report', 'Y', TO_DATE('11/18/2016 15:08:40', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('11/18/2016 15:08:55', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Daily Self and Child Status Report', 'N', 'N', 
    'Y', NULL, 'N', 'CHRG', 'N', 
    NULL, NULL, NULL, 'TYPE.TXNSTATUS,MESSAGE', 'TYPE,EXTNWCODE,LOGINID,PASSWORD,MSISDN,PIN', 
    'Y');
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('DUSRBALREQ', 'C2S', 'BOTH', 'TYPE MSISDN1', 'com.btsl.pretups.user.requesthandler.DailySelfChildBalanceRequestHandler', 
    'c2s.userbalancereport', 'Daily Self and Child User Balance Report', 'Y', TO_DATE('11/18/2016 15:08:40', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('11/18/2016 15:08:55', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Daily Self and Child User Balance Report', 'N', 'N', 
    'Y', NULL, 'N', 'CHRG', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,EXTNWCODE,LOGINID,PASSWORD,MSISDN,PIN', 
    'Y');
COMMIT;

