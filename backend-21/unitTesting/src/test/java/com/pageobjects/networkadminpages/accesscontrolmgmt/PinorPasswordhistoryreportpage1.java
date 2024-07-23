package com.pageobjects.networkadminpages.accesscontrolmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class PinorPasswordhistoryreportpage1 {
	WebDriver driver;

	public PinorPasswordhistoryreportpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "fromDate")
	private WebElement fromDate;

	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "userTypeCode")
	private WebElement userTypeCode;

	@FindBy(name = "pinPwdCode")
	private WebElement pinPwdCode;

	@FindBy(name = "domainCode")
	private WebElement domainCode;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "submitButton")
	private WebElement submitButton;

	public void EnterfromDate(String value) {
		Log.info("Trying to enter  value in fromDate ");
		fromDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void EntertoDate(String value) {
		Log.info("Trying to enter  value in toDate ");
		toDate.sendKeys(value);
		Log.info("Data entered  successfully");
	}

	public void SelectuserTypeCode(String value) {
		Log.info("Trying to Select   userTypeCode ");
		Select select = new Select(userTypeCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectpinPwdCode(String value) {
		Log.info("Trying to Select   pinPwdCode ");
		Select select = new Select(pinPwdCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectdomainCode(String value) {
		Log.info("Trying to Select   domainCode ");
		Select select = new Select(domainCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void SelectcategoryCode(String value) {
		Log.info("Trying to Select   categoryCode ");
		Select select = new Select(categoryCode);
		select.selectByVisibleText(value);
		Log.info("Data selected  successfully");
	}

	public void ClickOnsubmitButton() {
		Log.info("Trying to click on button  Submit ");
		submitButton.click();
		Log.info("Clicked on  Submit successfully");
	}

}
