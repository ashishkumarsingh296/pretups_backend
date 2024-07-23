package angular.testscripts.prereuisitesangular;

import angular.feature.C2SBulkTransferRevamp;
import angular.feature.GiftRechargeBulkRevamp;
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

@ModuleManager(name = Module.PREREQUISITE_GIFT_RECHARGE_BULK_REVAMP)
public class PreRequisite_GiftRechargeBulk_Revamp extends BaseTest {

    public PreRequisite_GiftRechargeBulk_Revamp() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_GiftRechargeBulkDaily(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_01_Test_GiftRechargeBulkDaily";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampDaily(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_GiftRechargeBulkWeekly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_02_Test_GiftRechargeBulkWeekly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampWeekly(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_GiftRechargeBulkMonthly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_03_Test_GiftRechargeBulkMonthly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampMonthly(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_GiftRechargeBulkInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_04_Test_GiftRechargeBulkInvalidMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_GiftRechargeBulkNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_05_Test_GiftRechargeBulkNegativeAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampNegativeAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }





    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_06_Test_GiftRechargeBulkZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_06_Test_GiftRechargeBulkZeroAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampZeroAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_07_Test_GiftRechargeBulkBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_07_Test_GiftRechargeBulkBlankAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_08_Test_GiftRechargeBulkBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_08_Test_GiftRechargeBulkBlankMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_09_Test_GiftRechargeBulkBlankSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_09_Test_GiftRechargeBulkBlankSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankSubService(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_10_Test_GiftRechargeBulkBlankLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_10_Test_GiftRechargeBulkBlankLanguageCode";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankLanguageCode(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_GiftRechargeBulkInvalidLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_11_Test_GiftRechargeBulkInvalidLanguageCode";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampInvalidLanguageCode(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_12_Test_GiftRechargeBulkInvalidSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_12_Test_GiftRechargeBulkInvalidSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampInvalidSubService(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_13_Test_GiftRechargeBulkBlankGifterMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_13_Test_GiftRechargeBulkBlankGifterMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankGifterMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_14_Test_GiftRechargeBulkBlankGifterName(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_14_Test_GiftRechargeBulkBlankGifterName";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankGifterName(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_15_Test_GiftRechargeBulkNumericGifterName(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_15_Test_GiftRechargeBulkNumericGifterName";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampNumericGifterName(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_16_Test_GiftRechargeBulkAlphabeticGifterMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_16_Test_GiftRechargeBulkAlphabeticGifterMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampAlphabeticGifterMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_17_Test_GiftRechargeBulkAlphanumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_17_Test_GiftRechargeBulkAlphanumericAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampAlphanumericAmount(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_18_Test_GiftRechargeBulkAlphamumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_18_Test_GiftRechargeBulkAlphanumericMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampAlphanumericMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_19_Test_GiftRechargeBulkResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_19_Test_GiftRechargeBulkResetButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampResetButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_20_Test_GiftRechargeBulkInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_20_Test_GiftRechargeBulkInvalidPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampInvalidPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_21_Test_GiftRechargeBulkBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_21_Test_GiftRechargeBulkBlankPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC21");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_22_Test_GiftRechargeBulkBlankScheduleDate(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_22_Test_GiftRechargeBulkBlankScheduleDate";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC22");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankScheduleDate(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_23_Test_GiftRechargeBulkBlankOccurrence(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_23_Test_GiftRechargeBulkBlankOccurrence";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC23");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankOccurrence(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_24_Test_GiftRechargeBulkBlankNoofIterations(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_24_Test_GiftRechargeBulkBlankNoofIterations";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC24");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankNoofIterations(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_25_Test_GiftRechargeBulkWithoutUploadingFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_25_Test_GiftRechargeBulkWithoutUploadingFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC25");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampWithoutUploadingFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_26_Test_GiftRechargeBulkTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_26_Test_GiftRechargeBulkTemplatewithoutData";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC26");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampTemplatewithoutData(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_27_Test_GiftRechargeBulkBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_27_Test_GiftRechargeBulkBlankTemplateFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC27");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampBlankTemplateFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_28_Test_GiftRechargeBulkInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_28_Test_GiftRechargeBulkInvalidFileType";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC28");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampInvalidFileType(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_29_Test_GiftRechargeBulkCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_29_Test_GiftRechargeBulkCopyButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPGFTBRC29");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            GiftRechargeBulkRevamp GiftRechargeBulk = new GiftRechargeBulkRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                GiftRechargeBulk.performGiftRechargeBulkRevampCopyButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
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
