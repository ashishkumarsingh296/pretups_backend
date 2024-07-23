package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.userpropertieslists.UserPropertiesListsAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userpropertieslistsresponsepojo.UserPropertiesListsResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.REST_USER_PROPERTIES_LISTS)
public class UserPropertiesLists extends BaseTest {
	static String moduleCode;
	UserPropertiesListsResponsePojo userPropertiesListsResponsePojo = new UserPropertiesListsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Map<String,String> Code = new HashMap<>();
	Map<String,String> parentGeo = new HashMap<>();
	
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][7];
		int j=0;
		for(int i=1;i<rowCount;i++) {
			String userCategory = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).toString();
			Object loginId= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Object password= ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Object msisdn = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Object pin= ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Object geography= ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i);
			for(int k=1;k<=rowCount;k++) {
				String parentCategory = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, k);
				
				if(userCategory.equals(parentCategory)) {
					Data[j][0]= loginId;
					Data[j][1] = password;
					Data[j][2] = msisdn;
					Data[j][3] = pin;
					Data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME,k);
					Data[j][5] = parentCategory;
					Data[j][6] = geography;
					
					j++;
				}
			
			
			}
		}
		
		Code.put("Super Distributor","DIST");
		Code.put("Dealer","SE"); 
		Code.put("Agent","AG");
		Code.put("Retailer","RET");
		
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
	@TestManager(TestKey="PRETUPS-6154")
	public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String categoryName, String parentName, String geography) throws Exception {

		final String methodName = "Test_UserPropertiesListsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPROPERTIESLISTS1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		UserPropertiesListsAPI userPropertiesListsAPI = new UserPropertiesListsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPropertiesListsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPropertiesListsAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userPropertiesListsAPI.setUserCategory(Code.get(categoryName));
		userPropertiesListsAPI.setParentCategory(Code.get(parentName));
		userPropertiesListsAPI.setParentGeography(geography);
		userPropertiesListsAPI.setParentUserId("");
		
		userPropertiesListsAPI.setExpectedStatusCode(200);
		userPropertiesListsAPI.perform();
		
		userPropertiesListsResponsePojo = userPropertiesListsAPI.getAPIResponseAsPOJO(UserPropertiesListsResponsePojo.class);

		Boolean statusCode = false;
		if (userPropertiesListsResponsePojo != null && userPropertiesListsResponsePojo.getMessage() != null && userPropertiesListsResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", userPropertiesListsResponsePojo.getMessage());
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6155")
	public void A_03_Test_NetworkCode_Invalid(String loginID, String password, String msisdn, String PIN, String categoryName, String parentName, String geography) throws Exception {

		final String methodName = "Test_UserPropertiesListsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPROPERTIESLISTS3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		UserPropertiesListsAPI userPropertiesListsAPI = new UserPropertiesListsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPropertiesListsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPropertiesListsAPI.setNetworkCode("N");
		userPropertiesListsAPI.setUserCategory(Code.get(categoryName));
		userPropertiesListsAPI.setParentCategory(Code.get(parentName));
		userPropertiesListsAPI.setParentGeography(geography);
		//userPropertiesListsAPI.setParentGeography(returnMap.get("Parent_Geography"));
		userPropertiesListsAPI.setParentUserId("");
		userPropertiesListsAPI.setExpectedStatusCode(200);
		userPropertiesListsAPI.perform();

		userPropertiesListsResponsePojo = userPropertiesListsAPI.getAPIResponseAsPOJO(UserPropertiesListsResponsePojo.class);

		Boolean statusCode = false;
		if (userPropertiesListsResponsePojo != null && userPropertiesListsResponsePojo.getMessageCode() != null && userPropertiesListsResponsePojo.getMessageCode().contains("1004011") ) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.completeAssertions();
		Assertion.assertEquals(String.valueOf(statusCode), "true");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6160")
	public void A_06_Test_Success_ParentUserId(String loginID, String password, String msisdn, String PIN, String categoryName, String parentName, String geography) throws Exception {

		final String methodName = "Test_UserPropertiesListsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPROPERTIESLISTS6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		UserPropertiesListsAPI userPropertiesListsAPI = new UserPropertiesListsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPropertiesListsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPropertiesListsAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userPropertiesListsAPI.setUserCategory(Code.get(categoryName));
		userPropertiesListsAPI.setParentCategory(Code.get(parentName));
		userPropertiesListsAPI.setParentGeography(geography);
		String userId = DBHandler.AccessHandler.getUserIdFromMsisdn(msisdn);
		userPropertiesListsAPI.setParentUserId(userId);
		
		userPropertiesListsAPI.setExpectedStatusCode(200);
		userPropertiesListsAPI.perform();
		
		userPropertiesListsResponsePojo = userPropertiesListsAPI.getAPIResponseAsPOJO(UserPropertiesListsResponsePojo.class);

		Boolean statusCode = false;
		if (userPropertiesListsResponsePojo != null && userPropertiesListsResponsePojo.getMessage() != null && userPropertiesListsResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", userPropertiesListsResponsePojo.getMessage());
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6157")
	public void A_07_Test_ParentNotAChiledOfLoggedInUser(String loginID, String password, String msisdn, String PIN, String categoryName, String parentName, String geography) throws Exception {

		final String methodName = "Test_UserPropertiesListsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPROPERTIESLISTS7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		UserPropertiesListsAPI userPropertiesListsAPI = new UserPropertiesListsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPropertiesListsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPropertiesListsAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userPropertiesListsAPI.setParentCategory(Code.get(categoryName));
		userPropertiesListsAPI.setUserCategory(Code.get(parentName));
		userPropertiesListsAPI.setParentGeography(geography);
		
		RandomGeneration random =new RandomGeneration();
		String userId= random.randomAlphaNumeric(10);
		userPropertiesListsAPI.setParentUserId(userId);
		
		userPropertiesListsAPI.setExpectedStatusCode(200);
		userPropertiesListsAPI.perform();
		
		userPropertiesListsResponsePojo = userPropertiesListsAPI.getAPIResponseAsPOJO(UserPropertiesListsResponsePojo.class);

		Boolean statusCode = false;
		if (userPropertiesListsResponsePojo != null && userPropertiesListsResponsePojo.getMessage() != null && userPropertiesListsResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(false, statusCode);
		Assertion.assertEquals("Parent user not found in the System", userPropertiesListsResponsePojo.getMessage());
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6159")
	public void A_09_Test_ParentCategoryInvalid(String loginID, String password, String msisdn, String PIN, String categoryName, String parentName, String geography) throws Exception {

		final String methodName = "Test_UserPropertiesListsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USERPROPERTIESLISTS9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		UserPropertiesListsAPI userPropertiesListsAPI = new UserPropertiesListsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userPropertiesListsAPI.setContentType(_masterVO.getProperty("contentType"));
		userPropertiesListsAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userPropertiesListsAPI.setUserCategory(categoryName);
		userPropertiesListsAPI.setParentCategory("SET");
		userPropertiesListsAPI.setParentGeography(geography);
		userPropertiesListsAPI.setParentUserId("");
		
		userPropertiesListsAPI.setExpectedStatusCode(200);
		userPropertiesListsAPI.perform();
		
		userPropertiesListsResponsePojo = userPropertiesListsAPI.getAPIResponseAsPOJO(UserPropertiesListsResponsePojo.class);

		Boolean statusCode = false;
		if (userPropertiesListsResponsePojo != null && userPropertiesListsResponsePojo.getMessage() != null && userPropertiesListsResponsePojo.getMessage().contains("Success")) {
					statusCode = true;
		} else {
			statusCode = false;
		}

		Assert.assertEquals(false, statusCode);
		Assertion.assertEquals("Parent user not found in the System", userPropertiesListsResponsePojo.getMessage());
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
