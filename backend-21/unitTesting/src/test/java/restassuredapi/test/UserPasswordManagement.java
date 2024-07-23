package restassuredapi.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import restassuredapi.api.channeluserservices.UserPasswordManagementAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userpasswordmanagementrequestpojo.UserPasswordManagementRequestPojo;
import restassuredapi.pojo.userpasswordmanagementresponsepojo.UserPasswordManagementResponsePojo;

@ModuleManager(name = Module.REST_USER_PASSWORD_MANAGEMENT)
public class UserPasswordManagement extends BaseTest {

	static String moduleCode;
	UserPasswordManagementRequestPojo userPasswordManagementRequestPojo = new UserPasswordManagementRequestPojo();
	UserPasswordManagementResponsePojo userPasswordManagementResponsePojo = new UserPasswordManagementResponsePojo();
	
	protected static String accessToken;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 
	
	String currentCategoryName;
	
	
	@DataProvider(name = "userData")
	public Object[][] getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
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
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
                categorySizeCounter++;
            }
        }
        categorySize.add("" + categorySizeCounter);
        totalObjectCounter = totalObjectCounter + categorySizeCounter;
    }

    Object[][] Data = new Object[totalObjectCounter][6];

    for (int j = 0, k = 0; j < alist2.size(); j++) {

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserMSISDN = null;
        String ChannelUserLogin = null;
        String ChannelUserPassword = null;
        String ChannelUserCategoryName = null;
        
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                ChannelUserLogin = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                ChannelUserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                ChannelUserCategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                currentCategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).toString();
            	Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                Data[k][4] = ChannelUserPassword;
                Data[k][5] = ChannelUserLogin;
                k++;
                
            }
        }

    }
    
    tranferDetails.put("Login_Id", Data[0][5].toString());
    tranferDetails.put("Password", Data[0][4].toString());
    tranferDetails.put("Msisdn", Data[0][2].toString());
    
    
    return Data;
    
	}
	
	
	
	public void setupData(String parLoginId, String parPassword, String childLoginId, String childMsisdn)
	{
		
		userPasswordManagementRequestPojo.setChildLoginId(childLoginId);
		userPasswordManagementRequestPojo.setChildMsisdn(childMsisdn);
		userPasswordManagementRequestPojo.setOperationID("1");
		userPasswordManagementRequestPojo.setRemarks(_masterVO.getProperty("passwordManagementRemarks"));
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
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_01_Test_userPasswordManagement_positive(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
			
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		//updating invalid password count of user to 3 so it gets blocked
	    String msidnToUpdate =transfer_Details.get("Msisdn");
	    DBHandler.AccessHandler.updateAnyColumnValue("USERS", "INVALID_PASSWORD_COUNT", "3", "MSISDN", msidnToUpdate);
				
	    
	    setupData(parLoginId, parPassword, childLoginId, childMsisdn);
		
		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform();
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);
		int status = userPasswordManagementResponsePojo.getStatusCode();
		Assert.assertEquals(200, status);
		Assertion.assertEquals(Integer.toString(status), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
				
	}
	

	@Test(dataProvider = "userData")
	public void A_02_Test_EmptyRemarks(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM2");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(parLoginId, parPassword, childLoginId, childMsisdn);

		userPasswordManagementRequestPojo.setRemarks("");
		
		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform();
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		int statusCode =userPasswordManagementResponsePojo.getStatusCode();
		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);		
	}
	
	
	@Test(dataProvider = "userData")
	public void A_03_Test_EmptyMsisdnAndId(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupData(parLoginId, parPassword, childLoginId, childMsisdn);
		
		userPasswordManagementRequestPojo.setChildLoginId("");
		userPasswordManagementRequestPojo.setChildMsisdn("");
		
		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform();
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		int statusCode =userPasswordManagementResponsePojo.getStatusCode();
		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
	}
	
	@Test(dataProvider = "userData")
	public void A_04_Test_InvalidOperationID(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
        
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM4");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");

		setupData(parLoginId, parPassword, childLoginId, childMsisdn);
		
		userPasswordManagementRequestPojo.setOperationID("6");
		
		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform();
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		int statusCode =userPasswordManagementResponsePojo.getStatusCode();
		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
	}
	
	
	
	@Test(dataProvider = "userData")
	public void A_05_Test_UnblockingUnblockeduser(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM5");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(parLoginId, parPassword, childLoginId, childMsisdn);

		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform();
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		String message =userPasswordManagementResponsePojo.getSuccessMsg();
		Assert.assertEquals(message, "Password of user is not blocked");
		Assertion.assertEquals(message, "Password of user is not blocked");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void A_06_Test_UnblockingBlockedUser(String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
        if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
		
        UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM6");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData(parLoginId, parPassword, childLoginId, childMsisdn);
		//updating invalid password count of user to 3 so it gets blocked
	    String msidnToUpdate =childMsisdn;
	    DBHandler.AccessHandler.updateAnyColumnValue("USERS", "INVALID_PASSWORD_COUNT", "3", "MSISDN", msidnToUpdate);
				
		

		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform(); //unblocking request
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		String message =userPasswordManagementResponsePojo.getSuccessMsg();
		Assert.assertEquals(message, "User password is unblocked successfully.");
		Assertion.assertEquals(message, "User password is unblocked successfully.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_07_Test_ResetPassword(
			String childCatName, String parCategoryName, String childMsisdn, String childLoginId, String parPassword, String parLoginId) throws IOException
	{


		if(_masterVO.getProperty("identifierType").equals("loginid"))
        	try {
        		BeforeMethod(parLoginId,parPassword,parCategoryName);
        	}catch(Exception e) {
        		Log.info(e.getMessage());
        	}
		
		final String methodName = "Test_UserPasswordManagementRestAPI";
        Log.startTestCase(methodName);
        
		UserPasswordManagementAPI userPasswordManagementAPI = new UserPasswordManagementAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		userPasswordManagementAPI.setContentType(_masterVO.getProperty("contentType"));
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPM7");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		
		setupData(parLoginId, parPassword, childLoginId, childMsisdn);
		
		userPasswordManagementRequestPojo.setOperationID("4");//reset request

		userPasswordManagementAPI.addBodyParam(userPasswordManagementRequestPojo);
		userPasswordManagementAPI.setExpectedStatusCode(200);
		userPasswordManagementAPI.perform(); //unblocking request
		
		userPasswordManagementResponsePojo =userPasswordManagementAPI.getAPIResponseAsPOJO(UserPasswordManagementResponsePojo.class);

		String message =userPasswordManagementResponsePojo.getSuccessMsg();
		Assert.assertEquals(message, "User password is reset successfully.");
		Assertion.assertEquals(message, "User password is reset successfully.");
		Assertion.completeAssertions();
		//updating  password to default value
	    String msidnToUpdate =transfer_Details.get("Msisdn");
	    DBHandler.AccessHandler.updateAnyColumnValue("USERS", "PASSWORD", "7eda411be5ea4a90917de90f9b1b8df5", "MSISDN", msidnToUpdate);

		Log.endTestCase(methodName);
	}
	
	
	
}
