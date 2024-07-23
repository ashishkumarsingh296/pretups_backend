package com.pageobjects.superadminpages.categorytransfercontrolprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class CategoryTrfControlProfilePage4 {

	@FindBy(name = "confirm")
	private WebElement confirmButton;

	@FindBy(xpath = "cancel")
	private WebElement cancelButton;

	@FindBy(xpath = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public CategoryTrfControlProfilePage4(WebDriver driver) {
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
