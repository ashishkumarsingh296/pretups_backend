

<jsp:include page="../help/common/topband.jsp" flush="true">
<jsp:param name="menuname" value="Network Counters"/>
<jsp:param name="jspHeading" value="eRecharge Network Counters"/>
</jsp:include>
<link rel="stylesheet" href="/pretups/monitorserver/help/common/main.css" type="text/css">
<table width="80%" align="center">
<tr align="left" valign="top"> 
	<td colspan="2" class="overview1">This screen displays the count for the number 
      of networks available on the eRecharge SMS server. It displays the maximum 
      load allowed, received request with request in progress, total request count 
      and total refusal on the particular network. With the help of this screen, 
      the Administrator can also get to know the last received request time and 
      last refused time along with the last under process time.</td>
</tr>
</table>

<TABLE width="80%" border="0" cellpadding="3" cellspacing="1" class="back" align="center">
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif" > Instance 
      ID :</td>
    <td width="70%" class="label_value"> 
      <table width="100%" border="0">
        <tr> 
          <td class="description" colspan="2">ID assigned to the instance</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"  height="16" align="top"> 
      Network Code :</td>
    <td width="70%" class="label_value"> 
      <table width="100%" border="0">
        <tr> 
          <td class="description" colspan="2">Code of the network</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Allowed 
      Load :</td>
    <td width="70%" class="label_value"> 
      <table width="100%" border="0">
        <tr> 
          <td class="description" colspan="2">Maximum request that the network 
            can handle successfully at one point of time</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Received 
      Count :</td>
    <td width="70%" class="label_value"> 
      <table width="100%" border="0">
        <tr> 
          <td class="description" colspan="2">The total number of requests received 
            by the network</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Current 
      Count :</td>
    <td width="70%" class="description">The total number of current requests to 
      the network</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Total Request 
      Count :</td>
    <td width="70%" class="description">The total number of requests received 
      by the network, i.e. Received Count + Current Count</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Total Refused 
      Count :</td>
    <td width="70%" class="description">The total number of requests that was 
      refused by the network before processing</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Last Received 
      Time :</td>
    <td width="70%" class="description">Time at which the last request was received</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Last Refused 
      Time :</td>
    <td width="70%" class="description">The last recorded time when the network 
      refused a request</td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Last Under 
      Process Time :</td>
    <td width="70%" class="description">The last recorded time when a request 
      was under process </td>
  </tr>
  <tr align="left" valign="top"> 
    <td class="label_head1"><img src="../help/common/images/scrollend.gif"> Total :</td>
    <td width="70%" class="description">The total corresponding values</td>
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
