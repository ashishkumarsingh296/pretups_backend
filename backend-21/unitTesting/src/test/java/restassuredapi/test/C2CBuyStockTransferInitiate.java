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
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cstock.C2CBuyStockTransferInitiateAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.C2CBuyStockInitiateRequestPojo;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Data;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Paymentdetail;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.c2cbuystockinitiateresponsepojo.C2CBuyStockInitiateResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.REST_C2C_STOCK_INITIATE)
public class C2CBuyStockTransferInitiate extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     ArrayList<Paymentdetail> paymentDetails;
     Paymentdetail paymentdetail;
    
	static String moduleCode;
	C2CBuyStockInitiateRequestPojo c2CBuyStockInitiateRequestPojo = new C2CBuyStockInitiateRequestPojo();
	C2CBuyStockInitiateResponsePojo c2CBuyStockInitiateResponsePojo = new C2CBuyStockInitiateResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Data data = new Data();
	Login login = new Login();
	NetworkAdminHomePage homepage = new NetworkAdminHomePage(driver);
	Product product= null;


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
		ArrayList<Product> products = new ArrayList<Product>();
		paymentDetails=new ArrayList<Paymentdetail>();
		paymentdetail= new Paymentdetail();

		RandomGeneration randomGeneration = new RandomGeneration();
		/*login.UserLogin(driver, "Operator", "Super Admin");
		String date = homepage.getDate();*/

		data.setDate("");
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setMsisdn(data1);
		data.setPin(data2);
		data.setLoginid("");
		data.setPassword("");
		data.setExtcode("");
		data.setMsisdn2(data3);
		data.setLoginid2("");
		data.setExtcode2("");
		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		for (int i = 1; i <= rowCount; i++) {
		product = new Product();
		String productShortCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_SHORT_CODE, i);
		String productCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
		product.setProductcode(productShortCode);
	//	String productName = DBHandler.AccessHandler.getProductNameByCode(productCode);
		String userBalance = DBHandler.AccessHandler.getUserBalance(productCode, data3);
		int prBalance= (int) Double.parseDouble(userBalance);
		int quantity=(int) (prBalance*0.01*0.01);
		product.setQty(String.valueOf(quantity));
		products.add(product);
		}
		data.setProducts(products);
		data.setRefnumber("");
			
		paymentdetail.setPaymentinstnumber(randomGeneration.randomNumeric(5));
		paymentdetail.setPaymentdate(currentDate);
		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentCode"));
		
		// array list of paymentdetails
		paymentDetails.add(paymentdetail);	
		data.setLanguage1(_masterVO.getProperty("languageCode0"));
	
		//sending array list
		data.setPaymentdetails(paymentDetails);
		data.setRemarks("Automation REST API");
		c2CBuyStockInitiateRequestPojo.setData(data);

	}
	Map<String, Object> headerMap = new HashMap<String, Object>();
	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", "1357");
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
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test(dataProvider = "userData")
	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setMsisdn("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(206, statusCode);
		String msg = c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Assertion.assertEquals(msg, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI3");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		data.setPin("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(206, statusCode);
		String msg = c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(msg, "PIN can not be blank.");
		Assertion.assertEquals(msg, "PIN can not be blank.");
		
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_04_Test_BlankRefNumber(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		//data.setPin("2468");
		data.setRefnumber("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	@Test(dataProvider = "userData")
	public void A_08_Test_BlankExtnwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI8");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setExtnwcode("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void A_09_Test_NumericPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI9");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setPin("ioi9");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PIN is not numeric.");
		Assertion.assertEquals(message, "PIN is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	public void A_10_Test_InvalidLanguage(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI10");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setLanguage1("adf");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.assertEquals(message, "LANGUAGE1 is not numeric.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_11_Test_InvalidMsisdnLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI11");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setMsisdn("723000000058678689797");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.assertEquals(message, "MSISDN length should lie between 6 and 15.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	@Test(dataProvider = "userData")
	public void A_12_Test_BlankInstrumentType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI12");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		paymentdetail.setPaymenttype("");
		paymentDetails.add(paymentdetail);
		
		data.setPaymentdetails(paymentDetails);
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	
	
	@Test(dataProvider = "userData")
	public void A_13_Test_PaymentInstrumentTypeCash(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI13");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		paymentdetail.setPaymenttype(_masterVO.getProperty("paymentInstrumentTypeCash"));
		paymentdetail.setPaymentinstnumber("");
		paymentDetails.add(paymentdetail);
		
		data.setPaymentdetails(paymentDetails);
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "userData")
	public void A_14_Test_InvalidPinLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI14");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setPin("1234567890123456");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(400);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		String message =c2CBuyStockInitiateResponsePojo.getDataObject().getMessage();
		Assert.assertEquals(message, "Invalid PIN length.");
		Assertion.assertEquals(message, "Invalid PIN length.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_15_Test_BlankLanguage(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
		final String methodName = "Test_C2CStockInitiateAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CSI15");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(msisdn,PIN,msisdn2);
		C2CBuyStockTransferInitiateAPI c2CBuyStockTransferInitiateAPI = new C2CBuyStockTransferInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		c2CBuyStockTransferInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
		
		data.setLanguage1("");
		c2CBuyStockInitiateRequestPojo.setData(data);
		
		c2CBuyStockTransferInitiateAPI.addBodyParam(c2CBuyStockInitiateRequestPojo);
		c2CBuyStockTransferInitiateAPI.setExpectedStatusCode(200);
		c2CBuyStockTransferInitiateAPI.perform();
		c2CBuyStockInitiateResponsePojo = c2CBuyStockTransferInitiateAPI
				.getAPIResponseAsPOJO(C2CBuyStockInitiateResponsePojo.class);
		
		int statusCode = Integer.parseInt(c2CBuyStockInitiateResponsePojo.getDataObject().getTxnstatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
}
