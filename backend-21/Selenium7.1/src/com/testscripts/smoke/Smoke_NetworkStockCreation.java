package com.testscripts.smoke;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.commons.PretupsI;
import com.dbrepository.DBHandler;
import com.utils._masterVO;
import com.utils.Log;

public class Smoke_NetworkStockCreation extends BaseTest {
	
	String MultiWalletPreferenceValue;
	Object[] walletType;
	Map<String, String> InitiateMap;
	int stockInitiationAmount;
	static boolean TestCaseCounter = false;

	@Test(dataProvider = "availableWallets")
	public void smoke_networkStockCreation(String wallet) throws NumberFormatException, SQLException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		int TotalInitiatedAmount;
		String InitiateMessage;
		String TransactionID;
		String Level1ApprovalMessage = null;
		String Level2ApprovalMessage = null;
		long ApprovalLimit;

		Log.startTestCase(this.getClass().getName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[Smoke]Network Stock");
			TestCaseCounter = true;
		}

		// Network Stock Initiation
		currentNode = test.createNode("To verify that Network Admin is able to initiate Network Stock for " + wallet + " wallet successfully");
		currentNode.assignCategory("Smoke");
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, wallet, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(InitiateMap.get("ApprovalLimit"));
		InitiateMessage = InitiateMap.get("Message");
		

		// Network Stock Initiate Message Verification
		currentNode = test.createNode("To verify that proper Network Stock Initiation Message is displayed");
		currentNode.assignCategory("Smoke");

		String Message = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.msg.success", TransactionID);
		if (InitiateMessage.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		// Network Stock Approval Level 1
		currentNode = test.createNode("To verify that Network Admin is able Approve Network Stock at Level 1 Approval");
		currentNode.assignCategory("Smoke");
		Level1ApprovalMessage = NetworkStock.approveNetworkStockatLevel1(TransactionID,
				"Automated Level 1 Network Stock Approval");

		
		// Network Stock Approval Level 2
		if (TotalInitiatedAmount > ApprovalLimit) {
			currentNode = test
					.createNode("To verify that Network Admin is able Approve Network Stock at Level 2 Approval");
			currentNode.assignCategory("Smoke");
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,
					"Automated Level 2 Network Stock Approval");
		}
		
		currentNode = test.createNode("To verify that proper message is displayed on Successful Network Stock Approval");
		currentNode.assignCategory("Smoke");
		if (Level2ApprovalMessage != null) {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level2approval.msg.success",
					TransactionID);
			if (Level2ApprovalMessage.equals(FinalMessage))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		} else {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.msg.success",
					TransactionID);
			if (Level1ApprovalMessage.equals(FinalMessage))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
			}
		}
		
		Log.endTestCase(this.getClass().getName());
	}

	@DataProvider(name = "availableWallets")
	public Object[] walletType() {
		MultiWalletPreferenceValue = DBHandler.AccessHandler.getSystemPreference(CONSTANT.MULTIWALLET_SYSTEM_STATUS);
		stockInitiationAmount = Integer.parseInt(_masterVO.getProperty("InitiateNetworkStockAmount"));
		if (MultiWalletPreferenceValue.equals("true")) {
			walletType = new Object[3];
			walletType[0] =  PretupsI.SALE_WALLET_LOOKUP;
			walletType[1] =  PretupsI.FOC_WALLET_LOOKUP;
			walletType[2] =  PretupsI.INCENTIVE_WALLET_LOOKUP;
		} else if (MultiWalletPreferenceValue.equals("false")) {
			walletType = new Object[1];
			walletType[0] = PretupsI.SALE_WALLET_LOOKUP;
		}

		return walletType;
	}

}
