package angular.testscripts.prereuisitesangular;

import angular.feature.O2CTransferRevampChannelAdmin;
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


@ModuleManager(name = Module.PREREQUISITE_O2C_TRANSFER_REVAMP)
public class PreRequisite_O2CTransferChannelAdmin_Revamp extends BaseTest {


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_O2CTransferbyMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_01_Test_O2CTransferbyMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  O2CBulkTransfer.PerformO2CApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag|| secondApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_O2CTransferbyLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_02_Test_O2CTransferbyLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF5");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  O2CBulkTransfer.PerformO2CApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag|| secondApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_O2CTransferbyUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_03_Test_O2CTransferbyUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF5");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByUserName(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  O2CBulkTransfer.PerformO2CApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag|| secondApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_O2CTransferAlphanumericMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_04_Test_O2CTransferAlphanumericMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferAlphanumericMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_O2CTransferBlankMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_05_Test_O2CTransferBlankMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferBlankMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_06_Test_O2CTransferInvalidMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_06_Test_O2CTransferBlankMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferInvalidMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_07_Test_O2CTransferBlankLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_07_Test_O2CTransferBlankLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferBlankLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_08_Test_O2CTransferResetButton(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_08_Test_O2CTransferResetButton";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferResetButton(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_09_Test_O2CTransferResetButtonSearchBy(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_09_Test_O2CTransferResetButtonSearchBy";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferResetButtonSearchBy(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_10_Test_O2CTransferAlphabeticReferenceNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_10_Test_O2CTransferAlphabeticReferenceNumber";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferAlphabeticReferenceNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_O2CTransferAlphabeticPaymentInstrumentNo(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_11_Test_O2CTransferAlphabeticPaymentInstrumentNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferAlphabeticPaymentInstrumentNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_12_Test_O2CTransferBlankAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_12_Test_O2CTransferBlankAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferBlankAmount(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_13_Test_O2CTransferBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_13_Test_O2CTransferBlankRemarks";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferBlankRemarks(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_14_Test_O2CTransferNegativeAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_14_Test_O2CTransferNegativeAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferNegativeAmount(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_15_Test_O2CTransferBlankPIN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_15_Test_O2CTransferBlankPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferBlankPIN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_16_Test_O2CTransferInvalidPIN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_16_Test_O2CTransferInvalidPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferInvalidPIN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_17_Test_O2CTransferInvalidLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_17_Test_O2CTransferInvalidLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);

            if (webAccessAllowed.equals("Y")) {
                O2CBulkTransfer.PerformO2CTransferInvalidLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_18_Test_O2CTransferRejectLevel1(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_18_Test_O2CTransferRejectLevel1";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF21");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CReject1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
               } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_19_Test_O2CTransferRejectLevel2(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_19_Test_O2CTransferRejectLevel2";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF22");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
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
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_18_Test_O2CTransferRejectLevel3(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_18_Test_O2CTransferRejectLevel3";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        Long netPayableAmount = null;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF23");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
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
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_19_Test_O2CTransferApproval1DuplicateExtTxnNo(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_19_Test_O2CTransferApproval1DuplicateExtTxnNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF2");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if (actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval1DuplicateExtTxnNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            }
                else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_20_Test_O2CTransferInvalidSearchBy(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_20_Test_O2CTransferInvalidSearchBy";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if (actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApprovalInvalidSearchBy(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            }
            else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_21_Test_PerformO2CTransferApproval1SearchMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_21_Test_PerformO2CTransferApproval1SearchMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF26");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1SearchMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_22_Test_PerformO2CTransferApproval1SearchUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_22_Test_PerformO2CTransferApproval1SearchUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF27");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1SearchUserName(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
               } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_22_Test_PerformO2CApproval1SearchDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_22_Test_PerformO2CTransferApproval1SearchUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF28");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1SearchDate(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_23_Test_PerformO2CApproval1BlankEnternalTxnNo(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_23_Test_PerformO2CApproval1BlankEnternalTxnNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF29");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1BlankExternalTxnNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_24_Test_PerformO2CApproval1BlankExternalTxnDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_24_Test_PerformO2CApproval1BlankExternalTxnDate";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF30");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = O2CBulkTransfer.PerformO2CApproval1BlankExternalTxnDate(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_25_Test_PerformO2CApproval1AlphanumericExternalTxnNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_25_Test_PerformO2CApproval1AlphanumericExternalTxnNumber";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF31");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval1AlphanumericExternalTxnNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_26_Test_PerformO2CApproval1AlphanumericReferenceNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_26_Test_PerformO2CApproval1AlphanumericReferenceNumber";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF32");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.O2C_TRANSFER_REVAMP, opCategoryName, EventsI.O2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            O2CTransferRevampChannelAdmin O2CBulkTransfer = new O2CTransferRevampChannelAdmin(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = O2CBulkTransfer.PerformO2CTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    O2CBulkTransfer.PerformO2CApproval1AlphanumericReferenceNumber(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("O2C Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }









    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */
    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {
        String O2CTransferCode = _masterVO.getProperty("O2CTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");


        ArrayList<String> opUserData = new ArrayList<>();
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
        ArrayList<String> alist1 = new ArrayList<>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(O2CTransferCode)) {
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
        Object[][] Data = new Object[userCounter][3];
        for (int i = 1, j = 0; i <= chnlCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            if (alist1.contains(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i))) {
                Data[j][1] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i);
                Data[j][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, i);
                Data[j][0] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
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
        Object[][] o2ctmpData = new Object[countTotal][4];
        for (int i = 0, j = 0, k = 0; j < countTotal; j++) {
            o2ctmpData[j][0] = Data[k][0];
            o2ctmpData[j][1] = Data[k][1];
            o2ctmpData[j][2] = Data[k][2];
            o2ctmpData[j][3] = ProductObject[i];
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


        Object[][] o2cData =new Object[countTotal][8];

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
