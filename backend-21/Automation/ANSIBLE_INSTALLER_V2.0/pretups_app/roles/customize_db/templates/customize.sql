spool on;
spool customization_{{ tomcat_no }}.log;
Insert into {{ PRETUPS_SCHEMA_USER_NAME }}.INSTANCE_LOAD
   (INSTANCE_ID, INSTANCE_NAME, CURRENT_STATUS, IP, PORT, 
    INSTANCE_LOAD, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, 
    INSTANCE_TPS, REQUEST_TIME_OUT, INSTANCE_TYPE, LOAD_TYPE_TPS, MAX_ALLOWED_LOAD, 
    MAX_ALLOWED_TPS, MODULE, SHOW_SMSC_STAT, SHOW_OAM_LOGS, IS_DR, 
    AUTHENTICATION_PASS, CONTEXT)
 Values
   ('{{ INSTANCE_ID }}', 'WEB Server-{{ tomcat_no }}', 'Y', '{{ IP }}', '{{ CONNECTOR_PORT }}', 
    100, TO_DATE('05/30/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', TO_DATE('05/30/2006 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 'ADMIN', 
    100, 10000, 'WEB', 'N', 1000, 
    200, 'ALL', 'Y', 'Y', 'P', 
    'G23QYHTfjWlJFc2sU0PYiSIv+4o1JjR9htycY1T+a3A=', 'pretups');
COMMIT;

spool off;
EOT
