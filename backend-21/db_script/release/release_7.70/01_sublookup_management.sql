INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Add_Sub_Lookup_mgmt', 'ADDSUBLOOKUP');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Modify_Sub_Lookup_mgmt', 'MODIFYSUBLOOKUP');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type, sub_group_name, sub_group_role, view_roles)
VALUES('OPERATOR', 'DELTSUBLOOKUP', 'Delete Sub LookUp', 'Masters', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Sub_Group', 'Y', 'Y');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Del_Sub_Lookup_mgmt', 'DELTSUBLOOKUP');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SUADM', 'DELTSUBLOOKUP', '1');

UPDATE roles
SET view_roles='Y',status='Y' 
WHERE role_code='ADDSUBLOOKUP';	

UPDATE roles
SET view_roles='Y',status='Y' 
WHERE role_code='MODIFYSUBLOOKUP';	

UPDATE pages_ui
SET page_url='/subLookUpManagement'
WHERE page_code='P_SU_SUBLKP_MAIN';

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type, sub_group_name, sub_group_role, view_roles)
VALUES('OPERATOR', 'SUBLOOKMGT', 'Masters', 'Sub Lookup Management', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Sub_Group', 'N', 'Y');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SUADM', 'SUBLOOKMGT', '1');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('SU0001', 'SUBLOOKMGT', 'WEB');

UPDATE pages_ui
SET menu_name='Sublookup Managment'
WHERE page_code='P_SU_SUBLKP_MAIN';

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDSUBLOOKUP', 'P_SU_SUBLKP_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('MODIFYSUBLOOKUP', 'P_SU_SUBLKP_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('DELTSUBLOOKUP', 'P_SU_SUBLKP_MAIN');

ALTER TABLE sub_lookups 
ALTER COLUMN lookup_type TYPE VARCHAR(10);
