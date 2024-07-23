package com.pageobjects.channeluserspages.c2cwithdraw;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CWithdrawProductPageSpring {

	@FindBy(name = "productList[0].requestedQuantity")
	private WebElement quantityslab0;
	
	@FindBy(name = "productList[1].requestedQuantity")
	private WebElement quantityslab1;

	@FindBy(name = "remarks")
	private WebElement remarks;

	@FindBy(name = "smsPin")
	private WebElement smsPin;

	@FindBy(id = "submitWithdrawReturn")
	public WebElement submitButton;

	@FindBy(id = "WithdrawResetSecond")
	public WebElement reset;

	@FindBy(name = "withdrawBackSecond")
	private WebElement backButton;
	
	@FindBy(xpath="//span[@class='errorClass']")
	private WebElement SuccessMessage;
	
	/*@FindBy(xpath="//[@class='error']")
	private WebElement fieldError;*/
	
	@FindBy(xpath="//label[@for='smsPin']")
	private WebElement fieldErrorSmsPin;
	
	@FindBy(xpath="//button[@id='alertify-ok']")
	private WebElement alertifyQuantity;

	WebDriver driver = null;

	public C2CWithdrawProductPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void enterQuantity0(String Quantityslab0) {
		Log.info("Trying to enter Quantity 1"+Quantityslab0);
		quantityslab0.sendKeys(Quantityslab0);
		Log.info("User entered Quantityslab0");
	}

	public void enterQuantity1(String Quantityslab1) {
		Log.info("Trying to enter Quantity 2"+Quantityslab1);
		quantityslab1.sendKeys(Quantityslab1);
		Log.info("User entered Quantityslab1");
	}

	public void enterRemarks(String Remarks) {
		Log.info("Trying to enter Remark"+Remarks);
		remarks.sendKeys(Remarks);
		Log.info("User entered Remarks");
	}

	public void enterSmsPin(String SmsPin) {
		Log.info("Trying to enter Sender's smsPin"+SmsPin);
		smsPin.sendKeys(SmsPin);
		Log.info("User entered Sender's smsPin");
	}

	public void clickSubmit() {
		Log.info("Trying to click on Submit button");
		submitButton.click();
		Log.info("User clicked submit");
	}

	public void clickReset() {
		Log.info("Trying to click on Reset button");
		reset.click();
		Log.info("User clicked Reset");
	}

	public void clickBackButton() {
		Log.info("Trying to click on Back button");
		backButton.click();
		Log.info("User clicked Back Button");
	}

	public void enterQuantity(String Quantityslab) {
		Log.info("Trying to enter quantity. "+Quantityslab);
		quantityslab0.sendKeys(Quantityslab);
		Log.info("Quantity entered successfully");
	}
	
	
	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = SuccessMessage.getText().trim();
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
		String message=SuccessMessage.getText().trim();
		Log.info("Message fetched successfuly.");
		return message;
	}
	
	/*public String getFieldError() {
		Log.info("Trying to get Field Error");
		String message=fieldError.getText().trim();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}*/
	
	public void enterQuantityforC2C(){
		Log.info("Enter Quantity.");
		List<WebElement> Qty=driver.findElements(By.xpath("//input[@name[contains(.,'requestedQuantity')]]"));
		for(int countQty=0; countQty < Qty.size(); countQty++){
			WebElement qtyIndex=driver.findElement(By.xpath("//input[@name='productList["+countQty+"].requestedQuantity']"));
			WebElement balance=driver.findElement(By.xpath("//input[@name='productList["+countQty+"].balanceAsString']/../span"));
			String productBalance=balance.getText();
			int prBalance= (int) Double.parseDouble(productBalance);
			int quantity=(int) (prBalance*0.2);
			qtyIndex.sendKeys(String.valueOf(quantity));
		}
		Log.info("Enetered quantity successfuly.");
	}
	
	public String getFieldErrorSmsPin() {
		Log.info("Trying to get Field Error for SmsPin");
		String message=fieldErrorSmsPin.getText().trim();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public boolean isAlertifyQuantity() {
		Log.info("Trying to get Field Error for SmsPin");
		boolean message=alertifyQuantity.isDisplayed();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	
}
