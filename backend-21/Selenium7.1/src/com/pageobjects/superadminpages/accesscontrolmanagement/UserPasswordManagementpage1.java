package com.pageobjects.superadminpages.accesscontrolmanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserPasswordManagementpage1 {
	WebDriver driver;

	public UserPasswordManagementpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "loginID")
	private WebElement loginID;

	@FindBy(name = "msisdn")
	private WebElement msisdn;

	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	public void EnterLoginid(String value) {
		Log.info("Trying to enter  value in userName ");
		loginID.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EnterMobileno(String value) {
		Log.info("Trying to Enter data in mobile no field  ");
		msisdn.sendKeys(value);
		Log.info("Data Enter successfully");
	}

	public void ClickonSubmit() {
		Log.info("Trying to click on button Submit ");
		btnSubmit.click();
		Log.info("Clicked on Submit successfully");
	}

}