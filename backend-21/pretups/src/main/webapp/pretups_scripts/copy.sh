#!/bin/sh
BAK="/pretupshome/pretups_sonatel/test" # store all backup files here
FTPU="oracle"
FTPP="p@ssw0rd!)!"
FTPS="172.16.1.156"
NOW=$(date +"%d-%m-%Y")
#lftp -u $FTPU,$FTPP -e "mkdir backup/$NOW;cd backup/$NOW; mput $BAK/*; quit" $FTPS
lftp -u $FTPU,$FTPP -e "cd /oracle/testftp; mput $BAK/*.txt; quit" $FTPS
