package com.pageobjects.channeluserspages.c2ctransfer;

import java.util.NoSuchElementException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.pretupsControllers.BTSLUtil;
import com.utils.Log;

public class C2CDetailsPage {

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
	
	@FindBy(name = "submitC2CVoucherButton")
	public WebElement submitVocuherButton;

	@FindBy(name = "resetButton")
	public WebElement reset;

	@FindBy(name = "backButton")
	private WebElement backButton;
	
	@FindBy(xpath="//ul/li")
	private WebElement SuccessMessage;
	
	@ FindBy(name = "paymentInstCode")
	private WebElement paymentInstrumntType;

	@ FindBy(name = "paymentInstNum")
	private WebElement paymentInstNum;
	
	@ FindBy(name = "paymentInstDate")
	private WebElement paymentInstDate;
	
	WebDriver driver = null;

	public C2CDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterRefNum(String RefNum) {
		try {
		refrenceNum.sendKeys(RefNum);
		Log.info("User entered Reference Number");
		}
		catch (Exception e) {
			Log.info("Reference number field not found.");
		}
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
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(remarks));
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks");
	}

	public void enterSmsPin(String SmsPin) {
		smsPin.sendKeys(SmsPin);
		Log.info("User entered Sender's smsPin");
	}
	
	
	public boolean checkSMSPINEmpty() {
		boolean flag = false;
		try {
		flag =smsPin.isDisplayed();
		return flag;
		}
		catch(org.openqa.selenium.NoSuchElementException e) {
			return flag;
		}
	}

	public void clickSubmit() {
		submitButton.click();
		Log.info("User clicked submit");
	}
	
	public void clickVocuherSubmit() {
		submitVocuherButton.click();
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
	
	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = SuccessMessage.getText();
		Log.info("Initiate Message is: "+TransactionMessage[0]);
		TransactionMessage[1] = TransactionMessage[0].substring(TransactionMessage[0].lastIndexOf("CT"),TransactionMessage[0].length()).replaceAll("[.]$","");
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
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(SuccessMessage));
		String message=SuccessMessage.getText();
		Log.info("MESSAGE :: "+message) ;
		Log.info("Message fetched successfuly.");
		return message;
	}
	
}
