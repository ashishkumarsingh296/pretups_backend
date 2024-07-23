var backImgWidth = backTopTitle();
var imagePath = getImagePath();
var clientVersion_actual = widget.clientVersion.split("/");
clientVersion_actual = clientVersion_actual[0].substring(clientVersion_actual[0].lastIndexOf(".")+1,clientVersion_actual[0].length);
var BackImage = backImage();
widget.logWrite(7,"subscriber imagePath::"+imagePath);
widget.logWrite(7," subscriber pretups clientVersion::"+clientVersion_actual);
widget.logWrite(7,"subscriber pretups clientVersion_config::"+clientVersion_config);

function getImagePath()
{
	
	var clientVersion 			= (widget.clientVersion).toUpperCase();
	var dpi = getDPI();
	var ANDROID_PATH_LOW			= "android/low/";
	var ANDROID_PATH_MEDIUM			= "android/medium/";
	var ANDROID_PATH_HIGH			= "android/high/";
	var ANDROID_PATH_XHIGH			= "android/xhigh/";
	var IPHONE_PATH 			    = "iphone/";
	var path = "wgt:214807648/1.0/";
	
	var imagePath="";
	if(clientVersion.indexOf("IOS") != -1)
    {
    	
    	imagePath = path+IPHONE_PATH ;
    }
    else if(clientVersion.indexOf("ANDROID") != -1)
    {
    	
    	
    	if(dpi <= 160)
    	{
    		imagePath = path+ANDROID_PATH_LOW ;

    	}else if(dpi <= 240)
    	{
    		imagePath = path+ANDROID_PATH_MEDIUM ;
    	}else if(dpi <= 320)
    	{
    		imagePath = path+ANDROID_PATH_HIGH ;
    	}else
    	{
    		imagePath = path+ANDROID_PATH_XHIGH ;
    	}
    }
	
	return imagePath;
	
}
function getDPI()
{
	var density = widget.getHeader("DENSITY"); 
	var dpi = "";
	
	widget.logWrite(6, " start screenWidth density ===>" + density);
	if(density != undefined && density != null && (density.indexOf(";") > -1))
	{
		dpi = density.split(";");
	//var actwidth = screenWidth *dpi[0];
		dpi  = dpi[1];
	}
	return dpi;
}
function responseStr(respText,strParam)
{

	var respParams;
	var paramValue="";
	if("" != nullorUndefCheck(respText))
	{
		respText = respText.split("&");
		for (var i = 0;i < respText.length; i++)
		{
			respParams = respText[i].split("=");
			if(respParams.length > 1 && respParams[0] == strParam)
			{
				paramValue = respParams[1];
			}
		}
	}
	/*if(paramValue.indexOf(":") > -1)
	{
		paramValue = paramValue.split(":");
		paramValue = paramValue[paramValue.length-1];
	}*/
	
	return paramValue.replace(/\n/g, ' ');

}

function backTopTitle()
{
	var screenwidth = widget.fetchScreenWidth() ;
	var actwidth = widget.fetchScreenWidth() ;
	var density = widget.getHeader("DENSITY"); 
	var resolution = 160 ;
	widget.logWrite(6, " start screenWidth density 1.5;240 ===>" + density);
	if(density != undefined && density != null && (density.indexOf(";") > -1))
	{
		var dpi = density.split(";");
		actwidth = screenwidth *dpi[0];
		resolution  = dpi[1] ;
	}

	var widthfactor = 0.08 ;
	if(resolution < 160)
	{
		widthfactor = 0.18 ;
	}
	else if(resolution < 240)
	{
		widthfactor = 0.10 ;
	}


	var backImgWidth = Math.round(Number(actwidth) * widthfactor);	
	return backImgWidth;
}
function validateMSISDN(msisdn)
{
	if(msisdn != "")
	{
		if(COUNTRY_CODE_CHECK)
		{
			if(msisdn .indexOf(COUNTRY_CODE) >-1)
			{
				msisdn = msisdn.replace(COUNTRY_CODE,"");
			}else if (msisdn.substring(0,2) == COUNTRYCODE_WITHOUTPLUS)
			{
				msisdn = msisdn.substring(2, msisdn.length);
			}else if (msisdn.substring(0,1) == MOBILENO_STARTWITHZERO)
			{
				msisdn = msisdn.substring(1, msisdn.length);
			}
		}
			
	}
	return msisdn;
}


// plug-in function for each request encryption
/*function getEncrypt(data)
{
	var encrypt_url = widget.widgetProperty("ENCRYPT_URL");
	//var encrypt_url=ENCRYPT_URL;
	widget.logWrite(7,"encryptUrl:"+encrypt_url);
	var respText = null;
	var xmlhttp = null;
	var encryptKey = widget.retrieveWidgetUserData(950181717,"ekey");
	var postdata = encryptKey+"|"+data;
	widget.logWrite(7,"encrypt postData:"+postdata);
	if (null == xmlhttp)
	{
		xmlhttp = new XMLHttpRequest();
	}
	if (xmlhttp)
	{
		xmlhttp.onreadystatechange = function ()
		{
			widget.logWrite(6, " getEncrypt xmlhttp.readyState =============== > " + xmlhttp.readyState);
			if (xmlhttp.readyState == 4)
			{
				if (200 == xmlhttp.status)
				{
					//widget.storePageData ("mstrack2plg", 1, xmlhttp.responseBytes) ; 
					respText = xmlhttp.responseText;
					widget.logWrite(6, " getEncrypt respText =============== > " + respText);
				}
			}
		};
	}
	xmlhttp.open("POST",encrypt_url, false);
	xmlhttp.send(postdata);
	return respText;
}
*/

// request time and response time handled
function changetimeformat() {
	var date = new Date();
	var currentdate = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var hour = date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	var millisec = date.getMilliseconds();
	//date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec;
	date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec+","+millisec;
	return date;
}


//custom cdr logs
function cdrcommon(cdrStr)
{
	var appName = "PRETUPS";
	var separator = "|";
	var fileName = "PRETUPS.Logger";
	
	var cdrObj = new CDR(fileName,separator);
	cdrObj.addItem(appName);
	cdrObj.addItem(widget.getHeader("IMEI"));
	cdrObj.addItem(cdrStr);
	cdrObj.commit();

}

// null and undefined check
function nullorUndefCheck(str)
{
	var str_value ="";
	if(str !=null && str !="null" && str !=undefined && str !="undefined")
	{
		str_value = str;
	}

	return str_value;
}
function addMenu(aMenu, aMenuName, aAction, alink, aIndex, aKeyPosition) // adds action to soft-keys dynamically.
{
    if (aKeyPosition == 0)
    {
        submenu = new MenuItem(aMenuName, aIndex);
        aMenu.append(submenu);
        submenu.onSelectEx = aAction;
        if (alink != "")
        {
            submenu.link = alink;
        }
    }
    else
    {
        aMenu.setRightSoftkeyLabelEx(aMenuName, aAction);
        if (alink != "")
        {
            aMenu.rightSoftkeyLink = alink;
        }
    }
}

function backImage()
{
	if(Number(clientVersion_actual) >= Number(clientVersion_config))

	{
              widget.logWrite(7,"if========>");

		return "<img valign='middle' width='8%'  align='left' resimg='back.png' src='"+imagePath+"back.png'/><img width='12%' style='margin-left:-15%' valign='middle' align='left' resimg='pretups_icon.png' src='"+imagePath+"pretups_icon.png'/>";
	}else
	{
              widget.logWrite(7,"Else==============>");

		return "<img  width='15%' align='left' resimg='icon_back.png' src='"+imagePath+"icon_back.9.png'/>";
	}
}

