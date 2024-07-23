package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateVoucherO2CPage3 {
   
	WebDriver driver = null;
	public InitiateVoucherO2CPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "slabsListIndexed[0].productID" )
	private WebElement productID;

	@FindBy(name = "slabsListIndexed[0].fromSerialNo" )
	private WebElement fromSerialNo0;
	
	@FindBy(name = "slabsListIndexed[0].toSerialNo" )
	private WebElement toSerialNo0;
	
	@FindBy(name = "paymentInstCode" )
	private WebElement paymentInstCode;
	
	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;
	
	@FindBy(name = "paymentInstDate" )
	private WebElement paymentInstDate;
	
	@FindBy(name = "smsPin" )
	private WebElement smsPin;
	
	@FindBy(name = "submitO2CVoucherProdButton" )
	private WebElement submitO2CVoucherProdButton;
	
	@FindBy(name = "resetButton" )
	private WebElement resetButton;
	
	@FindBy(name = "backButton" )
	private WebElement backButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectProductID(String value){
		Log.info("Trying to Select ProductID");
		Select select = new Select(productID);
		select.selectByVisibleText(value);
		Log.info("ProductID selected  successfully");
		}
	
	public void EnterFromSerialNumber(String value){
		Log.info("Trying to enter From Serial Number");
		fromSerialNo0.sendKeys(value);
		Log.info("From Serial Number entered  successfully");
		}
	
	public void EnterToSerialNumber(String value){
		Log.info("Trying to enter To Serial Number");
		toSerialNo0.sendKeys(value);
		Log.info("To Serial Number entered  successfully");
		}
	
	public void SelectPaymentType(String value){
		Log.info("Trying to Select Payment Type");
		Select select = new Select(paymentInstCode);
		select.selectByValue(value);
		Log.info("Payment Type selected  successfully");
		}
	
	public void enterPaymentInstNum(String PaymentInstNum) {
		paymentInstNum.sendKeys(PaymentInstNum);
		Log.info("User entered PaymentInstNum: "+PaymentInstNum);
	}
	
	public void EnterPaymentDate(String value){
		Log.info("Trying to enter payment Date");
		paymentInstDate.sendKeys(value);
		Log.info("Payment Date entered  successfully");
		}
	
	
	public void EnterPin(String value){
		Log.info("Trying to enter smsPin");
		smsPin.sendKeys(value);
		Log.info("Pin entered  successfully");
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitO2CVoucherProdButton.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonReset(){
		Log.info("Trying to click on Reset Button");
		resetButton.click();
		Log.info("Clicked on Reset Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	
	public String getMessage(){
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
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}

}
