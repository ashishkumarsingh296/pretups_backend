package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NetworkStockSubCategories {
	
	WebDriver driver = null;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=LVL1APV001')]]")
	private WebElement networkStockApproval1;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=LVL2APV001')]]")
	private WebElement networkStockApproval2;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VSTTXN001')]]")
	private WebElement ViewStockTransactions;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VCURRST001')]]")
	private WebElement ViewCurrentStock;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=INSTKREV01')]]")
	private WebElement networkStockDeduction;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=L1APSTDU01')]]")
	private WebElement networkStockDeductionLevel1;
	
	public NetworkStockSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickNetworkStockApproval1() {
		Log.info("Trying to click Network Stock Approval Level 1");
		WebDriverWait wait =new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(networkStockApproval1));
		networkStockApproval1.click();
		Log.info("User clicked Network Stock Approval Level 1");
	}
	
	public void clickNetworkStockApproval2() {
		Log.info("Trying to click Network Stock Approval Level 2");
		networkStockApproval2.click();
		Log.info(("User clicked Network Stock Approval Level 2"));
	}
	
	public void clickViewStockTransactions() {
		Log.info("Trying to click View Stock Transactions");
		ViewStockTransactions.click();
		Log.info("View Stock Transactions link clicked successfully");	
	}
	
	public void clickViewCurrentStock() {
		Log.info("Trying to click View Current Stock Link");
		ViewCurrentStock.click();
		Log.info("View Current Stock Link clicked successfully");
	}
	
	public void clickNetworkStockDeduction() {
		Log.info("Trying to click Network Stock Deduction Link");
		networkStockDeduction.click();
		Log.info("Network Stock Deduction link clicked successfully");
	}
	
	public void clickNetworkStockDeductionApproval() {
		Log.info("Trying to click Network Stock Deduction Approval Link");
		networkStockDeductionLevel1.click();
		Log.info("Network Stock Deduction Approval Link clicked successfully");
	}
}
