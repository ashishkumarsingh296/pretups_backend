//package restassuredapi.test;
//
//import java.text.DateFormat;
//import java.text.MessageFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.testng.Assert;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.classes.BaseTest;
//import com.classes.CaseMaster;
//import com.commons.ExcelI;
//import com.commons.MasterI;
//import com.reporting.extent.entity.ModuleManager;
//import com.utils.Assertion;
//import com.utils.ExcelUtility;
//import com.utils.Log;
//import com.utils.RandomGeneration;
//import com.utils._masterVO;
//import com.utils.constants.Module;
//
//import restassuredapi.api.c2cvouchertransfer.C2CVoucherTransferAPI;
//import restassuredapi.api.oauthentication.OAuthenticationAPI;
//import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.C2CVoucherInitiateRequestPojo;
//import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.Data;
//import restassuredapi.pojo.c2cvoucherinitiaterequestpojo.VoucherDetail;
//import restassuredapi.pojo.c2cvoucherinitiateresponsepojo.C2CVoucherInitiateResponsePojo;
//import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
//import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
//
//@ModuleManager(name = Module.REST_C2C_VOUCHER_INITIATE)
//public class C2CVoucherTransferInitiate extends BaseTest {
//	DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
//    Date dateobj = new Date(); 
//    String currentDate=df.format(dateobj);
//    
//    RandomGeneration randomGeneration = new RandomGeneration();
//	
//	static String moduleCode;
//	C2CVoucherInitiateRequestPojo c2CVoucherInitiateRequestPojo = new C2CVoucherInitiateRequestPojo();
//	C2CVoucherInitiateResponsePojo c2CVoucherInitiateResponsePojo = new C2CVoucherInitiateResponsePojo();
//	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
//	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
//
//	Data data = new Data();
//	VoucherDetail voucherDetail = new VoucherDetail();
//
//
//	@DataProvider(name ="userData")
//	public Object[][] TestDataFeed() {
//		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
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
//	
//	public void setupData(String data1,String data2,String data3) {
//		ArrayList<VoucherDetail> voucherdetails = new ArrayList<VoucherDetail>();
//		Map<String, String> tempData1 = new HashMap<String, String>();
//		tempData1.put("fromSerialNo", "9901130000000015");
//		tempData1.put("toSerialNo", "9901130000000015");
//		tempData1.put("denomination", "110.0");
//		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
//		data.setMsisdn(data1);
//		data.setPin(data2);
//		data.setLoginid("");
//		data.setPassword("");
//		data.setExtcode("");
//		data.setExtrefnum("");
//		data.setMsisdn2(data3);
//		data.setLoginid2("");
//		data.setExtcode2("");
//		data.setLanguage1(_masterVO.getProperty("languageCode0"));
//		data.setLanguage2(_masterVO.getProperty("languageCode0"));
//		data.setVouchertype(_masterVO.getProperty("enquiryVoucherType"));
//		data.setVouchersegment(_masterVO.getProperty("enquiryVoucherSegment"));
//		data.setPaymentinstnum(randomGeneration.randomNumeric(5));
//        data.setPaymentinstdate(currentDate);
//        data.setPaymentinstcode(_masterVO.getProperty("paymentInstrumentCode"));
//        
//        voucherDetail.setDenomination(tempData1.get("denomination"));
//        voucherDetail.setFromSerialNo(tempData1.get("fromSerialNo"));
//        voucherDetail.setToSerialNo(tempData1.get("toSerialNo"));
//        voucherdetails.add(voucherDetail);
//        data.setVoucherDetails(voucherdetails);
//        data.setRemarks(_masterVO.getProperty("Remarks"));
//	
//		c2CVoucherInitiateRequestPojo.setData(data);
//
//	}
//
//	Map<String, Object> headerMap = new HashMap<String, Object>();
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
//
//	// Successful data with valid data.
//
//	protected static String accessToken;
//
//
//	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
//	{
//		//if(accessToken==null) {
//		final String methodName = "Test_OAuthenticationTest";
//		Log.startTestCase(methodName);
//
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
//		moduleCode = CaseMaster.getModuleCode();
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//
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
//		Assert.assertEquals(statusCode, 200);
//		Assertion.assertEquals(Long.toString(statusCode), "200");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//
//
//	}
//
//
//	// Successful data with valid data.
//	@Test(dataProvider = "userData")
//	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI1");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherTransferAPI c2CVoucherInitiateAPI = new C2CVoucherTransferAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		Assert.assertEquals(200, statusCode);
//		Assertion.assertEquals(Integer.toString(statusCode), "200");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//
//	@Test(dataProvider = "userData")
//	public void A_02_Test_BlankMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI2");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setMsisdn("");
//		c2CVoucherInitiateRequestPojo.setData(data);
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.assertEquals(message, "Incorrect sender credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID with PASSWORD.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		
//		
//		
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_03_Test_BlankPin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI3");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		data.setPin("");
//		c2CVoucherInitiateRequestPojo.setData(data);
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "PIN can not be blank.");
//		Assertion.assertEquals(message, "PIN can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		
//	}
//	@Test(dataProvider = "userData")
//	public void A_04_Test_BlankExtnwcode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI4");
//		moduleCode = CaseMaster.getModuleCode();
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		data.setExtnwcode("");
//		c2CVoucherInitiateRequestPojo.setData(data);
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "External network code value is blank.");
//		Assertion.assertEquals(message, "External network code value is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_09_Test_InvalidLanguage1(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI9");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setLanguage1("5");
//		c2CVoucherInitiateRequestPojo.setData(data);
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "LANGUAGE1 is not valid.");
//		Assertion.assertEquals(message, "LANGUAGE1 is not valid.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_10_Test_BlankVoucherType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI10");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setVouchertype("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "VOUCHERTYPE can not be blank.");
//		Assertion.assertEquals(message, "VOUCHERTYPE can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//	
//	
//	@Test(dataProvider = "userData")
//	public void A_11_Test_BlankVoucherSegment(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI11");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setVouchersegment("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
//		Assertion.assertEquals(message, "VOUCHERSEGMENT can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//
//	
//	@Test(dataProvider = "userData")
//	public void A_12_Test_BlankMsisdn2(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI12");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setMsisdn2("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Incorrect reciever credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID.");
//		Assertion.assertEquals(message, "Incorrect reciever credential, Please provide atleast one from the EMPCODE, MSISDN and LOGINID.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//
//	@Test(dataProvider = "userData")
//	public void A_13_Test_BlankRemark(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI13");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setRemarks("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "REMARK can not be blank.");
//		Assertion.assertEquals(message, "REMARK can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//	
//	@Test(dataProvider = "userData")
//	public void A_14_Test_BlankPaymentInstrumentCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI14");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setPaymentinstcode("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "PAYMENTTYPE can not be blank.");
//		Assertion.assertEquals(message, "PAYMENTTYPE can not be blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//
//	@Test(dataProvider = "userData")
//	public void A_15_Test_BlankPaymentInstrumentDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String extCode,String msisdn2) throws Exception {
//		final String methodName = "Test_C2CVoucherInitiateAPI";
//		Log.startTestCase(methodName);
//		if(_masterVO.getProperty("identifierType").equals("loginid"))
//			BeforeMethod(loginID, password,categoryName);
//		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
//			BeforeMethod(msisdn, PIN,categoryName);
//		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTC2CVTI15");
//		moduleCode = CaseMaster.getModuleCode();
//
//		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
//		currentNode.assignCategory("REST");
//		setupData(msisdn2,PIN,msisdn);
//		C2CVoucherInitiateAPI c2CVoucherInitiateAPI = new C2CVoucherInitiateAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
//		c2CVoucherInitiateAPI.setContentType(_masterVO.getProperty("contentType"));
//		
//		
//		data.setPaymentinstdate("");
//		
//		c2CVoucherInitiateAPI.addBodyParam(c2CVoucherInitiateRequestPojo);
//		
//		c2CVoucherInitiateAPI.setExpectedStatusCode(200);
//		c2CVoucherInitiateAPI.perform();
//		c2CVoucherInitiateResponsePojo = c2CVoucherInitiateAPI
//				.getAPIResponseAsPOJO(C2CVoucherInitiateResponsePojo.class);
//		
//		int statusCode = Integer.parseInt(c2CVoucherInitiateResponsePojo.getDataObject().getTxnstatus());
//		
//		String message =c2CVoucherInitiateResponsePojo.getDataObject().getMessage();
//		Assert.assertEquals(message, "Payment instrument date is blank.");
//		Assertion.assertEquals(message, "Payment instrument date is blank.");
//		Assertion.completeAssertions();
//		Log.endTestCase(methodName);
//		Assert.assertEquals(206, statusCode);
//		Log.endTestCase(methodName);
//	}
//
//}
