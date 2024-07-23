package com.testscripts.uap;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.Features.O2CTransfer;
import com.Features.mapclasses.OperatorToChannelMap;
import com.aventstack.extentreports.Status;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;

@ModuleManager(name = Module.UAP_NETWORK_STOCK)
public class UAP_NetworkStock extends BaseTest {

	String MultiWalletPreferenceValue;
	Map<String, String> InitiateMap;
	int stockInitiationAmount;
	int stockDeductionAmount;
	String NetworkCode;
	String assignCategory="UAP";
	/*
	 * Test Case Number 1: To verify that Network Admin is able to Initiate Network Stock.
	 * 					   To verify that an Entry is made in Network_Stock_Transactions table after successful Network Stock Initiation
	 * 					   Test Case Covers Approval Level 1 & Approval Level 2 as per the Defined Preference.
	 * 				       Message is validated as well.
	 */
	@Test(dataProvider = "availableWallets")
    @TestManager(TestKey = "PRETUPS-268") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC1_UAP_NetworkStock(String wallet, String WalletCode) throws NumberFormatException, SQLException, AutomationException {
		
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

		final String methodName = "Test_NetworkStock";
        Log.startTestCase(methodName);
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);

		/*
		 * Test Case Number 1: Network Stock Initiation
		 */
		ViewCurrentStock_PreBalance = NetworkStock.getCurrentNetworkStockDetails(NetworkCode, WalletCode);
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("SNETWORKSTOCK1").getExtentCase(), wallet));
		currentNode.assignCategory(assignCategory);
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		TotalInitiatedAmount = Integer.parseInt(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(InitiateMap.get("ApprovalLimit"));
		InitiateMessage = InitiateMap.get("Message");
		InitiatorName = InitiateMap.get("Initiator UserName");
		
		/*
		 * Test Case Number 2: Network Stock Initiation Message Validation
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTOCK2").getExtentCase());
		currentNode.assignCategory(assignCategory);

		String Message = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.msg.success", TransactionID);
		Assertion.assertEquals(InitiateMessage, Message);
		
		/*
		 * Test Case Number 3: Network Stock Approval Level 1
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		Level1ApprovalMessage = NetworkStock.approveNetworkStockatLevel1(TransactionID,
				"Automated Level 1 Network Stock Approval");

		
		/*
		 * Test Case Number 4: Network Stock Approval Level 2
		 */
		if (TotalInitiatedAmount > ApprovalLimit) {
			currentNode = test
					.createNode(_masterVO.getCaseMasterByID("PNETWORKSTOCK3").getExtentCase());
			currentNode.assignCategory(assignCategory);
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,
					"Automated Level 2 Network Stock Approval");
		}
		
		/*
		 * Test Case Number 5: Network Stock Approved Message Validation
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("SNETWORKSTOCK3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		if (Level2ApprovalMessage != null) {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level2approval.msg.success",
					TransactionID);
			Assertion.assertEquals(Level2ApprovalMessage, FinalMessage);
			
		} else {
			String FinalMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.msg.success",
					TransactionID);
			Assertion.assertEquals(Level1ApprovalMessage, FinalMessage);
		}
			
		/*
		 * Test Case Number 6: To validate View Network Stock
		 */
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UNETWORKSTOCK1").getExtentCase(), wallet));
		currentNode.assignCategory(assignCategory);
		ViewCurrentStock_PostBalance = NetworkStock.getCurrentNetworkStockDetails(NetworkCode, WalletCode);
		NetworkStock.validateCurrentNetworkStock(ViewCurrentStock_PreBalance, ViewCurrentStock_PostBalance, stockInitiationAmount);

		/*Case commented on 20/03/2018
		 * Test Case Number 6: View Network Stock Transaction Validation
		 */
		/*currentNode = test.createNode("To verify that the Network Stock Transaction Performed for "+ wallet +" wallet is available in View Network Stock Transactions page");
		currentNode.assignCategory(assignCategory);
		NetworkStockTransactions NetworkStockTransactions = new NetworkStockTransactions(driver);
		NetworkStockTransactions.validateNetworkStockTransaction(TransactionID, "ALL", CONSTANT.NetworkName, wallet, "null", InitiatorName, TotalInitiatedAmount, "Closed");
		*/
		/*
		 * Test Case Number 7: Database Validation on successful Network stock Initiation.
		 */
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETWORKSTOCK2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean DBStatus = NetworkStock.getTransactionStatusInNetworkStockTransactionsTable(TransactionID);
		if (DBStatus == true)
			currentNode.log(Status.PASS, "An Entry for Transaction ID: " + TransactionID + " found in Network_Stock_Transactions table");
		else
			currentNode.log(Status.FAIL, "No record for Transaction ID: " + TransactionID + " found in Network_Stock_Transactions table");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*
	 * Test Case Number 2: To verify that Network Admin is not able to Initiate Network Stock for more than the defined amount
	 * 					   Test Case Covers Message validation as well.
	 */
	 @Test(dataProvider="availableWallets")
	 @TestManager(TestKey = "PRETUPS-270") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC2_NetworkStock(String Wallet, String WalletCode) throws SQLException {
		
		final String methodName = "Test_NetworkStock";
	    Log.startTestCase(methodName);
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UNETWORKSTOCK3").getExtentCase(),Wallet));
		currentNode.assignCategory(assignCategory);
		try {
		long DB_MAX_TRF = Long.parseLong(DBHandler.AccessHandler.getNetworkPreference(NetworkCode, CONSTANT.NETWORK_STOCK_REQUEST_LIMIT));
//		long MaxAllowStockTransfer = Long.parseLong(_parser.getDisplayAmount(DB_MAX_TRF));

		long MaxAllowStockTransfer = (long)Double.parseDouble(_parser.getDisplayAmount(DB_MAX_TRF));
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, MaxAllowStockTransfer+1);
		String InitiateMessage = InitiateMap.get("Message").trim();
		String ErrorMessage = InitiateMap.get("ErrorMessage");
		Assertion.assertNull(InitiateMessage, "Network Stock Initiation was successful with message: " + InitiateMessage);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETWORKSTOCK4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		long TotalInitiatedAmount = Long.parseLong(InitiateMap.get("TotalInitiatedStock"));
//		String preparedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.maxlimit", ""+TotalInitiatedAmount, ""+MaxAllowStockTransfer);
		String preparedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.maxlimit", ""+TotalInitiatedAmount, _parser.getDisplayAmount(DB_MAX_TRF));
		Assertion.assertEquals(ErrorMessage, preparedMessage);
		} catch (AutomationException e) {
			Assertion.assertSkip(e.toString());
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*
	 * Test Case Number 3: To verify that Network Admin is able to Reject Network Stock at Level 1 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	 @Test(dataProvider="availableWallets")
	 @TestManager(TestKey = "PRETUPS-271") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC3_NetworkStockRejectionAtLevel1Approval(String Wallet, String WalletCode) throws NumberFormatException, SQLException, AutomationException{
		
		 final String methodName = "Test_NetworkStockRejectionAtLevel1Approval";
	        Log.startTestCase(methodName);
		
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UNETWORKSTOCK5").getExtentCase(),Wallet));
		currentNode.assignCategory(assignCategory);
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		String TransactionID = InitiateMap.get("TransactionID");
		String RejectionMessage = NetworkStock.rejectNetworkStockatLevel1(TransactionID);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETWORKSTOCK6").getExtentCase());
		String ExpectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.success.cancel", TransactionID);
		Assertion.assertEquals(RejectionMessage, ExpectedMessage);
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	
	}
	
	/*
	 * Test Case Number 4: To verify that Network Admin is able to Reject Network Stock at Level 2 Approval.
	 * 					   Test Case Covers Message validation as well.
	 */
	 @Test(dataProvider="availableWallets")
		@TestManager(TestKey = "PRETUPS-273") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC4_NetworkStockRejectionAtLevel2Approval(String Wallet, String WalletCode) throws NumberFormatException, SQLException, AutomationException{
		final String methodname = "TC4_NetworkStockRejectionAtLevel2Approval";
		Log.startTestCase(methodname, Wallet, WalletCode);
		
		boolean ApprovalLevel2RoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE);
		// Objects Initialization
		NetworkStock NetworkStock = new NetworkStock(driver);

		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UNETWORKSTOCK7").getExtentCase(),Wallet));
		currentNode.assignCategory(assignCategory);
		if (ApprovalLevel2RoleStatus) {
			String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			_parser FirstApprovalAllowedLimit = new _parser();
			FirstApprovalAllowedLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, "FRSTAPPLM")).changeDenomation();
			long TC4_initiationAmount = FirstApprovalAllowedLimit.getValue() + 2;
			InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, TC4_initiationAmount);
			String TransactionID = InitiateMap.get("TransactionID");
			NetworkStock.approveNetworkStockatLevel1(TransactionID, "Automated Level 1 Network Stock Approval");
			String RejectionMessage = NetworkStock.rejectNetworkStockatLevel2(TransactionID);
			
			currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETWORKSTOCK8").getExtentCase());
			String ExpectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level2approval.success.cancel", TransactionID);
			Assertion.assertEquals(RejectionMessage, ExpectedMessage);
		} else {
			Assertion.assertSkip("Level 2 Network Stock Approval is not available in system, hence test case skipped.");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}

	/*
	 * Test Case Number 5: To verify that Network Admin is able to reduce the Network Stock Initiation amount on Level 1 Approval.
	 */
	 @Test(dataProvider="availableWallets")
		@TestManager(TestKey = "PRETUPS-274") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC5_NetworkStockDecreasedAmountOnApproval(String Wallet, String WalletCode) throws NumberFormatException, SQLException, AutomationException {
		
		// Variable Initialization
		InitiateMap = new HashMap<String, String>();
		long TotalInitiatedAmount;
		String InitiateMessage;
		String TransactionID;
		String Level1ApprovalMessage = null;
		String Level2ApprovalMessage = null;
		long ApprovalLimit;
		Object[] ApprovalReturnObject;

		NetworkStock NetworkStock = new NetworkStock(driver);
		
		final String methodName = "Test_NetworkStockDecreasedAmountOnApproval";
        Log.startTestCase(methodName);
		
		currentNode = test.createNode(MessageFormat.format(_masterVO.getCaseMasterByID("UNETWORKSTOCK9").getExtentCase(),Wallet));
		currentNode.assignCategory(assignCategory);		
		InitiateMap = NetworkStock.initiateNetworkStock(MultiWalletPreferenceValue, WalletCode, stockInitiationAmount);
		TotalInitiatedAmount = Long.parseLong(InitiateMap.get("TotalInitiatedStock"));
		TransactionID = InitiateMap.get("TransactionID");
		ApprovalLimit = Long.parseLong(InitiateMap.get("ApprovalLimit"));
		ApprovalReturnObject = NetworkStock.approveNetworkStockatLevel1(TransactionID, (stockInitiationAmount - 10), "Automated Level 1 Network Stock Approval");
		TotalInitiatedAmount = (int) ApprovalReturnObject[1];
		
		// Network Stock Approval Level 2
		if (TotalInitiatedAmount > ApprovalLimit) {
			Level2ApprovalMessage = NetworkStock.approveNetworkStockatLevel2(TransactionID,"Automated Level 2 Network Stock Approval");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
	/*
	 * Test Case Number 6: To verify that Network Admin is able to Initiate Network Stock Deduction.
	 * 					   Test Case Covers Approval Level.
	 * 				       Message is validated as well.
	 */
	/*@Test(dataProvider = "availableWallets")
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
		
		
		 * Test Case Number 1: Initiate Network Stock Deduction
		 
		currentNode = test.createNode("To verify that Network Admin is able to initiate Network Stock deduction for " + wallet + " wallet successfully");
		currentNode.assignCategory(assignCategory);
		if (deductionRoleStatus) {
			InitiateMap = NetworkStock.initiateStockDeduction(MultiWalletPreferenceValue, WalletCode, stockDeductionAmount);
			TransactionID = InitiateMap.get("TransactionID");
			InitiateMessage = InitiateMap.get("Message");
		} else {
			Log.skip("Network Stock Deduction is not available in system");
		}
		
		
		 * Test Case Number 2: Network Stock Initiation Message Validation
		 
		currentNode = test.createNode("To verify that proper Network Stock Initiation Message is displayed");
		currentNode.assignCategory(assignCategory);
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
		
		
		 * Test Case Number 3: Network Stock Approval Level 1
		 
		currentNode = test.createNode("To verify that Network Admin is able Approve Network Stock deduction at Level 1 Approval");
		currentNode.assignCategory(assignCategory);
		if (deductionRoleStatus)
			ApprovalMessage = NetworkStock.approveNetworkStockDeductionatLevel1(TransactionID, "Automated Level 1 Network Stock Approval");
		else
			Log.skip("Network Stock Deduction is not available in system");
		
		
		 * Test Case Number 5: Network Stock Approved Message Validation
		 
		currentNode = test.createNode("To verify that proper message is displayed on Successful Network Stock Deduction Approval");
		currentNode.assignCategory(assignCategory);
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
	}*/
	
	/*
	 * Test Case Number 7: To verify that Network Admin is able to reduce the Network Stock Initiation amount on Level 1 Approval.
	 */
	 @Test
	@TestManager(TestKey = "PRETUPS-275") // TO BE UNCOMMENTED WITH JIRA TEST ID
	public void TC7_AutoNetworkStock() throws NumberFormatException, SQLException, InterruptedException {
		final String methodname = "TC7_AutoNetworkStock";
		Log.startTestCase(methodname);
		
		currentNode = test.createNode(_masterVO.getCaseMasterByID("UNETWORKSTOCK10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		String PREF_AUTO_NETWORKSTOCK = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue("Network Code"), "AUTO_NWSTK_CRTN_ALWD");
		if (PREF_AUTO_NETWORKSTOCK != null && PREF_AUTO_NETWORKSTOCK.equalsIgnoreCase("true")) {
			NetworkStock NetworkStock = new NetworkStock(driver);
			O2CTransfer O2CTransfer = new O2CTransfer(driver);
			OperatorToChannelMap OperatorToChannelMap = new OperatorToChannelMap();
			HashMap<String, String> initiateMap = OperatorToChannelMap.getOperatorToChannelMapWithOperatorDetails(_masterVO.getProperty("O2CTransferCode"));
			
			boolean directO2CPreference = Boolean.parseBoolean(DBHandler.AccessHandler.getSystemPreference("AUTO_O2C_APPROVAL_ALLOWED"));
			String[] approvalLevel = DBHandler.AccessHandler.o2cApprovalLimits(initiateMap.get("TO_CATEGORY"), _masterVO.getMasterValue(MasterI.NETWORK_CODE));
			Long FirstApprovalLimit = Long.parseLong(approvalLevel[0]);
			Long SecondApprovalLimit = Long.parseLong(approvalLevel[1]);
			
			HashMap<String, String> AutoNS_Details = NetworkStock.evaluateAutoNetworkStockDetails(MultiWalletPreferenceValue);
			initiateMap.put("PRODUCT_TYPE", AutoNS_Details.get("Auto_NS_Product_Type"));
			initiateMap.put("INITIATION_AMOUNT", AutoNS_Details.get("O2C_InitiationAmount"));
			initiateMap = O2CTransfer.initiateO2CTransfer(initiateMap);
			long NetPayableAmount = _parser.getSystemAmount(initiateMap.get("NetPayableAmount"));
			if (!directO2CPreference) {
				if (NetPayableAmount <= FirstApprovalLimit) {
					O2CTransfer.performingLevel1Approval(initiateMap);
				} else if (NetPayableAmount <= SecondApprovalLimit) {
					O2CTransfer.performingLevel1Approval(initiateMap.get("TO_MSISDN"), initiateMap.get("TRANSACTION_ID"));
					O2CTransfer.performingLevel2Approval(initiateMap.get("TO_MSISDN"), initiateMap.get("TRANSACTION_ID"), null);
				} else if (NetPayableAmount > SecondApprovalLimit) {
					O2CTransfer.performingLevel1Approval(initiateMap.get("TO_MSISDN"), initiateMap.get("TRANSACTION_ID"));
					O2CTransfer.performingLevel2Approval(initiateMap.get("TO_MSISDN"), initiateMap.get("TRANSACTION_ID"), null);
					O2CTransfer.performingLevel3Approval(initiateMap.get("TO_MSISDN"), initiateMap.get("TRANSACTION_ID"), null);
				}	
			}
			
			NetworkStock.validateAutoNetworkStockModule(AutoNS_Details);
			NetworkStock.modifyAutoNetworkStockPreference(AutoNS_Details.get("Original_Preference_Value"));
			
		} else {
			Assertion.assertSkip("Auto Network Stock Module is not available system hence Test Case Skipped.");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodname);
	}
	
	/*
	 * DataProvider for Test Cases for Multi Wallet Handeling. The Test works on the basis of MULTIPLE_WALLET_APPLY
	 */
	@DataProvider(name = "availableWallets")
	public Object[][] walletType() {
		NetworkCode = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		MultiWalletPreferenceValue = DBHandler.AccessHandler.getNetworkPreference(NetworkCode, CONSTANT.MULTIWALLET_SYSTEM_STATUS);
		stockInitiationAmount = Integer.parseInt(_masterVO.getProperty("InitiateNetworkStockAmount"));
		stockDeductionAmount = Integer.parseInt(_masterVO.getProperty("NetworkStockDeductionAmount_UAP"));
		if (MultiWalletPreferenceValue.equalsIgnoreCase("true")) {
			return new Object[][] {{"Sale", PretupsI.SALE_WALLET_LOOKUP}, {"Free of Cost", PretupsI.FOC_WALLET_LOOKUP}, {"Incentive", PretupsI.INCENTIVE_WALLET_LOOKUP}};
		} else {
			return new Object[][] {{"Sale", PretupsI.SALE_WALLET_LOOKUP}};
		}
	}

}
