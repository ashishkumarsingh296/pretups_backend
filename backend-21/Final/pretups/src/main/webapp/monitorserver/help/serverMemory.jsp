

<jsp:include page="../help/common/topband.jsp" flush="true">
<jsp:param name="menuname" value=""/>
<jsp:param name="jspHeading" value="Server memory"/>
</jsp:include>
<link rel="stylesheet" href="/pretups/monitorserver/help/common/main.css" type="text/css">

<table width="80%" align="center">
<tr align="left" valign="top"> 
	<td colspan="2" class="overview1">This screen displays the memory details 
      of the eRecharge server.</td>
</tr>
</table>

<TABLE width="80%" border="0" cellpadding="3" cellspacing="1" class="back" align="center">
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif" >Total Memory 
      :</td>
    <td width="70%" class="description">The total memory capacity of the server</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1" height="28"><img src="../help/common/images/scrollend.gif"  height="16" align="top">Used 
      Memory :</td>
    <td width="70%" class="description" height="28">The used memory of the server </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Free Memory 
      : </td>
    <td width="70%" class="description">The available free memory of the server</td>
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
