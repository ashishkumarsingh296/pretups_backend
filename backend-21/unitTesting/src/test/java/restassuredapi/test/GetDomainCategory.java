package restassuredapi.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.utils.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.constants.Module;

import restassuredapi.api.getdomaincategory.GetDomainCategoryAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.getdomaincategoryrequestpojo.Data;
import restassuredapi.pojo.getdomaincategoryrequestpojo.GetDomainCategoryRequestPojo;
import restassuredapi.pojo.getdomaincategoryresponsepojo.GetDomainCategoryResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_GET_DOMAIN_CATEGORY)
public class GetDomainCategory extends BaseTest {


	static String moduleCode;
	GetDomainCategoryRequestPojo getDomainCategoryRequestPojo = new GetDomainCategoryRequestPojo();
	GetDomainCategoryResponsePojo getDomainCategoryResponsePojo = new GetDomainCategoryResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	HashMap<String,String> transfer_Details=new HashMap<String,String>();
	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();


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

	public void setupData(String logn,String pwd,String msisdn,String pin)
	{
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid(logn);
		data.setPassword(pwd);
		data.setMsisdn(msisdn);
		data.setPin(pin);
		data.setExtcode("");
		data.setMsisdn2("");
		getDomainCategoryRequestPojo.setData(data);

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

	// Successful data with valid data.

	protected static String accessToken;


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));

		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI
				.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		org.testng.Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-4285")
	public void A_01_Test_getDomainCategory_positive(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception
	{
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(200);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		int statusCode = Integer.parseInt(getDomainCategoryResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	

	@Test
	@TestManager(TestKey="PRETUPS-6420")
	public void A_02_Test_BlankLoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid("");
		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	

	@Test
	@TestManager(TestKey="PRETUPS-4289")
	public void A_03_Test_invalidExtCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode(new RandomGeneration().randomNumberWithoutZero(5));
		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "EXTCODE is not valid.");
		Assertion.assertEquals(message, "EXTCODE is not valid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	
	@Test
	@TestManager(TestKey="PRETUPS-6421")
	public void A_04_Test_BlankPassword(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setPassword("");
		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "PASSWORD can not be blank.");
		Assertion.assertEquals(message, "PASSWORD can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-4286")
	public void A_05_Test_BlankExtNwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setExtnwcode("");
		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	

	@Test
	@TestManager(TestKey="PRETUPS-4291")
	public void A_06_Test_InvalidMsisdn2(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		String msg=new RandomGeneration().randomNumberWithoutZero(12);
		data.setMsisdn2(msg);

		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "No Domain Code Found for "+msg+".");
		Assertion.assertEquals(message, "No Domain Code Found for "+msg+".");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-4290")
	public void A_07_Test_InvalidPassword(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_GetDomainCategoryAPI";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDC7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn,PIN);
		GetDomainCategoryAPI getDomainCategoryAPI = new GetDomainCategoryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setPassword(new RandomGeneration().randomAlphabets(7));
		getDomainCategoryRequestPojo.setData(data);
		getDomainCategoryAPI.addBodyParam(getDomainCategoryRequestPojo);
		getDomainCategoryAPI.setExpectedStatusCode(400);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(GetDomainCategoryResponsePojo.class);
		String message =getDomainCategoryResponsePojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "No such user exists, password is invalid.");
		Assertion.assertEquals(message, "No such user exists, password is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

}
