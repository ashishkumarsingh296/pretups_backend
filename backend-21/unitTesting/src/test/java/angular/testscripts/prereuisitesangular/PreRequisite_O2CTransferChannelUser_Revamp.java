package angular.testscripts.prereuisitesangular;

import angular.feature.O2CTransferRevampChannelAdmin;
import angular.feature.O2CTransferRevampChannelUser;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
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
import java.util.Map;

@ModuleManager(name = Module.PREREQUISITE_O2C_INITIATE_BY_CU_REVAMP)
public class PreRequisite_O2CTransferChannelUser_Revamp extends BaseTest {

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_O2CTransferByCU(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_01_Test_O2CTransferByCU";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransferCU.PerformO2CTransferCU(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if (actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if (flag || firstApprov < quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if (flag || secondApprov < quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_O2CTransferByCUReject1(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_02_Test_O2CTransferByCUReject1";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF21");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransferCU.PerformO2CTransferCU(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CReject1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_O2CTransferByCUReject2(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_02_Test_O2CTransferByCUReject1";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF22");


        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransferCU.PerformO2CTransferCU(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CReject2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_O2CTransferByCUReject3(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_02_Test_O2CTransferByCUReject3";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF23");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransferCU.PerformO2CTransferCU(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  O2CBulkTransfer.PerformO2CApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CReject3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_O2CTransferByCUResetButton(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_05_Test_O2CTransferByCUResetButton";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU2");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUResetButton(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_06_Test_O2CTransferByCUAlphabeticReferenceNumber(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_06_Test_O2CTransferByCUAlphabeticReferenceNumber";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU3");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUAlphabeticReferenceNo(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_07_Test_O2CTransferByCUAlphabeticPaymentInstrumentNo(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_07_Test_O2CTransferByCUAlphabeticPaymentInstrumentNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU4");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUAlphabeticPaymentInstrumentNo(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_08_Test_O2CTransferByCUBlankAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_08_Test_O2CTransferByCUBlankAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU5");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUBlankAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_09_Test_O2CTransferByCUBlankRemarks(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_09_Test_O2CTransferByCUBlankRemarks";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU6");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUBlankRemarks(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_10_Test_O2CTransferByCUNegativeAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_10_Test_O2CTransferByCUNegativeAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU7");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUNegativeAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_O2CTransferByCUAlphanumericAmount(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_11_Test_O2CTransferByCUAlphanumericAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU8");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUAlphanumericAmount(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_12_Test_O2CTransferByCUBlankPIN(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_12_Test_O2CTransferByCUBlankPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU9");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUBlankPIN(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_13_Test_O2CTransferByCUInvalidPIN(String opCategoryName, String opLoginId, String opPassword, String opPin, String cpParentName, String chCategoryName, String chMsisdn, String productName, String chPin, String chLoginId, String chPassword) {
        final String methodName = "TC_13_Test_O2CTransferByCUInvalidPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRFCU10");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, chCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(chCategoryName);
            O2CTransferRevampChannelUser O2CBulkTransferCU = new O2CTransferRevampChannelUser(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                O2CBulkTransferCU.PerformO2CTransferCUInvalidPIN(opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName, chPin, chLoginId, chPassword);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + chCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + chCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }














    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */




    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {
        String O2CInitiateCode = _masterVO.getProperty("O2CInitiateCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");


        ArrayList<String> opUserData =new ArrayList<String>();
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.O2C_TRANSFER_REVAMP,EventsI.O2CTRANSFER_EVENT);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which O2C transfer is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(O2CInitiateCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
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
        Object[][] Data = new Object[userCounter][6];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                Data[j][3] = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                Data[j][4] = ExcelUtility.getCellData(0, ExcelI.LOGIN_ID, i);
                Data[j][5] = ExcelUtility.getCellData(0, ExcelI.PASSWORD, i);
                j++;
            }
        }

        /*
         * Store products from Product Sheet to Object.
         */
        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.PRODUCT_SHEET);
        int prodRowCount = ExcelUtility.getRowCount();
        Object[] ProductObject = new Object[prodRowCount];
        for (int i = 0, j = 1; i < prodRowCount; i++, j++) {
            ProductObject[i] = ExcelUtility.getCellData(0, ExcelI.PRODUCT_NAME, j);
        }

        /*
         * Creating combination of channel users for each product.
         */
        int countTotal = ProductObject.length * userCounter;
        Object[][] o2ctmpData = new Object[countTotal][7];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            o2ctmpData[j][0] = Data[k][0];
            o2ctmpData[j][1] = Data[k][1];
            o2ctmpData[j][2] = Data[k][2];
            o2ctmpData[j][3] = ProductObject[i];
            o2ctmpData[j][4] = Data[k][3];
            o2ctmpData[j][5] = Data[k][4];
            o2ctmpData[j][6] = Data[k][5];


            if (k < userCounter) {
                k++;
                if (k >= userCounter) {
                    k = 0;
                    i++;
                    if (i >= ProductObject.length)
                        i = 0;
                }
            } else {
                k = 0;
            }
        }


        Object[][] o2cData =new Object[countTotal][11];

        int counter_1=0;

        for(int k=0;k<o2ctmpData.length;k++) {
            int counter_2=0;

            for(int j=0;j<opUserData.size();j++)
                o2cData[counter_1][counter_2++]=opUserData.get(j);

            for(int l=0;l<o2ctmpData[0].length;l++)
                o2cData[counter_1][counter_2++]=o2ctmpData[k][l];

            counter_1++;
        }

        return o2cData;

    }


}
