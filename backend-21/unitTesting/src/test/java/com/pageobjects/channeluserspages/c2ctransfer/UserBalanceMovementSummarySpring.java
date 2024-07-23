package com.pageobjects.channeluserspages.c2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class UserBalanceMovementSummarySpring {

	@FindBy(xpath ="//a[@href='#collapseOne']")
	private WebElement collapseOne;
	
	@FindBy(xpath ="//a[@href='#collapseTwo']")
	private WebElement collapseTwo;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=UBALMOV001')]]")
	private WebElement userBalanceMovementSummaryLink;
	
	@FindBy(id="fromDate")
	private WebElement fromDate;
	
	@FindBy(id="toDate")
	private WebElement toDate;
	
	@FindBy(id="msisdn")
	private WebElement msisdn;
	
	@FindBy(id="fromDate1")
	private WebElement fromDate1;
	
	@FindBy(id="toDate1")
	private WebElement toDate1;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainCode")
	private WebElement domainCode;
	
	@FindBy(id="parentCategoryCode")
	private WebElement parentCategoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	@FindBy(id="submitButtonForMsisdn")
	private WebElement submitButtonForMsisdn;
	
	@FindBy(id="iNETReportMSISDN")
	private WebElement iNETReportMSISDN;
	
	@FindBy(id="submitButtonForUserName")
	private WebElement submitButtonForUserName;
	
	@FindBy(id="iNETReportUserName")
	private WebElement iNETReportUserName;
	
	@FindBy(xpath="//input[@id='fromDate']/following-sibling::label")
	private WebElement fromDateMOBMsg;
	
	@FindBy(xpath="//input[@id='toDate']/following-sibling::label")
	private WebElement toDateMOBMsg;
	
	@FindBy(xpath="//input[@id='msisdn']/following-sibling::label")
	private WebElement msisdnMOBMsg;
	
	@FindBy(xpath="//input[@id='fromDate1']/following-sibling::label")
	private WebElement fromDateUserNameMsg;
	
	@FindBy(xpath="//input[@id='toDate1']/following-sibling::label")
	private WebElement toDateUserNameMsg;
	
	@FindBy(xpath="//select[@id='zoneCode']/following-sibling::label")
	private WebElement zoneCodeUserNameMsg;
	
	@FindBy(xpath="//select[@id='domainCode']/following-sibling::label")
	private WebElement domainCodeUserNameMSg;
	
	@FindBy(xpath="//select[@id='parentCategoryCode']/following-sibling::label")
	private WebElement parentCategoryCodeUserNameMSg;
	
	@FindBy(xpath="//input[@id='userName']/following-sibling::label")
	private WebElement userNameMSg;
	
	@FindBy(xpath="//*[@id='submitButtonForMsisdn' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabledMsisdn;
	
	@FindBy(xpath="//*[@id='submitButtonForUserName' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabledUserName;
	
    WebDriver driver=null;
	
	public UserBalanceMovementSummarySpring(WebDriver driver){
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
	public void enterfromDateMobileNumber(String fDate){
		Log.info("Trying to enter from date in mobile number panel: "+fDate);
		CommonUtils.selectDateInSpring(fromDate,fDate,driver);
		Log.info("From date entered successfully in mobile number panel");
			
	}
	
	public void entertoDateMobileNumber(String tDate){
		Log.info("Trying to enter from date in mobile number panel: "+tDate);
		CommonUtils.selectDateInSpring(toDate,tDate,driver);
		Log.info("From date entered successfully in mobile number panel");
			
	}
	
	public void enterfromDateUserName(String fDate){
		Log.info("Trying to enter from date in user name panel: "+fDate);
		CommonUtils.selectDateInSpring(fromDate1,fDate,driver);
		Log.info("From date entered successfully in user name panel");
			
	}
	
	public void entertoDateUserName(String tDate){
		Log.info("Trying to enter from date in user name panel: "+tDate);
		CommonUtils.selectDateInSpring(toDate1,tDate,driver);
		Log.info("To date entered successfully in user name panel");
			
	}
	
	public void selectZoneUserName(String zone){
		Log.info("Trying to select Zone : "+zone);
		Select select = new Select(zoneCode);
		select.selectByVisibleText(zone);
		Log.info("Zone selected successfully in user name panel");
	}
	
	public void selectdomainUserName(String domain){
		Log.info("Trying to select Channel Domain : "+domain);
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("Channel Domain selected successfully in user name panel");
	}
	
	public void selectchannelCategory(String channelCategory)
	{
		Log.info("Trying to select Channel Domain : "+channelCategory);
		Select select = new Select(parentCategoryCode);
		select.selectByVisibleText(channelCategory);
		Log.info("Channel Domain selected successfully in user name panel");
	}
	
	public void enterCategoryUser(String user)
	{
		Log.info("Trying to enter user name in User Name Panel: "+user);
		userName.sendKeys(user);
		Log.info("User Name entered successfully in User Name Panel");	
	}
	
	public void clicksubmitBtnMOB(){
		Log.info("Trying to click submit button. in Mobile Number Panel");
		submitButtonForMsisdn.click();
		Log.info("Submit button clicked successfuly. in Mobile Number Panel");
	}
	
	public void clickreportBtnMOB(){
		Log.info("Trying to click report button. in Mobile Number Panel");
		iNETReportMSISDN.click();
		Log.info("Report button clicked successfuly. in Mobile Number Panel");
	}
	
	public void clicksubmitBtnUSR(){
		Log.info("Trying to click submit button. in User Name Panel");
		submitButtonForUserName.click();
		Log.info("Submit button clicked successfuly. in User Name Panel");
	}
	
	public void clickreportBtnUSR(){
		Log.info("Trying to click report button. in User Name Panel");
		iNETReportUserName.click();
		Log.info("Report button clicked successfuly. in User Name Panel");
	}
	
	public void clickC2CTransferDetailslink() {
		Log.info("Trying to click User Balance Movement Summary link");
		userBalanceMovementSummaryLink.click();
		Log.info("User Balance Movement Summary link clicked successfully");
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
		if(attribute.equalsIgnoreCase("fromdate")){
			errormessage=fromDateMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("todate")){
			errormessage=toDateMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("msisdn")){
			errormessage=msisdnMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromdate1")){
			errormessage=fromDateUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("todate1")){
			errormessage=toDateUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("zoneCode")){
			errormessage=zoneCodeUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("domainCode")){
			errormessage=domainCodeUserNameMSg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryCode")){
			errormessage=parentCategoryCodeUserNameMSg.getText();
		}
		else if(attribute.equalsIgnoreCase("username")){
			errormessage=userNameMSg.getText();
		}
		
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
}
