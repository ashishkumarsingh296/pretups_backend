package com.pageobjects.channeladminpages.channelreportsO2C;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class ZeroBalanceCounterDetailSpringPageObject {

	@FindBy(xpath="//a[@href [contains(.,'pageCode=ZBALDET001')]]")
	private WebElement zeroBalanceCounterDetailsLink;
	
	@FindBy(id="thresholdTypeTHRESHOLD")
	private WebElement thresholdType;
	
	@FindBy(id="fromDateTHRESHOLD")
	private WebElement fromDate;
	
	@FindBy(id="toDateTHRESHOLD")
	private WebElement toDate;
	
	@FindBy(id="msisdn")
	private WebElement msisdn;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainCode")
	private WebElement domainCode;
	
	@FindBy(id="parentCategoryCode")
	private WebElement categoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	
	
	@FindBy(id="submitButtonForThreshold")
	private WebElement submit;
	
	@FindBy(id="iNETReportForThreshold")
	private WebElement report;   
	//div[@for='fromDateTHRESHOLD']

	@FindBy(xpath="//select[@id='thresholdTypeTHRESHOLD']/following-sibling::label")
	private WebElement thresholdTypeMsg;
	
	@FindBy(xpath="//input[@id='fromDateTHRESHOLD']/following-sibling::label")
	private WebElement fromDateMsg;  
	
	@FindBy(xpath="//input[@id='toDateTHRESHOLD']/following-sibling::label")
	private WebElement toDateMsg;
	
	@FindBy(xpath="//input[@id='msisdn']/following-sibling::label")
	private WebElement msisdnMsg;
	
	@FindBy(xpath="//*[@id='submitButtonForThreshold']")
	private WebElement submitbtnenabled;
		
	
	WebDriver driver=null;
	
	public ZeroBalanceCounterDetailSpringPageObject(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectThreshold(String threshold){
		Log.info("Trying to select Threshold: "+threshold);
		Select select = new Select(thresholdType);
		select.selectByVisibleText(threshold);
		Log.info("Threshold selected successfully.");
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
	
	public void enterMsisdn(String msisdnValue){
		Log.info("Trying to enter msisdn: "+msisdnValue);
		msisdn.sendKeys(msisdnValue);  
		Log.info("Msisdn  entered successfully");		
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
		
	
	public void clickZeroBalanceCounterDetailslink() {
		Log.info("Trying to click zero balance counter Details link");
		zeroBalanceCounterDetailsLink.click();
		Log.info("Zero Balance Counter Details link clicked successfully");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		
		 if(attribute.equalsIgnoreCase("toDate")){
			errormessage=toDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDate")){
			errormessage=fromDateMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("thresholdType")){
			errormessage=thresholdTypeMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("msisdn")){
			errormessage=msisdnMsg.getText();
			
		}
		
		else {
			Log.info("Issue with attribute ["+attribute+"] passed in method.");
		}
		
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
