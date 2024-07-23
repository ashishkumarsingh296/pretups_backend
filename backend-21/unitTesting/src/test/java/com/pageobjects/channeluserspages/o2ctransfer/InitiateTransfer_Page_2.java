package com.pageobjects.channeluserspages.o2ctransfer;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateTransfer_Page_2 {
	@ FindBy(name = "refrenceNum")
	private WebElement refrenceNumber;

	@ FindBy(name = "dataListIndexed[0].requestedQuantity")
	private WebElement quantity;
	
	@ FindBy(name = "remarks")
	private WebElement remarks;
	
	@ FindBy(name = "paymentInstCode")
	private WebElement paymentInstrumntType;

	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;
	
	@ FindBy(name = "paymentInstDate")
	private WebElement paymentInstDate;
		
	@ FindBy(id = "smsPin")
	private WebElement pin;

	@ FindBy(name = "submitButton")
	private WebElement submitButton;
	
	@ FindBy(name = "resetButton")
	private WebElement resetButton;
	
	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	WebDriver driver= null;
	
	public InitiateTransfer_Page_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterRefNumber(String RefNumber) {
		refrenceNumber.sendKeys(RefNumber);
		Log.info("User entered Ref number: "+RefNumber);
	}
	
	public void enterQuantity(String Quantity) {
		quantity.sendKeys(Quantity);
		Log.info("User entered Quantity: "+Quantity);
	}
	
	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks: "+Remarks);
	}
	
	public void selectPaymentInstrumntType(String PaymentInstrumntType) {
		Select select = new Select(paymentInstrumntType);
		select.selectByValue(PaymentInstrumntType);
		Log.info("User selected Payment Instrumnt Type: "+PaymentInstrumntType);
	}
	
	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered PaymentInstNum: "+PaymentInstNum);
	}
	
	public void enterPaymentInstDate(String PaymentInstDate) {
		paymentInstDate.sendKeys(PaymentInstDate);
		Log.info("User entered PaymentInstDate: "+PaymentInstDate);
	}
	
	public void enterPin(String Pin) {
		try {
		pin.sendKeys(Pin);
		Log.info("User entered Pin: "+Pin);
		}
		catch (NoSuchElementException e) {
			Log.writeStackTrace(e);
		}
	}
	
	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit Button");
	}
	
	public void clickResetButton() {
		resetButton.click();
		Log.info("User clicked Reset button");
	}
	
	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}
	
}
