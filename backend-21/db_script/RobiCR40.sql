drop table LOAN_PROFILES;

CREATE TABLE LOAN_PROFILES
(
  PROFILE_ID              VARCHAR2(10 BYTE) ,
  PROFILE_NAME              VARCHAR2(50 BYTE),
  CATEGORY_CODE              VARCHAR2(10 BYTE) NOT NULL,
  PROFILE_TYPE                 VARCHAR2(1 BYTE) NOT NULL,
  NETWORK_CODE                    VARCHAR2(2 BYTE)  NOT NULL,
  STATUS              VARCHAR2(1 BYTE) DEFAULT 'Y' NOT NULL,
  CREATED_BY                      VARCHAR2(20 BYTE) NOT NULL,
  CREATED_ON                      DATE          NOT NULL,
  MODIFIED_BY                     VARCHAR2(20 BYTE) NOT NULL,
  MODIFIED_ON                     DATE          NOT NULL
);

CREATE UNIQUE INDEX PK_LOAN_TRANSFER_PROFILE ON LOAN_PROFILES
(PROFILE_ID);

DROP INDEX INDEX_LP_CATEGORY_CODE;

CREATE  INDEX INDEX_LP_CATEGORY_CODE ON LOAN_PROFILES
(CATEGORY_CODE);


CREATE INDEX INDEX_LP_NETWORK_CODE ON LOAN_PROFILES
(NETWORK_CODE);


CREATE INDEX INDEX_LP_STATUS ON LOAN_PROFILES
(STATUS);


ALTER TABLE LOAN_PROFILES ADD (
  CONSTRAINT PK_LOAN_TRANSFER_PROFILE
PRIMARY KEY
(PROFILE_ID));

DROP TABLE LOAN_PROFILES_HISTORY;
CREATE TABLE LOAN_PROFILES_HISTORY
(
  PROFILE_ID              VARCHAR2(10 BYTE) ,
  PROFILE_NAME              VARCHAR2(50 BYTE),
  CATEGORY_CODE              VARCHAR2(10 BYTE) NOT NULL,
  PROFILE_TYPE                 VARCHAR2(1 BYTE) NOT NULL,
  NETWORK_CODE                    VARCHAR2(2 BYTE)  NOT NULL,
  STATUS              VARCHAR2(1 BYTE) DEFAULT 'Y' NOT NULL,
  CREATED_BY                      VARCHAR2(20 BYTE) NOT NULL,
  CREATED_ON                      DATE          NOT NULL,
  MODIFIED_BY                     VARCHAR2(20 BYTE) NOT NULL,
  MODIFIED_ON                     DATE          NOT NULL,
  OPERATION_PERFORMED        VARCHAR2(1 BYTE),
  ENTRY_DATE                 DATE
) 
PARTITION BY RANGE (ENTRY_DATE)
INTERVAL( NUMTODSINTERVAL(1,'DAY'))
(  
  PARTITION VALUES LESS THAN (TO_DATE(' 2021-09-30 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    LOGGING
    NOCOMPRESS
    TABLESPACE P_C2SDATA1
    PCTFREE    10
    INITRANS   1
    MAXTRANS   255
    STORAGE    (
                INITIAL          8M
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                BUFFER_POOL      DEFAULT
               ));

DROP TRIGGER TRIG_LOAN_PROFILES_HISTORY;
CREATE OR REPLACE TRIGGER "TRIG_LOAN_PROFILES_HISTORY" 
AFTER INSERT OR UPDATE OR DELETE ON LOAN_PROFILES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO LOAN_PROFILES_HISTORY(profile_id,profile_name,category_code,profile_type,network_code,status,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:NEW.profile_id,:NEW.profile_name, :NEW.category_code, :NEW.profile_type, :NEW.network_code, :NEW.status, :NEW.created_by, :NEW.created_on, :NEW.modified_by, :NEW.modified_on,'I',sysdate);
ELSIF UPDATING THEN
INSERT INTO LOAN_PROFILES_HISTORY(profile_id,profile_name,category_code,profile_type,network_code,status,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:NEW.profile_id,:NEW.profile_name, :NEW.category_code, :NEW.profile_type, :NEW.network_code, :NEW.status, :NEW.created_by, :NEW.created_on, :NEW.modified_by, :NEW.modified_on,'U',sysdate);
ELSIF DELETING THEN
INSERT INTO LOAN_PROFILES_HISTORY(profile_id,profile_name,category_code,profile_type,network_code,status,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:OLD.profile_id ,:OLD.profile_name, :OLD.category_code, :OLD.profile_type, :OLD.network_code, :OLD.status, :OLD.created_by, :OLD.created_on, :OLD.modified_by, :OLD.modified_on,'D',sysdate);
END IF;
END;


drop table LOAN_PROFILE_DETAILS;

CREATE TABLE LOAN_PROFILE_DETAILS
(
  PROFILE_ID              VARCHAR2(10 BYTE) ,
  PRODUCT_CODE         VARCHAR2(10 BYTE) NOT NULL,
  FROM_RANGE              NUMBER(5)  NOT NULL,
  TO_RANGE              NUMBER(5)  NOT NULL,
  INTEREST_TYPE              VARCHAR2(10 BYTE)  NOT NULL,
  INTEREST_VALUE                  NUMBER(20)  NOT NULL
 );

CREATE UNIQUE INDEX PK_LOAN_TRANSFER_PROFILE_DET ON LOAN_PROFILE_DETAILS
(PROFILE_ID,PRODUCT_CODE,FROM_RANGE);


ALTER TABLE LOAN_PROFILE_DETAILS ADD (
  CONSTRAINT PK_LOAN_TRANSFER_PROFILE_DET
PRIMARY KEY
(PROFILE_ID,PRODUCT_CODE,FROM_RANGE));

ALTER TABLE LOAN_PROFILE_DETAILS ADD (
  CONSTRAINT FK_LOAN_TRANSFER_PROFILE_DET    
FOREIGN KEY (PROFILE_ID)
    REFERENCES LOAN_PROFILES(PROFILE_ID));
    


DROP TABLE LOAN_PROFILE_DET_HISTORY;
CREATE TABLE LOAN_PROFILE_DET_HISTORY
(
  PROFILE_ID              VARCHAR2(10 BYTE) ,
  PRODUCT_CODE         VARCHAR2(10 BYTE) NOT NULL,
  FROM_RANGE              NUMBER(5)  NOT NULL,
  TO_RANGE              NUMBER(5)  NOT NULL,
  INTEREST_TYPE              VARCHAR2(10 BYTE)  NOT NULL,
  INTEREST_VALUE                  NUMBER(20)  NOT NULL,
  OPERATION_PERFORMED        VARCHAR2(1 BYTE),
  ENTRY_DATE                 DATE
) 
PARTITION BY RANGE (ENTRY_DATE)
INTERVAL( NUMTODSINTERVAL(1,'DAY'))
(  
  PARTITION VALUES LESS THAN (TO_DATE(' 2021-09-30 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    LOGGING
    NOCOMPRESS
    TABLESPACE P_C2SDATA1
    PCTFREE    10
    INITRANS   1
    MAXTRANS   255
    STORAGE    (
                INITIAL          8M
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                BUFFER_POOL      DEFAULT
               ));


DROP TRIGGER TRIG_LOAN_PROFILE_DET_HISTORY;
CREATE OR REPLACE TRIGGER "TRIG_LOAN_PROFILE_DET_HISTORY" 
AFTER INSERT OR UPDATE OR DELETE ON LOAN_PROFILE_DETAILS FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO LOAN_PROFILE_DET_HISTORY(profile_id,product_code,from_range,to_range,interest_type,interest_value,operation_performed,entry_date)
VALUES(:NEW.profile_id,:NEW.product_code,:NEW.from_range,:NEW.to_range,:NEW.interest_type,:NEW.interest_value,'I',sysdate);
ELSIF UPDATING THEN
INSERT INTO LOAN_PROFILE_DET_HISTORY(profile_id,product_code,from_range,to_range,interest_type,interest_value,operation_performed,entry_date)
VALUES(:NEW.profile_id,:NEW.product_code,:NEW.from_range,:NEW.to_range,:NEW.interest_type,:NEW.interest_value,'U',sysdate);
ELSIF DELETING THEN
INSERT INTO LOAN_PROFILE_DET_HISTORY(profile_id,product_code,from_range,to_range,interest_type,interest_value,operation_performed,entry_date)
VALUES(:OLD.profile_id,:OLD.product_code,:OLD.from_range,:OLD.to_range,:OLD.interest_type,:OLD.interest_value,'D',sysdate);
END IF;
END;




DROP TABLE CHANNEL_USER_LOAN_INFO;

CREATE TABLE CHANNEL_USER_LOAN_INFO
(
  USER_ID              VARCHAR2(15 BYTE)  NOT NULL ,
  PROFILE_ID           VARCHAR2(10 BYTE) ,
  PRODUCT_CODE         VARCHAR2(10 BYTE) NOT NULL,
LOAN_THREHOLD          NUMBER(12)  NOT NULL,
LOAN_AMOUNT            NUMBER(12)  NOT NULL,
LOAN_GIVEN             VARCHAR2(1 BYTE) DEFAULT 'N',
LOAN_GIVEN_AMOUNT      NUMBER(12) ,
LAST_LOAN_DATE         DATE ,
LAST_LOAN_TXN_ID       VARCHAR2(20 BYTE) ,
SETTLEMENT_ID          VARCHAR2(20 BYTE) ,
SETTLEMENT_DATE        DATE ,
SETTLEMENT_LOAN_AMOUNT  NUMBER(12) ,
SETTLEMENT_LOAN_INTEREST    NUMBER(12) ,
LOAN_TAKEN_FROM         VARCHAR2(15 BYTE) ,
SETTLEMENT_TO         VARCHAR2(15 BYTE) ,
OPTINOUT_ALLOWED       VARCHAR2(1 BYTE) DEFAULT 'Y',
OPTINOUT_ON            DATE ,
OPTINOUT_BY            VARCHAR2(15 BYTE) ,
CREATED_BY                      VARCHAR2(20 BYTE) NOT NULL,
  CREATED_ON                      DATE          NOT NULL,
  MODIFIED_BY                     VARCHAR2(20 BYTE) NOT NULL,
  MODIFIED_ON                     DATE          NOT NULL
);

DROP TABLE CHANNEL_USER_LOAN_INFO_HISTORY;

CREATE TABLE CHANNEL_USER_LOAN_INFO_HISTORY
(
  USER_ID              VARCHAR2(15 BYTE)  NOT NULL ,
  PROFILE_ID           VARCHAR2(10 BYTE) ,
  PRODUCT_CODE         VARCHAR2(10 BYTE) NOT NULL,
LOAN_THREHOLD          NUMBER(12)  NOT NULL,
LOAN_AMOUNT            NUMBER(12)  NOT NULL,
LOAN_GIVEN             VARCHAR2(1 BYTE) DEFAULT 'N',
LOAN_GIVEN_AMOUNT      NUMBER(12) ,
LAST_LOAN_DATE         DATE ,
LAST_LOAN_TXN_ID       VARCHAR2(20 BYTE) ,
SETTLEMENT_ID          VARCHAR2(20 BYTE) ,
SETTLEMENT_DATE        DATE ,
SETTLEMENT_LOAN_AMOUNT  NUMBER(12) ,
SETTLEMENT_LOAN_INTEREST    NUMBER(12) ,
LOAN_TAKEN_FROM         VARCHAR2(15 BYTE) ,
SETTLEMENT_TO         VARCHAR2(15 BYTE) ,
OPTINOUT_ALLOWED       VARCHAR2(1 BYTE) DEFAULT 'Y',
OPTINOUT_ON            DATE ,
OPTINOUT_BY            VARCHAR2(15 BYTE),
CREATED_BY                      VARCHAR2(20 BYTE) NOT NULL,
  CREATED_ON                      DATE          NOT NULL,
  MODIFIED_BY                     VARCHAR2(20 BYTE) NOT NULL,
  MODIFIED_ON                     DATE          NOT NULL,
  OPERATION_PERFORMED        VARCHAR2(1 BYTE),
  ENTRY_DATE                 DATE 
)PARTITION BY RANGE (ENTRY_DATE)
INTERVAL( NUMTODSINTERVAL(1,'DAY'))
(  
  PARTITION VALUES LESS THAN (TO_DATE(' 2021-09-30 00:00:00', 'SYYYY-MM-DD HH24:MI:SS', 'NLS_CALENDAR=GREGORIAN'))
    LOGGING
    NOCOMPRESS
    TABLESPACE P_C2SDATA1
    PCTFREE    10
    INITRANS   1
    MAXTRANS   255
    STORAGE    (
                INITIAL          8M
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                BUFFER_POOL      DEFAULT
               ));

              
DROP TRIGGER TRIG_CHANNEL_USER_LOAN_HISTORY;
CREATE OR REPLACE TRIGGER "TRIG_CHANNEL_USER_LOAN_HISTORY" 
AFTER INSERT OR UPDATE OR DELETE ON CHANNEL_USER_LOAN_INFO FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,'I',sysdate);
ELSIF UPDATING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:NEW.user_id,:NEW.profile_id,:NEW.product_code,:NEW.loan_threhold,:NEW.loan_amount,:NEW.loan_given,:NEW.loan_given_amount,:NEW.last_loan_date,:NEW.last_loan_txn_id,:NEW.settlement_id,:NEW.settlement_date,:NEW.settlement_loan_amount,:NEW.settlement_loan_interest,:NEW.loan_taken_from,:NEW.settlement_to,:NEW.optinout_allowed,:NEW.optinout_on,:NEW.optinout_by,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,'U',sysdate);
ELSIF DELETING THEN
INSERT INTO CHANNEL_USER_LOAN_INFO_HISTORY(user_id,profile_id,product_code,loan_threhold,loan_amount,loan_given,loan_given_amount,last_loan_date,last_loan_txn_id,settlement_id,settlement_date,settlement_loan_amount,settlement_loan_interest,loan_taken_from,settlement_to,optinout_allowed,optinout_on,optinout_by,created_by,created_on,modified_by,modified_on,operation_performed,entry_date)
VALUES(:OLD.user_id,:OLD.profile_id,:OLD.product_code,:OLD.loan_threhold,:OLD.loan_amount,:OLD.loan_given,:OLD.loan_given_amount,:OLD.last_loan_date,:OLD.last_loan_txn_id,:OLD.settlement_id,:OLD.settlement_date,:OLD.settlement_loan_amount,:OLD.settlement_loan_interest,:OLD.loan_taken_from,:OLD.settlement_to,:OLD.optinout_allowed,:OLD.optinout_on,:OLD.optinout_by,:OLD.created_by,:OLD.created_on,:OLD.modified_by,:OLD.modified_on,'D',sysdate);
END IF;
END;


DROP INDEX PK_CHANNEL_USER_LOAN_INFO;
CREATE UNIQUE INDEX PK_CHANNEL_USER_LOAN_INFO ON CHANNEL_USER_LOAN_INFO
(USER_ID,PRODUCT_CODE);

DROP INDEX INDEX_CLP_LOAN_GIVEN;

CREATE INDEX INDEX_CLP_LOAN_GIVEN ON CHANNEL_USER_LOAN_INFO
(LOAN_GIVEN);


ALTER TABLE CHANNEL_USER_LOAN_INFO DROP CONSTRAINT PK_CHANNEL_USER_LOAN_INFO;
ALTER TABLE CHANNEL_USER_LOAN_INFO ADD (
  CONSTRAINT PK_CHANNEL_USER_LOAN_INFO
PRIMARY KEY
(USER_ID,PRODUCT_CODE));

ALTER TABLE CHANNEL_USER_LOAN_INFO ADD (
  CONSTRAINT FK_USER_LOAN_PROFILE_ID
FOREIGN KEY (profile_id)
    REFERENCES LOAN_PROFILES(profile_id));

ALTER TABLE CHANNEL_USER_LOAN_INFO ADD (
  CONSTRAINT FK_USER_LOAN_USER_ID    
FOREIGN KEY (user_id)
    REFERENCES users(user_id));

INSERT INTO PROCESS_STATUS (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT) VALUES('USRLOANLIST',sysdate, 'C', sysdate, sysdate, 180, 1440, 'User Loan Process', 'NG', 0);



INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('USERWISE_LOAN_ENABLE', 'Loan Enable or disable User Wise', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Loan Enable or disable User Wise', 'Y', 'Y', 'C2S', 'Loan Enable or disable User Wise', sysdate, 'ADMIN', sysdate, 'ADMIN', 'true/false', 'Y');


INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('ALLOW_TRANSACTION_IF_LOAN_SETTLEMENT_FAIL', 'Allow Transaction if LOAN settelment fail', 'SYSTEMPRF', 'STRING', 'RETURN, RCREV', NULL, NULL, 50, 'This flag allow transaction to complete if LOAN settelment condition gets failed', 'N', 'Y', 'C2S', 'This flag allow transaction to complete if LOAN settelment condition gets failed', sysdate, 'ADMIN', sysdate, 'ADMIN', 'RETURN,RCREV', 'Y');


INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('ALLOW_GATEWAYCODE_FOR_LOAN_SETTLEMENT', 'Allow Gateway Code for  LOAN settelment', 'SYSTEMPRF', 'STRING', 'EXTGW,USSD,SMSC', NULL, NULL, 50, 'Allow Gateway Code for  LOAN settelment', 'N', 'Y', 'C2S', 'Allow Gateway Code for  LOAN settelment', sysdate, 'ADMIN', sysdate, 'ADMIN', 'EXTGW,USSD,SMSC', 'Y');



INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('RESTRICT_TRANSACTION_FOR_LOAN_SETTLEMENT', 'Restrict Txn Code for  LOAN settelment', 'SYSTEMPRF', 'STRING', 'FOC', NULL, NULL, 50, 'Restrict Txn Code for  LOAN settelment', 'N', 'Y', 'C2S', 'Restrict Txn Code for  LOAN settelment', sysdate, 'ADMIN', sysdate, 'ADMIN', 'FOC', 'Y');


--------------------------pages--------------------------------------
INSERT INTO ROLES
(DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE, ACCESS_TYPE)
VALUES('OPERATOR', 'LOANPROFILE', 'Loan Profile', 'Profile Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'LOANPROFILE', '1');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LOANPRF001', 'PROFILES', '/loanProfileAction.do?method=selectDomainAndCategory', 'Loan Profile', 'Y', 5, '2', '1', '/loanProfileAction.do?method=selectDomainAndCategory');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LOANPRF01A', 'PROFILES', '/loanProfileAction.do?method=selectDomainAndCategory', 'Loan Profile', 'N', 5, '2', '1', '/loanProfileAction.do?method=selectDomainAndCategory');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LOANPRF002', 'PROFILES', '/jsp/profile/loanProfileDetail.jsp', 'Loan Profile', 'N', 5, '2', '1', '/jsp/profile/loanProfileDetail.jsp');

INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LOANPRF003', 'PROFILES', '/jsp/profile/loanProfileFinalDetails.jsp', 'Loan Profile', 'N', 5, '2', '1', '/jsp/profile/loanProfileFinalDetails.jsp');
INSERT INTO PAGES
(PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID, SPRING_PAGE_URL)
VALUES('LOANPRF004', 'PROFILES', '/jsp/profile/loanProfileList.jsp', 'Loan Profile', 'N', 5, '2', '1', '/jsp/profile/loanProfileList.jsp');



INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LOANPROFILE', 'LOANPRF001', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LOANPROFILE', 'LOANPRF01A', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LOANPROFILE', 'LOANPRF002', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LOANPROFILE', 'LOANPRF003', '1');

INSERT INTO PAGE_ROLES
(ROLE_CODE, PAGE_CODE, APPLICATION_ID)
VALUES('LOANPROFILE', 'LOANPRF004', '1');



----------------------lookups-----------------------------------------------

INSERT INTO LOOKUP_TYPES
(LOOKUP_TYPE, LOOKUP_TYPE_NAME, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, MODIFIED_ALLOWED)
VALUES('LPTYP', 'Daily or Hourly', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN', 'N');

INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('D', 'Daily', 'LPTYP', 'Y', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN');
INSERT INTO LOOKUPS
(LOOKUP_CODE, LOOKUP_NAME, LOOKUP_TYPE, STATUS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY)
VALUES('H', 'Hourly', 'LPTYP', 'Y', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-06-07 00:00:00.000000', 'ADMIN');


-------------------------System preferences----------------------------------

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('LOAN_PROFILE_SLAB_LENGTH', 'Max number of slabs in Loan Profile', 'SYSTEMPRF', 'INT', '2', 1, 10, 10, 'Max number of slabs in Loan Profile', 'Y', 'Y', 'C2S', 'Max number of slabs in Loan Profile', TIMESTAMP '2021-05-17 07:58:52.000000', 'ADMIN', TIMESTAMP '2021-05-17 07:58:52.000000', 'SUPERADMIN', NULL, 'Y');

-----------------------ID generator--------------------------------------

INSERT INTO IDS
(ID_YEAR, ID_TYPE, NETWORK_CODE, LAST_NO, LAST_INITIALISED_DATE, FREQUENCY, DESCRIPTION)
VALUES('ALL', 'LOAN', 'NG', 3, SYSDATE, 'NA', 'Loan Profile ID');

INSERT INTO SYSTEM_PREFERENCES (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) VALUES('BLOCK_GATEWAYCODE_FOR_LOAN_SETTLEMENT', 'Block Gateway Code for  LOAN settelment', 'SYSTEMPRF', 'STRING', 'WEB,GSTREXTGW', NULL, NULL, 50, 'Block Gateway Code for  LOAN settelment', 'N', 'Y', 'C2S', 'Block Gateway Code for  LOAN settelment', sysdate, 'ADMIN', sysdate, 'ADMIN', 'WEB,GSTREXTGW', 'Y');








