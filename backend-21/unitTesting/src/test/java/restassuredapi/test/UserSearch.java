package restassuredapi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import restassuredapi.api.channeluserservices.UserSearchAPI;
import restassuredapi.pojo.usersearchrequestpojo.UserSearchRequestPojo;
import restassuredapi.pojo.usersearchresponsepojo.UserSearchResponsePojo;

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

@ModuleManager(name = Module.REST_USER_SEARCH)
public class UserSearch extends BaseTest {

	
		static String moduleCode;
		UserSearchRequestPojo userSearchRequestPojo= new UserSearchRequestPojo();
		UserSearchResponsePojo userSearchResponsePojo= new UserSearchResponsePojo();
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
		

		
	    public void setupData() {
	    	transfer_Details=getExcelData();
			userSearchRequestPojo.setIdentifierType(transfer_Details.get("Login_Id"));
			userSearchRequestPojo.setIdentifierValue(transfer_Details.get("Password"));
			userSearchRequestPojo.setCategory(1);
			userSearchRequestPojo.setSearchValue("%%");
			
	    }
		//Successful data with valid data.
		@Test
		public void A_01_Test_success() throws Exception
		{
			final String methodName = "Test_UserSearchAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUS1");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			UserSearchAPI userSearchAPI=new UserSearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			userSearchAPI.setContentType(_masterVO.getProperty("contentType"));
			userSearchAPI.addBodyParam(userSearchRequestPojo);
			userSearchAPI.setExpectedStatusCode(200);
			userSearchAPI.perform();
			userSearchResponsePojo =userSearchAPI.getAPIResponseAsPOJO(UserSearchResponsePojo.class);
			Boolean statusCode =userSearchResponsePojo.getStatus();
	
			Assert.assertEquals(true, statusCode);
			Assertion.assertEquals(statusCode.toString(), "true");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
			
			
			
		}
		@Test
		public void A_02_Test_InvalidCategory() throws Exception
		{
			final String methodName = "Test_UserSearchAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUS2");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			UserSearchAPI userSearchAPI=new UserSearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			userSearchAPI.setContentType(_masterVO.getProperty("contentType"));

			userSearchRequestPojo.setCategory(4);
			userSearchAPI.addBodyParam(userSearchRequestPojo);
			userSearchAPI.setExpectedStatusCode(200);
			userSearchAPI.perform();
			userSearchResponsePojo =userSearchAPI.getAPIResponseAsPOJO(UserSearchResponsePojo.class);
			int statusCode =userSearchResponsePojo.getStatusCode();
			Assert.assertEquals(400, statusCode);
			Assertion.assertEquals(Integer.toString(statusCode), "400");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		
		@Test
		public void A_03_Test_InvalidIdentifierType() throws Exception
		{
			final String methodName = "Test_UserSearchAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUS3");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			UserSearchAPI userSearchAPI=new UserSearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			userSearchAPI.setContentType(_masterVO.getProperty("contentType"));

			userSearchRequestPojo.setIdentifierType("invalid");
			userSearchAPI.addBodyParam(userSearchRequestPojo);
			userSearchAPI.setExpectedStatusCode(200);
			userSearchAPI.perform();
			userSearchResponsePojo =userSearchAPI.getAPIResponseAsPOJO(UserSearchResponsePojo.class);
			
			String message =userSearchResponsePojo.getFormError();
			Assert.assertEquals(message, "user.invalidloginid");
			Assertion.assertEquals(message, "user.invalidloginid");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}
		
		
		@Test
		public void A_04_Test_IdentifierValueBlank() throws Exception
		{
			final String methodName = "Test_UserSearchAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUS4");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			UserSearchAPI userSearchAPI=new UserSearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			userSearchAPI.setContentType(_masterVO.getProperty("contentType"));

			userSearchRequestPojo.setIdentifierValue("");
			userSearchAPI.addBodyParam(userSearchRequestPojo);
			userSearchAPI.setExpectedStatusCode(200);
			userSearchAPI.perform();
			userSearchResponsePojo =userSearchAPI.getAPIResponseAsPOJO(UserSearchResponsePojo.class);
			String message =userSearchResponsePojo.getFormError();
			Assert.assertEquals(message, "request.loggedin.password.notpresent");
			Assertion.assertEquals(message, "request.loggedin.password.notpresent");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
		}

		@Test
		public void A_05_Test_IdentifierValueInvalid() throws Exception
		{
			final String methodName = "Test_UserSearchAPI";
	        Log.startTestCase(methodName);
			
			CaseMaster CaseMaster = _masterVO.getCaseMasterByID("RESTUS5");
			moduleCode = CaseMaster.getModuleCode();
			
			currentNode = test.createNode(CaseMaster.getExtentCase());
			currentNode.assignCategory("REST");
			setupData();
			UserSearchAPI userSearchAPI=new UserSearchAPI(_masterVO.getMasterValue(MasterI.WEB_URL_REST));
			userSearchAPI.setContentType(_masterVO.getProperty("contentType"));

			userSearchRequestPojo.setIdentifierValue("Com");
			userSearchAPI.addBodyParam(userSearchRequestPojo);
			userSearchAPI.setExpectedStatusCode(200);
			userSearchAPI.perform();
			userSearchResponsePojo =userSearchAPI.getAPIResponseAsPOJO(UserSearchResponsePojo.class);
			String message =userSearchResponsePojo.getFormError();
			Assert.assertEquals(message, "user.invalidpassword");
			Assertion.assertEquals(message, "user.invalidpassword");
			Assertion.completeAssertions();
			Log.endTestCase(methodName);		}

		
		

}
