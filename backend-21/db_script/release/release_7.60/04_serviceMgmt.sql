UPDATE pages_ui
SET module_code='COMMON', page_url='/serviceManagement', menu_name='Service Management', show_menu='Y', sequence_no=1, menu_level='1', parent_page_code='ROOT', image='assets/images/updateCache/svg/updateCache.svg', role_code=NULL, domain_type='OPERATOR', category_code='NWADM', page_type='home_configuration', app_name=NULL
WHERE page_code='P_NW_SER_MGMT_MAIN';

DELETE FROM page_ui_roles
WHERE page_code='P_NW_SER_MGMT_MAIN';

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('SRVMGMT', 'P_NW_SER_MGMT_MAIN');


