package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.fetchuserdetails.FetchUserDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.userdelete.UserDeleteAPI;
import restassuredapi.pojo.fetchuserdetailsresponsepojo.FetchUserDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userdeleterequestpojo.UserDeleteRequestPojo;
import restassuredapi.pojo.userdeleteresponsepojo.UserDeleteResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.Assertion;
import com.utils.Decrypt;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.REST_USER_DELETE)
public class UserDeleteTest extends BaseTest{

	
	static String moduleCode;
	UserDeleteResponsePojo userDeleteResponsePojo = new UserDeleteResponsePojo();
    OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>();

	HashMap<String, String> returnMap = new HashMap<String, String>();
	HashMap<String, String> childMap = new HashMap<String, String>();
	String userIdChild ="";
	


	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[1][10];
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
			if(j==1) {
				break;
			}
			
			
		}
		return Data;
	}
	
	
	
	public HashMap<String, String> getChildData(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		
	
		for (int i = 1; i <= rowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("SE") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				childMap.put("LOGIN_ID2", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				childMap.put("PASSWORD2", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
				childMap.put("MSISDN2", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				childMap.put("PIN2", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				childMap.put("USERID2", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
		
		String userIdChild = DBHandler.AccessHandler.getUserIdLoginID(childMap.get("LOGIN_ID2"));
		return childMap;
		
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
	@TestManager(TestKey="PRETUPS-13368")
	public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_01_Test_Success_LoginId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		
		getChildData();
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
		userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
		userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
		userDeleteAPI.setRemarks("TEST");
		userDeleteAPI.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userDeleteAPI.setExpectedStatusCode(200);
		userDeleteAPI.perform();

		userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

		String statusCode = userDeleteResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(statusCode, "200");
		
		
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13369")
	public void A_02_Test_DeletedUser_Invalid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_02_Test_DeletedUser_Invalid";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		String invalidIdType ="login";
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChildData();
		String userIdChild2 = DBHandler.AccessHandler.getUserIdLoginID(childMap.get("LOGIN_ID2"));
		DBHandler.AccessHandler.updateAnyColumnValue("USERS", "STATUS",
				"N", "USER_ID", userIdChild2);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
		userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
		userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
		userDeleteAPI.setRemarks("TEST");
		userDeleteAPI.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		userDeleteAPI.setExpectedStatusCode(400);
		userDeleteAPI.perform();

		userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

		String statusCode = userDeleteResponsePojo.getStatus();
		String message = userDeleteResponsePojo.getMessage();
		
		DBHandler.AccessHandler.updateAnyColumnValue("USERS", "LOGIN_ID",
				childMap.get("LOGIN_ID2"), "USER_ID",userIdChild2);
		DBHandler.AccessHandler.updateAnyColumnValue("USERS", "STATUS",
				"Y", "USER_ID", userIdChild2);
		
		Assert.assertEquals(message, "User is either invalid or is already deleted/canceled.");
		Assertion.assertEquals(message, "User is either invalid or is already deleted/canceled.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		

	
}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13370")
	public void A_03_Test_BlankExtNwCOde(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_03_Test_BlankExtNwCOde";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
	userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
	userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
	userDeleteAPI.setRemarks("TEST");
	userDeleteAPI.setExtnwcode("");
	userDeleteAPI.setExpectedStatusCode(400);
	userDeleteAPI.perform();

	userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

	String statusCode = userDeleteResponsePojo.getStatus();
	String message = userDeleteResponsePojo.getMessage();
	
	Assert.assertEquals(message, "External network code value is blank.");
	Assertion.assertEquals(message, "External network code value is blank.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

	
	}
	

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13371")
	public void A_04_Test_InvalidExternalCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_04_Test_InvalidExternalCode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
	userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
	userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
	userDeleteAPI.setRemarks("TEST");
	userDeleteAPI.setExtnwcode(externalCode);
	userDeleteAPI.setExpectedStatusCode(400);
	userDeleteAPI.perform();

	userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

	String statusCode = userDeleteResponsePojo.getStatus();
	String message = userDeleteResponsePojo.getMessage();
	
	Assert.assertEquals(message, "External network code "+externalCode+" is invalid.");
	Assertion.assertEquals(message, "External network code "+externalCode+" is invalid.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);	
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13372")
	public void A_05_Test_InvalidIDtype(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_05_Test_InvalidIDtype";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
	userDeleteAPI.setIdType(externalCode);
	userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
	userDeleteAPI.setRemarks("TEST");
	userDeleteAPI.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
	userDeleteAPI.setExpectedStatusCode(400);
	userDeleteAPI.perform();

	userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

	String statusCode = userDeleteResponsePojo.getStatus();
	String message = userDeleteResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Invalid user.");
	Assertion.assertEquals(message, "Invalid user.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);	
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13373")
	public void A_06_Test_BlankRemark(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_06_Test_BlankRemark";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
	userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
	userDeleteAPI.setidValue(childMap.get("LOGIN_ID2"));
	userDeleteAPI.setRemarks("");
	userDeleteAPI.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
	userDeleteAPI.setExpectedStatusCode(400);
	userDeleteAPI.perform();

	userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

	String statusCode = userDeleteResponsePojo.getStatus();
	String message = userDeleteResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Remarks Required");
	Assertion.assertEquals(message, "Remarks Required");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);	
	}
	
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13374")
	public void A_07_Test_InvalidUSer(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_07_Test_InvalidUSer";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("USRDELETE7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		UserDeleteAPI userDeleteAPI = new UserDeleteAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		
		userDeleteAPI.setContentType(_masterVO.getProperty("contentType"));
	userDeleteAPI.setIdType(_masterVO.getProperty("loginId"));
	userDeleteAPI.setidValue(externalCode);
	userDeleteAPI.setRemarks("Test");
	userDeleteAPI.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
	userDeleteAPI.setExpectedStatusCode(400);
	userDeleteAPI.perform();

	userDeleteResponsePojo = userDeleteAPI.getAPIResponseAsPOJO(UserDeleteResponsePojo.class);

	String statusCode = userDeleteResponsePojo.getStatus();
	String message = userDeleteResponsePojo.getMessage();
	
	Assert.assertEquals(message, "User is either invalid or is already deleted/canceled.");
	Assertion.assertEquals(message, "User is either invalid or is already deleted/canceled.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);	
	}
	

	




}
