package angular.testscripts.prereuisitesangular;

import angular.feature.C2CTransferRevamp;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.commons.EventsI;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
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
import java.util.HashMap;

@ModuleManager(name = Module.PREREQUISITE_C2C_TRANSFER_REVAMP)
public class PreRequisite_C2CTransfer_Revamp extends BaseTest {


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_01_Test_C2CTransferMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_01_Test_C2CTransferMobikeBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        
        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	
            	c2cMap=C2CTransfer.performC2CTransferMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

                if (FromCategory.equalsIgnoreCase(ToCategory)){
                    Assertion.completeAssertions();
                    Log.endTestCase(methodName);
                }

                else{
                if(maxApprovalLevel == 0)
        		{
            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            	else if(maxApprovalLevel == 2)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            } }else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData") // UI BUG
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_02_Test_C2CTransferLoginBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_02_Test_C2CTransferLoginBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        
        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CTransfer.performC2CTransferLoginBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

                if (FromCategory.equalsIgnoreCase(ToCategory)){
                    Assertion.completeAssertions();
                    Log.endTestCase(methodName);
                }
                else {
                    if(maxApprovalLevel == 0)
            		{
                		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
            		}
                	if(maxApprovalLevel == 1)
            		{
                		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		}
                	else if(maxApprovalLevel == 2)
            		{
                		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
                		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		}
                	else if(maxApprovalLevel == 3)
            		{
                		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
                		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
                		c2cMap=C2CTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		}
            } }else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData") // UI BUG
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_03_Test_C2CTransferUsernameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_03_Test_C2CTransferUsernameBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CTransfer.performC2CTransferUsernameBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

                if (FromCategory.equalsIgnoreCase(ToCategory)){
                    Assertion.completeAssertions();
                    Log.endTestCase(methodName);
                }
                else{
                if(maxApprovalLevel == 0)
        		{
            		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
        		}
            	if(maxApprovalLevel == 1)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            	else if(maxApprovalLevel == 2)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            	else if(maxApprovalLevel == 3)
        		{
            		c2cMap=C2CTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
            		c2cMap=C2CTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);
        		}
            } }else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_04_Test_C2CTransferBlankBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_04_Test_C2CTransferBlankBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_05_Test_C2CTransferBlankMSISDNForMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_05_Test_C2CTransferBlankMSISDNForMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankMSISDNofMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData") 
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_06_Test_C2CTransferBlankTopupsForMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_06_Test_C2CTransferBlankTopupsForMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankTOPUPSofMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_07_Test_C2CTransferBlankCategoryOfUserNameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_07_Test_C2CTransferBlankCategoryOfUserNameBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankCategoryOfUsernameBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData") // UI BUG
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_08_Test_C2CTransferBlankUsernameOfUserNameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_08_Test_C2CTransferBlankUsernameOfUserNameBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT8");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankUsernameOfUsernameBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_09_Test_C2CTransferBlankCategoryOfLoginIDBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_09_Test_C2CTransferBlankCategoryOfLoginIDBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);

        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT9");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankCategoryOfLOGINIDBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")// BUG IN UI
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_10_Test_C2CTransferBlankLoginidOfLoginIDBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_10_Test_C2CTransferBlankLoginidOfLoginIDBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT10");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankLoginidOfLOGINIDBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData") 
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_11_Test_C2CTransferNegativeAmountOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_11_Test_C2CTransferNegativeAmountOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT11");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferNegativeAmountOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    //Added by Raghav
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_17_Test_C2CTransferSplCharAmtOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_17_Test_C2CTransferSplCharAmtOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT17");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTrfSplAmtOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData") 
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_12_Test_C2CTransferBlankRemarksOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_12_Test_C2CTransferBlankRemarksOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT12");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankRemarksOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_13_Test_C2CTransferBlankPaymentModeOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_13_Test_C2CTransferBlankPaymentModeOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT13");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankPaymentModeOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_14_Test_C2CTransferInvalidPinOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_14_Test_C2CTransferInvalidPinOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT14");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferInvalidPinMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData") 
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_15_Test_C2CTransferBlankAmountOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_15_Test_C2CTransferBlankAmountOfMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT15");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferBlankAmountOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void B_02_Test_C2CBuyMobileBuyerType_InvalidRefNoInApproval(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "B_02_Test_C2CBuyMobileBuyerType_InvalidRefNoInApprova";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CAPPRVL1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBUY_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CTransfer.performC2CTransferMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);


            	C2CTransfer.invalidRefNoApprovalLevel(FromCategory, ToCategory, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

//    @Test(dataProvider = "categoryData")
//    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
//    public void A_16_Test_C2CTransferInvalidRefNotOfMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
//        final String methodName = "A_16_Test_C2CTransferInvalidRefNotOfMobileBuyerType";
//        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
//        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT16");
//        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
//        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
//            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
//            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
//            if (webAccessAllowed.equals("Y")) {
//                C2CTransfer.performC2CTransferInvalidRefNoOfMobileBuyer(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
//            } else {
//                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
//            }
//        } else {
//            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
//        }
//        Assertion.completeAssertions();
//        Log.endTestCase(methodName);
//    }

    //Added by Raghav
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_18_Test_C2CTransferSplCharRefNo(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "A_18_Test_C2CTransferSplCharRefNo";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CT18");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CTRANSFER_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);
            if (webAccessAllowed.equals("Y")) {
                C2CTransfer.performC2CTransferSplCharRefNo(FromCategory, ToCategory, toMSISDN, FromPIN, catCode);
            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Bulk Transfer is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void B_03_Test_C2CBuyMobileBuyerType_RejectInApproval(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum,String FromParent) {
        final String methodName = "B_03_Test_C2CBuyMobileBuyerType_RejectInApproval";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CAPPRVL2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();

        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBUY_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CTransferRevamp C2CTransfer = new C2CTransferRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CTransfer.performC2CTransferMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);


            	C2CTransfer.rejectInApprovalLevel(FromCategory, ToCategory, FromPIN,c2cMap.get("TxnId"),maxApprovalLevel,FromParent);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "categoryData")
    public Object[][] TestDataFeed1() {
        String C2CTransferCode = _masterVO.getProperty("C2CTransferCode");
        String MasterSheetPath = _masterVO.getProperty("DataProvider");

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
        int rowCount = ExcelUtility.getRowCount();
        /*
         * Array list to store Categories for which C2C withdraw is allowed
         */
        ArrayList<String> alist1 = new ArrayList<String>();
        ArrayList<String> alist2 = new ArrayList<String>();
        ArrayList<String> categorySize = new ArrayList<String>();
        ArrayList<String> transfer_rule_type = new ArrayList<String>();
        for (int i = 1; i <= rowCount; i++) {
            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
            String services = ExcelUtility.getCellData(0, ExcelI.SERVICES, i);
            ArrayList<String> aList = new ArrayList<String>(Arrays.asList(services.split("[ ]*,[ ]*")));
            if (aList.contains(C2CTransferCode) || aList.contains(C2CTransferCode + "[P]") ||
                    aList.contains(C2CTransferCode + "[S]") || aList.contains(C2CTransferCode + "[O]") ||
                    aList.contains(C2CTransferCode + "[D]")) {
                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.TRANSFER_RULE_SHEET);
                alist1.add(ExcelUtility.getCellData(0, ExcelI.TO_CATEGORY, i));
                alist2.add(ExcelUtility.getCellData(0, ExcelI.FROM_CATEGORY, i));
                if (ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i).equals(""))
                    transfer_rule_type.add("D");
                else
                    transfer_rule_type.add(ExcelUtility.getCellData(0, ExcelI.TRF_RULE_TYPE, i));
            }
        }

        ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
        int channelUsersHierarchyRowCount = ExcelUtility.getRowCount();
     //   int channelUsersHierarchyRowCount = 2 ;

        /*
         * Calculate the Count of Users for each category
         */
        int totalObjectCounter = 0;
        for (int i = 0; i < alist1.size(); i++) {
            int categorySizeCounter = 0;
            for (int excelCounter = 0; excelCounter <= channelUsersHierarchyRowCount; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(i))) {
                    categorySizeCounter++;
                }
            }
            categorySize.add("" + categorySizeCounter);
            totalObjectCounter = totalObjectCounter + categorySizeCounter;
        }

        /*
         * Counter to count number of users exists in channel users hierarchy sheet
         * of Categories for which C2C Withdraw is allowed
         */

        Object[][] Data = new Object[totalObjectCounter][10];

        for (int j = 0, k = 0; j < alist1.size(); j++) {

            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserPIN = null;
            String ChannelUserParent =null;
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                    ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
                    ChannelUserParent = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, i);
                    break;
                }
            }

            for (int excelCounter = 1; excelCounter <= excelRowSize; excelCounter++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, excelCounter).equals(alist1.get(j))) {
                    Data[k][0] = alist2.get(j);
                    Data[k][1] = alist1.get(j);
                    Data[k][2] = ExcelUtility.getCellData(0, ExcelI.MSISDN, excelCounter);
                    Data[k][3] = ChannelUserPIN;
                    Data[k][4] = ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, excelCounter);
                    Data[k][5] = ExcelUtility.getCellData(0, ExcelI.PARENT_CATEGORY_NAME, excelCounter);
                    Data[k][6] = ExcelUtility.getCellData(0, ExcelI.GRPH_DOMAIN_TYPE, excelCounter);
                    Data[k][7] = ExcelUtility.getCellData(0, ExcelI.CATEGORY_CODE, excelCounter);
                    Data[k][8] = excelCounter;
                    Data[k][9] = ChannelUserParent;
                    k++;
                }
            }

        }

        /*
         * Clean data on the basis of transfer rules
         */
        String trfUserLevelAlllow = DBHandler.AccessHandler.getSystemPreference(CONSTANT.TRF_RULE_USER_LEVEL_ALLOW);
        if (trfUserLevelAlllow.equalsIgnoreCase("FALSE")) {
            int q = 0;
            ArrayList<Integer> removeData = new ArrayList<Integer>();
            for (int i = 0; i < alist1.size(); i++) {
                if (transfer_rule_type.get(i).equals("P")) {
                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));

                    for (int p = 0; p < Data.length; p++) {
                        if (Data[p][0].equals(alist2.get(i)) && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
                            Log.info("Data to be removed:[" + p + "]");
                            q++;
                            removeData.add(p);
                        }
                    }
                }

                ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
                int excelRowSize = ExcelUtility.getRowCount();

                if (transfer_rule_type.get(i).equals("O")) {
                    Log.info("From: " + alist2.get(i) + "| To: " + alist1.get(i) + "| TYPE: " + transfer_rule_type.get(i));
                    for (int p = 0; p < Data.length; p++) {
                        int k;
                        for (k = 1; k <= excelRowSize; k++) {
                            if (ExcelUtility.getCellData(0, ExcelI.SEQUENCE_NO, k).equals("1") && ExcelUtility.getCellData(0, ExcelI.DOMAIN_NAME, k).equals(Data[p][4])) {
                                break;
                            }
                        }

                        if ((Data[p][0].equals(alist2.get(i)) || Data[p][0].equals(ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, k)))
                                && Data[p][1].equals(alist1.get(i)) && !Data[p][5].equals(alist2.get(i))) {
                            Log.info("Data to be removed:[" + p + "]");
                            q++;
                            removeData.add(p);
                        }
                    }
                }
            }

            int newObj = Data.length - q;
            Object[][] Data1 = new Object[newObj][8];
            for (int l = 0, m = 0; l < Data.length; l++) {
                if (!removeData.contains(l)) {
                    for (int x = 0; x < 8; x++) {
                        Data1[m][x] = Data[l][x];
                    }
                    Log.info(Data1);
                    m++;
                }
            }


            return Data1;
        } else {   	
        return Data;
        }
    }
    /* ------------------------------------------------------------------------------------------------ */


}
