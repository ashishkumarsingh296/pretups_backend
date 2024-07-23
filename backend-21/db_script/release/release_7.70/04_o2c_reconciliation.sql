INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_RECON_MAIN', 'COMMON', '/changeIt', 'Reconciliation', 'Y', 26, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_RECON_O2C', 'COMMON', '/O2Creconcilation', 'Operator to channel reconciliation', 'Y', 26.2, '3', 'P_NW_RECON_MAIN', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('RECO2CTRF', 'P_NW_RECON_O2C');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('RECO2CTRF', 'P_NW_RECON_MAIN');