package com.testscripts.prerequisites;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.C2CTransfer;
import com.Features.ChannelUser;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.CaseMaster;
import com.classes.UserAccess;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pretupsControllers.BTSLUtil;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.CommonUtils;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_C2C_TRANSFER)
public class PreRequisite_Channel2ChannelTransfer extends BaseTest {

    HashMap<String, String> c2cMap = new HashMap<String, String>();
    HashMap<String, String> channelMap = new HashMap<>();

    @Test(dataProvider = "categoryData")
    @TestManager(TestKey = "PRETUPS-313") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_C2CTransfer(String FromCategory, String ToCategory, String toMSISDN, String FromPIN, String Domain, String ParentCategory, String geoType, String catCode, int RowNum) throws InterruptedException {
        final String methodName = "Test_C2CTransfer";
        Log.startTestCase(methodName, FromCategory, ToCategory, toMSISDN, FromPIN, Domain, ParentCategory, geoType, RowNum);
        CaseMaster CaseMaster = _masterVO.getCaseMasterByID("PC2CTRF1");
        C2CTransfer c2cTransfer = new C2CTransfer(driver);
        ChannelUser channelUser = new ChannelUser(driver);
        String networkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
        currentNode = test.createNode(MessageFormat.format(CaseMaster.getExtentCase(), FromCategory, ToCategory)).assignCategory(TestCategory.PREREQUISITE);
        String value = DBHandler.AccessHandler.getPreference(catCode, networkCode, PretupsI.MAX_APPROVAL_LEVEL_C2C_TRANSFER);
        int maxApprovalLevel = 0;
        if (BTSLUtil.isNullString(value)) {
            maxApprovalLevel = 0;
        } else{
            maxApprovalLevel = Integer.parseInt(value);
        if (CommonUtils.roleCodeExistInLinkSheet(RolesI.C2CTRF_ROLECODE, FromCategory)) {
            if (FromCategory.equals(ToCategory)) {
                channelMap = channelUser.channelUserInitiate(RowNum, Domain, ParentCategory, ToCategory, geoType);
                String APPLEVEL = DBHandler.AccessHandler.getSystemPreference(UserAccess.userapplevelpreference());
                if (APPLEVEL.equals("2")) {
                    channelUser.approveLevel1_ChannelUser();
                    channelUser.approveLevel2_ChannelUser();
                } else if (APPLEVEL.equals("1")) {
                    channelUser.approveLevel1_ChannelUser();
                } else {
                    Log.info("Approval not required.");
                }
                toMSISDN = channelMap.get("MSISDN");
            }
            c2cMap = c2cTransfer.channel2channelTransfer(FromCategory, ToCategory, toMSISDN, FromPIN);
            if (BTSLUtil.isNullString(value)) {
                Log.info("C2C Approval level is not Applicable");
            } else {
                if (maxApprovalLevel == 0) {
                    Log.info("C2C vocuher transfer Approval is perform at c2c transfer itself");
                }
                if (maxApprovalLevel == 1) {
                    c2cMap = c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                } else if (maxApprovalLevel == 2) {
                    c2cMap = c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                    c2cMap = c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                } else if (maxApprovalLevel == 3) {
                    c2cMap = c2cTransfer.performingLevel1Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                    c2cMap = c2cTransfer.performingLevel2Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                    c2cMap = c2cTransfer.performingLevel3Approval(FromCategory, ToCategory, toMSISDN, FromPIN, c2cMap.get("TransactionID"), maxApprovalLevel);
                }
            }
        } else {
            Assertion.assertSkip("Channel to Channel transfer link is not available to Category[" + FromCategory + "]");
        }
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

        Object[][] Data = new Object[totalObjectCounter][9];

        for (int j = 0, k = 0; j < alist1.size(); j++) {

            ExcelUtility.setExcelFile(MasterSheetPath, ExcelI.CHANNEL_USERS_HIERARCHY_SHEET);
            int excelRowSize = ExcelUtility.getRowCount();
            String ChannelUserPIN = null;
            for (int i = 1; i <= excelRowSize; i++) {
                if (ExcelUtility.getCellData(0, ExcelI.CATEGORY_NAME, i).equals(alist2.get(j))) {
                    ChannelUserPIN = ExcelUtility.getCellData(0, ExcelI.PIN, i);
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
            Object[][] Data1 = new Object[newObj][9];
            for (int l = 0, m = 0; l < Data.length; l++) {
                if (!removeData.contains(l)) {
                    for (int x = 0; x < 9; x++) {
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

    /* ----------------------------------------------------------------------------------------------- */
}
