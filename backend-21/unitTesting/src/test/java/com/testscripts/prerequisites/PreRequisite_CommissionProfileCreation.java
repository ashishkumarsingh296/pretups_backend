package com.testscripts.prerequisites;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.ExtentI;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_COMMISSION_PROFILE)
public class PreRequisite_CommissionProfileCreation extends BaseTest {

	HashMap<String,ArrayList<String>> OCPValues = new HashMap<String,ArrayList<String>>();//Other Commission Types and Values
	ArrayList<String[]> OCPTypeValue = new ArrayList<String[]>();
	HashMap<String,String> commTypes = new HashMap<String,String>();	
	int OCSize;
	
	/**
	 * Loads Other Commission Type from lookups
	 * and populates the corresponding "type values" from UI
	 */
	@Test(dataProvider = "categoryData") 
	public void A_loadOtherCommisionDetails() {
		String methodName = "A_loadOtherCommisionDetails";
		Log.info("Entered " + methodName);
		if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
			int OCPTypeCount = Integer.parseInt(_masterVO.getProperty("OCPTCount").trim());
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
	
	 @Test(dataProvider = "categoryData")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void B_CreateOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "B_CreateOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE0").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        /*OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.addOtherCommissionProfile(OCPData);
		        String message = MessagesDAO.prepareMessageByKey("profile.addotheradditionalprofile.message.successaddmessage");
		        CommissionProfile.writeOtherCommissionDetails(rowNum,OCPData);
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
	        }
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void C_ModifyOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "C_ModifyOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE2").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		      /*  OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
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
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void D_ViewOtherCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "D_ViewOtherCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE3").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        /*OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
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
	 }
	 
	 @Test(dataProvider = "")
	 @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void E_OCP_BlankEntry() throws InterruptedException {
	        final String methodName = "E_OCP_BlankEntry";
	        Log.startTestCase(methodName, "Entered");
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        currentNode = test.createNode(_masterVO.getCaseMasterByID("SITCOMMPROFILE28").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        String actual = CommissionProfile.ocpNegativeBlankEntry();
		        String message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.commissiontype");
		        Assertion.assertContainsEquals(actual,message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
	        }
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void F_OCP_TransferBlank(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "F_OCP_TransferBlank";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCOMMPROFILE29").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        /*OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.ocpNegativeTransferTypeBlank(OCPData);
		        String message = MessagesDAO.prepareMessageByKey("profile.addothercommissionprofile.error.transactiontypeempty");
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
	        }
	 }
	 
	 @Test(dataProvider = "categoryDataOCP")
	    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void G_OCP_StartRangeLesser(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "G_OCP_StartRangeLesser";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
	        
	        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
	        HashMap<String,String> OCPData = new HashMap<String,String>(); //Other Commission Profile Data
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCOMMPROFILE30").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);    

	        if("True".equalsIgnoreCase(DBHandler.AccessHandler.getSystemPreference(SystemPreferences.OTH_COM_CHNL))) {
		        /*OCPData.put("type",OCPTypeValue.get( rowNum % OCSize )[0]);
		        OCPData.put("typeValue", OCPTypeValue.get( rowNum % OCSize )[1]);*/
	        	loadOCPSet(OCPData, rowNum, categoryName, grade);
		       
		        Log.info("OCPValues: " + OCPValues.toString());
		        OCPData = CommissionProfile.ocpNegativeStartRangeLesser(OCPData);
		        	if("skip".equals(OCPData.get("message")))
		        		Assertion.assertSkip("Rows lesser than 2");
//		        String message = "From range of slab 2 should be greater than To range of slab 1";
		        	String message = MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.error.invalidcommissionslab", 
		        			MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.fromrange"),"2",
		        			MessagesDAO.prepareMessageByKey("profile.addcommissionprofile.label.torange"),"1");
		        Assertion.assertEquals(OCPData.get("message"),message);
	        }else {
	        	Assertion.assertSkip("Other Commission not applicable");
	        }
	 }
	
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-277") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_CreateCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_CreateCommissionProfile";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);
        
        CommissionProfile CommissionProfile = new CommissionProfile(driver);        
        
        // Test Case - Creating Commission Profile as per the DataProvider details
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE1").getExtentCase(), categoryName)).assignCategory(TestCategory.PREREQUISITE);
        String[] result = CommissionProfile.addCommissionProfile(domainName, categoryName, grade);
        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
        CommissionProfile.writeCommissionProfileToExcel(rowNum, result);
        Assertion.assertEquals(result[2], Message);

        if (!_masterVO.getClientDetail("CLIENT_NAME").equalsIgnoreCase("VIETNAM") && result[2].equals(Message)) {
            if(_masterVO.getClientDetail("COMM_PROF_STATUS").equals("0"))
        	CommissionProfile.CommissionProfileDefault(domainName, categoryName, grade, result[1]);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {

        String MasterSheetPath = _masterVO.getProperty("DataProvider");
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        Object[][] categoryData = new Object[rowCount][4];
        for (int i = 1, j = 0; i <= rowCount; i++, j++) {
            categoryData[j][0] = i;
            categoryData[j][1] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
            categoryData[j][2] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
            categoryData[j][3] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
        }
        return categoryData;
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
    

    /* ------------------------------------------------------------------------------------------------- */
}
