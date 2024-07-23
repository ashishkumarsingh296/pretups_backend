package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.channeluserservices.UserProfileThresholdAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.userprofilethresholdrequestpojo.UserProfileThresholdRequestPojo;
import restassuredapi.pojo.userprofilethresholdresponsepojo.UserProfileThresholdResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.REST_USER_PROF_THRESHOLD)
public class UserProfileThreshold extends BaseTest{
	static String moduleCode;
	UserProfileThresholdResponsePojo userProfileThresholdResponsePojo= new UserProfileThresholdResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CInitiateCode = _masterVO.getProperty("C2CInititateCode");
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
			if (aList.contains(C2CInitiateCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
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
		
		Object[][] Data = new Object[totalObjectCounter][11];

        for (int j = 0, k = 0; j < alist1.size(); j++) {

            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserLoginId = null;
            String ChannelUserMSISDN = null;
            String ChannelUserPIN = null;
            String ChannelUserPASS = null;
            
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                	ChannelUserMSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                    ChannelUserLoginId=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                    ChannelUserPIN=ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    ChannelUserPASS=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                    break;
                }
            }

            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                    Data[k][0] = alist2.get(j); //fromCategoryName
                    Data[k][1] = alist1.get(j); //toCategoryName
                    Data[k][2] = ChannelUserMSISDN; //fromMsisdn
                    Data[k][3] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][4] = ChannelUserLoginId;
                    Data[k][5] = ChannelUserPIN;
                    Data[k][6] = ChannelUserPASS;
                    Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                    Data[k][8] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                    Data[k][9] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                    Data[k][10] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                    k++;
                }
            }

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

	//Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception
	{
		final String methodName = "Test_UserProfThresholdAPI";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(toMSISDN, toPin, toCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPT1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
	
		UserProfileThresholdAPI userProfileThresholdAPI=new UserProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		userProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
		userProfileThresholdAPI.setUserId(fromLoginID);
		userProfileThresholdAPI.setExpectedStatusCode(200);
		userProfileThresholdAPI.perform();
		userProfileThresholdResponsePojo =userProfileThresholdAPI.getAPIResponseAsPOJO(UserProfileThresholdResponsePojo.class);
		long statusCode =userProfileThresholdResponsePojo.getStatusCode();
		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_02_Test_IdentifierTypeBlank(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception
	{
		final String methodName = "Test_UserProfThresholdAPI";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(toMSISDN, toPin, toCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPT2");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");

		UserProfileThresholdAPI userProfileThresholdAPI=new UserProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		userProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
		userProfileThresholdAPI.setUserId("");
		userProfileThresholdAPI.setExpectedStatusCode(400);
		userProfileThresholdAPI.perform();
		userProfileThresholdResponsePojo =userProfileThresholdAPI.getAPIResponseAsPOJO(UserProfileThresholdResponsePojo.class);
		String message = userProfileThresholdResponsePojo.getGlobalError();
		Assert.assertEquals(message, "No valid input type(login id) is provided");
		Assertion.assertEquals(message, "No valid input type(login id) is provided");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_03_Test_InvalidLoginId(String fromCategory, String toCategory,String fromMSISDN, String toMSISDN, String fromLoginID, String fromPIN, String fromPassword,String toCatcode, String toPin, String toLoginId, String toPassword) throws Exception
	{
		final String methodName = "Test_UserProfThresholdAPI";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(toLoginId, toPassword, toCategory);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(toMSISDN, toPin, toCategory);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUPT3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),fromCategory,toCategory));
		currentNode.assignCategory("REST");
		
		UserProfileThresholdAPI userProfileThresholdAPI=new UserProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		userProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
		String id=new RandomGeneration().randomAlphabets(5);
		userProfileThresholdAPI.setUserId(id);;
		userProfileThresholdAPI.setExpectedStatusCode(400);
		userProfileThresholdAPI.perform();
		userProfileThresholdResponsePojo =userProfileThresholdAPI.getAPIResponseAsPOJO(UserProfileThresholdResponsePojo.class);
		String message = userProfileThresholdResponsePojo.getGlobalError();
		Assert.assertEquals(message, "No user exists with this login ID("+id+")");
		Assertion.assertEquals(message,"No user exists with this login ID("+id+")");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
}
	