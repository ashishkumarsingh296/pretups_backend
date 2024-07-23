package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.utils.constants.Module;
import restassuredapi.api.channeluserservices.SelfProfileThresholdAPI;
import restassuredapi.pojo.selfprofilethersholdrequest.SelfProfileThresholdRequestPojo;
import restassuredapi.pojo.selfprofilethresholdresponsepojo.SelfProfileThresholdResponsePojo;

import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.reporting.extent.entity.ModuleManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;

	@ModuleManager(name = Module.REST_SELF_PROF_THRESHOLD)
	public class SelfProfileThreshold extends BaseTest{
		static String moduleCode;
		SelfProfileThresholdRequestPojo selfProfileThresholdRequestPojo= new SelfProfileThresholdRequestPojo();
		SelfProfileThresholdResponsePojo selfProfileThresholdResponsePojo= new SelfProfileThresholdResponsePojo();
		
		
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
	    return tranferDetails;
	    
		}
		
		
		
		
		
		
		
		@BeforeMethod(alwaysRun = true)
	    public void setupData() {
			transfer_Details=getExcelData();
			selfProfileThresholdRequestPojo.setIdentifierType(transfer_Details.get("Login_Id"));
			selfProfileThresholdRequestPojo.setIdentifierValue(transfer_Details.get("Password"));
	    }
		//Successful data with valid data.
		@Test
		public void A_01_Test_success() throws Exception
		{
			final String methodName = "Test_SelfProfThresholdAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSPT1");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			SelfProfileThresholdAPI selfProfileThresholdAPI=new SelfProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
			selfProfileThresholdAPI.addBodyParam(selfProfileThresholdRequestPojo);
			selfProfileThresholdAPI.setExpectedStatusCode(200);
			selfProfileThresholdAPI.perform();
			selfProfileThresholdResponsePojo =selfProfileThresholdAPI.getAPIResponseAsPOJO(SelfProfileThresholdResponsePojo.class);
			long statusCode =selfProfileThresholdResponsePojo.getStatusCode();
			Assert.assertEquals(200, statusCode);
			Assertion.assertEquals(Long.toString(statusCode), "200");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		@Test
		public void A_02_IdentifierTypeBlank() throws Exception
		{
			final String methodName = "Test_SelfProfThresholdAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSPT2");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			SelfProfileThresholdAPI selfProfileThresholdAPI=new SelfProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
			selfProfileThresholdRequestPojo.setIdentifierType("");
			selfProfileThresholdAPI.addBodyParam(selfProfileThresholdRequestPojo);
			selfProfileThresholdAPI.setExpectedStatusCode(200);
			selfProfileThresholdAPI.perform();
			selfProfileThresholdResponsePojo =selfProfileThresholdAPI.getAPIResponseAsPOJO(SelfProfileThresholdResponsePojo.class);
			String message =selfProfileThresholdResponsePojo.getFormError();
			Assert.assertEquals(message, "request.loggedin.loginid.notpresent");
			Assertion.assertEquals(message, "request.loggedin.loginid.notpresent");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
				}
		
		
		
		@Test
		public void A_03_IdentifierTypeInvalid() throws Exception
		{
			final String methodName = "Test_SelfProfThresholdAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSPT3");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			SelfProfileThresholdAPI selfProfileThresholdAPI=new SelfProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
			selfProfileThresholdRequestPojo.setIdentifierType("invalid");
			selfProfileThresholdAPI.addBodyParam(selfProfileThresholdRequestPojo);
			selfProfileThresholdAPI.setExpectedStatusCode(200);
			selfProfileThresholdAPI.perform();
			selfProfileThresholdResponsePojo =selfProfileThresholdAPI.getAPIResponseAsPOJO(SelfProfileThresholdResponsePojo.class);
			String message =selfProfileThresholdResponsePojo.getFormError();
			Assert.assertEquals(message, "user.invalidloginid");
			Assertion.assertEquals(message, "user.invalidloginid");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			
		}
		
		
		@Test
		public void A_04_IdentifierValueBlank() throws Exception
		{
			final String methodName = "Test_SelfProfThresholdAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSPT4");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			SelfProfileThresholdAPI selfProfileThresholdAPI=new SelfProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
			selfProfileThresholdRequestPojo.setIdentifierValue("");
			selfProfileThresholdAPI.addBodyParam(selfProfileThresholdRequestPojo);
			selfProfileThresholdAPI.setExpectedStatusCode(200);
			selfProfileThresholdAPI.perform();
			selfProfileThresholdResponsePojo =selfProfileThresholdAPI.getAPIResponseAsPOJO(SelfProfileThresholdResponsePojo.class);
			
			
			String message =selfProfileThresholdResponsePojo.getFormError();
			Assert.assertEquals(message, "request.loggedin.password.notpresent");
			Assertion.assertEquals(message, "request.loggedin.password.notpresent");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			
		}
		
		
		@Test
		public void A_05_IdentifierValueInvalid() throws Exception
		{
			final String methodName = "Test_SelfProfThresholdAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTSPT5");
			moduleCode = CaseMaster.getModuleCode();
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			SelfProfileThresholdAPI selfProfileThresholdAPI=new SelfProfileThresholdAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			selfProfileThresholdAPI.setContentType(_masterVO.getProperty("contentType"));
			selfProfileThresholdRequestPojo.setIdentifierValue("invalid");
			selfProfileThresholdAPI.addBodyParam(selfProfileThresholdRequestPojo);
			selfProfileThresholdAPI.setExpectedStatusCode(200);
			selfProfileThresholdAPI.perform();
			selfProfileThresholdResponsePojo =selfProfileThresholdAPI.getAPIResponseAsPOJO(SelfProfileThresholdResponsePojo.class);
			
			
			String message =selfProfileThresholdResponsePojo.getFormError();
			Assert.assertEquals(message, "user.invalidpassword");
			Assertion.assertEquals(message, "user.invalidpassword");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			
		}
		
		
		

}
