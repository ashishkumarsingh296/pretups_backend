package restassuredapi.test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.*;
import com.utils.constants.Module;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import restassuredapi.api.bulkapprovallist.BulkO2CApprovalListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.bulko2capprovallistrequestpojo.BulkO2CApprovalListRequestPojo;
import restassuredapi.pojo.bulko2capprovallistresponsepojo.BulkO2CApprovalListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.BULK_O2C_APPRVL_LIST)
public class BulkO2CApprovalList extends BaseTest {
    static String moduleCode;
    BulkO2CApprovalListRequestPojo bulkO2CApprovalListRequestPojo = new BulkO2CApprovalListRequestPojo();
    BulkO2CApprovalListResponsePojo bulkO2CApprovalListResponsePojo = new BulkO2CApprovalListResponsePojo();
    OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();

    @DataProvider(name = "userData")
    public Object[][] TestDataFeed(){
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        int user=0;
        String geoDomCode=null;
        for(int i=1;i<=rowCount;i++){
            if(ExcelUtility.getCellData(0,ExcelI.CATEGORY_CODE,i).equals("BCU")){
                user++;
        }
    }
        ExcelUtility.setExcelFile(MasterSheetPath,ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
        int row= ExcelUtility.getRowCount();
        for(int i=1;i<=row;i++){
            if(ExcelUtility.getCellData(0,ExcelI.DOMAIN_TYPE_NAME,i).equals("Zone"))
                geoDomCode=ExcelUtility.getCellData(0,ExcelI.DOMAIN_CODE,i).toUpperCase();
        }

        Object[][] Data = new Object[user][8];
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
                Data[j][7] = geoDomCode;
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
    	bulkO2CApprovalListRequestPojo.setApprovalLevel(_masterVO.getProperty("Level1"));
    	bulkO2CApprovalListRequestPojo.setApprovalType(_masterVO.getProperty("O2CType"));
    	bulkO2CApprovalListRequestPojo.setCategory(_masterVO.getProperty("OptCategoryCodes"));
    	bulkO2CApprovalListRequestPojo.setDomain(_masterVO.getProperty("OptCategoryCodes"));
    	bulkO2CApprovalListRequestPojo.setGeographicalDomain(_masterVO.getProperty("OptCategoryCodes"));
		

	}

    @Test(dataProvider = "userData")
    public void  A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_01_Test_Success";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST1");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
       setupData();
       
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
       int messageCode = bulkO2CApprovalListResponsePojo.getStatus();
        Assert.assertEquals(200, messageCode);
        Assertion.assertEquals("200", String.valueOf(messageCode));
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }

    @Test(dataProvider = "userData")
    public void  A_02_Blank_AprvLevel(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_02_Blank_AprvLevel";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST2");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setApprovalLevel("");
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Invalid Approval Level .");
        Assertion.assertEquals(message, "Invalid Approval Level .");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_03_Blank_AprvType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_03_Blank_AprvType";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST3");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setApprovalType("");
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Approval Type should either FOC or O2C.");
        Assertion.assertEquals(message, "Approval Type should either FOC or O2C.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_04_Blank_Category(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_04_Blank_Category";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST4");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setCategory("");
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_05_Blank_Domain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_05_Blank_Domain";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST5");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setDomain("");
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_06_Blank_GeoDomain(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_05_Blank_Domain";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST6");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setGeographicalDomain("");
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.assertEquals(message, "Enter either msisdn or geographicalDomain,domain,category combination correctly.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_07_Invalid_AprvLevel(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_07_Invalid_AprvLevel";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST7");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        String level=new RandomGeneration().randomAlphabets(3);
        bulkO2CApprovalListRequestPojo.setApprovalLevel(level);
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Invalid Approval Level "+level+".");
        Assertion.assertEquals(message, "Invalid Approval Level "+level+".");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    @Test(dataProvider = "userData")
    public void  A_08_Invalid_AprvType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_08_Invalid_AprvType";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("BULKO2CAPRVLIST8");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        setupData();
       
        bulkO2CApprovalListRequestPojo.setApprovalType(new RandomGeneration().randomAlphabets(3));
       BulkO2CApprovalListAPI bulkO2CApprovalListAPI = new BulkO2CApprovalListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       bulkO2CApprovalListAPI.setContentType(_masterVO.getProperty("contentType"));
       bulkO2CApprovalListAPI.addBodyParam(bulkO2CApprovalListRequestPojo);
       bulkO2CApprovalListAPI.setExpectedStatusCode(200);
	   bulkO2CApprovalListAPI.perform();
	   bulkO2CApprovalListResponsePojo = bulkO2CApprovalListAPI.getAPIResponseAsPOJO(BulkO2CApprovalListResponsePojo.class);
        String message = bulkO2CApprovalListResponsePojo.getMessage();
        Assert.assertEquals(message, "Approval Type should either FOC or O2C.");
        Assertion.assertEquals(message, "Approval Type should either FOC or O2C.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }
    
    
    



}
