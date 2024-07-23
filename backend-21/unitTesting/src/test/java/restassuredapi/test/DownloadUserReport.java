package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.downloaduserreportapi.DownloadUserReportApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.downloaduserlistrequestpojo.DownloadUserListRequestPojo;
import restassuredapi.pojo.downloaduserlistresponsepojo.DownloadUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.DWNLD_USER_REPORT)
public class DownloadUserReport extends BaseTest {
	
	static String moduleCode;
	ArrayList<DownloadUserListRequestPojo> result = new ArrayList<DownloadUserListRequestPojo>();
	DownloadUserListResponsePojo downloadUserListResponsePojo = new DownloadUserListResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
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
	
	public void setupData() {
		ArrayList<DownloadUserListRequestPojo> result = new ArrayList<DownloadUserListRequestPojo>();
		
		DownloadUserListRequestPojo downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("MOBILE_NUMBER"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("MOBILE_NUMBER"));
		
		result.add(downloadUserListRequestPojo);
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("BALANCE"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("BALANCE"));
		
		result.add(downloadUserListRequestPojo);
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("STATUS"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("STATUS"));
		
		result.add(downloadUserListRequestPojo);
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("DOMAIN"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("DOMAIN"));
		
		result.add(downloadUserListRequestPojo);
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("CATEGORY"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("CATEGORY"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("PARENT_NAME"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("PARENT_NAME"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("GEOGRAPHY"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("GEOGRAPHY"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("LOGINID"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("LOGINID"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("CONTACT_PERSON_NAME"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("CONTACT_PERSON_NAME"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("GRADE"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("GRADE"));
		
		result.add(downloadUserListRequestPojo);
		
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("REGISTERED_DATE_TIME"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("REGISTERED_DATE_TIME"));
		
		result.add(downloadUserListRequestPojo);
		
		
		downloadUserListRequestPojo = new DownloadUserListRequestPojo();
		downloadUserListRequestPojo.setColumnName(_masterVO.getProperty("LAST_MODIFIED_ON"));
		downloadUserListRequestPojo.setDisplayName(_masterVO.getProperty("LAST_MODIFIED_ON"));
		
		result.add(downloadUserListRequestPojo);
	}


	// Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setDomain(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setNetworkCode(_masterVO.getProperty("networkCode"));
		downloadUserReportApi.setGeography(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setStatus(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String status = downloadUserListResponsePojo.getStatus();
		
		if(status.equals("200")) {
			Assert.assertEquals(200, Integer.parseInt(status));
			Assertion.assertNotNull(status);
		}
		else {
			Assert.assertEquals(204, Integer.parseInt(status));
			Assertion.assertEquals(status, "204");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_02_Test_invalid_category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_invalid_category";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		setupData();
		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(new RandomGeneration().randomAlphabets(5));
		downloadUserReportApi.setDomain(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setNetworkCode(_masterVO.getProperty("networkCode"));
		downloadUserReportApi.setGeography(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setStatus(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String msg=downloadUserListResponsePojo.getMessage();
		
		Assert.assertEquals(msg, "Invalid category");
		Assertion.assertEquals(msg, "Invalid category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-13378")
	public void A_03_Test_invalid_domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_invalid_domain";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setDomain(new RandomGeneration().randomAlphabets(5));
		downloadUserReportApi.setNetworkCode(_masterVO.getProperty("networkCode"));
		downloadUserReportApi.setGeography(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setStatus(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String msg=downloadUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "Domain Entered is invalid.");
		Assertion.assertEquals(msg, "Domain Entered is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	

	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-13380")
	public void A_05_Test_invalid_geography(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_invalid_geography";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setDomain(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setNetworkCode(_masterVO.getProperty("networkCode"));
		downloadUserReportApi.setGeography(new RandomGeneration().randomAlphabets(5));
		downloadUserReportApi.setStatus(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String msg=downloadUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "Invalid geography");
		Assertion.assertEquals(msg, "Invalid geography");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-13381")
	public void A_06_Test_invalid_status(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_invalid_status";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		setupData();
		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setDomain(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setNetworkCode(_masterVO.getProperty("networkCode"));
		downloadUserReportApi.setGeography(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setStatus(new RandomGeneration().randomAlphabets(5));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String msg=downloadUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "NO Channel User Found With The Given Input.");
		Assertion.assertEquals(msg, "NO Channel User Found With The Given Input.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-13382")
	public void A_07_Test_blank_externalNG(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_07_Test_blank_externalNG";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DUR7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		DownloadUserReportApi downloadUserReportApi = new DownloadUserReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		downloadUserReportApi.setContentType(_masterVO.getProperty("contentType"));
		downloadUserReportApi.addBodyParam(result);
		downloadUserReportApi.setCategory(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setDomain(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setNetworkCode("");
		downloadUserReportApi.setGeography(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setStatus(_masterVO.getProperty("ALL"));
		downloadUserReportApi.setExpectedStatusCode(200);
		downloadUserReportApi.perform();
		downloadUserListResponsePojo = downloadUserReportApi
				.getAPIResponseAsPOJO(DownloadUserListResponsePojo.class);
		
		String msg=downloadUserListResponsePojo.getMessage();
		Assert.assertEquals(msg, "External network code value is blank.");
		Assertion.assertEquals(msg, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
