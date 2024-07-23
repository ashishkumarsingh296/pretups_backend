package com.pageobjects.channeladminpages.c2c.staffselfc2creports;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.CommonUtils;
import com.utils.Log;

public class StaffSelfC2CReportsSpring {
	
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=STFSLF001')]]")
	private WebElement staffSelfC2CLink;
	@FindBy(id="txnSubType")
	private WebElement txnSubType;
	
	@FindBy(id="fromDate")
	private WebElement fromDate;
	
	@FindBy(id="toDate")
	private WebElement toDate;
	
	@FindBy(id="fromTime")
	private WebElement fromTime;
	
	@FindBy(id="toTime")
	private WebElement toTime;
	
	@FindBy(id="initiatesummary")
	private WebElement submit;
	
	@FindBy(id="iNETReport")
	private WebElement iNETReport;
	
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
	
	@FindBy(xpath="//*[@id='iNETReport' and @class=' btn btn-primary  enabled']")
	private WebElement iNETReportenabled;
	
	@FindBy(xpath="//*[@id='initiatesummary' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabled;
WebDriver driver=null;
	
	public StaffSelfC2CReportsSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectTranserSubtype(String type){
		Log.info("Trying to select transfer subtype: "+type);
		Select select = new Select(txnSubType);
		select.selectByVisibleText(type);
		Log.info("Transfer subtype selected successfully.");
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
	public void clickStaffSelfC2CReportlink() throws InterruptedException {
		Log.info("Trying to click Staff Self C2C Report link");
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.elementToBeClickable(staffSelfC2CLink)).click();
		
		
		Log.info("Staff Self C2C Report link clicked successfully");
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickInetBtn(){
		Log.info("Trying to click Inet report button.");
		iNETReport.click();
		Log.info("inet report button clicked successfuly.");
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
	
	public boolean inetBtnenabled(){
		boolean enabled=false;
		try{if(iNETReportenabled.isDisplayed()){
			enabled=true;
			Log.info("inet button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("inet button is not enabled.");}
		return enabled;
	}
	
	
	
	
	
	
}
