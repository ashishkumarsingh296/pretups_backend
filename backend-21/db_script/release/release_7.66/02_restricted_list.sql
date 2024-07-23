INSERT INTO page_ui_roles(role_code, page_code)VALUES('VIEWRESTRICTMSISDN', 'P_SUBS_MGMT');

INSERT INTO page_ui_roles(role_code, page_code)VALUES('BLKREGSUB', 'P_SUBS_MGMT');

INSERT INTO page_ui_roles(role_code, page_code)VALUES('DELRESSUB', 'P_SUBS_MGMT');

DELETE FROM page_ui_roles WHERE role_code='OPTUSRAPRROLES' AND page_code='P_SUBS_MGMT';

INSERT INTO page_ui_roles(role_code, page_code)VALUES('BLACKLISTMGMT', 'P_SUBS_MGMT');

INSERT INTO page_ui_roles(role_code, page_code)VALUES('UNBLKSUBS', 'P_SUBS_MGMT');

INSERT INTO page_ui_roles(role_code, page_code)VALUES('P_APPR_L1_RES_LIST', 'P_APPR_L1');

DELETE FROM page_ui_roles WHERE role_code='P_APPR_L1_RES_LIST' AND page_code='P_APPR_L1';

INSERT INTO page_ui_roles(role_code, page_code) VALUES('RESTAPPROVEMSISDN', 'P_APPR_L1');