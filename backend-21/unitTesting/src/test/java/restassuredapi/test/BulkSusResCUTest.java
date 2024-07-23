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
import restassuredapi.api.bulkSusResCUAPI.BulkSusResCUAPI;
import restassuredapi.api.oauthentication.OAuthenticationAPI;
import restassuredapi.api.reprintVoucher.ReprintVoucher;
import restassuredapi.pojo.approvalSuspendReqpojo.ApprovalSuspendReqpojo;
import restassuredapi.pojo.approvalSuspendRespojo.ApprovalSuspendRespojo;
import restassuredapi.pojo.bulkSusResCURequestPojo.BulkSusResCURequestPojo;
import restassuredapi.pojo.bulkSusResCUResponsePojo.BulkSusResCUResponsePojo;
import restassuredapi.pojo.oauthenticationrequestpojo.OAuthenticationRequestPojo;
import restassuredapi.pojo.oauthenticationresponsepojo.OAuthenticationResponsePojo;


@ModuleManager(name = Module.BULK_SUS_RES_CHANNEL_USER_API)
public class BulkSusResCUTest extends BaseTest{
	static String moduleCode;
	
	BulkSusResCURequestPojo bulkSusResCURequestPojo=new BulkSusResCURequestPojo();
	BulkSusResCUResponsePojo bulkSusResCUResponsePojo=new BulkSusResCUResponsePojo();
	OAuthenticationRequestPojo oAuthenticationRequestPojo= new OAuthenticationRequestPojo();
	OAuthenticationResponsePojo oAuthenticationResponsePojo = new OAuthenticationResponsePojo();
	
	
	
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
	
	public BulkSusResCURequestPojo getReqBody(String file,String fileName,String fileType,String operationType) {
//		ApprovalSuspendReqpojo  approvalSuspendReqpojo=new ApprovalSuspendReqpojo();
//		approvalSuspendReqpojo.setAction(action);
//		approvalSuspendReqpojo.setLoginId(loginId);
//		approvalSuspendReqpojo.setRemarks(remarks);
//        approvalSuspendReqpojo.setRequestType(RequestType);			
//			return approvalSuspendReqpojo;
		BulkSusResCURequestPojo bulkSusResCURequestPojo = new BulkSusResCURequestPojo();
		bulkSusResCURequestPojo.setFile(file);
		bulkSusResCURequestPojo.setFileName(fileName);
		bulkSusResCURequestPojo.setFileType(fileType);
		bulkSusResCURequestPojo.setOperationType(operationType);
		return bulkSusResCURequestPojo;
	}
	

	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16285")
	public void A_01_Test_success_bulkSusResCU_with_msisdn(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_01_Test_success_suspendfromSR";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RES_SUS_BULK_CU_API01");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		BulkSusResCUAPI bulkSusResCUAPI  = new BulkSusResCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		bulkSusResCUAPI.setContentType(_masterVO.getProperty("contentType"));
		bulkSusResCUAPI.setType("MSISDN");
		
		bulkSusResCURequestPojo=getReqBody("NzI0MjM2Njg5Mjk0OTYzLDcyMTI3OTk5OTIxMjQxNA==","strig1382362","txt","SR");
		bulkSusResCUAPI.addBodyParam(bulkSusResCURequestPojo);
		bulkSusResCUAPI.setExpectedStatusCode(200);
		bulkSusResCUAPI.perform();
		bulkSusResCUResponsePojo=bulkSusResCUAPI.getAPIResponseAsPOJO(BulkSusResCUResponsePojo.class);
		
		String status=Integer.toString(bulkSusResCUResponsePojo.getStatus());
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16283")
	public void A_02_Test_success_bulkSusResCU_with_loginid(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_02_Test_success_bulkSusResCU_with_loginid";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RES_SUS_BULK_CU_API02");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		BulkSusResCUAPI bulkSusResCUAPI  = new BulkSusResCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		bulkSusResCUAPI.setContentType(_masterVO.getProperty("contentType"));
		bulkSusResCUAPI.setType("LOGIN");
		
		bulkSusResCURequestPojo=getReqBody("QVVUXzk4NDM4LEFVVF8yODMwMA==","strig14572154","txt","RR");
		bulkSusResCUAPI.addBodyParam(bulkSusResCURequestPojo);
		bulkSusResCUAPI.setExpectedStatusCode(200);
		bulkSusResCUAPI.perform();
		bulkSusResCUResponsePojo=bulkSusResCUAPI.getAPIResponseAsPOJO(BulkSusResCUResponsePojo.class);
		
		String status=Integer.toString(bulkSusResCUResponsePojo.getStatus());
		Assertion.assertEquals(status, "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
	
	@Test(dataProvider = "userData")
	//@TestManager(TestKey="PRETUPS-16284")
	public void A_03_Test_fail_bulkSusResCU_with_mix_loginid_and_msisdn(String loginid,String password,String categoryName) throws Exception {
		final String methodName = "A_03_Test_fail_bulkSusResCU_with_mix_loginid_and_msisdn";
		Log.startTestCase(methodName);
	    BeforeMethod(loginid, password, categoryName);
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RES_SUS_BULK_CU_API03");
		moduleCode = CaseMaster.getModuleCode();
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		BulkSusResCUAPI bulkSusResCUAPI  = new BulkSusResCUAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST_SWAGGER),
				accessToken);
		bulkSusResCUAPI.setContentType(_masterVO.getProperty("contentType"));
		bulkSusResCUAPI.setType("MSISDN");
		
		bulkSusResCURequestPojo=getReqBody("NzI0MjM2Njg5Mjk0OTYzLEFVVF8yODMwMA==","strig9753514","txt","SR");
		bulkSusResCUAPI.addBodyParam(bulkSusResCURequestPojo);
		bulkSusResCUAPI.setExpectedStatusCode(400);
		bulkSusResCUAPI.perform();
		bulkSusResCUResponsePojo=bulkSusResCUAPI.getAPIResponseAsPOJO(BulkSusResCUResponsePojo.class);
		
		String status=Integer.toString(bulkSusResCUResponsePojo.getStatus());
		Assertion.assertEquals(status, "400");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
	}
}
