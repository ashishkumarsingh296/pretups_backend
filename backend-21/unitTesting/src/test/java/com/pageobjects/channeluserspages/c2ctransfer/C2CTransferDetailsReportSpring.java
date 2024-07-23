package com.pageobjects.channeluserspages.c2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class C2CTransferDetailsReportSpring {
	
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTRWTR001')]]")
	private WebElement c2cTransferdetailsLink;
	
	
	@FindBy(id="txnSubTypeMOB")
	private WebElement txnSubTypeMOB;
	
	@FindBy(id="transferInOrOutMOB")
	private WebElement transferInOrOutMOB;	
	
	@FindBy(id="frommsisdn")
	private WebElement frommsisdn;
	
	@FindBy(id="tomsisdn")
	private WebElement tomsisdn;
	
	@FindBy(id="fromDateFormobileNumber")
	private WebElement fromDateFormobileNumber;
	
	@FindBy(id="toDateFormobileNumber")
	private WebElement toDateFormobileNumber;
	
	@FindBy(id="toTimeFormobileNumber")
	private WebElement toTimeFormobileNumber;
	
	@FindBy(id="fromTimeFormobileNumber")
	private WebElement fromTimeFormobileNumber;
	
	@FindBy(id="currentDateRptChkBoxMSISDN")
	private WebElement currentDateRptChkBoxMSISDN;
	
	@FindBy(id="staffReportMSISDN")
	private WebElement staffReportMSISDN;
	
	@FindBy(id="submitButtonForMsisdn")
	private WebElement submitButtonForMsisdn;
	
	@FindBy(id="iNETReportPanelOne")
	private WebElement iNETReportPanelOne;
	
	@FindBy(id="txnSubTypeUSR")
	private WebElement txnSubTypeUSR;
	
	@FindBy(id="transferInOrOutUSR")
	private WebElement transferInOrOutUSR;	
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainCode")
	private WebElement domainCode;
	
	@FindBy(id="fromDateForUserName")
	private WebElement fromDateForUserName;
	
	@FindBy(id="fromTimeForUserName")
	private WebElement fromTimeForUserName;
	
	@FindBy(id="toDateForUserName")
	private WebElement toDateForUserName;
	
	@FindBy(id="toTimeForUserName")
	private WebElement toTimeForUserName;
	
	@FindBy(id="fromtransferCategoryCode")
	private WebElement fromtransferCategoryCode;
	
	@FindBy(id="fromUserName")
	private WebElement fromUserName;
	
	@FindBy(id="totransferCategoryCode")
	private WebElement totransferCategoryCode;
	
	@FindBy(id="touserName")
	private WebElement touserName;
	
	@FindBy(id="currentDateRptChkBoxUserSearch")
	private WebElement currentDateRptChkBoxUserSearch;
	
	@FindBy(id="staffReportUserSearch")
	private WebElement staffReportUserSearch;
	
	@FindBy(id="submitButtonForUserName")
	private WebElement submitButtonForUserName;
	
	@FindBy(id="iNETReportPanelTwo")
	private WebElement iNETReportPanelTwo;
	
	@FindBy(xpath ="//a[@href='#collapseOne']")
	private WebElement collapseOne;
	
	@FindBy(xpath ="//a[@href='#collapseTwo']")
	private WebElement collapseTwo;
	
	@FindBy(xpath="//input[@id='fromDateFormobileNumber']/following-sibling::label")
	private WebElement fromDateFormobileNumberMsg;
	
	@FindBy(xpath="//input[@id='toDateFormobileNumber']/following-sibling::label")
	private WebElement toDateFormobileNumberMsg;
    
	@FindBy(xpath="//select[@id='txnSubTypeMOB']/following-sibling::label")
	private WebElement txnSubTypeMOBMsg;
	
	@FindBy(xpath="//select[@id='transferInOrOutMOB']/following-sibling::label")
	private WebElement transferInOrOutMOBMsg;
	
	@FindBy(xpath="//input[@id='frommsisdn']/following-sibling::label")
	private WebElement frommsisdnMsg;
	
	@FindBy(xpath="//input[@id='tomsisdn']/following-sibling::label")
	private WebElement tomsisdnMsg;
	
	@FindBy(xpath="//input[@name='fromTime']/following-sibling::label")
	private WebElement fromTimeMsg;
	
	@FindBy(xpath="//input[@name='toTime']/following-sibling::label")
	private WebElement toTimeMsg;
	
	@FindBy(xpath="//select[@id='txnSubTypeUSR']/following-sibling::label")
	private WebElement txnSubTypeUSRMsg;
	
	@FindBy(xpath="//select[@id='transferInOrOutUSR']/following-sibling::label")
	private WebElement transferInOrOutUSRMsg;
	
	@FindBy(xpath="//input[@id='fromDateForUserName']/following-sibling::label")
	private WebElement fromDateForUserNameMsg;
	
	@FindBy(xpath="//input[@id='toDateForUserName']/following-sibling::label")
	private WebElement toDateForUserNameMsg;
	
	@FindBy(xpath="//input[@id='fromTimeForUserName']/following-sibling::label")
	private WebElement fromTimeForUserNameMsg;
	
	@FindBy(xpath="//input[@id='toTimeForUserName']/following-sibling::label")
	private WebElement toTimeForUserNameMsg;
	
	@FindBy(xpath="//*[@id='submitButtonForMsisdn' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabledMsisdn;
	
	@FindBy(xpath="//*[@id='submitButtonForUserName' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabledUserName;
	
	WebDriver driver=null;
	
	public C2CTransferDetailsReportSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectTransferSubTypeMOB(String transferSubTypeMOB){
		Log.info("Trying to select Transfer Sub Type: "+transferSubTypeMOB);
		Select select = new Select(txnSubTypeMOB);
		select.selectByVisibleText(transferSubTypeMOB);
		Log.info("Transfer Sub Type selected successfully.");
	}
	
	public void selectTransferInOutMOB(String transferInOut){
		Log.info("Trying to select Transfer In/Out : "+transferInOut);
		Select select = new Select(transferInOrOutMOB);
		select.selectByVisibleText(transferInOut);
		Log.info("Transfer In/Out selected successfully.");
	}
	
	public void enterFromUserMobileNumber(String fromMsisdn )
	{
		Log.info("Trying to enter from User Msisdn"+fromMsisdn);
		frommsisdn.sendKeys(fromMsisdn);
		Log.info("From User Msisdn entered successfully");
	}
	
	public void enterToUserMobileNumber(String toMsisdn )
	{
		Log.info("Trying to enter to user Msisdn"+toMsisdn);
		tomsisdn.sendKeys(toMsisdn);
		Log.info("To user Msisdn entered successfully");
	}
	
	public void enterfromDateMobileNumber(String fDate){
		Log.info("Trying to enter from date in mobile number panel: "+fDate);
		CommonUtils.selectDateInSpring(fromDateFormobileNumber,fDate,driver);
		Log.info("From date entered successfully in mobile number panel");
			
	}
	
	public void entertoDateMobileNumber(String fDate){
		Log.info("Trying to enter from date in mobile number panel: "+fDate);
		CommonUtils.selectDateInSpring(toDateFormobileNumber,fDate,driver);
		Log.info("From date entered successfully in mobile number panel");
			
	}
	
	public void enterfromTimeMobileNumber(String fTime){
		Log.info("Trying to enter from time in mobile number panel: "+fTime);
		fromTimeFormobileNumber.sendKeys(fTime);
		Log.info("From time entered successfully in mobile number panel");
	}
	
	public void entertoTimeMobileNumber(String fTime){
		Log.info("Trying to enter from time in mobile number panel: "+fTime);
		toTimeFormobileNumber.sendKeys(fTime);
		Log.info("From time entered successfully in mobile number panel");
	}
	
	public void checkCurrentDate(){
		Log.info("Trying to check current date checbox in mobile number panel");
		currentDateRptChkBoxMSISDN.click();
		Log.info("Checbox clicked successfuly in mobile number panel");
	}
	
	public void checkstaffReportMSISDN()
	{
		Log.info("Trying to check staff user report checbox in mobile number panel");
		staffReportMSISDN.click();
		Log.info("staff user report Checbox clicked successfuly in mobile number panel");
	}
	
	public void selectTransferSubTypeUSR(String transferSubTypeUSR){
		Log.info("Trying to select Transfer Sub Type in USR: "+transferSubTypeUSR);
		Select select = new Select(txnSubTypeUSR);
		select.selectByVisibleText(transferSubTypeUSR);
		Log.info("Transfer Sub Type selected successfully. in USR");
	}
	
	public void selectTransferInOutUSR(String transferInOut){
		Log.info("Trying to select Transfer In/Out : "+transferInOut);
		Select select = new Select(transferInOrOutUSR);
		select.selectByVisibleText(transferInOut);
		Log.info("Transfer In/Out selected successfully.");
	}
	
	public void selectZone(String zone){
		Log.info("Trying to select zone: "+zone);
		Select select = new Select(zoneCode);
		select.selectByVisibleText(zone);
		Log.info("Zone selected successfully.");
	}
	
	public void selectDomain(String domain){
		Log.info("Trying to select zone: "+domain);
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("Zone selected successfully.");
	}
	
	public void enterfromDateUserName(String fDate){
		Log.info("Trying to enter from date in User Name panel: "+fDate);
		CommonUtils.selectDateInSpring(fromDateForUserName,fDate,driver);
		Log.info("From date entered successfully in User Name panel");
			
	}
	
	public void entertoDateUserName(String fDate){
		Log.info("Trying to enter from date in User Name panel: "+fDate);
		CommonUtils.selectDateInSpring(toDateForUserName,fDate,driver);
		Log.info("To date entered successfully in User Name panel");
			
	}
	
	public void enterfromTimeUserName(String fTime){
		Log.info("Trying to enter from time in User Name panel: "+fTime);
		fromTimeForUserName.sendKeys(fTime);
		Log.info("From time entered successfully in User Name panel");
	}
	
	public void entertoTimeUserName(String fTime){
		Log.info("Trying to enter to time in User Name panel: "+fTime);
		toTimeForUserName.sendKeys(fTime);
		Log.info("To time entered successfully in User Name panel");
	}
	
	public void checkCurrentDateUserName(){
		Log.info("Trying to check current date checbox in User Name panel");
		currentDateRptChkBoxUserSearch.click();
		Log.info("Checkbox clicked successfuly in User Name panel");
	}
	
	public void checkStaffReportUserName(){
		Log.info("Trying to check staff reportchecbox in User Name panel");
		staffReportUserSearch.click();
		Log.info("Checkbox clicked successfuly in User Name panel");
	}
	
	public void selectSearchCategoryUserName(String searchCategoryCode)
	{
		Log.info("Trying to select search category: "+searchCategoryCode);
		Select select = new Select(fromtransferCategoryCode);
		select.selectByVisibleText(searchCategoryCode);
		Log.info("Search category selected successfully.");
	}
	
	public void enterSearchUser(String user){
		Log.info("Trying to enter user name in User Name Panel: "+user);
		fromUserName.sendKeys(user);
		Log.info("User Name entered successfully in User Name Panel");		
	}
	
	public void selectTransferUserCategoryUserName(String searchCategoryCode)
	{
		Log.info("Trying to select transfer user category: "+searchCategoryCode);
		Select select = new Select(totransferCategoryCode);
		select.selectByVisibleText(searchCategoryCode);
		Log.info("Transfer User category selected successfully.");
	}
	
	public void enterTransferUser(String user){
		Log.info("Trying to enter user name in User Name Panel: "+user);
		touserName.sendKeys(user);
		Log.info("User Name entered successfully in User Name Panel");		
	}
	
	public void clicksubmitBtnMOB(){
		Log.info("Trying to click submit button. in Mobile Number Panel");
		submitButtonForMsisdn.click();
		Log.info("Submit button clicked successfuly. in Mobile Number Panel");
	}
	
	public void clickreportBtnMOB(){
		Log.info("Trying to click report button. in Mobile Number Panel");
		iNETReportPanelOne.click();
		Log.info("Report button clicked successfuly. in Mobile Number Panel");
	}
	
	public void clicksubmitBtnUSR(){
		Log.info("Trying to click submit button. in User Name Panel");
		submitButtonForUserName.click();
		Log.info("Submit button clicked successfuly. in User Name Panel");
	}
	
	public void clickreportBtnUSR(){
		Log.info("Trying to click report button. in User Name Panel");
		iNETReportPanelOne.click();
		Log.info("Report button clicked successfuly. in User Name Panel");
	}
	
	public void clickC2CTransferDetailslink() {
		Log.info("Trying to click C2C Transfer Details link");
		c2cTransferdetailsLink.click();
		Log.info("C2C Transfer Details link clicked successfully");
	}
	
	public void clickcollapseOne(){
		Log.info("Trying to click Submit button for Mobile Number Panel");
		collapseOne.click();
		Log.info("Clicked Submit button for ");
	}
	
	public void clickcollapseTwo(){
		Log.info("Trying to click Submit button for User Name panel");
		collapseTwo.click();
		Log.info("Clicked Submit button for ");
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
		if(attribute.equalsIgnoreCase("fromtime")){
			errormessage=fromTimeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("totime")){
			errormessage=toTimeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromTimeForUserName")){
			errormessage=fromTimeForUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toTimeForUserName")){
			errormessage=toTimeForUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDateFormobileNumber")){
			errormessage=fromDateFormobileNumberMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toDateFormobileNumber")){
			errormessage=toDateFormobileNumberMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDateForUserName")){
			errormessage=fromDateForUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toDateForUserName")){
			errormessage=toDateForUserNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("txnSubTypeMOB")){
			errormessage=txnSubTypeMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("transferInOrOutMOB")){
			errormessage=transferInOrOutMOBMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("frommsisdn")){
			errormessage=frommsisdnMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("tomsisdn")){
			errormessage=tomsisdnMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("txnSubTypeUSR")){
			errormessage=txnSubTypeUSRMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("transferInOrOutUSR")){
			errormessage=transferInOrOutUSRMsg.getText();
		}
		
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
}
