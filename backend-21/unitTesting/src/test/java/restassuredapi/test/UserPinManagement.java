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
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.userpinmanagement.UserPinManagementAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userpinmanagementrequestpojo.UserPinManagementRequestPojo;
import restassuredapi.pojo.userpinmanagementresponsepojo.UserPinManagementResponsePojo;

@ModuleManager(name = Module.USER_PIN_MGMT)
public class UserPinManagement extends BaseTest {
	static String moduleCode;
	
	UserPinManagementResponsePojo userPinManagementResponsePojo = new UserPinManagementResponsePojo();
	UserPinManagementRequestPojo userPinManagementRequestPojo = new UserPinManagementRequestPojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() -4;

		Object[][] Data = new Object[rowCount][7];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
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
	public void setupData(String data1) {
		userPinManagementRequestPojo.setRemarks(_masterVO.getProperty("Remarks"));
		userPinManagementRequestPojo.setMsisdn(data1);

	}
	@Test(dataProvider= "userData")
	public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_01_Test_Success_LoginId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);
		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		userPinManagementAPI.setResetPin(_masterVO.getProperty("status_Y"));

		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);
		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();
			
		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		boolean statusCode = false;
		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessage() != null && userPinManagementResponsePojo.getMessage().contains("Success")) {
			statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", userPinManagementResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_02_Test_Success_Msisdn_ResetPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_02_Test_Success_Msisdn_ResetPin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		
		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		userPinManagementAPI.setResetPin(_masterVO.getProperty("status_Y"));
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);

		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();

		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;

		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessage() != null && userPinManagementResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", userPinManagementResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_03_Test_Success_Msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_03_Test_Success_Msisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		
		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		userPinManagementAPI.setResetPin(_masterVO.getProperty("setN"));
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);

		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();

		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;

		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessage() != null && userPinManagementResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", userPinManagementResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_04_Test_NetworkCode_Invalid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_04_Test_NetworkCode_Invalid";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		
		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
	
		userPinManagementAPI.setResetPin(_masterVO.getProperty("setN"));
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);

		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();

		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;

		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessage() != null && userPinManagementResponsePojo.getMessage().contains("External network code N is invalid.")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		//Assertion.assertEquals("External network code "+ oAuthenticationRequestPojo.get() +" is invalid.", userPinManagementResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_05_Test_IdentifierType_Invalid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_05_Test_IdentifierType_Invalid";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		userPinManagementAPI.setResetPin(_masterVO.getProperty("setN"));
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);
		
		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();

		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;
		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessageCode() != null && userPinManagementResponsePojo.getMessageCode().contains("7617")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("IdentifierType is blank.", userPinManagementResponsePojo.getMessage());

		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_06_Test_EmptyRemarks(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_06_Test_EmptyRemarks";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
	
		userPinManagementAPI.setResetPin(_masterVO.getProperty("setN"));
		userPinManagementRequestPojo.setRemarks("");
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);
		
		
		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();
		
		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;
		if (userPinManagementResponsePojo != null && userPinManagementResponsePojo.getMessage() != null && userPinManagementResponsePojo.getMessageCode().contains("3031")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Remarks Required", userPinManagementResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_07_Test_InvalidUser(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {

		final String methodName = "A_07_Test_InvalidUser";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPINMANAGEMENT7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn);

		UserPinManagementAPI userPinManagementAPI = new UserPinManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPinManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		userPinManagementAPI.setResetPin(_masterVO.getProperty("setN"));
		userPinManagementRequestPojo.setMsisdn("");
		userPinManagementAPI.addBodyParam(userPinManagementRequestPojo);
	
		userPinManagementAPI.setExpectedStatusCode(200);
		userPinManagementAPI.perform();

		userPinManagementResponsePojo = userPinManagementAPI.getAPIResponseAsPOJO(UserPinManagementResponsePojo.class);

		Boolean statusCode = false;
		if (userPinManagementResponsePojo != null
				&& userPinManagementResponsePojo.getMessageCode() != null
				&& userPinManagementResponsePojo.getMessageCode().contains(
						"7618")) 
					statusCode = true;
		else
			statusCode = false;

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Invalid user.", userPinManagementResponsePojo.getMessage());
		Log.endTestCase(methodName);
	}
}
