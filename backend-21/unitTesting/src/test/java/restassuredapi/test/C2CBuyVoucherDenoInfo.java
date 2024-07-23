package restassuredapi.test;

import java.text.MessageFormat;
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
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cvoucherdeno.C2CVoucherDenoAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuyvoucherdenoinforequestpojo.C2CBuyVoucherDenoInfoRequestPojo;
import restassuredapi.pojo.c2cbuyvoucherdenoinforequestpojo.DenominationData;
import restassuredapi.pojo.c2cbuyvoucherdenoinforesponsepojo.C2CBuyVoucherDenoInfoResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;




@ModuleManager(name = Module.REST_VOUCHER_DENOMINATION)
public class C2CBuyVoucherDenoInfo extends BaseTest {
	
	 
	static String moduleCode;
	C2CBuyVoucherDenoInfoRequestPojo c2cBuyVoucherDenoInfoRequestPojo = new C2CBuyVoucherDenoInfoRequestPojo();
	C2CBuyVoucherDenoInfoResponsePojo c2cBuyVoucherDenoInfoResponsePojo = new C2CBuyVoucherDenoInfoResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	
	Login login = new Login();
	
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        
        Object[][] Data = new Object[rowCount][7];
        int j=0;
        for(int i=1;i<=rowCount;i++) {
        	Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
        	Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
            Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
            Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
            Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
            Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
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
	public void setupData(String data1,String data2,String msisdn) {
	
	
	c2cBuyVoucherDenoInfoRequestPojo.setIdentifierType(data1);
	c2cBuyVoucherDenoInfoRequestPojo.setIdentifierValue(data2);
	
	DenominationData data = new DenominationData();
	
	c2cBuyVoucherDenoInfoRequestPojo.setData(data);
	
	c2cBuyVoucherDenoInfoRequestPojo.getData().setLoginId(data1);
	c2cBuyVoucherDenoInfoRequestPojo.getData().setMsisdn(msisdn);
	String voucherType = DBHandler.AccessHandler.getVoucherTypeForUser(msisdn);
	c2cBuyVoucherDenoInfoRequestPojo.getData().setVoucherType(voucherType);
	c2cBuyVoucherDenoInfoRequestPojo.getData().setVoucherSegment(_masterVO.getProperty("enquiryVoucherSegment"));
		
		
	}

	// Successful data with valid data.
	
	 protected static String accessToken;
	    

	    public void BeforeMethod(String data1, String data2) throws Exception
	    {
	    	if(accessToken==null) {
	    	final String methodName = "Test_OAuthenticationTest";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());

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
	
	    }
	
	
	
	@Test(dataProvider = "userData")
	public void A_01_Test_successWithMsisdn(String loginID, String password, String msisdn,String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		 if(_masterVO.getProperty("identifierType").equals("loginid"))
             BeforeMethod(loginID, password);
         else if(_masterVO.getProperty("identifierType").equals("msisdn"))
             BeforeMethod(msisdn, PIN);
		 
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setLoginId("");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_02_Test_noLogin(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception 
	{
		final String methodName = "A_02_Test_noLogin";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setLoginId("");
		c2cBuyVoucherDenoInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		String code =  (String) c2cBuyVoucherDenoInfoResponsePojo.getAdditionalProperties().get("globalError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "no loginId or msisdn");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider="userData")
	public void A_03_Test_invalidIdentifierLogin(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception 
	{
		final String methodName = "A_02_Test_noLogin";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.setIdentifierType("abc");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		String code =  (String) c2cBuyVoucherDenoInfoResponsePojo.getAdditionalProperties().get("formError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "user.invalidloginid");

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="userData")
	public void A_04_Test_successWithLoginID(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_successWithLoginID";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider="userData")
	public void A_05_Test_failureWithInvalidType(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_05_Test_failureWithInvalidType";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setVoucherType("digital123");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		String code =  (String) c2cBuyVoucherDenoInfoResponsePojo.getAdditionalProperties().get("globalError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "voucher type is invalid");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="userData")
	public void A_06_Test_failureWithInvalidSegment(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_06_Test_failureWithInvalidSegment";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setVoucherSegment("National123");
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();
		
		String code =  (String) c2cBuyVoucherDenoInfoResponsePojo.getAdditionalProperties().get("globalError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "voucher segment is invalid");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider="userData")
	public void A_07_Test_successWithOperatorUser(String loginID, String password, String msisdn, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVDENO7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(loginID,password,msisdn);
		c2cBuyVoucherDenoInfoRequestPojo.getData().setLoginId("");
		c2cBuyVoucherDenoInfoRequestPojo.setIdentifierType(transferDetails.get("LOGIN_ID_ADMIN"));
		c2cBuyVoucherDenoInfoRequestPojo.setIdentifierValue(transferDetails.get("PASSWORD_ADMIN"));
		C2CVoucherDenoAPI c2cVoucherDenoAPI = new C2CVoucherDenoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2cVoucherDenoAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherDenoAPI.addBodyParam(c2cBuyVoucherDenoInfoRequestPojo);
		c2cVoucherDenoAPI.setExpectedStatusCode(200);
		c2cVoucherDenoAPI.perform();
		c2cBuyVoucherDenoInfoResponsePojo = c2cVoucherDenoAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherDenoInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherDenoInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
