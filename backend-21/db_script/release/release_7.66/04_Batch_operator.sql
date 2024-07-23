INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('DLTBATCHOPTUSR', 'P_NW_OPTUSER_MAIN');
============
INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('ADDBATCHOPTUSR', 'P_NW_OPTUSER_MAIN');
=========
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Delete_Batch_Opt_User', 'DLTBATCHOPTUSR');
=======
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_Batch_Opt_User', 'ADDBATCHOPTUSR');
======
DELETE FROM pretupsdatabase.page_ui_roles
WHERE role_code='DELBATCHOPTUSER' AND page_code='P_NW_OPTUSER_MAIN';
======
DELETE FROM pretupsdatabase.page_ui_roles
WHERE role_code='INITBATCHOPTUSER' AND page_code='P_NW_OPTUSER_MAIN';
=====
DELETE FROM pretupsdatabase.role_ui_mapping
WHERE role_ui_parent_id='networkAdmin' AND role_ui_id='Delete_Batch_Opt_User' AND rule_code='DELBATCHOPTUSER';
====
DELETE FROM pretupsdatabase.role_ui_mapping
WHERE role_ui_parent_id='networkAdmin' AND role_ui_id='Add_Batch_Opt_User' AND rule_code='INITBATCHOPTUSER';
====
INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('INITBATCHOPTUSER', 'P_NW_OPTUSER_MAIN');
====
INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('DELBATCHOPTUSER', 'P_NW_OPTUSER_MAIN');
====
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_Batch_Opt_User', 'DELBATCHOPTUSER');
====
DELETE FROM pretupsdatabase.role_ui_mapping
WHERE role_ui_parent_id='networkAdmin' AND role_ui_id='Add_Batch_Opt_User' AND rule_code='DELBATCHOPTUSER';
====
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_Batch_Opt_User', 'INITBATCHOPTUSER');
=====
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Delete_Batch_Opt_User', 'DELBATCHOPTUSER');
