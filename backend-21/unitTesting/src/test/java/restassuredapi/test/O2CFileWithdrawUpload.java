package restassuredapi.test;

import java.io.IOException;
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
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.C2CBatchDownloadTemplate.C2CBatchDownloadTemplateApi;
import restassuredapi.api.c2cbatchuserslistdownload.C2CBatchUsersListDownloadAPI;
import restassuredapi.api.c2cfileupload.C2CFileUploadApi;
import restassuredapi.api.o2cbatchwithdrawfileupload.O2CBatchWFileUploadApi;
import restassuredapi.api.o2cpurchaseorwithdrawtemplateapi.O2cPurchaseOrWithdrawTemplateApi;
import restassuredapi.api.o2cpurchaseorwithdrawuserlistapi.O2cPurchaseOrWithdrawUserListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2CFileUploadApiRequestpojo.C2CFileUploadApiRequestPojo;
import restassuredapi.pojo.c2CFileUploadApiResponsepojo.C2CFileUploadApiResponsePojo;
import restassuredapi.pojo.c2cbatchdownloadtemplateresponsepojo.C2cBatchDownloadTemplateResponsePojo;
import restassuredapi.pojo.c2cbatchuserslistdownloadresponsepojo.C2CBatchUsersListDownloadResponsePojo;
import restassuredapi.pojo.o2cbatchwithdrawfileuploadrequestpojo.O2CBatchWFileUploadRequestPojo;
import restassuredapi.pojo.o2cbatchwithdrawresponsepojo.O2CBatchFileWUploadResponsePojo;
import restassuredapi.pojo.o2cpurchaseorwithdrawtemplateresponsepojo.O2cPurchaseOrWithdrawTemplateResponsePojo;
import restassuredapi.pojo.o2cpurchaseorwithdrawuserlistresponsepojo.O2cPurchaseOrWithdrawUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.BATCH_O2C_WITHDRAW)
public class O2CFileWithdrawUpload extends BaseTest{

	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	Date tomorrow = DateUtils.addDays(new Date(), 3);
	String tomorrowdate = df.format(tomorrow);
	Date prior = DateUtils.addDays(new Date(), -1);
	String PriorDate = df.format(prior);
	String currentDate = df.format(dateobj);
	
	static String moduleCode;
	O2cPurchaseOrWithdrawUserListResponsePojo c2CBatchUsersListDownloadResponsePojo = new O2cPurchaseOrWithdrawUserListResponsePojo();
	O2CBatchWFileUploadRequestPojo c2CFileUploadApiRequestPojo = new O2CBatchWFileUploadRequestPojo();
	O2CBatchFileWUploadResponsePojo c2CFileUploadApiResponsePojo = new O2CBatchFileWUploadResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	O2cPurchaseOrWithdrawTemplateResponsePojo c2cBatchDownloadTemplateResponsePojo = new O2cPurchaseOrWithdrawTemplateResponsePojo();
	RandomGeneration rnd = new RandomGeneration();
	//Data data = new Data();
	Login login = new Login();
	String searchCategoryCode = null;
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
    	searchCategoryCode = _masterVO.getProperty("categoryCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int user = 0;
        String GeoDomainCode = null;
        for (int i = 1; i <= rowCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
                user++;
                GeoDomainCode = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i);
            }
        }
        String GeoDomainName = null;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHY_DOMAIN_TYPES_SHEET);
        int rowCount1 = ExcelUtility.getRowCount();
        for (int i = 1; i <= rowCount1; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, i).equals(GeoDomainCode)) {
                GeoDomainName = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE_NAME, i);
            }
        }
        String domainCode = null;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
        int rowCount2 = ExcelUtility.getRowCount();
        for (int i = 1; i <= rowCount2; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i).equals(GeoDomainName))
                domainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i).toUpperCase();
        }
       
//        ArrayList<String> opUserData =new ArrayList<String>();
//        Map<String, String> userInfo = UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.O2C_TRANSFER_REVAMP,searchCategoryCode);
//        opUserData.add(userInfo.get("CATEGORY_NAME"));
//        opUserData.add(userInfo.get("LOGIN_ID"));
//        opUserData.add(userInfo.get("MSISDN"));
//        opUserData.add(userInfo.get("USER_NAME"));
        Object[][] Data = new Object[user][12];
        int j = 0;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        for (int i = 1; i <= rowCount; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i).equals("BCU")) {
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                Data[j][7] = domainCode;
//                Data[j][8] = opUserData.get(0);
//                Data[j][9] = opUserData.get(1);
//                Data[j][10] = opUserData.get(2);
//                Data[j][11] = opUserData.get(3);
               
                j++;
            }
        }
        // Getting Channel User category hierarchy
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USER_CATEGORY_SHEET);
        int rowCount3 = ExcelUtility.getRowCount();
        int k=0;
        for(int i=1, dataLen =1; (i<= rowCount3 && dataLen<= Data.length); i++, dataLen++) {
        	Data[k][8] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i);
        	Data[k][9] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
        	k++;
        }
        
        // Getting Product details
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int rowCount4 = ExcelUtility.getRowCount();
        int l=0;
        for(int i=1, dataLen =1; (i<= rowCount4 && dataLen<= Data.length); i++, dataLen++) {
        	Data[l][10] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
        	l++;
        }
        int w = 0;
        for(int i=1; i<= Data.length; i++) {
        	Data[w][11] = "SALE"; //talk to Chetan
        	w++;
        }
        return Data;
    }


	public Map<String,String> downloadc2cBatchTemplate(String purchaseOrWithdraw) throws IOException{
		
		O2cPurchaseOrWithdrawTemplateApi c2CBatchDownloadTemplateApi = new O2cPurchaseOrWithdrawTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CBatchDownloadTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		c2CBatchDownloadTemplateApi.setPurchaseOrWithdraw(purchaseOrWithdraw);
		c2CBatchDownloadTemplateApi.setExpectedStatusCode(200);
		c2CBatchDownloadTemplateApi.perform();
		
		c2cBatchDownloadTemplateResponsePojo = c2CBatchDownloadTemplateApi
							.getAPIResponseAsPOJO(O2cPurchaseOrWithdrawTemplateResponsePojo.class);
	
		Map<String,String> response = new HashMap<String,String>();
		response.put("fileName", c2cBatchDownloadTemplateResponsePojo.getFileName().toString());
		response.put("fileType",c2cBatchDownloadTemplateResponsePojo.getFileType().toString());
		response.put("fileAttachment", c2cBatchDownloadTemplateResponsePojo.getFileattachment().toString());
		
		return response;
		
	}
	public String genExcelData(String categoryName,String productCode,String domainCode,String geoDomainCode,String walletType,String purchaseOrWithdraw) throws IOException
	{
		O2cPurchaseOrWithdrawUserListApi c2CBatchUsersListDownloadAPI = new O2cPurchaseOrWithdrawUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBatchUsersListDownloadAPI.setCategoryCode(categoryName);
		c2CBatchUsersListDownloadAPI.setDomainCode(domainCode);
		c2CBatchUsersListDownloadAPI.setGeoDomainCode(geoDomainCode);
		c2CBatchUsersListDownloadAPI.setProductCode(productCode);
		c2CBatchUsersListDownloadAPI.setPurchaseOrWithdraw(purchaseOrWithdraw);
		c2CBatchUsersListDownloadAPI.setWalletTypeOpt(walletType);
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		
		c2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(O2cPurchaseOrWithdrawUserListResponsePojo.class);
		

		String fileName= c2CBatchUsersListDownloadResponsePojo.getFileName().toString();
		String fileAttachment= c2CBatchUsersListDownloadResponsePojo.getFileattachment().toString();
		
		String dirPath=_masterVO.getProperty("O2CBatchUserList");
		String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
		ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
		
		RandomGeneration randomGeneration = new RandomGeneration();
		

		for(int a=0;a<ExcelUtility.getRowCount();a++) {
			for(int b=0;b<ExcelUtility.getColumnCount();b++) {
				System.out.print(ExcelUtility.getCellDataHSSF(a,b)+" ");
			}
			System.out.println();
		}
		int noOfRows = ExcelUtility.getRowCount();

		int counter_msisdn;
	
		String[][] arr = new String[noOfRows][2] ;
		for(int i=1; i<noOfRows; i++)
		{
			counter_msisdn = 0 ;
			arr[i][0] = ExcelUtility.getCellDataHSSF(i,0) ;
			if(arr[i][0] != null && !((arr[i][0]).equals(""))){counter_msisdn+=1 ;}
			arr[i][1] = ExcelUtility.getCellDataHSSF(i,5) ;
			if(arr[i][1] != null && !((arr[i][1]).equals(""))){counter_msisdn+=1 ;}
			
			if(counter_msisdn ==2)
			{
				Log.info("\n\n COUNTER_MSISDN = "+counter_msisdn) ;
				arr[0][0] = arr[i][0] ;
				arr[0][1] = arr[i][1] ;
				Log.info(" UNEMPTY STORED IN ARRAY TO wRITE IN EXCEL");
				Log.info("arr[0][0] "+arr[0][0]) ;
				break ;
			}
		}
	
		String msisdn= arr[1][0];
		
		String qty= randomGeneration.randomNumberWithoutZero(2);
		String remarks =randomGeneration.randomAlphabets(15);
		
		String excelData=msisdn + "," + qty + "," + productCode + "," + remarks;
		
		return excelData;
	}

public void setupData(String pin,String categoryName,String productCode,String domainCode,String geoDomainCode,String walletType,String purchaseOrWithdraw) throws IOException {

	List<String> excelData =new ArrayList<String>();
	excelData.add(genExcelData(categoryName,productCode,domainCode,geoDomainCode,walletType,purchaseOrWithdraw));

	
	Map<String,String> response = downloadc2cBatchTemplate(purchaseOrWithdraw);
	String fileName= response.get("fileName");
	String fileType= response.get("fileType");
	String fileAttachment= response.get("fileAttachment");
	
	String dirPath=_masterVO.getProperty("C2CBulkRecharge");
	String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
	ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
	String[] row = excelData.get(0).split(",");
	ExcelUtility.setCellDataXLS(row[0],1,0);
	ExcelUtility.setCellDataXLS("",1,1);
	ExcelUtility.setCellDataXLS("",1,2);
	ExcelUtility.setCellDataXLS("",1,3);
	ExcelUtility.setCellDataXLS(row[1],1,4);
	ExcelUtility.setCellDataXLS(row[3],1,5);
//	for(int i=0;i<excelData.size();i++) {
//		String[] row = excelData.get(i).split(",");
//		for(int j=0;j<row.length;j++) {
//			ExcelUtility.setCellDataXLS(row[j], 2+i,j);
//		}	
//	}
	String base64file = ExcelUtility.excelToBase64(filepath);
	Log.info("Base 64 file with data:");
	Log.info(base64file);
	c2CFileUploadApiRequestPojo.setBatchName(new RandomGeneration().randomAlphaNumeric(8));
	c2CFileUploadApiRequestPojo.setFileName(fileName);
	c2CFileUploadApiRequestPojo.setFileType(fileType);
	c2CFileUploadApiRequestPojo.setFileAttachment(base64file);
	c2CFileUploadApiRequestPojo.setLanguage1("english");
	c2CFileUploadApiRequestPojo.setLanguage2("spanish");
	c2CFileUploadApiRequestPojo.setPin(pin);
	
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

@Test(dataProvider = "userData")
public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception {
	final String methodName = "A_01_Test_success";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBWID1");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, walletType, _masterVO.getProperty("withdraw").toUpperCase());
	
	O2CBatchWFileUploadApi c2CFileUploadApi = new O2CBatchWFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setGeoDomain(geoDomainCode);
	c2CFileUploadApi.setCategory(categoryCode);
	c2CFileUploadApi.setChannelDomain(domainCode);
	c2CFileUploadApi.setProduct(productCode);
	c2CFileUploadApi.setWalletType("SAL");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(O2CBatchFileWUploadResponsePojo.class);
	String status = c2CFileUploadApiResponsePojo.getStatus();

	
	if(status == "200")
	Assert.assertEquals(200, status);
	Assertion.assertEquals(status, "200");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}


@Test(dataProvider = "userData")
public void A_02_Invalid_Category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception {
	final String methodName = "A_02_Invalid_Category";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBWID2");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, walletType, _masterVO.getProperty("withdraw").toUpperCase());
	
	O2CBatchWFileUploadApi c2CFileUploadApi = new O2CBatchWFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setGeoDomain(geoDomainCode);
	c2CFileUploadApi.setCategory("");
	c2CFileUploadApi.setChannelDomain(domainCode);
	c2CFileUploadApi.setProduct(productCode);
	c2CFileUploadApi.setWalletType("SAL");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(O2CBatchFileWUploadResponsePojo.class);
	String message = c2CFileUploadApiResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	
	Assert.assertEquals(message,"Category cannot be blank or alphanumeric.");
	Assertion.assertEquals(message, "Category cannot be blank or alphanumeric.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}
@Test(dataProvider = "userData")
public void A_03_Invalid_Geography(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception {
	final String methodName = "A_02_Invalid_Geography";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBWID3");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, walletType, _masterVO.getProperty("withdraw").toUpperCase());
	
	O2CBatchWFileUploadApi c2CFileUploadApi = new O2CBatchWFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setGeoDomain("");
	c2CFileUploadApi.setCategory(categoryCode);
	c2CFileUploadApi.setChannelDomain(domainCode);
	c2CFileUploadApi.setProduct(productCode);
	c2CFileUploadApi.setWalletType("SAL");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(O2CBatchFileWUploadResponsePojo.class);
	String message = c2CFileUploadApiResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	
	Assert.assertEquals(message,"Invalid geography");
	Assertion.assertEquals(message, "Invalid geography");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}

@Test(dataProvider = "userData")
public void A_04_Invalid_Product(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception {
	final String methodName = "A_02_Invalid_Product";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBWID4");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryCode));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, walletType, _masterVO.getProperty("withdraw").toUpperCase());
	
	O2CBatchWFileUploadApi c2CFileUploadApi = new O2CBatchWFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setGeoDomain(geoDomainCode);
	c2CFileUploadApi.setCategory(categoryCode);
	c2CFileUploadApi.setChannelDomain(domainCode);
	c2CFileUploadApi.setProduct("");
	c2CFileUploadApi.setWalletType("SAL");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(O2CBatchFileWUploadResponsePojo.class);
	String message = c2CFileUploadApiResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	
	Assert.assertEquals(message,"Product cannot be blank.");
	Assertion.assertEquals(message, "Product cannot be blank.");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}

@Test(dataProvider = "userData")
public void A_05_Invalid_Domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
		String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception {
	final String methodName = "A_02_Invalid_Product";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CBWID5");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categorCode,categoryName));   
	currentNode.assignCategory("REST");
	setupData(PIN,categoryCode, productCode, domainCode, geoDomainCode, walletType, _masterVO.getProperty("withdraw").toUpperCase());
	
	O2CBatchWFileUploadApi c2CFileUploadApi = new O2CBatchWFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setGeoDomain(geoDomainCode);
	c2CFileUploadApi.setCategory(categoryCode);
	c2CFileUploadApi.setChannelDomain("");
	c2CFileUploadApi.setProduct(productCode);
	c2CFileUploadApi.setWalletType("SAL");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(O2CBatchFileWUploadResponsePojo.class);
	String message = c2CFileUploadApiResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
	
	Assert.assertEquals(message,"Entered user is not from your allowed domain(s).");
	Assertion.assertEquals(message, "Entered user is not from your allowed domain(s).");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}

}
