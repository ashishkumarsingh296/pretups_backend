package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2Creconciliationpage4 {

	WebDriver driver;

	public O2Creconciliationpage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath = "//input[@value='Confirm']")
	private WebElement btnSuccess;

	@FindBy(name = "cancel")
	private WebElement btnFail;
	
	@FindBy(name = "backButton")
	private WebElement btnBack;
	
	public void ClickOnbtnConfirm() {
		Log.info("Trying to click on button Confirm ");
		btnSuccess.click();
		Log.info("Clicked on Confirm successfully");
	}

	public void ClickOnbtnCancel() {
		Log.info("Trying to click on button Cancel ");
		btnFail.click();
		Log.info("Clicked on Cancel Button successfully");
	}
	
	public void ClickOnbtnBack() {
		Log.info("Trying to click on button Back ");
		btnBack.click();
		Log.info("Clicked on Back Button successfully");
	}
}
