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

import restassuredapi.api.approvalSuspendChUAPI.ApprovalSuspendChUAPI;
import restassuredapi.api.channelUserListByStatus.ChannelUserListByStatusAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.pojo.approvalSuspendReqpojo.ApprovalSuspendReqpojo;
import restassuredapi.pojo.approvalSuspendRespojo.ApprovalSuspendRespojo;
import restassuredapi.pojo.channelUserListBystatus.requestpojo.ChannelUserByStatusReqPojo;
import restassuredapi.pojo.channelUserListBystatus.responsepojo.ChannelUserByStatusResPojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;

@ModuleManager(name = Module.CHANNELUSER_SR_SUSPEND_API)
public class ApprovalSuspendChannelUser  extends BaseTest {
	
	ApprovalSuspendReqpojo  approvalSuspendReqpojo=new ApprovalSuspendReqpojo();
	ApprovalSuspendRespojo approvalSuspendRespojo=new ApprovalSuspendRespojo();
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
	public ApprovalSuspendReqpojo getReqBody(String action,String loginId,String remarks,String RequestType) {
		ApprovalSuspendReqpojo  approvalSuspendReqpojo=new ApprovalSuspendReqpojo();
		approvalSuspendReqpojo.setAction(action);
		approvalSuspendReqpojo.setLoginId(loginId);
		approvalSuspendReqpojo.setRemarks(remarks);
        approvalSuspendReqpojo.setRequestType(RequestType);			
			return approvalSuspendReqpojo;
		}
		
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16209")
	public void A_01_Test_success_suspendfromSR(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_01_Test_success_suspendfromSR";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","AUT_96467" ,"Automation For suspend user","SUSPENDAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(200);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "200");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16210")
	public void A_02_Test_Invalid_loginId(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_02_Test_Invalid_loginId";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","AUT_96ddssds" ,"Automation For suspend user","SUSPENDAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16211")
	public void A_03_Test_Empty_loginId(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_03_Test_Empty_loginId";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","" ,"Automation For suspend user","SUSPENDAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16212")
	public void A_04_Test_Empty_Remarks(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_04_Test_Empty_Remarks";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API04");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","AUT_96467" ,"","SUSPENDAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16213")
	public void A_05_Test_Invalid_RequestType(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_05_Test_Invalid_RequestType";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API05");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","AUT_96467" ,"Automation For suspend user","jnndsksaknd");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16214")
	public void A_06_Test_Invalid_Action(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_06_Test_Invalid_Action";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_SR_SUSPEND_API06");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("vasdsadsa","AUT_96467" ,"Automation For suspend user","SUSPENDAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16287")
	public void A_01_Test_success_deletefromDR(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_01_Test_success_deletefromDR";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_DR_DELETE_API01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("A","AUT_87570" ,"Automation For delete user","DELETEAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(200);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "200");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16286")
	public void A_02_Test_Invalid_Action_Other_Than_A_R(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_02_Test_Invalid_Action_Other_Than_A_R";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("CHANNELUSER_DR_DELETE_API02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		approvalSuspendReqpojo=getReqBody("G","AUT_96467" ,"Automation For suspend user","DELETEAPPROVAL");
		ApprovalSuspendChUAPI api=new  ApprovalSuspendChUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER), accessToken);
		api.setContentType(_masterVO.getProperty("contentType"));
		api.addBodyParam(approvalSuspendReqpojo);
		api.setExpectedStatusCode(400);
		api.perform();
		approvalSuspendRespojo=api.getAPIResponseAsPOJO(ApprovalSuspendRespojo.class);
		String status=approvalSuspendRespojo.getStatus();
		
	    Assertion.assertEquals(status, "400");
	    Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
}
