package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_3_Page_4 {
	
	@ FindBy(name = "confirm")
	private WebElement confirmButton;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver= null;

	public FOC_Approval_3_Page_4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked confirm button.");
	}
	
	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked cancel button.");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked back button.");
	}
}
