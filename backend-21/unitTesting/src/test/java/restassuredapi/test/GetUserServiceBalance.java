package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.getchanneluserservicebalance.GetUserServiceBalanceApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.getuserservicebalancepojo.GetUserserviceBalanceResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_USER_SERVICE_BALANCE)
public class GetUserServiceBalance  extends BaseTest {

	static String moduleCode;
	GetUserserviceBalanceResponsePojo getUserserviceBalanceResponsePojo = new GetUserserviceBalanceResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][6];
		int j  = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
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
			@TestManager(TestKey="PRETUPS-9892")
			public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
			{
				final String methodName = "A_01_Test_success";
		        Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSRBALSVC1");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
				GetUserServiceBalanceApi API=new GetUserServiceBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				API.setContentType(_masterVO.getProperty("contentType"));
				API.setServicecode(_masterVO.getProperty("scope"));
				API.setExpectedStatusCode(200);
				API.perform();
				
				getUserserviceBalanceResponsePojo =API.getAPIResponseAsPOJO(GetUserserviceBalanceResponsePojo.class);
				String messageCode = getUserserviceBalanceResponsePojo.getStatus();
				Assert.assertEquals(200, Integer.parseInt(messageCode));
				Assertion.assertEquals(messageCode, "200");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
			@Test(dataProvider = "userData")
			@TestManager(TestKey="PRETUPS-9893")
			public void A_02_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
			{
				final String methodName = "A_02_Test_success";
		        Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSRBALSVC2");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
				

				GetUserServiceBalanceApi API=new GetUserServiceBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				API.setContentType(_masterVO.getProperty("contentType"));
				API.setServicecode(_masterVO.getProperty("GiftRechargeCode"));
				API.setExpectedStatusCode(200);
				API.perform();
				
				getUserserviceBalanceResponsePojo =API.getAPIResponseAsPOJO(GetUserserviceBalanceResponsePojo.class);
				String messageCode = getUserserviceBalanceResponsePojo.getStatus();
				Assert.assertEquals(200, Integer.parseInt(messageCode));
				Assertion.assertEquals(messageCode, "200");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}
			 //Negative case: data with Invalid service code. Provided RC is assigned to user 
			@Test(dataProvider = "userData")
			@TestManager(TestKey="PRETUPS-9896")
			public void A_03_Test_InvalidServiceCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
			{
				final String methodName = "A_03_Test_InvalidServiceCode";
		        Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSRBALSVC3");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
				
				
				
				GetUserServiceBalanceApi API=new GetUserServiceBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				API.setContentType(_masterVO.getProperty("contentType"));
				API.setServicecode(new RandomGeneration().randomAlphaNumeric(3));
				API.setExpectedStatusCode(400);
				API.perform();
				
				getUserserviceBalanceResponsePojo =API.getAPIResponseAsPOJO(GetUserserviceBalanceResponsePojo.class);
				String messageCode = getUserserviceBalanceResponsePojo.getStatus();
				Assert.assertEquals(400, Integer.parseInt(messageCode));
				Assertion.assertEquals(messageCode, "400");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
			}

	
}
