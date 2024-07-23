####################################################################
#                Cron Script                                       #
#                Bharti Telesoft LTD.                              #
#                Dated :03/02/2006                                 #
####################################################################
HOME=<Tomcat-Path>/webapps/pretups/pretups_scripts
# check for Services On which node Services are running
clear
echo
echo
a="/dev/cciss/c0d0p3"
used=`df -kh | grep "$a" | awk '{ print $5  }' | cut -f1 -d'%'`
ava=`df -kh | grep "$a" | awk '{ print $4  }'`
toa=`df -kh | grep "$a" | awk '{ print $2  }'`
fsa=`df -kh | grep "$a" | awk '{ print $6  }'`
echo
echo "      ******************************************************************************************"
echo "      *    MobiNil Pretups Server Disk Space Management Status  - `uname -n`                 *"
echo "      ******************************************************************************************"
echo "      *   Name of Disk Space  *      Total Disk      *      Used Percent    *     Available    *"
echo "      ******************************************************************************************"
echo "      *                       *                      *                      *                  *"
echo "      * $fsa :        *          $toa         *            $used %      *     $ava         *"
a1="/dev/cciss/c1d0p3"
used=`df -kh | grep "$a1" | awk '{ print $5  }' | cut -f1 -d'%'`
ava=`df -kh | grep "$a1" | awk '{ print $4  }'`
toa=`df -kh | grep "$a1" | awk '{ print $2  }'`
fsa=`df -kh | grep "$a1" | awk '{ print $6  }'`
echo "      *                       *                      *                      *                  *"
echo "      * $fsa :         *         $toa         *             $used %      *     $ava         *"
a2="/dev/cciss/c0d0p2"
used=`df -kh | grep "$a2" | awk '{ print $5  }' | cut -f1 -d'%'`
ava=`df -kh | grep "$a2" | awk '{ print $4  }'`
toa=`df -kh | grep "$a2" | awk '{ print $2  }'`
fsa=`df -kh | grep "$a2" | awk '{ print $6  }'`
echo "      *                       *                      *                      *                  *"
echo "      * $fsa :                   *         $toa         *            $used %      *     $ava         *"
echo "      *                       *                      *                      *                  *"
echo "      ******************************************************************************************"
#end of Script
