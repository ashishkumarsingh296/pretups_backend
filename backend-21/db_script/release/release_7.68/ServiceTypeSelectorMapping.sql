update pages_ui set page_url ='/serviceSelectMap' where page_code ='P_NW_SER_TYPSEL_MAIN';

update roles set view_roles ='Y' where role_code ='SERSELMAP';

update page_ui_roles set role_code ='SERSELMAP' where page_code ='P_NW_SER_TYPSEL_MAIN';
