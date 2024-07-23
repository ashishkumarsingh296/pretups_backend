INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_ENABLE', 'National Voucher Enable', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'National Voucher Enable For Super Admin', 'N', 'Y', 'C2S', 'National Voucher Enable', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2005-09-12 03:39:55.000000', 'SU0001', NULL, 'Y');



INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('NATIONAL_VOUCHER_NETWORK_CODE', 'National Voucher Network Code', 'SYSTEMPRF', 'STRING', 'XX', NULL, NULL, 50, 'National Voucher Network Code For Super Admin', 'Y', 'Y', 'C2S', 'National Voucher Network Code For Super Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2012-06-06 15:03:14.000000', 'SU0001', NULL, 'Y');


ALTER TABLE VOMS_CATEGORIES
  Add (NETWORK_CODE varchar2(2)) ;
  
  INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'INITVOMS', '1');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP1VOMS', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP2VOMS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'APP3VOMS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADDCAT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'INITVOMS', '1');



INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADDCAT', '1');






INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADDPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADDPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODIPROF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODIPROF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVIEWPRF', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVIEWPRF', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSADACTPR', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSADACTPR', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODACTPR', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODACTPR', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVIEWACT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVIEWACT', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSTPMGT', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSMODDENO', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSMODDENO', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSVWDEN', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSVWDEN', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOMSOTCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOMSOTCHGSTATUS', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VIEWBATCHLIST', '1');

INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VIEWBATCHLIST', '1');

ALTER TABLE VOMS_PRODUCTS
  Add (NETWORK_CODE varchar2(2)) ;


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');

ALTER TABLE VOMS_PRODUCTS
  Add (NETWORK_CODE varchar2(2)) ;


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('NWADM', 'VOUFIL', '1');


INSERT INTO CATEGORY_ROLES
(CATEGORY_CODE, ROLE_CODE, APPLICATION_ID)
VALUES('SUNADM', 'VOUFIL', '1');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ONLINE_BATCH_EXP_DATE_LIMIT', 'Online Batch Expiry Date Limit', 'SYSTEMPRF', 'INT', '1000', 0, 1000, 50, 'Vouchers expiry date processed for batch online', 'Y', 'Y', 'C2S', 'Vouchers expiry date processed for batch online', '2005-06-21 00:00:00.000', 'ADMIN', '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');



INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_VOUCHER_EXPIRY_EXTN_LIMIT', 'Max limit for vouchers to extend expiry limit', 'SYSTEMPRF', 'INT', '10000', 0, 10000, 50, 'Maximum vouchers to be processed for voucher expiry date', 'Y', 'Y', 'C2S', 'Maximum vouchers to be processed for voucher expiry date', '2005-06-21 00:00:00.000', 'ADMIN', '2012-02-08 00:00:00.000', 'SU0001', NULL, 'Y');

INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2019', 'VMPNEXPEXT', 'ALL', 0, '2006-06-03 00:00:00.000', 'NA', NULL);


   CREATE TABLE voms_pin_exp_ext (
	batch_no varchar(15) NOT NULL,
	voucher_type varchar(15) NULL,
	total_vouchers numeric(16) NULL,
	from_serial_no varchar(16) NULL,
	to_serial_no varchar(16) NULL,
	total_failure numeric(16) NULL,
	total_success numeric(16) NULL,
	network_code varchar(2) NULL,
	status varchar(2) NULL,
	created_on timestamp NULL,
	created_by varchar(20) NULL,
	modified_on timestamp NULL,
	modified_by varchar(20) NULL,
	remarks varchar(50) NULL,
	expiry_date timestamp NULL,
	CONSTRAINT pk_voms_pin_exp_ext PRIMARY KEY (batch_no)
)

INSERT INTO service_type
(service_type, module, "type", message_format, request_handler, error_key, description, flexible, created_on, created_by, modified_on, modified_by, "name", external_interface, unregistered_access_allowed, status, seq_no, use_interface_language, group_type, sub_keyword_applicable, file_parser, erp_handler, receiver_user_service_check, response_param, request_param, underprocess_check_reqd)
VALUES('VMSPINEXT', 'C2S', 'ALL', '[KEYWORD][DATA]', 'com.btsl.pretups.channel.transfer.requesthandler.VoucherExpiryDateExtensionController', 'Voucher Expiry Date Extension API', 'Voucher Expiry Date Extension API', 'Y', '2017-11-06 06:18:46.000', 'ADMIN', '2017-11-06 06:18:46.000', 'ADMIN', 'Voucher Expiry Date Extension API', 'Y', 'N', 'Y', 11, 'N', 'NA', 'N', NULL, NULL, NULL, 'TYPE,TXNSTATUS,MESSAGE', 'TYPE,MSISDN', 'Y');

INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'REST', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y', '2019-05-03 16:39:59.613', 'SU0001', '2019-05-03 16:39:59.613', 'SU0001', 'SVK4100242', NULL, '');


INSERT INTO service_keywords
(keyword, req_interface_type, service_port, service_type, "name", status, menu, sub_menu, allowed_version, modify_allowed, created_on, created_by, modified_on, modified_by, service_keyword_id, sub_keyword, request_param)
VALUES('VMSPINEXT', 'EXTGW', '190', 'VMSPINEXT', 'VMSEXPEXT', 'Y', '', '', '', 'Y', '2019-05-03 16:39:59.613', 'SU0001', '2019-05-03 16:39:59.613', 'SU0001', 'SVK4100142', NULL, '');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN', 'Voms Max Approval level for Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN', 'Voms Max Approval level for Super Network Adm', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');

INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, NAME, TYPE, VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN', 'Voms Max Approval level for Sub Network Admin', 'SYSTEMPRF', 'INT', '0', 2, 3, 50, 'Voms Max Approval level', 'Y', 'Y', 'C2S', 'Voms Max Approval level for Super Network Admin', TIMESTAMP '2005-06-16 00:00:00.000000', 'ADMIN', TIMESTAMP '2006-09-20 12:17:45.000000', 'SU0001', '2,3', 'Y');



INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROF_TALKTIME_MANDATORY', 'VOMS ADD PROFILE TALK TIME MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'talk-time will be mandatory if defined as true', 'N', 'Y', 'C2S', 'false if talk time is not required , true if required', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROF_VALIDITY_MANDATORY', 'VOMS ADD PROFILE VALIDITY MANDATORY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'validity(in days) will be mandatory if defined as true', 'N', 'Y', 'C2S', 'false if  validaity is not required , true if required ', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_DEF_MINMAXQTY', 'VOMS PROFILE DEFAULT MINMAXQTY', 'SYSTEMPRF', 'boolean', 'true', NULL, NULL, 50, 'required for fields to be hidden in voucher profile', 'N', 'Y', 'C2S', 'false if need to show max min reorder qty, true if not required', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_MIN_REORDERQTY', 'VOMS PROFILE DEFAULT MINQTY', 'SYSTEMPRF', 'INT', '10', NULL, NULL, 50, 'default value is taken as min re order quantity', 'N', 'Y', 'C2S', 'default value is taken as min re order quantity', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');




INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, module, remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('VOMS_PROFILE_MAX_REORDERQTY', 'VOMS PROFILE DEFAULT MAXQTY', 'SYSTEMPRF', 'INT', '1000', NULL, NULL, 50, 'default value is taken as max re order quantity', 'N', 'Y', 'C2S', 'default value is taken as max re order quantity', '2019-05-20 00:00:00.000', 'ADMIN', '2019-05-20 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO SYSTEM_PREFERENCES
(PREFERENCE_CODE, "name", "type", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES('DOWNLD_BATCH_BY_BATCHID', 'batches download by batch id ', 'SYSTEMPRF', 'BOOLEAN', 'true', NULL, NULL, 50, 'Is calendar icon required on GUI', 'N', 'Y', 'C2S', 'Is calendar icon required on GUI','2018-06-26 00:00:00.000000', 'ADMIN','2018-06-26 00:00:00.000000', 'ADMIN', NULL, 'N');

ALTER TABLE VOMS_VOUCHERS ADD pre_expiry_date timestamp;
ALTER TABLE VOMS_VOUCHERS ADD info1 VARCHAR(50);