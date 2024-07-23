#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi


sqlplus / as sysdba <<EOF
@grant_perm_schema_user.sql
EOF
