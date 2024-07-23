Insert into SERVICE_TYPE
   (SERVICE_TYPE, MODULE, TYPE, MESSAGE_FORMAT, REQUEST_HANDLER, 
    ERROR_KEY, DESCRIPTION, FLEXIBLE, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, NAME, EXTERNAL_INTERFACE, UNREGISTERED_ACCESS_ALLOWED, 
    STATUS, SEQ_NO, USE_INTERFACE_LANGUAGE, GROUP_TYPE, SUB_KEYWORD_APPLICABLE, 
    FILE_PARSER, RECEIVER_USER_SERVICE_CHECK, ERP_HANDLER)
 Values
   ('LSTXTRFSRV', 'C2S', 'ALL', 'KEYWORD PIN', 'com.btsl.pretups.user.requesthandler.LastXTransferServiceWiseRequestHandler', 
    'c2s.lasttransferstatusservicewise', 'Last X transfer servicewise report', 'Y', sysdate, 'ADMIN', 
    sysdate, 'ADMIN', 'Last X Transfer Service Wise Report by Ext System', 'N', 'N', 
    'Y', NULL, 'N', 'NA', 'N', 
    NULL, 'Y', 'NA');
	
Insert into SERVICE_KEYWORDS
   (KEYWORD, REQ_INTERFACE_TYPE, SERVICE_PORT, SERVICE_TYPE, NAME, 
    STATUS, MENU, SUB_MENU, ALLOWED_VERSION, MODIFY_ALLOWED, 
    CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SERVICE_KEYWORD_ID, 
    SUB_KEYWORD)
 Values
   ('LSTXTRFSRV', 'EXTGW', '190', 'LSTXTRFSRV', 'Last X transfer Service Wise', 
    'Y', NULL, NULL, NULL, 'Y', 
    sysdate, 'SU0001', sysdate, 'SU0001', 'SVK0000144', 
    NULL);
COMMIT;