package restassuredapi.test;

import java.text.MessageFormat;
import java.util.*;

import org.junit.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2ssubserviceslist.C2SSubServicesListAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.c2ssubservicesresponsepojo.C2SSubServicesResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.REST_C2S_SUBSERVICES_LIST)
public class C2SSubServicesList extends BaseTest {
	static String moduleCode;
	C2SSubServicesResponsePojo c2SSubServicesResponsePojo = new C2SSubServicesResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	Login login = new Login();
	HashMap<String,String> transferDetails=new HashMap<String,String>();
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
	@TestManager(TestKey="PRETUPS-6664")
	public void A_01_Test_Success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_C2SSubServicesListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.C2S_SERVICES_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Set<String> serviceType = new HashSet<>();
		for(int i=1;i<=rowCount;i++) {
			serviceType.add(ExcelUtility.getCellData(0, ExcelI.SERVICE_TYPE, i));
		}

		 for (String value : serviceType) {	

			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SSUBSERVICESLIST1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), categoryName,value.toString()));
			currentNode.assignCategory("REST");

			C2SSubServicesListAPI c2SSubServicesListAPI = new C2SSubServicesListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);

			c2SSubServicesListAPI.setContentType(_masterVO.getProperty("contentType"));
			c2SSubServicesListAPI.setServiceTypeCode(value.toString());
			c2SSubServicesListAPI.setExpectedStatusCode(200);
			c2SSubServicesListAPI.perform();

			c2SSubServicesResponsePojo = c2SSubServicesListAPI.getAPIResponseAsPOJO(C2SSubServicesResponsePojo.class);

			Boolean statusCode = false;

			if (c2SSubServicesResponsePojo != null && c2SSubServicesResponsePojo.getMessage() != null && c2SSubServicesResponsePojo.getMessage().contains("Success"))
				statusCode = true;
			else
				statusCode = false;
			Assert.assertEquals(true, statusCode);
			Assertion.assertEquals("Success", c2SSubServicesResponsePojo.getMessage());

			Log.endTestCase(methodName);
		}
	}
	
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-6665")
	public void A_02_Test_Success_ALL(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {

		final String methodName = "Test_C2SSubServicesListAPI";
		Log.startTestCase(methodName);
		if(_masterVO.getProperty("identifierType").equals("loginid"))
			BeforeMethod(loginID, password,categoryName);
		else if(_masterVO.getProperty("identifierType").equals("msisdn"))
			BeforeMethod(msisdn, PIN,categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("C2SSUBSERVICESLIST2");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");

		C2SSubServicesListAPI c2SSubServicesListAPI = new C2SSubServicesListAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),accessToken);

		c2SSubServicesListAPI.setContentType(_masterVO.getProperty("contentType"));
		c2SSubServicesListAPI.setServiceTypeCode(_masterVO.getProperty("scope"));
		
		c2SSubServicesListAPI.setExpectedStatusCode(200);
		c2SSubServicesListAPI.perform();
		
		c2SSubServicesResponsePojo = c2SSubServicesListAPI.getAPIResponseAsPOJO(C2SSubServicesResponsePojo.class);

		Boolean statusCode = false;

		if (c2SSubServicesResponsePojo != null && c2SSubServicesResponsePojo.getMessage() != null && c2SSubServicesResponsePojo.getMessage().contains("Success"))
			statusCode = true;
		else 
			statusCode = false;

		Assert.assertEquals(true, statusCode);
		Assertion.assertEquals("Success", c2SSubServicesResponsePojo.getMessage());
		
		Log.endTestCase(methodName);
	}
}
