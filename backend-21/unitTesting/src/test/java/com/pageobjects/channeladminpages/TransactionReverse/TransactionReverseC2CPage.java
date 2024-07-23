package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class TransactionReverseC2CPage {

	
	@FindBy(name = "msisdn")
	private WebElement RecieverMobileNumber;
	
	@FindBy (name = "transferNum")
	private WebElement TransferNum;

	@FindBy(name = "userCode")
	private WebElement SenderMobileNumber;
	
	@FindBy(name = "userLoginID")
	private WebElement SenderLoginID;

	@FindBy (name = "domainCode")
	private WebElement Domain;

	@FindBy (name = "categoryCode")
	private WebElement toCategory;

	@FindBy (name = "toUserName")
	private WebElement toUserName;

	@FindBy(name = "submitButton")
	private WebElement submitButton;


	@FindBy(name = "resetbutton")
	private WebElement resetButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	
	WebDriver driver = null;

	public TransactionReverseC2CPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void EnterTransferNum(String transferId){
		TransferNum.sendKeys(transferId);
		
		Log.info("User entered transfer number as :" +transferId);
		
		
	}
	
	public void EnterSenderUserName(String senderName){
		toUserName.sendKeys(senderName);
		
		Log.info("User entered Sender Login ID as :" +senderName);
		
		
	}
	
	
	
	public void EnterSenderLoginID(String senderLoginId){
		SenderLoginID.sendKeys(senderLoginId);
		
		Log.info("User entered Sender user Name as :" +senderLoginId);
		
		
	}
	public void EnterMobileNumber(String recMobileNum){
		RecieverMobileNumber.sendKeys(recMobileNum);
		
		Log.info("User entered Reciever Mobile number as :" +recMobileNum);
		
		
	}
	
	public void EnterSenderMobileNumber(String SenderMobileNum){
		SenderMobileNumber.sendKeys(SenderMobileNum);
		
		Log.info("User entered Sender Mobile number as :" +SenderMobileNum);
		
		
	}
	
/*	
	public void selectTransferCategory(String TransferCategory) {
		Log.info("Trying to select selectTransferCategory");
		try {
		Select select = new Select(this.transferCategory);
		select.selectByValue(TransferCategory);
		Log.info("TransferCategory selected successfully as: " + TransferCategory);
		}
		catch (Exception e) {
			Log.info("TransferCategory Dropdown not found");
		}
	}
	*/
	

	
	public void selectDomain(String Domain1) {
		Select select = new Select(Domain);
		select.selectByVisibleText(Domain1);
		Log.info("User selected Domain." +Domain1);
	}
	public void selectCategory(String Category) {
		Select select = new Select(toCategory);
		select.selectByVisibleText(Category);
		Log.info("User selected Category." +Category);
	}
	
		
	
	
	public void clickSubmit(){
		Log.info("User is trying to click submit button");
		submitButton.click();
		Log.info("User click submit button");
	}
	
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message is:" +msg);
		
		return msg;
		
	}
	
	
	
	

}


