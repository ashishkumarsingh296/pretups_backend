package angular.testscripts.prereuisitesangular;


import angular.feature.CUHomeRevamp;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.utils.*;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleManager(name = Module.PREREQUISITE_CU_HOME)
public class PreRequisite_CU_Home_Revamp extends BaseTest {

    @Test(dataProvider = "categoryData")
    public void TC_01_Test_CUHomeAddWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_01_Test_CUHomeAddWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
            CUHome.performHomeAddWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_02_Test_CUHomeRemoveAddedWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_02_Test_CUHomeRemoveAddedWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performHomeRemoveAddedWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_03_Test_CUHomeCancelAddedWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_03_Test_CUHomeCancelAddedWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performHomeCancelAddedWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_04_Test_CUHomeEditAddedWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_04_Test_CUHomeEditAddedWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performHomeEditAddedWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_05_Test_CUHomeAddMultipleWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_05_Test_CUHomeAddMultipleWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performHomeAddMultipleWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_06_Test_CUHomeMoveAddedWidget(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_06_Test_CUHomeMoveAddedWidget";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performHomeMoveAddedWidgets(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_07_Test_CUHomeCheckWidgetPersistence(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_07_Test_CUHomeCheckWidgetPersistence";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performWidgetPersistenceCheck(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    public void TC_08_Test_CUHomeValidateValueDate(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_08_Test_CUHomeCheckWidgetPersistence";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performDateValidationInValueWidget(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    public void TC_09_Test_CUHomeValidateCountDate(String ParentCategory, String FromCategory, String PIN) {
        final String methodName = "TC_09_Test_CUHomeValidateCountDate";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPCUHOME9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.CU_HOME_REVAMP, FromCategory, EventsI.CUHOME_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            CUHomeRevamp CUHome = new CUHomeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                CUHome.performDateValidationInCountWidget(ParentCategory, FromCategory, PIN);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Home Page is not available to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    
    
        /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
        String HomeCode = _masterVO.getProperty("HOME");
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
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            alist1.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
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
        Object[][] Data = new Object[userCounter][3];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                j++;
            }
        }

        return Data;
    }

    /* ------------------------------------------------------------------------------------------------ */


}
