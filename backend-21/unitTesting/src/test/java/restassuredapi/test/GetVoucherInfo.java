package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;


import restassuredapi.api.getvoucherinfo.GetVoucherInfoAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;

import restassuredapi.pojo.getvoucherinforequestpojo.Data;
import restassuredapi.pojo.getvoucherinforequestpojo.GetVoucherInfoRequestPojo;
import restassuredapi.pojo.getvoucherinforesponsepojo.GetVoucherInfoResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_C2S_GET_VOUCHER_INFO)
public class GetVoucherInfo extends BaseTest  {
	

	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    Date yesterday = DateUtils.addDays(dateobj, -1);
	String yesterdayDate = df.format(yesterday);
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	GetVoucherInfoResponsePojo getVoucherInfoResponsePojo = new GetVoucherInfoResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Data data = new Data();
	
	Product product= null;
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][9];
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
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			j++;
		}
		return Data;
	}
	
	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
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
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "Test_GETVOUCHERINFO";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGVI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		GetVoucherInfoAPI getVoucherInfoAPI = new GetVoucherInfoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getVoucherInfoAPI.setContentType(_masterVO.getProperty("contentType"));
		
		String userId=DBHandler.AccessHandler.getUserIdFromMsisdn(msisdn);
		getVoucherInfoAPI.setUserId(userId);
		getVoucherInfoAPI.setExpectedStatusCode(200);
		getVoucherInfoAPI.perform();
		getVoucherInfoResponsePojo = getVoucherInfoAPI
				.getAPIResponseAsPOJO(GetVoucherInfoResponsePojo.class);
		long statusCode = getVoucherInfoResponsePojo.getStatusCode();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	public void A_08_Invalid_userId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode, String domainName) throws Exception {
		final String methodName = "Test_GETVOUCHERINFO";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGVI8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		GetVoucherInfoAPI getVoucherInfoAPI = new GetVoucherInfoAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		getVoucherInfoAPI.setContentType(_masterVO.getProperty("contentType"));
		
		RandomGeneration random = new RandomGeneration();
		String userId=random.randomAlphaNumeric(10);
		getVoucherInfoAPI.setUserId(userId);
		getVoucherInfoAPI.setExpectedStatusCode(200);
		getVoucherInfoAPI.perform();
		getVoucherInfoResponsePojo = getVoucherInfoAPI
				.getAPIResponseAsPOJO(GetVoucherInfoResponsePojo.class);

		String message =getVoucherInfoResponsePojo.getGlobalError();
		Assert.assertEquals(message, "invalid login credentials");
		Assertion.assertEquals(message, "invalid login credentials");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	

}
