package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.utils.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.constants.Module;

import restassuredapi.api.autocompleteuserdetails.AutoCompleteUserDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.autocompleteuserdetailsrequestpojo.AutoCompleteUserDetailsRequestPojo;
import restassuredapi.pojo.autocompleteuserdetailsrequestpojo.Data;
import restassuredapi.pojo.autocompleteuserdetailsresponsepojo.AutoCompleteUserDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_AUTO_COMPLETE_USER_DETAILS)
public class AutoCompleteUserDetails extends BaseTest{
	
	static String moduleCode;
	
	AutoCompleteUserDetailsRequestPojo autoCompleteUserDetailsRequestPojo = new AutoCompleteUserDetailsRequestPojo();
	AutoCompleteUserDetailsResponsePojo autoCompleteUserDetailsResponsePojo = new AutoCompleteUserDetailsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();

	String minLength ="";
	String msisdnToSearch=new RandomGeneration().randomNumberWithoutZero(3);

	 @DataProvider(name = "userData")
     public Object[][] TestDataFeed1() {
            String MasterSheetPath = _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
            int rowCountoperator = ExcelUtility.getRowCount();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            
    		ArrayList<String> cateCode = new ArrayList<>();
    		for(int i=1;i<=rowCount;i++) {
    				cateCode.add(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,i));
    			
    		}
            
            Object[][] Data = new Object[rowCount+rowCountoperator][8];
            int j=0;
            for (int i = 1; i <= rowCount; i++) {
                   Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                   Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                   Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                   Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                   Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                   Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                   Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                   Data[j][7] =  cateCode.get((i+1)%cateCode.size());
                   j++;
                   }
    
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
            for (int i = 1; i <=rowCountoperator; i++) {
                   Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                   Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                   Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                   Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                   Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
                   Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                   Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                   Data[j][7] =  cateCode.get(i%cateCode.size());
                   j++;
                   }            

            return Data;
     }

	public String getSystemPreference() {
		 minLength = DBHandler.AccessHandler.getSystemPreference("MIN_LENGTH_TO_AUTOCOMPLETE");
		return minLength;
	}
	public void setupData(String categoryCode) {

		 minLength = getSystemPreference();
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn("");
		data.setPin("");
		data.setExtcode("");
		data.setLanguage1("");
		data.setLanguage2("");
		data.setMsisdnToSearch("");
		data.setLoginidToSearch("");
		data.setUsernameToSearch("AUT");
		data.setDomainCode("DIST");
		data.setCategoryCode(categoryCode);
		data.setGeoDomainCode("");
		autoCompleteUserDetailsRequestPojo.setData(data);
		
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


	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-4008")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String tcateCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(tcateCode);
		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
		autoCompleteUserDetailsAPI.perform();
		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
		String statusCode = autoCompleteUserDetailsResponsePojo.getMessage();
		Assert.assertEquals(statusCode, "Success");
		Assertion.assertEquals(statusCode, "Success");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6406")
//	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName ) throws Exception {
//		final String methodName = "Test_AutoCompleteUserDetails";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL2");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData();
//		data.setMsisdn("");
//		autoCompleteUserDetailsRequestPojo.setData(data);
//		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//
//		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
//		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
//		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
//		autoCompleteUserDetailsAPI.perform();
//		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
//				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
//		
//
//		String message =autoCompleteUserDetailsResponsePojo.getDataObject().getMessage();
//		
//		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}

//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6407")
//	public void A_03_Success_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
//		final String methodName = "Test_AutoCompleteUserDetails";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL3");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN);
//		data.setPin("");
//		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
//		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
//		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
//		autoCompleteUserDetailsAPI.perform();
//		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
//				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
//
//		
//		
//        String message =autoCompleteUserDetailsResponsePojo.getDataObject().getMessage();
//		
//		Assert.assertEquals(message, "PIN can not be blank.");
//		Assertion.assertEquals(message, "PIN can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}

//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6408")
//	public void A_04_Test_BlankExtnwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
//		final String methodName = "Test_AutoCompleteUserDetails";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL4");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN);
//		data.setExtnwcode("");
//		autoCompleteUserDetailsRequestPojo.setData(data);
//		
//		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
//		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
//		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
//		autoCompleteUserDetailsAPI.perform();
//		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
//				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
//
//		String message =autoCompleteUserDetailsResponsePojo.getDataObject().getMessage();
//		
//		Assert.assertEquals(message, "External network code value is blank.");
//		Assertion.assertEquals(message, "External network code value is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-4012")
	public void A_09_Test_InvalidMinMsisdnLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String tcateCode) throws Exception {
		
		final String methodName = "A_09_Test_InvalidMinMsisdnLength";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL9");
		moduleCode = CaseMaster.getModuleCode();
		 minLength =getSystemPreference();
		 currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(tcateCode);
			
		data.setMsisdnToSearch(new RandomGeneration().randomNumeric(Integer.parseInt(minLength)-1));
		data.setLoginidToSearch(new RandomGeneration().randomAlphabets(Integer.parseInt(minLength)-1));
		data.setUsernameToSearch("");
		autoCompleteUserDetailsRequestPojo.setData(data);	
		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
		autoCompleteUserDetailsAPI.perform();
		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
		
		//int statusCode = Integer.parseInt(autoCompleteUserDetailsResponsePojo.getDataObject().getTxnstatus());
       String messageCode =autoCompleteUserDetailsResponsePojo.getMessageCode();
		
		Assert.assertEquals(messageCode, "7609");
		Assertion.assertEquals(messageCode, "7609");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6414")
	public void A_10_Test_InvalidMinLoginIdLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String tcateCode) throws Exception {
		
		final String methodName = "A_10_Test_InvalidMinLoginIdLength";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL10");
		moduleCode = CaseMaster.getModuleCode();

		 minLength = getSystemPreference();
		 currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(tcateCode);
		data.setMsisdnToSearch("");
		data.setLoginidToSearch(new RandomGeneration().randomNumeric(Integer.parseInt(minLength)-1));
		data.setUsernameToSearch("");
		autoCompleteUserDetailsRequestPojo.setData(data);
		
		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
		autoCompleteUserDetailsAPI.perform();
		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
		
	    String message =autoCompleteUserDetailsResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "7609");
		Assertion.assertEquals(message, "7609");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6416")
	public void A_13_Test_InvalidDomainCodeData(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String tcateCode) throws Exception {
		
		final String methodName = "A_13_Test_InvalidDomainCodeData";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(tcateCode);
		data.setDomainCode("Agent");
		autoCompleteUserDetailsRequestPojo.setData(data);
		
		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
		autoCompleteUserDetailsAPI.perform();
		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
		
		//int statusCode = Integer.parseInt(autoCompleteUserDetailsResponsePojo.getDataObject().getTxnstatus());
       String message =autoCompleteUserDetailsResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "Error occured");
		Assertion.assertEquals(message, "Error occured");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

//	@Test(dataProvider = "userData")
//	@TestManager(TestKey="PRETUPS-6417")
//	public void A_14_Test_InvalidCategoryCodeData(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String domainName) throws Exception {
//		
//		final String methodName = "Test_AutoCompleteUserDetails";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("AUTOCMPLDETAIL14");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN);
//		data.setCategoryCode(new RandomGeneration().randomAlphabets(3));
//		autoCompleteUserDetailsRequestPojo.setData(data);
//		
//		AutoCompleteUserDetailsAPI autoCompleteUserDetailsAPI = new AutoCompleteUserDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		autoCompleteUserDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
//		autoCompleteUserDetailsAPI.addBodyParam(autoCompleteUserDetailsRequestPojo);
//		autoCompleteUserDetailsAPI.setExpectedStatusCode(200);
//		autoCompleteUserDetailsAPI.perform();
//		autoCompleteUserDetailsResponsePojo = autoCompleteUserDetailsAPI
//				.getAPIResponseAsPOJO(AutoCompleteUserDetailsResponsePojo.class);
//		
//		//int statusCode = Integer.parseInt(autoCompleteUserDetailsResponsePojo.getDataObject().getTxnstatus());
//       String message =autoCompleteUserDetailsResponsePojo.getDataObject().getMessage();
//		
//		Assert.assertEquals(message, "No Details found.");
//		Assertion.assertEquals(message, "No Details found.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
	
}
