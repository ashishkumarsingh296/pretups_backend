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
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cbatchuserslistdownload.C2CBatchUsersListDownloadAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbatchuserslistdownloadresponsepojo.C2CBatchUsersListDownloadResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.C2C_BATCH_USERS_DOWNLOAD)
public class C2CBatchUsersListDownload  extends BaseTest  {
	
	
	static String moduleCode;
	C2CBatchUsersListDownloadResponsePojo c2CBatchUsersListDownloadResponsePojo = new C2CBatchUsersListDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	HashMap<String,String> transfer_Details=new HashMap<String,String>();


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
	@TestManager(TestKey="PRETUPS-8049")
	public void A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "A_01_Test_Success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CBULD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		C2CBatchUsersListDownloadAPI c2CBatchUsersListDownloadAPI = new C2CBatchUsersListDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));

		c2CBatchUsersListDownloadAPI.setCategory(categoryName);
		c2CBatchUsersListDownloadAPI.setOperationType(_masterVO.getProperty("transferOperation"));
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		 

		c2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(C2CBatchUsersListDownloadResponsePojo.class);

		String status = c2CBatchUsersListDownloadResponsePojo.getStatus();

		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	
	@Test
	@TestManager(TestKey="PRETUPS-8050")
	public void A_02_Test_Invalid_Category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "A_02_Test_Invalid_Category";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CBULD2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		C2CBatchUsersListDownloadAPI c2CBatchUsersListDownloadAPI = new C2CBatchUsersListDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));

		c2CBatchUsersListDownloadAPI.setCategory("invalid");
		c2CBatchUsersListDownloadAPI.setOperationType(_masterVO.getProperty("transferOperation"));
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		 

		c2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(C2CBatchUsersListDownloadResponsePojo.class);

		String status = c2CBatchUsersListDownloadResponsePojo.getStatus();

		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	
	@Test
	@TestManager(TestKey="PRETUPS-8051")
	public void A_03_Test_Invalid_OperationType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = " A_03_Test_Invalid_OperationType";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CBULD3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		C2CBatchUsersListDownloadAPI c2CBatchUsersListDownloadAPI = new C2CBatchUsersListDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));

		c2CBatchUsersListDownloadAPI.setCategory(categoryName);
		c2CBatchUsersListDownloadAPI.setOperationType(_masterVO.getProperty("invalid"));
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		 

		c2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(C2CBatchUsersListDownloadResponsePojo.class);

		String status = c2CBatchUsersListDownloadResponsePojo.getStatus();

		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	
	}
	


}
