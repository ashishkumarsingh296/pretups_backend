package angular.testscripts.prereuisitesangular;

import angular.feature.C2CBuyRevamp;
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

@ModuleManager(name = Module.PREREQUISITE_C2C_RETURN_REVAMP)
public class PreRequisite_C2CReturn_Revamp extends BaseTest {


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_01_Test_C2CReturnMobileBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_01_Test_C2CReturnMobileBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR1");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CBuy.performC2CBuyMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);
            	
            	if(maxApprovalLevel == 0)
         		{
             		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
         		}
             	if(maxApprovalLevel == 1)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	else if(maxApprovalLevel == 2)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	else if(maxApprovalLevel == 3)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel3Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	
             	C2CBuy.performC2CReturnMobileBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent,c2cMap);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_02_Test_C2CReturnLoginBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_02_Test_C2CReturnLoginBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR2");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);
            
            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CBuy.performC2CBuyLoginBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);
            	
                 	if(maxApprovalLevel == 0)
             		{
                 		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
             		}
                 	if(maxApprovalLevel == 1)
             		{
                 		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		}
                 	else if(maxApprovalLevel == 2)
             		{
                 		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
                 		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		}
                 	else if(maxApprovalLevel == 3)
             		{
                 		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
                 		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
                 		c2cMap=C2CBuy.performingLevel3Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		}
                 	
                 	C2CBuy.performC2CReturnLoginBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent,c2cMap);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }


    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") // TO BE UNCOMMENTED WITH JIRA TEST CASE
    public void A_03_Test_C2CReturnUsernameBuyerType(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_03_Test_C2CReturnUsernameBuyerType";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR3");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	c2cMap=C2CBuy.performC2CBuyUsernameBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);
                 	
            	if(maxApprovalLevel == 0)
         		{
             		Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
         		}
             	if(maxApprovalLevel == 1)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	else if(maxApprovalLevel == 2)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	else if(maxApprovalLevel == 3)
         		{
             		c2cMap=C2CBuy.performingLevel1Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel2Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
             		c2cMap=C2CBuy.performingLevel3Approval(ToCategory, FromCategory, ToPin,c2cMap.get("TxnId"),maxApprovalLevel,ParentCategory);
         		}
             	
             	C2CBuy.performC2CReturnUsernameBuyerType(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent,c2cMap);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") 
    public void A_04_Test_C2CReturnMobileBuyerType_InvalidAmount(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_04_Test_C2CReturnMobileBuyerType_InvalidAmount";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR4");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	
             	
             	C2CBuy.performC2CReturnMobileBuyerType_InvalidAmount(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") 
    public void A_05_Test_C2CReturnMobileBuyerType_Blank_Amount(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_05_Test_C2CReturnMobileBuyerType_Blank_Amount";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR5");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CBUY_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	
             	
             	C2CBuy.performC2CReturnMobileBuyerType_BlankAmount(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") 
    public void A_06_Test_C2CReturnMobileBuyerType_Blank_Remarks(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_06_Test_C2CReturnMobileBuyerType_Blank_Remarks";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR6");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	
             	
             	C2CBuy.performC2CReturnMobileBuyerType_BlankRemarks(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

            } else {
                Assertion.assertSkip("As webaccess is not allowed for " + FromCategory + ", case is skipped.");
            }
        } else {
            Assertion.assertSkip("C2C Buy is not allowed to category[" + FromCategory + "].");
        }
        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }
    
    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-000") 
    public void A_07_Test_C2CReturnMobileBuyerType_Invalid_Pin(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum, String ToPin,String FromParent) {
        final String methodName = "A_07_Test_C2CReturnMobileBuyerType_Invalid_Pin";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PREVAMPC2CR7");
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);

        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        String value = DBHandler.AccessHandler.getPreference(catCode,networkCode,PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel= Integer.parseInt(value);
        HashMap<String, String> c2cMap = new HashMap<String, String>();
        
        if (CommonUtils.roleCodeExistInLinkSheetRevamp(RolesI.C2C_REVAMP, FromCategory,EventsI.C2CRETURN_EVENT)) {
            String webAccessAllowed = DBHandler.AccessHandler.webInterface(FromCategory);
            C2CBuyRevamp C2CBuy = new C2CBuyRevamp(driver);

            if (webAccessAllowed.equals("Y")) {
            	
             	
             	C2CBuy.performC2CReturnMobileBuyerType_InvalidPin(FromCategory, ToCategory, toMSISDN, FromPIN, catCode,FromParent);

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
        String C2CInititateCode = _masterVO.getProperty("C2CInititateCode");
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
            if (aList.contains(C2CInititateCode) || aList.contains(C2CInititateCode + "[P]") ||
                    aList.contains(C2CInititateCode + "[S]") || aList.contains(C2CInititateCode + "[O]") ||
                    aList.contains(C2CInititateCode + "[D]")) {
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

        Object[][] Data = new Object[totalObjectCounter][11];

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
                    Data[k][9] = ExcelUtility.getCellData(0, ExcelI.PIN, excelCounter);
                    Data[k][10] = ChannelUserParent;
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
        	Object [][] temp = new Object[1][11];
        	temp[0]=Data[0];
        	Data[0]=Data[6];
        	Data[6]=temp[0];
            return Data;
        }
    }
    /* ------------------------------------------------------------------------------------------------ */


}