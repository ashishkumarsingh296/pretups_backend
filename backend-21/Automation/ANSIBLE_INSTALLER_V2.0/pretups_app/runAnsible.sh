#!/bin/sh

echo " site.yml start time = ` date` " >>timelag.log
ansible-playbook -i hosts site.yml -vvvv
echo " site.yml end time = `date`" >>timelag.log


#echo " dbconf.yml start time = ` date` " >>timelag.log
#ansible-playbook -i hosts dbconf.yml -vvvv
#echo " dbconf.yml end time = `date`" >>timelag.log

