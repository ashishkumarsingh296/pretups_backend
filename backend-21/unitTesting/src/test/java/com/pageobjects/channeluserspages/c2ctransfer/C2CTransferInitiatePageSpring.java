package com.pageobjects.channeluserspages.c2ctransfer;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class C2CTransferInitiatePageSpring {

	@FindBy(id = "msisdn")
	public WebElement mobileNumber;

	@FindBy(id = "initiatetransfer1")
	public WebElement initiatetransferByMobileNo;

	@FindBy(name = "toCategoryCode")
	public WebElement categoryCode;

	@FindBy(id = "user")
	public WebElement toUserName;

	@FindBy(id = "initiatetransfer2")
	public WebElement initiatetransferByUserSearch;
	
	@FindBy(xpath="//*[@class='errorClass']")
	public WebElement messages;
	
	@FindBy(xpath="//a[@href='#collapseOne']")
	public WebElement ByMobileNumber;
	
	@FindBy(xpath="//a[@href='#collapseTwo']")
	public WebElement ByUserID;
	
	@FindBy(xpath ="//span[@class='errorClass']")
	public WebElement ServerSideErrorMsg;
	
	@FindBy(xpath="//label[@for='msisdn'][@class='error']")
	public WebElement msisdnfieldError;
	
	@FindBy(xpath="//label[@for='categoryList'][@class='error']")
	public WebElement ToCategoryFieldError;
	
	@FindBy(xpath="//label[@for='user'][@class='error']")
	public WebElement ToUserFieldError;
	
	
	
	WebDriver driver = null;

	public C2CTransferInitiatePageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public String enterMobileNo(String msisdn) {
		Log.info("Trying to entered Mobile Number");
		mobileNumber.sendKeys(msisdn);
		Log.info("User entered Mobile Number"+msisdn);
		return msisdn;
	}

	public void selectCategoryCode(String Category) {
		Select CategoryCode = new Select(categoryCode);
		CategoryCode.selectByVisibleText(Category);
		Log.info("User selected Category.");
	}

	public void enterToUserName(String ToUser) {
		Log.info("Trying to enter ToUserName");
		toUserName.sendKeys(ToUser);
		Log.info("enter ToUserName"+ToUser);
	}

	public void clickSubmitForMobileNo() {
		Log.info("Trying to clickSubmitForMobileNo");
		initiatetransferByMobileNo.click();
		Log.info("Clicked clickSubmitForMobileNo button");
	}

	public void clickSubmitForUserSearch() {
		Log.info("Trying to clickSubmitForUserSearch");
		initiatetransferByUserSearch.click();
		Log.info("Clicked clickSubmitForUserSearch button");
	}
	
	public void clickPannelByMobileNumber(){
		Log.info("Trying to clickPannelByMobileNumber");
		ByMobileNumber.click();
		Log.info("User clicked clickPannelByMobileNumber");
	}
	
	public void clickPannelByUserID(){
		Log.info("Trying to clickPannelByUserID");
		ByUserID.click();
		Log.info("User clicked clickPannelByUserID");
	}
	
	public String getServerSideErrorMsg()
	{
		Log.info("Trying to get server side field error");
		String errorMessage = ServerSideErrorMsg.getText();
		Log.info("server side field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getServerSideMultipleErrorMsg()
	{
		Log.info("Trying to get server side field error");
		StringBuffer buffer = new StringBuffer();
		List<WebElement> errorMessageElement = driver.findElements(By.xpath("//span[@class='errorClass']"));
		buffer.append(errorMessageElement.get(0).getText());
		buffer.append(errorMessageElement.get(1).getText());
		String errorMessage = buffer.toString();
		Log.info("server side field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getTransactionID() {
		String TransactionMessage[] = new String[2];
		try {
		TransactionMessage[0] = messages.getText();
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
		String message=messages.getText();
		Log.info("Message fetched successfuly."+message);
		return message;
	}
	
	public String getMobileNumberFieldError(){
		Log.info("Trying to get MSISDN field error");
		String errorMessage = msisdnfieldError.getText();
		Log.info("MSISDN field error: "+errorMessage);
		return errorMessage;
	}
	
	
	public String getToCategoryFieldError(){
		Log.info("Trying to get ToCategory field error");
		String errorMessage = ToCategoryFieldError.getText();
		Log.info("ToCategory field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getToUserFieldError(){
		Log.info("Trying to get To User field error");
		String errorMessage = ToUserFieldError.getText();
		Log.info("To User field error: "+errorMessage);
		return errorMessage;
	}
	
}
