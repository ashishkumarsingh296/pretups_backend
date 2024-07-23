PRETUPS-25210
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('adminChannelUser', 'Approval_user_level_one', 'APPROVALUSER');

PRETUPS-25228
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('adminChannelUser', 'Bulk_O2C_Purchase', 'INITBO2C');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('adminChannelUser', 'Bulk_O2C_Withdraw', 'O2CBWITHDRAW');

PRETUPS-25268
UPDATE lookups
SET lookup_code='M/S',lookup_name='M/s', status='Y', created_on='2005-11-06 00:00:00.000', created_by='ADMIN', modified_on='2005-11-06 00:00:00.000', modified_by='ADMIN'
WHERE lookup_code='CMPY' AND lookup_type='USRPX';

PRETUPS-25336
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Upload_MsgMgMt_Single', 'MSGSMGMT')
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Upload_MsgMgMt_Bulk', 'MSGMANGEMOF')

PRETUPS-25339
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_IccidMsisdn_Mapping', 'ASSMSISDNICCID')
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Upload_Key_ICCID/Management', 'ICCIDKEYMGMT')

PRETUPS-25281, PRETUPS-25236
UPDATE SYSTEM_PREFERENCES
SET NAME='Approver can edit user or not', "TYPE"='SYSTEMPRF', VALUE_TYPE='BOOLEAN', DEFAULT_VALUE='true', MIN_VALUE=NULL, MAX_VALUE=NULL, MAX_SIZE=50, DESCRIPTION='If this flag is true then approver can edit channel/operator user details during approval time', MODIFIED_ALLOWED='N', DISPLAY='Y', MODULE='C2S', REMARKS='If this flag is true then approver can edit channel/operator user details during approval time', CREATED_ON=TIMESTAMP '2007-11-16 00:00:00.000000', CREATED_BY='ADMIN', MODIFIED_ON=TIMESTAMP '2007-11-16 00:00:00.000000', MODIFIED_BY='ADMIN', ALLOWED_VALUES=NULL, FIXED_VALUE='Y'
WHERE PREFERENCE_CODE='APPROVER_CAN_EDIT';


PRETUPS-25369
INSERT INTO SYSTEM_PREFERENCES
    (PREFERENCE_CODE, NAME, "TYPE", VALUE_TYPE, DEFAULT_VALUE, MIN_VALUE, MAX_VALUE, MAX_SIZE, DESCRIPTION, 
    MODIFIED_ALLOWED, DISPLAY, MODULE, REMARKS, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, ALLOWED_VALUES, FIXED_VALUE)
VALUES
    ('SYSTEM_DATETIME_FORMAT', 'date time format', 'SYSTEMPRF', 'STRING', 'dd/MM/yyyy HH:mm:ss', 
    NULL, NULL, 5, 'date time selenium use', 'N', 'N', 'O2C', 'date time format', 
    TO_DATE('2005-06-16 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'ADMIN', 
    TO_DATE('2005-06-16 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), 'ADMIN', 
    NULL, 'Y');
	
PRETUPS-25452
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Del_Sub_Lookup_mgmt', 'DELTSUBLOOKUP');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Modify_Sub_Lookup_mgmt', 'MODIFYSUBLOOKUP');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Add_Sub_Lookup_mgmt', 'ADDSUBLOOKUP');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type, sub_group_name, sub_group_role, view_roles)
VALUES('OPERATOR', 'DELTSUBLOOKUP', 'Delete Sub LookUp', 'Masters', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Sub_Group', 'Y', 'Y');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type, sub_group_name, sub_group_role, view_roles)
VALUES('OPERATOR', 'SUBLOOKMGT', 'Masters', 'Sub Lookup Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Sub_Group', 'N', 'Y');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SUADM', 'DELTSUBLOOKUP', '1');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SUADM', 'SUBLOOKMGT', '1');

PRETUPS-25518
ALTER TABLE sub_lookups 
ALTER COLUMN lookup_type TYPE VARCHAR(10);