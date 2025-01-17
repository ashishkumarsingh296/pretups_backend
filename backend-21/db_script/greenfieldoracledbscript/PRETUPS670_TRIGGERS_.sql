DROP TRIGGER TRIG_USER_PHONES_PIN_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_USER_PHONES_PIN_HISTORY"  
AFTER INSERT OR UPDATE
OF SMS_PIN  ON  USER_PHONES
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE

   vstatus VARCHAR2 (2);
   vapprovaldate  DATE ;
   vapprovaltwodate  DATE ;
   vappvalue VARCHAR2 (10);
   vusertype VARCHAR2(10);

BEGIN

     SELECT STATUS,LEVEL1_APPROVED_ON,LEVEL2_APPROVED_ON,USER_TYPE  INTO vstatus,vapprovaldate,vapprovaltwodate,vusertype   FROM USERS WHERE USER_ID= :NEW.user_id;

     SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';

   BEGIN
  IF INSERTING THEN
    IF   vstatus='Y' THEN
     INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
          VALUES(:NEW.user_id, :NEW.msisdn, :NEW.sms_pin, vusertype, :NEW.phone_profile, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PIN');
         END IF;
   END IF;

  IF UPDATING THEN


       IF :NEW.SMS_PIN <> :OLD.SMS_PIN AND vstatus='Y' THEN
         INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
          VALUES(:NEW.user_id, :NEW.msisdn, :NEW.sms_pin, vusertype, :NEW.phone_profile, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PIN');

    END IF;


     IF  vstatus='Y' AND :NEW.PIN_MODIFIED_ON = vapprovaldate AND vappvalue ='1' THEN
             INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(:NEW.user_id, :NEW.msisdn, :NEW.sms_pin, vusertype, :NEW.phone_profile, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PIN');
    END IF;

    IF  vstatus='Y' AND :NEW.PIN_MODIFIED_ON = vapprovaltwodate AND vappvalue ='2' THEN
             INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid,pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(:NEW.user_id, :NEW.msisdn, :NEW.sms_pin,vusertype, :NEW.phone_profile, TRUNC(:NEW.modified_on), :NEW.modified_on, :NEW.modified_by, 'PIN');

    END IF;


END IF;
END;
END;
/


DROP TRIGGER TRIG_USER_BALANCES_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_USER_BALANCES_HISTORY"  
AFTER INSERT or UPDATE or DELETE OF balance ON USER_BALANCES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(:NEW.user_id,:NEW.network_code,:NEW.network_code_for,:NEW.product_code,
0,:NEW.balance,:NEW.last_transfer_type, :NEW.last_transfer_no,:NEW.last_transfer_on,'I',sysdate,sysdate,:NEW.balance_type);
ELSIF UPDATING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(:NEW.user_id,:NEW.network_code,:NEW.network_code_for,:NEW.product_code,
:OLD.balance,:NEW.balance,:NEW.last_transfer_type,:NEW.last_transfer_no,
:NEW.last_transfer_on,'U',sysdate,:NEW.daily_balance_updated_on,:NEW.balance_type);
ELSIF DELETING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on, operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(:OLD.user_id,:OLD.network_code,:OLD.network_code_for,:OLD.product_code,
:OLD.balance,:NEW.balance,:NEW.last_transfer_type,:NEW.last_transfer_no,
:NEW.last_transfer_on,'D',sysdate,:OLD.daily_balance_updated_on,:OLD.balance_type);
END IF;
END;
/


DROP TRIGGER TRIG_TRANSFER_RULES_HISTORY;

CREATE OR REPLACE TRIGGER TRIG_TRANSFER_RULES_HISTORY
AFTER INSERT OR UPDATE OR DELETE ON TRANSFER_RULES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(:NEW.MODULE, :NEW.network_code,:NEW.sender_subscriber_type, :NEW.receiver_subscriber_type,
:NEW.sender_service_class_id,:NEW.receiver_service_class_id,:NEW.created_on,:NEW.created_by,
:NEW.modified_on,:NEW.modified_by,:NEW.card_group_set_id,:NEW.status,'I',SYSDATE,:NEW.sub_service,:NEW.allowed_days,:NEW.allowed_series,:NEW.denied_series,:NEW.gateway_code,:NEW.category_code,:NEW.grade_code);
ELSIF UPDATING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(:NEW.MODULE, :NEW.network_code,:NEW.sender_subscriber_type, :NEW.receiver_subscriber_type,
:NEW.sender_service_class_id,:NEW.receiver_service_class_id,:NEW.created_on,:NEW.created_by,
:NEW.modified_on,:NEW.modified_by,:NEW.card_group_set_id,:NEW.status,'U',SYSDATE,:NEW.sub_service,:NEW.allowed_days,:NEW.allowed_series,:NEW.denied_series,:NEW.gateway_code,:NEW.category_code,:NEW.grade_code);
ELSIF DELETING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(:OLD.MODULE, :OLD.network_code,:OLD.sender_subscriber_type, :OLD.receiver_subscriber_type,
:OLD.sender_service_class_id,:OLD.receiver_service_class_id, :OLD.created_on, :OLD.created_by,
:OLD.modified_on, :OLD.modified_by,:OLD.card_group_set_id, :OLD.status, 'D',SYSDATE,:OLD.sub_service,:OLD.allowed_days,:OLD.allowed_series,:OLD.denied_series,:OLD.gateway_code,:NEW.category_code,:NEW.grade_code);
END IF;
END;
/


DROP TRIGGER TRIG_SYSTEM_PRF_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SYSTEM_PRF_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON SYSTEM_PREFERENCES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(:NEW.preference_code, :NEW.name,
:NEW.type, :NEW.value_type, :NEW.default_value, :NEW.min_value, :NEW.max_value,
:NEW.max_size, :NEW.description, :NEW.modified_allowed, :NEW.display, :NEW.module,
:NEW.remarks, :NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,
:NEW.allowed_values, :NEW.fixed_value,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(:NEW.preference_code, :NEW.name,
:NEW.type, :NEW.value_type, :NEW.default_value, :NEW.min_value, :NEW.max_value,
:NEW.max_size, :NEW.description, :NEW.modified_allowed, :NEW.display, :NEW.module,
:NEW.remarks, :NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,
:NEW.allowed_values, :NEW.fixed_value,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(:OLD.preference_code, :OLD.name,
:OLD.type, :OLD.value_type, :OLD.default_value, :OLD.min_value, :OLD.max_value,
:OLD.max_size, :OLD.description, :OLD.modified_allowed, :OLD.display, :OLD.module,
:OLD.remarks, :OLD.created_on, :OLD.created_by, :OLD.modified_on, :OLD.modified_by,
:OLD.allowed_values, :OLD.fixed_value,sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_SUBS_ROUTING_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SUBS_ROUTING_HISTORY"  
AFTER UPDATE OR DELETE ON SUBSCRIBER_ROUTING FOR EACH ROW
BEGIN
IF UPDATING THEN
INSERT INTO SUBSCRIBER_ROUTING_HISTORY(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(:NEW.msisdn,:NEW.interface_id,:NEW.subscriber_type,:NEW.external_interface_id,
:NEW.status,:NEW.created_by,:NEW.created_on,:NEW.modified_by,:NEW.modified_on,
:NEW.text1,:NEW.text2,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SUBSCRIBER_ROUTING_HISTORY(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(:OLD.msisdn,:OLD.interface_id,:OLD.subscriber_type,:OLD.external_interface_id,
:OLD.status,:OLD.created_by,:OLD.created_on,:OLD.modified_by,:OLD.modified_on,
:OLD.text1,:OLD.text2,sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_SUBSCRIBER_MSISDN_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SUBSCRIBER_MSISDN_HISTORY"  
AFTER DELETE ON SUBSCRIBER_MSISDN_ALIAS FOR EACH ROW
BEGIN
  IF INSERTING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
    values (:NEW.MSISDN,:NEW.USER_SID,:NEW.CREATED_ON,:NEW.CREATED_BY,:NEW.MODIFIED_ON,:NEW.MODIFIED_BY,:NEW.USER_NAME,:NEW.REQUEST_GATEWAY_CODE,:NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF UPDATING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
    values (:NEW.MSISDN,:NEW.USER_SID,:NEW.CREATED_ON,:NEW.CREATED_BY,:NEW.MODIFIED_ON,:NEW.MODIFIED_BY,:NEW.USER_NAME,:NEW.REQUEST_GATEWAY_CODE,:NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF DELETING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
    values (:OLD.MSISDN,:OLD.USER_SID,:OLD.CREATED_ON,:OLD.CREATED_BY,:OLD.MODIFIED_ON,:OLD.MODIFIED_BY,:OLD.USER_NAME,:OLD.REQUEST_GATEWAY_CODE,:OLD.REQUEST_GATEWAY_TYPE) ;
END IF;
END IF;
END IF;
END;
/


DROP TRIGGER TRIG_SERVICE_CLASS_PRF_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SERVICE_CLASS_PRF_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON SERVICE_CLASS_PREFERENCES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(:NEW.module, :NEW.network_code,
:NEW.service_class_id, :NEW.preference_code, :NEW.value, :NEW.created_on, :NEW.created_by,
:NEW.modified_on, :NEW.modified_by,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(:NEW.module, :NEW.network_code,
:NEW.service_class_id, :NEW.preference_code, :NEW.value, :NEW.created_on, :NEW.created_by,
:NEW.modified_on, :NEW.modified_by,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(:OLD.module, :OLD.network_code,
:OLD.service_class_id, :OLD.preference_code, :OLD.value, :OLD.created_on, :OLD.created_by,
:OLD.modified_on, :OLD.modified_by,sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_SCH_BATCH_MASTER_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SCH_BATCH_MASTER_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON SCHEDULED_BATCH_MASTER FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(:NEW.batch_id,
:NEW.status, :NEW.network_code, :NEW.total_count, :NEW.successful_count,
:NEW.upload_failed_count, :NEW.process_failed_count, :NEW.cancelled_count,
:NEW.scheduled_date, :NEW.parent_id, :NEW.owner_id, :NEW.parent_category,
:NEW.parent_domain, :NEW.SERVICE_TYPE, :NEW.created_on, :NEW.created_by,
:NEW.modified_on, :NEW.modified_by, :NEW.initiated_by, SYSDATE, 'I',:NEW.ref_batch_id,:NEW.active_user_id);
ELSIF UPDATING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(:NEW.batch_id,
:NEW.status, :NEW.network_code, :NEW.total_count, :NEW.successful_count,
:NEW.upload_failed_count, :NEW.process_failed_count, :NEW.cancelled_count,
:NEW.scheduled_date, :NEW.parent_id, :NEW.owner_id, :NEW.parent_category,
:NEW.parent_domain, :NEW.SERVICE_TYPE, :NEW.created_on, :NEW.created_by,
:NEW.modified_on, :NEW.modified_by, :NEW.initiated_by, SYSDATE, 'U',:NEW.ref_batch_id,:NEW.active_user_id);
ELSIF DELETING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(:OLD.batch_id,
:OLD.status, :OLD.network_code, :OLD.total_count, :OLD.successful_count,
:OLD.upload_failed_count, :OLD.process_failed_count, :OLD.cancelled_count,
:OLD.scheduled_date, :OLD.parent_id, :OLD.owner_id, :OLD.parent_category,
:OLD.parent_domain, :OLD.SERVICE_TYPE, :OLD.created_on, :OLD.created_by,
:OLD.modified_on, :OLD.modified_by, :OLD.initiated_by, SYSDATE, 'D',:OLD.ref_batch_id,:NEW.active_user_id);
END IF;
END;
/


DROP TRIGGER TRIG_SCH_BATCH_DETAIL_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_SCH_BATCH_DETAIL_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON SCHEDULED_BATCH_DETAIL FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(:NEW.batch_id, :NEW.subscriber_id,
:NEW.msisdn, :NEW.amount, :NEW.processed_on, :NEW.status, :NEW.transfer_id, :NEW.transfer_status,:NEW.error_code,
:NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,sysdate,'I',:NEW.sub_service);
ELSIF UPDATING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(:NEW.batch_id, :NEW.subscriber_id,
:NEW.msisdn, :NEW.amount, :NEW.processed_on, :NEW.status, :NEW.transfer_id, :NEW.transfer_status,:NEW.error_code,
:NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,sysdate,'U',:NEW.sub_service);
ELSIF DELETING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(:OLD.batch_id, :OLD.subscriber_id,
:OLD.msisdn, :OLD.amount, :OLD.processed_on, :OLD.status, :OLD.transfer_id, :OLD.transfer_status,:OLD.error_code,
:OLD.created_on, :OLD.created_by, :OLD.modified_on, :OLD.modified_by,sysdate,'D',:OLD.sub_service);
END IF;
END;
/


DROP TRIGGER TRIG_RESTRICTED_MSISDN_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_RESTRICTED_MSISDN_HISTORY"  
AFTER UPDATE OR DELETE ON RESTRICTED_MSISDNS FOR EACH ROW
BEGIN
IF UPDATING THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(:NEW.msisdn,:NEW.subscriber_id, :NEW.channel_user_id, :NEW.channel_user_category,
:NEW.owner_id, :NEW.employee_code, :NEW.employee_name, :NEW.network_code,
:NEW.monthly_limit, :NEW.min_txn_amount, :NEW.max_txn_amount, :NEW.total_txn_count,
:NEW.total_txn_amount, :NEW.black_list_status, :NEW.remark, :NEW.approved_by,
:NEW.approved_on, :NEW.associated_by, :OLD.status,:NEW.status, :NEW.association_date, :NEW.created_on,
:NEW.created_by, :NEW.modified_on, :NEW.modified_by, sysdate,
'U', :NEW.language, :NEW.country);
ELSIF DELETING THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(:OLD.msisdn,:OLD.subscriber_id, :OLD.channel_user_id, :OLD.channel_user_category,
:OLD.owner_id, :OLD.employee_code, :OLD.employee_name, :OLD.network_code,
:OLD.monthly_limit, :OLD.min_txn_amount, :OLD.max_txn_amount, :OLD.total_txn_count,
:OLD.total_txn_amount, :OLD.black_list_status, :OLD.remark, :OLD.approved_by,
:OLD.approved_on, :OLD.associated_by, :OLD.status, 'D', :OLD.association_date, :OLD.created_on,
:OLD.created_by, :OLD.modified_on, :OLD.modified_by, sysdate,
'D', :OLD.language, :OLD.country);
END IF;
END;
/


DROP TRIGGER TRIG_REG_INFO_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_REG_INFO_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON REG_INFO FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES (:NEW.msisdn, :NEW.transaction_id, :NEW.operation,
:NEW.created_by, :NEW.created_on, sysdate, 'I');
ELSIF UPDATING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(:NEW.msisdn, :NEW.transaction_id, :NEW.operation,
:NEW.created_by, :NEW.created_on, sysdate, 'U');
ELSIF DELETING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(:OLD.msisdn, :OLD.transaction_id, :OLD.operation,
:OLD.created_by, :OLD.created_on, sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_POS_KEY_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_POS_KEY_HISTORY"  
AFTER UPDATE OF MSISDN ON POS_KEYS FOR EACH ROW
BEGIN
 IF (:OLD.msisdn is not null AND :OLD.modified_on is not null) THEN
      INSERT INTO POS_KEY_HISTORY (icc_id,msisdn,modified_by,modified_on,
      created_on,created_by,network_code,sim_profile_id)
      VALUES  (:OLD.icc_id,:OLD.msisdn,:OLD.modified_by,:OLD.modified_on,
      :OLD.created_on,:OLD.created_by,:OLD.network_code,:OLD.sim_profile_id);
END IF;
END;
/


DROP TRIGGER TRIG_POST_PAY_CUST_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_POST_PAY_CUST_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON POSTPAID_CUST_PAY_MASTER FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(:NEW.queue_id, :NEW.network_code, :NEW.msisdn, :NEW.account_id, 
:NEW.amount, :NEW.transfer_id, :NEW.status, :NEW.entry_date, 
:NEW.description, :NEW.process_id, :NEW.process_date, :NEW.other_info, 
:NEW.service_type, :NEW.entry_type, :NEW.process_status, :NEW.module_code, 
:NEW.sender_id, :NEW.created_on, :NEW.source_type, :NEW.interface_id, 
:NEW.external_id, :NEW.service_class, :NEW.product_code, :NEW.tax_amount, 
:NEW.access_fee_amount, :NEW.entry_for, :NEW.bonus_amount, :NEW.sender_msisdn, 
:NEW.cdr_file_name, :NEW.gateway_code, :NEW.interface_amount, :NEW.imsi, 
'I',sysdate,:NEW.receiver_msisdn,:NEW.type );
ELSIF UPDATING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(:NEW.queue_id, :NEW.network_code, :NEW.msisdn, :NEW.account_id, 
:NEW.amount, :NEW.transfer_id, :NEW.status, :NEW.entry_date, 
:NEW.description, :NEW.process_id, :NEW.process_date, :NEW.other_info, 
:NEW.service_type, :NEW.entry_type, :NEW.process_status, :NEW.module_code, 
:NEW.sender_id, :NEW.created_on, :NEW.source_type, :NEW.interface_id, 
:NEW.external_id, :NEW.service_class, :NEW.product_code, :NEW.tax_amount, 
:NEW.access_fee_amount, :NEW.entry_for, :NEW.bonus_amount, :NEW.sender_msisdn, 
:NEW.cdr_file_name, :NEW.gateway_code, :NEW.interface_amount, :NEW.imsi, 
'U',sysdate,:NEW.receiver_msisdn,:NEW.type);
ELSIF DELETING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(:OLD.queue_id, :OLD.network_code, :OLD.msisdn, :OLD.account_id, 
:OLD.amount, :OLD.transfer_id, :OLD.status, :OLD.entry_date, 
:OLD.description, :OLD.process_id, :OLD.process_date, :OLD.other_info, 
:OLD.service_type, :OLD.entry_type, :OLD.process_status, :OLD.module_code, 
:OLD.sender_id, :OLD.created_on, :OLD.source_type, :OLD.interface_id, 
:OLD.external_id, :OLD.service_class, :OLD.product_code, :OLD.tax_amount, 
:OLD.access_fee_amount, :OLD.entry_for, :OLD.bonus_amount, :OLD.sender_msisdn, 
:OLD.cdr_file_name, :OLD.gateway_code, :OLD.interface_amount, :OLD.imsi, 
'D',sysdate,:OLD.receiver_msisdn,:OLD.type);
END IF;
END;
/


DROP TRIGGER TRIG_P2P_BUDDIES_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_P2P_BUDDIES_HISTORY"   
AFTER INSERT or UPDATE or DELETE ON P2P_BUDDIES FOR EACH ROW
DISABLE
BEGIN
IF INSERTING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (:NEW.buddy_msisdn,:NEW.parent_id,:NEW.buddy_seq_num,
:NEW.buddy_name,:NEW.status,null,null,null,null,null,
:NEW.created_on,:NEW.created_by,sysdate,'SYSTEM',:NEW.preferred_amount,
null,:NEW.prefix_id);
ELSIF UPDATING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (:OLD.buddy_msisdn,:OLD.parent_id,:OLD.buddy_seq_num,
:OLD.buddy_name,:NEW.status,:OLD.buddy_last_transfer_id,:OLD.buddy_last_transfer_on,
:OLD.buddy_last_transfer_type,:OLD.buddy_total_transfer,:OLD.buddy_total_transfer_amt,
:OLD.created_on,:OLD.created_by,sysdate,'SYSTEM',:NEW.preferred_amount,
:OLD.last_transfer_amount,:OLD.prefix_id);
ELSIF DELETING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (:OLD.buddy_msisdn,:OLD.parent_id,:OLD.buddy_seq_num,
:OLD.buddy_name,:OLD.status,:OLD.buddy_last_transfer_id,:OLD.buddy_last_transfer_on,
:OLD.buddy_last_transfer_type,:OLD.buddy_total_transfer,:OLD.buddy_total_transfer_amt,
:OLD.created_on,:OLD.created_by,sysdate,'SYSTEM',:OLD.preferred_amount,
:OLD.last_transfer_amount,:OLD.prefix_id);
END IF;
END;
/


DROP TRIGGER TRIG_OTA_ADM_TXN_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_OTA_ADM_TXN_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON OTA_ADM_TRANSACTION FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(:NEW.msisdn,:NEW.transaction_id,:NEW.operation,:NEW.response,
:NEW.created_by,:NEW.created_on,:NEW.lock_time,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(:NEW.msisdn,:NEW.transaction_id,:NEW.operation,:NEW.response,
:NEW.created_by,:NEW.created_on,:NEW.lock_time,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(:OLD.msisdn,:OLD.transaction_id,:OLD.operation,:OLD.response,
:OLD.created_by,:OLD.created_on,:OLD.lock_time,sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_NETWORK_PRF_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_NETWORK_PRF_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON NETWORK_PREFERENCES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(:NEW.network_code, :NEW.preference_code,
:NEW.value, :NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(:NEW.network_code, :NEW.preference_code,
:NEW.value, :NEW.created_on, :NEW.created_by, :NEW.modified_on, :NEW.modified_by,sysdate,'U');
ELSIF UPDATING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(:OLD.network_code, :OLD.preference_code,
:OLD.value, :OLD.created_on, :OLD.created_by, :OLD.modified_on, :OLD.modified_by,sysdate,'D');
END IF;


END;
/


DROP TRIGGER TRIG_NETWORKS_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_NETWORKS_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON NETWORKS FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES (:NEW.network_code,:NEW.network_name,:NEW.network_short_name,
:NEW.company_name,:NEW.report_header_name,:NEW.erp_network_code,:NEW.address1,:NEW.address2,
:NEW.city,:NEW.state,:NEW.zip_code, :NEW.country, :NEW.network_type, :NEW.status, :NEW.remarks, :NEW.language_1_message,
:NEW.language_2_message, :NEW.text_1_value, :NEW.text_2_value, :NEW.country_prefix_code, :NEW.mis_done_date,
:NEW.created_on,:NEW.created_by,:NEW.created_on,:NEW.created_by,:NEW.service_set_id,sysdate,
'I');
ELSIF UPDATING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(:NEW.network_code,:NEW.network_name,:NEW.network_short_name,
:NEW.company_name,:NEW.report_header_name,:NEW.erp_network_code,:NEW.address1,:NEW.address2,
:NEW.city,:NEW.state,:NEW.zip_code, :NEW.country, :NEW.network_type, :NEW.status, :NEW.remarks, :NEW.language_1_message,
:NEW.language_2_message, :NEW.text_1_value, :NEW.text_2_value, :NEW.country_prefix_code, :NEW.mis_done_date,
:NEW.created_on,:NEW.created_by,:NEW.modified_on,:NEW.modified_by,:NEW.service_set_id,sysdate,
'U');
ELSIF DELETING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(:OLD.network_code,:OLD.network_name,:OLD.network_short_name,
:OLD.company_name,:OLD.report_header_name,:OLD.erp_network_code,:OLD.address1,:OLD.address2,
:OLD.city,:OLD.state,:OLD.zip_code, :OLD.country, :OLD.network_type, :OLD.status, :OLD.remarks, :OLD.language_1_message,
:OLD.language_2_message, :OLD.text_1_value, :OLD.text_2_value, :OLD.country_prefix_code, :OLD.mis_done_date,
:OLD.created_on,:OLD.created_by,:OLD.modified_on,:OLD.modified_by,:OLD.service_set_id,sysdate,
'D');
END IF;
END;
/


DROP TRIGGER TRIG_CONTROL_PRF_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_CONTROL_PRF_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON CONTROL_PREFERENCES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(:NEW.control_code, :NEW.network_code,
:NEW.preference_code, :NEW.value,:NEW.created_on, :NEW.created_by, :NEW.modified_on,
:NEW.modified_by,sysdate,'I',:NEW.type);
ELSIF UPDATING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(:NEW.control_code, :NEW.network_code,
:NEW.preference_code, :NEW.value,:NEW.created_on, :NEW.created_by, :NEW.modified_on,
:NEW.modified_by,sysdate,'U',:NEW.type);
ELSIF DELETING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(:OLD.control_code, :OLD.network_code,
:OLD.preference_code, :OLD.value,:OLD.created_on, :OLD.created_by, :OLD.modified_on,
:OLD.modified_by,sysdate,'D',:OLD.type);
END IF;


END;
/


DROP TRIGGER TRIG_CHNL_TRF_RULES_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_CHNL_TRF_RULES_HISTORY"  
AFTER INSERT or UPDATE or delete ON CHNL_TRANSFER_RULES FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(:NEW.transfer_rule_id, :NEW.domain_code,
:NEW.network_code, :NEW.from_category, :NEW.to_category, :NEW.transfer_chnl_bypass_allowed,
:NEW.withdraw_allowed, :NEW.withdraw_chnl_bypass_allowed, :NEW.return_allowed,
:NEW.return_chnl_bypass_allowed, :NEW.approval_required, :NEW.first_approval_limit,
:NEW.second_approval_limit, :NEW.created_by, :NEW.created_on, :NEW.modified_by, :NEW.modified_on,
:NEW.status, sysdate, 'I', :NEW.transfer_type, :NEW.parent_association_allowed,
:NEW.direct_transfer_allowed, :NEW.transfer_allowed, :NEW.foc_transfer_type, :NEW.foc_allowed,
:NEW.type, :NEW.uncntrl_transfer_allowed,:NEW.restricted_msisdn_access,
:NEW.to_domain_code, :NEW.uncntrl_transfer_level, :NEW.cntrl_transfer_level,
:NEW.fixed_transfer_level, :NEW.fixed_transfer_category, :NEW.uncntrl_return_allowed,
:NEW.uncntrl_return_level, :NEW.cntrl_return_level, :NEW.fixed_return_level,
:NEW.fixed_return_category, :NEW.uncntrl_withdraw_allowed, :NEW.uncntrl_withdraw_level,
:NEW.cntrl_withdraw_level, :NEW.fixed_withdraw_level, :NEW.fixed_withdraw_category,:NEW.parent_assocation_allowed,:NEW.previous_status,:NEW.direct_payout_allowed);
ELSIF UPDATING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(:NEW.transfer_rule_id, :NEW.domain_code,
:NEW.network_code, :NEW.from_category, :NEW.to_category, :NEW.transfer_chnl_bypass_allowed,
:NEW.withdraw_allowed, :NEW.withdraw_chnl_bypass_allowed, :NEW.return_allowed,
:NEW.return_chnl_bypass_allowed, :NEW.approval_required, :NEW.first_approval_limit,
:NEW.second_approval_limit, :NEW.created_by, :NEW.created_on, :NEW.modified_by, :NEW.modified_on,
:NEW.status, sysdate, 'U', :NEW.transfer_type, :NEW.parent_association_allowed,
:NEW.direct_transfer_allowed, :NEW.transfer_allowed, :NEW.foc_transfer_type, :NEW.foc_allowed,
:NEW.type, :NEW.uncntrl_transfer_allowed,:NEW.restricted_msisdn_access,
:NEW.to_domain_code, :NEW.uncntrl_transfer_level, :NEW.cntrl_transfer_level,
:NEW.fixed_transfer_level, :NEW.fixed_transfer_category, :NEW.uncntrl_return_allowed,
:NEW.uncntrl_return_level, :NEW.cntrl_return_level, :NEW.fixed_return_level,
:NEW.fixed_return_category, :NEW.uncntrl_withdraw_allowed, :NEW.uncntrl_withdraw_level,
:NEW.cntrl_withdraw_level, :NEW.fixed_withdraw_level, :NEW.fixed_withdraw_category,:NEW.parent_assocation_allowed,:NEW.previous_status,:NEW.direct_payout_allowed);
ELSIF DELETING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(:OLD.transfer_rule_id, :OLD.domain_code,
:OLD.network_code, :OLD.from_category, :OLD.to_category, :OLD.transfer_chnl_bypass_allowed,
:OLD.withdraw_allowed, :OLD.withdraw_chnl_bypass_allowed, :OLD.return_allowed,
:OLD.return_chnl_bypass_allowed, :OLD.approval_required, :OLD.first_approval_limit,
:OLD.second_approval_limit, :OLD.created_by, :OLD.created_on, :OLD.modified_by, :OLD.modified_on,
:OLD.status, sysdate, 'D', :OLD.transfer_type, :OLD.parent_association_allowed,
:OLD.direct_transfer_allowed, :OLD.transfer_allowed, :OLD.foc_transfer_type, :OLD.foc_allowed,
:OLD.type, :OLD.uncntrl_transfer_allowed,:OLD.restricted_msisdn_access,
:OLD.to_domain_code, :OLD.uncntrl_transfer_level, :OLD.cntrl_transfer_level,
:OLD.fixed_transfer_level, :OLD.fixed_transfer_category, :OLD.uncntrl_return_allowed,
:OLD.uncntrl_return_level, :OLD.cntrl_return_level, :OLD.fixed_return_level,
:OLD.fixed_return_category, :OLD.uncntrl_withdraw_allowed, :OLD.uncntrl_withdraw_level,
:OLD.cntrl_withdraw_level, :OLD.fixed_withdraw_level, :OLD.fixed_withdraw_category,:OLD.parent_assocation_allowed,:NEW.previous_status,:NEW.direct_payout_allowed);
END IF;
END;
/


DROP TRIGGER TRIG_CHANNEL_USERS_HISTORY;

CREATE OR REPLACE TRIGGER TRIG_CHANNEL_USERS_HISTORY
AFTER INSERT OR UPDATE OR DELETE OF LMS_PROFILE, OPT_IN_OUT_STATUS, CONTROL_GROUP ON CHANNEL_USERS FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(:NEW.USER_ID, :NEW.USER_GRADE, :NEW.CONTACT_PERSON, :NEW.TRANSFER_PROFILE_ID, :NEW.COMM_PROFILE_SET_ID,
    :NEW.IN_SUSPEND, :NEW.OUT_SUSPEND, :NEW.OUTLET_CODE, :NEW.SUBOUTLET_CODE, :NEW.ACTIVATED_ON, :NEW.APPLICATION_ID, :NEW.MPAY_PROFILE_ID, :NEW.USER_PROFILE_ID, :NEW.IS_PRIMARY, :NEW.MCOMMERCE_SERVICE_ALLOW, :NEW.LOW_BAL_ALERT_ALLOW, :NEW.MCATEGORY_CODE, :NEW.ALERT_MSISDN, :NEW.ALERT_TYPE, :NEW.ALERT_EMAIL, :NEW.VOMS_DECRYP_KEY, :NEW.TRF_RULE_TYPE, :NEW.AUTO_O2C_ALLOW, :NEW.AUTO_FOC_ALLOW, :NEW.LMS_PROFILE_UPDATED_ON, :NEW.LMS_PROFILE, :NEW.REF_BASED, :NEW.ASSOCIATED_MSISDN, :NEW.ASSOCIATED_MSISDN_TYPE, :NEW.ASSOCIATED_MSISDN_CDATE, :NEW.ASSOCIATED_MSISDN_MDATE, :NEW.AUTO_C2C_ALLOW, :NEW.AUTO_C2C_QUANTITY, :NEW.OPT_IN_OUT_STATUS, :NEW.OPT_IN_OUT_NOTIFY_DATE, :NEW.OPT_IN_OUT_RESPONSE_DATE, :NEW.CONTROL_GROUP,sysdate,'I');
ELSIF UPDATING AND (nvl(:NEW.LMS_PROFILE,'XYZ') <> nvl(:OLD.LMS_PROFILE,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(:OLD.USER_ID,NVL( :OLD.USER_GRADE, :NEW.USER_GRADE), :OLD.CONTACT_PERSON,NVL( :OLD.TRANSFER_PROFILE_ID, :NEW.TRANSFER_PROFILE_ID), NVL(:OLD.COMM_PROFILE_SET_ID, :NEW.COMM_PROFILE_SET_ID),:OLD.IN_SUSPEND, :OLD.OUT_SUSPEND, :OLD.OUTLET_CODE, :OLD.SUBOUTLET_CODE, :OLD.ACTIVATED_ON, :OLD.APPLICATION_ID, :OLD.MPAY_PROFILE_ID, NVL(:OLD.USER_PROFILE_ID,:NEW.USER_PROFILE_ID), :OLD.IS_PRIMARY, :OLD.MCOMMERCE_SERVICE_ALLOW, :OLD.LOW_BAL_ALERT_ALLOW, :OLD.MCATEGORY_CODE, :OLD.ALERT_MSISDN, :OLD.ALERT_TYPE, :OLD.ALERT_EMAIL, :OLD.VOMS_DECRYP_KEY, :OLD.TRF_RULE_TYPE, :OLD.AUTO_O2C_ALLOW, :OLD.AUTO_FOC_ALLOW, :NEW.LMS_PROFILE_UPDATED_ON, :NEW.LMS_PROFILE, :OLD.REF_BASED, :OLD.ASSOCIATED_MSISDN, :OLD.ASSOCIATED_MSISDN_TYPE, :OLD.ASSOCIATED_MSISDN_CDATE, :OLD.ASSOCIATED_MSISDN_MDATE, :OLD.AUTO_C2C_ALLOW, :OLD.AUTO_C2C_QUANTITY, :NEW.OPT_IN_OUT_STATUS, :NEW.OPT_IN_OUT_NOTIFY_DATE, :NEW.OPT_IN_OUT_RESPONSE_DATE, :NEW.CONTROL_GROUP,sysdate,'U');
ELSIF UPDATING AND (nvl(:NEW.OPT_IN_OUT_STATUS,'XYZ') <> nvl(:OLD.OPT_IN_OUT_STATUS,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(:OLD.USER_ID,NVL( :OLD.USER_GRADE, :NEW.USER_GRADE), :OLD.CONTACT_PERSON,NVL( :OLD.TRANSFER_PROFILE_ID, :NEW.TRANSFER_PROFILE_ID), NVL(:OLD.COMM_PROFILE_SET_ID, :NEW.COMM_PROFILE_SET_ID),:OLD.IN_SUSPEND, :OLD.OUT_SUSPEND, :OLD.OUTLET_CODE, :OLD.SUBOUTLET_CODE, :OLD.ACTIVATED_ON, :OLD.APPLICATION_ID, :OLD.MPAY_PROFILE_ID, NVL(:OLD.USER_PROFILE_ID,:NEW.USER_PROFILE_ID), :OLD.IS_PRIMARY, :OLD.MCOMMERCE_SERVICE_ALLOW, :OLD.LOW_BAL_ALERT_ALLOW, :OLD.MCATEGORY_CODE, :OLD.ALERT_MSISDN, :OLD.ALERT_TYPE, :OLD.ALERT_EMAIL, :OLD.VOMS_DECRYP_KEY, :OLD.TRF_RULE_TYPE, :OLD.AUTO_O2C_ALLOW, :OLD.AUTO_FOC_ALLOW, :NEW.LMS_PROFILE_UPDATED_ON, :NEW.LMS_PROFILE, :OLD.REF_BASED, :OLD.ASSOCIATED_MSISDN, :OLD.ASSOCIATED_MSISDN_TYPE, :OLD.ASSOCIATED_MSISDN_CDATE, :OLD.ASSOCIATED_MSISDN_MDATE, :OLD.AUTO_C2C_ALLOW, :OLD.AUTO_C2C_QUANTITY, :NEW.OPT_IN_OUT_STATUS, :NEW.OPT_IN_OUT_NOTIFY_DATE, :NEW.OPT_IN_OUT_RESPONSE_DATE, :NEW.CONTROL_GROUP,sysdate,'U');
ELSIF UPDATING AND (nvl(:NEW.CONTROL_GROUP,'XYZ') <> nvl(:OLD.CONTROL_GROUP,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(:OLD.USER_ID,NVL( :OLD.USER_GRADE, :NEW.USER_GRADE), :OLD.CONTACT_PERSON,NVL( :OLD.TRANSFER_PROFILE_ID, :NEW.TRANSFER_PROFILE_ID), NVL(:OLD.COMM_PROFILE_SET_ID, :NEW.COMM_PROFILE_SET_ID),:OLD.IN_SUSPEND, :OLD.OUT_SUSPEND, :OLD.OUTLET_CODE, :OLD.SUBOUTLET_CODE, :OLD.ACTIVATED_ON, :OLD.APPLICATION_ID, :OLD.MPAY_PROFILE_ID, NVL(:OLD.USER_PROFILE_ID,:NEW.USER_PROFILE_ID), :OLD.IS_PRIMARY, :OLD.MCOMMERCE_SERVICE_ALLOW, :OLD.LOW_BAL_ALERT_ALLOW, :OLD.MCATEGORY_CODE, :OLD.ALERT_MSISDN, :OLD.ALERT_TYPE, :OLD.ALERT_EMAIL, :OLD.VOMS_DECRYP_KEY, :OLD.TRF_RULE_TYPE, :OLD.AUTO_O2C_ALLOW, :OLD.AUTO_FOC_ALLOW, :NEW.LMS_PROFILE_UPDATED_ON, :NEW.LMS_PROFILE, :OLD.REF_BASED, :OLD.ASSOCIATED_MSISDN, :OLD.ASSOCIATED_MSISDN_TYPE, :OLD.ASSOCIATED_MSISDN_CDATE, :OLD.ASSOCIATED_MSISDN_MDATE, :OLD.AUTO_C2C_ALLOW, :OLD.AUTO_C2C_QUANTITY, :NEW.OPT_IN_OUT_STATUS, :NEW.OPT_IN_OUT_NOTIFY_DATE, :NEW.OPT_IN_OUT_RESPONSE_DATE, :NEW.CONTROL_GROUP,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(:OLD.USER_ID, :OLD.USER_GRADE, :OLD.CONTACT_PERSON, :OLD.TRANSFER_PROFILE_ID, :OLD.COMM_PROFILE_SET_ID,:OLD.IN_SUSPEND, :OLD.OUT_SUSPEND, :OLD.OUTLET_CODE, :OLD.SUBOUTLET_CODE, :OLD.ACTIVATED_ON, :OLD.APPLICATION_ID, :OLD.MPAY_PROFILE_ID, :OLD.USER_PROFILE_ID, :OLD.IS_PRIMARY, :OLD.MCOMMERCE_SERVICE_ALLOW, :OLD.LOW_BAL_ALERT_ALLOW, :OLD.MCATEGORY_CODE, :OLD.ALERT_MSISDN, :OLD.ALERT_TYPE, :OLD.ALERT_EMAIL, :OLD.VOMS_DECRYP_KEY, :OLD.TRF_RULE_TYPE, :OLD.AUTO_O2C_ALLOW, :OLD.AUTO_FOC_ALLOW, :OLD.LMS_PROFILE_UPDATED_ON, :OLD.LMS_PROFILE, :OLD.REF_BASED, :OLD.ASSOCIATED_MSISDN, :OLD.ASSOCIATED_MSISDN_TYPE, :OLD.ASSOCIATED_MSISDN_CDATE, :OLD.ASSOCIATED_MSISDN_MDATE, :OLD.AUTO_C2C_ALLOW, :OLD.AUTO_C2C_QUANTITY, :OLD.OPT_IN_OUT_STATUS, :OLD.OPT_IN_OUT_NOTIFY_DATE, :OLD.OPT_IN_OUT_RESPONSE_DATE, :OLD.CONTROL_GROUP,sysdate,'D');
END IF;
END;
/


DROP TRIGGER TRIG_BARRED_MSISDN_HISTORY;

CREATE OR REPLACE TRIGGER "TRIG_BARRED_MSISDN_HISTORY"  
AFTER INSERT OR UPDATE OR DELETE ON BARRED_MSISDNS FOR EACH ROW
BEGIN
IF INSERTING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(:NEW.module,:NEW.network_code,:NEW.msisdn,:NEW.name,:NEW.user_type,
:NEW.barred_type,:NEW.created_on,:NEW.created_by,:NEW.created_on,
:NEW.created_by,:NEW.barred_reason,:NEW.created_date,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(:NEW.module,:NEW.network_code,:NEW.msisdn,:NEW.name,:NEW.user_type,
:NEW.barred_type,:NEW.created_on,:NEW.created_by,:NEW.modified_on,
:NEW.modified_by,:NEW.barred_reason,:NEW.created_date,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(:OLD.module,:OLD.network_code,:OLD.msisdn,:OLD.name,:OLD.user_type,
:OLD.barred_type,:OLD.created_on,:OLD.created_by,:OLD.modified_on,
:OLD.modified_by,:OLD.barred_reason,:OLD.created_date,sysdate,'D');
END IF;
END;
/


DROP TRIGGER INVALID_STATUS_CHANGE;

CREATE OR REPLACE TRIGGER "INVALID_STATUS_CHANGE"    
	AFTER UPDATE OF status ON voms_vouchers  
	FOR EACH ROW
DECLARE  
	Invalid_status_change EXCEPTION;  
	BEGIN  
	IF (:old.status IN ('CU','ST','DA') AND :old.status <> :new.status ) THEN  
    RAISE Invalid_status_change;  
    END IF;  
    END;
/


DROP TRIGGER CARD_HISTORY_TRIGGER;

CREATE OR REPLACE TRIGGER "CARD_HISTORY_TRIGGER"  
AFTER DELETE OR INSERT OR UPDATE
OF CARD_NICK_NAME
ON CARD_DETAILS 
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
IF INSERTING THEN
    INSERT INTO CARD_DETAILS_HISTORY (USER_ID,NAME_OF_EMBOSSING,CARD_NUMBER,CARD_TYPE,CARD_NICK_NAME,BANK,EXPIRY_DATE,MSISDN,DOB,EMAIL,ADDRESS,CREATED_ON,STATUS,ACCEPT_T_C,IS_DEFAULT,MODIFIED_ON,ACTION)
    VALUES (:NEW.USER_ID,:NEW.NAME_OF_EMBOSSING,:NEW.CARD_NUMBER,:NEW.CARD_TYPE,:NEW.CARD_NICK_NAME,:NEW.BANK,:NEW.EXPIRY_DATE,:NEW.MSISDN,:NEW.DOB,:NEW.EMAIL,:NEW.ADDRESS,sysdate,:NEW.STATUS,:NEW.ACCEPT_T_C,:NEW.IS_DEFAULT,SYSDATE,'I');
ELSIF UPDATING THEN
    INSERT INTO CARD_DETAILS_HISTORY (USER_ID,NAME_OF_EMBOSSING,CARD_NUMBER,CARD_TYPE,CARD_NICK_NAME,BANK,EXPIRY_DATE,MSISDN,DOB,EMAIL,ADDRESS,CREATED_ON,STATUS,ACCEPT_T_C,IS_DEFAULT,MODIFIED_ON,ACTION)
    VALUES (:NEW.USER_ID,:NEW.NAME_OF_EMBOSSING,:NEW.CARD_NUMBER,:NEW.CARD_TYPE,:NEW.CARD_NICK_NAME,:NEW.BANK,:NEW.EXPIRY_DATE,:NEW.MSISDN,:NEW.DOB,:NEW.EMAIL,:NEW.ADDRESS,:NEW.CREATED_ON,:NEW.STATUS,:NEW.ACCEPT_T_C,:NEW.IS_DEFAULT,SYSDATE,'U');
ELSIF DELETING THEN
    INSERT INTO CARD_DETAILS_HISTORY (USER_ID,NAME_OF_EMBOSSING,CARD_NUMBER,CARD_TYPE,CARD_NICK_NAME,BANK,EXPIRY_DATE,MSISDN,DOB,EMAIL,ADDRESS,CREATED_ON,STATUS,ACCEPT_T_C,IS_DEFAULT,MODIFIED_ON,ACTION)
    VALUES (:OLD.USER_ID,:OLD.NAME_OF_EMBOSSING,:OLD.CARD_NUMBER,:OLD.CARD_TYPE,:OLD.CARD_NICK_NAME,:OLD.BANK,:OLD.EXPIRY_DATE,:OLD.MSISDN,:OLD.DOB,:OLD.EMAIL,:OLD.ADDRESS,:OLD.CREATED_ON,:OLD.STATUS,:OLD.ACCEPT_T_C,:OLD.IS_DEFAULT,SYSDATE,'D');
END IF;
END CARD_HISTORY_TRIGGER;
/


