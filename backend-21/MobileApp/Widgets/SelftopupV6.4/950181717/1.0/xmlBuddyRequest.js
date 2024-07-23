function sendaddBuddyReq(buddyName,buddyNum,amount,pin,type){
	var cdrStr="";
	var postdata = "";
	
	buddyName = nullorUndefCheck(buddyName);
	buddyNum = validateMSISDN(nullorUndefCheck(buddyNum));
	amount = nullorUndefCheck(amount);
	type = nullorUndefCheck(type);
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_ADDBUDDY == STR_PIN_CHECK)
	{
		pin = nullorUndefCheck(pin);
	}else
	{
		if(Number(clientVersion_actual) >= Number(clientVersion_config))
		{
			pin = nullorUndefCheck(regPIN);
		}else
		{
			pin = "";
		}
		
	}
	
	
	
		
	if( "" == nullorUndefCheck(mobile))
	{
		mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");	
	}
	
	
	/*TYPE=PADDREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&BUDDYNNAME=<Buddy Nick Name>&MSISDN1=<Buddy MSISDN1>&AMOUNT=<Default Amount>&LANGUAGE1=en&PIN=<Subscriber PIN>*/
	if(SENDENCRYPTREQ)
	{
		postdata = "TYPE=" + ADDBUDDY_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI="+imei+"&BUDDYNNAME="+buddyName+"&MSISDN1="+buddyNum+"&AMOUNT="+amount+"&PIN="+pin+"&LANGUAGE1="+LANGUAGE1);
	}else
	{
		postdata = "TYPE=" + ADDBUDDY_TYPE + "&MSISDN=" + mobile + "&Message=IMEI="+imei+"&BUDDYNNAME="+buddyName+"&MSISDN1="+buddyNum+"&AMOUNT="+amount+"&PIN="+pin+"&LANGUAGE1="+LANGUAGE1;
	}
	
	widget.logWrite(7,"sendaddBuddyReq postdata request::"+postdata);
	if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;			
	}
	if (xmlHttp)
	{
		xmlHttp.onreadystatechange = function()

		{
                      widget.logWrite(7,"xmlHttp.readyState::"+xmlHttp.readyState+ "xmlHttp.status::" +xmlHttp.status);
			if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
			{
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7," response for sendaddBuddyReq::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);
					if(txn_status == STR_SUCCESS_CODE)
					{
						if(type == BUDDY_MODIFY)
						{
							txn_message = BUDDYMODIFY_MSG;
							window.location = "wgt:264711061/1.0:buddyDetails('"+buddyName+"','"+buddyNum+"','"+amount+"','"+txn_message+"')";
						}	
						window.location = "wgt:264711061/1.0:buddies('','','"+txn_message+"')";
						
						
					}else
					{
						document.getElementById("toast").innerHTML = txn_message ;
						document.getElementById("toast").style.display = "block";
						
					}
				}else
				 {
					document.getElementById("toast").innerHTML = STR_SERVER_ERROR ;
					document.getElementById("toast").style.display = "block";
					
					
				 }
			}else
			{
				
				document.getElementById("toast").innerHTML = STR_SERVICE_ERROR ;
				document.getElementById("toast").style.display = "block";
				
			}  				

		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| addBuddy ";
		xmlHttp.send (postdata) ;
	}

}



function senddeleteBuddyReq(buddyNickName,pin,type) {
	
	var cdrStr="";
	var postdata = "";
	var txn_status = "";
	buddyNickName = nullorUndefCheck(buddyNickName);
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_DELBUDDY == STR_PIN_CHECK)
	{
		pin = nullorUndefCheck(pin);
	}else
	{
		if(Number(clientVersion_actual) >= Number(clientVersion_config))
		{
			pin = nullorUndefCheck(regPIN);
		}else
		{
			pin = "";
		}
		
	}
	if( "" == nullorUndefCheck(mobile))
	{
		mobile = widget.retrieveWidgetUserData(950181717,"regMSISDN");	
	}
	
	
	/*TYPE=PDELREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&NNAME=<Subscriber Nick Name>&LANGUAGE1=en */
	if(SENDENCRYPTREQ)
	{
		postdata = "TYPE=" + DELETEBUDDY_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&NNAME=" + buddyNickName + "&LANGUAGE1="+LANGUAGE1);
	}else
	{
		postdata = "TYPE=" + DELETEBUDDY_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN=" + pin + "&NNAME=" + buddyNickName + "&LANGUAGE1="+LANGUAGE1;
	}
	
	widget.logWrite(7, "format request senddeleteBuddyReq : " + postdata);
	if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;			
	}
	if (xmlHttp)
	{
		xmlHttp.onreadystatechange = function()
		{
			if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
			{
				var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7,"response for senddeleteBuddyReq: "+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					 txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+changetimeformat();
					//widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);
					if(txn_status == STR_SUCCESS_CODE)
					{	
						if(type == BUDDY_MODIFY)
						{
							return txn_status;
						}else
						{
							window.location = "wgt:264711061/1.0:buddies('','','"+txn_message+"')";
						}
						
					}else
					{
						document.getElementById("toast").innerHTML = txn_message ;
						document.getElementById("toast").style.display = "block";
						
						
					}
					
				}else
				 {
					
					document.getElementById("toast").innerHTML = STR_SERVER_ERROR ;
					document.getElementById("toast").style.display = "block";
					
				 }
			}else
			{
				
				document.getElementById("toast").innerHTML = STR_SERVICE_ERROR ;
				document.getElementById("toast").style.display = "block";
				
			}  				

		};
		xmlHttp.open ("POST", url , false) ;
		xmlHttp.setRequestHeader("Content-Type", "xml");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| deletebuddy ";
		xmlHttp.send(postdata) ;
	}
	return txn_status;
}

function sendeditBuddyReq(buddyName,buddyNum,buddyAmount,modifyName,pin)
{
	var result = senddeleteBuddyReq(buddyName,pin,BUDDY_MODIFY);
	if(result == STR_SUCCESS_CODE)
	{
		sendaddBuddyReq(modifyName,buddyNum,buddyAmount,pin,BUDDY_MODIFY);
	}else
	{
		document.getElementById("toast").innerHTML = STR_SERVER_ERROR ;
		document.getElementById("toast").style.display = "block";
	}
}

