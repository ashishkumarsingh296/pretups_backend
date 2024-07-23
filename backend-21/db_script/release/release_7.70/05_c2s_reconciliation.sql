INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('BLKAMBSETLMNT', 'P_NW_RECON_MAIN');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Single_O2C', 'C2SRECONOPT');

INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Bulk_Ambigious_O2C', 'BLKAMBSETLMNT');