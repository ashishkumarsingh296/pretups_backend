package angular.testscripts.prereuisitesangular;

import angular.feature.FOCTransferRevamp;
import com.classes.BaseTest;
import com.classes.CaseMaster;
import com.classes.UserAccessRevamp;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
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


@ModuleManager(name = Module.PREREQUISITE_FOC_TRANSFER_REVAMP)
public class PreRequisite_FOCTransfer_Revamp extends BaseTest {

/*
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_FOCTransferByMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_01_Test_FOCTransferbyMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(opCategoryName,netCode,"FOC_ODR_APPROVAL_LVL");

        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCRF4");

       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
//                if(flag|| secondApprov<quantity){
//                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
//  //                  FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
//                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    */

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_01_Test_FOCTransferByMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_01_Test_FOCTransferbyMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF4");

       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		}
            	else if(maxApprovalLevel == 2)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_FOCTransferByLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_02_Test_FOCTransferByLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF5");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		}
            	else if(maxApprovalLevel == 2)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


/*    
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_02_Test_FOCTransferByLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_02_Test_FOCTransferByLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCRF5");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
 //                   flag = FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
 //                   flag =  FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag|| secondApprov<quantity){
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
  //                  FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

*/

/*
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_FOCTransferByUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_03_Test_FOCTransferByUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
        Long quantity = _parser.getSystemAmount(_masterVO.getProperty("Quantity"));
        Long firstApprov = Long.parseLong(approvalLevel[0]);
        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCRF6");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByUserName(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
 //                   flag = FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag || firstApprov<quantity) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
//                    flag =  FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag|| secondApprov<quantity){
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
 //                   FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    */

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_03_Test_FOCTransferByUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_03_Test_FOCTransferByUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF6");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF3");
        CaseMaster CaseMaster4 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF4");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByUserName(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		}
            	else if(maxApprovalLevel == 2)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
        		}
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_04_Test_FOCTransferAlphanumericMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_04_Test_FOCTransferAlphanumericMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferAlphanumericMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_05_Test_FOCTransferBlankMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_05_Test_FOCTransferBlankMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_06_Test_FOCTransferInvalidMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_06_Test_FOCTransferInvalidMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferInvalidMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_07_Test_FOCTransferBlankLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_07_Test_FOCTransferBlankLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_08_Test_FOCTransferResetButton(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_08_Test_FOCTransferResetButton";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferResetButton(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_09_Test_FOCTransferResetButtonSearchBy(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_09_Test_FOCTransferResetButtonSearchBy";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferResetButtonSearchBy(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_10_Test_FOCTransferAlphabeticReferenceNumber(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_10_Test_FOCTransferAlphabeticReferenceNumber";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferAlphabeticReferenceNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    /*@Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_FOCTransferAlphabeticPaymentInstrumentNo(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_11_Test_FOCTransferAlphabeticPaymentInstrumentNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCRF14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferAlphabeticPaymentInstrumentNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }*/

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_11_Test_FOCTransferBlankUsername(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_11_Test_FOCTransferBlankUsername";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankUsername(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_12_Test_FOCTransferBlankAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_12_Test_FOCTransferBlankAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankAmount(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_13_Test_FOCTransferBlankRemarks(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_13_Test_FOCTransferBlankRemarks";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF16");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankRemarks(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_14_Test_FOCTransferNegativeAmount(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_14_Test_FOCTransferNegativeAmount";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferNegativeAmount(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_15_Test_FOCTransferBlankPIN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_15_Test_FOCTransferBlankPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferBlankPIN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_16_Test_FOCTransferInvalidPIN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_16_Test_FOCTransferInvalidPIN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF19");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferInvalidPIN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }



    /*@Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_17_Test_FOCTransferFuturePaymentDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_17_Test_FOCTransferFuturePaymentDate";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPO2CTRF17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

         if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferFuturePaymentDate(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }*/




    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_17_Test_FOCTransferInvalidLoginID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_17_Test_FOCTransferInvalidLoginID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF20");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferInvalidLoginID(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_18_Test_FOCTransferInvalidUsername(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_18_Test_FOCTransferInvalidUsername";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPFOCTRF21");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                FOCTransfer.PerformFOCTransferInvalidUsername(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_19_Test_FOCTransferRejectLevel1(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_19_Test_FOCTransferRejectLevel1";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
//        Boolean flag = false;
//        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
//        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
//        String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
//        Long firstApprov = Long.parseLong(approvalLevel[0]);
//        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1"); //to change
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF22");// to change

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCReject1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_20_Test_FOCTransferRejectLevel2(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_20_Test_FOCTransferRejectLevel2";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
//        Boolean flag = false;
//        String netCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
//        String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(chCategoryName, netCode);
//        String directO2CPreference = DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED");
//        Long firstApprov = Long.parseLong(approvalLevel[0]);
//        Long secondApprov = Long.parseLong(approvalLevel[1]);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF2");
        CaseMaster CaseMaster3 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF23");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
//                if(actual.equals("NEW")) {
//                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
//                    flag = FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
//                }
//                if(flag) {
//                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
//                    FOCTransfer.PerformFOCReject2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
//                }
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		}
            	else if(maxApprovalLevel == 2)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCReject2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    Log.info("FOC transfer MAX Approval is 2");
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCReject2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_21_Test_FOCTransferByMSISDNWithAlphanumericRefNoApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
    	CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF24");
    	final String methodName = "TC_21_Test_FOCTransferByMSISDNWithAlphanumericRefNoApproval";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel >= 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.performApprovalWithAlphanumericReferenceNumber(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		
                    }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_22_Test_FOCTransferByMSISDNWithAlphanumericExtTxnApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
    	CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF25");
    	final String methodName = "TC_22_Test_FOCTransferByMSISDNWithAlphanumericExtTxnApproval";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel >= 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.performApprovalWithAlphanumericExternalTxnNumber(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		
                    }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_23_Test_FOCTransferByMSISDNWithoutExtTxnApproval(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
    	CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF26");
    	final String methodName = "TC_23_Test_FOCTransferByMSISDNWithoutExtTxnApproval";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel >= 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.performApprovalWithoutExternalTxnNumber(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		
                    }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_24_Test_FOCTransferByMSISDNWithInvalidSearchByApprovalID(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
    	CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPFOCTRF27");
    	final String methodName = "TC_24_Test_FOCTransferByMSISDNWithInvalidSearchByApprovalID";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        String value = DBHandler.AccessHandler.getSystemPreference("FOC_ODR_APPROVAL_LVL");
        int maxApprovalLevel= Integer.parseInt(value);
       if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                if(maxApprovalLevel == 0)
        		{
            		Log.info("FOC transfer Approval is perform at FOC transfer itself");
        		}
            	if(maxApprovalLevel >= 1)
        		{
            		currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.performApprovalWithInvalidSearchByTransactionID(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);        		
                    }
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + opCategoryName + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("FOC Transfer is not allowed to category[" + opCategoryName + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    
/*
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

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster3.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag =  FOCTransfer.PerformFOCApproval2(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
                }
                if(flag) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster4.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCReject3(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if (actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApproval1DuplicateExtTxnNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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

*/
/*

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_20_Test_O2CTransferInvalidSearchBy(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_20_Test_O2CTransferInvalidSearchBy";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if (actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    FOCTransfer.PerformFOCApprovalInvalidSearchBy(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
*/

/*

    @Test(dataProvider = "categoryData")
    //@TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void TC_21_Test_PerformFOCTransferApproval1SearchMSISDN(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_21_Test_PerformFOCTransferApproval1SearchMSISDN";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF26");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1SearchMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_22_Test_PerformFOCTransferApproval1SearchUserName(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_22_Test_PerformFOCTransferApproval1SearchUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF27");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1SearchUserName(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_22_Test_PerformFOCApproval1SearchDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_22_Test_PerformFOCTransferApproval1SearchUserName";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF28");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1SearchDate(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_23_Test_PerformFOCApproval1BlankEnternalTxnNo(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_23_Test_PerformFOCApproval1BlankEnternalTxnNo";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF29");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1BlankExternalTxnNo(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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
    public void TC_24_Test_PerformFOCApproval1BlankEnternalTxnDate(String opCategoryName,String opLoginId,String opPassword,String opPin,String cpParentName,String chCategoryName,String chMsisdn, String productName){
        final String methodName = "TC_24_Test_PerformFOCApproval1BlankEnternalTxnDate";
        Log.startTestCase(methodName, opCategoryName, opLoginId, opPassword, opPin, cpParentName, chCategoryName, chMsisdn, productName);
        Boolean flag = false;
        CaseMaster CaseMaster1 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF1");
        CaseMaster CaseMaster2 = _masterVO.getCaseMasterByID("PREVAMPO2CTRF30");

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.FOC_COMMISSION, opCategoryName, EventsI.FOC_COMMISSION)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(opCategoryName);
            FOCTransferRevamp FOCTransfer = new FOCTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                currentNode = test.createNode(MessageFormat.format(CaseMaster1.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                Map<String, String> map  = FOCTransfer.PerformFOCTransferByMSISDN(opCategoryName, chCategoryName, chMsisdn, productName, opPin);
                String txnId = map.get("TRANSACTION_ID");
                String actual = map.get("INITIATE_MESSAGE");
                if(actual.equals("NEW")) {
                    currentNode = test.createNode(MessageFormat.format(CaseMaster2.getExtentCase(), cpParentName, chCategoryName, productName)).assignCategory(TestCategory.PREREQUISITE);
                    flag = FOCTransfer.PerformFOCApproval1BlankExternalTxnDate(opCategoryName, chCategoryName, chMsisdn, productName, opPin, txnId);
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


*/







    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */
    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed() {
        String FOCTransferCode = _masterVO.getProperty("FOCCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");


        ArrayList<String> opUserData = new ArrayList<>();
        Map<String, String> userInfo = UserAccessRevamp.getUserWithAccessRevamp(RolesI.FOC_COMMISSION,EventsI.FOC_COMMISSION);
        opUserData.add(userInfo.get("CATEGORY_NAME"));
        opUserData.add(userInfo.get("LOGIN_ID"));
        opUserData.add(userInfo.get("PASSWORD"));
        opUserData.add(userInfo.get("PIN"));

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which FOC transfer is allowed
         */
        ArrayList<String> alist1 = new ArrayList<>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(FOCTransferCode)) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
            }
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which FOC transfer is allowed
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
         * Store required data of 'FOC transfer allowed category' users in Object
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
