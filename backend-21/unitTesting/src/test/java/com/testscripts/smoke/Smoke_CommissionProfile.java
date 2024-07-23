package com.testscripts.smoke;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

@ModuleManager(name = Module.SMOKE_COMMISSION_PROFILE)
public class Smoke_CommissionProfile extends BaseTest {

    private String CommProfile = null;
    HashMap<String,ArrayList<String>> OCPValues = new HashMap<String,ArrayList<String>>();//Other Commission Types and Values
	ArrayList<String[]> OCPTypeValue = new ArrayList<String[]>();
	int OCSize;
	static int OCPIterator = 0;
	
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
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE0").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);    

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
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE2").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);    

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
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("PCOMMPROFILE3").getExtentCase(), categoryName)).assignCategory(TestCategory.SMOKE);    

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
    @TestManager(TestKey = "PRETUPS-334") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void a_CommissionProfileCreation(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_CommissionProfileCreation";
        Log.startTestCase(methodName, domainName, categoryName, grade);

        CommissionProfile addCommissionProfile = new CommissionProfile(driver);
        
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SCOMMPROFILE1").getExtentCase(), domainName, categoryName)).assignCategory(TestCategory.SMOKE);
        String[] result = addCommissionProfile.addCommissionProfile(domainName, categoryName, grade);
        CommProfile = result[1];

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE2").getExtentCase());
        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
        Assertion.assertEquals(result[2], Message);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-342") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void b_ModifyAdditionalCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_ModifyAdditionalCommissionProfile";
        Log.startTestCase(methodName, domainName, categoryName, grade);

        CommissionProfile commissionProfile = new CommissionProfile(driver);
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE5").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String message = commissionProfile.modifyAdditionalCommissionProfile(domainName, categoryName, grade, CommProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE6").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
        Assertion.assertEquals(message, modifyCommProMsg);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-337") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void c_ModifyCommissionProfile(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_ModifyCommissionProfile";
        Log.startTestCase(methodName);

        CommissionProfile commissionProfile = new CommissionProfile(driver);
        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE3").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String message = commissionProfile.modifyCommissionProfile(domainName, categoryName, grade, CommProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE4").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String modifyCommProMsg = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
        Assertion.assertEquals(message, modifyCommProMsg);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-352") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void d_DeleteAdditionalCommProfile(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_DeleteAdditionalCommProfile";
        Log.startTestCase(methodName, domainName, categoryName, grade);

        CommissionProfile commissionProfile = new CommissionProfile(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SCOMMPROFILE9").getExtentCase(), CommProfile)).assignCategory(TestCategory.SMOKE);
        String StatusText = commissionProfile.deleteAdditionalCommProfile(domainName, categoryName, grade, CommProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE10").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successeditmessage");
        Assertion.assertEquals(StatusText, Message);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-344") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void e_DeleteCommProfile(String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_DeleteCommProfile";
        Log.startTestCase(methodName, domainName, categoryName, grade);

        CommissionProfile commissionProfile = new CommissionProfile(driver);
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SCOMMPROFILE7").getExtentCase(), CommProfile)).assignCategory(TestCategory.SMOKE);
        String StatusText = commissionProfile.deleteCommProfile(domainName, categoryName, grade, CommProfile);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("SCOMMPROFILE8").getExtentCase()).assignCategory(TestCategory.SMOKE);
        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successdeletemessage");
        Assertion.assertEquals(StatusText, Message);

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* ---------------------- D A T A  P R O V I D E R ------------------------- */
    /* ------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData1")
    public Object[][] TestDataFeed1() {
        String CustomerRechargeCode = _masterVO.getProperty("CustomerRechargeCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which Customer Recharge is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(CustomerRechargeCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
            }
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which C2S transfer is allowed
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int chnlCount = ExcelUtility.getRowCount();
        int userCounter = 0;
        for (int i = 1; i <= chnlCount; i++) {
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                userCounter++;
            }
        }

        /*
         * Store required data of 'C2S transfer allowed category' users in Object
         */
        Object[][] Data = new Object[userCounter][4];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, i);
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.GRADE, i);
                j++;
            }
        }


        Object[][] categoryData = new Object[1][3];
        categoryData[0][0] = Data[0][0];//ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, 1);
        categoryData[0][1] = Data[0][1];//ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, 1);
        categoryData[0][2] = Data[0][2];//ExcelUtility.getCellData(0, ExcelI.GRADE, 1);

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

    /* ------------------------------------------------------------------------------------------ */
}
