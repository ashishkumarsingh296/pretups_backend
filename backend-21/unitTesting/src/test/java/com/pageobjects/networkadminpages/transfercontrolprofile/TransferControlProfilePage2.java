package com.pageobjects.networkadminpages.transfercontrolprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class TransferControlProfilePage2 {

	@FindBy(name = "confirm")
	private WebElement confirmButton;

	@FindBy(name = "cancel")
	private WebElement cancelButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public TransferControlProfilePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked Confirm Button.");
	}

	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
}
