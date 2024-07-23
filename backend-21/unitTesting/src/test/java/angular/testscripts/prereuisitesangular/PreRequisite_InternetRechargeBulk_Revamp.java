package angular.testscripts.prereuisitesangular;

import angular.feature.C2SBulkTransferRevamp;
import angular.feature.InternetRechargeBulkRevamp;
import com.classes.BaseTest;
import com.classes.CONSTANT;
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

@ModuleManager(name = Module.PREREQUISITE_INTERNET_RECHARGE_BULK_REVAMP)
public class PreRequisite_InternetRechargeBulk_Revamp extends BaseTest {

    public PreRequisite_InternetRechargeBulk_Revamp() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_InternetRechargeBulkDaily(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_01_Test_InternetRechargeBulkDaily";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampDaily(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_InternetRechargeBulkWeekly(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_02_Test_InternetRechargeBulkWeekly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampWeekly(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_InternetRechargeBulkMonthly(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_03_Test_InternetRechargeBulkMonthly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampMonthly(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_InternetRechargeBulkInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_04_Test_InternetRechargeBulkInvalidMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_InternetRechargeBulkNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_05_Test_InternetRechargeBulkNegativeAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampNegativeAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_06_Test_InternetRechargeBulkZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_06_Test_InternetRechargeBulkZeroAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampZeroAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_07_Test_InternetRechargeBulkBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_07_Test_InternetRechargeBulkBlankAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_08_Test_InternetRechargeBulkBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_08_Test_InternetRechargeBulkBlankMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_09_Test_InternetRechargeBulkBlankSubService(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_09_Test_InternetRechargeBulkBlankSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankSubService(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_10_Test_InternetRechargeBulkBlankNotificationMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_10_Test_InternetRechargeBulkBlankNotificationMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankNotificationMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_InternetRechargeBulkInvalidNotificationMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_11_Test_InternetRechargeBulkInvalidNotificationMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampInvalidNotificationMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_12_Test_InternetRechargeBulkInvalidSubService(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_12_Test_InternetRechargeBulkInvalidSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampInvalidSubService(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_13_Test_InternetRechargeBulkAlphanumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_13_Test_InternetRechargeBulkAlphanumericAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampAlphanumericAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_14_Test_InternetRechargeBulkAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_14_Test_InternetRechargeBulkAlphanumericMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampAlphanumricMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_15_Test_InternetRechargeBulkResetButton(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_15_Test_InternetRechargeBulkResetButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampResetButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_16_Test_InternetRechargeBulkInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_16_Test_InternetRechargeBulkInvalidPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampInvalidPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_17_Test_InternetRechargeBulkBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_17_Test_InternetRechargeBulkBlankPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_18_Test_InternetRechargeBulkBlankScheduleDate(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_18_Test_InternetRechargeBulkBlankScheduleDate";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankScheduleDate(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_19_Test_InternetRechargeBulkBlankOccurrence(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_19_Test_InternetRechargeBulkBlankOccurrence";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankOccurrence(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_20_Test_InternetRechargeBulkBlankNoofIterations(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_20_Test_InternetRechargeBulkBlankNoofIterations";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankNoofIterations(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_21_Test_InternetRechargeBulkWithoutUploadingFile(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_21_Test_InternetRechargeBulkWithoutUploadingFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC21");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampWithoutUploadingFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_22_Test_InternetRechargeBulkTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_22_Test_InternetRechargeBulkTemplatewithoutData";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC22");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampTemplatewithoutData(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_23_Test_InternetRechargeBulkBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_23_Test_InternetRechargeBulkBlankTemplateFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC23");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampBlankTemplateFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_24_Test_InternetRechargeBulkInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {

        final String methodName = "TC_24_Test_InternetRechargeBulkInvalidFileType";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC24");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampInvalidFileType(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_25_Test_InternetRechargeBulkCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_25_Test_InternetRechargeBulkCopyButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPINTBRC25");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            InternetRechargeBulkRevamp InternetRechargeBulk = new InternetRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                InternetRechargeBulk.performInternetRechargeBulkRevampCopyButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("Bulk Internet Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
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




}


