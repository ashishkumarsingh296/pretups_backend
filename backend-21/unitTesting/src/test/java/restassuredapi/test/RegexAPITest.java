package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.dbrepository.DBHandler;
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
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.regex.RegexAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.regexresponsepojo.RegexResponsepojo;

@ModuleManager(name = Module.REGEX)
public class RegexAPITest extends BaseTest {
	
	static String moduleCode;
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	RegexResponsepojo regexResponsepojo = new RegexResponsepojo();
	
    GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transfer_Details=new HashMap<String,String>();

	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();



		Object[][] Data = new Object[rowCount][8];
		int k=0;
		for(int i=1;i<=rowCount;i++) {
			Data[k][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[k][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);  //to category
			Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);       //from category
			Data[k][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[k][7] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
			k++;
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

	@Test(dataProvider= "userData")
	@TestManager(TestKey="PRETUPS-6309")
	public void A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REGEX1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		RegexAPI selfRegistrationAPI = new RegexAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		selfRegistrationAPI.setContentType(_masterVO.getProperty("contentType"));
		String lng = _masterVO.getProperty("language");
		String crt =  DBHandler.AccessHandler.checkForCountry(lng);
		selfRegistrationAPI.setCountry(crt);
		selfRegistrationAPI.setLanguage(lng);
		selfRegistrationAPI.setExpectedStatusCode(200);
		selfRegistrationAPI.perform();

		regexResponsepojo = selfRegistrationAPI.getAPIResponseAsPOJO(RegexResponsepojo.class);

		long statusCode = Long.valueOf(regexResponsepojo.getStatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	@Test(dataProvider= "userData")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
		final String methodName = "A_02_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REGEX2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));

		currentNode.assignCategory("REST");
		
		RegexAPI regexAPI = new RegexAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
		regexAPI.setContentType(_masterVO.getProperty("contentType"));
		String lng = _masterVO.getProperty("language");
		String crt =  DBHandler.AccessHandler.checkForCountry(lng);
		regexAPI.setCountry(crt);
		regexAPI.setLanguage(lng);
		regexAPI.setExpectedStatusCode(401);
		regexAPI.perform();
		regexResponsepojo = regexAPI
				.getAPIResponseAsPOJO(RegexResponsepojo.class);
		String status = regexResponsepojo.getMessageCode();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider= "userData")
	public void A_03_Test_invalid_language(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
		final String methodName = "A_03_Test_invalid_language";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REGEX3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));

		currentNode.assignCategory("REST");
		
		RegexAPI regexAPI = new RegexAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		regexAPI.setContentType(_masterVO.getProperty("contentType"));
		String lng = _masterVO.getProperty("language");
		String crt =  DBHandler.AccessHandler.checkForCountry(lng);
		regexAPI.setCountry(crt);
		regexAPI.setLanguage("");
		regexAPI.setExpectedStatusCode(9044);
		regexAPI.perform();
		regexResponsepojo = regexAPI
				.getAPIResponseAsPOJO(RegexResponsepojo.class);
		String status = regexResponsepojo.getMessageCode();
		Assert.assertEquals(9044, Integer.parseInt(status));
		Assertion.assertEquals(status, "9044");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider= "userData")
	public void A_04_Test_invalid_country(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
		final String methodName = "A_04_Test_invalid_country";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REGEX4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));

		currentNode.assignCategory("REST");
		
		RegexAPI regexAPI = new RegexAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		regexAPI.setContentType(_masterVO.getProperty("contentType"));
		regexAPI.setCountry("");
		regexAPI.setLanguage(_masterVO.getProperty("language"));
		regexAPI.setExpectedStatusCode(241092);
		regexAPI.perform();
		regexResponsepojo = regexAPI
				.getAPIResponseAsPOJO(RegexResponsepojo.class);
		String status = regexResponsepojo.getMessageCode();
		Assert.assertEquals(241092, Integer.parseInt(status));
		Assertion.assertEquals(status, "241092");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider= "userData")
	public void A_05_Test_invalid_locale(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
		final String methodName = "A_05_Test_invalid_locale";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("REGEX5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		RegexAPI regexAPI = new RegexAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		regexAPI.setContentType(_masterVO.getProperty("contentType"));
		regexAPI.setCountry(new RandomGeneration().randomAlphabets(2));
		regexAPI.setLanguage(new RandomGeneration().randomAlphabets(2));
		regexAPI.setExpectedStatusCode(241093);
		regexAPI.perform();
		regexResponsepojo = regexAPI
				.getAPIResponseAsPOJO(RegexResponsepojo.class);
		String status = regexResponsepojo.getMessageCode();
		Assert.assertEquals(241093, Integer.parseInt(status));
		Assertion.assertEquals(status, "241093");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
}
