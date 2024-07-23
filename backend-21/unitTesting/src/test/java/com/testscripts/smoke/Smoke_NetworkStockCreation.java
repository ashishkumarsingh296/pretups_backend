package com.testscripts.smoke;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
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

@ModuleManager(name = Module.SMOKE_NETWORK_STOCK)
public class Smoke_NetworkStockCreation extends BaseTest {
	
	private String MultiWalletPreferenceValue;
	private int stockInitiationAmount;
	
	@Test(dataProvider = "availableWallets")
	@TestManager(TestKey = "PRETUPS-412") // TO BE UNCOMMENTED WITH JIRA TEST CASE ID
	public void Test_NetworkStockCreation(String wallet) throws NumberFormatException, SQLException, AutomationException {
		final String methodName = "Test_NetworkStockCreation";
		Log.startTestCase(methodName, wallet);

		// Variable Initialization
		Map<String, String> initiateMap = new HashMap<String, String>();
		int TotalInitiatedAmount;
		String InitiateMessage, TransactionID, Level1ApprovalMessage = null, Level2ApprovalMessage = null;
		long ApprovalLimit;
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);

		// Network Stock Initiation
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SNETWORKSTOCK1").getExtentCase(), wallet)).assignCategory(TestCategory.SMOKE);
		initiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, wallet, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(initiateMap.get("TotalInitiatedStock"));
		TransactionID = initiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(initiateMap.get("ApprovalLimit"));
		InitiateMessage = initiateMap.get("Message");
		
		// Network Stock Initiate Message Verification
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTOCK2").getExtentCase()).assignCategory(TestCategory.SMOKE);
		String Message = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.msg.success", TransactionID);
		Assertion.assertEquals(InitiateMessage, Message);
		
		// Network Stock Approval Level 1
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK2").getExtentCase()).assignCategory(TestCategory.SMOKE);
		Level1ApprovalMessage = NetworkStock.approveNetworkStockatLevel1(TransactionID,
				"Automated Level 1 Network Stock Approval");

		// Network Stock Approval Level 2
		if (TotalInitiatedAmount > ApprovalLimit) {
			currentNode = test
					.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK3").getExtentCase()).assignCategory(TestCategory.SMOKE);
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,
					"Automated Level 2 Network Stock Approval");
		}
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTOCK3").getExtentCase()).assignCategory(TestCategory.SMOKE);
		if (Level2ApprovalMessage != null) {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level2approval.msg.success",
					TransactionID);

			Assertion.assertEquals(Level2ApprovalMessage, FinalMessage);
		} else {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.msg.success",
					TransactionID);
			Assertion.assertEquals(Level1ApprovalMessage, FinalMessage);
		}

		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	/* -----------------------  D   A   T   A       P   R   O   V   I   D   E   R ---------------------- */
	/* ------------------------------------------------------------------------------------------------- */

	@DataProvider(name = "availableWallets")
	public Object[] walletTypeDP() {
		MultiWalletPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS);
		stockInitiationAmount = Integer.parseInt(_masterVO.getProperty("InitiateNetworkStockAmount"));
		if (MultiWalletPreferenceValue.equalsIgnoreCase("true"))
			return new String[] {PretupsI.SALE_WALLET_LOOKUP, PretupsI.FOC_WALLET_LOOKUP, PretupsI.INCENTIVE_WALLET_LOOKUP};
		else
			return new String[] {PretupsI.SALE_WALLET_LOOKUP};
	}

	/* ------------------------------------------------------------------------------------------------- */
}
