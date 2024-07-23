package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateFOCTransferPage_3 {
	
	@ FindBy(name = "confirmButton")
	private WebElement confirmButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	

	WebDriver driver= null;
	
	public InitiateFOCTransferPage_3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		Log.info("Trying to click Confirm Button");
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
}
