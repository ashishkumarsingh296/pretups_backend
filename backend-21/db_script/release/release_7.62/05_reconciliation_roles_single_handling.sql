INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_RECON_C2S', 'COMMON', '/C2Sreconcilation', 'Channel to subscriber reconcilation', 'Y', 26.1, '2', 'P_NW_RECON_MAIN', 'assets/images/channelUser/svg/channelUserIcon.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_RECON_MAIN', 'COMMON', '/C2Sreconcilation', 'Reconcilation', 'Y', 26, '1', 'ROOT', 'assets/images/home/svg/home.svg', NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);


INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('C2SRECONOPT', 'P_NW_RECON_MAIN');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('C2SRECONOPT', 'P_NW_RECON_C2S');


UPDATE roles
SET role_name='C2S Reconcilation', group_name='Reconciliation', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='C2SRECONOPT' AND domain_type='OPERATOR';