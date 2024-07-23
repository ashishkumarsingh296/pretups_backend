package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.o2cinitiatebyopt.O2CInitiateByOptAPI;
import restassuredapi.api.o2cstockapprovalapi.O2CStockApprovalAPI;
import restassuredapi.api.o2cwithdrawal.O2CWithdrawalAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2ctransferstockrequestpojo.Paymentdetail;
import restassuredapi.pojo.o2CStockApprovalRequestPojo.O2CStockApprovalRequestPojo;
import restassuredapi.pojo.o2CStockApprovalRequestPojo.O2cStockAppRequest;
import restassuredapi.pojo.o2CStockApprovalRequestPojo.PaymentDetails;
import restassuredapi.pojo.o2CStockApprovalRequestPojo.Product;
import restassuredapi.pojo.o2CStockApprovalResponsePojo.O2CStockApprovalResponsePojo;
import restassuredapi.pojo.o2cinitiatedbyoptresponsepojo.O2CInitiateByOptResponse;
import restassuredapi.pojo.o2cinitiateoptreqpojo.O2CInitiateByOptReqData;
import restassuredapi.pojo.o2cinitiateoptreqpojo.O2CInitiateByOptRequest;
import restassuredapi.pojo.o2cwithdrawalrequestpojo.O2CWithdrawalRequestData;
import restassuredapi.pojo.o2cwithdrawalrequestpojo.O2CWithdrawalRequestPojo;
import restassuredapi.pojo.o2cwithdrawalrequestpojo.ProductW;
import restassuredapi.pojo.o2cwithdrawalresponsepojo.O2CWithdrawalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.Products;

@ModuleManager(name = Module.O2C_WITHDRAWAL)
public class O2CWithdrawal extends BaseTest{
	
	
	DateFormat df = null;
    Date dateobj = null;
    String currentDate = null;   
	static String moduleCode;
	O2CInitiateByOptRequest o2CInitiateByOptRequest = new O2CInitiateByOptRequest();
	O2CInitiateByOptResponse o2CInitiateByOptResponse = new O2CInitiateByOptResponse();
	O2CStockApprovalRequestPojo o2CStockApprovalRequestPojo = new O2CStockApprovalRequestPojo();
	O2CStockApprovalResponsePojo o2CStockApprovalResponsePojo= new O2CStockApprovalResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	O2CWithdrawalRequestPojo o2CWithdrawalRequestPojo =new O2CWithdrawalRequestPojo();
	O2CWithdrawalResponsePojo o2CWithdrawalResponsePojo =new O2CWithdrawalResponsePojo(); 
			
	O2CInitiateByOptReqData data = new O2CInitiateByOptReqData();
	O2cStockAppRequest o2cStockAppRequest =new O2cStockAppRequest();
	O2CWithdrawalRequestData o2CWithdrawalRequestData = new O2CWithdrawalRequestData();
	
	 @DataProvider(name = "userData")
	    public Object[][] TestDataFeed() {
//	        String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
		    String O2CWithdrawalCode = _masterVO.getProperty("O2CWithdrawCode");
	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        
	        
	        ArrayList<String> opUserData =new ArrayList<String>();
//	        ArrayList<String> userInfo = UserAccessRevamp.getCategoryWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
	        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
	        opUserData.add(userInfo.get("CATEGORY_NAME"));
	        opUserData.add(userInfo.get("LOGIN_ID"));
	        opUserData.add(userInfo.get("PASSWORD"));
	        opUserData.add(userInfo.get("PIN"));
	        
	
	     
	        
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	        int rowCount = ExcelUtility.getRowCount();
	        /*
	         * Array list to store Categories for which O2C transfer is allowed
	         */
	        ArrayList<String> alist1 = new ArrayList<String>();
	        for (int i = 1; i <= rowCount; i++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
	            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
	            if (aList.contains(O2CWithdrawalCode)) {
	                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
	                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
	            }
	        }

	        /*
	         * Counter to count number of users exists in channel users hierarchy sheet
	         * of Categories for which O2C transfer is allowed
	         */
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	        int chnlCount = ExcelUtility.getRowCount();
	        int userCounter = 0;
	        for (int i = 1; i <= chnlCount; i++) {
	            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
	                userCounter++;
	            }
	        }

	        /*
	         * Store required data of 'O2C transfer allowed category' users in Object
	         */
	        Object[][] Data = new Object[userCounter][3];
	        for (int i = 1, j = 0; i <= chnlCount; i++) {
	            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
	                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
	                j++;
	            }
	        }

	        /*
	         * Store products from Product Sheet to Object.
	         */
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
	        int prodRowCount = ExcelUtility.getRowCount();
	        Object[] ProductObject = new Object[prodRowCount];
	        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
	            ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, j);
	        }

	        /*
	         * Creating combination of channel users for each product.
	         */
	        int countTotal = ProductObject.length * userCounter;
	        Object[][] o2ctmpData = new Object[countTotal][4];
	        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
	            o2ctmpData[j][0] = Data[k][0];
	            o2ctmpData[j][1] = Data[k][1];
	            o2ctmpData[j][2] = Data[k][2];
	            o2ctmpData[j][3] = ProductObject[i];
	            if (k < userCounter) {
	                k++;
	                if (k >= userCounter) {
	                    k = 0;
	                    i++;
	                    if (i >= ProductObject.length)
	                        i = 0;
	                }
	            } else {
	                k = 0;
	            }
	        }
	        
	    
	        Object[][] o2cData =new Object[countTotal][8];
	        
	        int counter_1=0;
	        	
	        for(int k=0;k<o2ctmpData.length;k++) {
	        	int counter_2=0;
	        		
	        	for(int j=0;j<opUserData.size();j++) 
	        	o2cData[counter_1][counter_2++]=opUserData.get(j);
	        		
	        	for(int l=0;l<o2ctmpData[0].length;l++) 
	        	o2cData[counter_1][counter_2++]=o2ctmpData[k][l];
	        			
	        	counter_1++;
	        	}
	      
	        return o2cData;
	        
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
		
		 public void setupData(String msisdn, String productCode,String opCategoryName,String pin) {
			 	
			 	DateFormat df = new SimpleDateFormat(DBHandler.AccessHandler.getSystemPreference("SYSTEM_DATE_FORMAT"));
			 	Date dateobj = new Date();
			 	String currentDate = df.format(dateobj);
				List<Products>products = new ArrayList<Products>();
				Products product = new Products();
				product.setProductcode(productCode);
				product.setQty(_masterVO.getProperty("Quantity"));
				products.add(product);
				data.setProducts(products);
				
				List<Paymentdetail> paymentdetails  = new ArrayList<Paymentdetail>();
				Paymentdetail paymentdetail = new Paymentdetail();
				paymentdetail.setPaymentinstnumber(new RandomGeneration().randomNumeric(5));
				paymentdetail.setPaymentdate(currentDate);
				paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
				paymentdetails.add(paymentdetail);
				data.setPaymentdetails(paymentdetails);
				
				data.setLanguage(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
				data.setRefnumber("");
				data.setMsisdn(msisdn);
				data.setRemarks(_masterVO.getProperty("Remarks"));
				
				String pinAllowed = DBHandler.AccessHandler.pinPreferenceForTXN(opCategoryName);
				
				if(pinAllowed.equals("Y")){
					data.setPin(pin);
				}
				
				List<O2CInitiateByOptReqData> o2CInitiateByOptReqData = new ArrayList<>();
				o2CInitiateByOptReqData.add(data);
				
				o2CInitiateByOptRequest.setData(o2CInitiateByOptReqData);
				
			}
		 
		 public void setupO2cApproval(String msisdn, String productCode,String txnId,String pin) {
			 
			 DateFormat df = new SimpleDateFormat(DBHandler.AccessHandler.getSystemPreference("SYSTEM_DATE_FORMAT"));
			 Date dateobj = new Date();
			 String currentDate = df.format(dateobj);
			 PaymentDetails paymentdetail = new PaymentDetails();
			 paymentdetail.setPaymentInstNumber(new RandomGeneration().randomNumeric(5));
			 paymentdetail.setPaymentDate(currentDate);
			 paymentdetail.setPaymentType(_masterVO.getProperty("paymentInstrumentCode"));
			 o2cStockAppRequest.setPaymentDetails(paymentdetail);
			 
			 List<Product> products = new ArrayList<Product>();
			 Product product = new Product();
			 product.setProductCode(productCode);
			 product.setAppQuantity(_masterVO.getProperty("Quantity"));
			 products.add(product);
			 o2cStockAppRequest.setProducts(products);
			 
			 o2cStockAppRequest.setCurrentStatus(_masterVO.getProperty("aprvLvl1"));
			 o2cStockAppRequest.setExtTxnDate(currentDate);
			 o2cStockAppRequest.setExtTxnNumber(new RandomGeneration().randomNumberWithoutZero(6));
			 o2cStockAppRequest.setRefNumber(new RandomGeneration().randomNumeric(9));
			 o2cStockAppRequest.setRemarks(new RandomGeneration().randomAlphabets(10));
			 o2cStockAppRequest.setStatus(_masterVO.getProperty("approve"));
			 o2cStockAppRequest.setToMsisdn(msisdn);
			 o2cStockAppRequest.setTxnDate(currentDate);
			 o2cStockAppRequest.setTxnId(txnId);
			 
			 List<O2cStockAppRequest> o2cStockAppRequests = new ArrayList<>();
			 o2cStockAppRequests.add(o2cStockAppRequest);
				
			 o2CStockApprovalRequestPojo.setO2cStockAppRequests(o2cStockAppRequests);
			 
		 }
		 
		 public O2CStockApprovalResponsePojo performO2cStockApproval() throws IOException {
			 
			 O2CStockApprovalAPI o2CStockApprovalAPI = new O2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			 o2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
			 o2CStockApprovalAPI.addBodyParam(o2CStockApprovalRequestPojo);
			 o2CStockApprovalAPI.perform();
			 o2CStockApprovalResponsePojo = o2CStockApprovalAPI
						.getAPIResponseAsPOJO(O2CStockApprovalResponsePojo.class);
			 
			 return o2CStockApprovalResponsePojo;
		 }
		 
		 public void setupO2CWithdrawal(String productCode, String pin, String chMsisdn) {
			 List<ProductW> products = new ArrayList<ProductW>();
			 ProductW product = new ProductW();
			 product.setProductcode(productCode);
			 product.setQty(_masterVO.getProperty("Quantity"));
			 products.add(product);
			 o2CWithdrawalRequestData.setProducts(products);
			 
			 o2CWithdrawalRequestData.setLanguage(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
			 o2CWithdrawalRequestData.setRemarks(_masterVO.getProperty("Remarks"));
			 o2CWithdrawalRequestData.setPin(pin);
			 String fromUserId=DBHandler.AccessHandler.getUserIdFromMsisdn(chMsisdn);
			 o2CWithdrawalRequestData.setFromUserId(fromUserId);
			 o2CWithdrawalRequestData.setWalletType(_masterVO.getProperty("FreeOfCost"));
			 
			 List<O2CWithdrawalRequestData> o2CWithdrawalRequestDataList = new ArrayList<O2CWithdrawalRequestData>();
			 o2CWithdrawalRequestDataList.add(o2CWithdrawalRequestData);
			 
			 o2CWithdrawalRequestPojo.setData(o2CWithdrawalRequestDataList);
		 }
		 
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-9798")
		public void A_01_Test_success(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chMsisdn,productCode,opCategoryName,opPin);

			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
			o2CInitiateByOptAPI.perform();
			o2CInitiateByOptResponse = o2CInitiateByOptAPI
					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
			String messageCode = o2CInitiateByOptResponse.getStatus();
			
			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 

 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
		    		 response = performO2cStockApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
		    	        response = performO2cStockApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			//O2C Withdrawal
		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupO2CWithdrawal(productCode,opPin,chMsisdn);
			
			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
			o2CWithdrawalAPI.perform();
			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
			messageCode = o2CWithdrawalResponsePojo.getStatus();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
		}
		
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-9798")
		public void A_02_Blank_Product(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_02_Blank_Product";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chMsisdn,productCode,opCategoryName,opPin);
			Log.info(chMsisdn +"***** " + productCode +" *******" + opCategoryName +" **************" + opPin);

			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
			o2CInitiateByOptAPI.perform();
			o2CInitiateByOptResponse = o2CInitiateByOptAPI
					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
			System.out.println(o2CInitiateByOptResponse +"******************************************");
			String messageCode = o2CInitiateByOptResponse.getStatus();
			
			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			
			
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 
 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
		    		 response = performO2cStockApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
		    	        response = performO2cStockApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			//O2C Withdrawal
		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD2");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupO2CWithdrawal(productCode,opPin,chMsisdn);
			List<ProductW> emptyList = null;
			o2CWithdrawalRequestPojo.getData().get(0).setProducts(emptyList);
			
			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
			o2CWithdrawalAPI.perform();
			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
			
			String errorcode = o2CWithdrawalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
			Assert.assertEquals(241070, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241070");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-9798")
		public void A_03_Blank_Remarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_03_Blank_Remarks";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chMsisdn,productCode,opCategoryName,opPin);

			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
			o2CInitiateByOptAPI.perform();
			o2CInitiateByOptResponse = o2CInitiateByOptAPI
					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
			String messageCode = o2CInitiateByOptResponse.getStatus();
			
			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 
 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
		    		 response = performO2cStockApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
		    	        response = performO2cStockApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			//O2C Withdrawal
		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD3");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupO2CWithdrawal(productCode,opPin,chMsisdn);
			o2CWithdrawalRequestPojo.getData().get(0).setWalletType("");
			
			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
			o2CWithdrawalAPI.perform();
			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
			
			String errorcode = o2CWithdrawalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
			Assert.assertEquals(241160, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "241160");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}

		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-9798")
		public void A_04_Blank_WalletType(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_04_Blank_WalletType";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chMsisdn,productCode,opCategoryName,opPin);

			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
			o2CInitiateByOptAPI.perform();
			o2CInitiateByOptResponse = o2CInitiateByOptAPI
					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
			String messageCode = o2CInitiateByOptResponse.getStatus();
			
			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 
 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
		    		 response = performO2cStockApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
		    	        response = performO2cStockApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			//O2C Withdrawal
		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD4");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupO2CWithdrawal(productCode,opPin,chMsisdn);
			o2CWithdrawalRequestPojo.getData().get(0).setRemarks("");
			
			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
			o2CWithdrawalAPI.perform();
			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
			
			String errorcode = o2CWithdrawalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
			Assert.assertEquals(3031, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "3031");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-9798")
		public void A_05_Invalid_Pin(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_05_Invalid_Pin";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupData(chMsisdn,productCode,opCategoryName,opPin);

			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
			o2CInitiateByOptAPI.perform();
			o2CInitiateByOptResponse = o2CInitiateByOptAPI
					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
			String messageCode = o2CInitiateByOptResponse.getStatus();
			
			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
			Assert.assertEquals(200, Integer.parseInt(messageCode));
			Assertion.assertEquals(messageCode, "200");
			
			
			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		     Long firstApprov = Long.parseLong(approvalLevel[0]);
		     Long secondApprov = Long.parseLong(approvalLevel[1]);
		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
		     
		     
		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
    		 String status = response.getStatus();
    		 
    		 Assert.assertEquals(200, Integer.parseInt(status));
 			 Assertion.assertEquals(status, "200");
 			 
 			 Log.info("Level 1 success !!");
		     
		    if (netPayableAmount > firstApprov) { // Approval 2
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
		    		 response = performO2cStockApproval();
		    		 status = response.getStatus();
		    		 
		    		 Assert.assertEquals(200, Integer.parseInt(status));
		 			 Assertion.assertEquals(status, "200");
		 			 
		 			 Log.info("Level 2 success !!");
		    }
		    	 
		    else if (netPayableAmount > secondApprov) { // Approval 3
		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
		    	        response = performO2cStockApproval();
		   		    	status = response.getStatus();
		   		    		 
		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
		   		    	 Assertion.assertEquals(status, "200");
		   		 			 
		   		    	 Log.info("Level 3 success !!");
		    	    }
		     
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			//O2C Withdrawal
		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD5");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupO2CWithdrawal(productCode,opPin,chMsisdn);
			o2CWithdrawalRequestPojo.getData().get(0).setPin(new RandomGeneration().randomNumeric(4));
			
			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
			o2CWithdrawalAPI.perform();
			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
			
			String errorcode = o2CWithdrawalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
			Assert.assertEquals(7015, Integer.parseInt(errorcode));
			Assertion.assertEquals(errorcode, "7015");
			
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
//		@Test(dataProvider = "userData")
//		@TestManager(TestKey="PRETUPS-9798")
//		public void A_06_Invalid_Qty(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
//			final String methodName = "A_06_Invalid_Qty";
//			Log.startTestCase(methodName);
//			if(_masterVO.getProperty("identifierType").equals("loginid"))
//				BeforeMethod(opLoginId, opPassword,opCategoryName);
//
//			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
//			moduleCode = CaseMaster.getModuleCode();
//
//			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
//			currentNode.assignCategory("REST");
//			setupData(chMsisdn,productCode,opCategoryName,opPin);
//
//			O2CInitiateByOptAPI o2CInitiateByOptAPI = new O2CInitiateByOptAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
//			o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
//			o2CInitiateByOptAPI.addBodyParam(o2CInitiateByOptRequest);
//			o2CInitiateByOptAPI.perform();
//			o2CInitiateByOptResponse = o2CInitiateByOptAPI
//					.getAPIResponseAsPOJO(O2CInitiateByOptResponse.class);
//			String messageCode = o2CInitiateByOptResponse.getStatus();
//			
//			String txnId=o2CInitiateByOptResponse.getSuccessList().get(0).getTransactionId();
//			Assert.assertEquals(200, Integer.parseInt(messageCode));
//			Assertion.assertEquals(messageCode, "200");
//			
//			
//			CaseMaster = _masterVO.getCaseMasterByID("O2CSA1");
//			moduleCode = CaseMaster.getModuleCode();
//
//			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
//			currentNode.assignCategory("REST");
//			setupO2cApproval(chMsisdn, productCode, txnId, opPin);
//			 String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName,_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		     Long firstApprov = Long.parseLong(approvalLevel[0]);
//		     Long secondApprov = Long.parseLong(approvalLevel[1]);
//		     Long netPayableAmount = (long) DBHandler.AccessHandler.getNetPayableAmt(txnId);
//		     
//		     
//		     O2CStockApprovalResponsePojo response = performO2cStockApproval();
//    		 String status = response.getStatus();
//    		 
//    		 Assert.assertEquals(200, Integer.parseInt(status));
// 			 Assertion.assertEquals(status, "200");
// 			 
// 			 Log.info("Level 1 success !!");
//		     
//		    if (netPayableAmount > firstApprov) { // Approval 2
//		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
//		    		 response = performO2cStockApproval();
//		    		 status = response.getStatus();
//		    		 
//		    		 Assert.assertEquals(200, Integer.parseInt(status));
//		 			 Assertion.assertEquals(status, "200");
//		 			 
//		 			 Log.info("Level 2 success !!");
//		    }
//		    	 
//		    else if (netPayableAmount > secondApprov) { // Approval 3
//		    	o2CStockApprovalRequestPojo.getO2cStockAppRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
//		    	        response = performO2cStockApproval();
//		   		    	status = response.getStatus();
//		   		    		 
//		   		    	 Assert.assertEquals(200, Integer.parseInt(status));
//		   		    	 Assertion.assertEquals(status, "200");
//		   		 			 
//		   		    	 Log.info("Level 3 success !!");
//		    	    }
//		     
//			Assertion.completeAssertions();
//			Log.endTestCase(methodName);
//			
//			//O2C Withdrawal
//		    CaseMaster = _masterVO.getCaseMasterByID("O2CWITHD4");
//			moduleCode = CaseMaster.getModuleCode();
//
//			currentNode = test.createNode(CaseMaster.getExtentCase());
//			currentNode.assignCategory("REST");
//			setupO2CWithdrawal(productCode,opPin,chMsisdn);
//			o2CWithdrawalRequestPojo.getData().get(0).getProducts().get(0).setQty(_masterVO.getProperty("negativeValue"));
//			
//			O2CWithdrawalAPI o2CWithdrawalAPI = new O2CWithdrawalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
//			o2CWithdrawalAPI.setContentType(_masterVO.getProperty("contentType"));
//			o2CWithdrawalAPI.addBodyParam(o2CWithdrawalRequestPojo);
//			o2CWithdrawalAPI.perform();
//			o2CWithdrawalResponsePojo = o2CWithdrawalAPI
//					.getAPIResponseAsPOJO(O2CWithdrawalResponsePojo.class);
//			
//			String errorcode = o2CWithdrawalResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
//			Assert.assertEquals(3031, Integer.parseInt(errorcode));
//			Assertion.assertEquals(errorcode, "3031");
//			
//			Assertion.completeAssertions();
//			Log.endTestCase(methodName);
//
//		}
		
}
