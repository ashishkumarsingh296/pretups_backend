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
    'Y', 'C2S', 'Auto Voucher Creation Allowed flag. If default_value column value is true then it will trigger auto  creation of vouchers else it will not trigger', SYSDATE, 'ADMIN', 
    SYSDATE, 'ADMIN', 'true,false', 'Y');
COMMIT;

alter table voms_products add AUTO_GENERATE VARCHAR2(5) default 'N';
Commit;
alter table voms_products add  AUTO_THRESHOLD VARCHAR2(10);
Commit;
alter table voms_products add  auto_quantity VARCHAR2(10);
Commit;
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('VOMSGENAUTO', TO_DATE('09/19/2017 14:47:46', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('09/19/2017 14:47:46', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('09/19/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'VOMS AUTO Generation Process', 'NG', 0);
COMMIT;

ALTER TABLE ROLES ADD (ACCESS_TYPE VARCHAR2(1 BYTE));
ALTER TABLE ROLES MODIFY(ACCESS_TYPE DEFAULT 'B');
update ROLES set ACCESS_TYPE='B';
Commit;

Insert into MESSAGE_GATEWAY
   (GATEWAY_CODE, GATEWAY_NAME, GATEWAY_TYPE, GATEWAY_SUBTYPE, PROTOCOL, 
    HANDLER_CLASS, NETWORK_CODE, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, HOST, STATUS, REQ_PASSWORD_PLAIN)
Values
   ('VSTK', 'VSTK', 'VSTK', 'SMPP', 'HTTP', 
    'com.client.pretups.gateway.parsers.STKParsers', 'VM', sysdate, 'SU0001', sysdate, 
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

SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('STK_MASTER_KEY', 'Master Key for STK', 'SYSTEMPRF', 'STRING', '6B4FC9246FB075B619626600EAA870F9', 
    NULL, NULL, 50, 'Master Key for STK', 'N', 
    'N', 'C2S', 'Master Key for STK', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
COMMIT;


--User Authentication API
SET DEFINE OFF;
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

SET DEFINE OFF;
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
    'Channel User Add', 'Channel User Add', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Channel User Add', 'Y', 'N', 
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
    'Channel User Modify', 'Channel User Modify', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Channel User Modify', 'Y', 'N', 
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
    'Geography API', 'Geography API', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Geography API', 'Y', 'N', 
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
    'View Geography API', 'View Geography API', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'View Geography API', 'Y', 'N', 
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
    'View User', 'View User', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'View User', 'Y', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 
    'Y');


CREATE TABLE ERP 
( 
ORDER_NUMBER NUMBER, 
HEADER_ID NUMBER, 
ORDER_TYPE_ID NUMBER, 
ORDER_TYPE VARCHAR2(100 BYTE), 
SHIP_TO_ORG_ID NUMBER, 
SALE_CHANNEL VARCHAR2(100 BYTE), 
PARTY_TYPE VARCHAR2(100 BYTE), 
PARTY_NUMBER NUMBER, 
PARTY_ID NUMBER, 
DEALER_CODE VARCHAR2(100 BYTE), 
PARTY_NAME VARCHAR2(100 BYTE), 
ORDER_DATE DATE, 
LINE_NUMBER NUMBER, 
LINE_ID NUMBER, 
INVENTORY_ITEM_ID NUMBER, 
ITEM_TYPE_CODE VARCHAR2(100 BYTE), 
ORDER_QUANTITY_UOM VARCHAR2(100 BYTE), 
SHIPPING_QUANTITY_UOM VARCHAR2(100 BYTE), 
ORDERED_QUANTITY NUMBER, 
PRICING_QUANTITY NUMBER, 
PRICING_QUANTITY_UOM VARCHAR2(100 BYTE), 
AMOUNT NUMBER, 
UNIT_LIST_PRICE NUMBER, 
TAX_VALUE NUMBER, 
ORDERED_ITEM VARCHAR2(100 BYTE), 
ORDERED_ITEM_ID NUMBER, 
LAST_MODIFIED DATE, 
CUST_PO_NUMBER VARCHAR2(100 BYTE), 
TRANSFER_ID VARCHAR2(20 BYTE), 
STATUS VARCHAR2(10 BYTE), 
ERROR_CODE VARCHAR2(20 BYTE) 
);

CREATE UNIQUE INDEX ERP_PK ON ERP (LINE_ID, ORDER_NUMBER) ;

ALTER TABLE ERP ADD CONSTRAINT ERP_PK PRIMARY KEY (LINE_ID, ORDER_NUMBER) ;	



ALTER TABLE pages ADD spring_page_url varchar2(100);
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
WHERE page_code =' C2CENQ001';


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
    sysdate, 'System out of service, please try again later.', 'Sistema fuera de servicio, por favor intente mas tarde.', 10, 'Y', 
    sysdate, 'SU0001', sysdate, 'SU0001', 'M', 
    5000, 5000, NULL);





Insert into INTERFACE_NODE_DETAILS
   (INTERFACE_ID, IP, PORT, URI, STATUS, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SEQUENCE_ID, 
    DELETED_ON, DELETED_BY, SUSPENDED_ON, SUSPENDED_BY)
 Values
   ('INTID00042', 'localhost', '8088', 'mockStealthIntegration', 'Y', 
    sysdate, 'CCLA0000054100', sysdate, 'CCLA0000054100', 'NODID00054', 
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
    'Enquiry by Transaction ID', 'Enquiry by Transaction ID', 'N', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Enquiry by Transaction ID', 'N', 'N', 
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
    'Last Txn Status by Subscriber MSISDN', 'Last Txn Status by Subscriber MSISDN', 'N', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Last Txn Status by Subscriber MSISDN', 'N', 'N', 
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
    'C2S Summary Enquiry', 'C2S Summary Enquiry', 'N', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'C2S Summary Enquiry', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE MSISDN1 PIN SERVICETYPE', 
    'Y');
	
	

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

-----------------------------VMS Burn rate indicator scripts-----------------------------------


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('VOMSBURNED', TO_DATE('05/18/2017 13:29:15', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('05/17/2017 17:46:02', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('05/18/2017 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    120, 1440, 'Voucher Burned rate indicator process', 'NG', 0);
COMMIT;

CREATE TABLE VOMS_DAILY_BURNED_VOUCHERS
(
  SUMMARY_DATE             DATE NOT NULL,
  PRODUCT_ID               VARCHAR2(15 BYTE) NOT NULL,
  PRODUCTION_NETWORK_CODE  VARCHAR2(2 BYTE) NOT NULL,
  USER_NETWORK_CODE        VARCHAR2(2 BYTE) NOT NULL,
  USER_ID                  VARCHAR2(15 BYTE) NOT NULL,
  TOTAL_DISTRIBUTED        NUMBER(16)           DEFAULT 0,
  TOTAL_RECHARGED          NUMBER(16)           DEFAULT 0,
  TOTAL_EXPIRED            NUMBER(16)           DEFAULT 0,
  TOTAL_STOLEN_DMG         NUMBER(16)           DEFAULT 0
);

 alter table VOMS_DAILY_BURNED_VOUCHERS add constraint t_pk primary key (user_id,product_id,user_network_code,production_network_code,summary_date); 
 
 alter table voms_daily_burned_vouchers ADD ( TOTAL_ONHOLD NUMBER(16)  DEFAULT 0,
TOTAL_SUSPENDED  NUMBER(16)  DEFAULT 0);
 
 
---------------------------------Burn rate indicator package---------------------------------------



CREATE OR REPLACE PACKAGE voms_burned_rate_pkg
AS
   PROCEDURE get_voms_data_dtrange (
      aiv_fromdate          IN       VARCHAR2,
      aiv_todate            IN       VARCHAR2,
      aov_message           OUT      VARCHAR2,
      aov_messageforlog     OUT      VARCHAR2,
      aov_sqlerrmsgforlog   OUT      VARCHAR2
   );

   PROCEDURE voms_burnrate_indicator (
      p_date               IN       DATE,
      p_returnmessage      OUT      VARCHAR2,
      p_returnlogmessage   OUT      VARCHAR2,
      p_sqlerrormessage    OUT      VARCHAR2
   );
END;
/


CREATE OR REPLACE PACKAGE BODY voms_burned_rate_pkg
AS
   PROCEDURE get_voms_data_dtrange (
      aiv_fromdate          IN       VARCHAR2,
      aiv_todate            IN       VARCHAR2,
      aov_message           OUT      VARCHAR2,
      aov_messageforlog     OUT      VARCHAR2,
      aov_sqlerrmsgforlog   OUT      VARCHAR2
   )
   IS
      ld_from_date            DATE;
      ld_to_date              DATE;
      n_date_for_process      DATE;
      voms_already_executed   NUMBER (1);
      sqlexception            EXCEPTION;
      alreadydoneexception    EXCEPTION;
      mainexception           EXCEPTION;
   BEGIN
      ld_from_date := TO_DATE (aiv_fromdate, 'dd/mm/yy');
      ld_to_date := TO_DATE (aiv_todate, 'dd/mm/yy');
      n_date_for_process := ld_from_date;
      voms_already_executed := 0;

      WHILE n_date_for_process <= ld_to_date
      ---run the process for each date less than the To Date
      LOOP
         DBMS_OUTPUT.put_line ('EXCEUTING FOR ::::::::' || n_date_for_process);

         BEGIN
            ---Check if the process has already run for the date
            SELECT 1
              INTO voms_already_executed
              FROM process_status
             WHERE process_id = 'VOMSBURNED'
               AND executed_upto >= n_date_for_process;

            DBMS_OUTPUT.put_line
                         (   'Burned Vouchers process already Executed, Date:'
                          || n_date_for_process
                         );
            aov_message := 'FAILED';
            aov_messageforlog :=
                  'Burned Vouchers process already Executed, Date:'
               || n_date_for_process;
            aov_sqlerrmsgforlog := ' ';
            RAISE alreadydoneexception;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               UPDATE process_status
                  SET executed_upto = n_date_for_process,
                      executed_on = SYSDATE
                WHERE process_id = 'VOMSBURNED';

               COMMIT;
               aov_message := 'SUCCESS';
               aov_messageforlog :=
                     'Burned Vouchers process successfully executed, Date Time:'
                  || SYSDATE;
               aov_sqlerrmsgforlog := ' ';
            WHEN alreadydoneexception
            THEN          --exception handled in case process already executed
               aov_sqlerrmsgforlog := SQLERRM;
               RAISE mainexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.put_line
                  (   'OTHERS Error when checking if  process has already been executed'
                   || SQLERRM
                  );
               aov_messageforlog :=
                     'OTHERS Error when checking if  process has already been executed, Date:'
                  || n_date_for_process;
               aov_sqlerrmsgforlog := SQLERRM;
               RAISE mainexception;
         END;

         voms_burnrate_indicator (n_date_for_process,
                                  aov_message,
                                  aov_messageforlog,
                                  aov_sqlerrmsgforlog
                                 );
         n_date_for_process := n_date_for_process + 1;
         COMMIT;
      END LOOP;
   EXCEPTION                            --Exception Handling of main procedure
      WHEN mainexception
      THEN
         ROLLBACK;
         DBMS_OUTPUT.put_line ('mainException Caught=' || SQLERRM);
         aov_message := 'FAILED';
      WHEN OTHERS
      THEN
         ROLLBACK;
         DBMS_OUTPUT.put_line ('OTHERS ERROR in Main procedure:=' || SQLERRM);
         aov_message := 'FAILED';
   END get_voms_data_dtrange;                          --End of main procedure

   PROCEDURE voms_burnrate_indicator (
      p_date               IN       DATE,
      p_returnmessage      OUT      VARCHAR2,
      p_returnlogmessage   OUT      VARCHAR2,
      p_sqlerrormessage    OUT      VARCHAR2
   )
   IS
      rcd_count                 NUMBER;
      p_consumed                NUMBER;
      p_distributed             NUMBER;
      p_stolen                  NUMBER;
      p_expired                 NUMBER;
      p_onhold                  NUMBER;
      p_suspended               NUMBER; 
      p_status                  VARCHAR (20);
      p_userid                  VARCHAR (20);
      p_product_id              VARCHAR (15);
      p_productionnetworkcode   VARCHAR (2);
      p_usernetworkcode         VARCHAR (2);
      p_modifiedon              DATE;
      sqlexception              EXCEPTION;
      exitexception             EXCEPTION;
      notinsertexception        EXCEPTION;

      CURSOR total_data
      IS
         SELECT   COUNT (1) AS total_count, user_id, product_id,
                  production_network_code, user_network_code, current_status,
                  TRUNC (TO_DATE (modified_on)) AS modified_on
             FROM voms_vouchers
            WHERE modified_on >= p_date
              AND modified_on < p_date + 1
              AND current_status IN ('EN', 'CU', 'ST','DA', 'EX','OH','S')
              AND user_id != ' '
         GROUP BY user_id,
                  product_id,
                  current_status,
                  production_network_code,
                  user_network_code,
                  TRUNC (TO_DATE (modified_on));
   BEGIN
      p_stolen := 0;
      p_distributed := 0;
      p_consumed := 0;
      p_expired := 0;
      p_onhold  := 0;
      p_suspended := 0;
      p_status := '';
      p_userid := '';
      p_product_id := '';
      p_productionnetworkcode := '';
      p_usernetworkcode := '';
      p_modifiedon := '';


      FOR total_data_cur IN total_data
      LOOP
         p_stolen := 0;
         p_distributed := 0;
         p_consumed := 0;
         p_expired := 0;
         p_onhold  := 0;
         p_suspended := 0;
         p_userid := total_data_cur.user_id;
         p_status := total_data_cur.current_status;
         p_product_id := total_data_cur.product_id;
         p_productionnetworkcode := total_data_cur.production_network_code;
         p_usernetworkcode := total_data_cur.user_network_code;
         p_modifiedon := total_data_cur.modified_on;

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
         BEGIN
            SELECT 1
              INTO rcd_count
              FROM voms_daily_burned_vouchers
             WHERE summary_date = p_modifiedon
               AND user_id = p_userid
               AND production_network_code = p_productionnetworkcode
               AND user_network_code = p_usernetworkcode
               AND product_id = p_product_id;

            DBMS_OUTPUT.put_line ('rcd_count=' || rcd_count);
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN
               --when no row returned for the distributor for particular date
               DBMS_OUTPUT.put_line
                       ('No Record found in voms_daily_burned_vouchers table');
               rcd_count := 0;
            WHEN sqlexception
            THEN
               DBMS_OUTPUT.put_line
                  (   'SQL EXCEPTION while checking for voms_daily_burned_vouchers  ='
                   || SQLERRM
                  );
               p_returnmessage := 'FAILED';
               p_returnlogmessage :=
                  'Exception while checking is record exist in voms_daily_burned_vouchers table ';
               RAISE sqlexception;
            WHEN OTHERS
            THEN
               DBMS_OUTPUT.put_line
                                  ('Exception while checking is record exist');
               p_returnmessage := 'FAILED';
               p_returnlogmessage :=
                  'Exception while checking is record exist in voms_daily_burned_vouchers table ';
               RAISE sqlexception;
         END;

         BEGIN
            IF rcd_count = 0
            THEN
               INSERT INTO voms_daily_burned_vouchers
                           (summary_date, product_id,
                            production_network_code, user_network_code,
                            user_id, total_distributed, total_recharged,
                            total_expired, total_stolen_dmg, total_onhold, total_suspended
                           )
                    VALUES (p_modifiedon, p_product_id,
                            p_productionnetworkcode, p_usernetworkcode,
                            p_userid, p_distributed, p_consumed,
                            p_expired, p_stolen , p_onhold , p_suspended
                           );

               p_returnmessage := 'SUCCESS';
               p_returnlogmessage :=
                  'Records successfully inserted in voms_daily_burned_vouchers table ';
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
            END IF;
         EXCEPTION
            WHEN sqlexception
            THEN
               p_returnmessage := 'FAILED';
               DBMS_OUTPUT.put_line ('SQL Exception for updating records ');
               RAISE exitexception;
            WHEN notinsertexception
            THEN
               p_returnmessage := 'FAILED';
               DBMS_OUTPUT.put_line
                  ('Not able to insert record in voms_daily_burned_vouchers ');
               RAISE exitexception;
            WHEN OTHERS
            THEN
               p_returnmessage := 'FAILED';
               p_returnlogmessage :=
                  'Exception while inserting/updating voms_daily_burned_vouchers table ';
               RAISE exitexception;
         END;
      END LOOP;
 
   END;
END voms_burned_rate_pkg;


--------------------------------------Burn rate indicator package ends------------------------


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
    4, '1', '1',  '/vomsBurnRateIndicatorAction.do?method=loadLists');
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


CREATE TABLE TEMP_BURN_RATE_INDICATOR
(
  ROW_NUMBER  NUMBER(20),
  DATA        VARCHAR2(4000 BYTE)
);

      
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



  --------------------------------------------VMS Burn rate indicator scripts end------------------------------------------
UPDATE pages SET spring_page_url='/pretups/ChannelToChannelSearchAction.form' WHERE page_code='C2CTRFDMM';
UPDATE pages SET spring_page_url='/pretups/ChannelToChannelSearchAction.form' WHERE page_code='C2CTRF001A';
UPDATE ids SET frequency='HOURS' WHERE id_type='OT' AND id_year='2017';
  COMMIT;
  
  
update req_message_gateway set underprocess_check_reqd='Y' where gateway_code='REST';
COMMIT; 

update service_type set status='N' where service_type in ('VRAG','ADV','CAUT','WRC','VR','VQ','MRC','VB','VSCH');
COMMIT; 

update req_message_gateway set underprocess_check_reqd='N' where gateway_code='REST';
COMMIT;


CREATE TABLE TPS_DETAILS
(
  TPS_DATE_TIME                    DATE           NOT NULL,
  INSTANCE_CODE                 VARCHAR2(3),
  TPS							NUMBER(10),
  TPS_DATE 						DATE  
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
    'c2s.change.pin', 'MAX TPS DETAILS', 'Y', sysdate, 'ADMIN', 
   sysdate, 'ADMIN', 'Max TPS Calculation Per Hour', 'N', 'N', 
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
   sysdate, 'SU0001',sysdate, 'SU0001', 'SVK9000076', 
    NULL, 'TYPE,QDATE,QHOUR');
	

update pages set menu_item='Y',menu_level=1 where PAGE_CODE in ('O2CBWDR01M','O2CWRAP01M');
  
 update pages set menu_level=2 where  PAGE_CODE in ('O2CBWDR01A','O2CWRAP01A');
commit;

--##########################################################################################################
--##
--##      PreTUPS_v7.2.0 DB Script
--##
--##########################################################################################################
--As discussed with Shishupal, these script will not be a part of 7.2.0

--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR001';
--UPDATE PAGES   SET SPRING_PAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTR01A';
--UPDATE PAGES   SET SPRING_PFAGE_URL = '/userprofile/userprofilethreshold.form' where PAGE_CODE = 'USRCNTRDMM';

--commit;

CREATE TABLE CACHE_TYPES
(
  CACHE_CODE            VARCHAR2(20 BYTE) NOT NULL,
  CACHE_NAME            VARCHAR2(50)     NOT NULL,
  STATUS                      VARCHAR2(1 BYTE)  NOT NULL,
  CACHE_KEY        VARCHAR2(70)     NOT NULL
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



UPDATE PAGES 
SET PAGE_CODE='URCLOBL001', MODULE_CODE='CHRPTUSR', PAGE_URL='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', MENU_NAME='Users Closing Balance', MENU_ITEM='Y', SEQUENCE_NO=26, MENU_LEVEL='2', APPLICATION_ID='1', SPRING_PAGE_URL='/reports/userClosingBalance.form' 
WHERE PAGE_CODE='URCLOBL001';


UPDATE PAGES 
SET PAGE_CODE='URCLOBL01A', MODULE_CODE='CHRPTUSR', PAGE_URL='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', MENU_NAME='Users Closing Balance', MENU_ITEM='N', SEQUENCE_NO=26, MENU_LEVEL='2', APPLICATION_ID='1', SPRING_PAGE_URL='/reports/userClosingBalance.form' 
WHERE PAGE_CODE='URCLOBL01A';


UPDATE PAGES 
SET PAGE_CODE='URCLOBLDMM', MODULE_CODE='CHRPTUSR', PAGE_URL='/userClosingBalance.do?method=loadUserClosingBalanceInputPage', MENU_NAME='Users Closing Balance', MENU_ITEM='Y', SEQUENCE_NO=26, MENU_LEVEL='1', APPLICATION_ID='1', SPRING_PAGE_URL='/reports/userClosingBalance.form' 
WHERE PAGE_CODE='URCLOBLDMM';

COMMIT;

ALTER TABLE cache_types
ADD PRIMARY KEY (cache_code); 

COMMIT;


INSERT INTO system_preferences
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('INET_REPORT_ALLOWED', 'INET REPORT WITH DATA TABLE ALLOWED', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'TO ENABLE INET REPORT ALONG WITH DATA TABLE', 'Y', 'N', 'C2S', 'TO ENABLE INET REPORT ALONG WITH DATA TABLE;CHANGES MADE DURING SPRING DEVELPOMENT', TIMESTAMP '2018-01-12 00:00:00.000', 'ADMIN', TIMESTAMP '2018-01-12 00:00:00.000', 'ADMIN', NULL, 'Y');


UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP001';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACP01A';
UPDATE pages SET spring_page_url ='/channelreport/load-additional-commission-details.form' where page_code='RPTVACPDMM';
   
COMMIT;

     
CREATE OR REPLACE FUNCTION "USERCLOSINGBALANCE" 
--This function is used for generate report of closing balance
--(p_userId  VARCHAR2) RETURN VARCHAR2
-- p_startDate date
-- p_endDate date
(p_userId  VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) RETURN VARCHAR2
IS
p_userCloBalDateWise VARCHAR2(4000) DEFAULT '';
balDate DATE;
balance NUMBER(10) DEFAULT 0;
productCode VARCHAR(10);
CURSOR c_userCloBal(p_userId VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) IS
        SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC, product_code ASC;
            BEGIN
        FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||'::'||balDate||'::'||balance||',';
        END LOOP;
                        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise)-1);
        END IF;
            RETURN p_userCloBalDateWise;
END;
/
COMMIT;

SET DEFINE OFF;
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

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SUBSRBR_MSG_STOP', 'Subscriber Message Stop', 'SYSTEMPRF', 'STRING', '', 
    NULL, NULL, 50, 'Subscriber Message Stop', 'N', 
    'Y', 'C2S', 'Subscriber Message Stop', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
COMMIT;
		
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('USR_BTCH_SUS_DEL_APRVL', 'User Batch Suspnd Deltn Apprvl', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'User Batch Suspnd Deltn Apprvl', 'N', 
    'Y', 'C2S', 'User Batch Suspnd Deltn Apprvl', SYSDATE, 'ADMIN', 
    SYSDATE, 'ADMIN', NULL, 'Y');	

COMMIT;
--##########################################################################################################
--##
--##      PreTUPS_v7.3.0 DB Script
--##
--##########################################################################################################
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDET001';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDET01A';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/zeroBalCounterDetail.form' WHERE PAGE_CODE='ZBALDETDMM';

UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUM001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUMDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/load-user-balances.form' WHERE PAGE_CODE='ZBALSUM01A' ;

UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEU01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEUDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/channel-user.form' WHERE PAGE_CODE='ROEU001' ;


UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACK001A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACK001' ;
UPDATE PAGES SET SPRING_PAGE_URL='/channeltransfer/o2cTransferAckAction.form' WHERE PAGE_CODE='O2CACKDMM' ;

UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCS001';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCS01A';
UPDATE PAGES SET   SPRING_PAGE_URL='/pretups/c2sTransfer.form' WHERE PAGE_CODE='RPTTRCSDMM';


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

UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV001';
UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOV01A';
UPDATE PAGES SET SPRING_PAGE_URL='/reports/userDailyBalMovement.form' WHERE page_code='UBALMOVDMM';
COMMIT;

--Operation Summary Report
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT001';
commit;
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPT00A';
commit;
update pages set spring_page_url = '/reports/operationSummaryReport.form' where page_code = 'OPTSRPTDMM';
commit;

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

---Staff self c2c reports

UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLF01A' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLFDMM' ;
UPDATE PAGES SET SPRING_PAGE_URL='/reports/staffSelfC2CReport.form' WHERE PAGE_CODE='STFSLF001' ;


--Associate Profile
UPDATE pages
SET spring_page_url='/user/load-associate-profile.form' WHERE page_code='ASSCUSR001';
COMMIT;

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

------------Other Commision--------

ALTER TABLE COMMISSION_PROFILE_SET_VERSION
 ADD (OTH_COMM_PRF_SET_ID  VARCHAR2(10 BYTE));
 
 ALTER TABLE CHANNEL_TRANSFERS  ADD (OTH_COMM_PRF_SET_ID  VARCHAR2(30 BYTE));
 
 ALTER TABLE CHANNEL_TRANSFERS_ITEMS
 ADD(
  OTH_COMMISSION_TYPE               VARCHAR2(10 BYTE),
  OTH_COMMISSION_RATE               NUMBER(16,4),
  OTH_COMMISSION_VALUE              NUMBER(20)
);

CREATE TABLE OTHER_COMM_PRF_SET
(
  OTH_COMM_PRF_SET_ID      VARCHAR2(10 BYTE)    NOT NULL,
  OTH_COMM_PRF_SET_NAME    NVARCHAR2(40)        NOT NULL,
  OTH_COMM_PRF_TYPE        VARCHAR2(10 BYTE)    NOT NULL,
  OTH_COMM_PRF_TYPE_VALUE  VARCHAR2(10 BYTE)    NOT NULL,
  NETWORK_CODE             VARCHAR2(2 BYTE)     NOT NULL,
  CREATED_ON               DATE                 NOT NULL,
  CREATED_BY               VARCHAR2(20 BYTE)    NOT NULL,
  MODIFIED_ON              DATE                 NOT NULL,
  MODIFIED_BY              VARCHAR2(20 BYTE),
  STATUS                   VARCHAR2(1 BYTE),
  O2C_CHECK_FLAG           VARCHAR2(1 BYTE)     DEFAULT 'N',
  C2C_CHECK_FLAG           VARCHAR2(1 BYTE)     DEFAULT 'N'
);

ALTER TABLE OTHER_COMM_PRF_SET ADD (
  CONSTRAINT PK_OTHER_COMM_PRF_SET
PRIMARY KEY
(OTH_COMM_PRF_SET_ID));

CREATE UNIQUE INDEX PK_OTHER_COMM_PRF_NAME ON OTHER_COMM_PRF_SET
(OTH_COMM_PRF_SET_NAME);




CREATE TABLE OTHER_COMM_PRF_DETAILS
(
  OTH_COMM_PRF_DETAIL_ID  VARCHAR2(10 BYTE),
  OTH_COMM_PRF_SET_ID     VARCHAR2(5 BYTE)      NOT NULL,
  START_RANGE             NUMBER(20),
  END_RANGE               NUMBER(20),
  OTH_COMMISSION_TYPE     VARCHAR2(5 BYTE)      NOT NULL,
  OTH_COMMISSION_RATE     NUMBER(16,4)
);

ALTER TABLE OTHER_COMM_PRF_DETAILS ADD (
  CONSTRAINT PK_OTHER_COMM_PRF_DETAILS
 PRIMARY KEY
 (OTH_COMM_PRF_DETAIL_ID));

	
	ALTER TABLE OTHER_COMM_PRF_DETAILS ADD (
  FOREIGN KEY (OTH_COMM_PRF_SET_ID) 
 REFERENCES OTHER_COMM_PRF_SET (OTH_COMM_PRF_SET_ID) );

Insert into LOOKUP_TYPES(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED) Values ('OTCTP', 'Other Commission Type', sysdate, 'ADMIN', sysdate, 'ADMIN', 'N');

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)  Values ('CAT', 'Category Code', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values ('GRAD', 'Grade', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY) Values('GAT', 'Gateway Code', 'OTCTP', 'Y', sysdate, 'ADMIN', sysdate, 'ADMIN');

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


Insert into SYSTEM_PREFERENCES    (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,    MIN_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY,    MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON,     MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE, MAX_VALUE) Values   ('OTH_COM_CHNL', 'Other Commission O2C and C2C', 'SYSTEMPRF', 'BOOLEAN', 'true',     NULL, 5, 'Other commission applicable flag', 'N', 'N',     'C2S', 'Other commission applicable flag', sysdate, 'ADMIN', sysdate,     'SU0001', 'true,false', 'Y', NULL);
	

Insert into IDS(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
Values    ('ALL', 'OT_COM_SID', 'ALL', 1, sysdate,'NA', 'Commission profile set ID');
Insert into IDS    (ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE,     FREQUENCY, DESCRIPTION) 
Values    ('ALL', 'OT_COM_DID', 'ALL', 1, sysdate ,'NA', 'Commission profile detail ID');

Commit;=======
UPDATE pages SET  page_url='/jsp/restrictedsubs/scheduleTopUpDetailsSts.jsp', spring_page_url='/jsp/restrictedsubs/scheduleTopUpDetails.jsp' WHERE page_code='SCHTOPUP02';



ALTER TABLE USERS ADD (MIGRATION_STATUS  VARCHAR2(5 BYTE));
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


CREATE TABLE TPS_DETAILS
(
  TPS_DATE_TIME                    DATE           NOT NULL,
  INSTANCE_CODE                 VARCHAR2(3),
  TPS							NUMBER(10),
  TPS_DATE 						DATE  
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
    'c2s.change.pin', 'MAX TPS DETAILS', 'Y', sysdate, 'ADMIN', 
   sysdate, 'ADMIN', 'Max TPS Calculation Per Hour', 'N', 'N', 
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
   sysdate, 'SU0001',sysdate, 'SU0001', 'SVK9000076', 
    NULL, 'TYPE,QDATE,QHOUR');
	

update pages set menu_item='Y',menu_level=1 where PAGE_CODE in ('O2CBWDR01M','O2CWRAP01M');
  
 update pages set menu_level=2 where  PAGE_CODE in ('O2CBWDR01A','O2CWRAP01A');
commit;

-- IRIS Changes Required 
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD (Promo_count Number(20));
ALTER TABLE DAILY_C2S_TRANS_DETAILS ADD (Promo_amount Number(24,2));
ALTER TABLE C2S_TRANSFERS add (    BONUS_AMOUNT NUMBER(20));
ALTER TABLE USER_DAILY_BALANCES  modify LAST_TRANSFER_NO          VARCHAR2(25 BYTE); 
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD (Promo_count Number(20));
ALTER TABLE MONTHLY_C2S_TRANS_DETAILS ADD (Promo_amount Number(24,2));

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('DIFF', 'Additional Bonus', 'COTYP', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('PROMO', 'Promotional Bonus', 'COTYP', 'Y', sysdate,
 'ADMIN', sysdate, 'ADMIN'); 

Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INTCT', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN');
Insert into LOOKUPS(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON,  CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values('IRIS', 'Promo Vas Interface', 'INCAT', 'Y', sysdate,  'ADMIN', sysdate, 'ADMIN'); 
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

----------------P2P DWH file creation info tag
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
		SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||PS.LAST_TRANSFER_ON
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        PS.REGISTERED_ON||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||STR.transfer_date||','||TO_CHAR(STR.transfer_date_time,'DD-MON-YY HH24:MI:SS')||','||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        ||STR.INFO1||','||STR.INFO2||','||STR.INFO3||','||STR.INFO4||','||STR.INFO5||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END;
/



ALTER TABLE SUBSCRIBER_TRANSFERS ADD VOUCHER_SERIAL_NUMBER varchar2(20);
ALTER TABLE SUBSCRIBER_TRANSFERS ADD (INFO1 varchar2(100),INFO2 varchar2(100),INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));
ALTER TABLE CHANNEL_TRANSFERS ADD ( INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));

--- Staff C2C Transfer
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2C001' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2C00A' ;
UPDATE PAGES  SET SPRING_PAGE_URL='/pretups/staffC2CTransferView.form'  WHERE PAGE_CODE='STFC2CDMM' ;

--For Fraud Management
ALTER TABLE P2P_SUBSCRIBERS_COUNTERS ADD VPIN_INVALID_COUNT NUMBER(5) DEFAULT 0;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VPIN_INVALID_COUNT', 'Invalid Pin Count For Voucher Recharge', 'SYSTEMPRF', 'INT', '5', 0, 10, 50, 'Invalid Pin Count For Voucher Recharge', 'N', 'N', 'P2P', 'Invalid Pin Count For Voucher Recharge', TIMESTAMP '2018-05-29 11:50:25.000000', 'ADMIN', TIMESTAMP '2018-05-29 15:50:25.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PIN_BLK_EXP_DRN', 'Voucher PIN block Expiry Duration', 'SYSTEMPRF', 'NUMBER', '300000', 0, 300000, 50, 'Voucher PIN block Expiry Duration in munutes', 'N', 'N', 'P2P', 'Voucher PIN block Expiry Duration in munutes', TIMESTAMP '2007-02-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', NULL, 'Y');


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
COMMIT;

UPDATE pages SET page_url='/userOperatorViewAction.do?method=loadSelfDetails',spring_page_url='/user/user_Operator_View_Action.form' WHERE page_code='VIEWUSRS01';
UPDATE pages SET page_url='/networkAction.do?method=loadNetworkListForChange&page=0',spring_page_url='/network/change-network.form' WHERE page_code in ('CNW001','CHNW001','CHNWDmm');
UPDATE pages SET page_url='/c2sreverse.do?method=c2sReversal',spring_page_url='/c2srecharge/reversal.form' WHERE page_code in ('C2SREV001','C2SREVDMM','C2SREV002');
commit;

ALTER TABLE VOMS_CATEGORIES MODIFY type VARCHAR2(10);
COMMIT;

--Persian Calendar - Starts --
INSERT ALL
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_FORMAT_CAL_JAVA', 'Date format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd', NULL, NULL, 50, 'Date format accepted by the system', 'Y', 'Y', 'C2S', 'Date format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DATE_TIME_FORMAT', 'Date time format for Java', 'SYSTEMPRF', 'STRING', 'yyyy/MM/dd HH:mm:ss', NULL, NULL, 50, 'Date Time format accepted by the system', 'Y', 'Y', 'C2S', 'Date Time format accepted by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_CALENDAR', 'Locale used for regional Calendar', 'SYSTEMPRF', 'STRING', 'fa_IR@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system of date value', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOCALE_ENGLISH', 'Locale used for english Calendar', 'SYSTEMPRF', 'STRING', '@calendar=persian', NULL, NULL, 50, 'Locale used by the system', 'Y', 'Y', 'C2S', 'Locale used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TIMEZONE_ID', 'Timezone id for Current location', 'SYSTEMPRF', 'STRING', 'Iran', NULL, NULL, 50, 'Timezone id used by the system', 'Y', 'Y', 'C2S', 'Timezone id used by the system', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_TYPE', 'Calendar type on GUI', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type to be displayed on GUI', 'Y', 'Y', 'C2S', 'Calendar type to be displayed on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDER_DATE_FORMAT', 'Date format for date selected by Cal on GUI', 'SYSTEMPRF', 'STRING', 'yyyy/mm/dd', NULL, NULL, 50, 'Date format for date selected by Calendar on GUI', 'Y', 'Y', 'C2S', 'Date format for date selected by Calendar on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CALENDAR_SYSTEM', 'Calendar system used in Query', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar system used in Query', 'Y', 'Y', 'C2S', 'Calendar system used in Query', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('FORMAT_MONTH_YEAR', 'Month and year format used', 'SYSTEMPRF', 'STRING', 'yyyy/mm', NULL, NULL, 50, 'Month and year format used', 'Y', 'Y', 'C2S', 'Month and year format used', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('EXTERNAL_CALENDAR_TYPE', 'Calendar type for external gateway', 'SYSTEMPRF', 'STRING', 'persian', NULL, NULL, 50, 'Calendar type for external gateway', 'Y', 'Y', 'C2S', 'Calendar type for external gateway', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_CAL_ICON_VISIBLE', 'Is calendar icon required on GUI', 'SYSTEMPRF', 'STRING', 'Y', NULL, NULL, 50, 'Is calendar icon required on GUI', 'Y', 'Y', 'C2S', 'Is calendar icon required on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IS_MON_DATE_ON_UI', 'Is date format contains MMM in date format', 'SYSTEMPRF', 'STRING', 'N', NULL, NULL, 50, 'Is date format contains MMM in date format', 'Y', 'Y', 'C2S', 'Is date format contains MMM in date format', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'Y')
SELECT * FROM dual
COMMIT;

UPDATE SYSTEM_PREFERENCES SET DEFAULT_VALUE = '0' WHERE PREFERENCE_CODE = 'FINANCIAL_YEAR_START';
COMMIT;
--Persian Calendar - Ends --

--Voucher changes for O2C module
ALTER TABLE VOMS_BATCHES
ADD (EXT_TXN_NO  VARCHAR2(20 BYTE));

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
    'Y', 'C2S', 'Voucher Status after thirdparty download', SYSDATE, 'ADMIN', 
    SYSDATE, 'ADMIN', NULL, 'Y');
COMMIT;


CREATE TABLE USER_VOUCHERTYPES
(
  USER_ID       VARCHAR2(15 BYTE)               NOT NULL,
  VOUCHER_TYPE  VARCHAR2(10 BYTE)               NOT NULL,
  STATUS        VARCHAR2(1 BYTE)                DEFAULT 'Y'                   NOT NULL,
  CONSTRAINT PK_USER_VOUCHER_TYPE PRIMARY KEY (USER_ID,VOUCHER_TYPE)
  
)

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

--- Claro Colombia code merge - Begin
SET DEFINE OFF;
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

--- User Closing Balance : Persian Date starts -------
CREATE OR REPLACE FUNCTION USERCLOSINGBALANCE
--This function is used for generate report of closing balance
--(p_userId  VARCHAR2) RETURN VARCHAR2
-- p_startDate date
-- p_endDate date
(p_userId  VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) RETURN VARCHAR2
IS
p_userCloBalDateWise VARCHAR2(4000) DEFAULT '';
balDate DATE;
balance NUMBER(15) DEFAULT 0;
productCode VARCHAR(10);
CURSOR c_userCloBal(p_userId VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) IS
        SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC, product_code ASC;
            BEGIN
        FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||'::'||TO_CHAR(balDate,'dd-mon-yy','nls_calendar=gregorian')||'::'||balance||',';
        END LOOP;
        
                        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise)-1);
         
        END IF;
            RETURN p_userCloBalDateWise;
END; 
COMMIT;
--- User Closing Balance : Persian Date ends -------
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
			
COMMIT:	

---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	starts ---------
UPDATE ROLES SET STATUS = 'N' WHERE ROLE_CODE IN ('IATBATCHIDRPT','IATBULKASCRPT','IATBULKRESRPT','IATBULKAPP','IATBTCHVIEW','IATSCHVIEW','IATSCHRPTSTS','IATBATCHIDRPT','IATBULKREG','IATBULKVIEW','IATBULKSUSPND','IATBULKRES','IATSCHDRC','IATBULKASC','IATBULKDEASC','IATBULKASCRPT','IATBULKRESRPT','IATSCHRPTSTS','IATCNCLSCH','IATCNCBTCH','IATRESCHD','IATBULKDEL','IATCNTRYMGMTAD','IATCNTRYMGMTMO','IATNWMGMTAD','IATNWMGMTMO','IATTRSERPT','IATTRFSUMRPT','IATTRFANSFERENQ','C2SSLABWISE','NORECHARGESLABWISE','NETWORKSUMRPT','RPTSCHDC2SRPT','RPTSCHDUDRRPT','RPTSCHDP2PRPT','DOWNLOADFILES');

COMMIT;
---- Disabling IAT, C2S Slab wise, Number of recharges slab wise, C2S Scheduling Download Report links	ends ---------


-----Sold voucher impact changes - Tejeshvi ---------------
alter table VOMS_VOUCHERS add SOLD_STATUS VARCHAR2(1) default 'N' not null;
alter table VOMS_VOUCHERS add SOLD_DATE DATE;

alter table VOMS_VOUCHERS_SNIFFER add SOLD_STATUS VARCHAR2(1) default 'N' not null;
alter table VOMS_VOUCHERS_SNIFFER add SOLD_DATE DATE;

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('SL', 'Sold', 'VSTAT', 'Y', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-07-04 00:00:00.000000', 'ADMIN');

COMMIT;
-----Sold voucher impact changes - Tejeshvi ---------------



-------------IFGO TAG FOR LMB REQUEST--
ALTER TABLE SOS_TRANSACTION_DETAILS ADD (INFO1 varchar2(100),INFO2 varchar2(100),INFO3 varchar2(100),INFO4 varchar2(100),INFO5 varchar2(100));
COMMIT;
-------------IFGO TAG FOR LMB REQUEST--

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi-------------- 
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
 	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
		SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||TO_CHAR(PS.LAST_TRANSFER_ON,'DD-fmMON-YY fmHH24:MI:SS')
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        TO_CHAR(PS.REGISTERED_ON,'DD-fmMON-YY fmHH24:MI:SS')||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||STR.transfer_date||','||TO_CHAR(STR.transfer_date_time,'DD-fmMON-YY fmHH24:MI:SS')||','||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END; 
COMMIT;

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------

-------Procedure USER_DAILY_CLOSING_BALANCE-Mohit-------------
CREATE OR REPLACE PROCEDURE USER_DAILY_CLOSING_BALANCE (
								rtn_message           OUT      VARCHAR2,
                            rtn_messageforlog     OUT      VARCHAR2,
                            rtn_sqlerrmsgforlog   OUT      VARCHAR2
                                   )
IS
/*       ############## TEMP table FOR USER daily  closing balance#####################
CREATE TABLE USER_DAILY_BAL_TEMP
(
  START_TIME DATE,
  END_TIME DATE,
  PROCESS VARCHAR2(20),
  STATUS_LOG VARCHAR2(100)
);              */

p_user_id  USER_BALANCES.user_id%TYPE;
p_product_code USER_BALANCES.product_code%TYPE;
p_network_code USER_BALANCES.network_code%TYPE;
p_network_code_for USER_BALANCES.network_code_for%TYPE;

q_user_id USER_DAILY_BALANCES.user_id%TYPE ;
q_network_code USER_DAILY_BALANCES.network_code%TYPE;
q_network_code_for USER_DAILY_BALANCES.network_code_for%TYPE;
q_product_code USER_DAILY_BALANCES.product_code%TYPE;
q_balance USER_DAILY_BALANCES.balance%TYPE;
q_prev_balance USER_DAILY_BALANCES.prev_balance%TYPE;
q_last_transfer_type USER_DAILY_BALANCES.last_transfer_type%TYPE;
q_last_transfer_no USER_DAILY_BALANCES.last_transfer_no%TYPE;
q_last_transfer_on USER_DAILY_BALANCES.last_transfer_on%TYPE;


q_daily_balance_updated_on DATE;
q_created_on DATE;
dayDifference NUMBER (5):= 0;
startCount NUMBER(3);
dateCounter DATE;




sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 CURSOR user_list_cur IS
        SELECT ub.user_id,ub.product_code,ub.network_code,ub.network_code_for
        FROM USER_BALANCES ub, USERS u
        WHERE ub.USER_ID=u.USER_ID
        AND TRUNC(daily_balance_updated_on) <>TRUNC(SYSDATE)
        AND TRUNC (u.modified_on) >= CASE WHEN (u.status='N') THEN (SYSDATE-366) WHEN (u.status='C') THEN (SYSDATE-366) ELSE TRUNC (u.modified_on) END;

--BEGIN

        --INSERT INTO USER_DAILY_BAL_TEMP(PROCESS,START_TIME) VALUES ('USER_DAILY_CLOSING_BALANCE_PROCESS',SYSDATE);


BEGIN
             FOR user_records IN user_list_cur
             LOOP
                     p_user_id:=user_records.user_id;
                    p_product_code:=user_records.product_code;
                    p_network_code:=user_records.network_code;
                    p_network_code_for:=user_records.network_code_for;
             BEGIN
                        SELECT user_id,network_code,network_code_for,product_code,balance,prev_balance,last_transfer_type,
                    last_transfer_no,last_transfer_on,TRUNC(daily_balance_updated_on) daily_balance_updated_on
                    INTO q_user_id,q_network_code,q_network_code_for,q_product_code,q_balance,q_prev_balance,
                    q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,q_daily_balance_updated_on
                    FROM USER_BALANCES
                    WHERE user_id=p_user_id
                    AND network_code=p_network_code
                    AND network_code_for=p_network_code_for
                    AND product_code=p_product_code
                    FOR UPDATE;

                    IF SQL%NOTFOUND
                    THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );
                         rtn_messageforlog:='Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                         rtn_sqlerrmsgforlog:=SQLERRM;
                         RAISE sqlexception;
                    END IF;

                    EXCEPTION
                       WHEN NO_DATA_FOUND
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('Exception NO_DATA_FOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM );

                       WHEN OTHERS
                       THEN
                          DBMS_OUTPUT.PUT_LINE ('OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id || SQLERRM );
                          rtn_messageforlog := 'OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
                          rtn_sqlerrmsgforlog := SQLERRM;
                          RAISE sqlexception;

             END;

             BEGIN

                     q_created_on  :=SYSDATE;
                  startCount := 1;
                  dateCounter:= q_daily_balance_updated_on;
                  dayDifference:= TRUNC(q_created_on) - q_daily_balance_updated_on;

                  DBMS_OUTPUT.PUT_LINE(' No Of dayDifference::'||dayDifference);


                FOR xyz IN startCount .. dayDifference
                LOOP


                     BEGIN


                       INSERT INTO USER_DAILY_BALANCES
                                  (balance_date,user_id,network_code,network_code_for,
                                    product_code,balance,prev_balance,last_transfer_type,
                                  last_transfer_no,last_transfer_on,created_on
                                  )
                              VALUES(dateCounter,q_user_id,q_network_code,
                                   q_network_code_for,q_product_code,q_balance,q_prev_balance,
                                   q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,
                                   q_created_on
                                  );
                      EXCEPTION
                        WHEN OTHERS
                        THEN
                           DBMS_OUTPUT.PUT_LINE ('Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id || SQLERRM );
                           rtn_messageforlog := 'Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id ;
                           rtn_sqlerrmsgforlog := SQLERRM;
                           RAISE sqlexception;



                    END;-- End of insert SQL

                    BEGIN

                        UPDATE USER_BALANCES SET
                               daily_balance_updated_on=q_created_on
                        WHERE user_id=p_user_id
                        AND product_code=p_product_code
                        AND network_code=p_network_code
                        AND network_code_for=p_network_code_for;

                        EXCEPTION
                           WHEN OTHERS
                           THEN
                              DBMS_OUTPUT.PUT_LINE ('Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id || SQLERRM );
                              rtn_messageforlog := 'Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id ;
                                 rtn_sqlerrmsgforlog := SQLERRM;
                              RAISE sqlexception;

                    END;-- End of update SQL

                 startCount:= startCount+1;
                dateCounter:= dateCounter+1;



           END LOOP;--End of daydiffrence loop

           COMMIT;
                DBMS_OUTPUT.PUT_LINE ('RECORDS COMMITED::'||p_user_id);

        END;--End oF Outer begin

            COMMIT;


     END LOOP;--End of outer for loop

                 rtn_message:='SUCCESS';
                 rtn_messageForLog :='PreTUPS USER_DAILY_CLOSING_BALANCE MIS successfully executed, Date Time:'||TO_CHAR(SYSDATE,'DD-fmMON-YY fmHH24:MI:SS');
                 rtn_sqlerrMsgForLog :=' ';

                 --UPDATE USER_DAILY_BAL_TEMP SET END_TIME=SYSDATE,STATUS_LOG=rtn_message WHERE PROCESS='USER_DAILY_CLOSING_BALANCE_PROCESS' AND trunc(START_TIME)=trunc(sysdate);
                 COMMIT;

         EXCEPTION --Exception Handling of main procedure
         WHEN sqlexception THEN
               ROLLBACK;
              DBMS_OUTPUT.PUT_LINE('sqlException Caught='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;

         WHEN OTHERS THEN
               ROLLBACK;
               DBMS_OUTPUT.PUT_LINE('OTHERS ERROR in USER_DAILY_CLOSING_BALANCE procedure:='||SQLERRM);
              rtn_message :='FAILED';
              RAISE sqlexception;



END;

--END; 

-------Procedure USER_DAILY_CLOSING_BALANCE-------------


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
			
			
			
		
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('P2P_DEBITCREDIT_COMMON', 'P2P API debit credit in same Request', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'P2P API debit credit in same Request', 'N', 
    'Y', 'C2S', 'P2P API debit credit in same Request', TO_DATE('06/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('09/11/2019 23:39:40', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');
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


SET DEFINE OFF;
Insert into LOOKUP_TYPES
   (LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, 
    MODIFIED_BY, MODIFIED_ALLOWED)
 Values
   ('TTYPE', 'Terminal Type', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/05/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', 'N');
COMMIT;


SET DEFINE OFF;
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

SET DEFINE OFF;
Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('SALESRPT', TO_DATE('06/06/2018 00:33:18', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('06/06/2018 06:14:32', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('06/06/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 0, 'Rightel Sales Daily Report', 'IR', 0);
COMMIT;

CREATE TABLE VOMS_DAILY_REPORT_DETAILS
(
  BATCH_ID           VARCHAR2(20 BYTE)          NOT NULL,
  BRANCH_CODE        VARCHAR2(9 BYTE),
  CITY_CODE          VARCHAR2(7 BYTE),
  TERMINAL_TYPE      VARCHAR2(3 BYTE),
  TERMINAL_CODE      VARCHAR2(9 BYTE),
  CREDIT_SALE_DATE   DATE,
  SERIAL_NO          VARCHAR2(16 BYTE),
  SERIAL_NO_CDIGIT   VARCHAR2(2 BYTE),
  PAYMENT_ID_NUMBER  VARCHAR2(12 BYTE),
  PAYMENT_TYPE       VARCHAR2(2 BYTE),
  TXN_ID             NUMBER(13),
  CARD_NUMBER        NUMBER(20),
  CREATED_BY         VARCHAR2(20 BYTE),
  CREATED_ON         DATE,
  MODIFIED_BY        VARCHAR2(20 BYTE),
  MODIFIED_ON        DATE,
  STATUS             VARCHAR2(10 BYTE),
  REMARKS            VARCHAR2(100 BYTE),
  EXTERNAL_CODE      VARCHAR2(20 BYTE),
  USER_ID            VARCHAR2(20 BYTE),
  VOUCHER_STATUS     VARCHAR2(10 BYTE),
  PRODUCT_ID         VARCHAR2(5 BYTE),
  DELIVER_DATE       DATE
)

CREATE TABLE VOMS_DAILY_REPORT_MASTER
(
  BATCH_ID          VARCHAR2(20 BYTE)           NOT NULL,
  USER_ID           VARCHAR2(15 BYTE)           NOT NULL,
  NETWORK_CODE      VARCHAR2(2 BYTE),
  NETWORK_CODE_FOR  VARCHAR2(2 BYTE),
  COMPANY_NAME      VARCHAR2(2 BYTE),
  OPERATOR_CODE     VARCHAR2(4 BYTE),
  BANK_CODE         VARCHAR2(3 BYTE),
  BATCH_FILE_NAME   VARCHAR2(100 BYTE),
  TOTAL_RECORD      NUMBER(12),
  TOTAL_AMOUNT      NUMBER(12),
  BATCH_DATE        DATE,
  CREATED_BY        VARCHAR2(20 BYTE),
  CREATED_ON        DATE,
  MODIFIED_BY       VARCHAR2(20 BYTE),
  MODIFIED_ON       DATE
)

drop table VOMS_DAILY_REPORT_DETAILS;
drop table VOMS_DAILY_REPORT_MASTER;

--This to be true only if external voucher uploaded in system
UPDATE SYSTEM_PREFERENCES  SET DEFAULT_VALUE='false' WHERE PREFERENCE_CODE='HASHING_ENABLE';
COMMIT;

---Date format for Response for API----
UPDATE SYSTEM_PREFERENCES
SET DEFAULT_VALUE='yyyy/MM/dd'
WHERE PREFERENCE_CODE='EXTERNAL_DATE_FORMAT';
commit;
------------------------------------

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PIN_REQUIRED_P2P', 'PIN Required for P2P', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', 'N', 'Y', 'P2P', 'Preference to define either PIN is required or not in P2P. Values may be TRUE or FALSE', TIMESTAMP '2005-07-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-17 16:17:53.000000', 'SU0001', NULL, 'Y');
COMMIT;

ALTER TABLE SUBSCRIBER_CONTROL ADD VPIN_INVALID_COUNT NUMBER(5) DEFAULT 0;

-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------
CREATE OR REPLACE PROCEDURE P2PDWHTEMPPRC 
(
 	   p_date IN DATE,
	   p_masterCnt OUT NUMBER,
	   p_transCnt  	  OUT 	 NUMBER,
	   p_message OUT VARCHAR2
)
IS

v_srno 				  		 	   NUMBER;
v_data							   VARCHAR (1000);

SQLException EXCEPTION;
EXITEXCEPTION 					 EXCEPTION;

CURSOR   P2P_MASTER  IS
        SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||TO_CHAR(PS.LAST_TRANSFER_ON,'DD-fmMON-YY fmHH24:MI:SS')
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        TO_CHAR(PS.REGISTERED_ON,'DD-fmMON-YY fmHH24:MI:SS')||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
        FROM P2P_SUBSCRIBERS PS, LOOKUPS LK, KEY_VALUES KV WHERE TRUNC(PS.ACTIVATED_ON) < p_date AND LK.LOOKUP_CODE = PS.STATUS
        AND LK.LOOKUP_TYPE = 'SSTAT'  AND KV.KEY(+) = PS.LAST_TRANSFER_STATUS AND KV.TYPE(+) = 'P2P_STATUS'  AND PS.STATUS IN('Y', 'S');

CURSOR P2P_TRANS IS
        SELECT STR.transfer_id||','||TO_CHAR(STR.transfer_date,'DD-fmMON-YY fmHH24:MI:SS')||','||TO_CHAR(STR.transfer_date_time,'DD-fmMON-YY fmHH24:MI:SS')||','
		||TRI1.msisdn||','||TRI2.msisdn||','
        ||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
        ||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
        ||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
        ||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
        ||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
        ||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
        ||STR.INFO1||','||STR.INFO2||','||STR.INFO3||','||STR.INFO4||','||STR.INFO5||','
        FROM TRANSFER_ITEMS TRI1, TRANSFER_ITEMS TRI2, SUBSCRIBER_TRANSFERS STR, KEY_VALUES KV1, KEY_VALUES KV2, KEY_VALUES KV
        WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
        AND TRI2.sno = 2 AND STR.transfer_date = p_date
        AND KV1.KEY(+) = TRI1.transfer_status AND KV2.KEY(+) = TRI2.transfer_status AND KV1.TYPE(+) = 'P2P_STATUS'
        AND KV2.TYPE(+) = 'P2P_STATUS' AND KV.KEY(+) = STR.transfer_status AND KV.TYPE(+) = 'P2P_STATUS'  ;

BEGIN
               DBMS_OUTPUT.PUT_LINE('Start P2P DWH PROC');
            v_srno := 0;
            v_data    := NULL;

            DELETE   TEMP_P2P_DWH_MASTER;
            DELETE    TEMP_P2P_DWH_TRANS;
            COMMIT;

           OPEN P2P_MASTER;
           LOOP
                        FETCH P2P_MASTER INTO v_data;
                        EXIT WHEN P2P_MASTER%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_MASTER;

            p_masterCnt := v_srno;
            DBMS_OUTPUT.PUT_LINE('p_masterCnt = '||p_masterCnt);
            v_srno := 0;
            v_data    := NULL;

           OPEN P2P_TRANS;
           LOOP
                        FETCH P2P_TRANS INTO v_data;
                        EXIT WHEN P2P_TRANS%NOTFOUND;
                                 v_srno := v_srno+1;
                                INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
                                VALUES (v_srno, v_data);

                                IF (MOD(v_srno , 10000) = 0)
                                THEN COMMIT;
                                END IF;

            END LOOP;
            CLOSE P2P_TRANS;

            p_transCnt :=v_srno;
            DBMS_OUTPUT.PUT_LINE('p_transCnt = '||p_transCnt);

        COMMIT;
        DBMS_OUTPUT.PUT_LINE('P2P DWH PROC Completed');
        p_message:='SUCCESS';

        EXCEPTION
                 WHEN SQLException THEN
                                   p_message:='Not able to migrate data, SQL Exception occoured';
                                 RAISE EXITEXCEPTION;
                  WHEN OTHERS THEN
                                   p_message:='Not able to migrate data, Exception occoured';
                              RAISE  EXITEXCEPTION;


END;
-----Procedure P2PDWHTEMPPRC - Tarun/Tejeshvi--------------


ALTER TABLE
	SOS_SETTLEMENT_FAIL_RECCORDS DROP
		CONSTRAINT PK_SOS_SETTLE_FAIL_REC;
COMMIT;


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
ALTER TABLE TRANSFER_RULES ADD CELL_GROUP_ID VARCHAR2(10) ;

--Alter Query For Remarks Column in Voms Batches
ALTER TABLE voms_batches ADD ( first_approver_remarks VARCHAR2(50),second_approver_remarks VARCHAR2(50),
third_approver_remarks VARCHAR2(50),first_approved_by VARCHAR2(20),second_approved_by VARCHAR2(20),
third_approved_by VARCHAR2(20),first_approved_on DATE,second_approved_on DATE,third_approved_on DATE);
Commit;


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values
   ('P2P_PRE_SERVCLASS_AS_POST', 'P2P module Pre to pre and pre - post support', 'SYSTEMPRF', 'BOOLEAN', 'false', 
    10, 10, 50, 'P2P module Pre to pre and pre - post support', 'N', 
    'N', 'P2P', 'P2P module Pre to pre and pre - post support', SYSDATE, 'ADMIN', 
    SYSDATE, 'ADMIN', 'true,false', 'Y');
		
COMMIT;

--DWH changes for INFO tags and DUAL Commission file
CREATE OR REPLACE PROCEDURE RP2PDWHTEMPPRC
(
           P_DATE                 IN DATE,
           P_MASTERCNT            OUT NUMBER,
           P_CHTRANSCNT           OUT    NUMBER,
           P_C2STRANSCNT          OUT    NUMBER,
           P_MESSAGE              OUT VARCHAR2
)
IS

SQLEXCEPTION EXCEPTION;
EXITEXCEPTION EXCEPTION;

BEGIN
        DBMS_OUTPUT.PUT_LINE('START RP2P DWH PROC');

        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_MASTER';
        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_CHTRANS';
        EXECUTE IMMEDIATE 'TRUNCATE TABLE TEMP_RP2P_DWH_C2STRANS';


    INSERT INTO TEMP_RP2P_DWH_MASTER ( SRNO, DATA )
    SELECT ROWNUM,(U.USER_ID||','||PARENT_ID||','||OWNER_ID||','||USER_TYPE||','||EXTERNAL_CODE||','||MSISDN
    ||','||REPLACE(L.LOOKUP_NAME,',',' ')||','||REPLACE(LOGIN_ID,',',' ')||','||U.CATEGORY_CODE||','||CAT.CATEGORY_NAME||','||
    UG.GRPH_DOMAIN_CODE||','||REPLACE(GD.GRPH_DOMAIN_NAME,',',' ')||','||
    REPLACE(USER_NAME,',',' ')||','||REPLACE(CITY,',',' ')||','||REPLACE(STATE,',',' ')||','||REPLACE(COUNTRY,',',' ')||',' ||',' ) DATA FROM USERS U, 

CATEGORIES CAT,USER_GEOGRAPHIES UG,GEOGRAPHICAL_DOMAINS GD,LOOKUPS L, LOOKUP_TYPES LT
    WHERE U.USER_ID=UG.USER_ID AND U.CATEGORY_CODE=CAT.CATEGORY_CODE AND U.STATUS<>'C'
    AND UG.GRPH_DOMAIN_CODE=GD.GRPH_DOMAIN_CODE AND L.LOOKUP_CODE=U.STATUS
    AND LT.LOOKUP_TYPE='URTYP' AND LT.LOOKUP_TYPE=L.LOOKUP_TYPE 
    AND TRUNC(U.CREATED_ON)<=P_DATE
    AND USER_TYPE='CHANNEL';
    COMMIT;
    SELECT MAX(SRNO) INTO P_MASTERCNT FROM TEMP_RP2P_DWH_MASTER;



    INSERT INTO TEMP_RP2P_DWH_CHTRANS ( SRNO, DATA )
    SELECT ROWNUM,DATA FROM (SELECT (CT.TRANSFER_ID||','||REQUEST_GATEWAY_TYPE||','||TO_CHAR

(CT.TRANSFER_DATE,'DD/MM/YYYY')
    ||','||TO_CHAR(CT.CREATED_ON,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE
    ||','||CT.TRANSFER_TYPE||','||CT.TRANSFER_SUB_TYPE||','||CT.TRANSFER_CATEGORY
    ||','||CT.TYPE||','||CT.FROM_USER_ID||','||CT.TO_USER_ID||','||CT.MSISDN||','||CT.TO_MSISDN
    ||','||CT.SENDER_CATEGORY_CODE||','||CT.RECEIVER_CATEGORY_CODE||','||
    CTI.SENDER_DEBIT_QUANTITY||','||CTI.RECEIVER_CREDIT_QUANTITY||','||CT.TRANSFER_MRP
    ||','||CTI.MRP||','||CTI.PAYABLE_AMOUNT||','||CTI.NET_PAYABLE_AMOUNT||','||0
    ||','||CTI.TAX1_VALUE||','||CTI.TAX2_VALUE||','||CTI.TAX3_VALUE||','||CTI.COMMISSION_VALUE
    ||','||','||','||CT.EXT_TXN_NO||','||TO_CHAR(CT.EXT_TXN_DATE,'DD/MM/YYYY')||','||','||CTI.PRODUCT_CODE||','||','
    || DECODE(CT.STATUS ,'CLOSE','200','240') ||','||','||','||','||','||','||','||','||','||','||CT.CELL_ID||','
||CTI.SENDER_POST_STOCK||','||CTI.SENDER_PREVIOUS_STOCK||','||CTI.RECEIVER_POST_STOCK||','||CTI.RECEIVER_PREVIOUS_STOCK||','||','||','||CT.SOS_STATUS||','||CT.SOS_SETTLEMENT_DATE||','||CTI.OTF_TYPE||','||CTI.OTF_RATE||','||CTI.OTF_AMOUNT||','||CT.INFO1||','||CT.INFO2||','||CT.INFO3||','||CT.INFO4||','||CT.INFO5||','||CT.DUAL_COMM_TYPE||',') DATA     

            FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI
    WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID(+)
    AND CT.STATUS IN('CLOSE','CNCL') 
    AND TRUNC(CT.CLOSE_DATE)=P_DATE
    ORDER BY CT.MODIFIED_ON,CT.TYPE);
    COMMIT;
    SELECT MAX(SRNO) INTO P_CHTRANSCNT FROM TEMP_RP2P_DWH_CHTRANS;



    INSERT INTO TEMP_RP2P_DWH_C2STRANS ( SRNO, DATA,TRANSFER_STATUS)
    SELECT ROWNUM,DATA,TRANSFER_STATUS FROM (SELECT (CT.TRANSFER_ID||','||REQUEST_GATEWAY_TYPE||','||TO_CHAR

(CT.TRANSFER_DATE,'DD/MM/YYYY')
    ||','||TO_CHAR(CT.TRANSFER_DATE_TIME,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE||','||CT.SERVICE_TYPE||','||','||   'SALE'||','||'C2S'||','||

CT.SENDER_ID||','||','||CT.SENDER_MSISDN||','||CT.RECEIVER_MSISDN||','||
    CT.SENDER_CATEGORY||','||','||CT.SENDER_TRANSFER_VALUE||','||CT.RECEIVER_TRANSFER_VALUE||','||
    CT.TRANSFER_VALUE||','||CT.QUANTITY||','||','||','|| CT.RECEIVER_ACCESS_FEE||','||
    CT.RECEIVER_TAX1_VALUE||','||CT.RECEIVER_TAX2_VALUE||','||0||','||','||CT.DIFFERENTIAL_APPLICABLE||','||
    CT.DIFFERENTIAL_GIVEN||','||','||','||','||CT.PRODUCT_CODE||','||CT.CREDIT_BACK_STATUS||','||CT.TRANSFER_STATUS
    ||','||CT.RECEIVER_BONUS_VALUE||','||CT.RECEIVER_VALIDITY||','||CT.RECEIVER_BONUS_VALIDITY||','
    ||CT.SERVICE_CLASS_CODE||','||CT.INTERFACE_ID||','||CT.CARD_GROUP_CODE
    ||','||REPLACE(KV.VALUE,',',' ')||','||CT.SERIAL_NUMBER||','||CT.INTERFACE_REFERENCE_ID||','||CT.CELL_ID||','||CT.SENDER_POST_BALANCE||','||CT.SENDER_PREVIOUS_BALANCE||','||CT.RECEIVER_POST_BALANCE||','||CT.RECEIVER_PREVIOUS_BALANCE||','||CT.REVERSAL_ID||','||CT.SUB_SERVICE ||','||','||','||','||CT.INFO1 ||','||CT.INFO2 ||','||CT.INFO3 ||','||CT.INFO4 ||','||CT.INFO5 ||',') DATA,CT.TRANSFER_STATUS TRANSFER_STATUS
    FROM C2S_TRANSFERS CT, KEY_VALUES KV ,Service_Type_Selector_Mapping STSM WHERE 
    CT.TRANSFER_DATE=P_DATE  AND 
    stsm.SELECTOR_CODE=CT.SUB_SERVICE AND stsm.SERVICE_TYPE=CT.SERVICE_TYPE 
    AND KV.KEY(+)=CT.ERROR_CODE AND KV.TYPE(+)='C2S_ERR_CD' ORDER BY CT.TRANSFER_DATE_TIME);
    COMMIT;

    SELECT MAX(SRNO) INTO P_C2STRANSCNT FROM TEMP_RP2P_DWH_C2STRANS;


    DBMS_OUTPUT.PUT_LINE('RP2P DWH PROC COMPLETED');
    P_MESSAGE:='SUCCESS';

    EXCEPTION
                 WHEN SQLEXCEPTION THEN
            P_MESSAGE:='NOT ABLE TO MIGRATE DATA, SQL EXCEPTION OCCOURED';
            RAISE EXITEXCEPTION;
                 WHEN OTHERS THEN
                        P_MESSAGE:='NOT ABLE TO MIGRATE DATA, EXCEPTION OCCOURED';
                        RAISE  EXITEXCEPTION;

END;	
COMMIT;

	
ALTER TABLE
	SOS_SETTLEMENT_FAIL_RECCORDS DROP
		CONSTRAINT PK_SOS_SETTLE_FAIL_REC;
COMMIT;

--Release 7.4 Voucher Order Request  Changes Starts----
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

COMMIT;

---- Release 7.4 : Download Vouchers for Voucher Order Requests starts
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'DOWNVOMS', 'Voms voucher download', 'Voucher Download', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'DOWNVOMS', '1');
COMMIT;
---- Release 7.4 : Download Vouchers for Voucher Order Requests ends

/* Channel Transfer Items - Tejeshvi*/
CREATE TABLE CHANNEL_VOUCHER_ITEMS (
	TRANSFER_ID 		VARCHAR2(20) 	NOT NULL,
	TRANSFER_DATE 		DATE 			NOT NULL,
	VOUCHER_TYPE 		VARCHAR2(15) 	NOT NULL,
	PRODUCT_ID 			VARCHAR2(15),
	MRP 				NUMBER(10,0) 	NOT NULL,
	REQUESTED_QUANTITY	NUMBER(20,0) 	NOT NULL,
	FROM_SERIAL_NO 		VARCHAR2(16),
	TO_SERIAL_NO 		VARCHAR2(16)
);

COMMIT;

/* On demand voucher - Test type - Tejeshvi*/
ALTER TABLE VOMS_TYPES MODIFY "TYPE" VARCHAR2(2) DEFAULT 'E';

INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
VALUES('test_phy', 'Test', 'RC', 'Y', TIMESTAMP '2018-09-14 16:59:49.000000', 'SU0001', TIMESTAMP '2018-09-14 16:59:49.000000', 'SU0001', 'PT');
INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
VALUES('test_elect', 'Test', 'EVD', 'Y', TIMESTAMP '2018-09-14 16:59:49.000000', 'SU0001', TIMESTAMP '2018-09-14 16:59:49.000000', 'SU0001', 'ET');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('ET', 'Electronic Test', 'VTYPE', 'Y', TIMESTAMP '2005-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-06 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PT', 'Physical Test', 'VTYPE', 'Y', TIMESTAMP '2005-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-11-06 00:00:00.000000', 'ADMIN');



INSERT INTO VOMS_VTYPE_SERVICE_MAPPING(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_elect', 'EVD', '1', 'Y', 406);
INSERT INTO VOMS_VTYPE_SERVICE_MAPPING(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_phy', 'EVD', '1', 'Y', 390);
INSERT INTO VOMS_VTYPE_SERVICE_MAPPING(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_phy', 'RC', '3', 'Y', 391);
INSERT INTO VOMS_VTYPE_SERVICE_MAPPING(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_phy', 'RC', '123', 'Y', 392);
INSERT INTO VOMS_VTYPE_SERVICE_MAPPING(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('test_phy', 'RC', '12', 'Y', 393);



/* On demand voucher - Test type - Tejeshvi*/

/* Payment Gateway - Tejeshvi*/
CREATE TABLE CHANNEL_TRANSFER_PAYMENTS (
	TRANSFER_ID 		VARCHAR2(15) 	NOT NULL,
	PAYMENT_ID 			VARCHAR2(15) 	NOT NULL,
	TRANSFER_DATE 		DATE 			NOT NULL,
	TRANSFER_DATE_TIME	DATE			NOT NULL,
	PAYMENT_STATUS 		VARCHAR2(10) 	NOT NULL,
	PAYMENT_AMOUNT		NUMBER(20)	 	NOT NULL
);
/* Payment Gateway - Tejeshvi*/

ALTER TABLE CHANNEL_TRANSFERS ADD PMT_INST_STATUS VARCHAR2(15) DEFAULT 'NA' NOT NULL ;

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

COMMIT;

ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_BY VARCHAR2(15);
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_DATE DATE;
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_FLAG VARCHAR2 (1);     
ALTER TABLE CHANNEL_TRANSFERS ADD RECONCILIATION_REMARK VARCHAR2 (50);
COMMIT;


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
   ('SUB', 'Subscriber', 'P2PPROMO', 'Y', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');

   
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('CEL', 'Cell Group', 'P2PPROMO', 'Y', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
    'ADMIN', TO_DATE('01/04/2013 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN');
	



COMMIT;

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
	
	COMMIT;

	
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
COMMIT;


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
COMMIT;


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


COMMIT;

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

COMMIT;

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
COMMIT;

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
   INSERT INTO NETWORK_INTERFACE_MODULES
(MODULE, NETWORK_CODE, METHOD_TYPE, COMM_TYPE, IP, PORT, CLASS_NAME)
VALUES('C2S', 'NG', 'PG', 'SINGLE_JVM', NULL, NULL, 'com.btsl.pretups.inter.module.InterfaceModule');

INSERT INTO INTERFACES
(INTERFACE_ID, EXTERNAL_ID, INTERFACE_DESCRIPTION, INTERFACE_TYPE_ID, STATUS, CLOUSER_DATE, MESSAGE_LANGUAGE1, MESSAGE_LANGUAGE2, CONCURRENT_CONNECTION, SINGLE_STATE_TRANSACTION, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, STATUS_TYPE, VAL_EXPIRY_TIME, TOPUP_EXPIRY_TIME, NUMBER_OF_NODES)
VALUES('INTID00075', 'PG', 'paymentgateway', 'PG01', 'Y', TIMESTAMP '2018-10-17 13:40:41.000000', 'test1', 'test1', 10, 'Y', TIMESTAMP '2018-10-17 13:40:41.000000', 'SU0001', TIMESTAMP '2018-10-17 13:40:41.000000', 'SU0001', 'M', 100000, 100000, '1');

ALTER TABLE USERS MODIFY PAYMENT_TYPE VARCHAR2(25) ; 

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CHNLUSR_VOUCHER_CATGRY_ALLWD', 'Chnl User Voucher Allwed Categories', 'CATPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Chnl User Voucher Allwed Categories', 'Y', 'Y', 'C2S', 'Chnl User Voucher Allwed Categories', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'false,true', 'N');
	
COMMIT;
   

Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('P2P_PROMO_TRF_APP', 'p2p Promotional trf applicable', 'NETWORKPRF', 'BOOLEAN', 'false', 
    NULL, NULL, 50, 'p2p Promotional transfer rule applicable', 'Y', 
    'Y', 'C2S', 'p2p Promotional transfer rule applicable', TO_DATE('10/16/2018 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('10/18/2018 15:03:14', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', NULL, 'Y');

	

Insert into NETWORK_PREFERENCES
   (NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY)
 Values
   ('NG', 'P2P_PROMO_TRF_APP', 'true', TO_DATE('10/13/2018 13:41:21', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000000002', 
    TO_DATE('10/13/2018 13:41:55', 'MM/DD/YYYY HH24:MI:SS'), 'NGLA0000000002');
ALTER TABLE CHANNEL_TRANSFERS MODIFY PMT_INST_NO VARCHAR2(50) ;

ALTER TABLE CHANNEL_VOUCHER_ITEMS ADD S_NO NUMBER(3) NOT NULL;
 	
COMMIT;	

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


COMMIT;

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


COMMIT;


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
COMMIT;

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
VALUES('DIST', 'NG', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', 'CATPRF');



	
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
    'Y', 'C2S', 'Check whetherLast C2C Enq msg required for receiver', SYSDATE, 'ADMIN', 
    SYSDATE, 'ADMIN', NULL, 'Y');
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
COMMIT;


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
COMMIT;

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
COMMIT;

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
VALUES('DIST', 'NG', 'CHNLUSR_VOUCHER_CATGRY_ALLWD', 'true', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', TIMESTAMP '2018-10-30 18:58:47.000000', 'NGLA0000003720', 'CATPRF');
COMMIT

ALTER TABLE COMMISSION_PROFILE_PRODUCTS
ADD payment_mode VARCHAR2(10) DEFAULT 'ALL';

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

COMMIT;



ALTER TABLE COMMISSION_PROFILE_PRODUCTS
   MODIFY PAYMENT_MODE  VARCHAR(10) NOT NULL;

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

COMMIT;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('O2CAMB_MINUTES_DELAY', 'Delay in ambiguous transaction for O2C Recon', 'SYSTEMPRF', 'INT', '-5', -100, 60, 50, 'Delay in ambiguous transaction for O2C Recon', 'N', 'Y', 'C2S', 'Delay in ambiguous transaction for O2C Recon', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');

UPDATE SYSTEM_PREFERENCES
SET default_value = 'yyyy/MM/dd HH24:mi:ss' where preference_code = 'DATE_TIME_FORMAT'

ALTER TABLE VOMS_VOUCHERS
ADD (VOUCHER_TYPE  VARCHAR2(12));

ALTER TABLE VOMS_VOUCHERS
ADD (CONSUMED_GATEWAY_TYPE  VARCHAR2(12));

ALTER TABLE VOMS_VOUCHERS
ADD (CONSUMED_GATEWAY_CODE  VARCHAR2(12));


CREATE TABLE VOMS_VOUCHER_DAILY_SUMMARY
(
  SUMMARY_DATE             DATE,
  PRODUCT_ID               VARCHAR2(15 BYTE),
  VOUCHER_TYPE             VARCHAR2(15 BYTE),
  DENOMINATION             NUMBER(16)           DEFAULT 0,
  PRODUCTION_NETWORK_CODE  VARCHAR2(2 BYTE),
  USER_NETWORK_CODE        VARCHAR2(2 BYTE),
  TOTAL_GENERATED          NUMBER(16)           DEFAULT 0,
  TOTAL_ENABLED            NUMBER(16)           DEFAULT 0,
  TOTAL_STOLEN             NUMBER(16)           DEFAULT 0,
  TOTAL_ON_HOLD            NUMBER(16)           DEFAULT 0,
  TOTAL_DAMAGED            NUMBER(16)           DEFAULT 0,
  OTHER_STATUS             NUMBER(16)           DEFAULT 0,
  TOTAL_CONSUMED           NUMBER(16)           DEFAULT 0,
  TOTAL_WAREHOUSE          NUMBER(16)           DEFAULT 0,
  TOTAL_PRINTING           NUMBER(16)           DEFAULT 0,
  TOTAL_SUSPENDED          NUMBER(16)           DEFAULT 0,
  TOTAL_EXPIRED            NUMBER(16)  			DEFAULT 0
);


CREATE UNIQUE INDEX PK_VOMS_VOU_DAI_SUM ON VOMS_VOUCHER_DAILY_SUMMARY
(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE, USER_NETWORK_CODE, VOUCHER_TYPE);


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
COMMIT;

/* Adding column for Upload Signed request form starts*/
ALTER TABLE VOMS_BATCHES ADD SIGNED_DOC BLOB ;
COMMENT ON COLUMN VOMS_BATCHES.REQUEST_FORM IS 'Signed Copy of approvals' ;

ALTER TABLE VOMS_BATCHES ADD SIGNED_DOC_TYPE VARCHAR2(100) ;
COMMENT ON COLUMN VOMS_BATCHES.SIGNED_DOC_TYPE IS 'Document type of Signed Copy' ;

ALTER TABLE VOMS_BATCHES ADD SIGNED_DOC_FILE_PATH VARCHAR2(500) ;
COMMENT ON COLUMN VOMS_BATCHES.SIGNED_DOC_FILE_PATH IS 'File path at server of Signed Copy' ;
COMMIT;
/* Adding column for Upload Signed request form ends*/


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

commit;	



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
COMMIT;



Insert into MODULES
   (MODULE_CODE, MODULE_NAME, SEQUENCE_NO, APPLICATION_ID)
 Values
   ('ETOPUPRPT', 'ETopUp Reports', 23, '1');
COMMIT;

SET DEFINE OFF;
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
COMMIT;


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

COMMIT;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('EMAIL_DEFAULT_LOCALE', 'Default locale for Email', 'SYSTEMPRF', 'STRING', 'en_US', NULL, NULL, 50, 'Default locale for Email', 'N', 'Y', 'C2S', 'Default locale for Email', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');
COMMIT;

update SYSTEM_PREFERENCES set MODULE= 'P2P' where PREFERENCE_CODE= 'P2P_PROMO_TRF_APP';
commit;

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
commit;

ALTER TABLE CHANNEL_TRANSFERS ADD (INFO6  VARCHAR2(100 BYTE),INFO7  VARCHAR2(100 BYTE),	INFO8  VARCHAR2(100 BYTE),INFO9  VARCHAR2(100 BYTE),INFO10  VARCHAR2(100 BYTE));
commit;

--##########################################################################################################
--##
--##      PreTUPS_v7.5.0 DB Script
--##
--##########################################################################################################

ALTER TABLE COMMISSION_PROFILE_PRODUCTS ADD TRANSACTION_TYPE VARCHAR2(10) DEFAULT 'ALL';

ALTER TABLE COMMISSION_PROFILE_PRODUCTS
DROP CONSTRAINT UK_COMMISSION_PROFILE_PRODUCTS;

ALTER TABLE COMMISSION_PROFILE_PRODUCTS ADD (
    CONSTRAINT UK_COMMISSION_PROFILE_PRODUCTS
UNIQUE (COMM_PROFILE_SET_ID, PRODUCT_CODE, PAYMENT_MODE, COMM_PROFILE_SET_VERSION, TRANSACTION_TYPE));

CREATE TABLE COMMISSION_PROFILE_OTF
(
  COMM_PROFILE_OTF_ID    VARCHAR2(10 BYTE),
  COMM_PROFILE_SET_ID        VARCHAR2(10 BYTE)  NOT NULL,
  COMM_PROFILE_SET_VERSION   VARCHAR2(5 BYTE)   NOT NULL,
  PRODUCT_CODE               VARCHAR2(20 BYTE)  NOT NULL,
  OTF_APPLICABLE_FROM       DATE,
  OTF_APPLICABLE_TO         DATE,
  OTF_TIME_SLAB             VARCHAR2(40 BYTE)
);

CREATE UNIQUE INDEX PK_COMMISSION_PROFILE_OTF ON COMMISSION_PROFILE_OTF
(COMM_PROFILE_OTF_ID);

CREATE UNIQUE INDEX UK_COMMISSION_PROFILE_OTF ON COMMISSION_PROFILE_OTF
(COMM_PROFILE_OTF_ID, PRODUCT_CODE, COMM_PROFILE_SET_VERSION);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('ALL', 'COMM_OTFID', 'ALL', 1, TIMESTAMP '2019-01-19 13:27:20.000000', 'NA', 'OTF ID');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TRANSACTION_TYPE', 'Transaction Type', 'SYSTEMPRF', 'BOOLEAN', 'True', NULL, NULL, 50, 'Flag to specify transaction type', 'Y', 'Y', 'C2S', 'Flag to specify transaction type', TIMESTAMP '2637-01-24 23:52:13.000000', 'ADMIN', TIMESTAMP '2637-01-24 23:52:13.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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

Commit;

--##########################################################################################################
--##
--##      PreTUPS_v7.6.0 DB Script
--##
--##########################################################################################################

SET DEFINE OFF;
Insert into LOOKUPS
   (LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, 
    CREATED_BY, MODIFIED_ON, MODIFIED_BY)
 Values
   ('SYSTEM', 'System', 'CNTRL', 'N', sysdate, 
    'ADMIN', sysdate, 'ADMIN');
COMMIT;


Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DOMAINCODE_FOR_SOS_YABX', 'SOS domain allowed via YABX ', 'NETWORKPRF', 'STRING', 'YABX', 
    NULL, NULL, 50, 'SOS domain allowed via YABX', 'N', 
    'N', 'C2S', 'SOS domain allowed via YABX', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'YABX', 'Y');
COMMIT;

SET DEFINE OFF;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('HANDLER_CLASS_FOR_YABX', 'YABX Settlement Handler Class', 'SYSTEMPRF', 'STRING', 'com.client.pretups.sos.requesthandler.SOSSettlementYABXhandler', 
    NULL, NULL, 10, 'YABX Settlement Handler Class', 'N', 
    'N', 'C2S', NULL, sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
COMMIT;
SET DEFINE OFF;
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
COMMIT;
SET DEFINE OFF;
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
COMMIT;



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMSPIN_EN_DE_CRYPTION_TYPE', 'EN DE CRYPTION for PIN PASS For VMS', 'SYSTEMPRF', 'STRING', 'AES', NULL, NULL, 50, 'use this preference only for EN DE CRYPTION TYPE for PIN PASS for VMS, not for DB. values can be SHA,DES or AES', 'N', 'Y', 'C2S', 'EN DE CRYPTION TYPE used for PIN PASS for VMS values can be SHA,DES or AES', TIMESTAMP '2007-07-25 11:00:00.000000', 'ADMIN', TIMESTAMP '2007-07-25 11:00:00.000000', 'ADMIN', 'NULL', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('IPV6_ENABLED', 'To Enable/Disable IPV6', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'To Enable/Disable IPV6', 'N', 'Y', 'C2S', 'To Enable/Disable IPV6', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');

COMMIT;



SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_SUBJECT', 'Low Network Stock Alert', 'NG', 'Low Network Stock Alert', 
    'Low Network Stock Alert', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_HEADER', 'Low Network Stock Notification', 'NG', 'Low Network Stock Notification', 
    'Low Network Stock Notification', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_FOOTER', 'Generated By System', 'NG', 'Generated By System', 
    'Generated By System', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_BALANCE_ALERT_NOTIFICATION_SUBJECT', 'Low balance alert', 'NG', 'Low balance alert', 
    'Low balance alert', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_BALANCE_ALERT_NOTIFICATION_CONTENT', 'Balance of a channel user is below alerting balance', 'NG', 'Balance of a channel user is below alerting balance', 
    'Balance of a channel user is below alerting balance', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_MSISDN', 'Channel user Msisdn', 'NG', 'Channel user Msisdn', 
    'Channel user Msisdn', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_Name', 'Channel user name:', 'NG', 'Channel user name:', 
    'Channel user name:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_PRODUCT', 'Product Code:', 'NG', 'Product Code:', 
    'Product Code:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_Balance', 'Channel user balance:', 'NG', 'Channel user balance:', 
    'Channel user balance:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

--##########################################################################################################
--##
--##      PreTUPS_v7.7.0 DB Script
--##
--##########################################################################################################

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_ENABLE', 'National Voucher Enable', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'National Voucher Enable For Super Admin', 'N', 'Y', 'C2S', 'National Voucher Enable', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_NETWORK_CODE', 'National Voucher Network Code', 'SYSTEMPRF', 'STRING', 'XX', NULL, NULL, 50, 'National Voucher Network Code For Super Admin', 'Y', 'Y', 'C2S', 'National Voucher Network Code For Super Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-06-06 15:03:14.000000', 'SU0001', NULL, 'Y');


ALTER TABLE VOMS_CATEGORIES
  Add (NETWORK_CODE varchar2(2)) ;
  
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
  Add (NETWORK_CODE varchar2(2)) ;


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');

ALTER TABLE VOMS_PRODUCTS
  Add (NETWORK_CODE varchar2(2)) ;


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');


SET DEFINE OFF;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_SUBJECT', 'Low Network Stock Alert', 'NG', 'Low Network Stock Alert', 
    'Low Network Stock Alert', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_HEADER', 'Low Network Stock Notification', 'NG', 'Low Network Stock Notification', 
    'Low Network Stock Notification', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_NETWORK_STOCK_NOTIFICATION_FOOTER', 'Generated By System', 'NG', 'Generated By System', 
    'Generated By System', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_BALANCE_ALERT_NOTIFICATION_SUBJECT', 'Low balance alert', 'NG', 'Low balance alert', 
    'Low balance alert', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'LOW_BALANCE_ALERT_NOTIFICATION_CONTENT', 'Balance of a channel user is below alerting balance', 'NG', 'Balance of a channel user is below alerting balance', 
    'Balance of a channel user is below alerting balance', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_MSISDN', 'Channel user Msisdn', 'NG', 'Channel user Msisdn', 
    'Channel user Msisdn', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_Name', 'Channel user name:', 'NG', 'Channel user name:', 
    'Channel user name:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_PRODUCT', 'Product Code:', 'NG', 'Product Code:', 
    'Product Code:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', 'CHANNELUSER_Balance', 'Channel user balance:', 'NG', 'Channel user balance:', 
    'Channel user balance:', NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;

INSERT INTO system_preferences
(preference_code, name, "TYPE", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ONLINE_BATCH_EXP_DATE_LIMIT', 'Online Batch Expiry Date Limit', 'SYSTEMPRF', 'INT', '1000', 0, 1000, 50, 'Vouchers expiry date processed for batch online', 'Y', 'Y', 'C2S', 'Vouchers expiry date processed for batch online',TIMESTAMP '2005-06-21 00:00:00.000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');



INSERT INTO system_preferences
(preference_code, name, "TYPE", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_VOUCHER_EXPIRY_EXTN_LIMIT', 'Max limit for vouchers to extend expiry limit', 'SYSTEMPRF', 'INT', '10000', 0, 10000, 50, 'Maximum vouchers to be processed for voucher expiry date', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher expiry date',TIMESTAMP '2005-06-21 00:00:00.000', 'ADMIN',TIMESTAMP '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');


INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2019', 'VMPNEXPEXT', 'ALL', 0,TIMESTAMP '2006-06-03 00:00:00.000', 'NA', NULL);

	INSERT INTO service_type (service_type, module, type, message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, name, external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
	VALUES('VMSPINEXT', 'C2S', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherExpiryDateExtensionController', 'Voucher Expiry Date Extension API', 'Voucher Expiry Date Extension API', 'Y',TIMESTAMP '2017-11-06 06:18:46.000', 'ADMIN', TIMESTAMP '2017-11-06 06:18:46.000', 'ADMIN', 'Voucher Expiry Date Extension API', 'Y', 'N', 'Y', 11, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, name, status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'REST', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y',TIMESTAMP '2019-05-03 16:39:59.613', 'SU0001',TIMESTAMP '2019-05-03 16:39:59.613', 'SU0001', 'SVK4100242', NULL, '');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, name, status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'EXTGW', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y', TIMESTAMP'2019-05-03 16:39:59.613', 'SU0001', TIMESTAMP'2019-05-03 16:39:59.613', 'SU0001', 'SVK4100142', NULL, '');


CREATE TABLE voms_pin_exp_ext
(
    batch_no varchar2(15)  NOT NULL,
    voucher_type varchar2(15) ,
    total_vouchers number(16,0),
    from_serial_no varchar2(16) ,
    to_serial_no varchar2(16) ,
    total_failure number(16,0),
    total_success number(16,0),
    network_code varchar2(2),
	status varchar2(2),
    created_on DATE,
    created_by varchar2(20) ,
    modified_on DATE,
    modified_by varchar2(20) ,
    remarks varchar2(50) ,
    expiry_date DATE,
    CONSTRAINT pk_voms_pin_exp_ext PRIMARY KEY (batch_no)
) 

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2019', 'VMPNEXPEXT', 'ALL', 0, TO_DATE('06/19/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'NA', NULL); 

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN', 'Voms Max Approval level for Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

	INSERT INTO SYSTEM_PREFERENCES
	(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
	VALUES('VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN', 'Voms Max Approval level for Super Network Adm', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN', 'Voms Max Approval level for Sub Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PROF_TALKTIME_MANDATORY', 'VOMS ADD PROFILE TALK TIME MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'talk-time will be mandatory if defined as true', 'N', 'N', 'C2S', 'false if talk time is not required , true if required ', TIMESTAMP '2019-05-20 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-05-20 00:00:00.000000', 'ADMIN', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PROF_VALIDITY_MANDATORY', 'VOMS ADD PROFILE VALIDITY MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'validity(in days) will be mandatory if defined as true', 'N', 'N', 'C2S', 'false if  validaity is not required , true if required ', TIMESTAMP '2019-05-20 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-05-20 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PROFILE_DEF_MINMAXQTY', 'VOMS PROFILE DEFAULT MINMAXQTY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'required for fields to be hidden in voucher profile', 'N', 'N', 'C2S', 'false if need to show max min reorder qty, true if not required', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PROFILE_MIN_REORDERQTY', 'VOMS PROFILE DEFAULT MINQTY', 'SYSTEMPRF', 'INT', '10', NULL, NULL, 50, 'default value is taken as min re order quantity', 'N', 'N', 'C2S', 'default value is taken as min re order quantity', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_PROFILE_MAX_REORDERQTY', 'VOMS PROFILE DEFAULT MANQTY', 'SYSTEMPRF', 'INT', '1000', NULL, NULL, 50, 'default value is taken as max re order quantity', 'N', 'N', 'C2S', 'default value is taken as max re order quantity', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2017-06-13 00:00:00.000000', 'ADMIN', NULL, 'Y');


ALTER TABLE VOMS_VOUCHERS ADD pre_expiry_date DATE;



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DOWNLD_BATCH_BY_BATCHID', 'batches download by batch id ', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Is calendar icon required on GUI', 'N', 'Y', 'C2S', 'Is calendar icon required on GUI', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'N');

ALTER TABLE VOMS_VOUCHERS ADD info1 VARCHAR2(50);

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
  USER_ID       VARCHAR2(15 BYTE)               NOT NULL,
  VOUCHER_SEGMENT  VARCHAR2(10 BYTE)            NOT NULL,
  STATUS        VARCHAR2(1 BYTE)                DEFAULT 'Y'                   NOT NULL,
  CONSTRAINT PK_USER_VOUCHER_SEGMENT PRIMARY KEY (USER_ID,VOUCHER_SEGMENT)
);

ALTER TABLE VOMS_PRODUCTS
  Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'LC';
  
ALTER TABLE VOMS_CATEGORIES
  Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'LC';
  
  ALTER TABLE VOMS_BATCHES
  Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'LC';
  
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_VOUCHERSEGMENT_ALLOWED', 'User voucher segment is allowed', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'User voucher type is allowed', 'Y', 'Y', 'C2S', 'User voucher segment is allowed', TIMESTAMP '2018-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NW_NATIONAL_PREFIX', 'NW_NATIONAL_PREFIX', 'SYSTEMPRF', 'STRING', '99', null, null, 50, 'Prefix for National Voucher', 'N', 'N', 'C2S', 'Prefix for National Voucher', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'N');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NW_CODE_NW_PREFIX_MAPPING', 'NW_CODE_NW_PREFIX_MAPPING', 'SYSTEMPRF', 'STRING', 'NG=11,PB=12', null, null, 250, 'Network code and network Prefix mapping', 'N', 'N', 'C2S', 'Values can be Comma saperated', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'N');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_NATIONAL_LOCAL_PREFIX_ENABLE', 'VOMS_NATIONAL_LOCAL_PREFIX_ENABLE', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'To enable/disable national/local prefix', 'N', 'N', 'C2S', 'To enable/disable national/local prefix', TIMESTAMP '2018-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-11 23:39:40.000000', 'SU0001', NULL, 'Y');


ALTER TABLE CHANNEL_VOUCHER_ITEMS
  Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'LC';
  
ALTER TABLE CHANNEL_VOUCHER_ITEMS
  Add NETWORK_CODE varchar2(2);
  
ALTER TABLE VOMS_VOUCHERS
  Add VOUCHER_SEGMENT varchar2(2) DEFAULT 'LC';

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
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_SERVICES', 'Services related to Voucher', 'SYSTEMPRF', 'STRING', 'VCN', 1, 1, 50, 'Enter Voucher services comma separated', 'N', 'N', 'C2S', 'VMS Services', TIMESTAMP '2007-07-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-07-25 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DFLTCARDGRP', 'Set Default cardgroup', 'DefaultCardGroup', 'configfiles/subscriber/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/setDefaultCardGroupSet', 'Y', 'Y', 'VMSDFLTCARDGRP');
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('LOADCARDGROUPSET', 'Card Group Set', 'CardGroupChangeStatus', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/loadCardGroupSetList', 'N', 'Y', NULL);
INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('SUSPENDCARDGROUP', 'Suspend Card Group Set', 'CardGroupChangeStatus', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/cardGroup/updateCardGroupSetStatus', 'N', 'Y', NULL);
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
  MODIFY MENU_NAME varchar2(35);


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
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_VOUCHER_GEN_LIMIT', 'Max limit for voucher generation', 'SYSTEMPRF', 'INT', '14', 0, 100000, 50, 'Maximum vouchers to be processed for voucher generation online', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher generation online', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOUCHER_GEN_EMAIL_NOTIFICATION', 'Voucher Generation Email Notification', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Email notification to the approvers and initiator should be sent or not', 'N', 'Y', 'C2S', 'Email notification to the approvers or initiator should be sent or not', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  INFO1 VARCHAR2(50);
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  VOUCHER_SEGMENT VARCHAR2(2);
   
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('DVD', 'C2S', 'PRE', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.DVDController', 'Digital Voucher Distribution', 'Digital Voucher Distribution', 'Y', TIMESTAMP '2007-01-01 00:00:00.000000', 'ADMIN', TIMESTAMP '2007-01-01 00:00:00.000000', 'ADMIN', 'Voucher Consumption', 'Y', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'WEB', '190', 'DVD', 'DVD_WEB', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK000055', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'SMSC', '190', 'DVD', 'DVD_SMSC', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', 'SVK0004247', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'USSD', '190', 'DVD', 'DVD_USSD', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004248', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'EXTGW', '190', 'DVD', 'DVD_EXTGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004249', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('DVD', 'MAPPGW', '190', 'DVD', 'DVD_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004349', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');

---- added by shishupal
INSERT INTO PRODUCT_SERVICE_TYPE_MAPPING
(PRODUCT_TYPE, SERVICE_TYPE, CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON, GIVE_ONLINE_DIFFERENTIAL, DIFFERENTIAL_APPLICABLE, SUB_SERVICE)
VALUES('PREPROD', 'DVD', 'ADMIN', TIMESTAMP '2006-05-30 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-05-30 00:00:00.000000', 'Y', 'Y', 1);
--added by yogesh dixit for my voucher enquiry for subscriber 
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('SUBSCRIBER_VOUCHER_PIN_REQUIRED', 'Voucher pin required to show for Subs enq', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Voucher pin required to show for subscriber Enq', 'N', 'Y', 'P2P', 'Voucher pin required to show for subs Enq', TIMESTAMP '2019-09-19 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-19 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('SELFVCRENQ', 'P2P', 'PRE', 'KEYWORD', 'com.btsl.pretups.p2p.subscriber.requesthandler.SubscriberVoucherInquiryController', 'Subscriber Voucher Inquiery', 'Subscriber Voucher Inquiery', 'Y', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', 'Subscriber Voucher Inquiery', 'N', 'Y', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_DVD_LIMIT', 'Max limit for DVD', 'SYSTEMPRF', 'INT', '10', 0, 5, 50, 'Maximum vouchers to be distributed', 'Y', 'Y', 'C2S', 'Maximum vouchers to be distributed', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 00:00:00.000000', 'SU0001', NULL, 'Y');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
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

 ALTER TABLE VOMS_VOUCHERS ADD  C2S_TRANSACTION_ID VARCHAR2(20);
 
 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_STATUS_CHANGE', 'VMS_D_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,S', NULL, NULL, 50, 'Possible status for Status Change for Digital Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Digital for Change ', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_STATUS_CHANGE', 'VMS_E_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,S', NULL, NULL, 50, 'Possible combination for Status Change for Electronics Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Electronics for Change', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_STATUS_CHANGE', 'VMS_P_STATUS_CHANG', 'SYSTEMPRF', 'STRING', 'EN,DA,ST,OH,WH,S', NULL, NULL, 50, 'Possible combination for Status Change for Physical Vouchers', 'N', 'N', 'C2S', 'Status Allowed for Physical for Change', TIMESTAMP '2005-06-21 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-02-08 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');


INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
VALUES('digital', 'digital des', 'VCN', 'Y', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', TIMESTAMP '2019-10-12 22:07:56.000000', 'SU0001', 'D');

INSERT INTO VOMS_VTYPE_SERVICE_MAPPING
(VOUCHER_TYPE, SERVICE_TYPE, SUB_SERVICE, STATUS, SERVICE_ID)
VALUES('digital', 'VCN', '1', 'Y', 409);


INSERT INTO VOMS_TYPES
(VOUCHER_TYPE, NAME, SERVICE_TYPE_MAPPING, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, "TYPE")
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
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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
    'Y', 'C2S', 'If this flag is true, it allows visibility of item code and secondary prefix code on Voucher Profile Screens', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'true,false', 'Y');
 
ALTER TABLE VOMS_PRODUCTS ADD  ITEM_CODE VARCHAR2(10);
 
ALTER TABLE VOMS_PRODUCTS ADD  SECONDARY_PREFIX_CODE VARCHAR2(10);

--##########################################################################################################
--##
--##      PreTUPS_v7.15.0 DB Script
--##
--##########################################################################################################

 ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD  C2S_TRANSACTION_ID VARCHAR2(20);
 
 INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_LIFECYCLE', 'Physical voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:PE:WH:EN:CU', NULL, NULL, 50, 'Physical voucher life cycle', 'N', 'Y', 'VMS', 'Physical voucher life cycle', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_LIFECYCLE', 'Digital voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:EN:CU', NULL, NULL, 50, 'Digital voucher life cycle', 'N', 'Y', 'VMS', 'Digital voucher life cycle', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_LIFECYCLE', 'Electronic voucher life cycle', 'SYSTEMPRF', 'STRING', 'GE:EN:CU', NULL, NULL, 50, 'Electronic voucher life cycle', 'N', 'Y', 'VMS', 'Electronic voucher life cycle', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_E_STATUS_CHANGE_MAP', 'Electronic voucher change status mapping', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Electronic voucher change status mapping', 'N', 'Y', 'VMS', 'Electronic voucher change status mapping', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_P_STATUS_CHANGE_MAP', 'Physical voucher change status mapping', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,PE:WH,PE:ST,WH:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Physical voucher change status mapping', 'N', 'Y', 'VMS', 'Electronic voucher change status mapping', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-06 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VMS_D_STATUS_CHANGE_MAP', 'VMS DIGITAL VOUCHER STATUS CHANGE MAP', 'SYSTEMPRF', 'STRING', 'OH:EN,OH:ST,OH:DA,OH:S,PA:EN,PA:S,S:EN,S:ST,S:OH,EN:OH,EN:ST,EN:DA,EN:S,GE:DA,GE:EN,GE:ST', NULL, NULL, 50, 'Map for digital vouchers changestaus', 'N', 'Y', 'C2S', 'Map for digital vouchers changestaus', TIMESTAMP '2005-07-13 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-13 00:00:00.000000', 'SUPERADMIN', NULL, 'Y');





--############################################Change Other /Generated Procedures ###################################################

create or replace TYPE stringArrayNew AS TABLE OF VARCHAR2 (1000);

create or replace PACKAGE                   "CHANGE_STATUS_PKG"
AS
  v_succFailFlag         VARCHAR2(200);
  v_maxErrorFlag         BOOLEAN;
  v_voucherNotFoundFlag  BOOLEAN;
  rcd_count              NUMBER;
  v_message              VARCHAR2(200);
  v_returnLogMessage     VARCHAR2(200);
  v_insertRowId          VARCHAR2(200);
  v_row_id               VARCHAR2(200);
  EXITEXCEPTION          EXCEPTION;
  SQLException           EXCEPTION;
  NOTINSERTEXCEPTION     EXCEPTION;
  fromStatus             VARCHAR2(200);
  toStatus               VARCHAR2(200);
  v_batchEnableStat      VARCHAR2(200);
  v_vouchStat            VARCHAR2(200);
  v_voucCurrStat         VARCHAR2(200);
  v_expDate              DATE;
  v_generationBatchNo    VARCHAR2(200);
  v_prodNetworkCode      VARCHAR2(200);
  v_userNetworkCode      VARCHAR2(200);
  v_productID            VARCHAR2(200);
  v_PreviousStatus       VARCHAR2(200);
  v_LastConsumedOn       DATE;
  v_LastRequestAttemptNo NUMBER;
  v_LastAttemptValue     VARCHAR2(200);
  v_processStatus VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE;
  v_batchGenStat             VARCHAR2(200);
  v_batchOnHoldStat          VARCHAR2(200);
  v_batchStolenStat          VARCHAR2(200);
  v_batchSoldStat            VARCHAR2(200);
  v_batchDamageStat          VARCHAR2(200);
  v_batchReconcileStat       VARCHAR2(200);
  v_batchPrintStat           VARCHAR2(200);
  v_wareHouseStat            VARCHAR2(200);
  v_preActiveStat            VARCHAR2(200);
  v_suspendStat              VARCHAR2(200);
  v_batchConStat             VARCHAR2(200);
  v_createdOn                DATE;
  v_modifiedBy               VARCHAR2(200);
  v_errorCount               NUMBER;
  v_serialStart              VARCHAR2(200);
  v_batchNo                  varchar2(200);
  v_batchType                VARCHAR2(200);
  v_processScreen            VARCHAR2(200);
  v_modifiedTime             DATE;
  v_enableCount              NUMBER;
  v_DamageStolenCount        NUMBER;
  v_DamageStolenAfterEnCount NUMBER;
  v_onHoldCount              NUMBER;
  v_counsumedCount           NUMBER;
  v_wareHouseCount           NUMBER;
  v_preActiveCount           NUMBER;
  v_suspendCount             NUMBER;
  v_referenceNo              VOMS_BATCHES.REFERENCE_NO%TYPE;
  v_RCAdminMaxdateallowed    NUMBER;
  v_EnableProcess VOMS_BATCHES.PROCESS%TYPE;
  v_ChangeProcess VOMS_BATCHES.PROCESS%TYPE;
  v_ReconcileProcess VOMS_BATCHES.PROCESS%TYPE;
  v_networkCode VOMS_BATCHES.NETWORK_CODE%TYPE;
  v_voucherNotFoundCount NUMBER;
  v_returnMessage        VARCHAR2(200);
  v_sqlErrorMessage      VARCHAR2(200);
  v_serialNoLength       NUMBER;
  v_seqId voms_batches.SEQUENCE_ID%type;
  v_prefString VARCHAR2(200);
  v_preference VARCHAR2(200);
  fhandleSuccess  utl_file.file_type;
  fhandleFail  utl_file.file_type;
  v_boolScreenOneOrTwoCheck BOOLEAN;
  
  
  
  v_bulkOutput  STRINGARRAYNEW :=STRINGARRAYNEW();
  v_bulkOutput_Counter  NUMBER:=1;
  v_lastModifieddate date;
  v_daysDifflastconCurrDate Number;
  v_modifieddate date;
  
  PROCEDURE p_changeVoucherStatus(
      p_batchNo               IN VOMS_BATCHES.BATCH_NO%TYPE,
      p_batchType             IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_fromSerialNo          IN VOMS_BATCHES.FROM_SERIAL_NO%TYPE,
      p_toSerialNo            IN VOMS_BATCHES.TO_SERIAL_NO%TYPE,
      p_batchEnableStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchGenStat          IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchOnHoldStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchStolenStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchSoldStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchDamageStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchReconcileStat    IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchPrintStat        IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_wareHouseStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_preActiveStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_suspendStat           IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_createdOn             IN VOMS_VOUCHERS.EXPIRY_DATE%TYPE,
      p_maxErrorAllowed       IN NUMBER,
      p_modifiedBy            IN VOMS_VOUCHER_AUDIT.MODIFIED_BY%TYPE,
      p_noOfVouchers          IN NUMBER,
      p_successProcessStatus  IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
      p_errorProcessStatus    IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
      p_batchConStat          IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_processScreen         IN NUMBER,
      p_modifiedTime          IN VOMS_BATCHES.MODIFIED_ON%TYPE,
      p_referenceNo           IN VOMS_BATCHES.REFERENCE_NO%TYPE,
      p_RCAdminMaxdateallowed IN NUMBER,
      p_prefString IN VARCHAR2,
      p_EnableProcess VOMS_BATCHES.PROCESS%TYPE,
      p_ChangeProcess VOMS_BATCHES.PROCESS%TYPE,
      p_ReconcileProcess VOMS_BATCHES.PROCESS%TYPE,
      p_networkCode VOMS_BATCHES.NETWORK_CODE%TYPE,
      p_seqID IN voms_batches.SEQUENCE_ID%type,
      p_returnMessage OUT VARCHAR2,
      p_returnLogMessage OUT VARCHAR2,
      p_sqlErrorMessage OUT VARCHAR2,
      p_bulkOutput  OUT STRINGARRAYNEW
      );
  
  PROCEDURE UPDATE_VOUCHER_ENABLE;
  PROCEDURE CHECK_CHANGE_VALID_PROC;
  PROCEDURE UPDATE_VOUCHERS;
  PROCEDURE INSERT_IN_SUMMARY_PROC;
  PROCEDURE INSERT_IN_AUDIT_PROC;
  PROCEDURE UPDATE_VOUCHER_ENABLE_OTHER;
  
END CHANGE_STATUS_PKG;




create or replace PACKAGE BODY "CHANGE_STATUS_PKG"
AS


  PROCEDURE p_changeVoucherStatus(
      p_batchNo               IN VOMS_BATCHES.BATCH_NO%TYPE,
      p_batchType             IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_fromSerialNo          IN VOMS_BATCHES.FROM_SERIAL_NO%TYPE,
      p_toSerialNo            IN VOMS_BATCHES.TO_SERIAL_NO%TYPE,
      p_batchEnableStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchGenStat          IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchOnHoldStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchStolenStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchSoldStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchDamageStat       IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchReconcileStat    IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_batchPrintStat        IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_wareHouseStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_preActiveStat         IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_suspendStat           IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_createdOn             IN VOMS_VOUCHERS.EXPIRY_DATE%TYPE,
      p_maxErrorAllowed       IN NUMBER,
      p_modifiedBy            IN VOMS_VOUCHER_AUDIT.MODIFIED_BY%TYPE,
      p_noOfVouchers          IN NUMBER,
      p_successProcessStatus  IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
      p_errorProcessStatus    IN VOMS_VOUCHER_AUDIT.PROCESS_STATUS%TYPE,
      p_batchConStat          IN VOMS_BATCHES.BATCH_TYPE%TYPE,
      p_processScreen         IN NUMBER,
      p_modifiedTime          IN VOMS_BATCHES.MODIFIED_ON%TYPE,
      p_referenceNo           IN VOMS_BATCHES.REFERENCE_NO%TYPE,
      p_RCAdminMaxdateallowed IN NUMBER,
      p_prefString            IN VARCHAR2,
      p_EnableProcess VOMS_BATCHES.PROCESS%TYPE,
      p_ChangeProcess VOMS_BATCHES.PROCESS%TYPE,
      p_ReconcileProcess VOMS_BATCHES.PROCESS%TYPE,
      p_networkCode VOMS_BATCHES.NETWORK_CODE%TYPE,
      p_seqID IN voms_batches.SEQUENCE_ID%type,
      p_returnMessage OUT VARCHAR2,
      p_returnLogMessage OUT VARCHAR2,
      p_sqlErrorMessage OUT VARCHAR2,
      p_bulkOutput OUT STRINGARRAYNEW )
      
  IS
  BEGIN
    /*set parameters to global variables so that they can be
    used by other procedures as well */
    v_batchEnableStat:=p_batchEnableStat;
    v_batchGenStat             :=p_batchGenStat;
    v_batchOnHoldStat          :=p_batchOnHoldStat;
    v_batchStolenStat          :=p_batchStolenStat;
    v_batchSoldStat            := p_batchSoldStat;
    v_batchDamageStat          :=p_batchDamageStat;
    v_batchReconcileStat       :=p_batchReconcileStat;
    v_batchPrintStat           :=p_batchPrintStat;
    v_wareHouseStat            :=p_wareHouseStat;
    v_preActiveStat            :=p_preActiveStat;
    v_suspendStat              :=p_suspendStat;
    v_batchConStat             :=p_batchConStat;
    v_createdOn                :=p_createdOn;
    v_modifiedBy               :=p_modifiedBy;
    v_errorCount               :=0;
    v_serialStart              :=p_fromSerialNo;
    v_batchNo                  :=p_batchNo;
    v_batchType                :=p_batchType;
    v_processScreen            :=p_processScreen;
    v_modifiedTime             :=p_modifiedTime;
    v_enableCount              :=0;
    v_DamageStolenCount        :=0;
    v_DamageStolenAfterEnCount :=0;
    v_onHoldCount              :=0;
    v_counsumedCount           :=0;
    v_wareHouseCount           :=0;
    v_preActiveCount           :=0;
    v_suspendCount             :=0;
    v_referenceNo              :=p_referenceNo;
    v_RCAdminMaxdateallowed    :=p_RCAdminMaxdateallowed;
    v_EnableProcess            :=p_EnableProcess;
    v_ChangeProcess            :=p_ChangeProcess;
    v_ReconcileProcess         :=p_ReconcileProcess;
    v_networkCode              :=p_networkCode;
    v_voucherNotFoundCount     :=0;
    v_seqId                    :=p_seqID;
    v_returnMessage            :='';
    v_sqlErrorMessage          :='';
    v_serialNoLength           :=LENGTH(p_fromSerialNo);
    v_prefString               :=p_prefString;
    BEGIN
      p_bulkOutput := STRINGARRAYNEW();
      v_bulkOutput.EXTEND( v_serialNoLength );
      p_bulkOutput.EXTEND( v_serialNoLength );
      fhandleSuccess := utl_file.fopen( 'TEST_DIR'                        -- File location
      , 'CHANGE_STATUS_SUCCESS_'|| v_preference || '_'||v_batchNo||'.log' -- File name
      , 'w'                                                               -- Open mode: w = write.
      );
      fhandleFail := utl_file.fopen( 'TEST_DIR'                        -- File location
      , 'CHANGE_STATUS_FAIL_'|| v_preference || '_'||v_batchNo||'.log' -- File name
      , 'w'                                                            -- Open mode: w = write.
      );
    EXCEPTION
    WHEN utl_file.invalid_path THEN
      DBMS_OUTPUT.PUT_LINE('Not able to open file handle , Invalid path '||SQLERRM);
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Not able to open file handle '||SQLERRM);
    END;
    -- Start the Loop --
    WHILE(v_serialStart<=p_toSerialNo)
    LOOP
      BEGIN
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
            CHECK_CHANGE_VALID_PROC;
          EXCEPTION
          WHEN EXITEXCEPTION THEN
            DBMS_OUTPUT.PUT_LINE('EXCEPTION while checking if voucher is valid  ='||SQLERRM);
            v_returnMessage:='FAILED';
            RAISE EXITEXCEPTION;
          WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('others EXCEPTION while checking if voucher is valid  ='||SQLERRM);
            v_returnMessage:='FAILED';
            RAISE EXITEXCEPTION;
          END;
          DBMS_OUTPUT.PUT_LINE('v_succFailFlag  ='||v_succFailFlag);
          -- If vouchers are valid then perform these steps
          IF(v_succFailFlag='SUCCESS') THEN
            /* If vouchers are valid for change status and the new
            voucher status is of enable type then
            1. Update voucher Table */
            IF(p_batchType=p_batchEnableStat AND v_processScreen=1) THEN
              BEGIN
                UPDATE_VOUCHER_ENABLE;
              EXCEPTION
              WHEN EXITEXCEPTION THEN
                DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                v_returnMessage:='FAILED';
                RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                v_returnMessage:='FAILED';
                RAISE EXITEXCEPTION;
              END;
            ELSIF(p_batchType=p_batchEnableStat AND (v_processScreen=2)) THEN
              BEGIN
                UPDATE_VOUCHER_ENABLE_OTHER;
              EXCEPTION
              WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
                DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
                v_returnMessage:='FAILED';
                DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                RAISE EXITEXCEPTION;
              END;
            ELSIF(p_batchType=p_batchEnableStat AND v_processScreen=3) THEN
              BEGIN
                UPDATE_VOUCHER_ENABLE_OTHER;
              EXCEPTION
              WHEN EXITEXCEPTION THEN
                v_returnMessage:='FAILED';
                DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
                v_returnMessage:='FAILED';
                DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers for Enable type  ='||SQLERRM);
                RAISE EXITEXCEPTION;
              END;
              /* If new voucher status is other than enable
              then perform these steps
              1. Update Vouchers. */
            ELSE
              BEGIN
                UPDATE_VOUCHERS;
              EXCEPTION
              WHEN EXITEXCEPTION THEN
                DBMS_OUTPUT.PUT_LINE('EXCEPTION while updating vouchers  ='||SQLERRM);
                v_returnMessage:='FAILED';
                RAISE EXITEXCEPTION;
              WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('others EXCEPTION while updating vouchers  ='||SQLERRM);
                v_returnMessage:='FAILED';
                RAISE EXITEXCEPTION;
              END;
            END IF; --   end of if(p_batchType=p_batchEnableStat)
          END IF;   --end of if(SUCCESS)
          IF(v_succFailFlag ='SUCCESS') THEN
            v_processStatus:=p_successProcessStatus; --store SU in status of VA table in case of success
            v_message      :='Success';
          ELSE
            v_processStatus:=p_errorProcessStatus; --store ER in status of VA table in case of error
          END IF;
          /* For all voucher status change log entry of each serial no
          in voucher udit table . Block for insertion in VA table */
          IF(v_voucherNotFoundFlag=FALSE) THEN
            BEGIN
              INSERT_IN_AUDIT_PROC;
            EXCEPTION
            WHEN EXITEXCEPTION THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in VA table ='||SQLERRM);
              RAISE EXITEXCEPTION;
            WHEN OTHERS THEN
              v_returnMessage:='FAILED';
              DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in VA table  ='||SQLERRM);
              RAISE EXITEXCEPTION;
            END;  -- end of inserting record in voucher_audit table
          END IF; -- end of  if(v_voucherNotFoundFlag=false)
        ELSE      -- Else of exceeding the max error allowed
          v_succFailFlag    :='FAILED';
          v_returnMessage   :='FAILED';
          v_maxErrorFlag    :=TRUE;
          v_message         :='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
          v_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
          RAISE EXITEXCEPTION;
          --ROLLBACK OR THROW EXECPTION
        END IF;
        v_serialStart:=v_serialStart+1; -- incrementing from serial no by 1
        v_serialStart:=LPAD(v_serialStart,v_serialNoLength,0);
        DBMS_OUTPUT.PUT_LINE('v_serialStart after incrementing ='||v_serialStart);
        /* catch the Exception of type EXITEXCEPTION thrown above */
      EXCEPTION
      WHEN EXITEXCEPTION THEN
        v_returnMessage:='FAILED';
        RAISE EXITEXCEPTION; --Throw same Exception to come out of the loop
      WHEN OTHERS THEN
        v_returnMessage:='FAILED';
        RAISE EXITEXCEPTION; --Throw same Exception to come out of the loop
      END;
    END LOOP; -- end of while loop
    DBMS_OUTPUT.PUT_LINE('v_serialStart after loop ='||v_serialStart);
    /*    Update the Voucher batch and voucher summary  Table */
    BEGIN
      INSERT_IN_SUMMARY_PROC;
    EXCEPTION
    WHEN EXITEXCEPTION THEN
      v_returnMessage:='FAILED';
      DBMS_OUTPUT.PUT_LINE('EXCEPTION while inserting in summary table  ='||SQLERRM);
      RAISE EXITEXCEPTION;
    WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting in summary table  ='||SQLERRM);
      RAISE EXITEXCEPTION;
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
      p_returnLogMessage:='Not able to update the vouchers status to. '||v_batchType;
    ELSE
      p_returnMessage         :='SUCCESS';
      IF(v_voucherNotFoundCount>0) THEN
        p_returnLogMessage    :='Successfully changed status with '||v_voucherNotFoundCount||' vouchers not found';
      ELSIF(v_errorCount       > 0) THEN
        p_returnLogMessage    :='Not able to update the status to '||v_batchType||' of  '|| v_errorCount ||' vouchers';
      ELSE
        p_returnLogMessage:='Successfully changed status of '||p_noOfVouchers||' vouchers';
      END IF;
    END IF;
    BEGIN
      IF p_returnMessage = 'SUCCESS' THEN
        utl_file.put(fhandleSuccess, v_serialStart || p_returnLogMessage || CHR(10));
       -- v_bulkOutput(v_bulkOutput_Counter):=  'Summary:'|| p_returnLogMessage;
       -- v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
        p_bulkOutput                      :=v_bulkOutput;
      ELSE
        utl_file.put(fhandleFail, v_serialStart || p_returnLogMessage || CHR(10));
       -- v_bulkOutput(v_bulkOutput_Counter):= 'Summary:'|| p_returnLogMessage;
       -- v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
        p_bulkOutput                      :=v_bulkOutput;
      END IF;
      utl_file.fclose(fhandleSuccess);
      utl_file.fclose(fhandleFail);
    EXCEPTION
    WHEN utl_file.invalid_path THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle , Invalid path '||SQLERRM);
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle '||SQLERRM);
    END;
  EXCEPTION
  WHEN EXITEXCEPTION THEN
    p_returnMessage   :='FAILED';
    p_returnLogMessage:='Not able to update the vouchers status to.. '||v_batchType;
    p_sqlErrorMessage :=v_sqlErrorMessage;
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
    --ROLLBACK;  --Rollback in case of Exception
    BEGIN
      utl_file.put(fhandleFail, v_serialStart || p_returnLogMessage || CHR(10));
      utl_file.fclose(fhandleSuccess);
      utl_file.fclose(fhandleFail);
      v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| p_returnLogMessage;
      v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
      p_bulkOutput                      :=v_bulkOutput;
    EXCEPTION
    WHEN utl_file.invalid_path THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle , Invalid path '||SQLERRM);
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle '||SQLERRM);
    END;
  WHEN OTHERS THEN
    p_returnMessage   :='FAILED';
    p_returnLogMessage:='Not able to update the vouchers status to... Trace'|| dbms_utility.format_error_backtrace ||' '||v_batchType||' p_preference '||p_prefString;
    p_sqlErrorMessage :=v_sqlErrorMessage;
    DBMS_OUTPUT.PUT_LINE('Procedure Exiting'||SQLERRM);
    --ROLLBACK;
    BEGIN
      utl_file.put(fhandleFail, v_serialStart || p_returnLogMessage || CHR(10));
      utl_file.fclose(fhandleSuccess);
      utl_file.fclose(fhandleFail);
      v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| p_returnLogMessage;
      v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
      p_bulkOutput                      :=v_bulkOutput;
    EXCEPTION
    WHEN utl_file.invalid_path THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle , Invalid path '||SQLERRM);
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Not able to close file handle '||SQLERRM);
    END;
    p_bulkOutput:=v_bulkOutput;
  END; --Rollback in case of Exception
  PROCEDURE CHECK_CHANGE_VALID_PROC
  IS
  BEGIN
    /* Get the voucher status abd then check whether that voucher
    is valid for status change or not */
    
    IF v_seqId = -1 THEN
    
        SELECT STATUS,
      CURRENT_STATUS,
      EXPIRY_DATE,
      GENERATION_BATCH_NO,
      PRODUCTION_NETWORK_CODE,
      NVL(USER_NETWORK_CODE,''),
      PRODUCT_ID,
      PREVIOUS_STATUS,
      LAST_CONSUMED_ON,
      LAST_REQUEST_ATTEMPT_NO,
      LAST_ATTEMPT_VALUE
    INTO v_vouchStat,
      v_voucCurrStat,
      v_expDate,
      v_generationBatchNo,
      v_prodNetworkCode,
      v_userNetworkCode,
      v_productID,
      v_PreviousStatus,
      v_LastConsumedOn,
      v_LastRequestAttemptNo,
      v_LastAttemptValue
    FROM VOMS_VOUCHERS
    WHERE SERIAL_NO=v_serialStart   FOR UPDATE OF STATUS,
      CURRENT_STATUS;

    ELSE
    
    SELECT STATUS,
      CURRENT_STATUS,
      EXPIRY_DATE,
      GENERATION_BATCH_NO,
      PRODUCTION_NETWORK_CODE,
      NVL(USER_NETWORK_CODE,''),
      PRODUCT_ID,
      PREVIOUS_STATUS,
      LAST_CONSUMED_ON,
      LAST_REQUEST_ATTEMPT_NO,
      LAST_ATTEMPT_VALUE
    INTO v_vouchStat,
      v_voucCurrStat,
      v_expDate,
      v_generationBatchNo,
      v_prodNetworkCode,
      v_userNetworkCode,
      v_productID,
      v_PreviousStatus,
      v_LastConsumedOn,
      v_LastRequestAttemptNo,
      v_LastAttemptValue
    FROM VOMS_VOUCHERS
    WHERE SERIAL_NO=v_serialStart and SEQUENCE_ID=v_seqId  FOR UPDATE OF STATUS,
      CURRENT_STATUS;
    
    END IF;  
      
    DBMS_OUTPUT.PUT_LINE('v_serialStart  ='||v_serialStart||'v_vouchStat'||v_vouchStat);
    /* Check whether that voucher has expired or not . If not then
    perform voucher valid for change status checking.*/
    BEGIN -- Begin of batch type checking
      IF(v_expDate>=v_createdOn) THEN
        FOR v_prefStringIndex IN
        (SELECT trim(regexp_substr(v_prefString, '[^,]+', 1, LEVEL)) l
        FROM dual
          CONNECT BY LEVEL <= regexp_count(v_prefString, ',')+1
        )
        
        
        
         
        LOOP
          DBMS_OUTPUT.put_line (v_prefStringIndex.l);
          SELECT regexp_substr (v_prefStringIndex.l, '[^:]+', 1, 1) ,
            regexp_substr (v_prefStringIndex.l, '[^:]+', 1, 2)
          INTO fromStatus,
            toStatus
          FROM dual;
          DBMS_OUTPUT.PUT_LINE(' fromStatus '||fromStatus);
          DBMS_OUTPUT.PUT_LINE(' toStatus '||toStatus);
          
          
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
				IF(v_vouchStat=v_batchReconcileStat) THEN ----Check1
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
					END IF; -- of consume type checking
              END IF;
              EXIT;
            END IF;
          END IF;
        END LOOP;





        IF(v_succFailFlag='FAILED')THEN
          v_errorCount  :=v_errorCount+1;
          v_message     :='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' this Screen';
        
        v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' this Screen';
        v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
        
        END IF;
      END IF;
    END;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    v_errorCount  :=v_errorCount+1;
    v_succFailFlag:='FAILED';
    --v_returnMessage:='FAILED';
    v_message:='NO Record found for voucher in vouchers table';
    --v_returnLogMessage:='NO Record found for voucher in vouchers table'||v_serialStart;
    v_voucherNotFoundCount:=v_voucherNotFoundCount+1;
    v_voucherNotFoundFlag :=TRUE;
    DBMS_OUTPUT.PUT_LINE('NO Record found for voucher in vouchers table'||v_serialStart);
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'NO Record found for voucher in vouchers table';
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;


    -- RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||v_serialStart);
    v_returnMessage   :='FAILED';
    v_returnLogMessage:='Exception while checking for voucher status in vouchers table'||v_serialStart||SQLERRM;
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| v_returnLogMessage;
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
    
    RAISE EXITEXCEPTION;
  END;
/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and user is coming from Enable screen*/
  PROCEDURE UPDATE_VOUCHER_ENABLE
  IS
  BEGIN
    /* If previous voucher status is of Reconcile then update */
    IF(v_vouchStat=v_batchReconcileStat) THEN
    
    IF v_seqId = -1 THEN
    
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        STATUS             =v_batchType,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart;
      
      
    ELSE
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        STATUS             =v_batchType,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart  and SEQUENCE_ID=v_seqId ;
      
      END IF;
      
    IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
        RAISE SQLException;
      ELSE
        v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Successfully updated!';
        v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
      END IF; -- end of if SQL%NOTFOUND
      /* If previous voucher status other than Reconcile then update */
    ELSE
      
      
      IF v_seqId = -1 THEN
      UPDATE VOMS_VOUCHERS
      SET ENABLE_BATCH_NO=v_batchNo,
        STATUS           =v_batchType,
        CURRENT_STATUS   =v_batchType,
        MODIFIED_BY      =v_modifiedBy,
        MODIFIED_ON      =v_modifiedTime,
        LAST_BATCH_NO    =v_batchNo,
        PREVIOUS_STATUS  =v_vouchStat
      WHERE serial_no    =v_serialStart;
      ELSE
      
       UPDATE VOMS_VOUCHERS
      SET ENABLE_BATCH_NO=v_batchNo,
        STATUS           =v_batchType,
        CURRENT_STATUS   =v_batchType,
        MODIFIED_BY      =v_modifiedBy,
        MODIFIED_ON      =v_modifiedTime,
        LAST_BATCH_NO    =v_batchNo,
        PREVIOUS_STATUS  =v_vouchStat
      WHERE serial_no    =v_serialStart and SEQUENCE_ID=v_seqId;
      
      END IF;
      
      
      IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
        RAISE SQLException;
      ELSE
        v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Successfully updated!';
        v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
      END IF; -- end of if SQL%NOTFOUND
    END IF;   -- end of if(v_vouchStat=p_batchReconcileStat)
  EXCEPTION
  WHEN SQLException THEN
    v_returnMessage   :='FAILED';
    v_message         :='Not able to update voucher table'||v_serialStart;
    v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
    DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| ' Not able to update voucher in vouchers table';
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
    RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    v_returnMessage:='FAILED';
    DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
    v_returnLogMessage                :='Exception while updating voucher table'||v_serialStart;
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Exception while updating voucher table';
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
    RAISE EXITEXCEPTION;
  END;
/*Procedure that will update voucher table. This will be called for all
voucher status other than Enable type */
  PROCEDURE UPDATE_VOUCHERS
  IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('INSIDE SUCCESS  ='||v_batchType||v_batchNo);
    IF(v_voucCurrStat=v_batchReconcileStat) THEN
    
    
    IF v_seqId = -1 THEN
      UPDATE VOMS_VOUCHERS
      SET STATUS        =v_batchType,
        CURRENT_STATUS  =v_batchType,
        LAST_BATCH_NO   =v_batchNo,
        MODIFIED_BY     =v_modifiedBy,
        MODIFIED_ON     =v_modifiedTime,
        PREVIOUS_STATUS =v_vouchStat,
        LAST_Attempt_NO =v_LastRequestAttemptNo,
        ATTEMPT_USED    =ATTEMPT_USED     +1,
        TOTAL_VALUE_USED=(TOTAL_VALUE_USED+v_LastAttemptValue)
      WHERE serial_no   =v_serialStart;
      
      ELSE
      
      UPDATE VOMS_VOUCHERS
      SET STATUS        =v_batchType,
        CURRENT_STATUS  =v_batchType,
        LAST_BATCH_NO   =v_batchNo,
        MODIFIED_BY     =v_modifiedBy,
        MODIFIED_ON     =v_modifiedTime,
        PREVIOUS_STATUS =v_vouchStat,
        LAST_Attempt_NO =v_LastRequestAttemptNo,
        ATTEMPT_USED    =ATTEMPT_USED     +1,
        TOTAL_VALUE_USED=(TOTAL_VALUE_USED+v_LastAttemptValue)
      WHERE serial_no   =v_serialStart and SEQUENCE_ID=v_seqId;
      
      
      END IF;
      
      
    IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
        RAISE SQLException;
        
      ELSE
        v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Successfully updated!';
        v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
     
      END IF;
    ELSE
      
      
      IF v_seqId = -1 THEN
      UPDATE VOMS_VOUCHERS
      SET STATUS       =v_batchType,
        CURRENT_STATUS =v_batchType,
        LAST_BATCH_NO  =v_batchNo,
        MODIFIED_BY    =v_modifiedBy,
        MODIFIED_ON    =v_modifiedTime,
        PREVIOUS_STATUS=v_vouchStat
      WHERE serial_no  =v_serialStart;
      ELSE
      
        UPDATE VOMS_VOUCHERS
      SET STATUS       =v_batchType,
        CURRENT_STATUS =v_batchType,
        LAST_BATCH_NO  =v_batchNo,
        MODIFIED_BY    =v_modifiedBy,
        MODIFIED_ON    =v_modifiedTime,
        PREVIOUS_STATUS=v_vouchStat
      WHERE serial_no  =v_serialStart and SEQUENCE_ID=v_seqId ;
      
      END IF;
      
      
      IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating voucher status  ='||SQLERRM);
        RAISE SQLException;
      ELSE
        v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Successfully updated!';
        v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
       
      END IF;
    END IF;
  EXCEPTION
  WHEN SQLException THEN
    v_returnMessage   :='FAILED';
    v_message         :='Not able to update voucher table'||v_serialStart;
    v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
    DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Not able to update voucher in vouchers table';
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;
     
    RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    v_returnMessage:='FAILED';
    DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
    v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Exception while updating voucher table';
    v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;


    RAISE EXITEXCEPTION;
  END;
/*Procedure that will update voucher table. This will be called only
if new voucher status is of Enable type and process is 2 or 3 ie
change status or Reconcile */
  PROCEDURE UPDATE_VOUCHER_ENABLE_OTHER
  IS
  BEGIN
    /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
    IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
    
    IF v_seqId = -1 THEN
    
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        STATUS             =v_batchType,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart;
      
    ELSE
    
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        STATUS             =v_batchType,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart and SEQUENCE_ID=v_seqId;
    
    
    END IF;
    
    
    
      IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
        RAISE SQLException;
      ELSE
      v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'SQL EXCEPTION while update voucher table... ='||SQLERRM;
     v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;

      END IF; -- end of if SQL%NOTFOUND
      /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
    ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
    
    IF v_seqId = -1 THEN
    
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart;
      
      
      ELSE
      
      UPDATE VOMS_VOUCHERS
      SET USER_NETWORK_CODE=NULL,
        ENABLE_BATCH_NO    =v_batchNo,
        CURRENT_STATUS     =v_batchType,
        LAST_BATCH_NO      =v_batchNo,
        MODIFIED_BY        =v_modifiedBy,
        MODIFIED_ON        =v_modifiedTime,
        PREVIOUS_STATUS    =v_vouchStat
      WHERE serial_no      =v_serialStart and SEQUENCE_ID=v_seqId;
      
      
      END IF;
      IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
        RAISE SQLException;
        
      ELSE
      v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'SQL EXCEPTION while update voucher table. ='||SQLERRM;
      v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;


      END IF; -- end of if SQL%NOTFOUND
    ELSE      --Added By Gurjeet on 11/10/2004 because this was missing
    
    IF v_seqId = -1 THEN
      UPDATE VOMS_VOUCHERS
      SET ENABLE_BATCH_NO=v_batchNo,
        STATUS           =v_batchType,
        CURRENT_STATUS   =v_batchType,
        MODIFIED_BY      =v_modifiedBy,
        MODIFIED_ON      =v_modifiedTime,
        LAST_BATCH_NO    =v_batchNo,
        PREVIOUS_STATUS  =v_vouchStat
      WHERE serial_no    =v_serialStart;
      ELSE
      
      UPDATE VOMS_VOUCHERS
      SET ENABLE_BATCH_NO=v_batchNo,
        STATUS           =v_batchType,
        CURRENT_STATUS   =v_batchType,
        MODIFIED_BY      =v_modifiedBy,
        MODIFIED_ON      =v_modifiedTime,
        LAST_BATCH_NO    =v_batchNo,
        PREVIOUS_STATUS  =v_vouchStat
      WHERE serial_no    =v_serialStart and SEQUENCE_ID=v_seqId ;
      END IF;
      
      IF SQL%NOTFOUND THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
        
       v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'SQL EXCEPTION while update voucher table.. ='||SQLERRM;
       v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;


        RAISE SQLException;
        
      ELSE
       v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Updated Successfully!';
       v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;

      END IF; -- end of if SQL%NOTFOUND
    END IF;   -- end of if(v_vouchStat=p_batchReconcileStat)
  EXCEPTION
  WHEN SQLException THEN
    v_returnMessage   :='FAILED';
    v_message         :='Not able to update voucher table'||v_serialStart;
    v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
    DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Not able to update voucher in vouchers table';
     v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;


    RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    v_returnMessage:='FAILED';
    DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
    v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
    
    v_bulkOutput(v_bulkOutput_Counter):= v_serialStart ||':'|| 'Exception while updating voucher table';
     v_bulkOutput_Counter              :=v_bulkOutput_Counter+1;

    RAISE EXITEXCEPTION;
  END;
/*Procedure that will insert values in voucher summary table.
This will be called at the end to update details */
  PROCEDURE INSERT_IN_AUDIT_PROC
  IS
  BEGIN
    BEGIN -- block for getting next row ID
      SELECT voucher_audit_id.NEXTVAL INTO v_row_id FROM dual;
      v_insertRowId:=TO_CHAR(v_row_id);
      DBMS_OUTPUT.PUT_LINE('v_insertRowId  ='||v_insertRowId);
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
      v_returnMessage   :='FAILED';
      RAISE EXITEXCEPTION;
    WHEN OTHERS THEN
      v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
      v_returnMessage   :='FAILED';
      RAISE EXITEXCEPTION;
    END;  -- end of getting next row id block
    BEGIN -- Block for inserting record in voucher_audit table
      INSERT
      INTO VOMS_VOUCHER_AUDIT
        (
          ROW_ID,
          SERIAL_NO,
          CURRENT_STATUS,
          PREVIOUS_STATUS,
          MODIFIED_BY,
          MODIFIED_ON,
          STATUS_CHANGE_SOURCE,
          STATUS_CHANGE_PARTNER_ID,
          BATCH_NO,
          MESSAGE,
          PROCESS_STATUS
        )
        VALUES
        (
          v_insertRowId,
          v_serialStart,
          v_batchType,
          v_vouchStat,
          v_modifiedBy,
          v_modifiedTime,
          'WEB',
          '',
          v_batchNo,
          v_message,
          v_processStatus
        );
    EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('others EXCEPTION while inserting next row no  ='||SQLERRM);
      v_returnMessage   :='FAILED';
      v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
      RAISE NOTINSERTEXCEPTION;
    END; -- end of inserting record in voucher_audit table
  EXCEPTION
  WHEN SQLException THEN
    v_returnMessage  :='FAILED';
    v_sqlErrorMessage:=SQLERRM;
    DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
    RAISE EXITEXCEPTION;
  WHEN NOTINSERTEXCEPTION THEN
    v_returnMessage  :='FAILED';
    v_sqlErrorMessage:=SQLERRM;
    DBMS_OUTPUT.PUT_LINE('SQL Exception for inserting in VA table '||v_serialStart);
    RAISE EXITEXCEPTION;
  WHEN EXITEXCEPTION THEN
    v_returnMessage  :='FAILED';
    v_sqlErrorMessage:=SQLERRM;
    RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    v_returnMessage   :='FAILED';
    v_returnLogMessage:='Exception while inserting in VA table '||v_serialStart||SQLERRM;
    v_sqlErrorMessage :=SQLERRM;
    RAISE EXITEXCEPTION;
  END;
/*Procedure that will insert values in voucher summary table.
This will be called at the end to update details */
  PROCEDURE INSERT_IN_SUMMARY_PROC
  IS
  BEGIN
    DBMS_OUTPUT.PUT_LINE('v_batchNo='||v_batchNo);
    UPDATE VOMS_VOUCHER_BATCH_SUMMARY
    SET TOTAL_ENABLED          =TOTAL_ENABLED            +v_enableCount,
      TOTAL_RECHARGED          =TOTAL_RECHARGED          +v_counsumedCount,
      TOTAL_STOLEN_DMG         =TOTAL_STOLEN_DMG         +(v_DamageStolenCount-v_DamageStolenAfterEnCount),
      TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount
    WHERE BATCH_NO             =v_referenceNo;
    --WHERE  BATCH_NO =v_generationBatchNo;
    IF SQL%NOTFOUND THEN
      DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while updating BATCH_SUMMARY  ='||SQLERRM);
      v_returnLogMessage:='Exception while updating BATCH_SUMMARY table';
      RAISE SQLException;
    END IF; -- end of if SQL%NOTFOUND
    BEGIN   --block for insertion/updation in voucher_summary
      BEGIN --block checking if record exist in voucher_summary
        SELECT '1'
        INTO rcd_count
        FROM dual
        WHERE EXISTS
          (SELECT 1
          FROM VOMS_VOUCHER_SUMMARY
          WHERE SUMMARY_DATE         =v_createdOn
          AND PRODUCT_ID             =v_productID
          AND PRODUCTION_NETWORK_CODE=v_networkCode
          AND USER_NETWORK_CODE      =v_networkCode
          );
        DBMS_OUTPUT.PUT_LINE('rcd_count='||rcd_count);
      EXCEPTION
      WHEN NO_DATA_FOUND THEN --when no row returned for the distributor
        DBMS_OUTPUT.PUT_LINE('No Record found in voucher summary table');
        rcd_count := 0;
      WHEN SQLException THEN
        DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while checking for voucher_summary  ='||SQLERRM);
        v_returnMessage   :='FAILED';
        v_returnLogMessage:='Exception while checking is record exist in summary table ';
        RAISE SQLException;
      WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Exception while checking is record exist');
        v_returnMessage   :='FAILED';
        v_returnLogMessage:='Exception while checking is record exist in summary table ';
        RAISE SQLException;
      END;
      IF rcd_count = 0 THEN
        INSERT
        INTO VOMS_VOUCHER_SUMMARY
          (
            SUMMARY_DATE,
            PRODUCT_ID,
            PRODUCTION_NETWORK_CODE,
            USER_NETWORK_CODE,
            TOTAL_ENABLED,
            TOTAL_STOLEN_DMG,
            TOTAL_STOLEN_DMG_AFTER_EN,
            TOTAL_ON_HOLD
          )
          VALUES
          (
            v_createdOn,
            v_productID,
            v_networkCode,
            v_networkCode,
            v_enableCount,
            (v_DamageStolenCount-v_DamageStolenAfterEnCount),
            v_DamageStolenAfterEnCount,
            v_onHoldCount
          );
      ELSE
        UPDATE VOMS_VOUCHER_SUMMARY
        SET TOTAL_ENABLED          =TOTAL_ENABLED            +v_enableCount,
          TOTAL_STOLEN_DMG         =TOTAL_STOLEN_DMG         +(v_DamageStolenCount-v_DamageStolenAfterEnCount),
          TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount,
          TOTAL_ON_HOLD            =TOTAL_ON_HOLD            +v_onHoldCount
        WHERE SUMMARY_DATE         =v_createdOn
        AND PRODUCT_ID             =v_productID
        AND PRODUCTION_NETWORK_CODE=v_networkCode
        AND USER_NETWORK_CODE      =v_networkCode;
      END IF;
    EXCEPTION
    WHEN SQLException THEN
      v_returnMessage:='FAILED';
      DBMS_OUTPUT.PUT_LINE('SQL/OTHERS EXCEPTION CAUGHT while Record exist in summary'||SQLERRM);
      RAISE SQLException;
    WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('EXCEPTION CAUGHT while Record exist in summary='||SQLERRM);
      v_returnMessage   :='FAILED';
      v_returnLogMessage:='Exception while insertin/updating summary table ';
      RAISE NOTINSERTEXCEPTION;
    END; --end of voucher_audit insertion block
  EXCEPTION
  WHEN SQLException THEN
    v_returnMessage:='FAILED';
    --DBMS_OUTPUT.PUT_LINE('SQL Exception for updating records '||p_fromSerialNo);
    RAISE EXITEXCEPTION;
  WHEN NOTINSERTEXCEPTION THEN
    v_returnMessage:='FAILED';
    --DBMS_OUTPUT.PUT_LINE('Not able to insert record in voucher_audit '||p_fromSerialNo);
    RAISE EXITEXCEPTION;
  WHEN OTHERS THEN
    v_returnMessage   :='FAILED';
    v_returnLogMessage:='Exception while inserting/updating summary table ';
    RAISE EXITEXCEPTION;
  END;
END CHANGE_STATUS_PKG;



--##########################################################################################################
--##
--##      PreTUPS_v7.16.0 DB Script
--##
--##########################################################################################################

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DVD_ORDER_BY_PARAMETERS', 'DVD ORDER BY PARAMETERS', 'SYSTEMPRF', 'STRING', 'EXPIRY_DATE , CREATED_ON, SERIAL_NO', NULL, NULL, 50, 'DVD ORDER BY PARAMETERS', 'N', 'Y', 'VMS', 'DVD ORDER BY PARAMETERS', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_CHANGE_STATUS_SYSTEM_LMT', 'Max system limit for change status ol', 'SYSTEMPRF', 'INT', '100', 0, 100000, 50, 'Maximum system limit for batches for online voucher change status', 'Y', 'Y', 'VMS', 'Maximum system level limit for online change status on batches', TIMESTAMP '2019-11-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-11 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ONLINE_CHANGE_STATUS_NETWORK_LMT', 'Max network limit for change status ol', 'SYSTEMPRF', 'INT', '100', 0, 100000, 50, 'Maximum network limit for batches for online voucher change status', 'Y', 'Y', 'VMS', 'Maximum network level limit for online change status on batches', TIMESTAMP '2019-11-11 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-11 00:00:00.000000', 'SU0001', NULL, 'Y');

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('CHANGESTATUSONLINE', TIMESTAMP '2019-11-12 00:00:00.000000', 'C', TIMESTAMP '2019-11-12 00:00:00.000000', TIMESTAMP '2019-11-12 00:00:00.000000', 360, 1440, 'change status online', 'NG', 0);

INSERT INTO NETWORK_PREFERENCES
(NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('NG', 'ONLINE_CHANGE_STATUS_NETWORK_LMT', '20', TIMESTAMP '2019-11-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-12 00:00:00.000000', 'SU0001');

INSERT INTO NETWORK_PREFERENCES
(NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('PB', 'ONLINE_CHANGE_STATUS_NETWORK_LMT', '20', TIMESTAMP '2019-11-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-11-12 00:00:00.000000', 'SU0001');
INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('CHNGESTATOLNWCOUNT', TIMESTAMP '2019-11-14 10:29:13.000000', 'C', TIMESTAMP '2019-11-14 10:29:13.000000', TIMESTAMP '2019-11-14 00:00:00.000000', 360, 1440, 'change status online', 'PB', 0);

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('CHNGESTATOLNWCOUNT', TIMESTAMP '2019-11-14 10:29:13.000000', 'C', TIMESTAMP '2019-11-14 10:29:13.000000', TIMESTAMP '2019-11-14 00:00:00.000000', 360, 1440, 'change status online', 'NG', 0);

ALTER TABLE VOMS_BATCHES ADD MASTER_BATCH_ID varchar2(15) DEFAULT 'NA';

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DVD_BATCH_FILEEXT', 'Batch DVD download file ext', 'SYSTEMPRF', 'STRING', 'xls', NULL, NULL, 50, 'the values for extension can be csv or xls or xlsx', 'N', 'Y', 'DVD', 'Extension of file to be downloaded or uploaded for batch DVD in batch recharge management', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C', 'MAX_APPROVAL_LEVEL_C2C', 'SYSTEMPRF', 'STRING', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-06-06 15:03:14.000000', 'SU0001', NULL, 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TRFINI', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfInitiateController', 'C2C initate', 'C2C initate', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'C2C initate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('CARD_GROUP_ALLOWED_CATEGORIES', 'Card group allowed categories', 'SYSTEMPRF', 'STRING', 'NWADM,SUNADM', NULL, NULL, 50, 'Card group allowed categories', 'Y', 'Y', 'VMS', 'Card group allowed categories', SYSDATE, 'ADMIN',SYSDATE, 'ADMIN', NULL, 'N');
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TRANSFER_RULE_ALLOWED_CATEGORIES', 'Transfer rule allowed categories', 'SYSTEMPRF', 'STRING', 'NWADM,SUNADM', NULL, NULL, 50, 'Transfer rule allowed categories', 'Y', 'Y', 'VMS', 'Transfer rule allowed categories', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', NULL, 'N');




--##########################################################################################################
--##
--##      PreTUPS_v7.17.0 DB Script
--##
--##########################################################################################################

DELETE FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='MAX_APPROVAL_LEVEL_C2C';

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_TRANSFER', 'Max Approval level C2C for Transfer', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_INITIATE', 'Max Approval level C2C for Buy', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C', 'Y', 'Y', 'C2S', 'Max appoval level C2C', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_INITIATE', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_ALLOWED_VOUCHER_LIST', 'List of vchr status allowed for c2c', 'SYSTEMPRF', 'STRING', 'EN,OH,PA,ST', NULL, 15, 50, 'List of vchr status allowed for c2c', 'N', 'Y', 'VOMS', 'Services', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'REST', '190', 'TRFVOMS', 'TRFVOMS_REST', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001',SYSDATE, 'SU0001', 'SVK0004292', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'EXTGW', '190', 'TRFVOMS', 'TRFVOMS_EXTGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004298', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'MAPPGW', '190', 'TRFVOMS', 'TRFVOMS_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004297', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'SMSC', '190', 'TRFVOMS', 'TRFVOMS_SMSC', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', 'SVK0004296', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'USSD', '190', 'TRFVOMS', 'TRFVOMS_USSD', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004295', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRF', 'WEB', '190', 'TRFVOMS', 'TRFVOMS_WEB', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004294', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'EXTGW', '190', 'INIVOMS', 'INIVOMS_EXTGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004301', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'MAPPGW', '190', 'INIVOMS', 'INIVOMS_MAPPGW', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004302', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'REST', '190', 'INIVOMS', 'INIVOMS_REST', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004306', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'SMSC', '190', 'INIVOMS', 'INIVOMS_SMSC', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', TIMESTAMP '2011-09-19 13:06:33.000000', 'SU0001', 'SVK0004303', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'USSD', '190', 'INIVOMS', 'INIVOMS_USSD', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004304', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,SELECTOR,PIN,LANGUAGE1,LANGUAGE2');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOMSTRFINI', 'WEB', '190', 'INIVOMS', 'INIVOMS_WEB', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004305', NULL, 'GTYPE,MSISDN');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REC_MSG_SEND_ALLOW_C2C', 'C2C Receiver message allow', 'C2CVOUCHER', 'BOOLEAN', 'true', NULL, NULL, 50, 'Service type wise receiver SMS message allow flag for C2C Voucher', 'Y', 'Y', 'C2S', 'Service type wise receiver SMS message allow flag.', TIMESTAMP '2019-12-09 07:21:58.000000', 'ADMIN', TIMESTAMP '2019-12-09 07:22:06.000000', 'SU0001', NULL, 'Y');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVOUCHER', 'C2S', 'PRE', NULL, 'com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherApprovalController', 'Voucher Approval', 'C2C Voucher Approval', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Voucher Approval', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'EXTGW', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004251', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'REST', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', SYSDATE, 'SU0001', 'SVK0004250', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'USSD', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004252', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'WEB', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0004253', NULL, 'GTYPE,MSISDN');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', 'Max Approval level C2C Voucher for Transfer', 'CATPRF', 'INT', '0', 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C_VOUCHER', 'Y', 'Y', 'C2S', 'Max appoval level C2C Voucher', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER', '0', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', 'Max Approval level C2C Voucher for Initiate', 'CATPRF', 'INT', NULL, 0, 3, 50, 'MAX_APPROVAL_LEVEL_C2C_VOUCHER', 'Y', 'Y', 'C2S', 'Max appoval level C2C Voucher', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CVOMSINI', 'Process c2c voms initiate', 'Process c2c voms initiate', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-rest-receiver/c2cvomstrfini', 'N', 'Y', 'C2CVOMSINI');


INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('INIVOMS', 'C2S', 'ALL', 'TYPE MSISDN2 PIN', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherC2CInitiateController', 'C2C Voucher Request', 'C2C Voucher Request', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');


INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('AG', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '1', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('DIST', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '1', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('OS', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '1', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('RET', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '1', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');

INSERT INTO CONTROL_PREFERENCES
(CONTROL_CODE, NETWORK_CODE, PREFERENCE_CODE, VALUE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, TYPE)
VALUES('SE', 'NG', 'MAX_APPROVAL_LEVEL_C2C_VOUCHER_INITIATE', '1', SYSDATE, 'NGLA0000010113', SYSDATE, 'NGLA0000010113', 'CATPRF');



INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CTRFVCRINI', 'Process c2c transfer vcr inititae', 'Process c2c transfer vcr inititae', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-rest-receiver/c2cvomstrf', 'N', 'Y', 'C2CTRFVCRINI');


INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CVINI', 'C2C Transfer Voucher', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CVCTRFAPR1', 'C2C Transfer Voucher Approval 1', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CVCTRFAPR2', 'C2C Transfer Voucher Approval 2', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CVCTRFAPR3', 'C2C Transfer Voucher Approval 3', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('DISTB_CHAN', 'C2CBUYVINI', 'C2C Buy Voucher', 'C2C Transfer', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');




INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CVINI', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CVCTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CVCTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CVCTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CVCTRFAPR1', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CVCTRFAPR1', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CVCTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CVCTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CVCTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CVCTRFAPR2', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CVCTRFAPR2', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CVCTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CVCTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CVCTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CVCTRFAPR3', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CVCTRFAPR3', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('OS', 'C2CBUYVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('DIST', 'C2CBUYVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('RET', 'C2CBUYVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SE', 'C2CBUYVINI', '1');
INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('AG', 'C2CBUYVINI', '1');







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
VALUES('C2C_EMAIL_NOTIFICATION', 'C2C Email Notification', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Email notification to the approvers should be sent or not', 'N', 'Y', 'C2S', 'Email notification to the approver should be sent or not', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_SMS_NOTIFY', 'C2C Notification Message', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'notification for batch C2C transfer', 'Y', 'Y', 'C2C', 'Notification for batch C2C transfer', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'true,false', 'Y');

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('C2CAPPRRECEIVER', 'Process C2C Approval Request', 'Process C2C Approval Request', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rest/c2s-receiver/trfappr', 'N', 'Y', 'C2CAPPRRECEIVER');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TRFINI', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfInitiateController', 'C2C Request', 'C2C Request', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Initiate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TRFAPPR', 'C2S', 'ALL', 'TYPE MSISDN2 AMOUNT PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2CTrfApprovalController', 'C2C Approval', 'C2C Approval', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Approval', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'EXTGW', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK1000904', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'SMSC', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101010', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'EXTGW', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101018', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFINI', 'REST', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101013', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'SMSC', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4102014', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'USSD', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101011', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFAPPR', 'REST', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101014', NULL, NULL);
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFAPPR', 'USSD', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK4101017', NULL, 'TYPE,MSISDN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN');


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

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVOUCHERAPPROVAL', 'SMSC', '190', 'C2CVOUCHER', 'C2C_VOUCHER_APPROVAL', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004254', NULL, 'GTYPE,MSISDN');



alter table channel_voucher_items add  TYPE VARCHAR2(10) DEFAULT 'O2C';
alter table channel_voucher_items add  FIRST_LEVEL_APPROVED_QUANTITY VARCHAR2(22) ;
alter table channel_voucher_items add  SECOND_LEVEL_APPROVED_QUANTITY VARCHAR2(22);
alter table channel_voucher_items add  INITIATED_QUANTITY VARCHAR2(22);
alter table channel_voucher_items add  FROM_USER VARCHAR2(15);
alter table channel_voucher_items add  TO_USER VARCHAR2(15) ;
alter table channel_voucher_items add  MODIFIED_ON TIMESTAMP;

--##########################################################################################################
--##
--##      PreTUPS_v7.18.0 DB Script
--##
--##########################################################################################################

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('C2CPMTYP', 'Payment Type', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'N');


INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('DD', 'Demand Draft', 'C2CPMTYP', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CHQ', 'Cheque', 'C2CPMTYP', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('OTH', 'Others', 'C2CPMTYP', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('CASH', 'Cash', 'C2CPMTYP', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN');>>>>>>> .r70442


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
VALUES('C2CVCRPT_DATEDIFF', 'Date diff for c2c voucher tracking report', 'SYSTEMPRF', 'INT', '40', 1, 15, 50, 'Number of days of the date difference for the c2c voucher transfer tracking report', 'N', 'Y', 'C2C', 'Maximum number of days of the date difference for c2c voucher transfer tracking', SYSDATE, 'ADMIN', SYSDATE 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('O2CVCRPT_DATEDIFF', 'Date diff for o2c voucher tracking report', 'SYSTEMPRF', 'INT', '15', 1, 15, 50, 'Number of days of the date difference for the o2c voucher transfer tracking report', 'N', 'Y', 'O2C', 'Maximum number of days of the date difference for o2c voucher transfer tracking', SYSDATE, 'ADMIN', SYSDATE, 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PAYMENTDETAILSMANDATE_C2C', 'Payment Details Mandatory C2C', 'SYSTEMPRF', 'INT', '0', 0, 1, 50, 'For C2C Payment Details will be mandatory to enter at which level', 'N', 'Y', 'C2C', 'Payment Details Mandatory for C2C', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', '0,1,2,3', 'N');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_ALLOW_CONTENT_TYPE', 'C2C Allowed Content Type', 'SYSTEMPRF', 'STRING', 'pdf,png,jpg', NULL, NULL, 50, 'C2C Allowed Content Type', 'N', 'N', 'C2C', 'C2C Allowed Content Type', SYSDATE, 'ADMIN', SYSDATE , 'ADMIN', NULL, 'Y');


ALTER TABLE CHANNEL_TRANSFERS
ADD APPROVAL_DOC BLOB;

ALTER TABLE CHANNEL_TRANSFERS
ADD APPROVAL_DOC_TYPE VARCHAR2(100);

ALTER TABLE CHANNEL_TRANSFERS
ADD APPROVAL_DOC_FILE_PATH VARCHAR2(500);

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('PAYMENTDETAILSMANDATEVOUCHER_C2C', 'Payment Details Mandatory C2C Voucher', 'SYSTEMPRF', 'INT', '0', 0, 1, 50, 'For C2C Payment Details will be mandatory to enter at which level for voucher', 'N', 'Y', 'C2C', 'Payment Details Mandatory for C2C Voucher', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', '0,1,2,3', 'N');


----------------------------------------------- Bundle Management - DDL - starts ------------------------------------------------------------

CREATE TABLE VOMS_BUNDLE_MASTER
(
  VOMS_BUNDLE_ID        NUMBER(20),
  BUNDLE_NAME           VARCHAR2(50 BYTE),
  BUNDLE_PREFIX         VARCHAR2(5 BYTE),
  RETAIL_PRICE          NUMBER,
  LAST_BUNDLE_SEQUENCE  NUMBER(20),
  CREATED_ON            DATE,
  CREATED_BY            VARCHAR2(50 BYTE),
  MODIFIED_ON           DATE,
  MODIFIED_BY           VARCHAR2(50 BYTE),
  STATUS                VARCHAR2(25 BYTE)
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


CREATE UNIQUE INDEX VOMS_BUNDLE_MASTER_PK ON VOMS_BUNDLE_MASTER
(VOMS_BUNDLE_ID)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


ALTER TABLE VOMS_BUNDLE_MASTER ADD (
  CONSTRAINT VOMS_BUNDLE_MASTER_PK
 PRIMARY KEY
 (VOMS_BUNDLE_ID)
    USING INDEX 
    TABLESPACE PRTP_DATA
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
               ));


CREATE TABLE VOMS_BUNDLE_DETAILS
(
  VOMS_BUNDLE_DETAIL_ID  NUMBER(20),
  VOMS_BUNDLE_ID         NUMBER(20),
  VOMS_BUNDLE_NAME       VARCHAR2(50 BYTE),
  PROFILE_ID             VARCHAR2(5 BYTE),
  QUANTITY               NUMBER,
  CREATED_ON             DATE,
  CREATED_BY             VARCHAR2(50 BYTE),
  MODIFIED_ON            DATE,
  MODIFIED_BY            VARCHAR2(50 BYTE),
  STATUS                 VARCHAR2(25 BYTE)
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


CREATE UNIQUE INDEX VOMS_BUNDLE_DETIAL_ID_PK ON VOMS_BUNDLE_DETAILS
(VOMS_BUNDLE_DETAIL_ID)
LOGGING
TABLESPACE PRTP_DATA
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL;


ALTER TABLE VOMS_BUNDLE_DETAILS ADD (
  CONSTRAINT VOMS_BUNDLE_DETIAL_ID_PK
 PRIMARY KEY
 (VOMS_BUNDLE_DETAIL_ID)
    USING INDEX 
    TABLESPACE PRTP_DATA
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
               ));

ALTER TABLE VOMS_BUNDLE_DETAILS ADD (
  FOREIGN KEY (VOMS_BUNDLE_ID) 
 REFERENCES VOMS_BUNDLE_MASTER (VOMS_BUNDLE_ID));
 
 
 ALTER TABLE VOMS_VOUCHERS
	ADD	( BUNDLE_ID NUMBER(20) , MASTER_SERIAL_NO NUMBER(20) )

ALTER TABLE CHANNEL_VOUCHER_ITEMS
		ADD ( BUNDLE_ID NUMBER(20) , REMARKS VARCHAR2(100) )

    CREATE TABLE MO_SO_NUMBER
(
  SOMOREFID          VARCHAR2(50 BYTE),
  FILENAME           VARCHAR2(500 BYTE),
  PROCESSINGDATE     DATE,
  FILEREFERENCE      VARCHAR2(50 BYTE),
  MONUMBER           VARCHAR2(50 BYTE),
  MOLINENUMBER       VARCHAR2(50 BYTE),
  DONUMBER           VARCHAR2(50 BYTE),
  WMSREFERENCE       VARCHAR2(50 BYTE),
  ORGCODE            VARCHAR2(50 BYTE),
  SUBINVENTORY       VARCHAR2(50 BYTE),
  ITEMCODE           VARCHAR2(50 BYTE),
  QUANTITY           VARCHAR2(50 BYTE),
  UOM                VARCHAR2(50 BYTE),
  FROMSERIAL_NUMBER  NUMBER,
  TOSERIAL_NUMBER    NUMBER,
  SEARCHKEY          NUMBER,
  ACTUALFROMNO       NUMBER,
  ACTUALTONO         NUMBER
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
	MODIFY LOOKUP_CODE VARCHAR2(15);
	
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
    'N', 'C2S', 'Ext contains alphaNum with specials(!@#$%*-)', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'N');


 
----------------------------------------------- Bundle Management - DML - ends ------------------------------------------------------------
--##########################################################################################################
--##
--##      PreTUPS_v7.19.0 DB Script
--##
--##########################################################################################################

 ALTER TABLE VOMS_VOUCHERS ADD C2C_TRANSFER_DATE TIMESTAMP;
ALTER TABLE VOMS_VOUCHERS ADD C2C_TRANSFER_ID VARCHAR2(20);

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
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVTAPLST', 'C2S', 'PRE', NULL, 'com.restapi.channel.transfer.channelvoucherapproval.ChannelToChannelVoucherApprovalList', 'Voucher Approval List', 'C2C Voucher Approval List', 'Y', TIMESTAMP '2019-12-04 09:01:38.000000', 'ADMIN', TIMESTAMP '2019-12-04 09:01:50.000000', 'ADMIN', 'C2C Voucher Approval List', 'Y', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVCRAPPLIST', 'REST', '190', 'C2CVTAPLST', 'C2C_VOUCHER_APPROVAL_LIST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', TIMESTAMP '2019-12-04 09:11:56.000000', 'SU0001', 'SVK0004257', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVCRAPPLIST', 'MAPPGW', '190', 'C2CVTAPLST', 'C2C_VOUCHER_APPROVAL_LIST', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 09:11:33.000000', 'SU0001', TIMESTAMP '2019-12-04 09:11:56.000000', 'SU0001', 'SVK410259', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_CONS', 'Date difference for reports for admin', 'SYSTEMPRF', 'INT', '5', 1, 30, 50, 'Number of days of the date difference for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', sysdate, 'ADMIN', sysdate, 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_MAX_DATEDIFF_USER_CONS', 'Date difference for reports for user', 'SYSTEMPRF', 'INT', '10', 1, 30, 50, 'Number of days of the date difference for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', sysdate, 'ADMIN', sysdate, 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_AVAIL', 'Date diff for availability report for admin', 'SYSTEMPRF', 'INT', '10', 1, 30, 50, 'Number of days of the date difference for the availability report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', sysdate, 'ADMIN', sysdate, 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_MAX_DATEDIFF_ADMIN_NLEVEL', 'Date diff for reports for admin for Nlevel', 'SYSTEMPRF', 'INT', '5', 1, 30, 50, 'Number of days of the date n level for the consumption report', 'N', 'Y', 'C2S', 'Maximum number of days of the date differnece for reports', sysdate, 'ADMIN', sysdate, 'ADMIN', NULL, 'Y');

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
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVWTRFVC', 'C2S', 'ALL', 'TYPE MSISDN PIN TRANSFERID TRANSFERTYPE NETWORKCODE NETWORKCODEFOR', 'com.btsl.pretups.channel.transfer.requesthandler.C2CVoucherTransferDetailsController', 'C2C Request', 'C2C Request', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'C2C View Transfer Voucher', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,IMEI,PIN,TRANSFERID,TRANSFERTYPE,NETWORKCODE,NETWORKCODEFOR,LANGUAGE1', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
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
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('MIN_LAST_DAYS_CG', 'Minimum Days for card group set version', 'SYSTEMPRF', 'INT', '1', 1, 365, 365, 'Minimum Days for card group set version', 'N', 'Y', 'C2S', 'Minimum Days for card group set version', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-10-25 15:20:27.000000', 'SU0001', NULL, 'Y');
INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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
VALUES('C2CVTYPE', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherInfoServices', 'Voucher Type Info', 'Voucher Type Info', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Voucher Type Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVSEG', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherSegmentInfo', 'Voucher Segment Info', 'Voucher Segment Info', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Voucher Segment Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');
INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVDEN', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherDenominationInfo', 'Voucher Segment Info', 'Voucher Segment Info', 'Y', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', 'C2C Voucher Segment Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVTYPE', 'MAPPGW', '190', 'C2CVTYPE', 'C2CVTYPE', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK9001016', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVSEG', 'MAPPGW', '190', 'C2CVSEG', 'C2CVSEG', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK9001017', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN,VTYPE');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVDEN', 'MAPPGW', '190', 'C2CVDEN', 'C2CVDEN', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK9001018', NULL, 'TYPE,MSISDN,IMEI,PIN,MHASH,TOKEN,VTYPE,VSEG');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_ALLOWED_VINFO', 'User allowed for voucher API', 'SYSTEMPRF', 'STRING', 'SUBCU,BCU,DIST,SE,SUADM,SUNADM,NWADM,SSADM,AG,RET', 0, 9999, 50, 'User allowed for voucher info services', 'N', 'Y', 'C2S', 'User allowed for voucher type APIs', SYSDATE, 'ADMIN', SYSDATE, 'ADMIN', NULL, 'N');

--##########################################################################################################
--##
--##      PreTUPS_v7.21.0 DB Script
--##
--##########################################################################################################

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('USRDETAILS', 'C2S', 'ALL', 'TYPE MSISDN2 PIN', 'com.btsl.pretups.user.requesthandler.ChannelUserDetailsController', 'User Details', 'User details', 'Y', TIMESTAMP '2020-02-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-02-25 00:00:00.000000', 'ADMIN', 'User Details', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,PIN,IMEI,LANGUAGE1,LANGUAGE2', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USRDETAILS', 'MAPPGW', '190', 'USRDETAILS', 'USRDETAILS', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-02-25 00:00:00.000000', 'SU0001', TIMESTAMP '2020-02-25 00:00:00.000000', 'SU0001', 'SVK3001170', NULL, 'TYPE,MSISDN,MSISDN2,PIN,IMEI,LANGUAGE1');

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
VALUES('C2SPRODTXNDETAILS', 'EXTGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000333', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'MAPPGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000334', NULL, 'TYPE,MSISDN,IMEI,MHASH,TOKEN,FROMDATE,TODATE,SERVICETYPE');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'USSD', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000335', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'WEB', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000336', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SPRODTXNDETAILS', 'REST', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000337', NULL, 'GTYPE,MSISDN');


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
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2CVINFO', 'C2S', 'PRE', 'TYPE PIN', 'com.restapi.user.service.VoucherInfo', 'Voucher Info', 'Voucher Info', 'Y', TIMESTAMP '2020-03-11 18:33:49.000000', 'ADMIN', TIMESTAMP '2020-03-11 18:33:49.000000', 'ADMIN', 'C2C Voucher Info', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', 'com.btsl.pretups.scheduletopup.process.C2CVoucherApprovalParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('C2SENQCOM', 'C2S', 'NA', 'TYPE MSISDN2 EXTTXNNUMBER PIN', 'com.btsl.pretups.channel.transfer.requesthandler.CommissionCalculatorController', 'C2S Enquiry Request', 'Enquiry request', 'N', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-12 00:00:00.000000', 'ADMIN', 'Enquiry request', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CVINFO', 'MAPPGW', '190', 'C2CVINFO', 'C2CVINFO', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-11 18:36:54.000000', 'SU0001', TIMESTAMP '2020-03-11 18:36:54.000000', 'SU0001', 'SVK9001019', NULL, 'MSISDN,IMEI,MHASH,TOKEN');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'EXTGW', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001021', NULL, 'GTYPE,MSISDN');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'MAPPGW', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001022', NULL, 'MSISDN,IMEI,PIN,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('COMINCOME', 'REST', '190', 'C2SENQCOM', 'C2S_COMMISION_INCOME', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', TIMESTAMP '2020-03-13 15:52:44.000000', 'SU0001', 'SVK9001020', NULL, 'GTYPE,MSISDN');



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
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('PASBDET', 'C2S', 'ALL', 'TYPE MSISDN PIN', 'com.btsl.pretups.channel.transfer.requesthandler.PassbookDetailsController', 'Passbook Detail', 'User Passbook Detail', 'Y', TIMESTAMP '2005-07-15 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-15 00:00:00.000000', 'ADMIN', 'Passbook Detail', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

ALTER TABLE VOMS_VOUCHERS_SNIFFER
	ADD	( BUNDLE_ID NUMBER(20) , MASTER_SERIAL_NO NUMBER(20) );
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD C2C_TRANSFER_DATE TIMESTAMP;
ALTER TABLE VOMS_VOUCHERS_SNIFFER ADD C2C_TRANSFER_ID VARCHAR2(20);
UPDATE service_type SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN' , REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,PIN,LANGUAGE1'
WHERE SERVICE_TYPE = 'TRFAPPR';
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2CTRFAPPR', 'MAPPGW', '190', 'TRFAPPR', 'C2C Approval', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', TIMESTAMP '2019-12-04 00:00:00.000000', 'SU0001', 'SVK0004299', NULL, 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');

ALTER TABLE PRETUPS_TRUNK_TEST.USER_OTP ADD OTP_COUNT NUMBER(3);
ALTER TABLE PRETUPS_TRUNK_TEST.USER_OTP ADD BARRED_DATE TIMESTAMP;

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
VALUES('C2SNPRODTXNDETAILS', 'EXTGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000338', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'MAPPGW', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000339', NULL, 'TYPE,MSISDN,IMEI,MHASH,TOKEN,FROMDATE,TODATE,SERVICETYPE,TOPPRODUCTS,NUMBEROFPRODORDENO');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'USSD', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000340', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'WEB', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000341', NULL, 'GTYPE,MSISDN');
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('C2SNPRODTXNDETAILS', 'REST', '190', 'C2STRANS', 'Recharge Count', 'Y', NULL, NULL, NULL, 'Y', SYSDATE, 'SU0001', SYSDATE, 'SU0001', 'SVK0000342', NULL, 'GTYPE,MSISDN');


INSERT INTO  SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('USRINCVIEW', 'C2S', 'POST', 'KEYWORD PIN', 'com.btsl.pretups.channel.transfer.requesthandler.TotalUserIncomeDetailsViewController', 'Total Income detailed VIEW', 'Total Income detailed VIEW', 'Y', TIMESTAMP '2020-03-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-03-25 00:00:00.000000', 'ADMIN', 'Total Income detailed VIEW', 'Y', 'N', 'N', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USRINCVIEW', 'REST', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009164', NULL, 'GTYPE,MSISDN');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USRINCVIEW', 'MAPPGW', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009169', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,PIN,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('USRINCVIEW', 'EXTGW', '190', 'USRINCVIEW', 'Total Income detailed VIEW', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', TIMESTAMP '2020-03-25 08:28:57.000000', 'SU0001', 'SVK0009969', NULL, 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,PIN,EXTNWCODE');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
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

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('DOMAINCAT', 'C2S', 'PRE', 'TYPE PIN', 'com.btsl.pretups.channel.transfer.requesthandler.GetDomainCategoryController', 'Get Domain And Category', 'Get Domain And Category', 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-04-03 00:00:00.000000', 'ADMIN', 'Get Domain And Category', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('GETDOMAINCATEGORY', 'REST', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004341', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('GETDOMAINCATEGORY', 'MAPPGW', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004342', NULL, 'TYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('GETDOMAINCATEGORY', 'USSD', '190', 'DOMAINCAT', 'GET_DOMAIN_CATEGORY', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', TIMESTAMP '2020-04-03 00:00:00.000000', 'SU0001', 'SVK0004345', NULL, 'GTYPE,MSISDN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
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
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('TXNCALVIEW', 'C2S', 'ALL', 'TYPE MSISDN PRODUCTS IMEI PIN LANGUAGE1', 'com.btsl.pretups.channel.transfer.requesthandler.TransactionAPICalculationController', 'Channel Voucher Enquiry', 'TRANSACTION API FOR CALCULATION', 'Y', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', TIMESTAMP '2019-09-17 00:00:00.000000', 'ADMIN', 'TRANSACTION API FOR CALCULATION', 'N', 'Y', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,PRODUCTS,IMEI,PIN,LANGUAGE1', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TXNCALVIEW', 'MAPPGW', '190', 'TXNCALVIEW', 'TXN API FOR CALCULATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', 'SVK4109920', NULL, 'TYPE,MSISDN,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TXNCALVIEW', 'REST', '190', 'TXNCALVIEW', 'TXN API FOR CALCULATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', TIMESTAMP '2019-11-21 01:56:55.000000', 'SU0001', 'SVK4109020', NULL, 'TYPE,MSISDN,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');




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



CREATE GLOBAL TEMPORARY TABLE TEMP_FND_MY_RET_STG
( "RECEIVER_MSISDN" VARCHAR2(20 BYTE), 
"SENDER_MSISDN" VARCHAR2(20 BYTE), 
"TRANSFER_DATE" DATE, 
"NETWORK_CODE" VARCHAR2(10 BYTE)
) ON COMMIT PRESERVE ROWS ;
CREATE INDEX "TEMP_FND_MY_RET_STG_INDEX1" ON "TEMP_FND_MY_RET_STG" ("RECEIVER_MSISDN");

CREATE TABLE SUBS_RETAILER_DETAILS (
	SUBSCRIBER_MSISDN VARCHAR2(15) NOT NULL,
	RETAILER1_MSISDN VARCHAR2(15),
	RETAILER1_FIRST_NAME VARCHAR2(40),
	RETAILER1_LAST_NAME VARCHAR2(40),
	RETAILER2_MSISDN VARCHAR2(15),
	RETAILER2_FIRST_NAME VARCHAR2(40),
	RETAILER2_LAST_NAME VARCHAR2(40),
	RETAILER3_MSISDN VARCHAR2(15),
	RETAILER3_FIRST_NAME VARCHAR2(40),
	RETAILER3_LAST_NAME VARCHAR2(40),
	RETAILER4_MSISDN VARCHAR2(15),
	RETAILER4_FIRST_NAME VARCHAR2(40),
	RETAILER4_LAST_NAME VARCHAR2(40),
	RETAILER5_MSISDN VARCHAR2(15),
	RETAILER5_FIRST_NAME VARCHAR2(40),
	RETAILER5_LAST_NAME VARCHAR2(40),
	STATUS VARCHAR2(2) NOT NULL,
	LAST_UPDATED_ON DATE,
	CREATED_ON DATE,
	NETWORK_CODE VARCHAR2(2),
	BATCH_NO NUMBER(4,0),
	CUR_RET_COUNT NUMBER(22,0)
) ;
CREATE UNIQUE INDEX SUBS_RET_DET_INDEX1 ON SUBS_RETAILER_DETAILS (SUBSCRIBER_MSISDN);
CREATE INDEX SUBS_RET_DET_INDEX2 ON SUBS_RETAILER_DETAILS (BATHC_NO);
CREATE INDEX SUBS_RET_DET_INDEX3 ON SUBS_RETAILER_DETAILS (CUR_RET_COUNT);


INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('FND_MY_RET_SUMM_STG', NULL, '', NULL, NULL, NULL, NULL, 'Find My Retaier Staging Table Status', 'NG', NULL);

INSERT INTO PROCESS_STATUS
(PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
VALUES('FND_MY_RET_SUMM_STGF', NULL, '', NULL, NULL, NULL, NULL, 'Find My Retaier Staging Table Status', 'NG', NULL);




-- Procedure--

create or replace PROCEDURE "FIN_MY_RETAILER_SUMMARY_CREATOR" (

BCKORFWD  IN VARCHAR2
)
IS

  PROCEXCEPTION                 EXCEPTION;
  count_FIND_MY_RET_SUMMARY_STG VARCHAR2(20);
  count_FIND_MY_RET_SUMMARY_STGF VARCHAR2(20);
  
  v_receiver_msisdn             VARCHAR2(20);
  v_sender_msisdn               VARCHAR2(20);
  v_network_code                VARCHAR2(10);
  v_first_name                  VARCHAR2(2000);
  v_last_name                   VARCHAR2(2000);
  v_msisdn                      VARCHAR2(20);
  t_receiver_msisdn             VARCHAR2(20) := NULL;
  t_sender_msisdn               VARCHAR2(20) := NULL;
  t_retailer1_fname             VARCHAR2(20) := NULL;
  t_retailer1_lname             VARCHAR2(20) := NULL;
  t_sender1_msisdn              VARCHAR2(20) := NULL;
  t_retailer2_fname             VARCHAR2(20) := NULL;
  t_retailer2_lname             VARCHAR2(20) := NULL;
  t_sender2_msisdn              VARCHAR2(20) := NULL;
  t_retailer3_fname             VARCHAR2(20) := NULL;
  t_retailer3_lname             VARCHAR2(20) := NULL;
  t_sender3_msisdn              VARCHAR2(20) := NULL;
  t_retailer4_fname             VARCHAR2(20) := NULL;
  t_retailer4_lname             VARCHAR2(20) := NULL;
  t_sender4_msisdn              VARCHAR2(20) := NULL;
  t_retailer5_fname             VARCHAR2(20) := NULL;
  t_retailer5_lname             VARCHAR2(20) := NULL;
  t_sender5_msisdn              VARCHAR2(20) := NULL;
  v_yesterday_date               DATE := SYSDATE-1;
  v_previous_date               DATE := NULL;
  v_next_date               DATE := NULL;
  v_cur_ret_count               NUMBER := 0;
  v_CUR_RET_COUNT_temp          NUMBER :=0;  
  v_commit_counter              NUMBER :=0;
  
  v_count_inserted              NUMBER:=0;
  v_count_inserted_counter              NUMBER:=0;
  
  v_count_updated              NUMBER:=0;
  v_count_COMMIT             NUMBER:=0;
  
  v_start_time               TIMESTAMP;
  v_end_time               TIMESTAMP;
  v_batch_no               NUMBER; 
  V_BATCH_SIZE             NUMBER := 50000;
  
  v_inserted              BOOLEAN := FALSE;
  
  CURSOR CUR_TEMP_FND_MY_RET_STG
  IS
    SELECT DISTINCT receiver_msisdn,
      sender_msisdn, network_code
    FROM
      (SELECT receiver_msisdn,
        sender_msisdn,network_code,
        row_number() over ( PARTITION BY receiver_msisdn ORDER BY TRANSFER_DATE DESC) AS receiver_rank
      FROM TEMP_FND_MY_RET_STG
      )
  WHERE receiver_rank < 20
  ORDER BY receiver_msisdn ;


 TYPE HASH_MAP
 IS
  TABLE OF VARCHAR2(30) INDEX BY VARCHAR2(30);
  
  
  
  l_FIRST_NAME_MAP HASH_MAP;
  l_LAST_NAME_MAP HASH_MAP;
  


 TYPE LAST_NAME_MAP
  IS
   TABLE OF VARCHAR2(30) INDEX BY VARCHAR2(30);
   
   
   

 CURSOR FNC
  IS
    SELECT firstname, MSISDN FROM USERS;


 CURSOR LNC
  IS
    SELECT lastname, MSISDN FROM USERS;


 BEGIN
 
  v_start_time := SYSTIMESTAMP;
 
  SELECT MAX(batch_no) INTO v_batch_no from SUBS_RETAILER_DETAILS;
 
  IF v_batch_no IS NULL THEN
   v_batch_no := 1;
   
   ELSE 
   v_batch_no := v_batch_no + 1;
  END IF;
  -- fetch SCHEDULER_STATUS - NULL indiactes that it is the very first time it is being executed.
 
  SELECT SCHEDULER_STATUS
  INTO count_FIND_MY_RET_SUMMARY_STG
  FROM PROCESS_STATUS
  WHERE PROCESS_ID                    = 'FND_MY_RET_SUMM_STG';
 
 
 
 
  SELECT SCHEDULER_STATUS
  INTO count_FIND_MY_RET_SUMMARY_STGF
  FROM PROCESS_STATUS
  WHERE PROCESS_ID                    = 'FND_MY_RET_SUMM_STGF';
  
  
 -- TRUNCATE STAGING TABLE
--  EXECUTE IMMEDIATE 'TRUNCATE TABLE FIND_MY_RET_SUMMARY_STG';
 
 DBMS_OUTPUT.PUT_LINE('FIND_MY_RET_SUMMARY_STG  Truncated!');
  
  
  
  
  IF BCKORFWD = 'FWD' AND (count_FIND_MY_RET_SUMMARY_STGF IS NULL ) THEN
  
  DBMS_OUTPUT.PUT_LINE('Inserting Into FIND_MY_RET_SUMMARY_STG');
    INSERT
    INTO TEMP_FND_MY_RET_STG
      (
        receiver_msisdn,
        sender_msisdn,
        TRANSFER_DATE,
        NETWORK_CODE
      )
    SELECT DISTINCT receiver_msisdn,
      sender_msisdn,
      transfer_date, --NO TRUNC REQUIRED
      network_code
    --FROM c2s_transfers WHERE TRANSFER_DATE >  (v_yesterday_date - 1 ) AND TRANSFER_DATE <  (v_yesterday_date + 1 ) ;
    FROM c2s_transfers WHERE TRANSFER_DATE =  TRUNC(v_yesterday_date) ;
    
    
    UPDATE PROCESS_STATUS
    SET EXECUTED_UPTO = v_yesterday_date , SCHEDULER_STATUS ='P'
    WHERE PROCESS_ID  ='FND_MY_RET_SUMM_STGF' ;
    
    
    
  -- NULL - It is the very first time it is being executed - in this case Full data of c2s_transfers will be INSERTED INTO FIND_MY_RET_SUMMARY_STG
  ELSIF BCKORFWD = 'FWD' AND (count_FIND_MY_RET_SUMMARY_STGF IS NOT NULL ) THEN
  
  


    SELECT (EXECUTED_UPTO + 1)  INTO v_next_date
      FROM PROCESS_STATUS
      WHERE PROCESS_ID='FND_MY_RET_SUMM_STGF';




    INSERT
    INTO TEMP_FND_MY_RET_STG
      (
        receiver_msisdn,
        sender_msisdn,
        TRANSFER_DATE,
        NETWORK_CODE
      )
    SELECT DISTINCT receiver_msisdn,
      sender_msisdn,
      TRUNC(transfer_date),
      NETWORK_CODE
    FROM c2s_transfers
    --WHERE TRUNC(TRANSFER_DATE) = TRUNC(v_previous_date);
   -- WHERE TRANSFER_DATE >  (v_previous_date - 1 ) AND TRANSFER_DATE <  (v_previous_date + 1 ) ;  
    WHERE TRANSFER_DATE = TRUNC(v_next_date) ;  
       
      
      
    IF TRUNC(v_next_date) <= TRUNC(SYSDATE) THEN
    
    UPDATE PROCESS_STATUS
    SET EXECUTED_UPTO = v_next_date, SCHEDULER_STATUS ='P'
    WHERE PROCESS_ID  ='FND_MY_RET_SUMM_STGF' ;
    
    END IF;
    
    
  
  ELSIF ( count_FIND_MY_RET_SUMMARY_STG IS NULL ) THEN
  
    INSERT
    INTO TEMP_FND_MY_RET_STG
      (
        receiver_msisdn,
        sender_msisdn,
        TRANSFER_DATE,
        NETWORK_CODE
      )
    SELECT DISTINCT receiver_msisdn,
      sender_msisdn,
      transfer_date, --NO TRUNC REQUIRED
      NETWORK_CODE
    --FROM c2s_transfers WHERE TRUNC(TRANSFER_DATE) = TRUNC(v_yesterday_date) ;
    --FROM c2s_transfers WHERE TRANSFER_DATE >  (v_yesterday_date - 1 ) AND TRANSFER_DATE <  (v_yesterday_date + 1 ) ;
    FROM c2s_transfers WHERE TRANSFER_DATE =  TRUNC(v_yesterday_date) ;
    
    
    
    UPDATE PROCESS_STATUS
    SET EXECUTED_UPTO = v_yesterday_date , SCHEDULER_STATUS ='P'
    WHERE PROCESS_ID  ='FND_MY_RET_SUMM_STG' ;
    
    
  ELSE
    -- fetch Newly INSERTED DATA of c2s_transfers and INSERT INTO  FIND_MY_RET_SUMMARY_STG Table 
    
    SELECT (EXECUTED_UPTO - 1)  INTO v_previous_date
      FROM PROCESS_STATUS
      WHERE PROCESS_ID='FND_MY_RET_SUMM_STG';
      
    
      
    INSERT
    INTO TEMP_FND_MY_RET_STG
      (
        receiver_msisdn,
        sender_msisdn,
        TRANSFER_DATE,
        NETWORK_CODE
      )
    SELECT DISTINCT receiver_msisdn,
      sender_msisdn,
      TRUNC(transfer_date),
      NETWORK_CODE
    FROM c2s_transfers
    --WHERE TRUNC(TRANSFER_DATE) = TRUNC(v_previous_date);
   -- WHERE TRANSFER_DATE >  (v_previous_date - 1 ) AND TRANSFER_DATE <  (v_previous_date + 1 ) ;  
    WHERE TRANSFER_DATE = TRUNC(v_previous_date) ;  
       
      
      
    UPDATE PROCESS_STATUS
    SET EXECUTED_UPTO = v_previous_date, SCHEDULER_STATUS ='P'
    WHERE PROCESS_ID  ='FND_MY_RET_SUMM_STG' ;
    
    
  END IF;
  
  COMMIT;
  v_count_COMMIT := v_count_COMMIT +1;
  
  DBMS_OUTPUT.PUT_LINE('FIND_MY_RET_SUMMARY_STG  Loaded!');
  -- Prepare FirstName Map - l_FIRST_NAME_MAP, Key - Retailer's MSISDN
  OPEN FNC;
  LOOP
    FETCH FNC INTO v_first_name, v_msisdn;
    l_FIRST_NAME_MAP(v_msisdn) := v_first_name;
    EXIT
  WHEN FNC%NOTFOUND;
  END LOOP;
  CLOSE FNC;
  
  
  -- Prepare LastName Map - l_LAST_NAME_MAP, Key - Retailer's MSISDN
  OPEN LNC;
  LOOP
    FETCH LNC INTO v_last_name, v_msisdn;
    l_LAST_NAME_MAP(v_msisdn) := v_last_name;
    EXIT
  WHEN LNC%NOTFOUND;
  END LOOP;
  CLOSE LNC;
  
  
  
  
  -- Iterate over the subscriber wise result
  -- Below Query results SUBSCRIBER_MSISDN, RETAILER_MSISDN
  
  OPEN CUR_TEMP_FND_MY_RET_STG;
  LOOP
    FETCH CUR_TEMP_FND_MY_RET_STG INTO v_receiver_msisdn, v_sender_msisdn, v_network_code;
    EXIT
    WHEN CUR_TEMP_FND_MY_RET_STG%NOTFOUND;
  
  ---DBMS_OUTPUT.PUT_LINE(v_receiver_msisdn||'  '||v_sender_msisdn||'   ' ||v_network_code);
  
    -- Very First Time t_receiver_msisdn will be NULL
    IF t_receiver_msisdn IS NULL THEN
      t_receiver_msisdn  := v_receiver_msisdn;
      IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
        t_retailer1_fname  := l_FIRST_NAME_MAP(v_sender_msisdn);
        t_retailer1_lname  := l_LAST_NAME_MAP(v_sender_msisdn);
      END IF;
      t_sender1_msisdn   := v_sender_msisdn;
      v_cur_ret_count := v_cur_ret_count+1;
    ELSE
    
      -- It indicates v_receiver_msisdn (SUBSCRIBER_MSISDN) is now different, so we can insert record into SUBS_RETAILER_DETAILS
      IF (t_receiver_msisdn <> v_receiver_msisdn ) THEN
        --DBMS_OUTPUT.PUT_LINE('INSERTING t_receiver_msisdn' || t_receiver_msisdn||'  v_receiver_msisdn  '||v_receiver_msisdn);
        -- insert data or UPDATE 
       v_inserted := TRUE; 
       
       SELECT (SELECT CUR_RET_COUNT  FROM SUBS_RETAILER_DETAILS WHERE SUBSCRIBER_MSISDN = TO_CHAR(t_receiver_msisdn) ) INTO v_CUR_RET_COUNT_temp  from DUAL;
        
        DBMS_OUTPUT.PUT_LINE(t_receiver_msisdn||'  v_CUR_RET_COUNT_temp '||v_CUR_RET_COUNT_temp);
        
        IF v_CUR_RET_COUNT_temp IS NULL THEN
        
        INSERT
        INTO SUBS_RETAILER_DETAILS
          (
            SUBSCRIBER_MSISDN,
            RETAILER1_MSISDN,
            RETAILER1_FIRST_NAME,
            RETAILER1_LAST_NAME,
            RETAILER2_MSISDN,
            RETAILER2_FIRST_NAME,
            RETAILER2_LAST_NAME,
            RETAILER3_MSISDN,
            RETAILER3_FIRST_NAME,
            RETAILER3_LAST_NAME,
            RETAILER4_MSISDN,
            RETAILER4_FIRST_NAME,
            RETAILER4_LAST_NAME,
            RETAILER5_MSISDN,
            RETAILER5_FIRST_NAME,
            RETAILER5_LAST_NAME,
            STATUS,
            CREATED_ON,
            LAST_UPDATED_ON,
            CUR_RET_COUNT,
            BATCH_NO,
            NETWORK_CODE
          )
          VALUES
          (
            t_receiver_msisdn,
            t_sender1_msisdn,
            t_retailer1_fname,
            t_retailer1_lname,
            t_sender2_msisdn,
            t_retailer2_fname,
            t_retailer2_lname,
            t_sender3_msisdn,
            t_retailer3_fname,
            t_retailer3_lname,
            t_sender4_msisdn,
            t_retailer4_fname,
            t_retailer4_lname,
            t_sender5_msisdn,
            t_retailer5_fname,
            t_retailer5_lname,
            'NW',
            SYSDATE,
            SYSDATE,
            v_cur_ret_count,
            v_batch_no,
            v_network_code
          );
          
          v_count_inserted := v_count_inserted + 1;
          v_count_inserted_counter := v_count_inserted_counter + 1;
          
          IF v_count_inserted_counter > V_BATCH_SIZE THEN
             v_batch_no :=  v_batch_no +1;
             v_count_inserted_counter := 0;
          
          END IF;
          
          
        ELSIF v_CUR_RET_COUNT_temp < 5 THEN
        
        DBMS_OUTPUT.PUT_LINE(t_receiver_msisdn||'  v_CUR_RET_COUNT_temp '||v_CUR_RET_COUNT_temp);
            IF v_CUR_RET_COUNT_temp < 2 THEN        
        
        

                                    if t_sender4_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = t_sender4_msisdn , RETAILER4_FIRST_NAME = t_retailer4_fname , RETAILER4_LAST_NAME = t_retailer4_lname,
                                      RETAILER5_MSISDN = RETAILER1_MSISDN, RETAILER5_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER1_LAST_NAME,
                                      CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 

                                    elsif t_sender3_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = RETAILER1_MSISDN , RETAILER4_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER2_MSISDN, RETAILER5_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER2_LAST_NAME,
                                      CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    
                                    elsif t_sender2_msisdn IS NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                                      CUR_RET_COUNT = 3 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    ELSE
                                    
                                    
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER3_MSISDN = RETAILER3_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                                      CUR_RET_COUNT = 2 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                                      
                                    END IF;
            ELSIF v_CUR_RET_COUNT_temp < 3 THEN
            
           
                                    if t_sender3_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = RETAILER1_MSISDN , RETAILER4_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER2_MSISDN, RETAILER5_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER2_LAST_NAME,
                                      CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    
                                    elsif t_sender2_msisdn IS NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                                      CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    ELSE
                                    
                                    
									
                                        UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                                     
                                      CUR_RET_COUNT = 3 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                                      
                                    END IF;
                            

            ELSIF v_CUR_RET_COUNT_temp  < 4 THEN
            
                        
                        if t_sender2_msisdn IS NOT NULL THEN
                        
                        
                          UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                          RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                          CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                        
                          ELSE
                        
                        
                        
                          UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                          CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                        
                        
                        END IF;
                
            
            ELSE
            
                        DBMS_OUTPUT.PUT_LINE(t_receiver_msisdn||'  v_CUR_RET_COUNT_temp else '||v_CUR_RET_COUNT_temp);
                        
                        UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                          CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 

            END IF;

           v_count_updated := v_count_updated + 1;
        END IF;
        
        ---v_commit_counter := v_commit_counter+1;
        
        IF v_commit_counter > 200 THEN
        COMMIT;
        v_count_COMMIT := v_count_COMMIT +1;
        END IF;
        
        -- re initialize variables
        t_receiver_msisdn := v_receiver_msisdn;
        t_sender1_msisdn  := v_sender_msisdn;
        t_sender2_msisdn  := NULL;
        t_sender3_msisdn  := NULL;
        t_sender4_msisdn  := NULL;
        t_sender5_msisdn  := NULL;
        IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
          t_retailer1_fname := l_FIRST_NAME_MAP(v_sender_msisdn);
        END IF;
        t_retailer2_fname := NULL;
        t_retailer3_fname := NULL;
        t_retailer4_fname := NULL;
        t_retailer5_fname := NULL;
        IF l_LAST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
          t_retailer1_lname := l_LAST_NAME_MAP(v_sender_msisdn);
        END IF;
        t_retailer2_lname := NULL;
        t_retailer3_lname := NULL;
        t_retailer4_lname := NULL;
        t_retailer5_lname := NULL;
      
        v_cur_ret_count := 1;  
      ELSE
      
       -- It indicates Subscriber - receiver combination is same as in previous iteration.
       -- If in previous iteration we captured RETAILER(N) information, then in this case it would be RETAILER(N+1) information
        
        v_inserted := FALSE;
        
        DBMS_OUTPUT.PUT_LINE('v_receiver_msisdn' || v_receiver_msisdn);
        
        IF (t_retailer1_fname    IS NULL ) THEN
          IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
            t_retailer1_fname      := l_FIRST_NAME_MAP(v_sender_msisdn);
            t_retailer1_lname      := l_LAST_NAME_MAP(v_sender_msisdn);
            t_sender1_msisdn       := v_sender_msisdn;
          END IF;
          v_cur_ret_count := 1;
        ELSIF (t_retailer2_fname IS NULL ) THEN
          IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
            t_retailer2_fname      := l_FIRST_NAME_MAP(v_sender_msisdn);
            t_retailer2_lname      := l_LAST_NAME_MAP(v_sender_msisdn);
            t_sender2_msisdn       := v_sender_msisdn;
          END IF;
          v_cur_ret_count := 2;
        ELSIF (t_retailer3_fname IS NULL ) THEN
          IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
            t_retailer3_fname      := l_FIRST_NAME_MAP(v_sender_msisdn);
            t_retailer3_lname      := l_LAST_NAME_MAP(v_sender_msisdn);
            t_sender3_msisdn       := v_sender_msisdn;
          END IF;
          
          v_cur_ret_count := 3;
        ELSIF (t_retailer4_fname IS NULL ) THEN
          IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
            t_retailer4_fname      := l_FIRST_NAME_MAP(v_sender_msisdn);
            t_retailer4_lname      := l_LAST_NAME_MAP(v_sender_msisdn);
            t_sender4_msisdn       := v_sender_msisdn;
          END IF;
          
          v_cur_ret_count := 4;
        ELSIF (t_retailer5_fname IS NULL ) THEN
          IF l_FIRST_NAME_MAP.EXISTS(v_sender_msisdn) = TRUE THEN
            t_retailer5_fname      := l_FIRST_NAME_MAP(v_sender_msisdn);
            t_retailer5_lname      := l_LAST_NAME_MAP(v_sender_msisdn);
            t_sender5_msisdn       := v_sender_msisdn;
          END IF;
          v_cur_ret_count := 5;
        END IF;
        
      END IF; -- End of t_receiver_msisdn <> v_receiver_msisdn 
    END IF;
  END LOOP;
  CLOSE CUR_TEMP_FND_MY_RET_STG;
  
  
  
  
  DBMS_OUTPUT.PUT_LINE('v_receiver_msisdn' || v_receiver_msisdn );
  
  
  --- Check for Last MSISDN
  IF v_inserted = FALSE THEN
  DBMS_OUTPUT.PUT_LINE('v_receiver_msisdn  false' || v_receiver_msisdn );
  
         SELECT (SELECT CUR_RET_COUNT  FROM SUBS_RETAILER_DETAILS WHERE SUBSCRIBER_MSISDN = TO_CHAR(t_receiver_msisdn) ) INTO v_CUR_RET_COUNT_temp  from DUAL;
        
        
        IF v_CUR_RET_COUNT_temp IS NULL THEN
        
        INSERT
        INTO SUBS_RETAILER_DETAILS
          (
            SUBSCRIBER_MSISDN,
            RETAILER1_MSISDN,
            RETAILER1_FIRST_NAME,
            RETAILER1_LAST_NAME,
            RETAILER2_MSISDN,
            RETAILER2_FIRST_NAME,
            RETAILER2_LAST_NAME,
            RETAILER3_MSISDN,
            RETAILER3_FIRST_NAME,
            RETAILER3_LAST_NAME,
            RETAILER4_MSISDN,
            RETAILER4_FIRST_NAME,
            RETAILER4_LAST_NAME,
            RETAILER5_MSISDN,
            RETAILER5_FIRST_NAME,
            RETAILER5_LAST_NAME,
            STATUS,
            CREATED_ON,
            LAST_UPDATED_ON,
            CUR_RET_COUNT,
            BATCH_NO,
            NETWORK_CODE
          )
          VALUES
          (
            t_receiver_msisdn,
            t_sender1_msisdn,
            t_retailer1_fname,
            t_retailer1_lname,
            t_sender2_msisdn,
            t_retailer2_fname,
            t_retailer2_lname,
            t_sender3_msisdn,
            t_retailer3_fname,
            t_retailer3_lname,
            t_sender4_msisdn,
            t_retailer4_fname,
            t_retailer4_lname,
            t_sender5_msisdn,
            t_retailer5_fname,
            t_retailer5_lname,
            'NW',
            SYSDATE,
            SYSDATE,
            v_cur_ret_count,
            v_batch_no,
            v_network_code
          );
          
          v_count_inserted := v_count_inserted + 1;
          v_count_inserted_counter := v_count_inserted_counter + 1;
          
          IF v_count_inserted_counter > V_BATCH_SIZE THEN
             v_batch_no :=  v_batch_no +1;
             v_count_inserted_counter := 0;
          
          END IF;
          
          
        ELSIF v_CUR_RET_COUNT_temp < 5 THEN
        
      DBMS_OUTPUT.PUT_LINE(t_receiver_msisdn||'  v_CUR_RET_COUNT_temp '||v_CUR_RET_COUNT_temp);
            IF v_CUR_RET_COUNT_temp < 2 THEN        
        
        
																																	   
																																	   
																																													

                                    if t_sender4_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = t_sender4_msisdn , RETAILER4_FIRST_NAME = t_retailer4_fname , RETAILER4_LAST_NAME = t_retailer4_lname,
                                      RETAILER5_MSISDN = RETAILER1_MSISDN, RETAILER5_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER1_LAST_NAME,
                                      CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 

                                    elsif t_sender3_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = RETAILER1_MSISDN , RETAILER4_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER2_MSISDN, RETAILER5_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER2_LAST_NAME,
                                      CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    
                                    elsif t_sender2_msisdn IS NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                                      CUR_RET_COUNT = 3 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    ELSE
                                    
                                    
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER3_MSISDN = RETAILER3_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                                      CUR_RET_COUNT = 2 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                                      
                                    END IF;
            ELSIF v_CUR_RET_COUNT_temp < 3 THEN
            
           
                                    if t_sender3_msisdn IS  NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = t_sender3_msisdn , RETAILER3_FIRST_NAME = t_retailer3_fname , RETAILER3_LAST_NAME = t_retailer3_lname,
                                      RETAILER4_MSISDN = RETAILER1_MSISDN , RETAILER4_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER2_MSISDN, RETAILER5_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER2_LAST_NAME,
                                      CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    
                                    elsif t_sender2_msisdn IS NOT NULL THEN
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                                      RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                                      CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                                    ELSE
                                    
                                    
                                    
                                      UPDATE SUBS_RETAILER_DETAILS SET 
                                      RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                                      RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                                      RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                                      RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                                      RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                                      
                                      CUR_RET_COUNT = 3 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                                      
                                    END IF;
                            

            ELSIF v_CUR_RET_COUNT_temp  < 4 THEN
            
                        
                        if t_sender2_msisdn IS NOT NULL THEN
                        
                        
                          UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = t_sender2_msisdn , RETAILER2_FIRST_NAME = t_retailer2_fname , RETAILER2_LAST_NAME = t_retailer2_lname,
                          RETAILER3_MSISDN = RETAILER1_MSISDN , RETAILER3_FIRST_NAME = RETAILER1_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER2_MSISDN , RETAILER4_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER3_MSISDN, RETAILER5_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER3_LAST_NAME,
                          CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ;
                        
                          ELSE
                        
                        
                        
                          UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                          CUR_RET_COUNT = 4 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 
                        
                        
                        END IF;
                
            
            ELSE
            
                        DBMS_OUTPUT.PUT_LINE(t_receiver_msisdn||'  v_CUR_RET_COUNT_temp else '||v_CUR_RET_COUNT_temp);
                        
                        UPDATE SUBS_RETAILER_DETAILS SET 
                          RETAILER1_MSISDN = t_sender1_msisdn , RETAILER1_FIRST_NAME = t_retailer1_fname , RETAILER1_LAST_NAME = t_retailer1_lname,
                          RETAILER2_MSISDN = RETAILER1_MSISDN , RETAILER2_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER2_LAST_NAME = RETAILER1_LAST_NAME,
                          RETAILER3_MSISDN = RETAILER2_MSISDN , RETAILER3_FIRST_NAME = RETAILER2_FIRST_NAME , RETAILER3_LAST_NAME = RETAILER2_LAST_NAME,
                          RETAILER4_MSISDN = RETAILER3_MSISDN , RETAILER4_FIRST_NAME = RETAILER3_FIRST_NAME , RETAILER4_LAST_NAME = RETAILER3_LAST_NAME,
                          RETAILER5_MSISDN = RETAILER4_MSISDN, RETAILER5_FIRST_NAME = RETAILER4_FIRST_NAME , RETAILER5_LAST_NAME = RETAILER4_LAST_NAME,
                          CUR_RET_COUNT = 5 WHERE SUBSCRIBER_MSISDN = t_receiver_msisdn ; 

            END IF;



           v_count_updated := v_count_updated + 1;
        END IF;

  
  END IF;
  -- Check For LAST MSISDN END
  
  IF BCKORFWD = 'FWD' THEN
  
  DBMS_OUTPUT.PUT_LINE('BCKORFWD is Forward '||BCKORFWD);
  
  
  ELSE
  
    UPDATE PROCESS_STATUS
    SET  SCHEDULER_STATUS ='C'
    WHERE PROCESS_ID  ='FND_MY_RET_SUMM_STG' ;
  
  END IF;
  
  IF ( count_FIND_MY_RET_SUMMARY_STG IS NULL ) THEN
     DBMS_OUTPUT.PUT_LINE('TRANSFER_DATE '||v_yesterday_date);
  ELSE
     DBMS_OUTPUT.PUT_LINE('TRANSFER_DATE '||v_previous_date);
  END IF;
  
  
  COMMIT;
  v_count_COMMIT := v_count_COMMIT +1;
  
  v_end_time := SYSTIMESTAMP;
  
  DBMS_OUTPUT.PUT_LINE('Records Inserted  '||v_count_inserted );
  DBMS_OUTPUT.PUT_LINE('Records Updaed  '||v_count_updated);
  DBMS_OUTPUT.PUT_LINE('No of Commits  '||v_count_COMMIT);
  DBMS_OUTPUT.PUT_LINE('Batch No(s)  ');
  DBMS_OUTPUT.PUT_LINE('Start Time  ' || v_start_time);
  DBMS_OUTPUT.PUT_LINE('End Time  ' || v_end_time);
  
  
  
EXCEPTION
WHEN procexception THEN
  DBMS_OUTPUT.PUT_LINE ('procexception in FIN_MY_RETAILER_SUMMARY_CREATOR 2:' || SQLERRM);
  dbms_output.put_line( dbms_utility.format_error_backtrace );
WHEN OTHERS THEN
  DBMS_OUTPUT.PUT_LINE ('OTHERS EXCEPTION in FIN_MY_RETAILER_SUMMARY_CREATOR 2:' || SQLERRM || 'v_sender_msisdn' ||v_sender_msisdn||' v_receiver_msisdn '||v_receiver_msisdn);
  dbms_output.put_line( dbms_utility.format_error_backtrace );
END FIN_MY_RETAILER_SUMMARY_CREATOR;

-- Procedure ends --



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

INSERT INTO NETWORK_SERVICES
(MODULE_CODE, SERVICE_TYPE, SENDER_NETWORK, RECEIVER_NETWORK, STATUS, LANGUAGE1_MESSAGE, LANGUAGE2_MESSAGE, CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON)
VALUES('P2P', 'VCN', 'PB', 'NG', 'Y', 'Voucher consumption', 'Voucher consumption', 'PBLA0000000004', TIMESTAMP '2017-02-08 16:35:19.000000', 'PBLA0000000004', TIMESTAMP '2017-02-08 16:35:19.000000');

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

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('O2CTRNSFR', 'C2S', 'PRE', 'TYPE', 'com.btsl.pretups.channel.transfer.requesthandler.O2CTransferInitiateMappController', 'O2C Transfer Initiate', 'O2C Transfer Initiate', 'Y', TIMESTAMP '2020-05-15 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-05-15 00:00:00.000000', 'ADMIN', 'O2C Transfer Initiate', 'N', 'N', 'Y', NULL, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');


INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('O2CINICU', 'MAPPGW', '190', 'O2CTRNSFR', 'O2C Transfer Initiate', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-05-15 00:00:00.000000', 'SU0001', TIMESTAMP '2020-05-15 00:00:00.000000', 'SU0001', 'SVK0004389', NULL, 'TYPE,MSISDN');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('O2CAGAPRL', 'C2S', 'ALL', 'TYPE TXNID STATUS REFNO PIN', 'com.btsl.pretups.channel.transfer.requesthandler.O2CTransferApprovalController', 'O2C Transfer Approval', 'O2C Transfer Approval', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'O2C Transfer Approval', 'N', 'N', 'Y', 2, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNID,TXNSTATUS,REFNO,MESSAGE', 'TYPE,TXNID,STATUS,REFNO,EXTNWCODE', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('O2CAGAPRL', 'MAPPGW', '190', 'O2CAGAPRL', 'O2C Approval', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2013-11-30 10:19:35.000000', 'SU0001', TIMESTAMP '2013-11-30 10:19:35.000000', 'SU0001', 'SVK3000183', NULL, 'TYPE,TXNID,STATUS,REFNO');

ALTER TABLE SYSTEM_PREFERENCES MODIFY NAME  NVARCHAR2(300);
ALTER TABLE SYSTEM_PRF_HISTORY MODIFY NAME  NVARCHAR2(300);
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('SMS_SENDER_NAME_FOR_SERVICE_TYPE', 'SMS Sender Name based on service type', 'SYSTEMPRF', 'STRING', 'RC:eTOPUP Customer Recharge,EVD:eVoucher Customer Recharge,USERADD:eTOPUP User Mgmt,BAR-UNBARUSER:eTOPUP Bar/Unbar Mgmt,C2SCPN:eTOPUP C2S PIN Mgmt,PINRESET:eTOPUP C2S PIN Reset,USERSR-SUSRESUSR:eTOPUP Suspend/Resume channel user,CPWD-UNBOCK_PSWD:eTOPUP User Password Mgmt,VOUCHER_PIN_RESEND-PINRESEND:eVoucher PIN Mgmt', 
    NULL, NULL, 12, 'SMS Sender Name based on service type', 'Y', 
    'Y', 'C2S', 'SMS Sender Name based on service type', sysdate, 'ADMIN', 
    sysdate, 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('BYPASS_EVD_KANEL_MES_STAT', 'Bypass Kannel Response for EVD', 'SYSTEMPRF', 'BOOLEAN', 'FALSE', 
    NULL, NULL, 5, 'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response', 'Y', 
    'Y', 'C2S', 'Bypass Kannel Response for EVD if TRUE- means transaction will have no dependency on Kannel response else if FALSE- means transaction will have dependency on Kannel response', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('LANGS_SUPT_ENCODING', 'Encoding for special languages', 'SYSTEMPRF', 'STRING', 'ar,ku,ku1,ru,fa', 
    NULL, NULL, 10, 'Encoding for special languages, comma separated', 'N', 
    'Y', 'C2S', 'Encoding for special languages has to be comma separated (ar,ku,ku1)', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', NULL, 'Y');
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('IS_ONE_TIME_SID', 'IS ONE TIME SID ALLOWED', 'SYSTEMPRF', 'BOOLEAN', 'true', 
    NULL, NULL, 5, 'IS_ONE_TIME_SID ', 'Y', 
    'Y', 'P2P', 'IS_ONE_TIME_SID', sysdate, 'ADMIN', 
    sysdate, 'SU0001', 'true,false', 'Y');
COMMIT;

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('SELFREG', 'MAPPGW', '190', 'SELFREG', 'SELF REGISTRATION', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2013-01-16 08:21:47.000000', 'SU0001', TIMESTAMP '2013-01-16 08:21:47.000000', 'SU0001', 'SVK000097', NULL, 'TYPE,MSISDN');

INSERT INTO SERVICE_TYPE
(SERVICE_TYPE, MODULE, "TYPE", MESSAGE_FORMAT, REQUEST_HANDLER, ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, UNDERPROCESS_CHECK_REQD)
VALUES('SELFREG', 'OPT', 'ALL', 'TYPE MSISDN', 'com.btsl.pretups.channel.transfer.requesthandler.SelfRegistrationController', 'Self Registration', 'Self Registration', 'Y', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-07-14 00:00:00.000000', 'ADMIN', 'Self Registration', 'N', 'N', 'Y', NULL, 'Y', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('RCREV', 'REST', '190', 'RCREV', 'Recharge Reversal', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2012-07-26 19:30:07.000000', 'SU0001', TIMESTAMP '2015-04-05 15:58:38.000000', 'SU0001', 'SVK0001998', NULL, 'GTYPE,MSISDN');

Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('IS_EMAIL_ALLOWED_AUTO_NTWKSTK', 'auto Network Stock  email allowed', 'SYSTEMPRF', 'BOOLEAN', 'false',NULL, NULL, 50, 'auto Network Stock  email allowed', 'N','Y', 'C2S', 'auto Network Stock  email allowed', sysdate, 'ADMIN',sysdate, 'SU0001', NULL, 'Y');

Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('ALERT_ALLOWED_USER', 'GeoFencing alert allowed to user', 'SYSTEMPRF', 'BOOLEAN', 'false',NULL, NULL, 50, 'GeoFencing alert allowed to user', 'N','Y', 'C2S', 'GeoFencing alert allowed to user', sysdate, 'ADMIN',sysdate, 'SU0001', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TOKEN_EXPIRE_TIME', 'Token Expire Time in sec', 'SYSTEMPRF', 'INT', '1200', 1, 9999, 50, 'Token Expire Time in sec', 'Y', 'Y', 'C2S', 'Expiry time of OAuth Token', TIMESTAMP '2020-06-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-06-25 00:00:00.000000', 'SU0001', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REFRESH_TOKEN_EXPIRE_TIME', 'Refresh Token Expire Time in sec', 'SYSTEMPRF', 'INT', '1200', 1, 9999, 50, 'Refresh Token Expire Time in sec', 'Y', 'Y', 'C2S', 'Expiry time of OAuth Token', TIMESTAMP '2020-06-25 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-06-25 00:00:00.000000', 'SU0001', NULL, 'Y');
--

--##########################################################################################################
--##
--##      PreTUPS_v7.23.0 DB Script
--##
--##########################################################################################################
ALTER TABLE SYSTEM_PREFERENCES MODIFY REMARKS VARCHAR2(500);
ALTER TABLE SYSTEM_PRF_HISTORY MODIFY REMARKS VARCHAR2(500);

ALTER TABLE PRODUCT_SERVICE_TYPE_MAPPING
ADD  PRODUCT_CODE varchar2(10) NULL

Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('DW_COMMISSION_CAL', 'DW Commission Calulation', 'SYSTEMPRF', 'STRING', 'OTH',  NULL, NULL, 20, 'DW Commission to be given either based Other profile or Both', 'Y',  'Y', 'C2S', 'This is applied only for Dual Wallet and Commission to be given either based Other profile=OTH or Both=BASE_OTH', sysdate, 'ADMIN',  sysdate, 'SU0001', 'OTH,BASE_OTH', 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('DW_ALLOWED_GATEWAYS', 'DW Allowed Gateways', 'SYSTEMPRF', 'STRING', 'EXTGW,DWEXTGW',  NULL, NULL, 20, 'DW Commission to be given based on defined gateway code', 'Y',  'Y', 'C2S', 'This is applied only for Dual Wallet and DW Commission to be given based on defined gateway code like EXTGW', sysdate, 'ADMIN',  sysdate, 'SU0001', 'OTH,BASE_OTH', 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,  MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,  DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,  MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
Values('USSD_RESP_SEP', 'USSD_RESP_SEP', 'SYSTEMPRF', 'STRING', 'EXTGW',  NULL, NULL, 6, 'SMS_PIN_BYPASS_GATEWAY TYPES', 'N',  'Y', 'C2S', 'USSD_RESP_SEP', SYSDATE, 'ADMIN',  SYSDATE, 'ADMIN', NULL, 'Y');

ALTER TABLE USERS ADD to_moved_user_id VARCHAR2(15) DEFAULT NULL;

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('C2C_BATCH_FILEEXT', 'C2C Batch download file ext', 'SYSTEMPRF', 'STRING', 'xls', NULL, NULL, 50, 'the values for extension can be csv or xls or xlsx', 'N', 'Y', 'C2S', 'Extension of file to be downloaded or uploaded for C2C batch', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-17 09:44:51.000000', 'ADMIN', NULL, 'Y');



-----VIL Merge----

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS','Sender Network Identification basis on IMSI','SYSTEMPRF','BOOLEAN','FALSE',null,null,50,'Sender Network Identification basis on IMSI','N','Y','P2P','MNP allowed in the system or not',to_date('16-JUN-05','DD-MON-RR'),'ADMIN',to_date('11-SEP-05','DD-MON-RR'),'SU0001',null,'Y');

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('ADD_INFO_REQUIRED_FOR_VOUCHER','Add Info of Voucher to be stored in Txn Table','SYSTEMPRF','BOOLEAN','true',null,null,50,'Add Info of Voucher to be stored in Txn Table','N','Y','P2P','Add Info of Voucher to be stored in Txn Table',to_date('16-JUN-05','DD-MON-RR'),'ADMIN',to_date('11-SEP-05','DD-MON-RR'),'SU0001',null,'Y');

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('EMAIL_ALERT_FORVOMS_ORDER_INITIATOR','EMAIL for Voucher Generate to Initiator','SYSTEMPRF','BOOLEAN','true',null,null,50,'EMAIL for Voucher Generate to Initiator','N','Y','P2P','EMAIL for Voucher Generate to Initiator',to_date('16-JUN-05','DD-MON-RR'),'ADMIN',to_date('11-SEP-05','DD-MON-RR'),'SU0001',null,'Y');

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('SMS_ALERT_FORVOMS_ORDER_INITIATOR','SMS for Voucher Generate to Initiator','SYSTEMPRF','BOOLEAN','true',null,null,50,'SMS for Voucher Generate to Initiator','N','Y','P2P','SMS for Voucher Generate to Initiator',to_date('16-JUN-05','DD-MON-RR'),'ADMIN',to_date('11-SEP-05','DD-MON-RR'),'SU0001',null,'Y');

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('NET_PREFIX_TO_VALIDATED_FOR_BAR_UNBAR','Prefix Validation for Bar UnBar','SYSTEMPRF','BOOLEAN','false',null,null,50,'Prefix Validation for Bar UnBar','N','Y','P2P','Prefix Validation for Bar UnBar',to_date('16-JUN-05','DD-MON-RR'),'ADMIN',to_date('11-SEP-05','DD-MON-RR'),'SU0001',null,'Y');

Insert into SYSTEM_PREFERENCES (PREFERENCE_CODE,NAME,TYPE,VALUE_TYPE,DEFAULT_VALUE,MIN_VALUE,MAX_VALUE,MAX_SIZE,DESCRIPTION,MODIFIED_ALLOWED,DISPLAY,MODULE,REMARKS,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,ALLOWED_VALUES,FIXED_VALUE) values ('VOMS_IS_MRPID_IN_SERIAL','IS MRP ID IN VOUCHER SERIAL NO','SYSTEMPRF','BOOLEAN','false',null,null,50,'IS MRP ID IN VOUCHER SERIAL NO','Y','Y','C2S','IS MRP ID IN VOUCHER SERIAL NO',to_date('19-JUL-19','DD-MON-RR'),'ADMIN',to_date('19-JUL-19','DD-MON-RR'),'SU0001',null,'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('FILE_UPLOAD_MAX_SIZE', 'C2C file upload max size', 'SYSTEMPRF', 'STRING', '2097152', 1000, 9999999, 50, 'C2C file upload max size', 'Y', 'Y', 'C2C', 'C2C file upload max size', TIMESTAMP '2020-07-20 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-07-20 00:00:00.000000', 'SU0001', NULL, 'Y');

--##########################################################################################################
--##
--##      PreTUPS_v7.24.0 DB Script
--##
--##########################################################################################################

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_ORDER_SLAB_LENGTH', 'Voucher Slab length ', 'SYSTEMPRF', 'INT', '4', 4, 4, 50, 'voucher slab length ', 'N', 'Y', 'VOMS', 'voucher slab length', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MIN_ALT_VALUE', 'Voucher MIN alerting value ', 'SYSTEMPRF', 'INT', '10', 10, 10, 50, 'voucher slab length ', 'N', 'Y', 'VOMS', 'voucher min alerting value', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DIGITAL_RECHARGE_VOUCHER_TYPE', 'DIGITAL_RECHARGE_VOUCHER_TYPE', 'SYSTEMPRF', 'STRING', 'digital,digital1,test_digit', NULL, NULL, 50, 'Digital Recharge Voucher Type', 'N', 'N', 'C2S', 'Digital Recharge Voucher Type', TIMESTAMP '2020-10-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2020-10-07 12:48:44.000000', 'SU0001', 'SYSTEM,GROUP,ALL', 'N');

--##########################################################################################################
--##
--##      PreTUPS_v7.25.0 DB Script
--##
--##########################################################################################################


ALTER TABLE OAUTH_ACCESS_TOKEN ADD CONSTRAINT token_pk PRIMARY KEY (token_id); 
ALTER TABLE oauth_refresh_token ADD  CONSTRAINT refresh_pk PRIMARY KEY (token_id);

ALTER TABLE CHANNEL_TRANSFERS_ITEMS
ADD FIRST_LEVEL_APPROVED_QTY number;

ALTER TABLE CHANNEL_TRANSFERS_ITEMS
ADD SECOND_LEVEL_APPROVED_QTY number;


--##########################################################################################################
--##
--##      PreTUPS_v7.26.0 DB Script
--##
--##########################################################################################################

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'VMBTCHUD', 'ALL', 2515, TIMESTAMP '2021-01-10 10:01:14.000000', 'NA', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'VMPNEXPEXT', 'ALL', 145, TIMESTAMP '2021-01-10 00:00:00.000000', 'NA', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'BATCH_ID', 'NG', 13, TIMESTAMP '2021-01-10 10:01:14.000000', 'NA', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'CB', 'NG', 1, TIMESTAMP '2021-01-10 22:54:50.000000', 'DAY', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'OB', 'NG', 27, TIMESTAMP '2021-01-10 06:24:55.000000', 'DAY', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'SB', 'NG', 2, TIMESTAMP '2021-01-10 05:21:14.000000', 'DAY', 'Scheduled Batch ID for corporate');

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'SBM', 'NG', 977, TIMESTAMP '2021-01-10 10:01:14.000000', 'NA', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'VMBTCHUD', 'NG', 552, TIMESTAMP '2021-01-10 10:01:14.000000', 'NA', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'OB', 'PB', 1, TIMESTAMP '2021-01-10 09:28:26.000000', 'DAY', NULL);

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2021', 'VMBTCHUD', 'PB', 552, TIMESTAMP '2021-01-10 10:01:14.000000', 'NA', NULL);

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

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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
ADD (
    MARGIN_AMOUNT NUMBER(20,0),
    OTF_AMOUNT NUMBER(20,0)
);

ALTER TABLE DAILY_C2S_TRANS_DETAILS 
ADD (
    TOTAL_MARGIN_AMOUNT NUMBER(20,0),
    TOTAL_OTF_AMOUNT NUMBER(20,0)
);

ALTER TABLE DAILY_CHNL_TRANS_DETAILS
ADD (
    TOTAL_COMMISSION_VALUE NUMBER(20,0),
    TOTAL_OTF_AMOUNT NUMBER(20,0)
);

INSERT INTO WEB_SERVICES_TYPES
(WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE, ROLE_CODE)
VALUES('DOWNLOADFILEUSER', 'Download File User', 'DownloadFileUser', 'configfiles/cardgroup/validation-cardgroup.xml', 'com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO', 'configfiles/restservice', '/rstapi/v1/channelUsers/channelUsersList', 'N', 'Y', 'DOWNLOADFILEUSER');

INSERT INTO PRETUPS_TRUNK_DEV.SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('USER_ALLOW_CONTENT_TYPE', 'USER Allowed Content Type', 'SYSTEMPRF', 'STRING', 'CSV', NULL, NULL, 50, 'USER Allowed Content Type', 'N', 'N', 'P2P', 'VMS Allowed Content Type', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', TIMESTAMP '2639-02-20 00:58:06.000000', 'ADMIN', NULL, 'Y');



-- PVG - Mobile App issue fixes - Start --

--C2C Transfer Stock
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN' WHERE KEYWORD = 'TRF' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';
UPDATE SERVICE_TYPE SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN' WHERE SERVICE_TYPE = 'TRF';


--Forgot PIN
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH' WHERE KEYWORD = 'OTPVDPINRST' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';

--User Hierarchy
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN' WHERE KEYWORD = 'UPUSRHRCHY' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';

--PPB
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,PIN,MSISDN2,AMOUNT,IMEI,LANGUAGE1,MHASH,TOKEN' WHERE KEYWORD = 'PPB' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';

--User Details
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,IMEI,LANGUAGE1' WHERE KEYWORD = 'USRDETAILS' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';

--Other Balance
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,IMEI,LANGUAGE1' WHERE KEYWORD = 'OTHERBALAN' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';
UPDATE SERVICE_TYPE SET REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,IMEI,LANGUAGE1,LANGUAGE2' WHERE SERVICE_TYPE = 'OTHERBALAN';

--C2C Buy Enquiry
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN' WHERE KEYWORD = 'C2CBUYUSENQ' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';

-- C2C Buy Initiate
INSERT INTO SERVICE_KEYWORDS
(KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, SUB_KEYWORD, REQUEST_PARAM)
VALUES('TRFINI', 'MAPPGW', '190', 'TRFINI', 'C2C Initiate', 'Y', NULL, NULL, NULL, 'Y', TIMESTAMP '2020-02-21 09:00:36.000000', 'SU0001', TIMESTAMP '2020-02-21 09:00:36.000000', 'SU0001', 'SVK4102010', NULL, 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1,MHASH,TOKEN');
UPDATE SERVICE_TYPE SET MESSAGE_FORMAT = 'TYPE MSISDN2 PRODUCTS PIN', 
REQUEST_PARAM = 'TYPE,MSISDN,MSISDN2,PRODUCTS,IMEI,LANGUAGE1' WHERE SERVICE_TYPE = 'TRFINI';


--C2C Return
UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN' WHERE KEYWORD = 'UPUSRHRCHY' 
AND REQ_INTERFACE_TYPE = 'MAPPGW';


UPDATE SERVICE_KEYWORDS
SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,MHASH,TOKEN,FROMDATE,TODATE,SERVICETYPE'
WHERE SERVICE_KEYWORD = ?C2SPRODTXNDETAILS? and REQ_INTERFACE_TYPE = 'MAPPGW';

UPDATE SERVICE_KEYWORDS SET REQUEST_PARAM = 'TYPE,MSISDN,IMEI,LANGUAGE1,MHASH,TOKEN,EXTNWCODE'
WHERE SERVICE_KEYWORD = ?USRINCVIEW? and REQ_INTERFACE_TYPE = 'MAPPGW';

-- PVG - Mobile App issue fixes - End --




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

ALTER TABLE ROLES ADD (SUB_GROUP_NAME varchar2(255) DEFAULT 'Sub_Group',
SUB_GROUP_ROLE varchar2(2) DEFAULT 'N');

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

ALTER TABLE ROLES ADD VIEW_ROLES varchar2(2) DEFAULT 'N';


update roles r SET r.view_roles='Y' WHERE r.domain_type='DISTB_CHAN'  AND r.role_code in ('C2SREV', 'C2SRECHARGE', 'C2CTRFINI', 'C2CRETURN', 'C2CTRF', 'C2CTRFAPR1', 'C2CTRFAPR2', 'C2CTRFAPR3', 'C2CWDL', 'C2CBUYVINI', 'C2CVINI', 'C2CVCTRFAPR1', 'C2CVCTRFAPR2', 'C2CVCTRFAPR3', 'BC2CAPPROVE', 'BC2CWDRAPP', 'BC2CINITIATE', 'BC2CWDRW', 'O2CRET', 'O2CINIT', 'INITVOMSOREQ', 'ADDCUSER', 'BATCHUSRINITIATE', 'DELETECUSER', 'EDITCUSER', 'VIEWCUSER', 'CHANGEPIN', 'CHANGESELFPIN', 'C2SUNBLOCKPIN', 'SUSPENDCUSER', 'C2SUNBLOCKPAS', 'MODSTAFFUSER', 'STFUSRAD', 'VIEWSTAFFUSER', 'RESSTAFFUSER', 'SUSSTAFFUSER', 'UNBARUSER', 'SCHEDULETOPUP', 'CANCELSCHEDULE', 'CNCLSCHEDULED', 'RESCHEDULETOPUP', 'VIEWSUBSSCHEDULE', 'VIEWSCHEDULED', 'BARUSER', 'CHANGELANG', 'VIEWBARREDLIST', 'SELFCHNLUSRMOD', 'DELSTAFFUSER', 'CHANGESELFPASSCU', 'UNBLOCKPINSTAFF', 'UNBLOCKPASSSTAFF');

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

ALTER TABLE OAUTH_USERS_LOGIN_INFO 
ADD (
    TOKEN_ID VARCHAR2(256) UNIQUE,
    USER_ID VARCHAR2(15)
);

DROP TABLE OAUTH_ACCESS_TOKENS ;
DROP TABLE OAUTH_REFRESH_TOKENS ;


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('REPORT_OFFLINE', 'Is the report offline or Online', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Is the report offline or Online', 'N', 'Y', 'C2S', 'Is the report offline or Online', TIMESTAMP '2021-06-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-06-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('OFFLINERPT_DOWNLD_PATH', 'Offline report download path', 'SYSTEMPRF', 'STRING', '/home/pretups/', NULL, NULL, 50, 'Offline report download path', 'N', 'Y', 'C2S', 'Offline report download path', TIMESTAMP '2021-08-26 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-08-26 09:44:51.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('ALLOW_SAME_REPORT_EXEC', 'Allow same report execution multiple times', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Allow same report execution multiple times', 'N', 'Y', 'C2S', 'Allow same report execution multiple times', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', NULL, 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('TOT_RPT_EXEC_PERUSER', 'Total no. of users to be executed in parallel', 'SYSTEMPRF', 'INT', '5', 10, 20, 50, 'Total no. of users to be executed in parallel', 'N', 'Y', 'VOMS', 'Total no. of users to be executed in parallel', TIMESTAMP '2021-08-27 00:00:00.000000', 'ADMIN', TIMESTAMP '2021-08-27 00:00:00.000000', 'ADMIN', NULL, 'Y');

CREATE TABLE REPORT_MASTER
  (REPORT_ID          VARCHAR2 (30) NOT NULL PRIMARY KEY,
  REPORT_NAME         VARCHAR2 (100) NOT NULL UNIQUE ,
   FILE_NAME_PREFIX  VARCHAR2(10)  NOT NULL,
   CREATE_ON DATE,
   RPT_PROCESSOR_BEAN_NAME VARCHAR2(50)  NOT NULL
   );



CREATE TABLE OFFLINE_REPORT_PROCESS
  (REPORT_PROCESS_ID          VARCHAR2 (30) NOT NULL PRIMARY KEY,
  REPORT_ID          VARCHAR2 (30) NOT NULL ,
  FILE_NAME         VARCHAR2 (50) NOT NULL UNIQUE ,
  REPORT_INITIATED_BY VARCHAR2(15),
  EXECUTION_START_TIME  TIMESTAMP,
  EXECUTION_END_TIME  TIMESTAMP,
  STATUS VARCHAR2(20),
  INSTANCE_ID VARCHAR2(20),
   CREATED_ON TIMESTAMP,
   TOTAL_RECORDS LONG,
   RPT_JSON_REQ  clob,
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
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
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

insert
	into
	pretupsdatabase.system_preferences (preference_code,"name","type",value_type,default_value,min_value,	max_value,
	max_size,description,modified_allowed,display,"module",remarks,created_on,created_by,modified_on,modified_by,allowed_values,fixed_value)
values('DEF_CHNL_TRANSFER_ALLOWED',
'Default transfer rules',
'SYSTEMPRF',
'STRING',
'C2CVOMSTRFINI,TRFINI',
null,
null,
50,
'Default transfer rules',
'N',
'N',
'C2C',
'Default transfer rules, will be used by MAPPGW',
'2021-12-24 00:00:00.000',
'ADMIN',
'2021-12-24 12:48:44.000',
'SU0001',
null,
'N');

--##########################################################################################################
--##
--##      PreTUPS_v7.39.0 DB Script
--##
--##########################################################################################################


INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('2022', 'OX', 'NG', 1, TIMESTAMP '2022-01-13 09:56:37.000000', 'MINUTES', NULL);


--##########################################################################################################
--##
--##      PreTUPS_v7.43.3 DB Script
--##
--##########################################################################################################


ALTER TABLE OAUTH_ACCESS_TOKEN MODIFY created_on timestamp;
ALTER TABLE OAUTH_ACCESS_TOKEN MODIFY modified_on timestamp;
ALTER TABLE OAUTH_REFRESH_TOKEN MODIFY created_on timestamp;
ALTER TABLE OAUTH_REFRESH_TOKEN MODIFY modified_on timestamp;


CREATE TABLE NONCE_RECORD 
   (	NONCE_ID VARCHAR2(40), 
	CREATED_ON TIMESTAMP (6)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 16384 NEXT 24576 MINEXTENTS 1 MAXEXTENTS 505
  PCTINCREASE 50 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;






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
	sequence_no NUMBER NOT NULL , 
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

--Added roles for O2C admin
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('O2C', 'Initiate_Transfer_admin', 'INITO2CTRF');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('O2C', 'Initiate_Voucher_order_request_admin', 'INITO2CTRF');


                 ALTER TABLE selector_amount_mapping
  add (    created_by varchar(20) ,
	created_on timestamp ,
	modified_by varchar(20) ,
	modified_on timestamp )
	
	
	
	update       selector_amount_mapping set created_by=(select user_id from users where category_code ='SUADM')   
update       selector_amount_mapping set MODIFIED_BY=(select user_id from users where category_code ='SUADM')
update       selector_amount_mapping SET CREATED_ON= SYSDATE
update       selector_amount_mapping SET MODIFIED_ON= SYSDATE

ALTER TABLE selector_amount_mapping
  MODIFY (    created_by NOT NULL ,
	created_on NOT NULL ,
	modified_by NOT NULL ,
	modified_on NOT NULL )
 