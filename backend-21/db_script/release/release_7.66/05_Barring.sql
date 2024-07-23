INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('homeScreen', 'Bar_User_Bulk', 'BARRUSERBULK');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('homeScreen', 'UnBar_User_Bulk', 'UNBARRUSERBULK');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UNBARRUSERBULK', 'P_BARR_MGMT');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('BARRUSERBULK', 'P_BARR_MGMT');



UPDATE roles
SET role_name='View Barred List', group_name='Masters', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='VIEWBARREDLIST' AND domain_type='OPERATOR';
UPDATE roles
SET role_name='Bar User', group_name='Masters', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='BARUSER' AND domain_type='OPERATOR';
UPDATE roles
SET role_name='Unbar User', group_name='Masters', status='N', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='UNBARUSER' AND domain_type='OPERATOR';
UPDATE roles
SET role_name='UnBar User in Bulk', group_name='Masters', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='UNBARRUSERBULK' AND domain_type='OPERATOR';
UPDATE roles
SET role_name='Bar User in Bulk', group_name='Masters', status='N', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='BARRUSERBULK' AND domain_type='OPERATOR';


INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UNBARRUSERBULK', 'P_BARR_MGMT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('BARRUSERBULK', 'P_BARR_MGMT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWBARREDLIST', 'P_BARR_MGMT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('BARUSER', 'P_BARR_MGMT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UNBARUSER', 'P_BARR_MGMT');

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_BARR_MGMT', 'COMMON', '/barUnbarAdmin', 'User Barring Management', 'Y', 1, '1', 'ROOT', 'assets/images/UserBarring/svg/UserBarring@1.5x.svg', NULL, NULL, NULL, 'home_actions', NULL, NULL);