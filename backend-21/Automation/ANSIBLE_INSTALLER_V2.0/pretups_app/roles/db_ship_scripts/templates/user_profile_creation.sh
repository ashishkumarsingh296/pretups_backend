#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi


sqlplus / as sysdba <<EOF
@user_profile_creation.sql
EOF
