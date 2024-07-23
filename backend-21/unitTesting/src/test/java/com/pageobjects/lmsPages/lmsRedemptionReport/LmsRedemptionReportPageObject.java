package com.pageobjects.lmsPages.lmsRedemptionReport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class LmsRedemptionReportPageObject {

		
	@FindBy(xpath="//a[@href [contains(.,'pageCode=LMSRDRPT01')]]")
	private WebElement lmsRedmReportLink;
	
	/*@FindBy(id="zoneCodePanelOne")
	private WebElement zoneCode;
	
	@FindBy(id="domainCodePanelOne")
	private WebElement domainCode;
	@FindBy(id="parentCategoryCode")
	private WebElement categoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	
	
	@FindBy(id="loginIDPanelOne")
	private WebElement loginID;
	*/
	
	@FindBy(id="fromDatePanelOne") 
	private WebElement fromDate;
	
	@FindBy(id="toDatePanelOne") 
	private WebElement toDate;
	
	@FindBy(id="msisdnIDPanelOne")
	private WebElement msisdn;
	
	@FindBy(id="redemptionTypePanelOne")
	private WebElement redemptionType;	
	
	
	
	@FindBy(id="submitButtonIDPanelOne")
	private WebElement submit;
	
	@FindBy(id="iNETReportForThreshold")
	private WebElement report;  
		
	@FindBy(xpath="//select[@id='fromDatePanelOne']/following-sibling::label")
	private WebElement fromDateMsg;
	
	@FindBy(xpath="//select[@id='toDatePanelOne']/following-sibling::label")
	private WebElement toDateMsg;
			
	@FindBy(xpath="//input[@id='redemptionTypePanelOne']/following-sibling::label")
	private WebElement redemptionTypeMsg;
			
	@FindBy(xpath="//input[@id='msisdnIDPanelOne']/following-sibling::label")
	private WebElement msisdnMsg;
	
	@FindBy(xpath="//*[@id='submitButtonIDPanelOne']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public LmsRedemptionReportPageObject(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectService(String service){
		Log.info("Trying to select Service: "+service);
		Select select = new Select(redemptionType);
		select.selectByVisibleText(service);
		Log.info("Service selected successfully.");
	}
	
		
	public void enterFromDate(String dateValue){
		Log.info("Trying to enter  date: "+dateValue);
		CommonUtils.selectDateInSpring(fromDate,dateValue,driver);
		Log.info(" date entered successfully");
			
	}
	public void enterToDate(String dateValue){
		Log.info("Trying to enter  date: "+dateValue);
		CommonUtils.selectDateInSpring(toDate,dateValue,driver);
		Log.info(" date entered successfully");
			
	}
	
	
	
	public void enterMsisdn(String msisdnValue){
		Log.info("Trying to enter msisdn: "+msisdnValue);
		msisdn.sendKeys(msisdnValue);  
		Log.info("Msisdn  entered successfully");  
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
		
	
	public void clickLmsRedReportLink() {
		Log.info("Trying to click LMS Redemption Report Link");
		lmsRedmReportLink.click();
		Log.info("LMS Redemption Report link clicked successfully");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		
		 if(attribute.equalsIgnoreCase("fromDate")){
			errormessage=fromDateMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("toDate")){
			errormessage=toDateMsg.getText();
							
		}else if(attribute.equalsIgnoreCase("redemptionType")){
			errormessage=redemptionTypeMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("msisdn")){
			errormessage=msisdnMsg.getText();
			
		}
		
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	public boolean submitBtnenabled(){
		boolean enabled=false;
		try{
			 if(submitbtnenabled.isDisplayed()){
				enabled=true;
				Log.info("Submit button is enabled.");
		      }
		}catch(Exception e){
			enabled = false;
		   Log.info("Submit button is not enabled.");
		}
		return enabled;
	}
}
