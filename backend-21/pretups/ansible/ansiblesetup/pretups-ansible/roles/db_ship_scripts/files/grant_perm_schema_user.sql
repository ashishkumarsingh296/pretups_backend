spool on;
spool grant_perm_schema_user.log;

   GRANT CONNECT TO {{ PRETUPS_SCHEMA_USER_NAME }};
   GRANT RESOURCE TO {{ PRETUPS_SCHEMA_USER_NAME }};
   ALTER USER {{ PRETUPS_SCHEMA_USER_NAME }} DEFAULT ROLE ALL;
   GRANT CREATE SESSION TO {{ PRETUPS_SCHEMA_USER_NAME }};
   GRANT UNLIMITED TABLESPACE TO {{ PRETUPS_SCHEMA_USER_NAME }};

spool off;