INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CRDGRP_MAIN_CCE', 'COMMON', '/changeIt', 'Card Group', 'Y', 9, '1', 'ROOT', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR', 'CCE', NULL, 'eTopup', NULL);

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CHNL_TO_SUB_CCE', 'COMMON', '/cardGroupC2s', 'Calculate C2S transfer value', 'Y', 9.1, '2', 'P_NW_CRDGRP_MAIN_CCE', NULL, NULL, 'OPERATOR', 'CCE', NULL, 'eTopup', NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWC2STRANSRULE', 'P_NW_CRDGRP_MAIN_CCE');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWC2STRANSRULE', 'P_NW_CHNL_TO_SUB_CCE');