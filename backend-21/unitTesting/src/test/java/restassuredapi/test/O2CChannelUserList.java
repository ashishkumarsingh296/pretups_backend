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
import restassuredapi.api.o2cChannelUserListApi.O2cChannelUserListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.channelUserListResponsepojo.ChannelUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.O2C_CHANNEL_USER_LIST)
public class O2CChannelUserList extends BaseTest {
    static String moduleCode;
    ChannelUserListResponsePojo channelUserListResponsePojo = new ChannelUserListResponsePojo();
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

    @Test(dataProvider = "userData")
    public void  A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_01_Test_Success";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
       O2cChannelUserListApi o2cChannelUserListApi = new O2cChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
       o2cChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
       o2cChannelUserListApi.setExpectedStatusCode(200);
       o2cChannelUserListApi.setDomainCode(_masterVO.getProperty("categoryCode"));
       o2cChannelUserListApi.setCategoryCode(_masterVO.getProperty("categoryCode"));
       o2cChannelUserListApi.setChannelOwnerCategory(_masterVO.getProperty("categoryCode"));
       o2cChannelUserListApi.setChannelOwnerCategoryUserID("NA");
       o2cChannelUserListApi.setGeoDomainCode(GeoDomainCode);
       o2cChannelUserListApi.setUserName(_masterVO.getProperty("userName"));
       o2cChannelUserListApi.perform();
       channelUserListResponsePojo = o2cChannelUserListApi.getAPIResponseAsPOJO(ChannelUserListResponsePojo.class);
       String messageCode = channelUserListResponsePojo.getStatus();
        Assert.assertEquals(200, Integer.parseInt(messageCode));
        Assertion.assertEquals(messageCode, "200");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
      }

    @Test(dataProvider = "userData")
    public void  A_02_Test_Invalid_Domain_Code(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_02_Test_Invalid_Domain_Code";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        O2cChannelUserListApi o2cChannelUserListApi = new O2cChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
        o2cChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
        o2cChannelUserListApi.setExpectedStatusCode(200);
        o2cChannelUserListApi.setDomainCode(new RandomGeneration().randomAlphaNumeric(4));
        o2cChannelUserListApi.setCategoryCode(_masterVO.getProperty("categoryCode"));
        o2cChannelUserListApi.setChannelOwnerCategory(_masterVO.getProperty("categoryCode"));
        o2cChannelUserListApi.setChannelOwnerCategoryUserID("NA");
        o2cChannelUserListApi.setGeoDomainCode(GeoDomainCode);
        o2cChannelUserListApi.setUserName(_masterVO.getProperty("userName"));
        o2cChannelUserListApi.perform();
        channelUserListResponsePojo = o2cChannelUserListApi.getAPIResponseAsPOJO(ChannelUserListResponsePojo.class);
        String message =channelUserListResponsePojo.getMessage();
        Assert.assertEquals(message, "Domain Entered is invalid.");
        Assertion.assertEquals(message, "Domain Entered is invalid.");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    public void  A_03_Test_Invalid_ChannelOwnerCategory(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String GeoDomainCode) throws Exception{
        final String methodName = "A_03_Test_Invalid_ChannelOwnerCategory";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("O2CINIOPT1");
        moduleCode = CaseMaster.getModuleCode();
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
        currentNode.assignCategory("REST");
        O2cChannelUserListApi o2cChannelUserListApi = new O2cChannelUserListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);
        o2cChannelUserListApi.setContentType(_masterVO.getProperty("contentType"));
        o2cChannelUserListApi.setExpectedStatusCode(200);
        o2cChannelUserListApi.setDomainCode(_masterVO.getProperty("domainCode"));
        o2cChannelUserListApi.setCategoryCode(_masterVO.getProperty("categoryCode"));
        o2cChannelUserListApi.setChannelOwnerCategory(new RandomGeneration().randomAlphaNumeric(4));
        o2cChannelUserListApi.setChannelOwnerCategoryUserID("NA");
        o2cChannelUserListApi.setGeoDomainCode(GeoDomainCode);
        o2cChannelUserListApi.setUserName(_masterVO.getProperty("userName"));
        o2cChannelUserListApi.perform();
        channelUserListResponsePojo = o2cChannelUserListApi.getAPIResponseAsPOJO(ChannelUserListResponsePojo.class);
        String message =channelUserListResponsePojo.getMessage();
        Assert.assertEquals(message, "Channel owner category does not exists .");
        Assertion.assertEquals(message, "Channel owner category does not exists .");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



}