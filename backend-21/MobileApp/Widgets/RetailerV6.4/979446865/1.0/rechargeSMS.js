function processRechargeSMS(aMsg){
	widget.logWrite(7, "original msg " + aMsg);
	var senderNo = widget.fetchMSISDNNumber();
	widget.logWrite(7, "senders number " + senderNo);
	var aMsgArr = aMsg.split(" ");
	widget.logWrite(7, "SMS Message array: " + aMsgArr);
	var str = "";
	var success = 0; // 1 is success
	if(aMsgArr != null && aMsgArr.length == 2){
		widget.logWrite(7, "SMS Message split length: " + aMsgArr.length);
		var value = aMsgArr[1];
		widget.logWrite(7, "Value split: " + value);
		var smsArr = value.split(":");
		widget.logWrite(7, "SMS Message split length: " + smsArr.length);
		if(smsArr != null && smsArr.length == 3){
			//var mobnum = smsArr[0];
			//var amount = smsArr[1];
			//var pin = smsArr[2];
			sendRechargeReq(smsArr[0],smsArr[1],smsArr[2]);
			success = 1;
		}
	}
	if(success != 1){
		var sendStatus =widget.sendSMS(senderNo,"All inputs required for recharge not received", null);
		if(sendStatus == 1){
			widget.logWrite(6, "SMS sent successfully on ERROR1");
		}else{
			widget.logWrite(6, "SMS not sent on ERROR1");
		}
	}
}
