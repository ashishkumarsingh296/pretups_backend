
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


