package com.pageobjects.channeluserspages.voucherOrderRequest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class voucherOrderRequestPage3 {

	@FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;

	@FindBy(name = "paymentInstDate")
	private WebElement paymentInstrumentDate;

	@FindBy(name = "smsPin")
	private WebElement pin;

	@FindBy(name = "paymentInstCode")
	private WebElement paymentInstType;

	@FindBy(name = "paymentGatewayType")
	private WebElement paymentGatewayType;

	@FindBy(name = "slabsListIndexed[0].quantity")
	private WebElement voucherQuantity;

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

	public voucherOrderRequestPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterVoucherQuanity(String quantity) {
		voucherQuantity.clear();
		voucherQuantity.sendKeys(quantity);
		Log.info("User entered Quantity: " + quantity);
	}

	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered Payment Inst Num: " + PaymentInstNum);
	}

	public void enterPin(String value) {
		pin.sendKeys(value);
		Log.info("Pin entered: " + value);
	}
	
	public void SelectPaymentInstType(String value) {
		Log.info("Trying to Payment");
		Select select = new Select(paymentInstType);
		select.selectByValue(value);
		Log.info("Payment selected successfully as:" + value);
	}
	
	public void SelectPaymentGatewayType(String value) {
		Log.info("Trying to Payment Gateway");
		Select select = new Select(paymentGatewayType);
		select.selectByValue(value);
		Log.info("Payment Gateway selected successfully as:" + value);
	}


	public void enterPaymentInstrumentDate(String PaymentInstrumentDate) {
		paymentInstrumentDate.sendKeys(PaymentInstrumentDate);
		Log.info("User entered Payment Instrument Date: " + PaymentInstrumentDate);
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
