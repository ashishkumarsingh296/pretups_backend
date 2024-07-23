package com.pageobjects.channeluserspages.voucherOrderRequest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class voucherOrderRequestPage1 {

	@FindBy(xpath = "//input[@name='submitButton1']")
	private WebElement submit;

	@FindBy(xpath = "//ul/li")
	private WebElement message;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	@FindBy(name = "voucherType")
	private WebElement voucherType;

	WebDriver driver = null;

	public voucherOrderRequestPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void SelectVoucherType(String value) {
		Log.info("Trying to Select voucherType");
		Select select = new Select(voucherType);
		select.selectByValue(value);
		Log.info("Voucher Type selected successfully as:" + value);
	}

	public void ClickonSubmit() {
		Log.info("Trying to click on Submit Button");
		submit.click();
		Log.info("Clicked on Submit Button successfully");
	}

}
