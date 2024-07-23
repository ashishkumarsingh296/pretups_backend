INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('MSGSMGMT', 'P_NW_MSG_MGMT');
INSERT INTO pretupsdatabase.page_ui_roles
(role_code, page_code)
VALUES('MSGMANGEMOF', 'P_NW_MSG_MGMT');
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Upload_MsgMgMt_Single', 'MSGSMGMT');
INSERT INTO pretupsdatabase.role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Upload_MsgMgMt_Bulk', 'MSGMANGEMOF');
update roles set role_name = 'Message management bulk' where role_code = 'MSGMANGEMOF'