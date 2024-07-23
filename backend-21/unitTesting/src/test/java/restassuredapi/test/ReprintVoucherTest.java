package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.getuserinfoapi.GetUserInfoAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.reprintVoucher.ReprintVoucher;
import restassuredapi.pojo.getuserinfoapiresponsepojo.GetUserInfoApiResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.reprintVoucherResponsePojo.ReprintVoucherResponsePojo;

@ModuleManager(name = Module.REPRINT_VOUCHER)
public class ReprintVoucherTest extends BaseTest{
	static String moduleCode;
	
	ReprintVoucherResponsePojo reprintVoucherResponsePojo = new ReprintVoucherResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

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
	
	
	@Test(dataProvider="userData")
	@TestManager(TestKey = "PRETUPS-15667")
	public void A_01_Test_InvalidPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_InvalidPin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REPRINTVOUCHER1");
		moduleCode = CaseMaster.getModuleCode();

		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		ReprintVoucher reprintVoucher = new ReprintVoucher(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		reprintVoucher.setContentType(_masterVO.getProperty("contentType"));
//		getUserInfoAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		String str = new RandomGeneration().randomAlphaNumeric(8);
//		getUserInfoAPI.setUserId(str);
//		getUserInfoAPI.setExpectedStatusCode(400);
		
		reprintVoucher.setTransactionId("D240321.0003");
		String str = new RandomGeneration().randomAlphaNumeric(4);
		reprintVoucher.setPin(str);
		
		reprintVoucher.setExpectedStatusCode(400);
		reprintVoucher.perform();
		reprintVoucherResponsePojo = reprintVoucher.getAPIResponseAsPOJO(ReprintVoucherResponsePojo.class);
		

		String message=reprintVoucherResponsePojo.getMessage();
//		Assert.assertEquals(message, "Details of user "+str+" not found.");
//		Assertion.assertEquals(message, "Details of user "+str+" not found.");
		Assert.assertEquals(message, "FAIL");
		Assertion.assertEquals(message, "FAIL");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider="userData")
	@TestManager(TestKey = "PRETUPS-15666")
	public void A_02_Test_InvalidTransactionId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_InvalidTransactionId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REPRINTVOUCHER2");
		moduleCode = CaseMaster.getModuleCode();

		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		ReprintVoucher reprintVoucher = new ReprintVoucher(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		reprintVoucher.setContentType(_masterVO.getProperty("contentType"));
//		getUserInfoAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		String str = new RandomGeneration().randomAlphaNumeric(8);
//		getUserInfoAPI.setUserId(str);
//		getUserInfoAPI.setExpectedStatusCode(400);
		
		reprintVoucher.setTransactionId("D240321.000");
		//String str = new RandomGeneration().randomAlphaNumeric(4);
		reprintVoucher.setPin(PIN);
		
		reprintVoucher.setExpectedStatusCode(400);
		reprintVoucher.perform();
		reprintVoucherResponsePojo = reprintVoucher.getAPIResponseAsPOJO(ReprintVoucherResponsePojo.class);
		

		String message=reprintVoucherResponsePojo.getMessage();
//		Assert.assertEquals(message, "Details of user "+str+" not found.");
//		Assertion.assertEquals(message, "Details of user "+str+" not found.");
		Assert.assertEquals(message, "Invalid transaction Id");
		Assertion.assertEquals(message, "Invalid transaction Id");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider="userData")
	@TestManager(TestKey = "PRETUPS-15665")
	public void A_03_Test_ValidPinAndTransactionId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_ValidPinAndTransactionId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REPRINTVOUCHER3");
		moduleCode = CaseMaster.getModuleCode();

		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		ReprintVoucher reprintVoucher = new ReprintVoucher(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		reprintVoucher.setContentType(_masterVO.getProperty("contentType"));
//		getUserInfoAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		String str = new RandomGeneration().randomAlphaNumeric(8);
//		getUserInfoAPI.setUserId(str);
//		getUserInfoAPI.setExpectedStatusCode(400);
		
		reprintVoucher.setTransactionId("D240321.0003");
		//String str = new RandomGeneration().randomAlphaNumeric(4);
		reprintVoucher.setPin(PIN);
		
		reprintVoucher.setExpectedStatusCode(200);
		reprintVoucher.perform();
		reprintVoucherResponsePojo = reprintVoucher.getAPIResponseAsPOJO(ReprintVoucherResponsePojo.class);
		

		String message=reprintVoucherResponsePojo.getMessage();
//		Assert.assertEquals(message, "Details of user "+str+" not found.");
//		Assertion.assertEquals(message, "Details of user "+str+" not found.");
		Assert.assertEquals(message, "SUCCESS");
		Assertion.assertEquals(message, "SUCCESS");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
