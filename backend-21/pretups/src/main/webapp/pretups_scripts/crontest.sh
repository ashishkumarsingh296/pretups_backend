####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
HOME=<Tomcat-Path>/webapps/pretups/pretups_scripts
# check for Services On which node Services are running
        curl 'http://127.0.0.1:5555/pretups/test.html'
        ret=`echo $?`
         if [ $ret = 0 ] ; then
        	  echo "`uname -n` is active"
                  sh diskSpace.sh 
         else 
                  echo "`uname -n` is not active"
         fi
