package restassuredapi.test;
import com.commons.ExcelI;
import com.utils.ExcelUtility;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2ctransferapprovallist.C2CTransferApprovalListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2ctransferapprovallistrequestpojo.C2CTransferApprovalListRequestPojo;
import restassuredapi.pojo.c2ctransferapprovallistrequestpojo.Data;
import restassuredapi.pojo.c2ctransferapprovallistresponsepojo.C2CTransferApprovalListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.REST_C2C_TRANSFER_APPROVAL_LIST)
public class C2CTransferApprovalListStock  extends BaseTest{
	
	static String moduleCode;
	C2CTransferApprovalListRequestPojo c2CTransferApprovalListRequestPojo = new C2CTransferApprovalListRequestPojo();
	C2CTransferApprovalListResponsePojo c2CTransferApprovalListResponsePojo = new C2CTransferApprovalListResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();

	 @DataProvider(name = "userData")
	    public Object[][] TestDataFed() {
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int rowCount = ExcelUtility.getRowCount();
	       
	        Object[][] Data = new Object[rowCount][7];
	        int j = 0;
	       
	        for (int i = 1; i <= rowCount; i++) {
	     
	                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
	                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
	                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	                Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	               
	                j++;
	            
	          
	        }
	        return Data;
	    }
	
	public void setupData(String categoryCode) {
		Object[][] login = DBHandler.AccessHandler.getChnlUserDetailsForRolecode(RolesI.C2C_TRANSFER_APPROVAL1, "DIST",categoryCode);
		String userID = DBHandler.AccessHandler.getUserIdLoginID(String.valueOf(login[0][0]));
		String domainCode = DBHandler.AccessHandler.getGrpDomainCode(userID);
		String domainName = DBHandler.AccessHandler.getGrpDomainName(domainCode);

		data.setApprovalLevel(_masterVO.getProperty("approvallevel1"));
		data.setCategory(_masterVO.getProperty("category"));	
		data.setDomain(_masterVO.getProperty("domain"));
		data.setEntriesPerPage("1");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setExtrefnum("");
		data.setGeographicalDomain(domainName);
		data.setMsisdn2("");
		data.setPageNumber("1");
		data.setRequestType("ALL");
		data.setTransactionId("");
		data.setUsernameToSearch("");
		data.setTransferType(_masterVO.getProperty("transferTypeStock"));
		c2CTransferApprovalListRequestPojo.setData(data);
	}
	Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("1357"));
		headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


	}


	// Successful data with valid data.

	protected static String accessToken;


	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
		//if(accessToken==null) {
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
		public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
			final String methodName = "Test_C2CTransferApprovalListStock";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CTAL1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(categorCode);
			
			C2CTransferApprovalListAPI c2CTransferApprovalListAPI = new C2CTransferApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CTransferApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2CTransferApprovalListAPI.addBodyParam(c2CTransferApprovalListRequestPojo);
			c2CTransferApprovalListAPI.perform();
			c2CTransferApprovalListResponsePojo = c2CTransferApprovalListAPI.getAPIResponseAsPOJO(C2CTransferApprovalListResponsePojo.class);
			String message = c2CTransferApprovalListResponsePojo.getDataObject().getMessage();
			if(message.equals("No Data Found on page 1.")) {
				Assertion.assertSkip("C2C Transaction approval list empty at given level.");
			}
			else {
			Assertion.assertEquals(message,"200:Successful");
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		


		@Test(dataProvider = "userData")
		public void A_09_Test_ExternalNetworkCodeIsBlank(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
			final String methodName = "Test_C2CTransferApprovalListStock";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CTAL9");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(categorCode);
			data.setExtnwcode("");
			c2CTransferApprovalListRequestPojo.setData(data);
			C2CTransferApprovalListAPI c2CTransferApprovalListAPI = new C2CTransferApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CTransferApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2CTransferApprovalListAPI.addBodyParam(c2CTransferApprovalListRequestPojo);
			c2CTransferApprovalListAPI.setExpectedStatusCode(400);
			c2CTransferApprovalListAPI.perform();
			c2CTransferApprovalListResponsePojo = c2CTransferApprovalListAPI.getAPIResponseAsPOJO(C2CTransferApprovalListResponsePojo.class);
			String message =c2CTransferApprovalListResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "External network code value is blank.");
			Assertion.assertEquals(message,"External network code value is blank.");		
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		
		@Test(dataProvider = "userData")
		public void A_10_Test_ExternalNetworkCodeIsInvalid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
			final String methodName = "Test_C2CTransferApprovalListStock";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CTAL10");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(categorCode);
			String nCode=new RandomGeneration().randomAlphaNumeric(4);
			data.setExtnwcode(nCode);
			c2CTransferApprovalListRequestPojo.setData(data);
			C2CTransferApprovalListAPI c2CTransferApprovalListAPI = new C2CTransferApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CTransferApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2CTransferApprovalListAPI.addBodyParam(c2CTransferApprovalListRequestPojo);
			c2CTransferApprovalListAPI.setExpectedStatusCode(400);
			c2CTransferApprovalListAPI.perform();
			c2CTransferApprovalListResponsePojo = c2CTransferApprovalListAPI.getAPIResponseAsPOJO(C2CTransferApprovalListResponsePojo.class);
			String message =c2CTransferApprovalListResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "External network code " + nCode +" is invalid.");
			Assertion.assertEquals(message, "External network code " + nCode +" is invalid.");		
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		public void A_16_Test_ApprovalLevel2(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
			final String methodName = "Test_C2CTransferApprovalListStock";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CTAL16");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(categorCode);
			data.setApprovalLevel(_masterVO.getProperty("approvallevel2"));
			c2CTransferApprovalListRequestPojo.setData(data);
			C2CTransferApprovalListAPI c2CTransferApprovalListAPI = new C2CTransferApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CTransferApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2CTransferApprovalListAPI.addBodyParam(c2CTransferApprovalListRequestPojo);
			c2CTransferApprovalListAPI.setExpectedStatusCode(400);
			c2CTransferApprovalListAPI.perform();
			c2CTransferApprovalListResponsePojo = c2CTransferApprovalListAPI.getAPIResponseAsPOJO(C2CTransferApprovalListResponsePojo.class);
			String message =c2CTransferApprovalListResponsePojo.getDataObject().getMessage();
			
			if(message.equals("No Data Found on page 1.")) {
				Assertion.assertSkip("No Data Found on page 1.");
			}
			else {
			Assertion.assertEquals(message,"200:Successful");
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		@Test(dataProvider = "userData")
		public void A_20_Test_ApprovalLevel3(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
			final String methodName = "Test_C2CTransferApprovalListStock";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CTAL40");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(categorCode);
			data.setApprovalLevel(_masterVO.getProperty("approvallevel3"));
			c2CTransferApprovalListRequestPojo.setData(data);
			C2CTransferApprovalListAPI c2CTransferApprovalListAPI = new C2CTransferApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CTransferApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2CTransferApprovalListAPI.addBodyParam(c2CTransferApprovalListRequestPojo);
			c2CTransferApprovalListAPI.setExpectedStatusCode(400);
			c2CTransferApprovalListAPI.perform();
			c2CTransferApprovalListResponsePojo = c2CTransferApprovalListAPI.getAPIResponseAsPOJO(C2CTransferApprovalListResponsePojo.class);
			String message =c2CTransferApprovalListResponsePojo.getDataObject().getMessage();
			
			if(message.equals("No Data Found on page 1.")) {
				Assertion.assertSkip("No Data Found on page 1.");
			}
			else {
			Assertion.assertEquals(message,"200:Successful");
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		



		

	
}
