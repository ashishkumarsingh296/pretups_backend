

<jsp:include page="../help/common/topband.jsp" flush="true">
<jsp:param name="menuname" value="Active Sessions"/>
<jsp:param name="jspHeading" value="Active Sessions"/>
</jsp:include>
<link rel="stylesheet" href="/pretups/monitorserver/help/common/main.css" type="text/css">
<table width="80%" align="center">
<tr align="left" valign="top"> 
	<td colspan="2" class="overview1">This screen displays the number of Active 
      Session running on the eRecharge Web Server. These active sessions allows 
      the administrator to invalidate any active user from the existing session.</td>
</tr>
</table>
<TABLE width="80%" border="0" cellpadding="3" cellspacing="1" class="back" align="center">
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif" >Login ID 
      :</td>
    <td width="70%" class="description">Login ID of the user</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1" height="28"><img src="../help/common/images/scrollend.gif"  height="16" align="top">Type 
      :</td>
    <td width="70%" class="description" height="28">The type associated with the 
      user </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Request 
      Host </td>
    <td width="70%" class="description">The request host address</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Request 
      IP :</td>
    <td width="70%" class="description">The request IP address</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Login Time 
      : </td>
    <td width="70%" class="description">The time at which the user logged in</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Last Accessed 
      Time :</td>
    <td width="70%" class="description">The time when the user last accessed the 
      system </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Invalidate 
      : </td>
    <td width="70%" class="description">Click to invalidate the user</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/button.gif" width="18" height="18"> 
     Back :</td>
    <td width="70%" class="label_value"> 
      <table width="100%" border="0" cellpadding="1" cellspacing="1">
        <tr> 
          <td class="description" colspan="2">Click <b>Back </b>to go to the previous 
            screen </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<jsp:include page="../help/common/bottomband.jsp" flush="true"/> 