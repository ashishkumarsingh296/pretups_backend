package com.pageobjects.channeluserspages.voucherOrderRequest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class voucherOrderRequestPage2 {

	@FindBy(xpath = "//input[@type='text' and @name='refrenceNum']")
	private WebElement referenceNumber;

	@FindBy(name = "slabsListIndexed[0].denomination")
	private WebElement denominationDropDown;

	@FindBy(xpath = "//input[@type='text' and @name='slabsListIndexed[0].quantity']")
	private WebElement quantityTextBox;

	@FindBy(xpath = "//textarea[@name='remarks']")
	private WebElement remarks;

	@FindBy(xpath = "//input[@name='submitOrderReqVoucherButton']")
	private WebElement submit;

	@FindBy(xpath = "//input[@name='resetButton']")
	private WebElement resetButton;

	@FindBy(xpath = "//input[@name='backButton']")
	private WebElement backButton;

	@FindBy(xpath = "//ul/li")
	private WebElement message;

	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	WebDriver driver = null;

	public voucherOrderRequestPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void SelectDenomination(String value) {
		Log.info("Trying to Select Denomination");
		Select select = new Select(denominationDropDown);
		select.selectByValue(value);
		Log.info("Denomination selected successfully as:" + value);
	}

	public void enterReference(String value) {
		Log.info("Trying to enter Reference");
		try {
			referenceNumber.sendKeys(value);
		} catch (Exception e) {
			Log.info("Reference Not Entered");
		}
		Log.info("Reference entred successfully as: " + value);
	}

	public void enterQuantity(String value) {
		Log.info("Trying to enter Quantity");
		try {
			quantityTextBox.sendKeys(value);
		} catch (Exception e) {
			Log.info("Quantity Not Entered");
		}
		Log.info("Quantity entred successfully as: " + value);
	}

	public void enterRemarks(String value) {
		Log.info("Trying to enter Remarks");
		try {
			remarks.sendKeys(value);
		} catch (Exception e) {
			Log.info("Remarks Not Entered");
		}
		Log.info("Remarks entred successfully as: " + value);
	}

	public void ClickonSubmit() {
		Log.info("Trying to click on Submit Button");
		submit.click();
		Log.info("Clicked on Submit Button successfully");
	}

	public void ClickonBAck() {
		Log.info("Trying to click on Back Button");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
	}

	public void ClickonReset() {
		Log.info("Trying to click on Reset Button");
		resetButton.click();
		Log.info("Clicked on Reset Button successfully");
	}

	public String getMessage() {
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
			Message = message.getText();
			Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}

	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
			Message = errorMessage.getText();
			Log.info("Error Message fetched successfully as:" + Message);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}

}
