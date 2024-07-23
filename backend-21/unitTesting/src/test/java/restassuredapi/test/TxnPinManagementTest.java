package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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

import junit.framework.Assert;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.txnpinmg.TxnPinManagementApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.txnpinmgresponsepojo.TxnPinMgResponsePojo;



@ModuleManager(name = Module.TRANSACTION_PIN_MANAGEMENT)
public class TxnPinManagementTest  extends BaseTest{
	static String moduleCode;
	 TxnPinMgResponsePojo respojo=new TxnPinMgResponsePojo();
	 OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	 OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
     
    
	 @DataProvider(name ="userData")
		public Object[][] TestDataFeed() {
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			Object[][] Data = new Object[rowCount][9];
			int j  = 0;
			for (int i = 1; i <= rowCount; i++) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				Data[j][7] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i);
				Data[j][8] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
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
		
		

		public void setupAuth(String data1, String data2) {
			oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
			oAuthenticationRequestPojo.setIdentifierValue(data1);
			oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
		}
		
		
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
				org.testng.Assert.assertEquals(statusCode, 200);
				Assertion.assertEquals(Long.toString(statusCode), "200");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);


			}
		 
		 
		 @Test(dataProvider = "userData")
		   @TestManager(TestKey="PRETUPS-15274")
			public void A_01(String loginID, String password,
					String msisdn, String PIN, String parentName, String categoryName, String categorCode,String geography,String domainName) throws Exception {
				final String methodName = "A_01";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PIN_MANAGEMENT01");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory("REST");
				TxnPinManagementApi api=new TxnPinManagementApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
				api.setContentType(_masterVO.getProperty("contentType"));
				api.setMsisdn(msisdn);
				api.perform();
				 respojo  = api
						.getAPIResponseAsPOJO(TxnPinMgResponsePojo.class);
			    long statusCode = respojo.getStatus();
				boolean isPinCgReq=respojo.isPinChangeRequired();
				int check=0;
				if(statusCode==200&& !isPinCgReq) {
					check=1;
				}
				
				Assert.assertEquals(1, check);
				Assertion.assertEquals(Long.toString(check), "1");
				Assertion.completeAssertions();
			}
		 
		 
		 @Test(dataProvider = "userData")
		   @TestManager(TestKey="PRETUPS-15275")
			public void A_02(String loginID, String password,
					String msisdn, String PIN, String parentName, String categoryName, String categorCode,String geography,String domainName) throws Exception {
				final String methodName = "A_02";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PIN_MANAGEMENT02");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory("REST");
				TxnPinManagementApi api=new TxnPinManagementApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
				api.setContentType(_masterVO.getProperty("contentType"));
				api.setMsisdn(msisdn);
				api.perform();
				 respojo  = api
						.getAPIResponseAsPOJO(TxnPinMgResponsePojo.class);
				 long statusCode = respojo.getStatus();
				 boolean isPinCgReq=respojo.isPinChangeRequired();
					int check=0;
					if(statusCode==200&& !isPinCgReq) {
						check=1;
					}
				
				Assert.assertEquals(1, check);
				Assertion.assertEquals(Long.toString(check), "1");
				Assertion.completeAssertions();
			}
		 
		 
		 @Test(dataProvider = "userData")
		   @TestManager(TestKey="PRETUPS-15276")
			public void A_03(String loginID, String password,
					String msisdn, String PIN, String parentName, String categoryName, String categorCode,String geography,String domainName) throws Exception {
				final String methodName = "A_02";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PIN_MANAGEMENT03");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory("REST");
				TxnPinManagementApi api=new TxnPinManagementApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
				api.setContentType(_masterVO.getProperty("contentType"));
				api.setMsisdn(msisdn);
				api.perform();
				 respojo  = api
						.getAPIResponseAsPOJO(TxnPinMgResponsePojo.class);
				long statusCode = respojo.getStatus();
				boolean isPinCgReq=respojo.isPinChangeRequired();
				int check=0;
				if(statusCode==200&& !isPinCgReq) {
					check=1;
				}
				
				Assert.assertEquals(1, check);
				Assertion.assertEquals(Long.toString(check), "1");
				Assertion.completeAssertions();
			}
}
