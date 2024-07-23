package com.testscripts.prerequisites;

import java.sql.SQLException;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.AutomationException;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils.constants.Module;
import com.utils.constants.TestCategory;

@ModuleManager(name = Module.PREREQUISITE_NETWORK_STOCK)
public class PreRequisite_NetworkStockCreation extends BaseTest {

    private String MultiWalletPreferenceValue;
    private Object[] walletType;
    private int stockInitiationAmount;

    @Test(dataProvider = "availableWallets")
    @TestManager(TestKey = "PRETUPS-292") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
    public void Test_NetworkStockCreation(String wallet) throws SQLException, AutomationException {
        final String methodName = "Test_NetworkStockCreation";
        Log.startTestCase(methodName, wallet);

        // Variable Initialization
        Map<String, String> initiateMap;
        int TotalInitiatedAmount;
        String TransactionID;
        long ApprovalLimit;

        NetworkStock NetworkStock = new NetworkStock(driver);

        currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK1").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        initiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, wallet, stockInitiationAmount);
        TotalInitiatedAmount = Integer.parseInt(initiateMap.get("TotalInitiatedStock"));
        TransactionID = initiateMap.get("TransactionID");
        ApprovalLimit = Long.parseLong(initiateMap.get("ApprovalLimit"));

        // Network Stock Approval Level 1
        currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK2").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
        NetworkStock.approveNetworkStockatLevel1(TransactionID, "Automated Level 1 Network Stock Approval");

        // Network Stock Approval Level 2
        if (TotalInitiatedAmount > ApprovalLimit) {
            currentNode = test
                    .createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK3").getExtentCase()).assignCategory(TestCategory.PREREQUISITE);
            NetworkStock.approveNetworkStockatLevel2(TransactionID, "Automated Level 2 Network Stock Approval");
        }

        Assertion.completeAssertions();
        Log.endTestCase(methodName);
    }

    /* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
    /* ------------------------------------------------------------------------------------------------- */

    @DataProvider(name = "availableWallets")
    public Object[] walletType() {
        MultiWalletPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS);
        stockInitiationAmount = Integer.parseInt(_masterVO.getProperty("InitiateNetworkStockAmount"));
        if (MultiWalletPreferenceValue.equalsIgnoreCase("true")) {
            walletType = new Object[3];
            walletType[0] = PretupsI.SALE_WALLET_LOOKUP;
            walletType[1] = PretupsI.FOC_WALLET_LOOKUP;
            walletType[2] = PretupsI.INCENTIVE_WALLET_LOOKUP;
        } else if (MultiWalletPreferenceValue.equalsIgnoreCase("false")) {
            walletType = new Object[1];
            walletType[0] = PretupsI.SALE_WALLET_LOOKUP;
        }

        return walletType;
    }

    /* ------------------------------------------------------------------------------------------------- */
}
