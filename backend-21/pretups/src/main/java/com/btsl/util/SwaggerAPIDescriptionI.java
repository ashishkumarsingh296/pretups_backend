package com.btsl.util;

import com.btsl.pretups.common.PretupsI;

public interface SwaggerAPIDescriptionI {

	public static final String PASSWORD_MANAGEMENT_DESC = "Operations List" + "\n" + "1 - Unblock password" + "\n"
			+ ("2 - Unblock and send password") + ("\n") + ("3 - Send password") + ("\n") + ("4 - Reset password") + ("\n")
			+ ("\n")
			+ (" loggedInIdentifierType : ") + (" ID of logged in user | ") + ("Mandatory | ") + (" Possible values: ")
			+ (" NA") + ("\n") + ("\n") + (" loggedInIdentifierValue : ") + (" Password of logged in user | ")
			+ ("Mandatory | ") + (" Possible values: ") + (" NA") + ("\n") + ("\n") + (" loginID : ")
			+ (" ID of child user | ") + ("Optional if msisdn is provided | ") + (" Possible values: ") + (" NA")
			+ ("\n") + ("\n") + (" msisdn : ") + (" msisdn of child user | ") + ("Optional if loginID is provided | ")
			+ (" Possible values: ") + (" NA") + ("\n") + ("\n") + (" remarks : ") + (" Reamrks for operation | ")
			+ ("Mandatory | ") + ("Possible values: ") + ("Any string") + ("\n") + ("\n") + (" operationID : ")
			+ (" Operation ID of operation to be performed |") + ("Mandatory | ")
			+ (" Possible values:") + (" 1,2,3,4") + ("\n");
	
	public static final String PIN_MANAGEMENT_DESC = "API Info:\r\n" + 
			"1) If value of reset pin is Y, then PIN will be reset and mail will be sent to user, else mail with current PIN will be sent to user\r\n" + 
			"2) No mail will be sent if the value of system preference EN DE CRYPTION for PIN PASS is set to SHA\r\n" + 
			"";
	
	public static final String ADD_CARD_GROUP = 
			"{\r\n"+"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
					"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
					"\"data\":{\r\n"+"\"cardGroupList\":[\r\n"+"{\r\n"+"\"voucherType\":\"physical\",\r\n"+"\"voucherTypeDesc\":\"Physical\",\r\n"+"\"voucherSegment\":\"LC\",\r\n"+"\"voucherSegmentDesc\":\"Local\",\r\n"+"\"voucherProductId\":\"2\",\r\n"+"\"cardGroupType\":\"VMS\",\r\n"+"\"voucherDenomination\":\"1.0\",\r\n"+"\"productName\":\"P1\",\r\n"+"\"maxReceiverAccessFeeAsString\":\"1\",\r\n"+"\"validityPeriodAsString\":\"30\",\r\n"+"\"minReceiverAccessFeeAsString\":\"1\",\r\n"+"\"inPromoAsString\":\"0\",\r\n"+"\"receiverAccessFeeRateAsString\":\"1.0\",\r\n"+"\"receiverTax1RateAsString\":\"1.0\",\r\n"+"\"receiverTax2RateAsString\":\"1.0\",\r\n"+"\"bonusTalkTimeValue\":0,\r\n"+"\"editDetail\":\"N\",\r\n"+"\"bonusTalkTimeConvFactor\":1.0,\r\n"+"\"bonusTalkTimeRate\":0.0,\r\n"+"\"bonusTalkTimeType\":\"AMT\",\r\n"+"\"bonusTalkTimeValidity\":\"0\",\r\n"+"\"serviceTypeSelector\":\"VCN_1\",\r\n"+"\"bonus1\":0.0,\r\n"+"\"bonus2validity\":0,\r\n"+"\"bonusTalktimevalidity\":0,\r\n"+"\"cardGroupCode\":\"cardvcag\",\r\n"+"\"validityPeriodType\":\"VLHI\",\r\n"+"\"validityPeriod\":30,\r\n"+"\"gracePeriod\":11,\r\n"+"\"receiverTax1Name\":\"Tax 1\",\r\n"+"\"receiverTax1Type\":\"PCT\",\r\n"+"\"receiverTax1Rate\":1.0,\r\n"+"\"receiverTax2Name\":\"Tax 2\",\r\n"+"\"receiverTax2Type\":\"PCT\",\r\n"+"\"receiverTax2Rate\":1.0,\r\n"+"\"receiverAccessFeeType\":\"PCT\",\r\n"+"\"receiverAccessFeeRate\":1.0,\r\n"+"\"minReceiverAccessFee\":100,\r\n"+"\"maxReceiverAccessFee\":100,\r\n"+"\"receiverConvFactor\":\"1\",\r\n"+"\"online\":\"Y\",\r\n"+"\"both\":\"Y\",\r\n"+"\"cosRequired\":\"Y\",\r\n"+"\"inPromo\":0.0,\r\n"+"\"cardName\":\"cardvcag\",\r\n"+"\"bonusAccList\":[\r\n"+"{\r\n"+"\"bundleID\":\"1\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"CVG\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"},\r\n"+"{\r\n"+"\"bundleID\":\"2\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"C\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"},\r\n"+"{\r\n"+"\"bundleID\":\"3\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"VG\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"}\r\n"+"],\r\n"+"\"bonus2\":0.0,\r\n"+"\"bonus1validity\":0,\r\n"+"\"rowIndex\":1,\r\n"+"\"status\":\"Y\",\r\n"+"\"transferValue\":0\r\n"+"}\r\n"+"],\r\n"+"\"cardGroupDetails\":{\r\n"+"\"applicableFromDate\":\"15/06/20\",\r\n"+"\"applicableFromHour\":\"20:00\",\r\n"+"\"defaultCardGroup\":\"N\",\r\n"+"\"lastModifiedOn\":0,\r\n"+"\"serviceTypeDesc\":\"Voucher Consumption\",\r\n"+"\"setTypeName\":\"Normal\",\r\n"+"\"subServiceTypeDescription\":\"Choice RC 1\",\r\n"+"\"networkCode\":\"NG\",\r\n"+"\"createdBy\":\"NGLA0000003720\",\r\n"+"\"modifiedBy\":\"NGLA0000003720\",\r\n"+"\"moduleCode\":\"P2P\",\r\n"+"\"status\":\"Y\",\r\n"+"\"cardGroupSetName\":\"cardvcagpr\"\r\n"+"}\r\n"+"}\r\n"+"}\r\n";
	public static final String ADD_P2P_CARD_GROUP = 
			"{\r\n"+"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
					"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
	"\"data\":{\r\n"+"\"cardGroupList\":[\r\n"+"{\r\n"+"\"cardGroupType\":\"P2P\",\r\n"+"\"maxReceiverAccessFeeAsString\":\"10\",\r\n"+"\"startRangeAsString\":\"1\",\r\n"+"\"maxSenderAccessFeeAsString\":\"10\",\r\n"+"\"minSenderAccessFeeAsString\":\"1\",\r\n"+"\"validityPeriodAsString\":\"30\",\r\n"+"\"endRangeAsString\":\"100\",\r\n"+"\"minReceiverAccessFeeAsString\":\"1\",\r\n"+"\"senderTax2RateAsString\":\"1.0\",\r\n"+"\"multipleOfAsString\":\"1\",\r\n"+"\"inPromoAsString\":\"0\",\r\n"+"\"senderAccessFeeRateAsString\":\"1.0\",\r\n"+"\"senderTax1RateAsString\":\"1.0\",\r\n"+"\"receiverAccessFeeRateAsString\":\"1.0\",\r\n"+"\"receiverTax1RateAsString\":\"1.0\",\r\n"+"\"receiverTax2RateAsString\":\"1.0\",\r\n"+"\"bonusTalkTimeValue\":0,\r\n"+"\"editDetail\":\"N\",\r\n"+"\"bonusTalkTimeConvFactor\":1.0,\r\n"+"\"bonusTalkTimeRate\":0.0,\r\n"+"\"bonusTalkTimeType\":\"AMT\",\r\n"+"\"bonusTalkTimeValidity\":\"0\",\r\n"+"\"serviceTypeSelector\":\"CDATA_2\",\r\n"+"\"bonus1\":0.0,\r\n"+"\"bonus2validity\":0,\r\n"+"\"bonusTalktimevalidity\":0,\r\n"+"\"startRange\":100,\r\n"+"\"endRange\":10000,\r\n"+"\"oldApplicableFrom\":0,\r\n"+"\"cardGroupCode\":\"cardp2pag\",\r\n"+"\"validityPeriodType\":\"VLHI\",\r\n"+"\"validityPeriod\":30,\r\n"+"\"gracePeriod\":30,\r\n"+"\"senderTax1Name\":\"Tax1\",\r\n"+"\"senderTax1Type\":\"PCT\",\r\n"+"\"senderTax1Rate\":1.0,\r\n"+"\"senderTax2Name\":\"Tax2\",\r\n"+"\"senderTax2Type\":\"PCT\",\r\n"+"\"senderTax2Rate\":1.0,\r\n"+"\"receiverTax1Name\":\"Tax1\",\r\n"+"\"receiverTax1Type\":\"PCT\",\r\n"+"\"receiverTax1Rate\":1.0,\r\n"+"\"receiverTax2Name\":\"Tax2\",\r\n"+"\"receiverTax2Type\":\"PCT\",\r\n"+"\"receiverTax2Rate\":1.0,\r\n"+"\"senderAccessFeeType\":\"PCT\",\r\n"+"\"senderAccessFeeRate\":1.0,\r\n"+"\"receiverAccessFeeType\":\"PCT\",\r\n"+"\"receiverAccessFeeRate\":1.0,\r\n"+"\"minSenderAccessFee\":100,\r\n"+"\"maxSenderAccessFee\":1000,\r\n"+"\"minReceiverAccessFee\":100,\r\n"+"\"maxReceiverAccessFee\":1000,\r\n"+"\"multipleOf\":100,\r\n"+"\"bonusValidityValue\":30,\r\n"+"\"online\":\"Y\",\r\n"+"\"both\":\"Y\",\r\n"+"\"senderConvFactor\":\"1\",\r\n"+"\"receiverConvFactor\":\"1\",\r\n"+"\"cosRequired\":\"Y\",\r\n"+"\"inPromo\":0.0,\r\n"+"\"cardName\":\"cardp2pag\",\r\n"+"\"bonusAccList\":[\r\n"+"{\r\n"+"\"bundleID\":\"1\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"CVG\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"},\r\n"+"{\r\n"+"\"bundleID\":\"2\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"C\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"},\r\n"+"{\r\n"+"\"bundleID\":\"3\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1\",\r\n"+"\"bonusName\":\"VG\",\r\n"+"\"restrictedOnIN\":\"Y\"\r\n"+"}\r\n"+"],\r\n"+"\"bonus2\":0.0,\r\n"+"\"bonus1validity\":0,\r\n"+"\"rowIndex\":1,\r\n"+"\"status\":\"Y\",\r\n"+"\"transferValue\":0\r\n"+"}\r\n"+"],\r\n"+"\"cardGroupDetails\":{\r\n"+"\"applicableFromDate\":\"15/06/20\",\r\n"+"\"applicableFromHour\":\"20:00\",\r\n"+"\"defaultCardGroup\":\"N\",\r\n"+"\"lastModifiedOn\":0,\r\n"+"\"serviceTypeDesc\":\"CP2P Data Transfer\",\r\n"+"\"setTypeName\":\"Normal\",\r\n"+"\"subServiceTypeDescription\":\"CDATA\",\r\n"+"\"networkCode\":\"NG\",\r\n"+"\"createdBy\":\"NGLA0000003720\",\r\n"+"\"modifiedBy\":\"NGLA0000003720\",\r\n"+"\"moduleCode\":\"P2P\",\r\n"+"\"status\":\"Y\",\r\n"+"\"cardGroupSetName\":\"cardp2pag13421\"\r\n"+"}\r\n"+"}\r\n"+"}\r\n";
	public static final String MODIFY_CARD_GROUP = 
			"{\r\n"+"\""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\":\"btnadm\","+"\n"+
					"\""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\":\"1357\","+"\n"+
					"\"data\":{\r\n"+"\"cardGroupList\":[\r\n"+"{\r\n"+"\"voucherType\":\"physical\",\r\n"+"\"voucherTypeDesc\":\"Physical\",\r\n"+"\"voucherSegment\":\"LC\",\r\n"+"\"voucherSegmentDesc\":\"Local\",\r\n"+"\"voucherProductId\":\"2\",\r\n"+"\"cardGroupType\":\"VMS\",\r\n"+"\"voucherDenomination\":\"1.0\",\r\n"+"\"senderConvFactor\":\"1\",\t\r\n"+"\"receiverConvFactor\":\"1\",\r\n"+"\"productName\":\"P1\",\r\n"+"\"receiverTax3RateAsString\":\"0.0\",\r\n"+"\"receiverTax4RateAsString\":\"0.0\",\r\n"+"\"maxReceiverAccessFeeAsString\":\"1\",\r\n"+"\"validityPeriodAsString\":\"30\",\r\n"+"\"minReceiverAccessFeeAsString\":\"1\",\r\n"+"\"applicableFromAsString\":\"15/06/2020:00:00\",\r\n"+"\"multipleOfAsString\":\"0\",\r\n"+"\"inPromoAsString\":\"0\",\r\n"+"\"receiverAccessFeeRateAsString\":\"1.0\",\r\n"+"\"receiverTax1RateAsString\":\"1.0\",\r\n"+"\"receiverTax2RateAsString\":\"1.0\",\r\n"+"\"bonusTalkTimeValue\":0,\r\n"+"\"bonusTalkTimeConvFactor\":1.0,\r\n"+"\"bonusTalkTimeBundleType\":\"AMT\",\r\n"+"\"bonusTalkTimeRate\":0.0,\r\n"+"\"bonusTalkTimeType\":\"AMT\",\r\n"+"\"bonusTalkTimeValidity\":\"0\",\r\n"+"\"serviceTypeSelector\":\"VCN_1\",\r\n"+"\"applicableFrom\":1592231400000,\r\n"+"\"cardGroupSubServiceId\":\"1\",\r\n"+"\"cardGroupSubServiceIdDesc\":\"ChoiceRC1\",\r\n"+"\"lastVersion\":null,\r\n"+"\"setType\":\"N\",\r\n"+"\"oldApplicableFrom\":0,\r\n"+"\"cardGroupCode\":\"cardvcag\",\r\n"+"\"validityPeriodType\":\"VLHI\",\r\n"+"\"validityPeriod\":30,\r\n"+"\"gracePeriod\":11,\r\n"+"\"receiverTax1Name\":\"Tax1\",\r\n"+"\"receiverTax1Type\":\"PCT\",\r\n"+"\"receiverTax1Rate\":1.0,\r\n"+"\"receiverTax2Name\":\"Tax2\",\r\n"+"\"receiverTax2Type\":\"PCT\",\r\n"+"\"receiverTax2Rate\":1.0,\r\n"+"\"receiverAccessFeeType\":\"PCT\",\r\n"+"\"receiverAccessFeeRate\":1.0,\r\n"+"\"minReceiverAccessFee\":100,\r\n"+"\"maxReceiverAccessFee\":100,\r\n"+"\"multipleOf\":0,\r\n"+"\"serviceTypeId\":\"VCN\",\r\n"+"\"serviceTypeDesc\":\"VoucherConsumption\",\r\n"+"\"setTypeName\":\"Normal\",\r\n"+"\"online\":\"Y\",\r\n"+"\"both\":\"Y\",\r\n"+"\"cosRequired\":\"Y\",\r\n"+"\"inPromo\":0.0,\r\n"+"\"cardName\":\"cardvcag\",\r\n"+"\"bonusAccList\":[\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4428\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14460\",\r\n"+"\"bundleID\":\"1\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"CVG\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"CVG\"\r\n"+"},\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4428\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14460\",\r\n"+"\"bundleID\":\"2\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"C\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"C\"\r\n"+"},\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4428\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14460\",\r\n"+"\"bundleID\":\"3\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"VG\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"VG\"\r\n"+"}\r\n"+"],\r\n"+"\"bonus2\":0.0,\r\n"+"\"bonus1validity\":0,\r\n"+"\"rowIndex\":1,\r\n"+"\"status\":\"Y\",\r\n"+"\"version\":\"1\",\r\n"+"\"transferValue\":0,\r\n"+"\"cardGroupSetID\":\"4428\",\r\n"+"\"cardGroupID\":\"14460\",\r\n"+"\"cardGroupSetName\":\"cardvcagpr12212\"\r\n"+"}\r\n"+"],\r\n"+"\"cardGroupDetails\":{\r\n"+"\"version\":\"2\",\r\n"+"\"applicableFromDate\":\"17/07/20\",\r\n"+"\"applicableFromHour\":\"20:00\",\r\n"+"\"lastModifiedOn\":0,\r\n"+"\"serviceTypeDesc\":\"Voucher Consumption\",\r\n"+"\"subServiceTypeDescription\":\"Choice RC 1\",\r\n"+"\"networkCode\":\"NG\",\r\n"+"\"modifiedBy\":\"NGLA0000003720\",\r\n"+"\"moduleCode\":\"P2P\",\r\n"+"\"cardGroupSetID\":\"4428\",\r\n"+"\"cardGroupSetName\":\"cardvcagpr12212\"\r\n"+"}\r\n"+"}\r\n"+"}\r\n";
	public static final String MODIFY_P2P_CARD_GROUP = 
			"{\r\n"+"\""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\":\"btnadm\","+"\n"+
					"\""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\":\"1357\","+"\n"+
					"\"data\":{\r\n"+"\"cardGroupList\":[\r\n"+"{\r\n"+"\"cardGroupType\":\"P2P\",\r\n"+"\"maxReceiverAccessFeeAsString\":\"10\",\r\n"+"\"startRangeAsString\":\"1\",\r\n"+"\"validityPeriodAsString\":\"30\",\r\n"+"\"endRangeAsString\":\"100\",\r\n"+"\"minReceiverAccessFeeAsString\":\"1\",\r\n"+"\"senderTax2RateAsString\":\"1.0\",\r\n"+"\"applicableFromAsString\":\"\",\r\n"+"\"multipleOfAsString\":\"1\",\r\n"+"\"inPromoAsString\":\"0\",\r\n"+"\"senderAccessFeeRateAsString\":\"1.0\",\r\n"+"\"senderTax1RateAsString\":\"1.0\",\r\n"+"\"receiverAccessFeeRateAsString\":\"1.0\",\r\n"+"\"receiverTax1RateAsString\":\"1.0\",\r\n"+"\"receiverTax2RateAsString\":\"1.0\",\r\n"+"\"bonusTalkTimeValue\":0,\r\n"+"\"editDetail\":\"N\",\r\n"+"\"bonusTalkTimeConvFactor\":1.0,\r\n"+"\"bonusTalkTimeBundleType\":null,\r\n"+"\"bonusTalkTimeRate\":0.0,\r\n"+"\"bonusTalkTimeType\":\"AMT\",\r\n"+"\"bonusTalkTimeValidity\":\"0\",\r\n"+"\"serviceTypeSelector\":\"CDATA_2\",\r\n"+"\"bonus1\":0.0,\r\n"+"\"bonus2validity\":0,\r\n"+"\"bonusTalktimevalidity\":0,\r\n"+"\"startRange\":100,\r\n"+"\"endRange\":10000,\r\n"+"\"oldApplicableFrom\":0,\r\n"+"\"cardGroupCode\":\"cardp2pag\",\r\n"+"\"validityPeriodType\":\"VLHI\",\r\n"+"\"validityPeriod\":30,\r\n"+"\"gracePeriod\":30,\r\n"+"\"senderTax1Name\":\"Tax1\",\r\n"+"\"senderTax1Type\":\"PCT\",\r\n"+"\"senderTax1Rate\":1.0,\r\n"+"\"senderTax2Name\":\"Tax2\",\r\n"+"\"senderTax2Type\":\"PCT\",\r\n"+"\"senderTax2Rate\":1.0,\r\n"+"\"receiverTax1Name\":\"Tax1\",\r\n"+"\"receiverTax1Type\":\"PCT\",\r\n"+"\"receiverTax1Rate\":1.0,\r\n"+"\"receiverTax2Name\":\"Tax2\",\r\n"+"\"receiverTax2Type\":\"PCT\",\r\n"+"\"receiverTax2Rate\":1.0,\r\n"+"\"senderAccessFeeType\":\"PCT\",\r\n"+"\"senderAccessFeeRate\":1.0,\r\n"+"\"receiverAccessFeeType\":\"PCT\",\r\n"+"\"receiverAccessFeeRate\":1.0,\r\n"+"\"minSenderAccessFee\":100,\r\n"+"\"maxSenderAccessFee\":1000,\r\n"+"\"minReceiverAccessFee\":100,\r\n"+"\"maxReceiverAccessFee\":1000,\r\n"+"\"multipleOf\":100,\r\n"+"\"bonusValidityValue\":30,\r\n"+"\"online\":\"Y\",\r\n"+"\"both\":\"Y\",\r\n"+"\"senderConvFactor\":\"11\",\r\n"+"\"receiverConvFactor\":\"1\",\r\n"+"\"cosRequired\":\"Y\",\r\n"+"\"inPromo\":0.0,\r\n"+"\"cardName\":\"cardp2pag\",\r\n"+"\"bonusAccList\":[\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4432\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14462\",\r\n"+"\"bundleID\":\"1\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"CVG\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"CVG\"\r\n"+"},\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4432\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14462\",\r\n"+"\"bundleID\":\"2\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"C\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"C\"\r\n"+"},\r\n"+"{\r\n"+"\"cardGroupSetID\":\"4432\",\r\n"+"\"version\":\"1\",\r\n"+"\"cardGroupID\":\"14462\",\r\n"+"\"bundleID\":\"3\",\r\n"+"\"type\":\"AMT\",\r\n"+"\"bonusValidity\":\"0\",\r\n"+"\"bonusValue\":\"0\",\r\n"+"\"multFactor\":\"1.0\",\r\n"+"\"bonusName\":\"VG\",\r\n"+"\"bundleType\":\"AMT\",\r\n"+"\"restrictedOnIN\":\"Y\",\r\n"+"\"bonusCode\":\"VG\"\r\n"+"}\r\n"+"],\r\n"+"\"bonus2\":0.0,\r\n"+"\"bonus1validity\":0,\r\n"+"\"rowIndex\":1,\r\n"+"\"status\":\"Y\",\r\n"+"\"transferValue\":0,\r\n"+"\"cardGroupID\":\"14462\"\r\n"+"}\r\n"+"],\r\n"+"\"cardGroupDetails\":{\r\n"+"\"version\":\"1\",\r\n"+"\"applicableFromDate\":\"19/06/20\",\r\n"+"\"applicableFromHour\":\"20:00\",\r\n"+"\"lastModifiedOn\":0,\r\n"+"\"serviceTypeDesc\":\"CP2P Data Transfer\",\r\n"+"\"setTypeName\":null,\r\n"+"\"subServiceTypeDescription\":\"CDATA\",\r\n"+"\"networkCode\":\"NG\",\r\n"+"\"modifiedBy\":\"NGLA0000003720\",\r\n"+"\"moduleCode\":\"P2P\",\r\n"+"\"cardGroupSetID\":\"4432\",\r\n"+"\"cardGroupSetName\":\"cardp2pag1342121\"\r\n"+"}\r\n"+"}\r\n"+"}\r\n";
	public static final String CALCULATE_VOUCHER_TRANSFER_RULE = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"   \"data\": {\r\n" + 
				"   \"networkCode\": \"NG\",\r\n" + 
				"   \"userId\":\"SYSTEM\",\r\n" + 
				"   \"moduletype\":\"Peer to Peer\",\r\n" + 
				"   \"gatewaycode\":\"EXTGW\",\r\n" + 
				"   \"servicetype\":\"Voucher Consumption\",\r\n" + 
				"   \"subservice\":\"CVG\",\r\n" + 
				"   \"sendertype\":\"Prepaid Subscriber\",\r\n" + 
				"   \"senderserviceclass\":\"ALL(ALL)\",\r\n" + 
				"   \"receivertype\":\"Prepaid Subscriber\",  \r\n" + 
				"   \"receiverserviceclass\":\"ALL(ALL)\",\r\n" + 
				"   \"denomination\":\"102\",\r\n" + 
				"   \"vouchersegment\":\"\",\r\n" + 
				"   \"vouchertype\":\"\",\r\n" + 
				"   \"productName\":\"\",\r\n" + 
				"   \"validitydate\":\"23/08/19\",\r\n" + 
				"   \"applicablefrom\" :\"23/08/19\",\r\n" + 
				"   \"applicabletime\":\"16:23\"\r\n" + 
				"                  }\r\n" + 
				"}\r\n";
		
		public static final String LOAD_CARD_GROUP_SET = "{\r\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"  \"data\": {\r\n" + 
				"    \"moduleCode\": \"P2P\",\r\n" + 
				"    \"networkCode\": \"NG\"\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				""; 

		public static final String UPDATE_CARD_GROUP_STATUS = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"  \"data\": {\r\n" + 
				"    \"moduleCode\": \"P2P\",\r\n" + 
				"    \"networkCode\": \"NG\",\r\n" + 
				"    \"cardGroupSetList\": [\r\n" + 
				"      {\r\n" + 
				"        \"cardGroupSetName\": \"AUTKc1Al9\",\r\n" + 
				"        \"serviceTypeDesc\": \"Voucher Consumption\",\r\n" + 
				"        \"subServiceTypeDescription\": \"Choice RC 1\",\r\n" + 
				"        \"modifiedBy\": \"SYSTEM\",\r\n" + 
				"        \"language1Message\": \"This is from API 5\",\r\n" + 
				"        \"language2Message\": \"This is from API 5\",\r\n" + 
				"        \"status\": \"Y\"\r\n" + 
				"      }\r\n" + 
				"    ]\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				"";

		public static final String DEFAULT_CARD_GROUP = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"    \"networkCode\": \"NG\",\r\n" + 
				"    \"userId\":\"SYSTEM\",\r\n" + 
				"    \"serviceTypeId\": \"1\",\r\n" + 
				"    \"subServiceTypeId\": \"RC\",\r\n" + 
				"    \"cardGroupSetId\": \"3099\",\r\n" + 
				"    \"moduleCode\":\"P2P\"\r\n" + 
				"}\r\n";
		
		public static final String LOAD_VERSION_LIST = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"  \"data\": {\r\n" + 
				"    \"moduleCode\": \"P2P\",\r\n" + 
				"    \"networkCode\": \"NG\",\r\n" + 
				"    \"numberOfDays\": \"30\"\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				"";
		
		public static final String DELETE_CARD_GROUP = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"  \"data\": {\r\n" + 
				"    \"serviceTypeDesc\": \"Voucher Consumption\",\r\n" + 
				"    \"subServiceTypeDesc\": \"Choice RC 1\",\r\n" + 
				"    \"cardGroupSetName\": \"AUTKc1Al9\",\r\n" + 
				"    \"modifiedBy\": \"SYSTEM\",\r\n" + 
				"    \"moduleCode\": \"P2P\",\r\n" + 
				"    \"networkCode\": \"NG\"\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				"";
		
		public static final String VIEW_CARD_GROUP_SET = "{\r\n" + 
		"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
		"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
		"  \"data\": {\r\n" + 
		"  \"serviceTypeDesc\": \"Voucher Consumption\",\r\n" + 
		"    \"subServiceTypeDesc\": \"Choice RC 1\",\r\n" + 
		"    \"cardGroupSetId\": \"3118\",\r\n" + 
		"    \"version\":\"1\",\r\n" + 
		"    \"networkCode\": \"NG\",\r\n" + 
		"    \"numberOfDays\": \"30\"\r\n" + 
		"  }\r\n" + 
		"}\r\n" + 
		"";
		
		public static final String PROCESS_CP2P_USER_REQUEST = "{\r\n" + 
				"                \"reqGatewayLoginId\": \"pretups\",\r\n" + 
				"                \"reqGatewayPassword\": \"1357\",\r\n" + 
				"                \"reqGatewayCode\": \"REST\",\r\n" + 
				"                \"reqGatewayType\": \"REST\",\r\n" + 
				"                \"servicePort\": \"190\",\r\n" + 
				"                \"sourceType\": \"JSON\",\r\n" + 
				"                \"data\": {\r\n" + 
				"                    \"subscriberMsisdn\": \"72105\"\r\n" +
				"                }\r\n" + 
				"}\r\n" + 
				"";
		
		public static final String CHNL_USR_AVAILABLE_VOUCHER_REQ = "{\r\n" + 
				"  \"reqGatewayLoginId\": \"pretups\",\r\n" + 
				"  \"reqGatewayPassword\": \"1357\",\r\n" + 
				"  \"reqGatewayCode\": \"REST\",\r\n" + 
				"  \"reqGatewayType\": \"REST\",\r\n" + 
				"  \"servicePort\": \"190\",\r\n" + 
				"  \"sourceType\": \"JSON\",\r\n" + 
				"  \"data\": {\r\n" + 
				"    \"extnwcode\": \"NG\",\r\n" + 
				"    \"msisdn\": \"72525252\",\r\n" + 
				"    \"pin\": \"1357\",\r\n" + 
				"    \"loginid\": \"ydist\",\r\n" + 
				"    \"password\": \"1357\",\r\n" + 
				"    \"extcode\": \"11113\",\r\n" + 
				"    \"vouchertype\": \"physical\",\r\n" + 
				"    \"vouchersegment\":\"NL\",\r\n" + 
				"    \"denomination\":\"11\",\r\n" + 
				"    \"voucherprofile\":\"3167\"\r\n" + 
				"  }\r\n" + 
				"}\r\n" + 
				"";
		
		public static final String PROCESS_CHANNEL_USER_REQ = "{\r\n" + " \"reqGatewayLoginId\": \"pretups\",\r\n" + 
				"    \"reqGatewayPassword\": \"1357\",\r\n" + 
				"    \"reqGatewayCode\": \"REST\",\r\n" + 
				"    \"reqGatewayType\": \"REST\",\r\n" + 
				"    \"servicePort\": \"190\",\r\n" + 
				"    \"sourceType\": \"JSON\",\r\n" + 
				"    \"data\": {\r\n" + 
				"        \"extnwcode\": \"NG\",\r\n" + 
				"        \"vouchertype\": \"physical\",\r\n" + 
				"    	 \"loginid\": \"ydist\",\r\n" + 
				"   	 \"password\": \"1357\",\r\n" + 
				"   	 \"extcode\": \"652335303\",\r\n" + 
				"        \"msisdn\": \"723180593373806\",\r\n" + 
				"        \"pin\": \"2468\",\r\n" + 
				"        \"msisdn2\": \"7234267858\",\r\n" + 
				"                \"vouchersegment\":\"Local\",\r\n" + 
				"        \"amount\":\"103\",\r\n" + 
				"        \"voucherprofile\":\"PROF1031\",\r\n" + 
				"        \"quantity\":\"3\",\r\n" + 
				"        \"language1\": \"0\",\r\n" + 
				"        \"language2\": \"0\",\r\n" + 
				"        \"selector\":\"1\"\r\n" + 
				"    }\r\n" + 
				"}\r\n" + 
				"";
		public static final String SELF_COMMISSION_ENQUIRY_DESC = "View self Commission Enquiry of the user" + ("\n") + 
				("Parameters:") + ("\n") + ("1. LoginID") + ("\n") + ("2. Password") + ("\n") + ("Only for Distribuitor, Retailer and Agent");
		
		public static final String SELF_PROFILE_THRSHOLD_DESC = "loggedInIdentifierType : ID of logged in user "
				+ "| Mandatory | Possible values: NA\r\n" + "\r\n" + "loggedInIdentifierValue : Password of logged in user "
				+ "| Mandatory | Possible values: NA";
		
		public static final String USER_PROFILE_THRSHOLD_DESC = "loggedInIdentifierType : ID of logged in user | Mandatory "
				+ "| Possible values: NA\r\n" + "\r\n" + "loggedInIdentifierValue : Password of logged in user | Mandatory "
				+ "| Possible values: NA\r\n" + "\r\n" + "loginID : ID of child user | Optional if msisdn is provided | "
				+ "Possible values: NA\r\n" + "\r\n" + "msisdn : msisdn of user | Optional if loginID is provided | "
				+ "Possible values: NA";
		
		public static final String SEARCH_USER_DESC = "Category List\r\n" + 
				"1 - Dealer\r\n" + "2 - Retailer\r\n" + "3 - Agent\r\n" + "\r\n" + "loggedInIdentifierType : ID of logged in"
				+ " user | Mandatory | Possible values: NA\r\n" + "\r\n" + 	"loggedInIdentifierValue : Password of logged in "
				+ "user | Mandatory | Possible values: NA\r\n" + "\r\n" + "category :Category of user to be searched "
				+ "|Mandatory | Possible values: 1,2,3\r\n" + "\r\n" + "searchValue : String to be searched | "
				+ "Possible values: Any string or %% for list of all users in the category";
		
		public static final String C2C_VOUCHER_APPROVAL = "C2C Voucher Approval API";
		public static final String C2C_TRANSFER_DETAILS = "C2C Transfer Details API";
		public static final String C2C_TRANSFER_APPROVAL_LIST = "C2C Transfer Approavl List API";
		public static final String C2C_BUY_VOUCHER_INITIATE = "Voucher Transfer Initiate";
		public static final String C2C_TRANSFER_VOUCHER_INITIATE  = "Voucher Transfer";
		public static final String C2C_TRANSFER_STOCK  = "C2C Stock Transfer";
		public static final String C2C_VOUCHER_TYPE  = "Get voucher type info";
		public static final String C2C_VOUCHER_SEGMENT  = "Get voucher segment info";
		public static final String C2C_VOUCHER_DENOMINATION  = "Get voucher denomination info";
		public static final String C2C_VOUCHER_INFO  = "Get voucher info";
		public static final String C2C_VOUCHER_COUNT  = "Get voucher count info";

		public static final String CHANNEL_USER_DETAILS = "Get Channel User info ";
		public static final String C2S_TOTAL_OF_TRANSACTION_CONTROLLER = "Get Total No of Transaction ";
		public static final String TOTAL_TRANSACTIONS_DETAILED_VIEW = "Total Transactions Detailed View ";

		
		public static final String USER_HIERARCHY_REQUEST = "{ \"reqGatewayLoginId\": \"pretups\", \"data\": { \"extcode\": \"\", \"loginid\": \"\", \"language2\": \"0\", \"language1\": \"0\", \"extnwcode\": \"NG\", \"type\": \"UPUSRHRCHY\", \"password\": \"1357\", \"pin\": \"2468\", \"msisdn\": \"aaa\" }, \"sourceType\": \"JSON\", \"reqGatewayType\": \"REST\", \"reqGatewayPassword\": \"1357\", \"servicePort\": \"190\", \"reqGatewayCode\": \"REST\" }";
		public static final String C2S_SRV_CNT = "Get current date and previous 1 month transaction amount of a user according to the services assigned ";
		public static final String C2S_PROD_TXN_DETAILS = "Get API for user transactional data";
		public static final String BALANCE_DETAILS = "API for user balance data";
		public static final String OTP_FOR_FORGOT_PASSWORD_CONTROLLER = " Otp For Forgot Password Controller";
		public static final String OTP_FOR_FORGOT_PIN_CONTROLLER = " Otp For Forgot Pin Controller";
		public static final String C2S_N_PROD_TXN_DETAILS = "Get API for Top 5 Products Recharge Details";
		
		public static final String OTP_FOR_USER_TRANFER="Otp For User Transfer";
		public static final String CONFIRM_USER_TRANFER="Confirm Channel User Transfer";

		public static final String NETWORK_STOCK_CREATION = "{\r\n" + 
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_TYPE+"\": \"btnadm\"," + "\n" +
				"  \""+PretupsI.REST_LOGGEDIN_IDENTIFIER_VALUE+"\": \"1357\"," + "\n" +
				"   \"data\": {\r\n" + 
				"   \"networkCode\": \"NG\",\r\n" + 
				"   \"userId\":\"SYSTEM\",\r\n" + 
				"   \"referenceNumber\":\"2323\",\r\n" + 
				"   \"remarks\":\"remarka\",\r\n" + 
				"   \"walletType\":\"SAL\",\r\n"+
				"    \"stockProductList\" : [\r\n" + 
				"        {\r\n" + 
				"                                \"productCode\":\"ETOPUP\",\r\n" + 
				"                                \"productName\":\"\",\r\n" + 
				"                                \"requestedQuantity\": \"10\"\r\n" + 
				"                                }\r\n" + 
				"                                ]\r\n" + 
				"  }\r\n" + 
				"}\r\n";
		public static final String OTP_VD_PIN_RST = "Otp Validation and PIN Reset";
		public static final String AUTO_COMPLETE= "Auto complete Users Details";
		public static final String USER_PMTYP = "Get User Payment Type";
		public static final String GET_DOMAIN_CATEGORY = "Get Domain And Category";
		public static final String GET_SENDER_RECEIVER_INFO = "Get Sender Receiver Detailed Info" + "\n" + "Tag c2ctrftype is \"C\" for C2C Transfer, \"B\" for C2C Buy, \"R\" for C2C Return and \"W\" for C2C Withdraw";
		public static final String ADD_CHANNEL_USER="Add Channel User";
		public static final String DELETE_USER="Delete a User";
		public static final String MODIFY_CHANNEL_USER="Modify Channel User";
		public static final String CHNL_USER_NETWORK="Network code (NG, PB)";
		public static final String CHNL_USER_CATEGORY="User Category (SE(Dealer), RET(Retailer), AG(Agent))";
		public static final String CHNL_USER_PARENT_CATEGORY="Parent Category (DIST(Super Distributor), SE(Dealer), RET(Retailer), AG(Agent))";
		public static final String CHNL_USER_PARENT_GEOGRAPHY="Parent Geography";
		public static final String CHNL_USER_LOGINID="Login Id";
		public static final String CHNL_USER_PASSWD="Password";
		public static final String CHNL_USER_TYPE="Identifier Type";
		public static final String RESET_PIN="Reset PIN";
		public static final String CHNL_USER_VALUE="Identifier Value";
		public static final String USR_SELECT="User select the value msisdn or loginid for which profile list will be shown";
		public static final String SELECTED_VALUE="Enter value of msisdn if selected MSISDN or enter loginId if selected LOGINID";
		public static final String NETWORK_CODE="Network Code";
		public static final String USR_SELECT_PUT="User select the value msisdn or loginid or extcode for which user have to be modified";
		public static final String SELECTED_VALUE_PUT="Enter value of msisdn if selected MSISDN or enter loginId if selected LOGINID or enter extcode if selected extcode";
		public static final String USR_SELECT_DEL="User select the value msisdn or loginid of the user they want to delete";
		public static final String DELETE_REQ="deleteUserRequest";
		public static final String SELECT_MSISDN_OR_LOGINID = "Select one from MSISDN or LOGIN to search User Details";
		public static final String C2S_RECHARGE="Prepaid Recharge";
		public static final String GIFT_RECHARGE="Gift Recharge";
		public static final String FILE_DOWNLOAD="File Download";
		public static final String POST_PAID_BILL="Postpaid Bill payment";
		public static final String AUTOCMPLT_USER_DETAILS_DESCRIPTION ="Api Info: \r\n" +
		                                                                "1. categoryCode and domainCode is optional.\r\n" + 
				                                                         "2. Provide one from msisdn with pin, loginid with passwod or extcode.\r\n"+
		                                                                "3. One from msisdnToSearch, loginIdToSearch or userNameToSearch is mandatory.";
		
		public static final String EVD="Electronic Voucher Distribution(EVD)";
		public static final String DVD="Digital Voucher Distribution(DVD)";
		public static final String MVD="Multiple Voucher Distribution(MVD)";
		public static final String INTERNET_RECHARGE="Internet Recharge";
		
		public static final String BULK_INTERNET_RECHARGE ="Bulk Internet Recharge Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named scheduleNow is either on or off .\r\n"+
                "3. Accepted value  for field  named batchType is either Normal or Restricted.\r\n"+
                "4. Accepted value  for field  named noOfDays can be natural number only.\r\n"+
                "5. Accepted values for field named occurence are Daily, Weekly , Monthly .";
		
		public static final String BULK_C2S_RECHARGE =" Bulk Prepaid Recharge Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named scheduleNow is either on or off .\r\n"+
                "3. Accepted value  for field  named batchType is either Normal or Restricted.\r\n"+
                "4. Accepted value  for field  named noOfDays can be natural number only.\r\n"+
                "5. Accepted values for field named occurence are Daily, Weekly , Monthly .";
		
		public static final String BULK_GIFT_RECHARGE ="Bulk Gift Recharge Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named scheduleNow is either on or off .\r\n"+
                "3. Accepted value  for field  named batchType is either Normal or Restricted.\r\n"+
                "4. Accepted value  for field  named noOfDays can be natural number only.\r\n"+
                "5. Accepted values for field named occurence are Daily, Weekly , Monthly .";
		
		public static final String BULK_USER_UPLOAD =" Bulk Users Upload Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named batchName is name of batch .\r\n"+
                "3. Accepted value  for field  named domainCode is domain code.\r\n"+
                "4. Accepted value  for field  named file have base64 converted xlsx file.\r\n"+
                "5. Accepted values for field fileName having name of file .\r\n"+
                "6. Accepted values for field fileType having type of file. example 'xlsx' .\r\n"+
                "7. Accepted value  for field  named geographyCode is geography code.";
		
		public static final String FIXLINE_RECHARGE="Fix Line Recharge";
		public static final String RECHARGE_REVERSAL="Recharge Reverse";
		public static final String PREPAID_RECHARGE="Prepaid Recharge";
		public static final String SERVICE_TYPES="Service Type Code";
		public static final String OAUTHENTICATION_API_INFO ="Api Info: \r\n" +
                 "1. Provide one from msisdn with pin, loginid with passwod or extcode.\r\n";
		public static final String REFRESH_TOKEN_API_INFO ="Api Info: \r\n" +
                "1. Refresh token can be generated using \"Generate Token\" API .\r\n";
		public static final String C2C_RETURN="C2C RETURN";
		public static final String C2C_TXN_ID="C2C Transaction ID";
		public static final String C2C_WITHDRAW="C2C WITHDRAW";
		public static final String C2C_INITIATE="C2C INITIATE";
		public static final String CATEGORY_CODE="CATEGORY_CODE";
		public static final String TRANSFER_TYPE="Enter the transfer type as TRANSFER or WITHDRAW";
		public static final String C2C_TRANSFER_MUL="C2C Transfer Multiple";
                
		public static final String CLIENT_ID = "CLIENT_ID";
		public static final String CLIENT_SECRET = "CLIENT_SECRET";
		
		public static final String LANGUAGE = "regEx validation for different languages(en,ar,fa,fr)";
		public static final String REQ_GATEWAY_CODE = "Request Gateway Code";
		public static final String REQ_GATEWAY_LOGIN_ID = "Request Gateway Login Id";
		public static final String REQ_GATEWAY_PWD = "Request Gateway Password";
		public static final String REQ_GATEWAY_TYPE = "Request Gateway Type";
		public static final String SERVICE_PORT = "Service Port";
		public static final String SCOPE = "Scopes";
		public static final String O2C_VOUCHER_TRF="O2C Voucher Transfer";
		public static final String FOC_TRF="FOC Transfer Inititae";
		public static final String O2C_VOUCHER_INI="O2C Voucher Initiate";
		public static final String O2C_WITHDRAW="O2C Withdraw";
		public static final String O2C_VOUCHER_APPROV="O2C Voucher Approval";
		
		public static final String USER_WIDGET_RESPONSE="Update or Add User Widgets";
		public static final String GET_USR_SVC_BAL="User Balance With Services";
		public static final String USERNAME="USERNAME";
		public static final String CHANNEL_OWNER_CATEGORY="CHANNEL OWNER CATEGORY";
		public static final String GEOGRAPHICAL_DOMAIN_CODE="Geographical Domain Code";
		public static final String CHANNEL_DOMAIN="Domain";
		public static final String FOC_TRANSFER_BATCH="FOC Batch Transfer \r\n"+ "Use Prefrence COM_PAY_OUT as true for Bulk Commission Payout or as false for FOC Batch"
		;
		
		public static final String SUSPEND_RESUME="Suspend Resume User";
		public static final String ADD_TRANSFER_RULE="Add Transfer rule";
		public static final String MODIFY_TRANSFER_RULE="Modify Transfer rule";
		public static final String DELETE_TRANSFER_RULE="Delete Transfer rule";
		
		public static final String BULK_DVD ="Bulk Digital Voucher Distribution Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named scheduleNow is either on or off .\r\n"+
                "3. Accepted value  for field  named noOfDays can be natural number only.\r\n"+
                "4. Accepted values for field named occurence are Daily, Weekly , Monthly .";
		public static final String CHANNEL_OWNER_USER_ID="Channel Owner Category UserID";
		public static final String O2C_TRANSFER_BATCH="O2C Batch Transfer";
		public static final String TYPE="Enter the  type as O2C or C2C";
		public static final String FOC_APPROVAL = "Api Info:" + ("\n")  + "1.staus: APPROVE OR REJECT." + ("\n") + "2.currentStaus: NEW, APPRV1 OR APPRV2.";
		public static final String BATCH_APPROVAL_LIST_DETAILS = "Api Info:\n"+"1.Approval Level: 1, 2 or 3 \n"+"2.Approval Type: O2C OR FOC.";
		public static final String BATCH_APPROVAL_DETAILS = "Api Info:\n"+"1.Approval Level: 1, 2 or 3 \n"+"2.Approval Type: O2C OR FOC \n"+"3.Approval Sub Type: T or W.";
		public static final String ADD_STAFF_USER="Add Staff User";
		public static final String EDIT_STAFF_USER = "Edit Staff User";
		public static final String BAR_USER= "Bar User";
		
		public static final String VIEW_C2S_BULK_RECHARGE_DETAILS= "View c2s bulk recharge details";		
		public static final String ENTER_LOGIN_ID= "Enter login ID";
		public static final String PIN_CHANGE= "User/Self Pin Change";
		public static final String BARRED_USER_LIST = "To Fetch Barred User List fill one of the below options:\r\n" + 
				"1) On the basis of MSISDN \r\n" + 
				"2) On the basis of USERNAME \r\n" +
				"3) On the basis of filter \r\n"
				+ "a. Domain – Non Editable in case of Channel user \r\n"
				+ "b. Geography – Non Editable in case of Channel User \r\n"
				+ "c. User Type -  STAFF OR CHANNEL USER \r\n"
				+ "d. Module – Channel To Subscriber(LOCALE MASTER) \r\n"
				+ "e. Barred As - Sender Only, Pre Populated ( In case of Channel user) \r\n"
				+ "f. Barring Type - ALL\r\n" 
				+ "g. From Date \r\n"
				+ "h. To Date \r\n"
				+ "i. category - ALL" +
				"";
		public static final String CANCEL_BATCH= "Cancel Scheduled Topup Batch";
		public static final String CANCEL_SINGLE_MSISDN_BATCH= "Cancel Single msidn in Scheduled Topup Batch";
		public static final String DOMAIN= "Parent Domain";
		public static final String USER_ID= "User Id";
		public static final String PASSWORD_CHANGE="change password";
		public static final String FORGOT_PASSWORD="forgot password";
		public static final String BULK_C2C_PROCESS_APPROVAL= "C2c bulk process approval";
		public static final String USER_NAME= "User name";
		public static final String TRANSACTION_ID= "Transaction ID";
		public static final String DISTRIBUTION_TYPE= "Distribution type";
		public static final String FILE_TYPE= "File type";
		public static final String C2STRANSFER_ENQUIRY= "C2S Transfer Enquiry API info: \r\n"+
									"1) Date Range < 20 \r\n"+
									"2) Enter Numeric Msisdn \r\n"+
									"3) Enter One of transfer ID or sender or reciever msisdn.";
		public static final String Lookup_Type= "Lookup type";
		public static final String USERCLOSINGBALANCE_ENQUIRY= "User closing balance enquiry API";
		public static final String CHANNEL_USER= "Channel User";
		public static final String OPTION_MOBILEorLOGINID="OPTION_LOGIN_ID/OPTION_MSISDN";
		public static final String REQUEST_TAB="C2C_MOBILENUMB_TAB_REQ/C2C_ADVANCED_TAB_REQ";
		public static final String REPORT_TASK_ID= "Report task ID";
		public static final String LOGIN_ID= "Please enter login ID";
		public static final String OFFLINE_ACTION= "Offline task actions";
		public static final String O2C_ACKKNOWLEDGE_DETAILS = "O2C ACKNOWLEDGE DOWNLOAD API";
		public static final String CHANNEL_USER_TRANSFER= "Channel User Transfer API info: \r\n"+"1) Enter either Msisdn or other fields.";
		public static final String C2C_OR_O2C_ENQ = ("Api Info:") + ("\n")
				+ ("1. Domain , Category and Geography is fixed.") + ("\n")
				+ ("2. Distribution type not applicable.") + ("\n")
				+ ("3. Enquiry Type: O2C or C2C") + ("\n")
				+ ("4. Search By: ") + ("\n")
				+ ("5. TRANSACTIONID: Only transactionId is required. ") + ("\n")
				+ ("6. MSISDN: senderMsisdn, receiverMsisdn, transferSubType, fromDate and toDate is required.") + ("\n")
				+ ("6a. C2C: Sender or Receiver mobile number or both,  transferSubType, fromDate and toDate is required.") + ("\n")
				+ ("6b. O2C: receiverMsisdn, transferSubType, fromDate and toDate is required.") + ("\n")
				+ ("7. ADVANCE: userID, orderStatus, transferSubType, fromDate and toDate is required. ") + ("\n") 
				+ ("7a. C2C: Sender or Receiver mobile number or both,  transferSubType, fromDate, toDate, transferCategory, userType and staffLoginID(for userType:STAFF) is required.") + ("\n")
				+ ("7b. O2C: receiverMsisdn, transferSubType, fromDate , toDate and transferCategory is required. userType and staffLoginID is not Applicable") + ("\n");
		public static final String TRANSFER_USER_HIERARCHY = "Transfer User Hierarchy API info: \r\n "
				+"1) Enter to and from UserID.";
	    public static final String BATCH_C2C_TRANSFER_DETAILS=("Batch C2C Transfer Details Api")+("\n")
	    		+("Search By :")+("\n")
	    		+("BATCHNO : Only batchId is required")+("\n")
	            +("ADVANCE :categoryCode,domainCode,fromDate,geographyCode,productCode,toDate,userId are required");
	    
	    public static final String O2C_TXN_REVERSAL = ("Api Info:") + ("\n")
					+ ("1. Both \"Transaction ID\" and \"remarks ID\" are mandatory."); 
	    public static final String ADD_CHANNEL_USER_APPRV="Add Channel User approval";
	    public static final String O2C_TXN_ENQUIRY = ("Api Info:") + ("\n")
	    		+ ("1. \"Transaction ID\" is required to search O2C Transaction details."); 
	    public static final String CHANNEL_USER_APPRV_LIST="Channel user approval list";
	    public static final String BULK_UPLOAD_API = "Bulk upload API";
	    
	    public static final String SELECT_USER_ACTION = "Select one user action(DELETE(DR),SUSPEND(SR),RESUME(RR) ) User Details";
	    public static final String SELECT_FILE_TYPE_TXT = "txt";
	    public static final String SELECT_FILE_NAME = "Enter file name";
	    public static final String SELECT_FILE_ATTACHMENT = "Base 64 encoded file";
	    public static final String MODIFY_DIVISION="Modify division";
	    public static final String MODIFY_DEPARTMENT="Modify Department";
	    public static final String VoucherBatchListFetch = "To Fetch Voucher batch list:\r\n" + 
				"1) On the basis of batch type\r\n" + 
				"2) On the basis of from date\r\n" +
				"3) On the basis of to date \r\n"
				+ "4) Batch type will be Y for Downloaded Batches \r\n"
				+ "5) Batch type will be N for Non Downloaded Batches \r\n"
				+ "6) Batch type will be ALL for All Batches \r\n"
				+ "7) Parameters in response contains decryption key \r\n";
	    public static final String VoucherBatchListDownload = "To download voucher file and decryption jar:\r\n" + 
				"1) On the basis of jarFlag\r\n" + 
				"2) On the basis batchNo\r\n" +
				"3) if jarFlag is Y then will download decryption jar in that case batchNo should be NA \r\n" +
				"4) if jarFlag is N and valid batchNo is given then will download voucher file \r\n" +
		        "5) if jarFlag is N decKey should also be given which is sent by /getVoucherDownloadFile API\r\n";
	    public static final String DOMAIN_CODE="DOMAIN_CODE";
	    public static final String DIVISION_ID="Enter division ID";
	    public static final String BARRED_USER_APPRV_LIST = "Barred user approval list"  ;
	    
	    public static final String BULK_EVD_RECHARGE =" Bulk EVD Recharge Api Info: \r\n" +
                "1. All the fields are manadatory.\r\n" + 
                "2. Accepted value  for field  named scheduleNow is either on or off .\r\n"+
                "3. Accepted value  for field  named batchType is either Normal or Restricted.\r\n"+
                "4. Accepted value  for field  named noOfDays can be natural number only.\r\n"+
                "5. Accepted values for field named occurence are Daily, Weekly , Monthly .";
	    
	    public static final String VOUCHER_CONSUMPTION_API ="Api Info: \r\n" +
	    		"voucher consumption api \r\n" + 
	    		"v_7.53.1 \r\n" +
                "Voucher Consuption/Reedem Api.";
	    public static final String SERVICE_TYPE ="serviceType" ;
	    public static final String SERVICE_KEYWORD_ID ="serviceKeywordID" ;
	    public static final String CLASS_HANDLER_API = "Load Class Handler List";
	    public static final String C2S_CARD_GROUP_BATCH_FILE = "Download File for Batch modify C2S Card Group";
	    public static final String C2S_CARD_GROUP_BATCH_MODIFY = "Batch modify C2S Card Group";
		public static final String ADD_OPERATOR_USER = "Add operator user";
	public static final String ADD_SERVICE_CLASS = "Add Service Class";
	public static final String MODIFY_SERVICE_CLASS = "Modify Service Class";
	public static final String MODIFY_NETWORK="Modify Network";
	

}
