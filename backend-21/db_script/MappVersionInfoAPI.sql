SET DEFINE OFF;
Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, ERP_HANDLER, RECEIVER_USER_SERVICE_CHECK, RESPONSE_PARAM, REQUEST_PARAM, 
    UNDERPROCESS_CHECK_REQD)
Values
   ('VERINFO', 'C2S', 'PRE', 'TYPE APPTYPE CURVERSION PLATFORM', 'com.btsl.pretups.requesthandler.MappVersionInfoHandler', 
    'Mobile App Version', 'Mobile App Version', 'Y', TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    TO_DATE('07/14/2005 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 'Order Lines', 'N', 'N', 
    'N', NULL, 'Y', 'NA', 'N', 
    NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE,APPTYPE,PLATFORM,UPDTYPE,UPDURL', 'TYPE APPTYPE CURVERSION PLATFORM', 
    'Y');
COMMIT;

SET DEFINE OFF;
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD, REQUEST_PARAM)
Values
   ('VERINFO', 'MAPPGW', '190', 'ENQSID', 'MAPP VERSION ENQUIRY', 
    'Y', NULL, NULL, NULL, 'Y', 
    TO_DATE('01/07/2011 08:49:12', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', TO_DATE('01/07/2011 08:49:12', 'MM/DD/YYYY HH24:MI:SS'), 'SU0001', 'SVK0000500', 
    NULL, 'GTYPE,MSISDN');
COMMIT;