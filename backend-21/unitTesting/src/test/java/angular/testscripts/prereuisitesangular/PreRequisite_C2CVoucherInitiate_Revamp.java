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
@ModuleManager(name = Module.PREREQUISITE_C2C_VOUCHER_Initiate_REVAMP)
public class PreRequisite_C2CVoucherInitiate_Revamp extends BaseTest {

    @Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_01_Test_C2CVoucherInitiateandApproval(String loginID, String categoryCode, String msisdn, String PIN, String tocategory, String categoryName, String parentName,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_01_Test_C2CVoucherInitiateandApproval";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName, EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherinitiate(loginID,categoryCode, msisdn,  PIN, tocategory, categoryName,parentName,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + parentName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + parentName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
  //@Test(dataProvider = "userData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void B_01_Test_C2CVoucherInitiateandReject(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "B_01_Test_C2CVoucherInitiateandReject";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateandReject(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_02_Test_C2CVoucherInitiateBlankSearchBuyerName(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_02_Test_C2CVoucherInitiateBlankSearchBuyerName";
        Log.startTestCase(methodName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateBlankSearchBuyerAndMsisdn(parentName, categoryName, msisdn2, PIN, categorCode);
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
    public void A_03_Test_C2CVoucherInitiateBlankMSISDNWithBuyerMobile(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_03_Test_C2CVoucherInitiateBlankMSISDNWithBuyerMobile";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateBlankMSISDNWithBuyerMobile(parentName, categoryName, msisdn2, PIN, categorCode);
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
    public void A_04_Test_C2CVoucherInitiateBlankCategoryWithUserName(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_04_Test_C2CVoucherInitiateBlankCategoryWithUserName";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateBlankCategoryWithUserName(parentName, categoryName, msisdn2, PIN, categorCode,userName);
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
    public void A_05_Test_C2CVoucherInitiateBlankUserNameWithCategory(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_05_Test_C2CVoucherInitiateBlankUserNameWithCategory";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateBlankUserNameWithCategory(parentName, categoryName, msisdn2, PIN, categorCode,userName);
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
    public void A_06_Test_C2CVoucherInitiateBlankCategoryWithLoginID(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_06_Test_C2CVoucherInitiateBlankCategoryWithLoginID";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateBlankCategoryWithLoginID(parentName, categoryName, msisdn2, PIN, categorCode,loginID);
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
    public void A_07_Test_C2CVoucherInitiateResetFields(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_07_Test_C2CVoucherInitiateResetFields";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateResetFields(parentName, categoryName, msisdn2, PIN, categorCode,loginID);
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
    public void A_08_Test_C2CVoucherInitiateInvalidMSISDNLength(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_08_Test_C2CVoucherInitiateInvalidMSISDNLength";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateInvalidMSISDNLength(parentName, categoryName, msisdn2, PIN, categorCode);
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
    public void A_09_Test_C2CVoucherInitiateInvalidMSISDN(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_09_Test_C2CVoucherInitiateInvalidMSISDN";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateInvalidMSISDN(parentName, categoryName, msisdn2, PIN, categorCode);
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
    public void A_10_Test_C2CVoucherInitiateBlankPaymentDate(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_10_Test_C2CVoucherInitiateBlankPaymentDate";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankPaymentDate(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_11_Test_C2CVoucherInitiateBlankPaymentType(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_11_Test_C2CVoucherInitiateBlankPaymentType";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankPaymentType(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_12_Test_C2CVoucherInitiateBlankRemarks(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_12_Test_C2CVoucherInitiateBlankRemarks";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankRemarks(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_13_Test_C2CVoucherInitiateBlankQuantity(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_13_Test_C2CVoucherInitiateBlankQuantity";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankQuantity(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_14_Test_C2CVoucherInitiateQuantityInvalid(String loginID, String categoryCode, String msisdn, String PIN, String tocategory, String categoryName, String parentName,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_14_Test_C2CVoucherInitiateQuantityInvalid";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithInvalidQuantity(loginID,categoryCode, msisdn,  PIN, tocategory, categoryName,parentName,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_15_Test_C2CVoucherInitiateBlankDenomination(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
        final String methodName = "A_15_Test_C2CVoucherInitiateBlankDenomination";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankDenomination(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
    public void A_16_Test_C2CVoucherInitiateBlankSegment(String loginID, String categoryCode, String msisdn, String PIN, String parentName, String categoryName, String categorCode,String msisdn2,String userap,String userpass,String userName,String voucherType,String type,String activeProfile, String mrp) {
  
    final String methodName = "A_16_Test_C2CVoucherInitiateBlankSegment";
        Log.startTestCase(methodName);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CVI16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), parentName, categoryName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, parentName,EventsI.C2CBULKTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(parentName);
            C2CVoucherRevamp C2CVoucher = new C2CVoucherRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CVoucher.performC2CVoucherInitiateWithBlankSegment(loginID,categoryCode, msisdn,  PIN, parentName, categoryName,categorCode,msisdn2,userap, userpass,userName,voucherType,type,activeProfile, mrp);
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
        String C2CTransferCode = _masterVO.getProperty("C2CVoucherInitiateCode");
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
        for (String s : alist2) {
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
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist1.get(j))) {
                    ChannelUserMSISDN=ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                    ChannelUserLoginId=ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                    ChannelUserPIN=ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    ChannelUserPASS=ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                  
                }
            }

            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist2.get(j))) {
                    Data[k][0] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, excelCounter);
                    Data[k][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][3] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                    Data[k][4] = alist1.get(j);
                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter);
                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
                    Data[k][7] = ChannelUserMSISDN;
                    Data[k][8] = UserAp.get("LoginAp");
                    Data[k][9] = UserAp.get("PassAp");
                    Data[k][10] = ExcelUtility.getCellData(0, ExcelI.USER_NAME,excelCounter);
                    k++;
                }
            }
        }

        int countTotal = voucherData.size()-1;
        Object[][] c2cData = new Object[countTotal][15];
        for (int i = 0; i < countTotal; i++) {

            int counter_j=0;

            for(int j=0;j<Data[0].length;j++) {
                c2cData[i][counter_j++]=Data[i][j];
            }

            for(int j=0;j<voucherData.get(i).size();j++) {
                c2cData[i][counter_j++]=voucherData.get(i).get(j);
            }

        }

        return c2cData;

    }
   }
