package restassuredapi.test;

import java.util.HashMap;

import org.bson.assertions.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cvouchersegment.C2CVoucherSegmentAPI;
import restassuredapi.pojo.c2cbuyvouchersegmentinforequestpojo.C2CBuyVoucherSegmentInfoRequestPojo;
import restassuredapi.pojo.c2cbuyvouchersegmentinforequestpojo.SegmentData;
import restassuredapi.pojo.c2cbuyvouchersegmentinforesponsepojo.C2CBuyVoucherSegmentInfoResponsePojo;




@ModuleManager(name = Module.REST_VOUCHER_SEGMENT)
public class C2CBuyVoucherSegmentInfo extends BaseTest {
		 
	static String moduleCode;
	C2CBuyVoucherSegmentInfoRequestPojo c2cBuyVoucherSegmentInfoRequestPojo = new C2CBuyVoucherSegmentInfoRequestPojo();
	C2CBuyVoucherSegmentInfoResponsePojo c2cBuyVoucherSegmentInfoResponsePojo = new C2CBuyVoucherSegmentInfoResponsePojo();
	
	
	Login login = new Login();
	
	HashMap<String,String> transferDetails=new HashMap<String,String>(); 

	public void getExcelData(){
		

	    String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int OperatorRowCount = ExcelUtility.getRowCount();
        for (int i = 1; i < OperatorRowCount; i++) {
               String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
               String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
               if (CategoryName.equals("DIST") && (!LoginID.equals(null) || !LoginID.equals(""))) {
             	  transferDetails.put("LOGIN_ID", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
             	  transferDetails.put("PASSWORD", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
                     transferDetails.put("MSISDN", ExcelUtility.getCellData(0, ExcelI.MSISDN, i));
               }
        }
        
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.OPERATOR_USERS_HIERARCHY_SHEET);
        int OperatorRowCountAdmin = ExcelUtility.getRowCount();
        for (int i = 1; i < OperatorRowCountAdmin; i++) {
               String CategoryName = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, i);
               String LoginID = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
               if (CategoryName.equals("SUADM") && (!LoginID.equals(null) || !LoginID.equals(""))) {
             	  transferDetails.put("LOGIN_ID_ADMIN", ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i));
             	  transferDetails.put("PASSWORD_ADMIN", ExcelUtility.getCellData(0, ExcelI.PASSWORD, i));
               }
        }

	}
	
	public void setupData() {
		
	getExcelData();
	
	c2cBuyVoucherSegmentInfoRequestPojo.setIdentifierType(transferDetails.get("LOGIN_ID"));
	c2cBuyVoucherSegmentInfoRequestPojo.setIdentifierValue(transferDetails.get("PASSWORD"));
	
	SegmentData data = new SegmentData();
	
	c2cBuyVoucherSegmentInfoRequestPojo.setData(data);
	
	c2cBuyVoucherSegmentInfoRequestPojo.getData().setLoginId(transferDetails.get("LOGIN_ID"));
	c2cBuyVoucherSegmentInfoRequestPojo.getData().setMsisdn(transferDetails.get("MSISDN"));
	String voucherType = DBHandler.AccessHandler.getVoucherTypeForUser(transferDetails.get("MSISDN"));
	c2cBuyVoucherSegmentInfoRequestPojo.getData().setVoucherType(voucherType);
		
		
	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_successWithMsisdn() throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setLoginId("");
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_02_Test_noLogin() throws Exception 
	{
		final String methodName = "A_02_Test_noLogin";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setLoginId("");
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();
		String code =  (String) c2cBuyVoucherSegmentInfoResponsePojo.getAdditionalProperties().get("globalError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "no loginId or msisdn");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
	@Test
	public void A_03_Test_invalidIdentifierLogin() throws Exception 
	{
		final String methodName = "A_03_Test_invalidIdentifierLogin";
		Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.setIdentifierType("abc");
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();
		
		String code =  (String) c2cBuyVoucherSegmentInfoResponsePojo.getAdditionalProperties().get("formError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "user.invalidloginid");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_04_Test_successWithLoginID() throws Exception {
		final String methodName = "A_04_Test_successWithLoginID";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	@Test
	public void A_05_Test_failureWithInvalidType() throws Exception {
		final String methodName = "A_05_Test_failureWithInvalidType";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setVoucherType("digital123");
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();
		String code =  (String) c2cBuyVoucherSegmentInfoResponsePojo.getAdditionalProperties().get("globalError");

		Assert.assertEquals(400, statusCode);
		Assertion.assertEquals(code, "voucher type is invalid");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	@Test
	public void A_06_Test_successWithOperatorUser() throws Exception {
		final String methodName = "A_06_Test_successWithOperatorUser";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSEGT6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherSegmentInfoRequestPojo.getData().setLoginId("");
		c2cBuyVoucherSegmentInfoRequestPojo.setIdentifierType(transferDetails.get("LOGIN_ID_ADMIN"));
		c2cBuyVoucherSegmentInfoRequestPojo.setIdentifierValue(transferDetails.get("PASSWORD_ADMIN"));
		C2CVoucherSegmentAPI c2cVoucherSegmentAPI = new C2CVoucherSegmentAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherSegmentAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherSegmentAPI.addBodyParam(c2cBuyVoucherSegmentInfoRequestPojo);
		c2cVoucherSegmentAPI.setExpectedStatusCode(200);
		c2cVoucherSegmentAPI.perform();
		c2cBuyVoucherSegmentInfoResponsePojo = c2cVoucherSegmentAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherSegmentInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherSegmentInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
}
