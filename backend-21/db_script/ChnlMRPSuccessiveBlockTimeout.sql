set head off;
set pagesize 0;
set define ^
set timing on;
set echo on;

spool MRPSuccessiveBlockTimeout.log;
-----------------DDL and DML Script--------------------------------
ALTER TABLE SYSTEM_PREFERENCES MODIFY NAME  NVARCHAR2(200);
ALTER TABLE SYSTEM_PRF_HISTORY MODIFY NAME  NVARCHAR2(200);
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) Values('SUCC_BLOCK_TIME_O2C', 'Last Successive MRP Block Time in second for O2C transaction', 'SYSTEMPRF', 'NUMBER', '300',NULL, NULL, 5, 'Last Successive MRP Block Time in second for O2C transaction', 'Y','Y', 'C2S', 'Last Successive MRP Block Time in second for O2C transaction', sysdate, 'ADMIN',sysdate, 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) Values('SUCC_BLOCK_TIME_C2C', 'Last Successive MRP Block Time in second for C2C transaction', 'SYSTEMPRF', 'NUMBER', '300',NULL, NULL, 5, 'Last Successive MRP Block Time in second for C2C transaction', 'Y','Y', 'C2S', 'Last Successive MRP Block Time in second for C2C transaction', sysdate, 'ADMIN',sysdate, 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) Values('MRP_BLOCK_TIME_ALLOWED_CHNL', 'Last Successive MRP Block Time for channel transaction Allowed', 'SYSTEMPRF', 'BOOLEAN', 'true',NULL, NULL, 5, 'Last Successive MRP Block Time for channel transaction Allowed', 'Y','Y', 'C2S', 'Last Successive MRP Block Time for channel transaction Allowed', sysdate, 'ADMIN',sysdate, 'SU0001', NULL, 'Y');
Insert into SYSTEM_PREFERENCES(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE,MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED,DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY,MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE) Values('MRP_BLOCK_TIMEOUT_SERVICES_GATEWAY_CHNL', 'Last Successive MRP Block Time for channel transaction with service type and request gateway code optional', 'SYSTEMPRF', 'STRING', 'O2C,C2C',0, 9999, 20, 'Last Successive MRP Block Time for channel transaction with service type and request gateway code optional', 'Y','Y', 'C2S', 'Last Successive MRP Block Time for channel transaction with service type and request gateway code optional like O2C-USSD|WEB|EXTGW,C2C-USSD|WEB|EXTGW', sysdate, 'ADMIN',sysdate, 'ADMIN', 'O2C,C2C,O2C-USSD|WEB|EXTGW,C2C-USSD|WEB|EXTGW', 'Y');
-----------------Enable roles--------------------------------
update roles set status='Y' where role_code in ('BC2CINITIATE','BC2CAPPROVE') and status<>'Y';
--update chnl_transfer_rules set transfer_allowed='Y' WHERE network_code= 'ML' AND domain_code= 'WS' AND to_category='WS' AND status='Y' AND TYPE='CHANNEL';
------------------------------------------------------------
COMMIT;
spool off;