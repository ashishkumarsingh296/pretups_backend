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
COMMIT;
