#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi


sqlplus / as sysdba <<EOF
@master_data_pretups_live.sql
EOF
