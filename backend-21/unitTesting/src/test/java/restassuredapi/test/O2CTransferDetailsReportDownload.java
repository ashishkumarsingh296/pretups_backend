package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import restassuredapi.api.o2CTransferDetailsReportDownload.O2CTransferDetailsReportDownloadApi;
import restassuredapi.pojo.o2Ctransferdetailsreportdownloadrequestpojo.O2CTransferDetailsReportDownloadRequestPojo;
import restassuredapi.pojo.o2Ctransferdetailsreportdownloadresponsepojo.O2CTransferDetailsReportDownloadResponsePojo;
import restassuredapi.pojo.o2Ctransferdetailsreportdownloadrequestpojo.Data;
import restassuredapi.pojo.o2Ctransferdetailsreportdownloadrequestpojo.DispHeaderColumn;

@ModuleManager(name = Module.O2C_TRANSFER_DETAILS_REPORT_DOWNLOAD)
public class O2CTransferDetailsReportDownload extends BaseTest {
	DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
	Date dateobj = new Date();
	String currentDate = df.format(dateobj);

	String fromDate = df.format(DateUtils.addDays(new Date(), -120));
	String toDate = df.format(DateUtils.addDays(new Date(), -1));

	static String moduleCode;
	O2CTransferDetailsReportDownloadRequestPojo o2CTransferDetailsReportDownloadRequestPojo = new O2CTransferDetailsReportDownloadRequestPojo();
	O2CTransferDetailsReportDownloadResponsePojo o2CTransferDetailsReportDownloadResponsePojo = new O2CTransferDetailsReportDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();

	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] Data = new Object[rowCount][8];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
		}
		return Data;
	}

	Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void setupData() {

		DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
		dispHeaderColumn.setColumnName("dateTime");
		dispHeaderColumn.setDisplayName("Date & Time");

		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setColumnName("transactionID");
		dispHeaderColumn1.setDisplayName("Transaction ID");

		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setColumnName("senderName");
		dispHeaderColumn2.setDisplayName("Sender name");

		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setColumnName("senderMsisdn");
		dispHeaderColumn3.setDisplayName("Sender mobile number");

		DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
		dispHeaderColumn4.setColumnName("receiverName");
		dispHeaderColumn4.setDisplayName("Receiver name");

		DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
		dispHeaderColumn5.setColumnName("receiverMsisdn");
		dispHeaderColumn5.setDisplayName("Receiver mobile number");

		DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
		dispHeaderColumn6.setColumnName("receiverQuantity");
		dispHeaderColumn6.setDisplayName("Receiver credit quantity");

		DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
		dispHeaderColumn7.setColumnName("transferCategory");
		dispHeaderColumn7.setDisplayName("Transfer Category");

		DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
		dispHeaderColumn8.setColumnName("transferSubType");
		dispHeaderColumn8.setDisplayName("Transfer Sub-Type");

		DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
		dispHeaderColumn9.setColumnName("modifiedOn");
		dispHeaderColumn9.setDisplayName("Modified on");

		DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
		dispHeaderColumn10.setColumnName("productName");
		dispHeaderColumn10.setDisplayName("Product name");

		DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
		dispHeaderColumn11.setColumnName("externalTransferNumber");
		dispHeaderColumn11.setDisplayName("External transfer number");

		DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
		dispHeaderColumn12.setColumnName("externalTransferDate");
		dispHeaderColumn12.setDisplayName("External transfer date");

		DispHeaderColumn dispHeaderColumn13 = new DispHeaderColumn();
		dispHeaderColumn13.setColumnName("transactionMode");
		dispHeaderColumn13.setDisplayName("Transaction mode");

		DispHeaderColumn dispHeaderColumn14 = new DispHeaderColumn();
		dispHeaderColumn14.setColumnName("requestedQuantity");
		dispHeaderColumn14.setDisplayName("Requested quantity");

		DispHeaderColumn dispHeaderColumn15 = new DispHeaderColumn();
		dispHeaderColumn15.setColumnName("approvedQuantity");
		dispHeaderColumn15.setDisplayName("Approved quantity");

		DispHeaderColumn dispHeaderColumn16 = new DispHeaderColumn();
		dispHeaderColumn16.setColumnName("commission");
		dispHeaderColumn16.setDisplayName("Commission");

		DispHeaderColumn dispHeaderColumn17 = new DispHeaderColumn();
		dispHeaderColumn17.setColumnName("cumulativeBaseCommission");
		dispHeaderColumn17.setDisplayName("Cummulative Base Commission");

		DispHeaderColumn dispHeaderColumn18 = new DispHeaderColumn();
		dispHeaderColumn18.setColumnName("tax1");
		dispHeaderColumn18.setDisplayName("Tax1");

		DispHeaderColumn dispHeaderColumn19 = new DispHeaderColumn();
		dispHeaderColumn19.setColumnName("tax2");
		dispHeaderColumn19.setDisplayName("Tax2");

		DispHeaderColumn dispHeaderColumn20 = new DispHeaderColumn();
		dispHeaderColumn20.setColumnName("payableAmount");
		dispHeaderColumn20.setDisplayName("Payable amount");

		DispHeaderColumn dispHeaderColumn21 = new DispHeaderColumn();
		dispHeaderColumn21.setColumnName("netPayableAmount");
		dispHeaderColumn21.setDisplayName("Net payable amount");

		DispHeaderColumn dispHeaderColumn22 = new DispHeaderColumn();
		dispHeaderColumn22.setColumnName("initiatorRemarks");
		dispHeaderColumn22.setDisplayName("Initiator remarks");

		DispHeaderColumn dispHeaderColumn23 = new DispHeaderColumn();
		dispHeaderColumn23.setColumnName("approver1Remarks");
		dispHeaderColumn23.setDisplayName("Approver1 remarks");

		DispHeaderColumn dispHeaderColumn24 = new DispHeaderColumn();
		dispHeaderColumn24.setColumnName("approver2Remarks");
		dispHeaderColumn24.setDisplayName("Approver2 remarks");

		DispHeaderColumn dispHeaderColumn25 = new DispHeaderColumn();
		dispHeaderColumn25.setColumnName("approver3Remarks");
		dispHeaderColumn25.setDisplayName("Approver3 remarks");

		DispHeaderColumn dispHeaderColumn26 = new DispHeaderColumn();
		dispHeaderColumn26.setColumnName("voucherBatchNumber");
		dispHeaderColumn26.setDisplayName("Voucher Batch no.");

		DispHeaderColumn dispHeaderColumn27 = new DispHeaderColumn();
		dispHeaderColumn27.setColumnName("vomsProductName");
		dispHeaderColumn27.setDisplayName("Voucher Product name");

		DispHeaderColumn dispHeaderColumn28 = new DispHeaderColumn();
		dispHeaderColumn28.setColumnName("batchType");
		dispHeaderColumn28.setDisplayName("Batch type");

		DispHeaderColumn dispHeaderColumn29 = new DispHeaderColumn();
		dispHeaderColumn29.setColumnName("totalNoofVouchers");
		dispHeaderColumn29.setDisplayName("Total no. of Vouchers");

		DispHeaderColumn dispHeaderColumn30 = new DispHeaderColumn();
		dispHeaderColumn30.setColumnName("fromSerialNumber");
		dispHeaderColumn30.setDisplayName("From Serial no.");

		DispHeaderColumn dispHeaderColumn31 = new DispHeaderColumn();
		dispHeaderColumn31.setColumnName("toSerialNumber");
		dispHeaderColumn31.setDisplayName("To Serial no.");

		DispHeaderColumn dispHeaderColumn32 = new DispHeaderColumn();
		dispHeaderColumn32.setColumnName("voucherSegment");
		dispHeaderColumn32.setDisplayName("Voucher Segment");

		DispHeaderColumn dispHeaderColumn33 = new DispHeaderColumn();
		dispHeaderColumn33.setColumnName("voucherType");
		dispHeaderColumn33.setDisplayName("Voucher Type");

		DispHeaderColumn dispHeaderColumn34 = new DispHeaderColumn();
		dispHeaderColumn34.setColumnName("voucherDenomination");
		dispHeaderColumn34.setDisplayName("Voucher Denomination");

		DispHeaderColumn dispHeaderColumn35 = new DispHeaderColumn();
		dispHeaderColumn35.setColumnName("transactionStatus");
		dispHeaderColumn35.setDisplayName("Transaction status");

		DispHeaderColumn dispHeaderColumn36 = new DispHeaderColumn();
		dispHeaderColumn36.setColumnName("distributionType");
		dispHeaderColumn36.setDisplayName("Distribution type");

//        DispHeaderColumn dispHeaderColumn37 = new DispHeaderColumn();
//        dispHeaderColumn37.setColumnName("receiverPreviousBalance");
//        dispHeaderColumn37.setDisplayName("Receiver previous balance");
//        
//        DispHeaderColumn dispHeaderColumn38 = new DispHeaderColumn();
//        dispHeaderColumn38.setColumnName("receiverPostBalance");
//        dispHeaderColumn38.setDisplayName("Receiver post balance");

		DispHeaderColumn dispHeaderColumn39 = new DispHeaderColumn();
		dispHeaderColumn39.setColumnName("requestGateWay");
		dispHeaderColumn39.setDisplayName("Request gateway name");

		DispHeaderColumn dispHeaderColumn40 = new DispHeaderColumn();
		dispHeaderColumn40.setColumnName("paymentInstType");
		dispHeaderColumn40.setDisplayName("Payment instrument type");

		DispHeaderColumn dispHeaderColumn41 = new DispHeaderColumn();
		dispHeaderColumn41.setColumnName("paymentInstNumber");
		dispHeaderColumn41.setDisplayName("Payment instrument number");

		DispHeaderColumn dispHeaderColumn42 = new DispHeaderColumn();
		dispHeaderColumn42.setColumnName("paymentInstDate");
		dispHeaderColumn42.setDisplayName("Payment instrument date");

		DispHeaderColumn dispHeaderColumn43 = new DispHeaderColumn();
		dispHeaderColumn43.setColumnName("firstLevelApprovedQuantity");
		dispHeaderColumn43.setDisplayName("Level 1 approved quantity");

		DispHeaderColumn dispHeaderColumn44 = new DispHeaderColumn();
		dispHeaderColumn44.setColumnName("secondLevelApprovedQuantity");
		dispHeaderColumn44.setDisplayName("Level 2 approved quantity");

		DispHeaderColumn dispHeaderColumn45 = new DispHeaderColumn();
		dispHeaderColumn45.setColumnName("thirdLevelApprovedQuantity");
		dispHeaderColumn45.setDisplayName("Level 3 approved quantity");

		DispHeaderColumn dispHeaderColumn46 = new DispHeaderColumn();
		dispHeaderColumn46.setColumnName("domainName");
		dispHeaderColumn46.setDisplayName("Domain name");

		DispHeaderColumn dispHeaderColumn47 = new DispHeaderColumn();
		dispHeaderColumn47.setColumnName("senderDebitQuantity");
		dispHeaderColumn47.setDisplayName("Sender debit quantity");

		DispHeaderColumn dispHeaderColumn48 = new DispHeaderColumn();
		dispHeaderColumn48.setColumnName("tax3");
		dispHeaderColumn48.setDisplayName("Tax3");

		List<DispHeaderColumn> dispHeaderColumnList = new ArrayList<DispHeaderColumn>();

		dispHeaderColumnList.add(dispHeaderColumn);
		dispHeaderColumnList.add(dispHeaderColumn1);
		dispHeaderColumnList.add(dispHeaderColumn2);
		dispHeaderColumnList.add(dispHeaderColumn3);
		dispHeaderColumnList.add(dispHeaderColumn4);
		dispHeaderColumnList.add(dispHeaderColumn5);
		dispHeaderColumnList.add(dispHeaderColumn6);
		dispHeaderColumnList.add(dispHeaderColumn7);
		dispHeaderColumnList.add(dispHeaderColumn8);
		dispHeaderColumnList.add(dispHeaderColumn9);
		dispHeaderColumnList.add(dispHeaderColumn10);
		dispHeaderColumnList.add(dispHeaderColumn11);
		dispHeaderColumnList.add(dispHeaderColumn12);
		dispHeaderColumnList.add(dispHeaderColumn13);
		dispHeaderColumnList.add(dispHeaderColumn14);
		dispHeaderColumnList.add(dispHeaderColumn15);
		dispHeaderColumnList.add(dispHeaderColumn16);
		dispHeaderColumnList.add(dispHeaderColumn17);
		dispHeaderColumnList.add(dispHeaderColumn18);
		dispHeaderColumnList.add(dispHeaderColumn19);
		dispHeaderColumnList.add(dispHeaderColumn20);
		dispHeaderColumnList.add(dispHeaderColumn21);
		dispHeaderColumnList.add(dispHeaderColumn22);
		dispHeaderColumnList.add(dispHeaderColumn23);
		dispHeaderColumnList.add(dispHeaderColumn24);
		dispHeaderColumnList.add(dispHeaderColumn25);
		dispHeaderColumnList.add(dispHeaderColumn26);
		dispHeaderColumnList.add(dispHeaderColumn27);
		dispHeaderColumnList.add(dispHeaderColumn28);
		dispHeaderColumnList.add(dispHeaderColumn29);
		dispHeaderColumnList.add(dispHeaderColumn30);
		dispHeaderColumnList.add(dispHeaderColumn31);
		dispHeaderColumnList.add(dispHeaderColumn32);
		dispHeaderColumnList.add(dispHeaderColumn33);
		dispHeaderColumnList.add(dispHeaderColumn34);
		dispHeaderColumnList.add(dispHeaderColumn35);
		dispHeaderColumnList.add(dispHeaderColumn36);
//        dispHeaderColumnList.add(dispHeaderColumn37);
//        dispHeaderColumnList.add(dispHeaderColumn38);
		dispHeaderColumnList.add(dispHeaderColumn39);
		dispHeaderColumnList.add(dispHeaderColumn40);
		dispHeaderColumnList.add(dispHeaderColumn41);
		dispHeaderColumnList.add(dispHeaderColumn42);
		dispHeaderColumnList.add(dispHeaderColumn43);
		dispHeaderColumnList.add(dispHeaderColumn44);
		dispHeaderColumnList.add(dispHeaderColumn45);
		dispHeaderColumnList.add(dispHeaderColumn46);
		dispHeaderColumnList.add(dispHeaderColumn47);
		dispHeaderColumnList.add(dispHeaderColumn48);

		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setCategoryCode("ALL");
		data.setDistributionType("ALL");
		data.setDomain("DIST");
		data.setExtnwcode("NG");
		data.setFromDate("01/09/21");
		data.setGeography("ALL");
		data.setToDate("30/09/21");
		data.setTransferSubType("ALL");
		data.setTransferCategory("ALL");
		data.setUser("ALL");
		data.setFileType("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
	}

	// Successful data with valid data.

	protected static String accessToken;

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		// if(accessToken==null) {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Positive Scenario
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-001")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(200);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(o2CTransferDetailsReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// network code provided is blank
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-002")
	public void A_02_Test_Negative2_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_02_Test_Negative2_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setExtnwcode("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Network Code Invaild or blank."); // need to check error code
		Assertion.assertEquals(messageCode, "Network Code Invaild or blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - fromDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-003")
	public void A_03_Test_Negative3_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_03_Test_Negative3_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setFromDate("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid date format."); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - toDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Test_Negative4_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_04_Test_Negative4_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setToDate("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid date format."); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - CategoryCode field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-005")
	public void A_05_Test_Negative5_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_05_Test_Negative5_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setCategoryCode("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid category"); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - domain field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-006")
	public void A_06_Test_Negative6_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_06_Test_Negative6_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setDomain("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Either Invalid domain or empty "); // need to check error code
		Assertion.assertEquals(messageCode, "Either Invalid domain or empty ");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank Geography field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-007")
	public void A_07_Test_Negative7_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_07_Test_Negative7_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setGeography("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "No record available."); // need to check error code
		Assertion.assertEquals(messageCode, "No record available.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank transferSubType field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-008")
	public void A_08_Test_Negative8_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_08_Test_Negative8_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setTransferSubType("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String errorCode = o2CTransferDetailsReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0)
				.getErrorCode();

		Assert.assertEquals(Integer.parseInt(errorCode), 1003006); // need to check error code
		Assertion.assertEquals(errorCode, "1003006");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank transferCategory field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-009")
	public void A_09_Test_Negative9_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_09_Test_Negative9_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setTransferCategory("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid transfer category"); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid transfer category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank user field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-010")
	public void A_10_Test_Negative10_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_10_Test_Negative10_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setUser("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid user."); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid user.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// blank - DistributionType field
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-011")
	public void A_11_Test_Negative11_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_11_Test_Negative11_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD11");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setDistributionType("");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(400);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String messageCode = o2CTransferDetailsReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid Distribution type."); // need to check error code
		Assertion.assertEquals(messageCode, "Invalid Distribution type.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-012")
	public void A_12_Test_Positive_O2CTransferDetailsReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_12_Test_Positive_O2CTransferDetailsReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDRDOWNLOAD12");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsReportDownloadApi o2CTransferDetailsReportDownloadApi = new O2CTransferDetailsReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		data.setFileType("XLSX");
		o2CTransferDetailsReportDownloadRequestPojo.setData(data);
		o2CTransferDetailsReportDownloadApi.addBodyParam(o2CTransferDetailsReportDownloadRequestPojo);
		o2CTransferDetailsReportDownloadApi.setExpectedStatusCode(200);
		o2CTransferDetailsReportDownloadApi.perform();
		o2CTransferDetailsReportDownloadResponsePojo = o2CTransferDetailsReportDownloadApi
				.getAPIResponseAsPOJO(O2CTransferDetailsReportDownloadResponsePojo.class);
		String fileType = o2CTransferDetailsReportDownloadResponsePojo.getFileType();

		Assert.assertEquals(fileType, "xlsx"); // need to check error code
		Assertion.assertEquals(fileType, "xlsx");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}