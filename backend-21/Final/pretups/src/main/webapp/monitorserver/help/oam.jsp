

<jsp:include page="../help/common/topband.jsp" flush="true">
<jsp:param name="menuname" value="OAM"/>
<jsp:param name="jspHeading" value="OAM Alarms Interface"/>
</jsp:include>
<link rel="stylesheet" href="/pretups/monitorserver/help/common/main.css" type="text/css">
<table width="80%" align="center">
<tr align="left" valign="top"> 
	<td colspan="2" class="overview1">This screen is used to view all the alarms 
      that has taken place at the system or interface level, with the criticality 
      of the error. While monitoring, the Administrator has the option to release 
      an alarm. This screen has two options, namely Pending Alarms and Current 
      Alarms. </td>
</tr>
</table>

<TABLE width="80%" border="0" cellpadding="3" cellspacing="1" class="back" align="center">
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif" >Pending 
      Alarms :</td>
    <td width="70%" class="description">Click to view the alarm(s) which is pending 
      on the server. A check box is provided on the left hand-side, which the 
      Administrator can use to clear the alarm(s) from the server.</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1" height="28"><img src="../help/common/images/scrollend.gif"  height="16" align="top">Current 
      Alarms :</td>
    <td width="70%" class="description" height="28">In Current Alarms, the Administrator 
      has the option to view the alarms by selecting the criticality , the component 
      and the date of the alarm(s).</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif">Criticality 
      : </td>
    <td width="70%" class="description"> 
      <p>Select the criticality of the alarm(s) which is to be viewed</p>
      <p><img src="../common/images/atopt.gif" width="17" height="16"> <span class="validation1">Major, 
        Minor, Fatal, All</span></p>
    </td>
  </tr>
  <tr align="left" valign="top">
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Component 
      : </td>
    <td width="70%" class="description">
      <p>The level at which the alarm(s) was triggered</p>
      <p><img src="../common/images/atopt.gif" width="17" height="16"> <span class="validation1">All, 
        System, Interface</span></p>
    </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Date :</td>
    <td width="70%" class="description">Select the date for which the alarm(s) 
      is to be viewed</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Line :</td>
    <td width="70%" class="description">The number of alarm(s) that is to be viewed</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/button.gif" width="18" height="18"> 
      <span class="btn">Submit :</span></td>
    <td width="70%" class="description">Click Submit to view the alarm(s) for 
      the selected criteria</td>
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