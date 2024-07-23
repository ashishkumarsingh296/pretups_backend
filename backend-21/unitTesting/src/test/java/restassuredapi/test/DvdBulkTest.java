package restassuredapi.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2sBulkPrepaidRechargeAPI.BulkPrepaidRechargeAPI;
import restassuredapi.api.dvdbulk.DvdBulkApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.rechargebulktemplate.DvdTemplateApi;
import restassuredapi.pojo.dvdbulkapirequestpojo.DvdBulkApiRequestPojo;
import restassuredapi.pojo.dvdbulkapiresponsepojo.DvdBulkApiResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.rechargetemplateresponsepojo.DvdTemplateResponsePojo;
import restassuredapi.pojo.c2SBulkPrepaidRechargeResponsePojo.C2SBulkPrepaidRechargeResponsePojo;
import restassuredapi.pojo.dvdbulkapirequestpojo.Data;

@ModuleManager(name = Module.DVD_BULK)
public class DvdBulkTest extends BaseTest{
	
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	DvdTemplateResponsePojo dvdTemplateResponsePojo = new DvdTemplateResponsePojo();
	DvdBulkApiRequestPojo dvdBulkApiRequestPojo = new DvdBulkApiRequestPojo();
	DvdBulkApiResponsePojo dvdBulkApiResponsePojo = new DvdBulkApiResponsePojo();
	Data data = new Data();
	
	
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
	
	/**
	 * Method setHeaders() is used too setup header data for authentication
	 */
	Map<String, Object> headerMap = new HashMap<String, Object>();
	public void setHeaders() 
	{
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType",_masterVO.getProperty("requestGatewayType") );
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	public void setupAuth(String data1, String data2) 
	{
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}
	
	// Successful data with valid data.
	protected static String accessToken;
	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{

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
    /**
     * This method will load voms data from DataProvider_oracle
     */
//	public Object[][] getVomsData() 
//	{
//
//		ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
//		int rowCount = ExcelUtility.getRowCount();
//		int vomCount=0;
//		for(int i=1;i<=rowCount;i++) {
//			if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")|| ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")){
//				vomCount++;
//			}
//		}
//		Object[][] vomsData = new Object[vomCount][5];
//		int j = 0;
//		for (int i = 1; i <= rowCount; i++) {
//			String voucherType = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i);
//			// String voucherSegment = ExcelUtility.getCellData(0, ExcelI.VOMS_SEGMENT, i);
//			if ("digital".equals(voucherType)) {
//				vomsData[j][0] = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i);
//				vomsData[j][1] = ExcelUtility.getCellData(0, ExcelI.VOMS_SEGMENT, i);
//				vomsData[j][2] = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
//				vomsData[j][3] = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i);
//				j++;
//				break;
//			}
//		}
//
//		for (int i = 1; i <= rowCount; i++) {
//			String voucherType = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i);
//			// String voucherSegment = ExcelUtility.getCellData(0, ExcelI.VOMS_SEGMENT, i);
//			if ("test_digit".equals(voucherType)) {
//				vomsData[j][0] = ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i);
//				vomsData[j][1] = 
//				vomsData[j][2] = ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i);
//				vomsData[j][3] = ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i);
//				j++;
//				break;
//			}
//		}
//		String productID1 = DBHandler.AccessHandler.fetchProductID((String) vomsData[0][4]);
//		vomsData[0][4] = productID1;
//		String productID2 = DBHandler.AccessHandler.fetchProductID((String) vomsData[1][4]);
//		vomsData[1][4] = productID2;
//		return vomsData;
//	}

	/**
	 * This method will download the template for DVD BULK
	 * 
	 */
	public DvdTemplateResponsePojo downloadGiftRechargeTemplate() throws IOException {

		DvdTemplateApi dvdTemplateApi = new DvdTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		dvdTemplateApi.setExpectedStatusCode(200);
		dvdTemplateApi.perform();
		dvdTemplateResponsePojo = dvdTemplateApi.getAPIResponseAsPOJO(DvdTemplateResponsePojo.class);

		return dvdTemplateResponsePojo;

	}
	
	
	public String genExcelData(String vType,String vSegment,String vDenom, String vProfile)
	{
		
		String excelData  = null;
		
		
			String subscriberMsisdn= UniqueChecker.generate_subscriber_MSISDN("Prepaid");
			String voucherType = vType;
			String voucherSegment = vSegment;
			String voucherDenomination = vDenom;
			String voucherProfile = vProfile;
			String noOfVouchers = new RandomGeneration().randomNumberWithoutZero(1);
			
			excelData = subscriberMsisdn + "," + voucherType + "," + voucherSegment + "," 
					+ voucherDenomination + "," + voucherProfile +  "," + noOfVouchers;
		
		Log.info("Creating Data to Write: " +  excelData);
		return excelData;
	}
	
	
   /**
    * This method will create DvdRequestVO
    *     
    */
	public void setupData(String pin,List<String> excelData, String vtype,String vsegment,String vdenom,String vprofile) throws IOException {

//		for(int i=0;i<2;i++) {
//			if(genExcelData(i) != null) {
//				excelData.add(genExcelData(i));
//			}
//		}
		excelData.add(genExcelData(vtype,vsegment,vdenom,vprofile));
		String preference = DBHandler.AccessHandler.getSystemPreference("C2C_BATCH_FILEEXT");
		Boolean changed = false;
		if(preference=="csv" || preference=="xlsx") {
			changed =true;
			DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE","xls","PREFERENCE_CODE",preference);
		}
		
		//if we have change the preference, then change back to its default value
		if(changed) {
			DBHandler.AccessHandler.updateAnyColumnValue("SYSTEM_PREFERENCES","DEFAULT_VALUE",preference,"PREFERENCE_CODE","xls");
		}
		data.setBatchType("normal");
		data.setExtnwcode( _masterVO.getMasterValue(MasterI.NETWORK_CODE));

		String fileName= downloadGiftRechargeTemplate().getFileName().toString();
		String fileType= downloadGiftRechargeTemplate().getFileType().toString();
		String fileAttachment= downloadGiftRechargeTemplate().getFileattachment().toString();
		String dirPath=_masterVO.getProperty("C2SBulkRecharge");
		String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
		
		Log.info("Writing Data into Excel.");
		ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
		for(int i=0;i<excelData.size();i++) {
			String[] row = excelData.get(i).split(",");
			for(int j=0;j<row.length;j++) {
				ExcelUtility.setCellDataXLS(row[j], 3+i,j);
			}	
		}
		String base64file = ExcelUtility.excelToBase64(filepath);
		
		data.setFile(base64file);
		data.setFileName(fileName);
		data.setFileType(fileType);
		
		data.setNoOfDays("");
		data.setOccurence("");
		data.setPin(pin);
		data.setScheduleDate("");
		data.setScheduleNow("");
		dvdBulkApiRequestPojo.setData(data);

	}
	
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String status = dvdBulkApiResponsePojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_02_Test_partial_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_02_Test_partial_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		String vomsData = "72300001" + "," + " "; //setting invalid row
		excelData.add(vomsData);
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		dvdBulkApiRequestPojo.getData().setFileName("test12341234"); //for test_7
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "File with this name is already in this system, please have other name.");
		Assertion.assertEquals(message, "File with this name is already in this system, please have other name.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_03_Test_invalid_file_type(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_03_Test_invalid_file_type";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		data = dvdBulkApiRequestPojo.getData();
		
		if(data.getFileType().equals("xlx")) {
			data.setFileType("xlsx");
		} else {
			data.setFileType("xlx");
		}
		dvdBulkApiRequestPojo.setData(data);
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Invalid file type.");
		Assertion.assertEquals(message, "Invalid file type.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_04_Test_invalid_voucher_count(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_04_Test_invalid_voucher_count";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelDataList = new ArrayList<>();
		//setting voucher
	//	Object[][] vomsData = getVomsData();
		String excelData  = null;
		String subscriberMsisdn= UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String voucherType = vouchertype;
		String voucherSegment = vomsSegment;
		String voucherDenomination = vomsMRP;
		String voucherProfile = (String) vomsProfile;
		String noOfVouchers = "1000";
		
		excelData = subscriberMsisdn + "," + voucherType + "," + voucherSegment + "," 
				+ voucherDenomination + "," + voucherProfile +  "," + noOfVouchers;
		String vomsDataStr = "72300001" + "," + " "; //setting invalid row
		excelDataList.add(vomsDataStr);
		setupData(PIN, excelDataList,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "All records contain error.");
		Assertion.assertEquals(message, "All records contain error.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
	@Test(dataProvider = "userData")
	public void A_05_Test_invalid_network_code(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_05_Test_invalid_network_code";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		String rnf=new RandomGeneration().randomAlphabets(3);
		dvdBulkApiRequestPojo.getData().setExtnwcode(rnf);
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "External network code "+rnf+" is invalid.");
		Assertion.assertEquals(message, "External network code "+rnf+" is invalid.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
	@Test(dataProvider = "userData")
	public void A_06_Test_invalid_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_06_Test_invalid_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		dvdBulkApiRequestPojo.getData().setPin(new RandomGeneration().randomAlphaNumeric(4)); 
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Invalid PIN");
		Assertion.assertEquals(message, "Invalid PIN");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
	@Test(dataProvider = "userData")
	public void A_07_Test_invalid_file_type(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode, String vouchertype,String vomsMRP,String vomsProfile,String vomsSegment) throws Exception {
		final String methodName = "A_03_Test_invalid_file_type";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("DVDBULK7");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		List<String> excelData = new ArrayList<>();
		String vomsData = "72300001" + "," + " "; //setting invalid row
		excelData.add(vomsData);
		setupData(PIN, excelData,vouchertype,vomsSegment,vomsMRP,vomsProfile);
		dvdBulkApiRequestPojo.getData().setFileName("test12341234"); //from test_2
		
		DvdBulkApi dvdBulkApi = new DvdBulkApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		dvdBulkApi.setContentType(_masterVO.getProperty("contentType"));
		dvdBulkApi.addBodyParam(dvdBulkApiRequestPojo);
		dvdBulkApi.setExpectedStatusCode(201);
		dvdBulkApi.perform();
		dvdBulkApiResponsePojo = dvdBulkApi.getAPIResponseAsPOJO(DvdBulkApiResponsePojo.class);
		String message =dvdBulkApiResponsePojo.getMessage();
		
		Assert.assertEquals(message, "File with this name is already in this system, please have other name.");
		Assertion.assertEquals(message, "File with this name is already in this system, please have other name.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}



}
