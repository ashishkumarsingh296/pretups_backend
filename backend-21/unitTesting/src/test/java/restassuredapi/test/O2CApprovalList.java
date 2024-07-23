package restassuredapi.test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.o2capprovallist.O2CApprovalTemplateApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2capprovalrequestpojo.O2CApprovalListRequestPojo;
import restassuredapi.pojo.o2capprovalresponsepojo.O2CApprovalListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

@ModuleManager(name = Module.O2C_APPROVAL_API_LIST)
public class O2CApprovalList extends BaseTest {
	  
	static String moduleCode;
	O2CApprovalListRequestPojo o2CApprovalListRequestPojo = new O2CApprovalListRequestPojo();
	O2CApprovalListResponsePojo o2CApprovalListResponsePojo = new O2CApprovalListResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	

	
	RandomGeneration randStr = new RandomGeneration();
	
	
@DataProvider(name = "userData")
public Object[][] TestDataFeed1() {
	String MasterSheetPath = _masterVO.getProperty("DataProvider");
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	int rowCountoperator = ExcelUtility.getRowCount();
	int user=0;
	for (int i = 1; i <=rowCountoperator; i++) {
		if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,i).equals("BCU"))
		user++;
		
	}
	
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
	int rowCount = ExcelUtility.getRowCount();
	ArrayList<String> msisdn2 = new ArrayList<>();
	for(int i=1;i<=rowCount;i++) {
		msisdn2.add(ExcelUtility.getCellData(0, ExcelI.MSISDN,i));

	}

	Object[][] Data = new Object[user][8];
	int j=0;
	ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	for (int i = 1; i <=rowCountoperator; i++) {
		if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,i).equals(PretupsI.CHANNELADMIN_CATCODE)) {
		Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
		Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
		Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
		Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
		Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
		Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
		Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
		Data[j][7] =  msisdn2.get(i% msisdn2.size());
		j++;
		}
	}

	return Data;
}


	public void setupData() {
		o2CApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("approvallevel1"));
		o2CApprovalListRequestPojo.setCategory(_masterVO.getProperty("category"));
		o2CApprovalListRequestPojo.setDomain(_masterVO.getProperty("domain"));
		o2CApprovalListRequestPojo.setGeographicalDomain(_masterVO.getProperty("domain"));
		o2CApprovalListRequestPojo.setMsisdn("");
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
	@TestManager(TestKey="PRETUPS-10004")
	public void A_01_TEST_SUCCESS(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String msisdn2) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPLIST1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		
		O2CApprovalTemplateApi o2CApprovalTemplateApi = new O2CApprovalTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CApprovalTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2CApprovalTemplateApi.addBodyParam(o2CApprovalListRequestPojo);
		o2CApprovalTemplateApi.setExpectedStatusCode(200);
		o2CApprovalTemplateApi.perform();
		o2CApprovalListResponsePojo = o2CApprovalTemplateApi
				.getAPIResponseAsPOJO(O2CApprovalListResponsePojo.class);
		//String txnid = o2CApprovalListResponsePojo.getDataObject().getTxnid();
		String status = String.valueOf(o2CApprovalListResponsePojo.getStatus());
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-10005")
	public void A_02_Test_Success_Msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String msisdn2) throws Exception {
		final String methodName = "A_02_Test_Success_Msisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPLIST2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		o2CApprovalListRequestPojo.setCategory("");
		o2CApprovalListRequestPojo.setDomain("");
		o2CApprovalListRequestPojo.setGeographicalDomain("");
		o2CApprovalListRequestPojo.setMsisdn(msisdn2);
		
		O2CApprovalTemplateApi o2CApprovalTemplateApi = new O2CApprovalTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CApprovalTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2CApprovalTemplateApi.addBodyParam(o2CApprovalListRequestPojo);
		o2CApprovalTemplateApi.setExpectedStatusCode(200);
		o2CApprovalTemplateApi.perform();
		o2CApprovalListResponsePojo = o2CApprovalTemplateApi
				.getAPIResponseAsPOJO(O2CApprovalListResponsePojo.class);
		//String txnid = o2CApprovalListResponsePojo.getDataObject().getTxnid();
		String status = String.valueOf(o2CApprovalListResponsePojo.getStatus());
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-10006")
	public void A_03_Test_Invalid_level(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String msisdn2) throws Exception {
		final String methodName = "A_03_Test_Invalid_level";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPLIST3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		o2CApprovalListRequestPojo.setApprovalLevel("4");
		
		
		O2CApprovalTemplateApi o2CApprovalTemplateApi = new O2CApprovalTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CApprovalTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2CApprovalTemplateApi.addBodyParam(o2CApprovalListRequestPojo);
		//o2CApprovalTemplateApi.setExpectedStatusCode(200);
		o2CApprovalTemplateApi.perform();
		o2CApprovalListResponsePojo = o2CApprovalTemplateApi
				.getAPIResponseAsPOJO(O2CApprovalListResponsePojo.class);
		//String txnid = o2CApprovalListResponsePojo.getDataObject().getTxnid();
		String status = String.valueOf(o2CApprovalListResponsePojo.getMessageCode());
		if(status == "241159")
		Assert.assertEquals(241159, status);
		Assertion.assertEquals(status, "241159");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-10007")
	public void A_04_Test_Invalid_domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String msisdn2) throws Exception {
		final String methodName = "A_04_Test_Invalid_domain";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPLIST4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		o2CApprovalListRequestPojo.setGeographicalDomain("");
		
		
		O2CApprovalTemplateApi o2CApprovalTemplateApi = new O2CApprovalTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CApprovalTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2CApprovalTemplateApi.addBodyParam(o2CApprovalListRequestPojo);
		//o2CApprovalTemplateApi.setExpectedStatusCode(200);
		o2CApprovalTemplateApi.perform();
		o2CApprovalListResponsePojo = o2CApprovalTemplateApi
				.getAPIResponseAsPOJO(O2CApprovalListResponsePojo.class);
		//String txnid = o2CApprovalListResponsePojo.getDataObject().getTxnid();
		String status = String.valueOf(o2CApprovalListResponsePojo.getMessageCode());
		if(status == "8131")
		Assert.assertEquals(8131, status);
		Assertion.assertEquals(status, "8131");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-10008")
	public void A_05_Test_Invalid_msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String msisdn2) throws Exception {
		final String methodName = "A_05_Test_Invalid_msisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CAPPLIST5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		o2CApprovalListRequestPojo.setCategory("");
		o2CApprovalListRequestPojo.setDomain("");
		o2CApprovalListRequestPojo.setGeographicalDomain("");
		o2CApprovalListRequestPojo.setMsisdn(new RandomGeneration().randomAlphaNumeric(10));
		
		
		O2CApprovalTemplateApi o2CApprovalTemplateApi = new O2CApprovalTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CApprovalTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		o2CApprovalTemplateApi.addBodyParam(o2CApprovalListRequestPojo);
		//o2CApprovalTemplateApi.setExpectedStatusCode(200);
		o2CApprovalTemplateApi.perform();
		o2CApprovalListResponsePojo = o2CApprovalTemplateApi
				.getAPIResponseAsPOJO(O2CApprovalListResponsePojo.class);
		//String txnid = o2CApprovalListResponsePojo.getDataObject().getTxnid();
		String status = String.valueOf(o2CApprovalListResponsePojo.getMessageCode());
		if(status == "5005")
		Assert.assertEquals(5005, status);
		Assertion.assertEquals(status, "5005");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
		}
