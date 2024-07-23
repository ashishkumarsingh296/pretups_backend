DELETE FROM page_ui_roles
WHERE role_code='OPTUSRAPRROLES' AND page_code='P_NW_TRF_RULES_MAIN';

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWC2STRFRULES', 'P_NW_TRF_RULES_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDC2STRFRULES', 'P_NW_TRF_RULES_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('MODC2STRFRULES', 'P_NW_TRF_RULES_MAIN');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('MODC2STRFRULES', 'P_NW_TRF_RULES_C2S');

INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('ADDC2STRFRULES', 'P_NW_TRF_RULES_C2S');
