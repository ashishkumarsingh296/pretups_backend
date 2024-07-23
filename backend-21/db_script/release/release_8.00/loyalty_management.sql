INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('REFERENCE_BASED', 'Reference Based', 'SYSTEMPRF', 'BOOLEAN', 'false', NULL, NULL, 50, 'Flag to specify reference based', 'Y', 'Y', 'C2S', 'Flag to specify reference based', '2637-01-24 23:52:13.000', 'ADMIN', '2637-01-24 23:52:13.000', 'SU0001', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('ACTIVATION_BONUS_SLAB_LENGTH', 'transaction max slabs', 'SYSTEMPRF', 'INT', '5', 1, 10, 50, 'Transaction Profile Details slabs', 'N', 'Y', 'C2S', 'Target based commission applicable', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');


INSERT INTO system_preferences
(preference_code, "name", "type", value_type, default_value, min_value, max_value, max_size, description, modified_allowed, display, "module", remarks, created_on, created_by, modified_on, modified_by, allowed_values, fixed_value)
VALUES('LOYALTY_TARTGET_SLAB_LENGTH', 'volume max slabs', 'SYSTEMPRF', 'INT', '3', 1, 10, 50, 'Volume Profile Details slabs', 'N', 'Y', 'C2S', 'Target based commission applicable', '2005-06-16 00:00:00.000', 'ADMIN', '2005-06-17 00:00:00.000', 'ADMIN', NULL, 'Y');