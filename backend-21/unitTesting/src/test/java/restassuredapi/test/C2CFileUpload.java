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
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2CFileUploadApiRequestpojo.C2CFileUploadApiRequestPojo;
import restassuredapi.pojo.c2CFileUploadApiResponsepojo.C2CFileUploadApiResponsePojo;
import restassuredapi.pojo.c2cbatchdownloadtemplateresponsepojo.C2cBatchDownloadTemplateResponsePojo;
import restassuredapi.pojo.c2cbatchuserslistdownloadresponsepojo.C2CBatchUsersListDownloadResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.REST_C2C_BULK_RECHARGE)
public class C2CFileUpload extends BaseTest {

	DateFormat df = new SimpleDateFormat("dd/MM/YY");
	Date dateobj = new Date();
	Date tomorrow = DateUtils.addDays(new Date(), 3);
	String tomorrowdate = df.format(tomorrow);
	Date prior = DateUtils.addDays(new Date(), -1);
	String PriorDate = df.format(prior);
	String currentDate = df.format(dateobj);
	
	static String moduleCode;
	C2CBatchUsersListDownloadResponsePojo c2CBatchUsersListDownloadResponsePojo = new C2CBatchUsersListDownloadResponsePojo();
	C2CFileUploadApiRequestPojo c2CFileUploadApiRequestPojo = new C2CFileUploadApiRequestPojo();
	C2CFileUploadApiResponsePojo c2CFileUploadApiResponsePojo = new C2CFileUploadApiResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	C2cBatchDownloadTemplateResponsePojo c2cBatchDownloadTemplateResponsePojo = new C2cBatchDownloadTemplateResponsePojo();
	RandomGeneration rnd = new RandomGeneration();
	//Data data = new Data();
	Login login = new Login();

	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
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
    Object[][] Data = new Object[totalObjectCounter][8];
    for (int j = 0, k = 0; j < alist2.size(); j++) {
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ToChannelUser= null;
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
            	ToChannelUser = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
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
                Data[k][7] = ToChannelUser;
                k++;
            }
        }
    }
    return Data;
}


	public Map<String,String> downloadc2cBatchTemplate() throws IOException{
		
		C2CBatchDownloadTemplateApi c2CBatchDownloadTemplateApi = new C2CBatchDownloadTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		c2CBatchDownloadTemplateApi.setContentType(_masterVO.getProperty("contentType"));
		c2CBatchDownloadTemplateApi.setTransferType("TRANSFER");
		c2CBatchDownloadTemplateApi.setExpectedStatusCode(200);
		c2CBatchDownloadTemplateApi.perform();
		
		c2cBatchDownloadTemplateResponsePojo = c2CBatchDownloadTemplateApi
							.getAPIResponseAsPOJO(C2cBatchDownloadTemplateResponsePojo.class);
	
		Map<String,String> response = new HashMap<String,String>();
		response.put("fileName", c2cBatchDownloadTemplateResponsePojo.getFileName().toString());
		response.put("fileType",c2cBatchDownloadTemplateResponsePojo.getFileType().toString());
		response.put("fileAttachment", c2cBatchDownloadTemplateResponsePojo.getFileattachment().toString());
		
		return response;
		
	}
	public String genExcelData(String categoryName) throws IOException
	{
		C2CBatchUsersListDownloadAPI c2CBatchUsersListDownloadAPI = new C2CBatchUsersListDownloadAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

		c2CBatchUsersListDownloadAPI.setContentType(_masterVO.getProperty("contentType"));
		c2CBatchUsersListDownloadAPI.setCategory(categoryName);
		c2CBatchUsersListDownloadAPI.setOperationType(_masterVO.getProperty("transferOperation"));
		c2CBatchUsersListDownloadAPI.setExpectedStatusCode(200);
		c2CBatchUsersListDownloadAPI.perform();
		
		c2CBatchUsersListDownloadResponsePojo = c2CBatchUsersListDownloadAPI.getAPIResponseAsPOJO(C2CBatchUsersListDownloadResponsePojo.class);
		

		String fileName= c2CBatchUsersListDownloadResponsePojo.getFileName().toString();
		String fileAttachment= c2CBatchUsersListDownloadResponsePojo.getFileattachment().toString();
		
		String dirPath=_masterVO.getProperty("C2CBulkRechargeUserList");
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
	
		String msisdn= arr[0][0];
		String qty= randomGeneration.randomNumberWithoutZero(1);
		String productCode= arr[0][1];
		String remarks =randomGeneration.randomAlphabets(15);
		
		String excelData=msisdn + "," + qty + "," + productCode + "," + remarks;
		
		return excelData;
	}

public void setupData(String pin,String categoryName) throws IOException {

	List<String> excelData =new ArrayList<String>();
	excelData.add(genExcelData(categoryName));

	
	Map<String,String> response = downloadc2cBatchTemplate();
	String fileName= response.get("fileName");
	String fileType= response.get("fileType");
	String fileAttachment= response.get("fileAttachment");
	
	String dirPath=_masterVO.getProperty("C2CBulkRecharge");
	String filepath=ExcelUtility.base64ToExcel(fileAttachment, fileName,dirPath);
	ExcelUtility.setExcelFileXLS(filepath,"Sheet1");
	String[] row = excelData.get(0).split(",");
	ExcelUtility.setCellDataXLS(row[0],1,0);
	ExcelUtility.setCellDataXLS(row[1],1,3);
	ExcelUtility.setCellDataXLS(row[2],1,4);
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
public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String ToCategoryName) throws Exception {
	final String methodName = "A_01_Test_success";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CUF1");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName,ToCategoryName));   
	currentNode.assignCategory("REST");
	setupData(PIN,ToCategoryName);
	
	C2CFileUploadApi c2CFileUploadApi = new C2CFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setCategory(ToCategoryName);
	c2CFileUploadApi.setOperationType("T");
	c2CFileUploadApi.setExpectedStatusCode(201);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(C2CFileUploadApiResponsePojo.class);
	String status = c2CFileUploadApiResponsePojo.getStatus();

	
	if(status == "200")
	Assert.assertEquals(200, status);
	Assertion.assertEquals(status, "200");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}


@Test(dataProvider = "userData")
public void A_02_Invalid_Category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String ToCategoryName) throws Exception {
	final String methodName = "A_02_Invalid_Category";
	Log.startTestCase(methodName);
	if(_masterVO.getProperty("identifierType").equals("loginid"))
		BeforeMethod(loginID, password,categoryName);
	else if(_masterVO.getProperty("identifierType").equals("msisdn"))
		BeforeMethod(msisdn, PIN,categoryName);
	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2CUF1");
	moduleCode = CaseMaster.getModuleCode();

	currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
	currentNode.assignCategory("REST");
	setupData(PIN,ToCategoryName);
	
	C2CFileUploadApi c2CFileUploadApi = new C2CFileUploadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
	c2CFileUploadApi.setContentType(_masterVO.getProperty("contentType"));
	c2CFileUploadApi.addBodyParam(c2CFileUploadApiRequestPojo);
	c2CFileUploadApi.setCategory(new RandomGeneration().randomAlphabets(5));
	c2CFileUploadApi.setOperationType("T");
	c2CFileUploadApi.setExpectedStatusCode(200);
	c2CFileUploadApi.perform();
	c2CFileUploadApiResponsePojo = c2CFileUploadApi
			.getAPIResponseAsPOJO(C2CFileUploadApiResponsePojo.class);
	String message = c2CFileUploadApiResponsePojo.getMessage();
	
	Assert.assertEquals(message,"Invalid category");
	Assertion.assertEquals(message, "Invalid category");
	Assertion.completeAssertions();
	Log.endTestCase(methodName);

}

}