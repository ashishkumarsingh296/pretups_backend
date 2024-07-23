package restassuredapi.test;

import java.io.IOException;
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

import restassuredapi.api.o2cpurchaseorwithdrawuserlistapi.O2cPurchaseOrWithdrawUserListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.o2CSearchDetailsRequestpojo.Data;
import restassuredapi.pojo.o2cpurchaseorwithdrawuserlistresponsepojo.O2cPurchaseOrWithdrawUserListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.O2C_PURCHASE_OR_WITHDRAW_USER_LIST)
public class O2cPurchaseOrWithdrawUserListTest extends BaseTest{

	static String moduleCode;
	
	O2cPurchaseOrWithdrawUserListResponsePojo o2cPurcOrWithResponsePojo = new O2cPurchaseOrWithdrawUserListResponsePojo();
    OAuthenticationRequestPojo oAuthenticationRequestPojo = new OAuthenticationRequestPojo();
    OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
    RandomGeneration rnd = new RandomGeneration();
    Data data =new Data();
    String searchCategoryCode = null;
    @DataProvider(name = "userData")
    public Object[][] TestDataFed() {
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
    
    Map<String, Object> headerMap = new HashMap<String, Object>();

	public void setHeaders() {
		headerMap.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
		headerMap.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
		headerMap.put("requestGatewayCode", _masterVO.getProperty("requestGatewayCode"));
		headerMap.put("requestGatewayLoginId", _masterVO.getProperty("requestGatewayLoginID"));
		headerMap.put("requestGatewayPsecure", _masterVO.getProperty("requestGatewayPasswordVMS"));
		headerMap.put("requestGatewayType", _masterVO.getProperty("requestGatewayType"));
		headerMap.put("scope", _masterVO.getProperty("scope"));
		headerMap.put("servicePort", _masterVO.getProperty("servicePort"));
	}

	protected static String accessToken;

	public void setupAuth(String data1, String data2) {
		oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
		oAuthenticationRequestPojo.setIdentifierValue(data1);
		oAuthenticationRequestPojo.setPasswordOrSmspin(data2);
	}

	public void BeforeMethod(String data1, String data2, String categoryName) throws Exception {

		final String methodName = "Test_OAuthenticationTest";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("OAUTHETICATION1");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");

		setHeaders();
		setupAuth(data1, data2);
		OAuthenticationAPI oAuthenticationAPI = new OAuthenticationAPI(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), headerMap);
		oAuthenticationAPI.setContentType(_masterVO.getProperty("contentType"));
		oAuthenticationAPI.addBodyParam(oAuthenticationRequestPojo);
		oAuthenticationAPI.setExpectedStatusCode(200);
		oAuthenticationAPI.perform();
		oAuthenticationResponsePojo = oAuthenticationAPI.getAPIResponseAsPOJO(OAuthenticationResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		accessToken = oAuthenticationResponsePojo.getToken();
		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	public O2cPurchaseOrWithdrawUserListResponsePojo downloadPurchaseOrWithdrawUserList(String purchaseOrWithdraw, String geoDomainCode, 
			String domainCode, String categoryCode, String productCode, String walletTypeOpt )
			throws IOException {

		O2cPurchaseOrWithdrawUserListApi o2cPurchaseOrWithdrawUserListApi = new O2cPurchaseOrWithdrawUserListApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		o2cPurchaseOrWithdrawUserListApi.setContentType(_masterVO.getProperty("contentType"));
		o2cPurchaseOrWithdrawUserListApi.setPurchaseOrWithdraw(purchaseOrWithdraw);
		o2cPurchaseOrWithdrawUserListApi.setGeoDomainCode(geoDomainCode);
		o2cPurchaseOrWithdrawUserListApi.setDomainCode(domainCode);
		o2cPurchaseOrWithdrawUserListApi.setCategoryCode(categoryCode);
		o2cPurchaseOrWithdrawUserListApi.setProductCode(productCode);
		
		//wallet details: only for withdraw user list
		o2cPurchaseOrWithdrawUserListApi.setWalletTypeOpt(walletTypeOpt);
		o2cPurchaseOrWithdrawUserListApi.setExpectedStatusCode(200);
		o2cPurchaseOrWithdrawUserListApi.perform();

		o2cPurcOrWithResponsePojo = o2cPurchaseOrWithdrawUserListApi
				.getAPIResponseAsPOJO(O2cPurchaseOrWithdrawUserListResponsePojo.class);

		return o2cPurcOrWithResponsePojo;

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10152")
	public void A_01_Test_success_purchase(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_01_Test_success_purchase";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST1");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String purchaseOrWithdraw = _masterVO.getProperty("purchase");

		// get template api response
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, domainCode,categoryCode, productCode, walletType);
		int status = o2cPurcOrWithResponsePojo.getStatus();
		Assert.assertEquals(200, status);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10153")
	public void A_02_Test_success_withdraw(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_02_Test_success_withdraw";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST2");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		String purchaseOrWithdraw = _masterVO.getProperty("withdraw");

		// get template api response
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, domainCode,categoryCode, productCode, walletType);
		int status = o2cPurcOrWithResponsePojo.getStatus();
		Assert.assertEquals(200, status);
		Assertion.assertEquals(String.valueOf(status), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10154")
	public void A_03_Test_invalid_purachaseOrWithdrawParam(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_03_Test_invalid_purachaseOrWithdrawParam";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST3");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		
		String purchaseOrWithdraw = "";  //random value;
		if(!rnd.randomAlphaNumeric(1).equalsIgnoreCase("p") || !rnd.randomAlphaNumeric(1).equalsIgnoreCase("W")) {
			purchaseOrWithdraw = rnd.randomAlphaNumeric(1);  //random value;
		}
		Log.info("purchaseOrWithdraw: "+ purchaseOrWithdraw);
		// get template api response
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, domainCode,categoryCode, productCode, walletType);
		String message = o2cPurcOrWithResponsePojo.getMessage();
		Assert.assertEquals( message, "Invalid request param "+ purchaseOrWithdraw + ". " + "Please select P for purchase or W for withdraw.");
		Assertion.assertEquals( message, "Invalid request param "+ purchaseOrWithdraw + ". " + "Please select P for purchase or W for withdraw.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10155")
	public void A_04_Test_geoDomainCode_empty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_04_Test_geoDomainCode_empty";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST4");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		
		String purchaseOrWithdraw = "P";  
		String emptyParam = "geoDomainCode";
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, null, domainCode,categoryCode, productCode, walletType);
		String message = o2cPurcOrWithResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
		Assert.assertEquals( message, emptyParam +" is empty.");
		Assertion.assertEquals( message, emptyParam +" is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10156")
	public void A_05_Test_domainCode_empty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_05_Test_domainCode_empty";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST5");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		
		String purchaseOrWithdraw = "P";  
		String emptyParam = "domainCode";
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, null,categoryCode, productCode, walletType);
		String message = o2cPurcOrWithResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
		Assert.assertEquals( message, emptyParam +" is empty.");
		Assertion.assertEquals( message, emptyParam +" is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10157")
	public void A_06_Test_categoryCode_empty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_06_Test_categoryCode_empty";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST6");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		
		String purchaseOrWithdraw = "P";  
		String emptyParam = "categoryCode";
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, domainCode, null, productCode, walletType);
		String message = o2cPurcOrWithResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
		Assert.assertEquals( message, emptyParam +" is empty.");
		Assertion.assertEquals( message, emptyParam +" is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey = "PRETUPS-10158")
	public void A_07_Test_productCode_empty(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, 
			String categorCode, String geoDomainCode, String domainCode, String categoryCode, String productCode, String walletType) throws Exception 
	{
		final String methodName = "A_07_Test_productCode_empty";
		Log.startTestCase(methodName);
		Log.startTestCase(methodName);
		if (_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password, categoryName);
		else if (_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN, categoryName);
		CaseMaster caseMaster = _masterVO.getCaseMasterByID("O2CPURORWITHUSERLIST7");
		moduleCode = caseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(caseMaster.getExtentCase(), categoryName));
		currentNode.assignCategory("REST");
		
		String purchaseOrWithdraw = "P";  
		String emptyParam = "productCode";
		o2cPurcOrWithResponsePojo = downloadPurchaseOrWithdrawUserList(
				purchaseOrWithdraw, geoDomainCode, domainCode, categoryCode, null, walletType);
		String message = o2cPurcOrWithResponsePojo.getErrorMap().getMasterErrorList().get(0).getErrorMsg();
		Assert.assertEquals( message, emptyParam +" is empty.");
		Assertion.assertEquals( message, emptyParam +" is empty.");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}





}
