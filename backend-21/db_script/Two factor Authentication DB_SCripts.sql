--Added table for storing users Generated OTP information
CREATE TABLE USER_OTP
(
  USER_ID       VARCHAR2(15 BYTE),
  MSISDN        VARCHAR2(15 BYTE),
  OTP_PIN       VARCHAR2(32 BYTE),
  STATUS        VARCHAR2(2 BYTE),
  GENERATED_ON  DATE,
  CREATED_BY    VARCHAR2(15 BYTE),
  CREATED_ON    DATE,
  MODIFIED_ON   DATE,
  MODIFIED_BY   VARCHAR2(15 BYTE),
  CONSUMED_ON   DATE
)

--Added column For marking Grades allowed for two factor Authentication
ALTER TABLE CHANNEL_GRADES ADD IS_2FA_ALLOWED varchar2(1);

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