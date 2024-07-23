package com.pageobjects.networkadminpages.multicurency;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;



public class AddCurrencyPage {
	@FindBy(name = "sourceCurrencyCode")
	private WebElement currencyCode;
	
	@FindBy(name = "sourceCurrencyName")
	private WebElement currencyName;
	
	@FindBy(name = "conversion")
	private WebElement conversionRate;
	
	@FindBy(name = "description")
	private WebElement description;
	
	@FindBy(name = "addCurrencySubmit")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;
	
	public AddCurrencyPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectCurrencyCode(String currency) {
		Log.info("Trying to select Currency Code");
		Select select = new Select(currencyCode);
		select.selectByVisibleText(currency);
		Log.info("Currency Code selected as " + currency);
	}
	
	public void selectCurrencyCode() {
		Log.info("Trying to select Currency Code");
		Select select = new Select(currencyCode);
		select.selectByIndex(1);
		Log.info("Currency Code selected");
	}
	
	public void enterCurrencyName(String currency) {
		Log.info("Trying to enter Currency Name");
		currencyName.sendKeys(currency);
		Log.info("Currency Name entered as " + currency);
	}
	
	public void enterConversion(String ConversionRate) {
		Log.info("Trying to enter Conversion Rate");
		conversionRate.clear();
		conversionRate.sendKeys(ConversionRate);
		Log.info("Conversion Rate entered as " + ConversionRate);
	}
	
	public void enterDescription(String Description) {
		Log.info("Trying to enter description");
		description.sendKeys(Description);
		Log.info("Description entered as " + Description);
	}
	
	public void clickSubmit() {
		Log.info("Trying to click submit button");
		submit.click();
		Log.info("Submit button clicked successfully");
	}
	
	public String getSuccessMessage() {
		Log.info("Trying to fetch success message");
		String message1 = message.getText();
		Log.info("Success Message Returend: " + message1);
		return message1;
	}
	
	public String getErrorMessage() {
		Log.info("Trying to fetch error message");
		String message1 = errorMessage.getText();
		Log.info("Error Message Returend: " + message1);
		return message1;
	}
	
}
