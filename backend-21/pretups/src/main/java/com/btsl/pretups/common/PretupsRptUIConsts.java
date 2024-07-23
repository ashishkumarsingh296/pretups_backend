package com.btsl.pretups.common;

public enum PretupsRptUIConsts {

	USER_NAME("User Name"), FROM_DATE("From date"), TO_DATE("To date"), PRODUCT_CODE("Product"),
	NETWORK_CODE("Network"), CATEGORY("Category"), GEOGRAPHY("Geography"), THRESHOLD("Threshold"),
	DOMAIN("DOMAIN"),
	PASSBOOKDOWNLOAD_FILENAME("Passbook"), LOWTHRESHOLDDOWNLOAD_FILENAME("Lowthreshold"),
	PASSBOOKREPORT_HEADING("Passbook Summary report"), Above_Threshold("Above Threshold"),
	Below_Threshold("Below Threshold"), PIN("PIN"), PASS("PASS"), LOWTHRESHOLDREPORT_HEADING("Low threshold  report"),
	// Numbers
	ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9),TEN(10) ,
	N11(11),N12(12),N13(13),N14(14),N15(15),

	PINPASSHISTDOWNLOAD_RPT_FILENAME("PinPassHistoryDownload.file.name"),
	PINPASSHISTDOWNLOAD_RPT_HEADING("PinPassHistoryDownload.file.heading"),
	PINPASSHISTDOWNLOAD_RPT_HEADER_DISPLAYVALUE("PinPassHistoryDownload.sheet.header.displayValue"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_USERNAME("PinPassHistoryDownload.sheet.cell.label.userName"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_FROMDATE("PinPassHistoryDownload.sheet.cell.label.fromDate"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_TODATE("PinPassHistoryDownload.sheet.cell.label.toDate"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_USERTYPE("PinPassHistoryDownload.sheet.cell.label.userType"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_DOMAIN("PinPassHistoryDownload.sheet.cell.label.domain"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_CATEORY("PinPassHistoryDownload.sheet.cell.label.category"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_NETWORKCODE("PinPassHistoryDownload.sheet.cell.label.networkCode"),
	PINPASSHISTDOWNLOAD_RPT_LABEL_REQTYPE("PinPassHistoryDownload.sheet.cell.label.reqType"),
	PINPASSHISTDOWNLOAD_RPT_COL_HEADER_LOGIN_ID("PinPassHistoryDownload.sheet.cell.label.loginID"),
	PINPASSHISTDOWNLOAD_RPT_COL_HEADER_MOBILE_NUM("PinPassHistoryDownload.sheet.cell.label.mobileNum"),

	C2SCOMMTRANSFDOWNLOAD_RPT_FILENAME("c2sTransCommDownload.file.name"),
	C2SCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE("c2sTransCommDownload.sheet.header.displayValue"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE("c2sTransCommDownload.sheet.cell.label.fromDate"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE("c2sTransCommDownload.sheet.cell.label.toDate"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN("c2sTransCommDownload.sheet.cell.label.domain"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY("c2sTransCommDownload.sheet.cell.label.category"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE("c2sTransCommDownload.sheet.cell.label.networkCode"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_SERVICE("c2sTransCommDownload.sheet.cell.label.service"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSTATUS("c2sTransCommDownload.sheet.cell.label.transStatus"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_GEOGRAPHY("c2sTransCommDownload.sheet.cell.label.geography"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFLOGINID("c2sTransCommDownload.sheet.cell.label.staffLoginID"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFMSISDN("c2sTransCommDownload.sheet.cell.label.staffMSISDN"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_MOBILENUM("c2sTransCommDownload.sheet.cell.label.mobileNum"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CHANNELUSERID("c2sTransCommDownload.sheet.cell.label.channelUserID"),
	C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_USERTYPE("c2sTransCommDownload.sheet.cell.label.userType"),

	REPORT_FROM_TIME(" 00:00:00"), REPORT_TO_TIME(" 23:59:59"),

	C2CCOMMTRANSFDOWNLOAD_RPT_FILENAME("c2cTransCommDownload.file.name"),
	C2CCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE("c2cTransCommDownload.sheet.header.displayValue"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE("c2cTransCommDownload.sheet.cell.label.fromDate"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE("c2cTransCommDownload.sheet.cell.label.toDate"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN("c2cTransCommDownload.sheet.cell.label.domain"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY("c2cTransCommDownload.sheet.cell.label.category"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE("c2cTransCommDownload.sheet.cell.label.networkCode"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_INCLUDESTAFF("c2cTransCommDownload.sheet.cell.label.includeStaff"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSTATUS("c2cTransCommDownload.sheet.cell.label.transStatus"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER("c2cTransCommDownload.sheet.cell.label.receiverNumber"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER("c2cTransCommDownload.sheet.cell.label.senderNumber"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINOUT("c2cTransCommDownload.sheet.cell.label.transferInout"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE("c2cTransCommDownload.sheet.cell.label.transferSubType"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERUSER("c2cTransCommDownload.sheet.cell.label.transferUser"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY("c2cTransCommDownload.sheet.cell.label.transferCategory"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERUSERCATEGORY("c2cTransCommDownload.sheet.cell.label.transferUserCategory"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER("c2cTransCommDownload.sheet.cell.label.sendUser"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_GEOGRAPHY("c2cTransCommDownload.sheet.cell.label.geography"),
	C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE("c2cTransCommDownload.sheet.cell.label.distributionType"),
	
	O2CTRANSACKNOWGEDOWNLOAD_RPT_FILENAME("o2cTransAcknoDetails.file.name"),
	O2CTRANSACKNOWGEDOWNLOAD_RPT_HEADER_DISPLAYVALUE("o2cTransAcknoDetails.sheet.header.displayValue"),
	O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_TRANSACTIONID("o2cTransAcknoDetails.sheet.cell.label.transactionID"),
	O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE("o2cTransAcknoDetails.sheet.cell.label.distributionType"),
	
	O2CTRANSFERDETDOWNLOAD_RPT_FILENAME("o2cTransferDetDownload.file.name"),
	O2CTRANSFERDETDOWNLOAD_RPT_HEADER_DISPLAYVALUE("o2cTransferDetDownload.sheet.header.displayValue"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_FROMDATE("o2cTransferDetDownload.sheet.cell.label.fromDate"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TODATE("o2cTransferDetDownload.sheet.cell.label.toDate"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN("o2cTransferDetDownload.sheet.cell.label.domain"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY("o2cTransferDetDownload.sheet.cell.label.category"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE("o2cTransferDetDownload.sheet.cell.label.networkCode"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE("o2cTransferDetDownload.sheet.cell.label.transferSubType"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY("o2cTransferDetDownload.sheet.cell.label.transferCategory"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_SENDERUSERNAME("o2cTransferDetDownload.sheet.cell.label.sendUserName"),
	O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE("o2cTransferDetDownload.sheet.cell.label.distributionType"),
	
	
	
	ADDTNLCOMMSUMMARY_RPT_FILENAME("addtlnlCommSummrpt.file.name"),
	ADDTNLCOMMSUMMARY_RPT_HEADER_DISPLAYVALUE("addtlnlCommSummrptdownld.sheet.header.displayValue"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE("addtlnlCommSummrptdownld.sheet.cell.label.fromDate"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE("addtlnlCommSummrptdownld.sheet.cell.label.toDate"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN("addtlnlCommSummrptdownld.sheet.cell.label.domain"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY("addtlnlCommSummrptdownld.sheet.cell.label.geography"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY("addtlnlCommSummrptdownld.sheet.cell.label.category"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE("addtlnlCommSummrptdownld.sheet.cell.label.networkCode"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE("addtlnlCommSummrptdownld.sheet.cell.label.service"),
	ADDTNLCOMMSUMMARY_RPT_LABEL_DAILY_MONTHLY("addtlnlCommSummrptdownld.sheet.cell.label.dailyMonthly"),
	
	REPORT_DATE("REPORT_DATE"),
	
	PASSBOOKOTHERSDOWNLOAD_RPT_FILENAME("passbookothersdownload.file.name"),
	PASSBOOKOTHERSDOWNLOAD_RPT_HEADER_DISPLAYVALUE("passbookothersdownload.sheet.header.displayValue"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN("passbookothersdownload.sheet.cell.label.domain"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY("passbookothersdownload.sheet.cell.label.category"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY("passbookothersdownload.sheet.cell.label.geography"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER("passbookothersdownload.sheet.cell.label.user"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT("passbookothersdownload.sheet.cell.label.product"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DES("passbookothersdownload.sheet.cell.label.desc"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE("passbookothersdownload.sheet.cell.label.fromDate"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE("passbookothersdownload.sheet.cell.label.toDate"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_NetworkCode("passbookothersdownload.sheet.cell.label.NetworkCode"),
	PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_ReconStatus("passbookothersdownload.sheet.cell.label.reconStatus"),
	
	
	LOWTHRESHOLDownload_RPT_HEADER_DISPLAYVALUE("LOWTHRESHOLDownload.sheet.header.displayValue"),
	
	BULKUSERADD_RPT_FILENAME("bulkuseraddrpt.file.name"),
	BULKUSERADD_RPT_HEADER_DISPLAYVALUE("bulkuseraddrpt.sheet.header.displayValue"),
	BULKUSERADD_RPT_LABEL_FROMDATE("bulkuseraddrpt.sheet.cell.label.fromDate"),
	BULKUSERADD_RPT_LABEL_TODATE("bulkuseraddrpt.sheet.cell.label.toDate"),
	BULKUSERADD_RPT_LABEL_DOMAIN("bulkuseraddrpt.sheet.cell.label.domain"),
	BULKUSERADD_RPT_LABEL_NETWORKCODE("bulkuseraddrpt.sheet.cell.label.networkCode"),
	BULKUSERADD_RPT_LABEL_GEOGRAPHY("bulkuseraddrpt.sheet.cell.label.geography"),
	BULKUSERADD_RPT_LABEL_BATCHNO("bulkuseraddrpt.sheet.cell.label.batchno"),
	BULKUSERADD_RPT_LABEL_BATCHNAME("bulkuseraddrpt.sheet.cell.label.batchname"),
	BULKUSERADD_RPT_LABEL_INITIATEDBY("bulkuseraddrpt.sheet.cell.label.initiatedby"),
	BULKUSERADD_RPT_LABEL_INITIATEDON("bulkuseraddrpt.sheet.cell.label.initiatedon"),
	BULKUSERADD_RPT_LABEL_BATCHSTATUS("bulkuseraddrpt.sheet.cell.label.batchStatus"),
	BULKUSERADD_RPT_LABEL_FILENAME("bulkuseraddrpt.sheet.cell.label.filename"),
	BULKUSERADD_RPT_LABEL_TOTALNO("bulkuseraddrpt.sheet.cell.label.totalno"),
	CACHE_CONTROL_VALUE("no-cache, no-store, must-revalidate"),
	PRAGMA_VALUE("no-cache"),
	
	
	ONLINE_FILE_PATH_KEY("ONLINE_FILE_PATH_KEY"); // FOR ONLINE DOWLOAD PATH KEY.
	
	
	
	
	
	
	
	
	
	
	private String reportValues;
	private int numValue;

	private PretupsRptUIConsts(int numValue) {
		this.numValue = numValue;
	}

	private PretupsRptUIConsts(String reportValues) {
		this.reportValues = reportValues;
	}

	public String getReportValues() {
		return reportValues;
	}

	public void setReportValues(String reportValues) {
		this.reportValues = reportValues;
	}

	public int getNumValue() {
		return numValue;
	}

	public void setNumValue(int numValue) {
		this.numValue = numValue;
	}

}