package com.pageobjects.channeluserspages.voucherOrderRequest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class voucherOrderRequestPage4 {

	
	@FindBy(name = "confirmVoucherOrderReqProdButton")
	private WebElement confirm;

	@FindBy(xpath = "//ul/li")
	private WebElement message;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	WebDriver driver = null;

	public voucherOrderRequestPage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void ClickonConfirm() {
		Log.info("Trying to click on Confirm Button");
		confirm.click();
		Log.info("Clicked on COnfirm Button successfully");
	}
	
}
