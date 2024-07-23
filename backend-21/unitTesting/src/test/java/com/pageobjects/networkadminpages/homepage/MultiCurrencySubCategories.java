package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;



public class MultiCurrencySubCategories {
	
	WebDriver driver = null;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CCNVN001')]]")
	private WebElement addCurrency;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CCNVNA1001')]]")
	private WebElement multiCurrencyApproval1;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CCNVNA2001')]]")
	private WebElement multiCurrencyApproval2;
	
	public MultiCurrencySubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver,  this);
	}
	
	public void clickAddCurrency() {
		Log.info("Trying to click Add Currency");
		addCurrency.click();
		Log.info("User clicked Add Currecny");
	}
	
	public void clickMultiCurrencyApproval1() {
		Log.info("Trying to click Level One Currency Approval");
		multiCurrencyApproval1.click();
		Log.info("User clicked Level One Currency Approval");
	}
	
	public void clickMultiCurrencyApproval2() {
		Log.info("Trying to click Level two Currency Approval");
		multiCurrencyApproval2.click();
		Log.info("User clicked Level two Currency Approval");
	}
}
