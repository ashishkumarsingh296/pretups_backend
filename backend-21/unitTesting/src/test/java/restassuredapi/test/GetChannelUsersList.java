package restassuredapi.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.classes.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

import restassuredapi.api.getchanneluserslist.GetChannelUsersListAPI;
import restassuredapi.api.getdomaincategory.GetDomainCategoryAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.getchanneluserslistresponsepojo.GetChannelUsersListResponsePojo;
import restassuredapi.pojo.getdomaincategoryrequestpojo.Data;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_GET_CHANNEL_USERS_LIST)
public class GetChannelUsersList extends BaseTest {
	static String moduleCode;
	GetChannelUsersListResponsePojo getChannelUsersListResponsePojo = new GetChannelUsersListResponsePojo();
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
	@TestManager(TestKey="PRETUPS-6173")
	public void A_01_Test_getChannelUsersList_positive(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
		int statusCode ;
		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && (getChannelUsersListResponsePojo.getMessage().contains("Success") || getChannelUsersListResponsePojo.getStatus()==204) ) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}


		Assert.assertEquals(statusCode,200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
					
	}
	
	
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6318")
	public void A_07_Test_invalidPageNUmber(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
	
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setPageNumber(_masterVO.getProperty("setN"));
		getChannelUsersListAPI.setEntriesPerPage(_masterVO.getProperty("bonusValue"));
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
//		int statusCode ;
//		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
//			statusCode = 200;
//		} else {
//			statusCode = 400;
//			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid Values For PageNumber or EntriesperPage.");
		Assertion.assertEquals(message, "Invalid Values For PageNumber or EntriesperPage.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6319")
	public void A_08_Test_blankUserNameWithcCat(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setCategory(categoryName);
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
//		int statusCode ;
//		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
//			statusCode = 200;
//		} else {
//			statusCode = 400;
//			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "With Username Domain,Category,GeographyCode is mandatory.");
		Assertion.assertEquals(message, "With Username Domain,Category,GeographyCode is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6320")
	public void A_09_Test_blankPageNumber(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setPageNumber(_masterVO.getProperty(""));
		getChannelUsersListAPI.setEntriesPerPage(_masterVO.getProperty("bonusValue"));
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
		int statusCode ;
		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "Please Provide both PageNumber & EntriesperPage.");
		Assertion.assertEquals(message, "Please Provide both PageNumber & EntriesperPage.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6321")
	public void A_10_Test_blankEntriesPerPage(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setPageNumber(_masterVO.getProperty("bonusValue"));
		getChannelUsersListAPI.setEntriesPerPage(_masterVO.getProperty(""));
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
		int statusCode ;
		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "Please Provide both PageNumber & EntriesperPage.");
		Assertion.assertEquals(message, "Please Provide both PageNumber & EntriesperPage.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6322")
	public void A_10_Test_onlyUserName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));

		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setUserName(userName);
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
		int statusCode ;
		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "With Username Domain,Category,GeographyCode is mandatory.");
		Assertion.assertEquals(message, "With Username Domain,Category,GeographyCode is mandatory.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
	 @Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6185")
	public void A_12_Test_invalidGeographywithUserName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String userName, String domainName) throws Exception
	{
		final String methodName = "Test_GetChannelUsersListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FCUL12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		GetChannelUsersListAPI getChannelUsersListAPI=new GetChannelUsersListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		getChannelUsersListAPI.setContentType(_masterVO.getProperty("contentType"));
		getChannelUsersListAPI.setUserName(userName);
		getChannelUsersListAPI.setCategory(categoryName);
		getChannelUsersListAPI.setGeography(new RandomGeneration().randomAlphabets(7));
		getChannelUsersListAPI.setDomain(domainName);

		
		getChannelUsersListAPI.setExpectedStatusCode(200);
		getChannelUsersListAPI.perform();
		getChannelUsersListResponsePojo = getChannelUsersListAPI
				.getAPIResponseAsPOJO(GetChannelUsersListResponsePojo.class);
		int statusCode ;
		if (getChannelUsersListResponsePojo != null && getChannelUsersListResponsePojo.getMessage() != null && getChannelUsersListResponsePojo.getMessage().contains("Success.")) {
			statusCode = 200;
		} else {
			statusCode = 400;
			}

		String message=getChannelUsersListResponsePojo.getMessage();
		Assert.assertEquals(message, "Invalid geography");
		Assertion.assertEquals(message, "Invalid geography");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	
}
