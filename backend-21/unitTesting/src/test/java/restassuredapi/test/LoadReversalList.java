package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.loadReversalList.LoadReversaListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.prepaidrecharge.PrepaidRechargeApi;
import restassuredapi.pojo.baseresponseMultiple.BaseResponseMultiple;
import restassuredapi.pojo.loadreversallistresponsepojo.LoadReversalListResponsePojo;
import restassuredapi.pojo.loadreversallistresquestpojo.LoadReversalListRequestPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.prepaidrechargepojo.PrepaidRechargeDetails;
import restassuredapi.pojo.prepaidrechargepojo.PrepaidRechargeRequestPojo;
import restassuredapi.pojo.prepaidrechargeresponsepojo.PrepaidRechargeResponsePojo;

@ModuleManager(name = Module.LOAD_REVERSAL_LIST)
public class LoadReversalList extends BaseTest{
	
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	LoadReversalListResponsePojo loadReversalListResponse = new LoadReversalListResponsePojo();
	LoadReversalListRequestPojo loadReversalListRequestPojo = new LoadReversalListRequestPojo();
	
	PrepaidRechargeRequestPojo addChannelUserRequestPojo = new PrepaidRechargeRequestPojo();
	PrepaidRechargeResponsePojo addChannelUserResponsePojo = new PrepaidRechargeResponsePojo();
	PrepaidRechargeDetails data = new PrepaidRechargeDetails();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

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
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);


	}
	public void setupData(String pin) {

		String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setAmount(new RandomGeneration().randomNumberWithoutZero(3));
		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
		data.setPin(pin);
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int rownum = 1; rownum <= rowCount; rownum++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {

				data.setSelector(DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
				break;
			}
		}
		data.setDate(_APIUtil.getCurrentTimeStamp());
		addChannelUserRequestPojo.setData(data);
	

	}
	
	
	 // Successful data with valid data.
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-6914")
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LOADREV1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
		
			
			loadReversalListRequestPojo.setSenderMsisdn(msisdn);
			loadReversalListRequestPojo.setReceiverMsisdn("");
			setupData(PIN);
			
			
			PrepaidRechargeApi prepaidRechargeAPI = new PrepaidRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			prepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
			prepaidRechargeAPI.addBodyParam(addChannelUserRequestPojo);
			prepaidRechargeAPI.setExpectedStatusCode(201);
			prepaidRechargeAPI.perform();
			PrepaidRechargeResponsePojo addChannelUserResponsePojo = prepaidRechargeAPI
					.getAPIResponseAsPOJO(PrepaidRechargeResponsePojo.class);
			String txnid = addChannelUserResponsePojo.getDataObject().getTxnid();
				
			
			loadReversalListRequestPojo.setTxnID(txnid);
			LoadReversaListAPI loadReversalListAPI = new LoadReversaListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			loadReversalListAPI.setContentType(_masterVO.getProperty("contentType"));
			loadReversalListAPI.addBodyParam(loadReversalListRequestPojo);
			
			loadReversalListAPI.setExpectedStatusCode(200);
			loadReversalListAPI.perform();
			loadReversalListResponse = loadReversalListAPI
					.getAPIResponseAsPOJO(LoadReversalListResponsePojo.class);

			String status = loadReversalListResponse.getStatus();

			Assert.assertEquals(status, "200");
			Assertion.assertEquals(status, "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-6914")
		public void A_02_Test_invalidTxnID(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
			final String methodName = "A_02_Test_invalidTxnID";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LOADREV2");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			
			loadReversalListRequestPojo.setSenderMsisdn(msisdn);
			
			loadReversalListRequestPojo.setTxnID(new RandomGeneration().randomAlphaNumeric(12));
			loadReversalListRequestPojo.setReceiverMsisdn("");
			LoadReversaListAPI loadReversalListAPI = new LoadReversaListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			loadReversalListAPI.setContentType(_masterVO.getProperty("contentType"));
			loadReversalListAPI.addBodyParam(loadReversalListRequestPojo);
			
			loadReversalListAPI.setExpectedStatusCode(200);
			loadReversalListAPI.perform();
			loadReversalListResponse = loadReversalListAPI
					.getAPIResponseAsPOJO(LoadReversalListResponsePojo.class);

			String status = loadReversalListResponse.getStatus();

			Assert.assertEquals(status, "200");
			Assertion.assertEquals(loadReversalListResponse.getSuccessList().toString(), "[]");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-6914")
		public void A_03_Test_blankTxnID(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
			final String methodName = "A_03_Test_blankTxnID";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LOADREV3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
		
			
			loadReversalListRequestPojo.setSenderMsisdn(msisdn);
			
			
			loadReversalListRequestPojo.setTxnID("");
			loadReversalListRequestPojo.setReceiverMsisdn("");
			
			LoadReversaListAPI loadReversalListAPI = new LoadReversaListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			loadReversalListAPI.setContentType(_masterVO.getProperty("contentType"));
			loadReversalListAPI.addBodyParam(loadReversalListRequestPojo);
			
			loadReversalListAPI.setExpectedStatusCode(400);
			loadReversalListAPI.perform();
			loadReversalListResponse = loadReversalListAPI
					.getAPIResponseAsPOJO(LoadReversalListResponsePojo.class);

			String status = loadReversalListResponse.getStatus();

			Assert.assertEquals(status, "400");
			Assertion.assertEquals(loadReversalListResponse.getMessage(), "Provide either receiver Msisdn or transaction ID to get reversal list.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-6914")
		public void A_04_Test_blankSenderMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
			final String methodName = "A_04_Test_blankSenderMsisdn";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LOADREV4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
		
			
			loadReversalListRequestPojo.setSenderMsisdn("");
			loadReversalListRequestPojo.setReceiverMsisdn("");
			loadReversalListRequestPojo.setTxnID(new RandomGeneration().randomAlphaNumeric(12));
			
			LoadReversaListAPI loadReversalListAPI = new LoadReversaListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			loadReversalListAPI.setContentType(_masterVO.getProperty("contentType"));
			loadReversalListAPI.addBodyParam(loadReversalListRequestPojo);
			
			loadReversalListAPI.setExpectedStatusCode(400);
			loadReversalListAPI.perform();
			loadReversalListResponse = loadReversalListAPI
					.getAPIResponseAsPOJO(LoadReversalListResponsePojo.class);

			String status = loadReversalListResponse.getStatus();

			Assert.assertEquals(status, "400");
			Assertion.assertEquals(loadReversalListResponse.getMessage(), "msisdn2 field is invaild or blank.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
	

}
