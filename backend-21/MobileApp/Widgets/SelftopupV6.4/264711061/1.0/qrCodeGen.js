var xmlhttp;
function generateQrCode(amount,msisdn)
{
	amount = nullorUndefCheck(amount);
	msisdn = nullorUndefCheck(msisdn);
	if("" == msisdn)
	{
		msisdn=widget.retrieveWidgetUserData(950181717,'regMSISDN');
	}
	var str = "";
	//var server_url_qr = widget.widgetProperty("QRCODEGEN_URL");
	//var SID = widget.fetchSubscriberID();
	var ucode = Math.floor((Math.random() * 100000) + 1);
	var IMEI = widget.getHeader("imei");
		
	var postData = "QRGEN|amount=" + amount + "&imei=" + IMEI + "&msisdn=" + msisdn + "&uniqueCode=" + ucode;
	widget.logWrite(7, "generateQrCode postData-->" + postData);
//	widget.logWrite(7, "generateQrCode server_url_qr-->" + server_url_qr);

	if (null == xmlhttp)
	{
		xmlhttp = new XMLHttpRequest();
	}
	if (xmlhttp)
	{
		xmlhttp.onreadystatechange = function ()
		{
			widget.logWrite(6, " generateQrCode xmlhttp.readyState =============== > " + xmlhttp.readyState);
			if (xmlhttp.readyState == 4)
			{
				if (200 == xmlhttp.status)
				{
					var respBytes = xmlhttp.responseBytes;
					widget.logWrite(7, "generateQrCode respBytes-->" + respBytes);
					if  (respBytes != null )
					{
						widget.storePageData(ucode + ".png", 1, respBytes);
						/*
						var Q_CREATE_TABLE = widget.widgetProperty("CREATE_M_SUBSCRIBER_TABLE");
						var Q_INS_DETAILS = widget.widgetProperty("INSERT_M_SUBSCRIBER_TABLE");
						widget.createTable(Q_CREATE_TABLE);
						widget.logWrite(7, "generateQrCode Q_CREATE_TABLE-->" + Q_CREATE_TABLE);
						widget.logWrite(7, "generateQrCode Q_INS_DETAILS-->" + Q_INS_DETAILS);

						// =================INSERT===================
						var sqlObj = new SQL();
						sqlObj.query = Q_INS_DETAILS;
						var sqlParam = new SQLParam();
						sqlParam.strParam = amount;
						sqlObj.addParam(sqlParam);
						sqlParam = new SQLParam();
						sqlParam.blobParam = respBytes;
						sqlObj.addParam(sqlParam);
						sqlParam = new SQLParam();
						sqlParam.strParam = SID;
						sqlObj.addParam(sqlParam);
						widget.executeUpdateQuery(sqlObj);*/
						//widget.addEditTableData(Q_INS_DETAILS);
						showQRcode(ucode);
					}
					else
					{
						str = STR_SERVICE_ERROR;
						var element = document.getElementById("toast");
						element.style.display = "block";
						element.innerHTML = str;
					}
				}
				else
				{
					str = STR_SERVER_ERROR;
					var element = document.getElementById("toast");
					element.style.display = "block";
					element.innerHTML = str;
				}
			}
		};
	}
	xmlhttp.open("POST", ENCRYPT_URL, false);
	xmlhttp.send(postData);
}



function showQRcode(ucode)
{
	var height = widget.fetchScreenHeight() ;
	if("" != nullorUndefCheck(height))
	{
		height = Number(height)/2-135;
	}
	var QrImagePath = widget.widgetProperty("IMAGE_PATH") + widget.retrievePagePath(ucode + ".png", 1);
	widget.logWrite(7, "showQRcode QrImagePath-->" + QrImagePath);
	var str= "<div   class='c3lTitle' style='margin-bottom:"+Number(height)+"'><a class='c3lMenuGroup' href='close://action=keypad://clearall?target=$amount$;$msisdn$'><img width='15%' align='left' resimg='icon_back.9.png' src='"+imagePath+"icon_back.9.png'/></a><span  class='topText' id='paytitle' valign='middle'>" + STR_PRETUPS_TITLE + "</span><hr/></div>";
	//str +="<div><span>Generated qrCod</span></div>";
	str += "<div  align='center' width='95%' class='c3lMenuGroup contentBg'><img align='center' src='" + QrImagePath + "' /></div>";
	//str = [str, "<div class='c3lNavigation bgWhite' style='margin-bottom:5px;'><img width='55%' align='left' class=''  src='" + imagePath + "icici_logo.png' resimg='icici_logo.png'/><img  width='40%' align='right' class=''  src='" + imagePath + "fdc.png' resimg='fdc.png'/></div>"].join("");
	var element = document.getElementById("qrcode");
	element.style.display = "block";
	element.innerHTML = str;
}