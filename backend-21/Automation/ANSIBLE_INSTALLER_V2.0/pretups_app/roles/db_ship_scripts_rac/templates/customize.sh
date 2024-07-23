#!/bin/sh
if [ -f ~/.bash_profile ]; then
        . ~/.bash_profile
fi

for i in customize*.sql; do
   
sqlplus / as sysdba <<EOF
@$i
EOF

done
