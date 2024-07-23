--##########################################################################################################
--##
--##      PreTUPS_v7.0.0 DB Script
--##
--##########################################################################################################


--##########################################################################################################
--##
--##      PreTUPS_v7.1.0 DB Script
--##
--##########################################################################################################

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VMS_AUTO_VOUCHER_CRTN_ALWD', 'Auto Voucher Creation Allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Auto Voucher Creation Allowed flag', 'Y', 
    'Y', 'C2S', 'Auto Voucher Creation Allowed flag. If default_value column value is true then it will trigger auto  creation of vouchers else it will not trigger', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'true,false', 'Y');
COMMIT;

alter table voms_products add AUTO_GENERATE VARCHAR(5) default 'N';
Commit;
alter table voms_products add  AUTO_THRESHOLD VARCHAR(10);
Commit;
alter table voms_products add  auto_quantity VARCHAR(10);
Commit;


INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'PB', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2019-12-26 02:25:21.000000', 'NGLA0000003720', TIMESTAMP '2019-12-26 02:25:21.000000', 'NGLA0000003720', 'CATPRF');
INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'PB', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', 'CATPRF');
INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'PB', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2019-12-26 02:25:21.000000', 'NGLA0000003720', TIMESTAMP '2019-12-26 02:25:21.000000', 'NGLA0000003720', 'CATPRF');
INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'PB', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2019-12-26 02:20:09.000000', 'NGLA0000003720', TIMESTAMP '2019-12-26 02:20:09.000000', 'NGLA0000003720', 'CATPRF');




INSERT INTO process_status
(process_id, start_date, scheduler_status, executed_upto, executed_on, expiry_time, before_interval, description, network_code, record_count)
VALUES('VOMSGENAUTO', '2017-09-19 18:12:24.054', 'C', '2017-09-19 18:01:51.928', '2017-09-19 00:00:00.000', 360, 1440, 'VOMS AUTO Generation Process', 'NG', 0);
Commit;

ALTER TABLE Roles ADD ACCESS_TYPE varchar(1);
ALTER TABLE ONLY roles ALTER COLUMN ACCESS_TYPE SET DEFAULT 'B';
update roles set ACCESS_TYPE='B';
Commit;

Insert into MESSAGE_GATEWAY
   (GATEWAY_CODE, GATEWAY_NAME, GATEWAY_TYPE, GATEWAY_SUBTYPE, PROTOCOL, 
    HANDLER_CLASS, NETWORK_CODE, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, HOST, STATUS, REQ_PASSWORD_PLAIN)
Values
   ('VSTK', 'VSTK', 'VSTK', 'SMPP', 'HTTP', 
    'com.client.pretups.gateway.parsers.STKParsers', 'VM', current_timestamp, 'SU0001', current_timestamp, 
    'SU0001', '172.16.11.120', 'Y', 'Y');

Insert into MESSAGE_GATEWAY_TYPES
   (GATEWAY_TYPE, GATEWAY_TYPE_NAME, ACCESS_FROM, PLAIN_MSG_ALLOWED, BINARY_MSG_ALLOWED, 
    FLOW_TYPE, RESPONSE_TYPE, TIMEOUT_VALUE, DISPLAY_ALLOWED, MODIFY_ALLOWED, 
    USER_AUTHORIZATION_REQD)
Values
   ('VSTK', 'VSTK', 'PHONE', 'Y', 'Y', 
    'C', 'PUSH', 0, 'Y', 'Y', 
    'Y');

Insert into MESSAGE_GATEWAY_SUBTYPES
   (GATEWAY_SUBTYPE, GATEWAY_TYPE, GATEWAY_SUBTYPE_NAME)
Values
   ('VSTK', 'VSTK', 'Vietnam STK');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('STK_MASTER_KEY', 'Master Key for STK', 'SYSTEMPRF', 'STRING', '6B4FC9246FB075B619626600EAA870F9', 
    NULL, NULL, 50, 'Master Key for STK', 'N', 
    'N', 'C2S', 'Master Key for STK', current_timestamp, 'ADMIN', 
    current_timestamp, 'ADMIN', NULL, 'Y');
COMMIT;

--User Authentication API
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('PIN_VALIDATATION_IN_USSD', 'Pin tag required in USSD', 'NETWORKPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'PIN is required or not in USSD', 'N', 
    'N', 'C2S', 'PIN is required or not in USSD', TO_DATE('07/31/2017 11:28:13', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/31/2017 11:28:13', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '10009', 'mclass^2&pid^61:10009:SUCCESS User name is {0}, Category is {1}, Category code is {2}.', 'CC', 'mclass^2&pid^61:10009:SUCCESS User name is {0}, Category is {1}, Category code is {2}.', 
    'mclass^2&pid^61:10009:SUCCESS User name is {0}, Category is {1}, Category code is {2}.', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;


Insert into CATEGORY_SERVICE_TYPE
   (CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE)
 Values
   ('NWADM', 'OPTAPIADD', 'NG');
   
   Insert into CATEGORY_SERVICE_TYPE
   (CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE)
 Values
   ('NWADM', 'OPTAPIMOD', 'NG');


Insert into CATEGORY_SERVICE_TYPE
   (CATEGORY_CODE, SERVICE_TYPE, NETWORK_CODE)
 Values
   ('NWADM', 'OPTAPISRD', 'NG');
   
   Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('CHNLUSRADD', 'OPT', 'ALL', 'KEYWORD', 'com.btsl.pretups.channel.transfer.requesthandler.AddChannelUserController', 
    'Channel User Add', 'Channel User Add', 'Y', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'Channel User Add', 'Y', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,IMEI,PIN', 
    'Y');

	Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('CHNLUSRMOD', 'OPT', 'ALL', 'KEYWORD', 'com.btsl.pretups.channel.transfer.requesthandler.ModifyChannelUserController', 
    'Channel User Modify', 'Channel User Modify', 'Y', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'Channel User Modify', 'Y', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,IMEI,PIN', 
    'Y');
	
	Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('GEOAPI', 'C2S', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.channel.transfer.requesthandler.GeoAddModDelController', 
    'Geography API', 'Geography API', 'Y', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'Geography API', 'Y', 'N', 
    'Y', 11, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 
    'Y');
	
	Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('VIEWGEOAPI', 'C2S', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.channel.transfer.requesthandler.ViewGeographyController', 
    'View Geography API', 'View Geography API', 'Y', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'View Geography API', 'Y', 'N', 
    'Y', 11, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 
    'Y');
	
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('VIEWUSRREQ', 'OPT', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.requesthandler.ViewUserController', 
    'View User', 'View User', 'Y', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'View User', 'Y', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 
    'Y');

CREATE TABLE ERP 
( 
ORDER_NUMBER int, 
HEADER_ID int, 
ORDER_TYPE_ID int, 
ORDER_TYPE varchar(100), 
SHIP_TO_ORG_ID int, 
SALE_CHANNEL varchar(100), 
PARTY_TYPE varchar(100), 
PARTY_int int, 
PARTY_ID int, 
DEALER_CODE varchar(100), 
PARTY_NAME varchar(100), 
ORDER_DATE DATE, 
LINE_int int, 
LINE_ID int, 
INVENTORY_ITEM_ID int, 
ITEM_TYPE_CODE varchar(100), 
ORDER_QUANTITY_UOM varchar(100), 
SHIPPING_QUANTITY_UOM varchar(100), 
ORDERED_QUANTITY bigint, 
PRICING_QUANTITY bigint, 
PRICING_QUANTITY_UOM varchar(100), 
AMOUNT bigint, 
UNIT_LIST_PRICE bigint, 
TAX_VALUE bigint, 
ORDERED_ITEM varchar(100), 
ORDERED_ITEM_ID int, 
LAST_MODIFIED DATE, 
CUST_PO_int varchar(100), 
TRANSFER_ID varchar(20), 
STATUS varchar(10), 
ERROR_CODE varchar(20),
PRIMARY KEY (LINE_ID, ORDER_NUMBER) 
)	



ALTER TABLE pages ADD COLUMN spring_page_url varchar(100);
UPDATE pages set spring_page_url = page_url;

--C2C Transfer
UPDATE pages
SET spring_page_url='/pretups/ChannelToChannelSearchAction.form'
WHERE page_code='C2CTRF001';

--C2C Withdraw
UPDATE pages
SET  spring_page_url='/channelToChannelWithdrawSearch/withdraw.form'
WHERE page_code IN ('C2CWDR001','C2CWDR001A','C2CWDRDMM');
 

--C2S Transfer
UPDATE pages SET spring_page_url='/transfer/c2sRecharge.form' WHERE page_code IN ('C2SRECHR01','C2SRECHR1A','C2SRECHRDM');

COMMIT;

--O2C Enquiry:
UPDATE pages
SET  spring_page_url='/channeltransfer/O2Cenquiry.form'
WHERE page_code='O2CENQ006';
UPDATE pages
SET  spring_page_url='/channeltransfer/O2Cenquiry.form'
WHERE page_code='O2CENQ006A';
UPDATE pages
SET  spring_page_url='/channeltransfer/O2Cenquiry.form'
WHERE page_code='O2CENQDMM2'; 


--C2C enquiry
UPDATE pages
SET spring_page_url= '/channeltransfer/chnlToChnlEnquiry.form'
WHERE page_code = 'C2CENQ001';


UPDATE pages
SET spring_page_url= '/channeltransfer/chnlToChnlEnquiry.form'
WHERE page_code = 'C2CENQ001A';


UPDATE pages
SET spring_page_url= '/channeltransfer/chnlToChnlEnquiry.form'
WHERE page_code = 'C2CENQDMM1';


--C2S enquiry
UPDATE pages
SET spring_page_url='/c2sTransfer/c2s-Transfer-Enquiry.form' WHERE page_code='C2STENQ001';

UPDATE pages
SET spring_page_url='/c2sTransfer/c2s-Transfer-Enquiry.form' WHERE page_code='C2STENQ01A';

UPDATE pages
SET spring_page_url='/c2sTransfer/c2s-Transfer-Enquiry.form' WHERE page_code='C2STENQDMM';

Insert into INTERFACE_TYPES
   (INTERFACE_TYPE_ID, INTERFACE_NAME, INTERFACE_CATEGORY, HANDLER_CLASS, UNDERPROCESS_MSG_REQD, 
    MAX_NODES, URI_REQ)
 Values
   ('CUINFO', 'SAP User Interface', 'PRE', 'com.client.pretups.userinfo.aup.requesthandler.ClaroAUPCUInfoWSINHandler', 'N', 
    1, 'N');



Insert into INTERFACES
   (INTERFACE_ID, EXTERNAL_ID, INTERFACE_DESCRIPTION, INTERFACE_TYPE_ID, STATUS, 
    CLOUSER_DATE, MESSAGE_LANGUAGE1, MESSAGE_LANGUAGE2, CONCURRENT_CONNECTION, SINGLE_STATE_TRANSACTION, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, STATUS_TYPE, 
    VAL_EXPIRY_TIME, TOPUP_EXPIRY_TIME, NUMBER_OF_NODES)
 Values
   ('INTID00042', '045', 'SAP User Interface', 'CUINFO', 'Y', 
    CURRENT_TIMESTAMP, 'System out of service, please try again later.', 'Sistema fuera de servicio, por favor intente mas tarde.', 10, 'Y', 
    CURRENT_TIMESTAMP, 'SU0001', CURRENT_TIMESTAMP, 'SU0001', 'M', 
    5000, 5000, NULL);





Insert into INTERFACE_NODE_DETAILS
   (INTERFACE_ID, IP, PORT, URI, STATUS, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SEQUENCE_ID, 
    DELETED_ON, DELETED_BY, SUSPENDED_ON, SUSPENDED_BY)
 Values
   ('INTID00042', 'localhost', '8088', 'mockStealthIntegration', 'Y', 
    CURRENT_TIMESTAMP, 'CCLA0000054100', CURRENT_TIMESTAMP, 'CCLA0000054100', 'NODID00054', 
    NULL, NULL, NULL, NULL);


Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('TXNENQREQ', 'C2S', 'NA', 'TYPE TXNID PIN', 'com.btsl.pretups.user.requesthandler.RechargeStatusHandler', 
    'Enquiry by Transaction ID', 'Enquiry by Transaction ID', 'N', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'Enquiry by Transaction ID', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,PIN,TXNID', 
    'Y');

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('LTSRVRREQ', 'C2S', 'NA', 'TYPE MSISDN1 MSISDN2 PIN', 'com.btsl.pretups.user.requesthandler.LastTransferStatusSubscriberWiseController', 
    'Last Txn Status by Subscriber MSISDN', 'Last Txn Status by Subscriber MSISDN', 'N', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'Last Txn Status by Subscriber MSISDN', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN1,PIN,MSISDN2', 
    'Y');
    
    
    Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('C2SSUMREQ', 'C2S', 'NA', 'TYPE MSISDN1 PIN FROMDATE TODATE SERVICETYPE SUBSERVICE', 'com.btsl.pretups.user.requesthandler.C2SSummaryEnquiryController', 
    'C2S Summary Enquiry', 'C2S Summary Enquiry', 'N', CURRENT_TIMESTAMP, 'ADMIN', 
    CURRENT_TIMESTAMP, 'ADMIN', 'C2S Summary Enquiry', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE MSISDN1 PIN SERVICETYPE', 
    'Y');
	
COMMIT;

INSERT INTO pages (page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url)
VALUES('CHNGPINCU1', 'CUSERS', '/changePinAction.do?method=loadDomainList', 'Change PIN', 'Y', 30, '2', '1', '/user/change-pin.form');

INSERT INTO page_roles(role_code, page_code, application_id) VALUES('CHANGEPINCU', 'CHNGPINCU1', '1 ');

UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='SE' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='AG' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='DIST' AND role_code='CHANGEPIN';
UPDATE category_roles SET role_code='CHANGEPINCU' WHERE category_code='RET' AND role_code='CHANGEPIN';

UPDATE roles SET role_code='CHANGEPINCU' WHERE role_code='CHANGEPIN' AND domain_type='DISTB_CHAN';
COMMIT;

INSERT INTO pages(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url)
VALUES('CUSRBALCU', 'C2SENQ', '/channelUserBalanceAction.do?method=loadDomainList', 'Other''s Balance', 'Y', 30, '2', '1', '/balances/userBalance.form');


INSERT INTO page_roles(role_code, page_code, application_id) VALUES('OTHERBALANCECU', 'CUSRBALCU', '1');


UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='SE' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='AG' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='DIST' AND role_code='OTHERBALANCE';
UPDATE category_roles SET role_code='OTHERBALANCECU' WHERE category_code='RET' AND role_code='OTHERBALANCE';

UPDATE roles SET role_code='OTHERBALANCECU' WHERE role_code='OTHERBALANCE' AND domain_type='DISTB_CHAN';
COMMIT;

UPDATE pages SET spring_page_url='/pretups/ChannelToChannelSearchAction.form' WHERE page_code='C2CTRFDMM';
UPDATE pages SET spring_page_url='/pretups/ChannelToChannelSearchAction.form' WHERE page_code='C2CTRF001A';
UPDATE ids SET frequency='HOURS' WHERE id_type='OT' AND id_year='2017';
COMMIT;


-----------------------------VMS Burn rate indicator scripts-----------------------------------

CREATE TABLE TEMP_BURN_RATE_INDICATOR
(
  ROW_NUMBER  int,
  DATA        VARCHAR(4000)
);

CREATE TABLE VOMS_DAILY_BURNED_VOUCHERS
(
  SUMMARY_DATE             DATE NOT NULL,
  PRODUCT_ID               VARCHAR(15) NOT NULL,
  PRODUCTION_NETWORK_CODE  VARCHAR(2) NOT NULL,
  USER_NETWORK_CODE        VARCHAR(2) NOT NULL,
  USER_ID                  VARCHAR(15) NOT NULL,
  TOTAL_DISTRIBUTED        INT           DEFAULT 0,
  TOTAL_RECHARGED          INT           DEFAULT 0,
  TOTAL_EXPIRED            INT          DEFAULT 0,
  TOTAL_STOLEN_DMG         INT           DEFAULT 0
);

 alter table VOMS_DAILY_BURNED_VOUCHERS add constraint t_pk primary key (user_id,product_id,user_network_code,production_network_code,summary_date); 
 
  alter table voms_daily_burned_vouchers ADD TOTAL_ONHOLD INT  DEFAULT 0;
  alter table voms_daily_burned_vouchers ADD TOTAL_SUSPENDED  INT  DEFAULT 0;
 
 Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('VOMSBURNED', TO_DATE('05/18/2017 13:29:15', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('05/17/2017 17:46:02', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('05/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    120, 1440, 'Voucher Burned rate indicator process', 'NG', 0);
COMMIT;

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMBRATE001', 'VOMSREPORT', '/vomsBurnRateIndicatorAction.do?method=loadLists', 'Voucher Burn Rate Indicator', 'Y', 
    4, '2', '1', '/vomsBurnRateIndicatorAction.do?method=loadLists');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMBRATE002', 'VOMSREPORT', '/jsp/vomsreport/viewVoucherBurnRate.jsp', 'Voucher Burn Rate Indicator', 'N', 
    4, '2', '1', '/jsp/vomsreport/viewVoucherBurnRate.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMBRATE00A', 'VOMSREPORT', '/vomsBurnRateIndicatorAction.do?method=loadLists', 'Voucher Burn Rate Indicator', 'N', 
    4, '2', '1', '/vomsBurnRateIndicatorAction.do?method=loadLists');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMBRATEDMM', 'VOMSREPORT', '/vomsBurnRateIndicatorAction.do?method=loadLists', 'Voucher Burn Rate Indicator', 'Y', 
    4, '1', '1', '/vomsBurnRateIndicatorAction.do?method=loadLists');
COMMIT;


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSBURNRATE', 'Voucher Burn Rate Indicator', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');
COMMIT;


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSBURNRATE', 'VMBRATE001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSBURNRATE', 'VMBRATE002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSBURNRATE', 'VMBRATE00A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSBURNRATE', 'VMBRATEDMM', '1');
COMMIT;


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOMSBURNRATE', '1');
COMMIT;

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('BURN_RATE_THRESHOLD_PCT', 'Burn rate threshold in percentage', 'SYSTEMPRF', 'STRING', '100', 
    1, 100, 50, 'Burn rate threshold in percentage', 'Y', 
    'Y', 'C2S', 'Burn rate threshold in percentage', TO_DATE('07/06/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/17/2005 13:51:58', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
COMMIT;



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VOUCHER_BURN_RATE_SMS_ALERT', 'Voucher Burn rate SMS alert', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Preference to define if Alert SMS is required or not. Values may be TRUE or FALSE', 'Y', 
    'Y', 'C2S', 'Preference to define if Alert SMS is required or not. Values may be TRUE or FALSE', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'true,false', 'Y');
COMMIT;


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VOUCHER_BURN_RATE_EMAIL_ALERT', 'Voucher Burn rate EMAIL alert', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Preference to define if Alert Email is required or not. Values may be TRUE or FALSE', 'Y', 
    'Y', 'C2S', 'Preference to define if Alert Email is required or not. Values may be TRUE or FALSE', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'true,false', 'Y');
COMMIT;


Insert into DOWNLOAD_SCHEDULED_REPORTS
   (REPORT_CODE, REPORT_NAME, TYPE, STATUS, PATH_KEY, 
    PREFIX, DATE_FORMAT, MODULE, DISPLAY_SEQ)
 Values
   ('RPT020', 'Voucher Burn rate Summary Report', 'csv', 'Y', 'VMS_BURN_RATE_SUMMARY_REPORT_PATH', 
    'BURNRATE_INDICATOR', 'ddMMyy', 'C2S', NULL);
COMMIT;





CREATE OR REPLACE FUNCTION get_voms_data_dtrange (
      aiv_fromdate          IN       character varying,
      aiv_todate            IN       character varying,
      aov_message           OUT      character varying,
      aov_messageforlog     OUT      character varying,
      aov_sqlerrmsgforlog   OUT      character varying
   ) RETURNS record
   as $$
   DECLARE 
      ld_from_date            TIMESTAMP(0);
      ld_to_date              TIMESTAMP(0);
      n_date_for_process      TIMESTAMP(0);
      voms_already_executed   SMALLINT ;

   BEGIN

	    RAISE NOTICE '%', 'Date range function eneterd'; 
      ld_from_date := TO_DATE (aiv_fromdate, 'dd/mm/yy');
      ld_to_date := TO_DATE (aiv_todate, 'dd/mm/yy');
      n_date_for_process := ld_from_date;
      voms_already_executed := 0;

      WHILE n_date_for_process <= ld_to_date
      ---run the process for each date less than the To Date
      LOOP
         RAISE NOTICE '%', 'EXCEUTING FOR ::::::::' || n_date_for_process;

         BEGIN
            ---Check if the process has already run for the date
            RAISE NOTICE '%', 'Before select ' || n_date_for_process;

            voms_already_executed=(SELECT 1
              FROM process_status
             WHERE process_id = 'VOMSBURNED'
               AND executed_upto >= n_date_for_process);
               
               RAISE NOTICE '%', 'After select ' || voms_already_executed;

			   
			      if voms_already_executed is not null then

            RAISE NOTICE '%',
                           'Burned Vouchers process already Executed, Date:'
                          || n_date_for_process;

            aov_message := 'FAILED';
            aov_messageforlog :=
                  'Burned Vouchers process already Executed, Date:'
               || n_date_for_process;
            aov_sqlerrmsgforlog := ' ';
           -- RAISE alreadydoneexception;
		    RAISE EXCEPTION  using errcode ='ERR02';
			else
    ---     EXCEPTION
         ---   WHEN NO_DATA_FOUND
       ---     THEN
               UPDATE process_status
                  SET executed_upto = n_date_for_process,
                      executed_on = CURRENT_TIMESTAMP
                WHERE process_id = 'VOMSBURNED';

               /* COMMIT; */
                  RAISE NOTICE '%',
                           'Burned Vouchers process successfully executed';
               aov_message := 'SUCCESS';
               aov_messageforlog :=
                     'Burned Vouchers process successfully executed, Date Time:'
                  || CURRENT_TIMESTAMP;
               aov_sqlerrmsgforlog := ' ';

               select * into aov_message, aov_messageforlog,aov_sqlerrmsgforlog from  voms_burnrate_indicator (n_date_for_process);
                aov_message := 'SUCCESS';
               aov_messageforlog :=
                     'Burned Vouchers process successfully executed, Date Time:';

        n_date_for_process := n_date_for_process + interval '1 day';         /* COMMIT; */
		
		 END IF;
		 END;
      END LOOP;
   EXCEPTION --Exception Handling of main procedure
 when sqlstate 'ERR01' then-- WHEN mainException THEN
  RAISE NOTICE '%','mainException Caught='||SQLERRM;
  aov_message :='FAILED';
  
  
  WHEN OTHERS THEN 
  RAISE NOTICE '%','OTHERS ERROR in Main procedure:='||SQLERRM;
  aov_message :='FAILED';
  
 
		 
   END;
$$ LANGUAGE plpgsql; 




 CREATE OR REPLACE FUNCTION voms_burnrate_indicator (
      p_date               IN       TIMESTAMP(0),
      p_returnmessage      OUT      character varying,
      p_returnlogmessage   OUT      character varying,
      p_sqlerrormessage    OUT      character varying
   ) RETURNS record
   as $$
   DECLARE 
      rcd_count                 INT;
      p_consumed                INT;
      p_distributed             INT;
      p_stolen                  INT;
      p_expired                 INT;
	  p_onhold                  INT;
	  p_suspended               INT;
      p_status                  VARCHAR (20);
      p_userid                  VARCHAR (20);
      p_product_id              VARCHAR (15);
      p_productionnetworkcode   VARCHAR (2);
      p_usernetworkcode         VARCHAR (2);
      p_modifiedon              TIMESTAMP(0);


    declare total_data CURSOR 
      IS
         SELECT   COUNT (1) AS total_count, user_id, product_id,
                  production_network_code, user_network_code, current_status,
                  date_trunc('day',MODIFIED_ON::TIMESTAMP)::DATE AS modified_on
             FROM voms_vouchers
            WHERE modified_on >= p_date
              AND modified_on < p_date::date+ interval '1' day
              AND current_status IN ('EN', 'CU', 'ST','DA', 'EX','OH','S')
              AND user_id != ' '
         GROUP BY user_id,
                  product_id,
                  current_status,
                  production_network_code,
                  user_network_code,
                  date_trunc('day',MODIFIED_ON::TIMESTAMP)::DATE;
                  
                  
   BEGIN

      p_stolen := 0;
      p_distributed := 0;
      p_consumed := 0;
      p_expired := 0;
	  p_onhold := 0;
	  p_suspended := 0;
      p_status := '';
      p_userid := '';
      p_product_id := '';
      p_productionnetworkcode := '';
      p_usernetworkcode := '';
      --p_modifiedon := '';
      
RAISE NOTICE '%',
                       'Input date ' || p_date;
                       

      FOR total_data_cur IN total_data
      LOOP
       
RAISE NOTICE '%',
                       'Cursor Entered';

         p_stolen := 0;
         p_distributed := 0;
         p_consumed := 0;
         p_expired := 0;
		 p_onhold := 0;
		 p_suspended := 0;
         p_userid := total_data_cur.user_id;
         p_status := total_data_cur.current_status;
         p_product_id := total_data_cur.product_id;
         p_productionnetworkcode := total_data_cur.production_network_code;
         p_usernetworkcode := total_data_cur.user_network_code;
         p_modifiedon := total_data_cur.modified_on;
         
         RAISE NOTICE '%',
                       'Modified on ' ||p_modifiedon;

          IF p_status = 'EN'
      THEN
         p_distributed := total_data_cur.total_count;
      ELSIF p_status = 'CU'
      THEN
         p_distributed := p_distributed + total_data_cur.total_count;
         p_consumed := total_data_cur.total_count;
      ELSIF p_status = 'EX'
      THEN
         p_distributed := p_distributed + total_data_cur.total_count;
         p_expired := total_data_cur.total_count;
      ELSIF p_status = 'ST' OR p_status = 'DA'
      THEN
         p_distributed := p_distributed + total_data_cur.total_count;
         p_stolen := total_data_cur.total_count;
      ELSIF p_status = 'OH'
      THEN
         p_distributed := p_distributed + total_data_cur.total_count;
         p_onhold := total_data_cur.total_count;
      ELSIF p_status = 'S'
      THEN
         p_distributed := p_distributed + total_data_cur.total_count;
         p_suspended := total_data_cur.total_count;
      END IF;



         --block checking if record exist in voms_daily_burned_vouchers
         begin

          rcd_count= (SELECT 1
              FROM voms_daily_burned_vouchers
             WHERE summary_date = p_modifiedon
               AND user_id = p_userid
               AND production_network_code = p_productionnetworkcode
               AND user_network_code = p_usernetworkcode
               AND product_id = p_product_id);

            RAISE NOTICE '%', 'rcd_count= ' || rcd_count; 
            RAISE NOTICE '%', 'p_userid= ' || p_userid;
            RAISE NOTICE '%', 'p_product_id= ' || p_product_id;
            
         
            --IF NOT FOUND
            if rcd_count is NULL 
            THEN
               --when no row returned for the distributor for particular date
                           RAISE NOTICE '%', 'Entered exception for no existing data found' ;
               RAISE NOTICE '%',
                       'No Record found in voms_daily_burned_vouchers table';
               rcd_count := 0;
                RAISE NOTICE '%', 'rcd_count after query execution in case of NULL= ' || rcd_count;
               end IF;
               
               EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',
                    'SQL EXCEPTION while checking for voms_daily_burned_vouchers  ='
                   || SQLERRM;
               p_returnmessage := 'FAILED';
               p_returnlogmessage :=
                  'Exception while checking is record exist in voms_daily_burned_vouchers table ';
               RAISE EXCEPTION  using errcode = 'ERR01';

         END;
RAISE NOTICE '%',
                       'Enter block for insert or update';
         BEGIN
            IF rcd_count = 0
            then
            RAISE NOTICE '%',
                       'Enter block for insert when rcd count =0';
               INSERT INTO voms_daily_burned_vouchers
                           (summary_date, product_id,
                            production_network_code, user_network_code,
                            user_id, total_distributed, total_recharged,
                            total_expired, total_stolen_dmg, total_onhold, total_suspended
                           )
                    VALUES (p_modifiedon, p_product_id,
                            p_productionnetworkcode, p_usernetworkcode,
                            p_userid, p_distributed, p_consumed,
                            p_expired, p_stolen, p_onhold, p_suspended
                           );

               p_returnmessage := 'SUCCESS';
               p_returnlogmessage :=
                  'Records successfully inserted in voms_daily_burned_vouchers table ';
                  RAISE NOTICE '%',
                       'Data inserted in voms_daily_burned_vouchers ';
            ELSE
               UPDATE voms_daily_burned_vouchers
                  SET total_distributed = total_distributed + p_distributed,
                      total_recharged = total_recharged + p_consumed,
                      total_expired = total_expired + p_expired,
                      total_stolen_dmg = total_stolen_dmg + p_stolen,
					  total_onhold = total_onhold + p_onhold,
					  total_suspended = total_suspended + p_suspended
                WHERE summary_date = p_modifiedon
                  AND user_id = p_userid
                  AND production_network_code = p_productionnetworkcode
                  AND user_network_code = p_usernetworkcode
                  AND product_id = p_product_id;

               p_returnmessage := 'SUCCESS';
               p_returnlogmessage :=
                  'Records successfully updated in voms_daily_burned_vouchers table ';
                  RAISE NOTICE '%',
                       'Data updated in voms_daily_burned_vouchers';
            END IF;
         EXCEPTION

            WHEN OTHERS
            THEN
               p_returnmessage := 'FAILED';
               p_returnlogmessage :=
                  'Exception while inserting/updating voms_daily_burned_vouchers table ';
			   RAISE EXCEPTION  using errcode = 'ERR01';
         END;
      END LOOP;
 
   END;
 $$ LANGUAGE plpgsql;


  --------------------------------------------VMS Burn rate indicator scripts end------------------------------------------

update req_message_gateway set underprocess_check_reqd='Y' where gateway_code='REST';
COMMIT;

update service_type set status='N' where service_type in ('VRAG','ADV','CAUT','WRC','VR','VQ','MRC','VB','VSCH');
COMMIT; 

update req_message_gateway set underprocess_check_reqd='N' where gateway_code='REST';
COMMIT;

CREATE TABLE TPS_DETAILS
(
  TPS_DATE_TIME                    DATE ,
  INSTANCE_CODE                 varchar(3),
  TPS							int,
  TPS_DATE 					DATE  
);

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('MAXTPS', 'C2S', 'ALL', 'TYPE QDATE QHOUR', 'com.btsl.pretups.requesthandler.MaxTPSHandler', 
    'c2s.change.pin', 'MAX TPS DETAILS', 'Y', current_timestamp, 'ADMIN', 
   current_timestamp, 'ADMIN', 'Max TPS Calculation Per Hour', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MAXTPS,MESSAGE', 'TYPE,MSISDN,PIN,QDATE,QHOUR,LANGUAGE1', 
    'Y');
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('MAXTPSHOURLYREQ', 'EXTGW', '190', 'MAXTPS', 'MAX TPS CALCULATION', 
    'Y', NULL, NULL, NULL, 'Y', 
   current_timestamp, 'SU0001',current_timestamp, 'SU0001', 'SVK9000076', 
    NULL, 'TYPE,QDATE,QHOUR');
	

update pages set menu_item='Y',menu_level=1 where PAGE_CODE in ('O2CBWDR01M','O2CWRAP01M'); 
update pages set menu_level=2 where  PAGE_CODE in ('O2CBWDR01A','O2CWRAP01A');
commit;


--##########################################################################################################
--##
--##      PreTUPS_v7.2.0 DB Script
--##
--##########################################################################################################
--Excluded as this is not a part of 7.2.0
--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR001';
-- PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR01A';
--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTRDMM';

--commit;

CREATE TABLE CACHE_TYPES
(
  CACHE_CODE            VARCHAR(20) NOT NULL,
  CACHE_NAME            VARCHAR(50)     NOT NULL,
  STATUS                      VARCHAR(1)  NOT NULL,
  CACHE_KEY        VARCHAR(70)     NOT NULL
);

Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CELLID', 'Cell ID Cache', 'Y', 'updatecacheservlet.cellidcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERWALLET', 'User wallet cache', 'Y', 'updatecacheservlet.userwalletcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERSERVICE', 'User services cache', 'Y', 'updatecacheservlet.userservicecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERDEFAULTCONFIG', 'User default config cache', 'Y', 'updatecacheservlet.userdefaultcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('USERALLWDSTATUS', 'User Allowed Status Cache', 'Y', 'updatecacheservlet.userstatuscache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRANSFERRULE', 'Transfer rule cache', 'Y', 'updatecacheservlet.transferrulescache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRFPRFPRD', 'Transfer Profile Product Cache', 'Y', 'updatecacheservlet.transferprofileproductcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('TRFPRF', 'Transfer Profile Cache', 'Y', 'updatecacheservlet.transferprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SIMPRF', 'Sim Profile Cache', 'Y', 'updatecacheservlet.simProfilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVSLTRMAPP', 'Service selector mapping cache', 'Y', 'updatecacheservlet.serviceselectormappingecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVPAYMENTMAPP', 'Service payment mapping cache', 'Y', 'updatecacheservlet.servicepaymentmappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('REGCONTRL', 'Registration Control Cache', 'Y', 'updatecacheservlet.registrationControlcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVINTFCROUTING', 'Service Interface Routing Cache', 'Y', 'updatecacheservlet.serviceInterfaceroutingecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVINTMAPP', 'Service Interface Mapping Cache', 'Y', 'updatecacheservlet.serviceInterfacemappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('REQINTFC', 'Request interface cache', 'Y', 'updatecacheservlet.requestinterfacecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('PAYMNTMETH', 'Payment method cache', 'Y', 'updatecacheservlet.paymentmethodcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVKEYWORD', 'Service keyword cache', 'Y', 'updatecacheservlet.servicekeywordcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('SRVCLASSINFO', 'Service Class Info By Code Cache', 'Y', 'updatecacheservlet.serviceclassbycodecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('ROUTINGCONTL', 'Routing Control Cache', 'Y', 'updatecacheservlet.routingControlcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('PREFERENCE', 'Preference cache', 'Y', 'updatecacheservlet.preferencecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWSERVICE', 'Network Service Cache', 'Y', 'updatecacheservlet.networkservicecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPRDSRVTYPE', 'Network Product Service Type Cache', 'Y', 'updatecacheservlet.networkproductservicetypecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPRD', 'Network Product Cache', 'Y', 'updatecacheservlet.networkproductcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWPREFIX', 'Network prefix cache', 'Y', 'updatecacheservlet.networkprefixcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NWINTRFCMOD', 'Network interface module cache', 'Y', 'updatecacheservlet.networkinterfacemodulecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('NETWORK', 'Network cache', 'Y', 'updatecacheservlet.networkcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MOBILENOPRFINTR', 'Mobile number prefix interfaces cache', 'Y', 'updatecacheservlet.msisdnprefixinterfacemappingcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGERESOURCE', 'Message Resource', 'Y', 'updatecacheservlet.messageresourcecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGEGTWCAT', 'Message Gateway For Category Cache', 'Y', 'updatecacheservlet.msggatwayforcatgorycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MSGGTW', 'Message gateway cache', 'Y', 'updatecacheservlet.messagegatewaycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('MESSAGE', 'Message Cache', 'Y', 'updatecacheservlet.messagecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LOOKUP', 'Lookup cache', 'Y', 'updatecacheservlet.loockupscache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LOGGER', 'Logger Config', 'Y', 'updatecacheservlet.loggercache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('LMSPRF', 'Lms Profile Cache', 'Y', 'updatecacheservlet.lmsprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('INTRROUTINGCONTL', 'Interface routing control cache', 'Y', 'updatecacheservlet.interfaceroutingcontrolcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('IATNW', 'IAT network cache', 'Y', 'updatecacheservlet.iatnetworkcachecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('IATCONTRYMAST', 'IAT country master cache', 'Y', 'updatecacheservlet.iatcoutrymastercache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('GROUPTYPEPRF', 'Group type profile cache', 'Y', 'updatecacheservlet.grouptypeprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('FILE', 'File cache', 'Y', 'updatecacheservlet.filecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CURRENCY', 'Currency Cache', 'Y', 'updatecacheservlet.currencycache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CONSTANTS', 'Constant Properties', 'Y', 'updatecacheservlet.constantcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('COMMPRF', 'Commission Profile Cache', 'Y', 'updatecacheservlet.commissionprofilecache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('CARDGROUP', 'Card Group Cache', 'Y', 'updatecacheservlet.cardgroupcache');
Insert into CACHE_TYPES
   (CACHE_CODE, CACHE_NAME, STATUS, CACHE_KEY)
 Values
   ('BONUSBUNDLE', 'Bonus bundle cache', 'Y', 'updatecacheservlet.bounsboundlecache');

UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTR001';
UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTRDMM';
UPDATE pages SET spring_page_url ='/pretups/Channel2ChannelTransferReport.form' where page_code='RPTRWTR01A';

UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDD01';
UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDD1A';
UPDATE pages SET spring_page_url ='/reportsO2C/o2cTransferDetails.form' where page_code='RPTO2CDDDM';

UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='Y', sequence_no=26, menu_level='2', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBL001';


UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='N', sequence_no=26, menu_level='2', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBL01A';


UPDATE pages
SET module_code='CHRPTUSR', page_url='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', menu_name='Users Closing Balance', menu_item='Y', sequence_no=26, menu_level='1', application_id='1', spring_page_url='/reports/userClosingBalance.form'
WHERE page_code='URCLOBLDMM';


   
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('INET_REPORT_ALLOWED', 'NET REPORT WITH DATA TABLE ALLOWED', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'TO ENABLE INET REPORT ALONG WITH DATA TABLE', 'Y', 'N', 'C2S', 'TO ENABLE INET REPORT ALONG WITH DATA TABLE;CHANGES MADE DURING SPRING DEVELPOMENT', TIMESTAMP '2018-01-18 00:00:00.000', 'ADMIN',TIMESTAMP  '2018-01-18 00:00:00.000', 'SU0001', NULL, 'Y');

ALTER TABLE cache_types
ADD PRIMARY KEY (cache_code); 

UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP001';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP01A';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACPDMM';


CREATE OR REPLACE FUNCTION userclosingbalance(p_userid character varying, p_startdate timestamp without time zone, p_enddate timestamp without time zone, p_startamt numeric, p_endamt numeric)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
declare 
p_userCloBalDateWise VARCHAR(4000) DEFAULT '' ;
balDate timestamp without time zone; 
balance numeric ; 
productCode VARCHAR(10);
c_userCloBal CURSOR(p_userId VARCHAR,p_startDate timestamp without time zone,p_endDate timestamp without time zone,p_startAmt numeric,p_endAmt numeric) IS
	   SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC, product_code ASC ;
        BEGIN
	    FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||'::'||balDate||'::'||balance||',';
        END LOOP;
        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise));        
        END IF;
            RETURN p_userCloBalDateWise;
END;
$function$


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, record_count)
 Values
   ('C2SERPDET', TO_DATE('10/26/2016 10:52:05', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('10/25/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('10/26/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'C2S Record Details', 'ET','0');
COMMIT;

update system_preferences
set modified_allowed='N' where preference_code='INET_REPORT_ALLOWED';

COMMIT;

--##########################################################################################################
--##
--##      PreTUPS_v7.3.0 DB Script
--##
--##########################################################################################################



UPDATE pages SET spring_page_url='/pretups/zeroBalCounterDetail.form' WHERE page_code='ZBALDET001';
UPDATE pages SET spring_page_url='/pretups/zeroBalCounterDetail.form' WHERE page_code='ZBALDET01A';
UPDATE pages SET spring_page_url='/pretups/zeroBalCounterDetail.form'WHERE page_code='ZBALDETDMM';

UPDATE pages SET  spring_page_url='/pretups/c2sTransfer.form' WHERE page_code='RPTTRCS01A';
UPDATE pages SET  spring_page_url='/pretups/c2sTransfer.form' WHERE page_code='RPTTRCSDMM';
UPDATE pages SET  spring_page_url='/pretups/c2sTransfer.form' WHERE page_code='RPTTRCS001';


UPDATE pages SET spring_page_url='/reports/load-user-balances.form' WHERE page_code='ZBALSUM001' ;
UPDATE pages SET spring_page_url='/reports/load-user-balances.form' WHERE page_code='ZBALSUMDMM' ;
UPDATE pages SET spring_page_url='/reports/load-user-balances.form' WHERE page_code='ZBALSUM01A' ;

UPDATE pages SET spring_page_url='/reports/channel-user.form' WHERE page_code='ROEU01A' ;
UPDATE pages SET spring_page_url='/reports/channel-user.form' WHERE page_code='ROEUDMM' ;
UPDATE pages SET spring_page_url='/reports/channel-user.form' WHERE page_code='ROEU001' ;





UPDATE pages SET spring_page_url='/channeltransfer/o2cTransferAckAction.form' WHERE page_code='O2CACKDMM' ;
UPDATE pages SET spring_page_url='/channeltransfer/o2cTransferAckAction.form' WHERE page_code='O2CACK001A' ;
UPDATE pages SET spring_page_url='/channeltransfer/o2cTransferAckAction.form' WHERE page_code='O2CACK001' ;


--ChangePin Module
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='SE' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='AG' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='DIST' AND role_code='CHANGEPINCU';
UPDATE category_roles SET role_code='CHANGEPIN' WHERE category_code='RET' AND role_code='CHANGEPINCU';

DELETE FROM page_roles
WHERE role_code='CHANGEPINCU' AND page_code='CHNGPINCU1';

DELETE FROM pages
WHERE page_code='CHNGPINCU1';

UPDATE roles SET role_code='CHANGEPIN' WHERE role_code='CHANGEPINCU' AND domain_type='DISTB_CHAN';
COMMIT;


--UserBalance Module
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='SE' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='AG' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='DIST' AND role_code='OTHERBALANCECU';
UPDATE category_roles SET role_code='OTHERBALANCE' WHERE category_code='RET' AND role_code='OTHERBALANCECU';


DELETE FROM page_roles
WHERE role_code='OTHERBALANCECU' AND page_code='CUSRBALCU';

DELETE FROM pages
WHERE page_code='CUSRBALCU';

UPDATE roles SET role_code='OTHERBALANCE' WHERE role_code='OTHERBALANCECU' AND domain_type='DISTB_CHAN'; 

COMMIT;

UPDATE pages
SET spring_page_url='/user/change-pin.form' WHERE page_code='CHNGPIN001';

UPDATE pages
SET spring_page_url='/balances/userBalance.form' WHERE page_code='CUSRBALV01';
COMMIT;


UPDATE pages
SET spring_page_url='/channelreport/load-additional-commission-summary.form' WHERE page_code='RPTADCS001';
COMMIT;

UPDATE pages SET spring_page_url='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV001';
UPDATE pages SET spring_page_url='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV01A';
UPDATE pages SET spring_page_url='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOVDMM';
COMMIT;

--Operation Summary Report
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT001';

update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT00A';

update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPTDMM';

--Struts support for BAR USER screen
UPDATE pages SET  page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/baruser/barreduser.form' WHERE page_code='BAR01';

UPDATE pages SET page_url='/subscriber/confirmBarredUser.jsp', spring_page_url='/subscriber/confirmBarredUser.jsp' WHERE page_code='BAR02';

UPDATE pages SET  page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/baruser/barreduser.form?method=loadBarredUser' WHERE page_code='BAR1Dmm';


UPDATE pages SET page_url='/barreduser.do?method=loadBarredUser', spring_page_url='/barreduser.do?method=loadBarredUser'
WHERE page_code='BAR01A';
commit;

------Struts & Spring support for UNBAR USER screen

UPDATE pages SET  page_url='/unbaruser.do?method=unBarUser', spring_page_url='/baruser/unbaruser.form'
WHERE page_code='UNBAR01';

UPDATE pages SET  page_url='/subscriber/selectUserToUnbarrSts.jsp', spring_page_url='/subscriber/selectUserToUnbarr.jsp' WHERE page_code='UNBAR02';

UPDATE pages SET page_url='/unbaruser.do?method=unBarUser', spring_page_url='/baruser/unbaruser.form' WHERE page_code='UNBAR1Dmm';

UPDATE pages SET  page_url='/unbaruser.do?method=unBarUser', spring_page_url='/unbaruser.do?method=unBarUser' WHERE page_code='UNBAR01A';


-------Struts & Spring support for view Barred list  screen
UPDATE pages SET page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBAR01';

UPDATE pages SET  page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/viewBarredUserAction.do?method=viewBarredList' WHERE page_code='VIEWBAR01A';


UPDATE pages SET page_url='/viewBarredUserAction.do?method=selectBarredUser', spring_page_url='/baruser/viewBarredUserAction.form' WHERE page_code='VIEWBARDmm';

UPDATE pages SET  page_url='/jsp/subscriber/viewBarredList.jsp',spring_page_url='/jsp/subscriber/viewBarredList.jsp' WHERE page_code='VIEWBAR02';



---------Staff self c2c report-----
UPDATE pages SET spring_page_url='/reports/staffSelfC2CReport.form' WHERE page_code='STFSLF01A' ;
UPDATE pages SET spring_page_url='/reports/staffSelfC2CReport.form' WHERE page_code='STFSLF001' ;
UPDATE pages SET spring_page_url='/reports/staffSelfC2CReport.form' WHERE page_code='STFSLFDMM' ;



Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
Values
   ('COMMT', 'Commission Type', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/02/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');

Insert into LOOKUPS (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values ('NC', 'Normal Commissioning', 'COMMT', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values ('PC', 'Positive Commissioning', 'COMMT', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
ALTER TABLE COMMISSION_PROFILE_SET ADD LAST_DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE COMMISSION_PROFILE_SET_VERSION ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE CHANNEL_TRANSFERS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE FOC_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE O2C_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
ALTER TABLE C2C_BATCH_ITEMS ADD DUAL_COMM_TYPE VARCHAR(2) DEFAULT 'NC' NOT NULL;
COMMIT;


--Associate Profile
UPDATE pages
SET spring_page_url='/user/load-associate-profile.form' WHERE page_code='ASSCUSR001';
COMMIT;


--Commission profile Status: Spring to struts changes
------------------------------------------------------
UPDATE pages SET page_url='/commissionProfileAction.do?method=loadDomainListForSuspend&page=0',spring_page_url='/commission-profile/status.form'
WHERE page_code='COMMPS001';


--Schedule RC script : Spring And struts compatibility
------------------------------------------------------
UPDATE pages SET  page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP01';

UPDATE pages SET  page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUP1A';

UPDATE pages SET page_url='/scheduleTopUp.do?method=scheduleTopUpAuthorise', spring_page_url='/schedule/scheduleTopUp.form?method=scheduleTopUpAuthorise' WHERE page_code='SCHTOPUPDM';


UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';



ALTER TABLE USERS ADD MIGRATION_STATUS VARCHAR(5);
COMMIT;

--Reschedule Batch Recharge :Spring to Struts compatibility
----------------------------------------

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP01';

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUP1A';

UPDATE pages SET  page_url='/rescheduleTopUp.do?method=rescheduleTopUpAuthorise', spring_page_url='/batch-reschedule/rescheduleTopUp.form' WHERE page_code='RSHTOPUPDM';

UPDATE pages SET  page_url='/jsp/restrictedsubs/rescheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/rescheduleTopUpDetails.jsp' WHERE page_code='RSHTOPUP02';


--Cancel Schedule TopUp :Spring to Struts compatibility
-----------------------------------------
UPDATE pages SET  page_url='/cancelScheduleRecharge.do?method=selectDetailsForSingleAuthorise', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH001';

UPDATE pages SET  page_url='/jsp/restrictedsubs/displayDetailsForCancelSingleSub.jsp',  spring_page_url='/restrictedsubs/cancel_schedule_recharge_details.form' WHERE page_code='CNCLSCH002';

UPDATE pages SET  page_url='/jsp/restrictedsubs/confirmDisplayDetailsForCancelSingleSub.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form' WHERE page_code='CNCLSCH003';

UPDATE pages SET  page_url='/jsp/restrictedsubs/selectBatchCancelSingleSchedule.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge.form' WHERE page_code='CNCLSCH004';


--Cancel Schedule Batch :Spring to Struts compatibility
------------------------------------------------------------
UPDATE pages SET  page_url='/loadCancelSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/cancel_batch_schedule_recharge.form' WHERE page_code='CNSCHTR01';

UPDATE pages SET  page_url='/jsp/restrictedsubs/cancelScheduleBatch.jsp', spring_page_url='/restrictedsubs/cancel_schedule_recharge_batch.form' WHERE page_code='CNSCHTR02';

------------Other Commision--------

ALTER TABLE COMMISSION_PROFILE_SET_VERSION
 ADD column OTH_COMM_PRF_SET_ID  VARCHAR(10 );
 
 ALTER TABLE CHANNEL_TRANSFERS  ADD column OTH_COMM_PRF_SET_ID  VARCHAR(30);
 
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS  ADD column   OTH_COMMISSION_TYPE  VARCHAR(10 );
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS  ADD column   OTH_COMMISSION_RATE numeric(16,4);
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS ADD column OTH_COMMISSION_VALUE   numeric(20);

CREATE TABLE OTHER_COMM_PRF_SET
(
  OTH_COMM_PRF_SET_ID      VARCHAR(10 )    NOT NULL PRIMARY KEY,
  OTH_COMM_PRF_SET_NAME    VARCHAR(40)        NOT NULL UNIQUE,
  OTH_COMM_PRF_TYPE        VARCHAR(10 )    NOT NULL,
  OTH_COMM_PRF_TYPE_VALUE  VARCHAR(10 )    NOT NULL,
  NETWORK_CODE             VARCHAR(2 )     NOT NULL,
  CREATED_ON               DATE                 NOT NULL,
  CREATED_BY               VARCHAR(20 )    NOT NULL,
  MODIFIED_ON              DATE                 NOT NULL,
  MODIFIED_BY              VARCHAR(20),
  STATUS                   VARCHAR(1),
  O2C_CHECK_FLAG           VARCHAR(1 )     DEFAULT 'N',
  C2C_CHECK_FLAG           VARCHAR(1 )     DEFAULT 'N'
);

CREATE TABLE OTHER_COMM_PRF_DETAILS
(
  OTH_COMM_PRF_DETAIL_ID  VARCHAR(10 )  PRIMARY KEY,
  OTH_COMM_PRF_SET_ID     VARCHAR(5 )      NOT NULL,
  START_RANGE             NUMERIC(20),
  END_RANGE               NUMERIC(20),
  OTH_COMMISSION_TYPE     VARCHAR(5 )      NOT NULL,
  OTH_COMMISSION_RATE     NUMERIC(16,4)
);

    ALTER TABLE OTHER_COMM_PRF_DETAILS ADD 
  FOREIGN KEY (OTH_COMM_PRF_SET_ID) 
 REFERENCES OTHER_COMM_PRF_SET (OTH_COMM_PRF_SET_ID) ;


Insert into LOOKUP_TYPES(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED) Values ('OTCTP', 'Other Commission Type', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'N');

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)  Values ('CAT', 'Category Code', 'OTCTP', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values ('GRAD', 'Grade', 'OTCTP', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values('GAT', 'Gateway Code', 'OTCTP', 'Y', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP001', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'Y', 28, '2', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP01A', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMPDMM', 'PROFILES', '/otherCommissionProfileAction.do?method=loadDomainList&page=0', 'Profile management', 'Y', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP005', 'MASTER', '/jsp/profile/selectOtherCommissionProfileSetForView.jsp', 'Other Commission View detail', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP004', 'MASTER', '/jsp/profile/selectOtherCommissionProfileSet.jsp', 'Other Commission modify', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP003', 'MASTER', '/jsp/profile/otherCommissionProfileDetailView.jsp', 'Other Commission detail view', 'N', 28, '1', '1');
Insert into PAGES(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID) Values ('OCOMP002', 'MASTER', '/jsp/profile/setTypeForOtherCommission.jsp', 'Other Commission Profile add', 'N', 28, '1', '1');
 
Insert into ROLES(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE) Values ('OPERATOR', 'OTHCOMPROMGMT', 'Add/Modify Other Commission Profile', 'Profile Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N');

Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP001', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP002', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP003', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP004', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP005', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMP01A', '1');
Insert into PAGE_ROLES(ROLE_CODE, PAGE_CODE, APPLICATION_ID) Values ('OTHCOMPROMGMT', 'OCOMPDMM', '1');
 
Insert into CATEGORY_ROLES(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) Values ('NWADM', 'OTHCOMPROMGMT', '1');


Insert into SYSTEM_PREFERENCES    (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,    MIN_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY,    MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON,     MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE, MAX_VALUE) Values   ('OTH_COM_CHNL', 'Other Commission O2C and C2C', 'SYSTEMPRF', 'BOOLEAN', 'true',     NULL, 5, 'Other commission applicable flag', 'N', 'N',     'C2S', 'Other commission applicable flag', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),     'SU0001', 'true,false', 'Y', NULL);
	

Insert into IDS(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
Values    ('ALL', 'OT_COM_SID', 'ALL', 1, TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'),'NA', 'Commission profile set ID');
Insert into IDS    (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE,     FREQUENCY, DESCRIPTION) 
Values    ('ALL', 'OT_COM_DID', 'ALL', 1, TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS') ,'NA', 'Commission profile detail ID');



CREATE TABLE TPS_DETAILS
(
  TPS_DATE_TIME                    DATE ,
  INSTANCE_CODE                 varchar(3),
  TPS							int,
  TPS_DATE 					DATE  
);

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('MAXTPS', 'C2S', 'ALL', 'TYPE QDATE QHOUR', 'com.btsl.pretups.requesthandler.MaxTPSHandler', 
    'c2s.change.pin', 'MAX TPS DETAILS', 'Y', current_timestamp, 'ADMIN', 
   current_timestamp, 'ADMIN', 'Max TPS Calculation Per Hour', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MAXTPS,MESSAGE', 'TYPE,MSISDN,PIN,QDATE,QHOUR,LANGUAGE1', 
    'Y');
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
 Values
   ('MAXTPSHOURLYREQ', 'EXTGW', '190', 'MAXTPS', 'MAX TPS CALCULATION', 
    'Y', NULL, NULL, NULL, 'Y', 
   current_timestamp, 'SU0001',current_timestamp, 'SU0001', 'SVK9000076', 
    NULL, 'TYPE,QDATE,QHOUR');
	

update pages set menu_item='Y',menu_level=1 where PAGE_CODE in ('O2CBWDR01M','O2CWRAP01M'); 
update pages set menu_level=2 where  PAGE_CODE in ('O2CBWDR01A','O2CWRAP01A');
commit;


-- IRIS Changes Required 
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD Promo_count decimal(20);
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD Promo_amount decimal(24,2);
ALTER TABLE C2S_TRANSFERS add BONUS_AMOUNT decimal(20);
ALTER TABLE USER_DAILY_BALANCES  alter column LAST_TRANSFER_NO type VARCHAR(25); 
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD Promo_count decimal(20);
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD Promo_amount decimal(24,2);


Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('DIFF', 'Additional Bonus', 'COTYP', 'Y', current_timestamp,  'ADMIN', current_timestamp, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('PROMO', 'Promotional Bonus', 'COTYP', 'Y', current_timestamp,
 'ADMIN', current_timestamp, 'ADMIN'); 

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INTCT', 'Y', current_timestamp,  'ADMIN', current_timestamp, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INCAT', 'Y', current_timestamp,  'ADMIN', current_timestamp, 'ADMIN'); 
update SERVICE_TYPE set status='Y',REQUEST_HANDLER='com.btsl.pretups.channel.transfer.requesthandler.SAPVASReversalController' where service_type='RCREV';

commit;


----------------------------View Schedule------------------------
UPDATE pages  SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH001';

UPDATE pages SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCH01A';

UPDATE pages SET page_url='/viewIndvTransfer.do?method=showSingleScheduleAuthorise', spring_page_url='/scheduleTopup/viewScheduleTopUp.form?method=showSingleScheduleAuthorise' WHERE page_code='VIEWSCHDMM';

UPDATE pages SET page_url='/jsp/restrictedsubs/viewSingleScheduleRechargeSts.jsp', spring_page_url='/jsp/restrictedsubs/viewSingleScheduleRecharge.jsp' WHERE page_code='VIEWSCH002';

----------------View Schedule Batch------------------
UPDATE pages SET page_url='/loadViewTransferSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR01';


UPDATE pages SET page_url='/loadViewTransferSchedule.do?method=viewScheduleTrf', spring_page_url='/restrictedsubs/view_schedule_rc_batch.form' WHERE page_code='VWSCHTR1A';


---------------------------------------View Network : Spring to Struts compatibility -------------------
UPDATE pages SET  page_url='/networkViewAction.do?method=loadNetworkListForView&page=0', spring_page_url='/network/network_view_action.form' WHERE page_code='NW3001';


UPDATE pages SET  page_url='/network/viewNetworkDetail.jsp',  spring_page_url='/network/viewNetworkListSpring.form' WHERE page_code='NW3002';


UPDATE pages SET  page_url='/networkViewAction.do?method=loadNetworkListForView&page=0', spring_page_url='/networkViewAction.do?method=loadNetworkListForView&page=0' WHERE page_code='NW3Dmm';

-------------------network status :struts and spring compatibility----------------------

UPDATE pages SET page_url='/networkStatusAction.do?method=loadNetworkStatusList&page=0', spring_page_url='/network/network_Status.form' WHERE page_code='NS001';
UPDATE pages SET page_url='/network/networkStatusView.jsp',spring_page_url='/network/save-network-status.form' WHERE page_code='NS002';

------------------- Load Network self details and C2S Reversal : Spring to Struts compatibility Starts -------------------

UPDATE pages SET page_url='/userOperatorViewAction.do?method=loadSelfDetails',spring_page_url='/user/user_Operator_View_Action.form' WHERE page_code='VIEWUSRS01';
UPDATE pages SET page_url='/networkAction.do?method=loadNetworkListForChange&page=0',spring_page_url='/network/change-network.form' WHERE page_code in ('CNW001','CHNW001','CHNWDmm');
UPDATE pages SET page_url='/c2sreverse.do?method=c2sReversal',spring_page_url='/c2srecharge/reversal.form' WHERE page_code in ('C2SREV001','C2SREVDMM','C2SREV002');
------------------- Load Network self details and C2S Reversal : Spring to Struts compatibility Ends -------------------

-------P2P DWH FIle Creation Info tag
CREATE OR REPLACE FUNCTION p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying)
 RETURNS record
 LANGUAGE plpgsql
AS $function$
DECLARE 

v_srno 	 INT;
v_data	VARCHAR (1000);



  DECLARE P2P_MASTER CURSOR  FOR
			SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||

	case when TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') is null then COALESCE(to_char(PS.LAST_TRANSFER_ON, 'DD/MM/YYYY HH12:MI:SS'), '') else TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') end ||','||
	--case when PS.LAST_TRANSFER_ON is null then '' else PS.LAST_TRANSFER_ON end ||','||

	REPLACE(case when  KV.VALUE is null then '' else  KV.VALUE end,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||
         case when PS.CREDIT_LIMIT is null then '' else PS.CREDIT_LIMIT::varchar(10) end||','||
        PS.REGISTERED_ON||','||case when  PS.LAST_TRANSFER_ID is null then '' else PS.LAST_TRANSFER_ID end||','
       ||  case when PS.LAST_TRANSFER_MSISDN is null then '' else PS.LAST_TRANSFER_MSISDN end||','
        ||PS.LANGUAGE||','||case when PS.COUNTRY is null then '' else PS.COUNTRY end||','
        ||REPLACE(case when PS.USER_NAME is null then '' else PS.USER_NAME end,',',' ')||','
        FROM KEY_VALUES KV right outer join  P2P_SUBSCRIBERS PS on PS.LAST_TRANSFER_STATUS=KV.KEY AND 'P2P_STATUS'=KV.TYPE ,LOOKUPS LK  
        WHERE  date_trunc('day',PS.ACTIVATED_ON::TIMESTAMP) < p_date AND LK.LOOKUP_CODE = PS.STATUS AND LK.LOOKUP_TYPE = 'SSTAT'  AND  PS.STATUS IN('Y','S') ;

 DECLARE P2P_TRANS CURSOR FOR
		SELECT STR.transfer_id||','||STR.transfer_date||','||STR.transfer_date_time||','||TRI1.msisdn||','||TRI2.msisdn||','
		||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
		||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
		||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
		||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
		||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
		||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
		||STR.INFO1||','||STR.INFO2||','||STR.INFO3||','||STR.INFO4||','||STR.INFO5||','
		FROM    KEY_VALUES KV1 right outer join TRANSFER_ITEMS TRI1 on TRI1.transfer_status= KV1.KEY AND KV1.TYPE = 'P2P_STATUS' , 
        KEY_VALUES KV2 right outer join TRANSFER_ITEMS TRI2 on TRI2.transfer_status=KV2.KEY AND KV2.TYPE = 'P2P_STATUS', 
        KEY_VALUES KV right outer join SUBSCRIBER_TRANSFERS STR on STR.transfer_status= KV.KEY AND KV.TYPE = 'P2P_STATUS' 
		WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
		AND TRI2.sno = 2 AND STR.transfer_date = p_date;

		BEGIN
			
	   		RAISE NOTICE '%','Start P2P DWH PROC1';
	   		
			v_srno := 0;
			v_data	:= NULL;

RAISE NOTICE '%','Start P2P DWH PROC..............100';
			DELETE from temp_p2p_dwh_master;
			DELETE from temp_p2p_dwh_trans;
	
			RAISE NOTICE '%','Start P2P DWH PROC..............1';

		   OPEN P2P_MASTER;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 1';
			FETCH  P2P_MASTER INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			 RAISE NOTICE '%','Start P2P DWH PROC..............outside if 1';
			v_srno := v_srno+1; 
			 RAISE NOTICE '%','Start P2P DWH PROC..............before inseert 1';
			INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
			VALUES (v_srno, v_data);
			raise notice '%','i am here:6=';

			IF (MOD(v_srno , 10000) = 0)
			THEN  COMMIT; 
			END IF;
			
		  END LOOP;
		  CLOSE P2P_MASTER;

			p_masterCnt := v_srno;
			RAISE NOTICE '%','p_masterCnt = '||p_masterCnt;
			v_srno := 0;
			v_data	:= NULL;

		   OPEN P2P_TRANS;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 2';
			FETCH P2P_TRANS INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			
			v_srno := v_srno+1;
			INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
			VALUES (v_srno, v_data);

			END LOOP;
			CLOSE P2P_TRANS;

			p_transCnt :=v_srno;
			RAISE NOTICE '%','p_transCnt = '||p_transCnt;

		/* COMMIT; */
		RAISE NOTICE '%','P2P DWH PROC Completed';
		p_message:='SUCCESS';

		EXCEPTION
		WHEN OTHERS THEN
		
			 p_message:='Not able to migrate data, Exception occoured';
	RAISE EXCEPTION 'Not able to migrate data, Exception occoured';


END;
$function$


ALTER TABLE SUBSCRIBER_TRANSFERS ADD VOUCHER_SERIAL_NUMBER VARCHAR(20);
ALTER TABLE SUBSCRIBER_TRANSFERS ADD INFO1 VARCHAR(100),
ADD INFO2 VARCHAR(100),
ADD INFO3 VARCHAR(100),
ADD INFO4 VARCHAR(100),
ADD INFO5 VARCHAR(100);

ALTER TABLE CHANNEL_TRANSFERS ADD INFO3 VARCHAR(100),
ADD INFO4 VARCHAR(100),
ADD INFO5 VARCHAR(100);

--Staff C2C transfer details
UPDATE pages SET spring_page_url='/pretups/staffC2CTransferView.form' WHERE page_code='STFC2CDMM' ;
UPDATE pages SET spring_page_url='/pretups/staffC2CTransferView.form' WHERE page_code='STFC2C00A' ;
UPDATE pages SET spring_page_url='/pretups/staffC2CTransferView.form' WHERE page_code='STFC2C001' ;

--For Fraud Management
ALTER TABLE P2P_SUBSCRIBERS_COUNTERS ADD VPIN_INVALID_COUNT numeric(5) DEFAULT 0;
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VPIN_INVALID_COUNT', 'Invalid Pin Count For Voucher Recharge', 'SYSTEMPRF', 'INT', '5', 0, 10, 50, 'Invalid Pin Count For Voucher Recharge', 'N', 'N', 'P2P', 'Invalid Pin Count For Voucher Recharge', TIMESTAMP '2018-05-29 11:50:25.000000', 'ADMIN', TIMESTAMP '2018-05-29 15:50:25.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PIN_BLK_EXP_DRN', 'Voucher PIN block Expiry Duration', 'SYSTEMPRF', 'NUMBER', '300000', 0, 300000, 50, 'Voucher PIN block Expiry Duration in munutes', 'N', 'N', 'P2P', 'Voucher PIN block Expiry Duration in munutes', TIMESTAMP '2007-02-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', NULL, 'Y');

/*Added three fields in User table starts */
ALTER TABLE USERS ADD DOCUMENT_TYPE VARCHAR(20);
ALTER TABLE USERS ADD DOCUMENT_NO VARCHAR(20);
ALTER TABLE USERS ADD PAYMENT_TYPE VARCHAR(20);

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('DOCTP', 'User Document Type', TO_DATE('10/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('10/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'Y');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PAN', 'PAN', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PASSPORT', 'Passport', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('AADHAR', 'Aadhar No', 'DOCTP', 'Y', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('06/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
/*Added three fields in User table ends */
COMMIT;

ALTER TABLE VOMS_CATEGORIES ALTER COLUMN type TYPE VARCHAR(10);
COMMIT;

--Persian Calendar - Starts --
--Persian Calendar - Starts --
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_FORMAT_CAL_JAVA', 'Date format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd', NULL, NULL, 50, 'Date format accepted by the system', 'Y', 'Y', 'C2S', 'Date format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_TIME_FORMAT', 'Date time format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd HH:mm:ss', NULL, NULL, 50, 'Date Time format accepted by the system', 'Y', 'Y', 'C2S', 'Date Time format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_CALENDAR', 'Locale used for regional Calendar', 'SYSTEMPRF', 'STRING', 'fa_IR@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_ENGLISH', 'Locale used for english Calendar', 'SYSTEMPRF', 'STRING', '@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TIMEZONE_ID', 'Timezone id for Current location', 'SYSTEMPRF', 'STRING', 'Iran', NULL, NULL, 50, 'Timezone id used by the system', 'Y', 'Y', 'C2S', 'Timezone id used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_TYPE', 'Calendar type on GUI', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type to be displayed on GUI', 'Y', 'Y', 'C2S', 'Calendar type to be displayed on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDER_DATE_FORMAT', 'Date format for date selected by Cal on GUI', 'SYSTEMPRF', 'STRING', 'yyyy/mm/dd', NULL, NULL, 50, 'Date format for date selected by Calendar on GUI', 'Y', 'Y', 'C2S', 'Date format for date selected by Calendar on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_SYSTEM', 'Calendar system used in Query', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar system used in Query', 'Y', 'Y', 'C2S', 'Calendar system used in Query', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('FORMAT_MONTH_YEAR', 'Month and year format used', 'SYSTEMPRF', 'STRING', 'yyyy/mm', NULL, NULL, 50, 'Month and year format used', 'Y', 'Y', 'C2S', 'Month and year format used', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('EXTERNAL_CALENDAR_TYPE', 'Calendar type for external gateway', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type for external gateway', 'Y', 'Y', 'C2S', 'Calendar type for external gateway', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_CAL_ICON_VISIBLE', 'Is calendar icon required on GUI', 'SYSTEMPRF', 'STRING', 'Y', NULL, NULL, 50, 'Is calendar icon required on GUI', 'Y', 'Y', 'C2S', 'Is calendar icon required on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_MON_DATE_ON_UI', 'Is date format contains MMM in date format', 'SYSTEMPRF', 'STRING', 'N', NULL, NULL, 50, 'Is date format contains MMM in date format', 'Y', 'Y', 'C2S', 'Is date format contains MMM in date format', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y');

COMMIT;

UPDATE SYSTEM_PREFERENCES SET DEFAULT_VALUE = '0' WHERE PREFERENCE_CODE = 'FINANCIAL_YEAR_START';
COMMIT;
--Persian Calendar - Ends --

CREATE TABLE USER_VOUCHERTYPES
(
  USER_ID       VARCHAR(15)               NOT NULL,
  VOUCHER_TYPE  VARCHAR(10)               NOT NULL,
  STATUS        VARCHAR(1)                DEFAULT 'Y'                   NOT NULL,
  CONSTRAINT PK_USER_VOUCHER_TYPE PRIMARY KEY (USER_ID,VOUCHER_TYPE);
  
)

-- Voucher changes for O2C module - START
ALTER TABLE VOMS_BATCHES 
ADD COLUMN EXT_TXN_NO VARCHAR;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('V', 'VOUCHER', 'TRFT', 'Y', TO_DATE('03/19/2012 11:14:17', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('03/19/2012 11:14:17', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('VOUCHER_THIRDPARTY_STATUS', 'Voucher Status after thirdparty download', 'SYSTEMPRF', 'STRING', 'WH', 
    NULL, NULL, 50, 'Voucher Status after thirdparty download', 'N', 
    'Y', 'C2S', 'Voucher Status after thirdparty download', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/17/2005 09:44:51', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


 Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_VOUCHERTYPE_ALLOWED', 'User voucher type is allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'User voucher type is allowed', 'N', 
    'Y', 'C2S', 'User voucher type is allowed', TO_DATE('06/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2019 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
	
COMMIT;	
-- Voucher changes for O2C module - END

--- Claro Colombia code merge - Begin
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_VOUCHERTYPE_ALLOWED', 'User voucher type is allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'User voucher type is allowed', 'N', 
    'Y', 'C2S', 'User voucher type is allowed', TO_DATE('06/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2019 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
	
Commit;	

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('COMMA_ALLOW_IN_LOGIN', 'Comma space in login id', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Comma space in login id', 'N', 
    'N', 'C2S', 'Comma space in login id', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('XML_DOC_ENCODING', 'XML encoding declaration', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'XML encoding declaration', 'N', 
    'N', 'C2S', 'XML encoding declaration', TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/02/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_EXTERNAL_CODE_DOMAINWISE', ' External Code Domain Based', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'User External Code Domain Based Validation', 'Y', 
    'N', 'C2S', 'User External Code Domain Based Validation', TO_DATE('08/09/2011 04:43:03', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('03/18/2016 15:26:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DISPLAY_COUNTRY', 'Display Country', 'SYSTEMPRF', 'STRING', 'US', 
    NULL, NULL, 50, 'Display Country', 'N', 
    'Y', 'C2S', NULL, TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DISPLAY_LANGUAGE', 'Display Language', 'SYSTEMPRF', 'STRING', 'en', 
    NULL, NULL, 50, 'Display Language', 'N', 
    'Y', 'C2S', NULL, TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/22/2017 13:34:08', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LOAD_BAL_IP_ALLOWED', 'Remote IP validation allow', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'Remote IP validation allow', 'N', 
    'N', 'C2S', 'Remote IP validation allow', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CHK_LAST_TXN_BY_USER_PHONES', 'Check Last Txn by USER_PHONES', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Check Last Txn using USER_PHONES', 'N', 
    'N', 'C2S', 'Check Last Txn using USER_PHONES', TO_DATE('07/13/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/17/2005 16:17:53', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LAST_X_CHNL_TXNSTATUS_ALLOWED', 'Status for last x C2C/O2C', 'SYSTEMPRF', 'STRING', 'CLOSE', 
    1, 10, 5, 'Status allowed for last x C2C/O2C ', 'N', 
    'N', 'C2S', 'Status allowed for last x C2C/O2C', TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'C2S', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LAST_X_C2S_TXNSTATUS_ALLOWED', 'Allowed status for last x C2S', 'SYSTEMPRF', 'STRING', '200', 
    1, 10, 5, 'Allowed status for last x C2S', 'N', 
    'N', 'C2S', 'Allowed status for last x C2S ', TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/28/2017 21:51:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'C2S', 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('MAX_HOST_TEXTBOX', 'Max length of Host Textbox', 'SYSTEMPRF', 'INT', '200', 
    15, 200, 50, 'Max length of Host Text box', 'N', 
    'Y', 'C2S', 'Max length of Host', TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/07/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DEL_CUSER_WITH_BALANCE_ALLOWED', 'Delete user with bal allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Delete Channel user with balance allowed', 'N', 
    'N', 'C2S', 'Delete Channel user with balance allowed', TO_DATE('08/23/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/23/2017 11:51:08', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_CLOSING_BALANCE_REPORT_FROM_AMOUNT', 'CLOSING BALANCERPT FROM_AMOUNT', 'SYSTEMPRF', 'STRING', '0', 
    NULL, NULL, 50, 'Previously it was hardcoded in code. so we make a entry in SystemPreferences for to amount for user closing balance report', 'N', 
    'Y', 'C2S', 'from amount for user closing balance report', TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('CAT_GATEWAY_PRODUCT_PREF', 'CATEGORY GATEWAY PRODUCT', 'SYSTEMPRF', 'STRING', 'RLMW:EXTGW:DEF2,RLMW:WEB:DEF2', 
    NULL, NULL, 50, 'CATEGORY GATEWAY PRODUCT', 'Y', 
    'Y', 'C2S', 'CATEGORY GATEWAY PRODUCT', TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USER_CLOSING_BALANCE_REPORT_TO_AMOUNT', 'CLOSING BALANCERPT TO_AMOUNT', 'SYSTEMPRF', 'STRING', '999999999999999999', 
    NULL, NULL, 50, 'Previously it was hardcoded in code. so we make a entry in SystemPreferences for to amount for user closing balance report', 'N', 
    'Y', 'C2S', 'to amount for user closing balance report', TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/28/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('WIRC_ACCOUNT_MSISDN_OPT', 'ACCOUNTID OR MSISDN REQUIRED', 'SYSTEMPRF', 'STRING', 'ACCOUNTID', 
    NULL, NULL, 50, 'CATEGORY GATEWAY PRODUCT', 'Y', 
    'Y', 'C2S', 'ACCOUNTID OR MSISDN ', TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/23/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;
--- Claro Colombia code merge - End

Insert into USER_VOUCHERTYPES
   (USER_ID, VOUCHER_TYPE, STATUS)
 Values
   ('SU0001', 'eletronic', 'Y');
Insert into USER_VOUCHERTYPES
   (USER_ID, VOUCHER_TYPE, STATUS)
 Values
   ('SU0001', 'physical', 'Y');
COMMIT;

---Mapping correct C2S Reversal Controller starts ------
UPDATE SERVICE_TYPE
SET  REQUEST_HANDLER='com.btsl.pretups.channel.transfer.requesthandler.C2SPrepaidReversalController'
WHERE SERVICE_TYPE='RCREV' AND MODULE='C2S'AND TYPE='PRE' AND NAME='Customer Recharge Reversal';
COMMIT;
---Mapping correct C2S Reversal Controller ends ------

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VOMS_DAMG_PIN_LNTH_ALLOW', 'Minimum pin length for Damage Voucher', 'SYSTEMPRF', 'INT', '3', 
    0, 10, 50, 'Minimum pin length for Damage Voucher', 'Y', 
    'N', 'C2S', 'Minimum pin length for Damage Voucher', TO_DATE('05/29/2018 11:50:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/29/2018 15:50:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;


INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'VCDETAILREPORT', '1'
            );


INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVC001', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'Y', 581, '2', '1',
             '/VCReport.do?method=detailVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVCDMM', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'Y', 581, '1', '1',
             '/VCReport.do?method=detailVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMVC01A', 'VOMSREPORT', '/VCReport.do?method=detailVCReport',
             'VC Detail Report', 'N', 581, '2', '1',
             '/VCReport.do?method=detailVCReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVC001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVCDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCDETAILREPORT', 'VOMVC01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'VCDETAILREPORT', 'VC Detail Report',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );
COMMIT;		
	
---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	starts ---------
UPDATE ROLES SET STATUS = 'N' WHERE ROLE_CODE IN ('IATBATCHIDRPT','IATBULKASCRPT','IATBULKRESRPT','IATBULKAPP','IATBTCHVIEW','IATSCHVIEW','IATSCHRPTSTS','IATBATCHIDRPT','IATBULKREG','IATBULKVIEW','IATBULKSUSPND','IATBULKRES','IATSCHDRC','IATBULKASC','IATBULKDEASC','IATBULKASCRPT','IATBULKRESRPT','IATSCHRPTSTS','IATCNCLSCH','IATCNCBTCH','IATRESCHD','IATBULKDEL','IATCNTRYMGMTAD','IATCNTRYMGMTMO','IATNWMGMTAD','IATNWMGMTMO','IATTRSERPT','IATTRFSUMRPT','IATTRFANSFERENQ','C2SSLABWISE','NORECHARGESLABWISE','NETWORKSUMRPT','RPTSCHDC2SRPT','RPTSCHDUDRRPT','RPTSCHDP2PRPT','DOWNLOADFILES');

COMMIT;
---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	ends ---------


-----Sold voucher impact changes - Tejeshvi ---------------
alter table VOMS_VOUCHERS add SOLD_STATUS VARCHAR(1) default 'N' not null;
alter table VOMS_VOUCHERS add SOLD_DATE DATE;

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('SL', 'Sold', 'VSTAT', 'Y', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN');

COMMIT;
-----Sold voucher impact changes - Tejeshvi ---------------

-------------IFGO TAG FOR LMB REQUEST--
ALTER TABLE SOS_TRANSACTION_DETAILS ADD COLUMN INFO1 varchar(100),ADD COLUMN INFO2 varchar(100),ADD COLUMN INFO3 varchar(100),ADD COLUMN INFO4 varchar(100),ADD COLUMN INFO5 varchar(100);
COMMIT;
-------------IFGO TAG FOR LMB REQUEST--


INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'SOLDVCREPORT', '1'
            );			
			

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVC001', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'Y', 582, '2', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVCDMM', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'Y', 582, '1', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('SOLDVC01A', 'VOMSREPORT', '/soldVCCard.do?method=loadsoldVCReport',
             'Detail of Sold VC Card', 'N', 582, '2', '1',
             '/soldVCCard.do?method=loadsoldVCReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVC001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVCDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('SOLDVCREPORT', 'SOLDVC01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'SOLDVCREPORT', 'Detail of Sold VC Card',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );
COMMIT;	

########### Voucher Delivery History #######
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLHDMM', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'Y', 
    8, '1', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLH01A', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'N', 
    8, '2', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMDLH001', 'VOMSREPORT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Voucher Delivery History', 'Y', 
    8, '2', '1', NULL);

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLH001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLH01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSDLHREPORT', 'VOMDLHDMM', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSDLHREPORT', 'Voucher Delivery History', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'VOMSDLHREPORT', '1');

COMMIT;

########Voucher sold summary Report ################

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSM001', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'Y', 
    9, '2', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSM01A', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'N', 
    9, '2', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOMSLSMDMM', 'VOMSREPORT', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport', 'Voucher Sold Summary', 'Y', 
    9, '1', '1', '/voucherSoldSummary.do?method=loadVoucherSoldSummaryReport');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSM001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSM01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSLSUMMREPORT', 'VOMSLSMDMM', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSLSUMMREPORT', 'Voucher Sold Summary', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'VOMSLSUMMREPORT', '1');

COMMIT;


######### LOOK Up ENTRY - Voucher sold ##############

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('VSLTYPE', 'Voucher Sold Summary', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CITY', 'City', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SLDATE', 'Sold Date', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CHANNEL', 'Channel', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('BANK', 'Bank', 'VSLTYPE', 'Y', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

/* SET DEFINE OFF; */
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('TTYPE', 'Terminal Type', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;


/* SET DEFINE OFF; */
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('02', 'ATM', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('03', 'Branchs Terminal', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('05', 'SMS', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('07', 'Telephone Bank', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('13', 'Web Kiosk', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('14', 'POS', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('59', 'Internet', 'TTYPE', 'Y', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/08/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
COMMIT;

/* SET DEFINE OFF; */
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('SALESRPT', TO_DATE('06/06/2018 00:33:18', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('06/06/2018 06:14:32', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('06/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 0, 'Rightel Sales Daily Report', 'IR', 0);
COMMIT;


CREATE TABLE VOMS_DAILY_REPORT_DETAILS
(
  BATCH_ID           VARCHAR(20)          NOT NULL,
  BRANCH_CODE        VARCHAR(9),
  CITY_CODE          VARCHAR(7),
  TERMINAL_TYPE      VARCHAR(3),
  TERMINAL_CODE      VARCHAR(9),
  CREDIT_SALE_DATE   TIMESTAMP(0),
  SERIAL_NO          VARCHAR(16),
  SERIAL_NO_CDIGIT   VARCHAR(2),
  PAYMENT_ID_NUMBER  VARCHAR(12),
  PAYMENT_TYPE       VARCHAR(2),
  TXN_ID             BIGINT,
  CARD_NUMBER        DECIMAL(20),
  CREATED_BY         VARCHAR(20),
  CREATED_ON         TIMESTAMP(0),
  MODIFIED_BY        VARCHAR(20),
  MODIFIED_ON        TIMESTAMP(0),
  STATUS             VARCHAR(10),
  REMARKS            VARCHAR(100),
  EXTERNAL_CODE      VARCHAR(20),
  USER_ID            VARCHAR(20),
  VOUCHER_STATUS     VARCHAR(10),
  PRODUCT_ID         VARCHAR(5),
  DELIVER_DATE       TIMESTAMP(0)
);

CREATE TABLE VOMS_DAILY_REPORT_MASTER
(
  BATCH_ID          VARCHAR(20)           NOT NULL,
  USER_ID           VARCHAR(15)           NOT NULL,
  NETWORK_CODE      VARCHAR(2),
  NETWORK_CODE_FOR  VARCHAR(2),
  COMPANY_NAME      VARCHAR(2),
  OPERATOR_CODE     VARCHAR(4),
  BANK_CODE         VARCHAR(3),
  BATCH_FILE_NAME   VARCHAR(100),
  TOTAL_RECORD      BIGINT,
  TOTAL_AMOUNT      BIGINT,
  BATCH_DATE        TIMESTAMP(0),
  CREATED_BY        VARCHAR(20),
  CREATED_ON        TIMESTAMP(0),
  MODIFIED_BY       VARCHAR(20),
  MODIFIED_ON       TIMESTAMP(0)
);

--This to be true only if external voucher uploaded in system
UPDATE SYSTEM_PREFERENCES  SET DEFAULT_VALUE='false' WHERE PREFERENCE_CODE='HASHING_ENABLE';
COMMIT;



----Date format for API's----------
UPDATE SYSTEM_PREFERENCES
SET DEFAULT_VALUE='yyyy/MM/dd'
WHERE PREFERENCE_CODE='EXTERNAL_DATE_FORMAT';
---------------------------------------------------


INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('PIN_REQUIRED_P2P', 'PIN Required for P2P', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', 'Y', 'Y', 'P2P', 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', '2010-07-28 00:00:00.000', 'ADMIN', '2010-07-28 00:00:00.000', 'ADMIN', NULL, 'Y');
COMMIT;

ALTER TABLE SUBSCRIBER_CONTROL ADD VPIN_INVALID_COUNT numeric(5) DEFAULT 0;
COMMIT;

ALTER TABLE
	SOS_SETTLEMENT_FAIL_RECCORDS DROP
		CONSTRAINT PK_SOS_SETTLE_FAIL_REC;
		
		

--------Batch user initiate------
ALTER TABLE BATCHES MODIFY FILE_NAME VARCHAR2(35);
COMMIT;
------------------------------------		

--##########################################################################################################
--##
--##      PreTUPS_v7.4.0 DB Script
--##
--##########################################################################################################
--Alter table TRANSFER_RULES for column CELL_GROUP_ID for Location based promotion support for vouchers
ALTER TABLE TRANSFER_RULES ADD CELL_GROUP_ID VARCHAR(10) ;

CREATE TABLE CHANNEL_VOUCHER_ITEMS (
	TRANSFER_ID 		VARCHAR(20) 	NOT NULL,
	TRANSFER_DATE 		DATE 			NOT NULL,
	VOUCHER_TYPE 		VARCHAR(15) 	NOT NULL,
	PRODUCT_ID 			VARCHAR(15),
	MRP 				NUMERIC(10,0) 	NOT NULL,
	REQUESTED_QUANTITY	NUMERIC(20,0) 	NOT NULL,
	FROM_SERIAL_NO 		VARCHAR(16),
	TO_SERIAL_NO 		VARCHAR(16)
);

--Release 7.4 : Voucher Order Request  Changes Starts----
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQI001', 'VOMSOREQ', '/jsp/voucherOrderRequest/selectDistType.jsp', 'Voucher Request Order Initiate', 'N', 40, '2', '1', '/jsp/voucherOrderRequest/selectDistType.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQI002', 'VOMSOREQ', '/jsp/voucherOrderRequest/voucherOrderReqDetails.jsp', 'Voucher Request Order Initiate', 'N', 40, '2', '1', '/jsp/voucherOrderRequest/voucherOrderReqDetails.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQI003', 'VOMSOREQ', '/jsp/voucherOrderRequest/voucherOrderReqProductDetails.jsp', 'Voucher Request Order Initiate', 'N', 40, '2', '1', '/jsp/voucherOrderRequest/voucherOrderReqProductDetails.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQI004', 'VOMSOREQ', '/jsp/voucherOrderRequest/voucherOrderReqProductDetailsConfirm.jsp', 'Voucher Request Order Initiate', 'N', 40, '2', '1', '/jsp/voucherOrderRequest/voucherOrderReqProductDetailsConfirm.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQO001', 'VOMSOREQ', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder', 'Voucher Request Order', 'Y', 40, '2', '1', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQODMM', 'VOMSOREQ', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder', 'Voucher Request Order', 'Y', 40, '1', '1', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOMSRQO01A', 'VOMSOREQ', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder', 'Voucher Request Order', 'N', 40, '2', '1', '/voucherOrderReqDistType.do?method=channelUserInitiatedOrder');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQO01A', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQODMM', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQO001', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQI001', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQI002', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQI003', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('INITVOMSOREQ', 'VOMSRQI004', '1');

INSERT INTO MODULES
(MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
VALUES('VOMSOREQ', 'Voucher Order Request', 40, '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'INITVOMSOREQ', '1');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'INITVOMSOREQ', 'Voucher Order Request Initiate', 'Voucher Order Request', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

--Release 7.4 : Voucher Order Request  Changes ends----

---- Release 7.4 : Download Vouchers for Voucher Order Requests starts
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'DOWNVOMS', 'Voms voucher download', 'Voucher Download', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'DOWNVOMS', '1');

---- Release 7.4 : Download Vouchers for Voucher Order Requests ends

/* Payment Gateway - Tejeshvi*/
CREATE TABLE CHANNEL_TRANSFER_PAYMENTS (
	TRANSFER_ID 		VARCHAR(15) 	NOT NULL,
	PAYMENT_ID 			VARCHAR(15) 	NOT NULL,
	TRANSFER_DATE 		DATE 			NOT NULL,
	TRANSFER_DATE_TIME	DATE			NOT NULL,
	PAYMENT_STATUS 		VARCHAR(10) 	NOT NULL,
	PAYMENT_AMOUNT		NUMERIC(20)	 	NOT NULL
);
/* Payment Gateway - Tejeshvi*/

ALTER TABLE CHANNEL_TRANSFERS ADD PMT_INST_STATUS VARCHAR(15) DEFAULT 'NA' NOT NULL ;


/*  Added Product Type in Look Up table to give flexibility in case of Voucher Order Requests starts**/
INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('VMSPT', 'VMS Product Type', TIMESTAMP '2016-03-30 15:50:34.000000', 'ADMIN', TIMESTAMP '2016-03-30 15:50:34.000000', 'ADMIN', 'N');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PREPROD', 'Prepaid', 'VMSPT', 'Y', TIMESTAMP '2007-02-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-02-14 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('POSTPROD', 'Postpaid', 'VMSPT', 'Y', TIMESTAMP '2007-02-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-02-14 00:00:00.000000', 'ADMIN');
/*  Added Product Type in Look Up table to give flexibility in case of Voucher Order Requests ends **/

ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_BY VARCHAR(15);
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_DATE DATE;
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_FLAG VARCHAR(1);     
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_REMARK VARCHAR(50);




/* add p2p promotional transfer rule entries */

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'ADDP2PPROMTRFRULE', 'Add p2p promotnl transfer rule', 'P2P Promotional transfer rule', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');
	
	
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRADDMM', 'P2PPRTRFRL', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel', 'Add p2p promotnl transfer rule', 'Y', 
    1, '1', '1', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRAD001', 'P2PPRTRFRL', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel', 'Add p2p promotnl transfer rule', 'Y', 
    1, '2', '1', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRAD01A', 'P2PPRTRFRL', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel', 'Add p2p promotnl transfer rule', 'N', 
    1, '2', '1', '/selectP2pPromotionalLevelAdd.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRAD002', 'P2PPRTRFRL', '/jsp/transferrules/addP2pPromotionalTransferRule.jsp', 'Add p2p promotnl transfer rule', 'N', 
    1, '2', '1', '/jsp/transferrules/addP2pPromotionalTransferRule.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRAD003', 'P2PPRTRFRL', '/jsp/transferrules/confirmAddP2pPromoTransferRuleDetails.jsp', 'Add p2p promotnl transfer rule', 'N', 
    1, '2', '1', '/jsp/transferrules/confirmAddP2pPromoTransferRuleDetails.jsp');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('ADDP2PPROMTRFRULE', 'P2PPRAD001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('ADDP2PPROMTRFRULE', 'P2PPRAD002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('ADDP2PPROMTRFRULE', 'P2PPRAD003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('ADDP2PPROMTRFRULE', 'P2PPRAD01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('ADDP2PPROMTRFRULE', 'P2PPRADDMM', '1');


Insert into MODULES
   (MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
 Values
   ('P2PPRTRFRL', 'P2P Promotional transfer rule', 389, '1');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'ADDP2PPROMTRFRULE', '1');
   

Insert into GROUP_ROLES
   (GROUP_ROLE_CODE, ROLE_CODE)
 Values
   ('NWADM', 'ADDP2PPROMTRFRULE'); 


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('P2P_PRO_TRF_ST_LVL_CODE', 'P2P Promotional trf start level', 'NETWORKPRF', 'INT', '1', 
    NULL, NULL, 50, 'P2P Promotional transfer rule start level. 1 for subsscriber, 2 for Cell id', 'Y', 
    'Y', 'P2P', 'P2P Promotional transfer rule start level. 1 for subsscriber, 2 for Cell id', TO_DATE('10/10/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('10/10/2018 15:03:14', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', '1,2', 'Y');
    
    Insert into NETWORK_PREFERENCES
   (NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'P2P_PRO_TRF_ST_LVL_CODE', '1', TO_DATE('02/13/2012 13:41:21', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000000002', 
    TO_DATE('02/13/2012 13:41:55', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000000002');


Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('P2PPROMO', 'P2P Promotional Level', TO_DATE('02/14/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/02/2007 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
   
   
    Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CEL', 'Cell Group', 'P2PPROMO', 'Y', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SUB', 'Subscriber', 'P2PPROMO', 'Y', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');




/* modify p2p promotional transfer rule entries */


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'MODP2PPROMTRFRULE', 'Mod p2p promotnl transfer rule', 'P2P Promotional transfer rule', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');




Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD01A', 'P2PPRTRFRL', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel', 'Mod p2p promotnl transfer rule', 'N', 
    2, '2', '1', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMDDMM', 'P2PPRTRFRL', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel', 'Mod p2p promotnl transfer rule', 'Y', 
    2, '1', '1', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD001', 'P2PPRTRFRL', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel', 'Mod p2p promotnl transfer rule', 'Y', 
    2, '2', '1', '/selectP2pPromotionalLevelModify.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD002', 'P2PPRTRFRL', '/jsp/transferrules/searchP2pPromotionalRuleDetailsModify.jsp', 'Mod p2p promotnl transfer rule', 'N', 
    2, '2', '1', '/jsp/transferrules/searchP2pPromotionalRuleDetailsModify.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD003', 'P2PPRTRFRL', '/jsp/transferrules/selectP2pPromotionalTransferRules.jsp', 'Mod p2p promotnl transfer rule', 'N', 
    2, '2', '1', '/jsp/transferrules/selectP2pPromotionalTransferRules.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD004', 'P2PPRTRFRL', '/jsp/transferrules/modifyP2pPromotionalTransferRules.jsp', 'Mod p2p promotnl transfer rule', 'N', 
    2, '2', '1', '/jsp/transferrules/modifyP2pPromotionalTransferRules.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRMD005', 'P2PPRTRFRL', '/jsp/transferrules/confirmModifyP2pPromotionalTransferRules.jsp', 'Mod p2p promotnl transfer rule', 'N', 
    2, '2', '1', '/jsp/transferrules/confirmModifyP2pPromotionalTransferRules.jsp');





Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD004', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD005', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMD01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('MODP2PPROMTRFRULE', 'P2PPRMDDMM', '1');



Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'MODP2PPROMTRFRULE', '1');
	
	
	
	
	
		/* view p2p promotional transfer rule entries */
	
	
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRV01A', 'P2PPRTRFRL', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel', 'View p2p promtnl transfer rule', 'N', 
    3, '2', '1', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRVDMM', 'P2PPRTRFRL', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel', 'View p2p promtnl transfer rule', 'Y', 
    3, '1', '1', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRV001', 'P2PPRTRFRL', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel', 'View p2p promtnl transfer rule', 'Y', 
    3, '2', '1', '/selectP2pPromotionalLevelView.do?method=loadPromotionalLevel');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRV002', 'P2PPRTRFRL', '/jsp/transferrules/searchP2pPromotionalRuleDetailsView.jsp', 'View p2p promtnl transfer rule', 'N', 
    3, '2', '1', '/jsp/transferrules/searchP2pPromotionalRuleDetailsView.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('P2PPRV003', 'P2PPRTRFRL', '/jsp/transferrules/viewP2pPromotionalTransferRulesView.jsp', 'View p2p promtnl transfer rule', 'N', 
    3, '2', '1', '/jsp/transferrules/viewP2pPromotionalTransferRulesView.jsp');




Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VWP2PPROMTRFRULE', 'P2PPRV001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VWP2PPROMTRFRULE', 'P2PPRV002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VWP2PPROMTRFRULE', 'P2PPRV003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VWP2PPROMTRFRULE', 'P2PPRV01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VWP2PPROMTRFRULE', 'P2PPRVDMM', '1');



Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'VWP2PPROMTRFRULE', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VWP2PPROMTRFRULE', 'View p2p promotnl transfer rule', 'P2P Promotional transfer rule', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');



Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
Values
   ('CHCGENREQ', 'C2S', 'PRE', 'TYPE MSISDN1 PIN SERVICETYPE', 'com.btsl.pretups.requesthandler.ChannelUserCGEnquiryRequestHandler', 
    'c2s.ChannelUserCGEnquiryRequestHandler', 'ChannelUser Card Group Enquiry', 'Y', TO_DATE('12/30/2015 18:19:39', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/30/2015 18:19:39', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'ChannelUser Card Group Enquiry', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE MSISDN1 PIN SERVICETYPE', 'TYPE MSISDN1 PIN SERVICETYPE', 
    'Y');


/* O2C Reconciliation entries */
Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) 
    Values('NWADM', 'RECO2CTRF', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
Values
   ('OPERATOR', 'RECO2CTRF', 'O2C reconciliation', 'Reconciliation', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC101', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange1', ' O2C reconciliation', 'Y', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC101A', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange1', ' O2C reconciliation', 'N', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC104', 'RECONCIL', '/jsp/channeltransfer/transferApprovalListLevelOne.jsp', ' O2C reconciliation', 'N', 
    10, '2', '1', '/jsp/channeltransfer/transferApprovalListLevelOne.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC106', 'RECONCIL', '/jsp/channeltransfer/transferDetailApprovalLevelOne.jsp', ' O2C reconciliation', 'N', 
    10, '2', '1', '/jsp/channeltransfer/transferDetailApprovalLevelOne.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC1DMM', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange1', ' O2C reconciliation', 'Y', 
    10, '1', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC101', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC101A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC104', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC106', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC1DMM', '1');




/* Payment gateway entries */
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('PG_INTEFRATION_ALLOWED', 'PG Integration Allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 50, 'PG Integration Allowed', 'N', 
    'Y', 'C2S', 'PG Integration Allowed', TO_DATE('04/21/2008 14:03:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('04/21/2008 14:03:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');



INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PENDING', 'Pending', 'TSTAT', 'Y', TIMESTAMP '2005-08-18 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-18 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ONLINE', 'Online', 'PMTYP', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');



Insert into INTERFACE_TYPES
   (INTERFACE_TYPE_ID, INTERFACE_NAME, INTERFACE_CATEGORY, HANDLER_CLASS, UNDERPROCESS_MSG_REQD, 
    MAX_NODES, URI_REQ)
Values
   ('PG01', 'Payment Gateway 01', 'PG', 'com.inter.pg.PaymentGatewayHandler', 'N', 
    1, 'Y');
Insert into INTERFACE_TYPES
   (INTERFACE_TYPE_ID, INTERFACE_NAME, INTERFACE_CATEGORY, HANDLER_CLASS, UNDERPROCESS_MSG_REQD, 
    MAX_NODES, URI_REQ)
Values
   ('PG02', 'Payment Gateway 02', 'PG', 'com.inter.pg.PaymentGatewayHandler', 'N', 
    1, 'Y');
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
Values
   ('PGTYP', 'Payment Gateway Name', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIn', TO_DATE('11/08/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('PG01', 'Payment Gateway 1', 'PGTYP', 'Y', TO_DATE('08/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/18/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
Values
   ('PG02', 'Payment Gateway 2', 'PGTYP', 'Y', TO_DATE('08/04/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('08/04/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('O2CREC105', 'RECONCIL', '/jsp/channeltransfer/viewTransferApproval.jsp', ' O2C reconciliation', 'N', 
    10, '2', '1', '/jsp/channeltransfer/viewTransferApproval.jsp');
    
    Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('RECO2CTRF', 'O2CREC105', '1');
   COMMIT:
   
   /* O2C Reconciliation report entries */

Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) 
    Values('NWADM', 'PRERECO2CTRF', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
Values
   ('OPERATOR', 'PRERECO2CTRF', 'O2C reconciliation report', 'Reconciliation', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('PREREC101', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange2', 'O2C reconciliation report', 'Y', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('PREREC101A', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange2', 'O2C reconciliation report', 'N', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('PREREC1DMM', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange2', 'O2C reconciliation report', 'Y', 
    10, '1', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('PRERECO2CTRF', 'PREREC101', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('PRERECO2CTRF', 'PREREC101A', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('PRERECO2CTRF', 'PREREC1DMM', '1');




/* O2C Reconciled report entries */

Insert into CATEGORY_ROLES (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID) 
    Values('NWADM', 'POSRECO2CTRF', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
Values
   ('OPERATOR', 'POSRECO2CTRF', 'O2C reconciled report', 'Reconciliation', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('POSREC101', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange3', 'O2C reconciled report', 'Y', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('POSREC101A', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange3', 'O2C reconciled report', 'N', 
    10, '2', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
Values
   ('POSREC1DMM', 'RECONCIL', '/o2cReconciliation.do?method=selectDateRange3', 'O2C reconciled report', 'Y', 
    10, '1', '1', '/transferApprovalDomainOne.do?method=searchDomainLevelOne');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('POSRECO2CTRF', 'POSREC101', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('POSRECO2CTRF', 'POSREC101A', '1');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
Values
   ('POSRECO2CTRF', 'POSREC1DMM', '1');





Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SERVICES_ALLOWED_SHOW_CARDGROUPLIST', 'secvices allowed to show cardgrouplist', 'SYSTEMPRF', 'STRING', 'ABC', 
    NULL, NULL, 50, 'Service Type Name allowed to show cardgrouplist on C2S webpage', 'Y', 
    'Y', 'C2S', 'Comma separated service type name for invoice no in C2S', TO_DATE('12/17/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/17/2013 07:39:23', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');

SET DEFINE OFF;
update system_preferences set default_value ='true' where preference_code='REC_MSG_SEND_ALLOW';
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LAST_C2C_ENQ_MSG_REQ', 'Last C2C Enq msg required', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Check whetherLast C2C Enq msg required for receiver', 'N', 
    'Y', 'C2S', 'Check whetherLast C2C Enq msg required for receiver', TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('06/16/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USSD_REC_MSG_SEND_ALLOW', 'USSD Receiver message allow', 'SERTYPPREF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Service type wise receiver USSD message allow flag.', 'Y', 
    'Y', 'C2S', 'Service type wise receiver USSD message allow flag.', TO_DATE('06/18/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('10/09/2008 11:13:41', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');



SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('C2S_SEQ_ALWD', 'C2S Sequence Allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Flag to Enable/Disable the Sequence Generation', 'Y', 
    'Y', 'C2S', 'Flag to Enable/Disable the Sequence Generation', TO_DATE('11/04/2015 23:52:13', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('11/04/2015 23:52:13', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('C2S_SEQID_FOR_GWC', 'Gateway Codes for Sequence ID', 'SYSTEMPRF', 'STRING', '', 
    NULL, NULL, 50, 'List of comma separated Gateway Codes For Which the Sequence ID is applied', 'Y', 
    'Y', 'C2S', 'Gateway Code For Which the Sequence ID is applied', TO_DATE('11/04/2015 23:52:18', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/01/2017 16:17:45', 'MM/DD/YYYY HH24:MI:SS'), 'CPSU0000094155', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('C2S_SEQID_APPL_SER', 'Services for Seq ID Generation', 'SYSTEMPRF', 'STRING', '', 
    NULL, NULL, 50, 'List of comma separated Service Codes for Which Generation of Seq ID is applicable', 'Y', 
    'Y', 'C2S', 'List of comma separated Service Codes for Which Generation of Seq ID is applicable', TO_DATE('11/04/2015 23:52:24', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('11/04/2015 23:52:24', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'BPB,DC,DTH,FLRC,PIN,PMD,RC', 'Y');


SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('ADDITIONAL_IN_FIELDS_ALLOWED', 'ADDITIONAL IN FIELDS ALLOWED', 'SYSTEMPRF', 'STRING', '', 
    NULL, NULL, 50, 'ADDITIONAL IN FIELDS ALLOWED', 'N', 
    'N', 'P2P', 'ADDITIONAL IN FIELDS ALLOWED', TO_DATE('12/01/2017 00:58:06', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('12/01/2017 00:58:06', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CHNLUSR_VOUCHER_CATGRY_ALLWD', 'Chnl User Voucher Allwed Categories', 'CATPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Chnl User Voucher Allwed Categories', 'Y', 'Y', 'C2S', 'Chnl User Voucher Allwed Categories', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'false,true', 'N');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', 'CATPRF');


	
ALTER TABLE COMMISSION_PROFILE_PRODUCTS
ADD payment_mode VARCHAR(10) DEFAULT 'ALL';

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('PMTMD', 'Payment Mode', TIMESTAMP '2005-06-10 17:24:56.000000', 'ADMIN', TIMESTAMP '2005-06-10 17:24:56.000000', 'ADMIN', 'N');



INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('DD', 'Demand Draft', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CHQ', 'Cheque', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('OTH', 'Others', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CASH', 'Cash', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ONLINE', 'Online', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ALL', 'DEFAULT', 'PMTMD', 'Y', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-04 00:00:00.000000', 'ADMIN');






ALTER TABLE COMMISSION_PROFILE_PRODUCTS
   ALTER COLUMN PAYMENT_MODE  SET NOT NULL;

ALTER TABLE COMMISSION_PROFILE_PRODUCTS
ADD CONSTRAINT UK_COMMISSION_PROFILE_PRODUCTS UNIQUE (COMM_PROFILE_SET_ID, PRODUCT_CODE, PAYMENT_MODE, COMM_PROFILE_SET_VERSION);

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('PAYMENT_MODE_ALWD', 'Payment Mode allowed', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Flag to Enable/Disable Payment Mode allowed', 'Y', 
    'Y', 'C2S', 'Flag to Enable/Disable Payment Mode allowed', TO_DATE('01/24/2637 23:52:13', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('01/24/2637 23:52:13', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('O2CAMB_MINUTES_DELAY', 'Delay in ambiguous transaction for O2C Recon', 'SYSTEMPRF', 'INT', '-5', -100, 60, 50, 'Delay in ambiguous transaction for O2C Recon', 'N', 'Y', 'C2S', 'Delay in ambiguous transaction for O2C Recon', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');

UPDATE SYSTEM_PREFERENCES
SET default_value = 'yyyy/MM/dd HH24:mi:ss' where preference_code = 'DATE_TIME_FORMAT'



ALTER TABLE CHANNEL_VOUCHER_ITEMS ADD S_NO numeric(22) NOT NULL;


ALTER TABLE VOMS_VOUCHERS
ADD column VOUCHER_TYPE VARCHAR(12);

ALTER TABLE VOMS_VOUCHERS
ADD column CONSUMED_GATEWAY_TYPE  VARCHAR(12);

ALTER TABLE VOMS_VOUCHERS
add column CONSUMED_GATEWAY_CODE  VARCHAR(12);


CREATE TABLE VOMS_VOUCHER_DAILY_SUMMARY
(
  SUMMARY_DATE             DATE,
  PRODUCT_ID               VARCHAR(15),
  VOUCHER_TYPE             VARCHAR(15),
  DENOMINATION             NUMERIC(16)           DEFAULT 0,
  PRODUCTION_NETWORK_CODE  VARCHAR(2),
  USER_NETWORK_CODE        VARCHAR(2),
  TOTAL_GENERATED          NUMERIC(16)           DEFAULT 0,
  TOTAL_ENABLED            NUMERIC(16)           DEFAULT 0,
  TOTAL_STOLEN             NUMERIC(16)           DEFAULT 0,
  TOTAL_ON_HOLD            NUMERIC(16)           DEFAULT 0,
  TOTAL_DAMAGED            NUMERIC(16)           DEFAULT 0,
  OTHER_STATUS             NUMERIC(16)           DEFAULT 0,
  TOTAL_CONSUMED           NUMERIC(16)           DEFAULT 0,
  TOTAL_WAREHOUSE          NUMERIC(16)           DEFAULT 0,
  TOTAL_PRINTING           NUMERIC(16)           DEFAULT 0,
  TOTAL_SUSPENDED          NUMERIC(16)           DEFAULT 0,
  TOTAL_EXPIRED            NUMERIC(16)  			DEFAULT 0
);

Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('VOUCHERDASUM', TO_DATE('08/31/2018 15:45:05', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('08/30/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('08/31/2018 15:45:04', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'VOMS Summary process', 'NG', 0);

	
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('VMS_ALLOW_CONTENT_TYPE', 'VMS Allowed Content Type', 'SYSTEMPRF', 'STRING', 'pdf,doc,docx', 
    NULL, NULL, 50, 'VMS Allowed Content Type', 'N', 
    'N', 'P2P', 'VMS Allowed Content Type', TO_DATE('02/20/2639 00:58:06', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/20/2639 00:58:06', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

	



/* Rightel reports db queries added */

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTRC001', 'VOMSREPORT', '/statRecharge.do?method=statRechargeReport',
             'Statistics of Recharge', 'Y', 591, '2', '1',
             '/statRecharge.do?method=statRechargeReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTRCDMM', 'VOMSREPORT', '/statRecharge.do?method=statRechargeReport',
             'Statistics of Recharge', 'Y', 591, '1', '1',
             '/statRecharge.do?method=statRechargeReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTRC01A', 'VOMSREPORT', '/statRecharge.do?method=statRechargeReport',
             'Statistics of Recharge', 'N', 591, '2', '1',
             '/statRecharge.do?method=statRechargeReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATRCHREPORT', 'VOMSTRC001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATRCHREPORT', 'VOMSTRCDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATRCHREPORT', 'VOMSTRC01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'VCSTATRCHREPORT', 'Statistics of Recharge',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );
			
			
INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'VCSTATSRVREPORT', '1'
            );

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTSR001', 'VOMSREPORT', '/statService.do?method=statServiceReport',
             'Statistics of Service', 'Y', 593, '2', '1',
             '/statService.do?method=statServiceReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTSRDMM', 'VOMSREPORT', '/statService.do?method=statServiceReport',
             'Statistics of Service', 'Y', 593, '1', '1',
             '/statService.do?method=statServiceReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('VOMSTSR01A', 'VOMSREPORT', '/statService.do?method=statServiceReport',
             'Statistics of Service', 'N', 593, '2', '1',
             '/statService.do?method=statServiceReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATSRVREPORT', 'VOMSTSR001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATSRVREPORT', 'VOMSTSRDMM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('VCSTATSRVREPORT', 'VOMSTSR01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'VCSTATSRVREPORT', 'Statistics of Service',
             'Voucher Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );	




 INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'ETOPC2SREPORT', '1'
            );

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPC2S001', 'ETOPUPRPT', '/bundleCharge.do?method=bundleChargeReport',
             'Bundle and charge Report', 'Y', 594, '2', '1',
             '/bundleCharge.do?method=bundleChargeReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPC2SDDM', 'ETOPUPRPT', '/bundleCharge.do?method=bundleChargeReport',
             'Bundle and charge Report', 'Y', 594, '1', '1',
             '/bundleCharge.do?method=bundleChargeReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPC2S01A', 'ETOPUPRPT', '/bundleCharge.do?method=bundleChargeReport',
             'Bundle and charge Report', 'N', 594, '2', '1',
             '/bundleCharge.do?method=bundleChargeReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPC2SREPORT', 'ETOPC2S001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPC2SREPORT', 'ETOPC2SDDM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPC2SREPORT', 'ETOPC2S01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'ETOPC2SREPORT', 'Bundle and charge Report',
             'ETopUp Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );


INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'ETOPINSDCSRPT', '1'
            );

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPIND001', 'ETOPUPRPT', '/increaseDecrease.do?method=increaseDecreaseReport',
             'Inc Dec Credit Transaction', 'Y', 596, '2', '1',
             '/increaseDecrease.do?method=increaseDecreaseReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPINDDDM', 'ETOPUPRPT', '/increaseDecrease.do?method=increaseDecreaseReport',
             'Inc Dec Credit Transaction', 'Y', 596, '1', '1',
             '/increaseDecrease.do?method=increaseDecreaseReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPIND01A', 'ETOPUPRPT', '/increaseDecrease.do?method=increaseDecreaseReport',
             'Inc Dec Credit Transaction', 'N', 596, '2', '1',
             '/increaseDecrease.do?method=increaseDecreaseReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPINSDCSRPT', 'ETOPIND001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPINSDCSRPT', 'ETOPINDDDM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPINSDCSRPT', 'ETOPIND01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'ETOPINSDCSRPT', 'Inc Dec Credit Transaction',
             'ETopUp Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );


Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CCASH', 'O2C Cash', 'INCQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CCONGMNT', 'O2C Consignment', 'INCQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CONLINE', 'O2C Online', 'INCQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CFOC', 'O2C FOC', 'INCQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('C2C', 'C2C', 'INCQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('INCQTTYP', 'Quantity Type Increase', TO_DATE('12/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');


	
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CWITHDRW', 'O2C Withdraw', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('O2CREVSAL', 'O2C Reversal', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('C2CWITHDRW', 'C2C Withdraw', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('C2CREVSAL', 'C2C Reversal', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('C2SRC', 'C2S Recharge', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('C2SBRC', 'C2S Bundle Recharge', 'DECQTTYP', 'Y', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('DECQTTYP', 'Quantity Type Decrease', TO_DATE('12/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('12/29/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');		
	
	
	


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'VOMSSTATREPORT', 'Voucher Statistics Report', 'Voucher Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'VOMSSTATREPORT', '1');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSSTATREPORT', 'VCSTAT001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSSTATREPORT', 'VCSTATDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOMSSTATREPORT', 'VCSTAT01A', '1');



Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VCSTAT001', 'VOMSREPORT', '/voucherStatistics.do?method=loadVoucherStatisticsReport', 'Voucher Statistics Report', 'Y', 
    10, '2', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VCSTATDMM', 'VOMSREPORT', '/voucherStatistics.do?method=loadVoucherStatisticsReport', 'Voucher Statistics Report', 'Y', 
    10, '1', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VCSTAT01A', 'VOMSREPORT', '/voucherStatistics.do?method=loadVoucherStatisticsReport', 'Voucher Statistics Report', 'N', 
    10, '2', '1', NULL);




Insert into MODULES
   (MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
 Values
   ('ETOPUPRPT', 'ETopUp Reports', 23, '1');


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTSALEDMM', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total Sales Report', 'Y', 
    3, '1', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTSALE01A', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total Sales Report', 'N', 
    3, '2', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTSALE001', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total Sales Report', 'Y', 
    3, '2', '1', NULL);



Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTSALESREPORT', 'TOTSALE001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTSALESREPORT', 'TOTSALE01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTSALESREPORT', 'TOTSALEDMM', '1');



Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'TOTSALESREPORT', 'Total Sales Report', 'ETopUp Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'TOTSALESREPORT', '1');


Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTC2SDMM', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total C2S Report', 'Y', 
    10, '1', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTC2S01A', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total C2S Report', 'N', 
    10, '2', '1', NULL);
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('TOTC2S001', 'ETOPUPRPT', '/voucherDelivery.do?method=loadVoucherDeliveryReport', 'Total C2S Report', 'Y', 
    10, '2', '1', NULL);



Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTC2SREPORT', 'TOTC2S001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTC2SREPORT', 'TOTC2S01A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('TOTC2SREPORT', 'TOTC2SDMM', '1');


Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
 Values
   ('OPERATOR', 'TOTC2SREPORT', 'Total C2S Report', 'ETopUp Reports', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N', 'B');


Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'TOTC2SREPORT', '1');






INSERT INTO category_roles
            (category_code, role_code, application_id
            )
     VALUES ('BCU', 'ETOPO2CRPT', '1'
            );

INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPO2C001', 'ETOPUPRPT', '/etopO2CPayment.do?method=etopO2CPaymentReport',
             'Cash Online Paymnet Process', 'Y', 596, '2', '1',
             '/etopO2CPayment.do?method=etopO2CPaymentReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPO2CDDM', 'ETOPUPRPT', '/etopO2CPayment.do?method=etopO2CPaymentReport',
             'Cash Online Paymnet Process', 'Y', 596, '1', '1',
             '/etopO2CPayment.do?method=etopO2CPaymentReport'
            );
INSERT INTO pages
            (page_code, module_code, page_url,
             menu_name, menu_item, sequence_no, menu_level, application_id,
             spring_page_url
            )
     VALUES ('ETOPO2C01A', 'ETOPUPRPT', '/etopO2CPayment.do?method=etopO2CPaymentReport',
             'Cash Online Paymnet Process', 'N', 596, '2', '1',
             '/etopO2CPayment.do?method=etopO2CPaymentReport'
            );



INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPO2CRPT', 'ETOPO2C001', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPO2CRPT', 'ETOPO2CDDM', '1'
            );
INSERT INTO page_roles
            (role_code, page_code, application_id
            )
     VALUES ('ETOPO2CRPT', 'ETOPO2C01A', '1'
            );


INSERT INTO ROLES
            (domain_type, role_code, role_name,
             group_name, status, role_type, from_hour, to_hour, group_role,
             application_id, gateway_types, role_for, is_default,
             is_default_grouprole, access_type
            )
     VALUES ('OPERATOR', 'ETOPO2CRPT', 'Cash Online Paymnet Process',
             'ETopUp Reports', 'Y', 'A', NULL, NULL, 'N',
             '1', 'WEB', 'B', 'N',
             'N', 'B'
            );

			INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PENDING', 'Pending', 'CTSTA', 'Y', TIMESTAMP '2005-08-18 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-08-18 00:00:00.000000', 'ADMIN');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('EMAIL_DEFAULT_LOCALE', 'Default locale for Email', 'SYSTEMPRF', 'STRING', 'en_US', NULL, NULL, 50, 'Default locale for Email', 'N', 'Y', 'C2S', 'Default locale for Email', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');


update SYSTEM_PREFERENCES set MODULE= 'P2P' where PREFERENCE_CODE= 'P2P_PROMO_TRF_APP';


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('PAYMENT_VERIFICATION_ALLOWED', 'Payment Verifcation required', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'Payment second layer Verifcation required', 'Y', 
    'Y', 'C2S', 'Chnl User Voucher Allwed Categories', TO_DATE('06/21/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('02/08/2012 12:48:44', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'false,true', 'N');


ALTER TABLE CHANNEL_TRANSFERS ADD column INFO6 VARCHAR(100), ADD column INFO7 VARCHAR(100),	ADD column INFO8 VARCHAR(100), ADD column INFO9 VARCHAR(100), ADD column INFO10 VARCHAR(100);

--##########################################################################################################
--##
--##      PreTUPS_v7.5.0 DB Script
--##
--##########################################################################################################

ALTER TABLE COMMISSION_PROFILE_PRODUCTS ADD TRANSACTION_TYPE VARCHAR(10) DEFAULT 'ALL';

ALTER TABLE COMMISSION_PROFILE_PRODUCTS
DROP CONSTRAINT UK_COMMISSION_PROFILE_PRODUCTS;

ALTER TABLE COMMISSION_PROFILE_PRODUCTS ADD CONSTRAINT UK_COMMISSION_PROFILE_PRODUCTS
UNIQUE (COMM_PROFILE_SET_ID, PRODUCT_CODE, PAYMENT_MODE, COMM_PROFILE_SET_VERSION, TRANSACTION_TYPE);

CREATE TABLE COMMISSION_PROFILE_OTF
(
  COMM_PROFILE_OTF_ID    VARCHAR(10),
  COMM_PROFILE_SET_ID        VARCHAR(10)  NOT NULL,
  COMM_PROFILE_SET_VERSION   VARCHAR(5)   NOT NULL,
  PRODUCT_CODE               VARCHAR(20)  NOT NULL,
  OTF_APPLICABLE_FROM       DATE,
  OTF_APPLICABLE_TO         DATE,
  OTF_TIME_SLAB             VARCHAR(40)
);

CREATE UNIQUE INDEX PK_COMMISSION_PROFILE_OTF ON COMMISSION_PROFILE_OTF
(COMM_PROFILE_OTF_ID);

CREATE UNIQUE INDEX UK_COMMISSION_PROFILE_OTF ON COMMISSION_PROFILE_OTF
(COMM_PROFILE_OTF_ID, PRODUCT_CODE, COMM_PROFILE_SET_VERSION);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('ALL', 'COMM_OTFID', 'ALL', 1, TIMESTAMP '2019-01-19 13:27:20.000000', 'NA', 'OTF ID');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TRANSACTION_TYPE', 'Transaction Type', 'SYSTEMPRF', 'BOOLEAN', 'True', NULL, NULL, 50, 'Flag to specify transaction type', 'Y', 'Y', 'C2S', 'Flag to specify transaction type', TIMESTAMP '2637-01-24 23:52:13.000000', 'ADMIN', TIMESTAMP '2637-01-24 23:52:13.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PAYMENT_MODE_ALWD', 'Payment Mode allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Flag to Enable/Disable Payment Mode allowed', 'N', 'Y', 'C2S', 'Flag to Enable/Disable Payment Mode allowed', TIMESTAMP '2637-01-24 23:52:13.000000', 'ADMIN', TIMESTAMP '2637-01-24 23:52:13.000000', 'SU0001', NULL, 'Y');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('O2C', 'Operator to Channel', 'TRXTP', 'Y', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('C2C', 'Channel to Channel', 'TRXTP', 'Y', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ALL', 'Default', 'TRXTP', 'Y', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('TRXTP', 'Transaction Type', TIMESTAMP '2005-10-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-06 00:00:00.000000', 'ADMIN', 'N');



--##########################################################################################################
--##
--##      PreTUPS_v7.6.0 DB Script
--##
--##########################################################################################################

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SYSTEM', 'System', 'CNTRL', 'N', TIMESTAMP '2019-03-12 00:00:00.000000', 
    'ADMIN', TIMESTAMP '2019-03-12 00:00:00.000000', 'ADMIN');



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DOMAINCODE_FOR_SOS_YABX', 'SOS domain allowed via YABX ', 'NETWORKPRF', 'STRING', 'YABX', 
    NULL, NULL, 50, 'SOS domain allowed via YABX', 'N', 
    'N', 'C2S', 'SOS domain allowed via YABX',  TIMESTAMP '2019-03-12 00:00:00.000000', 'ADMIN', 
     TIMESTAMP '2019-03-12 00:00:00.000000', 'ADMIN', 'YABX', 'Y');



Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('HANDLER_CLASS_FOR_YABX', 'YABX Settlement Handler Class', 'SYSTEMPRF', 'STRING', 'com.client.pretups.sos.requesthandler.SOSSettlementYABXhandler', 
    NULL, NULL, 10, 'YABX Settlement Handler Class', 'N', 
    'N', 'C2S', NULL,  TIMESTAMP '2019-03-12 00:00:00.000000', 'ADMIN', 
     TIMESTAMP '2019-03-12 00:00:00.000000', 'ADMIN', NULL, 'Y');


Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, UNDERPROCESS_CHECK_REQD, 
    REQUEST_PARAM)
 Values
   ('SOSFLGUPDT', 'C2S', 'ALL', 'TYPE MSISDN2 SOSALLOWED  SOSALLOWEDAMOUNT SOSTHRESHOLDLIMIT SOSTXNID', 'com.btsl.pretups.user.requesthandler.SOSFlagUpdateRequestHandler', 
    'SOS Flag Update', 'SOS Flag Update Request', 'Y', TO_DATE('04/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('04/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'SOS Flag Update Request', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, 'NA', 'Y', 'TYPE,TXNSTATUS,DATE,EXTREFNUM,MESSAGE', 'Y', 
    'TYPE,EXTNWCODE,MSISDN2,SOSALLOWED,SOSALLOWEDAMOUNT,SOSTHRESHOLDLIMIT,SOSTXNID');


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SOS_ALLOWED_FOR_YABX', 'SOS allowed for YABX', 'SYSTEMPRF', 'BOOLEAN', 'FALSE', 
    0, 999999999999, 20, 'SOS allowed for YABX', 'N', 
    'N', 'C2S', 'SOS allowed for YABX', TO_DATE('05/05/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('05/05/2014 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');




INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMSPIN_EN_DE_CRYPTION_TYPE', 'EN DE CRYPTION for PIN PASS For VMS', 'SYSTEMPRF', 'STRING', 'AES', NULL, NULL, 50, 'use this preference only for EN DE CRYPTION TYPE for PIN PASS for VMS, not for DB. values can be SHA,DES or AES', 'N', 'Y', 'C2S', 'EN DE CRYPTION TYPE used for PIN PASS for VMS values can be SHA,DES or AES', TIMESTAMP '2007-07-25 11:00:00.000000', 'ADMIN', TIMESTAMP '2007-07-25 11:00:00.000000', 'ADMIN', 'NULL', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IPV6_ENABLED', 'To Enable/Disable IPV6', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'To Enable/Disable IPV6', 'N', 'Y', 'C2S', 'To Enable/Disable IPV6', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');




--##########################################################################################################
--##
--##      PreTUPS_v7.7.0 DB Script
--##
--##########################################################################################################
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_ENABLE', 'National Voucher Enable', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'National Voucher Enable For Super Admin', 'N', 'Y', 'C2S', 'National Voucher Enable', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_NETWORK_CODE', 'National Voucher Network Code', 'SYSTEMPRF', 'STRING', 'XX', NULL, NULL, 50, 'National Voucher Network Code For Super Admin', 'Y', 'Y', 'C2S', 'National Voucher Network Code For Super Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-06-06 15:03:14.000000', 'SU0001', NULL, 'Y');


ALTER TABLE VOMS_CATEGORIES
  Add COLUMN NETWORK_CODE varchar(2) ;
  
  INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'INITVOMS', '1');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP1VOMS', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP2VOMS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP3VOMS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADDCAT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'INITVOMS', '1');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADDCAT', '1');






INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADDPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADDPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODIPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODIPROF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVIEWPRF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVIEWPRF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADACTPR', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADACTPR', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODACTPR', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODACTPR', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVIEWACT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVIEWACT', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSTPMGT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODDENO', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODDENO', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVWDEN', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVWDEN', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSOTCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSOTCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VIEWBATCHLIST', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VIEWBATCHLIST', '1');

ALTER TABLE VOMS_PRODUCTS
  Add NETWORK_CODE varchar(2) ;


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');

ALTER TABLE VOMS_PRODUCTS
  Add NETWORK_CODE varchar(2);


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');

INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ONLINE_BATCH_EXP_DATE_LIMIT', 'Online Batch Expiry Date Limit', 'SYSTEMPRF', 'INT', '1000', 0, 1000, 50, 'Vouchers expiry date processed for batch online', 'Y', 'Y', 'C2S', 'Vouchers expiry date processed for batch online', '2005-06-21 00:00:00.000', 'ADMIN', '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');



INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_VOUCHER_EXPIRY_EXTN_LIMIT', 'Max limit for vouchers to extend expiry limit', 'SYSTEMPRF', 'INT', '10000', 0, 10000, 50, 'Maximum vouchers to be processed for voucher expiry date', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher expiry date', '2005-06-21 00:00:00.000', 'ADMIN', '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2019', 'VMPNEXPEXT', 'ALL', 0, '2006-06-03 00:00:00.000', 'NA', NULL);


   CREATE TABLE voms_pin_exp_ext (
	batch_no varchar(15) NOT NULL,
	voucher_type varchar(15) NULL,
	total_vouchers numeric(16) NULL,
	from_serial_no varchar(16) NULL,
	to_serial_no varchar(16) NULL,
	total_failure numeric(16) NULL,
	total_success numeric(16) NULL,
	network_code varchar(2) NULL,
	status varchar(2) NULL,
	created_on timestamp NULL,
	created_by varchar(20) NULL,
	modified_on timestamp NULL,
	modified_by varchar(20) NULL,
	remarks varchar(50) NULL,
	expiry_date timestamp NULL,
	CONSTRAINT pk_voms_pin_exp_ext PRIMARY KEY (batch_no)
)

INSERT INTO service_type
(service_type, module, TYPE, message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, "name", external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('VMSPINEXT', 'C2S', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherExpiryDateExtensionController', 'Voucher Expiry Date Extension API', 'Voucher Expiry Date Extension API', 'Y', '2017-11-06 06:18:46.000', 'ADMIN', '2017-11-06 06:18:46.000', 'ADMIN', 'Voucher Expiry Date Extension API', 'Y', 'N', 'Y', 11, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'REST', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y', '2019-05-03 16:39:59.613', 'SU0001', '2019-05-03 16:39:59.613', 'SU0001', 'SVK4100242', NULL, '');


INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'EXTGW', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y', '2019-05-03 16:39:59.613', 'SU0001', '2019-05-03 16:39:59.613', 'SU0001', 'SVK4100142', NULL, '');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN', 'Voms Max Approval level for Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN', 'Voms Max Approval level for Super Network Adm', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN', 'Voms Max Approval level for Sub Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');



INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROF_TALKTIME_MANDATORY', 'VOMS ADD PROFILE TALK TIME MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'talk-time will be mandatory if defined as true', 'N', 'Y', 'C2S', 'false if talk time is not required , true if required', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROF_VALIDITY_MANDATORY', 'VOMS ADD PROFILE VALIDITY MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'validity(in days) will be mandatory if defined as true', 'N', 'Y', 'C2S', 'false if  validaity is not required , true if required ', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_DEF_MINMAXQTY', 'VOMS PROFILE DEFAULT MINMAXQTY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'required for fields to be hidden in voucher profile', 'N', 'Y', 'C2S', 'false if need to show max min reorder qty, true if not required', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_MIN_REORDERQTY', 'VOMS PROFILE DEFAULT MINQTY', 'SYSTEMPRF', 'INT', '10', NULL, NULL, 50, 'default value is taken as min re order quantity', 'N', 'Y', 'C2S', 'default value is taken as min re order quantity', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');




INSERT INTO system_preferences
(preference_code, "name", TYPE, value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_MAX_REORDERQTY', 'VOMS PROFILE DEFAULT MAXQTY', 'SYSTEMPRF', 'INT', '1000', NULL, NULL, 50, 'default value is taken as max re order quantity', 'N', 'Y', 'C2S', 'default value is taken as max re order quantity', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, "name", TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DOWNLD_BATCH_BY_BATCHID', 'batches download by batch id ', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Is calendar icon required on GUI', 'N', 'Y', 'C2S', 'Is calendar icon required on GUI','2018-06-26 00:00:00.000000', 'ADMIN','2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'N');

ALTER TABLE VOMS_VOUCHERS ADD COLUMN pre_expiry_date timestamp;
ALTER TABLE VOMS_VOUCHERS ADD info1 VARCHAR(50);

ALTER TABLE PAGES ALTER COLUMN MENU_NAME type VARCHAR(35);
ALTER TABLE PAGES ALTER COLUMN PAGE_CODE type VARCHAR(12);
ALTER TABLE PAGE_ROLES ALTER COLUMN PAGE_CODE type VARCHAR(12);

--##########################################################################################################
--##
--##      PreTUPS_v7.8.0 DB Script
--##
--##########################################################################################################

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('LC', 'Local', 'VMSSEG', 'Y', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('NL', 'National', 'VMSSEG', 'Y', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-08 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('VMSSEG', 'VMS Segment', TIMESTAMP '2005-10-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-06 00:00:00.000000', 'ADMIN', 'N');
COMMIT;


CREATE TABLE USER_VOUCHER_SEGMENTS
(
  USER_ID       VARCHAR(15)               NOT NULL,
  VOUCHER_SEGMENT  VARCHAR(10)            NOT NULL,
  STATUS        VARCHAR(1)                DEFAULT 'Y'                   NOT NULL,
  CONSTRAINT PK_USER_VOUCHER_SEGMENT PRIMARY KEY (USER_ID,VOUCHER_SEGMENT)
);

ALTER TABLE VOMS_PRODUCTS
  Add VOUCHER_SEGMENT varchar(2) DEFAULT 'LC';
  
ALTER TABLE VOMS_CATEGORIES
  Add VOUCHER_SEGMENT varchar(2) DEFAULT 'LC';

  ALTER TABLE VOMS_BATCHES
  Add VOUCHER_SEGMENT varchar(2) DEFAULT 'LC';
  
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_VOUCHERSEGMENT_ALLOWED', 'User voucher segment is allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'User voucher type is allowed', 'Y', 'Y', 'C2S', 'User voucher segment is allowed', TIMESTAMP '2018-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NW_NATIONAL_PREFIX', 'NW_NATIONAL_PREFIX', 'SYSTEMPRF', 'STRING', '99', null, null, 50, 'Prefix for National Voucher', 'N', 'N', 'C2S', 'Prefix for National Voucher', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'N');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NW_CODE_NW_PREFIX_MAPPING', 'NW_CODE_NW_PREFIX_MAPPING', 'SYSTEMPRF', 'STRING', 'NG=11,PB=12', null, null, 250, 'Network code and network Prefix mapping', 'N', 'N', 'C2S', 'Values can be Comma saperated', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_NATIONAL_LOCAL_PREFIX_ENABLE', 'VOMS_NATIONAL_LOCAL_PREFIX_ENABLE', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'To enable/disable national/local prefix', 'N', 'N', 'C2S', 'To enable/disable national/local prefix', TIMESTAMP '2018-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');


ALTER TABLE CHANNEL_VOUCHER_ITEMS
  Add VOUCHER_SEGMENT varchar(2) DEFAULT 'LC';
  
  ALTER TABLE CHANNEL_VOUCHER_ITEMS
  Add NETWORK_CODE varchar(2);
  
ALTER TABLE VOMS_VOUCHERS
  Add VOUCHER_SEGMENT varchar(2) DEFAULT 'LC';

  
--##########################################################################################################
--##
--##      PreTUPS_v7.9.0 DB Script
--##
--##########################################################################################################

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSADDCARDGRP', 'Add Voucher Card Group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSADDCARDGRP', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSADDCARDGRP', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCR0001', 'CARDGROUP', '/cardGroupDetailsAction.do?method=load&page=0&cardGroupType=VMS', 'Add Voucher Card Group', 'Y', 20, '2', '1', '/cardGroupDetailsAction.do?method=load&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCRDmm', 'CARDGROUP', '/cardGroupDetailsAction.do?method=load&page=0&cardGroupType=VMS', 'Add Voucher Card Group', 'Y', 20, '1', '1', '/cardGroupDetailsAction.do?method=load&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCR0002', 'CARDGROUP', '/jsp/cardgroup/cardGroupDetailsView.jsp', 'Add/Edit voucher Card Confirmation', 'N', 20, '2', '1', '/jsp/cardgroup/cardGroupDetailsView.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCR0003', 'CARDGROUP', '/jsp/cardgroup/addCardGroup.jsp', 'Add/Edit Voucher Single Card Detai', 'N', 20, '2', '1', '/jsp/cardgroup/addCardGroup.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCR0004', 'CARDGROUP', '/jsp/cardgroup/addTemp.jsp', 'For refreshing voucher cardGroupDe', 'N', 20, '2', '1', '/jsp/cardgroup/addTemp.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCR0005', 'CARDGROUP', '/jsp/cardgroup/testCardGroup.jsp', 'For Testing Voucher Card Group Set', 'N', 20, '2', '1', '/jsp/cardgroup/testCardGroup.jsp');


INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCR0001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCRDmm', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCR0002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCR0003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCR0004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSADDCARDGRP', 'VMSCR0005', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSEDITCARDGRP', 'Modify Voucher Card Group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSEDITCARDGRP', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSEDITCARDGRP', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VSELCRDmm', 'CARDGROUP', '/selectCardGroupSetAction.do?method=loadCardGroupSetNames&page=0&cardGroupType=VMS', 'Modify Voucher Card Group', 'Y', 21, '1', '1', '/selectCardGroupSetAction.do?method=loadCardGroupSetNames&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VSELCR001', 'CARDGROUP', '/selectCardGroupSetAction.do?method=loadCardGroupSetNames&page=0&cardGroupType=VMS', 'Modify Voucher Card Group', 'Y', 21, '2', '1', '/selectCardGroupSetAction.do?method=loadCardGroupSetNames&page=0&cardGroupType=VMS');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSEDITCARDGRP', 'VSELCR001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSEDITCARDGRP', 'VSELCRDmm', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSVIEWTRANSRULE', 'Calculate Voucher transfer value', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSVIEWTRANSRULE', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSVIEWTRANSRULE', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCRTR001', 'CARDGROUP', '/viewTransferRuleAction.do?method=loadTransferRule&page=0&cardGroupType=VMS', 'Calculate Voucher Transfer Value', 'Y', 22, '2', '1', '/viewTransferRuleAction.do?method=loadTransferRule&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSCRTRDmm', 'CARDGROUP', '/viewTransferRuleAction.do?method=loadTransferRule&page=0&cardGroupType=VMS', 'Calculate Voucher Transfer Value', 'Y', 22, '1', '1', '/viewTransferRuleAction.do?method=loadTransferRule&page=0&cardGroupType=VMS');


INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWTRANSRULE', 'VMSCRTR001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWTRANSRULE', 'VMSCRTRDmm', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSSUSCARDGRP', 'Voucher Card Group Status', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSSUSCARDGRP', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSSUSCARDGRP', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSSCR0001', 'CARDGROUP', '/cardGroupListAction.do?method=loadCardGroupList&page=0&cardGroupType=VMS', 'Voucher Card Group Status', 'Y', 23, '2', '1', '/cardGroupListAction.do?method=loadCardGroupList&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSSCRDmm', 'CARDGROUP', '/cardGroupListAction.do?method=loadCardGroupList&page=0&cardGroupType=VMS', 'Voucher Card Group Status', 'Y', 23, '1', '1', '/cardGroupListAction.do?method=loadCardGroupList&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSSCR0002', 'CARDGROUP', '/jsp/cardGroupListView.jsp', 'Voucher Card Group Status', 'N', 4, '2', '1', '/jsp/cardGroupListView.jsp');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSSUSCARDGRP', 'VMSSCR0001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSSUSCARDGRP', 'VMSSCR0002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSSUSCARDGRP', 'VMSSCRDmm', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSVIEWCARDGRP', 'View  Voucher Card Group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSVIEWCARDGRP', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSVIEWCARDGRP', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSVCRDmm', 'CARDGROUP', '/viewCardGroupSetAction.do?method=loadCardGroupSetNamesForView&page=0&cardGroupType=VMS', 'View  Voucher Card Group', 'Y', 24, '1', '1', '/viewCardGroupSetAction.do?method=loadCardGroupSetNamesForView&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSVCR0001', 'CARDGROUP', '/viewCardGroupSetAction.do?method=loadCardGroupSetNamesForView&page=0&cardGroupType=VMS', 'View  Voucher Card Group', 'Y', 24, '2', '1', '/viewCardGroupSetAction.do?method=loadCardGroupSetNamesForView&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSVCR0002', 'CARDGROUP', '/jsp/cardgroup/p2pVersionList.jsp', 'View  Voucher Card Group', 'N', 24, '2', '1', '/jsp/cardgroup/p2pVersionList.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSVCR0003', 'CARDGROUP', '/jsp/cardgroup/cardGroupDetailsView.jsp', 'View  Voucher Card Group', 'N', 24, '2', '1', '/jsp/cardgroup/cardGroupDetailsView.jsp');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWCARDGRP', 'VMSVCR0001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWCARDGRP', 'VMSVCR0002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWCARDGRP', 'VMSVCR0003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSVIEWCARDGRP', 'VMSVCRDmm', '1');


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSBATCGMOD', 'Batch modify Voucher card group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSBATCGMOD', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSBATCGMOD', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGP001', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'Y', 25, '2', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGP01A', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'N', 25, '2', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGPDMM', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'Y', 25, '1', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGP001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGP01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGPDMM', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSDFLTCARDGRP', 'Default Voucher Card Group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSDFLTCARDGRP', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSDFLTCARDGRP', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSSELDF01', 'CARDGROUP', '/selectCardGroupSetAction.do?method=loadDefaultCardGroupSetNames&page=0&cardGroupType=VMS', 'Default Voucher Card Group set', 'Y', 26, '2', '1', '/selectCardGroupSetAction.do?method=loadDefaultCardGroupSetNames&page=0&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSSELDFDM', 'CARDGROUP', '/selectCardGroupSetAction.do?method=loadDefaultCardGroupSetNames&page=0&cardGroupType=VMS', 'Default Voucher Card Group set', 'Y', 26, '1', '1', '/selectCardGroupSetAction.do?method=loadDefaultCardGroupSetNames&page=0&cardGroupType=VMS');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSDFLTCARDGRP', 'VMSSELDF01', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSDFLTCARDGRP', 'VMSSELDFDM', '1');

ALTER TABLE CARD_GROUP_DETAILS
Add VOUCHER_TYPE varchar2(15) DEFAULT 'NA' NOT NULL
Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'NA' NOT NULL
Add VOUCHER_PRODUCT_ID varchar2(15) DEFAULT 'NA' NOT NULL;


INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DFLTCARDGRP', 'Set Default cardgroup', 'DefaultCardGroupI', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/default-cardgroup/set-defaultcardgroup', 'Y', 'Y', 'VMSDFLTCARDGRP');

--##########################################################################################################
--##
--##      PreTUPS_v7.10.0 DB Script
--##
--##########################################################################################################

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_SERVICES', 'Services related to Voucher', 'SYSTEMPRF', 'STRING', 'VCN', 1, 1, 50, 'Enter Voucher services comma separated', 'N', 'N', 'C2S', 'VMS Services', TIMESTAMP '2007-07-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-07-25 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DFLTCARDGRP', 'Set Default cardgroup', 'DefaultCardGroupI', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/setDefaultCardGroupSet', 'Y', 'Y', 'VMSDFLTCARDGRP');
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('LOADCARDGROUPSET', 'Card Group Set', 'CardGroupChangeStatusI', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/loadCardGroupSetList', 'N', 'Y', NULL);
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('SUSPENDCARDGROUP', 'Suspend Card Group Set', 'CardGroupChangeStatusI', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/updateCardGroupSetStatus', 'N', 'Y', NULL);
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DELETECARDGROUP', 'Delete cardgroup', 'ViewCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/deleteCardGroup', 'Y', 'Y', 'DELETECARDGROUP');
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('MODIFYCARDGROUP', 'Modify cardgroup', 'AddCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/modifyCardgroup', 'N', 'Y', 'VMSEDITCARDGRP');
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('ADDCARDGROUP', 'Add cardgroup', 'AddCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/addCardgroup', 'N', 'Y', 'ADDCARDGROUP');

--##########################################################################################################
--##
--##      PreTUPS_v7.11.0 DB Script
--##
--##########################################################################################################

ALTER TABLE PAGES
  ALTER MENU_NAME TYPE varchar(35);


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VMSBATCGMOD', 'Batch modify Voucher card group', 'Card Group', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VMSBATCGMOD', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VMSBATCGMOD', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGP001', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'Y', 25, '2', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGP01A', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'N', 25, '2', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VMSBCGPDMM', 'CARDGROUP', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS', 'Batch Modify Voucher Card Group', 'Y', 25, '1', '1', '/batchCardGroupModify.do?method=selectNetworkCodeCardGroup&TYPE=Voucher&cardGroupType=VMS');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGP001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGP01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VMSBATCGMOD', 'VMSBCGPDMM', '1');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_VOUCHER_GEN_LIMIT', 'Max limit for voucher generation', 'SYSTEMPRF', 'INT', '14', 0, 100000, 50, 'Maximum vouchers to be processed for voucher generation online', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher generation online', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOUCHER_GEN_EMAIL_NOTIFICATION', 'Voucher Generation Email Notification', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Email notification to the approvers and initiator should be sent or not', 'N', 'Y', 'C2S', 'Email notification to the approvers or initiator should be sent or not', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOUCHER_GEN_SMS_NOTIFICATION', 'Voucher Generation Sms Notification', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Sms notification to the approvers and initiator should be sent or not', 'N', 'Y', 'C2S', 'Sms notification to the approvers or initiator should be sent or not', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('VIEWCARDGROUP', 'View cardgroup', 'ViewCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/viewCardGroupSetDetails', 'Y', 'Y', 'VIEWCARDGROUP');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('VIEWCARDGRPVERSION', 'View cardgroup version', 'ViewCardGroupVersion', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/cardGroupSetVersions', 'Y', 'Y', 'VIEWCARDGRPVERSION');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('CALCULATEVOUCHERCARDGROUP', 'calculate voucher cardgroup', 'CalculateVoucherCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/calVoucherTransferRule', 'N', 'Y', 'CALCULATEVOUCHERCARDGROUP');

--##########################################################################################################
--##
--##      PreTUPS_v7.12.0 DB Script
--##
--##########################################################################################################
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  PRE_EXPIRY_DATE DATE;
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  INFO1 VARCHAR(50);
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  VOUCHER_SEGMENT VARCHAR(2);


      
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('DVD', 'C2S', 'PRE', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.DVDController', 'Digital Voucher Distribution', 'Digital Voucher Distribution', 'Y', TIMESTAMP '2007-01-01 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-01-01 00:00:00.000000', 'ADMIN', 'Voucher Consumption', 'Y', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'WEB', '190', 'DVD', 'DVD_WEB', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK000055', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'SMSC', '190', 'DVD', 'DVD_SMSC', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004247', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'USSD', '190', 'DVD', 'DVD_USSD', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004248', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'EXTGW', '190', 'DVD', 'DVD_EXTGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004249', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'MAPPGW', '190', 'DVD', 'DVD_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004349', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');

---- added by shishupal
INSERT INTO PRODUCT_SERVICE_TYPE_MAPPING
(PRODUCT_TYPE, SERVICE_TYPE, CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON, GIVE_ONLINE_DIFFERENTIAL, DIFFERENTIAL_APPLICABLE, SUB_SERVICE)
VALUES('PREPROD', 'DVD', 'ADMIN', TIMESTAMP '2006-05-30 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-05-30 00:00:00.000000', 'Y', 'Y', 1);


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_DVD_LIMIT', 'Max limit for DVD', 'SYSTEMPRF', 'INT', '10', 0, 5, 50, 'Maximum vouchers to be distributed', 'Y', 'Y', 'C2S', 'Maximum vouchers to be distributed', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000000', 'SU0001', NULL, 'Y');


--added by yogesh dixit for my voucher enquiry for subscriber 
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('SUBSCRIBER_VOUCHER_PIN_REQUIRED', 'Voucher pin required to show for Subs enq', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Voucher pin required to show for subscriber Enq', 'N', 'Y', 'P2P', 'Voucher pin required to show for subs Enq', TIMESTAMP '2019-09-19 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-19 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('SELFVCRENQ', 'P2P', 'PRE', 'KEYWORD', 'com.btsl.pretups.p2p.subscriber.requesthandler.SubscriberVoucherInquiryController', 'Subscriber Voucher Inquiery', 'Subscriber Voucher Inquiery', 'Y', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', 'Subscriber Voucher Inquiery', 'N', 'Y', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('VCAVLBLREQ', 'C2S', 'ALL', 'KEYWORD', 'com.btsl.pretups.channel.transfer.requesthandler.ChannelVoucherEnquiryController', 'Channel Voucher Enquiry', 'Channel Voucher Enquiry', 'Y', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', 'Channel Voucher Enquiry', 'N', 'Y', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,EXTNWCODE,DATE,MSISDN,PIN,LOGINID,PASSWORD,EXTCODE,LANGUAGE1', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('VCAVLBLREQ', 'EXTGW', '190', 'VCAVLBLREQ', 'VCAVLBLREQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-09-18 02:56:37.000000', 'SU0001', TIMESTAMP '2019-09-18 02:56:37.000000', 'SU0001', 'SVK4101004', NULL, NULL);

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('VCAVLBLREQ', 'REST', '190', 'VCAVLBLREQ', 'VCAVLBLREQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-09-20 03:09:57.000000', 'SU0001', TIMESTAMP '2019-09-20 03:09:57.000000', 'SU0001', 'SVK4101006', NULL, NULL);

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('VCAVLBLREQ', 'SMSC', '190', 'VCAVLBLREQ', 'VCAVLBLREQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-09-24 03:45:50.000000', 'SU0001', TIMESTAMP '2019-09-24 03:45:50.000000', 'SU0001', 'SVK4101009', NULL, NULL);

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('VCAVLBLREQ', 'USSD', '190', 'VCAVLBLREQ', 'VCAVLBLREQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-09-23 06:08:18.000000', 'SU0001', TIMESTAMP '2019-09-23 06:08:18.000000', 'SU0001', 'SVK4101008', NULL, NULL);


--##########################################################################################################
--##
--##      PreTUPS_v7.13.0 DB Script
--##
--##########################################################################################################

-- Added by Sparsh Kalra
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('VIEWUSERPROFILETHRESHOLD', 'View User Profile Threshold', 'ChannelUserServices', 'configfiles\channeluser\validation-channeluser.xml', '', 'configfiles/restservice', '/rest/user/userthreshold', 'Y', 'Y', 'VIEWUSERPROFILETHRESHOLD');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('VIEWSELFPROFILETHRESHOLD', 'View Self Profile Threshold', 'ChannelUserServices', 'configfiles\channeluser\validation-channeluser.xml', '', 'configfiles/restservice', '/rest/user/selfthreshold', 'Y', 'Y', 'VIEWSELFPROFILETHRESHOLD');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('SEARCHUSER', 'Search Channel User', 'ChannelUserServices', 'configfiles\channeluser\validation-channeluser.xml', '', 'configfiles/restservice', '/rest/user/searchuser', 'Y', 'Y', 'SEARCHUSER');


--##########################################################################################################
--##
--##      PreTUPS_v7.14.0 DB Script
--##
--##########################################################################################################

 ALTER TABLE VOMS_VOUCHERS ADD  C2S_TRANSACTION_ID VARCHAR(20);
 
 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_STATUS_CHANGE', 'VMS_D_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,S', NULL, NULL, 50, 'Possible status for Status Change for Digital Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Digital for Change ', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_STATUS_CHANGE', 'VMS_E_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,S', NULL, NULL, 50, 'Possible combination for Status Change for Electronics Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Electronics for Change', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_STATUS_CHANGE', 'VMS_P_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,WH,S', NULL, NULL, 50, 'Possible combination for Status Change for Physical Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Physical for Change', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');


INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('digital', 'Digital', 'VCN', 'Y', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', 'D');

INSERT INTO VOMS_VTYPE_SERVICE_MAPPING
(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('digital', 'VCN', '1', 'Y', 409);


INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('test_digit', 'Digital Test', 'VCN', 'Y', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', 'DT');

INSERT INTO VOMS_VTYPE_SERVICE_MAPPING
(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_digit', 'VCN', '1', 'Y', 410);


INSERT INTO USER_VOUCHERTYPES
(USER_ID, VOUCHER_TYPE, STATUS)
VALUES('SU0001', 'digital', 'Y');

INSERT INTO USER_VOUCHERTYPES
(USER_ID, VOUCHER_TYPE, STATUS)
VALUES('SU0001', 'test_digit', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('SCREEN_WISE_ALLOWED_VOUCHER_TYPE', 'SCREEN_WISE_ALLOWED_VOUCHER_TYPE', 'SYSTEMPRF', 'STRING', 'ACTIVE_PROF:E,ET;VOUC_DOWN:P,PT;O2C:D,DT,P,PT,E', NULL, NULL, 50, 'Allowed Voucher Types screen-wise', 'N', 'N', 'C2S', 'Allowed Voucher Types screen-wise', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('ONLINEVOMSGEN', TIMESTAMP '2019-10-25 16:04:53.000000', 'C', TIMESTAMP '2019-10-25 16:04:53.000000', TIMESTAMP '2019-10-25 00:00:00.000000', 360, 1440, 'VOMS Generation Process', 'NG', 0);

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('VOMSBATCHINITCOUNT', TIMESTAMP '2019-08-29 13:16:09.000000', 'C', TIMESTAMP '2019-08-29 13:16:09.000000', TIMESTAMP '2019-08-29 00:00:00.000000', 360, 1440, 'VOMS Generation Process', 'NG', 0);

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('VOMSBATCHINITCOUNT', TIMESTAMP '2019-08-29 13:16:09.000000', 'C', TIMESTAMP '2019-08-29 13:16:09.000000', TIMESTAMP '2019-08-29 00:00:00.000000', 360, 1440, 'VOMS Generation Process', 'PB', 0);

INSERT INTO NETWORK_PREFERENCES
(NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('NG', 'ONLINE_VOUCHER_GEN_LIMIT_NW', '10', TIMESTAMP '2007-12-03 13:43:33.000000', 'ADMIN', TIMESTAMP '2015-08-06 13:37:24.000000', 'SU0001');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_VOUCHER_GEN_LIMIT_NW', 'Max network limit for voucher generation', 'NETWORKPRF', 'INT', '10', 0, 100000, 50, 'Maximum vouchers to be processed for voucher generation online', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher generation online', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOUCHER_PROFILE_OTHER_INFO', 'Voucher Profile Other Information', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'To allow visibility of item code and secondary prefix code on Voucher Profile Screens', 'N', 
    'Y', 'C2S', 'If this flag is true, it allows visibility of item code and secondary prefix code on Voucher Profile Screens', NOW(), 'ADMIN', NOW(), 'ADMIN', 'true,false', 'Y');
 
ALTER TABLE VOMS_PRODUCTS ADD  ITEM_CODE VARCHAR(10);
 
ALTER TABLE VOMS_PRODUCTS ADD  SECONDARY_PREFIX_CODE VARCHAR(10);

--##########################################################################################################
--##
--##      PreTUPS_v7.15.0 DB Script
--##
--##########################################################################################################

 ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  C2S_TRANSACTION_ID VARCHAR(20);
 
 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_LIFECYCLE', 'Digital voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:EN:CU', NULL, NULL, 50, 'Digital voucher life cycle', 'N', 'Y', 'VMS', 'Digital voucher life cycle', '2019-11-06 00:00:00.000000', 'ADMIN', '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');

 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_LIFECYCLE', 'Physical voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:PE:WH:EN:CU', NULL, NULL, 50, 'Physical voucher life cycle', 'N', 'Y', 'VMS', 'Physical voucher life cycle', '2019-11-06 00:00:00.000000', 'ADMIN', '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');

 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_LIFECYCLE', 'Electronic voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:EN:CU', NULL, NULL, 50, 'Electronic voucher life cycle', 'N', 'Y', 'VMS', 'Electronic voucher life cycle', '2019-11-06 00:00:00.000000', 'ADMIN', '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');


 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_STATUS_CHANGE_MAP', 'Electronic voucher change status mapping', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Electronic voucher change status mapping', 'N', 'Y', 'VMS', 'Electronic voucher change status mapping', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_STATUS_CHANGE_MAP', 'Physical voucher change status mapping', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,PE:WH,PE:ST,WH:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Physical voucher change status mapping', 'N', 'Y', 'VMS', 'Electronic voucher change status mapping', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_STATUS_CHANGE_MAP', 'VMS DIGITAL VOUCHER STATUS CHANGE MAP', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Map for digital vouchers changestaus', 'N', 'Y', 'C2S', 'Map for digital vouchers changestaus', TIMESTAMP '2005-07-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-13 00:00:00.000000', 'SUPERADMIN', NULL, 'Y');

 --##########################################Change other/generated status #######################################
 
CREATE OR REPLACE FUNCTION pretupsdatabase.p_changevoucherstatus(p_batchno character varying, p_batchtype character varying, p_fromserialno character varying, p_toserialno character varying, p_batchenablestat character varying, p_batchgenstat character varying, p_batchonholdstat character varying, p_batchstolenstat character varying, p_batchsoldstat character varying, p_batchdamagestat character varying, p_batchreconcilestat character varying, p_batchprintstat character varying, p_warehousestat character varying, p_preactivestat character varying, p_suspendstat character varying, p_createdon character varying, p_maxerrorallowed integer, p_modifiedby character varying, p_noofvouchers integer, p_successprocessstatus character varying, p_errorprocessstatus character varying, p_batchconstat character varying, p_processscreen integer, p_modifiedtime character varying, p_referenceno character varying, p_rcadminmaxdateallowed integer, p_enableprocess character varying, p_changeprocess character varying, p_reconcileprocess character varying, p_networkcode character varying, p_seqid integer, p_prefstring character varying, OUT p_returnmessage character varying, OUT p_returnlogmessage character varying, OUT p_sqlerrormessage character varying, OUT p_response character[])
 RETURNS record
 LANGUAGE plpgsql
AS $function$
  DECLARE
    -- Variables Declaration
    v_batchEnableStat voms_batches.BATCH_TYPE%type;
    v_batchGenStat voms_batches.BATCH_TYPE%type;
    v_batchOnHoldStat voms_batches.BATCH_TYPE%type;
    v_batchStolenStat voms_batches.BATCH_TYPE%type;
    v_batchSoldStat voms_batches.BATCH_TYPE%type;
    v_batchDamageStat voms_batches.BATCH_TYPE%type;
    v_batchReconcileStat voms_batches.BATCH_TYPE%type;
    v_batchConStat voms_batches.BATCH_TYPE%type;
    v_batchPrintStat voms_batches.BATCH_TYPE%type;
    v_wareHouseStat voms_batches.BATCH_TYPE%type;
    v_preActiveStat voms_batches.BATCH_TYPE%type;
    v_suspendStat voms_batches.BATCH_TYPE%type;
    v_batchNo voms_batches.BATCH_NO%type;
    v_batchType voms_batches.BATCH_TYPE%type;
    v_vouchStat voms_vouchers.STATUS%type;
    v_voucCurrStat voms_vouchers.CURRENT_STATUS%type;
    v_expDate voms_vouchers.EXPIRY_DATE%type;
    v_createdOn TIMESTAMP without TIME zone;
    v_productID voms_vouchers.PRODUCT_ID%type;
    v_modifiedBy voms_voucher_audit.MODIFIED_BY%type;
    rcd_count INT;
    v_networkCode voms_batches.NETWORK_CODE%type;
    v_succFailFlag VARCHAR(32767);
    v_message voms_voucher_audit.MESSAGE%type;
    v_errorCount    INTEGER;
	 v_errorCountReal    INTEGER;
    v_processScreen INT;
    v_modifiedTime  TIMESTAMP without TIME zone;
    v_processStatus voms_voucher_audit.PROCESS_STATUS%type;
    v_row_id INT;
    v_insertRowId voms_voucher_audit.ROW_ID%type;
    v_serialStart voms_batches.FROM_SERIAL_NO%type;
    v_referenceNo voms_batches.REFERENCE_NO%type;
    v_EnableProcess voms_batches.PROCESS%type;
    v_ChangeProcess voms_batches.PROCESS%type;
    v_ReconcileProcess voms_batches.PROCESS%type;
    v_returnMessage VARCHAR(32767);

    v_returnLogMessage         VARCHAR(32767);
    v_sqlErrorMessage          VARCHAR(32767);
    v_enableCount              INTEGER;
    v_DamageStolenCount        INTEGER;
    v_DamageStolenAfterEnCount INTEGER;
    v_onHoldCount              INTEGER;
    v_counsumedCount           INT;
    v_voucherNotFoundCount     INTEGER;
    v_preActiveCount           INT;
    v_voucherNotFoundFlag      BOOLEAN;
    v_maxErrorFlag             BOOLEAN;
    v_LastRequestAttemptNo voms_vouchers.LAST_REQUEST_ATTEMPT_NO%type;
    v_LastAttemptValue voms_vouchers.LAST_ATTEMPT_VALUE%type;
    v_RCAdminMaxdateallowed INT;
    v_serialNoLength        INT;
    v_seqId                 INT;
    v_prefString            VARCHAR(32767);
    p_responseIndex         INT;
    -- Declaration of Variables Ends --
  BEGIN
    p_responseIndex     :=0;
    v_batchEnableStat   :=p_batchEnableStat;
    v_batchGenStat      :=p_batchGenStat;
    v_batchOnHoldStat   :=p_batchOnHoldStat;
    v_batchStolenStat   :=p_batchStolenStat;
    v_batchSoldStat     := p_batchSoldStat;
    v_batchDamageStat   :=p_batchDamageStat;
    v_batchReconcileStat:=p_batchReconcileStat;
    v_batchPrintStat    :=p_batchPrintStat;
    v_wareHouseStat     :=p_wareHouseStat;
    v_preActiveStat     :=p_preActiveStat;
    v_suspendStat       :=p_suspendStat;
    v_batchConStat      :=p_batchConStat;
    v_modifiedBy        :=p_modifiedBy;
    v_errorCount        :=0;
	v_errorCountReal    :=0;
    v_serialStart       :=p_fromSerialNo;
    v_batchNo           :=p_batchNo;
    v_batchType         :=p_batchType;
    v_processScreen     :=p_processScreen;
    v_enableCount       :=0;
    v_DamageStolenCount :=0;
    --  v_DamageStolenAfterEnCount :=0;
    v_onHoldCount          :=0;
    v_counsumedCount       :=0;
    v_preActiveCount       :=0;
    v_referenceNo          :=p_referenceNo;
    v_RCAdminMaxdateallowed:=p_RCAdminMaxdateallowed;
    v_EnableProcess        :=p_EnableProcess;
    v_ChangeProcess        :=p_ChangeProcess;
    v_ReconcileProcess     :=p_ReconcileProcess;
    v_networkCode          :=p_networkCode;
    v_voucherNotFoundCount :=0;
    v_returnMessage        :='';
    v_sqlErrorMessage      :='';
    v_serialNoLength       :=LENGTH(p_fromSerialNo);
    v_seqId                :=p_seqId;
    v_LastRequestAttemptNo := 0.0;
    v_LastAttemptValue     :=0.0;
    v_prefString           := p_prefString;
    
    -- Start the Loop --
    WHILE(v_serialStart<=p_toSerialNo)
    LOOP
      BEGIN
        SELECT to_timestamp(p_createdOn ,'YYYY-MM-DD HH24:MI:SS')::TIMESTAMP without TIME zone
        INTO v_createdOn ;
        SELECT to_timestamp(p_modifiedTime ,'YYYY-MM-DD HH24:MI:SS')::TIMESTAMP without TIME zone
        INTO v_modifiedTime ;
        v_succFailFlag       :='FAILED';
        v_maxErrorFlag       :=FALSE;
        v_voucherNotFoundFlag:=FALSE;
        rcd_count            :=0;
        v_message            :='';
        v_returnLogMessage   :='';
        /* Check that the total invalid vouchers are less
        than the max error entries allowed */
        IF(v_errorCount<= p_maxErrorAllowed) THEN
          --Block for checking which vouchers are valid for the incoming new voucher status
          BEGIN
            RAISE NOTICE 'Before check_change_valid_proc  %',
            v_createdOn ;
            RAISE NOTICE 'Before check_change_valid_proc  %',
            v_seqId ;
            SELECT *
            INTO v_DamageStolenAfterEnCount ,
              v_vouchStat ,
              v_voucCurrStat ,
              v_productID ,
              v_LastRequestAttemptNo ,
              v_LastAttemptValue ,
              v_succFailFlag ,
              v_enableCount ,
              v_errorCount ,
              v_message ,
              v_DamageStolenCount ,
              v_onHoldCount,
              v_voucherNotFoundCount ,
              v_returnMessage ,
              v_returnLogMessage
            FROM VSC_CHECK_CHANGE_VALID_PROC( v_serialStart, v_voucherNotFoundFlag, v_processScreen ,v_createdOn , v_batchType, v_batchEnableStat , v_batchGenStat, v_EnableProcess , v_batchStolenStat , v_batchOnHoldStat, v_preActiveStat, v_suspendStat, v_ChangeProcess , v_wareHouseStat , v_batchPrintStat , v_batchReconcileStat , v_modifiedTime , v_RCAdminMaxdateallowed , v_ReconcileProcess, v_batchConStat,v_batchDamageStat, v_seqId,v_prefstring ) ;
            
			v_errorCountReal := v_errorCountReal + v_errorCount;
			v_errorCount := v_errorCountReal;
			
            IF(v_message         <> '') THEN
          
              p_response [p_responseIndex] := v_serialStart ||':' || v_message;
              p_responseIndex              := p_responseIndex+1;
              
            END IF;
            RAISE NOTICE 'AFTER check_change_valid_proc %,%, % ',
            v_vouchStat,
            v_voucCurrStat ,
            v_productID ;
          EXCEPTION
          WHEN SQLSTATE 'EXITE' THEN
            RAISE NOTICE 'EXCEPTION while checking if voucher is valid  =   % ',
            SQLERRM ;
            v_returnMessage              :='FAILED';
            
            p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while checking if voucher is valid';
            p_responseIndex              := p_responseIndex+1;
            RAISE
          EXCEPTION
            'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
            RAISE NOTICE 'others EXCEPTION while checking if voucher is valid  =   % ',
            SQLERRM ;
            v_returnMessage              :='FAILED';
            
            p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while checking if voucher is valid';
            p_responseIndex              := p_responseIndex+1;
            RAISE
          EXCEPTION
            'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
          END;
          RAISE NOTICE 'Iv_succFailFlag  =   % ',
          v_succFailFlag ;
          -- If vouchers are valid then perform these steps
          IF(v_succFailFlag='SUCCESS') THEN
            /* If vouchers are valid for change status and the new
            voucher status is of enable type then
            1. Update voucher Table */
            
            IF(p_batchType       =p_batchEnableStat AND v_processScreen=1) THEN
              BEGIN
                RAISE NOTICE 'vsc_update_voucher_enable before %, % , %, %, %, %, %, %',
                v_vouchstat,
                v_batchreconcilestat ,
                v_batchno,
                v_batchtype,
                v_modifiedby,
                v_modifiedtime,
                v_serialstart,
                v_seqId ;
                SELECT *
                INTO v_returnlogmessage,
                  v_returnmessage,
                  v_message
                FROM vsc_update_voucher_enable( v_vouchstat, v_batchreconcilestat , v_batchno, v_batchtype, v_modifiedby, v_modifiedtime, v_serialstart, v_seqId );
                
                p_response [p_responseIndex] := v_serialStart ||': Successfully updated!';
                p_responseIndex              := p_responseIndex+1;
                RAISE NOTICE 'vsc_update_voucher_enable after ';
              EXCEPTION
              WHEN SQLSTATE 'EXITE' THEN
                RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                v_returnMessage              :='FAILED';
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
              WHEN OTHERS THEN
                RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                v_returnMessage              :='FAILED';
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
              END;
            ELSIF(p_batchType=p_batchEnableStat AND (v_processScreen=2)) THEN
              BEGIN
                RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER before ';
                SELECT *
                INTO v_returnlogmessage,
                  v_returnmessage,
                  v_message
                FROM VSC_UPDATE_VOUCHER_ENABLE_OTHER( v_batchno, v_batchtype, v_modifiedby , v_modifiedtime , v_vouchStat , v_batchReconcileStat , v_voucCurrStat , v_serialStart , v_seqId,v_batchConStat ) ;
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'Successfuly Updated!';
                p_responseIndex              := p_responseIndex+1;
                RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER after ';
              EXCEPTION
              WHEN SQLSTATE 'EXITE' THEN
                v_returnMessage     :='FAILED';
                
                RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
              WHEN OTHERS THEN
                v_returnMessage     :='FAILED';
                
                RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
              END;
              /*code changed by kamini .
              elsif(p_batchType=p_batchEnableStat AND (v_processScreen=2 OR v_processScreen=3)) then*/
            ELSIF(p_batchType=p_batchEnableStat AND v_processScreen=3) THEN
              BEGIN
                RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER before ';
                SELECT *
                INTO v_returnlogmessage,
                  v_returnmessage,
                  v_message
                FROM VSC_UPDATE_VOUCHER_ENABLE_OTHER( v_batchno, v_batchtype, v_modifiedby , v_modifiedtime , v_vouchStat , v_batchReconcileStat , v_voucCurrStat , v_serialStart , v_seqId,v_batchConStat ) ;
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'Successfuly Updated!';
                p_responseIndex              := p_responseIndex+1;
                RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER after ';
              EXCEPTION
              WHEN SQLSTATE 'EXITE' THEN
                v_returnMessage     :='FAILED';
                
                RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
              WHEN OTHERS THEN
                v_returnMessage     :='FAILED';
                
                RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ',
                SQLERRM ;
                p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while updating vouchers for Enable type';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
              END;
              /* If new voucher status is other than enable
              then perform these steps
              1. Update Vouchers. */
            ELSE
              BEGIN
                RAISE NOTICE 'VSC_UPDATE_VOUCHERS before ';
                SELECT *
                INTO v_returnlogmessage,
                  v_returnmessage,
                  v_message
                FROM VSC_UPDATE_VOUCHERS( v_vouchStat, v_batchreconcilestat, v_batchno , v_batchtype , v_modifiedby , v_modifiedtime, v_serialstart , v_voucCurrStat , v_LastRequestAttemptNo, v_LastAttemptValue , v_seqId ) ;
                
                p_response [p_responseIndex] := v_serialStart ||': Successfully updated!';
                p_responseIndex              := p_responseIndex+1;
                RAISE NOTICE 'VSC_UPDATE_VOUCHERS after ';
              EXCEPTION
              WHEN SQLSTATE 'EXITE' THEN
                RAISE NOTICE 'EXCEPTION while updating vouchers    =  % ',
                SQLERRM ;
                v_returnMessage              :='FAILED';
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while updating vouchers';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
              WHEN OTHERS THEN
                RAISE NOTICE 'others EXCEPTION while updating vouchers  =  % ',
                SQLERRM ;
                v_returnMessage              :='FAILED';
                
                p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while updating vouchers';
                p_responseIndex              := p_responseIndex+1;
                RAISE
              EXCEPTION
                'others EXCEPTION while updating vouchers  ' USING ERRCODE = 'EXITE';
              END;
            END IF; --   en d of if(p_batchType=p_batchEnableStat)
          END IF;   --end of if(SUCCESS)
          IF(v_succFailFlag ='SUCCESS') THEN
            v_processStatus:=p_successProcessStatus; --store SU in status of VA table in case of success
            v_message      :='Success';
          ELSE
            v_processStatus:=p_errorProcessStatus; --store ER in status of VA table in case of error
          END IF;
          /* For all voucher status change log entry of each serial no
          in voucher udit table . Block for insertion in VA table */
          -- if condition added on 13/02/04 so that if voucher not found then
          -- that entry is not made in VA table
          IF(v_voucherNotFoundFlag=FALSE) THEN
            BEGIN
              RAISE NOTICE 'VSC_INSERT_IN_AUDIT_PROC before ';
              SELECT *
              INTO v_returnMessage,
                v_returnLogMessage,
                v_sqlErrorMessage
              FROM VSC_INSERT_IN_AUDIT_PROC( v_insertRowId, v_serialStart, v_batchType, v_vouchStat, v_modifiedBy, v_modifiedTime, v_batchNo, v_message, v_processStatus, v_row_id ) ;
              
              RAISE NOTICE 'VSC_INSERT_IN_AUDIT_PROC after %, %, %',
              v_returnMessage,
              v_returnLogMessage,
              v_sqlErrorMessage ;
            EXCEPTION
            WHEN SQLSTATE 'EXITE' THEN
              v_returnMessage     :='FAILED';
              
              RAISE NOTICE 'EXCEPTION while inserting in VA table = % ',
              SQLERRM ;
              p_response [p_responseIndex] := v_serialStart ||':' || 'EXCEPTION while inserting in VA table';
              p_responseIndex              := p_responseIndex+1;
              RAISE
            EXCEPTION
              'EXCEPTION while inserting in VA table = ' USING ERRCODE = 'EXITE';
            WHEN OTHERS THEN
              v_returnMessage     :='FAILED';
              
              RAISE NOTICE 'others EXCEPTION while inserting in VA table  = % ',
              SQLERRM ;
              p_response [p_responseIndex] := v_serialStart ||':' || 'others EXCEPTION while inserting in VA table';
              p_responseIndex              := p_responseIndex+1;
              RAISE
            EXCEPTION
              'others EXCEPTION while inserting in VA table ' USING ERRCODE = 'EXITE';
            END;  -- end of inserting record in voucher_audit table
          END IF; -- end of  if(v_voucherNotFoundFlag=false)
        ELSE      -- Else of exceeding the max error allowed
          v_succFailFlag               :='FAILED';
          v_returnMessage              :='FAILED';
         
          v_maxErrorFlag               :=TRUE;
          v_message                    :='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
          v_returnLogMessage           :='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
          p_response [p_responseIndex] := v_serialStart ||':' || v_message;
          p_responseIndex              := p_responseIndex+1;
          RAISE
        EXCEPTION
          'Exceeded the max error ' USING ERRCODE = 'EXITE';
        END IF;
        v_serialStart:=v_serialStart::bigint+1; -- incrementing from serial no by 1
        v_serialStart:=LPAD(v_serialStart,v_serialNoLength,'0');
        RAISE NOTICE 'v_serialStart after incrementing = % ',
        v_serialStart ;
        /* catch the Exception of type EXITEXCEPTION thrown above */
      EXCEPTION
      WHEN SQLSTATE 'EXITE' THEN
        v_returnMessage     :='FAILED';
        
        RAISE NOTICE 'EXCEPTION in while loop % ',
        SQLERRM ;
        RAISE
      EXCEPTION
        'FAILED ' USING ERRCODE = 'EXITE';
      WHEN OTHERS THEN
        v_returnMessage     :='FAILED';
        
        RAISE NOTICE 'EXCEPTION other in while loop % ',
        SQLERRM ;
        RAISE
      EXCEPTION
        'FAILED ' USING ERRCODE = 'EXITE';
      END;
    END LOOP; -- end of while loop
    RAISE NOTICE 'v_serialStart after loop =% ',
    v_serialStart ;
    /*    Update the Voucher batch and voucher summary  Table */
    BEGIN
      RAISE NOTICE 'VSC_INSERT_IN_SUMMARY_PROC before ';
      SELECT *
      INTO v_returnlogmessage ,
        v_returnmessage
      FROM VSC_INSERT_IN_SUMMARY_PROC( v_enableCount, v_counsumedCount, v_DamageStolenCount , v_DamageStolenAfterEnCount, v_referenceNo, v_createdOn, v_productID, v_networkCode, v_onHoldCount, rcd_count, p_fromSerialNo ) ;
      
      RAISE NOTICE 'VSC_INSERT_IN_SUMMARY_PROC after ';
    EXCEPTION
    WHEN SQLSTATE 'EXITE' THEN
      v_returnMessage     :='FAILED';
      
      RAISE NOTICE 'EXCEPTION while inserting in summary table  =% ',
      SQLERRM ;
      RAISE
    EXCEPTION
      'EXCEPTION while inserting in summary table' USING ERRCODE = 'EXITE';
   WHEN OTHERS THEN
      v_returnMessage     :='FAILED';
      
      RAISE NOTICE 'others EXCEPTION while inserting in summary table  =% ',
      SQLERRM ;
      RAISE
    EXCEPTION
      'others EXCEPTION while inserting in summary table ' USING ERRCODE = 'EXITE';
    END;
    --COMMIT;  --final commit
    /* If all the entries are inavlid for status change then
    return FAILED else return SUCCESS . Also return the message that
    needs to be written in the log file. */
    IF(p_noOfVouchers    =v_errorCount) THEN
      p_returnMessage   :='FAILED';
      p_returnLogMessage:='Vouchers is in '||v_vouchStat||' and cannot be made to '||v_batchType||'';
    ELSIF(v_maxErrorFlag =TRUE) THEN
      p_returnMessage   :='FAILED';
      p_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
    ELSIF(v_returnMessage='FAILED') THEN
      p_returnMessage   :='FAILED';
      p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
    ELSE
      p_returnMessage         :='SUCCESS';
      IF(v_voucherNotFoundCount>0) THEN
        p_returnLogMessage    :='Successfully changed status with '||v_voucherNotFoundCount||' vouchers not found';
      ELSIF(v_errorCount       > 0) THEN
        p_returnLogMessage    :='Not able to update the status to '||v_batchType||' of  '|| v_errorCount ||' vouchers';
      ELSE
        p_returnLogMessage:='Successfully changed status of '||p_noOfVouchers||' vouchers'  ;
      END IF;
    END IF;

    --p_response [p_responseIndex] := 'Summary:' || p_returnLogMessage;
    p_responseIndex              := p_responseIndex+1;
  EXCEPTION
  WHEN SQLSTATE 'EXITE' THEN
    p_returnMessage:='FAILED';
    --p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
    p_returnLogMessage:='SQL Not able to update the vouchers status to '||v_batchType;
    p_sqlErrorMessage :=v_sqlErrorMessage;
    RAISE NOTICE 'Procedure Exiting% ',
    SQLERRM ;
  WHEN OTHERS THEN
    p_returnMessage:='FAILED';
    --p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
    p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
    p_sqlErrorMessage :=v_sqlErrorMessage;
    RAISE NOTICE 'Procedure Exiting% ',
    SQLERRM ;
    --ROLLBACK;
  END; --Rollback in case of Exception
$function$



CREATE OR REPLACE FUNCTION vsc_check_change_valid_proc(
	v_serialstart character varying,
	v_vouchernotfoundflag boolean,
	v_processscreen integer,
	v_createdon timestamp without time zone,
	v_batchtype character varying,
	v_batchenablestat character varying,
	v_batchgenstat character varying,
	v_enableprocess character varying,
	v_batchstolenstat character varying,
	v_batchonholdstat character varying,
	v_preactivestat character varying,
	v_suspendstat character varying,
	v_changeprocess character varying,
	v_warehousestat character varying,
	v_batchprintstat character varying,
	v_batchreconcilestat character varying,
	v_modifiedtime timestamp without time zone,
	v_rcadminmaxdateallowed integer,
	v_reconcileprocess character varying,
	v_batchconstat character varying,
	v_batchdamagestat character varying,
	v_seqid integer,
	v_prefstring character varying,
	OUT v_damagestolenafterencount integer,
	OUT v_vouchstat character varying,
	OUT v_vouccurrstat character varying,
	OUT v_productid character varying,
	OUT v_lastrequestattemptno double precision,
	OUT v_lastattemptvalue numeric,
	OUT v_succfailflag character varying,
	OUT v_enablecount integer,
	OUT v_errorcount integer,
	OUT v_message character varying,
	OUT v_damagestolencount integer,
	OUT v_onholdcount integer,
	OUT v_vouchernotfoundcount integer,
	OUT v_returnmessage character varying,
	OUT v_returnlogmessage character varying)
    RETURNS record
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
AS $BODY$
DECLARE
v_expDate voms_vouchers.EXPIRY_DATE%type;
v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
v_prodNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
v_PreviousStatus voms_vouchers.PREVIOUS_STATUS%type;
v_LastConsumedOn voms_vouchers.LAST_CONSUMED_ON%type;
v_wareHouseCount INT;
v_suspendCount INT;
v_modifieddate date;
v_lastModifieddate date;
v_daysDifflastconCurrDate INT;
fromStatus TEXT ;
toStatus TEXT ;
splitVar TEXT ;
v_boolScreenOneOrTwoCheck BOOLEAN;

QUERY TEXT := '';

QRY_SELECT_VOMS_VOUCHERS TEXT :=' SELECT STATUS,CURRENT_STATUS,EXPIRY_DATE,GENERATION_BATCH_NO, PRODUCTION_NETWORK_CODE
         ,PRODUCT_ID,PREVIOUS_STATUS,LAST_CONSUMED_ON,LAST_REQUEST_ATTEMPT_NO,LAST_ATTEMPT_VALUE  
        FROM VOMS_VOUCHERS WHERE SERIAL_NO=$1 ';
         
QRY_SEQ_ID TEXT :=' AND SEQUENCE_ID=$2 ';

QRY_FOR_UPDATE TEXT :=  ' FOR UPDATE  ';

BEGIN

v_enableCount  :=0;
v_errorCount:=0;
v_DamageStolenCount :=0;
v_onHoldCount :=0;
v_wareHouseCount:=0;
v_suspendCount:=0;
v_daysDifflastconCurrDate := 0;
v_voucherNotFoundCount:=0;
v_returnMessage:='';
v_userNetworkCode:='';
v_DamageStolenAfterEnCount  :=0;
v_LastRequestAttemptNo  :=0.0;
v_LastAttemptValue  :=0.0 ;
v_message :='';
v_returnLogMessage :='';
fromStatus := '' ;
toStatus := '' ;
splitVar := '';
v_boolScreenOneOrTwoCheck := FALSE;
v_succFailFlag             := 'FAILED';
 RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started ' ;
 
         /* Get the voucher status abd then check whether that voucher
         is valid for status change or not */
	   IF(v_seqId = 0 ) THEN
		QUERY = QRY_SELECT_VOMS_VOUCHERS || QRY_FOR_UPDATE;
	   ELSE
		QUERY = QRY_SELECT_VOMS_VOUCHERS || QRY_SEQ_ID || QRY_FOR_UPDATE;
           END IF; 

		 RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started, QUERY =  % ', QUERY ;

		RAISE NOTICE 'Before Exceute query =  % ', v_serialStart;
		RAISE NOTICE 'Before Exceute query =  % ', v_seqId ;

		EXECUTE  QUERY USING v_serialStart,v_seqId into
		v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,
		v_prodNetworkCode,v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue ;
         
		RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started, Query Executed v_generationBatchNo %',v_generationBatchNo ;

		RAISE NOTICE '%,%,%,%,%',v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,v_prodNetworkCode;

		RAISE NOTICE '%,%,%,%,%',
		v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue ;

		/*initialization required in order to return value , value can not be null*/
		if(v_LastRequestAttemptNo is null) then
			v_LastRequestAttemptNo:=0;
		end if;
		if(v_LastAttemptValue is null) then 
			v_LastAttemptValue:=0;
		end if;
		
       /* Check whether that voucher has expired or not . If not then
    perform voucher valid for change status checking.*/
    BEGIN -- Begin of batch type checking
      IF(v_expDate>=v_createdOn) THEN
        FOR splitVar IN SELECT unnest( string_to_array(v_prefString, ',') ) 
		LOOP
		fromStatus :=split_part(splitVar, ':',1);
		toStatus :=split_part(splitVar, ':',2);
		  RAISE NOTICE 'fromStatus %',fromStatus; 
		  RAISE NOTICE ' toStatus %', toStatus;         
          IF v_processScreen =  1 THEN
			  IF fromStatus = 'GE' THEN
				v_boolScreenOneOrTwoCheck := TRUE;
			  ELSE
				v_boolScreenOneOrTwoCheck := FALSE;
			  END IF;
            ELSE
			  IF fromStatus =  'GE' THEN
				v_boolScreenOneOrTwoCheck := FALSE;
			  ELSE
				v_boolScreenOneOrTwoCheck := TRUE;
			  END IF;
            END IF;
          IF( (v_vouchStat = fromStatus)  AND (v_boolScreenOneOrTwoCheck = TRUE)) THEN
            IF(toStatus             =v_batchType)THEN
              v_succFailFlag       :='SUCCESS';
              IF(v_batchType        =v_batchEnableStat) THEN
                v_enableCount      :=v_enableCount+1;
				IF(v_vouchStat=v_batchReconcileStat) THEN 
					v_modifieddate:=(TRUNC(v_modifiedTime));
					v_lastModifieddate:=(TRUNC(v_LastConsumedOn));
					v_daysDifflastconCurrDate:=(v_modifieddate-v_lastModifieddate);
					IF v_daysDifflastconCurrDate<=v_RCAdminMaxdateallowed THEN
						v_succFailFlag:='SUCCESS';
					END IF;
				END IF;
              ELSIF(v_batchType     =v_batchStolenStat) THEN
					v_DamageStolenCount:=v_DamageStolenCount+1;
					 IF(v_vouchStat=v_batchEnableStat) THEN
						v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
					 ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
						v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
					 ELSIF(v_vouchStat=v_suspendStat) THEN 
						v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
					END IF;
              ELSIF(v_batchType     =v_batchDamageStat) THEN
					v_DamageStolenCount:=v_DamageStolenCount+1;
					IF(v_vouchStat=v_batchEnableStat) THEN
						v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
					ELSIF(v_vouchStat=v_batchOnHoldStat) THEN
						v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
					END IF;
              ELSIF(v_batchType     =v_batchOnHoldStat) THEN
                v_onHoldCount      :=v_onHoldCount+1;
              ELSIF(v_batchType     =v_wareHouseStat) THEN
                v_wareHouseCount   :=v_wareHouseCount+1;
              ELSIF(v_batchType     =v_suspendStat)THEN
                v_suspendCount     :=v_suspendCount+1;
			   ELSIF(v_batchType=v_batchConStat)  THEN
					IF(v_vouchStat=v_batchReconcileStat) THEN
					v_succFailFlag:='SUCCESS';
					END IF; 
              END IF;
              EXIT;
            END IF;
          END IF;
        END LOOP;
        IF(v_succFailFlag='FAILED')THEN
          v_errorCount  :=v_errorCount+1;
          v_message     :='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' this Screen';
        END IF;
      END IF;
    END;
     
v_returnMessage:='Function : VSC_CHECK_CHANGE_VALID_PROC executed successfully ';
v_returnLogMessage :=v_returnMessage;
  RAISE NOTICE '%',v_returnMessage;
EXCEPTION
           WHEN NO_DATA_FOUND THEN
              v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='NO Record found for voucher in vouchers table';
              v_voucherNotFoundCount:=v_voucherNotFoundCount+1;
              v_voucherNotFoundFlag:=TRUE;
              RAISE NOTICE '%','NO Record found for voucher in vouchers table'||v_serialStart;
         WHEN OTHERS THEN
               RAISE NOTICE '%','SQL Exception for updating records '||SQLERRM;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking for voucher status in vouchers table'||v_serialStart||SQLERRM;
               RAISE EXCEPTION 'Exception while checking for voucher status in vouchers table' USING ERRCODE = 'EXITE';
END;
$BODY$;



CREATE OR REPLACE FUNCTION vsc_update_voucher_enable_other(
	v_batchno character varying,
	v_batchtype character varying,
	v_modifiedby character varying,
	v_modifiedtime timestamp without time zone,
	v_vouchstat character varying,
	v_batchreconcilestat character varying,
	v_vouccurrstat character varying,
	v_serialstart character varying,
	v_seqid integer,
	v_batchconstat character varying,
	OUT v_returnlogmessage character varying,
	OUT v_returnmessage character varying,
	OUT v_message character varying)
    RETURNS record
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
AS $BODY$
DECLARE
FUNC_NAME TEXT :='VSC_UPDATE_VOUCHER_ENABLE_OTHER';
QRY_WHEN_STATUS_RECON TEXT :=' UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=$1,STATUS=$2,CURRENT_STATUS=$3,LAST_BATCH_NO=$4,
            MODIFIED_BY=$5,MODIFIED_ON=$6,
            PREVIOUS_STATUS=$7
            WHERE serial_no=$8 ';
          
QRY_WHEN_STATUS_CON TEXT :=' UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=$1,CURRENT_STATUS=$2,LAST_BATCH_NO=$3,
            MODIFIED_BY=$4,MODIFIED_ON=$5,
            PREVIOUS_STATUS=$6
            WHERE serial_no=$7 ';
			
QRY_WHEN_STATUS_OTHER TEXT :=' UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=$1,STATUS=$2,CURRENT_STATUS=$3,
            MODIFIED_BY=$4,MODIFIED_ON=$5,LAST_BATCH_NO=$6,PREVIOUS_STATUS= $7
            WHERE serial_no=$8 ';		
        

BEGIN
RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER staterd ' ;

	   IF(v_seqId != 0 ) THEN
		QRY_WHEN_STATUS_RECON = QRY_WHEN_STATUS_RECON || ' AND SEQUENCE_ID= $9' ;
		QRY_WHEN_STATUS_CON = QRY_WHEN_STATUS_CON || ' AND SEQUENCE_ID= $8'  ;
		QRY_WHEN_STATUS_OTHER = QRY_WHEN_STATUS_OTHER || ' AND SEQUENCE_ID= $9'  ;
           END IF; 
           
	RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER, v_vouchStat= % v_batchReconcileStat= % v_voucCurrStat % ',v_vouchStat,v_batchReconcileStat, v_voucCurrStat ;

            BEGIN

           /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
		RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  status Reconcile ' ;
            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
	    RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_RECON before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_RECON using v_batchNo,v_batchType, v_batchType, v_batchNo, v_modifiedBy, v_modifiedTime, v_vouchStat, v_serialStart, v_seqId;
           RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_RECON after execution' ;
           
      

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_consumed before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_CON USING v_batchNo,v_batchType,v_batchNo,v_modifiedBy,v_modifiedTime,v_vouchStat,v_serialStart, v_seqId;
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_consumed after execution' ;
  
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_other before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_OTHER USING v_batchNo, v_batchType, v_batchType, v_modifiedBy, v_modifiedTime, v_batchNo, v_vouchStat, v_serialStart, v_seqId;
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_other after execution' ;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
           EXCEPTION
		WHEN OTHERS THEN
		 RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER, SQL Exception for updating records '||SQLERRM;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while UPDATING for voucher status in vouchers table'||SQLERRM;
               RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while checking for voucher status in vouchers table' USING ERRCODE = 'SQLEX';
           
           END;
           v_message:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Succesfully executed';
            RAISE NOTICE '%',v_message;
EXCEPTION
 when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
       RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Not able to update voucher in vouchers table'||SQLERRM;
        RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Not able to update voucher in vouchers table' USING ERRCODE = 'SQLEX';

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      v_returnLogMessage:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating voucher table'||v_serialStart;
        RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating records'||SQLERRM;
        RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating records' USING ERRCODE = 'EXITE';
END;
$BODY$;



--##########################################################################################################
--##
--##      PreTUPS_v7.16.0 DB Script
--##
--##########################################################################################################

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DVD_ORDER_BY_PARAMETERS', 'DVD ORDER BY PARAMETERS', 'SYSTEMPRF', 'STRING', 'EXPIRY_DATE , CREATED_ON, SERIAL_NO', NULL, NULL, 50, 'DVD ORDER BY PARAMETERS', 'N', 'Y', 'VMS', 'DVD ORDER BY PARAMETERS', now(), 'ADMIN', now(), 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ONLINE_CHANGE_STATUS_SYSTEM_LMT', 'Max system limit for change status ol', 'SYSTEMPRF', 'INT', '100', 0, 100000, 50, 'Maximum system limit for batches for online voucher change status', 'Y', 'Y', 'VMS', 'Maximum system level limit for online change status on batches', TIMESTAMP '2019-11-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-11 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ONLINE_CHANGE_STATUS_NETWORK_LMT', 'Max network limit for change status ol', 'SYSTEMPRF', 'INT', '100', 0, 100000, 50, 'Maximum network limit for batches for online voucher change status', 'Y', 'Y', 'VMS', 'Maximum network level limit for online change status on batches', TIMESTAMP '2019-11-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-11 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO PROCESS_STATUS
(process_id, start_date, scheduler_status, executed_upto, executed_on, expiry_time, before_interval, description, network_code, record_count)
VALUES('CHANGESTATUSONLINE', TIMESTAMP '2019-11-12 00:00:00.000000', 'C', TIMESTAMP '2019-11-12 00:00:00.000000', TIMESTAMP '2019-11-12 00:00:00.000000', 360, 1440, 'change status online', 'NG', 0);

INSERT INTO NETWORK_PREFERENCES
(network_code, preference_code, value, created_on, created_by, modified_on, modified_by)
VALUES('NG', 'ONLINE_CHANGE_STATUS_NETWORK_LMT', '20', TIMESTAMP '2019-11-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-12 00:00:00.000000', 'SU0001');

INSERT INTO NETWORK_PREFERENCES
(network_code, preference_code, value, created_on, created_by, modified_on, modified_by)
VALUES('PB', 'ONLINE_CHANGE_STATUS_NETWORK_LMT', '20', TIMESTAMP '2019-11-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-12 00:00:00.000000', 'SU0001');

INSERT INTO PROCESS_STATUS
(process_id, start_date, scheduler_status, executed_upto, executed_on, expiry_time, before_interval, description, network_code, record_count)
VALUES('CHNGESTATOLNWCOUNT', TIMESTAMP '2019-11-14 10:29:13.000000', 'C', TIMESTAMP '2019-11-14 10:29:13.000000', TIMESTAMP '2019-11-14 00:00:00.000000', 360, 1440, 'change status online', 'PB', 0);

INSERT INTO PROCESS_STATUS
(process_id, start_date, scheduler_status, executed_upto, executed_on, expiry_time, before_interval, description, network_code, record_count)
VALUES('CHNGESTATOLNWCOUNT', TIMESTAMP '2019-11-14 10:29:13.000000', 'C', TIMESTAMP '2019-11-14 10:29:13.000000', TIMESTAMP '2019-11-14 00:00:00.000000', 360, 1440, 'change status online', 'NG', 0);

ALTER TABLE VOMS_BATCHES ADD MASTER_BATCH_ID VARCHAR(15) default 'NA';

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DVD_BATCH_FILEEXT', 'Batch DVD download file ext', 'SYSTEMPRF', 'STRING', 'xls', NULL, NULL, 50, 'the values for extension can be csv or xls or xlsx', 'N', 'Y', 'DVD', 'Extension of file to be downloaded or uploaded for batch DVD in batch recharge management', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C', 'MAX_APPROVAL_LEVEL_C2C', 'SYSTEMPRF', 'STRING', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', '2019-11-11 00:00:00.000', 'ADMIN', '2019-11-11 00:00:00.000', 'SU0001', NULL, 'Y');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "NAME", EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TFINI', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfInitiateController', 'C2C initate', 'C2C initiate', 'Y', '2005-07-14 00:00:00.000', 'ADMIN', '2005-07-14 00:00:00.000', 'ADMIN', 'C2C initiate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CARD_GROUP_ALLOWED_CATEGORIES', 'Card group allowed categories', 'SYSTEMPRF', 'STRING', 'NWADM,SUNADM', NULL, NULL, 50, 'Card group allowed categories', 'Y', 'Y', 'VMS', 'Card group allowed categories',  NOW(), 'ADMIN', NOW(), 'ADMIN', NULL, 'N');
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TRANSFER_RULE_ALLOWED_CATEGORIES', 'Transfer rule allowed categories', 'SYSTEMPRF', 'STRING', 'NWADM,SUNADM', NULL, NULL, 50, 'Transfer rule allowed categories', 'Y', 'Y', 'VMS', 'Transfer rule allowed categories',  NOW(), 'ADMIN',  NOW(), 'ADMIN', NULL, 'N');




--##########################################################################################################
--##
--##      PreTUPS_v7.17.0 DB Script
--##
--##########################################################################################################

DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='MAX_APPROVAL_LEVEL_C2C';

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_TRANSFER', 'Max Approval level C2C for Transfer', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_INITIATE', 'Max Approval level C2C for Buy', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('C2C_ALLOWED_VOUCHER_LIST', 'List of vchr status allowed for c2c', 'SYSTEMPRF', 'STRING', 'EN,OH,PA,ST', NULL, 15, 50, 'List of vchr status allowed for c2c', 'N', 'Y', 'VOMS', 'Services', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'REST', '190', 'TRFVOMS', 'TRFVOMS_REST', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004292', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'EXTGW', '190', 'TRFVOMS', 'TRFVOMS_EXTGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004298', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'MAPPGW', '190', 'TRFVOMS', 'TRFVOMS_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004297', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'SMSC', '190', 'TRFVOMS', 'TRFVOMS_SMSC', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004296', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'USSD', '190', 'TRFVOMS', 'TRFVOMS_USSD', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004295', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'WEB', '190', 'TRFVOMS', 'TRFVOMS_WEB', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004294', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'EXTGW', '190', 'INIVOMS', 'INIVOMS_EXTGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004301', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'MAPPGW', '190', 'INIVOMS', 'INIVOMS_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004302', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'REST', '190', 'INIVOMS', 'INIVOMS_REST', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004306', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'SMSC', '190', 'INIVOMS', 'INIVOMS_SMSC', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004303', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'USSD', '190', 'INIVOMS', 'INIVOMS_USSD', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004304', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'WEB', '190', 'INIVOMS', 'INIVOMS_WEB', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004305', NULL, 'GTYPE,MSISDN');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REC_MSG_SEND_ALLOW_C2C', 'C2C Receiver message allow', 'C2CVOUCHER', 'BOOLEAN', 'true', NULL, NULL, 50, 'Service type wise receiver SMS message allow flag for C2C Voucher', 'Y', 'Y', 'C2S', 'Service type wise receiver SMS message allow flag.', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVOUCHER', 'C2S', 'PRE', NULL, 'com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherApprovalController', 'Voucher Approval', 'C2C Voucher Approval', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN', 'C2C Voucher Approval', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'EXTGW', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004251', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'REST', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', TIMESTAMP '2019-12-04 09:11:56.000000', 'SU0001', 'SVK0004250', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'USSD', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004252', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'WEB', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0004253', NULL, 'GTYPE,MSISDN');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', 'Max Approval level C2C Voucher for Transfer', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C_VOUCHER', 'Y', 'Y', 'C2S', 'Max appoval level C2C Voucher', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', 'Max Approval level C2C Voucher for Initiate', 'CATPRF', 'INT', NULL, 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C_VOUCHER', 'Y', 'Y', 'C2S', 'Max appoval level C2C Voucher', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CVOMSINI', 'Process c2c voms initiate', 'Process c2c voms initiate', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-rest-receiver/c2cvomstrfini', 'N', 'Y', 'C2CVOMSINI');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('INIVOMS', 'C2S', 'ALL', 'TYPE MSISDN2 PIN', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherC2CInitiateController', 'C2C Voucher Request', 'C2C Voucher Request', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'C2C Request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '0', NOW(), 'NGLA0000010113', NOW(), 'NGLA0000010113', 'CATPRF');


INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CTRFVCRINI', 'Process c2c transfer vcr inititae', 'Process c2c transfer vcr inititae', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-rest-receiver/c2cvomstrf', 'N', 'Y', 'C2CTRFVCRINI');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINI001', 'CHNL2CHNL', '/channelToChannelVoucherInitiateSearchAction.do?method=userSearch', 'C2C Transfer Voucher', 'Y', 50, '2', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINI002', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateSearchUserVoucher.jsp', 'C2C Transfer Voucher', 'N', 50, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateSearchUserVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINI003', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProductVoucher.jsp', 'C2C Transfer Voucher', 'N', 50, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProductVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINI004', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProductVoucher.jsp', 'C2C Transfer Voucher', 'N', 50, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProductVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINI01A', 'CHNL2CHNL', '/channelToChannelVoucherInitiateSearchAction.do?method=userSearch', 'C2C Transfer Voucher', 'N', 50, '2', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVINIDMM', 'CHNL2CHNL', '/channelToChannelVoucherInitiateSearchAction.do?method=userSearch', 'C2C Transfer Voucher', 'Y', 50, '1', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP101', 'CHNL2CHNL', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Voucher Approval 1', 'Y', 51, '2', '1', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP101A', 'CHNL2CHNL', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP102', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP103', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP104', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP105', 'CHNL2CHNL', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP106', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelOne.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelOne.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP109', 'CHNL2CHNL', '/jsp/channeltransfer/C2CFinalVoucher.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/C2CFinalVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP1DMM', 'CHNL2CHNL', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Voucher Approval 1', 'Y', 51, '1', '1', '/c2cVoucherTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAPR07', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetails.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetails.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAPR08', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirm.jsp', 'C2C Transfer Voucher Approval 1', 'N', 51, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirm.jsp');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP201', 'CHNL2CHNL', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo', 'C2C Transfer Voucher Approval 2', 'Y', 52, '2', '1', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP201A', 'CHNL2CHNL', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP202', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP203', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP204', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP205', 'CHNL2CHNL', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP206', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelTwo.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelTwo.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP207', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsTwo.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsTwo.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP208', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirmTwo.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirmTwo.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP209', 'CHNL2CHNL', '/jsp/channeltransfer/C2CFinalVoucher.jsp', 'C2C Transfer Voucher Approval 2', 'N', 52, '2', '1', '/jsp/channeltransfer/C2CFinalVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP2DMM', 'CHNL2CHNL', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo', 'C2C Transfer Voucher Approval 2', 'Y', 52, '1', '1', '/c2cVoucherTransferApprovalTwo.do?method=searchLevelTwo');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP301', 'CHNL2CHNL', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree', 'C2C Transfer Voucher Approval 3', 'Y', 53, '2', '1', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP301A', 'CHNL2CHNL', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP302', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/c2cTransferApprovalLvl1List.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP303', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP304', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP305', 'CHNL2CHNL', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/viewC2CTransferVoucherApproval.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP306', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelThree.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/c2cTransferVoucherDetailApprovalLevelThree.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP307', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsThree.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsThree.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP308', 'CHNL2CHNL', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirmThree.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/c2cApproveVoucherProductDetailsConfirmThree.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP309', 'CHNL2CHNL', '/jsp/channeltransfer/C2CFinalVoucher.jsp', 'C2C Transfer Voucher Approval 3', 'N', 53, '2', '1', '/jsp/channeltransfer/C2CFinalVoucher.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CVAP3DMM', 'CHNL2CHNL', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree', 'C2C Transfer Voucher Approval 3', 'Y', 53, '1', '1', '/c2cVoucherTransferApprovalThree.do?method=searchLevelThree');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYVDMM', 'CHNL2CHNL', '/channelToChannelBuyVoucherInitiateSearchAction.do?method=userSearchBuy', 'C2C Buy Voucher', 'Y', 49, '1', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYV001', 'CHNL2CHNL', '/channelToChannelBuyVoucherInitiateSearchAction.do?method=userSearchBuy', 'C2C Buy Voucher', 'Y', 49, '2', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYV01A', 'CHNL2CHNL', '/channelToChannelBuyVoucherInitiateSearchAction.do?method=userSearchBuy', 'C2C Buy Voucher', 'N', 49, '2', '1', '/pretups/channelToChannelVoucherInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYV002', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateSearchUserBuyVoucher.jsp', 'C2C Buy Voucher', 'N', 49, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYV003', 'CHNL2CHNL', '/jsp/channeltransfer/BuyVoucherOrderReqDetails.jsp', 'C2C Buy Voucher', 'N', 49, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CBUYV004', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProductBuyVoucher.jsp', 'C2C Buy Voucher', 'N', 49, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');







INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP101', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP101A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP102', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP103', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP104', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP1DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP105', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP106', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAPR08', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAP109', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR1', 'C2CVAPR07', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP201', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP201A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP202', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP203', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP204', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP2DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP205', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP206', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP207', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP208', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR2', 'C2CVAP209', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP307', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP301', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP301A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP302', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP303', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP304', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP3DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP305', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP306', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP308', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVCTRFAPR3', 'C2CVAP309', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINI001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINI004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINI01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINI002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINI003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CVINI', 'C2CVINIDMM', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYV001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYV002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYV003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYV004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYV01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CBUYVINI', 'C2CBUYVDMM', '1');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_EMAIL_NOTIFICATION', 'C2C Email Notification', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Email notification to the approvers should be sent or not', 'N', 'Y', 'C2S', 'Email notification to the approver should be sent or not', NOW(), 'ADMIN', NOW(), 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_SMS_NOTIFY', 'C2C Notification Message', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'notification for batch C2C transfer', 'Y', 'Y', 'C2C', 'Notification for batch C2C transfer', NOW(), 'ADMIN', NOW(), 'ADMIN', 'true,false', 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CAPPRRECEIVER', 'Process C2C Approval Request', 'Process C2C Approval Request', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-receiver/trfappr', 'N', 'Y', 'C2CAPPRRECEIVER');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TRFINI', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfInitiateController', 'C2C Request', 'C2C Request', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN', 'C2C Initiate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TRFAPPR', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfApprovalController', 'C2C Approval', 'C2C Approval', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN', 'C2C Approval', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'EXTGW', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK1000904', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'SMSC', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101010', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'EXTGW', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101018', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFINI', 'REST', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101013', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'SMSC', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4102014', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'USSD', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101011', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFAPPR', 'REST', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101014', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'USSD', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK4101017', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRF', 'C2C Transfer', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRFAPR1', 'C2C Transfer Approval 1', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRFAPR2', 'C2C Transfer Approval 2', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRFAPR3', 'C2C Transfer Approval 3', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRFINI', 'C2C Buy Stock', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');





INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRF', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRF', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRF', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRF', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRF', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRFINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRFINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRFINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRFINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRFINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRFAPR3', '1');





INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1006', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelOne.jsp', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelOne.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1005', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelOne.jsp', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelOne.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1DMM', 'CHNL2CHNL', '/c2cTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Approval 1', 'Y', 46, '1', '1', '/c2cTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1001', 'CHNL2CHNL', '/c2cTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Approval 1', 'Y', 46, '2', '1', '/c2cTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAP1001A', 'CHNL2CHNL', '/c2cTransferApprovalOne.do?method=searchLevelOne', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/c2cTransferApprovalOne.do?method=searchLevelOne');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1002', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1003', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR1004', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Approval 1', 'N', 46, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2006', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelTwo.jsp', 'C2C Transfer Approval 2', 'N', 47, '2', '1', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelTwo.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2DMM', 'CHNL2CHNL', '/channelToChannelInitiateSearchAction.do?method=userSearch', 'C2C Transfer Approval 2', 'Y', 47, '1', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2001', 'CHNL2CHNL', '/c2cTransferApprovalTwo.do?method=searchLevelTwo', 'C2C Transfer Approval 2', 'Y', 47, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAP2001A', 'CHNL2CHNL', '/channelToChannelInitiateSearchAction.do?method=userSearch', 'C2C Transfer Approval 2', 'N', 47, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2002', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp', 'C2C Transfer Approval 2', 'N', 47, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2003', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Approval 2', 'N', 47, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR2004', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Approval 2', 'N', 47, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3DMM', 'CHNL2CHNL', '/channelToChannelInitiateSearchAction.do?method=userSearch', 'C2C Transfer Approval 3', 'Y', 48, '1', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3001', 'CHNL2CHNL', '/c2cTransferApprovalThree.do?method=searchLevelThree', 'C2C Transfer Approval 3', 'Y', 48, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAP3001A', 'CHNL2CHNL', '/channelToChannelInitiateSearchAction.do?method=userSearch', 'C2C Transfer Approval 3', 'N', 48, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3002', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp', 'C2C Transfer Approval 3', 'N', 48, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateSearchUser.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3003', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp', 'C2C Transfer Approval 3', 'N', 48, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateViewProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3004', 'CHNL2CHNL', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp', 'C2C Transfer Approval 3', 'N', 48, '2', '1', '/jsp/channeltransfer/chnlToChnlInitiateConfirmProduct.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('C2CAPR3006', 'CHNL2CHNL', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelThree.jsp', 'C2C Transfer Approval 3', 'N', 48, '2', '1', '/jsp/channeltransfer/c2cTransferDetailApprovalLevelThree.jsp');






INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRF001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRF001A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRF002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRF003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRF004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRF', 'C2CTRFDMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINI001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINI001A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINI002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINI003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINI004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFINI', 'C2CINIDMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAP1001A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAP2001A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAP3001A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3003', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3004', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3DMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1006', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR2', 'C2CAPR2006', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR3', 'C2CAPR3006', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFAPR1', 'C2CAPR1005', '1');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('C2CVOUCHERAPPROVAL', 'SMSC', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', '2019-12-04 00:00:00.000', 'SU0001', '2019-12-04 00:00:00.000', 'SU0001', 'SVK0004255', NULL, 'GTYPE,MSISDN');


alter table channel_voucher_items add  TYPE VARCHAR(10) DEFAULT 'O2C';
alter table channel_voucher_items add  FIRST_LEVEL_APPROVED_QUANTITY VARCHAR(22) ;
alter table channel_voucher_items add  SECOND_LEVEL_APPROVED_QUANTITY VARCHAR(22);
alter table channel_voucher_items add  INITIATED_QUANTITY VARCHAR(22);
alter table channel_voucher_items add  FROM_USER VARCHAR(15);
alter table channel_voucher_items add  TO_USER VARCHAR(15) ;
alter table channel_voucher_items add  MODIFIED_ON TIMESTAMP;


--##########################################################################################################
--##
--##      PreTUPS_v7.18.0 DB Script
--##
--##########################################################################################################

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('C2CPMTYP', 'Payment Type', NOW(), 'ADMIN', NOW(), 'ADMIN', 'N');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('DD', 'Demand Draft', 'C2CPMTYP', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CHQ', 'Cheque', 'C2CPMTYP', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('OTH', 'Others', 'C2CPMTYP', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CASH', 'Cash', 'C2CPMTYP', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN');>>>>>>> .r70442


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'O2CTRFRPT', 'Voucher transaction report', 'Channel Reports-O2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'O2CTRFRPT', 'Voucher transaction report', 'Channel Reports-O2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPTO2CV001', 'CHRPTO2C', '/o2cTrfVoucherRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher transaction report', 'Y', 53, '1', '1', '/reportsO2C/o2cTransferDetails.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPTO2CV01A', 'CHRPTO2C', '/o2cTrfVoucherRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher transaction report', 'N', 53, '2', '1', '/reportsO2C/o2cTransferDetails.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPTO2CVDMM', 'CHRPTO2C', '/o2cTrfVoucherRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher transaction report', 'Y', 53, '2', '1', '/reportsO2C/o2cTransferDetails.form');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('O2CTRFRPT', 'RPTO2CV001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('O2CTRFRPT', 'RPTO2CV01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('O2CTRFRPT', 'RPTO2CVDMM', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'O2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'O2CTRFRPT', '1');


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'C2CTRFRPT', 'Voucher transaction report', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CTRFRPT', 'Voucher transaction report', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'C2CTRFRPT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'C2CTRFRPT', '1');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPC2CTR001', 'CHRPTC2C', '/c2cTrfVoucherRpt.do?method=loadc2cNlevelTrackingVoucher', 'Voucher transaction report', 'Y', 355, '1', '1', '/pretups/Channel2ChannelTransferReport.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPC2CTR01A', 'CHRPTC2C', '/c2cTrfVoucherRpt.do?method=loadc2cNlevelTrackingVoucher', 'Voucher transaction report', 'Y', 355, '1', '1', '/pretups/Channel2ChannelTransferReport.form');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('RPC2CTRDMM', 'CHRPTC2C', '/c2cTrfVoucherRpt.do?method=loadc2cNlevelTrackingVoucher', 'Voucher transaction report', 'Y', 355, '2', '1', '/pretups/Channel2ChannelTransferReport.form');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFRPT', 'RPC2CTR001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFRPT', 'RPC2CTR01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('C2CTRFRPT', 'RPC2CTRDMM', '1');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2CVCRPT_DATEDIFF', 'Date diff for c2c voucher tracking report', 'SYSTEMPRF', 'INT', '40', 1, 15, 50, 'Number of days of the date difference for the c2c voucher transfer tracking report', 'N', 'Y', 'C2C', 'Maximum number of days of the date difference for c2c voucher transfer tracking', NOW(), 'ADMIN', NOW() 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('O2CVCRPT_DATEDIFF', 'Date diff for o2c voucher tracking report', 'SYSTEMPRF', 'INT', '15', 1, 15, 50, 'Number of days of the date difference for the o2c voucher transfer tracking report', 'N', 'Y', 'O2C', 'Maximum number of days of the date difference for o2c voucher transfer tracking', NOW(), 'ADMIN', NOW(), 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PAYMENTDETAILSMANDATE_C2C', 'Payment Details Mandatory C2C', 'SYSTEMPRF', 'INT', '0', 0, 1, 50, 'For C2C Payment Details will be mandatory to enter at which level', 'N', 'Y', 'C2C', 'Payment Details Mandatory for C2C', NOW(), 'ADMIN', NOW(), 'ADMIN', '0,1,2,3', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_ALLOW_CONTENT_TYPE', 'C2C Allowed Content Type', 'SYSTEMPRF', 'STRING', 'pdf,png,jpg', NULL, NULL, 50, 'C2C Allowed Content Type', 'N', 'N', 'C2C', 'C2C Allowed Content Type', NOW(), 'ADMIN', NOW() , 'ADMIN', NULL, 'Y');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('DD', 'Demand Draft', 'C2CPMTYP', 'Y',  NOW(), 'ADMIN',  NOW(), 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CHQ', 'Cheque', 'C2CPMTYP', 'Y',  NOW(), 'ADMIN',  NOW(), 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('OTH', 'Others', 'C2CPMTYP', 'Y',  NOW(), 'ADMIN',  NOW(), 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ONLINE', 'Online', 'C2CPMTYP', 'Y',  NOW(), 'ADMIN',  NOW(), 'ADMIN');


INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('C2CPMTYP', 'Payment Type', NOW(), 'ADMIN',  NOW(), 'ADMIN', 'N');


ALTERTABLEchannel_transfersaddAPPROVAL_DOCbytea;
ALTERTABLEchannel_transfersaddAPPROVAL_DOC_TYPEVARCHAR(100);
ALTERTABLEchannel_transfersaddAPPROVAL_DOC_FILE_PATHVARCHAR(500);

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PAYMENTDETAILSMANDATEVOUCHER_C2C', 'Payment Details Mandatory C2C Voucher', 'SYSTEMPRF', 'INT', '0', 0, 1, 50, 'For C2C Payment Details will be mandatory to enter at which level for voucher', 'N', 'Y', 'C2C', 'Payment Details Mandatory for C2C Voucher', NOW(), 'ADMIN', NOW(), 'ADMIN', '0,1,2,3', 'N');



----------------------------------------------- Bundle Management - DDL - Starts ------------------------------------------------------------
CREATE TABLE VOMS_BUNDLE_MASTER
(
  VOMS_BUNDLE_ID        NUMERIC(20)  PRIMARY KEY ,
  BUNDLE_NAME           VARCHAR(50 ),
  BUNDLE_PREFIX         VARCHAR(5 ),
  RETAIL_PRICE          NUMERIC,
  LAST_BUNDLE_SEQUENCE  NUMERIC(20),
  CREATED_ON            DATE,
  CREATED_BY            VARCHAR(50 ),
  MODIFIED_ON           DATE,
  MODIFIED_BY           VARCHAR(50 ),
  STATUS                VARCHAR(25 )
)
TABLESPACE PRTP_DATA;

CREATE TABLE VOMS_BUNDLE_DETAILS
(
  VOMS_BUNDLE_DETAIL_ID  NUMERIC(20)  PRIMARY KEY,
  VOMS_BUNDLE_ID         NUMERIC(20)  REFERENCES VOMS_BUNDLE_MASTER (VOMS_BUNDLE_ID),
  VOMS_BUNDLE_NAME       VARCHAR(50 ),
  PROFILE_ID             VARCHAR(5 ),
  QUANTITY               NUMERIC,
  CREATED_ON             DATE,
  CREATED_BY             VARCHAR(50 ),
  MODIFIED_ON            DATE,
  MODIFIED_BY            VARCHAR(50 ),
  STATUS                 VARCHAR(25 )
)
TABLESPACE PRTP_DATA;

	 ALTER TABLE VOMS_VOUCHERS
		ADD	 column BUNDLE_ID NUMERIC(20) , 
		ADD	 column  MASTER_SERIAL_NO NUMERIC(20) ;

ALTER TABLE CHANNEL_VOUCHER_ITEMS
	ADD	 column  BUNDLE_ID NUMERIC(20) ,
  ADD	 column  REMARKS VARCHAR(100) ;
	
	

CREATE TABLE MO_SO_NUMBER
(
  SOMOREFID          VARCHAR(50 ),
  FILENAME           VARCHAR(500 ),
  PROCESSINGDATE     DATE,
  FILEREFERENCE      VARCHAR(50 ),
  MONUMBER           VARCHAR(50 ),
  MOLINENUMBER       VARCHAR(50 ),
  DONUMBER           VARCHAR(50 ),
  WMSREFERENCE       VARCHAR(50 ),
  ORGCODE            VARCHAR(50 ),
  SUBINVENTORY       VARCHAR(50 ),
  ITEMCODE           VARCHAR(50 ),
  QUANTITY           VARCHAR(50 ),
  UOM                VARCHAR(50 ),
  FROMSERIAL_NUMBER  NUMERIC,
  TOSERIAL_NUMBER    NUMERIC,
  SEARCHKEY          NUMERIC,
  ACTUALFROMNO       NUMERIC,
  ACTUALTONO         NUMERIC
)
TABLESPACE PRTP_DATA;

----------------------------------------------- Bundle Management - DDL - ends ------------------------------------------------------------

----------------------------------------------- Bundle Management - DML - starts ------------------------------------------------------------

		
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CAPV107', 'OPT2CHNL', '/jsp/channeltransfer/viewPackageTransferApproval.jsp', 'Approve Level 1', 'N', 
    2, '2', '1', '/jsp/channeltransfer/viewPackageTransferApproval.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CAPV108', 'OPT2CHNL', '/jsp/channeltransfer/approvePackageProductDetails.jsp', 'Approve Level 1', 'N', 
    2, '2', '1', '/jsp/channeltransfer/viewPackageTransferApproval.jsp');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF010', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF011', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF013', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF014', 'OPT2CHNL', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/voucherProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF016', 'OPT2CHNL', '/jsp/channeltransfer/packageTransferDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageTransferDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF017', 'OPT2CHNL', '/jsp/channeltransfer/packageProductDetails.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageProductDetails.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF018', 'OPT2CHNL', '/jsp/channeltransfer/packageProductDetailsConfirm.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/packageProductDetailsConfirm.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('O2CTRF019', 'OPT2CHNL', '/jsp/channeltransfer/O2CpackageFinal.jsp', 'Initate Transfer', 'N', 
    1, '2', '1', '/jsp/channeltransfer/O2CpackageFinal.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF011', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF010', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF014', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF013', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF018', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF019', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF017', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('INITO2CTRF', 'O2CTRF016', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APV1O2CTRF', 'O2CAPV107', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('APV1O2CTRF', 'O2CAPV108', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD001', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'Y', 
    5, '2', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD001A', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'N', 
    5, '2', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD002', 'VOMSDWN', '/jsp/channeltransfer/enquirySearchAttribute.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquirySearchAttribute.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD003', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferSearchUser.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferSearchUser.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD004', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferList.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferList.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD005', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferView.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferView.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD006', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '2', '1', '/channeltransfer/O2Cenquiry.form');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD006A', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '2', '1', '/channeltransfer/O2Cenquiry.form');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD007', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferList.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferList.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD008', 'VOMSDWN', '/jsp/channeltransfer/enquiryTransferView.jsp', 'VOMS Download', 'N', 
    5, '2', '1', '/jsp/channeltransfer/enquiryTransferView.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD01', 'VOMSDWN', '/stock/o2cEnq_input.action?serviceType=O2C', 'VOMS Download', 'N', 
    1, '2', '2', '/stock/o2cEnq_input.action?serviceType=O2C');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHD1DM', 'VOMSDWN', '/stock/o2cEnq_input.action?serviceType=O2C', 'VOMS Download', 'N', 
    1, '1', '2', '/stock/o2cEnq_input.action?serviceType=O2C');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHDDMM', 'VOMSDWN', '/voucherDownload.do?method=userSearchAttribute', 'VOMS Download', 'N', 
    5, '1', '1', '/channelTransferEnquiryAction.do?method=userSearchAttribute');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VOUCHDDMM2', 'VOMSDWN', '/voucherDownload.do?method=channelUserEnquiry', 'VOMS Download', 'N', 
    5, '1', '1', '/channeltransfer/O2Cenquiry.form');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD001A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD004', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD005', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD006', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD006A', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD007', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD008', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD01', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD1DM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHDDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHDDMM2', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('DOWNVOUCH', 'VOUCHD001', '1');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'DOWNVOUCH', 'Voucher download', 'Voucher Download', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
   
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('BCU', 'DOWNVOUCH', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVB001', 'VOUBUNDLE', '/addVoucherBundleAction.do?method=loadVoucherBundleList', 'Add voucher bundle', 'Y', 
    1, '2', '1', '/addVoucherBundleAction.do?method=loadVoucherBundleList');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVBDMM', 'VOUBUNDLE', '/addVoucherBundleAction.do?method=loadVoucherBundleList', 'Add voucher bundle', 'Y', 
    1, '1', '1', '/addVoucherBundleAction.do?method=loadVoucherBundleList');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVB001', 'VOUBUNDLE', '/viewVoucherBundleAction.do?method=viewVoucherBundles', 'View voucher bundle', 'Y', 
    3, '2', '1', '/viewVoucherBundleAction.do?method=viewVoucherBundles');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVBDMM', 'VOUBUNDLE', '/viewVoucherBundleAction.do?method=viewVoucherBundles', 'View voucher bundle', 'Y', 
    3, '1', '1', '/viewVoucherBundleAction.do?method=viewVoucherBundles');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB001', 'VOUBUNDLE', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify', 'Modify voucher bundle', 'Y', 
    2, '2', '1', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVBDMM', 'VOUBUNDLE', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify', 'Modify voucher bundle', 'Y', 
    2, '1', '1', '/modifyVoucherBundleAction.do?method=loadVoucherBundleListForModify');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB002', 'VOUBUNDLE', '/jsp/voucherbundle/modifyVoucherBundle.jsp', 'Modify voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/modifyVoucherBundle.jsp');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMVWVB002', 'VOUBUNDLE', '/jsp/voucherbundle/viewVoucherBundles.jsp', 'View voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/viewVoucherBundles.jsp');

Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUADDBUN', 'Add voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUVWBUN', 'View voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');
Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, IS_DEFAULT_GROUPROLE, IS_DEFAULT, ACCESS_TYPE, ROLE_FOR)
 Values
   ('OPERATOR', 'VOUMODBUN', 'Modify voucher bundle', 'Voucher bundle', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'N', 'N', 'B', 'B');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVBDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUVWBUN', 'VMVWVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUVWBUN', 'VMVWVBDMM', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVBDMM', '1');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUADDBUN', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUVWBUN', '1');
Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('SUADM', 'VOUMODBUN', '1');

Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('ALL', 'VOMS_BUNID', 'ALL', 200, TO_DATE('12/10/2019 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);
Insert into IDS
   (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, 
    FREQUENCY, DESCRIPTION)
 Values
   ('ALL', 'VOMS_DETID', 'ALL', 216, TO_DATE('10/14/2019 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'NA', NULL);

Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('ADDVOUBUN', 'Add Voucher Bundle', 'Add Voucher Bundle', NULL, 'com.btsl.voms.voucherbundle.web.VoucherBundleForm', 
    'configfiles/restservice', '/rest/voucherBundle/addVoucherBundle', 'N', 'Y', 'VOUADDBUN');
Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
 Values
   ('MODVOUBUN', 'Modify Voucher Bundle', 'Modify Voucher Bundle', NULL, 'com.btsl.voms.voucherbundle.web.VoucherBundleForm', 
    'configfiles/restservice', '/rest/voucherBundle/modifyVoucherBundle', 'N', 'Y', 'VOUMODBUN');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_BUN_PRE_ID_NULL_ALLOW', 'IS_BUN_PRE_ID_NULL_ALLOW', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS_BUN_PRE_ID_NULL_ALLOW', 'N', 
    'N', 'C2S', 'IS_BUN_PRE_ID_NULL_ALLOW', TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', 'N', 
    'N', 'C2S', 'IS_VOU_BUN_NAME_LEN_ZERO_ALLOW', TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVB002', 'VOUBUNDLE', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp', 'Add voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVB002', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB003', 'VOUBUNDLE', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp', 'Modify voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp');

Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB003', '1');

Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('BCA Format', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterBCA', 'VCH_FRMT', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterBCA');
Insert into KEY_VALUES
   (KEY, VALUE, TYPE, TEXT1)
 Values
   ('Print Vendor Format', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterPrintVendor', 'VCH_FRMT', 'com.btsl.pretups.channel.transfer.util.clientutils.FileWriterPrintVendor');

Insert into MO_SO_NUMBER
   (SOMOREFID, FILENAME, PROCESSINGDATE, FILEREFERENCE, MONUMBER, 
    MOLINENUMBER, DONUMBER, WMSREFERENCE, ORGCODE, SUBINVENTORY, 
    ITEMCODE, QUANTITY, UOM, FROMSERIAL_NUMBER, TOSERIAL_NUMBER, 
    SEARCHKEY, ACTUALFROMNO, ACTUALTONO)
 Values
   ('00001', 'FILENAME', TO_DATE('12/11/2019 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'REFERENCE', 'MO', 
    'LINE', '1', '2323324', '534534', 'INVETORY', 
    'ITEM', 'QUANTITY', 'UOM', 134200000005, 134200000005, 
    4, 534, 5345);
    
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'EN', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('PA', 'DA', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'CU', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('EN', 'DA', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('DA', 'EN', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('DA', 'PA', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');
Insert into VOMS_VOUCHERS_STATUS_MAPPING
   (STATUS, MAPPED_STATUS, NETWORK_CODE, CREATED_DATE, CREATED_BY, 
    MODIFIED_DATE, MODIFIED_BY)
 Values
   ('DA', 'CU', 'IN', TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS', 
    TO_DATE('01/06/2020 04:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYS');

ALTER TABLE LOOKUPS
ALTER COLUMN LOOKUP_CODE TYPE VARCHAR(15);

Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('NORMALMODE', 'Normal', 'O2CDM', 'Y', TO_DATE('11/01/2019 04:49:26', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/01/2019 04:49:26', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
    
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('PACKAGEMODE', 'Package', 'O2CDM', 'Y', TO_DATE('11/01/2019 04:49:41', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('11/01/2019 04:49:41', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

UPDATE SERVICE_TYPE SET MESSAGE_FORMAT = 'TYPE FROM_SERIALNO TO_SERIALNO STATUS MASTER_SERIALNO MSISDN', RESPONSE_PARAM = 'TYPE,FROM_SERIALNO,TO_SERIALNO,MASTER_SERIAL_NO,MSISDN,PRE_STATUS,REQ_STATUS,TXNSTATUS,MESSAGE' WHERE SERVICE_TYPE='VSCH';


UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,FROM_SERIALNO,TO_SERIALNO,MASTER_SERIALNO,MSISDN,STATUS,LOGINID,PASSWORD,EXTNWCODE' WHERE KEYWORD='VOMSSTCHGREQ';
 

Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('O2CDM', 'O2C Distribution Mode', TO_DATE('01/08/2020 21:18:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('01/08/2020 21:18:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');

Insert into MODULES
   (MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
 Values
   ('VOUBUNDLE', 'Voucher Bundle Management', 390, '1');

   
  Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMADVB002', 'VOUBUNDLE', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp', 'Add voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmAddVoucherBundle.jsp');
	
	Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
 Values
   ('VMMOVB003', 'VOUBUNDLE', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp', 'Modify voucher bundle', 'N', 
    2, '2', '1', '/jsp/voucherbundle/confirmModifyVoucherBundle.jsp');
	
	Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_BLANK_VOUCHER_REQ', 'IS_BLANK_VOUCHER_REQ', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'IS_BLANK_VOUCHER_REQ', 'N', 
    'N', 'C2S', 'IS_BLANK_VOUCHER_REQ', TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/30/2019 06:40:25', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
	
	
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUADDBUN', 'VMADVB002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUMODBUN', 'VMMOVB003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('VOUVWBUN', 'VMVWVB002', '1');
 COMMIT;

 Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('ALPHANUM_SPCL_REGEX', 'Ext contains alphaNum with specials(!@#$%*-)', 'SYSTEMPRF', 'STRING', '[a-zA-Z\\d!@#$%*-]+?', 
    0, 9999, 50, 'Ext contains alphaNum with specials(!@#$%*-)', 'N', 
    'N', 'C2S', 'Ext contains alphaNum with specials(!@#$%*-)',now(), 'ADMIN', 
    now() ,'ADMIN', NULL, 'N');

----------------------------------------------- Bundle Management - DML - ends ------------------------------------------------------------
--##########################################################################################################
--##
--##      PreTUPS_v7.19.0 DB Script
--##
--##########################################################################################################

 ALTER TABLE VOMS_VOUCHERS ADD C2C_TRANSFER_DATE TIMESTAMP;
ALTER TABLE VOMS_VOUCHERS ADD C2C_TRANSFER_ID VARCHAR(20);

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VNDTLTR', 'Voucher N Level Detail Tracking', 'Voucher Tracking Report', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'VNDTLTR', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'VNDTLTR', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VNDTL001', 'CHRPTC2C', '/c2cNLevelDtlVoucherRpt.do?method=loadc2cNlevelDetailTrackingVoucher', 'Voucher Tracking Report', 'Y', 45, '1', '1', '/pretups/channelToChannelInitiateSearchAction.form');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VNDTLDMM', 'CHRPTC2C', '/c2cNLevelDtlVoucherRpt.do?method=loadc2cNlevelDetailTrackingVoucher', 'Voucher Tracking Report', 'Y', 45, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VNDTL001A', 'CHRPTC2C', '/c2cNLevelDtlVoucherRpt.do?method=loadc2cNlevelDetailTrackingVoucher', 'Voucher Tracking Report', 'N', 45, '2', '1', '/pretups/channelToChannelInitiateSearchAction.form');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VNDTLTR', 'VNDTL001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VNDTLTR', 'VNDTLDMM', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VNDTLTR', 'VNDTL001A', '1');


INSERT INTO SERVICE_TYPE
(service_type, module, "type", message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, "name", external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('C2CVTAPLST', 'C2S', 'PRE', NULL, 'com.restapi.channel.transfer.channelvoucherapproval.ChannelToChannelVoucherApprovalList', 'Voucher Approval List', 'C2C Voucher Approval List', 'Y', TIMESTAMP '2019-12-04 09:01:38.000000', 'ADMIN', TIMESTAMP '2019-12-04 09:01:50.000000', 'ADMIN', 'C2C Voucher Approval List', 'Y', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('C2CVCRAPPLIST', 'REST', '190', 'C2CVTAPLST', 'C2C_VOUCHER_APPROVAL_LIST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', TIMESTAMP '2019-12-04 09:11:56.000000', 'SU0001', 'SVK0004257', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('C2CVCRAPPLIST', 'MAPPGW', '190', 'C2CVTAPLST', 'C2C_VOUCHER_APPROVAL_LIST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', TIMESTAMP '2019-12-04 09:11:56.000000', 'SU0001', 'SVK410259', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');

INSERT INTO system_preferences
(preference_code, name, "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_NLEVEL', 'Date diff for reports for admin for Nlevel', 'SYSTEMPRF', 'INT', '5', 1, 30, 50, 'Number of days of the date n level for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', '2020-01-30 11:24:31.038', 'ADMIN', '2020-01-30 11:24:31.038', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, name, "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_AVAIL', 'Date diff for availability report for admin', 'SYSTEMPRF', 'INT', '10', 1, 30, 50, 'Number of days of the date difference for the availability report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', '2020-01-30 11:24:31.012', 'ADMIN', '2020-01-30 11:24:31.012', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, name, "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('REPORT_MAX_DATEDIFF_USER_CONS', 'Date difference for reports for user', 'SYSTEMPRF', 'INT', '10', 1, 30, 50, 'Number of days of the date difference for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', '2020-01-30 11:24:30.984', 'ADMIN', '2020-01-30 11:24:30.984', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, name, "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_CONS', 'Date difference for reports for admin', 'SYSTEMPRF', 'INT', '5', 1, 30, 50, 'Number of days of the date difference for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', '2020-01-30 11:24:30.942', 'ADMIN', '2020-01-30 11:24:30.942', 'ADMIN', NULL, 'Y');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOCNRPT001', 'CHRPTC2C', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails','Voucher consump report - channel', 'Y', 283, '2', '1', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOCNRPT01A', 'CHRPTC2C', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher consump report - channel', 'N', 283, '2', '1', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOCNRPTDMM', 'CHRPTC2C', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher consump report - channel', 'Y', 283, '1', '1', '/voucherConsumeRpt.do?method=loadO2cVoucherTxnDetails');


INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUCNREPORT', 'VOCNRPT001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUCNREPORT', 'VOCNRPT01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUCNREPORT', 'VOCNRPTDMM', '1');



INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('COMP_SHOP', 'VOUCNREPORT', 'Voucher consumption report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('CORPORATE', 'VOUCNREPORT', 'Voucher consumption report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'VOUCNREPORT', 'Voucher consumption report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VOUCNREPORT', 'Voucher consumption report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('CORPE', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'VOUCNREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'VOUCNREPORT', '1');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVWTRFVC', 'C2S', 'ALL', 'TYPE MSISDN PIN TRANSFERID TRANSFERTYPE NETWORKCODE NETWORKCODEFOR', 'com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherTransferDetailsController', 'C2C Request', 'C2C Request', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'C2C View Transfer Voucher', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,IMEI,PIN,TRANSFERID,TRANSFERTYPE,NETWORKCODE,NETWORKCODEFOR,LANGUAGE1', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVOUCHER', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherApprovalController', 'Voucher Approval', 'C2C Voucher Approval', 'Y', TIMESTAMP '2019-12-04 09:01:38.000000', 'ADMIN', TIMESTAMP '2019-12-04 09:01:50.000000', 'ADMIN', 'C2C Voucher Approval', 'Y', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVIEWVC', 'REST', '190', 'C2CVWTRFVC', 'C2C_TRF_VOMS_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-01-15 09:31:25.000000', 'SU0001', TIMESTAMP '2020-01-15 09:31:25.000000', 'SU0001', 'SVK4101015', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVIEWVC', 'MAPPGW', '190', 'C2CVWTRFVC', 'C2C_TRF_VOMS_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-01-15 09:31:41.000000', 'SU0001', TIMESTAMP '2020-01-15 09:31:41.000000', 'SU0001', 'SVK4101016', NULL, 'TYPE,MSISDN,IMEI,PIN,TRANSFERID,TRANSFERTYPE,NETWORKCODE,NETWORKCODEFOR,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'MAPPGW', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004259', NULL, 'TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MIN_LAST_DAYS_CG', 'Minimum Days for card group set version', 'SYSTEMPRF', 'INT', '1', 1, 365, 365, 'Minimum Days for card group set version', 'N', 'Y', 'C2S', 'Minimum Days for card group set version', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-25 15:20:27.000000', 'SU0001', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_LAST_DAYS_CG', 'Maximum Days for card group set version', 'SYSTEMPRF', 'INT', '365', 2, 365, 365, 'Maximum Days for card group set version', 'N', 'Y', 'C2S', 'Maximum Days for card group set version', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-25 15:20:27.000000', 'SU0001', NULL, 'Y');


INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOAVRPT001', 'CHRPTC2C', '/vouAvailableRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher available report - channel', 'Y', 282, '2', '1', '/pretups/Channel2ChannelTransferReport.form');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOAVRPT01A', 'CHRPTC2C', '/vouAvailableRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher available report - channel', 'N', 282, '2', '1', '/pretups/Channel2ChannelTransferReport.form');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('VOAVRPTDMM', 'CHRPTC2C', '/vouAvailableRpt.do?method=loadO2cVoucherTxnDetails', 'Voucher available report - channel', 'Y', 282, '1', '1', '/pretups/Channel2ChannelTransferReport.form');



INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUAVAREPORT', 'VOAVRPT001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUAVAREPORT', 'VOAVRPT01A', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('VOUAVAREPORT', 'VOAVRPTDMM', '1');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('COMP_SHOP', 'VOUAVAREPORT', 'Voucher available report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('CORPORATE', 'VOUAVAREPORT', 'Voucher available report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'VOUAVAREPORT', 'Voucher available report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'VOUAVAREPORT', 'Voucher available report - channel', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('CORPE', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'VOUAVAREPORT', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'VOUAVAREPORT', '1');



--##########################################################################################################
--##
--##      PreTUPS_v7.20.0 DB Script
--##
--##########################################################################################################


UPDATE SERVICE_TYPE
SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN'
WHERE SERVICE_TYPE = 'RET';

UPDATE SERVICE_TYPE
SET MESSAGE_FORMAT = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,PIN,LANGUAGE1,LANGUAGE2'
WHERE SERVICE_TYPE = 'RET';

UPDATE SERVICE_KEYWORDS
SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,LANGUAGE2,MHASH,TOKEN'
WHERE SERVICE_TYPE = 'RET' AND REQ_INTERFACE_TYPE = 'MAPPGW'

UPDATE SERVICE_TYPE
SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN'
WHERE SERVICE_TYPE = 'TRF';

UPDATE SERVICE_TYPE
SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,PIN,LANGUAGE1'
WHERE SERVICE_TYPE = 'TRF'

UPDATE SERVICE_KEYWORDS
SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,PIN,LANGUAGE1'
WHERE SERVICE_TYPE = 'TRF' AND REQ_INTERFACE_TYPE = 'MAPPGW'

UPDATE SERVICE_TYPE
SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN'
WHERE SERVICE_TYPE = 'WD';

UPDATE SERVICE_TYPE
SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,PIN,LANGUAGE1,LANGUAGE2'
WHERE SERVICE_TYPE = 'WD';

UPDATE SERVICE_KEYWORDS
SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,LANGUAGE2,MHASH,TOKEN'
WHERE SERVICE_TYPE = 'WD' AND REQ_INTERFACE_TYPE = 'MAPPGW'

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVTYPE', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherInfoServices', 'Voucher Type Info', 'Voucher Type Info', 'Y', TIMESTAMP '2020-02-11 13:03:06.000000', 'ADMIN', TIMESTAMP '2020-02-11 13:03:06.000000', 'ADMIN', 'C2C Voucher Type Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVSEG', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherSegmentInfo', 'Voucher Segment Info', 'Voucher Segment Info', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN', 'C2C Voucher Segment Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVDEN', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherDenominationInfo', 'Voucher Segment Info', 'Voucher Segment Info', 'Y', NOW(), 'ADMIN', NOW(), 'ADMIN', 'C2C Voucher Segment Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVTYPE', 'MAPPGW', '190', 'C2CVTYPE', 'C2CVTYPE', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK9001016', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVSEG', 'MAPPGW', '190', 'C2CVSEG', 'C2CVSEG', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK9001017', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN,VTYPE');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVDEN', 'MAPPGW', '190', 'C2CVDEN', 'C2CVDEN', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK9001018', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN,VTYPE,VSEG');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_ALLOWED_VINFO', 'User allowed for voucher API', 'SYSTEMPRF', 'STRING', 'SUBCU,BCU,DIST,SE,SUADM,SUNADM,NWADM,SSADM,AG,RET', 0, 9999, 50, 'User allowed for voucher info services', 'N', 'Y', 'C2S', 'User allowed for voucher type APIs', NOW(), 'ADMIN', NOW(), 'ADMIN', NULL, 'N');

DELETE FROM roles
WHERE role_code='NETWORKVASMAPPING' AND domain_type='OPERATOR';


--##########################################################################################################
--##
--##      PreTUPS_v7.21.0 DB Script
--##
--##########################################################################################################
INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, name, status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('USRDETAILS', 'MAPPGW', '190', 'USRDETAILS', 'USRDETAILS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-02-25 00:00:00.000000', 'SU0001', TIMESTAMP '2020-02-25 00:00:00.000000', 'SU0001', 'SVK3001170', NULL, 'TYPE,MSISDN,MSISDN2,PIN,IMEI,LANGUAGE1');

INSERT INTO service_type
(service_type, module, "type", message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, "name", external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('USRDETAILS', 'C2S', 'ALL', 'TYPE MSISDN2 PIN', 'com.btsl.pretups.user.requesthandler.ChannelUserDetailsController', 'User Details', 'User details', 'Y', TIMESTAMP '2020-02-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-02-25 00:00:00.000000', 'ADMIN', 'User Details', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,PIN,IMEI,LANGUAGE1,LANGUAGE2', 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CVOUCHERAPP', 'C2C Voucher Approval API', 'RestReceiver', 'configfiles/restrictedsubs/restricted-subs-validator.xml', NULL, 'configfiles/restservice', '/rest/c2s-rest-receiver/c2cvoucherapproval', 'Y', 'Y', NULL);


INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CBUYENQ', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CUserBuyEnquiryController', 'C2C Buy User Enquiry', 'C2C Buy User Enquiry', 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', 'C2C Buy User Enquiry', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'MAPPGW', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004260', NULL, 'TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'REST', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004261', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'WEB', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004262', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'USSD', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004263', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'EXTGW', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004264', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CBUYUSENQ', 'SMSC', '190', 'C2CBUYENQ', 'C2C_USER_BUY_ENQ', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004265', NULL, 'GTYPE,MSISDN');

INSERT INTO  SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('RECENT_C2C_TXN', 'Recent c2c transactions of a user', 'SYSTEMPRF', 'INT', '2', 1, 365, 365, 'Recent c2c transactions of a user', 'N', 'Y', 'C2S', 'Recent c2c transactions of a user', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-25 15:20:27.000000', 'SU0001', NULL, 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2STRANS', 'C2S', 'PRE', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2STransactionController', 'C2S Transactions', 'C2S Transactions', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'Transactions', 'N', 'N', 'Y', NULL, 'Y', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN1,MSISDN2,LOGINID,PASSWORD,AMOUNT,SELECTOR,IMEI,PIN,LANGUAGE1,LANGUAGE2', 'Y');



INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'EXTGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000333', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'MAPPGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000334', NULL, 'TYPE,MSISDN,IMEI,MHASH,TOKEN,FROMDATE,TODATE,SERVICETYPE');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'USSD', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000335', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'WEB', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000336', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'REST', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000337', NULL, 'GTYPE,MSISDN');




INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2STRFCNT', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2STransferServiceTotalAmountController', 'C2S Service Enquiry', 'C2S Service Enquiry', 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', 'C2C Buy User Enquiry', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SSRVTRFCNT', 'EXTGW', '190', 'C2STRFCNT', 'C2S_TRF_SERVICE_CNT', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004280', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SSRVTRFCNT', 'MAPPGW', '190', 'C2STRFCNT', 'C2S_TRF_SERVICE_CNT', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004270', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,FROMDATE,TODATE');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SSRVTRFCNT', 'REST', '190', 'C2STRFCNT', 'C2S_TRF_SERVICE_CNT', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004269', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SSRVTRFCNT', 'USSD', '190', 'C2STRFCNT', 'C2S_TRF_SERVICE_CNT', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004278', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2STOTTRNS', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2STotalNoOfTransactionController', 'C2S Total No. Of Transaction', 'C2S Total No. Of Transaction', 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-12 00:00:00.000000', 'ADMIN', 'C2S Total No. Of Transaction', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2STOTALTRANS', 'EXTGW', '190', 'C2STOTTRNS', 'C2S_TOTAL_TRNS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', 'SVK0004334', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2STOTALTRANS', 'MAPPGW', '190', 'C2STOTTRNS', 'C2S_TOTAL_TRNS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', 'SVK0004337', NULL, 'TYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2STOTALTRANS', 'REST', '190', 'C2STOTTRNS', 'C2S_TOTAL_TRNS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', 'SVK0004333', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2STOTALTRANS', 'SMSC', '190', 'C2STOTTRNS', 'C2S_TOTAL_TRNS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', 'SVK0004335', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2STOTALTRANS', 'USSD', '190', 'C2STOTTRNS', 'C2S_TOTAL_TRNS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-12 00:00:00.000000', 'SU0001', 'SVK0004336', NULL, 'GTYPE,MSISDN');



INSERT INTO SERVICE_TYPE (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD) VALUES ('PSBKRPT', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.UserHierarchy', 'Passbook Download', 'Passbook Download', 'Y', TO_DATE('2020-03-17 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'ADMIN', TO_DATE('2020-03-17 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'ADMIN', 'Passbook Download', 'N', 'N', 'Y', 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.UserHierarchylParser', 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, REQUEST_PARAM) VALUES ('PSBKRPT', 'EXTGW', '190', 'PSBKRPT', 'PSBKRPT', 'Y', 'Y', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', 'SVK9001090', 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, REQUEST_PARAM) VALUES ('PSBKRPT', 'REST', '190', 'PSBKRPT', 'PSBKRPT', 'Y', 'Y', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', 'SVK9001091', 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, REQUEST_PARAM) VALUES ('PSBKRPT', 'MAPPGW', '190', 'PSBKRPT', 'PSBKRPT', 'Y', 'Y', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', 'SVK9001092', 'TYPE,MSISDN,IMEI,PIN,LANGUAGE1,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, REQUEST_PARAM) VALUES ('PSBKRPT', 'USSD', '190', 'PSBKRPT', 'PSBKRPT', 'Y', 'Y', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', TO_DATE('2019-12-04 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'SU0001', 'SVK9001095', 'GTYPE,MSISDN');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2SENQCOM', 'C2S', 'NA', 'TYPE MSISDN2 EXTTXNNUMBER PIN', 'com.btsl.pretups.channel.transfer.requesthandler.CommissionCalculatorController', 'C2S Enquiry Request', 'Enquiry request', 'N', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', 'Enquiry request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'EXTGW', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001021', NULL, 'GTYPE,MSISDN');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'MAPPGW', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001022', NULL, 'MSISDN,IMEI,PIN,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'REST', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001020', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVINFO', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherInfo', 'Voucher Info', 'Voucher Info', 'Y', TIMESTAMP '2020-03-11 18:33:49.000000', 'ADMIN', TIMESTAMP '2020-03-11 18:33:49.000000', 'ADMIN', 'C2C Voucher Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVINFO', 'MAPPGW', '190', 'C2CVINFO', 'C2CVINFO', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-11 18:36:54.000000', 'SU0001', TIMESTAMP '2020-03-11 18:36:54.000000', 'SU0001', 'SVK9001019', NULL, 'MSISDN,IMEI,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('PASBDET', 'EXTGW', '190', 'PASBDET', 'Passbook Detail', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004789', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('PASBDET', 'REST', '190', 'PASBDET', 'Passbook Detail', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004279', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('PASBDET', 'MAPPGW', '190', 'PASBDET', 'Passbook Detail', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004498', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('PASBDET', 'USSD', '190', 'PASBDET', 'Passbook Detail', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004498', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('PASBDET', 'C2S', 'ALL', 'TYPE MSISDN PIN', 'com.btsl.pretups.channel.transfer.requesthandler.PassbookDetailsController', 'Passbook Detail', 'User Passbook Detail', 'Y', TIMESTAMP '2005-07-15 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-15 00:00:00.000000', 'ADMIN', 'Passbook Detail', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD	 BUNDLE_ID numeric(20);
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD	 MASTER_SERIAL_NO numeric(20) ;
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD C2C_TRANSFER_DATE TIMESTAMP;
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD C2C_TRANSFER_ID VARCHAR(20);

UPDATE service_type SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN' , REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,PIN,LANGUAGE1'
WHERE SERVICE_TYPE = 'TRFAPPR';
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFAPPR', 'MAPPGW', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004299', NULL, 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');

ALTER TABLE user_otp ADD OTP_COUNT NUMERIC(3);
ALTER TABLE user_otp ADD BARRED_DATE TIMESTAMP;

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('OTPVPINRST', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.user.requesthandler.OtpValidationandPinUpdation',
'User OTP Validate & PIN update', 'User OTP Validate & PIN update', 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', 'User OTP Validate & PIN update', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('OTPVDPINRST', 'MAPPGW', '190', 'OTPVPINRST', 'OTP_VALIDATE_PIN_RST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004243', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('OTPVDPINRST', 'REST', '190', 'OTPVPINRST', 'OTP_VALIDATE_PIN_RST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004244', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');




INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_INVALID_OTP', 'Maximum number of incorrect otp attempts allowed', 'SYSTEMPRF', 'INT', '3', 1, 60, 50, 'Maximum number of incorrect otp attempts allowed', 'N', 'Y', 'C2S', 'Maximum number of incorrect otp attempts allowed', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('BLOCK_TIME_INVALID_OTP', 'Block time duration of a user', 'SYSTEMPRF', 'INT', '60', 1, 600, 600, 'Blocking time of a user if incorrect otp limit exceeds', 'N', 'Y', 'C2S', 'Blocking time of a user if incorrect otp limit exceeds', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');





INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'EXTGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000338', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'MAPPGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000339', NULL, 'TYPE,MSISDN,IMEI,MHASH,TOKEN,FROMDATE,TODATE,SERVICETYPE,TOPPRODUCTS,NUMBEROFPRODORDENO');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'USSD', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000340', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'WEB', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000341', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'REST', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', NOW(), 'SU0001', NOW(), 'SU0001', 'SVK0000342', NULL, 'GTYPE,MSISDN');


INSERT INTO pretupsdatabase.service_type
(service_type, module, "type", message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, "name", external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('LASTXTRF', 'C2S', 'ALL', 'TYPE PIN', 'com.btsl.pretups.user.requesthandler.LastXTransferRequestHandler', 'c2s.lasttransferstatus', 'Last X transfer report', 'Y', '2005-07-14 00:00:00.000', 'ADMIN', '2005-07-14 00:00:00.000', 'ADMIN', 'C2S Last X Transfer Report', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,IMEI,PIN,LANGUAGE1', 'Y');


INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('USRINCVIEW', 'REST', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009164', NULL, 'GTYPE,MSISDN');


INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('USRINCVIEW', 'MAPPGW', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009169', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,PIN,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('USRINCVIEW', 'EXTGW', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009969', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,PIN,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('USRINCVIEW', 'USSD', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009269', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,PIN,EXTNWCODE');

INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('OTPCHNGPIN', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.SendOtpForForgotPinController', 'Send OTP to change pin', 'Send OTP to change pin', 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-23 00:00:00.000000', 'ADMIN', 'Send OTP to change pin', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('OTPFORFORGOTPIN', 'REST', '190', 'OTPCHNGPIN', 'OTP_TO_CHANGE_PIN', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004343', NULL, 'GTYPE,MSISDN');

INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('OTPFORFORGOTPIN', 'MAPPGW', '190', 'OTPCHNGPIN', 'OTP_FOR_FORGOT_PIN', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004344', NULL, 'TYPE,MSISDN');




INSERT INTO  SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('OTP_VALIDITY_PERIOD', 'OTP Valid Duration', 'SYSTEMPRF', 'INT', '5', 0, 100000, 50, 'Max Duration for which OTP will be valid', 'Y', 'Y', 'C2S', 'Max Duration for which OTP will be valid ', TIMESTAMP '2020-03-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-25 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO  SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('OTP_RESEND_TIMES', 'No. of Times Otp Sent in a duration', 'SYSTEMPRF', 'INT', '3', 0, 100000, 50, 'No. of Times Otp Sent in a duration(OTP_RESEND_DURATION)', 'Y', 'Y', 'C2S', 'No. of Times Otp Sent in a duration(OTP_RESEND_DURATION)', TIMESTAMP '2020-03-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-25 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO  SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('OTP_RESEND_DURATION', 'OTP Resend Duration', 'SYSTEMPRF', 'INT', '30', 0, 100000, 50, 'Max No. of Times Otp Sent in this duration', 'Y', 'Y', 'C2S', 'Max No. of Times Otp Sent in this duration', TIMESTAMP '2020-03-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-25 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO  SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TWO_FA_REQ_FOR_PIN', 'TWO Factor Authentication Req For Pin', 'CATPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'two factor Authentication Required For Pin', 'Y', 'Y', 'C2S', '2 factor Authentication Required For Pin', TIMESTAMP '2020-03-23 12:46:53.000000', 'ADMIN', TIMESTAMP '2020-03-23 12:46:53.000000', 'SU0001', 'true,false', 'Y');


INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TOTTRANS', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.TotalTransactionsDetailedView', 'Total Transcation Detailed View', 'Total Transcation Detailed View', 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-23 00:00:00.000000', 'ADMIN', 'Total Transcation Detailed View', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TOTTRANSDETAIL', 'EXTGW', '190', 'TOTTRANS', 'TOT_TRANS_DETAILED_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004340', NULL, 'GTYPE,MSISDN,PIN');
INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TOTTRANSDETAIL', 'MAPPGW', '190', 'TOTTRANS', 'TOT_TRANS_DETAILED_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004338', NULL, 'TYPE,MSISDN');
INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TOTTRANSDETAIL', 'REST', '190', 'TOTTRANS', 'TOT_TRANS_DETAILED_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004331', NULL, 'GTYPE,MSISDN');
INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TOTTRANSDETAIL', 'USSD', '190', 'TOTTRANS', 'TOT_TRANS_DETAILED_VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', TIMESTAMP '2020-03-23 00:00:00.000000', 'SU0001', 'SVK0004339', NULL, 'GTYPE,MSISDN');


--##########################################################################################################
--##
--##      PreTUPS_v7.22.0 DB Script
--##
--##########################################################################################################

INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('USRPMTYP', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.UserPaymentTypesController', 'User Payment Types', 'User Payment Types', 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', 'User Information of Buy & Transfer', 'S', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USRPMTYPE', 'REST', '190', 'USRPMTYP', 'USR_PMT_TYP', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004289', NULL, 'GTYPE,MSISDN');


INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('USRINFO', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.SenderReceiverDetailsController', 'User Information of Buy & Transfer', 'User Information of Buy & Transfer', 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-12-04 00:00:00.000000', 'ADMIN', 'User Information of Buy & Transfer', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO  SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USERINFO', 'REST', '190', 'USRINFO', 'USR_INFO_BUY_TRF', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004288', NULL, 'GTYPE,MSISDN');

INSERT INTO service_type
(service_type, "module", "type", message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, name, external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('DOMAINCAT', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.GetDomainCategoryController', 'Get Domain And Category', 'Get Domain And Category', 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-04-03 00:00:00.000000', 'ADMIN', 'Get Domain And Category', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('GETDOMAINCATEGORY', 'REST', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004341', NULL, 'GTYPE,MSISDN');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('GETDOMAINCATEGORY', 'MAPPGW', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004342', NULL, 'TYPE,MSISDN');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('GETDOMAINCATEGORY', 'USSD', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004345', NULL, 'GTYPE,MSISDN');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('GETDOMAINCATEGORY', 'EXTGW', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004346', NULL, 'GTYPE,MSISDN');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('AUTOCOMPLETE_USER_DETAILS_COUNT', 'No. of User Details fetched at a time', 'SYSTEMPRF', 'INT', '50', 1, 9999, 50, 'No. of User Details fetched at a time', 'Y', 'Y', 'C2S', 'No. of User Details fetched at a time', TIMESTAMP '2020-04-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-04-06 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MIN_LENGTH_TO_AUTOCOMPLETE', 'Minimum character provided by user to get User Details', 'SYSTEMPRF', 'INT', '3', 1, 100, 50, 'Minimum character provided by user to get User Details', 'Y', 'Y', 'C2S', 'Minimum character provided by user to get User Details', TIMESTAMP '2020-04-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-04-07 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('AUTOCOMPLT', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.AutoCompleteUserDetailsController', 'Auto Complete User Details', 'Auto Complete User Details', 'Y', TIMESTAMP '2020-04-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-04-06 00:00:00.000000', 'ADMIN', 'Auto Complete User Details', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('AUTOCOMPLETE', 'REST', '190', 'AUTOCOMPLT', 'AutoComplete msisdn/LId/UName', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-06 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-06 00:00:00.000000', 'SU0001', 'SVK0004353', NULL, 'TYPE,MSISDN');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TXNCALVIEW', 'C2S', 'ALL', 'TYPE MSISDN PRODUCTS IMEI PIN LANGUAGE1', 'com.btsl.pretups.channel.transfer.requesthandler.TransactionAPICalculationController', 'Channel Voucher Enquiry', 'TRANSACTION API FOR CALCULATION', 'Y', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', 'TRANSACTION API FOR CALCULATION', 'N', 'Y', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,PRODUCTS,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TXNCALVIEW', 'MAPPGW', '190', 'TXNCALVIEW', 'TXN API FOR CALCULATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', 'SVK4109920', NULL, 'TYPE,MSISDN,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TXNCALVIEW', 'REST', '190', 'TXNCALVIEW', 'TXN API FOR CALCULATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', 'SVK4109020', NULL, 'TYPE,MSISDN,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');



INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('RCACHEDMM', 'MASTER', '/updateRedisCache.do?method=updateRedisCache', 'Update Redis Cache', 'Y', 101, '1', '1', '/updateRedisCache.do?method=updateCache', NULL);
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('RCACHE001', 'MASTER', '/updateRedisCache.do?method=updateRedisCache', 'Update Redis Cache', 'Y', 101, '2', '1', '/updateRedisCache.do?method=updateCache', NULL);
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('RCACHE002', 'MASTER', '/jsp/master/updateRedisCacheSuccess.jsp', 'Update Redis Cache', 'N', 101, '2', '1', '/jsp/master/updateCacheRedisSuccess.jsp', NULL);

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('RADISCACHEUPDATE', 'RCACHE001', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('RADISCACHEUPDATE', 'RCACHE002', '1');
INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('RADISCACHEUPDATE', 'RCACHEDMM', '1');


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'RADISCACHEUPDATE', 'Update Radis Cache', 'Masters', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'RADISCACHEUPDATE', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'RADISCACHEUPDATE', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('CCC', 'RADISCACHEUPDATE', '1 ');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SSADM', 'RADISCACHEUPDATE', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUADM', 'RADISCACHEUPDATE', '1');


INSERT INTO GROUP_ROLES
(GROUP_ROLE_CODE, ROLE_CODE)
VALUES('SSADM', 'RADISCACHEUPDATE');
INSERT INTO GROUP_ROLES
(GROUP_ROLE_CODE, ROLE_CODE)
VALUES('SUNADM', 'RADISCACHEUPDATE');
INSERT INTO GROUP_ROLES
(GROUP_ROLE_CODE, ROLE_CODE)
VALUES('OPTRB2', 'RADISCACHEUPDATE');
INSERT INTO GROUP_ROLES
(GROUP_ROLE_CODE, ROLE_CODE)
VALUES('NWADM', 'RADISCACHEUPDATE');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'SCHEDULETOPUP', '1');

INSERT INTO network_services
(module_code, service_type, sender_network, receiver_network, status, language1_message, language2_message, created_by, created_on, modified_by, modified_on)
VALUES('P2P', 'VCN', 'PB', 'PB', 'Y', 'l', 'l', 'NGLA0000011551', '2017-04-04 11:31:46.518', 'NGLA0000011551', '2017-04-04 11:31:46.518');

INSERT INTO network_services
(module_code, service_type, sender_network, receiver_network, status, language1_message, language2_message, created_by, created_on, modified_by, modified_on)
VALUES('P2P', 'VCN', 'NG', 'PB', 'Y', 'l', 'l', 'NGLA0000011551', '2017-04-04 11:31:46.518', 'NGLA0000011551', '2017-04-04 11:31:46.518');


ALTER TABLE voms_batches ALTER COLUMN first_approved_on TYPE timestamp;
ALTER TABLE voms_batches ALTER COLUMN second_approved_on TYPE timestamp;
ALTER TABLE voms_batches ALTER COLUMN third_approved_on TYPE timestamp;

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'APP1VOMS', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SSADM', 'APP1VOMS', '1'); 
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('NWADM', 'APP1VOMS', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'APP2VOMS', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SSADM', 'APP2VOMS', '1'); 
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('NWADM', 'APP2VOMS', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'APP3VOMS', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SSADM', 'APP3VOMS', '1'); 
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('NWADM', 'APP3VOMS', '1');

INSERT INTO service_type
(service_type, "module", type, message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, name, external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('O2CTRNSFR', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.O2CTransferInitiateMappController', 'O2C Transfer Initiate', 'O2C Transfer Initiate', 'Y', TIMESTAMP '2020-05-15 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-05-15 00:00:00.000000', 'ADMIN', 'O2C Transfer Initiate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('O2CINICU', 'MAPPGW', '190', 'O2CTRNSFR', 'O2C Transfer Initiate', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-05-15 00:00:00.000000', 'SU0001', TIMESTAMP '2020-05-15 00:00:00.000000', 'SU0001', 'SVK0004389', NULL, 'TYPE,MSISDN');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('O2CAGAPRL', 'C2S', 'ALL', 'TYPE TXNID STATUS REFNO PIN', 'com.btsl.pretups.channel.transfer.requesthandler.O2CTransferApprovalController', 'O2C Transfer Approval', 'O2C Transfer Approval', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'O2C Transfer Approval', 'N', 'N', 'Y', 2, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNID,TXNSTATUS,REFNO,MESSAGE', 'TYPE,TXNID,STATUS,REFNO,EXTNWCODE', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('O2CAGAPRL', 'MAPPGW', '190', 'O2CAGAPRL', 'O2C Approval', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2013-11-30 10:19:35.000000', 'SU0001', TIMESTAMP '2013-11-30 10:19:35.000000', 'SU0001', 'SVK3000183', NULL, 'TYPE,TXNID,STATUS,REFNO,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFREG', 'MAPPGW', '190', 'SELFREG', 'SELF REGISTRATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2013-01-16 08:21:47.000000', 'SU0001', TIMESTAMP '2013-01-16 08:21:47.000000', 'SU0001', 'SVK000097', NULL, 'TYPE,MSISDN');



INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('SELFREG', 'OPT', 'ALL', 'TYPE MSISDN', 'com.btsl.pretups.channel.transfer.requesthandler.SelfRegistrationController', 'Self Registration', 'Self Registration', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'Self Registration', 'N', 'N', 'Y', NULL, 'Y', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');




ALTER TABLE voms_batches RENAME COLUMN "SIGNED_DOC" TO SIGNED_DOC;
ALTER TABLE voms_batches RENAME COLUMN "SIGNED_DOC_TYPE" TO SIGNED_DOC_TYPE;
ALTER TABLE voms_batches RENAME COLUMN "SIGNED_DOC_FILE_PATH" TO SIGNED_DOC_FILE_PATH;


Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('IS_EMAIL_ALLOWED_AUTO_NTWKSTK', 'auto Network Stock  email allowed', 'SYSTEMPRF', 'BOOLEAN', 'false',NULL, NULL, 50, 'auto Network Stock  email allowed', 'N','Y', 'C2S', 'auto Network Stock  email allowed', now(), 'ADMIN',now(), 'SU0001', NULL, 'Y');

Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('ALERT_ALLOWED_USER', 'GeoFencing alert allowed to user', 'SYSTEMPRF', 'BOOLEAN', 'false',NULL, NULL, 50, 'GeoFencing alert allowed to user', 'N','Y', 'C2S', 'GeoFencing alert allowed to user', now(), 'ADMIN',now(), 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TOKEN_EXPIRE_TIME', 'Token Expire Time in sec', 'SYSTEMPRF', 'INT', '1200', 1, 9999, 50, 'Token Expire Time in sec', 'Y', 'Y', 'C2S', 'Expiry time of OAuth Token', TIMESTAMP '2020-06-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-06-25 00:00:00.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REFRESH_TOKEN_EXPIRE_TIME', 'Refresh Token Expire Time in sec', 'SYSTEMPRF', 'INT', '1200', 1, 9999, 50, 'Refresh Token Expire Time in sec', 'Y', 'Y', 'C2S', 'Expiry time of OAuth Token', TIMESTAMP '2020-06-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-06-25 00:00:00.000000', 'SU0001', NULL, 'Y');


--##########################################################################################################
--##
--##      PreTUPS_v7.23.0 DB Script
--##
--##########################################################################################################
ALTER TABLE SYSTEM_PREFERENCES alter column REMARKS type VARCHAR(500);
ALTER TABLE SYSTEM_PRF_HISTORY alter column REMARKS type VARCHAR(500);
ALTER TABLE PRODUCT_SERVICE_TYPE_MAPPING
ADD COLUMN PRODUCT_CODE varchar(10) null;

ALTER TABLE PRODUCT_SERVICE_TYPE_MAPPING
ADD COLUMN PRODUCT_CODE varchar(10) null;
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('DW_COMMISSION_CAL', 'DW Commission Calulation', 'SYSTEMPRF', 'STRING', 'OTH',  NULL, NULL, 20, 'DW Commission to be given either based Other profile or Both', 'Y',  'Y', 'C2S', 'This is applied only for Dual Wallet and Commission to be given either based Other profile=OTH or Both=BASE_OTH', now(), 'ADMIN',  now(), 'SU0001', 'OTH,BASE_OTH', 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('DW_ALLOWED_GATEWAYS', 'DW Allowed Gateways', 'SYSTEMPRF', 'STRING', 'EXTGW,DWEXTGW',  NULL, NULL, 20, 'DW Commission to be given based on defined gateway code', 'Y',  'Y', 'C2S', 'This is applied only for Dual Wallet and DW Commission to be given based on defined gateway code like EXTGW', now(), 'ADMIN',  now(), 'SU0001', 'OTH,BASE_OTH', 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('USSD_RESP_SEP', 'USSD_RESP_SEP', 'SYSTEMPRF', 'STRING', 'EXTGW',  NULL, NULL, 6, 'SMS_PIN_BYPASS_GATEWAY TYPES', 'N',  'Y', 'C2S', 'USSD_RESP_SEP', now(), 'ADMIN',  now(), 'ADMIN', NULL, 'Y');


ALTER TABLE users
ADD COLUMN to_moved_user_id varchar(15) DEFAULT null;
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_BATCH_FILEEXT', 'C2C Batch download file ext', 'SYSTEMPRF', 'STRING', 'xls', NULL, NULL, 50, 'the values for extension can be csv or xls or xlsx', 'N', 'Y', 'C2S', 'Extension of file to be downloaded or uploaded for C2C batch', '2005-06-16 00:00:00.000000', 'ADMIN', '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('BYPASS_EVD_KANEL_MES_STAT', 'Bypass Kannel Response for EVD', 'SYSTEMPRF', 'BOOLEAN', 'FALSE', 
    NULL, NULL, 5, 'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response', 'Y', 
    'Y', 'C2S', 'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response', now(), 'ADMIN', 
    now(), 'ADMIN', NULL, 'Y');
	
	
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('FILE_UPLOAD_MAX_SIZE', 'C2C file upload max size', 'SYSTEMPRF', 'STRING', '2097152', 1000, 9999999, 50, 'C2C file upload max size', 'Y', 'Y', 'C2C', 'C2C file upload max size', TIMESTAMP '2020-07-20 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-07-20 00:00:00.000000', 'SU0001', NULL, 'Y');



--##########################################################################################################
--##
--##      PreTUPS_v7.24.0 DB Script
--##
--##########################################################################################################

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_ORDER_SLAB_LENGTH', 'Voucher Slab length ', 'SYSTEMPRF', 'INT', '4', 4, 4, 50, 'voucher slab length ', 'N', 'Y', 'VOMS', 'voucher slab length', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_MIN_ALT_VALUE', 'Voucher MIN alerting value ', 'SYSTEMPRF', 'INT', '10', 10, 10, 50, 'voucher slab length ', 'N', 'Y', 'VOMS', 'voucher min alerting value', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('RCREV', 'REST', '190', 'RCREV', 'Recharge Reversal', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2012-07-26 19:30:07.000000', 'SU0001', TIMESTAMP '2015-04-05 15:58:38.000000', 'SU0001', 'SVK0001998', NULL, 'GTYPE,MSISDN');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('DIGITAL_RECHARGE_VOUCHER_TYPE', 'DIGITAL_RECHARGE_VOUCHER_TYPE', 'SYSTEMPRF', 'STRING', 'digital,digital1,test_digit', NULL, NULL, 50, 'Digital Recharge Voucher Type', 'N', 'N', 'C2S', 'Digital Recharge Voucher Type', TIMESTAMP '2020-10-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-10-07 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

--##########################################################################################################
--##
--##      PreTUPS_v7.25.0 DB Script
--##
--##########################################################################################################

ALTER TABLE oauth_access_token ADD PRIMARY KEY (token_id);
ALTER TABLE oauth_refresh_token ADD PRIMARY KEY (token_id);

ALTER TABLE CHANNEL_TRANSFERS_ITEMS
ADD FIRST_LEVEL_APPROVED_QTY varchar(15);

ALTER TABLE CHANNEL_TRANSFERS_ITEMS
ADD SECOND_LEVEL_APPROVED_QTY varchar(15);


--##########################################################################################################
--##
--##      PreTUPS_v7.26.0 DB Script
--##
--##########################################################################################################

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'OB', 'PB', 1, '2021-01-10 12:17:08.201', 'DAY', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'OB', 'NG', 1, '2021-01-10 08:10:25.988', 'DAY', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'SBM', 'NG', 1092, '2021-01-10 23:41:20.219', 'NA', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'SB', 'NG', 30, '2021-01-10 00:16:44.700', 'DAY', 'Scheduled Batch ID for corporate');

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'CB', 'NG', 11, '2021-01-10 23:13:43.438', 'DAY', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'SEQNUM', 'ALL', 1, '2021-01-10 00:00:00.000', 'NA', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'VMBTCHUD', 'ALL', 1, '2021-01-10 00:00:00.000', 'NA', NULL);

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'OX', 'NG', 4, '2021-01-10 06:11:08.527', 'MINUTES', NULL);
INSERT INTO pretupsdatabase.ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2021', 'CX', 'NG', 1, '2021-01-10 06:14:19.599', 'MINUTES', NULL);

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'R_O2CCOMMISSION', 'O2C Commission', 'O2C Commission', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('O2CCOMMISS', 'O2C', '/jsp/user/dummy.jsp', 'Operator to channel', 'Y', 7, '1', '2', '/jsp/user/dummy.jsp', 'R_O2CCOMMISSION');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'R_O2CCOMMISSION', '2');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('SUBCU', 'R_O2CCOMMISSION', 'E_O2CCOMMISSION', 'Commission', 'role.label.o2c.commission', 'event.label.o2c.commission', 'Y');

INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGBC0000001564', 'R_O2CCOMMISSION', 'WEB');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGBC0000001564', 'R_O2CCOMMISSION', 'E_O2CCOMMISSION', 'WEB', 'Y');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'R_O2CBULKCOMMISSION', 'O2C Bulk Commission', 'O2C Bulk Commission', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('O2CBULKCOMM', 'O2C', '/jsp/user/dummy.jsp', 'Operator to channel', 'Y', 7, '1', '2', '/jsp/user/dummy.jsp', 'R_O2CBULKCOMMISSION');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'R_O2CBULKCOMMISSION', '2');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('BCU', 'R_O2CBULKCOMMISSION', 'E_O2CBULKCOMMISSION', 'Bulk Commission', 'role.label.o2c.bulkcommission', 'event.label.o2c.bulkcommission', 'Y');

INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGBC0000001564', 'R_O2CBULKCOMMISSION', 'WEB');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGBC0000001564', 'R_O2CBULKCOMMISSION', 'E_O2CBULKCOMMISSION', 'WEB', 'Y');

CREATE TABLE USER_WIDGETS(
   USER_ID VARCHAR(15),
   USER_WIDGET_LIST VARCHAR(50),
   PRIMARY KEY( USER_ID )
);


INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('O2CWIDGET', 'O2C Widgets', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', 'N');

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('C2SWIDGET', 'C2S Widgets', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', 'N');

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('C2CWIDGET', 'C2C Widgets', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', 'N');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('DVD', 'Digital Voucher Distribution', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('EVD', 'Electronic Voucher Distribution', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('GRC', 'C2S Gift Recharge', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PPB', 'Postpaid Bill Payment', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('RC', 'Customer Recharge', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('MVD', 'Multiple Voucher Distribution', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('INTRRC', 'Internet', 'C2SWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');



INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CWID', 'C2C WITHDRAW', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CRETURN', 'C2C RETURN', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CBULK', 'C2C BULK', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CTRFBUYVCHR', 'C2C Transfer BUY VOUCHER', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CBUY', 'C2C BUY', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CTRFVOUCHER', 'C2C Transfer Voucher', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CTRANSFER', 'C2C Transfer', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('E_C2CBULKWID', 'C2C Bulk Withdraw', 'C2CWIDGET', 'Y', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-01-11 00:00:00.000000', 'ADMIN');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_BULK_FILE_SIZE_BYTES', 'Maximum file size in bytes', 'SYSTEMPRF', 'INT', '10000000', 0, 10000000, 10000000, 'MAX File Size For Bulk', 'N', 'Y', 'C2S', 'MAX File Size For Bulk', TIMESTAMP '2021-02-03 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-03-03 03:39:55.000000', 'SU0001', NULL, 'Y');



--##########################################################################################################
--##
--##      PreTUPS_v7.27.0 DB Script
--##
--##########################################################################################################

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_O2CBULKAPPRV1', 'Operator to Channel Bulk Approval', 'Operator to Channel Bulk Approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BAPPRO2C1', 'O2C', '/jsp/user/dummy.jsp', 'Operator to Channel Bulk Approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_O2CBULKAPPRV1');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_O2CBULKAPPRV1', '2');
INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV1', 'E_BULKPURCHASE1', 'Purchase', 'role.label.o2c.bulkappr1', 'event.label.o2c.bulkappr.purchase1', 'Y');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV1', 'E_BULKWITHDRAW1', 'Withdraw', 'role.label.o2c.bulkappr1', 'event.label.o2c.bulkappr.withdraw1', 'Y');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV1', 'WEB');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV1', 'E_BULKPURCHASE1', 'WEB', 'Y');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV1', 'E_BULKWITHDRAW1', 'WEB', 'Y');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_O2CBULKAPPRV2', 'Operator to Channel Bulk Approval', 'Operator to Channel Bulk Approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BAPPRO2C2', 'O2C', '/jsp/user/dummy.jsp', 'Operator to Channel Bulk Approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_O2CBULKAPPRV2');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_O2CBULKAPPRV2', '2');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV2', 'E_BULKPURCHASE2', 'Purchase', 'role.label.o2c.bulkappr2', 'event.label.o2c.bulkappr.purchase2', 'Y');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV2', 'E_BULKWITHDRAW2', 'Withdraw', 'role.label.o2c.bulkappr2', 'event.label.o2c.bulkappr.withdraw2', 'Y');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV2', 'WEB');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV2', 'E_BULKPURCHASE2', 'WEB', 'Y');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV2', 'E_BULKWITHDRAW2', 'WEB', 'Y');


INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_O2CBULKAPPRV3', 'Operator to Channel Bulk Approval', 'Operator to Channel Bulk Approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BAPPRO2C3', 'O2C', '/jsp/user/dummy.jsp', 'Operator to Channel Bulk Approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_O2CBULKAPPRV3');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_O2CBULKAPPRV3', '2');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV3', 'E_BULKPURCHASE3', 'Purchase', 'role.label.o2c.bulkappr3', 'event.label.o2c.bulkappr.purchase3', 'Y');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_O2CBULKAPPRV3', 'E_BULKWITHDRAW3', 'Withdraw', 'role.label.o2c.bulkappr3', 'event.label.o2c.bulkappr.withdraw3', 'Y');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV3', 'WEB');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV3', 'E_BULKPURCHASE3', 'WEB', 'Y');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_O2CBULKAPPRV3', 'E_BULKWITHDRAW3', 'WEB', 'Y');


INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_BULKCOMMSNAPPRV1', 'Bulk commission payout transfer approval', 'Bulk commission payout transfer approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BCOMAPRO2C1', 'O2C', '/jsp/user/dummy.jsp', 'Bulk commission approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_BULKCOMMSNAPPRV1');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_BULKCOMMSNAPPRV1', '2');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_BULKCOMMSNAPPRV1', 'E_BULKCOMMSNAPPRV1', 'FOC', 'role.label.o2c.bulkcommappr1', 'event.label.o2c.bulkcommappr.foc1', 'Y');


INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV1', 'WEB');

INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV1', 'E_BULKCOMMSNAPPRV1', 'WEB', 'Y');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_BULKCOMMSNAPPRV2', 'Bulk commission payout transfer approval', 'Bulk commission payout transfer approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BCOMAPRO2C2', 'O2C', '/jsp/user/dummy.jsp', 'Bulk commission approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_BULKCOMMSNAPPRV2');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_BULKCOMMSNAPPRV2', '2');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_BULKCOMMSNAPPRV2', 'E_BULKCOMMSNAPPRV2', 'FOC', 'role.label.o2c.bulkcommappr2', 'event.label.o2c.bulkcommappr.foc2', 'Y');


INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV2', 'WEB');


INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV2', 'E_BULKCOMMSNAPPRV2', 'WEB', 'Y');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('OPERATOR', 'R_BULKCOMMSNAPPRV3', 'Bulk commission payout transfer approval', 'Bulk commission payout transfer approval', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO pages
(page_code, module_code, page_url, menu_name, menu_item, sequence_no, menu_level, application_id, spring_page_url,role_code)
VALUES('BCOMAPRO2C3', 'O2C', '/jsp/user/dummy.jsp', 'Bulk commission approval', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp','R_BULKCOMMSNAPPRV3');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('BCU', 'R_BULKCOMMSNAPPRV3', '2');

INSERT INTO role_events
(category_code, role_code, event_code, event_name, role_label_key, event_label_key, status)
VALUES('BCU', 'R_BULKCOMMSNAPPRV3', 'E_BULKCOMMSNAPPRV3', 'FOC', 'role.label.o2c.bulkcommappr3', 'event.label.o2c.bulkcommappr.foc3', 'Y');


INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV3', 'WEB');


INSERT INTO user_role_events
(user_id, role_code, event_code, gateway_type, status)
VALUES('NGBC0000001564', 'R_BULKCOMMSNAPPRV3', 'E_BULKCOMMSNAPPRV3', 'WEB', 'Y');

ALTER TABLE ADJUSTMENTS_MISTMP 
ADD COLUMN MARGIN_AMOUNT NUMERIC(20,0),
ADD COLUMN OTF_AMOUNT NUMERIC(20,0);

ALTER TABLE DAILY_C2S_TRANS_DETAILS 
ADD COLUMN TOTAL_MARGIN_AMOUNT NUMERIC(20,0),
ADD COLUMN TOTAL_OTF_AMOUNT NUMERIC(20,0);

ALTER TABLE DAILY_CHNL_TRANS_DETAILS
ADD COLUMN TOTAL_COMMISSION_VALUE NUMERIC(20,0),
ADD COLUMN TOTAL_OTF_AMOUNT NUMERIC(20,0);

INSERT INTO web_services_types
(web_service_type, description, resource_name, validator_name, formbean_name, config_path, web_service_url, is_rba_require, is_data_validation_require, role_code)
VALUES('DOWNLOADFILEUSER', 'Download File User', 'DownloadFileUser', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rstapi/v1/channelUsers/channelUsersList', 'N', 'Y', 'DOWNLOADFILEUSER');

INSERT INTO PRETUPS_TRUNK_DEV.SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_ALLOW_CONTENT_TYPE', 'USER Allowed Content Type', 'SYSTEMPRF', 'STRING', 'CSV', NULL, NULL, 50, 'USER Allowed Content Type', 'N', 'N', 'P2P', 'VMS Allowed Content Type', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', NULL, 'Y');



--##########################################################################################################
--##
--##      PreTUPS_v7.28.0 DB Script
--##
--##########################################################################################################

--##########################################################################################################
--##
--##      PreTUPS_v7.29.0 DB Script
--##
--##########################################################################################################

--##########################################################################################################
--##
--##      PreTUPS_v7.30.0 DB Script
--##
--##########################################################################################################

--##########################################################################################################
--##
--##      PreTUPS_v7.31.0 DB Script
--##
--##########################################################################################################


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'R_CHANNELUSERDET', 'Channel User Details', 'Channel User', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'R_CHANNELUSERDET', '2');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('CHNLUSRDET', 'CHNLUSER', '/jsp/user/dummy.jsp', 'Channel User Details', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp', 'R_CHANNELUSERDET');

INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'WEB');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_CHANNELUSER', 'Channel User', 'role.label.channeluser', 'event.label.channeluser', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_CHANNELUSER', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_PINMGMT', 'Pin Management', 'role.label.channeluser.pinmgmt', 'event.label.channeluser.pinmgmt', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_PINMGMT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_PASSWORDMGMT', 'Password Management', 'role.label.channeluser.passwordmgmt', 'event.label.channeluser.passwordmgmt', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_PASSWORDMGMT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_VIEWDETAILS', 'View Details', 'role.label.channeluser.viewdetails', 'event.label.channeluser.viewdetails', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_VIEWDETAILS', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_EDIT', 'Edit User', 'role.label.channeluser.edit', 'event.label.channeluser.edit', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_EDIT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_DELETE', 'Delete User', 'role.label.channeluser.delete', 'event.label.channeluser.delete', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_DELETE', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_ADD', 'Add User', 'role.label.channeluser.add', 'event.label.channeluser.add', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_ADD', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_BULKADD', 'Bulk Add Users', 'role.label.channeluser.bulkadd', 'event.label.channeluser.bulkadd', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_BULKADD', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_SUSPEND', 'Suspend User', 'role.label.channeluser.suspend', 'event.label.channeluser.suspend', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_SUSPEND', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_RESUME', 'Resume User', 'role.label.channeluser.resume', 'event.label.channeluser.resume', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_RESUME', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_BAR', 'Bar User', 'role.label.channeluser.bar', 'event.label.channeluser.bar', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_BAR', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_CHANNELUSERDET', 'E_UNBAR', 'Unbar User', 'role.label.channeluser.unbar', 'event.label.channeluser.unbar', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_CHANNELUSERDET', 'E_UNBAR', 'WEB', 'Y');

INSERT INTO MODULES
(MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
VALUES('STAFUSER', 'Staff User', 7, '2');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'R_STAFFUSERDET', 'Staff User Details', 'Staff User', 'Y', 'A', NULL, NULL, 'N', '2', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'R_STAFFUSERDET', '2');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL, ROLE_CODE)
VALUES('STAFUSRDET', 'STAFUSER', '/jsp/user/dummy.jsp', 'Staff User Details', 'Y', 1, '1', '2', '/jsp/user/dummy.jsp', 'R_STAFFUSERDET');

INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'WEB');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_STAFFUSER', 'Staff User', 'role.label.staffuser', 'event.label.staffuser', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_STAFFUSER', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_PINMGMT', 'Pin Management', 'role.label.staffuser.pinmgmt', 'event.label.staffuser.pinmgmt', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_PINMGMT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_PASSWORDMGMT', 'Password Management', 'role.label.staffuser.passwordmgmt', 'event.label.staffuser.passwordmgmt', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_PASSWORDMGMT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_VIEWDETAILS', 'View Details', 'role.label.staffuser.viewdetails', 'event.label.staffuser.viewdetails', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_VIEWDETAILS', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_EDIT', 'Edit User', 'role.label.staffuser.edit', 'event.label.staffuser.edit', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_EDIT', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_DELETE', 'Delete User', 'role.label.staffuser.delete', 'event.label.staffuser.delete', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_DELETE', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_ADD', 'Add User', 'role.label.staffuser.add', 'event.label.staffuser.add', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_ADD', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_SUSPEND', 'Suspend User', 'role.label.staffuser.suspend', 'event.label.staffuser.suspend', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_SUSPEND', 'WEB', 'Y');

INSERT INTO ROLE_EVENTS
(CATEGORY_CODE, ROLE_CODE, EVENT_CODE, EVENT_NAME, ROLE_LABEL_KEY, EVENT_LABEL_KEY, STATUS)
VALUES('DIST', 'R_STAFFUSERDET', 'E_RESUME', 'Resume User', 'role.label.staffuser.resume', 'event.label.staffuser.resume', 'Y');

INSERT INTO USER_ROLE_EVENTS
(USER_ID, ROLE_CODE, EVENT_CODE, GATEWAY_TYPE, STATUS)
VALUES('NGD0000002741', 'R_STAFFUSERDET', 'E_RESUME', 'WEB', 'Y');

--##########################################################################################################
--##
--##      PreTUPS_v7.32.0 DB Script
--##
--##########################################################################################################

ALTER TABLE ROLES ADD SUB_GROUP_NAME varchar(255) DEFAULT 'Sub_Group';
ALTER TABLE ROLES add SUB_GROUP_ROLE varchar(2) DEFAULT 'N';

UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Recharge Management', ROLE_NAME = 'Reversal', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2SREV' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Recharge Management', ROLE_NAME = 'Recharge', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2SRECHARGE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Buy Stock', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CTRFINI' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Return Stock', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CRETURN' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Transfer Stock', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CTRF' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Transfer Approval 1', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CTRFAPR1' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Transfer Approval 2', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CTRFAPR2' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Transfer Approval 3', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CTRFAPR3' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Withdraw Stock', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CWDL' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Voucher Management', ROLE_NAME = 'Buy Vouchers', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CBUYVINI' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Voucher Management', ROLE_NAME = 'Transfer Vouchers', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CVINI' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Voucher Management', ROLE_NAME = 'Transfer Vouchers Approval 1', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CVCTRFAPR1' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Voucher Management', ROLE_NAME = 'Transfer Vouchers Approval 2', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CVCTRFAPR2' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Voucher Management', ROLE_NAME = 'Transfer Vouchers Approval 3', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2CVCTRFAPR3' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Bulk Stock Management', ROLE_NAME = 'Approve Batch Transfer', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BC2CAPPROVE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Bulk Stock Management', ROLE_NAME = 'Approve Batch Withdraw', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BC2CWDRAPP' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Bulk Stock Management', ROLE_NAME = 'Initiate Batch Transfer', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BC2CINITIATE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Channel', SUB_GROUP_NAME = 'Bulk Stock Management', ROLE_NAME = 'Initiate Batch Withdraw', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BC2CWDRW' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Operator to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Return Stock', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'O2CRET' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Operator to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Initiate Transfer', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'O2CINIT' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Operator to Channel', SUB_GROUP_NAME = 'Stock Management', ROLE_NAME = 'Initiate Voucher order request', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'INITVOMSOREQ' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Add Channel User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'ADDCUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Initiate user addition in batch', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BATCHUSRINITIATE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Delete Channel User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'DELETECUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Modify Channel User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'EDITCUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'View Channel User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'VIEWCUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'Access Management', ROLE_NAME = 'Change PIN for others', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'CHANGEPIN' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen (top right)', SUB_GROUP_NAME = 'Security Settings', ROLE_NAME = 'Change PIN for Self', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'CHANGESELFPIN' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'Access Management', ROLE_NAME = 'Channel User PIN Management', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2SUNBLOCKPIN' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'Access Management', ROLE_NAME = 'Suspend Channel User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'SUSPENDCUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel User', SUB_GROUP_NAME = 'Access Management', ROLE_NAME = 'Change Password for others', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'C2SUNBLOCKPAS' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Staff User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Modify Staff user', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'MODSTAFFUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Staff User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Add Staff user', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'STFUSRAD' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Staff User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'View Staff user', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'VIEWSTAFFUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Staff User', SUB_GROUP_NAME = 'User Management', ROLE_NAME = 'Resume Staff user', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'RESSTAFFUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Staff User', SUB_GROUP_NAME = 'Access Management', ROLE_NAME = 'Suspend Staff user', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'SUSSTAFFUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen', SUB_GROUP_NAME = 'User Barring Management', ROLE_NAME = 'Unbar User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'UNBARUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Batch Recharge Management', ROLE_NAME = 'Batch recharge', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'SCHEDULETOPUP' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Schedule Management', ROLE_NAME = 'Cancel Subscriber from schedule', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'CANCELSCHEDULE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Schedule Management', ROLE_NAME = 'Cancel scheduled batch', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'CNCLSCHEDULED' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Schedule Management', ROLE_NAME = 'Re-schedule batch', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'RESCHEDULETOPUP' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Schedule Management', ROLE_NAME = 'View status of scheduled subscriber', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'VIEWSUBSSCHEDULE' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Channel to Subscriber', SUB_GROUP_NAME = 'Schedule Management', ROLE_NAME = 'View status of scheduled batch', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'VIEWSCHEDULED' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen', SUB_GROUP_NAME = 'User Barring Management', ROLE_NAME = 'Bar User', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'BARUSER' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen (top right)', SUB_GROUP_NAME = 'Change Language', ROLE_NAME = 'Change Language', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'CHANGELANG' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen', SUB_GROUP_NAME = 'User Barring Management', ROLE_NAME = 'View Barred List', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'VIEWBARREDLIST' AND DOMAIN_TYPE ='DISTB_CHAN';
UPDATE ROLES SET GROUP_NAME = 'Home Screen (top right)', SUB_GROUP_NAME = 'Profile', ROLE_NAME = 'Self details modify', SUB_GROUP_ROLE = 'Y' WHERE ROLE_CODE = 'SELFCHNLUSRMOD' AND DOMAIN_TYPE ='DISTB_CHAN';


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE)
VALUES('DISTB_CHAN', 'DELSTAFFUSER', 'Delete Staff user', 'Staff Users', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'User Management', 'Y');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE)
VALUES('DISTB_CHAN', 'CHANGESELFPASSCU', 'Change Password for Self', 'Home Screen (top right)', 'N', 'A', NULL, NULL, 'Y', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'Y');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE)
VALUES('DISTB_CHAN', 'UNBLOCKPINSTAFF', 'Staff User PIN Management', 'Staff Users', 'N', 'A', NULL, NULL, 'Y', '1', 'WEB', 'B', 'N', 'N', 'B', 'Access Management', 'Y');

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE)
VALUES('DISTB_CHAN', 'UNBLOCKPASSSTAFF', 'Change Password for Others', 'Staff Users', 'N', 'A', NULL, NULL, 'Y', '1', 'WEB', 'B', 'N', 'N', 'B', 'Access Management', 'Y');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'DELSTAFFUSER', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'CHANGESELFPASSCU', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'UNBLOCKPINSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'UNBLOCKPASSSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'DELSTAFFUSER', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'CHANGESELFPASSCU', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'UNBLOCKPINSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'UNBLOCKPASSSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'DELSTAFFUSER', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'CHANGESELFPASSCU', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'UNBLOCKPINSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'UNBLOCKPASSSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'DELSTAFFUSER', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'CHANGESELFPASSCU', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'UNBLOCKPINSTAFF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'UNBLOCKPASSSTAFF', '1');


ALTER TABLE ROLES ADD VIEW_ROLES varchar(2) DEFAULT 'N';

update roles r SET view_roles='Y' WHERE domain_type='DISTB_CHAN'  AND role_code in ('C2SREV', 'C2SRECHARGE', 'C2CTRFINI', 'C2CRETURN', 'C2CTRF', 'C2CTRFAPR1', 'C2CTRFAPR2', 'C2CTRFAPR3', 'C2CWDL', 'C2CBUYVINI', 'C2CVINI', 'C2CVCTRFAPR1', 'C2CVCTRFAPR2', 'C2CVCTRFAPR3', 'BC2CAPPROVE', 'BC2CWDRAPP', 'BC2CINITIATE', 'BC2CWDRW', 'O2CRET', 'O2CINIT', 'INITVOMSOREQ', 'ADDCUSER', 'BATCHUSRINITIATE', 'DELETECUSER', 'EDITCUSER', 'VIEWCUSER', 'CHANGEPIN', 'CHANGESELFPIN', 'C2SUNBLOCKPIN', 'SUSPENDCUSER', 'C2SUNBLOCKPAS', 'MODSTAFFUSER', 'STFUSRAD', 'VIEWSTAFFUSER', 'RESSTAFFUSER', 'SUSSTAFFUSER', 'UNBARUSER', 'SCHEDULETOPUP', 'CANCELSCHEDULE', 'CNCLSCHEDULED', 'RESCHEDULETOPUP', 'VIEWSUBSSCHEDULE', 'VIEWSCHEDULED', 'BARUSER', 'CHANGELANG', 'VIEWBARREDLIST', 'SELFCHNLUSRMOD', 'DELSTAFFUSER', 'CHANGESELFPASSCU', 'UNBLOCKPINSTAFF', 'UNBLOCKPASSSTAFF');


--##########################################################################################################
--##
--##      PreTUPS_v7.33.0 DB Script
--##
--##########################################################################################################

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DOWNLOADSCHMSISDNBATCH', 'Download Scheduled Msisdn in Batch', 'DownloadSchMsidnBatch', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rstapi/v1/c2sServices/viewScheduleTopup', 'N', 'Y', 'DOWNLOADSCHMSISDNBATCH');


--##########################################################################################################
--##
--##      PreTUPS_v7.35.0 DB Script
--##
--##########################################################################################################

alter table oauth_users_login_info 
add column token_id VARCHAR(256) unique,
add column user_id VARCHAR(15);

DROP TABLE OAUTH_ACCESS_TOKENS ;
DROP TABLE OAUTH_REFRESH_TOKENS ;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_OFFLINE', 'Is the report offline or Online', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Is the report offline or Online', 'N', 'Y', 'C2S', 'Is the report offline or Online', TIMESTAMP '2021-06-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-06-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('OFFLINERPT_DOWNLD_PATH', 'Offline report download path', 'SYSTEMPRF', 'STRING', '/home/pretups/', NULL, NULL, 50, 'Offline report download path', 'N', 'Y', 'C2S', 'Offline report download path', TIMESTAMP '2021-08-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-08-26 09:44:51.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ALLOW_SAME_REPORT_EXEC', 'Allow same report execution multiple times', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Allow same report execution multiple times', 'N', 'Y', 'C2S', 'Allow same report execution multiple times', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TOT_RPT_EXEC_PERUSER', 'Total no. of users to be executed in parallel', 'SYSTEMPRF', 'INT', '5', 10, 20, 50, 'Total no. of users to be executed in parallel', 'N', 'Y', 'VOMS', 'Total no. of users to be executed in parallel', TIMESTAMP '2021-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');


CREATE TABLE REPORT_MASTER
  (REPORT_ID          VARCHAR (30) NOT NULL PRIMARY KEY,
  REPORT_NAME         VARCHAR (100) NOT NULL UNIQUE ,
   FILE_NAME_PREFIX  VARCHAR(10)  NOT NULL,
   CREATE_ON DATE,
   RPT_PROCESSOR_BEAN_NAME VARCHAR(50)  NOT NULL
   );

  CREATE TABLE OFFLINE_REPORT_PROCESS
  (REPORT_PROCESS_ID          VARCHAR(30) NOT NULL PRIMARY KEY,
  REPORT_ID          VARCHAR(30) NOT NULL ,
  FILE_NAME         VARCHAR(50) NOT NULL UNIQUE ,
  REPORT_INITIATED_BY VARCHAR(15),
  EXECUTION_START_TIME  TIMESTAMP,
  EXECUTION_END_TIME  TIMESTAMP,
  STATUS VARCHAR(20),
  INSTANCE_ID VARCHAR(20),
   CREATED_ON TIMESTAMP,
   TOTAL_RECORDS bigint,
   RPT_JSON_REQ  text,
   CONSTRAINT fk_ORP_RM
  FOREIGN KEY (REPORT_ID)
  REFERENCES REPORT_MASTER (REPORT_ID),
   CONSTRAINT fk_ORP_USER
  FOREIGN KEY (REPORT_INITIATED_BY)
  REFERENCES USERS(USER_ID)
  
   );  
   
   
INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('ALL', 'OFFLINERPT', 'ALL', 1, TIMESTAMP '2021-08-15 14:03:46.000000', 'NA', 'Entry for OFFLINE REPORT PROCESS ID');

INSERT INTO REPORT_MASTER
(REPORT_ID, REPORT_NAME,FILE_NAME_PREFIX ,CREATE_ON, RPT_PROCESSOR_BEAN_NAME)
VALUES('OFFLINE_101', 'Channel to subscriber tranfer commission','C2Strf', TIMESTAMP '2021-08-15 14:03:46.000000', 'C2STransferCommReportProcessor');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_ALLOW_CONTENT_TYPE', 'USER Allowed Content Type', 'SYSTEMPRF', 'STRING', 'CSV', NULL, NULL, 50, 'USER Allowed Content Type', 'N', 'N', 'P2P', 'VMS Allowed Content Type', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', NULL, 'Y');


INSERT INTO REPORT_MASTER
(REPORT_ID, REPORT_NAME, CREATE_ON, RPT_PROCESSOR_BEAN_NAME, FILE_NAME_PREFIX)
VALUES('OFFLINE_102', 'Passbook Others report ', TIMESTAMP '2021-08-15 14:03:46.000000', 'PassbookOthersProcessor', 'PBO');

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('RSTATUS', 'Recharge statuses', TIMESTAMP '2008-02-07 10:50:08.000000', 'ADMIN', TIMESTAMP '2007-02-10 10:50:08.000000', 'ADMIN', 'N');

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('TRINOUT', 'Transaction In or out', TIMESTAMP '2008-02-07 10:50:08.000000', 'ADMIN', TIMESTAMP '2007-02-10 10:50:08.000000', 'ADMIN', 'N');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('200', 'SUCCESS','RSTATUS', 'Y', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('206', 'FAIL','RSTATUS', 'Y', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('250', 'AMBIGUOUS','RSTATUS', 'Y', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('IN', 'In','TRINOUT', 'Y', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('OUT', 'Out','TRINOUT', 'Y', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-05 00:00:00.000000', 'ADMIN');





INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('REST', 'Rest Gateway', 'SRTYP', 'Y', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN');

  INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('MAPPGW', 'Mobile App Gateway', 'SRTYP', 'Y', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN');

  INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('XMLGW', 'XML Gateway', 'SRTYP', 'Y', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-10-14 00:00:00.000000', 'ADMIN');
  
  
  

-- c2s transfer and commission roles
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'C2STRCSRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'C2STRCSRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'C2STRCSRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'C2STRCSRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'C2STRCSRPTCOMM', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'C2STRCSRPTCOMM', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2STRCSRPTCOMM', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2STRCSRPTCOMM', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2STRCSRPTCOMM', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'C2STRCSRPTCOMM', '1');

-- for staff roles  c2s transfer and commission

INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'STFC2STRFRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'STFC2STRFRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'STFC2STRFRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'STFC2STRFRPTCOMM', 'Channel to subscriber transfer & commission', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'STFC2STRFRPTCOMM', '1');

  INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGD0000002760', 'C2STRCSRPTCOMM', 'WEB');

  INSERT INTO USER_ROLES
(USER_ID, ROLE_CODE, GATEWAY_TYPES)
VALUES('NGD0000002762', 'STFC2STRFRPTCOMM', 'WEB');



-- O2C Transfer details......


  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'O2CTRFDETRPT_ANG', 'Operator to Channel Transfer Details', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'O2CTRFDETRPT_ANG', 'Operator to Channel Transfer Details', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'O2CTRFDETRPT_ANG', 'Operator to Channel Transfer Details', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'O2CTRFDETRPT_ANG', 'Operator to Channel Transfer Details', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'O2CTRFDETRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'O2CTRFDETRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'O2CTRFDETRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'O2CTRFDETRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'O2CTRFDETRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'O2CTRFDETRPT_ANG', '1');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'O2CTRFDETRPT_ANG', '1');

  

--o2C Transfer acknowledge

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'O2CTRANSFERACK_ANG', 'Operator to Channel Acknowledgement', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'O2CTRANSFERACK_ANG', 'Operator to Channel Acknowledgement', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'O2CTRANSFERACK_ANG', 'Operator to Channel Acknowledgement', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'O2CTRANSFERACK_ANG', 'Operator to Channel Acknowledgement', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'O2CTRANSFERACK_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'O2CTRANSFERACK_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'O2CTRANSFERACK_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'O2CTRANSFERACK_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'O2CTRANSFERACK_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'O2CTRANSFERACK_ANG', '1');




  

--Low threshold Report


  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'LOWTHRSHRPT_ANG', 'Low Threshold & Transaction Report', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'LOWTHRSHRPT_ANG', 'Low Threshold & Transaction Report', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'LOWTHRSHRPT_ANG', 'Low Threshold & Transaction Report', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'LOWTHRSHRPT_ANG', 'Low Threshold & Transaction Report', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'LOWTHRSHRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'LOWTHRSHRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'LOWTHRSHRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'LOWTHRSHRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'LOWTHRSHRPT_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'LOWTHRSHRPT_ANG', '1');




  
  --Profile  -> View self details

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'VIEWCUSERSELF_ANG', 'View Self Details', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'VIEWCUSERSELF_ANG', 'View Self Details', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'VIEWCUSERSELF_ANG', 'View Self Details', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'VIEWCUSERSELF_ANG', 'View Self Details', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'VIEWCUSERSELF_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'VIEWCUSERSELF_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'VIEWCUSERSELF_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'VIEWCUSERSELF_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'VIEWCUSERSELF_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'VIEWCUSERSELF_ANG', '1');

--Profile  -> Self Balance

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'SELFBALANCE_ANG', '1');

--Profile  -> Self Balance


  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'SELFBALANCE_ANG', 'Self Balance', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'SELFBALANCE_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'SELFBALANCE_ANG', '1');


-- Profile ->self counter

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'SELFCOUNTER_ANG', 'Self Threshold and Usage', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'SELFCOUNTER_ANG', 'Self Threshold and Usage', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'SELFCOUNTER_ANG', 'Self Threshold and Usage', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'SELFCOUNTER_ANG', 'Self Threshold and Usage', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'SELFCOUNTER_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'SELFCOUNTER_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'SELFCOUNTER_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'SELFCOUNTER_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'SELFCOUNTER_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'SELFCOUNTER_ANG', '1');

-- Profile - > SELFCHNLUSRMOD_ANG



  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'SELFCHNLUSRMOD_ANG', 'Self details modify', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'SELFCHNLUSRMOD_ANG', 'Self details modify', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'SELFCHNLUSRMOD_ANG', 'Self details modify', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'SELFCHNLUSRMOD_ANG', 'Self details modify', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'SELFCHNLUSRMOD_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'SELFCHNLUSRMOD_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'SELFCHNLUSRMOD_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'SELFCHNLUSRMOD_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'SELFCHNLUSRMOD_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'SELFCHNLUSRMOD_ANG', '1');




-- Profile - > CHANGELANG_ANG

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'CHANGELANG_ANG', 'Change Language', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'CHANGELANG_ANG', 'Change Language', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'CHANGELANG_ANG', 'Change Language', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'CHANGELANG_ANG', 'Change Language', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Profile', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'CHANGELANG_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'CHANGELANG_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'CHANGELANG_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'CHANGELANG_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'CHANGELANG_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'CHANGELANG_ANG', '1');

---SECURITY SETTINGS

-- chage pin for self


  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'CHANGESELFPIN_ANG', 'Change PIN for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'CHANGESELFPIN_ANG', 'Change PIN for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'CHANGESELFPIN_ANG', 'Change PIN for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'CHANGESELFPIN_ANG', 'Change PIN for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'CHANGESELFPIN_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'CHANGESELFPIN_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'CHANGESELFPIN_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'CHANGESELFPIN_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'CHANGESELFPIN_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'CHANGESELFPIN_ANG', '1');


---

  INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'CHANGESELFPASSCU_ANG', 'Change Password for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'CHANGESELFPASSCU_ANG', 'Change Password for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'CHANGESELFPASSCU_ANG', 'Change Password for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'CHANGESELFPASSCU_ANG', 'Change Password for Self', 'Home Screen (top  right)', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Security Settings', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'CHANGESELFPASSCU_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'CHANGESELFPASSCU_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'CHANGESELFPASSCU_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'CHANGESELFPASSCU_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'CHANGESELFPASSCU_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'CHANGESELFPASSCU_ANG', '1');




INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('COMP_SHOP', 'BULKUSERADDSTS_ANG', 'Bulk user addition status', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('CORPORATE', 'BULKUSERADDSTS_ANG', 'Bulk user addition status', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('DISTB_CHAN', 'BULKUSERADDSTS_ANG', 'Bulk user addition status', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'Y', 'Y');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE, SUB_GROUP_NAME, SUB_GROUP_ROLE, VIEW_ROLES)
VALUES('OPERATOR', 'BULKUSERADDSTS_ANG', 'Bulk user addition status', 'Reports & Enquiries', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Detail Report', 'N', 'N');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('BCU', 'BULKUSERADDSTS_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUBCU', 'BULKUSERADDSTS_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'BULKUSERADDSTS_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'BULKUSERADDSTS_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'BULKUSERADDSTS_ANG', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('0000', 'BULKUSERADDSTS_ANG', '1');

DELETE FROM SERVICE_TYPE WHERE SERVICE_TYPE='FRC';
DELETE FROM SERVICE_KEYWORDS WHERE SERVICE_TYPE = 'FRC';
COMMIT;


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('AUTO_SEARCH', 'AUTO_SEARCH', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 5, 'Auto search username or loginId, true or false', 'N', 'Y', 'C2C', 'Auto search username or loginId', TIMESTAMP '2021-09-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-09-13 00:00:00.000000', 'ADMIN', NULL, 'Y');


--##########################################################################################################
--##
--##      PreTUPS_v7.39.0 DB Script
--##
--##########################################################################################################



INSERT
	INTO
	SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME,"TYPE",VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE)
VALUES('DEF_CHNL_TRANSFER_ALLOWED',
'Default transfer rules',
'SYSTEMPRF',
'STRING',
'C2CVOMSTRFINI,TRFINI',
NULL,
NULL,
50,
'Date format accepted by the system',
'N',
'N',
'C2C',
'Default transfer rules, will be used by MAPPGW',
TIMESTAMP '2005-07-13 00:00:00.000000',
'ADMIN',
TIMESTAMP '2005-07-13 00:00:00.000000',
'ADMIN',
NULL,
'Y');


--##########################################################################################################
--##
--##      PreTUPS_v7.40.0 DB Script
--##
--##########################################################################################################


INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2022', 'OX', 'NG', 1, '2022-01-13 09:56:37.000000', 'MINUTES', NULL);


--##########################################################################################################
--##
--##      PreTUPS_v7.43.3 DB Script
--##
--##########################################################################################################


alter table oauth_access_token alter column created_on type timestamp;
alter table oauth_access_token alter column modified_on type timestamp;
alter table oauth_refresh_token alter column created_on type timestamp;
alter table oauth_refresh_token alter column modified_on type timestamp;


CREATE TABLE NONCE_RECORD (
	NONCE_ID varchar(40) NULL,
	CREATED_ON timestamp NULL
);



--##########################################################################################################
--##
--##      PreTUPS_v7.50.7 DB Script
--##
--##########################################################################################################


CREATE TABLE role_ui_mapping 
   (	role_ui_parent_id VARCHAR(20), 
	role_ui_id VARCHAR(40), 
	rule_code VARCHAR(30)
   );
   
  
CREATE TABLE rule_ui_mapping 
   (	 rule_id VARCHAR(40), 
	rule_expression VARCHAR(200)
   );
   

CREATE TABLE page_ui_roles 
   (	role_code VARCHAR(30) NOT NULL, 
	page_code VARCHAR(20) NOT NULL, 
	 PRIMARY KEY (role_code, page_code));
	
CREATE TABLE pages_ui
   (	page_code VARCHAR(20) NOT NULL , 
	module_code VARCHAR(10) NOT NULL , 
	page_url VARCHAR(100) NOT NULL , 
	menu_name VARCHAR(35) NOT NULL , 
	show_menu VARCHAR(1) NOT NULL , 
	sequence_no numeric NOT NULL , 
	menu_level VARCHAR(1) NOT NULL , 
	parent_page_code VARCHAR(20), 
	image VARCHAR(100), 
	role_code VARCHAR(30), 
	domain_type VARCHAR(10), 
	 PRIMARY KEY (page_code));
	
	
	

--pages ui insertions

INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_RC_MAIN', 'C2S', '/recharge', 'Recharge', 'Y', 2, '1', 'ROOT', 'assets/images/recharge_icons/recharge.svg', NULL, 'DISTB_CHAN');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_RC_RC', 'C2S', '/recharge', 'Recharge', 'Y', 2.1, '2', 'P_RC_MAIN', NULL, NULL, 'DISTB_CHAN');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_RC_RV', 'C2S', '/reversal', 'Reversal', 'Y', 2.2, '2', 'P_RC_MAIN', NULL, NULL, 'DISTB_CHAN');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_RC_SCM', 'C2S', '/scheduleMgmt', 'Schedule Management', 'Y', 2.3, '2', 'P_RC_MAIN', NULL, NULL, 'DISTB_CHAN');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_HOME_MAIN', 'HOME', '/home', 'Home', 'Y', 1, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_C2C_MAIN', 'C2C', '/channeltochannel', 'Channel To Channel', 'Y', 3, '1', 'ROOT', 'assets/images/c2c_icons/channel_to_channel_sidebar.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_C2C_TX', 'C2C', '/channeltochannel', 'Transaction', 'Y', 3.1, '2', 'P_C2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_C2C_AP1', 'C2C', '/approvallevel/ap1', 'Approval Level 1', 'Y', 3.2, '2', 'P_C2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_C2C_AP2', 'C2C', '/approvallevel/ap2', 'Approval Level 2', 'Y', 3.3, '2', 'P_C2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_C2C_AP3', 'C2C', '/approvallevel/ap3', 'Approval Level 3', 'Y', 3.4, '2', 'P_C2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L1', 'COMMON', '/approvallevelo2c/ap1', 'Approval Level1', 'Y', 4, '1', 'ROOT', 'assets/images/Approve/Group 19.png', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_O2C_L1_TXN', 'COMMON', '/approvallevelo2c/ap1', 'Transaction', 'Y', 4.1, '2', 'P_APPR_L1', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L1_COMMISSION', 'COMMON', '/approvallevelo2c/focap1', 'Commission', 'Y', 4.2, '2', 'P_APPR_L1', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L1_CHNL_USR', 'COMMON', '/approvallevelchanneluser', 'Channel User', 'Y', 4.3, '2', 'P_APPR_L1', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_CHNL_USR_ADM_MAIN', 'CHNL_USR', '/channelAdmin/Single', 'Channel User', 'Y', 7, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_CHNL_USR_ADM_SNGL', 'CHNL_USR', '/channelAdmin/Single', 'Single', 'Y', 7.1, '2', 'P_CHNL_USR_ADM_MAIN', NULL, NULL, 'OPERATOR');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_CHNL_USR_ADM_BLK', 'CHNL_USR', '/channelAdmin/bulk/bulkadduser', 'Bulk', 'Y', 7.2, '2', 'P_CHNL_USR_ADM_MAIN', NULL, NULL, 'OPERATOR');
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L2', 'COMMON', '/approvallevelo2c/ap2', 'Approval Level 2', 'Y', 5, '1', 'ROOT', 'assets/images/Approve/Group 19.png', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_O2C_L2_TXN', 'COMMON', '/approvallevelo2c/ap2', 'Transaction', 'Y', 5.1, '2', 'P_APPR_L2', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L2_COMMISSION', 'COMMON', '/approvallevelo2c/focap2', 'Commission', 'Y', 5.2, '2', 'P_APPR_L2', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L2_CHNL_USR', 'COMMON', '/approvallevelchanneluser', 'Channel User', 'Y', 5.3, '2', 'P_APPR_L2', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L3', 'COMMON', '/approvallevelo2c/ap3', 'Approval Level 3', 'Y', 6, '1', 'ROOT', 'assets/images/Approve/Group 19.png', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_O2C_L3_TXN', 'COMMON', '/approvallevelo2c/ap3', 'Transaction', 'Y', 6.1, '2', 'P_APPR_L3', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_APPR_L3_COMMISSION', 'COMMON', '/approvallevelo2c/focap3', 'Commission', 'Y', 6.2, '2', 'P_APPR_L3', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_O2C_MAIN', 'O2C', '/operatortochannel', 'Operator To Channel', 'Y', 8, '1', 'ROOT', 'assets/images/o2c_icons/operatortochannel.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_O2C_TXN', 'O2C', '/operatortochannel', 'Trasaction', 'Y', 8.1, '2', 'P_O2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_O2C_COMM', 'O2C', '/commission', 'Commission', 'Y', 8.2, '2', 'P_O2C_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_STAFF_MAIN', 'STAFF', '/staffuser', 'Staff User', 'Y', 9, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_STAFF_ADMIN_MAIN', 'STAFF', '/channelAdmin/viewstaffuser', 'Staff User', 'Y', 10, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_VCH_DWNLD_ADM_MAIN', 'VCH_DWNLD', '/channelAdmin/createBatch', 'Voucher Download', 'Y', 11, '1', 'ROOT', 'assets/images/recharge_icons/recharge.svg', NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_VCH_CRT_BTCH_ADM', 'VCH_DWNLD', '/channelAdmin/createBatch', 'Create Batch', 'Y', 11.1, '2', 'P_VCH_DWNLD_ADM_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_VCH_DWNLD_ADMIN', 'VCH_DWNLD', '/channelAdmin/voucherDownload', 'Voucher Download', 'Y', 11.2, '2', 'P_VCH_DWNLD_ADM_MAIN', NULL, NULL, NULL);
INSERT INTO PAGES_UI
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, SHOW_MENU, SEQUENCE_NO, MENU_LEVEL, PARENT_PAGE_CODE, IMAGE, ROLE_CODE, DOMAIN_TYPE)
VALUES('P_CHNL_USR_MAIN', 'CHNL_USR', '/channelUser', 'Channel User', 'Y', 7, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'DISTB_CHAN');




--page_ui_roles insertions

INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2SREV', 'P_RC_RV');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2SRECHARGE', 'P_RC_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2SRECHARGE', 'P_RC_RC');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('SCHEDULETOPUP', 'P_RC_SCM');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CANCELSCHEDULE', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CNCLSCHEDULED', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('RESCHEDULETOPUP', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWSUBSSCHEDULE', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWSCHEDULED', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BATCHID', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('SCHTRFSTS', 'Recharge');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('UNBARUSER', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BARUSER', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWBARREDLIST', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2STRCSRPT', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('STFC2STRFRPT', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ZBALCOUNTERDET', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('O2CTRANSFERACK', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWTRFENQ', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CHNLTRFDETAILS', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('USERTRFOTP', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('UTRFCNFOTP', 'P_HOME_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRFINI', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CRETURN', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRF', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRFAPR1', 'P_C2C_AP1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRFAPR2', 'P_C2C_AP2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRFAPR3', 'P_C2C_AP3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CWDL', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CBUYVINI', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CVINI', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CVCTRFAPR1', 'P_C2C_AP1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CVCTRFAPR2', 'P_C2C_AP2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CVCTRFAPR3', 'P_C2C_AP3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BC2CAPPROVE', 'P_C2C_AP1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BC2CWDRAPP', 'P_C2C_AP1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BC2CINITIATE', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BC2CWDRW', 'P_C2C_TX');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2CTRFINI', 'P_C2C_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CBULKAPPRV1', 'R_O2CBULKAPPRV1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CBULKAPPRV2', 'R_O2CBULKAPPRV2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CBULKAPPRV3', 'R_O2CBULKAPPRV3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV1', 'P_APPR_O2C_L1_TXN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV2', 'P_APPR_O2C_L2_TXN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV3', 'P_APPR_O2C_L3_TXN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE1', 'P_APPR_L1_COMMISSION');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE2', 'P_APPR_L2_COMMISSION');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE3', 'P_APPR_L3_COMMISSION');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('OPTUSRAPRROLES', 'P_APPR_L1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV1', 'P_APPR_L1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE1', 'P_APPR_L1');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('C2SREV', 'P_RC_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('SCHEDULETOPUP', 'P_RC_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('APPROVALUSER', 'P_APPR_L1_CHNL_USR');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ADDCUSER', 'P_CHNL_USR_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('EDITCUSER', 'P_CHNL_USR_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DELETECUSER', 'P_CHNL_USR_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ADDCUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('EDITCUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DELETECUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ADDCUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('EDITCUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DELETECUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('OPTUSRAPRROLES', 'P_APPR_L2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV2', 'P_APPR_L2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE2', 'P_APPR_L2');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('OPTUSRAPRROLES', 'P_APPR_L3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CAPPRV3', 'P_APPR_L3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('FOCAPPROVE3', 'P_APPR_L3');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('APPROVALUSER', 'P_APPR_L2_CHNL_USR');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CCOMMISSION', 'P_O2C_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2CCOMMISSION', 'P_O2C_COMM');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2C', 'P_O2C_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_O2C', 'P_O2C_TXN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('SUSPENDCUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWCUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('RESUMECUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CHANGEPINCUSER', 'P_CHNL_USR_ADM_SNGL');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('SUSPENDCUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWCUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('RESUMECUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CHANGEPINCUSER', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('STFUSRAD', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('MODSTAFFUSER', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VIEWSTAFFUSER', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('R_STAFFUSERDET', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DELSTAFFUSER', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('CHANGEPINSTAFF', 'P_STAFF_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ADDCSTF', 'P_STAFF_ADMIN_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('MODCSTF', 'P_STAFF_ADMIN_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('VICSTF', 'P_STAFF_ADMIN_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ENBLVOUCHER', 'P_VCH_DWNLD_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DOWNVOMS', 'P_VCH_DWNLD_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ENBLVOUCHER', 'P_VCH_CRT_BTCH_ADM');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DOWNVOMS', 'P_VCH_DWNLD_ADMIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BATCHUSRINITIATE', 'P_CHNL_USR_ADM_BLK');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('BATCHUSRINITIATE', 'P_CHNL_USR_ADM_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('ADDCUSER', 'P_CHNL_USR_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('EDITCUSER', 'P_CHNL_USR_MAIN');
INSERT INTO PAGE_UI_ROLES
(ROLE_CODE, PAGE_CODE)
VALUES('DELETECUSER', 'P_CHNL_USR_MAIN');


--rule_ui_mapping insertions

INSERT INTO RULE_UI_MAPPING
(RULE_ID, RULE_EXPRESSION)
VALUES('R_channelToChannelSingle', '[C2S.Batch_recharge] || [C2S.TEST1]');
INSERT INTO RULE_UI_MAPPING
(RULE_ID, RULE_EXPRESSION)
VALUES('R_bulkToggle', '[C2C.Initiate_Batch_Transfer] || [C2C.Initiate_Batch_Withdraw]');
INSERT INTO RULE_UI_MAPPING
(RULE_ID, RULE_EXPRESSION)
VALUES('R_transferStock', '[C2C.Transfer_Stock]');
INSERT INTO RULE_UI_MAPPING
(RULE_ID, RULE_EXPRESSION)
VALUES('R_transferVoucher', '[C2C.Transfer_Vouchers]');



--role_ui_mapping insertions

INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Buy_Stock', 'C2CTRFINI');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Return_Stock', 'C2CRETURN');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Stock', 'C2CTRF');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Approval_1', 'C2CTRFAPR1');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Approval_2', 'C2CTRFAPR2');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Approval_3', 'C2CTRFAPR3');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Withdraw_Stock', 'C2CWDL');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Buy_Vouchers', 'C2CBUYVINI');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Vouchers', 'C2CVINI');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Vouchers_Approval_1', 'C2CVCTRFAPR1');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Vouchers_Approval_2', 'C2CVCTRFAPR2');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Transfer_Vouchers_Approval_3', 'C2CVCTRFAPR3');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Approve_Batch_Transfer', 'BC2CAPPROVE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Approve_Batch_Withdraw', 'BC2CWDRAPP');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Initiate_Batch_Transfer', 'BC2CINITIATE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2C', 'Initiate_Batch_Withdraw', 'BC2CWDRW');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Reversal', 'C2SREV');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Recharge', 'C2SRECHARGE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Batch_recharge', 'SCHEDULETOPUP');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Cancel_Subscriber_from_schedule', 'CANCELSCHEDULE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Cancel_scheduled_batch', 'CNCLSCHEDULED');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Reschedule_batch', 'RESCHEDULETOPUP');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'View_status_of_scheduled_subscriber', 'VIEWSUBSSCHEDULE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'View_status_of_scheduled_batch', 'VIEWSCHEDULED');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'View_batchid_report', 'BATCHID');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('C2S', 'Schedule_topup_status_report', 'SCHTRFSTS');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('O2C', 'Return_Stock', 'O2CRET');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('O2C', 'Initiate_Transfer', 'O2CINIT');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('O2C', 'Initiate_Voucher_order_request', 'INITVOMSOREQ');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Add_Channel_User', 'ADDCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Initiate_user_addition_in_batch', 'BATCHUSRINITIATE');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Delete_Channel_User', 'DELETECUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Modify_Channel_User', 'EDITCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'View_Channel_User', 'VIEWCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Change_PIN_for_others', 'CHANGEPIN');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Channel_User_PIN_Management', 'C2SUNBLOCKPIN');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Suspend_Channel_User', 'SUSPENDCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Resume_Channel_User', 'RESUMECUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'Change_Password_for_others', 'C2SUNBLOCKPAS');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'CHANGE_NOTIFICATION_LANG', 'CHNOTLAG');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'ASSOCIATE_PROFILE', 'ASSCUSR');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'VIEW_USER_HIERARCHY', 'CHNLUSRVW');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('channelUser', 'SUS_RES_BULK', 'UNREGUSERINBULK');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Modify_Staff_user', 'MODSTAFFUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Add_Staff_user', 'STFUSRAD');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'View_Staff_user', 'VIEWSTAFFUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Resume_Staff_user', 'RESSTAFFUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Delete_Staff_user', 'DELSTAFFUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Suspend_Staff_user', 'SUSSTAFFUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Change_PIN_for_Staff', 'CHANGEPINSTAFF');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Staff_User_PIN_Management', 'UNBLOCKPINSTAFF');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('staffUser', 'Access_Control_Management', 'UNBLOCKPASSSTAFF');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Admin_Domain_Type', 'OPERATOR');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Add_Admin_Channel_User', 'ADDCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Edit_Admin_Channel_User', 'EDITCUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Delete_Admin_Channel_User', 'DELETECUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Barred_for_delete', 'BARFORDEL');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Create_batch', 'ENBLVOUCHER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Voucher_Download', 'DOWNVOMS');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Supend_hierarchy', 'CHNLUSRSPN');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Resume_hierarchy', 'CHNLUSRRES');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Transfer_hierarchy', 'CHNLUSERTRF');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Auto_C2C_SOS', 'AUTOC2CCRLMT');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'ASSOCIATE_PROFILE', 'ASSCUSR');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('adminChannelUser', 'Edit_Admin_Channel_User_Roles', 'CUSEROLES');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'Unbar_User', 'UNBARUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'Bar_User', 'BARUSER');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_Barred_List', 'VIEWBARREDLIST');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_C2S_Details', 'C2STRCSRPT');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_C2S_Details_Staff', 'STFC2STRFRPT');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_Low_Threshold', 'ZBALCOUNTERDET');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_O2C_ACK', 'O2CTRANSFERACK');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_O2C_Transfer_Details', 'VIEWTRFENQ');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'View_C2C_Transfer_Details', 'CHNLTRFDETAILS');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'Channel_User_Transfer_Initiate', 'USERTRFOTP');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('homeScreen', 'Channel_User_Transfer_Confirmation', 'UTRFCNFOTP');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('securityScreen', 'View_Change_Pin', 'CHANGESELFPIN');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('securityScreen', 'View_Change_Password', 'CHANGESELFPASSCU');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('c2cEnquiryScreen', 'C2C_CHNL_ENQUIRY', 'C2CTRFENQ');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('c2cEnquiryScreen', 'C2C_STAFF_ENQUIRY', 'STFC2CTRFENQ');
INSERT INTO ROLE_UI_MAPPING
(ROLE_UI_PARENT_ID, ROLE_UI_ID, RULE_CODE)
VALUES('c2cEnquiryScreen', 'C2C_STAFF_SELF_ENQUIRY', 'SELFSTAFFC2C');





insert
	into
	SYSTEM_PREFERENCES
(PREFERENCE_CODE,
	NAME,
	type,
	VALUE_TYPE,
	DEFAULT_VALUE,
	MIN_VALUE,
	MAX_VALUE,
	MAX_SIZE,
	DESCRIPTION,
	MODIFIED_ALLOWED,
	DISPLAY,
	module,
	REMARKS,
	CREATED_ON,
	CREATED_BY,
	MODIFIED_ON,
	MODIFIED_BY,
	ALLOWED_VALUES,
	FIXED_VALUE)
values('BYPASS_EVD_KANEL_MES_STAT',
'Bypass Kannel Response for EVD',
'SYSTEMPRF',
'BOOLEAN',
'FALSE',
null,
null,
5,
'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response',
'Y',
'Y',
'C2S',
'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response',
TIMESTAMP '2020-06-01 12:45:41.000000',
'ADMIN',
TIMESTAMP '2020-06-01 12:45:41.000000',
'ADMIN',
null,
'Y');



DELETE  FROM CATEGORY_ROLES cr  WHERE CR.ROLE_CODE IN (
'BULKUSERADDSTS_ANG',
'CHANGELANG_ANG',
'CHANGESELFPASSCU_ANG',
'CHANGESELFPIN_ANG',
'LOWTHRSHRPT_ANG',
'O2CTRANSFERACK_ANG',
'O2CTRFDETRPT_ANG',
'SELFBALANCE_ANG',
'SELFCHNLUSRMOD_ANG',
'SELFCOUNTER_ANG',
'SLFCOMMENQ_ANG',
'VIEWCUSERSELF_ANG'
)

DELETE  FROM ROLES R  WHERE R.ROLE_CODE IN (
'BULKUSERADDSTS_ANG',
'CHANGELANG_ANG',
'CHANGESELFPASSCU_ANG',
'CHANGESELFPIN_ANG',
'LOWTHRSHRPT_ANG',
'O2CTRANSFERACK_ANG',
'O2CTRFDETRPT_ANG',
'SELFBALANCE_ANG',
'SELFCHNLUSRMOD_ANG',
'SELFCOUNTER_ANG',
'SLFCOMMENQ_ANG',
'VIEWCUSERSELF_ANG'
)

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('ALL', 'BULKPROCID', 'ALL', 1, TIMESTAMP '2022-01-01 14:03:46.000000', 'NA', 'Entry for bulk upload PROCESS ID');

UPDATE LOOKUPS  SET lookup_name ='Super Network Admin' WHERE LOOKUP_CODE ='SUNADM' AND LOOKUP_TYPE ='VOPUSR';

--Roles for O2C admin
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('O2C', 'Initiate_Transfer_admin', 'INITO2CTRF');
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('O2C', 'Initiate_Voucher_order_request_admin', 'INITO2CTRF');

         ALTER TABLE selector_amount_mapping
  add column    created_by varchar(20) NOT NULL,
add column	created_on timestamp NOT NULL,
add column	modified_by varchar(20) NOT NULL,
add column	modified_on timestamp NOT NULL;


update       selector_amount_mapping set created_by=(select user_id from users where category_code ='SUADM')   
update       selector_amount_mapping set MODIFIED_BY=(select user_id from users where category_code ='SUADM')
update       selector_amount_mapping SET CREATED_ON= CURRENT_DATE
update       selector_amount_mapping SET MODIFIED_ON= CURRENT_DATE

 ALTER TABLE selector_amount_mapping
  MODIFY (    created_by NOT NULL ,
	created_on NOT NULL ,
	modified_by NOT NULL ,
	modified_on NOT NULL )
            