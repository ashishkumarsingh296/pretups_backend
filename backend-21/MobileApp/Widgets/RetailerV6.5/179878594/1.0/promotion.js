var widget = window.widget;
var xmlhttp = null;
var SMS_BEARER_TYPE = 3;
var USSD_BEARER_TYPE = 2;
var WEB_TYPE = 4;
var scMobileNum = widget.fetchMSISDNNumber();
if (null == scMobileNum)
{
	scMobileNum = "244305184";
}
// Promotion servlet urls
var URL = "http://localhost:8899/promotion/OPHandler?";
var IMGURL = "http://localhost:8899/promotion/images/";
var GATEWAY_URL = widget.systemProperty("SELFCARE_GATEWAY");

function getMsisdn()
{
	var msisdn = widget.fetchMSISDNNumber();
	if (msisdn == null)
	{
		msisdn = "919876543210";
	}
	return msisdn;
}

function getSubscriberTypePromo()
{
	var stypeObj = sendRequestPromo("Account", "GetSubscriberProfile", null);
	var subProfile = stypeObj.result[0];
	var stype = 1;
	var strStype;
	if (subProfile != null)
	{
		if (subProfile.SubscriberType)
		{
			stype = subProfile.SubscriberType;
		}
	}
	if (stype == 1)
	{
		strStype = "Prepaid";
	}
	else
	{
		strStype = "Postpaid";
	}
	return strStype;
}

function sendRequestPromo(category, method, params, fileName)
{
	widget.logWrite(6, "sendRequest->fileName:" + fileName);
	var jsonObj = null;
	var postData;
	var iface = getBearerType();
	if (params == null)
	{
		params = "";
	}
	//    postData = ["<Request>", "<TransID>T12345</TransID>", "<AppsName>Webaxn</AppsName>", "<Interface>", iface, "</Interface>", "<MSISDN>", mobileNum, "</MSISDN>", "<Format>json</Format>", "<Category>", category, "</Category>", "<Method>", method, "</Method>", params, "</Request>"].join("");
	postData = ["TransID=", widget.fetchTransactionID(), "&AppsName=Webaxn&Interface=", iface, "&MSISDN=", scMobileNum, "&Format=json&Category=", category, "&Method=", method, params].join("");
	widget.logWrite(7, "postData-->" + postData);
	if (null == xmlhttp)
	{
		xmlhttp = new XMLHttpRequest();
	}
	if (xmlhttp)
	{
		xmlhttp.onreadystatechange = function ()
		{
			if (xmlhttp.readyState == 4)
			{
				var resStatus = xmlhttp.status;
				if (resStatus == 200)
				{
					widget.logWrite(7, "got jSON response-->" + xmlhttp.responseText);
					var respText = xmlhttp.responseText;
					if (respText != null)
					{
						widget.storePageData(fileName, 1, xmlhttp.responseBytes);
						jsonObj = eval('(' + xmlhttp.responseText + ')');
					}
					else
					{
						noResponseERR();
					}
				}
				else
				{
					noResponseERR();
				}
			}
		};
	}
	if (xmlhttp)
	{
		xmlhttp.open("GET", GATEWAY_URL + "?" + postData, false);
		xmlhttp.send(null);
	}
	widget.logWrite(7, "got jSON response-->" + jsonObj);
	return jsonObj;
}

function getBearerType()
{
	var bearer = widget.fetchBearerType();
	var rVal;
	switch (bearer)
	{
	case SMS_BEARER_TYPE:
		rVal = "SMS";
		break;
	case USSD_BEARER_TYPE:
		rVal = "USSD";
		break;
	default:
		rVal = "Client";
		break;
	}
	return rVal;
}

function getPromotionText(keyword)
{
	widget.logWrite(6, "getPromotionText sample start keyword ---> " + keyword);
	var pObj = promotionRequest(keyword);
	if (!pObj.result || pObj.StatusCode != 0)
	{
		return "";
	}
	var pDetails = pObj.result[0];
	var imgSrc = "";
	var displayText = "";
	var actionContent = "";
	var str = "";
	widget.logWrite(6, "getPromotionText sample pObj ---> " + pObj);
	if (pDetails != null)
	{
		if (pDetails.smallImage)
		{
			imgSrc = IMGURL + pDetails.smallImage;
		}
		if (pDetails.mediumImage)
		{
			imgSrc = IMGURL + pDetails.mediumImage;
		}
		if (pDetails.largeImage)
		{
			imgSrc = IMGURL + pDetails.largeImage;
		}
		if (pDetails.displayText)
		{
			displayText = pDetails.displayText;
		}
		if (pDetails.ActionContent)
		{
			actionContent = pDetails.ActionContent;
		}
		var cls = "";
		var pType = isFeaturePhonePromo();
		if (1 == pType)
		{
			cls = "scadbg";
		}
		else
		{
			cls = "scadbgcol";
		}
		if (actionContent == null || actionContent == "")
		{
			str = "<div id ='adv' class='c3lMenuGroup " + cls + "'><img id='adv' src='" + imgSrc + "'/><span class='scpromotext'>" + displayText + "</span></div>";
		}
		else
		{
			str = "<a id ='adv' class='c3lMenuGroup " + cls + "' href='" + actionContent + "'><img id='adv' src='" + imgSrc + "'/><span class='scpromotext'>" + displayText + "</span></a>";
		}
	}
	widget.logWrite(6, "getPromotionText END str ---> " + str);
	return str;
}

function promotionRequest(keyword)
{
	var jsonObj;
	var iface = getBearerType();
	var msisdn = 9921865018;
	//	var stype = getSubscriberTypePromo();
	//var pUrl = URL + "TransID="+widget.fetchTransactionID()+"&AppsName=selfcare&Interface="+iface+"&MSISDN="+msisdn+"&Format=json&Category=OP&Method=GetOP&SubscriberType="+stype+"&Type=Promotion&Keyword="+keyword;
	var pUrl = URL + "TransID=" + widget.fetchTransactionID() + "&AppsName=selfcare&Interface=" + iface + "&MSISDN=" + msisdn + "&Format=json&Category=OP&Method=GetOP&SubscriberType=all&Type=Promotion&Keyword=" + keyword;
	if (null == xmlhttp)
	{
		xmlhttp = new XMLHttpRequest();
	}
	if (xmlhttp)
	{
		xmlhttp.onreadystatechange = function ()
		{
			if (xmlhttp.readyState == 4)
			{
				var resStatus = xmlhttp.status;
				if (resStatus == 200)
				{
					widget.logWrite(7, "promotion jSON response-->" + xmlhttp.responseText);
					var respText = xmlhttp.responseText;
					if (respText != null)
					{
						jsonObj = eval('(' + xmlhttp.responseText + ')');
					}
					else
					{
						dispNoContent();
					}
				}
				else
				{
					dispNoContent();
				}
			}
		};
	}
	if (xmlhttp)
	{
		widget.logWrite(7, "promotion req url -->" + pUrl);
		xmlhttp.open("GET", pUrl, false);
		xmlhttp.send(null);
	}
	return jsonObj;
}

function isFeaturePhonePromo()
{
	var screenWidth = widget.fetchScreenWidth();
	var clientVersion = widget.clientVersion;
	var feaPhoneWidth = 240;
	widget.logWrite(6, "* Screen Width *" + screenWidth + " * Client Vesrion *" + clientVersion);
	screenWidth = Number(screenWidth);
	if (clientVersion.toUpperCase().indexOf("ANDROID") != -1 || clientVersion.toUpperCase().indexOf("IOS") != -1)
	{
		return 1;
	}
	else
	{
		if (screenWidth <= feaPhoneWidth)
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}
}

function dispNoContent()
{
	widget.logWrite(7, "No content from offers n promotion");
}

