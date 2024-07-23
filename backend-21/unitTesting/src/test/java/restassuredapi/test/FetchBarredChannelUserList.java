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
import restassuredapi.api.fetchbarredlist.FetchBarredListApi;
import restassuredapi.api.internetrecharge.InternetRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.Bar;
import restassuredapi.pojo.barunbarchanneluserrequestpojo.BarUnbarChannelUserRequestPojo;
import restassuredapi.pojo.barunbarchanneluserresponsepojo.BarUnbarChannelUserResponsePojo;
import restassuredapi.pojo.fetchbarreduserlistrequestpojo.FetchBarredListRequestPojo;
import restassuredapi.pojo.fetchbarreduserlistresponsepojo.FetchBarredListResponsepojo;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeDetails;
import restassuredapi.pojo.internetrechargerequestpojo.InternetRechargeRequestPojo;
import restassuredapi.pojo.internetrechargeresponsepojo.InternetRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.FETCH_BARRED_LIST)
public class FetchBarredChannelUserList extends BaseTest {
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate=df.format(dateobj);   
	static String moduleCode;
	FetchBarredListRequestPojo fetchBarredListRequestPojo = new FetchBarredListRequestPojo();
	FetchBarredListResponsepojo fetBarredListResponsepojo = new FetchBarredListResponsepojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	InternetRechargeDetails data = new InternetRechargeDetails();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
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
		fetchBarredListRequestPojo.setBarredAs("SENDER");
		fetchBarredListRequestPojo.setBarredtype("C2S:SL017");
		fetchBarredListRequestPojo.setCategory("DIST");
		fetchBarredListRequestPojo.setDomain("ALL");
		fetchBarredListRequestPojo.setFromDate("01/05/21");
		fetchBarredListRequestPojo.setGeography("ALL");
		fetchBarredListRequestPojo.setModule("C2S");
		fetchBarredListRequestPojo.setMsisdn("72324234234");
		fetchBarredListRequestPojo.setTodate("25/05/21");
		fetchBarredListRequestPojo.setUserName("deepadist");
		fetchBarredListRequestPojo.setUserType("STAFF");
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
	@TestManager(TestKey="PRETUPS-13791")
	public void A_01_Test_success_msidn(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success_msidn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHBARREDUSER1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		FetchBarredListApi fetchBarredListApi = new FetchBarredListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchBarredListApi.setContentType(_masterVO.getProperty("contentType"));
		fetchBarredListApi.addBodyParam(fetchBarredListRequestPojo);
		fetchBarredListApi.setExpectedStatusCode(200);
		fetchBarredListApi.perform();
		fetBarredListResponsepojo = fetchBarredListApi
				.getAPIResponseAsPOJO(FetchBarredListResponsepojo.class);
		String status = fetBarredListResponsepojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13792")
	public void A_02_Test_success_username(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success_msidn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHBARREDUSER2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		fetchBarredListRequestPojo.setMsisdn("");
		FetchBarredListApi fetchBarredListApi = new FetchBarredListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchBarredListApi.setContentType(_masterVO.getProperty("contentType"));
		fetchBarredListApi.addBodyParam(fetchBarredListRequestPojo);
		fetchBarredListApi.setExpectedStatusCode(200);
		fetchBarredListApi.perform();
		fetBarredListResponsepojo = fetchBarredListApi
				.getAPIResponseAsPOJO(FetchBarredListResponsepojo.class);
		String status = fetBarredListResponsepojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13793")
	public void A_03_Test_success_filter(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_success_filter";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHBARREDUSER3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		fetchBarredListRequestPojo.setMsisdn("");
		fetchBarredListRequestPojo.setUserName("");
		FetchBarredListApi fetchBarredListApi = new FetchBarredListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchBarredListApi.setContentType(_masterVO.getProperty("contentType"));
		fetchBarredListApi.addBodyParam(fetchBarredListRequestPojo);
		fetchBarredListApi.setExpectedStatusCode(200);
		fetchBarredListApi.perform();
		fetBarredListResponsepojo = fetchBarredListApi
				.getAPIResponseAsPOJO(FetchBarredListResponsepojo.class);
		String status = fetBarredListResponsepojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-13794")
	public void A_04_Test_Missing_Bartype(String loginID, String password, String msisdn, String PIN, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_Test_Missing_Bartype";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("FETCHBARREDUSER4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();

		FetchBarredListApi fetchBarredListApi = new FetchBarredListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		fetchBarredListApi.setContentType(_masterVO.getProperty("contentType"));
		fetchBarredListApi.addBodyParam(fetchBarredListRequestPojo);
		fetchBarredListApi.setExpectedStatusCode(241320);
		fetchBarredListApi.perform();
		fetBarredListResponsepojo = fetchBarredListApi
				.getAPIResponseAsPOJO(FetchBarredListResponsepojo.class);
		String errorcode = fetBarredListResponsepojo.getMessageCode();
		if(errorcode == "241320")
		Assert.assertEquals(241320, errorcode);
		Assertion.assertEquals(errorcode, "241320");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
