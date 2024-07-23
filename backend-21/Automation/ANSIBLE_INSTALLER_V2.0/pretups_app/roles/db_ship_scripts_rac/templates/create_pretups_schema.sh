#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi


sqlplus / as sysdba <<EOF
@create_pretups_schema.sql
EOF
