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
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.refreshtoken.RefreshTokenApi;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;
import restassuredapi.pojo.refreshtokenrequestpojo.RefreshTokenRequestPojo;
import restassuredapi.pojo.refreshtokenresponsepojo.RefreshTokenResponsePojo;



@ModuleManager(name = Module.REST_AUTO_COMPLETE_USER_DETAILS)
public class RefreshTokenTest extends BaseTest {

	static String moduleCode;
	RefreshTokenRequestPojo refreshTokenRequestPojo = new RefreshTokenRequestPojo();
	RefreshTokenResponsePojo refreshTokenResponsePojo = new RefreshTokenResponsePojo(); 
	 OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
     OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
    
    
HashMap<String, String> returnMap = new HashMap<String, String>();
Map<String, Object> headerMap = new HashMap<String, Object>();
Map<String, Object> headerMap1 = new HashMap<String, Object>();

	
	 @DataProvider(name = "userData")
     public Object[][] TestDataFeed1() {
            String MasterSheetPath = _masterVO.getProperty("DataProvider");
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
            int rowCountoperator = ExcelUtility.getRowCount();
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int rowCount = ExcelUtility.getRowCount();
            Object[][] Data = new Object[rowCount+rowCountoperator][7];
            int j=0;
            for (int i = 1; i <= rowCount; i++) {
                   Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                   Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                   Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                   Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                   Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                   Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                   Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                   j++;
                   }
    
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
            for (int i = 1; i <=rowCountoperator; i++) {
                   Data[j][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                   Data[j][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                   Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                   Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                   Data[j][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_NAME, i);
                   Data[j][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                   Data[j][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                   j++;
                   }            

            return Data;
     }
	 public void setupData(String token) {
			refreshTokenRequestPojo.setRefreshToken(token);
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
	    public void setHeaders1() {
	        headerMap1.put("CLIENT_ID", _masterVO.getProperty("CLIENT_ID"));
	        headerMap1.put("CLIENT_SECRET", _masterVO.getProperty("CLIENT_SECRET"));
	        headerMap1.put("scope", _masterVO.getProperty("scope"));
	    }
	    public void setupAuth(String data1, String data2) {
	        oAuthenticationRequestPojo.setIdentifierType(_masterVO.getProperty("identifierType"));
	        oAuthenticationRequestPojo.setIdentifierValue(data1);
	        oAuthenticationRequestPojo.setPasswordOrSmspin(data2);


	    }

	    // Successful data with valid data.

	    protected static String accessToken;


	    OAuthenticationResponsePojo BeforeMethod(String data1, String data2, String categoryName) throws Exception
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
			return oAuthenticationResponsePojo;


	    }


	@Test(dataProvider="userData")
	public void A_01_Test_success(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode) throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		
		 String token=null;
		 if(_masterVO.getProperty("identifierType").equals("loginid"))
	            token = BeforeMethod(loginID, password,categoryName).getRefreshToken();
	        else if(_masterVO.getProperty("identifierType").equals("msisdn"))
	        	token = BeforeMethod(msisdn, PIN,categoryName).getRefreshToken();
		 CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RT1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(),categoryName));
		currentNode.assignCategory("REST");
		setupData(token);
		setHeaders1();
		RefreshTokenApi refreshTokenApi = new RefreshTokenApi(
				_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),headerMap1);
		refreshTokenApi.setContentType(_masterVO.getProperty("contentType"));
		refreshTokenApi.addBodyParam(refreshTokenRequestPojo);
		refreshTokenApi.setExpectedStatusCode(200);
		refreshTokenApi.perform();
		refreshTokenResponsePojo = refreshTokenApi.getAPIResponseAsPOJO(RefreshTokenResponsePojo.class);
		long statusCode = oAuthenticationResponsePojo.getStatus();

		Assert.assertEquals(statusCode, 200);
		Assertion.assertEquals(Long.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}

}
