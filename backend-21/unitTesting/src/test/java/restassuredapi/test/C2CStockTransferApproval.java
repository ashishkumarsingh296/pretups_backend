//package restassuredapi.test;
//
//import java.text.DateFormat;
//import java.text.MessageFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//import org.testng.Assert;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.classes.BaseTest;
//import com.classes.CaseMaster;
//import com.commons.ExcelI;
//import com.commons.MasterI;
//import com.commons.PretupsI;
//import com.dbrepository.DBHandler;
//import com.reporting.extent.entity.ModuleManager;
//import com.utils.Assertion;
//import com.utils.ExcelUtility;
//import com.utils.Log;
//import com.utils.RandomGeneration;
//import com.utils._masterVO;
//import com.utils.constants.Module;
//
//import restassuredapi.api.c2cstock.C2CStockApprovalAPI;
//import restassuredapi.api.oauthentication.OAuthenticationAPI;
//import restassuredapi.pojo.c2cstockapprovalrequestpojo.C2CStockApprovalRequestPojo;
//import restassuredapi.pojo.c2cstockapprovalrequestpojo.Data;
//import restassuredapi.pojo.c2cstockapprovalrequestpojo.Paymentdetail;
//import restassuredapi.pojo.c2cstockapprovalrequestpojo.Product;
//import restassuredapi.pojo.c2cstockapprovalresponsepojo.C2CStockApprovalResponsePojo;
//import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
//import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
//
//@ModuleManager(name = Module.REST_C2C_STOCK_APPROVAL)
//public class C2CStockTransferApproval extends BaseTest {
//	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
//     Date dateobj = new Date();
//     String currentDate=df.format(dateobj);
//	static String moduleCode;
//	C2CStockApprovalRequestPojo c2CStockApprovalRequestPojo = new C2CStockApprovalRequestPojo();
//	C2CStockApprovalResponsePojo c2CStockApprovalResponsePojo = new C2CStockApprovalResponsePojo();
//	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
//	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
//
//	Data data = new Data();
//	Paymentdetail paymentdetail= new Paymentdetail();
//	Product product = new Product();
//	List<Paymentdetail> paymentDetail = new ArrayList<Paymentdetail>();
//
//
//	@DataProvider(name ="userData")
//	public Object[][] TestDataFeed() {
//		HashMap<String,String> tranferDetails=new HashMap<String,String>();
//		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
//		String MasterSheetPath = _masterVO.getProperty("DataProvider");
//		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//		int rowCount = ExcelUtility.getRowCount();
//		ArrayList<String> alist1 = new ArrayList<String>();
//		ArrayList<String> alist2 = new ArrayList<String>();
//		ArrayList<String> categorySize = new ArrayList<String>();
//		for (int i = 1; i <= rowCount; i++) {
//			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
//			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
//			if (aList.contains(C2CTransferCode)) {
//				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
//				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
//				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
//			}
//		}
//		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
//		int totalObjectCounter = 0;
//		for (int i = 0; i < alist2.size(); i++) {
//			int categorySizeCounter = 0;
//			for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
//					categorySizeCounter++;
//				}
//			}
//			categorySize.add("" + categorySizeCounter);
//			totalObjectCounter = totalObjectCounter + categorySizeCounter;
//		}
//		Object[][] Data = new Object[totalObjectCounter][9];
//		for (int j = 0, k = 0; j < alist2.size(); j++) {
//			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
//			int excelRowSize = ExcelUtility.getRowCount();
//			String ChannelUserMSISDN = null;
//			for (int i = 1; i <= excelRowSize; i++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
//					ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
//					break;
//				}
//			}
//			for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
//				if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
//					Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
//					Data[k][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
//					Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
//					Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
//					Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
//					Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
//					Data[k][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
//					Data[k][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, excelCounter);
//					Data[k][8] = ChannelUserMSISDN;
//
//					k++;
//				}
//			}
//		}
//		return Data;
//	}
//	Map<String, Object> headerMap = new HashMap<String, Object>();
//
//	public void setHeaders() {
//		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
//		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
//		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
//		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
//		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
//		headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
//		headerMap.put("scope", _masterVO.getProperty("scope"));
//		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
//	}
//
//	public void setupAuth(String data1, String data2) {
//		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
//		oAuthenticationRequestPojo.setIdentifierValue(data1);
//		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
//
//
//	}
//
//	// Successful data with valid data.
//
//	protected static String accessToken;
//
//
//	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception
//	{
//		final String methodName = "Test_OAuthenticationTest";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
//		moduleCode = CaseMaster.getModuleCode();
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//
//		currentNode.assignCategory("REST");
//
//		setHeaders();
//		setupAuth(data1,data2);
//		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap);
//		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
//		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
//		oAuthenticationAPI.setExpectedStatusCode(200);
//		oAuthenticationAPI.perform();
//		oAuthenticationResponsePojo = oAuthenticationAPI
//				.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
//		long statusCode = oAuthenticationResponsePojo.getStatus();
//
//		accessToken = oAuthenticationResponsePojo.getToken();
//		org.testng.Assert.assertEquals(statusCode, 200);
//		Assertion.assertEquals(Long.toString(statusCode), "200");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//
//
//	}
//	
//	public void setupData(String data1,String data2,String data3) {
//
//		ArrayList<Product> products = new ArrayList<Product>();
//		RandomGeneration randomGeneration = new RandomGeneration();
//		data.setDate("");
//		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		data.setMsisdn(data1); //from msisdn
//		data.setPin(data2); //from pin
//		data.setLoginid("");
//		data.setPassword("");
//		data.setExtcode("");
//		String TxnId= DBHandler.AccessHandler.fetchTransferIdWithStatus(_masterVO.getProperty("currstatus"),_masterVO.getProperty("transferTypeStock"),PretupsI.C2C);
//		data.setTxnid(TxnId);
//		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
//		int rowCount = ExcelUtility.getRowCount();
//		for (int i = 1; i <= rowCount; i++) {
//		product = new Product();
//		String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
//		product.setProductcode(productShortCode);
//			String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
//			product.setProductcode(productShortCode);
//			//	String productName = DBHandler.AccessHandler.getProductNameByCode(productCode);
//			String userBalance = DBHandler.AccessHandler.getUserBalance(productCode, data3);
//			int prBalance= (int) Double.parseDouble(userBalance);
//			int quantity=(int) (prBalance*0.01*0.01);
//			product.setQty(String.valueOf(quantity));
//			products.add(product);
//		}
//		
//		data.setProducts(products);
//		data.setRefnumber("");
//		paymentdetail.setPaymentinstnumber(randomGeneration.randomNumeric(5));
//		paymentdetail.setPaymentdate(currentDate);
//		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
//		paymentDetail.add(paymentdetail);
//		data.setPaymentdetails(paymentDetail);
//		data.setCurrentstatus(_masterVO.getProperty("currstatus"));
//		data.setStatus(_masterVO.getProperty("status1"));
//		data.setRemarks(_masterVO.getProperty("Remarks"));
//		
//		data.setLanguage1(_masterVO.getProperty("languageCode0"));
//			
//		c2CStockApprovalRequestPojo.setData(data);
//	}
//
//	// Successful data with valid data.
//	@Test(dataProvider = "userData")
//	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA1");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(200);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String statusCode = c2CStockApprovalResponsePojo.getDataObject().getTxnstatus();
//		String msg = c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		if(statusCode.equals("200")) {
//			Assertion.assertEquals("200", statusCode);
//		}
//		
//		else if(statusCode.equals("400")) {
//			if(msg.equals("Transaction ID does not exist with status APPRV1."))
//				Assertion.assertSkip("No voucher is present");
//			
//			else
//				Assertion.assertFail("Test Case failed due to some error.");	
//		}
//		
//		else
//			Assertion.assertFail("Test Case failed due to some error.");
//	}
//	@Test(dataProvider = "userData")
//	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA2");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("");
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		
//		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA3");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setPin("");
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "PIN can not be blank.");
//		Assertion.assertEquals(message, "PIN can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_04_Test_BlankExtnwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA4");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setExtnwcode("");
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "External network code value is blank.");
//		Assertion.assertEquals(message, "External network code value is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_09_Test_BlankTxnid(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA9");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setTxnid("");
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "TXNID can not be blank.");
//		Assertion.assertEquals(message, "TXNID can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_10_Test_BlankCurrentstatus(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA10");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setCurrentstatus("");
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Transaction ID does not exist with status .");
//		Assertion.assertEquals(message, "Transaction ID does not exist with status .");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_11_Test_AlphaNumericMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA11");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn(new RandomGeneration().randomAlphaNumeric(13));
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//
//		Assert.assertEquals(message, "MSISDN is not numeric.");
//		Assertion.assertEquals(message, "MSISDN is not numeric.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//
//	@Test(dataProvider = "userData")
//	public void A_12_Test_InvalidMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA12");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn(new RandomGeneration().randomAlphaNumeric(5));
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//
//		Assert.assertEquals(message, "MSISDN is not numeric.");
//		Assertion.assertEquals(message, "MSISDN is not numeric.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_13_Test_InvalidLengthPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA13");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setPin(new RandomGeneration().randomNumeric(15));
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Invalid PIN length.");
//		Assertion.assertEquals(message, "Invalid PIN length.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	@Test(dataProvider = "userData")
//	public void A_14_Test_InvalidExtnwCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA14");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		String ext=new RandomGeneration().randomAlphabets(4);
//		data.setExtnwcode(ext);
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "External network code "+ext+" is invalid.");
//		Assertion.assertEquals(message, "External network code "+ext+" is invalid.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_17_Test_InvalidLanguage1(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA17");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setLanguage1(new RandomGeneration().randomAlphabets(4));
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "LANGUAGE1 is not valid.");
//		Assertion.assertEquals(message, "LANGUAGE1 is not valid.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_18_Test_BlankPaymentInstrumentType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA18");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//	
//		
//		paymentdetail.setPaymenttype("");
//		paymentDetail.add(paymentdetail);
//		data.setPaymentdetails(paymentDetail);
//
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
//		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_19_Test__BlankPaymentInstrumentDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA19");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//	
//		
//		paymentdetail.setPaymentdate("");
//		paymentDetail.add(paymentdetail);
//		data.setPaymentdetails(paymentDetail);
//
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Payment instrument date is blank.");
//		Assertion.assertEquals(message, "Payment instrument date is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_20_Test__BlankPaymentInstrumentNumber(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CStockApprovalAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSA20");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn,PIN,msisdn2);
//		C2CStockApprovalAPI c2CStockApprovalAPI = new C2CStockApprovalAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CStockApprovalAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//	
//		
//		paymentdetail.setPaymentinstnumber("");
//		paymentDetail.add(paymentdetail);
//		data.setPaymentdetails(paymentDetail);
//
//		c2CStockApprovalRequestPojo.setData(data);
//		c2CStockApprovalAPI.addBodyParam(c2CStockApprovalRequestPojo);
//		c2CStockApprovalAPI.setExpectedStatusCode(400);
//		c2CStockApprovalAPI.perform();
//		c2CStockApprovalResponsePojo = c2CStockApprovalAPI
//				.getAPIResponseAsPOJO(C2CStockApprovalResponsePojo.class);
//		String message =c2CStockApprovalResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
//		Assertion.assertEquals(message, "PAYMENTINSTNUMBER can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
// 
//	
//	
//}
