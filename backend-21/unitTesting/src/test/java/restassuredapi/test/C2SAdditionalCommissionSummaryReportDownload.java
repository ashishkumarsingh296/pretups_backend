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

import restassuredapi.api.c2SAdditionalCommissionSummaryReportDownload.C2SAdditionalCommissionSummaryReportDownloadApi;
import restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadrequestpojo.C2SAdditionalCommissionSummaryReportDownloadRequestPojo;
import restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadresponsepojo.C2SAdditionalCommissionSummaryReportDownloadResponsePojo;
import restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadrequestpojo.Data;
import restassuredapi.pojo.c2Sadditionalcommissionsummaryreportdownloadrequestpojo.DispHeaderColumn;

@ModuleManager(name = Module.C2S_ADDITIONAL_COMMISSION_SUMMARY_REPORT_DOWNLOAD)
public class C2SAdditionalCommissionSummaryReportDownload extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
     String fromDate = df.format(DateUtils.addDays(new Date(), -500));
     String toDate = df.format(DateUtils.addDays(new Date(), -1));
       
	static String moduleCode;
	C2SAdditionalCommissionSummaryReportDownloadRequestPojo c2SAdditionalCommissionSummaryReportDownloadRequestPojo = new C2SAdditionalCommissionSummaryReportDownloadRequestPojo();
	C2SAdditionalCommissionSummaryReportDownloadResponsePojo c2SAdditionalCommissionSummaryReportDownloadResponsePojo = new C2SAdditionalCommissionSummaryReportDownloadResponsePojo();
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
        dispHeaderColumn.setColumnName("TransferDateOrMonth");
        dispHeaderColumn.setDisplayName("Transfer Date/Month");
       
        DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
        dispHeaderColumn1.setColumnName("loginID");
        dispHeaderColumn1.setDisplayName("Login ID");
       
        DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
        dispHeaderColumn2.setColumnName("userName");
        dispHeaderColumn2.setDisplayName("User name");
       
        DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
        dispHeaderColumn3.setColumnName("userMobileNumber");
        dispHeaderColumn3.setDisplayName("User mobile number");
       
        DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
        dispHeaderColumn4.setColumnName("userCategory");
        dispHeaderColumn4.setDisplayName("User category");
       
        DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
        dispHeaderColumn5.setColumnName("userGeography");
        dispHeaderColumn5.setDisplayName("User geography");
       
        DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
        dispHeaderColumn6.setColumnName("parentName");
        dispHeaderColumn6.setDisplayName("Parent name");
       
        DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
        dispHeaderColumn7.setColumnName("parentMobileNumber");
        dispHeaderColumn7.setDisplayName("Parent mobile number");
       
        DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
        dispHeaderColumn8.setColumnName("parentCategory");
        dispHeaderColumn8.setDisplayName("Parent category");
       
        DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
        dispHeaderColumn9.setColumnName("parentGeography");
        dispHeaderColumn9.setDisplayName("Parent geography");
       
        DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
        dispHeaderColumn10.setColumnName("ownerName");
        dispHeaderColumn10.setDisplayName("Owner name");
       
        DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
        dispHeaderColumn11.setColumnName("ownerMobileNumber");
        dispHeaderColumn11.setDisplayName("Owner mobile number");
       
        DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
        dispHeaderColumn12.setColumnName("ownerCategory");
        dispHeaderColumn12.setDisplayName("Owner category");
       
        DispHeaderColumn dispHeaderColumn13 = new DispHeaderColumn();
        dispHeaderColumn13.setColumnName("ownerGeography");
        dispHeaderColumn13.setDisplayName("Owner geography");
       
        DispHeaderColumn dispHeaderColumn14 = new DispHeaderColumn();
        dispHeaderColumn14.setColumnName("service");
        dispHeaderColumn14.setDisplayName("Service");
              
        DispHeaderColumn dispHeaderColumn15 = new DispHeaderColumn();
        dispHeaderColumn15.setColumnName("subService");
        dispHeaderColumn15.setDisplayName("Sub service");
       
        DispHeaderColumn dispHeaderColumn16 = new DispHeaderColumn();
        dispHeaderColumn16.setColumnName("transactionCount");
        dispHeaderColumn16.setDisplayName("Transaction count");
       
        DispHeaderColumn dispHeaderColumn17 = new DispHeaderColumn();
        dispHeaderColumn17.setColumnName("differentialCommission");
        dispHeaderColumn17.setDisplayName("Differential commission");
                                
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
        dispHeaderColumnList.add(dispHeaderColumn11);
        dispHeaderColumnList.add(dispHeaderColumn12);
        dispHeaderColumnList.add(dispHeaderColumn13);
        dispHeaderColumnList.add(dispHeaderColumn14);
        dispHeaderColumnList.add(dispHeaderColumn15);
        dispHeaderColumnList.add(dispHeaderColumn16);
        dispHeaderColumnList.add(dispHeaderColumn17);
       		
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setCategoryCode("ALL");
		data.setDailyOrmonthlyOption("DAILY");
		data.setDomain("ALL");
		data.setFromDate(fromDate);
		data.setFromMonthYear("01/20");
		data.setGeography("ALL");
		data.setService("ALL");
		data.setToDate(toDate);
		data.setToMonthYear("08/21");	
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.setData(data);
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
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(200);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getStatus());
		
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - CategoryCode
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-002")
		public void A_02_Test_Negative2_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_Negative2_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setCategoryCode("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - dailyOrmonthlyOption
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-003")
		public void A_03_Test_Negative3_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_Negative3_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setDailyOrmonthlyOption("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - domain
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-004")
		public void A_04_Test_Negative4_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_Negative4_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setDomain("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - fromDate
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-005")
		public void A_05_Test_Negative5_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_Negative5_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setFromDate("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - fromMonthYear
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-006")
		public void A_06_Test_Negative6_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_06_Test_Negative6_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setFromMonthYear("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - geography
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-007")
		public void A_07_Test_Negative7_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_07_Test_Negative7_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setGeography("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - service
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-008")
		public void A_08_Test_Negative8_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_08_Test_Negative8_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setService("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - toDate
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-009")
		public void A_09_Test_Negative9_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_09_Test_Negative9_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setToDate("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// blank - toMonthYear
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-010")
		public void A_10_Test_Negative10_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_10_Test_Negative10_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setToMonthYear("");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
		
		// with - MONTHLY option - need to work on it
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-011")
		public void A_11_Test_Negative11_C2SAdditionalCommissionSummaryReportDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_11_Test_Negative11_C2SAdditionalCommissionSummaryReportDownload";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SACSRDOWNLOAD11");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
				
		C2SAdditionalCommissionSummaryReportDownloadApi c2SAdditionalCommissionSummaryReportDownloadApi = new C2SAdditionalCommissionSummaryReportDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SAdditionalCommissionSummaryReportDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
		c2SAdditionalCommissionSummaryReportDownloadRequestPojo.getData().setDailyOrmonthlyOption("MONTHLY");
				
		c2SAdditionalCommissionSummaryReportDownloadApi.addBodyParam(c2SAdditionalCommissionSummaryReportDownloadRequestPojo);
		c2SAdditionalCommissionSummaryReportDownloadApi.setExpectedStatusCode(400);
		c2SAdditionalCommissionSummaryReportDownloadApi.perform();
		c2SAdditionalCommissionSummaryReportDownloadResponsePojo = c2SAdditionalCommissionSummaryReportDownloadApi
				.getAPIResponseAsPOJO(C2SAdditionalCommissionSummaryReportDownloadResponsePojo.class);
		String errorCode = c2SAdditionalCommissionSummaryReportDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
		Assert.assertEquals(Integer.parseInt(errorCode), 125278); //need to check error code
		Assertion.assertEquals(errorCode,"125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		}
}