package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateO2CTransferPage6 {
	
	@ FindBy(name = "confirmO2CVoucherProdButton")
	private WebElement  confirmO2CVoucherProdButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	

	WebDriver driver= null;
	
	public InitiateO2CTransferPage6(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		confirmO2CVoucherProdButton.click();
		Log.info("User clicked Confirm button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}
}
