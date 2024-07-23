function sendaddCardReq(cardName,cardNum,cvv,expiry,nickName,pin,msisdn,amount,saveCardCheck)
{
	var cdrStr="";
	var postdata = "";
	
	cardName = nullorUndefCheck(cardName);
	cardNum = nullorUndefCheck(cardNum);
	//var cardNum = nullorUndefCheck(cardno1)+nullorUndefCheck(cardno2)+nullorUndefCheck(cardno3)+nullorUndefCheck(cardno4);
	//widget.logWrite(7,"sendaddCardReq postdata request"+cardNum);
	cvv = nullorUndefCheck(cvv);
	expiry = nullorUndefCheck(expiry);
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_ADDCARD == STR_PIN_CHECK)
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
	
	if(expiry.indexOf("/") >-1)
	{
	expiry = expiry.split("/");
   	expiry = expiry[0] + "/" +  expiry[1].substring(2,4);
	}
   	nickName = nullorUndefCheck(nickName);
	//var pin = widget.retrieveUserData("regPin");
	
	/*TYPE=STPACREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&HOLDERNAME=<Holder Name>	&CARDNO=<Card number>&EDATE=<Card’s Expiry date>&NNAME=<Subscriber Nick Name>&LANGUAGE1=en*/
   	if(SENDENCRYPTREQ)
	{
   		postdata = "TYPE=" + ADDCARD_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&HOLDERNAME=" + cardName +"&CARDNO=" + cardNum + "&EDATE=" + expiry + "&NNAME=" + nickName);
	}else
	{
		postdata = "TYPE=" + ADDCARD_TYPE + "&MSISDN=" + mobile + "&IMEI=" + imei + "&PIN=" + pin + "&HOLDERNAME=" + cardName +"&CARDNO=" + cardNum + "&EDATE=" + expiry + "&NNAME=" + nickName + "&LANGUAGE1="+LANGUAGE1;
	}
	widget.logWrite(7,"sendaddCardReq postdata request"+postdata);
	if (null == xmlHttp)
	{
		xmlHttp = new XMLHttpRequest () ;			
	}
	if (xmlHttp)
	{
		xmlHttp.onreadystatechange = function()
		{
                     widget.logWrite(7,"ready state======>>>>>>>"+xmlHttp.readyState+ "Ready status======>"+xmlHttp.status+ "Response Text====="+xmlHttp.responseText);

			if (4 == xmlHttp.readyState &&  200 == xmlHttp.status)
			{
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7,"response for sendaddCardReq:"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var respTime = changetimeformat();
					var txn_status = responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+respTime;
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);

					if(txn_status == STR_SUCCESS_CODE)
					{
						//window.location = "wgt:251238406/1.0:cards()";
						if("true"== nullorUndefCheck(saveCardCheck))
						{
							//sendviewCardReq('',cvv,nickName);
							sendSubsCardRechReq(cvv,nickName,msisdn,amount,pin);
						}else
						{
							/*divElement= document.getElementById("cardAlert");
							divElement.title = "PreTUPS";
							divElement.innerHTML = txn_message+"<setvar name='cvv' value=''/><setvar name='nickName' value=''/><setvar name='cardNum' value=''/><setvar name='cardName' value=''/><setvar name='cardType' value=''/><setvar name='expiry' value=''/><setvar name='newName' value=''/>";

							divElement.style.display = "block";*/

	
							window.location="wgt:251238406/1.0:cards('"+txn_message+"')";
								//sendviewCardReq();
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
		xmlHttp.setRequestHeader("Content-Type", "plain");
		xmlHttp.setRequestHeader("Connection", "close");
		cdrStr += changetimeformat()+"| addCard ";
		xmlHttp.send (postdata) ;
	}



}

/*this function for subscriber self/another request of PreTUPS*/
function sendeditCardReq(cardNum,cardName,cardType,expiry,nickName,newName,pin){
	
	//nickName = "comcard1";
	//newName ='comcard123';
	var cdrStr="";
	var postdata = "";
	nickName 	= nullorUndefCheck(nickName);
	newName 	= nullorUndefCheck(newName);
	cardNum 	= nullorUndefCheck(cardNum);
	cardName 	= nullorUndefCheck(cardName);
	expiry 	= nullorUndefCheck(expiry);
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_EDITCARD == STR_PIN_CHECK)
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
	//var pin = widget.retrieveUserData("regPin");
	
	/*TYPE=STPMCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&NNAME=<Subscriber Nick Name>&NEWNNAME=<Subscriber New Nick Name>&LANGUAGE1=en	*/
	if(SENDENCRYPTREQ)
	{
	postdata = "TYPE=" + EDITCARD_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN=" + pin + "&NNAME=" + nickName + "&NEWNNAME="+ newName);
	}else
	{
	postdata = "TYPE=" + EDITCARD_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN=" + pin + "&NNAME=" + nickName + "&NEWNNAME="+ newName;
	}
	
	widget.logWrite(7,"sendeditCardReq postdata request:"+postdata);
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
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7,"response for sendeditCardReq::"+xmlText);
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var respTime = changetimeformat();
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+respTime;
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);

					if(txn_status == STR_SUCCESS_CODE)
					{
						
						window.location="wgt:251238406/1.0:cardDetails('"+cardNum+"','"+cardName+"','"+cardType+"','"+expiry+"','"+newName+"','"+txn_message+"')";
						/*divElement= document.getElementById("cardAlert");
						divElement.title = "PreTUPS";
						divElement.innerHTML = txn_message+"<setvar name='cvv' value=''/><setvar name='nickName' value=''/><setvar name='cardNum' value='"+cardNum+"'/><setvar name='cardName' value='"+cardName+"'/><setvar name='cardType' value='"+cardType+"'/><setvar name='expiry' value='"+expiry+"'/><setvar name='newName' value='"+newName+"'/>";
						divElement.style.display = "block";
*/
						//window.location = "wgt:251238406/1.0:cards('"+cardNum+"','"+cardName+"','"+cardType+"','"+expiry+"','"+newName+"')";
						//sendviewCardReq('','','',cardNum,cardName,cardType,expiry,newName);
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
		cdrStr += changetimeformat()+"| editCard ";
		xmlHttp.send (postdata) ;
	}
}



function senddeleteCardReq(nickName,pin) {
	
	var cdrStr="";
	var postdata = "";
	nickName 	= nullorUndefCheck(nickName);
	pin		 	= nullorUndefCheck(pin);
	
	if(PIN_CHECK_ALL == STR_PIN_CHECK && PIN_CHECK_NONFIN == STR_PIN_CHECK && PIN_CHECK_DELCARD == STR_PIN_CHECK)
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
	
	
	
	/*TYPE=STPDCREQ&IMEI=<IMEI no>&MSISDN=<Initiating Subscriber MSISDN>&PIN=<Subscriber PIN>&NNAME=<Subscriber Nick Name>&LANGUAGE1=en */
	if(SENDENCRYPTREQ)
	{
		postdata = "TYPE=" + DELETECARD_TYPE + "&MSISDN=" + mobile + "&Message="+getEncrypt("IMEI=" + imei + "&PIN="+ pin + "&NNAME=" + nickName);
	}else
	{
		postdata = "TYPE=" + DELETECARD_TYPE + "&MSISDN=" + mobile + "&Message=IMEI=" + imei + "&PIN="+ pin + "&NNAME=" + nickName;
	}
	widget.logWrite(7, "senddeleteCardReq postdata request :: " + postdata);
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
				//var xmlDoc = xmlHttp.responseXML ;
				var xmlText = xmlHttp.responseText ;
				widget.logWrite(7,"response for senddeleteCardReq: "+xmlText );
				if (xmlText != null && !xmlText.indexOf("null") > -1 )
				{
					var respTime = changetimeformat();
					var txn_status 	= responseStr(xmlText, STR_TXNSTATUS) ;
					var txn_message	 = responseStr(xmlText, STR_TXNMESSAGE) ;
					cdrStr +="|"+txn_status+"|"+txn_message+"|"+respTime;
					widget.writeCDR (1, cdrStr) ;
					widget.logWrite(7,"cdrstr logs.."+cdrStr);
					cdrcommon(cdrStr);

					if(txn_status == STR_SUCCESS_CODE)
					{
						
						window.location="wgt:251238406/1.0:cards('"+txn_message+"')";
						/*divElement= document.getElementById("cardAlert");
						divElement.title = "PreTUPS";
						//divElement.innerHTML = txn_message+"<setvar name='cvv' value=''/><setvar name='nickName' value=''/><setvar name='cardNum' value=''/><setvar name='cardName' value=''/><setvar name='cardType' value=''/><setvar name='expiry' value=''/><setvar name='newName' value=''/>";
						divElement.innerHTML = txn_message;
						divElement.style.display = "block";*/
						//sendviewCardReq();
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
		cdrStr += changetimeformat()+"| deleteCard";
		xmlHttp.send (postdata) ;
	}

}
