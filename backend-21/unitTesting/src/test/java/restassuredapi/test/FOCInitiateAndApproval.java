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

import restassuredapi.api.focInitiateApi.FOCInititaeAPI;
import restassuredapi.api.focapproval.FOCApprovalAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.focInitiateReponsePojo.FocInititateResponsePojo;
import restassuredapi.pojo.focInitiateRequestPojo.Datum;
import restassuredapi.pojo.focInitiateRequestPojo.FocInititateRequestPojo;
import restassuredapi.pojo.focInitiateRequestPojo.FocProduct;
import restassuredapi.pojo.focapprovalrequestpojo.FOCApprovalRequestPojo;
import restassuredapi.pojo.focapprovalrequestpojo.FocApprovalRequest;
import restassuredapi.pojo.focapprovalresponsepojo.FOCApprovalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.FOC_APPROVAL)
public class FOCInitiateAndApproval extends BaseTest {

	DateFormat df = null;
	Date dateobj = null;
	String currentDate = null;
	String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	FocInititateRequestPojo focInititateRequestPojo = new FocInititateRequestPojo();
	FocInititateResponsePojo focInitiateReponsePojo = new FocInititateResponsePojo();
	
	FocApprovalRequest focApprovalRequest = new FocApprovalRequest();
	FOCApprovalRequestPojo focApprovalRequestPojo = new FOCApprovalRequestPojo();
	FOCApprovalResponsePojo focApprovalResponsePojo = new FOCApprovalResponsePojo();
	restassuredapi.pojo.focInitiateRequestPojo.Datum data = new restassuredapi.pojo.focInitiateRequestPojo.Datum();
	RandomGeneration rnd = new RandomGeneration();

	String searchCategoryCode = null;

	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        
        ArrayList<String> opUserData =new ArrayList<String>();
        //ArrayList<String> userInfo = UserAccessRevamp.getCategoryWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("MSISDN"));
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
            if (aList.contains(O2CTransferCode)) {
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
        
    
        Object[][] o2cData =new Object[countTotal][9];
        
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
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	protected static String accessToken;

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {

		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	public void setupFOCInitiate(String pin,String Msisdn) {
	
		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		data.setMsisdn2(Msisdn);
		data.setPin(Integer.parseInt(pin));
		data.setRefnumber(Integer.parseInt(new RandomGeneration().randomNumeric(7)));
		data.setRemarks(_masterVO.getProperty("Remarks"));
		FocProduct focProduct = new FocProduct();
		focProduct.setAppQuantity(Integer.parseInt(_masterVO.getProperty("DefappQty")));
		focProduct.setProductCode(_masterVO.getProperty("ProductCode"));
		List<FocProduct> focProducts = new ArrayList<FocProduct>();
		focProducts.add(focProduct);
		data.setFocProducts(focProducts);
		List list = new ArrayList<>();
		list.add(data);
		focInititateRequestPojo.setData(list);
	}
	
	 public void setupFOCApproval(String msisdn, String productCode,String txnId) {
		 
		 DateFormat df = new SimpleDateFormat(DBHandler.AccessHandler.getSystemPreference("SYSTEM_DATE_FORMAT"));
		 Date dateobj = new Date();
		 String currentDate = df.format(dateobj);
		 focApprovalRequest.setCurrentStatus(_masterVO.getProperty("aprvLvl1"));
		 focApprovalRequest.setExtTxnDate(currentDate);
		 focApprovalRequest.setExtTxnNumber(new RandomGeneration().randomNumberWithoutZero(6));
		 focApprovalRequest.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		 focApprovalRequest.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		 focApprovalRequest.setRefNumber(new RandomGeneration().randomNumeric(9));
		 focApprovalRequest.setRemarks(new RandomGeneration().randomAlphabets(10));
		 focApprovalRequest.setStatus(_masterVO.getProperty("approve"));
		 focApprovalRequest.setToMsisdn(msisdn);
		 focApprovalRequest.setTxnId(txnId);
		 
		 List<FocApprovalRequest> focApprovalRequests = new ArrayList<>();
		 focApprovalRequests.add(focApprovalRequest);
			
		 focApprovalRequestPojo.setFocApprovalRequests(focApprovalRequests);
		 
	 }
	 
	 public FOCApprovalResponsePojo performFOCApproval() throws IOException {
		 
		 FOCApprovalAPI focApprovalAPI = new FOCApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		 focApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
		 focApprovalAPI.addBodyParam(focApprovalRequestPojo);
		 focApprovalAPI.perform();
		 focApprovalResponsePojo = focApprovalAPI
					.getAPIResponseAsPOJO(FOCApprovalResponsePojo.class);
		 
		 return focApprovalResponsePojo;
	 }

	// Successful data with valid data
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-13090")
		public void A_01_Test_success(String opCategoryName,String opLoginId,String opPassword,String opMsisdn, String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_01_Test_success";
			Log.startTestCase(methodName);
			
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(opMsisdn, opPin,opCategoryName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupFOCInitiate(opPin,chMsisdn);
			List lst = focInititateRequestPojo.getData();
			data =(Datum) lst.get(0);
			data.getFocProducts().get(0).setAppQuantity(Integer.parseInt(_masterVO.getProperty("userappQty")));
			
			FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
			focInititaeAPI.addBodyParam(focInititateRequestPojo);
			focInititaeAPI.setExpectedStatusCode(201);
			focInititaeAPI.perform();
			
			focInitiateReponsePojo = focInititaeAPI.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		    String status = focInitiateReponsePojo.getStatus();
		    Assert.assertEquals(200, Integer.parseInt(status));
			// Assertion.assertEquals(status, "200");
			
			String txnId=focInitiateReponsePojo.getSuccessList().get(0).getTransactionId();
			
			
			// FOC APPROVAL 
			
			// int approvalLevelPreference = Integer.parseInt( DBHandler.AccessHandler.getSystemPreferenceDefaultValue("FOC_ODR_APPROVAL_LVL") );
			
			CaseMaster = _masterVO.getCaseMasterByID("FOCAPPRV1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName, chCategoryName));
			currentNode.assignCategory("REST");
			
			setupFOCApproval(chMsisdn, productCode, txnId);
			FOCApprovalResponsePojo response = performFOCApproval();
   		    String approvedStatus = response.getStatus();
   		    //System.out.println(approvedStatus+"***************");
   		 
   		     Assert.assertEquals(200, Integer.parseInt(approvedStatus));
			 Assertion.assertEquals(approvedStatus, "200");
			 Assertion.completeAssertions();
			 /*Log.endTestCase(methodName);
			 Log.info("Level 1 success !!");
			 
			 if ( approvalLevelPreference > 1 ) {
				 focApprovalRequestPojo.getFocApprovalRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl2"));
	    		 response = performFOCApproval();
	    		 status = response.getStatus();
	    		 
	    		 Assert.assertEquals(200, Integer.parseInt(status));
	 			 Assertion.assertEquals(status, "200");
	 			 
	 			 Log.info("Level 2 success !!");
			 }
			 
			 if ( approvalLevelPreference > 2 ) {
				 focApprovalRequestPojo.getFocApprovalRequests().get(0).setCurrentStatus(_masterVO.getProperty("aprvLvl3"));
	    		 response = performFOCApproval();
	    		 status = response.getStatus();
	    		 
	    		 Assert.assertEquals(200, Integer.parseInt(status));
	 			 // Assertion.assertEquals(status, "200");
	 			 
	 			 Log.info("Level 3 success !!");
			 }*/
			 
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-13091")
		public void A_02_Test_InvalidTxnID(String opCategoryName,String opLoginId,String opPassword,String opMsisdn, String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_02_Test_InvalidTxnID";
			Log.startTestCase(methodName);
			
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(opMsisdn, opPin,opCategoryName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupFOCInitiate(opPin,chMsisdn);
			List lst = focInititateRequestPojo.getData();
			data =(Datum) lst.get(0);
			data.getFocProducts().get(0).setAppQuantity(Integer.parseInt(_masterVO.getProperty("userappQty")));
			
			FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
			focInititaeAPI.addBodyParam(focInititateRequestPojo);
			focInititaeAPI.setExpectedStatusCode(200);
			focInititaeAPI.perform();
			
			focInitiateReponsePojo = focInititaeAPI.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		    String status = focInitiateReponsePojo.getStatus();
		    Assert.assertEquals(200, Integer.parseInt(status));
			// Assertion.assertEquals(status, "200");
			
			String txnId=focInitiateReponsePojo.getSuccessList().get(0).getTransactionId();
			
			
			/*
			 *  FOC APPROVAL 
			 */
			
			CaseMaster = _masterVO.getCaseMasterByID("FOCAPPRV2");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName));
			currentNode.assignCategory("REST");
			
			setupFOCApproval(chMsisdn, productCode, "12345");  // invalid txn id
			FOCApprovalResponsePojo response = performFOCApproval();
   		    String approvedStatus = response.getStatus();
   		 
   		    Assert.assertEquals(400, Integer.parseInt(approvedStatus));
			Assertion.assertEquals(approvedStatus, "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-13092")
		public void A_03_Test_InvalidCurrentStatus(String opCategoryName,String opLoginId,String opPassword,String opMsisdn, String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_03_Test_InvalidCurrentStatus";
			Log.startTestCase(methodName);
			
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(opMsisdn, opPin,opCategoryName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupFOCInitiate(opPin,chMsisdn);
			List lst = focInititateRequestPojo.getData();
			data =(Datum) lst.get(0);
			data.getFocProducts().get(0).setAppQuantity(Integer.parseInt(_masterVO.getProperty("userappQty")));
			
			FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
			focInititaeAPI.addBodyParam(focInititateRequestPojo);
			focInititaeAPI.setExpectedStatusCode(200);
			focInititaeAPI.perform();
			
			focInitiateReponsePojo = focInititaeAPI.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		    String status = focInitiateReponsePojo.getStatus();
		    Assert.assertEquals(200, Integer.parseInt(status));
			// Assertion.assertEquals(status, "200");
			
			String txnId=focInitiateReponsePojo.getSuccessList().get(0).getTransactionId();
			
			
			/*
			 *  FOC APPROVAL 
			 */
			
			CaseMaster = _masterVO.getCaseMasterByID("FOCAPPRV3");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName));
			currentNode.assignCategory("REST");
			
			setupFOCApproval(chMsisdn, productCode, txnId);
			focApprovalRequest.setCurrentStatus(_masterVO.getProperty("aprvLvl2")); // invalid current status
			
			FOCApprovalResponsePojo response = performFOCApproval();
   		    String approvedStatus = response.getStatus();
   		 
   		    Assert.assertEquals(400, Integer.parseInt(approvedStatus));
			Assertion.assertEquals(approvedStatus, "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-13093")
		public void A_04_Test_InvalidReciverMsisdn(String opCategoryName,String opLoginId,String opPassword,String opMsisdn, String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productCode) throws Exception {
			final String methodName = "A_04_Test_InvalidReciverMsisdn";
			Log.startTestCase(methodName);
			
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(opLoginId, opPassword,opCategoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(opMsisdn, opPin,opCategoryName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),opCategoryName));
			currentNode.assignCategory("REST");
			setupFOCInitiate(opPin,chMsisdn);
			List lst = focInititateRequestPojo.getData();
			data =(Datum) lst.get(0);
			data.getFocProducts().get(0).setAppQuantity(Integer.parseInt(_masterVO.getProperty("userappQty")));
			
			FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
			focInititaeAPI.addBodyParam(focInititateRequestPojo);
			focInititaeAPI.setExpectedStatusCode(200);
			focInititaeAPI.perform();
			
			focInitiateReponsePojo = focInititaeAPI.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		    String status = focInitiateReponsePojo.getStatus();
		    Assert.assertEquals(200, Integer.parseInt(status));
			// Assertion.assertEquals(status, "200");
			
			String txnId=focInitiateReponsePojo.getSuccessList().get(0).getTransactionId();
			
			
			/*
			 *  FOC APPROVAL 
			 */
			
			CaseMaster = _masterVO.getCaseMasterByID("FOCAPPRV4");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), opCategoryName));
			currentNode.assignCategory("REST");
			
			setupFOCApproval("12345", productCode, txnId);  // Invalid msisdn
			FOCApprovalResponsePojo response = performFOCApproval();
   		    String approvedStatus = response.getStatus();
   		 
   		    Assert.assertEquals(400, Integer.parseInt(approvedStatus));
			Assertion.assertEquals(approvedStatus, "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}


}
