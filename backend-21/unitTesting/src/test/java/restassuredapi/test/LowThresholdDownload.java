package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import restassuredapi.api.lowThresholdDownload.LowThresholdDownloadApi;
import restassuredapi.pojo.lowthresholddownloadrequestpojo.Data;
import restassuredapi.pojo.lowthresholddownloadrequestpojo.DispHeaderColumn;
import restassuredapi.pojo.lowthresholddownloadrequestpojo.LowThresholdDownloadRequestPojo;
import restassuredapi.pojo.lowthresholddownloadresponsepojo.LowThresholdDownloadResponsePojo;


@ModuleManager(name = Module.LOW_THRESHOLD_DOWNLOAD)
public class LowThresholdDownload extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -120));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
       
	static String moduleCode;
	LowThresholdDownloadRequestPojo lowThresholdDownloadRequestPojo = new LowThresholdDownloadRequestPojo();
	LowThresholdDownloadResponsePojo lowThresholdDownloadResponsePojo = new LowThresholdDownloadResponsePojo();
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
		
		DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
		dispHeaderColumn.setColumnName("userName");
		dispHeaderColumn.setDisplayName("User name");
		
		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setColumnName("mobileNumber");
		dispHeaderColumn1.setDisplayName("Mobile number");
		
		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setColumnName("userStatus");
		dispHeaderColumn2.setDisplayName("User status");
		
		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setColumnName("dateTime");
		dispHeaderColumn3.setDisplayName("Dated time");
		
		DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
		dispHeaderColumn4.setColumnName("transactionID");
		dispHeaderColumn4.setDisplayName("CTransaction ID");
		
		DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
		dispHeaderColumn5.setColumnName("transferType");
		dispHeaderColumn5.setDisplayName("Transfer type");
		
		DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
		dispHeaderColumn6.setColumnName("categoryName");
		dispHeaderColumn6.setDisplayName("Category Name");
		
		DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
		dispHeaderColumn7.setColumnName("threshHold");
		dispHeaderColumn7.setDisplayName("Threshold");
		
		DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
		dispHeaderColumn8.setColumnName("productName");
		dispHeaderColumn8.setDisplayName("Product name");
		
		DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
		dispHeaderColumn9.setColumnName("previousBalance");
		dispHeaderColumn9.setDisplayName("Previous balance");
		
		DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
		dispHeaderColumn10.setColumnName("currentBalance");
		dispHeaderColumn10.setDisplayName("Post balance");
		
		List<DispHeaderColumn> dispHeaderColumnList= new ArrayList<DispHeaderColumn>();
	  
		dispHeaderColumnList.add(dispHeaderColumn);
		dispHeaderColumnList.add(dispHeaderColumn1);
		dispHeaderColumnList.add(dispHeaderColumn2);
		dispHeaderColumnList.add(dispHeaderColumn3);
		dispHeaderColumnList.add(dispHeaderColumn4);
		dispHeaderColumnList.add(dispHeaderColumn5);
		dispHeaderColumnList.add(dispHeaderColumn6);
		dispHeaderColumnList.add(dispHeaderColumn7);
		dispHeaderColumnList.add(dispHeaderColumn8);
		dispHeaderColumnList.add(dispHeaderColumn9);
		dispHeaderColumnList.add(dispHeaderColumn10);
	
		data.setCategory("DIST");
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDomain("DIST");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFileType("");
		data.setFromDate(fromDate);
		data.setGeography("ALL");
		data.setThreshhold("ALL");
		data.setToDate(toDate);		
		lowThresholdDownloadRequestPojo.setData(data);		
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
		@TestManager(TestKey="PRETUPS-001")
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
		lowThresholdDownloadApi.setExpectedStatusCode(200);
		lowThresholdDownloadApi.perform();
		lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
				.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(lowThresholdDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
			
	}
		
	//network code provided is blank 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-002")
		public void A_02_Test_Negative2_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_Negative2_LowThresoldDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		lowThresholdDownloadRequestPojo.getData().setExtnwcode("");
				
		lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
		lowThresholdDownloadApi.setExpectedStatusCode(400);
		lowThresholdDownloadApi.perform();
		lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
				.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
		String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278);
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// blank - fromDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-003")
	public void A_03_Test_Negative3_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_03_Test_Negative3_LowThresoldDownload";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD3");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));
	
	lowThresholdDownloadRequestPojo.getData().setFromDate("");		
				
	lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
	lowThresholdDownloadApi.setExpectedStatusCode(400);
	lowThresholdDownloadApi.perform();
	lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
			.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
	String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	        
	Assert.assertEquals(Integer.parseInt(errorCode), 7519);
	Assertion.assertEquals(errorCode,"7519");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
} 
	// blank - toDate
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-004")
	public void A_04_Test_Negative4_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_04_Test_Negative4_LowThresoldDownload";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD4");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
			
	LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));
	
	lowThresholdDownloadRequestPojo.getData().setToDate("");

	lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
	lowThresholdDownloadApi.setExpectedStatusCode(400);
	lowThresholdDownloadApi.perform();
	lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
			.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
	String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	
	Assert.assertEquals(Integer.parseInt(errorCode), 7520);
	Assertion.assertEquals(errorCode,"7520");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - Category field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-005")
	public void A_05_Test_Negative5_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_05_Test_Negative5_LowThresoldDownload";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD5");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
	
	LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));

	lowThresholdDownloadRequestPojo.getData().setCategory("");

	lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
	lowThresholdDownloadApi.setExpectedStatusCode(400);
	lowThresholdDownloadApi.perform();
	lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
			.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
	String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	
	Assert.assertEquals(Integer.parseInt(errorCode), 1021005);
	Assertion.assertEquals(errorCode,"1021005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - domain field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-006")
	public void A_06_Test_Negative6_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_06_Test_Negative6_LowThresoldDownload";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD6");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
	
	LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));

	lowThresholdDownloadRequestPojo.getData().setDomain("");

	lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
	lowThresholdDownloadApi.setExpectedStatusCode(400);
	lowThresholdDownloadApi.perform();
	lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
			.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
	String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	
	Assert.assertEquals(Integer.parseInt(errorCode), 241197);
	Assertion.assertEquals(errorCode,"241197");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - geography field
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-007")
	public void A_07_Test_Negative7_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
	final String methodName = "A_07_Test_Negative7_LowThresoldDownload";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD7");
	moduleCode = CaseMaster.getModuleCode();
	currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
	currentNode.assignCategory("REST");
	setupData();
	
	LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));

	lowThresholdDownloadRequestPojo.getData().setGeography("");

	lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
	lowThresholdDownloadApi.setExpectedStatusCode(400);
	lowThresholdDownloadApi.perform();
	lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
			.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
	String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	
	Assert.assertEquals(Integer.parseInt(errorCode), 1020005);
	Assertion.assertEquals(errorCode,"1020005");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
	}
	
	// blank - threshold field
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-008")
		public void A_08_Test_Negative8_LowThresoldDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_08_Test_Negative8_LowThresoldDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("LTDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
		
		LowThresholdDownloadApi lowThresholdDownloadApi = new LowThresholdDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		lowThresholdDownloadApi.setContentType(_masterVO.getProperty("contentType"));

		lowThresholdDownloadRequestPojo.getData().setThreshhold("");

		lowThresholdDownloadApi.addBodyParam(lowThresholdDownloadRequestPojo);
		lowThresholdDownloadApi.setExpectedStatusCode(400);
		lowThresholdDownloadApi.perform();
		lowThresholdDownloadResponsePojo = lowThresholdDownloadApi
				.getAPIResponseAsPOJO(LowThresholdDownloadResponsePojo.class);
		String errorCode = lowThresholdDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(Integer.parseInt(errorCode), 241325);
		Assertion.assertEquals(errorCode,"241325");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		} 
	
}