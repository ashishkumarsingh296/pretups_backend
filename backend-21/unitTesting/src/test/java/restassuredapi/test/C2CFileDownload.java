package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cfiledownload.C2CFileDownloadAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbatchdownloadtemplateresponsepojo.C2cBatchDownloadTemplateResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2C_DOWNLOAD_FILE)
public class C2CFileDownload extends BaseTest {

	static String moduleCode;
	C2cBatchDownloadTemplateResponsePojo c2cBatchDownloadTemplateResponsePojo = new C2cBatchDownloadTemplateResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

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
			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);


	}

	@Test(dataProvider= "userData")
	@TestManager(TestKey="PRETUPS-6956")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CDL1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		String txnId = DBHandler.AccessHandler.getC2CTransactionID(true);
		if(BTSLUtil.isNullString(txnId) == false)
		{
			C2CFileDownloadAPI c2cFileDownloadAPI = new C2CFileDownloadAPI(
					_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
			c2cFileDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
			c2cFileDownloadAPI.setTransactionID(txnId);
			c2cFileDownloadAPI.setExpectedStatusCode(200);
			c2cFileDownloadAPI.perform();
			c2cBatchDownloadTemplateResponsePojo = c2cFileDownloadAPI
					.getAPIResponseAsPOJO(C2cBatchDownloadTemplateResponsePojo.class);

			long statusCode = c2cBatchDownloadTemplateResponsePojo.getStatus();

			Assert.assertEquals(statusCode, 200);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
		}
		else
		{
			Assertion.assertSkip("No transaction id found with a document");
			Assertion.completeAssertions();
		}
		Log.endTestCase(methodName);
		
	}
	@Test(dataProvider= "userData")
	@TestManager(TestKey="PRETUPS-6957")
	public void A_02_Test_invalidTxnId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
		final String methodName = "A_02_Test_invalidTxnId";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CDL2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		String txnId = "ABC";
		C2CFileDownloadAPI c2cFileDownloadAPI = new C2CFileDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2cFileDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cFileDownloadAPI.setTransactionID(txnId);
		c2cFileDownloadAPI.setExpectedStatusCode(400);
		c2cFileDownloadAPI.perform();
		c2cBatchDownloadTemplateResponsePojo = c2cFileDownloadAPI
				.getAPIResponseAsPOJO(C2cBatchDownloadTemplateResponsePojo.class);
		
		
		String status = c2cBatchDownloadTemplateResponsePojo.getMessageCode();
		Assert.assertEquals(241024, Integer.parseInt(status));
		Assertion.assertEquals(status, "241024");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider= "userData")
	@TestManager(TestKey="PRETUPS-6958")
	public void A_03_Test_invalidTxnIdWithNoFile(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
		final String methodName = "A_03_Test_invalidTxnIdWithNoFile";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CDL3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		
		String txnId = DBHandler.AccessHandler.getC2CTransactionID(false);
		C2CFileDownloadAPI c2cFileDownloadAPI = new C2CFileDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2cFileDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cFileDownloadAPI.setTransactionID(txnId);
		c2cFileDownloadAPI.setExpectedStatusCode(400);
		c2cFileDownloadAPI.perform();
		c2cBatchDownloadTemplateResponsePojo = c2cFileDownloadAPI
				.getAPIResponseAsPOJO(C2cBatchDownloadTemplateResponsePojo.class);
		
		
		String status = c2cBatchDownloadTemplateResponsePojo.getMessageCode();
		Assert.assertEquals(241024, Integer.parseInt(status));
		Assertion.assertEquals(status, "241024");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider= "userData")
	@TestManager(TestKey="PRETUPS-6959")
	public void A_04_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode ) throws Exception {
		final String methodName = "A_04_Test_invalid_token";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CDL4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		String txnId = DBHandler.AccessHandler.getC2CTransactionID(false);
		C2CFileDownloadAPI c2cFileDownloadAPI = new C2CFileDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken+new RandomGeneration().randomAlphabets(4));
		c2cFileDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cFileDownloadAPI.setTransactionID(txnId);
		c2cFileDownloadAPI.setExpectedStatusCode(401);
		c2cFileDownloadAPI.perform();
		
		
		c2cBatchDownloadTemplateResponsePojo = c2cFileDownloadAPI
				.getAPIResponseAsPOJO(C2cBatchDownloadTemplateResponsePojo.class);
		String status = c2cBatchDownloadTemplateResponsePojo.getMessageCode();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}	
	
		}
