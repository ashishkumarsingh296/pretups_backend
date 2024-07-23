package com.pageobjects.channeluserspages.c2cwithdraw;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CWDetailsPage {

	@FindBy(name = "refrenceNum")
	private WebElement refrenceNum;

	@FindBy(name = "dataListIndexed[0].requestedQuantity")
	private WebElement quantityslab0;
	
	@FindBy(name = "dataListIndexed[1].requestedQuantity")
	private WebElement quantityslab1;

	@FindBy(name = "remarks")
	private WebElement remarks;

	@FindBy(name = "smsPin")
	private WebElement smsPin;

	@FindBy(name = "submitButton")
	public WebElement submitButton;

	@FindBy(name = "resetButton")
	public WebElement reset;

	@FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(xpath="//ul/li")
	private WebElement SuccessMessage;

	WebDriver driver = null;

	public C2CWDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterRefNum(String RefNum) {
		refrenceNum.sendKeys(RefNum);
		Log.info("User entered Reference Number");
	}

	public void enterQuantity0(String Quantityslab0) {
		quantityslab0.sendKeys(Quantityslab0);
		Log.info("User entered Quantityslab0");
	}

	public void enterQuantity1(String Quantityslab1) {
		quantityslab1.sendKeys(Quantityslab1);
		Log.info("User entered Quantityslab1");
	}

	public void enterRemarks(String Remarks) {
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks");
	}

	public void enterSmsPin(String SmsPin) {
		smsPin.sendKeys(SmsPin);
		Log.info("User entered Sender's smsPin");
	}

	public void clickSubmit() {
		submitButton.click();
		Log.info("User clicked submit");
	}

	public void clickReset() {
		reset.click();
		Log.info("User clicked Reset");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button");
	}

	public void enterQuantity(String Quantityslab) {
		Log.info("Trying to enter quantity.");
		quantityslab0.sendKeys(Quantityslab);
		Log.info("Quantity entered successfully");
	}
	
	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = SuccessMessage.getText();
		Log.info("Initiate Message is: "+TransactionMessage[0]);
		TransactionMessage[1] = TransactionMessage[0].substring(TransactionMessage[0].lastIndexOf("CW"),TransactionMessage[0].length()).replaceAll("[.]$","");
		Log.info("Transaction ID Extracted as : "+TransactionMessage[1]);
		}
		catch (NoSuchElementException e)
		{ Log.writeStackTrace(e); }
		catch (Exception e)
		{ Log.writeStackTrace(e); }
		return TransactionMessage[1];
	}
	
	public String getMessage() {
		Log.info("Trying to get Message on GUI.");
		String message=SuccessMessage.getText();
		Log.info("Message fetched successfuly.");
		return message;
	}
	
}
