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

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.passbookDownload.PassbookDownloadApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.passbookdownloadrequestpojo.Data;
import restassuredapi.pojo.passbookdownloadrequestpojo.DispHeaderColumn;
import restassuredapi.pojo.passbookdownloadrequestpojo.PassbookDownloadRequestPojo;
import restassuredapi.pojo.passbookdownloadresponsepojo.PassbookDownloadResponsePojo;

@ModuleManager(name = Module.PASSBOOK_DOWNLOAD)
public class PassbookDownload extends BaseTest {
	 DateFormat df = new SimpleDateFormat("dd/MM/YYYY");
     Date dateobj = new Date();
     String currentDate=df.format(dateobj);
     
 //   getSystemPreferenceDefaultValue
     
      String fromDate = df.format(DateUtils.addDays(new Date(), -25));
      String toDate = df.format(DateUtils.addDays(new Date(), -1));
     
     String productCode;
     
	static String moduleCode;
	PassbookDownloadRequestPojo passbookDownloadRequestPojo = new PassbookDownloadRequestPojo();
	PassbookDownloadResponsePojo passbookDownloadResponsePojo = new PassbookDownloadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

	Data data = new Data();
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed(){
		
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int productRowCount = ExcelUtility.getRowCount();
        Object[][] product = new Object[productRowCount+2][1];
        int i =0;
        for (i = 1; i <= productRowCount; i++) {
        	product[i][0] =   ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
        }
        product[i][0] = "ALL";
		
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();		
	
	    int k=i;
		Object[][] Data = new Object[rowCount*(k)][9];
		Object[][] tempData = new Object[1][9];
		int j=0;
		int t=0;
		for( j=1;j<=rowCount;j++) {
		  //Product combination
			for(int m=1; m<=productRowCount+1;m++) {
				
			   	
				tempData[0][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,j);
				tempData[0][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, j);
				tempData[0][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, j);
				tempData[0][3] = ExcelUtility.getCellData(0, ExcelI.PIN, j);
				tempData[0][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, j);
				tempData[0][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, j);
				tempData[0][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, j);
				tempData[0][7] = ExcelUtility.getCellData(0, ExcelI.EXTERNAL_CODE, j);
				if(m==3) {
					tempData[0][8]="ALL";	
				} else {
					tempData[0][8] =product[m][0];
				}
				 Data[t] = Arrays.copyOf(tempData[0], tempData[0].length);
				 				 
				 t=t+1;
				
			}
			
		}
		
		StringBuilder sb = new StringBuilder();
		
	for (int d=0;d<Data.length;d++) {
		sb.setLength(0);
		for(int f=0;f<=8;f++) {
			sb.append(Data[d][f]).append("  ");
		}
		//System.out.println(sb.toString());
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

	public void setupData() {
		
		DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
		dispHeaderColumn.setColumnName("transDate");
		dispHeaderColumn.setDisplayName("TransactionDate");
		
		DispHeaderColumn dispHeaderColumn1 = new DispHeaderColumn();
		dispHeaderColumn1.setColumnName("productName");
		dispHeaderColumn1.setDisplayName("Product");
		
		DispHeaderColumn dispHeaderColumn2 = new DispHeaderColumn();
		dispHeaderColumn2.setColumnName("openingBalance");
		dispHeaderColumn2.setDisplayName("Opening Balance");
		
		DispHeaderColumn dispHeaderColumn3 = new DispHeaderColumn();
		dispHeaderColumn3.setColumnName("stockPurchase");
		dispHeaderColumn3.setDisplayName("Stock Purchase");
		
		DispHeaderColumn dispHeaderColumn4 = new DispHeaderColumn();
		dispHeaderColumn4.setColumnName("channelSales");
		dispHeaderColumn4.setDisplayName("Channel Sales");
		
		DispHeaderColumn dispHeaderColumn5 = new DispHeaderColumn();
		dispHeaderColumn5.setColumnName("customerSales");
		dispHeaderColumn5.setDisplayName("Customer Sales");
		
		DispHeaderColumn dispHeaderColumn6 = new DispHeaderColumn();
		dispHeaderColumn6.setColumnName("commission");
		dispHeaderColumn6.setDisplayName("Commission");
		
		DispHeaderColumn dispHeaderColumn7 = new DispHeaderColumn();
		dispHeaderColumn7.setColumnName("c2cwithdrawal");
		dispHeaderColumn7.setDisplayName("C2C withdrwal");
		
		DispHeaderColumn dispHeaderColumn8 = new DispHeaderColumn();
		dispHeaderColumn8.setColumnName("c2creturnSale");
		dispHeaderColumn8.setDisplayName("C2C return sale");
		
		DispHeaderColumn dispHeaderColumn9 = new DispHeaderColumn();
		dispHeaderColumn9.setColumnName("o2cReturnAmount");
		dispHeaderColumn9.setDisplayName("o2c return amount");
		
		DispHeaderColumn dispHeaderColumn10 = new DispHeaderColumn();
		dispHeaderColumn10.setColumnName("o2cWithdrawAmount");
		dispHeaderColumn10.setDisplayName("o2c withdraw amount");
		
		DispHeaderColumn dispHeaderColumn11 = new DispHeaderColumn();
		dispHeaderColumn11.setColumnName("c2cReverseAmount");
		dispHeaderColumn11.setDisplayName("c2c withdraw amount");
		
		DispHeaderColumn dispHeaderColumn12 = new DispHeaderColumn();
		dispHeaderColumn12.setColumnName("closingBalance");
		dispHeaderColumn12.setDisplayName("Closing balance");
		
		List<DispHeaderColumn> dispHeaderColumnList= new ArrayList<DispHeaderColumn>();
		
		dispHeaderColumnList.add(dispHeaderColumn);
		dispHeaderColumnList.add(dispHeaderColumn1);
		dispHeaderColumnList.add(dispHeaderColumn2);
		dispHeaderColumnList.add(dispHeaderColumn3);
		dispHeaderColumnList.add(dispHeaderColumn4);
		dispHeaderColumnList.add(dispHeaderColumn5);
		dispHeaderColumnList.add(dispHeaderColumn6);
		dispHeaderColumnList.add(dispHeaderColumn7);
		dispHeaderColumnList.add(dispHeaderColumn8);
		dispHeaderColumnList.add(dispHeaderColumn9);
		dispHeaderColumnList.add(dispHeaderColumn10);
		dispHeaderColumnList.add(dispHeaderColumn11);
		dispHeaderColumnList.add(dispHeaderColumn12); 
	
		data.setDispHeaderColumnList(dispHeaderColumnList);
		data.setExtnwcode(_masterVO.getMasterValue(MasterI.NETWORK_CODE));
		data.setFromDate(fromDate);
		data.setToDate(toDate);
		data.setProductCode(productCode);
		data.setFileType("");
		passbookDownloadRequestPojo.setData(data);
		
	}
	
	// Successful data with valid data.

	protected static String accessToken;

	public void BeforeMethod(String data1, String data2,String categoryName) throws Exception
	{
		 //if(accessToken==null) { 
		  final String methodName = "Test_OAuthenticationTest"; 
		  Log.startTestCase(methodName);
		  
		  CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		  moduleCode = CaseMaster.getModuleCode(); currentNode =
		  test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName)); 
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
	@TestManager(TestKey="PRETUPS-001")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBDOWNLOAD1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData();
		
		PassbookDownloadApi passbookDownloadApi = new PassbookDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		passbookDownloadApi.setContentType(_masterVO.getProperty("contentType"));
	//	System.out.println(passbookDownloadRequestPojo.getData().toString());
		passbookDownloadApi.addBodyParam(passbookDownloadRequestPojo);
		passbookDownloadApi.setExpectedStatusCode(200);		
		passbookDownloadApi.perform();
		passbookDownloadResponsePojo = passbookDownloadApi
				.getAPIResponseAsPOJO(PassbookDownloadResponsePojo.class);
		int statusCode = Integer.parseInt(passbookDownloadResponsePojo.getStatus());
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	//network code provided is blank 
			@Test(dataProvider = "userData")
			@TestManager(TestKey="PRETUPS-002")
			public void A_02_Test_Negative2_PassBookDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
				final String methodName = "A_02_Test_Negative2_PassBookDownload";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBDOWNLOAD2");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
				currentNode.assignCategory("REST");
				setupData();
				
				PassbookDownloadApi passbookDownloadApi = new PassbookDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				passbookDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
				passbookDownloadRequestPojo.getData().setExtnwcode("");
				
				passbookDownloadApi.addBodyParam(passbookDownloadRequestPojo);
				passbookDownloadApi.setExpectedStatusCode(400);
				passbookDownloadApi.perform();
				passbookDownloadResponsePojo = passbookDownloadApi
						.getAPIResponseAsPOJO(PassbookDownloadResponsePojo.class);
				String errorCode = passbookDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
		        
				Assert.assertEquals(Integer.parseInt(errorCode), 125278);
				Assertion.assertEquals(errorCode,"125278");
				Assertion.completeAssertions();
				Log.endTestCase(methodName);
		}
			// blank - fromDate
			@Test(dataProvider = "userData")
			@TestManager(TestKey="PRETUPS-003")
			public void A_03_Test_Negative3_PassBookDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
				final String methodName = "A_03_Test_Negative3_PassBookDownload";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBDOWNLOAD3");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
				currentNode.assignCategory("REST");
				setupData();
				
				PassbookDownloadApi passbookDownloadApi = new PassbookDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
				passbookDownloadApi.setContentType(_masterVO.getProperty("contentType"));
				
				passbookDownloadRequestPojo.getData().setFromDate("");
				
				passbookDownloadApi.addBodyParam(passbookDownloadRequestPojo);
				passbookDownloadApi.setExpectedStatusCode(400);
				passbookDownloadApi.perform();
				passbookDownloadResponsePojo = passbookDownloadApi
						.getAPIResponseAsPOJO(PassbookDownloadResponsePojo.class);
				String errorCode = passbookDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
				        
			Assert.assertEquals(Integer.parseInt(errorCode), 1004003);
			Assertion.assertEquals(errorCode,"1004003");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	 }
			// blank - toDate
			@Test(dataProvider = "userData")
			@TestManager(TestKey="PRETUPS-004")
			public void A_04_Test_Negative4_PassBookDownload(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String externalCode,String productCode) throws Exception {
				final String methodName = "A_04_Test_Negative4_PassBookDownload";
				Log.startTestCase(methodName);
				if(_masterVO.getProperty("identifierType").equals("loginid"))
					BeforeMethod(loginID, password,categoryName);
				else if(_masterVO.getProperty("identifierType").equals("msisdn"))
					BeforeMethod(msisdn, PIN,categoryName);
				CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PBDOWNLOAD4");
				moduleCode = CaseMaster.getModuleCode();
				currentNode = test.createNode(CaseMaster.getExtentCase(),categoryName);
				currentNode.assignCategory("REST");
				setupData();
				
			PassbookDownloadApi passbookDownloadApi = new PassbookDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
			passbookDownloadApi.setContentType(_masterVO.getProperty("contentType"));
			
			passbookDownloadRequestPojo.getData().setToDate("");
			
			passbookDownloadApi.addBodyParam(passbookDownloadRequestPojo);
			passbookDownloadApi.setExpectedStatusCode(400);
			passbookDownloadApi.perform();
			passbookDownloadResponsePojo = passbookDownloadApi
					.getAPIResponseAsPOJO(PassbookDownloadResponsePojo.class);
			String errorCode = passbookDownloadResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorCode();
				        
			Assert.assertEquals(Integer.parseInt(errorCode), 1004003);
			Assertion.assertEquals(errorCode,"1004003");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	 }
}