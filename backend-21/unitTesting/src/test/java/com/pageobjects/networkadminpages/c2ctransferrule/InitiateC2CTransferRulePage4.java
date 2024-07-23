package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateC2CTransferRulePage4 {
	
	@FindBy(name = "btnAddCnf")
	WebElement confirmButton;
	
	@FindBy(name = "btnCncl")
	WebElement cancelButton;
	
	@FindBy(name = "btnAddBack")
	WebElement backButton;
	
	WebDriver driver= null;

	public InitiateC2CTransferRulePage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmButton() {
		Log.info("Trying to click on Confirm Button");
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void clickCancelButton() {
		Log.info("Trying to click on Back Button");
		cancelButton.click();
		Log.info("Back Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
	
}
