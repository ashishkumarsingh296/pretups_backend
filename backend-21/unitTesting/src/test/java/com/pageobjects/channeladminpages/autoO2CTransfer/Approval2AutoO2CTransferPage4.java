package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class Approval2AutoO2CTransferPage4 {
	
	@ FindBy(name = "approve")
	private WebElement confirmButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
WebDriver driver= null;
	
	public Approval2AutoO2CTransferPage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickApproveButton() {
		confirmButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

}
