package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.totalincome.TotalIncomeApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.totalincomedetailedviewrequestpojo.Data;
import restassuredapi.pojo.totalincomedetailedviewrequestpojo.TotalIncomeDetailedViewRequestPojo;
import restassuredapi.pojo.totalincomedetailedviewresponsepojo.TotalIncomeDetailedViewResponsePojo;

@ModuleManager(name = Module.REST_TOTAL_INCOME_TEST)
public class TotalIncomeDetailedViewTest extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     Date yesterday = DateUtils.addDays(dateobj, -200);
 	
 	String yesterdayDate = df.format(yesterday);
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	TotalIncomeDetailedViewRequestPojo totalIncomeDetailedViewRequestPojo = new TotalIncomeDetailedViewRequestPojo();
	TotalIncomeDetailedViewResponsePojo totalIncomeDetailedViewResponsePojo = new TotalIncomeDetailedViewResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Data data = new Data();
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

	public void setupData() {
		

		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFromDate(yesterdayDate);
		data.setToDate(currentDate);
		totalIncomeDetailedViewRequestPojo.setData(data);

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
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "Test_TotalIncome";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TOTALINCOME1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		TotalIncomeApi totalIncomeApi = new TotalIncomeApi (_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		totalIncomeApi.setContentType(_masterVO.getProperty("contentType"));
		totalIncomeApi.addBodyParam(totalIncomeDetailedViewRequestPojo);
		totalIncomeApi.setExpectedStatusCode(200);
		totalIncomeApi.perform();
		totalIncomeDetailedViewResponsePojo = totalIncomeApi
				.getAPIResponseAsPOJO(TotalIncomeDetailedViewResponsePojo.class);
		int statusCode = Integer.parseInt(totalIncomeDetailedViewResponsePojo.getMessageCode());

		Assert.assertEquals(statusCode, 9020);
		Assertion.assertEquals(Integer.toString(statusCode), "9020");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	public void A_09_Test_InvalidDateTest(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {

		final String methodName = "Test_TotalIncome";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TOTALINCOME9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		data.setFromDate(currentDate);
		data.setToDate(yesterdayDate);
		
		TotalIncomeApi totalIncomeApi = new TotalIncomeApi (_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		totalIncomeApi.setContentType(_masterVO.getProperty("contentType"));
		totalIncomeApi.addBodyParam(totalIncomeDetailedViewRequestPojo);
		totalIncomeApi.setExpectedStatusCode(400);
		totalIncomeApi.perform();
		totalIncomeDetailedViewResponsePojo = totalIncomeApi
				.getAPIResponseAsPOJO(TotalIncomeDetailedViewResponsePojo.class);
		
		String message =totalIncomeDetailedViewResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "From Date is greater than to date.");
		Assertion.assertEquals(message, "From Date is greater than to date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_10_Test_InvalidDateFormatTest(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "Test_TotalIncome";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TOTALINCOME10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		data.setFromDate(new RandomGeneration().randomAlphaNumeric(6));
		
		totalIncomeDetailedViewRequestPojo.setData(data);;
		
		TotalIncomeApi totalIncomeApi = new TotalIncomeApi (_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		totalIncomeApi.setContentType(_masterVO.getProperty("contentType"));
		totalIncomeApi.addBodyParam(totalIncomeDetailedViewRequestPojo);
		totalIncomeApi.setExpectedStatusCode(400);
		totalIncomeApi.perform();
		totalIncomeDetailedViewResponsePojo = totalIncomeApi
				.getAPIResponseAsPOJO(TotalIncomeDetailedViewResponsePojo.class);
		
		String message =totalIncomeDetailedViewResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "Invalid date format.");
		Assertion.assertEquals(message, "Invalid date format.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

}

