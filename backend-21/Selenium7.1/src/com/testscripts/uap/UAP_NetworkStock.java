package com.testscripts.uap;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.Features.NetworkStock;
import com.Features.Enquiries.NetworkStockTransactions;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.utils._masterVO;
import com.utils.Log;
import com.utils._parser;

public class UAP_NetworkStock extends BaseTest {
	
	String MultiWalletPreferenceValue;
	Object[][] walletType;
	Map<String, String> InitiateMap;
	int stockInitiationAmount;
	int stockDeductionAmount;
	static boolean TestCaseCounter = false;
	String NetworkCode;
	
	/*
	 * Test Case Number 1: To verify that Network Admin is able to Initiate Network Stock.
	 * 					   To verify that an Entry is made in Network_Stock_Transactions table after successful Network Stock Initiation
	 * 					   Test Case Covers Approval Level 1 & Approval Level 2 as per the Defined Preference.
	 * 				       Message is validated as well.
	 */
	@Test(dataProvider = "availableWallets")
	public void TC1_UAP_NetworkStock(String wallet, String WalletCode) throws NumberFormatException, SQLException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		int TotalInitiatedAmount;
		String InitiatorName;
		String InitiateMessage;
		String TransactionID;
		String Level1ApprovalMessage = null;
		String Level2ApprovalMessage = null;
		Object[][] ViewCurrentStock_PreBalance;
		Object[][] ViewCurrentStock_PostBalance;
		long ApprovalLimit;

		Log.startTestCase(this.getClass().getName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Network Stock");
			TestCaseCounter = true;
		}

		/*
		 * Test Case Number 1: Network Stock Initiation
		 */
		ViewCurrentStock_PreBalance = NetworkStock.getCurrentNetworkStockDetails(NetworkCode, WalletCode);
		currentNode = test.createNode("To verify that Network Admin is able to initiate Network Stock for " + wallet + " wallet successfully");
		currentNode.assignCategory("UAP");
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(InitiateMap.get("ApprovalLimit"));
		InitiateMessage = InitiateMap.get("Message");
		InitiatorName = InitiateMap.get("Initiator UserName");
		
		/*
		 * Test Case Number 2: Network Stock Initiation Message Validation
		 */
		currentNode = test.createNode("To verify that proper Network Stock Initiation Message is displayed");
		currentNode.assignCategory("UAP");

		String Message = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.msg.success", TransactionID);
		if (InitiateMessage.equals(Message))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		/*
		 * Test Case Number 3: Network Stock Approval Level 1
		 */
		currentNode = test.createNode("To verify that Network Admin is able Approve Network Stock at Level 1 Approval");
		currentNode.assignCategory("UAP");
		Level1ApprovalMessage = NetworkStock.approveNetworkStockatLevel1(TransactionID,
				"Automated Level 1 Network Stock Approval");

		
		/*
		 * Test Case Number 4: Network Stock Approval Level 2
		 */
		if (TotalInitiatedAmount > ApprovalLimit) {
			currentNode = test
					.createNode("To verify that Network Admin is able Approve Network Stock at Level 2 Approval");
			currentNode.assignCategory("UAP");
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,
					"Automated Level 2 Network Stock Approval");
		}
		
		/*
		 * Test Case Number 5: Network Stock Approved Message Validation
		 */
		currentNode = test.createNode("To verify that proper message is displayed on Successful Network Stock Approval");
		currentNode.assignCategory("UAP");
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
			
		/*
		 * Test Case Number 6: To validate View Network Stock
		 */
		currentNode = test.createNode("To verify that Network Stock is updated on View Current Stock link on successful Network Stock Initiation for "+ wallet +" wallet");
		currentNode.assignCategory("UAP");
		ViewCurrentStock_PostBalance = NetworkStock.getCurrentNetworkStockDetails(NetworkCode, WalletCode);
		NetworkStock.validateCurrentNetworkStock(ViewCurrentStock_PreBalance, ViewCurrentStock_PostBalance, stockInitiationAmount);

		/*
		 * Test Case Number 6: View Network Stock Transaction Validation
		 */
		currentNode = test.createNode("To verify that the Network Stock Transaction Performed for "+ wallet +" wallet is available in View Network Stock Transactions page");
		currentNode.assignCategory("UAP");
		NetworkStockTransactions NetworkStockTransactions = new NetworkStockTransactions(driver);
		NetworkStockTransactions.validateNetworkStockTransaction(TransactionID, "ALL", CONSTANT.NetworkName, wallet, "null", InitiatorName, TotalInitiatedAmount, "Closed");
		
		/*
		 * Test Case Number 7: Database Validation on successful Network stock Initiation.
		 */
		currentNode = test.createNode("To verify that On Successful Network Stock Initiation an entry is made in Network Stock Transactions Table");
		currentNode.assignCategory("UAP");
		boolean DBStatus = NetworkStock.getTransactionStatusInNetworkStockTransactionsTable(TransactionID);
		if (DBStatus == true)
			currentNode.log(Status.PASS, "An Entry for Transaction ID: " + TransactionID + " found in Network_Stock_Transactions table");
		else
			currentNode.log(Status.FAIL, "No record for Transaction ID: " + TransactionID + " found in Network_Stock_Transactions table");
		
		Log.endTestCase(this.getClass().getName());
	}
	
	/*
	 * Test Case Number 2: To verify that Network Admin is not able to Initiate Network Stock for more than the defined amount
	 * 					   Test Case Covers Message validation as well.
	 */
	@Test(dataProvider="availableWallets")
	public void TC2_NetworkStock(String Wallet, String WalletCode) throws SQLException {
		
		Log.startTestCase(this.getClass().getName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		currentNode = test.createNode("To verify that Operator User is not able to Initiate Network Stock more than the specified MRP amount for " + Wallet + " wallet Type");
		currentNode.assignCategory("UAP");
		_parser networkStockMaxAmountParser = new _parser();
		networkStockMaxAmountParser.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(NetworkCode, CONSTANT.NETWORK_STOCK_REQUEST_LIMIT)).changeDenomation();
		long MaxAllowStockTransfer = networkStockMaxAmountParser.getValue();

		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, MaxAllowStockTransfer+1);
		String InitiateMessage = InitiateMap.get("Message").trim();
		String ErrorMessage = InitiateMap.get("ErrorMessage");
		if (InitiateMessage != null && !InitiateMessage.equals("")) {
		currentNode.log(Status.FAIL, "Success Message Found: " + InitiateMessage);
		currentNode.log(Status.FAIL, "Network Stock Initiation was successful");
		}
		
		currentNode = test.createNode("To verify that Proper Error Message is displayed on performing Network Stock Initation for more than the specificed MRP amount");
		currentNode.assignCategory("UAP");
		long TotalInitiatedAmount = Long.parseLong(InitiateMap.get("TotalInitiatedStock"));
		String preparedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.maxlimit", ""+TotalInitiatedAmount, ""+MaxAllowStockTransfer);
		if (ErrorMessage.equals(preparedMessage))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + preparedMessage + "] but found [" + ErrorMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
	}
	
	/*
	 * Test Case Number 3: To verify that Network Admin is able to Reject Network Stock at Level 1 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	@Test(dataProvider="availableWallets")
	public void TC3_NetworkStockRejectionAtLevel1Approval(String Wallet, String WalletCode) throws NumberFormatException, SQLException{
		
		Log.startTestCase(this.getClass().getName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		currentNode = test.createNode("To verify that Operator User is able to Reject Network Stock Transaction at Level 1 Approval for " + Wallet + " wallet Type");
		currentNode.assignCategory("UAP");
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		String TransactionID = InitiateMap.get("TransactionID");
		String RejectionMessage = NetworkStock.rejectNetworkStockatLevel1(TransactionID);
		
		currentNode = test.createNode("To verify that Proper Rejection message is displayed on successful Network Stock Reject");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.success.cancel", TransactionID);
		if (RejectionMessage.equals(ExpectedMessage))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + ExpectedMessage + "] but found [" + RejectionMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
	
	}
	
	/*
	 * Test Case Number 4: To verify that Network Admin is able to Reject Network Stock at Level 2 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	@Test(dataProvider="availableWallets")
	public void TC4_NetworkStockRejectionAtLevel2Approval(String Wallet, String WalletCode) throws NumberFormatException, SQLException{
		
		Log.startTestCase(this.getClass().getName() + " :: " + Thread.currentThread().getStackTrace()[1].getMethodName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);

		currentNode = test.createNode("To verify that Operator User is able to Reject Network Stock Transaction at Level 2 Approval for " + Wallet + " wallet Type");
		currentNode.assignCategory("UAP");
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser FirstApprovalAllowedLimit = new _parser();
		FirstApprovalAllowedLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "FRSTAPPLM")).changeDenomation();
		long TC4_initiationAmount = FirstApprovalAllowedLimit.getValue() + 2;
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, TC4_initiationAmount);
		String TransactionID = InitiateMap.get("TransactionID");
		NetworkStock.approveNetworkStockatLevel1(TransactionID, "Automated Level 1 Network Stock Approval");
		String RejectionMessage = NetworkStock.rejectNetworkStockatLevel2(TransactionID);
		
		currentNode = test.createNode("To verify that Proper Rejection message is displayed on successful Network Stock Reject");
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level2approval.success.cancel", TransactionID);
		if (RejectionMessage.equals(ExpectedMessage))
			currentNode.log(Status.PASS, "Message Validation Successful");
		else {
			currentNode.log(Status.FAIL, "Expected [" + ExpectedMessage + "] but found [" + RejectionMessage + "]");
			currentNode.log(Status.FAIL, "Message Validation Failed");
		}
		
		Log.endTestCase(this.getClass().getName() + " :: " + Thread.currentThread().getStackTrace()[1].getMethodName());
	
	}

	/*
	 * Test Case Number 5: To verify that Network Admin is able to reduce the Network Stock Initiation amount on Level 1 Approval.
	 */
	@Test(dataProvider="availableWallets")
	public void TC5_NetworkStockDecreasedAmountOnApproval(String Wallet, String WalletCode) throws NumberFormatException, SQLException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		int TotalInitiatedAmount;
		String InitiateMessage;
		String TransactionID;
		String Level1ApprovalMessage = null;
		String Level2ApprovalMessage = null;
		long ApprovalLimit;
		Object[] ApprovalReturnObject;

		NetworkStock NetworkStock = new NetworkStock(driver);
		
		Log.startTestCase(this.getClass().getName());
		
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Integer.parseInt(InitiateMap.get("ApprovalLimit"));
		currentNode = test.createNode("To verify that Network Admin is able to reduce Network Stock for " + Wallet + " wallet successfully during Approval Level 1");
		currentNode.assignCategory("UAP");		
		ApprovalReturnObject = NetworkStock.approveNetworkStockatLevel1(TransactionID, (stockInitiationAmount - 10), "Automated Level 1 Network Stock Approval");
		TotalInitiatedAmount = (int) ApprovalReturnObject[1];
		
		// Network Stock Approval Level 2
		if (TotalInitiatedAmount > ApprovalLimit) {
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,"Automated Level 2 Network Stock Approval");
		}
	}
	
	/*
	 * Test Case Number 6: To verify that Network Admin is able to Initiate Network Stock Deduction.
	 * 					   Test Case Covers Approval Level.
	 * 				       Message is validated as well.
	 */
	@Test(dataProvider = "availableWallets")
	public void TC6_NetworkStockDeduction(String wallet, String WalletCode) throws NumberFormatException, SQLException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		String InitiateMessage = null;
		String TransactionID = null;
		String ApprovalMessage = null;
		String Message = null;
		
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);

		Log.startTestCase(this.getClass().getName());
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);

		if (TestCaseCounter == false) {
			test = extent.createTest("[UAP]Network Stock");
			TestCaseCounter = true;
		}
		
		/*
		 * Test Case Number 1: Initiate Network Stock Deduction
		 */
		currentNode = test.createNode("To verify that Network Admin is able to initiate Network Stock deduction for " + wallet + " wallet successfully");
		currentNode.assignCategory("UAP");
		if (deductionRoleStatus) {
			InitiateMap = NetworkStock.initiateStockDeduction(MultiWalletPreferenceValue, WalletCode, stockDeductionAmount);
			TransactionID = InitiateMap.get("TransactionID");
			InitiateMessage = InitiateMap.get("Message");
		} else {
			Log.skip("Network Stock Deduction is not available in system");
		}
		
		/*
		 * Test Case Number 2: Network Stock Initiation Message Validation
		 */
		currentNode = test.createNode("To verify that proper Network Stock Initiation Message is displayed");
		currentNode.assignCategory("UAP");
		if (deductionRoleStatus) {
			Message = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.deduction.msg.success", TransactionID);
			if (InitiateMessage.equals(Message))
				currentNode.log(Status.PASS, "Message Validation Successful");
			else {
				currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
				currentNode.log(Status.FAIL, "Message Validation Failed");
				 }
		} else {
			Log.skip("Network Stock Deduction is not available in system");
		}
		
		/*
		 * Test Case Number 3: Network Stock Approval Level 1
		 */
		currentNode = test.createNode("To verify that Network Admin is able Approve Network Stock deduction at Level 1 Approval");
		currentNode.assignCategory("UAP");
		if (deductionRoleStatus)
			ApprovalMessage = NetworkStock.approveNetworkStockDeductionatLevel1(TransactionID, "Automated Level 1 Network Stock Approval");
		else
			Log.skip("Network Stock Deduction is not available in system");
		
		/*
		 * Test Case Number 5: Network Stock Approved Message Validation
		 */
		currentNode = test.createNode("To verify that proper message is displayed on Successful Network Stock Deduction Approval");
		currentNode.assignCategory("UAP");
		if (deductionRoleStatus) {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.msg.success", TransactionID);
				if (ApprovalMessage.equals(FinalMessage))
					currentNode.log(Status.PASS, "Message Validation Successful");
				else {
					currentNode.log(Status.FAIL, "Expected [" + Message + "] but found [" + InitiateMessage + "]");
					currentNode.log(Status.FAIL, "Message Validation Failed");
					 }
		} else {
			Log.skip("Network Stock Deduction is not available in System");
		}
		
		Log.endTestCase(this.getClass().getName());
	}
	
	/*
	 * DataProvider for Test Cases for Multi Wallet Handeling. The Test works on the basis of MULTIPLE_WALLET_APPLY
	 */
	@DataProvider(name = "availableWallets")
	public Object[] walletType() {
		NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		MultiWalletPreferenceValue = DBHandler.AccessHandler.getNetworkPreference(NetworkCode, CONSTANT.MULTIWALLET_SYSTEM_STATUS);
		stockInitiationAmount = Integer.parseInt(_masterVO.getProperty("InitiateNetworkStockAmount"));
		stockDeductionAmount = Integer.parseInt(_masterVO.getProperty("NetworkStockDeductionAmount_UAP"));
		if (MultiWalletPreferenceValue.equals("true")) {
			
			walletType = new Object[][] {{"Sale", PretupsI.SALE_WALLET_LOOKUP}, {"Free of Cost", PretupsI.FOC_WALLET_LOOKUP}, {"Incentive", PretupsI.INCENTIVE_WALLET_LOOKUP}};
		} else if (MultiWalletPreferenceValue.equals("false")) {
			walletType = new Object[][] {{"Sale", PretupsI.SALE_WALLET_LOOKUP}};
		}

		return walletType;
	}

}
