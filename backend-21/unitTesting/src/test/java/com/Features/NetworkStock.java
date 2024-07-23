package com.Features;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.classes.CONSTANT;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.AutomationException;
import com.commons.ExcelI;
import com.commons.MasterI;
import com.commons.PretupsI;
import com.commons.RolesI;
import com.dbrepository.DBHandler;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkStockSubCategories;
import com.pageobjects.networkadminpages.homepage.PreferenceSubCategories;
import com.pageobjects.networkadminpages.homepage.ServiceClassPreference;
import com.pageobjects.networkadminpages.networkstock.InitiateNetworkStockPage;
import com.pageobjects.networkadminpages.networkstock.InitiateStockDeductionPage_1;
import com.pageobjects.networkadminpages.networkstock.InitiateStockDeductionPage_2;
import com.pageobjects.networkadminpages.networkstock.InitiateStockDeductionPage_3;
import com.pageobjects.networkadminpages.networkstock.NetworkStockApprovalPage;
import com.pageobjects.networkadminpages.networkstock.StockDeductionApprovalPage_1;
import com.pageobjects.networkadminpages.networkstock.StockDeductionApprovalPage_2;
import com.pageobjects.networkadminpages.networkstock.ViewStockTransactionsPage1;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.pageobjects.superadminpages.homepage.SuperAdminHomePage;
import com.pageobjects.superadminpages.preferences.ServicePreferencePage;
import com.pageobjects.superadminpages.preferences.SystemPreferencePage;
import com.testscripts.prerequisites.UpdateCache;
import com.utils.ExcelUtility;
import com.utils.Log;
import com.utils.PaginationHandler;
import com.utils._masterVO;
import com.utils._parser;

public class NetworkStock {
	
	WebDriver driver;
	NetworkAdminHomePage homePage;
	NetworkStockSubCategories NetworkStockSubCategories;
	Login login;
	InitiateNetworkStockPage NetworkStockPage;
	InitiateStockDeductionPage_1 StockDeductionPage_1;
	InitiateStockDeductionPage_2 StockDeductionPage_2;
	InitiateStockDeductionPage_3 StockDeductionPage_3;
	
	StockDeductionApprovalPage_1 StockDeductionApproval_1;
	StockDeductionApprovalPage_2 StockDeductionApproval_2;
	NetworkStockApprovalPage StockApproval;
	SelectNetworkPage networkPage;
	SystemPreferencePage sysPref;
	ServicePreferencePage servPref;
	SuperAdminHomePage suHomepage;
	NetworkAdminHomePage naHomepage;
	PreferenceSubCategories naPref;
	ServiceClassPreference naServPref;
	
	ViewStockTransactionsPage1 ViewStockTransactionsPage1;
	
	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public NetworkStock(WebDriver driver) {
		this.driver = driver;
		
		//Page Initialization
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		NetworkStockPage = new InitiateNetworkStockPage(driver);
		StockDeductionPage_1 = new InitiateStockDeductionPage_1(driver);
		StockDeductionPage_2 = new InitiateStockDeductionPage_2(driver);
		StockDeductionPage_3 = new InitiateStockDeductionPage_3(driver);
		
		StockDeductionApproval_1 = new StockDeductionApprovalPage_1(driver);
		StockDeductionApproval_2 = new StockDeductionApprovalPage_2(driver);
		StockApproval = new NetworkStockApprovalPage(driver);
		ViewStockTransactionsPage1 = new ViewStockTransactionsPage1(driver);
		NetworkStockSubCategories = new NetworkStockSubCategories(driver);
		networkPage = new SelectNetworkPage(driver);
		sysPref = new SystemPreferencePage(driver);
		servPref = new ServicePreferencePage(driver);
		suHomepage = new SuperAdminHomePage(driver);
		naHomepage = new NetworkAdminHomePage(driver);
		naPref = new PreferenceSubCategories(driver);
		naServPref = new ServiceClassPreference(driver);
	}
	
	/**
	 * Network Admin Initiate Flow
	 * @param MultiWalletPreferenceValue
	 * @param wallet
	 * @param stockInitiationAmount
	 * @return ResultMap <"Message", "TransactionID", "TotalInitiatedStock", "ApprovalLimit">
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 * @throws AutomationException 
	 */
	public HashMap<String, String> initiateNetworkStock(String MultiWalletPreferenceValue, String wallet, long stockInitiationAmount) throws NumberFormatException, SQLException, AutomationException {
		final String methodname = "initiateNetworkStock";
		Log.methodEntry(methodname, MultiWalletPreferenceValue, wallet, stockInitiationAmount);
		
		HashMap<String, String> ResultMap = new HashMap<String, String>();					
		Log.info("Loaded 'InitiateNetworkStockAmount' as: "+stockInitiationAmount);
			
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_INITIATE_ROLECODE); //Getting User with Access to Initiate Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		ResultMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
		ResultMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
		//User Access module ends.
				
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		if (MultiWalletPreferenceValue.equalsIgnoreCase("true")){
			NetworkStockPage.selectWalletType(wallet);
			ResultMap.put("WalletType", wallet);
			NetworkStockPage.clickSubmit();
		}
		NetworkStockPage.inputRandomRefNum();
		long totalStockInitiated = NetworkStockPage.inputProductsAmount(stockInitiationAmount);
		ResultMap.put("TotalInitiatedStock", ""+totalStockInitiated);
		NetworkStockPage.fetchproductPreBalances(MultiWalletPreferenceValue);
		NetworkStockPage.enterRemarks();
		NetworkStockPage.clickInitiateStock();
		NetworkStockPage.clickConfirm();
		String TransactionMessage[] = NetworkStockPage.getTransactionID();
		String ErrorMessage = NetworkStockPage.getErrorMessage();
		ResultMap.put("Message", TransactionMessage[0]);
		ResultMap.put("TransactionID", TransactionMessage[1]);
		ResultMap.put("ErrorMessage", ErrorMessage);
		String approvalLimit = StockApproval.getStockApprovalLimit();
		ResultMap.put("ApprovalLimit", approvalLimit);
		homePage.clickLogout();
		
		Log.methodExit(methodname);
		return ResultMap;
	}
	
	/**
	 * Network Admin Initiate Negative Flow
	 * @param HashMap<String, String>
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 * @throws AutomationException 
	 */
	public HashMap<String, String> initiateNetworkStock(HashMap<String, String> networkStockMap) throws NumberFormatException, SQLException, AutomationException {				
				
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_INITIATE_ROLECODE); //Getting User with Access to Initiate Network Stock
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				networkStockMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
				networkStockMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
				//User Access module ends.
				
				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				
				if (networkStockMap.get("MultiWalletPreference").equalsIgnoreCase("true")){
					if (!networkStockMap.get("WalletType").equalsIgnoreCase(""))
						NetworkStockPage.selectWalletType(networkStockMap.get("WalletType"));
					    networkStockMap.put("WalletType", networkStockMap.get("WalletType"));
				NetworkStockPage.clickSubmit();
				}
				
				//if else Conditions based on the tests to be done (action = submit / back)
				if (networkStockMap.get("action").equalsIgnoreCase("submit")) {
				NetworkStockPage.inputRefNum(networkStockMap.get("ReferenceNo"));
				if (!networkStockMap.get("InitiationAmount").equalsIgnoreCase("")) {
				long totalStockInitiated = NetworkStockPage.inputProductsAmount(Long.parseLong(networkStockMap.get("InitiationAmount")));
				networkStockMap.put("TotalInitiatedStock", ""+totalStockInitiated);
				NetworkStockPage.fetchproductPreBalances(networkStockMap.get("MultiWalletPreference"));
				}
				NetworkStockPage.enterRemarks(networkStockMap.get("Remarks"));
				NetworkStockPage.clickInitiateStock();
				NetworkStockPage.clickConfirm();
				String TransactionMessage[] = NetworkStockPage.getTransactionID();
				String ErrorMessage = NetworkStockPage.getErrorMessage();
				networkStockMap.put("Message", TransactionMessage[0]);
				networkStockMap.put("TransactionID", TransactionMessage[1]);
				networkStockMap.put("ErrorMessage", ErrorMessage);
				String approvalLimit = StockApproval.getStockApprovalLimit();
				networkStockMap.put("ApprovalLimit", approvalLimit);
				}
				else if (networkStockMap.get("action").equalsIgnoreCase("back")) {
					NetworkStockPage.clickBackButton();
					if (!networkStockMap.get("WalletType").equalsIgnoreCase(""))
						NetworkStockPage.selectWalletType(networkStockMap.get("WalletType"));
					NetworkStockPage.clickSubmit();
				}
				
				homePage.clickLogout();
				return networkStockMap;
			}
	
	public String getErrorMessage() {
		return NetworkStockPage.getErrorMessage();
	}
	
	/**
	 * Network Stock Approval Level 1 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String approveNetworkStockatLevel1(String TransactionID, String Remarks) {
		final String methodname = "approveNetworkStockatLevel1";
		Log.methodEntry(methodname, TransactionID, Remarks);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL1_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockApproval1();
		StockApproval.selectTransactionID(TransactionID);
		StockApproval.clickViewStockTransaction();
		StockApproval.enterApproval1Remarks(Remarks);
		StockApproval.clickApprove();
		StockApproval.clickConfirm();
		String Approval1Message = StockApproval.getMessage();
		
		Log.methodExit(methodname);
		return Approval1Message;
	}
	
	/**
	 * Network Stock Approval Level 1 Flow with Initiation Stock update (Negative)
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public HashMap<String, String> approveNetworkStockatLevel1(HashMap<String, String> networkStockMap) {
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL1_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockApproval1();
		StockApproval.selectTransactionID(networkStockMap.get("TransactionID"));
		StockApproval.clickViewStockTransaction();
		if (networkStockMap.get("ApprovedQuantity") != null)
			networkStockMap.put("ModifiedTotalStock", "" + StockApproval.inputProductsAmount(Integer.parseInt(networkStockMap.get("ApprovedQuantity"))));
		else
			networkStockMap.put("ModifiedTotalStock", networkStockMap.get("TotalInitiatedStock"));
		StockApproval.enterApproval1Remarks(networkStockMap.get("Remarks"));
		
		if (networkStockMap.get("approval1Action").equalsIgnoreCase("submit")) {
		StockApproval.clickApprove();
		StockApproval.clickConfirm();
		networkStockMap.put("ApprovalMessage", StockApproval.getMessage());
		networkStockMap.put("ApprovalErrMessage", StockApproval.getErrorMessage());
		}
		else if (networkStockMap.get("approval1Action").equalsIgnoreCase("cancelTxn")) {
			StockApproval.clickReject();
			driver.switchTo().alert().accept();
		}
		else if (networkStockMap.get("approval1Action").equalsIgnoreCase("back")) {
			StockApproval.clickBackButton();
			StockApproval.selectTransactionID(networkStockMap.get("TransactionID"));
			StockApproval.clickViewStockTransaction();
		}
		else if (networkStockMap.get("approval1Action").equalsIgnoreCase("reset")) {
			StockApproval.clickResetButton();
			networkStockMap.put("RemarksAfterResetBtnOnApproval", StockApproval.getApproval1Remarks());
		}
		
		return networkStockMap;
	}
	
	
	/**
	 * Network Stock Approval Level 2 Flow (Negative)
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public HashMap<String, String> approveNetworkStockatLevel2(HashMap<String, String> networkStockMap) {
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockApproval2();
		StockApproval.selectTransactionID(networkStockMap.get("TransactionID"));
		StockApproval.clickViewStockTransaction();
		StockApproval.enterApproval2Remarks(networkStockMap.get("Remarks"));
				
			if (networkStockMap.get("approval2Action").equalsIgnoreCase("submit")) {
				StockApproval.clickApprove();
				StockApproval.clickConfirm();
				networkStockMap.put("ApprovalMessage", StockApproval.getMessage());
				networkStockMap.put("ApprovalErrMessage", StockApproval.getErrorMessage());
				}
			else if (networkStockMap.get("approval2Action").equalsIgnoreCase("cancelTxn")) {
				StockApproval.clickReject();
				driver.switchTo().alert().accept();
			}
			else if (networkStockMap.get("approval2Action").equalsIgnoreCase("back")) {
				StockApproval.clickBackButton();
				StockApproval.selectTransactionID(networkStockMap.get("TransactionID"));
				StockApproval.clickViewStockTransaction();
			}
			else if (networkStockMap.get("approval2Action").equalsIgnoreCase("reset")) {
				StockApproval.clickResetButton();
				networkStockMap.put("RemarksAfterResetBtnOnApproval", StockApproval.getApproval2Remarks());
			}

	return networkStockMap;
	}
	
	
	/**
	 * View Current Network Stock 
	 * @param WalletCode
	 * @return HashMap
	 * @throws SQLException 
	 */
	public String[][] getCurrentNetworkStockDetails(String NetworkCode, String WalletCode) throws SQLException {
		final String methodName = "getCurrentNetworkStockDetails";
		Log.debug("Entered ::" + methodName + "( "+ NetworkCode + ", " + WalletCode + ")");	
		
		Object[][] productObj; 
		productObj = DBHandler.AccessHandler.getProductsDetails(NetworkCode, WalletCode);
		int productObjSize = productObj.length;
		String productBalances[][] = new String[productObjSize][3];
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_CURRENT_STOCK_ROLECODE); //Getting User with Access to View Current Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickViewCurrentStock();
				
		for (int i=0; i<productObjSize; i++) {
			productBalances[i][0] = productObj[i][1].toString();
			productBalances[i][1] = driver.findElement(By.xpath("//tr/td[@class = 'tabcol' and text()[normalize-space() = '"+ productObj[i][1] +"']]/following-sibling::td[2][text() = '"+ WalletCode +"']/following-sibling::td[4]")).getText();
			productBalances[i][2] = productObj[i][0].toString();
		}
		
		Log.debug(methodName, productBalances);
		return productBalances;	
	}
	
	/**
	 * View Current Stock Table Validator
	 * @Param PreBalance[][], PostBalance[][]
	 * @return boolean
	 */
	public boolean validateCurrentNetworkStock(Object[][] preBalances, Object[][] postBalances, int InitiationAmount) {
		boolean validatorStatus = false;
		int productObjectSize = preBalances.length;
		for (int i=0; i<productObjectSize; i++) {
			Log.info("Validating Current Network Stock for Product: "+ preBalances[i][0]);
			Log.info("Pre Balance Found: " + preBalances[i][1] + " | Post Balance Found: " + postBalances[i][1] + " | Initiation Amount: " + InitiationAmount);
	        BigDecimal preBalanceAmount = new BigDecimal((String)preBalances[i][1]);
	        BigDecimal initiateAmount = new BigDecimal(InitiationAmount);
	        BigDecimal ExpectedPreBalance = preBalanceAmount.add(initiateAmount);
			String ExpectedBalance = ExpectedPreBalance.toString();
			/*double ExpectedPreBalance = Double.parseDouble(String.valueOf(preBalances[i][1])) + (double)InitiationAmount;
			String ExpectedBalance = "" + ExpectedPreBalance;*/
			if (ExpectedBalance.equals(postBalances[i][1].toString())) {
				Log.info("<b>Stock Validation Successful.</b>");
				validatorStatus = true;
			}
			else {
				Log.failNode("Enquiry Failure For Product: " + preBalances[i][0]);
				Log.failNode("Expected [" + ExpectedBalance + "] but found [" + postBalances[i][1] + "]");
				validatorStatus=false;
			}
		}
		return validatorStatus;
	}
	
	/**
	 * Network Stock Approval Level 1 Flow with Initiation Stock update
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public Object[] approveNetworkStockatLevel1(String TransactionID, int updatedAmount, String Remarks) {
		
		Object[] returnObj= new Object[2];
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL1_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockApproval1();
		StockApproval.selectTransactionID(TransactionID);
		StockApproval.clickViewStockTransaction();
		int ModifiedTotalStock = StockApproval.inputProductsAmount(updatedAmount);
		StockApproval.enterApproval1Remarks(Remarks);
		StockApproval.clickApprove();
		StockApproval.clickConfirm();
		String Approval1Message = StockApproval.getMessage();
		returnObj[0] = Approval1Message;
		returnObj[1] = ModifiedTotalStock;
		return returnObj;
	}
	
	/**
	 * Network Stock Reject at Level 1 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String rejectNetworkStockatLevel1(String TransactionID) {
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL1_ROLECODE); //Getting User with Access to Approve Network Stock
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				//User Access module ends.

				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockApproval1();
				StockApproval.selectTransactionID(TransactionID);
				StockApproval.clickViewStockTransaction();
				StockApproval.clickReject();
				StockApproval.PressOkOnConfirmRejectDialog();
				String RejectionMessage = StockApproval.getMessage();
				return RejectionMessage;
			}
	
	/**
	 * Network Stock Reject at Level 2 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String rejectNetworkStockatLevel2(String TransactionID) {
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE); //Getting User with Access to Approve Network Stock
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				//User Access module ends.

				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockApproval2();
				StockApproval.selectTransactionID(TransactionID);
				StockApproval.clickViewStockTransaction();
				StockApproval.clickReject();
				StockApproval.PressOkOnConfirmRejectDialog();
				String RejectionMessage = StockApproval.getMessage();
				return RejectionMessage;
			}
	
	
	/**
	 * Network Stock Approval Level 2 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String approveNetworkStockatLevel2(String TransactionID, String Remarks) {
		final String methodname = "approveNetworkStockatLevel2";
		Log.methodEntry(methodname, TransactionID, Remarks);
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_APPROVAL_LEVEL2_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockApproval2();
		StockApproval.selectTransactionID(TransactionID);
		StockApproval.clickViewStockTransaction();
		StockApproval.enterApproval2Remarks(Remarks);
		StockApproval.clickApprove();
		StockApproval.clickConfirm();
		String Approval2Message = StockApproval.getMessage();
		
		Log.methodExit(methodname);
		return Approval2Message;
	}
	
	/**
	 * Verify Post Network Stock Flow
	 * @param MultiWalletPreferenceValue
	 * @return boolean
	 */
	public boolean verifyPostNetworkStock(String MultiWalletPreferenceValue, String wallet, int stockInitiationAmount) {
				homePage.clickNetworkStock();
				if (MultiWalletPreferenceValue.equalsIgnoreCase("true")){
				NetworkStockPage.selectWalletType(wallet);
				NetworkStockPage.clickSubmit();
				}
				NetworkStockPage.fetchproductPostBalances(MultiWalletPreferenceValue);
				boolean stockComparisonResult = NetworkStockPage.ComparePostStocks(stockInitiationAmount);
				
				return stockComparisonResult;
				//Assert.assertTrue(stockComparisonResult, "Post Stock Balance Does not match with Pre Balance + Requested Quantity");
			}

	public boolean getTransactionStatusInNetworkStockTransactionsTable(String TransactionID) {
		boolean TransactionStatus = false;
		String RepoStatus = DBHandler.AccessHandler.checkNetworkStockTransactionsForNetworkStockID(TransactionID);
		if (RepoStatus.equals("Y")) {
			TransactionStatus = true;
		}
		return TransactionStatus;
	}
	
	/**
	 * Network Stock Deduction Flow
	 * @param MultiWalletPreferenceValue
	 * @param wallet
	 * @param stockInitiationAmount
	 * @return ResultMap <"Message", "TransactionID", "TotalInitiatedStock", "ApprovalLimit">
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 */
	public HashMap<String, String> initiateStockDeduction(String MultiWalletPreferenceValue, String wallet, int stockInitiationAmount) throws NumberFormatException, SQLException {
				HashMap<String, String> ResultMap = new HashMap<String, String>();					
				Log.info("Loaded 'InitiateNetworkStockAmount' as: "+stockInitiationAmount);
				
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				ResultMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
				ResultMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
				//User Access module ends.
				
				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockDeduction();
				if (MultiWalletPreferenceValue.equalsIgnoreCase("true")){
				StockDeductionPage_1.selectWalletType(wallet);
				ResultMap.put("WalletType", wallet);
				StockDeductionPage_1.clickSubmit();
				}
				StockDeductionPage_2.inputRandomRefNum();
				int totalStockInitiated = StockDeductionPage_2.inputProductsAmount(stockInitiationAmount);
				ResultMap.put("TotalInitiatedStock", ""+totalStockInitiated);
				StockDeductionPage_2.fetchproductPreBalances(MultiWalletPreferenceValue);
				StockDeductionPage_2.enterRemarks();
				StockDeductionPage_2.clickInitiateStock();
				StockDeductionPage_3.clickConfirm();
				String TransactionMessage[] = StockDeductionPage_3.getTransactionID();
				String ErrorMessage = StockDeductionPage_3.getErrorMessage();
				ResultMap.put("Message", TransactionMessage[0]);
				ResultMap.put("TransactionID", TransactionMessage[1]);
				ResultMap.put("ErrorMessage", ErrorMessage);
				homePage.clickLogout();
				return ResultMap;
			}

	/**
	 * Network Stock Deduction Flow (Negative)
	 * @param HashMap<String, String>
	 * @return ResultMap <"Message", "TransactionID", "TotalInitiatedStock", "ApprovalLimit">
	 * @throws SQLException 
	 * @throws NumberFormatException 
	 */
	public HashMap<String, String> initiateStockDeduction(HashMap<String, String> deductionMap) throws NumberFormatException, SQLException {
				
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				deductionMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
				deductionMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
				//User Access module ends.
				
				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockDeduction();
				if (deductionMap.get("MultiWalletPreference").equalsIgnoreCase("true")){
					if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
						StockDeductionPage_1.selectWalletType(deductionMap.get("WalletType"));
				deductionMap.put("WalletType", deductionMap.get("WalletType"));
				StockDeductionPage_1.clickSubmit();
				}
				
				StockDeductionPage_2.inputRandomRefNum();
				//if else Conditions based on the tests to be done (action = submit / back)
				if (deductionMap.get("action").equalsIgnoreCase("submit")) {				
					if (!deductionMap.get("InitiationAmount").equalsIgnoreCase("")) {
						deductionMap.put("TotalInitiatedStock","" + StockDeductionPage_2.inputProductsAmount(Integer.parseInt(deductionMap.get("InitiationAmount"))));
						StockDeductionPage_2.fetchproductPreBalances(deductionMap.get("MultiWalletPreference"));
					}
				StockDeductionPage_2.enterRemarks();
				StockDeductionPage_2.clickInitiateStock();
				StockDeductionPage_3.clickConfirm();
				String TransactionMessage[] = StockDeductionPage_3.getTransactionID();
				String ErrorMessage = StockDeductionPage_3.getErrorMessage();
				deductionMap.put("Message", TransactionMessage[0]);
				deductionMap.put("TransactionID", TransactionMessage[1]);
				deductionMap.put("ErrorMessage", ErrorMessage);
				}
				else if (deductionMap.get("action").equalsIgnoreCase("back")) {
					NetworkStockPage.clickBackButton();
					if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
						NetworkStockPage.selectWalletType(deductionMap.get("WalletType"));
					NetworkStockPage.clickSubmit();
				}
				homePage.clickLogout();
				
				return deductionMap;
			}

	
	
	/**
	 * Network Stock Deduction Approval Level 1 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public String approveNetworkStockDeductionatLevel1(String TransactionID, String Remarks) {
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_APPROVAL_ROLECODE); //Getting User with Access to Approve Network Stock
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				//User Access module ends.

				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockDeductionApproval();
				StockDeductionApproval_1.selectTransactionID(TransactionID);
				StockDeductionApproval_1.clickViewStockDetails();
				StockDeductionApproval_2.enterApproval1Remarks(Remarks);
				StockDeductionApproval_2.clickApprove();
				StockDeductionApproval_2.clickConfirmButton();
				String Approval1Message = StockDeductionApproval_2.getMessage();
				return Approval1Message;
			}
	
	
	/**
	 * Network Stock Deduction Approval Level 1 Flow
	 * @param TransactionID
	 * @param Remarks
	 * @return Approval Message
	 */
	public HashMap<String, String> approveNetworkStockDeductionatLevel1(HashMap<String, String> deductionMap) {
				//Operator User Access Implementation by Krishan.
				userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_APPROVAL_ROLECODE); //Getting User with Access to Approve Network Stock
				login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
				//User Access module ends.

				networkPage.selectNetwork();
				homePage.clickNetworkStock();
				NetworkStockSubCategories.clickNetworkStockDeductionApproval();
				StockDeductionApproval_1.selectTransactionID(deductionMap.get("TransactionID"));
				StockDeductionApproval_1.clickViewStockDetails();
				if (deductionMap.get("ApprovedQuantity") != null)
					deductionMap.put("ModifiedTotalStock", "" + StockDeductionApproval_1.inputProductsAmount(Integer.parseInt(deductionMap.get("ApprovedQuantity"))));
				else
					deductionMap.put("ModifiedTotalStock", deductionMap.get("TotalInitiatedStock"));
				
				StockDeductionApproval_2.enterApproval1Remarks(deductionMap.get("Remarks"));
				StockDeductionApproval_2.clickApprove();
				StockDeductionApproval_2.clickConfirmButton();
				deductionMap.put("ApprovalMessage", StockDeductionApproval_2.getMessage());
				return deductionMap;
			}
	
	/**
	 * Network Stock Transactions
	 * @param HashMap<String, String>
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> viewStockTransactions(HashMap<String, String> stockTransactionMap) {
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_STOCK_TRANSACTIONS_ROLECODE); //Getting User with Access to Approve Network Stock
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.

		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickViewStockTransactions();
		if (stockTransactionMap.get("TransactionID") != null)
			ViewStockTransactionsPage1.enterTransactionID(stockTransactionMap.get("TransactionID"));
		if (stockTransactionMap.get("viewTransactionFromDate") != null)
			ViewStockTransactionsPage1.enterFromDate(stockTransactionMap.get("viewTransactionFromDate"));
		if (stockTransactionMap.get("viewTransactionToDate") != null)
			ViewStockTransactionsPage1.enterToDate(stockTransactionMap.get("viewTransactionToDate"));
		if (stockTransactionMap.get("viewTransactionStockType") != null)
		ViewStockTransactionsPage1.selectStockType(stockTransactionMap.get("viewTransactionStockType"));
		
		ViewStockTransactionsPage1.clickSubmitButton();
		PaginationHandler PaginationHandler = new PaginationHandler();
		PaginationHandler.getToLastPage(driver);
		
		if (stockTransactionMap.get("ViewStockTransactionID") != null)
			ViewStockTransactionsPage1.selectTransactionIDRadio(stockTransactionMap.get("ViewStockTransactionID"));
		else
			ViewStockTransactionsPage1.selectTransactionIDRadio(stockTransactionMap.get("TransactionID"));
		ViewStockTransactionsPage1.clickSubmitButton();
		return stockTransactionMap;
	}
	
	public HashMap<String, String> evaluateAutoNetworkStockDetails(String MultiWalletPreferenceValue) throws SQLException {
		final String methodname = "getAutoNetworkStockDetails";
		Log.methodEntry(methodname);
		
		HashMap<String, String> autoNetworkStockDetails= new HashMap<String, String>();
		String AUTONETWORK_STR = DBHandler.AccessHandler.getNetworkPreference(_masterVO.getMasterValue(MasterI.NETWORK_CODE), "AUTO_NWSTK_CRTN_THRESHOLD");
		
		autoNetworkStockDetails.put("Original_Preference_Value", AUTONETWORK_STR);
		
		if (AUTONETWORK_STR != null) {
			String[] breakdown = AUTONETWORK_STR.split(",");
			String[] stockLevelPref = breakdown[0].split(":");
			autoNetworkStockDetails.put("WalletType", stockLevelPref[0]);
			autoNetworkStockDetails.put("Auto_NS_Product_Code", stockLevelPref[1]);
			
			long currentBalance = Long.parseLong(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), autoNetworkStockDetails.get("WalletType")), 0, 2).get(autoNetworkStockDetails.get("Auto_NS_Product_Code")));	
			autoNetworkStockDetails.put("Stock_Before_Auto_NS", _parser.getDisplayAmount(currentBalance));
			
			long newThreshold = currentBalance - _parser.getSystemAmount(50);
			autoNetworkStockDetails.put("Auto_NS_Threshold", _parser.getDisplayAmount(newThreshold));
			autoNetworkStockDetails.put("Auto_NS_Value", stockLevelPref[3]);
			
			String newPreferenceValue = autoNetworkStockDetails.get("WalletType") + ":" + autoNetworkStockDetails.get("Auto_NS_Product_Code") + ":" + autoNetworkStockDetails.get("Auto_NS_Threshold") + ":" + autoNetworkStockDetails.get("Auto_NS_Value");
			
			modifyAutoNetworkStockPreference(newPreferenceValue);
			
			ExcelUtility.setExcelFile(_masterVO.getProperty("DataProvider"), ExcelI.PRODUCT_SHEET);
			int rowCount = ExcelUtility.getRowCount();
			for (int i = 0; i<=rowCount; i++) {
				String ProductCode = ExcelUtility.getCellData(0, ExcelI.PRODUCT_CODE, i);
				if (ProductCode.equalsIgnoreCase(autoNetworkStockDetails.get("Auto_NS_Product_Code"))) {
					autoNetworkStockDetails.put("Auto_NS_Product_Type", ExcelUtility.getCellData(0, ExcelI.PRODUCT_TYPE, i));
					break;
				}
			}
			
			autoNetworkStockDetails.put("O2C_InitiationAmount", "100");
		}
		
		Log.info(methodname + " returns: " + Arrays.asList(autoNetworkStockDetails));
		Log.methodExit(methodname);
		return autoNetworkStockDetails;
	}
	
	/**
	 * Modify Auto Network Stock Preference.
	 */
	public void modifyAutoNetworkStockPreference(String value){
		String preferenceCode2 = DBHandler.AccessHandler.getNamefromSystemPreference(CONSTANT.AUTO_NWSTK_CRTN_THRESHOLD);
		String valuespreftype[] = DBHandler.AccessHandler.getTypeOFPreference("", _masterVO.getMasterValue("Network Code"), CONSTANT.AUTO_NWSTK_CRTN_THRESHOLD);
		if(valuespreftype[1].equals(PretupsI.NETWORK_PREFERENCE_TYPE)){
			Map<String,String> usermapO=UserAccess.getUserWithAccess(RolesI.NETWORK_PREFERENCE);
			login.LoginAsUser(driver, usermapO.get("LOGIN_ID"), usermapO.get("PASSWORD"));
			networkPage.selectNetwork();	
			naHomepage.clickPreferences();
				naPref.clickNetworkPreferenceLink();
				servPref.setValueofServicePreference(preferenceCode2, value);
				servPref.clickModifyBtn();
				servPref.clickConfirmBtn();
		} else if (valuespreftype[1].equals(PretupsI.SYSTEM_PREFERENCE_TYPE)) {
			Map<String,String> usermapO=UserAccess.getUserWithAccess(RolesI.SYSTEM_PREFERENCE);
			login.LoginAsUser(driver, usermapO.get("LOGIN_ID"), usermapO.get("PASSWORD"));
			networkPage.selectNetwork();	
			naHomepage.clickPreferences();
				sysPref.clickSystemPrefernce();
				sysPref.selectModule("C2S");
				sysPref.selectPreferenceType(PretupsI.NETWORK_PREFERENCE_TYPE);
				sysPref.clickSubmitButton();
				sysPref.setValueofSystemPreference(preferenceCode2, value);
				sysPref.clickModifyBtn();
				sysPref.clickConfirmBtn();
		}
		
		new UpdateCache().updateCache();
	}
	
	public HashMap<String, String> validateAutoNetworkStockModule(HashMap<String, String> autoNetworkStockDetails) throws SQLException {
		final String methodname = "validateAutoNetworkStockModule";
		Log.methodEntry(methodname, Arrays.asList(autoNetworkStockDetails));
		
		long currentBalance = Long.parseLong(_parser.convertToHashMap(DBHandler.AccessHandler.getProductsDetails(_masterVO.getMasterValue(MasterI.NETWORK_CODE), autoNetworkStockDetails.get("WalletType")), 0, 2).get(autoNetworkStockDetails.get("Auto_NS_Product_Code")));
		
		autoNetworkStockDetails.put("Stock_After_Auto_NS", _parser.getDisplayAmount(currentBalance));	
		long NS_Before_AutoNS = _parser.getSystemAmount(autoNetworkStockDetails.get("Stock_Before_Auto_NS"));
		long Expected_NS_After_AutoNS = (NS_Before_AutoNS - _parser.getSystemAmount(autoNetworkStockDetails.get("O2C_InitiationAmount"))) + _parser.getSystemAmount(autoNetworkStockDetails.get("Auto_NS_Value"));
		
		if (currentBalance == Expected_NS_After_AutoNS) {
			StringBuilder logStr = new StringBuilder("<pre>----- Auto Network Stock Validator -----<br>");
			logStr.append("Network Stock Before O2C Transfer: " + _parser.getDisplayAmount(NS_Before_AutoNS) + "<br>");
			logStr.append("Auto Network Stock Threshold: " + autoNetworkStockDetails.get("Auto_NS_Threshold") + "<br>");
			logStr.append("Auto Network Stock Value: " + autoNetworkStockDetails.get("Auto_NS_Value") + "<br>");
			logStr.append("O2C Transfer Intiation Amount: " + autoNetworkStockDetails.get("O2C_InitiationAmount") + "<br>");
			logStr.append("Network Stock After O2C Transfer: " + _parser.getDisplayAmount(currentBalance) + "<br>");
			logStr.append("Expected Network Stock (" +_parser.getDisplayAmount(Expected_NS_After_AutoNS)+ ") = Actual Network Stock (" + _parser.getDisplayAmount(currentBalance) + ")<br>");
			logStr.append("<font color='limegreen'><b>Auto Network Stock is working as epxected</b></font></pre>");
			Log.info(logStr.toString());
		} else {
			StringBuilder logStr = new StringBuilder("<pre>----- Auto Network Stock Validator -----<br>");
			logStr.append("Network Stock Before O2C Transfer: " + _parser.getDisplayAmount(NS_Before_AutoNS) + "<br>");
			logStr.append("Auto Network Stock Threshold: " + autoNetworkStockDetails.get("Auto_NS_Threshold") + "<br>");
			logStr.append("Auto Network Stock Value: " + autoNetworkStockDetails.get("Auto_NS_Value") + "<br>");
			logStr.append("O2C Transfer Intiation Amount: " + autoNetworkStockDetails.get("O2C_InitiationAmount") + "<br>");
			logStr.append("Network Stock After O2C Transfer: " + _parser.getDisplayAmount(currentBalance) + "<br>");
			logStr.append("Expected Network Stock (" +_parser.getDisplayAmount(Expected_NS_After_AutoNS)+ ") != Actual Network Stock (" + _parser.getDisplayAmount(currentBalance) + ")<br>");
			logStr.append("<font color='red'><b>Auto Network Stock is not working as expected</b></font></pre>");
			Log.failNode(logStr.toString());
		}
		
		Log.info(methodname + " returns: " + Arrays.asList(autoNetworkStockDetails));
		Log.methodExit(methodname);
		return autoNetworkStockDetails;
	}
	public HashMap<String, String> initiateStockDeductionSuccess(HashMap<String, String> deductionMap) throws NumberFormatException, SQLException {
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		deductionMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
		deductionMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockDeduction();
		if (deductionMap.get("MultiWalletPreference").equalsIgnoreCase("true")){
			if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
				StockDeductionPage_1.selectWalletType(deductionMap.get("WalletType"));
		deductionMap.put("WalletType", deductionMap.get("WalletType"));
		StockDeductionPage_1.clickSubmit();
		}
		
		StockDeductionPage_2.inputRandomRefNum();
		//if else Conditions based on the tests to be done (action = submit / back)
		if (deductionMap.get("action").equalsIgnoreCase("submit")) {				
			if (!deductionMap.get("InitiationAmount").equalsIgnoreCase("")) {
				deductionMap.put("TotalInitiatedStock","" + StockDeductionPage_2.inputProductsAmount(Integer.parseInt(deductionMap.get("InitiationAmount"))));
				StockDeductionPage_2.fetchproductPreBalances(deductionMap.get("MultiWalletPreference"));
			}
		StockDeductionPage_2.enterRemarks();
		StockDeductionPage_2.clickInitiateStock();
		StockDeductionPage_3.clickConfirm();
		String TransactionMessage[] = StockDeductionPage_3.getTransactionID();
		String result = StockDeductionPage_3.getMessage();
		deductionMap.put("TransactionID", TransactionMessage[1]);
		deductionMap.put("result", result);
		}
		else if (deductionMap.get("action").equalsIgnoreCase("back")) {
			NetworkStockPage.clickBackButton();
			if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
				NetworkStockPage.selectWalletType(deductionMap.get("WalletType"));
			NetworkStockPage.clickSubmit();
		}
		homePage.clickLogout();
		
		return deductionMap;
	}

public HashMap<String, String> initiateStockDeductionApproval(HashMap<String, String> deductionMap) throws NumberFormatException, SQLException {
		
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.NETWORK_STOCK_DEDUCTION_ROLECODE);
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		deductionMap.put("Initiator LoginID", userAccessMap.get("LOGIN_ID"));
		deductionMap.put("Initiator UserName", userAccessMap.get("USER_NAME"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickNetworkStockDeduction();
		if (deductionMap.get("MultiWalletPreference").equalsIgnoreCase("true")){
			if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
				StockDeductionPage_1.selectWalletType(deductionMap.get("WalletType"));
		deductionMap.put("WalletType", deductionMap.get("WalletType"));
		StockDeductionPage_1.clickSubmit();
		}
		
		StockDeductionPage_2.inputRandomRefNum();
		//if else Conditions based on the tests to be done (action = submit / back)
		if (deductionMap.get("action").equalsIgnoreCase("submit")) {				
			if (!deductionMap.get("InitiationAmount").equalsIgnoreCase("")) {
				deductionMap.put("TotalInitiatedStock","" + StockDeductionPage_2.inputProductsAmount(Integer.parseInt(deductionMap.get("InitiationAmount"))));
				StockDeductionPage_2.fetchproductPreBalances(deductionMap.get("MultiWalletPreference"));
			}
		StockDeductionPage_2.enterRemarks();
		StockDeductionPage_2.clickInitiateStock();
		StockDeductionPage_3.clickConfirm();
		String TransactionMessage[] = StockDeductionPage_3.getTransactionID();
		String result = StockDeductionPage_3.getMessage();
		deductionMap.put("TransactionNo", TransactionMessage[0]);
		deductionMap.put("result", result);
		}
		else if (deductionMap.get("action").equalsIgnoreCase("back")) {
			NetworkStockPage.clickBackButton();
			if (!deductionMap.get("WalletType").equalsIgnoreCase(""))
				NetworkStockPage.selectWalletType(deductionMap.get("WalletType"));
			NetworkStockPage.clickSubmit();
		}
		homePage.clickLogout();
		
		return deductionMap;
	}

}
