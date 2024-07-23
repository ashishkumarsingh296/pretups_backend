package com.pageobjects.channeladminpages.ChannelReportsSummary;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class OperationSummaryReportSpring {


	@FindBy(xpath="//a[@href [contains(.,'pageCode=OPTSRPT001')]]")
	private WebElement operationSummaryReport;
	
	@FindBy(id="zoneList")
	private WebElement zoneList;
	
	@FindBy(id="domainList")
	private WebElement domainList;
	
	@FindBy(id="parentCategoryList")
	private WebElement parentCategoryList;

	
	@FindBy(id="user")
	private WebElement user;
	
	@FindBy(id="fromDate")
	private WebElement fromDate;
	
	@FindBy(id="toDate")
	private WebElement toDate;
	
	@FindBy(id="radioNetCodeMain")
	private WebElement radioNetCodeMain;
	
	@FindBy(id="radioNetCodeTotal")
	private WebElement radioNetCodeTotal;
	
	@FindBy(id="submitButton")
	private WebElement submitButton;
	
	@FindBy(id="iNETReport")
	private WebElement iNETReport;
	
	@FindBy(xpath="//input[@id='fromDate']/following-sibling::label")
	private WebElement fromDateMsg;
	
	@FindBy(xpath="//input[@id='toDate']/following-sibling::label")
	private WebElement toDateMsg;
		
	@FindBy(xpath="//select[@id='zoneList']/following-sibling::label")
	private WebElement zoneListMsg;
	
	@FindBy(xpath="//select[@id='domainList']/following-sibling::label")
	private WebElement domainListMsg;
	
	@FindBy(xpath="//select[@id='parentCategoryList']/following-sibling::label")
	private WebElement parentCategoryListMsg;
	
	@FindBy(xpath="//input[@id='user']/following-sibling::label")
	private WebElement userMsg;
	
	@FindBy(xpath="//*[@id='submitButton']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public OperationSummaryReportSpring(WebDriver driver){
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
		Log.info("Trying to select Domain: "+domain);
		try{
		Select select = new Select(domainList);
		select.selectByVisibleText(domain);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Domain selected successfully.");
	}
    
	public void selectCategory(String parentCategory){
		Log.info("Trying to select category: "+parentCategory);
		try{
		Select select = new Select(parentCategoryList);
		select.selectByVisibleText(parentCategory);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Category selected successfully.");
	}
	
	public void enterUserName(String userName){
		Log.info("Trying to enter user name: "+userName);
		try{
		user.clear();
		user.sendKeys(userName);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("User Name entered successfully");		
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
	
	public void selectMainRadioButton(String type) 
    { 
            if(type=="true") 
            { 
                    driver.findElement(By.id("radioNetCodeMain")).click(); 
            } 
    } 
    public void selectTotalRadioButton(String type) 
    { 
            if(type=="true") 
            { 
                    driver.findElement(By.id("radioNetCodeTotal")).click(); 
            } 
    }

	
	
	public void clickOperationSummaryReportlink() {
		Log.info("Trying to click Operation Summary Report link");
		operationSummaryReport.click();
		Log.info(" Operation Summary Report link clicked successfully");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submitButton.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickInetBtn(){
		Log.info("Trying to click Report button.");
		iNETReport.click();
		Log.info("Report button clicked successfuly.");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
       if(attribute.equalsIgnoreCase("toDate")){
			errormessage=toDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("fromDate")){
			errormessage=fromDateMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryCode")){
			errormessage=parentCategoryListMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("userName")){
			errormessage=userMsg.getText();
		}
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	
	public boolean submitBtnEnabled(){
		boolean enabled=false;
		try{if(submitbtnenabled.isEnabled()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}

}

