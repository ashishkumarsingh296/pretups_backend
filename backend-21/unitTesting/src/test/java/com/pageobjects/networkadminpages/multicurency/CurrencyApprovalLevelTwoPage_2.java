package com.pageobjects.networkadminpages.multicurency;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class CurrencyApprovalLevelTwoPage_2 {
WebDriver driver= null;
	
	@FindBy(name = "approval2")
	private WebElement submit;

	@FindBy(name = "btnBack2")
	private WebElement back;
	
	@FindBy(name = "conversion")
	private WebElement conversionRate;
	

	
	public CurrencyApprovalLevelTwoPage_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterConversion(String ConversionRate) {
		Log.info("Trying to enter Conversion Rate");
		conversionRate.clear();
		conversionRate.sendKeys(ConversionRate);
		Log.info("Conversion Rate entered as " + ConversionRate);
	}
	
	public void clickSubmit() {
		Log.info("Trying to click submit button");
		submit.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickBack() {
		Log.info("Trying to click back button");
		back.click();
		Log.info("Back button clicked successfully");
	}
	
	public void acceptAlert() {
		Log.info("Trying to accept alert");
		Alert alert = driver.switchTo().alert();
		alert.accept();
		Log.info("Alert accepted successfully");
	}
}
