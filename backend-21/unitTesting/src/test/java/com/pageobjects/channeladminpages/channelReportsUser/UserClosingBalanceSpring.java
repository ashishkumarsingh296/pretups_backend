package com.pageobjects.channeladminpages.channelReportsUser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.CommonUtils;
import com.utils.Log;

public class UserClosingBalanceSpring {
	

	@FindBy(xpath="//a[@href [contains(.,'pageCode=URCLOBL001')]]")
	private WebElement userClosingBalance;
	
	@FindBy(id="zoneCode")
	private WebElement zoneCode;
	
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
	
	@FindBy(id="fromAmount")
	private WebElement fromAmount;
	
	@FindBy(id="toAmount")
	private WebElement toAmount;
	
	@FindBy(id="submit")
	private WebElement submit;
	
	@FindBy(id="reset")
	private WebElement reset;
	
	@FindBy(xpath="//input[@id='fromDate']/following-sibling::label")
	private WebElement fromDateMsg;
	
	@FindBy(xpath="//input[@id='toDate']/following-sibling::label")
	private WebElement toDateMsg;
	
	@FindBy(xpath="//input[@id='fromAmount']/following-sibling::label")
	private WebElement fromAmountMsg;
	
	@FindBy(xpath="//input[@id='toAmount']/following-sibling::label")
	private WebElement toAmountMsg;
	
	@FindBy(xpath="//select[@id='zoneCode']/following-sibling::label")
	private WebElement zoneCodeMsg;
	
	@FindBy(xpath="//select[@id='domainList']/following-sibling::label")
	private WebElement domainListMsg;
	
	@FindBy(xpath="//select[@id='parentCategoryList']/following-sibling::label")
	private WebElement parentCategoryListMsg;
	
	@FindBy(xpath="//input[@id='user']/following-sibling::label")
	private WebElement userMsg;
	
	@FindBy(xpath="//*[@id='submit']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public UserClosingBalanceSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectZone(String zone){
		Log.info("Trying to select zone: "+zone);
		try{
		Select select = new Select(zoneCode);
		select.selectByVisibleText(zone);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Zone selected successfully.");
	}
	
	public void selectDomain(String domain){
		Log.info("Trying to select zone: "+domain);
		try{
		Select select = new Select(domainList);
		select.selectByVisibleText(domain);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("Zone selected successfully.");
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
	
	public void enterFromAmount(String frAmount){
		Log.info("Trying to enter from Amount: "+frAmount);
		fromAmount.sendKeys(frAmount);
		Log.info("from Amount entered successfully");
	}
	
	public void enterToAmount(String tAmount){
		Log.info("Trying to enter To Amount: "+tAmount);
		toAmount.sendKeys(tAmount);
		Log.info("To Amount entered successfully");
	}
	
	public void clickUserClosingBalancelink() {
		Log.info("Trying to click User Closing Balance link");
		userClosingBalance.click();
		Log.info(" User Closing Balance link clicked successfully");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickResetBtn(){
		Log.info("Trying to click reset button.");
		reset.click();
		Log.info("Reset button clicked successfuly.");
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
		else if(attribute.equalsIgnoreCase("fromAmount")){
			errormessage=fromAmountMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("toAmount")){
			errormessage=toAmountMsg.getText();
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
