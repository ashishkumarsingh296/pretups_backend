INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('adminChannelUser', 'Bulk_O2C_Withdraw', 'O2CBWITHDRAW');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('adminChannelUser', 'Bulk_O2C_Purchase', 'INITBO2C');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('INITBO2C', 'P_O2C_TXN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('O2CBWITHDRAW', 'P_O2C_TXN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('INITBO2C', 'P_O2C_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('O2CBWITHDRAW', 'P_O2C_MAIN');