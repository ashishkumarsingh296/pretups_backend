
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDSERVICE', 'P_SW_STK');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('MODIFYSERVICE', 'P_SW_STK');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ASSIGNSERVICE', 'P_SW_STK_Assign');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDSERVICE', 'P_SW_STK_MAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('MODIFYSERVICE', 'P_SW_STK_MAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ASSIGNSERVICE', 'P_SW_STK_MAIN');

-----

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Add_Stk', 'ADDSERVICE');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Assign_Stk', 'ASSIGNSERVICE');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Modify_Stk', 'MODIFYSERVICE');

-----

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SW_STK', 'COMMON', '/stkService', 'Add/Modify Services', 'Y', 60.1, '2', 'P_SW_STK_MAIN', NULL, NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SW_STK_Assign', 'COMMON', '/assignStkService', 'Assign Services', 'Y', 60.2, '2', 'P_SW_STK_MAIN', NULL, NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SW_STK_MAIN', 'COMMON', '/stkService', 'STK Service', 'Y', 60, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);
