package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.o2ctransactionreversal.O2cTransactionReversalApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2ctransactionreversalrequestpojo.O2cTransactionReversalRequestPojo;
import restassuredapi.pojo.o2ctransactionreversalresponsepojo.O2cTransactionReversalResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.O2C_TXN_REVERSAL)
public class O2CTransactionReversalTest extends BaseTest {
	DateFormat df = null;
	Date dateobj = null;
	String currentDate = null;
	static String moduleCode;
	O2cTransactionReversalRequestPojo o2cTransactionReversalRequestPojo = new O2cTransactionReversalRequestPojo();
	O2cTransactionReversalResponsePojo o2cTransactionReversalResponsePojo = new O2cTransactionReversalResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Login login = new Login();

	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> transferDetails = new HashMap<String, String>();

	@DataProvider(name = "userData")
	public Object[][] TestDataFed() {
		String searchCategoryCode = _masterVO.getProperty("categoryCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		int user = 0;
		String GeoDomainCode = null;
		for (int i = 1; i <= rowCount; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
				user++;
				GeoDomainCode = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
			}
		}
		String GeoDomainName = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
		int rowCount1 = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount1; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i).equals(GeoDomainCode)) {
				GeoDomainName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, i);
			}
		}
		String DomainCode = null;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
		int rowCount2 = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount2; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i).equals(GeoDomainName))
				DomainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i).toUpperCase();
		}

		ArrayList<String> opUserData = new ArrayList<String>();
		Map<String, String> userInfo = UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.O2C_TRANSFER_REVAMP,
				searchCategoryCode);
		opUserData.add(userInfo.get("CATEGORY_NAME"));
		opUserData.add(userInfo.get("LOGIN_ID"));
		opUserData.add(userInfo.get("MSISDN"));
		opUserData.add(userInfo.get("USER_NAME"));
		Object[][] Data = new Object[user][12];
		int j = 0;
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
		for (int i = 1; i <= rowCount; i++) {
			if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
				Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				Data[j][7] = DomainCode;
				Data[j][8] = opUserData.get(0);
				Data[j][9] = opUserData.get(1);
				Data[j][10] = opUserData.get(2);
				Data[j][11] = opUserData.get(3);

				j++;
			}

		}
		return Data;
	}

	int index = 0;
	List<String> list = Arrays.asList("OT220204.1709.100001", "OT220204.1717.100001");

	public void setupDataTxnId() {
		if (list.size() > index)
			;
		o2cTransactionReversalRequestPojo.setTransactionID(list.get(index++));

		o2cTransactionReversalRequestPojo.setRemarks("Automation Testing");

//		String CustomerRechargeCode = _masterVO.getProperty("InternetRechargeCode");
//		data.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
//		data.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
//		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
//		data.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
//		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		data.setAmount(new RandomGeneration().randomNumberWithoutZero(3));
//		data.setPin(pin);
//		data.setNotifMsisdn(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
//		data.setExtrefnum(new RandomGeneration().randomNumeric(7));
//		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.C2S_SERVICES_SHEET);
//		int rowCount = ExcelUtility.getRowCount();
//		for (int rownum = 1; rownum <= rowCount; rownum++) {
//			String service = ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, rownum);
//			String cardGroupName = ExcelUtility.getCellData(0, ExcelI.CARDGROUP_NAME, rownum);
//			if (service.equals(CustomerRechargeCode)&& !cardGroupName.isEmpty()) {
//
//				data.setSelector(DBHandler.AccessHandler.getSelectorCode(ExcelUtility.getCellData(0, ExcelI.SELECTOR_NAME, rownum),service));
//				break;
//			}
//		}
//		data.setDate(_APIUtil.getCurrentTimeStamp());
//		internetRechargeRequestPojo.setData(data);

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

	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15598")
	public void A_01_Test_success_TxnID(String loginID, String password, String msisdn, String PIN, String parentName,
			String categoryName, String categorCode, String domainCode, String sCategoryName, String sLoginId,
			String sMSISDN, String userName) throws Exception {
		final String methodName = "A_01_Test_success_TxnID";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTXNREVERSAL01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupDataTxnId();

		O2cTransactionReversalApi o2cTxnRevListApi = new O2cTransactionReversalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cTxnRevListApi.setContentType(_masterVO.getProperty("contentType"));
		o2cTxnRevListApi.addBodyParam(o2cTransactionReversalRequestPojo);
		o2cTxnRevListApi.setExpectedStatusCode(200);
		o2cTxnRevListApi.perform();
		o2cTransactionReversalResponsePojo = o2cTxnRevListApi
				.getAPIResponseAsPOJO(O2cTransactionReversalResponsePojo.class);
		int status = o2cTransactionReversalResponsePojo.getStatus();
		Assert.assertEquals(201, status);
		Assertion.assertEquals(String.valueOf(status), "201");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15599")
	public void A_02_Test_Fail_reverseInvalidTransactionID(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String domainCode, String sCategoryName,
			String sLoginId, String sMSISDN, String userName) throws Exception {

		final String methodName = "A_02_Test_Fail_reverseInvalidTransactionID";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTXNREVERSAL02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		this.index = 0;
		setupDataTxnId();

		O2cTransactionReversalApi o2cTransactionReversalApi = new O2cTransactionReversalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cTransactionReversalApi.setContentType(_masterVO.getProperty("contentType"));
		o2cTransactionReversalApi.addBodyParam(o2cTransactionReversalRequestPojo);
		o2cTransactionReversalApi.setExpectedStatusCode(400);
		o2cTransactionReversalApi.perform();
		o2cTransactionReversalResponsePojo = o2cTransactionReversalApi
				.getAPIResponseAsPOJO(O2cTransactionReversalResponsePojo.class);
		int status = o2cTransactionReversalResponsePojo.getStatus();
		Assert.assertEquals(400, status);
		Assertion.assertEquals(String.valueOf(status), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15599")
	public void A_03_Test_Fail_transactionIDEmpty(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String domainCode, String sCategoryName,
			String sLoginId, String sMSISDN, String userName) throws Exception {

		final String methodName = "A_03_Test_Fail_transactionIDEmpty";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTXNREVERSAL03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		o2cTransactionReversalRequestPojo.setTransactionID(null);
		o2cTransactionReversalRequestPojo.setTransactionID("transactionID null");

		O2cTransactionReversalApi o2cTransactionReversalApi = new O2cTransactionReversalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cTransactionReversalApi.setContentType(_masterVO.getProperty("contentType"));
		o2cTransactionReversalApi.addBodyParam(o2cTransactionReversalRequestPojo);
		o2cTransactionReversalApi.setExpectedStatusCode(400);
		o2cTransactionReversalApi.perform();
		o2cTransactionReversalResponsePojo = o2cTransactionReversalApi
				.getAPIResponseAsPOJO(O2cTransactionReversalResponsePojo.class);
		int status = o2cTransactionReversalResponsePojo.getStatus();
		Assert.assertEquals(400, status);
		Assertion.assertEquals(String.valueOf(status), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-15599")
	public void A_04_Test_Fail_remarksmpty(String loginID, String password, String msisdn, String PIN,
			String parentName, String categoryName, String categorCode, String domainCode, String sCategoryName,
			String sLoginId, String sMSISDN, String userName) throws Exception {

		final String methodName = "A_04_Test_Fail_remarksmpty";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CTXNREVERSAL04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		o2cTransactionReversalRequestPojo.setTransactionID("remarks null");
		o2cTransactionReversalRequestPojo.setTransactionID(null);

		O2cTransactionReversalApi o2cTransactionReversalApi = new O2cTransactionReversalApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cTransactionReversalApi.setContentType(_masterVO.getProperty("contentType"));
		o2cTransactionReversalApi.addBodyParam(o2cTransactionReversalRequestPojo);
		o2cTransactionReversalApi.setExpectedStatusCode(400);
		o2cTransactionReversalApi.perform();
		o2cTransactionReversalResponsePojo = o2cTransactionReversalApi
				.getAPIResponseAsPOJO(O2cTransactionReversalResponsePojo.class);
		int status = o2cTransactionReversalResponsePojo.getStatus();
		Assert.assertEquals(400, status);
		Assertion.assertEquals(String.valueOf(status), "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

}
