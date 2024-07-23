#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi

impdp system/manager  directory=dbdump_dir  dumpfile={{ Dump_name }}.dmp logfile={{ Dump_name }}.log remap_schema={{ SOURCE_PRETUPS_SCHEMA_NAME }}:{{ PRETUPS_SCHEMA_USER_NAME }} 

