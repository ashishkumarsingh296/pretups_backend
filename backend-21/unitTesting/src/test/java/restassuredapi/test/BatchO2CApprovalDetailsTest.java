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
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.BatchO2CApprovalDetailApi.BatchO2CApprovalDetailsApi;
import restassuredapi.api.bulkapprovallist.BulkO2CApprovalListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.BatchO2CApprovalDetailRequestpojo;
import restassuredapi.pojo.BatchO2CApprovalDetailRequestpojo.Data;
import restassuredapi.pojo.BatchO2CApprovalDetailResponsepojo.BatchO2CApprovalDetailResponsepojo;
import restassuredapi.pojo.bulko2capprovallistresponsepojo.BulkO2CApprovalListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.BATCH_O2C_APPRV_DETAILS)
public class BatchO2CApprovalDetailsTest extends BaseTest{
	 static String moduleCode;
	   OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	    BatchO2CApprovalDetailRequestpojo batchO2CApprovalDetailRequestpojo =new BatchO2CApprovalDetailRequestpojo();
	    BatchO2CApprovalDetailResponsepojo batchO2CApprovalDetailResponsepojo = new BatchO2CApprovalDetailResponsepojo();
	    Data data = new Data();
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

	        Object[][] Data = new Object[user][7];
	        int j=0;
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
	        for(int i=1;i<=rowCount;i++){
	            if(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE,i).equals("BCU")){
	                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
	                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
	                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
	                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
	                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
	                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	                Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
	                j++;
	              
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
	    public void setupData() {
	    	data.setApprovalLevel(_masterVO.getProperty("Level1"));
	    	data.setApprovalType(_masterVO.getProperty("O2CType"));
	    	data.setBatchId("NGOB280121.002");
	    	data.setApprovalSubType(_masterVO.getProperty("transferOperation"));
	    	batchO2CApprovalDetailRequestpojo.setData(data);
	    }
	    @Test(dataProvider = "userData")
	    public void  A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_01_Test_Success";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST1");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	       
	       setupData();
	       
	       BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
	       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
	       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
	       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
		   bulkO2CApprovalListAPI.perform();
		   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
	       String messageCode = batchO2CApprovalDetailResponsepojo.getStatus();
	        Assert.assertEquals("200", messageCode);
	        Assertion.assertEquals("200", messageCode);
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_02_Blank_AprvLevel(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_02_Blank_AprvLevel";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST2");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        data.setApprovalLevel("");
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		        String message = batchO2CApprovalDetailResponsepojo.getMessage();
	        Assert.assertEquals(message, "Invalid Approval Level .");
	        Assertion.assertEquals(message, "Invalid Approval Level .");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_03_Blank_AprvType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_03_Blank_AprvType";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST3");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        data.setApprovalType("");
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		       String message = batchO2CApprovalDetailResponsepojo.getMessage();
	        Assert.assertEquals(message, "Approval Type should either FOC or O2C.");
	        Assertion.assertEquals(message, "Approval Type should either FOC or O2C.");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_04_Invalid_AprvLevel(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_04_Invalid_AprvLevel";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST4");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        String level=new RandomGeneration().randomAlphabets(3);
	        data.setApprovalLevel(level);
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		      String message = batchO2CApprovalDetailResponsepojo.getMessage();
	        Assert.assertEquals(message, "Invalid Approval Level "+level+".");
	        Assertion.assertEquals(message, "Invalid Approval Level "+level+".");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_05_Invalid_AprvType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_05_Invalid_AprvType";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST5");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        data.setApprovalType(new RandomGeneration().randomAlphabets(3));
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		     String message = batchO2CApprovalDetailResponsepojo.getMessage();
	        Assert.assertEquals(message, "Approval Type should either FOC or O2C.");
	        Assertion.assertEquals(message, "Approval Type should either FOC or O2C.");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_06_Blank_AprvSubType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_06_Blank_AprvSubType";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST6");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        data.setApprovalSubType("");
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(400);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		     String messageCode = batchO2CApprovalDetailResponsepojo.getMessageCode();
	        Assert.assertEquals(messageCode, "8135");
	        Assertion.assertEquals(messageCode, "8135");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    @Test(dataProvider = "userData")
	    public void  A_07_Blank_BatchId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception{
	        final String methodName = "A_07_Blank_BatchId";
	        Log.startTestCase(methodName);
	        if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
	        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BATCHO2CAPRVLIST7");
	        moduleCode = CaseMaster.getModuleCode();
	        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
	        currentNode.assignCategory("REST");
	        setupData();
	       
	        data.setBatchId("");
	        batchO2CApprovalDetailRequestpojo.setData(data);
	        BatchO2CApprovalDetailsApi bulkO2CApprovalListAPI = new BatchO2CApprovalDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
		       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
		       bulkO2CApprovalListAPI.addBodyParam(batchO2CApprovalDetailRequestpojo);
		       bulkO2CApprovalListAPI.setExpectedStatusCode(400);
			   bulkO2CApprovalListAPI.perform();
			   batchO2CApprovalDetailResponsepojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BatchO2CApprovalDetailResponsepojo.class);
		     String messageCode = batchO2CApprovalDetailResponsepojo.getMessage();
	        Assert.assertEquals(messageCode, "Batch ID should not be empty.");
	        Assertion.assertEquals(messageCode, "Batch ID should not be empty.");
	        Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	      }
	    
}
