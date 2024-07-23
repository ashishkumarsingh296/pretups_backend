package angular.testscripts.prereuisitesangular;

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
import angular.feature.DVDBulkRechargeRevamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

@ModuleManager(name = Module.PREREQUISITE_DVD_VOUCHER_BULK_REVAMP)
public class PreRequisite_DVDBulkRecharge_Revamp extends BaseTest {

    public PreRequisite_DVDBulkRecharge_Revamp() {
        CHROME_OPTIONS = CONSTANT.CHROME_OPTION_DVDBULKRECHARGE;
    }

    @Test(dataProvider = "categoryData")
    public void TC_01_Test_DVDBulkRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_01_Test_DVDBulkRecharge";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRecharge(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_02_Test_DVDBulkRechargeResetButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_02_Test_DVDBulkRechargeResetButton";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeResetButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    public void TC_03_Test_DVDBulkRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_03_Test_DVDBulkRechargeCopyButton";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeCopyButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_04_Test_DVDBulkRechargeInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_04_Test_DVDBulkRechargeInvalidMSISDN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_05_Test_DVDBulkRechargeNegativeQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_05_Test_DVDBulkRechargeNegativeQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeNegativeQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    public void TC_06_Test_DVDBulkRechargeBlankQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_06_Test_DVDBulkRechargeBlankQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_07_Test_DVDBulkRechargeBlankVoucherType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_07_Test_DVDBulkRechargeBlankVoucherType";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankVoucherType(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_08_Test_DVDBulkRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_08_Test_DVDBulkRechargeBlankPIN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_09_Test_DVDBulkRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_09_Test_DVDBulkRechargeInvalidPIN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeInvalidPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_10_Test_DVDBulkRechargeBlankSegment(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_10_Test_DVDBulkRechargeBlankSegment";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankSegment(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    public void TC_11_Test_DVDBulkRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_11_Test_DVDBulkRechargeBlankDenomination";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankDenomination(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_12_Test_DVDBulkRechargeBlankProfileID(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_12_Test_DVDBulkRechargeBlankProfileID";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankProfileID(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_13_Test_DVDBulkRechargeZeroQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_13_Test_DVDBulkRechargeZeroQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeZeroQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_14_Test_DVDBulkRechargeInvalidFileType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_14_Test_DVDBulkRechargeInvalidFileType";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeInvalidFileType(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_15_Test_DVDBulkRechargeTemplatewithoutData(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_15_Test_DVDBulkRechargeTemplatewithoutData";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeTemplatewithoutData(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_16_Test_DVDBulkRechargeBlankTemplateFile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_16_Test_DVDBulkRechargeBlankTemplateFile";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDBRC16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDBulkRechargeRevamp DVDRecharge = new DVDBulkRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDBulkRechargeBlankTemplateFile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Bulk Recharge is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }





    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
        String DVDRechargeCode = _masterVO.getProperty("DVDRecharge");
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
            if (aList.contains(DVDRechargeCode)) {
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
                Data[j][3] = DVDRechargeCode;
                j++;
            }
        }

        return Data;
    }

    /* ------------------------------------------------------------------------------------------------ */



}
