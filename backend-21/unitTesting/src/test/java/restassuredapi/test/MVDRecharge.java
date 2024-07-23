package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import restassuredapi.api.mvdrecharge.MVDRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.mvdrechargerequestpojo.MVDRechargeDetails;
import restassuredapi.pojo.mvdrechargerequestpojo.MVDRechargeRequestPojo;
import restassuredapi.pojo.mvdrechargeresponse.MVDRechargeResponsePojo;
import restassuredapi.pojo.mvdrechargeresponse.MVDRechargeResponsePojo.TxnIdObject;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.MVD_RECHARGE)
public class MVDRecharge extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	MVDRechargeRequestPojo mvdRechargeRequestPojo = new MVDRechargeRequestPojo();
	MVDRechargeResponsePojo mVDRechargeResponsePojo = new MVDRechargeResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	

	MVDRechargeDetails data = new MVDRechargeDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed1() {
		String CustomerRechargeCode = _masterVO.getProperty("MVDRechargeCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which Customer Recharge is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		
		ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.VOMS_DENOM_PROFILE);
		int evdRow = ExcelUtility.getRowCount();
		ArrayList<String> mvdDenomination = new ArrayList<>();
		for(int i=1;i<=evdRow;i++) {
			if(ExcelUtility.getCellData(0, ExcelI.VOMS_SERVICE,i).equals(CustomerRechargeCode)) {
				mvdDenomination.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP,i));
			}
		}
		/*
		 * Counter to count number of users exists in channel users hierarchy sheet
		 * of Categories for which O2C transfer is allowed
		 */
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}

		
		/*
		 * Store required data of 'O2C transfer allowed category' users in Object
		 */
		Object[][] Data = new Object[userCounter][9];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				Data[j][7] = CustomerRechargeCode;
				Data[j][8] = mvdDenomination.get(i%mvdDenomination.size());
				j++;
			}
		}
		
		return Data;
	}

	

	
	public void setupData(String PIN, String mrp) {

		String CustomerRechargeCode = _masterVO.getProperty("MVDRechargeCode");
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setAmount("441");
		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
		data.setQty(new RandomGeneration().randomNumberWithoutZero(1));
		data.setPin(PIN);
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
		mvdRechargeRequestPojo.setData(data);

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

	protected static String accessToken=null;


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
	@TestManager(TestKey="PRETUPS-7949")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
	
		
		setupData(PIN,vomsMRP);
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(200);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		
	
		String status = mVDRechargeResponsePojo.getDataObject().getTxnstatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7950")
	public void A_02_Test_blank_msisdn2(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_02_Test_blank_msisdn2";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setMsisdn2("");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11100, Integer.parseInt(status));
		Assertion.assertEquals(status, "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7951")
	public void A_03_Test_blank_nwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_03_Test_blank_nwcode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setExtnwcode("");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(1004004, Integer.parseInt(status));
		Assertion.assertEquals(status, "1004004");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}	
	
	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7952")
	public void A_04_Test_invalid_nwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_04_Test_invalid_nwcode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setExtnwcode("dfg");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(1004011, Integer.parseInt(status));
		Assertion.assertEquals(status, "1004011");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7953")
	public void A_05_Test_invalid_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_05_Test_invalid_pin";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD5");
		moduleCode = CaseMaster.getModuleCode();
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setPin("2324");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getTxnstatus();
		Assert.assertEquals(206, Integer.parseInt(status));
		Assertion.assertEquals(status, "206");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7954")
	public void A_06_Test_blank_qty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_06_Test_blank_qty";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD6");
		moduleCode = CaseMaster.getModuleCode();
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setQty("");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11100, Integer.parseInt(status));
		Assertion.assertEquals(status, "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}	
	
	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7955")
	public void A_07_Test_invalid_qty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_07_Test_invalid_qty";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setQty("dfg");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11102, Integer.parseInt(status));
		Assertion.assertEquals(status, "11102");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7956")
	public void A_08_Test_max_qty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_08_Test_max_qty";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setExtnwcode("dfg");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(1004011, Integer.parseInt(status));
		Assertion.assertEquals(status, "1004011");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7957")
	public void A_09_Test_blank_amount(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_09_Test_blank_amount";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setAmount("");
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String status = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11100, Integer.parseInt(status));
		Assertion.assertEquals(status, "11100");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData") 
	@TestManager(TestKey="PRETUPS-7958")
	public void A_10_Test_invalid_amount(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String customerCode, String vomsMRP) throws Exception {
		final String methodName = "A_10_Test_invalid_amount";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("MVD10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		setupData(PIN,vomsMRP);
		mvdRechargeRequestPojo.getData().setAmount(new RandomGeneration().randomAlphabets(2));
		MVDRechargeApi mvdRechargeApi = new MVDRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		mvdRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		mvdRechargeApi.addBodyParam(mvdRechargeRequestPojo);
		mvdRechargeApi.setExpectedStatusCode(400);
		mvdRechargeApi.perform();
		mVDRechargeResponsePojo = mvdRechargeApi
				.getAPIResponseAsPOJO(MVDRechargeResponsePojo.class);
		String errorcode = mVDRechargeResponsePojo.getDataObject().getErrorcode();
		Assert.assertEquals(11109, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "11109");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
