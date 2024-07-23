package restassuredapi.test;

import java.text.MessageFormat;
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
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.o2cAcknowledgePdfDownloadApi.O2cAcknowledgePdfDownloadApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2CAcknowledgePdfDownloadRequestPojo.Data;
import restassuredapi.pojo.o2CAcknowledgePdfDownloadRequestPojo.O2cAcknowledgePdfDownloadReqPojo;
import restassuredapi.pojo.o2cAcknowledgePdfDownloadResponsePojo.O2cAcknowledgePdfDownloadResPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.O2C_ACKNOWLEDGE_PDF_DOWNLOAD)
public class O2cAcknowledgePdfDownload extends BaseTest {

	static String moduleCode;
	O2cAcknowledgePdfDownloadReqPojo o2cAcknowledgePdfDownloadReqPojo = new O2cAcknowledgePdfDownloadReqPojo();
	O2cAcknowledgePdfDownloadResPojo o2cAcknowledgePdfDownloadResPojo = new O2cAcknowledgePdfDownloadResPojo();

	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();

	RandomGeneration random = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount() - 4;

		Object[][] Data = new Object[rowCount][8];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
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
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void setupData() {

	//	data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setExtcode("234");
		data.setExtnwcode("NG");
		data.setNetworkCode("NG");
		data.setNetworkCodeFor("NG");
		data.setTransferType("");

	}

	// Successful data with valid data.

	protected static String accessToken;

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {
		// if(accessToken==null) {
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

	
	// Positive Scenario
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-001")
	public void A_01_Test_O2CAcknowledgePdfDownload_Positive(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_O2CAcknowledgePdfDownload_Positive";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPDFDL01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		String transaction_id = DBHandler.AccessHandler.getChannelTransfersTxnId("OPT" , msisdn);
		O2cAcknowledgePdfDownloadApi o2cAcknowledgePdfDownloadApi = new O2cAcknowledgePdfDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cAcknowledgePdfDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid(loginID);
		data.setMsisdn(msisdn);
		data.setPassword(password);
		data.setPin(PIN);
		data.setTransferId(transaction_id);
		o2cAcknowledgePdfDownloadReqPojo.setData(data);
		o2cAcknowledgePdfDownloadApi.addBodyParam(o2cAcknowledgePdfDownloadReqPojo);
		o2cAcknowledgePdfDownloadApi.setExpectedStatusCode(200);
		o2cAcknowledgePdfDownloadApi.perform();
		o2cAcknowledgePdfDownloadResPojo = o2cAcknowledgePdfDownloadApi
				.getAPIResponseAsPOJO(O2cAcknowledgePdfDownloadResPojo.class);
		String statusCode = o2cAcknowledgePdfDownloadResPojo.getStatus();
		Assert.assertEquals(statusCode, "200");
		Assertion.assertEquals(statusCode, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-002")
	public void A_02_Test_O2CAcknowledgePdfDownload_Negative(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_02_Test_O2CAcknowledgePdfDownload_Negative";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPDFDL02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		O2cAcknowledgePdfDownloadApi o2cAcknowledgePdfDownloadApi = new O2cAcknowledgePdfDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cAcknowledgePdfDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid(loginID);
		data.setMsisdn(msisdn);
		data.setPassword(password);
		data.setPin(PIN);
		String transaction_id = random.randomAlphaNumeric(14);
		data.setTransferId(transaction_id);
		o2cAcknowledgePdfDownloadReqPojo.setData(data);
		o2cAcknowledgePdfDownloadApi.addBodyParam(o2cAcknowledgePdfDownloadReqPojo);
		o2cAcknowledgePdfDownloadApi.setExpectedStatusCode(400);
		o2cAcknowledgePdfDownloadApi.perform();
		o2cAcknowledgePdfDownloadResPojo = o2cAcknowledgePdfDownloadApi
				.getAPIResponseAsPOJO(O2cAcknowledgePdfDownloadResPojo.class);
		String message = o2cAcknowledgePdfDownloadResPojo.getMessage();
		Assert.assertEquals(message, "Transaction is not successful");
		Assertion.assertEquals(message, "Transaction is not successful");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-003")
	public void A_03_Test_O2CAcknowledgePdfDownload_Negative(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_Test_O2CAcknowledgePdfDownload_Negative";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPDFDL03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		String transaction_id = DBHandler.AccessHandler.getChannelTransfersTxnId("OPT" , "7265465465");
		O2cAcknowledgePdfDownloadApi o2cAcknowledgePdfDownloadApi = new O2cAcknowledgePdfDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cAcknowledgePdfDownloadApi.setContentType(_masterVO.getProperty("contentType"));
		data.setLoginid(loginID);
		data.setMsisdn(msisdn);
		data.setPassword(password);
		data.setPin(PIN);
		data.setTransferId(transaction_id);
		o2cAcknowledgePdfDownloadReqPojo.setData(data);
		o2cAcknowledgePdfDownloadApi.addBodyParam(o2cAcknowledgePdfDownloadReqPojo);
		o2cAcknowledgePdfDownloadApi.setExpectedStatusCode(400);
		o2cAcknowledgePdfDownloadApi.perform();
		o2cAcknowledgePdfDownloadResPojo = o2cAcknowledgePdfDownloadApi
				.getAPIResponseAsPOJO(O2cAcknowledgePdfDownloadResPojo.class);
		String message = o2cAcknowledgePdfDownloadResPojo.getMessage();
		Assert.assertEquals(message, "Transaction is not successful");
		Assertion.assertEquals(message, "Transaction is not successful");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
