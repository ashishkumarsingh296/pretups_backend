package com.pageobjects.channeladminpages.channelreportsO2C;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class C2STransferSpringPageObject {

		
	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTTRCS001')]]")
	private WebElement c2STraLink;
	
	@FindBy(id="serviceTypeIDPanelOne")
	private WebElement serviceType;
	
	@FindBy(id="transferStatusIDPanelOne")
	private WebElement transferStatus;
	
	@FindBy(id="dateIDPanelOne") 
	private WebElement date;
	
	@FindBy(id="fromTimeIDPanelOne")
	private WebElement fromTime;
	
	@FindBy(id="toTimeIDPanelOne")
	private WebElement toTime;
	
	@FindBy(id="msisdnIDPanelOne")
	private WebElement msisdn;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainCode")
	private WebElement domainCode;
	
	@FindBy(id="parentCategoryCode")
	private WebElement categoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	
	@FindBy(id="submitButtonIDPanelOne")
	private WebElement submit;
	
	@FindBy(id="iNETReport")
	private WebElement report;
	
	@FindBy(xpath="//select[@id='serviceTypeIDPanelOne']/following-sibling::label")
	private WebElement serviceTypeMsg;
	
	@FindBy(xpath="//select[@id='transferStatusIDPanelOne']/following-sibling::label")
	private WebElement transferStatusMsg;
	
	@FindBy(xpath="//input[@id='dateIDPanelOne']/following-sibling::label")  
	private WebElement dateMsg;
	
	@FindBy(xpath="//input[@id='fromTimeIDPanelOne']/following-sibling::label")
	private WebElement fromTimeMsg;
	
	@FindBy(xpath="//input[@id='toTimeIDPanelOne']/following-sibling::label")
	private WebElement toTimeMsg;
	
	@FindBy(xpath="//input[@id='msisdnIDPanelOne']/following-sibling::label")
	private WebElement msisdnMsg;
	
	@FindBy(xpath="//*[@id='submitButtonIDPanelOne']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public C2STransferSpringPageObject(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectService(String service){
		Log.info("Trying to select Service: "+service);
		Select select = new Select(serviceType);
		select.selectByVisibleText(service);
		Log.info("Service selected successfully.");
	}
	
	public void selectStatus(String status){
		Log.info("Trying to select status: "+status);
		Select select = new Select(transferStatus);
		select.selectByVisibleText(status);
		Log.info("Status selected successfully.");
	}
	
	public void enterDate(String dateValue){
		Log.info("Trying to enter  date: "+dateValue);
		CommonUtils.selectDateInSpring(date,dateValue,driver);
		Log.info(" date entered successfully");
			
	}
	
	public void enterfromTime(String fTime){
		Log.info("Trying to enter from time: "+fTime);
		
		fromTime.sendKeys(fTime);
		Log.info("Fromtimeentered successfully");
			
	}
	public void entertoTime(String tTime){
		Log.info("Trying to enter to date: "+tTime);		
		toTime.sendKeys(tTime);
		Log.info("To time entered successfully");
	}
	
	public void enterMsisdn(String msisdnValue){
		Log.info("Trying to enter msisdn: "+msisdnValue);
		msisdn.sendKeys(msisdnValue);  
		Log.info("Msisdn  entered successfully");  
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
		
	
	public void clickC2STransferlink() {
		Log.info("Trying to click c2s transfer link");
		c2STraLink.click();
		Log.info("c2s Transfer link clicked successfully");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		
		 if(attribute.equalsIgnoreCase("serviceType")){
			errormessage=serviceTypeMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("transferStatus")){
			errormessage=transferStatusMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("date")){
			errormessage=dateMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("fromTime")){
			errormessage=fromTimeMsg.getText();
			
		}else if(attribute.equalsIgnoreCase("toTime")){
			errormessage=toTimeMsg.getText();
			
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
