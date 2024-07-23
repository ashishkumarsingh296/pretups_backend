package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2Sreconciliationpage3 {
	
	WebDriver driver;

	public C2Sreconciliationpage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "btnSuccess")
	private WebElement btnSuccess;

	@FindBy(name = "btnFail")
	private WebElement btnFail;
	
	@FindBy(name = "btnCncl")
	private WebElement btnCncl;
	
	@FindBy(name = "btnBack")
	private WebElement btnBack;
	
	@FindBy(xpath = "//ul/li")
	private WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button Success ");
		btnSuccess.click();
		Log.info("Clicked on Success successfully");
	}


	public void ClickOnbtnFail() {
		Log.info("Trying to click on button  Fail ");
		btnFail.click();
		Log.info("Clicked on  Fail Button successfully");
	}
	
	public void ClickOnbtnCncl() {
		Log.info("Trying to click on button  Cancel ");
		btnCncl.click();
		Log.info("Clicked on  Cancel Button successfully");
	}
	
	public void ClickOnbtnBack() {
		Log.info("Trying to click on button Back ");
		btnBack.click();
		Log.info("Clicked on Back Button successfully");
	}
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try {
			errorMsg = errorMessage.getText();
		} catch (Exception e) {
			Log.info("No error Message found.");
		}
		try {
			UIMsg = UIMessage.getText();
		} catch (Exception e) {
			Log.info("No Success Message found.");
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}

}
