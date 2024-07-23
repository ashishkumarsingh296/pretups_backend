INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDUSER', 'P_NW_OPTUSER_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('EDITUSER', 'P_NW_OPTUSER_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWUSER', 'P_NW_OPTUSER_MAIN');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_Operator_User', 'ADDUSER');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Modify_Operator_User', 'EDITUSER');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'View_Operator_User', 'VIEWUSER');


UPDATE roles
SET role_name='Add Operator User', group_name='Operator Users', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='ADDUSER' AND domain_type='OPERATOR';

UPDATE roles
SET role_name='Modify Operator User', group_name='Operator Users', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='EDITUSER' AND domain_type='OPERATOR';

UPDATE roles
SET role_name='View Operator User', group_name='Operator Users', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='VIEWUSER' AND domain_type='OPERATOR';
