package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2sgettransactionedetail.C2SGetTransactionDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.c2sgettransactiondetailrequestpojo.Data;

import restassuredapi.pojo.c2sgettransactiondetailrequestpojo.C2SGetTransactionDetailsRequestPojo;
import restassuredapi.pojo.c2sgettransactiondetailresponsepojo.C2SGetTransactionDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_C2S_GET_TRANSACTION_DETAILS)
public class C2SGetTransactionDetails extends BaseTest{
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     
     Date yesterday = DateUtils.addDays(new Date(), -1);
     Date threeDaysAgo = DateUtils.addDays(new Date(), -3);
     
     String yesterdayDate = df.format(yesterday);
     String threeDaysAgoDateStr = df.format(threeDaysAgo);
     
     String currentDate=df.format(dateobj);   
	
     static String moduleCode;
	
	C2SGetTransactionDetailsRequestPojo c2SGetTransactionDetailsRequestPojo = new C2SGetTransactionDetailsRequestPojo();
	C2SGetTransactionDetailsResponsePojo c2SGetTransactionDetailsResponsePojo = new C2SGetTransactionDetailsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Data data = new Data();
	Login login = new Login();
	Product product= null;
	
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


	// Successful data with valid data.

	protected static String accessToken;


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception
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
	
	
	public void setupData() {
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLanguage1("");
		data.setFromDate(threeDaysAgoDateStr);
		data.setToDate(yesterdayDate);
		data.setServiceType(_masterVO.getProperty("CustomerRechargeCode"));
		c2SGetTransactionDetailsRequestPojo.setData(data);

	}

	// Successful data with valid data.
	@Test(dataProvider= "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2SGetTransactionDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGTD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SGetTransactionDetailsAPI c2SGetTransactionDetailsAPI = new C2SGetTransactionDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SGetTransactionDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SGetTransactionDetailsAPI.addBodyParam(c2SGetTransactionDetailsRequestPojo);
		c2SGetTransactionDetailsAPI.setExpectedStatusCode(200);
		c2SGetTransactionDetailsAPI.perform();
		c2SGetTransactionDetailsResponsePojo = c2SGetTransactionDetailsAPI
				.getAPIResponseAsPOJO(C2SGetTransactionDetailsResponsePojo.class);
		int statusCode = Integer.parseInt(c2SGetTransactionDetailsResponsePojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	@Test(dataProvider= "userData")
	public void A_04_Test_BlankExtnwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2SGetTransactionDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGTD4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SGetTransactionDetailsAPI c2SGetTransactionDetailsAPI = new C2SGetTransactionDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SGetTransactionDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setExtnwcode("");
		c2SGetTransactionDetailsRequestPojo.setData(data);
		c2SGetTransactionDetailsAPI.addBodyParam(c2SGetTransactionDetailsRequestPojo);
		c2SGetTransactionDetailsAPI.setExpectedStatusCode(200);
		c2SGetTransactionDetailsAPI.perform();
		c2SGetTransactionDetailsResponsePojo = c2SGetTransactionDetailsAPI
				.getAPIResponseAsPOJO(C2SGetTransactionDetailsResponsePojo.class);
		String errorCode =c2SGetTransactionDetailsResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
	
		Assert.assertEquals(errorCode, "125278");
		Assertion.assertEquals(errorCode, "125278");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	
	
	@Test(dataProvider= "userData")
	public void A_09_Test_InvalidDateTest(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_C2SGetTransactionDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGTD9");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SGetTransactionDetailsAPI c2SGetTransactionDetailsAPI = new C2SGetTransactionDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SGetTransactionDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setFromDate(yesterdayDate);
		data.setToDate(threeDaysAgoDateStr);
		c2SGetTransactionDetailsRequestPojo.setData(data);;
		c2SGetTransactionDetailsAPI.addBodyParam(c2SGetTransactionDetailsRequestPojo);
		c2SGetTransactionDetailsAPI.setExpectedStatusCode(200);
		c2SGetTransactionDetailsAPI.perform();
		c2SGetTransactionDetailsResponsePojo = c2SGetTransactionDetailsAPI
				.getAPIResponseAsPOJO(C2SGetTransactionDetailsResponsePojo.class);
		String errorCode =c2SGetTransactionDetailsResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(errorCode, "7514");
		Assertion.assertEquals(errorCode, "7514");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider= "userData")
	public void A_10_Test_InvalidDateFormatTest(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2SGetTransactionDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2SGTD10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		C2SGetTransactionDetailsAPI c2SGetTransactionDetailsAPI = new C2SGetTransactionDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2SGetTransactionDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setFromDate(new RandomGeneration().randomAlphaNumeric(6));
		
		c2SGetTransactionDetailsRequestPojo.setData(data);;
		c2SGetTransactionDetailsAPI.addBodyParam(c2SGetTransactionDetailsRequestPojo);
		c2SGetTransactionDetailsAPI.setExpectedStatusCode(200);
		c2SGetTransactionDetailsAPI.perform();
		c2SGetTransactionDetailsResponsePojo = c2SGetTransactionDetailsAPI
				.getAPIResponseAsPOJO(C2SGetTransactionDetailsResponsePojo.class);
		String errorCode =c2SGetTransactionDetailsResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(errorCode, "1004003");
		Assertion.assertEquals(errorCode, "1004003");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
