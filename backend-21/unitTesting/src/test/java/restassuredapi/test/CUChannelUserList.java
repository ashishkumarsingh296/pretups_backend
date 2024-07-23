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
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.cuChannelUserListApi.CUChannelUserListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.cuchanneluserlistresponsepojo.CUChannelUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.CU_CHANNELUSER_LIST)
public class CUChannelUserList extends BaseTest {
	
	static String moduleCode;
	CUChannelUserListResponsePojo cUChannelUserListResponsePojo = new CUChannelUserListResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Login login = new Login();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][8];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
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


	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
		//if(accessToken==null) {
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
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}


	// Successful data with valid data.
	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-13376")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");


		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode(_masterVO.getProperty("networkCode"));
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String status = cUChannelUserListResponsePojo.getStatus();
		
		if(status.equals("200")) {
		Assert.assertEquals(200, Integer.parseInt(status));
		Assertion.assertEquals(status, "200");
		}
		
		else {
			Assert.assertEquals(204, Integer.parseInt(status));
			Assertion.assertEquals(status, "204");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13377")
	public void A_02_Test_invalid_category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_invalid_category";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode(_masterVO.getProperty("networkCode"));
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		
		Assert.assertEquals(msg, "Invalid category");
		Assertion.assertEquals(msg, "Invalid category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13378")
	public void A_03_Test_invalid_domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_invalid_domain";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setDomain(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setExternalNetworkCode(_masterVO.getProperty("networkCode"));
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "Domain Entered is invalid.");
		Assertion.assertEquals(msg, "Domain Entered is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13379")
	public void A_04_Test_invalid_externalNG(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_invalid_externalNG";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(400);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "External network code {0} is invalid.");
		Assertion.assertEquals(msg, "External network code {0} is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13380")
	public void A_05_Test_invalid_geography(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_invalid_geography";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode(_masterVO.getProperty("networkCode"));
		cUChannelUserListApi.setGeography(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "Invalid geography");
		Assertion.assertEquals(msg, "Invalid geography");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13381")
	public void A_06_Test_invalid_status(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_invalid_status";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode(_masterVO.getProperty("networkCode"));
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "NO Channel User Found With The Given Input.");
		Assertion.assertEquals(msg, "NO Channel User Found With The Given Input.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13382")
	public void A_07_Test_blank_externalNG(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_07_Test_blank_externalNG";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CUCUL7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		CUChannelUserListApi cUChannelUserListApi = new CUChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		cUChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
		cUChannelUserListApi.setCategory(new RandomGeneration().randomAlphabets(5));
		cUChannelUserListApi.setDomain(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExternalNetworkCode("");
		cUChannelUserListApi.setGeography(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setStatus(_masterVO.getProperty("ALL"));
		cUChannelUserListApi.setExpectedStatusCode(200);
		cUChannelUserListApi.perform();
		cUChannelUserListResponsePojo = cUChannelUserListApi
				.getAPIResponseAsPOJO(CUChannelUserListResponsePojo.class);
		
		String msg=cUChannelUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "External network code value is blank.");
		Assertion.assertEquals(msg, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
