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
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.viewtaxcalculationvoucherstock.ViewTaxCalculationVoucherStockApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.Data;
import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.Products;
import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.ViewTxnCalculationRequestPojo;
import restassuredapi.pojo.txncalculationvoucherstockresponsepojo.ViewTxnCalculationVoucherStockResponsePojo;

@ModuleManager(name = Module.REST_VIEW_TXN_CALCULATOR)
public class ViewTxnCalculationVoucherStock extends BaseTest {
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	String currentDate = df.format(dateobj);
	static String moduleCode;
	ViewTxnCalculationRequestPojo viewTxnCalculationRequestPojo = new ViewTxnCalculationRequestPojo();
	ViewTxnCalculationVoucherStockResponsePojo ViewTxnCalculationVoucherStockResponsePojo = new ViewTxnCalculationVoucherStockResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();

	@DataProvider(name = "userData")
	public Object[][] TestDataFeed() {
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();


		Object[][] Data = new Object[rowCount][8];
		int j = 0;
		for (int i = 1; i <= rowCount; i++) {
			Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i); //from
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.COMMISSION_PROFILE, i);
			j++;
		}

		return Data;

	}


	public void setupData(String data1, String data2, String data3) {

		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setMsisdn(data1);
		data.setPin(data2);
		data.setExtcode("");
		data.setCommissionProfileID(DBHandler.AccessHandler.getCommProfileID(data3));
		data.setCommissionProfileVersion("1");

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);

		int rowCount = ExcelUtility.getRowCount();
		Products products[] = new Products[rowCount];


		for (int i = 1; i <= rowCount; i++) {
			String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
			String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			products[i - 1] = new Products();
			products[i - 1].setProductcode(productShortCode);
			String userBalance = DBHandler.AccessHandler.getUserBalance(productCode, data1);
			int prBalance = (int) Double.parseDouble(userBalance);
			int quantity = (int) (prBalance * 0.01 * 0.01);
			products[i - 1].setQty(String.valueOf(quantity));
		}
		data.setProducts(products);
		data.setCbcflag(_masterVO.getProperty("status_Y"));
		data.setTransferSubType(_masterVO.getProperty("transferOperation"));
		data.setTransferType(_masterVO.getProperty("scope"));
		data.setPaymenttype(_masterVO.getProperty("scope"));
		data.setDualCommission(_masterVO.getProperty("DualCommission"));
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		viewTxnCalculationRequestPojo.setData(data);

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
	public void A_01_Test_success_ViewTaxCalculationVoucherStock(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Positive_ViewTaxCalculationVoucherStock";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewTaxCalculationVoucherStockApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewTaxCalculationVoucherStockApi.setContentType(_masterVO.getProperty("contentType"));
		viewTaxCalculationVoucherStockApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewTaxCalculationVoucherStockApi.setExpectedStatusCode(200);
		viewTaxCalculationVoucherStockApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewTaxCalculationVoucherStockApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	//network code  provided is blank
	@Test(dataProvider = "userData")
	public void A_06_Test_Invalid_Network_Code(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Invalid_Network_Code";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW6");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewTaxCalculationVoucherStockApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewTaxCalculationVoucherStockApi.setContentType(_masterVO.getProperty("contentType"));
		viewTxnCalculationRequestPojo.getData().setExtnwcode("");
		viewTaxCalculationVoucherStockApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewTaxCalculationVoucherStockApi.setExpectedStatusCode(200);
		viewTaxCalculationVoucherStockApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewTaxCalculationVoucherStockApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());
		String message = ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	// blank msisdn
	@Test(dataProvider = "userData")
	public void A_07_Test_Blank_Msisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Blank_Msisdn";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewTaxCalculationVoucherStockApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewTaxCalculationVoucherStockApi.setContentType(_masterVO.getProperty("contentType"));
		viewTxnCalculationRequestPojo.getData().setMsisdn("");
		viewTaxCalculationVoucherStockApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewTaxCalculationVoucherStockApi.setExpectedStatusCode(200);
		viewTaxCalculationVoucherStockApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewTaxCalculationVoucherStockApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());
		String message = ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	@Test(dataProvider = "userData")
	public void A_08_Test_Blank_Pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Blank_Pin";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW8");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewTaxCalculationVoucherStockApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewTaxCalculationVoucherStockApi.setContentType(_masterVO.getProperty("contentType"));
		viewTxnCalculationRequestPojo.getData().setPin("");
		viewTaxCalculationVoucherStockApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewTaxCalculationVoucherStockApi.setExpectedStatusCode(200);
		viewTaxCalculationVoucherStockApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewTaxCalculationVoucherStockApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());
		String message = ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "PIN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	@Test(dataProvider = "userData")
	public void A_09_Test_Invalid_Pin_length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Invalid_Pin_length";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW9");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewPassBookApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewPassBookApi.setContentType(_masterVO.getProperty("contentType"));
		viewTxnCalculationRequestPojo.getData().setPin("421312");
		viewPassBookApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewPassBookApi.setExpectedStatusCode(200);
		viewPassBookApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewPassBookApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());
		String message = ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "Invalid PIN length.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}

	//  msisdn of invalid length.
	@Test(dataProvider = "userData")
	public void A_10_Test_Msisdn_Length(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String comProfile) throws Exception {
		final String methodName = "Test_Msisdn_Length";
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("TXNCALVIEW10");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn, PIN, comProfile);
		ViewTaxCalculationVoucherStockApi viewPassBookApi = new ViewTaxCalculationVoucherStockApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		viewPassBookApi.setContentType(_masterVO.getProperty("contentType"));
		viewTxnCalculationRequestPojo.getData().setMsisdn(viewTxnCalculationRequestPojo.getData().getMsisdn() + "23423423234234234");
		viewPassBookApi.addBodyParam(viewTxnCalculationRequestPojo);
		viewPassBookApi.setExpectedStatusCode(200);
		viewPassBookApi.perform();
		ViewTxnCalculationVoucherStockResponsePojo = viewPassBookApi
				.getAPIResponseAsPOJO(ViewTxnCalculationVoucherStockResponsePojo.class);
		int statusCode = Integer.parseInt(ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getTxnstatus());
		String message = ViewTxnCalculationVoucherStockResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(statusCode, 206);
		Assertion.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}
}
