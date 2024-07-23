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
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.postpaidbillpayment.PostpaidBillPaymentApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.postpaidbillpaymentrequestpojo.PostpaidBillPaymentDetails;
import restassuredapi.pojo.postpaidbillpaymentrequestpojo.PostpaidBillPaymentRequestPojo;
import restassuredapi.pojo.postpaidbillpaymentresponsepojo.PostpaidBillPaymentResponsePojo;
@ModuleManager(name = Module.POSTPAIDBILL_PAYMENT)
public class PostpaidBillPayment extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	PostpaidBillPaymentRequestPojo addChannelUserRequestPojo = new PostpaidBillPaymentRequestPojo();
	PostpaidBillPaymentResponsePojo addChannelUserResponsePojo = new PostpaidBillPaymentResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	PostpaidBillPaymentDetails data = new PostpaidBillPaymentDetails();
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
		String postpaidBillPaymentCode = _masterVO.getProperty("PostpaidBillPaymentCode");
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_POSTPAID_PREFIX);
		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setPin(pin);
		data.setAmount(new RandomGeneration().randomNumberWithoutZero(3));
		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int rownum = 1; rownum <= rowCount; rownum++) {
			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
			if (service.equals(postpaidBillPaymentCode)&& !cardGroupName.isEmpty()) {

				data.setSelector(DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
				break;
			}
		}
		data.setDate(_APIUtil.getCurrentTimeStamp());
		addChannelUserRequestPojo.setData(data);
		

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
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		setupData(PIN);
		
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(201);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String txnid = addChannelUserResponsePojo.getDataObject().getTxnid();
		String status = DBHandler.AccessHandler.getTransactionIDStatus(txnid);
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(401);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String status = addChannelUserResponsePojo.getDataObject().getTXNSTATUS();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_03_Test_invalid_selector(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_selector";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		addChannelUserRequestPojo.getData().setSelector("9");
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(8500);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String errorcode = addChannelUserResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(8500, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "8500");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	public void A_04_Test_invalid_selector_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_selector_length";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		addChannelUserRequestPojo.getData().setSelector(new RandomGeneration().randomNumeric(2));
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(11104);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String errorcode = addChannelUserResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11104, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11104");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	public void A_05_Test_invalid_selector_alphabets(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_selector_alphabets";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		addChannelUserRequestPojo.getData().setSelector(new RandomGeneration().randomAlphabets(2));
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(11102);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String errorcode = addChannelUserResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11102, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11102");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	public void A_06_Test_invalid_amount(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_amount";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		addChannelUserRequestPojo.getData().setAmount(new RandomGeneration().randomAlphabets(2));
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(11109);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String errorcode = addChannelUserResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11109, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11109");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	public void A_07_Test_invalid_amount_zero(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_amount_zero";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPB7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		addChannelUserRequestPojo.getData().setAmount("0");
		PostpaidBillPaymentApi addChannelUserAPI = new PostpaidBillPaymentApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		addChannelUserAPI.setContentType(_masterVO.getProperty("contentType"));
		addChannelUserAPI.addBodyParam(addChannelUserRequestPojo);
		addChannelUserAPI.setExpectedStatusCode(7022);
		addChannelUserAPI.perform();
		addChannelUserResponsePojo = addChannelUserAPI
				.getAPIResponseAsPOJO(PostpaidBillPaymentResponsePojo.class);
		String errorcode = addChannelUserResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(7022, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "7022");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
		}
