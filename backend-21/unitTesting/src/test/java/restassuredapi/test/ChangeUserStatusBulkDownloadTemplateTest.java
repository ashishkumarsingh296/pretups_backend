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
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.changeUserStatusDownloadTemplate.ChangeUserStatuBulkDownloadTemplateApi;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.changeUserStatusDownloadTemplateResponsePojo.CngStatusDwnTemplateResponsepojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.CHANGE_USER_STATUS_DOWNLOAD_TEMPLATE)
public class ChangeUserStatusBulkDownloadTemplateTest  extends BaseTest {

	
   CngStatusDwnTemplateResponsepojo cngStatusDwnTemplateResponsepojo=new CngStatusDwnTemplateResponsepojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	static String moduleCode;
	
	
	@DataProvider(name ="userData")
	public Object[][] TestDataFeed() {
		 String MasterSheetPath = _masterVO.getProperty("DataProvider");
		 ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
         int OperatorRowCountAdmin = ExcelUtility.getRowCount();
         Object[][] Data = new Object[1][3];
         int k=0;
         for (int i = 1; i < OperatorRowCountAdmin; i++) {
        	    
                String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
                String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                if (CategoryName.equals("BCU") && (!LoginID.equals(null) || !LoginID.equals(""))) {
                	Data[k][0]= ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                	Data[k][1]= ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);      
                	Data[k][2]= ExcelUtility.getCellData(0,  ExcelI.CATEGORY_NAME, i);
                	k++;
                	break;
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
		headerMap.put("requestGatewayPsecure","1357");
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
	
		
	@Test(dataProvider = "userData")
	@TestManager(TestKey="PRETUPS-16361")
	public void A_01_Test_DownloadTemplate(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_01_Test_DownloadTemplate";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANGE_USER_STATUS_TEMPLATE_DOWNLOAD_API01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
	
		ChangeUserStatuBulkDownloadTemplateApi api=new  ChangeUserStatuBulkDownloadTemplateApi(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		
		api.setExpectedStatusCode(200);
		api.perform();
		cngStatusDwnTemplateResponsepojo=api.getAPIResponseAsPOJO(CngStatusDwnTemplateResponsepojo.class);
	    String status=String.valueOf(cngStatusDwnTemplateResponsepojo.getStatus());
	    Assertion.assertEquals(status, "200");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	

	
}
