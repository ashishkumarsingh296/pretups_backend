package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import restassuredapi.api.c2CTransferCommissionReportDownload.C2CTransferCommissionReportDownloadApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2Ctransfercommissionreportdownloadrequestpojo.C2CTransferCommissionReportDownloadRequestPojo;
import restassuredapi.pojo.c2Ctransfercommissionreportdownloadrequestpojo.Data;
import restassuredapi.pojo.c2Ctransfercommissionreportdownloadrequestpojo.DispHeaderColumn;
import restassuredapi.pojo.c2Ctransfercommissionreportdownloadresponsepojo.C2CTransferCommissionReportDownloadResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2C_TRANSFER_COMMISSION_REPORT_DOWNLOAD)
public class C2CTransferCommissionReportDownload extends BaseTest {
	/*
	 * DateFormat df = new SimpleDateFormat("dd/MM/YYYY"); Date dateobj = new
	 * Date(); String currentDate=df.format(dateobj);
	 * 
	 * String fromDate = df.format(DateUtils.addDays(new Date(), -500)); String
	 * toDate = df.format(DateUtils.addDays(new Date(), -1));
	 */

	static String moduleCode;
	C2CTransferCommissionReportDownloadRequestPojo c2CTransferCommissionReportDownloadRequestPojo = new C2CTransferCommissionReportDownloadRequestPojo();
	C2CTransferCommissionReportDownloadResponsePojo c2CTransferCommissionReportDownloadResponsePojo = new C2CTransferCommissionReportDownloadResponsePojo();
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
		dispHeaderColumn.setDisplayName("Date & time");
		dispHeaderColumn.setColumnName("transdateTime");

		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setDisplayName("Transaction ID");
		dispHeaderColumn1.setColumnName("transactionID");

		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setDisplayName("Modified on");
		dispHeaderColumn2.setColumnName("modifiedOn");

		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setDisplayName("Transfer category");
		dispHeaderColumn3.setColumnName("transferCategory");

		DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
		dispHeaderColumn4.setDisplayName("Transaction status");
		dispHeaderColumn4.setColumnName("transactionStatus");

		DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
		dispHeaderColumn5.setDisplayName("Distribution type");
		dispHeaderColumn5.setColumnName("distributionType");

		DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
		dispHeaderColumn6.setDisplayName("Product name");
		dispHeaderColumn6.setColumnName("productName");

		DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
		dispHeaderColumn7.setDisplayName("Sender");
		dispHeaderColumn7.setColumnName("senderName");

		DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
		dispHeaderColumn8.setDisplayName("Sender category");
		dispHeaderColumn8.setColumnName("senderCategory");

		DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
		dispHeaderColumn9.setDisplayName("Sender debit quantity");
		dispHeaderColumn9.setColumnName("senderDebitQuantity");

		DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
		dispHeaderColumn10.setDisplayName("Sender mobile number");
		dispHeaderColumn10.setColumnName("senderMsisdn");

		DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
		dispHeaderColumn11.setDisplayName("Sender previous balance");
		dispHeaderColumn11.setColumnName("senderPreviousStock");

		DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
		dispHeaderColumn12.setDisplayName("Sender post balance");
		dispHeaderColumn12.setColumnName("senderPostStock");

		DispHeaderColumn dispHeaderColumn13 = new DispHeaderColumn();
		dispHeaderColumn13.setDisplayName("Receiver");
		dispHeaderColumn13.setColumnName("receiverName");

		DispHeaderColumn dispHeaderColumn14 = new DispHeaderColumn();
		dispHeaderColumn14.setDisplayName("Receiver category");
		dispHeaderColumn14.setColumnName("receiverCategory");

		DispHeaderColumn dispHeaderColumn15 = new DispHeaderColumn();
		dispHeaderColumn15.setDisplayName("Receiver credit quantity");
		dispHeaderColumn15.setColumnName("receiverCreditQuantity");

		DispHeaderColumn dispHeaderColumn16 = new DispHeaderColumn();
		dispHeaderColumn16.setDisplayName("Receiver mobile number");
		dispHeaderColumn16.setColumnName("receiverMsisdn");

		DispHeaderColumn dispHeaderColumn17 = new DispHeaderColumn();
		dispHeaderColumn17.setDisplayName("Receiver previous balance");
		dispHeaderColumn17.setColumnName("receiverPreviousStock");

		DispHeaderColumn dispHeaderColumn18 = new DispHeaderColumn();
		dispHeaderColumn18.setDisplayName("Receiver post balance");
		dispHeaderColumn18.setColumnName("receiverPostStock");

		DispHeaderColumn dispHeaderColumn19 = new DispHeaderColumn();
		dispHeaderColumn19.setDisplayName("Transfer In/Out");
		dispHeaderColumn19.setColumnName("transferInOut");

		DispHeaderColumn dispHeaderColumn20 = new DispHeaderColumn();
		dispHeaderColumn20.setDisplayName("Transfer sub type");
		dispHeaderColumn20.setColumnName("transferSubType");

		DispHeaderColumn dispHeaderColumn21 = new DispHeaderColumn();
		dispHeaderColumn21.setDisplayName("Request source");
		dispHeaderColumn21.setColumnName("requestedSource");

		DispHeaderColumn dispHeaderColumn22 = new DispHeaderColumn();
		dispHeaderColumn22.setDisplayName("Request gateway name");
		dispHeaderColumn22.setColumnName("requestGateway");

		DispHeaderColumn dispHeaderColumn23 = new DispHeaderColumn();
		dispHeaderColumn23.setDisplayName("Requested Quantity");
		dispHeaderColumn23.setColumnName("requestedQuantity");

		DispHeaderColumn dispHeaderColumn24 = new DispHeaderColumn();
		dispHeaderColumn24.setDisplayName("Denomination");
		dispHeaderColumn24.setColumnName("denomination");

		DispHeaderColumn dispHeaderColumn25 = new DispHeaderColumn();
		dispHeaderColumn25.setDisplayName("Commission");
		dispHeaderColumn25.setColumnName("commission");

		DispHeaderColumn dispHeaderColumn26 = new DispHeaderColumn();
		dispHeaderColumn26.setDisplayName("Cumulative base commission");
		dispHeaderColumn26.setColumnName("cumulativeBaseCommission");

		DispHeaderColumn dispHeaderColumn27 = new DispHeaderColumn();
		dispHeaderColumn27.setDisplayName("Tax 1");
		dispHeaderColumn27.setColumnName("tax1");

		DispHeaderColumn dispHeaderColumn28 = new DispHeaderColumn();
		dispHeaderColumn28.setDisplayName("Tax 2");
		dispHeaderColumn28.setColumnName("tax2");

		DispHeaderColumn dispHeaderColumn29 = new DispHeaderColumn();
		dispHeaderColumn29.setDisplayName("Tax 3");
		dispHeaderColumn29.setColumnName("tax3");

		DispHeaderColumn dispHeaderColumn30 = new DispHeaderColumn();
		dispHeaderColumn30.setDisplayName("Payable amount");
		dispHeaderColumn30.setColumnName("payableAmount");

		DispHeaderColumn dispHeaderColumn31 = new DispHeaderColumn();
		dispHeaderColumn31.setDisplayName("Net payable amount");
		dispHeaderColumn31.setColumnName("netPayableAmount");

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

		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDistributionType("ALL");
		data.setTransferCategory("ALL");
		data.setCategoryCode("ALL");
		data.setDomain("DIST");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFileType("CSV");
		data.setFromDate("30/11/21");
		data.setGeography("HARYANA");
		data.setIncludeStaffDetails("false");
		data.setReceiverMobileNumber("");
		data.setReqTab("C2C_ADVANCED_TAB_REQ");
		data.setSenderMobileNumber("");
		data.setToDate("13/12/21");
		data.setTransferInout("ALL"); // can give "IN" or "OUT" also
		data.setTransferSubType("ALL");
		data.setTransferUser("ALL");
		data.setTransferUserCategory("ALL");
		data.setUser("ALL");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
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

	// Verify that API is working with all valid inputs.
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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when distribution type is selected as Stock.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-002")
	public void A_02_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_02_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setDistributionType("STOCK");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		;

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when distribution type is selected as Voucher.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-003")
	public void A_03_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_03_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setDistributionType("VOUCHTRACK");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Transfer.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_04_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setTransferSubType("T");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		;

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Return.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-005")
	public void A_05_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_05_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setTransferSubType("R");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Withdraw.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-006")
	public void A_06_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_06_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setTransferSubType("W");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		;

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Reverse.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-007")
	public void A_07_Test_Negative7_C2CTransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_07_Test_Negative7_C2CTransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setTransferSubType("X");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		;

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify API is not working when fromDate entered is greater that toDate.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-008")
	public void A_08_Test_Negative8_C2CTransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_08_Test_Negative8_C2CTransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setToDate("30/10/21");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		c2CTransferCommissionReportDownloadRequestPojo.getData().setIncludeStaffDetails("");
		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		String msgCode = c2CTransferCommissionReportDownloadResponsePojo.getMessageCode();

		Assert.assertEquals(msgCode, "From Date is greater than to date.");
		Assertion.assertEquals(msgCode, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when included staff details.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-009")
	public void A_09_Test_Success_C2CTransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_09_Test_Success_C2CTransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setIncludeStaffDetails("true");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);

		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is not working when username entered in user is same as in transfer user.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-010")
	public void A_10_Test_Negative10_C2CTransferCommissionReportDownload(String loginID, String password, String msisdn,
			String PIN, String parentName, String categoryName, String categorCode, String externalCode)
			throws Exception {
		final String methodName = "A_10_Test_Negative10_C2CTransferCommissionReportDownload";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCRDOWNLOAD10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportDownloadApi c2CTransferCommissionReportDownloadApi = new C2CTransferCommissionReportDownloadApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setCategoryCode("DIST");
		data.setTransferUserCategory("DIST");
		data.setUser("NGD0000209989");
		data.setTransferUser("NGD0000209989");
		c2CTransferCommissionReportDownloadRequestPojo.setData(data);
		c2CTransferCommissionReportDownloadApi.addBodyParam(c2CTransferCommissionReportDownloadRequestPojo);
		c2CTransferCommissionReportDownloadApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportDownloadApi.perform();
		c2CTransferCommissionReportDownloadResponsePojo = c2CTransferCommissionReportDownloadApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportDownloadResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 400);
		Assertion.assertEquals(Integer.toString(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}