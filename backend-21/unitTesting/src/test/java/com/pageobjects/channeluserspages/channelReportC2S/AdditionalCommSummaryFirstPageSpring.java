package com.pageobjects.channeluserspages.channelReportC2S;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class AdditionalCommSummaryFirstPageSpring {

	@FindBy(id="zoneList")
	private WebElement zoneList;
	
	@FindBy(id="domainList")
	private WebElement domainList;
	
	@FindBy(id="parentCategoryList")
	private WebElement parentCategoryList;
	
	@FindBy(id="serviceTypeList")
	private WebElement serviceTypeList;
	
	@FindBy(id="radioDaily")
	private WebElement radioDaily;
	
	@FindBy(id="radioMonthly")
	private WebElement radioMonthly;
	
	@FindBy(id="fromDate")
	private WebElement fromDate;
	
	@FindBy(id="toDate")
	private WebElement toDate;
	
	
	@FindBy(id="fromMonth")
	private WebElement fromMonth;
	
	@FindBy(id="toMonth")
	private WebElement toMonth;
	
	@FindBy(xpath="//*[@id='submitButton']")
	private WebElement submitButton;
	
	@FindBy(xpath="//label[@for='zoneList']")
	private WebElement zoneErrorMsg;
	
	@FindBy(xpath="//label[@for='domainList']")
	private WebElement domainErrorMsg;
	
	@FindBy(xpath="//label[@for='parentCategoryList']")
	private WebElement categoryErrorMsg;
	
	@FindBy(xpath="//label[@for='serviceTypeList']")
	private WebElement serviceErrorMsg;
	
	@FindBy(xpath="//input[@id='fromDate']/following-sibling::label")
	private WebElement fromDateMsg;
	
	@FindBy(xpath="//input[@id='toDate']/following-sibling::label")
	private WebElement toDateMsg;
	
	@FindBy(xpath="//input[@id='fromMonth']/following-sibling::label")
	private WebElement fromMonthMsg;
	
	@FindBy(xpath="//input[@id='toMonth']/following-sibling::label")
	private WebElement toMonthMsg;
	
	
	WebDriver driver=null;
	public AdditionalCommSummaryFirstPageSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectZone(String zone){
		Log.info("Trying to select zone: "+zone);
		try{
		Select select = new Select(zoneList);
		select.selectByVisibleText(zone);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Zone selected successfully.");
	}
	
	public void selectDomain(String domain){
		Log.info("Trying to select domain: "+domain);
		try{
		Select select = new Select(domainList);
		select.selectByVisibleText(domain);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Domain selected successfully.");
	}
	
	public void selectCategory(String category){
		Log.info("Trying to select category: "+category);
		try{
		Select select = new Select(parentCategoryList);
		select.selectByVisibleText(category);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Category selected successfully.");
	}
	
	public void selectServiceType(String serviceType){
		Log.info("Trying to select Service: "+serviceType);
		try{
		Select select = new Select(serviceTypeList);
		select.selectByVisibleText(serviceType);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Service selected successfully.");
	}
	
	public void selectDailyRadio(){
		Log.info("Trying to select Daily Radio");
		radioDaily.click();
		Log.info("Selected Daily Radio");
	}
	
	public void selectMonthlyRadio(){
		Log.info("Trying to select Monthly Radio");
		radioMonthly.click();
		Log.info("Selected Monthly Radio");
	}
	
	public void selectFromDate(String fromDateValue){
		Log.info("Trying to select From Date: "+fromDateValue);
		CommonUtils.selectDateInSpring(fromDate,fromDateValue,driver);
		Log.info("From Date selected successfully.");
	}
	
	public void selectToDate(String toDateValue){
		Log.info("Trying to select To Date: "+toDateValue);
		CommonUtils.selectDateInSpring(fromDate,toDateValue,driver);
		Log.info("To Date selected successfully.");
	}
	
	public void enterFromMonth(String fromMonthvalue){
		Log.info("Trying to enter from month: "+fromMonthvalue);
		fromMonth.clear();
		fromMonth.sendKeys(fromMonthvalue);
		Log.info("From month entered successfully.");
	}
	
	public void enterToMonth(String toMonthvalue){
		Log.info("Trying to select toTime: "+toMonthvalue);
		toMonth.clear();
		toMonth.sendKeys(toMonthvalue);
		Log.info("toTime selected successfully.");
	}
	
	public boolean submitBtnEnabled(){
		boolean enabled=false;
		try{if(submitButton.isEnabled()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submitButton.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("zone")){
			errormessage=zoneErrorMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("domain")){
			errormessage=domainErrorMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("serviceType")){
			errormessage=serviceErrorMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDate")){
			errormessage=fromDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toDate")){
			errormessage=toDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromMonth")){
			errormessage=fromMonthMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toMonth")){
			errormessage=toMonthMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryList")){
			errormessage=categoryErrorMsg.getText();
		}

		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}

		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
}
