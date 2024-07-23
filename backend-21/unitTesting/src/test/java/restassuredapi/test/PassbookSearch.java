package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.passbookSearch.PassbookSearchApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.passbooksearchrequestpojo.PassbookSearchDetails;
import restassuredapi.pojo.passbooksearchrequestpojo.PassbookSearchRequestPojo;
import restassuredapi.pojo.passbooksearchresponsepojo.PassbookSearchResponsePojo;

@ModuleManager(name = Module.PASSBOOK_SEARCH)
public class PassbookSearch extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY"); 
//	 DateFormat sdf = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
 //   getSystemPreferenceDefaultValue
     
      String fromDate = df.format(DateUtils.addDays(new Date(), -500));
      String toDate = df.format(DateUtils.addDays(new Date(), -1));
     
     String productCode;
     
	static String moduleCode;
	PassbookSearchRequestPojo passbookSearchRequestPojo = new PassbookSearchRequestPojo();
	PassbookSearchResponsePojo passbookSearchResponsePojo = new PassbookSearchResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	PassbookSearchDetails data = new PassbookSearchDetails();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int productRowCount = ExcelUtility.getRowCount();
        Object[][] product = new Object[productRowCount+2][1];
        int i =0;
        for (i = 1; i <= productRowCount; i++) {
        	product[i][0] =   ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
        }
        product[i][0] = "ALL";
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();		
	
	     int k=i;
		Object[][] Data = new Object[rowCount*(k)][9];
		Object[][] tempData = new Object[1][9];
		int j=0;
		int t=0;
		for( j=1;j<=rowCount;j++) {
		  //Product combination
			for(int m=1; m<=productRowCount+1;m++) {
				
			   	
				tempData[0][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,j);
				tempData[0][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
				tempData[0][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, j);
				tempData[0][3] = ExcelUtility.getCellData(0, ExcelI.PIN, j);
				tempData[0][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, j);
				tempData[0][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				tempData[0][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, j);
				tempData[0][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, j);
				if(m==3) {
					tempData[0][8]="ALL";	
				} else {
					tempData[0][8] =product[m][0];
				}
				 Data[t] = Arrays.copyOf(tempData[0], tempData[0].length);
				 				 
				 t=t+1;
				
			}
			
		}
		
		StringBuilder sb = new StringBuilder();
		
	for (int d=0;d<Data.length;d++) {
		sb.setLength(0);
		for(int f=0;f<=8;f++) {
			sb.append(Data[d][f]).append("  ");
		}
		//System.out.println(sb.toString());
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
		
		/* Read from excel
		String dbName =_masterVO.getMasterValue(MasterI.DB_INTERFACE_TYPE);
		if(dbName.contains("PostGreSQL")) {
			df = new SimpleDateFormat("dd/MM/YYYY");
			fromDate = df.format(DateUtils.addDays(new Date(), -20));
		}else {
			df = new SimpleDateFormat("dd/MM/YY");
			toDate = df.format(DateUtils.addDays(new Date(), -1));
		} */
		
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFromDate(fromDate);
		data.setToDate(toDate);
		data.setProductCode(productCode);
		passbookSearchRequestPojo.setData(data);
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
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBSEARCH1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		PassbookSearchApi passbookSearchApi = new PassbookSearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookSearchApi.setContentType(_masterVO.getProperty("contentType"));
		System.out.println(passbookSearchRequestPojo.getData().toString());
		passbookSearchApi.addBodyParam(passbookSearchRequestPojo);
		passbookSearchApi.setExpectedStatusCode(200);
		passbookSearchApi.perform();
		passbookSearchResponsePojo = passbookSearchApi
				.getAPIResponseAsPOJO(PassbookSearchResponsePojo.class);
		int statusCode = Integer.parseInt(passbookSearchResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
			
	//	String txnid = passbookSearchResponsePojo.getDataObject().getTxnid();
	//	String status = DBHandler.AccessHandler.getTransactionIDStatus(txnid);
	//	if(status == "200")
	//	Assert.assertEquals(200, status);
	//	Assertion.assertEquals(status, "200");
	//	Assertion.completeAssertions();
	//  Log.endTestCase(methodName);

	}
	
		//network code provided is blank 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-002")
		public void A_02_Test_Negative2_PassBookSearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
			final String methodName = "A_02_Test_Negative2_PassBookSearch";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBSEARCH2");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
			currentNode.assignCategory("REST");
			setupData();
			PassbookSearchApi passbookSearchApi = new PassbookSearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			passbookSearchApi.setContentType(_masterVO.getProperty("contentType"));
			passbookSearchRequestPojo.getData().setExtnwcode("");
			passbookSearchApi.addBodyParam(passbookSearchRequestPojo);
			passbookSearchApi.setExpectedStatusCode(400);
			passbookSearchApi.perform();
			passbookSearchResponsePojo = passbookSearchApi
					.getAPIResponseAsPOJO(PassbookSearchResponsePojo.class);
			String errorCode = passbookSearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	        
			Assert.assertEquals(Integer.parseInt(errorCode), 125278);
			Assertion.assertEquals(errorCode,"125278");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	}
		// blank - fromDate
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-003")
		public void A_03_Test_Negative3_PassBookSearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
		final String methodName = "A_03_Test_Negative3_PassBookSearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBSEARCH3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
		PassbookSearchApi passbookSearchApi = new PassbookSearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		passbookSearchApi.setContentType(_masterVO.getProperty("contentType"));
		passbookSearchRequestPojo.getData().setFromDate("");
		passbookSearchApi.addBodyParam(passbookSearchRequestPojo);
		passbookSearchApi.setExpectedStatusCode(400);
		passbookSearchApi.perform();
		passbookSearchResponsePojo = passbookSearchApi
				.getAPIResponseAsPOJO(PassbookSearchResponsePojo.class);
		String errorCode = passbookSearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
			        
		Assert.assertEquals(Integer.parseInt(errorCode), 1004003);
		Assertion.assertEquals(errorCode,"1004003");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
 }
		// blank - toDate
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-004")
		public void A_04_Test_Negative4_PassBookSearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
		final String methodName = "A_04_Test_Negative4_PassBookSearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBSEARCH4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
		currentNode.assignCategory("REST");
		setupData();
		PassbookSearchApi passbookSearchApi = new PassbookSearchApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		passbookSearchApi.setContentType(_masterVO.getProperty("contentType"));
		passbookSearchRequestPojo.getData().setToDate("");
		passbookSearchApi.addBodyParam(passbookSearchRequestPojo);
		passbookSearchApi.setExpectedStatusCode(400);
		passbookSearchApi.perform();
		passbookSearchResponsePojo = passbookSearchApi
				.getAPIResponseAsPOJO(PassbookSearchResponsePojo.class);
		String errorCode = passbookSearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
			        
		Assert.assertEquals(Integer.parseInt(errorCode), 1004003);
		Assertion.assertEquals(errorCode,"1004003");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
 }		
}