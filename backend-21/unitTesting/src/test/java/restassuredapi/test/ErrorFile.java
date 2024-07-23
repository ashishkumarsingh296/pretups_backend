package restassuredapi.test;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cstock.C2CBuyStockTransferInitiateAPI;
import restassuredapi.api.c2cstock.C2CStockApprovalAPI;
import restassuredapi.api.c2cstockreturn.C2CStockReturnAPI;
import restassuredapi.api.errorfile.ErrorFileAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.C2CBuyStockInitiateRequestPojo;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Data;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Paymentdetail;
import restassuredapi.pojo.c2cbuystockinitiaterequestpojo.Product;
import restassuredapi.pojo.c2cbuystockinitiateresponsepojo.C2CBuyStockInitiateResponsePojo;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.C2CStockApprovalRequestPojo;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.DataApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.PaymentdetailApproval;
import restassuredapi.pojo.c2cstockapprovalrequestpojo.ProductApproval;
import restassuredapi.pojo.c2cstockapprovalresponsepojo.C2CStockApprovalResponsePojo;
import restassuredapi.pojo.c2cstockreturnrequestpojo.C2CStockReturnRequestPojo;
import restassuredapi.pojo.c2cstockreturnrequestpojo.DataR;
import restassuredapi.pojo.c2cstockreturnrequestpojo.ProductR;
import restassuredapi.pojo.c2cstockreturnresponsepojo.C2CStockReturnResponsePojo;
import restassuredapi.pojo.errorfilerequestpojo.ErrorFileRequestPojo;
import restassuredapi.pojo.errorfilerequestpojo.MasterErrorList;
import restassuredapi.pojo.errorfilerequestpojo.RowErrorMsgList;
import restassuredapi.pojo.errorfileresponsepojo.ErrorFileResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.REST_C2C_STOCK_RETURN)
public class ErrorFile extends BaseTest {
	
	 DateFormat df = new SimpleDateFormat("dd/MM/YY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
    
	static String moduleCode;
	ErrorFileRequestPojo errorFileRequestPojo = new ErrorFileRequestPojo();
	ErrorFileResponsePojo errorFileResponsePojo = new ErrorFileResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Login login = new Login();
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		Object[][] Data = new Object[rowCount][8];
		int j=0;
		for(int i=1;i<=rowCount;i++) {
			Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
			Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
			Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
			Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
			Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
			Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
			Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
			Data[j][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, i);
			j++;
		}
		return Data;
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

	
	public void setupData() throws IOException {
		errorFileRequestPojo= new ErrorFileRequestPojo();;
		
		File error =new File(_masterVO.getProperty("errorFilePath"));
		byte[] fileContent = FileUtils.readFileToByteArray(error);
   		String encodedString = Base64.getEncoder().encodeToString(fileContent);
   		errorFileRequestPojo.setFile(encodedString);
   		
   		errorFileRequestPojo.setFiletype(_masterVO.getProperty("xlsxFile"));
   		errorFileRequestPojo.setPartialError(false);
   		
   		List<RowErrorMsgList> errorList = new ArrayList<RowErrorMsgList>();
   		for(int i=0;i<3;i++) {
   			RowErrorMsgList tempRErrorList = new RowErrorMsgList();
   			String rowValue="";
   			if(i==1)
   				rowValue="Line 20";
   			else
   				rowValue="Line 18";
   			
   			List<MasterErrorList> merrorList = new ArrayList<MasterErrorList>();
   			MasterErrorList temp = new MasterErrorList();
   			temp.setErrorCode(new RandomGeneration().randomAlphabets(5));
   			temp.setErrorMsg(new RandomGeneration().randomAlphabets(5));
   		 
   			merrorList.add(temp);
   			
   			tempRErrorList.setRowValue(rowValue);
   			tempRErrorList.setRowName(new RandomGeneration().randomAlphabets(5));
   			tempRErrorList.setMasterErrorList(merrorList);
   			tempRErrorList.setRowErrorMsgList(null);
   			
   			errorList.add(tempRErrorList);
   		}
   		
   		errorFileRequestPojo.setRowErrorMsgLists(errorList);
   		
	}

	// Successful data with valid data.
	@Test(dataProvider = "userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTEF1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		
		ErrorFileAPI errorFileAPI = new ErrorFileAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		errorFileAPI.setContentType(_masterVO.getProperty("contentType"));
		errorFileAPI.addBodyParam(errorFileRequestPojo);
		errorFileAPI.setExpectedStatusCode(200);
		errorFileAPI.perform();
		errorFileResponsePojo = errorFileAPI
				.getAPIResponseAsPOJO(ErrorFileResponsePojo.class);
		
		String statuscode = errorFileResponsePojo.getStatus();
		
		Assert.assertEquals(statuscode, "200");
		Assertion.assertEquals("200", statuscode);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	public void A_02_Invalid_FileType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTEF2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		errorFileRequestPojo.setFiletype(new RandomGeneration().randomAlphabets(3));
		ErrorFileAPI errorFileAPI = new ErrorFileAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		errorFileAPI.setContentType(_masterVO.getProperty("contentType"));
		errorFileAPI.addBodyParam(errorFileRequestPojo);
		errorFileAPI.setExpectedStatusCode(400);
		errorFileAPI.perform();
		errorFileResponsePojo = errorFileAPI
				.getAPIResponseAsPOJO(ErrorFileResponsePojo.class);
		
		String msg = errorFileResponsePojo.getMessage();
		
		Assert.assertEquals(msg, "Invalid file type.");
		Assertion.assertEquals(msg, "Invalid file type.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	
	
	@Test(dataProvider = "userData")
	public void A_03_blank_FileType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_03_blank_FileType";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTEF3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		errorFileRequestPojo.setFiletype("");
		ErrorFileAPI errorFileAPI = new ErrorFileAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		errorFileAPI.setContentType(_masterVO.getProperty("contentType"));
		errorFileAPI.addBodyParam(errorFileRequestPojo);
		errorFileAPI.setExpectedStatusCode(400);
		errorFileAPI.perform();
		errorFileResponsePojo = errorFileAPI
				.getAPIResponseAsPOJO(ErrorFileResponsePojo.class);
		
		String msg = errorFileResponsePojo.getMessage();
		
		Assert.assertEquals(msg, "Invalid file type.");
		Assertion.assertEquals(msg, "Invalid file type.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	public void A_04_blank_rowErrorList(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode) throws Exception {
		final String methodName = "A_04_blank_rowErrorList";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTEF4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		errorFileRequestPojo.setRowErrorMsgLists(new ArrayList<>());
		ErrorFileAPI errorFileAPI = new ErrorFileAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		errorFileAPI.setContentType(_masterVO.getProperty("contentType"));
		errorFileAPI.addBodyParam(errorFileRequestPojo);
		errorFileAPI.setExpectedStatusCode(400);
		errorFileAPI.perform();
		errorFileResponsePojo = errorFileAPI
				.getAPIResponseAsPOJO(ErrorFileResponsePojo.class);
		
		String msg = errorFileResponsePojo.getMessage();
		
		Assert.assertEquals(msg, "Row Error List is empty.");
		Assertion.assertEquals(msg, "Row Error List is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
}
