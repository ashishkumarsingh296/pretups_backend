package com.testscripts.prerequisites;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils._masterVO;
import com.utils.Log;

public class PreRequisite_NetworkStockCreation extends BaseTest {

	String MultiWalletPreferenceValue;
	Object[] walletType;
	Map<String, String> InitiateMap;
	int stockInitiationAmount;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "availableWallets")
	public void prerequisite_networkStockCreation(String wallet) throws NumberFormatException, SQLException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		int TotalInitiatedAmount;
		String TransactionID;
		long ApprovalLimit;
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		Log.startTestCase(this.getClass().getName());
		
		// Check if Test Case is already available. If not Test Case is created for Extent Report & Counter is updated
		if (TestCaseCounter == false) {
			test = extent.createTest("[Pre-Requisite]Network Stock Creation");
			TestCaseCounter = true;
		}

		// Network Stock Initiation
		currentNode = test.createNode("To verify that Network Admin is able to initiate Network Stock successfully");
		currentNode.assignCategory("Pre-Requisite");
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, wallet, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(InitiateMap.get("ApprovalLimit"));
			

		// Network Stock Approval Level 1
		currentNode = test.createNode("To verify that Network Admin is able Approve Network Stock at Level 1 Approval");
		currentNode.assignCategory("Pre-Requisite");
		NetworkStock.approveNetworkStockatLevel1(TransactionID,	"Automated Level 1 Network Stock Approval");

		
		// Network Stock Approval Level 2
		if (TotalInitiatedAmount > ApprovalLimit) {
			currentNode = test
					.createNode("To verify that Network Admin is able Approve Network Stock at Level 2 Approval");
			currentNode.assignCategory("Pre-Requisite");
		NetworkStock.approveNetworkStockatLevel2(TransactionID,	"Automated Level 2 Network Stock Approval");
		}
		
		Log.endTestCase(this.getClass().getName());		
	}

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
}
