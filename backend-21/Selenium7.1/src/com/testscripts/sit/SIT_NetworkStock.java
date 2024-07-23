package com.testscripts.sit;

import java.sql.SQLException;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.Features.NetworkStock;
import com.Features.mapclasses.NetworkStockMap;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.classes.BaseTest;
import com.classes.CONSTANT;
import com.classes.MessagesDAO;
import com.commons.MasterI;
import com.dbrepository.DBHandler;
import com.utils.Log;
import com.utils.Validator;
import com.utils._masterVO;
import com.utils._parser;

public class SIT_NetworkStock extends BaseTest {

	static boolean TestCaseCounter = false;
	NetworkStockMap networkMap = new NetworkStockMap();
	
	@Test()
	public void CASEA_NullWallet() {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user does not select Wallet Type while Performing Network Stock Initiate");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("WalletType", "");
		
		if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			try {
				NetworkStock.initiateNetworkStock(networkParamMap);
				Log.failNode("Network Stock Initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.nowallet");
			Validator.messageCompare(actualMessage, expectedMessage);
			}
		}
		else
			currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
	}
	
	@Test()
	public void CASEB_NullInitiationAmount() {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user does not enter Network Stock Initiation Amount");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "");
		
			try {
				NetworkStock.initiateNetworkStock(networkParamMap);
				Log.failNode("Network Stock Initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
			Validator.messageCompare(actualMessage, expectedMessage);
			}
	}
	
	@Test()
	public void CASEC_BackBtnValidation() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is able to click Back Button");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("action", "back");
		
		if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			NetworkStock.initiateNetworkStock(networkParamMap);
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
		}
		else
			currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
			
	}
	
	@Test()
	public void CASED_NullAmountOnApproval() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user enters 0 as approval Quantity");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		networkParamMap.put("ApprovedQuantity", "0");
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("ApprovalErrMessage") != null) {
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.error.appqtyrequired");
			Validator.messageCompare(networkParamMap.get("ApprovalErrMessage"), expectedMessage);
		}
		else
			Log.failNode("No Error Message found, hence Test Case failed.");
		
	}
	
	@Test()
	public void CASEE_IncreasedAmountOnApproval() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user enteres Approval Quantity more than the Initiated quantity");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		long ApprovalQuantity = Integer.parseInt(networkParamMap.get("InitiationAmount")) + 5;
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		networkParamMap.put("ApprovedQuantity", "" + ApprovalQuantity);
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("ApprovalErrMessage") != null) {
			String preparedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.error.appqty");
			String expectedMessage = preparedMessage.split("{0}")[0];
			Validator.partialmessageCompare(networkParamMap.get("ApprovalErrMessage"), expectedMessage);
		}
		else
			Log.failNode("No Error Message found, hence Test Case failed.");
		
	}

	
	@Test()
	public void CASEF_TransactionCancelOnApproval() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that operator user is able to Cancel Network Stock Transaction at Approval Level 1");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "cancelTxn");
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		try {
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		} catch(Exception e) {
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Network Stock Transaction Closed successfully at Approval Level 1", ExtentColor.GREEN));
		}
	}
	
	@Test()
	public void CASEG_BackBtnValidationOnApproval() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is able to click Back Button on Network Stock Approval Level 1");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "back");
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
	}
	
	@Test()
	public void CASEH_ResetBtnValidationOnApproval() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is able to click Reset Button on Network Stock Approval Level 1");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "reset");
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("RemarksAfterResetBtnOnApproval").equalsIgnoreCase(""))
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Reset button working as expected, hence test case passed", ExtentColor.GREEN));
		else {
			currentNode.log(Status.FAIL, MarkupHelper.createLabel("Reset button not working as expected, hence test case failed", ExtentColor.RED));
			currentNode.log(Status.FAIL, "<pre><b>Expected:</b> On pressing Reset Button, the field should be cleared.<br><b>Found: </b> Value found in Remarks Field</pre>");
		}
	}
	
	@Test()
	public void CASEI_TransactionCancelOnApproval2() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that operator user is able to Cancel Network Stock Transaction at Approval Level 2");
		currentNode.assignCategory("SIT");
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue() + 1;
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval2Action", "cancelTxn");
		networkParamMap.put("InitiationAmount", "" + networkStockApprovalAmount);
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		try {
		networkParamMap = NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		networkParamMap = NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		} catch(Exception e) {
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Network Stock Transaction Closed successfully at Approval Level 2", ExtentColor.GREEN));
		}
	}
	
	@Test()
	public void CASEJ_BackBtnValidationOnApproval2() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is able to click Back Button on Network Stock Approval Level 1");
		currentNode.assignCategory("SIT");
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue() + 1;
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval2Action", "back");
		networkParamMap.put("InitiationAmount", "" + networkStockApprovalAmount);
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
	}
	
	@Test()
	public void CASEK_NetworkStockTransactionsDateValidation() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is view initiated Network Stock Transaction in Network Stock Transactions using TransactionID.");
		currentNode.assignCategory("SIT");
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue();
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (Long.parseLong(networkParamMap.get("ModifiedTotalStock")) > networkStockApprovalAmount)
			NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		NetworkStock.viewStockTransactions(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Transaction ID found in View Stock Transactions, hence Test Case Passed", ExtentColor.GREEN));
	}
	
	@Test()
	public void CASEL_NetworkStockTransactionsDateValidation2() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is view initiated Network Stock Transaction in Network Stock Transactions using FROM & TO Date criteria.");
		currentNode.assignCategory("SIT");
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue();
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (Long.parseLong(networkParamMap.get("ModifiedTotalStock")) > networkStockApprovalAmount)
			NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		
		//:::: Map for Data Validation Case ::::
		String transactionid_container = networkParamMap.get("TransactionID");
		networkParamMap.put("TransactionID", null);
		networkParamMap.put("ViewStockTransactionID", transactionid_container);
		
		NetworkStock.viewStockTransactions(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Transaction ID found in View Stock Transactions, hence Test Case Passed", ExtentColor.GREEN));
	}

	@Test()
	public void CASEM_NetworkStockDeductionNullWallet() {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user does not select Wallet Type while Performing Network Stock Deduction");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("WalletType", "");
		
		if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			try {
				NetworkStock.initiateStockDeduction(networkParamMap);
				Log.failNode("Network Stock Deduction initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.nowallet");
			Validator.messageCompare(actualMessage, expectedMessage);
			}
		}
		else
			currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
	}
	
	@Test()
	public void CASEN_NullInitiationAmount() {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that proper error message is displayed if user does not enter Network Stock Deduction Amount");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "");
		
			try {
				NetworkStock.initiateStockDeduction(networkParamMap);
				Log.failNode("Network Stock Deduction Initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
			Validator.messageCompare(actualMessage, expectedMessage);
			}
	}
	
	@Test()
	public void CASEO_stockDeductionBackBtnValidation() throws NumberFormatException, SQLException {
		Log.startTestCase(this.getClass().getName());
		
		if (TestCaseCounter == false) { 
			test = extent.createTest("[SIT]Network Stock");
			TestCaseCounter = true;
		}
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode("To verify that user is able to click Back Button while performing Network Stock Deduction");
		currentNode.assignCategory("SIT");
		
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("action", "back");
		
		NetworkStock.initiateNetworkStock(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
	}

}