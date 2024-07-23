package com.pageobjects.networkadminpages.multicurency;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddCurrencyConfirmationPage {

	@FindBy(name = "addCurrencySubmit")
	private WebElement submit;
	
	@FindBy(name = "btnBack")
	private WebElement back;
	
	WebDriver driver= null;
	
	public AddCurrencyConfirmationPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmit() {
		Log.info("Trying to click submit button on Confirmation page");
		submit.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickBack() {
		Log.info("Trying to click Back button");
		submit.click();
		Log.info("Back button clicked successfully");
	}
	
}
