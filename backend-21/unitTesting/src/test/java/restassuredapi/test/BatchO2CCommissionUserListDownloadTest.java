package restassuredapi.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.BatchO2CApprovalDetailApi.BatchO2CApprovalDetailsApi;
import restassuredapi.api.batchO2CCommissionUserListDownload.BatchO2CCommissionUserListDownloadApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.BatchO2CApprovalDetailRequestpojo;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.Data;
import restassuredapi.pojo.BatchO2CApprovalDetailResponsepojo.BatchO2CApprovalDetailResponsepojo;
import restassuredapi.pojo.batchO2CCommissionUserListDownloadRequestpojo.BatchO2CCommissionUserListDownloadRequestpojo;
import restassuredapi.pojo.batchO2CCommissionUserListDownloadResponsepojo.BatchO2CCommissionUserListDownloadResponsepojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.BATCH_O2C_COMMISSION_USER_LIST_DOWNLOAD)

public class BatchO2CCommissionUserListDownloadTest extends BaseTest{
	static String moduleCode;
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
    BatchO2CCommissionUserListDownloadRequestpojo batchO2CCommissionUserListDownloadRequestpojo;
    BatchO2CCommissionUserListDownloadResponsepojo batchO2CCommissionUserListDownloadResponsepojo;

    @DataProvider(name = "userData")
    public Object[][] TestDataFeed(){
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int user=0;
       
        for(int i=1;i<=rowCount;i++){
            if(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE,i).equals("BCU")){
                user++;
              
        }
    }

        Object[][] Data1 = new Object[user][5];
        int j=0;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        for(int i=1;i<=rowCount;i++){
            if(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE,i).equals("BCU")){
                Data1[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                Data1[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                Data1[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data1[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data1[j][4] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                j++;
            }

        }
        
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        rowCount = ExcelUtility.getRowCount();
        Object[][] Data2 = new Object[rowCount][4];
        j=0;
        for(int i=1; i<=rowCount; i++) {
        	Data2[j][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
        	Data2[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
        	Data2[j][2] = ExcelUtility.getCellData(0, ExcelI.GEOGRAPHY, i);
        	Data2[j][3] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        	j++;
        }
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        rowCount = ExcelUtility.getRowCount();
        Object[][] Data3 = new Object[rowCount][1];
        j=0;
        for(int i=1; i<=rowCount; i++) {
        	Data3[j++][0] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
        }
        
        Object[][] Data4 = new Object[2][1];
        Data4[0][0] = "template";
        Data4[1][0] = "userList";
        
        Data3 = joinMatrix(Data3, Data4);
        Data2 = joinMatrix(Data2, Data3);
        Data1 = joinMatrix(Data1, Data2);
        
        return Data1;
    }
    
    public Object[][] joinMatrix(Object[][] Data1, Object[][] Data2) {
    	Object[][] Data;
    	int row=Data1.length * Data2.length;
        int col = Data1[0].length + Data2[0].length;
        int c1=Data1[0].length, c2=Data2[0].length;
    	Data = new Object[row][col];
    	int ind=0;
    	for(int i=0; i<Data1.length; i++) {
    		for(int j=0; j<Data2.length; j++) {
    			int k=0;
    			for(k=0; k<c1; k++)	Data[ind][k]=Data1[i][k];
    			for(int l=0; l<c2; l++) Data[ind][k++]=Data2[j][l];
    			ind+=1;
    		}
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
    
    public void setupData(String category, String domain, String fileType, String geography, String product) {
    	batchO2CCommissionUserListDownloadRequestpojo = new BatchO2CCommissionUserListDownloadRequestpojo();
    	
    	batchO2CCommissionUserListDownloadRequestpojo.setCategory(category);
    	batchO2CCommissionUserListDownloadRequestpojo.setDomain(domain);
    	batchO2CCommissionUserListDownloadRequestpojo.setFileType(fileType);
    	batchO2CCommissionUserListDownloadRequestpojo.setGeography(geography);
    	batchO2CCommissionUserListDownloadRequestpojo.setProduct(product);
    }

    
    @Test(dataProvider = "userData")
    @TestManager(TestKey="PRETUPS-00000")
    public void  A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String categoryName, String channelDomainName, String channelCategoryCode, String channelGeography, String ChannelCateogryName, String productCode, String fileType) throws Exception{
        final String methodName = "A_01_Test_Success";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CCOMSNUSRLSTDWNLDT1");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),ChannelCateogryName, productCode));
        currentNode.assignCategory("REST");
       
       setupData(channelCategoryCode, channelDomainName, fileType, channelGeography, productCode);
       
       BatchO2CCommissionUserListDownloadApi batchO2CCommissionUserListDownloadApi = new BatchO2CCommissionUserListDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       batchO2CCommissionUserListDownloadApi.setContentType(_masterVO.getProperty("contentType"));
       batchO2CCommissionUserListDownloadApi.addBodyParam(batchO2CCommissionUserListDownloadRequestpojo);
       batchO2CCommissionUserListDownloadApi.setExpectedStatusCode(200);
       batchO2CCommissionUserListDownloadApi.perform();
       batchO2CCommissionUserListDownloadResponsepojo = batchO2CCommissionUserListDownloadApi.getAPIResponseAsPOJO(BatchO2CCommissionUserListDownloadResponsepojo.class);
       String messageCode = batchO2CCommissionUserListDownloadResponsepojo.getStatus();
        Assert.assertEquals("200", messageCode);
        Assertion.assertEquals("200", messageCode);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey="PRETUPS-00000")
    public void  A_02_Test_EmptyFileType(String loginID, String password, String msisdn, String PIN, String categoryName, String channelDomainName, String channelCategoryCode, String channelGeography, String ChannelCateogryName, String productCode, String fileType) throws Exception{
        final String methodName = "A_02_Test_EmptyFileType";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CCOMSNUSRLSTDWNLDT2");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),ChannelCateogryName, productCode));
        currentNode.assignCategory("REST");
       
       setupData(channelCategoryCode, channelDomainName, "", channelGeography, productCode);
       
       BatchO2CCommissionUserListDownloadApi batchO2CCommissionUserListDownloadApi = new BatchO2CCommissionUserListDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       batchO2CCommissionUserListDownloadApi.setContentType(_masterVO.getProperty("contentType"));
       batchO2CCommissionUserListDownloadApi.addBodyParam(batchO2CCommissionUserListDownloadRequestpojo);
       batchO2CCommissionUserListDownloadApi.setExpectedStatusCode(200);
       batchO2CCommissionUserListDownloadApi.perform();
       batchO2CCommissionUserListDownloadResponsepojo = batchO2CCommissionUserListDownloadApi.getAPIResponseAsPOJO(BatchO2CCommissionUserListDownloadResponsepojo.class);
       String message = batchO2CCommissionUserListDownloadResponsepojo.getMessage();
       String expectedMessage = "Empty or invalid file type.";
        Assert.assertEquals(expectedMessage, message);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey="PRETUPS-00000")
    public void  A_03_Test_EmptyCategoryName(String loginID, String password, String msisdn, String PIN, String categoryName, String channelDomainName, String channelCategoryCode, String channelGeography, String ChannelCateogryName, String productCode, String fileType) throws Exception{
    	if(fileType.equals("template")) return;
    	final String methodName = "A_03_Test_EmptyCategoryName";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CCOMSNUSRLSTDWNLDT3");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),ChannelCateogryName, productCode));
        currentNode.assignCategory("REST");
       
       setupData("", channelDomainName, fileType, channelGeography, productCode);
       
       BatchO2CCommissionUserListDownloadApi batchO2CCommissionUserListDownloadApi = new BatchO2CCommissionUserListDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       batchO2CCommissionUserListDownloadApi.setContentType(_masterVO.getProperty("contentType"));
       batchO2CCommissionUserListDownloadApi.addBodyParam(batchO2CCommissionUserListDownloadRequestpojo);
       batchO2CCommissionUserListDownloadApi.setExpectedStatusCode(400);
       batchO2CCommissionUserListDownloadApi.perform();
       batchO2CCommissionUserListDownloadResponsepojo = batchO2CCommissionUserListDownloadApi.getAPIResponseAsPOJO(BatchO2CCommissionUserListDownloadResponsepojo.class);
       String message = batchO2CCommissionUserListDownloadResponsepojo.getMessage();
       String expectedMessage = "Category cannot be blank or alphanumeric.";
        Assert.assertEquals(expectedMessage, message);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey="PRETUPS-00000")
    public void  A_04_Test_invalid_tokens(String loginID, String password, String msisdn, String PIN, String categoryName, String channelDomainName, String channelCategoryCode, String channelGeography, String ChannelCateogryName, String productCode, String fileType) throws Exception{
    	if(fileType.equals("template")) return;
    	final String methodName = "A_04_Test_invalid_tokens";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CCOMSNUSRLSTDWNLDT4");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),ChannelCateogryName, productCode));
        currentNode.assignCategory("REST");
       
       setupData("", channelDomainName, fileType, channelGeography, productCode);
       
       BatchO2CCommissionUserListDownloadApi batchO2CCommissionUserListDownloadApi = new BatchO2CCommissionUserListDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken+new RandomGeneration().randomAlphabets(4));
       batchO2CCommissionUserListDownloadApi.setContentType(_masterVO.getProperty("contentType"));
       batchO2CCommissionUserListDownloadApi.addBodyParam(batchO2CCommissionUserListDownloadRequestpojo);
       batchO2CCommissionUserListDownloadApi.setExpectedStatusCode(400);
       batchO2CCommissionUserListDownloadApi.perform();
       batchO2CCommissionUserListDownloadResponsepojo = batchO2CCommissionUserListDownloadApi.getAPIResponseAsPOJO(BatchO2CCommissionUserListDownloadResponsepojo.class);

       	String status = batchO2CCommissionUserListDownloadResponsepojo.getMessageCode();
		Assert.assertEquals(241018, Integer.parseInt(status));
		Assertion.assertEquals(status, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
       
      }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey="PRETUPS-00000")
    public void  A_05_Test_EmptyDomainName(String loginID, String password, String msisdn, String PIN, String categoryName, String channelDomainName, String channelCategoryCode, String channelGeography, String ChannelCateogryName, String productCode, String fileType) throws Exception{
    	if(fileType.equals("template")) return;
    	final String methodName = "A_05_Test_EmptyDomainName";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CCOMSNUSRLSTDWNLDT5");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),ChannelCateogryName, productCode));
        currentNode.assignCategory("REST");
       
       setupData(channelCategoryCode, "", fileType, channelGeography, productCode);
       
       BatchO2CCommissionUserListDownloadApi batchO2CCommissionUserListDownloadApi = new BatchO2CCommissionUserListDownloadApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       batchO2CCommissionUserListDownloadApi.setContentType(_masterVO.getProperty("contentType"));
       batchO2CCommissionUserListDownloadApi.addBodyParam(batchO2CCommissionUserListDownloadRequestpojo);
       batchO2CCommissionUserListDownloadApi.setExpectedStatusCode(400);
       batchO2CCommissionUserListDownloadApi.perform();
       batchO2CCommissionUserListDownloadResponsepojo = batchO2CCommissionUserListDownloadApi.getAPIResponseAsPOJO(BatchO2CCommissionUserListDownloadResponsepojo.class);
       String message = batchO2CCommissionUserListDownloadResponsepojo.getMessage();
       String expectedMessage = "Domain cannot be blank.";
        Assert.assertEquals(expectedMessage, message);
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    
    
    
    
    
}
