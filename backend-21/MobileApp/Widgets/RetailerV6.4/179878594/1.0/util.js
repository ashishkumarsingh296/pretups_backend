// ================================Util Functions =================================
// data encryption function
var clientVersion_actual = widget.clientVersion.split("/");
clientVersion_actual = clientVersion_actual[0].substring(clientVersion_actual[0].lastIndexOf(".")+1,clientVersion_actual[0].length);
function getEncrypt(data)
{
	
	var encrypt_url="AES128Plugin:plugin.webaxn.comviva.com?";
	var respText = null;
	var xmlhttp = null;
	var encryptKey = widget.retrieveUserData("ekey");
	if("" == nullorUndefCheck(encryptKey))
	{
		if(DEMO_FLAG == 1 || DEMO_FLAG == "1")
		{
			encryptKey = DEMO_EKEY;
		}else
		{
			encryptKey = widget.retrieveWidgetUserData(179878594,"ekey");	
		}
	
		
	}
	var postdata = encryptKey+"|"+data;
	widget.logWrite(6, " encryptString =============== > " + postdata);
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
// mobilenumber validation of start with 0,91, +91
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
	//date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec+","+millisec;
	date = currentdate + "/" + month + "/" + year.toString().substring(2,4);
	return date;
}
function dateAndTime(indate) {
	var date = new Date(indate);
	var currentdate = date.getDate();
	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var hour = date.getHours();
	var min = date.getMinutes();
	var sec = date.getSeconds();
	var millisec = date.getMilliseconds();
	//date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec;
	//date = currentdate + "-" + month + "-" + year + " " + hour + ":" + min + ":" + sec+","+millisec;
	date = currentdate + "/" + month + "/" + year.toString().substring(2,4)+","+hour+":"+min+":"+sec;
	return date;
}
function cdrcommon(cdrStr)
{
	var appName = "Retailer PRETUPS";
	var separator = "|";
	var fileName = "PRETUPS.Logger";
	
	var cdrObj = new CDR(fileName,separator);
	cdrObj.addItem(appName);
	cdrObj.addItem(widget.getHeader("IMEI"));
	cdrObj.addItem(cdrStr);
	cdrObj.commit();

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
	widget.logWrite(6, " start screenWidth density ===>" + density);
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
		widthfactor = 0.12 ;
	}


	var backImgWidth = Math.round(Number(actwidth) * widthfactor);	
	return backImgWidth;
}
function closeApp()
{
	widget.savePermanent = SAVE_PERMINENT;
	var divEle = document.getElementById("exitApp");
	divEle.innerHTML = EXIT_TEXT;
	divEle.style.display = "block"; 
}

function nullorUndefCheck(str)
{
	var str_value ="";
	if(str !=null && str !="null" && str !=undefined && str !="undefined")
	{
		str_value = str;
	}

	return str_value;
}