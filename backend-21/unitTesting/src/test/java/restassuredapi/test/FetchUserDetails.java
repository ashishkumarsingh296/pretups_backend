package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
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

import restassuredapi.api.fetchuserdetails.FetchUserDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.fetchuserdetailsresponsepojo.FetchUserDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.REST_FETCH_USER_DETAILS)
public class FetchUserDetails extends BaseTest {
	
	static String moduleCode;
    FetchUserDetailsResponsePojo fetchUserDetailsResponsePojo = new FetchUserDetailsResponsePojo();
    OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>();

	HashMap<String, String> returnMap = new HashMap<String, String>();


	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][10];
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
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, i);
			Data[j][9] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
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
	protected static String accessToken;
	
	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}
    
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
	@TestManager(TestKey="PRETUPS-6346")
	public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_01_Test_Success_LoginId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUserDetailsAPI fetchUserDetailsAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		fetchUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		fetchUserDetailsAPI.setIdType(_masterVO.getProperty("loginId"));
		fetchUserDetailsAPI.setidValue(loginID);
		fetchUserDetailsAPI.setExpectedStatusCode(200);
		fetchUserDetailsAPI.perform();

		fetchUserDetailsResponsePojo = fetchUserDetailsAPI.getAPIResponseAsPOJO(FetchUserDetailsResponsePojo.class);

		long statusCode = fetchUserDetailsResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6359")
	public void A_04_Test_idType_Invalid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_04_Test_idType_Invalid";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		String invalidIdType ="login";
		FetchUserDetailsAPI fetchUserDetailsAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		
		fetchUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		fetchUserDetailsAPI.setIdType(invalidIdType);
		fetchUserDetailsAPI.setidValue(loginID);
		fetchUserDetailsAPI.setExpectedStatusCode(400);
		fetchUserDetailsAPI.perform();
		fetchUserDetailsResponsePojo = fetchUserDetailsAPI.getAPIResponseAsPOJO(FetchUserDetailsResponsePojo.class);

		String message = fetchUserDetailsResponsePojo.getMessage();
		
        System.out.println(message);
        
		Assert.assertEquals(message, "Invalid idType(" +invalidIdType +").");
		Assertion.assertEquals(message,"Invalid idType(" +invalidIdType +").");  
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6360")
	public void A_05_Test_InvalidIdValue(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_05_Test_InvalidIdValue";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUserDetailsAPI fetchUserDetailsAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		
		fetchUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		fetchUserDetailsAPI.setIdType(_masterVO.getProperty("loginId"));
		String name=new RandomGeneration().randomAlphabets(7);
		fetchUserDetailsAPI.setidValue(name);
		fetchUserDetailsAPI.setExpectedStatusCode(400);
		fetchUserDetailsAPI.perform();

		fetchUserDetailsResponsePojo = fetchUserDetailsAPI.getAPIResponseAsPOJO(FetchUserDetailsResponsePojo.class);

		String message = fetchUserDetailsResponsePojo.getMessage();

		Assert.assertEquals(message, "No user exists with this login ID("+name+").");
		Assertion.assertEquals(message, "No user exists with this login ID("+name+").");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6349")
	public void A_07_Test_InvalidSubUser(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_07_Test_InvalidSubUser";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		FetchUserDetailsAPI fetchUserDetailsAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		fetchUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		fetchUserDetailsAPI.setIdType(_masterVO.getProperty("loginId"));
		String name=new RandomGeneration().randomAlphabets(7);
		fetchUserDetailsAPI.setidValue(name);
		fetchUserDetailsAPI.setExpectedStatusCode(400);
		fetchUserDetailsAPI.perform();

		fetchUserDetailsResponsePojo = fetchUserDetailsAPI.getAPIResponseAsPOJO(FetchUserDetailsResponsePojo.class);

		String message = fetchUserDetailsResponsePojo.getMessage();

		Assert.assertEquals(message, "No user exists with this login ID("+name+").");
		Assertion.assertEquals(message, "No user exists with this login ID("+name+").");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6362")
	public void A_09_Test_UserStatusIsResumed(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {
		

		final String methodName = "A_09_Test_UserStatusIsResumed";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUserDetailsAPI fetchUserDetailsAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		fetchUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		fetchUserDetailsAPI.setIdType(_masterVO.getProperty("loginId"));
		fetchUserDetailsAPI.setidValue(loginID);
		fetchUserDetailsAPI.setExpectedStatusCode(200);
		fetchUserDetailsAPI.perform();

		fetchUserDetailsResponsePojo = fetchUserDetailsAPI.getAPIResponseAsPOJO(FetchUserDetailsResponsePojo.class);
		boolean statusCode = false;


		if (fetchUserDetailsResponsePojo != null  && fetchUserDetailsResponsePojo.getBarredUserDetails() !=null) {
					statusCode = true;
		} else {
			statusCode = false;
		}
		String message = fetchUserDetailsResponsePojo.getMessage();
		
		Assert.assertEquals(false, statusCode );
		Assertion.assertEquals(message, "Success");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	}
