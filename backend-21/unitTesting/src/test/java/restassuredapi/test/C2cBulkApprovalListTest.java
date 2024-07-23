package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
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
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cbulkapprovallistapi.C2cBulkApprovalListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbulkapprovallistresponsepojo.C2cBulkApprovalListResponsePojo;
import restassuredapi.pojo.c2cbulkapprovallistresponsepojo.MasterErrorList;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.REST_GET_C2C_BULK_APP_LIST)
public class C2cBulkApprovalListTest extends BaseTest {
	
	static String moduleCode;
    C2cBulkApprovalListResponsePojo c2cBulkApprovalListResponsePojo = new C2cBulkApprovalListResponsePojo();
    C2cBulkApprovalListAPI c2cBulkApprovalListAPI = null;
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
	@TestManager(TestKey="PRETUPS-14848")
	public void A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETC2CBULKAPPLIST1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		c2cBulkApprovalListAPI = new C2cBulkApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2cBulkApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cBulkApprovalListAPI.setDomain("ALL");
		c2cBulkApprovalListAPI.setCategory(categorCode);
		c2cBulkApprovalListAPI.setGeography("ALL");
		c2cBulkApprovalListAPI.setExpectedStatusCode(200);
		c2cBulkApprovalListAPI.perform();

		c2cBulkApprovalListResponsePojo = c2cBulkApprovalListAPI.getAPIResponseAsPOJO(C2cBulkApprovalListResponsePojo.class);

		String statusCode = c2cBulkApprovalListResponsePojo.getStatus();

		Assert.assertEquals(Integer.parseInt(statusCode), 200);
		Assertion.assertEquals(statusCode, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-14849")
	public void A_02_Test_accessToken_empty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_02_Test_accessToken_empty";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETC2CBULKAPPLIST2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		c2cBulkApprovalListAPI = new C2cBulkApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), "abc");
		
		c2cBulkApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cBulkApprovalListAPI.setDomain("ALL");
		c2cBulkApprovalListAPI.setCategory(categorCode);
		c2cBulkApprovalListAPI.setGeography("ALL");
		c2cBulkApprovalListAPI.setExpectedStatusCode(401);
		c2cBulkApprovalListAPI.perform();
		c2cBulkApprovalListResponsePojo = c2cBulkApprovalListAPI.getAPIResponseAsPOJO(C2cBulkApprovalListResponsePojo.class);

		String message = c2cBulkApprovalListResponsePojo.getMessage();
        
		Assert.assertEquals(message, "Invalid Token format.");
		Assertion.assertEquals(message,"Invalid Token format.");  
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-14850")
	public void A_03_Test_InvalidIdValue(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {

		final String methodName = "A_03_Test_InvalidIdValue";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETC2CBULKAPPLIST3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		c2cBulkApprovalListAPI = new C2cBulkApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		
		c2cBulkApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cBulkApprovalListAPI.setDomain("ALL");
		c2cBulkApprovalListAPI.setCategory(null);
		c2cBulkApprovalListAPI.setGeography("ALL");
		c2cBulkApprovalListAPI.setExpectedStatusCode(400);
		c2cBulkApprovalListAPI.perform();

		c2cBulkApprovalListResponsePojo = c2cBulkApprovalListAPI.getAPIResponseAsPOJO(C2cBulkApprovalListResponsePojo.class);

		List<MasterErrorList> masterErrorList =  c2cBulkApprovalListResponsePojo.getErrorMap().getMasterErrorList();
		String message = "";
		if(masterErrorList.size() > 0) {
			message = masterErrorList.get(0).getErrorMsg();
		}

		Assert.assertEquals(message, "Category cannot be blank or alphanumeric.");
		Assertion.assertEquals(message, "Category cannot be blank or alphanumeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
//
//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6349")
//	public void A_07_Test_InvalidSubUser(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {
//
//		final String methodName = "A_07_Test_InvalidSubUser";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS7");
//		moduleCode = CaseMaster.getModuleCode();
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//	
//		FetchUserDetailsAPI c2cBulkApprovalListAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2cBulkApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
//		c2cBulkApprovalListAPI.setIdType(_masterVO.getProperty("loginId"));
//		String name=new RandomGeneration().randomAlphabets(7);
//		c2cBulkApprovalListAPI.setidValue(name);
//		c2cBulkApprovalListAPI.setExpectedStatusCode(400);
//		c2cBulkApprovalListAPI.perform();
//
//		c2cBulkApprovalListResponsePojo = c2cBulkApprovalListAPI.getAPIResponseAsPOJO(C2cBulkApprovalListResponsePojo.class);
//
//		String message = c2cBulkApprovalListResponsePojo.getMessage();
//
//		Assert.assertEquals(message, "No user exists with this login ID("+name+").");
//		Assertion.assertEquals(message, "No user exists with this login ID("+name+").");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//
//	
//	}
//	
//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6362")
//	public void A_09_Test_UserStatusIsResumed(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception {
//		
//
//		final String methodName = "A_09_Test_UserStatusIsResumed";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHUSERDETAILS9");
//		moduleCode = CaseMaster.getModuleCode();
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//
//		c2cBulkApprovalListAPI = new FetchUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//
//		c2cBulkApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
//		c2cBulkApprovalListAPI.setIdType(_masterVO.getProperty("loginId"));
//		c2cBulkApprovalListAPI.setidValue(loginID);
//		c2cBulkApprovalListAPI.setExpectedStatusCode(200);
//		c2cBulkApprovalListAPI.perform();
//
//		c2cBulkApprovalListResponsePojo = c2cBulkApprovalListAPI.getAPIResponseAsPOJO(C2cBulkApprovalListResponsePojo.class);
//		boolean statusCode = false;
//
//
//		if (c2cBulkApprovalListResponsePojo != null  && c2cBulkApprovalListResponsePojo.getBarredUserDetails() !=null) {
//					statusCode = true;
//		} else {
//			statusCode = false;
//		}
//		String message = c2cBulkApprovalListResponsePojo.getMessage();
//		
//		Assert.assertEquals(false, statusCode );
//		Assertion.assertEquals(message, "Success");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//
//	
//	}

	}
