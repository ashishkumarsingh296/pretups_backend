package restassuredapi.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UniqueChecker;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.bulkgiftrecharge.BulkGiftRechargeApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.rechargebulktemplate.GiftRechargeTemplateApi;
import restassuredapi.pojo.bulkgiftrechargerequestpojo.C2CBulkGiftRechargeRequestPojo;
import restassuredapi.pojo.bulkgiftrechargerequestpojo.Data;
import restassuredapi.pojo.bulkgiftrechargeresponsepojo.C2CBulkGiftRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.rechargetemplateresponsepojo.GiftRechargeTemplateResponsePojo;

@ModuleManager(name = Module.BULK_RECHARGE_GIFT)
public class BulkGiftRecharge extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);   
     
	static String moduleCode;
	C2CBulkGiftRechargeRequestPojo c2CBulkGiftRechargeRequestPojo = new C2CBulkGiftRechargeRequestPojo();
	C2CBulkGiftRechargeResponsePojo c2CBulkGiftRechargeResponsePojo = new C2CBulkGiftRechargeResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	GiftRechargeTemplateResponsePojo giftRechargeTemplateResponsePojo = new GiftRechargeTemplateResponsePojo();
	

	Data data = new Data();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][7];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);

			j++;
		}
		return Data;
	}

	public Map<String,String> downloadGiftRechargeTemplate() {
		
		Map<String, String> downloadGRC =new HashMap<String,String>();
		
		GiftRechargeTemplateApi giftRechargeTemplateApi = new GiftRechargeTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		giftRechargeTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		giftRechargeTemplateApi.setExpectedStatusCode(200);
		giftRechargeTemplateApi.perform();

		
			try {
				giftRechargeTemplateResponsePojo = giftRechargeTemplateApi
						.getAPIResponseAsPOJO(GiftRechargeTemplateResponsePojo.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		downloadGRC.put("fileName",giftRechargeTemplateResponsePojo.getFileName().toString());
		downloadGRC.put("fileType",giftRechargeTemplateResponsePojo.getFileType().toString());
		downloadGRC.put("fileAttachment",giftRechargeTemplateResponsePojo.getFileattachment().toString());
		
		return downloadGRC;
		
	}
	
	
	public String genExcelData()
	{
		RandomGeneration randomGeneration = new RandomGeneration();
		String msisdn= UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String subService="1";
		String amount=randomGeneration.randomNumeric(4);
		String recieverLangCode="0";
		String gifterMsisdn= UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String gifterName=randomGeneration.randomAlphabets(8);
		String gifterLangCode="1";
		
		String excelData=msisdn + "," + subService + "," + amount + "," + recieverLangCode + "," + gifterMsisdn + "," + gifterName + "," + gifterLangCode;
		
		return excelData;
	}
	public void setupData(String data1) {
		
		
		//add random data to excel file
		List<String> excelData =new ArrayList<String>();
		for(int i=0;i<3;i++) {
			excelData.add(genExcelData());
		}
		
		data.setBatchType("normal");
		data.setExtnwcode( _masterVO.getMasterValue(MasterI.NETWORK_CODE));
		
		//get preference from DB
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
		
		//download the gift recharge template 
		Map<String,String> downloadedGRCtemp= downloadGiftRechargeTemplate();
		String fileName= downloadedGRCtemp.get("fileName");
		String fileType= downloadedGRCtemp.get("fileType");
		String fileAttachment= downloadedGRCtemp.get("fileAttachment");	
		
		//convert the base64 to excel
		String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,_masterVO.getProperty("C2SBulkGiftRecharge"));
		
		//add the data to excel file
		ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
		for(int i=0;i<excelData.size();i++) {
			String[] row = excelData.get(i).split(",");
			for(int j=0;j<row.length;j++) {
				ExcelUtility.setCellDataXLS(row[j], 2+i,j);
			}	
		}
		
		//convert back excel to base64
		String base64file = ExcelUtility.excelToBase64(filepath);
		data.setFile(base64file);
		data.setFileName(fileName);
		data.setFileType(fileType);
		
		RandomGeneration randomGeneration = new RandomGeneration();
		String noOfDays= randomGeneration.randomNumberWithoutZero(1);
		data.setNoOfDays(noOfDays);
		
		data.setOccurence("Daily");
		data.setPin(data1);
		data.setScheduleDate(currentDate);
		data.setScheduleNow("on");
		
		c2CBulkGiftRechargeRequestPojo.setData(data);
	
		
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
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(201);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		

		String status = c2CBulkGiftRechargeResponsePojo.getStatus();
		Assert.assertEquals(status, "200");
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	@Test(dataProvider = "userData")
	public void A_02_Test_blank_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_02_Test_blank_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		data.setPin("");
		c2CBulkGiftRechargeRequestPojo.setData(data);
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(201);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		
		String message= c2CBulkGiftRechargeResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Blank Pin Request,Please enter a Pin.");
		Assertion.assertEquals(message, "Blank Pin Request,Please enter a Pin.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "userData")
	public void A_03_Test_blank_schedule_date(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_03_Test_blank_schedule_date";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		data.setScheduleDate("");
		c2CBulkGiftRechargeRequestPojo.setData(data);
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(201);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		
		String message= c2CBulkGiftRechargeResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Your request cannot be processed at this time, please try again later.");
		Assertion.assertEquals(message, "Your request cannot be processed at this time, please try again later.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_04_Test_invalid_pin(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_04_Test_invalid_pin";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		
		RandomGeneration randomGeneration = new RandomGeneration();
		String pin= randomGeneration.randomNumeric(4);
		data.setPin(pin);
		c2CBulkGiftRechargeRequestPojo.setData(data);
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(201);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		
		String message= c2CBulkGiftRechargeResponsePojo.getMessage();
		
		Assert.assertEquals(message, "The PIN you have entered is incorrect.");
		Assertion.assertEquals(message, "The PIN you have entered is incorrect.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	public void A_05_Test_next_date_with_schedule_now_on(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_next_date_with_schedule_now";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		
		Date tomorrow = DateUtils.addDays(new Date(), 1);
		String tomorrowdate = df.format(tomorrow);
		data.setScheduleNow("on");
		data.setScheduleDate(tomorrowdate);
		c2CBulkGiftRechargeRequestPojo.setData(data);
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(201);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		
		String message= c2CBulkGiftRechargeResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Please provide current date in case of Schedue now.");
		Assertion.assertEquals(message, "Please provide current date in case of Schedue now.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	public void A_06_Test_previous_date(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_06_Test_invalid_previous_date";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkGiftRecharge2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		BulkGiftRechargeApi bulkGiftRechargeApi = new BulkGiftRechargeApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkGiftRechargeApi.setContentType(_masterVO.getProperty("contentType"));
		
		Date yesterday = DateUtils.addDays(new Date(),-1);
		String yesterdaydate = df.format(yesterday);
		data.setScheduleNow("off");
		data.setScheduleDate(yesterdaydate);
		c2CBulkGiftRechargeRequestPojo.setData(data);
		bulkGiftRechargeApi.addBodyParam(c2CBulkGiftRechargeRequestPojo);
		bulkGiftRechargeApi.setExpectedStatusCode(200);
		bulkGiftRechargeApi.perform();

		c2CBulkGiftRechargeResponsePojo = bulkGiftRechargeApi
				.getAPIResponseAsPOJO(C2CBulkGiftRechargeResponsePojo.class);
		
		String message= c2CBulkGiftRechargeResponsePojo.getMessage();
		
		Assert.assertEquals(message, "Please provide current date or date after current date.");
		Assertion.assertEquals(message, "Please provide current date or date after current date.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
}
