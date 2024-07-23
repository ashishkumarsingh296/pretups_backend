INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSADDCARDGRP', 'P_NW_VCH');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSVIEWCARDGRP', 'P_NW_VCH');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSEDITCARDGRP', 'P_NW_VCH');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSDFLTCARDGRP', 'P_NW_VCH');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSVIEWTRANSRULE', 'P_NW_VCH');
INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VMSBATCGMOD', 'P_NW_VCH');


INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Batch_VCG_Modify', 'VMSBATCGMOD');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Add_VCG_Card_Group', 'VMSADDCARDGRP');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'View_VCG_Card_Group', 'VMSVIEWCARDGRP');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Modify_VCG_Card_Group', 'VMSEDITCARDGRP');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Default_VCG_Card_Group', 'VMSDFLTCARDGRP');
INSERT INTO role_ui_mapping
(role_ui_parent_id, role_ui_id, rule_code)
VALUES('networkAdmin', 'Calculate_VCG_Card_Group', 'VMSVIEWTRANSRULE');
