package restassuredapi.test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccess;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import restassuredapi.api.o2CSearchDetailsApi.O2CSearchDetailsApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2CSearchDetailsRequestpojo.Data;
import restassuredapi.pojo.o2CSearchDetailsRequestpojo.SearchDetailsRequestPojo;
import restassuredapi.pojo.o2CSearchDetailsResponsepojo.SearchDetailsResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ModuleManager(name = Module.O2C_GET_SEARCH_DETAILS)
public class O2CSearchDetailsTest extends BaseTest {

    static String moduleCode;
    SearchDetailsRequestPojo searchDetailsRequestPojo = new SearchDetailsRequestPojo();
    SearchDetailsResponsePojo searchDetailsResponsePojo = new SearchDetailsResponsePojo();
    OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
    Data data =new Data();
    String searchCategoryCode =null;
    
    @DataProvider(name = "userData")
    public Object[][] TestDataFed() {
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
        String DomainCode = null;
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.GEOGRAPHICAL_DOMAINS_SHEET);
        int rowCount2 = ExcelUtility.getRowCount();
        for (int i = 1; i <= rowCount2; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.DOMAIN_TYPE_NAME, i).equals(GeoDomainName))
                DomainCode = ExcelUtility.getCellData(0, ExcelI.DOMAIN_CODE, i).toUpperCase();
        }
        String searchCategoryCode = _masterVO.getProperty("categoryCode");
        ArrayList<String> opUserData =new ArrayList<String>();
        Map<String, String> userInfo = UserAccess.getUserWithAccesswithCategorywithDomain(RolesI.O2C_TRANSFER_REVAMP,searchCategoryCode);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("MSISDN"));
        opUserData.add(userInfo.get("USER_NAME"));
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
                Data[j][7] = DomainCode;
                Data[j][8] = opUserData.get(0);
                Data[j][9] = opUserData.get(1);
                Data[j][10] = opUserData.get(2);
                Data[j][11] = opUserData.get(3);
               
                j++;
            }
          
        }
        return Data;
    }

    public void setupData(){
      data.setChannelDomain("");
      data.setChannelOwnerCategory("");
      data.setChannelOwnerName("");
      data.setChannelOwnerUserID("");
      data.setChannelUserID("");
      data.setGeoDomainCode("");
      data.setUserCategory("");
      searchDetailsRequestPojo.setData(data);
    }

    Map<String, Object> headerMap = new HashMap<String, Object>();

    public void setHeaders() {
    	String searchCategoryCode = _masterVO.getProperty("categoryCode");
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
    public void A_01_Test_Success_LoginId(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String domainCode, String sCategoryName, String sLoginId, String sMSISDN, String userName) throws Exception{
        final String methodName = "A_01_Test_Success_LoginId";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CSD01");
        moduleCode = caseMaster.getModuleCode();

        currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(),categoryName));

        currentNode.assignCategory("REST");

        setupData();
        O2CSearchDetailsApi o2CSearchDetailsApi = new O2CSearchDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
        o2CSearchDetailsApi.setContentType(_masterVO.getProperty("contentType"));
        o2CSearchDetailsApi.addBodyParam(searchDetailsRequestPojo);
        o2CSearchDetailsApi.setIdentifierType("LOGINID");
        o2CSearchDetailsApi.setIdentifierValue(sLoginId);
        o2CSearchDetailsApi.setExpectedStatusCode(200);
        o2CSearchDetailsApi.perform();

        searchDetailsResponsePojo = o2CSearchDetailsApi.getAPIResponseAsPOJO(SearchDetailsResponsePojo.class);
        String status = searchDetailsResponsePojo.getStatus();
        Assert.assertEquals(200, Integer.parseInt(status));
        Assertion.assertEquals(status, "200");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    public void A_02_Test_Success_MSISDN(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String domainCode, String sCategoryName, String sLoginId, String sMSISDN, String userName) throws Exception{
        final String methodName = "A_02_Test_Success_MSISDN";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CSD02");
        moduleCode = caseMaster.getModuleCode();

        currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(),categoryName));

        currentNode.assignCategory("REST");

        setupData();
        O2CSearchDetailsApi o2CSearchDetailsApi = new O2CSearchDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
        o2CSearchDetailsApi.setContentType(_masterVO.getProperty("contentType"));
        o2CSearchDetailsApi.addBodyParam(searchDetailsRequestPojo);
        o2CSearchDetailsApi.setIdentifierType("MSISDN");
        o2CSearchDetailsApi.setIdentifierValue(sMSISDN);
        o2CSearchDetailsApi.setExpectedStatusCode(200);
        o2CSearchDetailsApi.perform();

        searchDetailsResponsePojo = o2CSearchDetailsApi.getAPIResponseAsPOJO(SearchDetailsResponsePojo.class);
        String status = searchDetailsResponsePojo.getStatus();
        Assert.assertEquals(200, Integer.parseInt(status));
        Assertion.assertEquals(status, "200");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    public void A_03_Test_Success_UserName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String domainCode, String sCategoryName, String sLoginId, String sMSISDN, String userName) throws Exception{
        final String methodName = "A_03_Test_Success_UserName";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CSD03");
        moduleCode = caseMaster.getModuleCode();

        currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(),categoryName));

        currentNode.assignCategory("REST");

        String userID = DBHandler.AccessHandler.getUserId(userName);
        setupData();
        data.setChannelDomain(searchCategoryCode);
        data.setChannelOwnerCategory(searchCategoryCode);
        data.setChannelOwnerUserID(userID);
        data.setChannelUserID(userID);
        data.setGeoDomainCode(domainCode);
        data.setUserCategory(searchCategoryCode);
        searchDetailsRequestPojo.setData(data);
        O2CSearchDetailsApi o2CSearchDetailsApi = new O2CSearchDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
        o2CSearchDetailsApi.setContentType(_masterVO.getProperty("contentType"));
        o2CSearchDetailsApi.addBodyParam(searchDetailsRequestPojo);
        o2CSearchDetailsApi.setIdentifierType("USERNAME");
        o2CSearchDetailsApi.setIdentifierValue(userName);
        o2CSearchDetailsApi.setExpectedStatusCode(200);
        o2CSearchDetailsApi.perform();

        searchDetailsResponsePojo = o2CSearchDetailsApi.getAPIResponseAsPOJO(SearchDetailsResponsePojo.class);
        String status = searchDetailsResponsePojo.getStatus();
        Assert.assertEquals(200, Integer.parseInt(status));
        Assertion.assertEquals(status, "200");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    public void A_04_Test_Blank_identifierValue(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String domainCode, String sCategoryName, String sLoginId, String sMSISDN, String userName) throws Exception{
        final String methodName = "A_04_Test_Blank_identifierValue";
        Log.startTestCase(methodName);
        if(_masterVO.getProperty("identifierType").equals("loginid"))
            BeforeMethod(loginID, password,categoryName);
        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
            BeforeMethod(msisdn, PIN,categoryName);
        CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CSD04");
        moduleCode = caseMaster.getModuleCode();

        currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(),categoryName));

        currentNode.assignCategory("REST");

        setupData();
        O2CSearchDetailsApi o2CSearchDetailsApi = new O2CSearchDetailsApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
        o2CSearchDetailsApi.setContentType(_masterVO.getProperty("contentType"));
        o2CSearchDetailsApi.addBodyParam(searchDetailsRequestPojo);
        o2CSearchDetailsApi.setIdentifierType("723284661328421");
        o2CSearchDetailsApi.setIdentifierValue(" ");
        o2CSearchDetailsApi.setExpectedStatusCode(400);
        o2CSearchDetailsApi.perform();

        searchDetailsResponsePojo = o2CSearchDetailsApi.getAPIResponseAsPOJO(SearchDetailsResponsePojo.class);
        String messageCode = searchDetailsResponsePojo.getMessageCode();
        Assert.assertEquals(241137, Integer.parseInt(messageCode));
        Assertion.assertEquals(messageCode, "241137");
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
}
