package com.pageobjects.networkadminpages.accesscontrolmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserpasswordMgmtpage1 {
	WebDriver driver;

	public UserpasswordMgmtpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "loginID")
	private WebElement loginID;

	@FindBy(name = "msisdn")
	private WebElement msisdn;

	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	@FindBy(name = "remarks")
	private WebElement remarks;

	public void EnterloginID(String value) {
		Log.info("Trying to enter  value in loginID ");
		loginID.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void Entermsisdn(String value) {
		Log.info("Trying to enter  value in msisdn ");
		msisdn.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void Enterremarks(String value) {
		Log.info("Trying to enter  value in remarks ");
		remarks.sendKeys(value);
		Log.info("Data entered  successfully");
	}

}
