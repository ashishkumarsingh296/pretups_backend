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

import restassuredapi.api.c2SAdditionalCommissionSummarySearch.C2SAdditionalCommissionSummarySearchApi;
import restassuredapi.pojo.c2Sadditionalcommissionsummarysearchrequestpojo.C2SAdditionalCommissionSummarySearchRequestPojo;
import restassuredapi.pojo.c2Sadditionalcommissionsummarysearchresponsepojo.C2SAdditionalCommissionSummarySearchResponsePojo;
import restassuredapi.pojo.c2Sadditionalcommissionsummarysearchrequestpojo.Data;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2S_ADDITIONAL_COMMISSION_SUMMARY_SEARCH)
public class C2SAdditionalCommissionSummarySearch extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -500));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
     
    static String moduleCode;
    C2SAdditionalCommissionSummarySearchRequestPojo c2SAdditionalCommissionSummarySearchRequestPojo = new C2SAdditionalCommissionSummarySearchRequestPojo();
    C2SAdditionalCommissionSummarySearchResponsePojo c2SAdditionalCommissionSummarySearchResponsePojo = new C2SAdditionalCommissionSummarySearchResponsePojo();
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

	public void setupData() {
		
		data.setCategoryCode("ALL");
		data.setDailyOrmonthlyOption("DAILY");
		data.setDomain("ALL");
		data.setFromDate(fromDate);
		data.setFromMonthYear("01/2021");
		data.setGeography("ALL");
		data.setService("ALL");
		data.setToDate(toDate);
		data.setToMonthYear("08/2021");
		c2SAdditionalCommissionSummarySearchRequestPojo.setData(data);
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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
		c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
		c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(200);
		c2SAdditionalCommissionSummarySearchApi.perform();
		c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
		int statusCode = Integer.parseInt(c2SAdditionalCommissionSummarySearchResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// blank - categoryCode 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-002")
	public void A_02_Test_Negative2_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_02_Test_Negative2_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH2");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setCategoryCode("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - dailyOrmonthlyOption 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-003")
	public void A_03_Test_Negative3_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_03_Test_Negative3_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH3");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setDailyOrmonthlyOption("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - domain 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-004")
	public void A_04_Test_Negative4_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_04_Test_Negative4_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH4");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setDomain("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005);// need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - fromDate 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-005")
	public void A_05_Test_Negative5_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_05_Test_Negative5_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH5");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setFromDate("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - fromMonthYear 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-006")
	public void A_06_Test_Negative6_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_06_Test_Negative6_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH6");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setFromMonthYear("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - geography 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-007")
	public void A_07_Test_Negative7_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_07_Test_Negative7_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH7");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setGeography("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - service 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-008")
	public void A_08_Test_Negative8_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_08_Test_Negative8_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH8");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setService("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - toDate 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-009")
	public void A_09_Test_Negative9_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_09_Test_Negative9_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH9");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setToDate("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - toMonthYear 
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-010")
	public void A_10_Test_Negative10_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_10_Test_Negative10_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH10");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setToMonthYear("");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// With Monthly option - need to work on it
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-011")
	public void A_11_Test_Negative11_C2SAdditionalCommissionSummarySearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_11_Test_Negative11_C2SAdditionalCommissionSummarySearch";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSSEARCH11");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	C2SAdditionalCommissionSummarySearchApi c2SAdditionalCommissionSummarySearchApi = new C2SAdditionalCommissionSummarySearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	c2SAdditionalCommissionSummarySearchApi.setContentType(_masterVO.getProperty("contentType"));
			
	c2SAdditionalCommissionSummarySearchRequestPojo.getData().setDailyOrmonthlyOption("MONTHLY");
			
	c2SAdditionalCommissionSummarySearchApi.addBodyParam(c2SAdditionalCommissionSummarySearchRequestPojo);
	c2SAdditionalCommissionSummarySearchApi.setExpectedStatusCode(400);
	c2SAdditionalCommissionSummarySearchApi.perform();
	c2SAdditionalCommissionSummarySearchResponsePojo = c2SAdditionalCommissionSummarySearchApi
			.getAPIResponseAsPOJO(C2SAdditionalCommissionSummarySearchResponsePojo.class);
	String errorCode = c2SAdditionalCommissionSummarySearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
        
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005); // need to check error code
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}	
}