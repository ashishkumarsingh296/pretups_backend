<jsp:directive.page import="java.util.Enumeration" />
<%@page import="com.selftopup.util.BTSLUtil"%>
<%@ page import="com.btsl.pretups.common.PretupsI" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);
	// Calculate the view sources url
	String sourceUrl = request.getContextPath() + "/viewSource.action";
	boolean bolModuleCodeFound = false;
	boolean bolPageCodeFound = false;
	boolean bolURLConstainsScript = false;

	if (request.getQueryString() != null
			&& request.getQueryString().contains("SCRIPT>")) {
		bolURLConstainsScript = true;
	}	
	ArrayList<com.selftopup.menu.MenuItem> menuItemList = null;
	com.opensymphony.xwork2.ActionInvocation inv = com.opensymphony.xwork2.ActionContext
			.getContext().getActionInvocation();
	org.apache.struts2.dispatcher.mapper.ActionMapping mapping = org.apache.struts2.ServletActionContext
			.getActionMapping();
	if (inv != null) {
		com.opensymphony.xwork2.util.location.Location loc = inv
				.getProxy().getConfig().getLocation();
		sourceUrl += "?config="
				+ (loc != null ? loc.getURI() + ":"
						+ loc.getLineNumber() : "");
		sourceUrl += "&className="
				+ inv.getProxy().getConfig().getClassName();
		if (inv.getResult() != null
				&& inv.getResult() instanceof org.apache.struts2.dispatcher.StrutsResultSupport) {
			sourceUrl += "&page="
					+ mapping.getNamespace()
					+ "/"
					+ ((org.apache.struts2.dispatcher.StrutsResultSupport) inv
							.getResult()).getLastFinalLocation();
		}
	} else {
		sourceUrl += "?page=" + request.getServletPath();
	}
%>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page"%>
<%@ page import="java.util.ArrayList,com.selftopup.cp2p.subscriber.businesslogic.*,com.selftopup.util.*"%>
<jsp:directive.page import="com.selftopup.menu.MenuBL" />
<jsp:directive.page import="com.selftopup.util.UtilValidate" />
<jsp:directive.page import="com.selftopup.util.Constants" />
<jsp:directive.page import="com.selftopup.util.BTSLUtil" />
<%@ taglib uri="menu" prefix="menu"%>
<%@ taglib uri="struts-tags" prefix="s"%>

<%	
	String requestLocale = BTSLUtil.getBTSLLocale(request)
			.getLanguage()
			+ "_" + BTSLUtil.getBTSLLocale(request).getCountry();
	boolean localeFlag = false;
	String locale;
	String localeList = Constants.getProperty("ALLOWED_LOCALES");
	boolean isModCodeFromRequest = true;
	String modCode = request.getParameter("moduleCode");
	String pageCode = request.getParameter("pageCode");
	if (UtilValidate.isEmpty(modCode)) {
		isModCodeFromRequest = false;
		modCode = (String) session.getAttribute("moduleCode");
		if (modCode == null) {
			modCode = "ALL";
		}
	}
	if (UtilValidate.isEmpty(pageCode)) {
		pageCode = (String) session.getAttribute("pageCode");
		if (pageCode == null) {
			pageCode = "ALL";
		}
	}

	session.setAttribute("moduleCode", modCode);
	//session.setAttribute("pageCode",pageCode);
	String display = "all";
	String getActualFile = request.getRequestURI();
	String getFileArr[] = getActualFile.split("/");
	String print = getFileArr[getFileArr.length - 1];
	String getFileArrPrec[] = getFileArr[getFileArr.length - 1]
			.split("_");
	String getFile = getFileArrPrec[getFileArrPrec.length - 1];
	if (getFile.equals("gethomepage.action")) {
		display = "none";
	}

	if (BTSLUtil.isStringIn(requestLocale, localeList)) {
		localeFlag = true;
	}

	String logoImage = "logo5.gif"; //Earlier-- logo.gif --> top.bmp
	if (localeFlag) {
		logoImage = "logo_" + requestLocale + ".jpg";
	}
	String rightImage = "logo5.gif";
	String midImage = "spacer.gif";
	String moduleCodeTemp = request.getParameter("moduleCode");
	String context = request.getContextPath();
	String securityLogoutUrl = context + "/cp2plogin/cp2plogin_logedout.action";
	//System.out.println("topband.jsp moduleCodeTemp="+moduleCodeTemp);
	String imagePath = context + "/jsp/selftopup/common/images/";
	String selectedMenuName = null;
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title><s:text name="project.title" /></title>
		<%
			String cssName;
			if (localeFlag) {
				cssName = "/jsp/selftopup/common/main_" + requestLocale + ".css";
			} else {
				cssName = "/jsp/selftopup/common/main.css";
			}
		%>

		<script type="text/javascript">
function backExpire()
{
	if(window.document.test.isBack)
	{
		var isBack=window.document.test.isBack.value;
	}
	else
	{
		var isBack="0";
	}
	if(isBack!="1")
  	{
		window.document.test.isBack.value="1";
	}
	else
	{
		window.document.write('     ');
		location.replace("<%=securityLogoutUrl%>");
	}

	//document.oncontextmenu = function(){ 
		//window.status = 'Right-click is disabled'; 
		//event.returnValue=false; 
	//} 
	
	//MSIE6 disable F5 && ctrl+r && ctrl+n 
	document.onkeydown=function()
	{ 
	//alert('keycode='+event.keyCode + 'event.ctrlKey='+event.ctrlKey ); 
	switch (event.keyCode)
	 { 
	case 116 : //refresh using F5 
	event.returnValue=false; 
	event.keyCode=0; 
	window.status = 'Refresh is disabled'; 
	window.document.write('     ');
	location.replace("<%=securityLogoutUrl%>");
	
	case 82 ://refresh using ctrl+r
	if (event.ctrlKey) { 
	event.returnValue=false; 
	event.keyCode=0; 
	window.status = 'Refresh is disabled';
	} 
   } 
 }	
}
function checkObj()
{
     document.getElementById('xvyc2').value = '1';
     if(typeof(parent.tangoObject) == 'undefined')
     {
     	location.replace("<%=securityLogoutUrl%>");
     }
}
function navigateMenu(url)
{ 
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", url);
		document.body.appendChild(form);		
		form.submit();	
	    document.body.removeChild(form);		   
}
function showClock()
{
	var clock=new Date();
	var hours=clock.getHours();
	var minutes=clock.getMinutes();
	var seconds=clock.getSeconds();

	if (hours<10){	
	hours="0" + hours;
	}
	if (minutes<10){
	minutes="0" + minutes;
	}
	if (seconds<10){
	seconds="0" + seconds;
	}
	t=setTimeout('showClock()',500);
}
function logOutUser()
{
	if(window.screenTop > 10000)
	{
		var mywin=window.open('<%=context
					+ "/cp2plogin/cp2plogin_logout.action?id=popup_main2"%>','','scrollbars=no,resizable=no,width=10,height=10');
		mywin.close();
	}
}
function logOut(url)
{
	window.document.forms[0].action=url;
	window.document.forms[0].submit();
}
function win_popup(helpfile)
{
	<%
		if(localeFlag)
		{
	%>
			var theURL = "<%=context + "/jsp/selftopup/webhelp_" + requestLocale%>"+helpfile+"?id=popup_main1";
	<%
		}
		else
		{
	%>		
			
			var theURL = "<%=context + "/jsp/selftopup/webhelp/Index.htm"%>";
	<%
	
		}
		
	%>	
	window.open(theURL,'','scrollbars=yes,resizable=yes,width=600,height=500');
}
</script>

<script type="text/javascript" src="<%=request.getContextPath()%>/jsp/selftopup/common/common.js">
   	checkForClickJackingAttack();
</script>
		<link rel="stylesheet" type="text/css"
			href="<%=request.getContextPath() + cssName%>" />
		<link
			href="<s:url value='/jsp/selftopup/common/main.css' encode='false' includeParams='none'/>"
			rel="stylesheet" type="text/css" media="all" />

		
		<title><s:text name="project.title" /></title>

	</head>

	<%
		String menuRequired = request.getParameter("menuRequired");
		if ((menuRequired != null)
				&& (menuRequired.trim().equalsIgnoreCase("N"))) {
	%>
	<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
		onLoad="javascript:backExpire();javascript:checkObj();showClock();<decorator:getProperty property='body.onload' />;">
	<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
		onLoad="javascript:backExpire();showClock();<decorator:getProperty property='body.onload' />;">
		<%
			} else {
		%>
	
	<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
		onLoad="javascript:backExpire();javascript:checkObj();showClock();<decorator:getProperty property='body.onload' />;"
		onUnload="javascript:logOutUser()">
	<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"
		onLoad="javascript:backExpire();showClock();<decorator:getProperty property='body.onload' />;"
		onUnload="javascript:logOutUser()">
		<%
			}
			String isBack = "0";
		%>
	
	<body id="page-home"
		onload="<decorator:getProperty property='body.onload' />">
		<div id="page">
			<div id="outer-header">
				<div id="header" class="clearfix">
					<div id="search">
					</div>
					<!-- end search -->
					<%
						String requestLanguage = BTSLUtil.getBTSLLocale(request)
								.getLanguage();
						String rtlLanguages = Constants
								.getProperty("LANGUAGES_RTL_DIRECTION");
						if (BTSLUtil.isStringIn(requestLanguage, rtlLanguages)) {
					%><body style="direction: rtl;">
						<%
							} else {
						%>
					
					<body style="direction: ltr;">
						<%
							}
						%>

						<form name="test">
							<input type="hidden" name="isBack" />
						</form>
						<form name="topband">
							<%
								CP2PSubscriberVO cp2pSubscriberVO = (CP2PSubscriberVO) session.getAttribute("cp2pSubscriberVO");
								String commonPath = null;
								if (cp2pSubscriberVO != null) {
									commonPath = request.getContextPath();
									java.io.File file = null;
									try {
										file = new java.io.File(getServletConfig()
												.getServletContext().getRealPath(commonPath));
									} catch (Exception e) {
									}
									if (!(file != null && file.exists())) {
										commonPath = request.getContextPath() + "/jsp/selftopup/common/";
									}
								} else {
									try{
									session.invalidate();
									}
									catch(IllegalStateException ise){
									
									}
									finally{
								%>	
											<script type="text/javascript">
												window.location.href="<%=context+ "/cp2plogin/cp2plogin_logedout.action"%>";
											</script>
								<%		
									}
								%>
											<script type="text/javascript">
												window.location.href="<%=context+ "/cp2plogin/cp2plogin_logedout.action"%>";
											</script>
							<%
								}
								String mainCSSlink = commonPath + "/main.css";
							%>
							<link rel="stylesheet" href="<%=mainCSSlink%>" type="text/css" />
							<%
								//find if the page is printer friendly by default no
								String printable = request.getParameter("printable");
								String heading = request.getParameter("heading");
								String helpFile="webhelp";
								//String helpFile = request.getParameter("helpFile");
								String mandatoryLabel = request.getParameter("mandatoryLabel");

								if (printable == null)
									printable = "N";
								//set the printerfriendly attribute into the session as we have to change the display of bottomband accordingly
								session.setAttribute("printable", printable);
								session.setAttribute("menuRequired", menuRequired);

								boolean displayMenu = true;
								boolean dispMenu = true;
								//System.out.println("topband.jsp menuRequired="+menuRequired);
								//System.out.println("topband.jsp printable="+printable);

								if (request.getParameter("noMenu") != null
										&& request.getParameter("urlCode") == null) {
									displayMenu = false;
							%>
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<!-- table a0 -->
								<tr>
									<td background="../common/images/spacer.gif">
										<table width="100%" border="0" cellpadding="0" cellspacing="0">
											<!-- table a1 -->
											<tr>
												<td colspan="3">
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0">
														<!-- table a2 -->
														<tr>
															<td width="30%" valign="bottom">
																<img src="<%=commonPath + "images/mobiquity6.jpg"%>"
																	alt="ComvivaLogo" width="150" height="40" />
															</td>
															<td width=15% align="right" bgcolor="white"
																style="border: none;">
																<img src="<%=commonPath + "images/logo.gif"%>"
																	alt="ComvivaLogo" width="150" height="25" />
															</td>
														</tr>
														<tr>
															<td colspan="2" valign="top" class="bgcollow">
																&nbsp;
															</td>
														</tr>
													</table>
													<!-- table a2 end -->

												</td>
											</tr>
										</table>
										<!-- table a1 end -->
									</td>
								</tr>
							</table>
							<!-- table a0 end -->
							<%
								} else if ((menuRequired != null)
										&& (menuRequired.trim().equalsIgnoreCase("N"))) {
									displayMenu = false;
								}
								if (UtilValidate.isEmpty((String) session.getAttribute("dispMenu"))) {
									dispMenu = false;
								}

								if (displayMenu) {
									if (dispMenu) {
										menuItemList = (ArrayList) session
												.getAttribute("menuItemList");
									}
									if (moduleCodeTemp == null) {
										moduleCodeTemp = (String) session
												.getAttribute("moduleCode");
									} else {
										session.setAttribute("moduleCode", moduleCodeTemp);
										try {
											String urlC = (String) session.getAttribute("urlCode");
											if (urlC != null)
												session.removeAttribute("urlCode");
										} catch (Exception ex) {
											System.err.println(ex.getMessage());
											ex.printStackTrace();
										}
									}
							%>
							<table width="95%" height="590" align="center" border="0"
								cellpadding="0" cellspacing="0">
								<!-- table a3 -->
								<tr valign="top">
									<td>
										<table width="100%" border="0" cellpadding="0" cellspacing="0">
											<!-- table a4 -->
											<tr>
												<td colspan="2">
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0">
														<!-- table a5 -->
														<tr>
															<td>
																<br />
															</td>
														</tr>
														<tr>

															<%
																//String home_url = context + "/login/login_gethomepage.action";
															%>
															<td width="50%">
																<div
																	style="font-size: 18px; color: #173556; margin-left: +8px; font-weight: bold;">
																	<s:text name="Application.Name" />
																</div>
															</td>
															<td width=15% align="right" bgcolor="white"
																style="border: none;">
																<img src="<%=commonPath + "images/logo.gif"%>"
																	alt="ComvivaLogo" width="120" height="25" />
															</td>
														</tr>
													</table>
													<table width=100%>
														<tr style="background-color: #C0C0C0; height: 10px">
															<!--
         	<td colspan="2" valign="top" class="bgcollow">
          	&nbsp;
         	</td>
         
         </tr>
		  
		 <tr>
 	 	 
 	 	 <td>
 	 	 -->
															<td width="10px">
																<%
																	String home_url = context + "/cp2plogin/cp2plogin_gethomepage.action";
																%>
																<a href="<%=home_url%>"> <img
																		src="<%=commonPath + "images/home.jpeg"%>"
																		border="0px" alt="home" border="none" width="22"
																		height="20" /> </a>
															</td>
															<td align="left" colspan="2">
																<table cellpadding="0" cellspacing="5" width="100%">
																	<tr>
																		<td width="82%">
																			<%
																				if (BTSLUtil.isStringIn(requestLanguage, rtlLanguages)) {
																			%>
																			<div align="right" class="header">
																				<%
																					} else {
																				%>
																				<div align="left" class="header">
																					<%
																						}
																					%>


																					<s:text name="common.topband.label.account" />
																					<s:text name="label.seperator" />
																					<%
																						if (cp2pSubscriberVO.getLoginId() != null) {
																					%>
																					<%=cp2pSubscriberVO.getLoginId()%>
																					<%
																						} else {
																					%>
																					<%=cp2pSubscriberVO.getLoginId()%>
																					<%
																						}
																					%>
																					|
																					<s:text name="common.topband.label.network" />
																					<s:text name="label.seperator" />
																					<%=cp2pSubscriberVO.getNetworkName()%>


																					|
																					<span class="header"> <s:text
																							name="common.topband.label.pagecode" /> <s:text
																							name="label.seperator" /> <%=pageCode%> | <s:text
																							name="common.topband.label.category" /> <s:text
																							name="label.seperator" /> <%=cp2pSubscriberVO.getCategory()%>
																						| <s:text name="common.topband.label.time" /> <s:text
																							name="label.seperator" /> <!--<s:div id="showText"></s:div>
       			--> <%
 	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
 				PretupsI.TIMESTAMP_DATESPACEHHMMSS);
 		sdf.setLenient(false);
 %> <%=sdf.format(new java.util.Date())%> | <s:text
																							name="common.topband.label.oldtime" />
																						<s:text name="label.seperator" /> <%if(cp2pSubscriberVO.getLastLoginOn()!=null) %><%=sdf.format(cp2pSubscriberVO.getLastLoginOn())%>
																						&nbsp; &nbsp; &nbsp; &nbsp; </span>
																				</div>
																		</td>
																		<%
																			String changePasswordUrl = context
																						+ "/cp2plogin/changePwd_input.action?moduleCode=CHGPWD&page=0";
																		%>
																		<td width="240px" style="float: right;">
																			<%
																				if (BTSLUtil.isStringIn(requestLanguage, rtlLanguages)) {
																			%>
																			<div align="left">
																				<%
																					} else {
																				%>
																				<div align="right">
																					<%
																						}
																							//if(SessionCounter.pushUrlMap!=null)
																							if (true) {
																								String url = null;
																								//String url=(String)SessionCounter.pushUrlMap.get(session.getId());
																								if (UtilValidate.isEmpty(url)) {
																					%>
																					<a href="<%=changePasswordUrl%>" class="toplink">
																						<s:text name="common.topband.label.changepassword" />
																					</a> |

																					<%
																						}
																							}
																							String logoutUrl = context + "/cp2plogin/cp2plogin_logout.action";
																					%>
																					<a href="#" onclick="logOut('<%=logoutUrl%>')"
																						class="toplink"> <s:text
																							name="common.topband.label.logout" /> </a>
																				</div>
																		</td>
																	</tr>
																</table>
															</td>
															<!--if block ending of tr and td tags -->
														</tr>
													</table>
													<!-- table a5 end-->
												</td>

											</tr>
											<tr>
												<td colspan="2" valign="top" class="bgcollow">
													&nbsp;
												</td>
											</tr>
											<tr>

												<td valign="top" width="240px">

													<table width="100%" border="0px" cellpadding="0"
														cellspacing="0">
														<!-- a6 table -->
														<tr>
															<td>
																<%
																	if (!"N".equals(request.getParameter("leftMenu"))) {
																			try {
																				int i1 = 0;
																				System.out.println("%#$%#$%$%" + menuItemList);
																%>

																<menu:menu menuList="<%=menuItemList%>" level="1">
																	<%
																		if (menuObj != null) {
																											if (menuObj.getModuleCode()
																													.equals(modCode)) {
																												bolModuleCodeFound = true;
																											}
																											String tempMenuUrl = menuUrl;
																											String nextPageCode = pageCode;
																											StringBuffer strBuff = new StringBuffer();
																											if (!MenuBL.isHourBetweenStrings((new java.util.Date()).getHours(),menuObj.getFromTimeStr(),menuObj.getToTimeStr(),",", strBuff)) {
																												tempMenuUrl = context	+ "/commonAction/commonAction.action?method=invalidTimings";
																												/*tempMenuUrl=context+"/common/messagePage.jsp?parentCode="+parentCode+"&errorMessage="+BTSLUtil.generateSuccessMessage(Locale.getDefault(),"user.topband.message.notallowedtiming,"+strBuff.toString());*/

																											} else {

																												String tempURL = context+ menuUrl;
																												if (tempURL.indexOf("?") == -1)
																													tempMenuUrl = context+ menuUrl+ "?moduleCode="+ moduleCode+ "&level=2";
																												else
																													tempMenuUrl = context+ menuUrl+ "&moduleCode="+ moduleCode+ "&level=2";
																											}
																	if (moduleCode == null)
																		moduleCode = "";
																		System.out.println("************ moduleCode="+ moduleCode+ " moduleCodeTemp="+ moduleCodeTemp);
																	if (moduleCode.equals(moduleCodeTemp)&& display.equalsIgnoreCase("all")) {
																	%>
																	<table width="100%" border="0" cellspacing="1"
																		cellpadding="1">
																		<tr>
																			<td width="100%" class="level1active"
																				style="border-bottom-style: solid; border-bottom-color: #173556; border-bottom-width: 1px;">
																				<a onclick="javascript:navigateMenu('<%=tempMenuUrl%>')">
																								<s:text name="<%=moduleCode%>"/>
																					<%
																						System.out.println("$$$$$$$$$$$$"+ tempMenuUrl+ moduleCode);
																					%> </a>
																			</td>
																		</tr>
																		<jsp:include page="/jsp/selftopup/common/mtopband.jsp"
																			flush="false">
																			<jsp:param name="selected" value="true" />
																		</jsp:include>
																	</table>
																	<%
																		selectedMenuName = moduleCode;
																											} else {
																	%>
																	<table width="100%" border="0" cellspacing="1"
																		cellpadding="1">
																		<tr>
																			<td width="10%"
																				style="border-bottom-style: solid; border-bottom-color: #173556; border-bottom-width: 1px;">
																				<span class="level1"><a class="level1"
																					onclick="javascript:navigateMenu('<%=tempMenuUrl%>')">
																					<s:text name="<%=moduleCode%>"/>
																						</a></span>
																			</td>
																		</tr>
																	</table>
																	<%
																		}
																											i1++;
																										}
																	%>
																</menu:menu>
																<%
																	} catch (Exception ex) {
																			}
																		}
																%>

															</td>
														</tr>
														<%
															if (dispMenu) {
																	if (menuItemList.size() > 0) {
														%>

														<tr>

														</tr>
														<%
															}
														%>
														<%
															}
														%>
													</table>
													<!-- end a6 table -->



												</td>
												<td valign="top" width="75%" align="left"
													style="padding-left: 30px">
													<%
														if (display.equalsIgnoreCase("all")) {
													%>
													<table width="100%" border="0px" cellpadding="3"
														cellspacing="0" style="background-color: #C0C0C0;">
														<!-- table a7 starts -->
														<tr>
															<td width="60%">
																<%
																	if (!UtilValidate.isEmpty(modCode)) {

																				if (!modCode.equals("ALL")) {
																%>
																<font class="heading"> <s:text name="<%=modCode%>" />
																</font>
																<%
																	}

																			}
																%>
																<%
																	if (!UtilValidate.isEmpty(pageCode)) {

																				if (!pageCode.equals("ALL")) {
																%>
																<span style="FONT-WEIGHT: bold; FONT-SIZE: 10pt;">&gt;&nbsp;</span><font
																	class="headingArihant"> <s:text name="<%=pageCode%>"></s:text>
																	<%
																		}
																				}
																	%> </font>
															</td>
														</tr>

													</table>
													<%
														}
													%>
													<!--  table a7 ends -->
													<table width="85%" border="0" align="center"
														cellpadding="0" cellspacing="0">
														<!-- table a8 starts -->
														<tr>
															<td>
																<%
																	if (helpFile == null || helpFile.length() == 0) {
																%>
																<br />
																<%
																	} else if (helpFile != null
																				&& BTSLUtil.isStringIn(requestLanguage,
																						rtlLanguages)) {
																%>
																<p align="left">
																	<a href="javascript:win_popup('<%=helpFile%>')"><img
																			src="<%=commonPath + "/images/help1.gif"%>" border="0"
																			align="absbottom"
																			alt='<s:text name="common.topband.tooltip.help"/>'>
																	</a>
																</p>
																<%
																	} else if (helpFile != null) {
																%>
																<p align="right">
																	<a href="javascript:win_popup('<%=helpFile%>')"><img
																			src="<%=commonPath + "/images/help1.gif"%>" border="0"
																			align="absbottom"
																			alt='<s:text name="common.topband.tooltip.help"/>'>
																	</a>
																</p>
																<%
																	}
																	} else if (printable.equals("Y")) {
																%>
															</td>
														</tr>
														<tr>
															<td>
																<div align="center">
																</div>
																<%
																	}
																%>
															</td>
														</tr>
														<%
															if (heading != null) {
														%>
														<tr>
															<td>
																<div align="center" class="heading">
																	<s:text name="<%=heading%>" />
																</div>
																<br />
															</td>
														</tr>
														<%
															}

															if (mandatoryLabel != null && "Y".equals(mandatoryLabel)) {
														%>
														<tr>
															<td>
																<div class="mandatory">
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																	<s:text name="heading.mandatory" />
																</div>

															</td>
														</tr>
														<%
															}
														%>
													</table>
													<!-- table a8 ends -->
													<input type="hidden" name="xvyc2" value="2" />
													</form>

													<!-- end header -->

													<s:actionerror />
													<s:actionmessage />

													<table align="left" width="100%" height="100%">
														<tr>
															<td colspan="0" valign="top" class="bgcollow">

															</td>
														</tr>
														<tr>
															<td>
																<div id="content" class="clearfix">
																	<decorator:body />
																	<!-- <div id="nav"></div> end nav -->
																	<!-- </div> end content -->
															</td>
														</tr>
														<tr>
															<td colspan="0" valign="top" class="bgcollow">

															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
										<!-- table a4 ends -->
									</td>
								</tr>


							</table>
							<!-- table a3 ends -->
							<table width="95%" align="center" border="0" cellpadding="0"
								cellspacing="0">
								<tr style="background-color: #C0C0C0">
									<td colspan="6">
										<br />
									</td>
								</tr>
								<tr>
									<td>
										<table align="center" width="100%">
											<tr>
												<td align="center">
													<font size="2" face="Calibri" color="#173556"><s:text
															name="login.index.label.clientright" /> </font>
												</td>
											</tr>
											<tr>
												<td align="center">
													<%
														String str = "out";
													%>

												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
				</div>
				<!-- end page -->
				<%
				
				if(menuItemList!=null){
					for (int i = 0; i < menuItemList.size(); i++) {
						com.selftopup.menu.MenuItem objMenuItem = (com.selftopup.menu.MenuItem) menuItemList
								.get(i);
						if (pageCode.equals("ALL")) {
							//if(objMenuItem.getModuleCode().equals(modCode) || objMenuItem.getLevel().equals("1")){
							if (objMenuItem.getLevel().equals("1")) {
								pageCode = objMenuItem.getPageCode();
								if (modCode.equals("ALL") || modCode.equals("CHGPWD")) {
									bolModuleCodeFound = true;
								}
							}
						}
						if (objMenuItem.getPageCode().equals(pageCode)) {
							bolPageCodeFound = true;
						}
					}
					if (bolPageCodeFound == false || bolModuleCodeFound == false
							|| bolURLConstainsScript == true) {

						session.invalidate();
				%>
				<script type="text/javascript">
window.location.href="<%=context + "/cp2plogin/cp2plogin_logedout.action"%>";
</script>
				<%
					}
					
					}
				%>
				<script type="text/javascript">
window.onbeforeunload = function(evt){    
   <%   (request.getSession()).setAttribute("backExpire","Y");%> 
    }  
    </script>
	
	</body>
</html>