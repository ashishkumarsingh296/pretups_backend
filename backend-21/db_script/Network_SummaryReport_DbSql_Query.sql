Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR001', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'Y', 
    517, '2', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR01A', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'N', 
    517, '2', '1');
Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR1DMM', 'MASTER', '/master/networkSummaryReport.form', 'Network Summary Report', 'Y', 
    517, '1', '1');



Insert into ROLES
   (DOMAIN_TYPE, ROLE_CODE, ROLE_NAME, GROUP_NAME, STATUS, 
    ROLE_TYPE, FROM_HOUR, TO_HOUR, GROUP_ROLE, APPLICATION_ID, 
    GATEWAY_TYPES, ROLE_FOR, IS_DEFAULT, IS_DEFAULT_GROUPROLE)
 Values
   ('OPERATOR', 'NETWORKSUMRPT', 'Network Summary Report', 'Masters', 'Y', 
    'A', NULL, NULL, 'N', '1', 
    'WEB', 'B', 'N', 'N');

Insert into CATEGORY_ROLES
   (CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
 Values
   ('NWADM', 'NETWORKSUMRPT', '1');
   
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR001', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR002', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR003', '1');
Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR01A', '1');

Insert into PAGES
   (PAGE_CODE, MODULE_CODE, PAGE_URL, MENU_NAME, MENU_ITEM, 
    SEQUENCE_NO, MENU_LEVEL, APPLICATION_ID)
 Values
   ('NTWSMR1DMM', 'MASTER', '/selectServiceType.do?method=loadServiceTypeForNetworkServices', 'Network Summary Report', 'Y', 
    518, '1', '1');


Insert into PAGE_ROLES
   (ROLE_CODE, PAGE_CODE, APPLICATION_ID)
 Values
   ('NETWORKSUMRPT', 'NTWSMR1DMM', '1');   









Insert into WEB_SERVICES_TYPES
   (WEB_SERVICE_TYPE, DESCRIPTION, RESOURCE_NAME, VALIDATOR_NAME, FORMBEAN_NAME, 
    CONFIG_PATH, WEB_SERVICE_URL, IS_RBA_REQUIRE, IS_DATA_VALIDATION_REQUIRE)
 Values
   ('NETSUMMDNLD', 'Download User List', 'NetworkSummaryReportRestService', NULL, NULL, 
    'configfiles/restservice', '/rest/networkSummary/download-monthly', 'N', 'N');

 


CREATE TABLE PROC_ERROR_LOG
(
  DESC1  VARCHAR2(2000 BYTE)
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


DROP SEQUENCE GTRANSSUMM_ID;

CREATE SEQUENCE TRANSSUMM_ID
  START WITH 1
  MAXVALUE 999999999999
  MINVALUE 0
  CYCLE
  NOCACHE
  NOORDER;



CREATE OR REPLACE PROCEDURE PRETUPS_BL660.dump_trans_summary (
   start_date_time   IN   VARCHAR2
)
AS
   err_val                VARCHAR2 (2000);
   check_flag             NUMBER (2);
   check_flag2              NUMBER (2);
   alreadydoneexception   EXCEPTION;
      setelmentnotdoneexception   EXCEPTION;
-- Thrown if the Proc has already been executed already, and the process termnates
BEGIN
   SELECT COUNT (*)
     INTO check_flag
     FROM process_status p
    WHERE executed_upto = start_date_time
      AND  p.PROCESS_ID = 'RUNTRNSUM'
      AND SCHEDULER_STATUS = 'C';
    
     SELECT COUNT (*)
     INTO check_flag2
     from c2s_transfers c
     WHERE transfer_date = start_date_time
     and transfer_status in ('206','205');
    
   IF check_flag <> 0 
   THEN
                  INSERT INTO proc_error_log
                              (desc1
                              )
                       VALUES (   'RUNTRNSUM'
                               || 'RUN_DUPLICATE  At Time :-  '
                               || TO_CHAR (SYSDATE, 'DDMMRRRRHHMMSS')
                               || start_date_time
                               || SYSDATE
                               || 'F'
                              );

                  DBMS_OUTPUT.put_line
                              ('The Procedure is executed several times for the same date');
                  RAISE alreadydoneexception;
                  COMMIT;
                 
               
           ELSIF check_flag2 <>0
               THEN
                  INSERT INTO proc_error_log
                              (desc1
                              )
                       VALUES (   'RUNTRNSUM'
                               || 'The settelment process not run yet'
                               || TO_CHAR (SYSDATE, 'DDMMRRRRHHMMSS')
                               || start_date_time
                               || SYSDATE
                               || 'F'
                              );

                  DBMS_OUTPUT.put_line
                              ('The settelment process not run yet');
                  RAISE setelmentnotdoneexception;
                  COMMIT;
                  
                  
               
    ELSE 
 
      
      INSERT INTO transaction_summary
         SELECT transsumm_id.NEXTVAL, x.*
           FROM (SELECT   transfer_date, TO_CHAR (transfer_date_time, 'HH24'),
                          c.network_code, c.interface_id, c.sender_category,
                          c.service_type, c.sub_service,
                          request_gateway_code g_c,
                          SUM (DECODE (transfer_status, 200, 1, 0)
                              ) success_count,
                          SUM (DECODE (transfer_status, 200, 0, 250, 0, 1)
                              ) error_count,
                          SUM (DECODE (transfer_status,
                                       200, c.transfer_value,
                                       0
                                      )
                              ) success_amt,
                          SUM (DECODE (transfer_status,
                                       200, 0,
                                       250, 0,
                                       c.transfer_value
                                      )
                              ) error_amt,
                          SUM (receiver_tax1_value + receiver_tax2_value
                              ) tax_amt,
                          SUM (c.receiver_access_fee) access_fee,
                          SUM (c.receiver_transfer_value) as REC
                     FROM c2s_transfers c
                    WHERE transfer_date = start_date_time
                 GROUP BY transfer_date,
                          c.network_code,
                          c.interface_id,
                          c.service_type,
                          c.sub_service,
                          TO_CHAR (transfer_date_time, 'HH24'),
                          request_gateway_code,
                          c.sender_category) x;

      COMMIT;
   END IF;
EXCEPTION
   WHEN NO_DATA_FOUND
   THEN
      err_val := SQLERRM;

      INSERT INTO proc_error_log
           VALUES ('INSERT_INTO_TRANS-EXCEPTION NO_DATA_FOUND' || err_val);

      COMMIT;
   WHEN OTHERS
   THEN
      err_val := SQLERRM;

      INSERT INTO proc_error_log
           VALUES ('INSERT_INTO_TRANS-EXCEPTION in  OTHERS' || err_val);

      COMMIT;
END;
/


Insert into PROCESS_STATUS
   (PROCESS_ID, START_DATE, SCHEDULER_STATUS, EXECUTED_UPTO, EXECUTED_ON, 
    EXPIRY_TIME, BEFORE_INTERVAL, DESCRIPTION, NETWORK_CODE, RECORD_COUNT)
 Values
   ('RUNTRNSUM', TO_DATE('02/02/2016 14:54:27', 'MM/DD/YYYY HH24:MI:SS'), 'C', TO_DATE('02/01/2016 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('02/02/2016 14:54:27', 'MM/DD/YYYY HH24:MI:SS'), 
    360, 1440, 'RUN_PROCEDURE_TRANS_SUMMARY', 'BD', '0');
