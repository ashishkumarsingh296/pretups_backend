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
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.MasterI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.reporting.extent.entity.ModuleManager;
import com.testmanagement.core.TestManager;
import com.utils.Assertion;
import com.utils.Log;
import com.utils._masterVO;
import com.utils._parser;
import com.utils.constants.Module;
@ModuleManager(name = Module.SIT_NETWORK_STOCK)
public class SIT_NetworkStock extends BaseTest {

	static boolean TestCaseCounter = false;
	String assignCategory="SIT";
	static String key="";
 
    @TestManager(TestKey = "PRETUPS-266") 
	@Test
	public void CASEA_NullWallet() {
    	 final String methodName = "CASEA_NullWallet";
         Log.startTestCase(methodName);
	
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK1").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("WalletType", "");
		
		if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			try {
				NetworkStock.initiateNetworkStock(networkParamMap);
				Assertion.assertFail("Network Stock Initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.nowallet");
			  Assertion.assertEquals(actualMessage, expectedMessage);
			}
		}
		else
			Assertion.assertSkip("Only single wallet available in system, hence Test Case skipped.");
		
		
			//currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
  
    @TestManager(TestKey = "PRETUPS-269") 
    	@Test
	public void CASEB_NullInitiationAmount() {
    	 final String methodName = "CASEB_NullInitiationAmount";
         Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK2").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "");
		
			try {
				NetworkStock.initiateNetworkStock(networkParamMap);
				Assertion.assertFail("Network Stock Initiated successfully, hence Test Case Failed");
				
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
		//	Validator.messageCompare(actualMessage, expectedMessage);
			  Assertion.assertEquals(actualMessage, expectedMessage);
			}
			Assertion.completeAssertions();
			Log.endTestCase(methodName);
	}
	

    @TestManager(TestKey = "PRETUPS-276") 
	@Test
	public void CASEC_BackBtnValidation() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASEB_NullInitiationAmount";
         Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK3").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("action", "back");
		
		if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			NetworkStock.initiateNetworkStock(networkParamMap);
		
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
			}
		else
			Assertion.assertSkip("Only single wallet available in system, hence Test Case skipped.");
		//	currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
			
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

    @TestManager(TestKey = "PRETUPS-281") 
	@Test
	public void CASED_NullAmountOnApproval() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASED_NullAmountOnApproval";
         Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK4").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		networkParamMap.put("ApprovedQuantity", "0");
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("ApprovalErrMessage") != null) {
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.error.appqtyrequired");
			 Assertion.assertEquals(networkParamMap.get("ApprovalErrMessage"), expectedMessage);
		}
		else
			Assertion.assertFail("No Error Message found, hence Test Case failed.");
			
		
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

    @TestManager(TestKey = "PRETUPS-282") 
	@Test
	public void CASEE_IncreasedAmountOnApproval() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASEE_IncreasedAmountOnApproval";
         Log.startTestCase(methodName);
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK5").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		long ApprovalQuantity = Integer.parseInt(networkParamMap.get("InitiationAmount")) + 5;
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		networkParamMap.put("ApprovedQuantity", "" + ApprovalQuantity);
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("ApprovalErrMessage") != null) {
			String preparedMessage = MessagesDAO.prepareMessageByKey("networkstock.level1approval.error.appqty");
			String expectedMessage = preparedMessage.split("\\{0}")[0];
			 Assertion.assertContainsEquals(networkParamMap.get("ApprovalErrMessage"), expectedMessage);
			
		}
		else
		Assertion.assertFail("No Error Message found, hence Test Case failed.");
		
		
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

	
    @TestManager(TestKey = "PRETUPS-284") 
	@Test
	public void CASEF_TransactionCancelOnApproval() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASEF_TransactionCancelOnApproval";
         Log.startTestCase(methodName);
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK6").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "cancelTxn");
		networkParamMap = NetworkStock.initiateNetworkStock(networkParamMap);
		try {
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		networkParamMap = NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		} catch(Exception e) {
	
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Network Stock Transaction Closed successfully at Approval Level 1", ExtentColor.GREEN));
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

    @TestManager(TestKey = "PRETUPS-286") 
	@Test
	public void CASEG_BackBtnValidationOnApproval() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASEF_TransactionCancelOnApproval";
         Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK7").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "back");
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

    @TestManager(TestKey = "PRETUPS-288") 
	@Test
	public void CASEH_ResetBtnValidationOnApproval() throws NumberFormatException, SQLException, AutomationException {
    	 final String methodName = "CASEH_ResetBtnValidationOnApproval";
         Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK8").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval1Action", "reset");
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (networkParamMap.get("RemarksAfterResetBtnOnApproval").equalsIgnoreCase(""))
			currentNode.log(Status.PASS, MarkupHelper.createLabel("Reset button working as expected, hence test case passed", ExtentColor.GREEN));
		else {
			Assertion.assertFail("Reset button not working as expected, hence test case failed");
			Assertion.assertFail("<pre><b>Expected:</b> On pressing Reset Button, the field should be cleared.<br><b>Found: </b> Value found in Remarks Field</pre>");
		//	currentNode.log(Status.FAIL, MarkupHelper.createLabel("Reset button not working as expected, hence test case failed", ExtentColor.RED));
			//currentNode.log(Status.FAIL, "<pre><b>Expected:</b> On pressing Reset Button, the field should be cleared.<br><b>Found: </b> Value found in Remarks Field</pre>");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
   
    @TestManager(TestKey = "PRETUPS-289")	
    @Test 
	public void CASEI_TransactionCancelOnApproval2() throws NumberFormatException, SQLException, AutomationException {
    	final String methodName = "CASEI_TransactionCancelOnApproval2";
        Log.startTestCase(methodName);
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK9").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean approvalLevel2Status = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE);
		
		if (approvalLevel2Status) {
			String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			_parser networkStockApprovalLimit = new _parser();
			networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
			long networkStockApprovalAmount = networkStockApprovalLimit.getValue() + 1;
			
			NetworkStockMap networkMap = new NetworkStockMap();
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
		} else {
		Assertion.assertSkip("Network Stock Approval Level 2 is not available in system");
			//	Log.skip("Network Stock Approval Level 2 is not available in system");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	

    @TestManager(TestKey = "PRETUPS-293") 
	@Test
	public void CASEJ_BackBtnValidationOnApproval2() throws NumberFormatException, SQLException, AutomationException {
    	final String methodName = "CASEI_TransactionCancelOnApproval2";
        Log.startTestCase(methodName);
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK10").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean approvalLevel2Status = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE);
		
		if (approvalLevel2Status) {
			String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
			_parser networkStockApprovalLimit = new _parser();
			networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
			long networkStockApprovalAmount = networkStockApprovalLimit.getValue() + 1;
			
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("approval2Action", "back");
			networkParamMap.put("InitiationAmount", "" + networkStockApprovalAmount);
			NetworkStock.initiateNetworkStock(networkParamMap);
			NetworkStock.approveNetworkStockatLevel1(networkParamMap);
			NetworkStock.approveNetworkStockatLevel2(networkParamMap);
			Assertion.assertPass("Back button working as expected, hence test case passed");
			//currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
		} else {
			Assertion.assertSkip("Network Stock Approval Level 2 is not available in system");
			//Log.skip("Network Stock Approval Level 2 is not available in system");
		}
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
    @TestManager(TestKey = "PRETUPS-299") 
	@Test
	public void CASEK_NetworkStockTransactionsDateValidation() throws NumberFormatException, SQLException, AutomationException {
    	final String methodName = "CASEK_NetworkStockTransactionsDateValidation";
        Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK11").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue();

		NetworkStockMap networkMap = new NetworkStockMap();
		HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap();
		NetworkStock.initiateNetworkStock(networkParamMap);
		NetworkStock.approveNetworkStockatLevel1(networkParamMap);
		if (Long.parseLong(networkParamMap.get("ModifiedTotalStock")) > networkStockApprovalAmount)
			NetworkStock.approveNetworkStockatLevel2(networkParamMap);
		NetworkStock.viewStockTransactions(networkParamMap);
	   currentNode.log(Status.PASS, MarkupHelper.createLabel("Transaction ID found in View Stock Transactions, hence Test Case Passed", ExtentColor.GREEN));
	
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
    }
   
    @TestManager(TestKey = "PRETUPS-300") 
	@Test
	public void CASEL_NetworkStockTransactionsDateValidation2() throws NumberFormatException, SQLException, AutomationException {
    	final String methodName = "CASEK_NetworkStockTransactionsDateValidation";
        Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK12").getExtentCase());
		currentNode.assignCategory(assignCategory);
		
		String selectedNetwork = _masterVO.getMasterValue(MasterI.NETWORK_CODE);
		_parser networkStockApprovalLimit = new _parser();
		networkStockApprovalLimit.convertStringToLong(DBHandler.AccessHandler.getNetworkPreference(selectedNetwork, CONSTANT.NETWORK_STOCK_FIRSTAPPROVAL_LIMIT)).changeDenomation();
		long networkStockApprovalAmount = networkStockApprovalLimit.getValue();
		
		NetworkStockMap networkMap = new NetworkStockMap();
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
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
    }

    @TestManager(TestKey = "PRETUPS-325") 
	@Test
	public void CASEM_NetworkStockDeductionNullWallet() {
    	final String methodName = "CASEM_NetworkStockDeductionNullWallet";
        Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK13").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		
		if (deductionRoleStatus) {
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("WalletType", "");
			
			if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
				try {
					NetworkStock.initiateStockDeduction(networkParamMap);
					Log.failNode("Network Stock Deduction initiated successfully, hence Test Case Failed");
				}
				catch(Exception e){
				String actualMessage = NetworkStock.getErrorMessage();
				String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.nowallet");
				//Validator.messageCompare(actualMessage, expectedMessage);
				Assertion.assertEquals(actualMessage, expectedMessage);
				}
			}
			else
				Assertion.assertSkip("Only single wallet available in system, hence Test Case skipped.");
				//currentNode.log(Status.SKIP, "Only single wallet available in system, hence Test Case skipped.");
		} else {
		//	Log.skip("Network Stock Deduction is not available in system");
		Assertion.assertSkip("Network Stock Deduction is not available in system");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
	
    @TestManager(TestKey = "PRETUPS-332") 
	@Test
	public void CASEN_NullInitiationAmount() {
    	final String methodName = "CASEM_NetworkStockDeductionNullWallet";
        Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK14").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		
		if (deductionRoleStatus) {
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "");
		
			try {
				NetworkStock.initiateStockDeduction(networkParamMap);
				Assertion.assertFail("Network Stock Deduction Initiated successfully, hence Test Case Failed");
				//Log.failNode("Network Stock Deduction Initiated successfully, hence Test Case Failed");
			}
			catch(Exception e){
			String actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
			Assertion.assertEquals(actualMessage, expectedMessage);
			//Validator.messageCompare(actualMessage, expectedMessage);
			}
		} else {
			Assertion.assertSkip("Network Stock Deduction is not available in system");
			//Log.skip("Network Stock Deduction is not available in system");
		}
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}

    @TestManager(TestKey = "PRETUPS-339") 
	@Test
	public void CASEO_stockDeductionBackBtnValidation() throws NumberFormatException, SQLException, AutomationException {
    	final String methodName = "CASEM_NetworkStockDeductionNullWallet";
        Log.startTestCase(methodName);
		
		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK15").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		
		if (deductionRoleStatus) {
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("action", "back");
			
			if (networkParamMap.get("MultiWalletPreference").equalsIgnoreCase("true")) {
			NetworkStock.initiateNetworkStock(networkParamMap);
			
	      currentNode.log(Status.PASS, MarkupHelper.createLabel("Back button working as expected, hence test case passed", ExtentColor.GREEN));
			} else {
				Assertion.assertSkip("Only single wallet available in system, hence Test Case skipped.");
				//Log.skip("Only single wallet available in system, hence Test Case skipped.");
			}
		} else {
		Assertion.assertSkip("Network Stock Deduction is not available in system");
			//	Log.skip("Network Stock Deduction is not available in system");
		}
		
		
		Assertion.completeAssertions();
		Log.endTestCase(methodName);
	}
    
    @TestManager(TestKey = "PRETUPS-1842") 
   	@Test
   	public void CASEP_network_stockDeduction() throws NumberFormatException, SQLException, AutomationException {
       	final String methodName = "network_stockDeduction";
           Log.startTestCase(methodName);
   		

   		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK16").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		
		if (deductionRoleStatus) {
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "100");
			HashMap<String, String> result;
			String actualMessage;
		
			try {
				result = NetworkStock.initiateStockDeductionSuccess(networkParamMap);
				key = result.get("TransactionID");
				String Expected = MessagesDAO.prepareMessageByKey("networkstock.initiatestock.deduction.msg.success",result.get("TransactionID"));
				 Assertion.assertEquals(result.get("result"), Expected);
			           Assertion.completeAssertions();
			        Log.endTestCase(methodName);
			        
			}
			catch(Exception e){
			 actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
			Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else {
			Assertion.assertSkip("Network Stock Deduction is not available in system");
		}
   		
   		Assertion.completeAssertions();
   		Log.endTestCase(methodName);
   	}
    
    
    @TestManager(TestKey = "PRETUPS-1843") 
   	@Test
   	public void CASEQ_network_stockDeductionApproval() throws NumberFormatException, SQLException, AutomationException {
       	final String methodName = "network_stockDeduction";
           Log.startTestCase(methodName);
   		

   		NetworkStock NetworkStock = new NetworkStock(driver);
		currentNode=test.createNode(_masterVO.getCaseMasterByID("SITNETWORKSTOCK17").getExtentCase());
		currentNode.assignCategory(assignCategory);
		boolean deductionRoleStatus = UserAccess.getRoleStatus(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		
		if (deductionRoleStatus) {
			NetworkStockMap networkMap = new NetworkStockMap();
			HashMap<String, String> networkParamMap = networkMap.getNetworkStockMap("InitiationAmount", "100");
			String actualMessage;
		
			try {
				actualMessage = NetworkStock.approveNetworkStockDeductionatLevel1(key,"Network Stock deduction Approval");
				String Expected = MessagesDAO.prepareMessageByKey("networkstock.level1approval.msg.success", key);
				 Assertion.assertEquals(actualMessage, Expected);
			           Assertion.completeAssertions();
			        Log.endTestCase(methodName);
			        
			}
			catch(Exception e){
			 actualMessage = NetworkStock.getErrorMessage();
			String expectedMessage = MessagesDAO.prepareMessageByKey("networkstock.includestocktxn.error.noitems");
			Assertion.assertEquals(actualMessage, expectedMessage);
			}
		} else {
			Assertion.assertSkip("Network Stock Deduction is not available in system");
		}
   		
   		Assertion.completeAssertions();
   		Log.endTestCase(methodName);
   	}
    
    
    

}