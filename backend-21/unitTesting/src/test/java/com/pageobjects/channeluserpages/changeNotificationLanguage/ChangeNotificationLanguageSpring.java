package com.pageobjects.channeluserpages.changeNotificationLanguage;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ChangeNotificationLanguageSpring {

	@FindBy(xpath ="//a[@href='#collapseOne']")
	private WebElement collapseOne;
	
	@FindBy(xpath ="//a[@href='#collapseTwo']")
	private WebElement collapseTwo;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=CHNOLG001')]]")
	private WebElement changeNotificationLanguageLink;
	
	
	@FindBy(id="msisdn")
	private WebElement msisdn;
	
	@FindBy(id="categoryCode")
	private WebElement categoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	@FindBy(id="submitButtonMSISDN")
	private WebElement submitButtonMSISDN;
	
	@FindBy(id="submitButtonForUserName")
	private WebElement submitButtonForUserName;
	
	@FindBy(xpath="//input[@id='msisdn']/following-sibling::label")
	private WebElement msisdnMOBMsg;
	
	@FindBy(xpath="//select[@id='categoryCode']/following-sibling::label")
	private WebElement categoryCodeUserNameMSg;
	
	@FindBy(xpath="//input[@id='userName']/following-sibling::label")
	private WebElement userNameMSg;
	
	@FindBy(xpath="//*[@id='submitButtonMSISDN' and @class='btn btn-primary ']")
	private WebElement submitbtnenabledMsisdn;
	
	@FindBy(xpath="//*[@id='submitButtonForUserName' and @class='btn btn-primary ']")
	private WebElement submitbtnenabledUserName;
	
    WebDriver driver=null;
	
	public ChangeNotificationLanguageSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void enterFromUserMobileNumber(String fromMsisdn )
	{
		Log.info("Trying to enter from User Msisdn"+fromMsisdn);
		msisdn.sendKeys(fromMsisdn);
		Log.info("From User Msisdn entered successfully");
	}
	
	public void clickMSIDN(String fromMsisdn)
	{
		Log.info("Trying to click from User Msisdn"+fromMsisdn);
		msisdn.click();
		Log.info("From User Msisdn clicked successfully");
	}
	
	public void clickuserName()
	{
		Log.info("Trying to click User Name from User Name Panel");
		userName.click();
		Log.info("User Name clicked successfully");
	}
	
	public void selectchannelCategory(String channelCategory)
	{
		Log.info("Trying to select Channel Category : "+channelCategory);
		Select select = new Select(categoryCode);
		select.selectByVisibleText(channelCategory);
		Log.info("Channel Category selected successfully in user name panel");
	}
	
	public void enterCategoryUser(String user)
	{
		Log.info("Trying to enter user name in User Name Panel: "+user);
		userName.sendKeys(user);
		Log.info("User Name entered successfully in User Name Panel");	
	}
	
	public void clicksubmitBtnMOB(){
		Log.info("Trying to click submit button. in Mobile Number Panel");
		submitButtonMSISDN.click();
		Log.info("Submit button clicked successfuly. in Mobile Number Panel");
	}
	
	
	public void clicksubmitBtnUSR(){
		Log.info("Trying to click submit button. in User Name Panel");
		submitButtonForUserName.click();
		Log.info("Submit button clicked successfuly. in User Name Panel");
	}
	
	
	public void clickChangeNotificationLanguagelink() {
		Log.info("Trying to click Change Notification Language link");
		changeNotificationLanguageLink.click();
		Log.info("Change Notification Language link clicked successfully");
	}
	
	public void clickcollapseOne(){
		Log.info("Trying to click Submit button for Mobile Number Panel");
		collapseOne.click();
		Log.info("Clicked Submit button for ");
	}
	
	public void clickcollapseTwo(){
		Log.info("Trying to select Category Panel");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.info("Exception:"+e);
		}
		collapseTwo.click();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.info("Exception:"+e);
		}
		Log.info("User Selected By Category No");
	}
	
	public boolean submitBtnenabledMSISDN(){
		boolean enabled=false;
		try{if(submitbtnenabledMsisdn.isDisplayed()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
	
	public boolean submitBtnenabledUserName(){
		boolean enabled=false;
		try{if(submitbtnenabledUserName.isDisplayed()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("msisdn")){
			errormessage=msisdnMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("categoryCode")){
			errormessage=categoryCodeUserNameMSg.getText();
		}
		else if(attribute.equalsIgnoreCase("username")){
			errormessage=userNameMSg.getText();
		}
		
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
}
