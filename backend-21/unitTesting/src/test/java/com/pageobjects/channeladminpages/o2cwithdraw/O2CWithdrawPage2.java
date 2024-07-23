package com.pageobjects.channeladminpages.o2cwithdraw;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class O2CWithdrawPage2 {
	@ FindBy(name = "walletType")
	private WebElement walletType;

	@ FindBy(name = "walletSubmit")
	private WebElement submitButton;
	
	@ FindBy(name = "backToWallet")
	private WebElement backButton;

	WebDriver driver= null;

	public O2CWithdrawPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectWalletType(String index) {
		Select select = new Select(walletType);
		select.selectByValue(index);
		Log.info("User selected wallet Type.");
	}
	
	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit Button.");
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}

}
