spool on;
spool pretups_table_space;

create or replace directory Dbdump_dir as '{{ BASE_DIR }}/{{ Temp_Dir }}' ;
spool off;

