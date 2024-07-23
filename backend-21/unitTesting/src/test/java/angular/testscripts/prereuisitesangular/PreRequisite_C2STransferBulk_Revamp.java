package angular.testscripts.prereuisitesangular;

import angular.feature.C2SBulkTransferRevamp;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
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

@ModuleManager(name = Module.PREREQUISITE_C2S_BULK_TRANSFER_REVAMP)
public class PreRequisite_C2STransferBulk_Revamp extends BaseTest {

    public PreRequisite_C2STransferBulk_Revamp() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_C2SBULKTRANSFER;
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_C2SBulkTransferDaily(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_01_Test_C2SBulkTransferDaily";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferDaily(ParentCategory, FromCategory, PIN, service);
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
    public void TC_02_Test_C2SBulkTransferWeekly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_02_Test_C2SBulkTransferWeekly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferWeekly(ParentCategory, FromCategory, PIN, service);
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
    public void TC_03_Test_C2SBulkTransferMonthly(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_03_Test_C2SBulkTransferMonthly";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferMonthly(ParentCategory, FromCategory, PIN, service);
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
    public void TC_04_Test_C2SBulkTransferInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_04_Test_C2SBulkTransferInvalidMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
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
    public void TC_05_Test_C2SBulkTransferNegativeAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_05_Test_C2SBulkTransferNegativeAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferNegativeAmount(ParentCategory, FromCategory, PIN, service);
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
    public void TC_06_Test_C2SBulkTransferZeroAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_06_Test_C2SBulkTransferZeroAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferZeroAmount(ParentCategory, FromCategory, PIN, service);
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
    public void TC_07_Test_C2SBulkTransferBlankAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_07_Test_C2SBulkTransferBlankAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankAmount(ParentCategory, FromCategory, PIN, service);
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
    public void TC_08_Test_C2SBulkTransferBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_08_Test_C2SBulkTransferBlankMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankMSISDN(ParentCategory, FromCategory, PIN, service);
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
    public void TC_09_Test_C2SBulkTransferBlankSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_09_Test_C2SBulkTransferBlankSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankSubService(ParentCategory, FromCategory, PIN, service);
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
    public void TC_10_Test_C2SBulkTransferBlankLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_10_Test_C2SBulkTransferBlankLanguageCode";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankLanguageCode(ParentCategory, FromCategory, PIN, service);
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
    public void TC_11_Test_C2SBulkTransferInvalidLanguageCode(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_11_Test_C2SBulkTransferInvalidLanguageCode";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferInvalidLanguageCode(ParentCategory, FromCategory, PIN, service);
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
    public void TC_12_Test_C2SBulkTransferInvalidSubService(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_12_Test_C2SBulkTransferInvalidSubService";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferInvalidSubService(ParentCategory, FromCategory, PIN, service);
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
    public void TC_13_Test_C2SBulkTransferAlphanumericAmount(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_13_Test_C2SBulkTransferAlphanumericAmount";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferAlphanumericAmount(ParentCategory, FromCategory, PIN, service);
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
    public void TC_14_Test_C2SBulkTransferAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_14_Test_C2SBulkTransferAlphanumericMSISDN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferAlphanumericMSISDN(ParentCategory, FromCategory, PIN, service);
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
    public void TC_15_Test_C2SBulkTransferResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_15_Test_C2SBulkTransferResetButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferResetButton(ParentCategory, FromCategory, PIN, service);
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
    public void TC_16_Test_C2SBulkTransferInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_16_Test_C2SBulkTransferInvalidPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferInvalidPIN(ParentCategory, FromCategory, PIN, service);
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
    public void TC_17_Test_C2SBulkTransferBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_17_Test_C2SBulkTransferBlankPIN";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankPIN(ParentCategory, FromCategory, PIN, service);
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
    public void TC_18_Test_C2SBulkTransferBlankScheduleDate(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "A_18_Test_C2SBulkTransferBlankScheduleDate";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankScheduleDate(ParentCategory, FromCategory, PIN, service);
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
    public void TC_19_Test_C2SBulkTransferBlankOccurrence(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_19_Test_C2SBulkTransferBlankOccurrence";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankOccurrence(ParentCategory, FromCategory, PIN, service);
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
    public void TC_20_Test_C2SBulkTransferBlankNoofIterations(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_20_Test_C2SBulkTransferBlankNoofIterations";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankNoofIterations(ParentCategory, FromCategory, PIN, service);
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
    public void TC_21_Test_C2SBulkTransferWithoutUploadingFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_21_Test_C2SBulkTransferWithoutUploadingFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC21");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferWithoutUploadingFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



//    @Test(dataProvider = "categoryData")
//    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
//    public void TC_22_Test_C2SBulkTransferTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
//       final String methodName = "TC_22_Test_C2SBulkTransferTemplatewithoutData";
//        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
//       CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC22");
//        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
//
//        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
//            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
//            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);
//
//            if (webAccessAllowed.equals("Y")) {
//                C2SBulkTransfer.performC2SBulkTransferTemplatewithoutData(ParentCategory, FromCategory, PIN, service);
//            } else {
//                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
//            }
//        } else {
//            Assertion.assertSkip("C2S Bulk Transfer is not allowed to category[" + FromCategory + "].");
//       }
//        Assertion.completeAssertions();
//        Log.endTestCase(methodName);
//    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_23_Test_C2SBulkTransferBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_23_Test_C2SBulkTransferBlankTemplateFile";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC23");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferBlankTemplateFile(ParentCategory, FromCategory, PIN, service);
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
    public void TC_24_Test_C2SBulkTransferInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_24_Test_C2SBulkTransferInvalidFileType";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC24");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferInvalidFileType(ParentCategory, FromCategory, PIN, service);
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
    public void TC_25_Test_C2SBulkTransferCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_25_Test_C2SBulkTransferCopyButton";
        Log.startTestCase(methodName, ParentCategory,FromCategory,PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2SBRC25");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory,EventsI.C2S_RECHARGE_BULK_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2SBulkTransferRevamp C2SBulkTransfer = new C2SBulkTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2SBulkTransfer.performC2SBulkTransferCopyButton(ParentCategory, FromCategory, PIN, service);
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
        int chnlCount = ExcelUtility.getRowCount()-4;
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
