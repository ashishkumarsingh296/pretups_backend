package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.fetchStaffUserDetails.FetchStaffUserDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.fetchStaffUserDetailsResponsePojo.FetchStaffUserDetailsResponse;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.FETCH_STAFFUSER_DETAILS)
public class FetchStaffUserDetails extends BaseTest {

	static String moduleCode;
	FetchStaffUserDetailsResponse fetchStaffUserDetailsResponse = new FetchStaffUserDetailsResponse(); // need to edit
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] Data = new Object[rowCount][7];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
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

	protected static String accessToken;

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {

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

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-001")
	public void A_01_Test_Fetch_Staff_User_Details_Positive(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_01_Test_Fetch_Staff_User_Details_Positive";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography1"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabAdv"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(200);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		int statusCode;
		if (fetchStaffUserDetailsResponse != null
				&& Integer.parseInt(fetchStaffUserDetailsResponse.getStatus()) == 200) {
			statusCode = 200;
		} else {
			statusCode = 400;
		}

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-002")
	public void A_02_Test_API_With_Invalid_Category_Code(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_02_Test_API_With_Invalid_Category_Code";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphabets(5);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(random);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabAdv"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid category");
		Assertion.assertEquals(message, "Invalid category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-003")
	public void A_03_Test_API_With_Invalid_LoginId(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_03_Test_API_With_Invalid_LoginId";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphabets(5);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(random);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabAdv"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid login ID");
		Assertion.assertEquals(message, "Invalid login ID");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-004")
	public void A_04_Test_API_With_Invalid_Msisdn(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_04_Test_API_With_Invalid_Msisdn";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphaNumeric(8);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(random);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabMsisdn"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-005")
	public void A_05_Test_API_With_Invalid_DomainCode(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_05_Test_API_With_Invalid_DomainCode";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS05");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphaNumeric(8);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(random);
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabAdv"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Either Invalid domain or empty ");
		Assertion.assertEquals(message, "Either Invalid domain or empty ");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-006")
	public void A_06_Test_API_With_Invalid_Geography_Code(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_06_Test_API_With_Invalid_Geography_Code";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS06");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphaNumeric(8);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(random);
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabAdv"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid geography or empty");
		Assertion.assertEquals(message, "Invalid geography or empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
	
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-007")
	public void A_07_Test_API_With_Invalid_ReqTab(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_07_Test_API_With_Invalid_ReqTab";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS07");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String random = new RandomGeneration().randomAlphaNumeric(8);
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(random);
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid request tab provided.");
		Assertion.assertEquals(message, "Invalid request tab provided.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-008")
	public void A_08_Test_Fetch_StaffUser_Details_Negative(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categoryCode) throws Exception {
		final String methodName = "A_08_Test_Fetch_StaffUser_Details_Negative";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FSTFUSRDTLS08");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		FetchStaffUserDetailsAPI fetchStaffUserDetailsAPI = new FetchStaffUserDetailsAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchStaffUserDetailsAPI.getContentType();
		fetchStaffUserDetailsAPI.setCategoryCode(categoryCode);
		fetchStaffUserDetailsAPI.setChannlUserIDOrMSISDN(loginID);
		fetchStaffUserDetailsAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchStaffUserDetailsAPI.setGeography(_masterVO.getProperty("geography"));
		fetchStaffUserDetailsAPI.setReqTab(_masterVO.getProperty("reqTabMsisdn"));
		fetchStaffUserDetailsAPI.setExpectedStatusCode(400);
		fetchStaffUserDetailsAPI.perform();
		fetchStaffUserDetailsResponse = fetchStaffUserDetailsAPI
				.getAPIResponseAsPOJO(FetchStaffUserDetailsResponse.class);
		String message = null;
		message = fetchStaffUserDetailsResponse.getMessageCode();
		System.out.println(message);
		Assert.assertEquals(message, "Invalid MSISDN");
		Assertion.assertEquals(message, "Invalid MSISDN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
}
}
