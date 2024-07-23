package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.suspendResume.suspendResumeTestAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.suspendResumerequestpojo.Data1;
import restassuredapi.pojo.suspendResumerequestpojo.SuspendResumeRequestPojo;
import restassuredapi.pojo.suspendResumerespnsepojo.SuspendResumeResponsePojo;

@ModuleManager(name = Module.REST_SUSPEND_RESUME)
public class suspendResumeTest extends BaseTest {
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	SuspendResumeRequestPojo suspendResumeRequestPojo = new SuspendResumeRequestPojo();
	SuspendResumeResponsePojo suspendResumeResponsePojo= new SuspendResumeResponsePojo();
	Data1 data = new Data1();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[1][8];
		int j  = 0;
		for (int i = 1; i < 2; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i+1);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i+1);
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
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


	}
	public void setupData(String logn,String msisdn)
	{
		data.setLoginid(logn);
		data.setMsisdn(msisdn);
		data.setRemarks("");
		data.setReqType("");
		data.setUserType("");
		suspendResumeRequestPojo.setData(data);


}
	// Successful data with valid data.

		protected static String accessToken;


		public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
			final String methodName = "Test_OAuthenticationTest";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));

			currentNode.assignCategory("REST");

			setHeaders();
			setupAuth(data1, data2);
			OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
			oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
			oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
			oAuthenticationAPI.setExpectedStatusCode(200);
			oAuthenticationAPI.perform();
			oAuthenticationResponsePojo = oAuthenticationAPI
					.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
			long statusCode = oAuthenticationResponsePojo.getStatus();

			accessToken = oAuthenticationResponsePojo.getToken();
			org.testng.Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		@Test(dataProvider = "userData")
		
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_01_Test_success";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR1");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData(ChloginID,Chmsisdn);
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
//			DBHandler.AccessHandler.getSystemPreference("REQ_CUSER_SUS_APP");
			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
			//if we have change the preference, then change back to its default value
//			if(changed) {
//				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEF");
//			
			data.setReqType("suspend");
			data.setUserType("channel");
			data.setRemarks("success");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(200);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			data.setReqType("resume");
			data.setUserType("channel");
			data.setRemarks("success");
		  suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(200);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			statusCode = suspendResumeResponsePojo.getStatus();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test(dataProvider = "userData")
		public void A_02_Test_Blank_Remark(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_02_Test_Blank_Remark";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR2");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData(ChloginID,Chmsisdn);
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);

			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
		
			data.setReqType("suspend");
			data.setUserType("channel");
			data.setRemarks("");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(400);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test(dataProvider = "userData")
		public void A_03_Test_Blank_REQUIREDTYPE(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_03_Test_Blank_REQUIREDTYPE";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR3");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData(ChloginID,Chmsisdn);
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);

			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
		
			data.setReqType("");
			data.setUserType("channel");
			data.setRemarks("blank required type");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(400);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test(dataProvider = "userData")
		public void A_04_Test_Blank_USER_TYPE(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_04_Test_Blank_USER_TYPE";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR4");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData("","");
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);

			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
		
			data.setReqType("suspend");
			data.setUserType("");
			data.setRemarks("blank userType");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(400);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test(dataProvider = "userData")
		public void A_05_Test_Blank_LOGINID_AND_MSISDN(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_05_Test_Blank_LOGINID_AND_MSISDN";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR5");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData("","");
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);

			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
		
			data.setReqType("suspend");
			data.setUserType("channel");
			data.setRemarks("blank loginId");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(400);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		@Test(dataProvider = "userData")
		public void A_06_Test_USER_CANNOT_SUSPEND_HIMSELF(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName,String ChloginID,String Chmsisdn) throws Exception
		{
			final String methodName = "A_06_Test_Blank_LOGINID_AND_MSISDN";
			 Log.startTestCase(methodName);
		        if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("SR5");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
				currentNode.assignCategory("REST");
			setupData(loginID,msisdn);
			String REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);

			Boolean changed = false;
			if(REQ_CUSER_SUS_APP.equals("true")) {
				changed =true;
				DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","false","PREFERENCE_CODE","REQ_CUSER_SUS_APP");
			}
			REQ_CUSER_SUS_APP = DBHandler.AccessHandler.getSystemPreferenceDefaultValue(PretupsI.REQ_CUSER_SUS_APP);
		
			data.setReqType("suspend");
			data.setUserType("channel");
			data.setRemarks("user cannot suspend himself");
			suspendResumeTestAPI suspendResumeTestAPI = new suspendResumeTestAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			suspendResumeTestAPI.setContentType(_masterVO.getProperty("contentType"));
			suspendResumeTestAPI.addBodyParam(suspendResumeRequestPojo);
			suspendResumeTestAPI.setExpectedStatusCode(400);
			suspendResumeTestAPI.perform();
			suspendResumeResponsePojo = suspendResumeTestAPI.getAPIResponseAsPOJO(SuspendResumeResponsePojo.class);
			long statusCode = suspendResumeResponsePojo.getStatus();

			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		
}
