package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cbuyvoucher.C2CBuyVoucherAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuyvoucherrequestpojo.C2CBuyVoucherRequestPojo;
import restassuredapi.pojo.c2cbuyvoucherrequestpojo.Data;
import restassuredapi.pojo.c2cbuyvoucherrequestpojo.VoucherDetail;
import restassuredapi.pojo.c2cbuyvoucherresponsepojo.C2CBuyVoucherResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_C2C_BUY_VOUCHER)
public class C2CBuyVoucher extends BaseTest {
	static String moduleCode;

	C2CBuyVoucherRequestPojo c2CBuyVoucherRequestPojo = new C2CBuyVoucherRequestPojo();
	C2CBuyVoucherResponsePojo c2CBuyVoucherResponsePojo = new C2CBuyVoucherResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Data data = new Data();
	VoucherDetail voucher = new VoucherDetail();
	

	RandomGeneration randomGeneration = new RandomGeneration();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
			int categorySizeCounter = 0;
			for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
					categorySizeCounter++;
				}
			}
			categorySize.add("" + categorySizeCounter);
			totalObjectCounter = totalObjectCounter + categorySizeCounter;
		}
		Object[][] Data = new Object[totalObjectCounter][9];
		for (int j = 0, k = 0; j < alist2.size(); j++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int excelRowSize = ExcelUtility.getRowCount();
			String ChannelUserMSISDN = null;
			for (int i = 1; i <= excelRowSize; i++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
					ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
					break;
				}
			}
			for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
					Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
					Data[k][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
					Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
					Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
					Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
					Data[k][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
					Data[k][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, excelCounter);
					Data[k][8] = ChannelUserMSISDN;

					k++;
				}
			}
		}
		return Data;
	}

	public void setupData(String data1,String data2,String data3) {
		
		List<VoucherDetail> voucherDetails = new ArrayList<VoucherDetail>();

		// setting current date
		DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
		Date dateobj = new Date();
		String currentDate = df.format(dateobj);

		// setting voucher details
		voucher.setDenomination(_masterVO.getProperty("buyVoucherDenom"));
		voucher.setQuantity(_masterVO.getProperty("buyVoucherQuantity"));
		
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode("");
		data.setMsisdn(data1);
		data.setPin(data2);
		data.setRemarks(_masterVO.getProperty("Remarks"));

		data.setLanguage1(_masterVO.getProperty("languageCode0"));
		
		data.setExtrefnum("");
		data.setMsisdn2(data3);
		data.setLoginid2("");
		data.setExtcode2("");
		voucher.setVoucherType(_masterVO.getProperty("buyVoucherType"));
		voucher.setVouchersegment(_masterVO.getProperty("segmentTypeNational"));
		voucherDetails.add(voucher);
		data.setVoucherDetails(voucherDetails);
		data.setPaymentinstdate(currentDate);
		data.setPaymentinstcode(_masterVO.getProperty("paymentInstrumentCode"));
		data.setPaymentinstnum(randomGeneration.randomNumeric(5));
		c2CBuyVoucherRequestPojo.setData(data);
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

	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyVoucherResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

		@Test(dataProvider = "userData")
	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setMsisdn("");
		c2CBuyVoucherRequestPojo.setData(data);
		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message,
				"Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(message,
				"Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

		@Test(dataProvider = "userData")
	public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));
	
		data.setPin("");
		c2CBuyVoucherRequestPojo.setData(data);
		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PIN can not be blank.");
		Assertion.assertEquals(message, "PIN can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	// optional
			@Test(dataProvider = "userData")
	public void A_04_Test_BlankExtrefnum(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setExtrefnum("");
		c2CBuyVoucherRequestPojo.setData(data);
		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyVoucherResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

		@Test(dataProvider = "userData")
	public void A_05_Test_BlankVouchersegment(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));
		voucher.setVouchersegment("");
		c2CBuyVoucherRequestPojo.setData(data);
		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
		Assertion.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
		@Test(dataProvider = "userData")
	public void A_09_Test_BlankVoucherDetails(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		List<VoucherDetail> voucherDetails = new ArrayList<VoucherDetail>();
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		voucher.setDenomination("");
		voucher.setQuantity("");
		voucherDetails.add(voucher);
		data.setVoucherDetails(voucherDetails);
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Voucher Details Are Empty");
		Assertion.assertEquals(message, "5026");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


		@Test(dataProvider = "userData")
	public void A_10_Test_BlankExtNwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		data.setExtnwcode("");
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

		@Test(dataProvider = "userData")
	public void A_11_Test_BlankVoucherType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		voucher.setVoucherType("");
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
		@Test(dataProvider = "userData")
	public void A_12_Test_BlankPaymentInstrumentType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		data.setPaymentinstcode("");
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
		@Test(dataProvider = "userData")
	public void A_13_Test_BlankPaymentInstrumentDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		data.setPaymentinstdate("");
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Payment instrument date is blank.");
		Assertion.assertEquals(message, "Payment instrument date is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
		@Test(dataProvider = "userData")
	public void A_14_Test_BlankPaymentInstrumentNumber(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CBuyVoucherAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

		data.setPaymentinstnum("");
		c2CBuyVoucherRequestPojo.setData(data);

		c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
		c2CBuyVoucherAPI.setExpectedStatusCode(200);
		c2CBuyVoucherAPI.perform();
		c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
		String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
		Assertion.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	// work left
			@Test(dataProvider = "userData")
		public void A_15_Test_InvalidLanguage1(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
			final String methodName = "Test_C2CBuyVoucherAPI";
			Log.startTestCase(methodName);
			if(_masterVO.getProperty("identifierType").equals("loginid"))
				BeforeMethod(loginID, password,categoryName);
			else if(_masterVO.getProperty("identifierType").equals("msisdn"))
				BeforeMethod(msisdn, PIN,categoryName);
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CBV15");
			moduleCode = CaseMaster.getModuleCode();

			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
			currentNode.assignCategory("REST");
			setupData(msisdn,PIN,msisdn2);
			C2CBuyVoucherAPI c2CBuyVoucherAPI = new C2CBuyVoucherAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			c2CBuyVoucherAPI.setContentType(_masterVO.getProperty("contentType"));

			data.setLanguage1(new RandomGeneration().randomNumberWithoutZero(2));
			c2CBuyVoucherRequestPojo.setData(data);

			c2CBuyVoucherAPI.addBodyParam(c2CBuyVoucherRequestPojo);
			c2CBuyVoucherAPI.setExpectedStatusCode(200);
			c2CBuyVoucherAPI.perform();
			c2CBuyVoucherResponsePojo = c2CBuyVoucherAPI.getAPIResponseAsPOJO(C2CBuyVoucherResponsePojo.class);
			String message = c2CBuyVoucherResponsePojo.getDataObject().getMessage();
			Assert.assertEquals(message, "LANGUAGE1 is not valid.");
			Assertion.assertEquals(message, "LANGUAGE1 is not valid.");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

}
