package restassuredapi.test;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.Login;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.c2cvouchertype.C2CVoucherTypeAPI;
import restassuredapi.pojo.c2cbuyvouchertypeinforequestpojo.C2CBuyVoucherTypeInfoRequestPojo;
import restassuredapi.pojo.c2cbuyvouchertypeinforequestpojo.TypeData;
import restassuredapi.pojo.c2cbuyvouchertypeinforesponsepojo.C2CBuyVoucherTypeInfoResponsePojo;



@ModuleManager(name = Module.REST_VOUCHER_TYPE)
public class C2CBuyVoucherTypeInfo extends BaseTest {
	
	 
	static String moduleCode;
	C2CBuyVoucherTypeInfoRequestPojo c2cBuyVoucherTypeInfoRequestPojo = new C2CBuyVoucherTypeInfoRequestPojo();
	C2CBuyVoucherTypeInfoResponsePojo c2cBuyVoucherTypeInfoResponsePojo = new C2CBuyVoucherTypeInfoResponsePojo();
	
	
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
           
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
       	int rowCount = ExcelUtility.getRowCount();
       	rowCount = 1;
       
       	for (int i = 1; i <= rowCount; i++) {
       		transferDetails.put("voucherType", ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
       		transferDetails.put("type", ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
       	}
	}
	
	public void setupData() {
		
	getExcelData();
	
	TypeData data = new TypeData();
	c2cBuyVoucherTypeInfoRequestPojo.setData(data);
	
	c2cBuyVoucherTypeInfoRequestPojo.setIdentifierType(transferDetails.get("LOGIN_ID"));
	c2cBuyVoucherTypeInfoRequestPojo.setIdentifierValue(transferDetails.get("PASSWORD"));
	
	c2cBuyVoucherTypeInfoRequestPojo.getData().setLoginId(transferDetails.get("LOGIN_ID"));
	c2cBuyVoucherTypeInfoRequestPojo.getData().setMsisdn(transferDetails.get("MSISDN"));
	
	c2cBuyVoucherTypeInfoRequestPojo.getData().setVoucherList(transferDetails.get("type"));	
		

	}

	// Successful data with valid data.
	@Test
	public void A_01_Test_successWithMsisdn() throws Exception {
		final String methodName = "A_01_Test_success";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE1");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.getData().setLoginId("");
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();

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
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE2");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.getData().setLoginId("");
		c2cBuyVoucherTypeInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();	
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();
		String code =  (String) c2cBuyVoucherTypeInfoResponsePojo.getAdditionalProperties().get("globalError");

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
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE3");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.setIdentifierType("abc");
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();
		String code =  (String) c2cBuyVoucherTypeInfoResponsePojo.getAdditionalProperties().get("formError");
		
		Assert.assertEquals(400, statusCode);
		
		Assertion.assertEquals(code, "user.invalidloginid");
		Assertion.completeAssertions();
		
		Log.endTestCase(methodName);
	}
	
	@Test
	public void A_04_Test_successWithLoginID() throws Exception {
		final String methodName = "A_04_Test_successWithLoginID";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE4");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.getData().setMsisdn("");
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test
	public void A_05_Test_successWithOperatorUser() throws Exception {
		final String methodName = "A_05_Test_successWithOperatorUser";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE5");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.getData().setMsisdn("");
		c2cBuyVoucherTypeInfoRequestPojo.setIdentifierType(transferDetails.get("LOGIN_ID_ADMIN"));
		c2cBuyVoucherTypeInfoRequestPojo.setIdentifierValue(transferDetails.get("PASSWORD_ADMIN"));
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test
	public void A_06_Test_successWithEmptyVoucherList() throws Exception {
		final String methodName = "A_05_Test_successWithOperatorUser";
		Log.startTestCase(methodName);

		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVTYPE6");
		moduleCode = CaseMaster.getModuleCode();

		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		c2cBuyVoucherTypeInfoRequestPojo.getData().setVoucherList("");
		C2CVoucherTypeAPI c2cVoucherTypeAPI = new C2CVoucherTypeAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		c2cVoucherTypeAPI.setContentType(_masterVO.getProperty("contentType"));
		c2cVoucherTypeAPI.addBodyParam(c2cBuyVoucherTypeInfoRequestPojo);
		c2cVoucherTypeAPI.setExpectedStatusCode(200);
		c2cVoucherTypeAPI.perform();
		c2cBuyVoucherTypeInfoResponsePojo = c2cVoucherTypeAPI
				.getAPIResponseAsPOJO(C2CBuyVoucherTypeInfoResponsePojo.class);
		int statusCode = c2cBuyVoucherTypeInfoResponsePojo.getStatusCode();

		Assert.assertEquals(200, statusCode);
		Assertion.assertEquals(Integer.toString(statusCode), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
}
