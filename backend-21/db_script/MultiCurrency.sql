
SET DEFINE OFF;
Insert into MESSAGE_GATEWAY_TYPES
   (GATEWAY_TYPE, GATEWAY_TYPE_NAME, ACCESS_FROM, PLAIN_MSG_ALLOWED, BINARY_MSG_ALLOWED, 
    FLOW_TYPE, RESPONSE_TYPE, TIMEOUT_VALUE, DISPLAY_ALLOWED, MODIFY_ALLOWED, 
    USER_AUTHORIZATION_REQD)
 Values
   ('TPARTYGW', 'TPARTYGW', 'PHONE', 'Y', 'Y', 
    'R', 'RESPONSE', 10000, 'Y', 'Y', 
    'Y');
COMMIT;
SET DEFINE OFF;
Insert into MESSAGE_GATEWAY_SUBTYPES
   (GATEWAY_SUBTYPE, GATEWAY_TYPE, GATEWAY_SUBTYPE_NAME)
 Values
   ('TPARTYGW', 'TPARTYGW', 'TPARTYGW');
COMMIT;



SET DEFINE OFF;
Insert into CLASS_HANDLERS
   (HANDLER_TYPE, HANDLER_NAME, HANDLER_CLASS, HANDLER_SUBTYPE)
 Values
   ('MESS_GAT_PARSER', 'ThirdPartyParsers', 'com.btsl.pretups.gateway.parsers.ThirdPartyParsers', 'TPARTYGW');
COMMIT;

Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('CURENCYMOD', 'C2S', 'ALL', 'TYPE SC TC TCNTRY RATE', 'com.btsl.pretups.user.requesthandler.CurrencyConverterRequestHandler', 
    'c2s.currencyconverterstatus', 'Currency Rate Updation', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Currecncy Rate Updation', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,DATE,REFNO,TXNSTATUS,MESSAGE,SC TC TCNTRY RATE,ERRORCODE,ERRORMESSAGE', 'TYPE SC TC TCNTRY RATE', 
    'Y');
	
	Insert into PRODUCT_SERVICE_TYPE_MAPPING
   (PRODUCT_TYPE, SERVICE_TYPE, CREATED_BY, CREATED_ON, MODIFIED_BY, 
    MODIFIED_ON, GIVE_ONLINE_DIFFERENTIAL, DIFFERENTIAL_APPLICABLE, SUB_SERVICE)
 Values
   ('PREPROD', 'MRC', 'ADMIN', sysdate, 'ADMIN', 
   sysdate, 'Y', 'Y', 'DEF');

SET DEFINE OFF;
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
 Values
   ('MRC', 'C2S', 'PRE', 'TYPE MSISDN2 AMOUNT CURRENCY PIN', 'com.btsl.pretups.channel.transfer.requesthandler.C2SPrepaidController', 
    'Multi Currency Prepaid Recharge', 'Multi Currency Prepaid Recharge', 'Y', TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Multi Currency Prepaid Recharge', 'Y', 'N', 
    'Y', NULL, 'Y', 'NA', 'N', 
    'com.btsl.pretups.scheduletopup.process.RechargeBatchFileParser', NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN,MSISDN2,AMOUNT,CURRENCY,SELECTOR,IMEI,PIN,LANGUAGE1,LANGUAGE2', 
    'Y');
COMMIT;


Alter table c2s_transfers add MULTICURRENCY_DETAIL VARCHAR2(20 BYTE);
COMMIT;
Insert into SYSTEM_PREFERENCES
   (PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, 
    MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, 
    DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
 Values
   ('DEFAULT_CURRENCY', 'Default currency', 'SYSTEMPRF', 'STRING', 'USD', 
    NULL, NULL, 50, 'Default currency', 'N', 
    'Y', 'C2S', NULL, TO_DATE('08/13/2016 17:40:27', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('08/13/2016 17:40:27', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', NULL, 'Y');
COMMIT;
update system_preferences set default_value='RC,DTH,PIN,DC,CE,CBP,PMD,CCN,RPB,MRC' where preference_code='SRVC_PROD_INTFC_MAPPING_ALLOWED';