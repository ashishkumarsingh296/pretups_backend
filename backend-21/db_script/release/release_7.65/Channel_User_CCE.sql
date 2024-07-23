UPDATE pages_ui
SET module_code='COMMON', page_url='/channelAdmin/Single', menu_name='Channel User', show_menu='Y', sequence_no=3, menu_level='1', parent_page_code='ROOT', image='assets/images/channelUser/svg/channelUserIcon.svg', role_code=NULL, domain_type='OPERATOR', category_code='CCE', page_type=NULL, app_name='eTopup', is_default=NULL
WHERE page_code='P_CHNL_USR_MAIN_CCE';


INSERT INTO page_ui_roles
(role_code, page_code)
VALUES('VIEWCUSER', 'P_CHNL_USR_MAIN_CCE');


UPDATE roles
SET role_name='View Channel User', group_name='Channel Enquiry', status='Y', role_type='A', from_hour=NULL, to_hour=NULL, group_role='N', application_id='1', gateway_types='WEB', role_for='B', is_default='N', is_default_grouprole='N', access_type='B', sub_group_name='Reports & Enquiries', sub_group_role='N', view_roles='Y'
WHERE role_code='VIEWCUSER' AND domain_type='OPERATOR';