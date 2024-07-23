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

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import restassuredapi.api.fetchusernameautosearch.FetchUsernameAutosearchAPI;
import restassuredapi.pojo.fetchusernameautosearchresponsepojo.FetchUsernameAutosearchResponsePojo;


@ModuleManager(name = Module.FETCH_USERNAME_AUTOSEARCH)
public class FetchUsernameAutosearch extends BaseTest {
	static String moduleCode;
	FetchUsernameAutosearchResponsePojo fetchUsernameAutosearchResponsePojo = new FetchUsernameAutosearchResponsePojo(); // need to edit
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
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
	@TestManager(TestKey="PRETUPS-001")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FUAUTOSEARCH1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUsernameAutosearchAPI fetchUsernameAutosearchAPI=new FetchUsernameAutosearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchUsernameAutosearchAPI.getContentType();
		fetchUsernameAutosearchAPI.setCategoryCode(_masterVO.getProperty("categoryCode")); 
		fetchUsernameAutosearchAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchUsernameAutosearchAPI.setGeography(_masterVO.getProperty("geography"));
		fetchUsernameAutosearchAPI.setUserName(_masterVO.getProperty("userName"));
		fetchUsernameAutosearchAPI.setExpectedStatusCode(200);
		fetchUsernameAutosearchAPI.perform();
		fetchUsernameAutosearchResponsePojo = fetchUsernameAutosearchAPI
				.getAPIResponseAsPOJO(FetchUsernameAutosearchResponsePojo.class);
		int statusCode ;
		if (fetchUsernameAutosearchResponsePojo != null && Integer.parseInt(fetchUsernameAutosearchResponsePojo.getStatus())==200) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}
		
		Assert.assertEquals(statusCode,200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);			
	}
    
    // blank - categoryCode field
    @Test(dataProvider = "userData")
   	@TestManager(TestKey="PRETUPS-002")
    public void A_02_Test_Negative2_FetchUsernameAutosearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "A_02_Test_Negative2_FetchUsernameAutosearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FUAUTOSEARCH2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUsernameAutosearchAPI fetchUsernameAutosearchAPI=new FetchUsernameAutosearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchUsernameAutosearchAPI.getContentType();
		fetchUsernameAutosearchAPI.setCategoryCode("");
		fetchUsernameAutosearchAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchUsernameAutosearchAPI.setGeography(_masterVO.getProperty("geography"));
		fetchUsernameAutosearchAPI.setUserName(_masterVO.getProperty("userName"));
		fetchUsernameAutosearchAPI.setExpectedStatusCode(400);
		fetchUsernameAutosearchAPI.perform();
		fetchUsernameAutosearchResponsePojo = fetchUsernameAutosearchAPI
				.getAPIResponseAsPOJO(FetchUsernameAutosearchResponsePojo.class);
		
		String errorCode =fetchUsernameAutosearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(Integer.parseInt(errorCode), 1021005);
		Assertion.assertEquals(errorCode,"1021005");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
    
 // blank - domainCode field
    @Test(dataProvider = "userData")
   	@TestManager(TestKey="PRETUPS-003")
    public void A_03_Test_Negative3_FetchUsernameAutosearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "A_03_Test_Negative3_FetchUsernameAutosearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FUAUTOSEARCH3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUsernameAutosearchAPI fetchUsernameAutosearchAPI=new FetchUsernameAutosearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchUsernameAutosearchAPI.getContentType();
		fetchUsernameAutosearchAPI.setCategoryCode(_masterVO.getProperty("categoryCode"));
		fetchUsernameAutosearchAPI.setDomainCode("");
		fetchUsernameAutosearchAPI.setGeography(_masterVO.getProperty("geography"));
		fetchUsernameAutosearchAPI.setUserName(_masterVO.getProperty("userName")); 
		fetchUsernameAutosearchAPI.setExpectedStatusCode(400);
		fetchUsernameAutosearchAPI.perform();
		fetchUsernameAutosearchResponsePojo = fetchUsernameAutosearchAPI
				.getAPIResponseAsPOJO(FetchUsernameAutosearchResponsePojo.class);
		String errorCode =fetchUsernameAutosearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(Integer.parseInt(errorCode), 241197);
		Assertion.assertEquals(errorCode,"241197");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);		
	}

    // blank - geography field
    @Test(dataProvider = "userData")
   	@TestManager(TestKey="PRETUPS-004")
    public void A_04_Test_Negative4_FetchUsernameAutosearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "A_04_Test_Negative4_FetchUsernameAutosearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FUAUTOSEARCH4");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUsernameAutosearchAPI fetchUsernameAutosearchAPI=new FetchUsernameAutosearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchUsernameAutosearchAPI.getContentType();
		fetchUsernameAutosearchAPI.setCategoryCode(_masterVO.getProperty("categoryCode"));
		fetchUsernameAutosearchAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchUsernameAutosearchAPI.setGeography("");
		fetchUsernameAutosearchAPI.setUserName(_masterVO.getProperty("userName"));
		fetchUsernameAutosearchAPI.setExpectedStatusCode(400);
		fetchUsernameAutosearchAPI.perform();
		fetchUsernameAutosearchResponsePojo = fetchUsernameAutosearchAPI
				.getAPIResponseAsPOJO(FetchUsernameAutosearchResponsePojo.class);
		String errorCode =fetchUsernameAutosearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(Integer.parseInt(errorCode), 1020005);
		Assertion.assertEquals(errorCode,"1020005");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
/*		String messageCode =fetchUsernameAutosearchResponsePojo.getMessage();
		Assert.assertEquals(messageCode,"Invalid geography or empty");
		Assertion.assertEquals(messageCode, "Invalid geography or empty");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);*/			
	}
    
    // blank - userName field
    @Test(dataProvider = "userData")
   	@TestManager(TestKey="PRETUPS-005")
    public void A_05_Test_Negative5_FetchUsernameAutosearch(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "A_05_Test_Negative5_FetchUsernameAutosearch";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FUAUTOSEARCH5");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		FetchUsernameAutosearchAPI fetchUsernameAutosearchAPI=new FetchUsernameAutosearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchUsernameAutosearchAPI.getContentType();
		fetchUsernameAutosearchAPI.setCategoryCode(_masterVO.getProperty("categoryCode"));
		fetchUsernameAutosearchAPI.setDomainCode(_masterVO.getProperty("domainCode"));
		fetchUsernameAutosearchAPI.setGeography(_masterVO.getProperty("geography"));
		fetchUsernameAutosearchAPI.setUserName("comviva");
		fetchUsernameAutosearchAPI.setExpectedStatusCode(400);
		fetchUsernameAutosearchAPI.perform();
		fetchUsernameAutosearchResponsePojo = fetchUsernameAutosearchAPI
				.getAPIResponseAsPOJO(FetchUsernameAutosearchResponsePojo.class);
		String errorCode =fetchUsernameAutosearchResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		
		Assert.assertEquals(Integer.parseInt(errorCode), 1003006);
		Assertion.assertEquals(errorCode,"1003006");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);		
	}
	
}