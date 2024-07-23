set define off;
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '1016019', 'mclass^2&pid^61:1016019:You can enquire for maximum {0} transactions', 'ALL', 'mclass^2&pid^61:1016019:You can enquire for maximum {0} transactions', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);

Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '1016020', 'mclass^2&pid^61:1016020:Service type can not be blank for transaction type C2S', 'ALL', 'mclass^2&pid^61:1016020:Service type can not be blank for transaction type C2S', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);	
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '1016021', 'mclass^2&pid^61:1016021:C2C INOUT can not be blank for transaction type C2C', 'ALL', 'mclass^2&pid^61:1016021:C2C INOUT can not be blank for transaction type C2C', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);	
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '1016022', 'mclass^2&pid^61:1016022:You have not done any transaction in last {0} days', 'ALL', 'mclass^2&pid^61:1016022:You have not done any transaction in last {0} days', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '1016023', 'mclass^2&pid^61:1016023:Channel user MSISDN <MSISDN1> can not be blank', 'ALL', 'mclass^2&pid^61:1016023:Channel user MSISDN <MSISDN1> can not be blank', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
	
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '24120', 'mclass^2&pid^61:24120:{0}- TxnID:{1},MSISDN:{2},Status:{3},Type:{4},Amount:{5},PostBal:{6}', 'ALL', 'mclass^2&pid^61:24120:{0}- TxnID:{1},MSISDN:{2},Status:{3},Type:{4},Amount:{5},PostBal:{6}', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);	
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '24121', 'mclass^2&pid^61:24121:Transfer details {0}.', 'ALL', 'mclass^2&pid^61:24121:Transfer details {0}.', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
	
Insert into MESSAGES_MASTER
   (MESSAGE_TYPE, MESSAGE_CODE, DEFAULT_MESSAGE, NETWORK_CODE, MESSAGE1, 
    MESSAGE2, MESSAGE3, MCLASS, DESCRIPTION, MESSAGE4, 
    MESSAGE5)
 Values
   ('ALL', '24124', 'mclass^2&pid^61:24124:Transfer details TxnID:{1},MSISDN:{2},Status:{3},Type:{4},Amount:{5},PostBal:{6}', 'ALL', 'mclass^2&pid^61:24124:Transfer details TxnID:{1},MSISDN:{2},Status:{3},Type:{4},Amount:{5},PostBal:{6}', 
    NULL, NULL, 'Y', NULL, NULL, 
    NULL);
COMMIT;
