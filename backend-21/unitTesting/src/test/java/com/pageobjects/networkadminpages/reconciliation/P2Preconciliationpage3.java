package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2Preconciliationpage3 {
	
	WebDriver driver;

	public P2Preconciliationpage3(WebDriver driver) {
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
	
	@FindBy(xpath="//button[@id='alertify-ok']")
    private WebElement alertOK;
	
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
	
	public void clickOkAlertBtn() {
		try {
		Log.info("Trying to click OK Alert Button");
		alertOK.submit();
		Log.info("OK Alert Button clicked successfully");
		} catch(Exception e) {}
		}

}
