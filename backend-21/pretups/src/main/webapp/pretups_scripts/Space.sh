HOME=/pretupshome/tomcat5_web/webapps/pretups/monitorserver
nodename=`uname -n`

echo '<%@ page language="java" import="java.util.*" %>' > $HOME/diskspace.jsp
echo '<%@ page language="java" import="com.btsl.util.*"%>' >>$HOME/diskspace.jsp

echo '<%' >>$HOME/diskspace.jsp
#echo 'String path = request.getContextPath();'>> $HOME/diskspace.jsp
#echo 'String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";'>>$HOME/diskspace.jsp
echo '%>' >>$HOME/diskspace.jsp

echo '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">'>>$HOME/diskspace.jsp
echo '<html>' >>$HOME/diskspace.jsp
echo '  <head>' >> $HOME/diskspace.jsp
echo '    <jsp:include page="/monitorserver/common/topband.jsp"/> ' >> $HOME/diskspace.jsp
echo '	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">' >> $HOME/diskspace.jsp
echo '    <title>PreTUPS Server Space Status</title>' >> $HOME/diskspace.jsp
echo '    <meta http-equiv="pragma" content="no-cache">' >> $HOME/diskspace.jsp
echo '    <meta http-equiv="cache-control" content="no-cache">' >> $HOME/diskspace.jsp
echo '  </head>' >> $HOME/diskspace.jsp
echo '  <BODY leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>' >> $HOME/diskspace.jsp
echo '  <div class=heading><center><strong>PreTUPS eRecharge Server Disk Usage:' $nodename >> $HOME/diskspace.jsp
echo '</strong></center></div>' >>$HOME/diskspace.jsp
echo '<br/>' >>$HOME/diskspace.jsp
echo '<table height=44 width=450 border=1 cellspacing=0 cellpadding=0  align=center>' >> $HOME/diskspace.jsp
echo '<tr><td align=center>' >> $HOME/diskspace.jsp
echo '<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>' >> $HOME/diskspace.jsp
echo 'Server Disk </font>' >> $HOME/diskspace.jsp 
echo '</td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Total Disk</b></font>  </td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Used percent</b></font> </td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b> Available</b></font> ' >> $HOME/diskspace.jsp
echo '</BR></td></tr>'>> $HOME/diskspace.jsp

a1="/dev/cciss/c0d0p2"
a2="/dev/cciss/c0d0p1"
a3="/dev/cciss/c0d0p5"
a4="/dev/cciss/c0d0p3"
a5="/dev/cciss/c0d1p1"
a6="/dev/cciss/c1d0p3"

declare -i count
count=1
until [ $count -gt 6 ]
do
case $count in
        1)a1=$a1;;
        2)a1=$a2;;
        3)a1=$a3;;
        4)a1=$a4;;
        5)a1=$a5;;
        6)a1=$a6;;
esac
a=$a1
############### Fetching information about the partitioning###############

used=`df -kh | grep "$a" | awk '{ print $5  }' | cut -f1 -d'%'`
ava=`df -kh | grep "$a" | awk '{ print $4  }'`
toa=`df -kh | grep "$a" | awk '{ print $2  }'`
fsa=`df -kh | grep "$a" | awk '{ print $6  }'`



############### Fetching information about the partitioning###############

echo '<tr><td>' >> $HOME/diskspace.jsp
echo '<font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2><b>' >> $HOME/diskspace.jsp
echo $fsa >> $HOME/diskspace.jsp
echo '</b></font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>' >> $HOME/diskspace.jsp
echo $toa >> $HOME/diskspace.jsp
echo '</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>' >> $HOME/diskspace.jsp
echo $used >> $HOME/diskspace.jsp 
echo '%</font></td><td align=center><font color=#808080 style=Verdana, Arial, Helvetica, sans-serif SIZE=2>' >> $HOME/diskspace.jsp
echo $ava >> $HOME/diskspace.jsp
echo '</font>' >> $HOME/diskspace.jsp
echo '</BR></td></tr>' >> $HOME/diskspace.jsp
count=$count+1
done

echo '</table>' >> $HOME/diskspace.jsp
echo '   <br>' >> $HOME/diskspace.jsp
echo '   <jsp:include page="/monitorserver/common/bottomband.jsp"/>'>>$HOME/diskspace.jsp
echo '	</body></html>'>> $HOME/diskspace.jsp
