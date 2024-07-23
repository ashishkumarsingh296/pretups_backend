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

