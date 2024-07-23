ALTER TABLE CURRENCY_CONVERSION_MAPPING
 DROP PRIMARY KEY CASCADE;
DROP TABLE CURRENCY_CONVERSION_MAPPING CASCADE CONSTRAINTS;

CREATE TABLE CURRENCY_CONVERSION_MAPPING
(
  SOURCE_CURRENCY_CODE  VARCHAR2(10 BYTE),
  TARGET_CURRENCY_CODE  VARCHAR2(10 BYTE),
  SOURCE_CURRENCY_NAME  NVARCHAR2(50),
  TARGET_CURRENCY_NAME  NVARCHAR2(50),
  COUNTRY               VARCHAR2(10 BYTE),
  CONVERSION            NUMBER(10),
  MULT_FACTOR           NUMBER(10),
  DESCRIPTION           VARCHAR2(1 BYTE),
  CREATED_ON            DATE,
  CREATED_BY            VARCHAR2(20 BYTE),
  MODIFIED_ON           DATE,
  MODIFIED_BY           VARCHAR2(20 BYTE),
  REFERENCE_ID          VARCHAR2(30 BYTE)
);


CREATE UNIQUE INDEX CURRENCY_CONVERSION_MAPPING_PK ON CURRENCY_CONVERSION_MAPPING
(SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, COUNTRY);


CREATE OR REPLACE TRIGGER TRIG_CURRENCY_CONV_HISTORY 
AFTER DELETE OR INSERT OR UPDATE
ON CURRENCY_CONVERSION_MAPPING 
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
   IF INSERTING
   THEN
      INSERT INTO currency_conv_mapp_history
                  (source_currency_code, target_currency_code,
                   source_currency_name, target_currency_name,
                   country, conversion,                    mult_factor, description,
                   created_on, modified_on,
                   modified_by, entry_date, operation_performed, reference_id
                  )
           VALUES (:new.source_currency_code, :new.target_currency_code,
                   :new.source_currency_name, :new.target_currency_name,
                   :new.country, :new.conversion, 
                   :new.mult_factor, :new.description,
                   :new.created_on, :new.modified_on,
                   :new.modified_by, sysdate, 'I', :new.reference_id
                  );
   ELSIF UPDATING
   THEN
         INSERT INTO currency_conv_mapp_history
                              (source_currency_code, target_currency_code,
                               source_currency_name, target_currency_name,
                               country, conversion,mult_factor, description,
                               created_on, modified_on,
                               modified_by, entry_date, operation_performed, reference_id
                              )
         VALUES (:old.source_currency_code, :old.target_currency_code,
                               :old.source_currency_name, :old.target_currency_name,
                               :old.country, :old.conversion, 
                               :old.mult_factor, :old.description,
                               :old.created_on, :old.modified_on,
                               :old.modified_by,sysdate,'U', :old.reference_id
                              );
   ELSIF DELETING THEN
         INSERT INTO currency_conv_mapp_history
                              (source_currency_code, target_currency_code,
                               source_currency_name, target_currency_name,
                               country, conversion,                    mult_factor, description,
                               created_on, modified_on,
                               modified_by, entry_date, operation_performed, reference_id
                              )
                       VALUES (:old.source_currency_code, :old.target_currency_code,
                               :old.source_currency_name, :old.target_currency_name,
                               :old.country, :old.conversion, 
                               :old.mult_factor, :old.description,
                               :old.created_on, :old.modified_on,
                               :old.modified_by, sysdate, 'D', :old.reference_id
                              );
   END IF;
END;
/


ALTER TABLE CURRENCY_CONVERSION_MAPPING ADD (
  CONSTRAINT CURRENCY_CONVERSION_MAPPING_PK
 PRIMARY KEY
 (SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, COUNTRY);

			   
DROP TABLE CURRENCY_CONV_MAPP_HISTORY CASCADE CONSTRAINTS;

CREATE TABLE CURRENCY_CONV_MAPP_HISTORY
(
  SOURCE_CURRENCY_CODE  VARCHAR2(10 BYTE),
  TARGET_CURRENCY_CODE  VARCHAR2(10 BYTE),
  SOURCE_CURRENCY_NAME  NVARCHAR2(50),
  TARGET_CURRENCY_NAME  NVARCHAR2(50),
  COUNTRY               VARCHAR2(10 BYTE),
  CONVERSION            NUMBER(10),
  MULT_FACTOR           NUMBER(10),
  DESCRIPTION           VARCHAR2(1 BYTE),
  CREATED_ON            DATE,
  CREATED_BY            VARCHAR2(20 BYTE),
  MODIFIED_ON           DATE,
  MODIFIED_BY           VARCHAR2(20 BYTE),
  ENTRY_DATE            DATE,
  OPERATION_PERFORMED   VARCHAR2(1 BYTE),
  REFERENCE_ID          VARCHAR2(30 BYTE)
);

Alter table CURRENCY_CONVERSION_MAPPING add STATUS VARCHAR2(2 BYTE);
Alter table CURRENCY_CONV_MAPP_HISTORY add STATUS VARCHAR2(2 BYTE);
		   

CREATE OR REPLACE TRIGGER TRIG_CURRENCY_CONV_HISTORY 
AFTER DELETE OR INSERT OR UPDATE
ON CURRENCY_CONVERSION_MAPPING 
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
   IF INSERTING
   THEN
      INSERT INTO currency_conv_mapp_history
                  (source_currency_code, target_currency_code,
                   source_currency_name, target_currency_name,
                   country, conversion,                    mult_factor, description,
                   created_on, modified_on,
                   modified_by, entry_date, operation_performed, reference_id,status
                  )
           VALUES (:new.source_currency_code, :new.target_currency_code,
                   :new.source_currency_name, :new.target_currency_name,
                   :new.country, :new.conversion, 
                   :new.mult_factor, :new.description,
                   :new.created_on, :new.modified_on,
                   :new.modified_by, sysdate, 'I', :new.reference_id , :new.status
                  );
   ELSIF UPDATING
   THEN
         INSERT INTO currency_conv_mapp_history
                              (source_currency_code, target_currency_code,
                               source_currency_name, target_currency_name,
                               country, conversion,mult_factor, description,
                               created_on, modified_on,
                               modified_by, entry_date, operation_performed, reference_id,status
                              )
         VALUES (:old.source_currency_code, :old.target_currency_code,
                               :old.source_currency_name, :old.target_currency_name,
                               :old.country, :old.conversion, 
                               :old.mult_factor, :old.description,
                               :old.created_on, :old.modified_on,
                               :old.modified_by,sysdate,'U', :old.reference_id,:old.status
                              );
   ELSIF DELETING THEN
         INSERT INTO currency_conv_mapp_history
                              (source_currency_code, target_currency_code,
                               source_currency_name, target_currency_name,
                               country, conversion,                    mult_factor, description,
                               created_on, modified_on,
                               modified_by, entry_date, operation_performed, reference_id,status
                              )
                       VALUES (:old.source_currency_code, :old.target_currency_code,
                               :old.source_currency_name, :old.target_currency_name,
                               :old.country, :old.conversion, 
                               :old.mult_factor, :old.description,
                               :old.created_on, :old.modified_on,
                               :old.modified_by, sysdate, 'D', :old.reference_id,:old.status
                              );
   END IF;
END;
/


SET DEFINE OFF;
Insert into CURRENCY_CONVERSION_MAPPING
   (SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, SOURCE_CURRENCY_NAME, TARGET_CURRENCY_NAME, COUNTRY, 
    CONVERSION, MULT_FACTOR, DESCRIPTION, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, REFERENCE_ID, STATUS)
 Values
   ('USD', 'INR', 'DOLLER', 'RUPEE', 'US', 
    1, 1, '', TO_DATE('08/04/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    TO_DATE('09/02/2016 12:48:42', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', '8989898989', 'Y');
Insert into CURRENCY_CONVERSION_MAPPING
   (SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, SOURCE_CURRENCY_NAME, TARGET_CURRENCY_NAME, COUNTRY, 
    CONVERSION, MULT_FACTOR, DESCRIPTION, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, REFERENCE_ID, STATUS)
 Values
   ('BIRR', 'USD', 'BIRR', 'DOLLAR', 'US', 
    11, 1, '', TO_DATE('08/04/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    TO_DATE('08/16/2016 11:42:48', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', '8989898989', 'Y');
Insert into CURRENCY_CONVERSION_MAPPING
   (SOURCE_CURRENCY_CODE, TARGET_CURRENCY_CODE, SOURCE_CURRENCY_NAME, TARGET_CURRENCY_NAME, COUNTRY, 
    CONVERSION, MULT_FACTOR, DESCRIPTION, CREATED_ON, CREATED_BY, 
    MODIFIED_ON, MODIFIED_BY, REFERENCE_ID, STATUS)
 Values
   ('INR', 'USD', 'RUPEE', 'DOLLAR', 'US', 
    1, 1, '', TO_DATE('08/13/2016 17:54:37', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', 
    TO_DATE('08/13/2016 17:54:37', 'MM/DD/YYYY HH24:MI:SS'), 'SYSTEM', '8989898989', 'Y');
COMMIT;
