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
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.getgeodomaincategory.GetGeoDomainCatApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.geodomaincatresponsepojo.Geodomaincatresponsepojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.GEODOMAINDETAILS)
public class GetGeoDomainCatDetails extends BaseTest{


	static String moduleCode;
	Geodomaincatresponsepojo getDomainCategoryResponsePojo = new Geodomaincatresponsepojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Map<String, Object> headerMap = new HashMap<>();
	

		@DataProvider(name ="userData")
		public Object[][] TestDataFeed(){
			String MasterSheetPath = _masterVO.getProperty("DataProvider");
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();

			Object[][] Data = new Object[rowCount][7];
			int j=0;
			for(int i=1;i<=rowCount;i++) {
				Data[j][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i);
				Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
				Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
				Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
				Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
				Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
				
				j++;
			}
			return Data;


		}

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
	public void A_01_Test_getDomainCategory_positive(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception
	{
		final String methodName = "A_01_Test_getDomainCategory_positive";
		Log.startTestCase(methodName);
		 if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDDC1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		
		GetGeoDomainCatApi getDomainCategoryAPI = new GetGeoDomainCatApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		getDomainCategoryAPI.setExpectedStatusCode(200);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(Geodomaincatresponsepojo.class);
		int statusCode = Integer.parseInt(getDomainCategoryResponsePojo.getStatus());

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
			
	}
	

	@Test(dataProvider = "userData")
	public void A_02_Test_invalid_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_02_Test_invalid_token";
		Log.startTestCase(methodName);
		 if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDDC2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");

		GetGeoDomainCatApi getDomainCategoryAPI = new GetGeoDomainCatApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), new RandomGeneration().randomAlphabets(6)+accessToken);
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		getDomainCategoryAPI.setExpectedStatusCode(200);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(Geodomaincatresponsepojo.class);
		String message =getDomainCategoryResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "241018");
		Assertion.assertEquals(message, "241018");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	/*
	@Test(dataProvider="userData")
	public void A_03_Test_blank_token(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_03_Test_blank_token";
		Log.startTestCase(methodName);
		 if(_masterVO.getProperty("identifierType").equals("loginid"))
	            BeforeMethod(loginID, password,categoryName);
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	            BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTGDDC3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));   
		currentNode.assignCategory("REST");
		//setupData();
		GetGeoDomainCatApi getDomainCategoryAPI = new GetGeoDomainCatApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER)," ");
		getDomainCategoryAPI.setContentType(_masterVO.getProperty("contentType"));
		getDomainCategoryAPI.setExpectedStatusCode(200);
		getDomainCategoryAPI.perform();
		getDomainCategoryResponsePojo = getDomainCategoryAPI
				.getAPIResponseAsPOJO(Geodomaincatresponsepojo.class);
		String message =getDomainCategoryResponsePojo.getMessageCode();
		
		Assert.assertEquals(message, "241023");
		Assertion.assertEquals(message, "241023");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}	
	
	*/
	
}
