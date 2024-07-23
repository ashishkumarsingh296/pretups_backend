package restassuredapi.test;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.RolesI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.GenerateMSISDN;
import com.utils.Log;
import com.utils.RandomGeneration;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.getparentcatlistApi.GetParentCatListApi;
import restassuredapi.api.o2ctxnrevlist.O2cTxnRevListApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.getparentcatlistrequestpojo.GetParentCatListResponsePojo;
import restassuredapi.pojo.o2ctxnrevlistresponsepojo.O2cTxnRevListResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.PARENT_CAT_LIST)
public class ParentCatList extends BaseTest {
	 DateFormat df = null;
     Date dateobj = null;
     String currentDate=null;
	static String moduleCode;

	GetParentCatListResponsePojo responsePojo = new GetParentCatListResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	Login login = new Login();
	
	RandomGeneration randStr = new RandomGeneration();
	GenerateMSISDN gnMsisdn = new GenerateMSISDN();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
	
	@DataProvider(name = "userData")
    public Object[][] TestDataFeed() {
        ArrayList<String> opUserData =new ArrayList<String>();
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        
        Object[][] Data =new Object[rowCount+1][4];
        for (int i = 1; i <= rowCount; i++) {
        	Data[i][0]=ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
        	Data[i][1]=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
        	Data[i][2]=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
        	Data[i][3]=ExcelUtility.getCellData(0, ExcelI.PIN, i);
        }
        
        
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));
        
        for(int i=0 ; i<Data[0].length ; i++) {
        	Data[0][i] = opUserData.get(i);
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


	// Successful data with valid data.
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15702")
	public void A_01_Test_success(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(LoginId, Password, CategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PARENTCATLIST01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
		currentNode.assignCategory("REST");

		GetParentCatListApi getParentCatListApi = new GetParentCatListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getParentCatListApi.setContentType(_masterVO.getProperty("contentType"));
		getParentCatListApi.setCategoryCode("AG");
		getParentCatListApi.setExpectedStatusCode(200);
		getParentCatListApi.perform();
		responsePojo = getParentCatListApi
				.getAPIResponseAsPOJO(GetParentCatListResponsePojo.class);
		String status = responsePojo.getStatus();
		if(status == "200")
		Assert.assertEquals(200, status);
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15703")
	public void A_02_Test_Empty_catCode(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
		final String methodName = "A_02_Test_Empty_catCode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(LoginId, Password, CategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PARENTCATLIST01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
		currentNode.assignCategory("REST");

		GetParentCatListApi getParentCatListApi = new GetParentCatListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getParentCatListApi.setContentType(_masterVO.getProperty("contentType"));
		getParentCatListApi.setCategoryCode(null);
		getParentCatListApi.setExpectedStatusCode(400);
		getParentCatListApi.perform();
		responsePojo = getParentCatListApi
				.getAPIResponseAsPOJO(GetParentCatListResponsePojo.class);
		String status = responsePojo.getStatus();
		if(status == "400")
		Assert.assertEquals(400, status);
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-15704")
	public void A_03_Test_Invalid_catCode(String CategoryName,String LoginId,String Password,String Pin) throws Exception {
		final String methodName = "A_03_Test_Invalid_catCode";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(LoginId, Password, CategoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PARENTCATLIST02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),CategoryName));
		currentNode.assignCategory("REST");

		GetParentCatListApi getParentCatListApi = new GetParentCatListApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		getParentCatListApi.setContentType(_masterVO.getProperty("contentType"));
		getParentCatListApi.setCategoryCode("SE12");
		getParentCatListApi.setExpectedStatusCode(400);
		getParentCatListApi.perform();
		responsePojo = getParentCatListApi
				.getAPIResponseAsPOJO(GetParentCatListResponsePojo.class);
		String status = responsePojo.getStatus();
		if(status == "400")
		Assert.assertEquals(400, status);
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	
	

	
}
