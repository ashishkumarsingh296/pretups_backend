package angular.testscripts.prereuisitesangular;

import angular.feature.PassbookPageRevamp;

import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleManager(name = Module.PREREQUISITE_PASSBOOKPAGE_REVAMP)
public class PreRequisite_PassbookPage_Revamp extends BaseTest {
	
    public PreRequisite_PassbookPage_Revamp() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
    }
    
    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
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
         * of Categories for which O2C transfer is allowed
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
         * Store required data of 'O2C transfer allowed category' users in Object
         */
        Object[][] Data = new Object[userCounter][4];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                Data[j][3] = CustomerRechargeCode;
                j++;
            }
        }

        return Data;
    }

    /* ------------------------------------------------------------------------------------------------ */
    /* -----------------------------  T		E	S	T	C	A	S	E	S  -------------------------------- */
    

    @Test(dataProvider = "categoryData")
//  @TestManager(TestKey = "PRETUPS-001") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_LoginLogout(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
    	final String methodName = "TC_01_Test_LoginLogout";
    	Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
    	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPASSBOOK1");
    	currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.PASSBOOK_DETAILS, FromCategory, EventsI.PASSBOOK_DETAILS_REVAMP)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            PassbookPageRevamp PassbookPage = new PassbookPageRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	PassbookPage.performLoginLogout(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("PassbookPage is not allowed to category[" + FromCategory + "].");
          }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    } 
    
    @Test(dataProvider = "categoryData")
//  @TestManager(TestKey = "PRETUPS-002") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_VerifyHidelink(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
    	final String methodName = "TC_02_Test_VerifyHidelink";
    	Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
    	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPASSBOOK2");
    	currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
    	        
    	if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.PASSBOOK_DETAILS, FromCategory, EventsI.PASSBOOK_DETAILS_REVAMP)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            PassbookPageRevamp PassbookPage = new PassbookPageRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	PassbookPage.performHidelink(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("PassbookPage is not allowed to category[" + FromCategory + "].");
          }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
//  @TestManager(TestKey = "PRETUPS-003") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_VerifyProceedButton(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
    	final String methodName = "TC_03_Test_VerifyProceedButton";
    	Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
    	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPASSBOOK3");
    	currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.PASSBOOK_DETAILS, FromCategory, EventsI.PASSBOOK_DETAILS_REVAMP)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            PassbookPageRevamp PassbookPage = new PassbookPageRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	PassbookPage.performProceedButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("PassbookPage is not allowed to category[" + FromCategory + "].");
          }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
//  @TestManager(TestKey = "PRETUPS-004") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_SelectFromdateTodate(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
    	final String methodName = "TC_04_Test_SelectFromdateTodate";
    	Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
    	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPASSBOOK4");
    	currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.PASSBOOK_DETAILS, FromCategory, EventsI.PASSBOOK_DETAILS_REVAMP)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            PassbookPageRevamp PassbookPage = new PassbookPageRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	PassbookPage.performSelectFromdateTodate(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("PassbookPage is not allowed to category[" + FromCategory + "].");
          }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
//  @TestManager(TestKey = "PRETUPS-005") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_VerifyProductcode(String ParentCategory, String FromCategory, String PIN, String service) throws InterruptedException {
    	final String methodName = "TC_05_Test_VerifyProductcode";
    	Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
    	
    	CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPPASSBOOK5");
    	currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.PASSBOOK_DETAILS, FromCategory, EventsI.PASSBOOK_DETAILS_REVAMP)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            PassbookPageRevamp PassbookPage = new PassbookPageRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	PassbookPage.performVerifyProductcode(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("PassbookPage is not allowed to category[" + FromCategory + "].");
          }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   