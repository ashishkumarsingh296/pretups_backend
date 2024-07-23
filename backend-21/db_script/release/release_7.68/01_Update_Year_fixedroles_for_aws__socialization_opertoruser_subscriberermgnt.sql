INSERT INTO ids
(id_year, id_type, network_code, last_no, last_initialised_date, frequency, description)
VALUES('2024', 'VMBTCHUD', 'ALL', 203, '2024-01-09 00:00:00.000', 'NA', NULL);

UPDATE categories
SET fixed_roles='Y' WHERE category_code='NWADM';

UPDATE roles
SET view_roles='Y'
WHERE role_code='BLACKLISTMGMT' AND domain_type='OPERATOR';

UPDATE roles
SET view_roles='Y'
WHERE role_code='UNBLKSUBS' AND domain_type='OPERATOR';

UPDATE roles
SET view_roles='Y'
WHERE role_code='BLKREGSUB' AND domain_type='OPERATOR';

UPDATE roles
SET view_roles='Y'
WHERE role_code='DELRESSUB' AND domain_type='OPERATOR';

INSERT INTO category_roles
(category_code, role_code, application_id)
VALUES('NWADM', 'DLTBATCHOPTUSR', '1');

INSERT INTO roles
(domain_type, role_code, role_name, group_name, status, role_type, from_hour, to_hour, group_role, application_id, gateway_types, role_for, is_default, is_default_grouprole, access_type, sub_group_name, sub_group_role, view_roles)
VALUES('OPERATOR', 'DLTBATCHOPTUSR', 'batch Operator User Delete', 'Operator Users', 'Y', 'A', NULL, NULL, 'N', '1', 'WEB', 'B', 'N', 'N', 'B', 'Sub_Group', 'N', 'Y');





