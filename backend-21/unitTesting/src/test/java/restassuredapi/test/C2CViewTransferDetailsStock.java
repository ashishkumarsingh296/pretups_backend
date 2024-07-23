package restassuredapi.test;

import com.commons.ExcelI;
import com.utils.ExcelUtility;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cviewtransferdetails.C2CViewTransferDetailsAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cviewtransferdetailsrequestpojo.C2CViewTransferDetailsRequestPojo;
import restassuredapi.pojo.c2cviewtransferdetailsrequestpojo.Data;
import restassuredapi.pojo.c2cviewtransferdetailsresponsepojo.C2CViewTransferDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.REST_C2C_VIEW_TRANSFER_DETAILS)
public class C2CViewTransferDetailsStock extends BaseTest {
	
	static String moduleCode;
	C2CViewTransferDetailsRequestPojo c2CViewTransferDetailsRequestPojo = new C2CViewTransferDetailsRequestPojo();
	C2CViewTransferDetailsResponsePojo c2CViewTransferDetailsResponsePojo = new C2CViewTransferDetailsResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][7];
		int j  = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
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
		org.testng.Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	public void setupData(String data1,String data2,String data3) {
		String transferID = DBHandler.AccessHandler.fetchTransferIdWithStatus("NEW",_masterVO.getProperty("transferTypeStock"),PretupsI.C2C);
		data.setTransferId(transferID);
		data.setTransferType(_masterVO.getProperty("transferTypeStock"));
		data.setNetworkCode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setNetworkCodeFor(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setMsisdn("");
		data.setPin(data3);
		data.setLoginid(data1);
		data.setPassword(data2);
		data.setExtcode("");
		c2CViewTransferDetailsRequestPojo.setData(data);
	}

	// Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2CViewTransferDetailsStock";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSVTD18");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,PIN);
		C2CViewTransferDetailsAPI c2CViewTransferDetailsAPI = new C2CViewTransferDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CViewTransferDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CViewTransferDetailsAPI.addBodyParam(c2CViewTransferDetailsRequestPojo);
		c2CViewTransferDetailsAPI.setExpectedStatusCode(200);
		c2CViewTransferDetailsAPI.perform();
		c2CViewTransferDetailsResponsePojo = c2CViewTransferDetailsAPI.getAPIResponseAsPOJO(C2CViewTransferDetailsResponsePojo.class);
		String status = c2CViewTransferDetailsResponsePojo.getStatus();
		Assert.assertEquals("200", status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_02_Test_TransferIDBlank(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2CViewTransferDetailsStock";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSVTD19");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,PIN);
		data.setTransferId("");
		c2CViewTransferDetailsRequestPojo.setData(data);
		C2CViewTransferDetailsAPI c2CViewTransferDetailsAPI = new C2CViewTransferDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CViewTransferDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CViewTransferDetailsAPI.addBodyParam(c2CViewTransferDetailsRequestPojo);
		c2CViewTransferDetailsAPI.setExpectedStatusCode(200);
		c2CViewTransferDetailsAPI.perform();
		c2CViewTransferDetailsResponsePojo = c2CViewTransferDetailsAPI.getAPIResponseAsPOJO(C2CViewTransferDetailsResponsePojo.class);
		String message =c2CViewTransferDetailsResponsePojo.getMessage();

		Assert.assertEquals(message, "Transfer ID is empty.");
		Assertion.assertEquals(message, "Transfer ID is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	

	
	
	
	
	
	
	
	@Test(dataProvider = "userData")
	public void A_13_Test_BlankNetworkCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2CViewTransferDetailsStock";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSVTD30");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,PIN);
		data.setNetworkCode("");
		c2CViewTransferDetailsRequestPojo.setData(data);
		C2CViewTransferDetailsAPI c2CViewTransferDetailsAPI = new C2CViewTransferDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CViewTransferDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CViewTransferDetailsAPI.addBodyParam(c2CViewTransferDetailsRequestPojo);
		c2CViewTransferDetailsAPI.setExpectedStatusCode(200);
		c2CViewTransferDetailsAPI.perform();
		c2CViewTransferDetailsResponsePojo = c2CViewTransferDetailsAPI.getAPIResponseAsPOJO(C2CViewTransferDetailsResponsePojo.class);
		String message =c2CViewTransferDetailsResponsePojo.getMessage();

		Assert.assertEquals(message, "Network Code is empty.");
		Assertion.assertEquals(message, "Network Code is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	

	@Test(dataProvider = "userData")
	public void A_14_Test_BlankNetworkCodeFor(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2CViewTransferDetailsStock";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSVTD31");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,PIN);
		data.setNetworkCodeFor("");
		c2CViewTransferDetailsRequestPojo.setData(data);


		C2CViewTransferDetailsAPI c2CViewTransferDetailsAPI = new C2CViewTransferDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CViewTransferDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CViewTransferDetailsAPI.addBodyParam(c2CViewTransferDetailsRequestPojo);
		c2CViewTransferDetailsAPI.setExpectedStatusCode(200);
		c2CViewTransferDetailsAPI.perform();
		c2CViewTransferDetailsResponsePojo = c2CViewTransferDetailsAPI.getAPIResponseAsPOJO(C2CViewTransferDetailsResponsePojo.class);
		String message =c2CViewTransferDetailsResponsePojo.getMessage();

		Assert.assertEquals(message, "Network CodeFor is empty.");
		Assertion.assertEquals(message, "Network CodeFor is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

	@Test(dataProvider = "userData")
	public void A_17_Test_BlankTransferType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "Test_C2CViewTransferDetailsStock";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSVTD34");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(loginID,password,PIN);
		data.setTransferType("");
		c2CViewTransferDetailsRequestPojo.setData(data);
		
		
		C2CViewTransferDetailsAPI c2CViewTransferDetailsAPI = new C2CViewTransferDetailsAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CViewTransferDetailsAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CViewTransferDetailsAPI.addBodyParam(c2CViewTransferDetailsRequestPojo);
		c2CViewTransferDetailsAPI.setExpectedStatusCode(200);
		c2CViewTransferDetailsAPI.perform();
		c2CViewTransferDetailsResponsePojo = c2CViewTransferDetailsAPI.getAPIResponseAsPOJO(C2CViewTransferDetailsResponsePojo.class);
		String message =c2CViewTransferDetailsResponsePojo.getMessage();
		Assert.assertEquals(message, "Transfer Type is empty.");
		Assertion.assertEquals(message, "Transfer Type is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

}
