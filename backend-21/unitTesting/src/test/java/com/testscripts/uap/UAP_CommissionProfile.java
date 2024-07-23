package com.testscripts.uap;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.CommissionProfile;
import com.classes.BaseTest;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.commons.PretupsI;
import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.UAP_COMMISSION_PROFILE)

public class UAP_CommissionProfile extends BaseTest{

	static boolean TestCaseCounter = false;
	String CommProfile;
	String profileName;
	static String moduleCode;
	String assignCategory="UAP";
	HashMap<String,ArrayList<String>> OCPValues = new HashMap<String,ArrayList<String>>();//Other Commission Types and Values
	ArrayList<String[]> OCPTypeValue = new ArrayList<String[]>();
	int OCSize;
	static int OCPIterator = 0;

	@DataProvider(name = "categoryData1")
	public Object[][] TestDataFeed() {

		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		//int rowCount = ExcelUtility.getRowCount();
		Object[][] categoryData = new Object[1][3];
		categoryData[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
		categoryData[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
		categoryData[0][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, 1);

		return categoryData;
	}

	@DataProvider(name = "categoryData")
	public Object[][] getCategoryData() {
		String additionalCommission = null;
		String MasterSheetPath = _masterVO.getProperty("DataProvider");
		ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
		int rowCount = ExcelUtility.getRowCount();
		Object[][] Data = new Object[1][3];
		
		for (int i = 1; i <= rowCount; i++) {
			additionalCommission = ExcelUtility.getCellData(0, ExcelI.ADDITIONAL_COMMISSION, i);
			if (additionalCommission.equals("Y")) {
				Data[0][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
				Data[0][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
				Data[0][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
				break;
			}
		}

		return Data;
	}

	 @DataProvider(name = "categoryDataOCP")
	 public Object[][] TestDataFeed2() {

	        String MasterSheetPath = _masterVO.getProperty("DataProvider");
	        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			rowCount = 1;
	        Object[][] categoryData = new Object[rowCount][4];
	        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
	            categoryData[j][0] = i;
	            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
	            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
	            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
	        }
	        return categoryData;
	    }
	 
		@Test 
		public void AA_loadOtherCommisionDetails() {
			String methodName = "AA_loadOtherCommisionDetails";
			Log.info("Entered " + methodName);
			if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
				int OCPTypeCount = Integer.parseInt(_masterVO.getProperty("OCPTCount").trim());
				HashMap<String,String> commTypes = new HashMap<String,String>();
				CommissionProfile CommissionProfile = new CommissionProfile(driver);
				ResultSet rs = null ;
				try {
						rs = DBHandler.AccessHandler.getLookupByType(PretupsI.OTH_COMM_TYPE_LOOKUP);
						while(rs.next()) {
							commTypes.put(rs.getString("LOOKUP_CODE"), rs.getString("LOOKUP_NAME"));
						}
						Log.info("Other Commission Type list size: " + commTypes.size());
					}catch(Exception e) {
						Log.info("Exception in loading commission types");
					}
				OCPValues = CommissionProfile.loadOtherCommissionValuesByType(commTypes);
			
			
				for(Map.Entry<String, ArrayList<String>> type: OCPValues.entrySet()) {
					if(type.getValue() != null)
					{
						for(String value: type.getValue()) 
						{
							OCPTypeValue.add(new String[] {type.getKey() , value}); //Other commission Type, Other commission type Value
						}
					}
					else
					{
						int i = 0;
						do
						{
						OCPTypeValue.add(new String[] {type.getKey() , "select default"}); /*Other commission Type only set,
																							 Other commission type Value to be selected later from data provider*/
						i++;
						}while(i < OCPTypeCount);
					}
				}
				OCSize = OCPTypeValue.size();
			}
		}
		
		public void loadOCPSet(HashMap<String,String> OCPData, int rowNum, String categoryName, String grade) {
			String methodName = "loadOCPSet";
			Log.methodEntry(methodName, rowNum, categoryName, grade);
			
			OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);		
			if(OCPData.get("type").equals(PretupsI.CATEGORY))
			{
				OCPData.put("typeValue",categoryName);
			}
			else if(OCPData.get("type").equals(PretupsI.GRADE)) 
			{
				OCPData.put("typeValue",grade);
			}
			else
			{
				OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
			}
			OCPData.put("row", Integer.toString(rowNum));
		}
		
		 @Test(dataProvider = "categoryDataOCP")
			@TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void AB_CreateOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
				final String methodName = "AB_CreateOtherCommissionProfile";
				Log.startTestCase(methodName, domainName, categoryName, grade);
				
				CommissionProfile CommissionProfile = new CommissionProfile(driver);        
				HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
				currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE0").getExtentCase(), categoryName)).assignCategory(TestCategory.UAP);    
	
				if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
					/*OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
					OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
					loadOCPSet(OCPData, rowNum, categoryName, grade);
				   
					Log.info("OCPValues: " + OCPValues.toString());
					OCPData = CommissionProfile.addOtherCommissionProfile(OCPData);
					String message = MessagesDAO.prepareMessageByKey("profile.addotheradditionalprofile.message.successaddmessage");
					CommissionProfile.writeOtherCommissionDetails(rowNum, OCPData);
					Assertion.assertEquals(OCPData.get("message"),message);
				}else {
					Assertion.assertSkip("Other Commission not applicable");
				}
				Assertion.completeAssertions();
			Log.endTestCase(methodName);
		 }
		 
		 @Test(dataProvider = "categoryDataOCP")
			@TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void AC_ModifyOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
				final String methodName = "AC_ModifyOtherCommissionProfile";
				Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
				
				CommissionProfile CommissionProfile = new CommissionProfile(driver);        
				HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
				currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE2").getExtentCase(), categoryName)).assignCategory(TestCategory.UAP);    
	
				if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
					/*OCSize = OCPTypeValue.size();
					OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
					OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
					OCPData.put("row", Integer.toString(rowNum));*/
					loadOCPSet(OCPData, rowNum, categoryName, grade);
				   
					Log.info("OCPValues: " + OCPValues.toString());
					OCPData = CommissionProfile.modifyOtherCommissionProfile(OCPData);
					String message = MessagesDAO.prepareMessageByKey("profile.addadditionalotherprofile.message.successeditmessage");
					CommissionProfile.writeOtherCommissionDetails(rowNum,OCPData);
					Assertion.assertEquals(OCPData.get("message"),message);
				}else {
					Assertion.assertSkip("Other Commission not applicable");
				}
				Assertion.completeAssertions();
			Log.endTestCase(methodName);
		 }
		 
		 @Test(dataProvider = "categoryDataOCP")
			@TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
			public void AD_DViewOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
				final String methodName = "AD_DViewOtherCommissionProfile";
				Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
				
				CommissionProfile CommissionProfile = new CommissionProfile(driver);        
				HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
				currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE3").getExtentCase(), categoryName)).assignCategory(TestCategory.UAP);    
	
				if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
					/*OCSize = OCPTypeValue.size();
					OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
					OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);
					OCPData.put("row", Integer.toString(rowNum));*/
					loadOCPSet(OCPData, rowNum, categoryName, grade);
				   
					Log.info("OCPValues: " + OCPValues.toString());
					boolean viewed = CommissionProfile.viewOtherCommissionProfile(OCPData);
					if(viewed)
						Assertion.assertPass("Other commission viewed successfully");
					else {
						Assertion.assertFail("Not able to view profile");
						ExtentI.attachScreenShot();
					}
				}else {
					Assertion.assertSkip("Other Commission not applicable");
				}
				Assertion.completeAssertions();
			Log.endTestCase(methodName);
		 }
	

	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-345") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void a_commissionProfileCreation(String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_commissionProfileCreation";
	    Log.startTestCase(methodName);
	    CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		moduleCode="["+assignCategory+"]"+_masterVO.getCaseMasterByID("PCOMMPROFILE1").getModuleCode();
		
		HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE0").getExtentCase(), categoryName)).assignCategory(TestCategory.UAP);
	        OCPData.put("type", _masterVO.getProperty("OCPType"));
	        OCPData.put("typeValue", _masterVO.getProperty("OCPTypeValue"));
	        
	        OCPData = addCommissionProfile.addOtherCommissionProfile(OCPData);
	        String message = MessagesDAO.prepareMessageByKey("profile.addotheradditionalprofile.message.successaddmessage");
	        Assertion.assertEquals(OCPData.get("message"),message);
        }
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCOMMPROFILE1").getExtentCase(),domainName,categoryName));
		currentNode.assignCategory(assignCategory);

		String[] result = addCommissionProfile.addCommissionProfile(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];
		Log.info("The Created Commission profile name is : " + CommProfile);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		Assertion.assertEquals(result[2], Message);
		 Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}


	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-346") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void b_viewCommissionProfile(String domainName, String categoryName, String grade)throws InterruptedException{

		final String methodName = "Test_viewCommissionProfile";
	    Log.startTestCase(methodName);

		CommissionProfile commissionProfile = new CommissionProfile(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCOMMPROFILE1").getExtentCase(), domainName,categoryName));//"To verify that Network Admin is able to view Commission Profile for domain "+domainName+" and category Name "+categoryName);
		currentNode.assignCategory(assignCategory);

		String actual = commissionProfile.viewCommissionProfile(domainName, categoryName, grade, CommProfile);
		String expected= MessagesDAO.prepareMessageByKey("profile.commissionprofiledetailview.view.heading");
		Assertion.assertEquals(actual, expected);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}




	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-347") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void c_modifyCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		final String methodName = "Test_modifyCommissionProfile";
	    Log.startTestCase(methodName);
		CommissionProfile commissionProfile = new CommissionProfile(driver);


		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE3").getExtentCase());//"To verify that Network Admin is able to do successful Commission Profile Modification");
		currentNode.assignCategory(assignCategory);
		String message = commissionProfile.modifyCommissionProfile(domainName, categoryName, grade, CommProfile);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		Assertion.assertEquals(message, modifyCommProMsg);
		 Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-348") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void d_modifyCommissionProfileForFutureDate(String domainName, String categoryName, String grade) throws InterruptedException {

		final String methodName = "Test_modifyCommissionProfileForFutureDate";
	    Log.startTestCase(methodName);
		CommissionProfile commissionProfile = new CommissionProfile(driver);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String message = commissionProfile.modifyCommissionProfileForFutureDate(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		Assertion.assertEquals(message, modifyCommProMsg);
		 Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	

	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-349") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void e_modifyAdditionalCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		final String methodName = "Test_modifyAdditionalCommissionProfile";
	    Log.startTestCase(methodName);
		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		//test = extent.createTest("[UAP]Commission Profile");
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String message = commissionProfile.modifyAdditionalCommissionProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		
		String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		Assertion.assertEquals(message, modifyCommProMsg);
		 Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	
	
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-350") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void f_suspendAdditionalCommProfile(String domainName, String categoryName, String grade)
			throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		final String methodName = "Test_suspendAdditionalCommProfile";
	    Log.startTestCase(methodName);
	    
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCOMMPROFILE4").getExtentCase(),CommProfile));
		currentNode.assignCategory(assignCategory);
		String StatusText = commissionProfile.suspendAdditionalCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		Assertion.assertEquals(StatusText, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-351") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void g_Modify_RemoveAdditionalCommProfileSlab(String domainName, String categoryName, String grade)
			throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		
		final String methodName = "Test_Modify_RemoveAdditionalCommProfileSlab";
	    Log.startTestCase(methodName);
	    
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SCOMMPROFILE9").getExtentCase(),CommProfile));
		currentNode.assignCategory(assignCategory);
		String StatusText = commissionProfile.deleteAdditionalCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
		Assertion.assertEquals(StatusText, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);

	}

	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-353") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void h_deleteCommProfile(String domainName, String categoryName, String grade) throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		final String methodName = "Test_deleteCommProfile";
	    Log.startTestCase(methodName);

		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SCOMMPROFILE7").getExtentCase(),CommProfile));
		currentNode.assignCategory(assignCategory);

		String StatusText = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfile);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");
		Assertion.assertEquals(StatusText, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}


	
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-354") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void i_commissionProfileCreationWithDuplicateName(String domainName, String categoryName, String grade)	throws InterruptedException {

		final String methodName = "Test_commissionProfileCreationWithDuplicateName";
	    Log.startTestCase(methodName);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCOMMPROFILE6").getExtentCase(),domainName,categoryName));
		currentNode.assignCategory(assignCategory);

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String actual = addCommissionProfile.addCommissionProfileWithDuplicateName(domainName, categoryName, grade, CommProfile);
		System.out.println(actual);
		

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.commissionprofilenamealreadyexist");
		Assertion.assertEquals(actual, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}

	
		
	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-355") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void j_specificGeoDomainCommissionProfileCreation(String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_specificGeoDomainCommissionProfileCreation";
	    Log.startTestCase(methodName);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UCOMMPROFILE8").getExtentCase(),domainName,categoryName));
		currentNode.assignCategory(assignCategory);

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String[] result = addCommissionProfile.addCommissionProfileWithSpecificGeography(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
		Assertion.assertEquals(result[2], Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	
	//@Test(dataProvider = "categoryData")
	public void j_blankMandatoryField(String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_blankMandatoryField";
	    Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE12").getExtentCase());
		currentNode.assignCategory(assignCategory);

		CommissionProfile addCommissionProfile = new CommissionProfile(driver);
		String actual = addCommissionProfile.addCommissionProfileWithBlankMandatoryField(domainName, categoryName, grade);
		System.out.println(actual);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.mandatoryApplicableDate","");
		System.out.println(Message);
		Assertion.assertEquals(actual, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	

	@Test(dataProvider = "categoryData")
	 @TestManager(TestKey = "PRETUPS-356") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void k_deleteCommProfileAssociatedWithUser(String domainName, String categoryName, String grade) throws InterruptedException {

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		final String methodName = "Test_deleteCommProfileAssociatedWithUser";
	    Log.startTestCase(methodName);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE10").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String StatusText = commissionProfile.deleteCommProfileAssociatedWithUser(domainName, categoryName, grade);
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.commissionassociatedwithuser");
		Assertion.assertEquals(StatusText, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
	
	//@Test(dataProvider = "categoryData")
	public void l_commissionProfileStatusChange(String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_commissionProfileStatusChange";
	    Log.startTestCase(methodName);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE14").getExtentCase());
		currentNode.assignCategory(assignCategory);

		CommissionProfile commissionProfile = new CommissionProfile(driver);
		String[] result = commissionProfile.addCommissionProfile(domainName, categoryName, grade);
		System.out.println(result);
		CommProfile=result[1];
		System.out.println("The Created Commission profile name is : " + CommProfile);
		
		String actual = commissionProfile.CommissionProfileStatusChange(domainName, categoryName, grade,CommProfile);

		currentNode = test.createNode(_masterVO.getCaseMasterByID("UCOMMPROFILE15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successsuspendmessage");
		Assertion.assertEquals(actual, Message);
		 Assertion.completeAssertions();
		 Log.endTestCase(methodName);
	}
	
}
