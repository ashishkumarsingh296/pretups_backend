package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;

import restassuredapi.api.channeluserservices.ViewSelfCommEnquiryAPI;
import restassuredapi.pojo.userpasswordmanagementresponsepojo.UserPasswordManagementResponsePojo;
import restassuredapi.pojo.viewselfcommenquiryrequestpojo.ViewSelfCommEnquiryRequestPojo;
import restassuredapi.pojo.viewselfcommenquiryresponsepojo.ViewSelfCommEnquiryResponsePojo;

@ModuleManager(name = Module.REST_VIEW_SELF_COMM_ENQUIRY)
public class ViewSelfCommEnquiry extends BaseTest{
	static String moduleCode;
	ViewSelfCommEnquiryRequestPojo viewSelfCommEnquiryRequestPojo= new ViewSelfCommEnquiryRequestPojo();
	ViewSelfCommEnquiryResponsePojo viewSelfCommEnquiryResponsePojo= new ViewSelfCommEnquiryResponsePojo();
	
	HashMap<String,String> transfer_Details=new HashMap<String,String>(); 

	public HashMap<String,String> getExcelData(){
		HashMap<String,String> tranferDetails=new HashMap<String,String>();    
		String C2CTransferCode = _masterVO.getProperty("C2CBuyVoucherTransferCode");
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
		int rowCount = ExcelUtility.getRowCount();

		ArrayList<String> alist1 = new ArrayList<String>();
		ArrayList<String> alist2 = new ArrayList<String>();
		ArrayList<String> categorySize = new ArrayList<String>();

		for (int i = 1; i <= rowCount; i++) {
			ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
			String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
			if (aList.contains(C2CTransferCode)) {
				ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
				alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
				alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
			}
		}
		// int rowCount = ExcelUtility.getRowCount();
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();

		int totalObjectCounter = 0;
		for (int i = 0; i < alist2.size(); i++) {
		    int categorySizeCounter = 0;
        for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(i))) {
                categorySizeCounter++;
            }
        }
        categorySize.add("" + categorySizeCounter);
        totalObjectCounter = totalObjectCounter + categorySizeCounter;
    }

    Object[][] Data = new Object[totalObjectCounter][9];

    for (int j = 0, k = 0; j < alist2.size(); j++) {

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int excelRowSize = ExcelUtility.getRowCount();
        String ChannelUserMSISDN = null;
        String ChannelUserLogin = null;
        String ChannelUserPassword = null;
        
        for (int i = 1; i <= excelRowSize; i++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                ChannelUserMSISDN = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                ChannelUserLogin = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                ChannelUserPassword = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                break;
            }
        }

        for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
            if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                Data[k][0] = alist2.get(j);
                Data[k][1] = alist1.get(j);
                Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                Data[k][3] = ChannelUserMSISDN;
                Data[k][4] = ChannelUserPassword;
                Data[k][5] = ChannelUserLogin;
                Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                Data[k][8] = excelCounter;
                k++;
                
            }
        }

    }
    
    tranferDetails.put("Login_Id", Data[0][5].toString());
    tranferDetails.put("Password", Data[0][4].toString());
    tranferDetails.put("Msisdn", Data[0][2].toString());
    
    
       
    return tranferDetails;
    
	}
	

	
	
	
    public void setupData() {
    	transfer_Details=getExcelData();
		viewSelfCommEnquiryRequestPojo.setIdentifierType(transfer_Details.get("Login_Id"));
		viewSelfCommEnquiryRequestPojo.setIdentifierValue(transfer_Details.get("Password"));
    }
    
    public void setupData_dealer() {
    	
    	String Login_Id_dealer=ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.LOGIN_ID, new String[]{ExcelI.CATEGORY_NAME}, new String[]{"Dealer"});
    	String Password_dealer=ExtentI.getValueofCorrespondingColumns(ExcelI.CHANNEL_USERS_HIERARCHY_SHEET, ExcelI.PASSWORD, new String[]{ExcelI.CATEGORY_NAME}, new String[]{"Dealer"});

		viewSelfCommEnquiryRequestPojo.setIdentifierType(Login_Id_dealer);
		viewSelfCommEnquiryRequestPojo.setIdentifierValue(Password_dealer);
    }
	//Successful data with valid data.
	@Test
	public void A_01_Test_success() throws Exception
	{
		final String methodName = "Test_ViewSelfCommEnquiryRestAPI";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE1");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));

		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		long status = viewSelfCommEnquiryResponsePojo.getStatusCode();
		Assert.assertEquals(200, status);
		Assertion.assertEquals(Long.toString(status), "200");
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
		
		
	}
	
	@Test
	public void A_02_Test_identifierTypeEmpty() throws Exception
	{
		final String methodName = "Test_identifierTypeEmpty";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE2");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));

		viewSelfCommEnquiryRequestPojo.setIdentifierType("");
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("request.loggedin.loginid.notpresent",errorMessage);
		
		Assertion.assertEquals("request.loggedin.loginid.notpresent", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

	}
	
	@Test
	public void A_03_Test_identifierValueEmpty() throws Exception
	{
		final String methodName = "Test_identifierValueEmpty";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE3");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));

		viewSelfCommEnquiryRequestPojo.setIdentifierValue("");
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("request.loggedin.password.notpresent",errorMessage);
		Assertion.assertEquals("request.loggedin.password.notpresent", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	}
	
	@Test
	public void A_04_Test_TypeValueEmpty() throws Exception
	{
		final String methodName = "Test_TypeValueEmpty";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE4");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		viewSelfCommEnquiryRequestPojo.setIdentifierType("");

		viewSelfCommEnquiryRequestPojo.setIdentifierValue("");
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("request.loggedin.loginid.notpresent",errorMessage);
		Assertion.assertEquals("request.loggedin.loginid.notpresent", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
		}
	
	@Test
	public void A_05_Test_unauthorisedUser() throws Exception
	{
		final String methodName = "Test_unauthorisedUser";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE5");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData_dealer();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("user.notauthorized",errorMessage);
		Assertion.assertEquals("user.notauthorized", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
			
	}
	
	@Test
	public void A_06_Test_wrongidentifierType() throws Exception
	{
		final String methodName = "Test_wrongidentifierType";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE6");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));

		viewSelfCommEnquiryRequestPojo.setIdentifierType("ydistinvalid");
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("user.invalidloginid",errorMessage);
		Assertion.assertEquals("user.invalidloginid", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
}
	
	@Test
	public void A_07_Test_wrongidentifierValue() throws Exception
	{
		final String methodName = "Test_wrongidentifierValue";
        Log.startTestCase(methodName);
		
		CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTVSCE7");
		moduleCode = CaseMaster.getModuleCode();
		
		currentNode = test.createNode(CaseMaster.getExtentCase());
		currentNode.assignCategory("REST");
		setupData();
		ViewSelfCommEnquiryAPI viewSelfCommEnquiryAPI=new ViewSelfCommEnquiryAPI
				(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
		viewSelfCommEnquiryAPI.setContentType(_masterVO.getProperty("contentType"));

		viewSelfCommEnquiryRequestPojo.setIdentifierValue("13579");
		viewSelfCommEnquiryAPI.addBodyParam(viewSelfCommEnquiryRequestPojo);
		viewSelfCommEnquiryAPI.setExpectedStatusCode(200);
		viewSelfCommEnquiryAPI.perform();
		viewSelfCommEnquiryResponsePojo =viewSelfCommEnquiryAPI.getAPIResponseAsPOJO(ViewSelfCommEnquiryResponsePojo.class);
		String errorMessage = viewSelfCommEnquiryResponsePojo.getFormError();
		Assert.assertEquals("user.invalidpassword",errorMessage);
		Assertion.assertEquals("user.invalidpassword", errorMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);	
		}

}
