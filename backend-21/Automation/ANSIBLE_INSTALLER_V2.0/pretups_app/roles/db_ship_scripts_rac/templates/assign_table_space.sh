#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi


sqlplus / as sysdba <<EOF
@assign_table_space.sql
EOF
