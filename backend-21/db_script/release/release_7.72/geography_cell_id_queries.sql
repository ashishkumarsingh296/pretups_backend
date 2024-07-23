
INSERT INTO pages_ui
(page_code, module_code, page_url, menu_name, show_menu, sequence_no, menu_level, parent_page_code, image, role_code, domain_type, category_code, page_type, app_name, is_default)
VALUES('P_NW_CELL_ID_GEO', 'COMMON', '/cellIDGeoDomain', 'Cell ID - Geographical Domain', 'Y', 34.3, '4', 'P_NW_CELLID_MGMTMAIN', NULL, NULL, 'OPERATOR', 'NWADM', NULL, 'eTopup', NULL);

-----

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('GEOGCELLMGMT', 'P_NW_CELL_ID_GEO');


------

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('GEOGCELLMGMT', 'P_NW_CELLID_MGMTMAIN');