package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.C2CBatchDownloadTemplate.C2CBatchDownloadTemplateApi;
import restassuredapi.api.getStafUsersApi.GetStaffUsersApi;
import restassuredapi.api.geto2cproducts.GetO2CProductsApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbatchdownloadtemplateresponsepojo.C2cBatchDownloadTemplateResponsePojo;
import restassuredapi.pojo.getStaffUsersResponsepojo.GetStaffUsersResponsepojo;
import restassuredapi.pojo.geto2cproductsresponsepojo.GetO2CProductsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.GET_STAFF_USERS)
public class GetStaffUsersTest extends BaseTest{

	static String moduleCode;
	GetStaffUsersResponsepojo getStaffUsersResponsepojo;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        ArrayList<String> opUserData =new ArrayList<String>();
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        
        Object[][] Data =new Object[rowCount][4];
        int j=0;
        for (int i = 1; i <=rowCount; i++) {
        	Data[j][0]=ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        	Data[j][1]=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
        	Data[j][2]=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
        	Data[j][3]=ExcelUtility.getCellData(0, ExcelI.PIN, i);
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
		@TestManager(TestKey="PRETUPS-13471")
		public void A_01_Test_success(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(LoginId, Password, CategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETSTAFFUSERS1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
			currentNode.assignCategory("REST");

			GetStaffUsersApi getStaffUsersApi = new GetStaffUsersApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			getStaffUsersApi.setContentType(_masterVO.getProperty("contentType"));
			getStaffUsersApi.setExpectedStatusCode(200);
			getStaffUsersApi.perform();
			getStaffUsersResponsepojo = getStaffUsersApi
					.getAPIResponseAsPOJO(GetStaffUsersResponsepojo.class);
			String messageCode = getStaffUsersResponsepojo.getStatus();
			
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
					     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
	 
	 	@Test(dataProvider= "userData")
		@TestManager(TestKey="PRETUPS-13472")
		public void A_02_Test_invalid_token(String categoryName,String loginID,String password,String pin) throws Exception {
			final String methodName = "A_02_Test_invalid_token";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETSTAFFUSERS2");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");

			GetStaffUsersApi getStaffUsersApi = new GetStaffUsersApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
			getStaffUsersApi.setContentType(_masterVO.getProperty("contentType"));
			getStaffUsersApi.setExpectedStatusCode(401);
			getStaffUsersApi.perform();
			
			getStaffUsersResponsepojo = getStaffUsersApi
					.getAPIResponseAsPOJO(GetStaffUsersResponsepojo.class);
			
			String status = getStaffUsersResponsepojo.getMessageCode();
			Assert.assertEquals(241018, Integer.parseInt(status));
			Assertion.assertEquals(status, "241018");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
}
