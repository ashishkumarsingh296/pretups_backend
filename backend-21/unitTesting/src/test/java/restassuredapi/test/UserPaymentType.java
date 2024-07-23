package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
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
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.userpaymenttype.UserPaymentAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userpaymentrequestpojo.Data;
import restassuredapi.pojo.userpaymentrequestpojo.UserPaymentRequestpojo;
import restassuredapi.pojo.userpaymentresponsepojo.UserPaymentResponsepojo;

@ModuleManager(name = Module.REST_USR_PAYMENT_TYPE)
public class UserPaymentType extends BaseTest{
	
	static String moduleCode;
	
	UserPaymentRequestpojo userPaymentRequestpojo = new UserPaymentRequestpojo();
	UserPaymentResponsepojo userPaymentResponsepojo = new UserPaymentResponsepojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();



		Object[][] Data = new Object[rowCount][7];
		int j  = 0;
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
		headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
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


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception
	{
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));

		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1,data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
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

	public void setupData() {
		data.setExtcode("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		data.setLanguage2(_masterVO.getProperty("languageCode0"));
		data.setLoginid("");
		data.setMsisdn("");
		data.setPassword("");
		data.setPin("");
		data.setType(_masterVO.getProperty("C2CType"));
		userPaymentRequestpojo.setData(data);
	}
	
	public void setupDataLoginId(String data1,String data2,String data3) {

		setupData();

		data.setLoginid(data1);  //login
		data.setMsisdn("");
		data.setPassword(data2);
		data.setPin(data3);
		data.setType(_masterVO.getProperty("C2CType"));
		userPaymentRequestpojo.setData(data);

	}
	public void setupDataMsisdn(String data1,String data2) {
		setupData();
		data.setLoginid("");
		data.setMsisdn(data1);
		data.setPassword("");
		data.setPin(data2);
		data.setType(_masterVO.getProperty("C2CType"));
		userPaymentRequestpojo.setData(data);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-5893")
	public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_UserPaymentAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRPAYMENT1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupDataLoginId(loginID,password,PIN);

		UserPaymentAPI userPaymentAPI = new UserPaymentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		userPaymentAPI.setContentType(_masterVO.getProperty("contentType"));
		userPaymentAPI.addBodyParam(userPaymentRequestpojo);
		userPaymentAPI.setExpectedStatusCode(200);
		userPaymentAPI.perform();

		userPaymentResponsepojo = userPaymentAPI.getAPIResponseAsPOJO(UserPaymentResponsepojo.class);

		Boolean statusCode = false;


		if (userPaymentResponsepojo != null && userPaymentResponsepojo.getDataObject().getMessage() != null && userPaymentResponsepojo.getDataObject().getMessage().contains("Enquiry is successful.")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Enquiry is successful.", userPaymentResponsepojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	
	}
	
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6442")
	public void A_02_Test_Success_Msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_UserPaymentAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRPAYMENT2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupDataMsisdn(msisdn,PIN);

		UserPaymentAPI userPaymentAPI = new UserPaymentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		userPaymentAPI.setContentType(_masterVO.getProperty("contentType"));
		userPaymentAPI.addBodyParam(userPaymentRequestpojo);
		userPaymentAPI.setExpectedStatusCode(200);
		userPaymentAPI.perform();

		userPaymentResponsepojo = userPaymentAPI.getAPIResponseAsPOJO(UserPaymentResponsepojo.class);

		Boolean statusCode = false;


		if (userPaymentResponsepojo != null && userPaymentResponsepojo.getDataObject().getMessage() != null && userPaymentResponsepojo.getDataObject().getMessage().contains("Enquiry is successful.")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Enquiry is successful.", userPaymentResponsepojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6443")
	public void A_03_Test_BlankLoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_UserPaymentAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRPAYMENT3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		data.setLoginid("");
		userPaymentRequestpojo.setData(data);

		UserPaymentAPI userPaymentAPI = new UserPaymentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		userPaymentAPI.setContentType(_masterVO.getProperty("contentType"));
		userPaymentAPI.addBodyParam(userPaymentRequestpojo);
		userPaymentAPI.setExpectedStatusCode(400);
		userPaymentAPI.perform();
		userPaymentResponsepojo = userPaymentAPI
				.getAPIResponseAsPOJO(UserPaymentResponsepojo.class);
		

		String message =userPaymentResponsepojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6444")
	public void A_04_Test_BlankExtnwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_UserPaymentAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRPAYMENT4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		data.setExtnwcode("");
		userPaymentRequestpojo.setData(data);

		UserPaymentAPI userPaymentAPI = new UserPaymentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		userPaymentAPI.setContentType(_masterVO.getProperty("contentType"));
		userPaymentAPI.addBodyParam(userPaymentRequestpojo);
		userPaymentAPI.setExpectedStatusCode(400);
		userPaymentAPI.perform();
		userPaymentResponsepojo = userPaymentAPI
				.getAPIResponseAsPOJO(UserPaymentResponsepojo.class);
		
		String message =userPaymentResponsepojo.getDataObject().getMessage();
		
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	

	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6449")
	public void A_09_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_UserPaymentAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRPAYMENT9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupDataLoginId(loginID,password,PIN);

		UserPaymentAPI userPaymentAPI = new UserPaymentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		userPaymentAPI.setContentType(_masterVO.getProperty("contentType"));
		userPaymentAPI.addBodyParam(userPaymentRequestpojo);
		userPaymentAPI.setExpectedStatusCode(200);
		userPaymentAPI.perform();

		userPaymentResponsepojo = userPaymentAPI.getAPIResponseAsPOJO(UserPaymentResponsepojo.class);

		Boolean statusCode = false;


		if (userPaymentResponsepojo != null && userPaymentResponsepojo.getDataObject().getMessage() != null && userPaymentResponsepojo.getDataObject().getMessage().contains("Enquiry is successful.")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Enquiry is successful.", userPaymentResponsepojo.getDataObject().getMessage());
		
		Log.endTestCase(methodName);

	
	}
	
}
