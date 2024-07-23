package com.pageobjects.channeladminpages.channelreportsO2C;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class O2CtransferdetailsSpring{

	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTO2CDD01')]]")
	private WebElement o2cTransferdetailsLink;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainCode")
	private WebElement domainCode;
	
	@FindBy(id="fromtransferCategoryCode")
	private WebElement categoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	@FindBy(id="txnSubType")
	private WebElement transfersubtype;
	
	@FindBy(id="transferCategory")
	private WebElement transferCategory;
	
	@FindBy(id="fromDate")
	private WebElement fromDate;
	
	@FindBy(id="fromTime")
	private WebElement fromTime;
	
	@FindBy(id="toDate")
	private WebElement toDate;
	
	@FindBy(id="toTime")
	private WebElement toTime;
	
	@FindBy(id="currentDateRptChkBox")
	private WebElement currentDateBox;
	
	@FindBy(id="submitUserSearchButton")
	private WebElement submit;
	
	@FindBy(id="iNETReport")
	private WebElement report;
	
	@FindBy(xpath="//input[@id='fromDate']/following-sibling::label")
	private WebElement fromDateMsg;
	
	@FindBy(xpath="//input[@id='toDate']/following-sibling::label")
	private WebElement toDateMsg;
	
	@FindBy(xpath="//input[@id='fromTime']/following-sibling::label")
	private WebElement fromTimeMsg;
	
	@FindBy(xpath="//input[@id='toTime']/following-sibling::label")
	private WebElement toTimeMsg;
	
	@FindBy(xpath="//select[@id='txnSubType']/following-sibling::label")
	private WebElement txnSubtypeMsg;
	
	@FindBy(xpath="//*[@id='submitUserSearchButton' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public O2CtransferdetailsSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectZone(String zone){
		Log.info("Trying to select zone: "+zone);
		Select select = new Select(zoneCode);
		select.selectByVisibleText(zone);
		Log.info("Zone selected successfully.");
	}
	
	public void selectDomain(String domain){
		Log.info("Trying to select domain: "+domain);
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("Domain selected successfully.");
	}
	
	public void selectCategory(String category){
		Log.info("Trying to select category: "+category);
		Select select = new Select(categoryCode);
		select.selectByVisibleText(category);
		Log.info("Category selected successfully.");
	}
	
	public void enterUserName(String user){
		Log.info("Trying to enter user name: "+user);
		userName.sendKeys(user);
		Log.info("User Name entered successfully");		
	}
	
	public void selectTranserSubtype(String type){
		Log.info("Trying to select transfer subtype: "+type);
		Select select = new Select(transfersubtype);
		select.selectByVisibleText(type);
		Log.info("Transfer subtype selected successfully.");
	}
	
	public void selectTransferCategory(String category){
		Log.info("Trying to select transfer category: "+category);
		Select select = new Select(transferCategory);
		select.selectByVisibleText(category);
		Log.info("Transfer category selected successfully.");
	}
	
	public void enterfromDate(String fDate){
		Log.info("Trying to enter from date: "+fDate);
		CommonUtils.selectDateInSpring(fromDate,fDate,driver);
		Log.info("From date entered successfully");
			
	}

	public void entertoDate(String tDate){
		Log.info("Trying to enter to date: "+tDate);
		CommonUtils.selectDateInSpring(toDate,tDate,driver);
		Log.info("To date entered successfully");
	}
	
	public void enterfromTime(String fTime){
		Log.info("Trying to enter from time: "+fTime);
		fromTime.sendKeys(fTime);
		Log.info("From time entered successfully");
	}

	public void entertoTime(String tTime){
		Log.info("Trying to enter to time: "+tTime);
		toTime.sendKeys(tTime);
		Log.info("To Time entered successfully");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickreportBtn(){
		Log.info("Trying to click report button.");
		report.click();
		Log.info("Report button clicked successfuly.");
	}
	
	public void checkCurrentDate(){
		Log.info("Trying to check current date checbox");
		currentDateBox.click();
		Log.info("Checbox clicked successfuly.");
	}
	
	public void clickO2CTransferDetailslink() {
		Log.info("Trying to click O2C Transfer Details link");
		o2cTransferdetailsLink.click();
		Log.info("O2C Transfer Details link clicked successfully");
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
		else if(attribute.equalsIgnoreCase("toDate")){
			errormessage=toDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDate")){
			errormessage=fromDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("subtype")){
			errormessage=txnSubtypeMsg.getText();
		}
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	public boolean submitBtnenabled(){
		boolean enabled=false;
		try{if(submitbtnenabled.isDisplayed()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
}

