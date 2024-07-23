package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateAutoO2CTransferPage3 {
	
	@ FindBy(name = "submitButton")
	private WebElement confirmButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	

	WebDriver driver= null;
	
	public InitiateAutoO2CTransferPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked Confirm button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

}
