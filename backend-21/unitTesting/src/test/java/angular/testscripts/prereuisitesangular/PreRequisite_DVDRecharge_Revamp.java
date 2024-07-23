package angular.testscripts.prereuisitesangular;


import angular.feature.DVDRechargeRevamp;
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

@ModuleManager(name = Module.PREREQUISITE_DVD_VOUCHER_REVAMP)
public class PreRequisite_DVDRecharge_Revamp extends BaseTest {

    @Test(dataProvider = "categoryData")
    public void TC_01_Test_DVDRecharge(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_01_Test_DVDRecharge";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRecharge(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_02_Test_DVDRechargeCopyButton(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_02_Test_DVDRechargeCopyButton";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeCopyButton(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_03_Test_DVDRechargeInvalidPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_03_Test_DVDRechargeInvalidPIN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeInvalidPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_04_Test_DVDRechargeBlankPIN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_04_Test_DVDRechargeBlankPIN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankPIN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_05_Test_DVDRechargeBlankMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_05_Test_DVDRechargeBlankMSISDN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_06_Test_DVDRechargeBlankQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_06_Test_DVDRechargeBlankQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_07_Test_DVDRechargeBlankVoucherType(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_07_Test_DVDRechargeBlankVoucherType";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankVoucherType(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_08_Test_DVDRechargeInvalidMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_08_Test_DVDRechargeInvalidMSISDN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeInvalidMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_09_Test_DVDRechargeNegativeQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_09_Test_DVDRechargeNegativeQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeNegativeQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_10_Test_DVDRechargeAlphanumericMSISDN(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_10_Test_DVDRechargeAlphanumericMSISDN";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeAlphanumericMSISDN(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_11_Test_DVDRechargeAddSlot(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_11_Test_DVDRechargeAddSlot";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeAddSlots(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_12_Test_DVDRechargeBlankDenomination(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_12_Test_DVDRechargeBlankDenomination";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankDenomination(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_13_Test_DVDRechargeBlankVoucherProfile(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_13_Test_DVDRechargeBlankVoucherProfile";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeBlankVoucherProfile(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    public void TC_14_Test_DVDRechargeZeroQuantity(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_14_Test_DVDRechargeZeroQuantity";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeZeroQuantity(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    public void TC_15_Test_DVDRechargeDeleteSlot(String ParentCategory, String FromCategory, String PIN, String service) {
        final String methodName = "TC_15_Test_DVDRechargeDeleteSlot";
        Log.startTestCase(methodName, ParentCategory, FromCategory, PIN, service);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPDVDRC15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getDescription(), FromCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2SRECHARGE_REVAMP, FromCategory, EventsI.C2S_RECHARGE_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            DVDRechargeRevamp DVDRecharge = new DVDRechargeRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                DVDRecharge.performDVDRechargeDeleteSlot(ParentCategory, FromCategory, PIN, service);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("DVD Recharge is not allowed to category[" + FromCategory + "].");
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
