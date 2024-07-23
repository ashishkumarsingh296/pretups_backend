
--Link-C2C Transfer Summary Report
--Add rolecode in roles table
INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('DISTB_CHAN', 'C2CTRFSUMMARY', 'C2C Transfer Summary', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('CORPORATE', 'C2CTRFSUMMARY', 'C2C Transfer Summary', 'Channel Reports-C2C', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');


--Add rolecode in category_role table
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('DIST', 'C2CTRFSUMMARY', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SE', 'C2CTRFSUMMARY', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('AG', 'C2CTRFSUMMARY', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('RET', 'C2CTRFSUMMARY', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('CORPE', 'C2CTRFSUMMARY', '1 ');



--Link-Internal User Roles report
--Add rolecode in roles table
INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('DISTB_CHAN', 'INTERNALUSERLIST', 'Internal User List', 'Channel Reports-User', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('CORPORATE', 'INTERNALUSERLIST', 'Internal User List', 'Channel Reports-User', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

--Add rolecode in category_role table
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('DIST', 'INTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SE', 'INTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('AG', 'INTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('RET', 'INTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('CORPE', 'INTERNALUSERLIST', '1 ');


--Link-External User List
--Add rolecode in roles table
INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('DISTB_CHAN', 'EXTERNALUSERLIST', 'External User List', 'Channel Reports-User', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type)
VALUES('CORPORATE', 'EXTERNALUSERLIST', 'External User List', 'Channel Reports-User', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B');

--Add rolecode in category_role table
INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('DIST', 'EXTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('SE', 'EXTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('AG', 'EXTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('RET', 'EXTERNALUSERLIST', '1 ');

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('CORPE', 'EXTERNALUSERLIST', '1 ');


##burn date defect fixes..
UPDATE system_preferences SET value_type='STRING' WHERE preference_code='BURN_RATE_THRESHOLD_PCT';

##Removes the default constraint of the column ‘ref_txn_id’ of ‘network_stock_transactions’ table).

ALTER TABLE network_stock_transactions ALTER COLUMN ref_txn_id DROP DEFAULT;

#fixes for alert MSISDN
update channel_users set alert_msisdn='' where alert_msisdn='null';
