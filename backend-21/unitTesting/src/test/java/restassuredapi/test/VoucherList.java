package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.VoucherPinResend.VoucherResendApi;
import restassuredapi.api.bulkgiftrecharge.BulkGiftRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.bulkgiftrechargerequestpojo.Data;
import restassuredapi.pojo.bulkgiftrechargeresponsepojo.C2CBulkGiftRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.voucherPinResend.DataVoucher;
import restassuredapi.pojo.voucherPinResend.VoucherPinResendRequestPOJO;
import restassuredapi.pojo.voucherPinResend.VoucherPinResendResponsePOJO;

@ModuleManager(name = Module.VOUCHER_RESEND)
public class VoucherList extends BaseTest {

	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
     
	static String moduleCode;
	VoucherPinResendRequestPOJO voucherPinResendRequestPOJO= new VoucherPinResendRequestPOJO();
	VoucherPinResendResponsePOJO voucherPinResendResponsePOJO = new VoucherPinResendResponsePOJO();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	

	DataVoucher data = new DataVoucher();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	
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

	public void setupData(String data1) {
		data.setCustomerMsisdn("normal");
		data.setDate("");
		data.setRemarks("");
		data.setRequestGatewayCode("");
		data.setSerialNo("");
		data.setSubscriberMsisdn("");
		data.setTransactionid("");
		
		voucherPinResendRequestPOJO.setData(data);
	
		
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
	
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		VoucherResendApi voucherResendApi = new VoucherResendApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		voucherResendApi.setContentType(_masterVO.getProperty("contentType"));
		voucherResendApi.addBodyParam(voucherPinResendRequestPOJO);
		voucherResendApi.setExpectedStatusCode(201);
		voucherResendApi.perform();

		voucherPinResendResponsePOJO = voucherResendApi
				.getAPIResponseAsPOJO(VoucherPinResendResponsePOJO.class);
		

		String status = Integer.toString(voucherPinResendResponsePOJO.getStatus());
		Assert.assertEquals(status, "200");
		Assertion.assertContainsEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_02_Test_fail(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_02_Test_fail";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		data.setDate("");
		voucherPinResendRequestPOJO.setData(data);
		VoucherResendApi voucherResendApi = new VoucherResendApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		voucherResendApi.setContentType(_masterVO.getProperty("contentType"));
		voucherResendApi.addBodyParam(voucherPinResendRequestPOJO);
		voucherResendApi.setExpectedStatusCode(201);
		voucherResendApi.perform();

		voucherPinResendResponsePOJO = voucherResendApi
				.getAPIResponseAsPOJO(VoucherPinResendResponsePOJO.class);
		

         String message= voucherPinResendResponsePOJO.getMessage();
		
		Assert.assertEquals(message, "No transactions Found");
		Assertion.assertEquals(message, "No transactions Found");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_03_Test_fail(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_03_Test_fail";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		data.setTransactionid("");
		voucherPinResendRequestPOJO.setData(data);
		VoucherResendApi voucherResendApi = new VoucherResendApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		voucherResendApi.setContentType(_masterVO.getProperty("contentType"));
		voucherResendApi.addBodyParam(voucherPinResendRequestPOJO);
		voucherResendApi.setExpectedStatusCode(201);
		voucherResendApi.perform();

		voucherPinResendResponsePOJO = voucherResendApi
				.getAPIResponseAsPOJO(VoucherPinResendResponsePOJO.class);
		

         String message= voucherPinResendResponsePOJO.getMessage();
		
		Assert.assertEquals(message, "No transactions Found");
		Assertion.assertEquals(message, "No transactions Found");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_04_Test_fail(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_04_Test_fail";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		data.setSubscriberMsisdn("");
		voucherPinResendRequestPOJO.setData(data);
		VoucherResendApi voucherResendApi = new VoucherResendApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		voucherResendApi.setContentType(_masterVO.getProperty("contentType"));
		voucherResendApi.addBodyParam(voucherPinResendRequestPOJO);
		voucherResendApi.setExpectedStatusCode(201);
		voucherResendApi.perform();

		voucherPinResendResponsePOJO = voucherResendApi
				.getAPIResponseAsPOJO(VoucherPinResendResponsePOJO.class);
		

         String message= voucherPinResendResponsePOJO.getMessage();
		
		Assert.assertEquals(message, "No transactions Found");
		Assertion.assertEquals(message, "No transactions Found");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_05_Test_fail(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_05_Test_fail";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		data.setCustomerMsisdn("");
		voucherPinResendRequestPOJO.setData(data);
		VoucherResendApi voucherResendApi = new VoucherResendApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		voucherResendApi.setContentType(_masterVO.getProperty("contentType"));
		voucherResendApi.addBodyParam(voucherPinResendRequestPOJO);
		voucherResendApi.setExpectedStatusCode(201);
		voucherResendApi.perform();

		voucherPinResendResponsePOJO = voucherResendApi
				.getAPIResponseAsPOJO(VoucherPinResendResponsePOJO.class);
		

         String message= voucherPinResendResponsePOJO.getMessage();
		
		Assert.assertEquals(message, "No transactions Found");
		Assertion.assertEquals(message, "No transactions Found");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
}
