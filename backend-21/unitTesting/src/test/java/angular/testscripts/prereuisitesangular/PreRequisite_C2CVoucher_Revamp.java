package angular.testscripts.prereuisitesangular;

import angular.feature.C2CTransferBulkRevamp;
import angular.feature.C2CVoucherRevamp;
import com.Features.O2CTransfer;
import com.Features.VMS;
import com.Features.mapclasses.OperatorToChannelMap;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UserAccess;
import com.commons.*;
import com.databuilder.BuilderLogic;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
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
import java.util.HashMap;
import java.util.List;

@ModuleManager(name = Module.PREREQUISITE_C2C_VOUCHER_REVAMP)
public class PreRequisite_C2CVoucher_Revamp extends BaseTest {

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_01_Test_C2CVoucherTransferandApproval(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_01_Test_C2CVoucherTransferandApproval";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransfer(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
   // @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void B_01_Test_C2CVoucherTransferandReject(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "B_01_Test_C2CVoucherTransferandReject";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferandReject(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_02_Test_C2CVoucherTransferBlankSearchBuyerName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_02_Test_C2CVoucherTransferBlankSearchBuyerName";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferBlankSearchBuyerAndMsisdn(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_03_Test_C2CVoucherTransferBlankMSISDNWithBuyerMobile(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_03_Test_C2CVoucherTransferBlankMSISDN";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferBlankMSISDNWithBuyerMobile(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_04_Test_C2CVoucherTransferBlankCategoryWithUserName(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_04_Test_C2CVoucherTransferBlankCategoryWithUserName";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferBlankCategoryWithUserName(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_05_Test_C2CVoucherTransferBlankUserNameWithCategory(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_05_Test_C2CVoucherTransferBlankUserNameWithCategory";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferBlankUserNameWithCategory(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_06_Test_C2CVoucherTransferBlankCategoryWithLoginID(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_06_Test_C2CVoucherTransferBlankCategoryWithLoginID";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferBlankCategoryWithLoginID(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_07_Test_C2CVoucherTransferResetFields(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_07_Test_C2CVoucherTransferResetFields";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferResetFields(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_08_Test_C2CVoucherTransferInvalidMSISDNLength(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_08_Test_C2CVoucherTransferInvalidMSISDNLength";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferInvalidMSISDNLength(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_09_Test_C2CVoucherTransferInvalidMSISDN(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_09_Test_C2CVoucherTransferInvalidMSISDN";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferInvalidMSISDN(parentName, categoryName, msisdn2, PIN, categorCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_10_Test_C2CVoucherTransferBlankPaymentDate(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_10_Test_C2CVoucherTransferBlankPaymentDate";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankPaymentDate(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_11_Test_C2CVoucherTransferBlankPaymentType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_11_Test_C2CVoucherTransferBlankPaymentType";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankPaymentType(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_12_Test_C2CVoucherTransferBlankRemarks(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_12_Test_C2CVoucherTransferBlankRemarks";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankRemarks(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_13_Test_C2CVoucherTransferBlankToSerial(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_13_Test_C2CVoucherTransferBlankToSerial";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankToSerial(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_14_Test_C2CVoucherTransferInvalidToSerial(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_14_Test_C2CVoucherTransferInvalidToSerial";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithInvalidToSerial(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_15_Test_C2CVoucherTransferInvalidFromSerial(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_15_Test_C2CVoucherTransferInvalidFromSerial";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithInvalidFromSerial(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_16_Test_C2CVoucherTransferBlankFromSerial(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_16_Test_C2CVoucherTransferBlankFromSerial";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankFromSerial(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_17_Test_C2CVoucherTransferBlankDenomination(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_17_Test_C2CVoucherTransferBlankDenomination";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankDenomination(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_18_Test_C2CVoucherTransferBlankSegment(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_18_Test_C2CVoucherTransferBlankSegment";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankSegment(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_19_Test_C2CVoucherTransferBlankType(String loginID, String password, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String Touser,String Fromuser,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_19_Test_C2CVoucherTransferBlankType";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVT19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherTransferWithBlankType(loginID,password, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,Touser,Fromuser,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "userData")
    public Object[][] TestDataFeed(){
        String C2CTransferCode = _masterVO.getProperty("C2CVoucherTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");
		HashMap<String,String> UserAp=new HashMap<String,String>();
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int rowCount1 = ExcelUtility.getRowCount();
        for(int i=1;i<=rowCount1;i++) {
        	if(ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE,i).equals("DIST")) {
        		UserAp.put("LoginAp",ExcelUtility.getCellData(0, ExcelI.LOGIN_ID,i));
        		UserAp.put("PassAp", ExcelUtility.getCellData(0, ExcelI.PASSWORD,i));
        	}
        }
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        ArrayList<String> alist1 = new ArrayList<String>();
        ArrayList<String> alist2 = new ArrayList<String>();
        ArrayList<String> categorySize = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(C2CTransferCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
            }
        }
        ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.VOMS_DENOM_PROFILE);
		rowCount = ExcelUtility.getRowCount();
		ArrayList<ArrayList<String>> voucherData= new ArrayList<ArrayList<String>>();
		for (int i = 1; i <= rowCount; i++) {
				ArrayList<String> voucherTempData =new ArrayList<>();
				if(ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("P")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("PT")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("DT")||ExcelUtility.getCellData(0,ExcelI.VOMS_TYPE,i).equals("D")){
			
									
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_VOUCHER_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_TYPE, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_PROFILE_NAME, i));
				voucherTempData.add(ExcelUtility.getCellData(0, ExcelI.VOMS_MRP, i));
				
				voucherData.add(voucherTempData);
			
				}		
		}
		
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
        
        

        int totalObjectCounter = 0;
        for (String s : alist1) {
            int categorySizeCounter = 0;
            for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(s)) {
                    categorySizeCounter++;
                }
            }
            categorySize.add("" + categorySizeCounter);
            totalObjectCounter = totalObjectCounter + categorySizeCounter;
        }
        Object[][] Data = new Object[totalObjectCounter][11];
        for (int j = 0, k = 0; j < alist2.size(); j++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserMSISDN = null,ChannelUserLoginId=null,ChannelUserPIN=null,ChannelUserPASS=null;
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                    ChannelUserMSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                    ChannelUserLoginId=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                    ChannelUserPIN=ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    ChannelUserPASS=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                    break;
                }
            }
            
            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                    Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                    Data[k][1] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, excelCounter);
                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                    Data[k][4] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                    Data[k][7] = ChannelUserMSISDN;
                    Data[k][8] =alist1.get(j);
                    Data[k][9] = alist2.get(j);
                    Data[k][10] = ExcelUtility.getCellData(0, ExcelI.USER_NAME,excelCounter);
                    k++;
                }
            }
        }
        
        int countTotal = voucherData.size();
        Object[][] c2cData = new Object[countTotal][15];
        for (int i = 0; i < countTotal; i++) {
        	
        	int counter_j=0;
        	
        	for(int j=0;j<Data[0].length;j++) {
        		c2cData[i][counter_j++]=Data[i][j];
        	}
        	
        	for(int j=0;j<voucherData.get(i).size();j++) {
        		c2cData[i][counter_j++]=voucherData.get(0).get(j);
        	}
       
        }
        
        return c2cData;
   
    }

}
