package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import restassuredapi.api.internetrecharge.InternetRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeDetails;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;
import restassuredapi.pojo.internetrechargeresponsepojo.InternetRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.INTERNET_RECHARGE)
public class InternetRecharge extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	 InternetRechargeRequestPojo internetRechargeRequestPojo = new InternetRechargeRequestPojo();
	 InternetRechargeResponsePojo internetRechargeResponsePojo = new InternetRechargeResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	

	InternetRechargeDetails data = new InternetRechargeDetails();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
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


	public void setupData(String pin) {

		String CustomerRechargeCode = _masterVO.getProperty("InternetRechargeCode");
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setAmount(new RandomGeneration().randomNumberWithoutZero(3));
		data.setPin(pin);
		data.setNotifMsisdn(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
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
		internetRechargeRequestPojo.setData(data);
		

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
	@TestManager(TestKey="PRETUPS-6657")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);

		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(201);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String txnid = internetRechargeResponsePojo.getDataObject().getTxnid();
		String status = DBHandler.AccessHandler.getTransactionIDStatus(txnid);
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6658")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);


		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(401);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String status = internetRechargeResponsePojo.getDataObject().getTXNSTATUS();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6659")
	public void A_03_Test_invalid_selector_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_invalid_selector_length";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);

		internetRechargeRequestPojo.getData().setSelector(new RandomGeneration().randomNumeric(2));
		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(11104);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String errorcode = internetRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11104, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11104");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6660")
	public void A_04_Test_invalid_selector_alphabets(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_invalid_selector_alphabets";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);

		internetRechargeRequestPojo.getData().setSelector(new RandomGeneration().randomAlphabets(2));
		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(11102);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String errorcode = internetRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11102, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11102");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6661")
	public void A_05_Test_invalid_amount(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_invalid_amount";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);

		internetRechargeRequestPojo.getData().setAmount(new RandomGeneration().randomAlphabets(2));
		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(11109);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String errorcode = internetRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11109, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11109");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6662")
	public void A_06_Test_invalid_msisdn_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_invalid_selector_length";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("INTRRC6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);

		internetRechargeRequestPojo.getData().setMsisdn2(new RandomGeneration().randomNumeric(3));
		InternetRechargeApi internetRechargeApi = new InternetRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		internetRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		internetRechargeApi.addBodyParam(internetRechargeRequestPojo);
		internetRechargeApi.setExpectedStatusCode(11101);
		internetRechargeApi.perform();
		internetRechargeResponsePojo = internetRechargeApi
				.getAPIResponseAsPOJO(InternetRechargeResponsePojo.class);
		String errorcode = internetRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11101, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11101");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
		}
