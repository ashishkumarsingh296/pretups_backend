package com.pageobjects.networkadminpages.networkstock;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

/**
 * @author krishan.chawla
 * This class Contains the Page Objects for Initiate Network Stock
 **/

public class InitiateStockDeductionPage_1 {
	
	WebDriver driver= null;
	
	//Wallet Type Selector
	@FindBy(name="walletType")
	private WebElement walletType;
	
	//Submit Button
	@FindBy(name = "walletSubmit")
	private WebElement SubmitBtn;
	
	public InitiateStockDeductionPage_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectWalletType(String wallet) {
		Log.info("Trying to select Wallet Type");
		Select walletTypeSelector = new Select(walletType);
		walletType.click();
		walletTypeSelector.selectByValue(wallet);
		Log.info("Wallet type selected as: "+wallet);
	}
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		SubmitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
}
