package com.pageobjects.channeladminpages.channelreportssuser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class ZeroBalCounterSummarySpring {

	@FindBy(xpath="//a[@href [contains(.,'pageCode=ZBALSUM001')]]")
	private WebElement zeroBalSummLink;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
	@FindBy(id="domainList")
	private WebElement domainCode;
	
	@FindBy(id="parentCategoryList")
	private WebElement parentCategoryCode;
	
	@FindBy(id="thresholdType")
	private WebElement thresholdType;
	
	@FindBy(id="radioNetCode")
	private WebElement radioNetCode;
	@FindBy(id="dailyDate")
	private WebElement dailyDate;
	@FindBy(id="radioNetCodetwo")
	private WebElement radioNetCodetwo;
	@FindBy(id="fromMonth")
	private WebElement fromMonth;
	
	@FindBy(id="initiatesummary")
	private WebElement submit;
	
	
	@FindBy(xpath="//div[@for='dailyDate']")
	private WebElement dailyMsg;
	
	@FindBy(xpath="//input[@id='zoneCode']/following-sibling::label")
	private WebElement zoneCodeMsg;
	@FindBy(xpath="//div[@for='domainList']")
	private WebElement domainCodeMsg;
	@FindBy(xpath="//div[@for='parentCategoryList']")
	private WebElement parentCategoryCodeMsg;

	@FindBy(xpath="//div[@for='thresholdType']")
	private WebElement thresholdTypeMsg;
	
	@FindBy(xpath="//div[@for='fromMonth']")
	private WebElement monthlyMsg;
	
	@FindBy(xpath="//*[@id='initiatesummary' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabled;
WebDriver driver=null;
	
	public ZeroBalCounterSummarySpring(WebDriver driver){
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
		Select select = new Select(parentCategoryCode);
		select.selectByVisibleText(category);
		Log.info("Category selected successfully.");
	}
	
	public void clickzeroBalSummlink() {
		Log.info("Trying to click O2C Transfer Details link");
		zeroBalSummLink.click();
		Log.info("O2C Transfer Details link clicked successfully");
	}
	

	public void selectThresholdType(String type){
		Log.info("Trying to select threshold type: "+type);
		Select select = new Select(thresholdType);
		select.selectByVisibleText(type);
		Log.info("thresholdType selected successfully.");
	}
	
	public void selectDailyDate(String fDate){
		Log.info("Trying to enter from date: "+fDate);
		CommonUtils.selectDateInSpring(dailyDate,fDate,driver);
		Log.info("Daily date entered successfully");
	}
	public void selectMonthlyDate(String type){
		Log.info("Trying to select Monthly Date: "+type);
		Select select = new Select(fromMonth);
		select.selectByVisibleText(type);
		Log.info("Monthly Date selected successfully.");
	}
	
	public void selectDailyRadioButton(String type)
	{
		if(type=="true")
		{
			driver.findElement(By.id("radioNetCode")).click();
		}
	}
	public void selectMonthlyRadioButton(String type)
	{
		if(type=="true")
		{
			driver.findElement(By.id("radioNetCodetwo")).click();
		}
	}
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("dailyDate")){
			errormessage=dailyMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("zoneCode")){
			errormessage=zoneCodeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("domainList")){
			errormessage=domainCodeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryList")){
			errormessage=parentCategoryCodeMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("thresholdType")){
			errormessage=thresholdTypeMsg.getText();
		}	
		else if(attribute.equalsIgnoreCase("fromMonth")){
			errormessage=monthlyMsg.getText();
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
