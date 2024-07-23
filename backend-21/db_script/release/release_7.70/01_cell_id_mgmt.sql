INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CELL_ID_MGMT', 'COMMON', '/cellGroup', 'Cell ID Management', 'Y', 32, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR', 'CCE', NULL, 'eTopup', NULL);
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CELL_MGMT', 'COMMON', '/manageCellGroup', 'Cell Group', 'Y', 32.1, '2', 'P_NW_CELLID_MGMTMAIN', NULL, NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CELL_ID_ASS', 'COMMON', '/cellIDAssosiate', 'Cell ID - Cell Group', 'Y', 34.2, '3', 'P_NW_CELLID_MGMTMAIN', NULL, NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLGRPMGT', 'P_NW_CELL_MGMT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLGRPMGT', 'P_NW_CELLID_MGMTMAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLIDMGT', 'P_NW_CELL_ID_ASS');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLIDMGT', 'P_NW_CELLID_MGMTMAIN');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'cell_ID_Associate', 'CELLIDMGT');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLIDRASS', 'P_NW_CELL_ID_ASS');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'cell_ID_Re_Associate', 'CELLIDRASS');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('CELLIDRASS', 'P_NW_CELLID_MGMTMAIN');