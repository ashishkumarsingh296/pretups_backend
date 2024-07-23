package com.testscripts.sit;

import java.text.MessageFormat;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.CommissionProfile;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.commons.ExcelI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.SIT_CommProfile_CBC)
public class SIT_CommProfileCBC extends BaseTest {
	
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
	

	
	@Test(dataProvider = "categoryData")
   @TestManager(TestKey = "PRETUPS-1829") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void A_01_Test_CreateCommissionProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_CreateCommissionProfile";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

        CommissionProfile CommissionProfile = new CommissionProfile(driver);
        // Test Case - Creating Commission Profile as per the DataProvider details
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE1").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        String[] result = CommissionProfile.addCommissionProfileCBC(domainName, categoryName, grade);
        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage", "");
       // CommissionProfile.writeCommissionProfileToExcel(rowNum, result);
        Assertion.assertEquals(result[2], Message);

        if (!_masterVO.getClientDetail("CLIENT_NAME").equalsIgnoreCase("VIETNAM") && result[2].equals(Message)) {
            CommissionProfile.CommissionProfileDefault(domainName, categoryName, grade, result[1]);
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	

	@Test(dataProvider = "categoryData")
   @TestManager(TestKey = "PRETUPS-1830") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void A_02_Test_CreateCommissionProfileDelete(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
        final String methodName = "Test_CreateCommissionProfile";
        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

        CommissionProfile CommissionProfile = new CommissionProfile(driver);
        // Test Case - Creating Commission Profile as per the DataProvider details
        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE2").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
        String[] result = CommissionProfile.addCommissionProfileDelete(domainName, categoryName, grade);
     
        String Message = MessagesDAO.prepareMessageByKey("profile.deletecommissionprofile.error.productalreadyexistincbc");
        if(result[2].equalsIgnoreCase("skip")) {
        	Assertion.assertSkip("Not valid case for this version");
        }
        else {
        Assertion.assertEquals(result[2], Message);
        if(!BTSLUtil.isNullString(result[3]))
        Assertion.assertEquals(result[3], Message);
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
	
		@Test(dataProvider = "categoryData")
	   @TestManager(TestKey = "PRETUPS-1831") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	    public void A_03_Test_CreateCommissionProfileCheckOtf(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
	        final String methodName = "Test_CreateCommissionProfile";
	        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

	        CommissionProfile CommissionProfile = new CommissionProfile(driver);
	        // Test Case - Creating Commission Profile as per the DataProvider details
	        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE3").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
	        String[] result = CommissionProfile.addCommissionProfilecheckotf(domainName, categoryName, grade);
	        if(result[2].equalsIgnoreCase("skip")) {
	        	Assertion.assertSkip("Not valid case for this version");
	        }
	        else {
	        	  Assertion.assertEquals(result[2], "pass");
	        }
	           Assertion.completeAssertions();
	        Log.endTestCase(methodName);
	    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1832") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_04_Test_CreateCommissionProfileNegativeProfile(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE4").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfilecheckNegative(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("errors.required", "Profile name");
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1833") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_05_Test_CreateCommissionProfileNegativeShort(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE5").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfilecheckNegative1(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("errors.required", "Short code");
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1834") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_06_Test_CreateCommissionProfileNegativedate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE6").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfilecheckNegativedate(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("errors.required", "Applicable from");
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1835") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_07_Test_CreateCommissionProfileNegativetime(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE7").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfilecheckNegativetime(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("errors.required", "Applicable time");
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }

		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1836") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_08_Test_CreateCommissionProfileCheckadditional(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE8").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfilecheckadditional(domainName, categoryName, grade);
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], "pass");
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1837") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_09_Test_CreateCommissionProfileCheckCBCvalidataion(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CreateCommissionProfile";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE9").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String[] result = CommissionProfile.addCommissionProfileCBCvalidation(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("errors.required","Product");
		        if(result[2].equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result[2], Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
	
	
	
	
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1798") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_11_Test_CheckCBCvalidataionSlabList(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionSlabList";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE11").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationSlab(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalid.otfdetails","2","1","CBC");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
	
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1799") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_12_Test_CheckCBCvalidataionFromDate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionFromDate";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE12").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationFromDate(domainName, categoryName, grade,"SITCBCCOMMPROFILE12");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.frommissing.otf.single");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1800") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_13_Test_CheckCBCvalidataionToDate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionToDate";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE13").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationToDate(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf.single");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }

		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1801") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_14_Test_CheckCBCvalidataionSlabBlank(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionSlabBlank";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE14").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationSlabBlank(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.commissionslablistempty");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1802") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_15_Test_CheckCBCvalidataionInvalidValue(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionInvalidValue";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE15").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationInvalidValue(domainName, categoryName, grade,"SITCBCCOMMPROFILE15");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfValuenumeric","1");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1803") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_16_Test_CheckCBCvalidataionBlankValue(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionBlankValue";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE16").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationInvalidValue(domainName, categoryName, grade,"SITCBCCOMMPROFILE16");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfValue","1");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }

		
	/*	@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1804") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_17_Test_CheckCBCvalidataionInvalidRate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionInvalidRate";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE17").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationInvalidRate(domainName, categoryName, grade, "SITCBCCOMMPROFILE17");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfRateDecimalnumeric","1");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		*/

		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1805") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_18_Test_CheckCBCvalidataionBlankRate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionBlankRate";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE18").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationInvalidRate(domainName, categoryName, grade,"SITCBCCOMMPROFILE18");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfRate","1");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1806") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_19_Test_CheckCBCvalidataionDates(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionDates";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE19").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationFromDate(domainName, categoryName, grade,"SITCBCCOMMPROFILE19");
		        String Message = MessagesDAO.prepareMessageByKey("profile.otfprofile.error.incompatibledates");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    } 
	/*	
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1807") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_20_Test_CheckCBCvalidataionTime(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE20").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBCvalidationTime(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("profile.otfprofile.error.incompatibletime");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		*/
	
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1808") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_21_Test_CreateCBCwithoutDate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE21").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBC(domainName, categoryName, grade,"SITCBCCOMMPROFILE21");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1809") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_22_Test_CreateCBCwithoutTime(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE22").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBC(domainName, categoryName, grade,"SITCBCCOMMPROFILE22");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1810") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_23_Test_CreateCBC(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE23").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addCBC(domainName, categoryName, grade,"SITCBCCOMMPROFILE23");
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1811") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_24_Test_CreateCommissionwithoutCBC(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE24").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.addcommissonwithoutCBC(domainName, categoryName, grade);
		        String Message = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
	
		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1812") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_25_Test_CBCAddView(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE25").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.viewCBC(domainName, categoryName, grade,"SITCBCCOMMPROFILE25");
		        String Message = "pass";
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }

		

		@Test(dataProvider = "categoryData")
		   @TestManager(TestKey = "PRETUPS-1813") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
		    public void A_26_Test_CBCAddViewCommission(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
		        final String methodName = "Test_CheckCBCvalidataionTime";
		        Log.startTestCase(methodName, rowNum, domainName, categoryName, grade);

		        CommissionProfile CommissionProfile = new CommissionProfile(driver);
		        // Test Case - Creating Commission Profile as per the DataProvider details
		        currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE26").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
		        String result = CommissionProfile.viewCBC(domainName, categoryName, grade,"SITCBCCOMMPROFILE26");
		        String Message = "pass";
		        if(result.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(result, Message);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
		
		@Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1814") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_27_ModifyCBCCompareSlabs(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_CheckBaseCommPref";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE27").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
       		String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
    		System.out.println(result);
    		String CommProfile=result[1];
    		Log.info("The Created Commission profile name is : " + CommProfile);
               String msg = CommissionProfile.modifyCBCWrongValueSeq(domainName, categoryName, grade,CommProfile);
               String Expected = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalid.otfdetails","2","1","CBC");
              
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
		    }
        
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1815") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_28_ModifyCBCFromDateNull(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_CheckBaseCommPref";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE28").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCDate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE28");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.frommissing.otf.single");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1816") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_29_ModifyCBCToDateNull(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_CheckBaseCommPref";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE29").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCDate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE29");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.tomissing.otf.single");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
	
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1817") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_30_ModifyCBCSlab(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_CheckBaseCommPref";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE30").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCSlab(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE30");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.error.commissionslablistempty");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1818") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_31_ModifyCBCValueInvalid(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCValueInvalid";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE31").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCValueRate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE31");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfValuenumeric","1");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1819") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_32_ModifyCBCValueBlank(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCValueBlank";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE32").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCValueRate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE32");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfValue","1");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
      
       /* @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1820") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_33_ModifyCBCRateInvalid(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCRateInvalid";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE33").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCValueRate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE33");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfRateDecimalnumeric","1");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }*/
        
       
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1821") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_34_ModifyCBCRateBlank(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCRateBlank";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE34").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCValueRate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE34");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addotfprofile.error.invalidOtfRate","1");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        
       @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1822") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_35_ModifyCBCDateValidation(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCDateValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE35").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCDate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE35");
               String Expected = MessagesDAO.prepareMessageByKey("profile.otfprofile.error.incompatibledates");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        
    /*   @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1823") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_36_ModifyCBCTimeValidation(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE36").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCDate(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE36");
               String Expected = MessagesDAO.prepareMessageByKey("profile.otfprofile.error.incompatibletime");
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }*/
        
         @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1824") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_37_ModifyCBCwithoutDate(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE37").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
      //         String msg = CommissionProfile.modifyCBC(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE37");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
        //       if(msg.equalsIgnoreCase("skip")) {
		  //      	Assertion.assertSkip("Not valid case for this version");
		    //    }
		      //  else {
		        	  Assertion.assertEquals(result[2], Expected);
		        //}
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1825") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_38_ModifyCBCwithoutTime(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE38").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
     //          String msg = CommissionProfile.modifyCBC(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE38");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
       //        if(msg.equalsIgnoreCase("skip")) {
		 //       	Assertion.assertSkip("Not valid case for this version");
		   //     }
		     //   else {
		        	  Assertion.assertEquals(result[2], Expected);
		       // }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
       
            @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1826") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_39_ModifyCBCwithoutDateTime(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE39").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
            //   String msg = CommissionProfile.modifyCBC(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE39");
               String Expected = MessagesDAO.prepareMessageByKey("profile.addadditionalprofile.message.successaddmessage");
              // if(msg.equalsIgnoreCase("skip")) {
		        //	Assertion.assertSkip("Not valid case for this version");
		       // }
		      //  else {
		        	  Assertion.assertEquals(result[2], Expected);
		      //  }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
        
               @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1827") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_40_ModifyCBC(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE40").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCView(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE40");
               String Expected = "pass";
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
        
                   @Test(dataProvider = "categoryData")
        @TestManager(TestKey = "PRETUPS-1828") // TO BE UNCOMMENTED WITH JIRA TEST ID
        public void A_41_ModifyCBCView(int rowNum, String domainName, String categoryName, String grade) throws InterruptedException {
               
               final String methodName = "Test_ModifyCBCTimeValidation";
         Log.startTestCase(methodName);
         
         currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE41").getExtentCase(), categoryName)).assignCategory(TestCategory.SIT);
               //moduleCode = CaseMaster1.getModuleCode();
               CommissionProfile CommissionProfile = new CommissionProfile(driver);
               String[] result = CommissionProfile.addCommissionProfilewithoutAdditionalCommission(domainName, categoryName, grade);
       		   System.out.println(result);
       		    String CommProfile=result[1];
               String msg = CommissionProfile.modifyCBCView(domainName, categoryName, grade,CommProfile,"SITCBCCOMMPROFILE41");
               String Expected = "pass";
               if(msg.equalsIgnoreCase("skip")) {
		        	Assertion.assertSkip("Not valid case for this version");
		        }
		        else {
		        	  Assertion.assertEquals(msg, Expected);
		        }
		           Assertion.completeAssertions();
		        Log.endTestCase(methodName);
        }
       
                   
                   
                
	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1838") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_42_CheckBaseCommPrefFalse(int rowNum, String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_CheckBaseCommPref";
		Log.startTestCase(methodName);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE42").getExtentCase(), categoryName))
				.assignCategory(TestCategory.SIT);
		// moduleCode = CaseMaster1.getModuleCode();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		String msg = CommissionProfile.changeCBCPreference("False");

		String result = CommissionProfile.checkCBCLink(domainName, categoryName, grade);
		if (result.equalsIgnoreCase("skip")) {
			Assertion.assertSkip("Not valid case for this version");
		} else {
			Assertion.assertEquals(result, "Fail");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1839") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_43_CheckBaseCommPrefTrue(int rowNum, String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_CheckBaseCommPref";
		Log.startTestCase(methodName);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE43").getExtentCase(), categoryName))
				.assignCategory(TestCategory.SIT);
		// moduleCode = CaseMaster1.getModuleCode();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		String msg = CommissionProfile.changeCBCPreference("true");

		String result = CommissionProfile.checkCBCLink(domainName, categoryName, grade);
		if (result.equalsIgnoreCase("skip")) {
			Assertion.assertSkip("Not valid case for this version");
		} else {
			Assertion.assertEquals(result, "Pass");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);

		Log.endTestCase(methodName);
	}

	@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1840") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_44_CheckBaseCommPrefBlank(int rowNum, String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_CheckBaseCommPref";
		Log.startTestCase(methodName);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE44").getExtentCase(), categoryName))
				.assignCategory(TestCategory.SIT);
		// moduleCode = CaseMaster1.getModuleCode();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		String msg = CommissionProfile.changeCBCPreference("");
		String preferenceCode = DBHandler.AccessHandler
				.getNamefromSystemPreference(CONSTANT.TARGET_BASED_BASE_COMMISSION);
		String Expected = MessagesDAO.prepareMessageByKey("preference.selectsystempreference.error.required",
				MessagesDAO.getLabelByKey("services.updateparameters.label.value"), preferenceCode);
	
			
		if (msg.equalsIgnoreCase(Expected))
			Assertion.assertPass("Test Case Passed");
		else
			Assertion.assertFail("Test Case Failed");
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}

	/*@Test(dataProvider = "categoryData")
	@TestManager(TestKey = "PRETUPS-1841") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void A_45_CheckBaseCommSlabPrefBlank(int rowNum, String domainName, String categoryName, String grade)
			throws InterruptedException {

		final String methodName = "Test_CheckBaseCommPref";
		Log.startTestCase(methodName);

		currentNode = test.createNode(
				MessageFormat.format(_masterVO.getCaseMasterByID("SITCBCCOMMPROFILE45").getExtentCase(), categoryName))
				.assignCategory(TestCategory.SIT);
		// moduleCode = CaseMaster1.getModuleCode();
		CommissionProfile CommissionProfile = new CommissionProfile(driver);

		HashMap<String, String> initiateMap = new HashMap<String, String>();
		String msg = CommissionProfile.changeCBCPreference("");
		String preferenceCode = DBHandler.AccessHandler
				.getNamefromSystemPreference(CONSTANT.TARGET_BASED_BASE_COMMISSION_SLABS);
		String Expected = MessagesDAO.prepareMessageByKey("preference.selectsystempreference.error.required",
				MessagesDAO.getLabelByKey("services.updateparameters.label.value"), preferenceCode);
		if (msg.equalsIgnoreCase(Expected))
			Assertion.assertPass("Test Case Passed");
		else
			Assertion.assertFail("Test Case Failed");
		Assertion.completeAssertions();

		Log.endTestCase(methodName);
	}*/
}
