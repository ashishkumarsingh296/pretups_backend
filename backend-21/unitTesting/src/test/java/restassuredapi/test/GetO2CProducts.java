package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import restassuredapi.api.geto2cproducts.GetO2CProductsApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.geto2cproductsresponsepojo.GetO2CProductsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.GET_O2C_PRODUCTS)
public class GetO2CProducts extends BaseTest{
	
	
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate = df.format(dateobj);   
	static String moduleCode;
	GetO2CProductsResponsePojo getO2CProductsRepsonsePojo = new GetO2CProductsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        ArrayList<String> opUserData =new ArrayList<String>();
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        
        Object[][] Data =new Object[rowCount+1][4];
        for (int i = 1; i <= rowCount; i++) {
        	Data[i][0]=ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        	Data[i][1]=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
        	Data[i][2]=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
        	Data[i][3]=ExcelUtility.getCellData(0, ExcelI.PIN, i);
        }
        
        
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));
        
        for(int i=0 ; i<Data[0].length ; i++) {
        	Data[0][i] = opUserData.get(i);
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
		@TestManager(TestKey="PRETUPS-10199")
		public void A_01_Test_success(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(LoginId, Password, CategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETO2CPRO1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
			currentNode.assignCategory("REST");

			GetO2CProductsApi getO2CProductsApi = new GetO2CProductsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			getO2CProductsApi.setContentType(_masterVO.getProperty("contentType"));
			getO2CProductsApi.setExpectedStatusCode(200);
			getO2CProductsApi.perform();
			getO2CProductsRepsonsePojo = getO2CProductsApi
					.getAPIResponseAsPOJO(GetO2CProductsResponsePojo.class);
			String messageCode = getO2CProductsRepsonsePojo.getStatus();
			
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
					     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-10201")
		public void A_02_Test_invalid_token(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
			final String methodName = "A_02_Test_invalid_token";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(LoginId, Password, CategoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("GETO2CPRO2");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
			currentNode.assignCategory("REST");

			GetO2CProductsApi getO2CProductsApi = new GetO2CProductsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken + new RandomGeneration().randomAlphabets(4));
			getO2CProductsApi.setContentType(_masterVO.getProperty("contentType"));
			getO2CProductsApi.setExpectedStatusCode(401);
			getO2CProductsApi.perform();
			getO2CProductsRepsonsePojo = getO2CProductsApi
					.getAPIResponseAsPOJO(GetO2CProductsResponsePojo.class);
			String message = getO2CProductsRepsonsePojo.getMessage();
			
			Assert.assertEquals("Invalid Token format.",message);
			Assertion.assertEquals(message, "Invalid Token format.");
					     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		

}
