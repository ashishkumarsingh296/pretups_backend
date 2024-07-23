package com.Features.Enquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.classes.BaseTest;
import com.classes.Login;
import com.classes.UserAccess;
import com.commons.RolesI;
import com.pageobjects.networkadminpages.homepage.NetworkAdminHomePage;
import com.pageobjects.networkadminpages.homepage.NetworkStockSubCategories;
import com.pageobjects.networkadminpages.networkstock.ViewStockTransactionsPage1;
import com.pageobjects.superadminpages.homepage.SelectNetworkPage;
import com.utils.Log;

public class NetworkStockTransactions extends BaseTest{
	
	WebDriver driver;
	NetworkAdminHomePage homePage;
	NetworkStockSubCategories NetworkStockSubCategories;
	Login login;
	ViewStockTransactionsPage1 ViewStockTransactionsPage1;
	SelectNetworkPage networkPage;

	Map<String, String> userAccessMap = new HashMap<String, String>();
	
	public NetworkStockTransactions(WebDriver driver) {
		this.driver = driver;
		
		//Page Initialization
		homePage = new NetworkAdminHomePage(driver);
		login = new Login();
		NetworkStockSubCategories = new NetworkStockSubCategories(driver);
		ViewStockTransactionsPage1 = new ViewStockTransactionsPage1(driver);
		networkPage = new SelectNetworkPage(driver);
	}
	
	public HashMap<String, String> prepareViewStockTransactionsPage1DAO() {
		
		HashMap<String, String> StockTransactionsDAO= new HashMap<String, String>();
		
		String Stock_Transaction_Locator_DAO = "2";
		final String Network_Locator_DAO = "3";
		final String Stock_Type_Locator_DAO = "4";
		final String Wallet_Type_Locator_DAO = "5";
		final String Stock_Transaction_Date_Locator_DAO = "6";
		final String Initiator_Locator_DAO = "7";
		final String Approved_By_Locator_DAO = "8";
		final String Approved_On_Locator_DAO = "9";
		final String Stock_MRP_Locator_DAO = "10";
		final String Status_Locator_DAO = "11";
		
		final String StockTransactionLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Stock_Transaction_Locator_DAO + "]";
		final String NetworkLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Network_Locator_DAO + "]";
		final String StockTypeLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Stock_Type_Locator_DAO + "]";
		final String WalletTypeLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Wallet_Type_Locator_DAO + "]";
		final String StockTransactionDateLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Stock_Transaction_Date_Locator_DAO + "]";
		final String InitatorLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Initiator_Locator_DAO + "]";
		final String ApprovedByLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Approved_By_Locator_DAO + "]";
		final String ApprovedOnLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Approved_On_Locator_DAO + "]";
		final String StockMRPLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Stock_MRP_Locator_DAO + "]";
		final String StatusLocator = "//form/table/tbody/tr/td/table/tbody/tr[2]/td[" + Status_Locator_DAO + "]";
		
		StockTransactionsDAO.put("Transaction Number", driver.findElement(By.xpath(StockTransactionLocator)).getText());
		StockTransactionsDAO.put("Network", driver.findElement(By.xpath(NetworkLocator)).getText());
		StockTransactionsDAO.put("Stock Type", driver.findElement(By.xpath(StockTypeLocator)).getText());
		StockTransactionsDAO.put("Wallet Type", driver.findElement(By.xpath(WalletTypeLocator)).getText());
		StockTransactionsDAO.put("Stock Transaction Date", driver.findElement(By.xpath(StockTransactionDateLocator)).getText());
		StockTransactionsDAO.put("Initator", driver.findElement(By.xpath(InitatorLocator)).getText());
		StockTransactionsDAO.put("Approved By", driver.findElement(By.xpath(ApprovedByLocator)).getText());
		StockTransactionsDAO.put("Approved On", driver.findElement(By.xpath(ApprovedOnLocator)).getText());
		StockTransactionsDAO.put("Stock MRP", driver.findElement(By.xpath(StockMRPLocator)).getText());
		StockTransactionsDAO.put("Status", driver.findElement(By.xpath(StatusLocator)).getText());
		
		return StockTransactionsDAO;
	}
	
	public void validateNetworkStockTransaction(String TransactionID, String stockType, String NetworkCode, String Wallet, String StockTransactionDate, String Initiator, int TotalStockInitiated, String Status) {
		
		HashMap<String, String> ViewStockTransactionPage1DAO = new HashMap<String, String>();
				
		//Operator User Access Implementation by Krishan.
		userAccessMap = UserAccess.getUserWithAccess(RolesI.VIEW_STOCK_TRANSACTIONS_ROLECODE); //Getting User with Access to View Stock Transactions
		login.LoginAsUser(driver, userAccessMap.get("LOGIN_ID"), userAccessMap.get("PASSWORD"));
		//User Access module ends.
		
		networkPage.selectNetwork();
		homePage.clickNetworkStock();
		NetworkStockSubCategories.clickViewStockTransactions();
		ViewStockTransactionsPage1.enterTransactionID(TransactionID);
		ViewStockTransactionsPage1.selectStockType(stockType);
		ViewStockTransactionsPage1.clickSubmitButton();
		
		//Enquiry Validator Begins
		ViewStockTransactionPage1DAO = prepareViewStockTransactionsPage1DAO();
		Assert.assertEquals(TransactionID, ViewStockTransactionPage1DAO.get("Transaction Number"));
		Log.info("TransactionID - Validator - Success");
		Assert.assertEquals(NetworkCode, ViewStockTransactionPage1DAO.get("Network").toString());
		Log.info("Network - Validator - Success");
		Assert.assertEquals(Wallet.toLowerCase(), ViewStockTransactionPage1DAO.get("Wallet Type").toString().toLowerCase());
		Log.info("Wallet - Validator - Success");
		Assert.assertEquals(Initiator, ViewStockTransactionPage1DAO.get("Initator").toString());
		Log.info("Initator - Validator - Success");
		Assert.assertEquals(""+TotalStockInitiated, ViewStockTransactionPage1DAO.get("Stock MRP").toString());
		Log.info("Stock MRP - Validator - Success");
	}
	
}
