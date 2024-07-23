package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
import com.commons.SystemPreferences;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cvouchertxndetailapi.C2CVoucherTxnDetailApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.transactionaldatarequestpojo.TransactionalDataRequestPojo;
import restassuredapi.pojo.transactionaldataresponsepojo.TransactionalDataResponsePojo;

@ModuleManager(name = Module.C2C_VOUCHER_TXN)
public class C2CVoucherTxnDetailTest extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/yy");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -500));
     String toDate = df.format(DateUtils.addDays(new Date(), -300));
     
	static String moduleCode;
	TransactionalDataRequestPojo transactionalDataRequestPojo = new TransactionalDataRequestPojo();
	TransactionalDataResponsePojo transactionalDataResponsePojo = new TransactionalDataResponsePojo();
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
	public void setupData() {
		transactionalDataRequestPojo.setToDate(toDate);
		transactionalDataRequestPojo.setFromDate(fromDate);

	}
	
	    // Successful data with valid data.
		@Test(dataProvider = "userData")
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
			final String methodName = "Test_Positive";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(200);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			int statusCode = transactionalDataResponsePojo.getStatus();

			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		// blank from date
		@Test(dataProvider = "userData")
		public void A_02_Test_blank_fromDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "Test_Negative4_ViewPassBook";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN2");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
					
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			transactionalDataRequestPojo.setFromDate("");
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(400);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			String msg = transactionalDataResponsePojo.getMessageCode();
            
			Assert.assertEquals("Invalid date format.",msg );
			Assertion.assertEquals("Invalid date format.",msg);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);			
		}
		
		// blank to date
		@Test(dataProvider = "userData")
		public void A_03_Test_blank_toDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
			final String methodName = "A_03_Test_blank_toDate";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN3");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
					
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			transactionalDataRequestPojo.setToDate("");
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(400);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			String msg = transactionalDataResponsePojo.getMessageCode();
            
			Assert.assertEquals("Invalid date format.",msg );
			Assertion.assertEquals("Invalid date format.",msg);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);						
		}
		
		@Test(dataProvider = "userData")
		public void A_04_Test_fromDate_GreaterThan_ToDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
			final String methodName = "A_04_Test_fromDate_GreaterThan_ToDate";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN4");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
					
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			transactionalDataRequestPojo.setFromDate(toDate);
			transactionalDataRequestPojo.setToDate(fromDate);
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(400);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			String msg = transactionalDataResponsePojo.getMessageCode();
            
			Assert.assertEquals("From Date is greater than to date.",msg );
			Assertion.assertEquals("From Date is greater than to date.",msg);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);							
		}
		
		@Test(dataProvider = "userData")
		public void A_05_Test_invalid_fromDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
			final String methodName = "A_05_Test_invalid_fromDate";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN5");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
					
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			transactionalDataRequestPojo.setFromDate(new RandomGeneration().randomAlphabets(5));
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(400);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			String msg = transactionalDataResponsePojo.getMessageCode();
            
			Assert.assertEquals("Invalid date format.",msg );
			Assertion.assertEquals("Invalid date format.",msg);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);								
		}
		
		@Test(dataProvider = "userData")
		public void A_06_Test_invalid_toDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
			final String methodName = "A_06_Test_invalid_toDate";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CVTXN6");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
					
			currentNode.assignCategory("REST");
			setupData();
			C2CVoucherTxnDetailApi c2CVoucherTxnDetailApi = new C2CVoucherTxnDetailApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CVoucherTxnDetailApi.setContentType(_masterVO.getProperty("contentType"));
			transactionalDataRequestPojo.setToDate(new RandomGeneration().randomAlphabets(5));
			c2CVoucherTxnDetailApi.addBodyParam(transactionalDataRequestPojo);
			c2CVoucherTxnDetailApi.setExpectedStatusCode(400);
			c2CVoucherTxnDetailApi.perform();
			transactionalDataResponsePojo = c2CVoucherTxnDetailApi
					.getAPIResponseAsPOJO(TransactionalDataResponsePojo.class);
			String msg = transactionalDataResponsePojo.getMessageCode();
            
			Assert.assertEquals("Invalid date format.",msg );
			Assertion.assertEquals("Invalid date format.",msg);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);								
		}
				


}
