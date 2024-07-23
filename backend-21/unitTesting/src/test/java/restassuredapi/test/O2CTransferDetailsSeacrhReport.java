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
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.o2CTransferDetailsSeacrhReport.O2CTransferDetailsSeacrhReportApi;
import restassuredapi.pojo.o2Ctransferdetailsseacrhreportrequestpojo.O2CTransferDetailsSeacrhReportRequestPojo;
import restassuredapi.pojo.o2Ctransferdetailsseacrhreportresponsepojo.O2CTransferDetailsSeacrhReportResponsePojo;
import restassuredapi.pojo.o2Ctransferdetailsseacrhreportrequestpojo.Data;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.O2C_TRANSFER_DETAILS_SEARCH_REPORT)
public class O2CTransferDetailsSeacrhReport extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -120));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
     
    static String moduleCode;
    O2CTransferDetailsSeacrhReportRequestPojo o2CTransferDetailsSeacrhReportRequestPojo = new O2CTransferDetailsSeacrhReportRequestPojo();
    O2CTransferDetailsSeacrhReportResponsePojo o2CTransferDetailsSeacrhReportResponsePojo = new O2CTransferDetailsSeacrhReportResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount()-4;

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

	public void setupData() {
		
		data.setCategoryCode("DIST");
		data.setDistributionType("ALL");
		data.setDomain("DIST");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFromDate("01/09/21");
		data.setGeography("ALL");
		data.setToDate("30/09/21");
		data.setTransferSubType("ALL");
		data.setTransferCategory("ALL");
		data.setUser("ALL");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);
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
	
	// Positive Scenario
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-001")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(200);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		int statusCode = Integer.parseInt(o2CTransferDetailsSeacrhReportResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	//network code provided is blank 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-002")
	public void A_02_Test_Negative2_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_02_Test_Negative2_O2CTransferDetailsSeacrhReport";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT2");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	currentNode.assignCategory("REST");
	setupData();
			
	O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
			
	data.setExtnwcode("");
	o2CTransferDetailsSeacrhReportRequestPojo.setData(data);
	o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
	o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
	o2CTransferDetailsSeacrhReportApi.perform();
	o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
			.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
	String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

	Assert.assertEquals(messageCode, "Network Code Invaild or blank."); // need to check error code
	Assertion.assertEquals(messageCode, "Network Code Invaild or blank.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - fromDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-003")
	public void A_03_Test_Negative3_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_03_Test_Negative3_O2CTransferDetailsSeacrhReport";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT3");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	currentNode.assignCategory("REST");
	setupData();
			
	O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
			
	data.setFromDate("");
	o2CTransferDetailsSeacrhReportRequestPojo.setData(data);
	o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
	o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
	o2CTransferDetailsSeacrhReportApi.perform();
	o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
			.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
	String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

	Assert.assertEquals(messageCode, "Invalid date format."); // need to check error code
	Assertion.assertEquals(messageCode, "Invalid date format.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	
	// blank - toDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-004")
	public void A_04_Test_Negative4_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_04_Test_Negative4_O2CTransferDetailsSeacrhReport";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT4");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	currentNode.assignCategory("REST");
	setupData();
			
	O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
			
	data.setToDate("");
	o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
	o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
	o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
	o2CTransferDetailsSeacrhReportApi.perform();
	o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
			.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
	String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

	Assert.assertEquals(messageCode, "Invalid date format.");
	Assertion.assertEquals(messageCode, "Invalid date format.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - CategoryCode field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-005")
	public void A_05_Test_Negative5_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_Negative5_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setCategoryCode("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Invalid category");
		Assertion.assertEquals(messageCode, "Invalid category");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// blank - domain field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-006")
	public void A_06_Test_Negative6_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_Negative6_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setDomain("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "Either Invalid domain or empty ");
		Assertion.assertEquals(messageCode, "Either Invalid domain or empty ");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// blank - geography field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-007")
	public void A_07_Test_Negative7_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_07_Test_Negative7_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setGeography("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);		
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "No record available.");
		Assertion.assertEquals(messageCode, "No record available.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// blank - transferSubType field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-008")
	public void A_08_Test_Negative8_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_08_Test_Negative8_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setTransferSubType("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "No record available.");
		Assertion.assertEquals(messageCode, "No record available.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	// blank - TransferCategory field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-009")
	public void A_09_Test_Negative9_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_09_Test_Negative9_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setTransferCategory("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String message = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();
	Assert.assertEquals(message, "Invalid transfer category"); 
	Assertion.assertEquals(message, "Invalid transfer category");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - user field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-010")
	public void A_10_Test_Negative10_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_10_Test_Negative10_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setUser("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String message = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();
	Assert.assertEquals(message, "Invalid user."); 
	Assertion.assertEquals(message, "Invalid user.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - DistributionType field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-011")
	public void A_11_Test_Negative11_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_11_Test_Negative11_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT11");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setDistributionType("");
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);	
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String message = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();
		Assert.assertEquals(message, "Invalid Distribution type."); 
		Assertion.assertEquals(message, "Invalid Distribution type.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-012")
	public void A_12_Test_Positive_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_12_Test_Positive_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT12");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
				
		data.setDistributionType("STOCK");
		data.setTransferSubType("ALL");
		data.setTransferCategory("ALL");	
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();

		Assert.assertEquals(messageCode, "No record available.");
		Assertion.assertEquals(messageCode, "No record available.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-013")
	public void A_13_Test_Negative_O2CTransferDetailsSeacrhReport(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_13_Test_Negative_O2CTransferDetailsSeacrhReport";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTDSREPORT13");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
				
		O2CTransferDetailsSeacrhReportApi o2CTransferDetailsSeacrhReportApi = new O2CTransferDetailsSeacrhReportApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		o2CTransferDetailsSeacrhReportApi.setContentType(_masterVO.getProperty("contentType"));
		data.setCategoryCode(categorCode);
		data.setUser("NGSE0000002761");	
		o2CTransferDetailsSeacrhReportRequestPojo.setData(data);
		o2CTransferDetailsSeacrhReportApi.addBodyParam(o2CTransferDetailsSeacrhReportRequestPojo);
		o2CTransferDetailsSeacrhReportApi.setExpectedStatusCode(400);
		o2CTransferDetailsSeacrhReportApi.perform();
		o2CTransferDetailsSeacrhReportResponsePojo = o2CTransferDetailsSeacrhReportApi
				.getAPIResponseAsPOJO(O2CTransferDetailsSeacrhReportResponsePojo.class);
		String messageCode = o2CTransferDetailsSeacrhReportResponsePojo.getMessageCode();
		Assert.assertEquals(messageCode, "No record available.");
		Assertion.assertEquals(messageCode, "No record available.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}