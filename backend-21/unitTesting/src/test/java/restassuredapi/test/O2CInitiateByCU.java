package restassuredapi.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import restassuredapi.api.o2cinitiatebycu.O2CInitiateByCUAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2ctransferstockrequestpojo.Paymentdetail;
import restassuredapi.pojo.c2ctransferstockresponsepojo.C2CTransferStockResponsepojo;
import restassuredapi.pojo.o2cinitiatecureqpojo.O2CInitiateByCUReqData;
import restassuredapi.pojo.o2cinitiatecureqpojo.O2CInitiateByCURequest;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.txncalculationvoucherstockrequestpojo.Products;

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
@ModuleManager(name = Module.O2C_INITIATE_BY_CU)
public class O2CInitiateByCU extends BaseTest {
	DateFormat df = new SimpleDateFormat("dd/MM/YY");
    Date dateobj = new Date();
    String currentDate = df.format(dateobj);   
	static String moduleCode;
	O2CInitiateByCURequest o2CInitiateByCURequest = new O2CInitiateByCURequest();
	C2CTransferStockResponsepojo c2CTransferStockResponsepojo = new C2CTransferStockResponsepojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	

	O2CInitiateByCUReqData data = new O2CInitiateByCUReqData();
	Login login = new Login();
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails = null; 
	public void getExcelData(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelRowCount = ExcelUtility.getRowCount();
		for (int i = 1; i < channelRowCount; i++) {
			String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
			if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
				transferDetails.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
				transferDetails.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
				transferDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
				transferDetails.put("PIN", ExcelUtility.getCellData(0, ExcelI.PIN, i));
				break;
			}
		}
	}
    
	
	
	public void setupData() {
		if(transferDetails == null)
		{
			transferDetails = new HashMap<String,String> ();
			getExcelData();
		}
		List<Products> products = new ArrayList<Products>();
		List<Paymentdetail> paymentdetails  = new ArrayList<Paymentdetail>();
		data.setLanguage(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
			Products product = new Products();
			String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
			String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
			product.setProductcode(productShortCode);
			String userBalance = DBHandler.AccessHandler.getUserBalance(
					productCode, this.transferDetails.get("MSISDN"));
			int prBalance = (int) Double.parseDouble(userBalance);
			int quantity = (int) (prBalance * 0.01 * 0.01);
			product.setQty(String.valueOf(quantity));
			products.add(product);
		}
		data.setProducts(products);
		Paymentdetail paymentdetail = new Paymentdetail();
		data.setRefnumber("");
		paymentdetail.setPaymentinstnumber(randStr.randomNumeric(5));
		paymentdetail.setPaymentdate(currentDate);
		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		paymentdetails.add(paymentdetail);
		data.setPaymentdetails(paymentdetails);
		List<O2CInitiateByCUReqData> o2CInitiateByOptReqData = new ArrayList<>();
		o2CInitiateByOptReqData.add(data);
		o2CInitiateByCURequest.setData(o2CInitiateByOptReqData);
		
		oAuthenticationRequestPojo.setIdentifierType("loginid");
		oAuthenticationRequestPojo.setIdentifierValue(transferDetails.get("LOGIN_ID"));
		oAuthenticationRequestPojo.setPasswordOrSmspin(transferDetails.get("PASSWORD"));
		
	}
	 protected static String accessToken;
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
	    @BeforeMethod ()
	    public void BeforeMethod() throws Exception
	    {
		if (accessToken == null) {
			final String methodName = "Test_OAuthenticationTest";
			Log.startTestCase(methodName);

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());

			currentNode.assignCategory("REST");
			setupData();
			setHeaders();
			OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
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
	    }


	// Successful data with valid data.
	@Test
	@TestManager(TestKey="PRETUPS-9802")
	public void A_01_Test_success() throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin(transferDetails.get("PIN"));
		o2CInitiateByCURequest.getData().get(0).getProducts().get(0).setQty("10");
		o2CInitiateByCURequest.getData().get(0).getProducts().get(1).setQty("10");
		o2CInitiateByCURequest.getData().get(0).setRemarks("o2cInitiateByCUTest");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String messageCode = c2CTransferStockResponsepojo.getStatus();
		Assert.assertEquals(200, Integer.parseInt(messageCode));
		Assertion.assertEquals(messageCode, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	/*@Test
	 * @TestManager(TestKey="PRETUPS-0000")
	public void A_02_Test_invalid_token() throws Exception {
		final String methodName = "A_01_Test_invalid_token";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin(transferDetails.get("PIN"));
		o2CInitiateByCURequest.getData().get(0).setRemarks("o2cInitiateByCUTest");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken + (new RandomGeneration().randomAlphabets(4)));
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String status = c2CTransferStockResponsepojo.getMessageCode();
		Assert.assertEquals(1080002, Integer.parseInt(status));
		Assertion.assertEquals(status, "1080002");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}*/
	
	@Test
	@TestManager(TestKey="PRETUPS-9803")
	public void A_03_Test_invalid_pin() throws Exception {
		final String methodName = "A_03_Test_invalid_msisdn";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin("7436");
		o2CInitiateByCURequest.getData().get(0).setRemarks("o2cInitiateByCUTest");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String errorcode = c2CTransferStockResponsepojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(7015, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "7015");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test
	@TestManager(TestKey="PRETUPS-9804")
	public void A_04_Test_empty_remarks() throws Exception {
		final String methodName = "A_04_Test_empty_remarks";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin(transferDetails.get("PIN"));
		o2CInitiateByCURequest.getData().get(0).setRemarks("");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String errorcode = c2CTransferStockResponsepojo.getErrorMap().getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(3031, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "3031");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test
	@TestManager(TestKey="PRETUPS-9805")
	public void A_05_Test_invalid_product() throws Exception {
		final String methodName = "A_05_Test_invalid_product";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin(transferDetails.get("PIN"));
		o2CInitiateByCURequest.getData().get(0).setRemarks("o2cInitiateByCUTest");
		o2CInitiateByCURequest.getData().get(0).getProducts().get(0).setProductcode("106");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String errorcode = c2CTransferStockResponsepojo.getErrorMap().getRowErrorMsgLists().get(0).getRowErrorMsgList().get(0).getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(4599, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "4599");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test
	@TestManager(TestKey="PRETUPS-9806")
	public void A_06_Test_invalid_quantity() throws Exception {
		final String methodName = "A_06_Test_invalid_quantity";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINICU6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		o2CInitiateByCURequest.getData().get(0).setLanguage("1");
		o2CInitiateByCURequest.getData().get(0).setPin(transferDetails.get("PIN"));
		o2CInitiateByCURequest.getData().get(0).getProducts().get(0).setQty("-3");
		o2CInitiateByCURequest.getData().get(0).setRemarks("o2cInitiateByCUTest");
		O2CInitiateByCUAPI o2CInitiateByOptAPI = new O2CInitiateByCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2CInitiateByOptAPI.setContentType(_masterVO.getProperty("contentType"));
		o2CInitiateByOptAPI.addBodyParam(o2CInitiateByCURequest);
		o2CInitiateByOptAPI.perform();
		c2CTransferStockResponsepojo = o2CInitiateByOptAPI
				.getAPIResponseAsPOJO(C2CTransferStockResponsepojo.class);
		String errorcode = c2CTransferStockResponsepojo.getErrorMap().getRowErrorMsgLists().get(0).getRowErrorMsgList().get(0).getRowErrorMsgLists().get(0).getMasterErrorList().get(0).getErrorCode();
		Assert.assertEquals(8122, Integer.parseInt(errorcode));
		Assertion.assertEquals(errorcode, "8122");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
