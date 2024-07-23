package restassuredapi.test;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cEnquiry.C2CEnquiryAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cenquiryrequestpojo.C2cAndO2cEnquiryRequestVO;
import restassuredapi.pojo.c2cenquiryresponsepojo.C2cAndO2cEnquiryResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_C2C_ENQUIRY)
public class C2CEnquiry  extends BaseTest{
	  static String moduleCode;
	  C2cAndO2cEnquiryResponsePojo c2cResponsepojo=new C2cAndO2cEnquiryResponsePojo();
	  C2cAndO2cEnquiryRequestVO c2cRequestPojo=new C2cAndO2cEnquiryRequestVO();
	  String toMsisdn,toMsisdn2="";
	  String unLoginId,unPassword,unCategoryName,unMsisdn,unPin;
	  LocalDate to_date=LocalDate.now();
	  LocalDate from_date=to_date.minusDays(25);
	  DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/YYYY");
	  String currentDate=formatter.format(to_date);
	  String fromDate=formatter.format(from_date);
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
			    toMsisdn=  (String)Data[j][2];
			    toMsisdn2=(String)Data[0][2];
			    String category=(String)Data[j][6];
			    if( category!=null&&category.equalsIgnoreCase("RET")||category.equalsIgnoreCase("AG") ) {
			    	unLoginId=(String)Data[j][0];
			    	unPassword=	(String)Data[j][1];	    	
			    	unCategoryName=(String)Data[j][5];
			    	unMsisdn=(String)Data[j][2];
			    	unPin=(String)Data[j][3];
			    }
				j++;
			}
			return Data;
		}
		
		HashMap<String,String> transferDetails=new HashMap<String,String>(); 
		public void setDataTransfer(String loginID, String msisdn,String categorCode,String geography,String domainName) {
			String transactionID = DBHandler.AccessHandler.fetchTransferIdWithStatus("NEW",_masterVO.getProperty("transferTypeStock"),PretupsI.C2C);
            String userId= DBHandler.AccessHandler.getUserIdLoginID(loginID); 
            String domainCode=DBHandler.AccessHandler.getDomainCode(domainName);
            String geographyCode=DBHandler.AccessHandler.getGeoCode(geography);
			
            transferDetails.put("transactionID", transactionID);
			transferDetails.put("userId", userId);
			transferDetails.put("categorCode", categorCode);
			transferDetails.put("geography", geographyCode);
			transferDetails.put("domainCode", domainCode);
			transferDetails.put("fromMsisdn",msisdn);	
		}
		
		public void setupDataForTranId() {
			c2cRequestPojo.setTransactionID(transferDetails.get("transactionID"));
            c2cRequestPojo.setCategory("");
            c2cRequestPojo.setDistributionType("");
            c2cRequestPojo.setDomain("");
            c2cRequestPojo.setFromDate("");
            c2cRequestPojo.setGeography("");
            c2cRequestPojo.setOrderStatus("");
            c2cRequestPojo.setProductCode("");
            c2cRequestPojo.setReceiverMsisdn("");
            c2cRequestPojo.setSenderMsisdn("");
            c2cRequestPojo.setToDate("");
            c2cRequestPojo.setTransferSubType("");
            c2cRequestPojo.setUserID("");
		}
		
		
		
		public void setupDataForMsisdn(String transferSubType) {
			c2cRequestPojo.setTransactionID("");
            c2cRequestPojo.setCategory("");
            c2cRequestPojo.setDistributionType("");
            c2cRequestPojo.setDomain("");
            c2cRequestPojo.setFromDate(fromDate);
            c2cRequestPojo.setGeography("");
            c2cRequestPojo.setOrderStatus("");
            c2cRequestPojo.setProductCode("");
            if(toMsisdn.equals(transferDetails.get("fromMsisdn"))) {
              c2cRequestPojo.setReceiverMsisdn(toMsisdn2);
            }
            else {
            c2cRequestPojo.setReceiverMsisdn(toMsisdn);
            }
            c2cRequestPojo.setSenderMsisdn(transferDetails.get("fromMsisdn"));
            c2cRequestPojo.setToDate(currentDate);
            c2cRequestPojo.setTransferSubType(transferSubType);
            c2cRequestPojo.setUserID("");
		}
		
		
		public void setupDataForAdvance(String transferSubType) {
			c2cRequestPojo.setTransactionID("");
            c2cRequestPojo.setCategory(transferDetails.get("categorCode"));
            c2cRequestPojo.setDistributionType("ALL");
            c2cRequestPojo.setDomain(transferDetails.get("domainCode"));
            c2cRequestPojo.setFromDate(fromDate);
            c2cRequestPojo.setGeography(transferDetails.get("geography"));
            c2cRequestPojo.setOrderStatus("ALL");
            c2cRequestPojo.setProductCode("ALL");
            c2cRequestPojo.setReceiverMsisdn("");
            c2cRequestPojo.setSenderMsisdn("");
            c2cRequestPojo.setToDate(currentDate);
            c2cRequestPojo.setTransferSubType(transferSubType);
            c2cRequestPojo.setUserID(transferDetails.get("userId"));
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
		   @TestManager(TestKey="PRETUPS-14960")
			public void A_01_c2c_Enquiry_TransactionId(String loginID, String password,
					String msisdn, String PIN, String parentName, String categoryName, String categorCode,String geography,String domainName) throws Exception {
				final String methodName = "A_01_c2c_Enquiry_TransactionId";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CE01");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase());
				currentNode.assignCategory("REST");
				setDataTransfer(loginID, msisdn, categorCode, geography,domainName);
				setupDataForTranId();
				C2CEnquiryAPI c2cEnquiryApi=new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
				c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
				c2cEnquiryApi.setEnquiryType("C2C");
				c2cEnquiryApi.setSearchBy("TRANSACTIONID");
				c2cEnquiryApi.setBodyParam(c2cRequestPojo);
				c2cEnquiryApi.perform();
				 c2cResponsepojo  = c2cEnquiryApi
						.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
				long statusCode = c2cResponsepojo.getStatus();
				Assert.assertEquals(statusCode, 200);
				Assertion.assertEquals(Long.toString(statusCode), "200");
				Assertion.completeAssertions();
			}
			
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-14961")
		public void A_02_c2c_Enquiry_Msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
				String categorCode,String geography,String domainName) throws Exception {
			final String methodName = "A_02_c2c_Enquiry_Msisdn";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CE02");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setDataTransfer(loginID, msisdn, categorCode, geography,domainName);
			setupDataForMsisdn("ALL");
			C2CEnquiryAPI c2cEnquiryApi=new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
			c2cEnquiryApi.setEnquiryType("C2C");
			c2cEnquiryApi.setSearchBy("MSISDN");
			c2cEnquiryApi.setBodyParam(c2cRequestPojo);
			c2cEnquiryApi.perform();
			 c2cResponsepojo  = c2cEnquiryApi
					.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
			long statusCode = c2cResponsepojo.getStatus();
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
		}
		
		
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-14962")
		public void A_03_c2c_Enquiry_Advance(String loginID, String password, String msisdn, String PIN, String parentName,
				String categoryName, String categorCode, String geography, String domainName) throws Exception {
			final String methodName = "A_03_c2c_Enquiry_Advance";
			Log.startTestCase(methodName);
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password, categoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN, categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CE03");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setDataTransfer(loginID, msisdn, categorCode, geography, domainName);
			setupDataForAdvance("ALL");
			C2CEnquiryAPI c2cEnquiryApi = new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
					accessToken);
			c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
			c2cEnquiryApi.setEnquiryType("C2C");
			c2cEnquiryApi.setSearchBy("ADVANCE");
			c2cEnquiryApi.setBodyParam(c2cRequestPojo);
			c2cEnquiryApi.perform();
			c2cResponsepojo = c2cEnquiryApi.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
			long statusCode = c2cResponsepojo.getStatus();
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
		}
		
		
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-14963")
		public void A_04_c2c_Enquiry_All_QueryParam_UserNt_Hierarchy(String loginID, String password, String msisdn, String PIN, String parentName, 
				String categoryName, String categorCode,String geography,String domainName) throws Exception {
			final String methodName = "A_04_c2c_Enquiry_All_QueryParam_UserNt_Hierarchy";
			Log.startTestCase(methodName);
			String messageCode="transferenquiry.enquirysearchattribute.msg.notauthorise";
			
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(unLoginId, unPassword,unCategoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(unMsisdn, unPin,unCategoryName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CE04");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setDataTransfer(loginID, msisdn, categorCode, geography, domainName);
			
			//transactionId
			setupDataForTranId();
			C2CEnquiryAPI c2cEnquiryApi = new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
					accessToken);
			c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
			c2cEnquiryApi.setEnquiryType("C2C");
			c2cEnquiryApi.setSearchBy("TRANSACTIONID");
			c2cEnquiryApi.setBodyParam(c2cRequestPojo);
			c2cEnquiryApi.perform();
			c2cResponsepojo = c2cEnquiryApi.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
			String messageCode1 = c2cResponsepojo.getMessageCode();

			// msisdn
			setupDataForMsisdn("ALL");
			c2cEnquiryApi = new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
			c2cEnquiryApi.setEnquiryType("C2C");
			c2cEnquiryApi.setSearchBy("MSISDN");
			c2cEnquiryApi.setBodyParam(c2cRequestPojo);
			c2cEnquiryApi.perform();
			c2cResponsepojo = c2cEnquiryApi.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
			String messageCode2 = c2cResponsepojo.getMessageCode();
			

			//advance
			setupDataForAdvance("ALL");
			c2cEnquiryApi = new C2CEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2cEnquiryApi.setContentType(_masterVO.getProperty("contentType"));
			c2cEnquiryApi.setEnquiryType("C2C");
			c2cEnquiryApi.setSearchBy("ADVANCE");
			c2cEnquiryApi.setBodyParam(c2cRequestPojo);
			c2cEnquiryApi.perform();
			c2cResponsepojo = c2cEnquiryApi.getAPIResponseAsPOJO(C2cAndO2cEnquiryResponsePojo.class);
			String messageCode3 = c2cResponsepojo.getMessageCode();
			 int unauthorised=0;
				if (messageCode1 != null && messageCode2 != null && messageCode3 != null) {
					if (messageCode1.contains(messageCode) && messageCode2.contains(messageCode)
							&& (messageCode3.contains(messageCode) || messageCode3.equals("2241288"))) {
						unauthorised = 1;
					}
				}
			Assert.assertEquals(1, unauthorised);
			Assertion.assertEquals(Long.toString(unauthorised), "1");
			Assertion.completeAssertions();
			
		}		
		
}
