package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.trfuserdetails.TrfUserDetailsAPI;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.trfuserdetailsrequestpojo.Data;
import restassuredapi.pojo.trfuserdetailsrequestpojo.TrfUserDetailsRequestPojo;
import restassuredapi.pojo.trfuserdetailsresponsepojo.TrfUserDetailsResponsePojo;


@ModuleManager(name = Module.REST_SENDER_RECEIVER_DETAILS)
public class TrfUsersDetails extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
    
	static String moduleCode;
	TrfUserDetailsRequestPojo trfUserDetailsRequestPojo = new TrfUserDetailsRequestPojo();
	TrfUserDetailsResponsePojo trfUserDetailsResponsePojo = new TrfUserDetailsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
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
    Object[][] Data = new Object[totalObjectCounter][8];
    for (int j = 0, k = 0; j < alist2.size(); j++) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserMSISDN = null;
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                break;
            }
        }
        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                Data[k][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
            	Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
            	Data[k][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                Data[k][7] = ChannelUserMSISDN;

                k++;
            }
        }  
    }
    return Data;
}

	
	public void setupData(String data1,String data2,String data3) {

		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setMsisdn(data1);
		data.setPin(data2);
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode("");
		data.setMsisdn2(data3);
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		data.setLanguage2("");
		data.setC2ctrftype("B");
		trfUserDetailsRequestPojo.setData(data);
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

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


	}

	// Successful data with valid data.

	protected static String accessToken;


	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));

		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
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
	@TestManager(TestKey="PRETUPS-5885")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(200);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		int statusCode = Integer.parseInt(trfUserDetailsResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6434")
	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setMsisdn("");
		trfUserDetailsRequestPojo.setData(data);
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		int statusCode = Integer.parseInt(trfUserDetailsResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(206, statusCode);
		String msg = trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6435")
	public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setPin("");
		trfUserDetailsRequestPojo.setData(data);
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		int statusCode = Integer.parseInt(trfUserDetailsResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(206, statusCode);
		String msg = trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "PIN can not be blank.");
		Assertion.assertEquals(msg, "PIN can not be blank.");
		
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-5887")
	public void A_04_Test_ReceiverBlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setMsisdn2("");
		trfUserDetailsRequestPojo.setData(data);
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		int statusCode = Integer.parseInt(trfUserDetailsResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(206, statusCode);
		String msg = trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "Receiver's mobile number is invalid");
		Assertion.assertEquals(msg, "Receiver's mobile number is invalid");
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-5890")
	public void A_08_Test_BlankExtnwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setExtnwcode("");
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6439")
	public void A_09_Test_NumericPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setPin(new RandomGeneration().randomAlphabets(4));
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PIN is not numeric.");
		Assertion.assertEquals(message, "PIN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6429")
	public void A_10_Test_InvalidLanguage(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setLanguage1(new RandomGeneration().randomAlphabets(3));
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6430")
	public void A_11_Test_InvalidMsisdnLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setMsisdn(new RandomGeneration().randomNumeric(25));
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6431")
	public void A_12_Test_InvalidTransferMode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setC2ctrftype("X");
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "C2C Transfer Mode is invalid. ");
		Assertion.assertEquals(message, "C2C Transfer Mode is invalid. ");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6432")
	public void A_13_Test_BlankTransferMode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setC2ctrftype("");
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "C2C Transfer Mode is invalid. ");
		Assertion.assertEquals(message, "C2C Transfer Mode is invalid. ");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6433")
	public void A_14_Test_InvalidPinLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
		final String methodName = "Test_TrfUsersDetailsAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setPin("1234567890123456");
		trfUserDetailsRequestPojo.setData(data);
		
		trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
		trfUserDetailsAPI.setExpectedStatusCode(400);
		trfUserDetailsAPI.perform();
		trfUserDetailsResponsePojo = trfUserDetailsAPI
				.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
		
		String message =trfUserDetailsResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid PIN length.");
		Assertion.assertEquals(message, "Invalid PIN length.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// Successful data with valid data.
		@Test(dataProvider = "userData")
		@TestManager(TestKey="PRETUPS-5892")
		public void A_15_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2) throws Exception {
			final String methodName = "Test_TrfUsersDetailsAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TRFUSERSDETAILS15");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
			currentNode.assignCategory("REST");
			setupData(msisdn,PIN,msisdn2);
			data.setC2ctrftype("B");
			TrfUserDetailsAPI trfUserDetailsAPI = new TrfUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			trfUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
			trfUserDetailsAPI.addBodyParam(trfUserDetailsRequestPojo);
			trfUserDetailsAPI.setExpectedStatusCode(200);
			trfUserDetailsAPI.perform();
			trfUserDetailsResponsePojo = trfUserDetailsAPI
					.getAPIResponseAsPOJO(TrfUserDetailsResponsePojo.class);
			int statusCode = Integer.parseInt(trfUserDetailsResponsePojo.getDataObject().getTxnstatus());

			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Integer.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
		}
}

