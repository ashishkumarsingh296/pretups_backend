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

import restassuredapi.api.fixedrecharge.FixedRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.fixedrechargerequestpojo.FixedRechargeDetails;
import restassuredapi.pojo.fixedrechargerequestpojo.FixedRechargeRequestPojo;
import restassuredapi.pojo.fixedrechargeresponsepojo.FixedRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.FIXED_RECHARGE)
public class FixedRecharge extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	 FixedRechargeRequestPojo fixedRechargeRequestPojo = new FixedRechargeRequestPojo();
	 FixedRechargeResponsePojo fixedRechargeResponsePojo = new FixedRechargeResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	public FixedRechargeDetails data = new FixedRechargeDetails();
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
		String postpaidBillPaymentCode = _masterVO.getProperty("FixLineRechargeCode");
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setAmount(new RandomGeneration().randomNumberWithoutZero(3));
		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
		data.setPin(pin);
		data.setNotifMsisdn(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
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
		fixedRechargeRequestPojo.setData(data);

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
	@TestManager(TestKey="PRETUPS-6671")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(201);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String txnid = fixedRechargeResponsePojo.getDataObject().getTxnid();
		String status = DBHandler.AccessHandler.getTransactionIDStatus(txnid);
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6672")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(401);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String status = fixedRechargeResponsePojo.getDataObject().getTXNSTATUS();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6676")
	public void A_03_Test_invalid_msisdn_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_invalid_msisdn_length";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		fixedRechargeRequestPojo.getData().setMsisdn2(new RandomGeneration().randomNumeric(3));
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(11104);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String errorcode = fixedRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11101, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11101");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6673")
	public void A_04_Test_invalid_selector_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_invalid_selector_length";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		fixedRechargeRequestPojo.getData().setSelector(new RandomGeneration().randomNumeric(2));
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(11104);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String errorcode = fixedRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11104, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11104");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6674")
	public void A_05_Test_invalid_selector_alphabets(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_invalid_selector_alphabets";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		fixedRechargeRequestPojo.getData().setSelector(new RandomGeneration().randomAlphabets(2));
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(11102);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String errorcode = fixedRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11102, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11102");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6675")
	public void A_06_Test_invalid_amount(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_invalid_amount";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FIXEDRC6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN);
		fixedRechargeRequestPojo.getData().setAmount(new RandomGeneration().randomAlphabets(2));
		FixedRechargeApi fixedRechargeApi = new FixedRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fixedRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		fixedRechargeApi.addBodyParam(fixedRechargeRequestPojo);
		fixedRechargeApi.setExpectedStatusCode(11109);
		fixedRechargeApi.perform();
		fixedRechargeResponsePojo = fixedRechargeApi
				.getAPIResponseAsPOJO(FixedRechargeResponsePojo.class);
		String errorcode = fixedRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11109, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11109");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

}
