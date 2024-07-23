insert
	into
	page_ui_roles (role_code,
	page_code)
values
	 ('BATCGMOD',
'P_NW_CHNL_TO_SUB');

update
	roles
set
	role_name = 'Batch modify C2S card group',
	group_name = 'Card Group',
	status = 'Y',
	role_type = 'A',
	from_hour = null,
	to_hour = null,
	group_role = 'N',
	application_id = '1',
	gateway_types = 'WEB',
	role_for = 'B',
	is_default = 'N',
	is_default_grouprole = 'N',
	access_type = 'B',
	sub_group_name = 'Sub_Group',
	sub_group_role = 'N',
	view_roles = 'Y'
where
	role_code = 'BATCGMOD'
	and domain_type = 'OPERATOR';



insert
	into
	role_ui_mapping (role_ui_parent_id,
	role_ui_id,
	rule_code)
values
	 ('networkAdmin',
'Batch_C2S_Modify',
'BATCGMOD');

UPDATE pages_ui
SET module_code='COMMON', page_url='/cardGroupC2s', menu_name='Channel To Subscriber', show_menu='Y', sequence_no=9.1, menu_level='2', parent_page_code='P_NW_CRDGRP_MAIN', image=NULL, role_code=NULL, domain_type='OPERATOR', category_code='NWADM', page_type=NULL, app_name='eTopup'
WHERE page_code='P_NW_CHNL_TO_SUB';
