package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2Creconciliationpage3 {

	WebDriver driver;

	public O2Creconciliationpage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "approve")
	private WebElement btnSuccess;

	@FindBy(name = "reject")
	private WebElement btnFail;
	
	@FindBy(name = "backButton")
	private WebElement btnBack;
	
	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button Success ");
		btnSuccess.click();
		Log.info("Clicked on Success successfully");
	}

	public void ClickOnbtnFail() {
		Log.info("Trying to click on button  Fail ");
		btnFail.click();
		Log.info("Clicked on Fail Button successfully");
	}
	
	
	public void ClickOnbtnBack() {
		Log.info("Trying to click on button Back ");
		btnBack.click();
		Log.info("Clicked on Back Button successfully");
	}

}
