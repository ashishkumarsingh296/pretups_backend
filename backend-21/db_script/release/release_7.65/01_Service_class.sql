UPDATE pages_ui
SET module_code='COMMON', page_url='/services', menu_name='Service Class Management', show_menu='Y', sequence_no=4, menu_level='1', parent_page_code='ROOT', image='assets/images/updateCache/svg/updateCache.svg', role_code=NULL, domain_type='OPERATOR', category_code='SUADM', page_type='home_configuration', app_name=NULL, is_default=NULL
WHERE page_code='P_SU_SER_CLS_MAIN';

UPDATE page_ui_roles
SET role_code='ADDSERVICECLASS', page_code='P_SU_SER_CLS_MAIN'
WHERE role_code='ADDSERVICECLASS' AND page_code='P_SU_SER_CLS_MAIN';

UPDATE roles
SET role_name='Add Service Class', group_name='Masters', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Sub_Group', sub_group_role='N', view_roles='Y'
WHERE role_code='ADDSERVICECLASS' AND domain_type='OPERATOR';



========================Regex==================


ALPHA_NUMERIC_WITH_COMMA=^([A-Za-z0-9 ]+)(,\s*[A-Za-z0-9 ]+)*$
