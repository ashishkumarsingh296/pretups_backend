package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._APIUtil;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.barunbar.BarUnbarApi;
import restassuredapi.api.internetrecharge.InternetRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.Bar;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.BarUnbarChannelUserRequestPojo;
import restassuredapi.pojo.barunbarchanneluserresponsepojo.BarUnbarChannelUserResponsePojo;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeDetails;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;
import restassuredapi.pojo.internetrechargeresponsepojo.InternetRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.BAR_UNBAR)
public class BarUnbarChannelUser extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	BarUnbarChannelUserRequestPojo barUnbarChannelUserRequestPojo = new BarUnbarChannelUserRequestPojo();
	BarUnbarChannelUserResponsePojo barUnbarChannelUserResponsePojo = new BarUnbarChannelUserResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	

	InternetRechargeDetails data = new InternetRechargeDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][7];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			//Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
		}
		return Data;
	}


	public void setupData() {
		barUnbarChannelUserRequestPojo.setModule("C2S");
		barUnbarChannelUserRequestPojo.setMsisdn("72324234234");
		barUnbarChannelUserRequestPojo.setUserName("");
		barUnbarChannelUserRequestPojo.setUserType("SENDER");
		ArrayList<Bar> barList = new ArrayList<>();
		Bar bar = new Bar();
		bar.setBarringReason("for testing");
		bar.setBarringType("C2S:SL017");
		barList.add(bar);
		barUnbarChannelUserRequestPojo.setBar(barList);
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
	@TestManager(TestKey="PRETUPS-13429")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.addType("Bar");
		barUnbarApi.setExpectedStatusCode(200);
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		String status = barUnbarChannelUserResponsePojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13429")
	public void A_01_Test_success1(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.addType("Un-bar");
		barUnbarApi.setExpectedStatusCode(200);
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		String status = barUnbarChannelUserResponsePojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13430")
	public void A_02_Test_Not_User_Hierarchy(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_Not_User_Hierarchy";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		
		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarChannelUserRequestPojo.setMsisdn("72010101020");
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.setExpectedStatusCode(401);
		barUnbarApi.addType("Bar");
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		String errorcode = barUnbarChannelUserResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(241233, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "241233");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13431")
	public void A_03_Test_Missing_Msisdn(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_Missing_Msisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		barUnbarChannelUserRequestPojo.setMsisdn("");
		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.setExpectedStatusCode(401);
		barUnbarApi.addType("Bar");
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		
		String errorcode = barUnbarChannelUserResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(241239, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "241239");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13432")
	public void A_04_Test_Missing_Bartype(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_Missing_Bartype";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarChannelUserRequestPojo.getBar().get(0).setBarringType("");;
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.setExpectedStatusCode(241239);
		barUnbarApi.addType("Bar");
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		String errorcode = barUnbarChannelUserResponsePojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(241239, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "241239");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13433")
	public void A_05_Test_Missing_Barusertype(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_05_Test_Missing_Barusertype";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BARUNBAR5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		BarUnbarApi barUnbarApi = new BarUnbarApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		barUnbarApi.setContentType(_masterVO.getProperty("contentType"));
		barUnbarChannelUserRequestPojo.setUserType("");
		barUnbarApi.addBodyParam(barUnbarChannelUserRequestPojo);
		barUnbarApi.setExpectedStatusCode(241239);
		barUnbarApi.addType("Bar");
		barUnbarApi.perform();
		barUnbarChannelUserResponsePojo = barUnbarApi
				.getAPIResponseAsPOJO(BarUnbarChannelUserResponsePojo.class);
		String errorcode = barUnbarChannelUserResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(241239, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "241239");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

}
