package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import restassuredapi.api.pinPasswordHistoryDownload.PinPasswordHistoryDownloadApi;
import restassuredapi.pojo.pinpasswordhistorydownloadrequestpojo.Data;
import restassuredapi.pojo.pinpasswordhistorydownloadrequestpojo.DispHeaderColumn;
import restassuredapi.pojo.pinpasswordhistorydownloadrequestpojo.PinPasswordHistoryDownloadRequestPojo;
import restassuredapi.pojo.pinpasswordhistorydownloadresponsepojo.PinPasswordHistoryDownloadResponsePojo;

@ModuleManager(name = Module.PIN_PASSWORD_HISTORY_DOWNLOAD)
public class PinPasswordHistoryDownload extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -30));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
       
	static String moduleCode;
	PinPasswordHistoryDownloadRequestPojo pinPasswordHistoryDownloadRequestPojo = new PinPasswordHistoryDownloadRequestPojo();
	PinPasswordHistoryDownloadResponsePojo pinPasswordHistoryDownloadResponsePojo = new PinPasswordHistoryDownloadResponsePojo();
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
		dispHeaderColumn.setDisplayName("User Name");
		
		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setColumnName("msisdnOrLoginID");
		dispHeaderColumn1.setDisplayName("Mobilenumber");
		
		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setColumnName("modifiedBy");
		dispHeaderColumn2.setDisplayName("Modified By");
		
		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setColumnName("modifiedOn");
		dispHeaderColumn3.setDisplayName("Modified On");
				
		List<DispHeaderColumn> dispHeaderColumnList= new ArrayList<DispHeaderColumn>();
	  
		dispHeaderColumnList.add(dispHeaderColumn);
		dispHeaderColumnList.add(dispHeaderColumn1);
		dispHeaderColumnList.add(dispHeaderColumn2);
		dispHeaderColumnList.add(dispHeaderColumn3);
			
		data.setCategoryCode("DIST");
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setDomain("DIST");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFileType("");
		data.setFromDate(fromDate);
		data.setReqType("PIN");
//		for PWD (reqType) need to write one more positive scenario and pass the PWD value directly for time being
		data.setToDate(toDate);
		data.setUserType("ALL");
		pinPasswordHistoryDownloadRequestPojo.setData(data);
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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(200);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(pinPasswordHistoryDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		//network code provided is blank 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-002")
		public void A_02_Test_Negative2_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_Negative2_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setExtnwcode("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278);
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - fromDate 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-003")
		public void A_03_Test_Negative3_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_Negative3_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setFromDate("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 7519);
		Assertion.assertEquals(errorCode,"7519");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - toDate 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-004")
		public void A_04_Test_Negative4_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_Negative4_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setToDate("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 7520);
		Assertion.assertEquals(errorCode,"7520");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - CategoryCode field 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-005")
		public void A_05_Test_Negative5_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_Negative5_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setCategoryCode("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 1021005);
		Assertion.assertEquals(errorCode,"1021005");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - domain field 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-006")
		public void A_06_Test_Negative6_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_Negative6_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setDomain("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 241197);
		Assertion.assertEquals(errorCode,"241197");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank reqType field 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-007")
		public void A_07_Test_Negative7_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_07_Test_Negative7_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setReqType("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 241326);
		Assertion.assertEquals(errorCode,"241326");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank UserType field 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-008")
		public void A_08_Test_Negative8_PinPasswordHistoryDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_08_Test_Negative8_PinPasswordHistoryDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		pinPasswordHistoryDownloadRequestPojo.getData().setUserType("");
				
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(400);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		String errorCode = pinPasswordHistoryDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 2241247);
		Assertion.assertEquals(errorCode,"2241247");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// Successful data with valid data for PWD.
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-009")
		public void A_09_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_09_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PPHDOWNLOAD9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		PinPasswordHistoryDownloadApi pinPasswordHistoryDownloadApi = new PinPasswordHistoryDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		pinPasswordHistoryDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		
		pinPasswordHistoryDownloadRequestPojo.getData().setReqType("PWD");
		
		pinPasswordHistoryDownloadApi.addBodyParam(pinPasswordHistoryDownloadRequestPojo);
		pinPasswordHistoryDownloadApi.setExpectedStatusCode(200);
		pinPasswordHistoryDownloadApi.perform();
		pinPasswordHistoryDownloadResponsePojo = pinPasswordHistoryDownloadApi
				.getAPIResponseAsPOJO(PinPasswordHistoryDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(pinPasswordHistoryDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
}
