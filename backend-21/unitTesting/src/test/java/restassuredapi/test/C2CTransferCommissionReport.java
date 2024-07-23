package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
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

import restassuredapi.api.c2CTransferCommissionReport.C2CTransferCommissionReportApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2Ctransfercommissionreportrequestpojo.C2CTransferCommissionReportRequestPojo;
import restassuredapi.pojo.c2Ctransfercommissionreportrequestpojo.Data;
import restassuredapi.pojo.c2Ctransfercommissionreportresponsepojo.C2CTransferCommissionReportResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2C_TRANSFER_COMMISSION_REPORT)
public class C2CTransferCommissionReport extends BaseTest {
	/*
	 * DateFormat df = new SimpleDateFormat("dd/MM/YYYY"); Date dateobj = new
	 * Date(); String currentDate=df.format(dateobj);
	 * 
	 * String fromDate = df.format(DateUtils.addDays(new Date(), -120)); String
	 * toDate = df.format(DateUtils.addDays(new Date(), -1));
	 */

	static String moduleCode;
	C2CTransferCommissionReportRequestPojo c2CTransferCommissionReportRequestPojo = new C2CTransferCommissionReportRequestPojo();
	C2CTransferCommissionReportResponsePojo c2CTransferCommissionReportResponsePojo = new C2CTransferCommissionReportResponsePojo();
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

		data.setDistributionType("ALL");
		data.setTransferCategory("ALL");
		data.setCategoryCode("");
		data.setDomain("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFromDate("01/12/21");
		data.setGeography("");
		data.setIncludeStaffDetails("false");
		data.setReceiverMobileNumber("");
		data.setSenderMobileNumber("");
		data.setToDate("13/12/21");
		data.setTransferInout("ALL");
		data.setTransferSubType("ALL");
		data.setTransferUser("");
		data.setTransferUserCategory("");
		data.setUser("");
		data.setReqTab("C2C_MOBILENUMB_TAB_REQ");
		c2CTransferCommissionReportRequestPojo.setData(data);
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
	public void A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setDistributionType("STOCK");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setDistributionType("VOUCHTRACK");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Transfer.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_04_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setTransferSubType("T");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setTransferSubType("R");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Withdraw.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-006")
	public void A_06_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_06_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setTransferSubType("W");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when transfer sub type is selected as Reverse.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-007")
	public void A_07_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_07_Test_Success";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setTransferSubType("X");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify API is not working when fromDate entered is greater that toDate.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-008")
	public void A_08_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_08_Test_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setToDate("30/10/21");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		String msgCode = c2CTransferCommissionReportResponsePojo.getMessageCode();

		Assert.assertEquals(msgCode, "From Date is greater than to date.");
		Assertion.assertEquals(msgCode, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is not working when sender msisdn entered is invalid.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-009")
	public void A_09_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_09_Test_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber("1243%%#$!@");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		String msgCode = c2CTransferCommissionReportResponsePojo.getMessageCode();

		Assert.assertEquals(msgCode, "Invalid sender mobile number or empty");
		Assertion.assertEquals(msgCode, "Invalid sender mobile number or empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is not working when sender msisdn entered is blank.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-010")
	public void A_10_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_10_Test_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber("");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		String msgCode = c2CTransferCommissionReportResponsePojo.getMessageCode();

		Assert.assertEquals(msgCode, "Invalid sender mobile number or empty");
		Assertion.assertEquals(msgCode, "Invalid sender mobile number or empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is not working when receiver msisdn entered is invalid.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-011")
	public void A_11_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_11_Test_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT11");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setReceiverMobileNumber("32$$!@D12");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(400);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		String msgCode = c2CTransferCommissionReportResponsePojo.getMessageCode();

		Assert.assertEquals(msgCode, "Invalid receiver mobile number");
		Assertion.assertEquals(msgCode, "Invalid receiver mobile number");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// Verify that API is working when included staff details.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-012")
	public void A_12_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String externalCode) throws Exception {
		final String methodName = "A_12_Test_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT12");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setSenderMobileNumber(msisdn);
		data.setIncludeStaffDetails("true");
		c2CTransferCommissionReportRequestPojo.setData(data);
		c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
		c2CTransferCommissionReportApi.setExpectedStatusCode(200);
		c2CTransferCommissionReportApi.perform();
		c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
				.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
		int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Verify that API is working when entered sender and receiver msisdn.
		@Test(dataProvider = "userData")
		@TestManager(TestKey = "PRETUPS-013")
		public void A_13_Test_Negative(String loginID, String password, String msisdn, String PIN, String parentName,
				String categoryName, String categorCode, String externalCode) throws Exception {
			final String methodName = "A_13_Test_Negative";
			Log.startTestCase(methodName);
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password, categoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN, categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CTCREPORT13");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
			currentNode.assignCategory("REST");
			setupData();

			C2CTransferCommissionReportApi c2CTransferCommissionReportApi = new C2CTransferCommissionReportApi(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2CTransferCommissionReportApi.setContentType(_masterVO.getProperty("contentType"));
			data.setSenderMobileNumber(msisdn);
			data.setReceiverMobileNumber("720091023");
			c2CTransferCommissionReportRequestPojo.setData(data);
			c2CTransferCommissionReportApi.addBodyParam(c2CTransferCommissionReportRequestPojo);
			c2CTransferCommissionReportApi.setExpectedStatusCode(200);
			c2CTransferCommissionReportApi.perform();
			c2CTransferCommissionReportResponsePojo = c2CTransferCommissionReportApi
					.getAPIResponseAsPOJO(C2CTransferCommissionReportResponsePojo.class);
			int statusCode = Integer.parseInt(c2CTransferCommissionReportResponsePojo.getStatus());

			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
}