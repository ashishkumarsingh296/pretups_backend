package com.pageobjects.channeladminpages.channelenquiries;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTransfers {

	WebDriver driver = null;
	
	@FindBy(name = "transferNum")
	private WebElement TransferNumber;
	
	@FindBy(name = "submitButton")
	private WebElement SubmitButton;
	
	public O2CTransfers(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterTransferNumber(String TransferNum) {
		Log.info("Trying to enter Transfer Number");
		TransferNumber.sendKeys(TransferNum);
		Log.info("Transfer Number entered successfully as: " + TransferNum);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		SubmitButton.click();
		Log.info("Submit Button clicked successfully");
	}
}
