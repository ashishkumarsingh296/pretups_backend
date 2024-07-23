package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
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

import restassuredapi.api.c2sUserBalanceApi.C2SUserBalanceApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2sUserBalancerequestpojo.C2SUserBalanceRequestPojo;
import restassuredapi.pojo.c2sUserBalanceresponsepojo.C2SUserBalanceResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.C2S_USER_BALANCE)
public class C2SUserBalanceTest extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     
     Date yesterday = DateUtils.addDays(dateobj, -200);
  	Date todate = DateUtils.addDays(dateobj, -100);
  
  	String yesterdayDate = df.format(yesterday);
    String currentDate=df.format(todate); 
   
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	C2SUserBalanceRequestPojo c2sUserBalanceRequestPojo = new C2SUserBalanceRequestPojo();
	C2SUserBalanceResponsePojo c2sUserBalanceResponsePojo= new C2SUserBalanceResponsePojo();
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

	public void setupData() {
		
		c2sUserBalanceRequestPojo.setFromDate(yesterdayDate);
		c2sUserBalanceRequestPojo.setToDate(currentDate);
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
	@TestManager(TestKey="PRETUPS-0000")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
	{
		final String methodName = "A_01_Test_success";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSERBALANCE1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SUserBalanceApi API=new C2SUserBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		API.setContentType(_masterVO.getProperty("contentType"));
		API.addBodyParam(c2sUserBalanceRequestPojo);
		API.setExpectedStatusCode(200);
		API.perform();
		
		c2sUserBalanceResponsePojo =API.getAPIResponseAsPOJO(C2SUserBalanceResponsePojo.class);
		String Status = c2sUserBalanceResponsePojo.getStatus().toString();
		Assert.assertEquals("200", Status);
		Assertion.assertEquals(Status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-0000")
	public void A_02_Test_Invalid_Token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
	{
		final String methodName = "A_02_Test_Invalid_Token";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSERBALANCE2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SUserBalanceApi API=new C2SUserBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken+new RandomGeneration().randomAlphaNumeric(4));
		API.setContentType(_masterVO.getProperty("contentType"));
		API.addBodyParam(c2sUserBalanceRequestPojo);
		API.setExpectedStatusCode(401);
		API.perform();
		
		c2sUserBalanceResponsePojo =API.getAPIResponseAsPOJO(C2SUserBalanceResponsePojo.class);
		String Status = c2sUserBalanceResponsePojo.getStatus().toString();
		Assert.assertEquals("401", Status);
		Assertion.assertEquals(Status, "401");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-0000")
	public void A_03_Test_Invalid_Date(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName) throws Exception
	{
		final String methodName = "A_03_Test_Invalid_Date";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SUSERBALANCE3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		c2sUserBalanceRequestPojo.setFromDate(new RandomGeneration().randomAlphabets(8));
		c2sUserBalanceRequestPojo.setToDate(yesterdayDate);
		C2SUserBalanceApi API=new C2SUserBalanceApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		API.setContentType(_masterVO.getProperty("contentType"));
		API.addBodyParam(c2sUserBalanceRequestPojo);
		API.setExpectedStatusCode(400);
		API.perform();
		
		c2sUserBalanceResponsePojo =API.getAPIResponseAsPOJO(C2SUserBalanceResponsePojo.class);
		String message =c2sUserBalanceResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid date format.");
		Assertion.assertEquals(message, "Invalid date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
