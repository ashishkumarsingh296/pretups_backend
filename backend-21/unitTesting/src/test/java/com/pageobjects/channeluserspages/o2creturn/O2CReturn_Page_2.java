package com.pageobjects.channeluserspages.o2creturn;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CReturn_Page_2 {

	@FindBy(name = "returnedProductListIndexed[0].requestedQuantity")
	private WebElement quantity;
	
	@FindBy(name="remarks")
	private WebElement Remarks;
	
	@FindBy(name = "smsPin")
	private WebElement pin;
	
	@FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement successMessage;
	
	WebDriver driver;
	
	public O2CReturn_Page_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterQuantity(String Quantity) {
		Log.info("Trying to enter O2C Return Quantity");
		quantity.sendKeys(Quantity);
		Log.info("O2C Return Quantity Entered successfully");
	}
	
	public void enterRemarks(String O2CReturnRemarks) {
		Log.info("Trying to enter O2C Return Remarks");
		Remarks.sendKeys(O2CReturnRemarks);
		Log.info("O2C Return Remarks entered successfully");
		}
	
	public void enterPIN(String PIN) {
		Log.info("Trying to enter Channel User PIN");
		pin.sendKeys(PIN);
		Log.info("PIN Entered successfully as: " + PIN);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click submit button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
}
