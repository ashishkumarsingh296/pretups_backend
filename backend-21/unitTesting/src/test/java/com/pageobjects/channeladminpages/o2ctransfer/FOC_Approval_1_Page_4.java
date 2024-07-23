package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_1_Page_4 {
	
	@ FindBy(name = "confirm")
	private WebElement confirmButton;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver= null;

	public FOC_Approval_1_Page_4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		Log.info("Trying to click Confirm Button");
		confirmButton.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	public void clickCancelButton() {
		Log.info("Trying to click Cancel Button");
		cancelButton.click();
		Log.info("Cancel Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
}
