UPDATE pages_ui
SET module_code='COMMON', page_url='/interface-mgmt', menu_name='Interface Management', show_menu='Y', sequence_no=2, menu_level='1', parent_page_code='ROOT', image='assets/images/updateCache/svg/updateCache.svg', role_code=NULL, domain_type='OPERATOR', category_code='SUADM', page_type='home_configuration', app_name=NULL, is_default=NULL
WHERE page_code='P_SU_INT_MGMT_MAIN';

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MIN_VALIDITY_EXPIRY_TIME', 'Minimum Expiry Time', 'SYSTEMPRF', 'INT', '1000', 1, 999999999, 50, 'Minimum Expiry Time', 'N', 'Y', 'C2S', 'Minimum Expiry Time', '2023-09-15 00:00:00.000', 'ADMIN', '2023-09-15 00:00:00.000', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_VALIDITY_EXPIRY_TIME', 'Maximum Expiry Time', 'SYSTEMPRF', 'INT', '300000', 1, 999999999, 50, 'Maximum Expiry Time', 'N', 'Y', 'C2S', 'Maximum Expiry Time', '2023-09-15 00:00:00.000', 'ADMIN', '2023-09-15 00:00:00.000', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MIN_TOPUP_EXPIRY_TIME', 'Minimum Topup Time', 'SYSTEMPRF', 'INT', '1000', 1, 999999999, 50, 'Minimum Topup Time', 'N', 'Y', 'C2S', 'Minimum Topup Time', '2023-09-15 00:00:00.000', 'ADMIN', '2023-09-15 00:00:00.000', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_TOPUP_EXPIRY_TIME', 'Maximum Topup Time', 'SYSTEMPRF', 'INT', '300000', 1, 999999999, 50, 'Maximum Topup Time', 'N', 'Y', 'C2S', 'Maximum Topup Time', '2023-09-15 00:00:00.000', 'ADMIN', '2023-09-15 00:00:00.000', 'ADMIN', NULL, 'Y');

INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('MAX_IP_NODES', 'Maximum IP Nodes', 'SYSTEMPRF', 'INT', '99', 1, 9999, 50, 'Maximum IP Nodes', 'N', 'Y', 'C2S', 'Maximum IP Nodes', '2023-09-15 00:00:00.000', 'ADMIN', '2023-09-15 00:00:00.000', 'ADMIN', NULL, 'Y');

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_INT_MGMT_MAIN', 'COMMON', '/interface-mgmt', 'Interface Management', 'Y', 2, '1', 'ROOT', 'assets/images/updateCache/svg/updateCache.svg', NULL, 'OPERATOR', 'NWADM', 'home_configuration', NULL, NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('INTERFACE', 'P_NW_INT_MGMT_MAIN');

UPDATE roles
SET role_name='Interface Management', group_name='Masters', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='INTERFACE' AND domain_type='OPERATOR';

DELETE FROM page_ui_roles
WHERE role_code='NWLEVL2APPROVAL' AND page_code='P_SU_INT_MGMT_MAIN';
DELETE FROM page_ui_roles
WHERE role_code='OPTUSRAPRROLES' AND page_code='P_SU_INT_MGMT_MAIN';
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('INTERFACE', 'P_SU_INT_MGMT_MAIN');


