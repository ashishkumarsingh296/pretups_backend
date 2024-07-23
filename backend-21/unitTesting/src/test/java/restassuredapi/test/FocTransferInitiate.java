package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
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
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.focInitiateApi.FOCInititaeAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.focInitiateReponsePojo.FocInititateResponsePojo;
import restassuredapi.pojo.focInitiateRequestPojo.Datum;
import restassuredapi.pojo.focInitiateRequestPojo.FocInititateRequestPojo;
import restassuredapi.pojo.focInitiateRequestPojo.FocProduct;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
@ModuleManager(name = Module.FOC_INITIATE)
public class FocTransferInitiate extends BaseTest{
	static String moduleCode;
	String[] details;
	
	FocInititateRequestPojo focInititateRequestPojo = new FocInititateRequestPojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	FocInititateResponsePojo focInitiateReponsePojo = new FocInititateResponsePojo();

	restassuredapi.pojo.focInitiateRequestPojo.Datum data = new restassuredapi.pojo.focInitiateRequestPojo.Datum();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	
	@DataProvider(name = "userData")
	public Object[][] getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
		String C2CTransferCode = _masterVO.getProperty("FOCCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		// int rowCount = ExcelUtility.getRowCount();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
		    int categorySizeCounter = 0;
        for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
                categorySizeCounter++;
            }
        }
        categorySize.add("" + categorySizeCounter);
        totalObjectCounter = totalObjectCounter + categorySizeCounter;
    }

    Object[][] Data = new Object[totalObjectCounter][8];

    for (int j = 0, k = 0; j < alist2.size(); j++) {

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserMSISDN = null;
        String ChannelUserLogin = null;
        String ChannelUserPassword = null;
        
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                ChannelUserLogin = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                ChannelUserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][5] = ChannelUserLogin;
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.USER_NAME, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
          
                k++;
                
            }
        }

    }
    return Data;
    
	}
	

	public void setupData(String pin,String Msisdn) {
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


	// Successful data with valid data
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-12875")
	public void A_01_Test_success(String fromcatCode,String tocatCode,String tousermsisdn,String Msisdn,String Pin,String loginId,String username,String catName) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        String pass = userInfo.get("PASSWORD");
		String PIN = userInfo.get("PIN");
		String categoryName =userInfo.get("CATEGORY_NAME");
		String msisdn =userInfo.get("MSISDN");
		String LoginID = userInfo.get("LOGIN_ID");
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(LoginID, pass, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,tousermsisdn);
		List lst = focInititateRequestPojo.getData();
		data =(Datum) lst.get(0);
		data.getFocProducts().get(0).setAppQuantity(Integer.parseInt(_masterVO.getProperty("userappQty")));
		FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
		focInititaeAPI.addBodyParam(focInititateRequestPojo);
		focInititaeAPI.setExpectedStatusCode(201);
		focInititaeAPI.perform();
		focInitiateReponsePojo = focInititaeAPI
				.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		//String txnid = focInitiateReponsePojo.getDataObject().getTxnid();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(focInitiateReponsePojo.getSuccessList().get(0).getTransactionId());
	    String status = focInitiateReponsePojo.getMessageCode();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	//Invalid Pin
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-12876")
	public void A_02_Test_success(String fromcatCode,String tocatCode,String tousermsisdn,String Msisdn,String Pin,String loginId,String username,String catName) throws Exception {
		final String methodName = "A_02_Test_success";
		Log.startTestCase(methodName);
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        String pass = userInfo.get("PASSWORD");
		String PIN = userInfo.get("PIN");
		String categoryName =userInfo.get("CATEGORY_NAME");
		String msisdn =userInfo.get("MSISDN");
		String LoginID = userInfo.get("LOGIN_ID");
		transferDetails.get("userName");
		transferDetails.get("pin");
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(LoginID, pass, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData("1234",tousermsisdn);
		List lst = focInititateRequestPojo.getData();
		data =(Datum) lst.get(0);
		data.getFocProducts().get(0).setAppQuantity(100);
		FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
		focInititaeAPI.addBodyParam(focInititateRequestPojo);
		focInititaeAPI.setExpectedStatusCode(201);
		focInititaeAPI.perform();
		focInitiateReponsePojo = focInititaeAPI
				.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
		String status = focInitiateReponsePojo.getMessageCode().toString();
		Assertion.assertEquals(status, "7015");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	//empty Product
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-12877")
		public void A_03_Test_success(String fromcatCode,String tocatCode,String tousermsisdn,String Msisdn,String Pin,String loginId,String username,String catName) throws Exception {
			final String methodName = "A_02_Test_success";
			Log.startTestCase(methodName);
	        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
	        String pass = userInfo.get("PASSWORD");
			String PIN = userInfo.get("PIN");
			String categoryName =userInfo.get("CATEGORY_NAME");
			String msisdn =userInfo.get("MSISDN");
			String LoginID = userInfo.get("LOGIN_ID");
			transferDetails.get("userName");
			transferDetails.get("pin");
			if (_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(LoginID, pass, categoryName);
			else if (_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN, categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI3");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(PIN,tousermsisdn);
			List lst = focInititateRequestPojo.getData();
			data =(Datum) lst.get(0);
			data.setFocProducts(null);
			FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
			focInititaeAPI.addBodyParam(focInititateRequestPojo);
			focInititaeAPI.setExpectedStatusCode(201);
			focInititaeAPI.perform();
			focInitiateReponsePojo = focInititaeAPI
					.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
			String status = focInitiateReponsePojo.getMessageCode().toString();
			Assertion.assertEquals(status, "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

		}
		
		
		
		//Lang1 messge length could not exceed 30 char
				@Test(dataProvider = "userData")
				@TestManager(TestKey="PRETUPS-12878")
				public void A_04_Test_success(String fromcatCode,String tocatCode,String tousermsisdn,String Msisdn,String Pin,String loginId,String username,String catName) throws Exception {
					final String methodName = "A_02_Test_success";
					Log.startTestCase(methodName);
			        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
			        String pass = userInfo.get("PASSWORD");
					String PIN = userInfo.get("PIN");
					String categoryName =userInfo.get("CATEGORY_NAME");
					String msisdn =userInfo.get("MSISDN");
					String LoginID = userInfo.get("LOGIN_ID");
					transferDetails.get("userName");
					transferDetails.get("pin");
					if (_masterVO.getProperty("identifierType").equals("loginid"))
						BeforeMethod(LoginID, pass, categoryName);
					else if (_masterVO.getProperty("identifierType").equals("msisdn"))
						BeforeMethod(msisdn, PIN, categoryName);
					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI4");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
					currentNode.assignCategory("REST");
					setupData(PIN,tousermsisdn);
					List lst = focInititateRequestPojo.getData();
					data =(Datum) lst.get(0);
					data.setLanguage1("this is my message of length greater than 30 chars ");
					FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
					focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
					focInititaeAPI.addBodyParam(focInititateRequestPojo);
					focInititaeAPI.setExpectedStatusCode(201);
					focInititaeAPI.perform();
					focInitiateReponsePojo = focInititaeAPI
							.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
					String status = focInitiateReponsePojo.getMessageCode().toString();
					Assertion.assertEquals(status, "400");
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				//Lang2 messge length could not exceed 30 char
				@Test(dataProvider = "userData")
				@TestManager(TestKey="PRETUPS-12879")
				public void A_05_Test_success(String fromcatCode,String tocatCode,String tousermsisdn,String Msisdn,String Pin,String loginId,String username,String catName) throws Exception {
					final String methodName = "A_02_Test_success";
					Log.startTestCase(methodName);
			        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
			        String pass = userInfo.get("PASSWORD");
					String PIN = userInfo.get("PIN");
					String categoryName =userInfo.get("CATEGORY_NAME");
					String msisdn =userInfo.get("MSISDN");
					String LoginID = userInfo.get("LOGIN_ID");
					transferDetails.get("userName");
					transferDetails.get("pin");
					if (_masterVO.getProperty("identifierType").equals("loginid"))
						BeforeMethod(LoginID, pass, categoryName);
					else if (_masterVO.getProperty("identifierType").equals("msisdn"))
						BeforeMethod(msisdn, PIN, categoryName);
					CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FOCINI5");
					moduleCode = CaseMaster.getModuleCode();
					currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
					currentNode.assignCategory("REST");
					setupData(PIN,tousermsisdn);
					List lst = focInititateRequestPojo.getData();
					data =(Datum) lst.get(0);
					data.setLanguage2("this is my message of length greater than 30 chars ");
					FOCInititaeAPI focInititaeAPI = new FOCInititaeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
					focInititaeAPI.setContentType(_masterVO.getProperty("contentType"));
					focInititaeAPI.addBodyParam(focInititateRequestPojo);
					focInititaeAPI.setExpectedStatusCode(201);
					focInititaeAPI.perform();
					focInitiateReponsePojo = focInititaeAPI
							.getAPIResponseAsPOJO(FocInititateResponsePojo.class);
					String status = focInitiateReponsePojo.getMessageCode().toString();
					Assertion.assertEquals(status, "400");
					Assertion.completeAssertions();
					Log.endTestCase(methodName);

				}
				
				
				
}
