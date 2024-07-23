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

import restassuredapi.api.dvdapi.DvdApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.dvdapirequestpojo.DvdApiRequestPojo;
import restassuredapi.pojo.dvdapirequestpojo.VoucherDetail;
import restassuredapi.pojo.dvdapiresponsepojo.DvdApiResponsePojo;
import restassuredapi.pojo.dvdapiresponsepojo.MasterErrorList;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo; 
import restassuredapi.pojo.dvdapiresponsepojo.RowErrorMsgLists;

@ModuleManager(name = Module.DVD_API_TEST)
public class DvdApiTest extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
	static String moduleCode;
	DvdApiRequestPojo dvdApiRequestPojo = new DvdApiRequestPojo();
	DvdApiResponsePojo dvdApiResponsePojo = new DvdApiResponsePojo();
	VoucherDetail voucherDetails = null;
	List<VoucherDetail> voucherDetailsList = null;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String, String> vomsData = new HashMap<String, String>();

	Map<String, Object> headerMap = new HashMap<>();

//	public HashMap<String, String> getVomsData() {
//		
//		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE_API);
//		int rowCount = ExcelUtility.getRowCount();
//		int Domcount=0;
//		for(int i=1;i<+rowCount;i++){
//			if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")|| ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D"))
//				Domcount++;
//		}
//		for (int i = 1; i <= Domcount; i++) {
//			String voucherType =  ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i);
//			if("digital".equals(voucherType)|| "test_digit".equals(voucherType)) {
//				vomsData.put("voucherTypeD", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
//				vomsData.put("voucherSegmentD", ExcelUtility.getCellData(0, ExcelI.VOMS_SEGMENT, i));
//				vomsData.put("voucherDenominationD", ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
//				vomsData.put("voucherProfileNameD", ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
//				break;
//			}
//		}
//		String productID1 = DBHandler.AccessHandler.fetchProductID(vomsData.get("voucherProfileNameD"));
//		vomsData.put("voucherProfileId", productID1);
//		return vomsData;
//	}
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String CustomerRechargeCode = _masterVO.getProperty("DVDRecharge");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		/*
		 * Array list to store Categories for which Customer Recharge is allowed
		 */
		ArrayList<String> alist1 = new ArrayList<String>();
		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(CustomerRechargeCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}

		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
		int rCount = ExcelUtility.getRowCount();
		ArrayList<Integer> a = new ArrayList<>();
		for(int i=1;i<=rCount;i++) {
			if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")|| ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")){
				a.add(i);
			}
		}
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int chnlCount = ExcelUtility.getRowCount();
		int userCounter = 0;
		for (int i = 1; i <= chnlCount; i++) {
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
				userCounter++;
			}
		}
		Object[][] Data = new Object[userCounter][11];
		for (int i = 1, j = 0; i <= chnlCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.VOMS_DENOM_PROFILE);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, a.get(i%a.size()));
			Data[j][8] = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, a.get(i%a.size()) );
			String prodName = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, a.get(i%a.size()) );
			Data[j][9] = DBHandler.AccessHandler.fetchProductID(prodName);
			Data[j][10] = DBHandler.AccessHandler.getVoucherSegment(Data[j][9].toString());
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			j++;
		}
		}
		return Data;
	}


	public void setupData(String pin, String voucherTypeD, String voucherDenominationD, String voucherProfileId, String voucherSegment ) {


		voucherDetails = new VoucherDetail();
		voucherDetails.setDenomination(voucherDenominationD);
		voucherDetails.setQuantity(new RandomGeneration().randomNumberWithoutZero(1));
		voucherDetails.setVoucherProfile(voucherProfileId);
		voucherDetails.setVoucherSegment(voucherSegment);
		voucherDetails.setVoucherType(voucherTypeD);
		voucherDetailsList = new ArrayList<VoucherDetail>();
		voucherDetailsList.add(voucherDetails);
		
		dvdApiRequestPojo.setLanguage1(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		dvdApiRequestPojo.setLanguage2(DBHandler.AccessHandler.checkForLangCode(_masterVO.getMasterValue(MasterI.LANGUAGE)));
		String prefix = _masterVO.getMasterValue(MasterI.SUBSCRIBER_PREPAID_PREFIX);
		dvdApiRequestPojo.setMsisdn2(prefix + new RandomGeneration().randomNumeric(gnMsisdn.generateMSISDN()));
		dvdApiRequestPojo.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		dvdApiRequestPojo.setSelector("1");
		dvdApiRequestPojo.setPin(pin);
		dvdApiRequestPojo.setVoucherDetails(voucherDetailsList);

	}
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
		org.testng.Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);


	}


	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7867")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		String statusCode = dvdApiResponsePojo.getStatus();
	   
		Assert.assertEquals(statusCode, "200");
		Assertion.assertEquals(statusCode, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7868")
	public void A_02_Test_invalid_receiverMsisdn(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_02_Test_invalid_receiverMsisdn";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		dvdApiRequestPojo.setMsisdn2(new RandomGeneration().randomAlphaNumeric(10));
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		String message = dvdApiResponsePojo.getMessage();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);
		
		    
		Assert.assertEquals(message, "Invalid MSISDN2 as per network NG.");
		Assertion.assertEquals(message, "Invalid MSISDN2 as per network NG.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7869")
	public void A_03_Test_blank_networkCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_03_Test_blank_networkCode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		dvdApiRequestPojo.setExtnwcode("");
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		String message = dvdApiResponsePojo.getMessage();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);
		
		 
		Assert.assertEquals(message, "External network code value is blank.");
		Assertion.assertEquals(message, "External network code value is blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7870")
	public void A_04_Test_invalid_networkCode(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_04_Test_invalid_networkCode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		String d=new RandomGeneration().randomAlphabets(4);
		dvdApiRequestPojo.setExtnwcode(d);
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		String message = dvdApiResponsePojo.getMessage();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);

		Assert.assertEquals(message, "External network code "+d+" is invalid.");
		Assertion.assertEquals(message, "External network code "+d+" is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7871")
	public void A_05_Test_invalid_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_05_Test_invalid_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		dvdApiRequestPojo.setPin(new RandomGeneration().randomAlphaNumeric(5));
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		String message = dvdApiResponsePojo.getMessage();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);

		Assert.assertEquals(message, "Invalid PIN");
		Assertion.assertEquals(message, "Invalid PIN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7872")
	public void A_06_Test_blank_voucherDetails(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_06_Test_blank_voucherDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		dvdApiRequestPojo.getVoucherDetails().get(0).setVoucherType("");
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);
		
		
		RowErrorMsgLists rowErrorMsgLists = dvdApiResponsePojo.getErrorMap().getRowErrorMsgLists().get(0);
		MasterErrorList masterErrorList = rowErrorMsgLists.getMasterErrorList().get(0);
		String message = masterErrorList.getErrorMsg();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);

		Assert.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.assertEquals(message, "VOUCHERTYPE can not be blank.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-7873")
	public void A_07_Test_invalid_voucherDetails(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_07_Test_invalid_voucherDetails";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVD7");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(PIN,vouchertype,vomsMRP,vomsProfile,vomsSegment);
		dvdApiRequestPojo.getVoucherDetails().get(0).setVoucherSegment(new RandomGeneration().randomAlphaNumeric(6));
		
		DvdApi dvdApi = new DvdApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdApi.setContentType(_masterVO.getProperty("contentType"));
		dvdApi.addBodyParam(dvdApiRequestPojo);
		dvdApi.setExpectedStatusCode(200);
		dvdApi.perform();
		dvdApiResponsePojo = dvdApi
				.getAPIResponseAsPOJO(DvdApiResponsePojo.class);

		RowErrorMsgLists rowErrorMsgLists = dvdApiResponsePojo.getErrorMap().getRowErrorMsgLists().get(0);
		MasterErrorList masterErrorList = rowErrorMsgLists.getMasterErrorList().get(0);
		String message = masterErrorList.getErrorMsg();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnBatchid);

		Assert.assertEquals(message, "Invalid Voucher Segment.");
		Assertion.assertEquals(message, "Invalid Voucher Segment.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
		}
