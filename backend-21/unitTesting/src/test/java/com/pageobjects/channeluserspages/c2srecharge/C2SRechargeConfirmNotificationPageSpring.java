package com.pageobjects.channeluserspages.c2srecharge;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class C2SRechargeConfirmNotificationPageSpring {

	@FindBy(xpath = "//a[@href='notify.form']")
	// @FindBy(linkText = "Click here for final notification message.")
	private WebElement notificationMsgLink;

	@FindBy(xpath = "//a[@href='process-backc2srecharge.form']")
	private WebElement backButton;

	@FindBy(xpath = "//td[text()[contains(.,'status')]]/following-sibling::td")
	private WebElement transferStatus;
	
	@FindBy(xpath = "//td[contains(text(),'Transfer id')]/following-sibling::td")
	private WebElement transferID;
	
	@FindBy(xpath = "//td[contains(text(),'Sender mobile number')]/following-sibling::td")
	private WebElement senderMSISDN;
	
	@FindBy(xpath = "//td[contains(text(),'Receiver mobile number')]/following-sibling::td")
	private WebElement receiverMSISDN;
	
	@FindBy(xpath = "//td[contains(text(),'Transfer value')]/following-sibling::td")
	private WebElement amount;
	
	@FindBy(xpath = "//td[contains(text(),'Sender post balance')]/following-sibling::td")
	private WebElement balance;
	
	@FindBy(xpath="//*[@class='errorClass']")
	private WebElement SuccessMessage;
	
	WebDriver driver = null;

	public C2SRechargeConfirmNotificationPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickNotificationMsgLink() {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		wait.until(ExpectedConditions.elementToBeClickable(
				By.xpath("//a[@href='notify.form']")));
		
		notificationMsgLink.click();
		Log.info("User clicked Notification Msg link to display Success Message");
	}

	public boolean notificationMsgLinkVisibility() {
		boolean result = false;
		try {
			if (notificationMsgLink.isDisplayed()) {
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;

	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back button");
	}

	public String transferStatus(){
		Log.info("Trying to get transfer Status.");
		String trfStatus = transferStatus.getText();
		Log.info("Transfer status fetched as : "+trfStatus);
		return trfStatus;
	}
	
	public String getTransferID(){
		Log.info("Trying to get transfer ID.");
		String transferIDValue = transferID.getText();
		Log.info("Transfer ID fetched as : "+transferIDValue);
		return transferIDValue;
	}
	
	public String getSenderMSISDN(){
		Log.info("Trying to get Sender MSISDN.");
		String senderMSISDNValue = senderMSISDN.getText();
		Log.info("Sender MSISDN fetched as : "+senderMSISDNValue);
		return senderMSISDNValue;
	}
	
	public String getReceiverMSISDN(){
		Log.info("Trying to get Receiver MSISDN.");
		String receiverMSISDNValue = receiverMSISDN.getText();
		Log.info("Receiver MSISDN fetched as : "+receiverMSISDNValue);
		return receiverMSISDNValue;
	}
	
	public String getTransferAmount(){
		Log.info("Trying to get Transfer Amount");
		String transferAmountValue = amount.getText();
		Log.info("Transfer Amount fetched as : "+transferAmountValue);
		return transferAmountValue;
	}
	
	public String getBalance(){
		Log.info("Trying to get Balance");
		String balanceValue = balance.getText();
		Log.info("Balance fetched as : "+balanceValue);
		return balanceValue;
	}
	
	public String getSuccessMessage() {
		Log.info("Trying to get Message on GUI.");
		String message=SuccessMessage.getText();
		Log.info("Message fetched successfuly.");
		return message;
	}
	
}
