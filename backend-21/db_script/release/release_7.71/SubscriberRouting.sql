INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SU_SUBS_ROUT_MAIN', 'COMMON', '/changeIt', 'Subscriber Routing', 'Y', 61, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SU_ADD_SUBS_ROUT', 'COMMON', '/num-subRouting', 'Add Routing number', 'Y', 61.1, '2', 'P_SU_SUBS_ROUT_MAIN', NULL, NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UPROUMSISD', 'P_SU_SUBS_ROUT_MAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UPROUMSISD', 'P_SU_ADD_SUBS_ROUT');
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SU_UPL_PROCSS_ROUT', 'COMMON', '/upl-subRouting', 'Upload and process', 'Y', 61.2, '2', 'P_SU_SUBS_ROUT_MAIN', NULL, NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UPLOADPROCESSFILE', 'P_SU_SUBS_ROUT_MAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('UPLOADPROCESSFILE', 'P_SU_UPL_PROCSS_ROUT');

INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_SU_DEL_SUBS_ROUT', 'COMMON', '/del-subRouting', 'Delete routing number', 'Y', 61.3, '2', 'P_SU_SUBS_ROUT_MAIN', NULL, NULL, 'OPERATOR', 'SUADM', NULL, 'eTopup', NULL);

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('DELETESUBSROUTING', 'P_SU_SUBS_ROUT_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('DELETEBATCHROUTING', 'P_SU_SUBS_ROUT_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('DELETESUBSROUTING', 'P_SU_DEL_SUBS_ROUT');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('DELETEBATCHROUTING', 'P_SU_DEL_SUBS_ROUT');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Delete_Subscriber_Routing', 'DELETESUBSROUTING');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('superAdmin', 'Bulk_Delete_Subscriber_Routing', 'DELETEBATCHROUTING');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('SU0001', 'DELETESUBSROUTING', 'WEB');

INSERT INTO user_roles
(user_id, role_code, gateway_types)
VALUES('SU0001', 'DELETEBATCHROUTING', 'WEB');

ALTER TABLE subscriber_routing ADD CONSTRAINT pksubscriber_routing PRIMARY KEY (msisdn, subscriber_type);

update roles set view_roles ='Y' where role_code in ('DELETESUBSROUTING','DELETEBATCHROUTING','UPLOADPROCESSFILE','UPROUMSISD');
