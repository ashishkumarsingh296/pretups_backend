INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_LOYLT_MGMT_MAIN', 'COMMON', '/changeIt', 'Loyalty Management', 'Y', 24, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_LOYLT_PROF_MGMT', 'COMMON', '/LoyaltyProfileMgmt', 'Profile management', 'Y', 24.1, '2', 'P_NW_LOYLT_MGMT_MAIN', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('LMSPROCR', 'P_NW_LOYLT_MGMT_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('LMSPROCR', 'P_NW_LOYLT_PROF_MGMT');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_Loyalty_Profile', 'LMSPROCR');