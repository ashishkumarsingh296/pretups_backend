INSERT INTO pages_ui(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, 	page_type, app_name, is_default)
	VALUES('P_NW_SER_PRO_AMT_MAP', 'COMMON', '/serviceProAmtMapping', 'Service Product Amount Mapping', 'Y', 1, '1', 'ROOT', 	'assets/images/updateCache/svg/updateCache.svg', NULL, 'OPERATOR', 'NWADM', 'home_configuration', NULL, NULL);
insert
	into page_ui_roles (role_code,page_code)
	values('SELAMTMGMT','P_NW_SER_PRO_AMT_MAP');
insert
	into role_ui_mapping (role_ui_parent_id,role_ui_id,rule_code)
	values('networkAdmin','Service_Product_Amount_Mapping','SELAMTMGMT');