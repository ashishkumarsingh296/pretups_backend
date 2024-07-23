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
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2sBulkPrepaidRechargeAPI.BulkPrepaidRechargeAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.rechargebulktemplate.CustomerRechargeTemplateApi;
import restassuredapi.pojo.c2SBulkInternetRechargerRequestPojo.Data;
import restassuredapi.pojo.c2SBulkPrepaidRechargeRequestPojo.C2SBulkPrepaidRechargeRequestPojo;
import restassuredapi.pojo.c2SBulkPrepaidRechargeResponsePojo.C2SBulkPrepaidRechargeResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.rechargetemplateresponsepojo.CustomerRechargeTemplateResponsePojo;

@ModuleManager(name = Module.C2S_Bulk_Prepaid_Recharge)
public class C2SBulkPrepaidRecharge extends BaseTest{

	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	Date tomorrow = DateUtils.addDays(new Date(), 3);
	String tomorrowdate = df.format(tomorrow);
	Date prior = DateUtils.addDays(new Date(), -1);
	String PriorDate = df.format(prior);
	String currentDate = df.format(dateobj);
	
	static String moduleCode;
	C2SBulkPrepaidRechargeRequestPojo c2SBulkPrepaidRechargeRequestPojo = new C2SBulkPrepaidRechargeRequestPojo();
	C2SBulkPrepaidRechargeResponsePojo c2sBulkPrepaidRechargeResponsePojo = new C2SBulkPrepaidRechargeResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	CustomerRechargeTemplateResponsePojo customerRechargeTemplateResponsePojo = new CustomerRechargeTemplateResponsePojo();
	RandomGeneration rnd = new RandomGeneration();
	Data data = new Data();
	Login login = new Login();
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

	
	

	public CustomerRechargeTemplateResponsePojo downloadGiftRechargeTemplate() throws IOException{
		
		CustomerRechargeTemplateApi customerRechargeTemplateApi = new CustomerRechargeTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		customerRechargeTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		customerRechargeTemplateApi.setExpectedStatusCode(200);
		customerRechargeTemplateApi.perform();
		
		customerRechargeTemplateResponsePojo = customerRechargeTemplateApi
							.getAPIResponseAsPOJO(CustomerRechargeTemplateResponsePojo.class);

			
		
		return customerRechargeTemplateResponsePojo;
		
	}
	
	public String genExcelData()
	{
		RandomGeneration randomGeneration = new RandomGeneration();
		String msisdn= UniqueChecker.generate_subscriber_MSISDN("Prepaid");
		String subService="1";
		String amount=randomGeneration.randomNumeric(3);
		String languageCode = "0";
		
		String excelData=msisdn + "," + subService + "," + amount + "," + languageCode;
		Log.info("Creating Data to Write.");
		return excelData;
	}



	public void setupData(String data1) throws IOException {
		
		List<String> excelData =new ArrayList<String>();
		for(int i=0;i<3;i++) {
			excelData.add(genExcelData());
		}
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
				ExcelUtility.setCellDataXLS(row[j], 2+i,j);
				
			}	
		}
		String base64file = ExcelUtility.excelToBase64(filepath);
		
		data.setFile(base64file);
		data.setFileName(fileName);
		data.setFileType(fileType);
		
		data.setNoOfDays(new RandomGeneration().randomNumberWithoutZero(1));
		data.setOccurence("Daily");
		data.setPin(data1);
		data.setScheduleDate(currentDate);
		data.setScheduleNow("on");
		c2SBulkPrepaidRechargeRequestPojo.setData(data);

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
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
		currentNode.assignCategory("REST");
		setupData(PIN);
		
		BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
		bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
		bulkPrepaidRechargeAPI.logRequestBody(c2SBulkPrepaidRechargeRequestPojo);
		bulkPrepaidRechargeAPI.setExpectedStatusCode(201);
		bulkPrepaidRechargeAPI.perform();
		c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
				.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
		String status = c2sBulkPrepaidRechargeResponsePojo.getStatus();
		//String status = DBHandler.AccessHandler.getTransactionIDStatus(txnid);
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}


	@Test(dataProvider = "userData")
public void A_02_PriorDate (String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	final String methodName = "A_02_PriorDate";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid2");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
	currentNode.assignCategory("REST");
	setupData(PIN);
	
	BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	data.setScheduleDate(PriorDate);
	data.setScheduleNow("off");
	c2SBulkPrepaidRechargeRequestPojo.setData(data);
	bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
	bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
	bulkPrepaidRechargeAPI.setExpectedStatusCode(200);
	bulkPrepaidRechargeAPI.perform();
	c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
			.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
	String message =c2sBulkPrepaidRechargeResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Please provide current date or date after current date.");
	Assertion.assertEquals(message, "Please provide current date or date after current date.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}


	@Test(dataProvider = "userData")
public void A_03_DateWithScheduleNow (String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	final String methodName = "A_03_DateWithScheduleNow";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid3");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
	currentNode.assignCategory("REST");
	setupData(PIN);
	rnd = new RandomGeneration();
	BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	
	data.setScheduleDate(tomorrowdate);
	data.setScheduleNow("on");
	c2SBulkPrepaidRechargeRequestPojo.setData(data);
	bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
	bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
	bulkPrepaidRechargeAPI.setExpectedStatusCode(200);
	bulkPrepaidRechargeAPI.perform();
	c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
			.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
	String message =c2sBulkPrepaidRechargeResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Please provide current date in case of Schedue now.");
	Assertion.assertEquals(message, "Please provide current date in case of Schedue now.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}


	@Test(dataProvider = "userData")
public void A_04_InvalidPIN (String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	final String methodName = "A_04_InvalidPIN";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid4");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
	currentNode.assignCategory("REST");
	setupData(PIN);
	rnd = new RandomGeneration();
	BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	data.setPin(rnd.randomNumberWithoutZero(4));
	c2SBulkPrepaidRechargeRequestPojo.setData(data);
	bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
	bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
	bulkPrepaidRechargeAPI.setExpectedStatusCode(200);
	bulkPrepaidRechargeAPI.perform();
	c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
			.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
	String message =c2sBulkPrepaidRechargeResponsePojo.getMessage();
	
	Assert.assertEquals(message, "The PIN you have entered is incorrect.");
	Assertion.assertEquals(message, "The PIN you have entered is incorrect.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}
	@Test(dataProvider = "userData")
public void A_05_BlankScheduleDate (String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	final String methodName = "A_05_BlankScheduleDate";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid5");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
	currentNode.assignCategory("REST");
	setupData(PIN);
	rnd = new RandomGeneration();
	BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	data.setScheduleDate("");
	data.setScheduleNow("off");
	c2SBulkPrepaidRechargeRequestPojo.setData(data);
	bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
	bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
	bulkPrepaidRechargeAPI.setExpectedStatusCode(200);
	bulkPrepaidRechargeAPI.perform();
	c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
			.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
	String message =c2sBulkPrepaidRechargeResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Your request cannot be processed at this time, please try again later.");
	Assertion.assertEquals(message, "Your request cannot be processed at this time, please try again later.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}



	@Test(dataProvider = "userData")
public void A_06_BlankPIN (String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	final String methodName = "A_06_BlankPIN";
	Log.startTestCase(methodName);

	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SBulkPrepaid6");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
	currentNode.assignCategory("REST");
	setupData(PIN);
	rnd = new RandomGeneration();
	BulkPrepaidRechargeAPI bulkPrepaidRechargeAPI = new BulkPrepaidRechargeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	data.setPin("");
	c2SBulkPrepaidRechargeRequestPojo.setData(data);
	bulkPrepaidRechargeAPI.setContentType(_masterVO.getProperty("contentType"));
	bulkPrepaidRechargeAPI.addBodyParam(c2SBulkPrepaidRechargeRequestPojo);
	bulkPrepaidRechargeAPI.setExpectedStatusCode(200);
	bulkPrepaidRechargeAPI.perform();
	c2sBulkPrepaidRechargeResponsePojo = bulkPrepaidRechargeAPI
			.getAPIResponseAsPOJO(C2SBulkPrepaidRechargeResponsePojo.class);
	String message =c2sBulkPrepaidRechargeResponsePojo.getMessage();
	
	Assert.assertEquals(message, "Blank Pin Request,Please enter a Pin.");
	Assertion.assertEquals(message, "Blank Pin Request,Please enter a Pin.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);
}



}
