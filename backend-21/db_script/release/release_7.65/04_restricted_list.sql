INSERT INTO pages_ui(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_APPR_L1_RES_LIST', 'COMMON', '/approvallevelo2c/focap2', 'Restricted subscriber list', 'Y', 4.4, '2', 'P_APPR_L1', NULL, NULL, 'OPERATOR', 'BCU', NULL, 'eTopup', NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('OPTUSRAPRROLES', 'P_APPR_L1_RES_LIST');