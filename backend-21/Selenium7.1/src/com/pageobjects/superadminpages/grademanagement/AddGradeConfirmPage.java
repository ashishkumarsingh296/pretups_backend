package com.pageobjects.superadminpages.grademanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddGradeConfirmPage {

	@FindBy(name = "confirmAdd")
	private WebElement confirmButton;

	@FindBy(name = "cancel")
	private WebElement cancelButton;

	@FindBy(name = "back")
	private WebElement backButton;

	WebDriver driver = null;

	public AddGradeConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void ClickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked Confirm button");
	}

	public void ClickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel button");
	}

	public void ClickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

}
