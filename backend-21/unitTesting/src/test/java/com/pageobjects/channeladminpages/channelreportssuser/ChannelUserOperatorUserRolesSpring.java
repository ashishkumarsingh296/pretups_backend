package com.pageobjects.channeladminpages.channelreportssuser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ChannelUserOperatorUserRolesSpring {
	@FindBy(xpath="//a[@href [contains(.,'pageCode=ROEU001')]]")
	private WebElement externalUserRoleLink;
	
	@FindBy(id="zoneList")
	private WebElement zoneCode;
	
	@FindBy(id="domainList")
	private WebElement domainCode;
	
	@FindBy(id="parentCategoryList")
	private WebElement parentCategoryCode;
	
	@FindBy(id="userName")
	private WebElement userName;
	
	@FindBy(id="userStatus")
	private WebElement userStatus;
	
	@FindBy(id="sortType")
	private WebElement sortType;
	
	@FindBy(id="initiatesummary")
	private WebElement submit;
	
	@FindBy(id="iNETReport")
	private WebElement iNETReport;
	
	@FindBy(xpath="//input[@id='userName']/following-sibling::label")
	private WebElement userNameMsg;
	
	@FindBy(xpath="//input[@id='zoneList']/following-sibling::label")
	private WebElement zoneListMsg;
	@FindBy(xpath="//select[@id='domainList']/following-sibling::label")
	private WebElement domainListMsg;
	@FindBy(xpath="//select[@id='parentCategoryList']/following-sibling::label")
	private WebElement parentCategoryListMsg;
	@FindBy(xpath="//select[@id='userStatus']/following-sibling::label")
	private WebElement userStatusMsg;
	@FindBy(xpath="//select[@id='sortType']/following-sibling::label")
	private WebElement sortTypeMsg;
	
	@FindBy(xpath="//*[@id='initiatesummary' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabled;
	@FindBy(xpath="//*[@id='iNetReport' and @class=' btn btn-primary  enabled']")
	private WebElement iNETReportenabled;
WebDriver driver=null;
	
	public ChannelUserOperatorUserRolesSpring(WebDriver driver){
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
	
	public void clickExternalUsersReportlink() {
		Log.info("Trying to click External Users Report link");
		externalUserRoleLink.click();
		Log.info("External Users Report link clicked successfully");
	}
	

	public void enterUserName(String user){
		Log.info("Trying to enter user name: "+user);
		try{
		userName.clear();
		userName.sendKeys(user);
		}catch(Exception e){
			Log.info("Exception: "+e);
		}
		Log.info("User Name entered successfully");		
	}
 
	
	public void selectuserStatus(String status){
		Log.info("Trying to select domain: "+status);
		Select select = new Select(userStatus);
		select.selectByVisibleText(status);
		Log.info("userStatus selected successfully.");
	}
	
	public void selectsortType(String sort){
		Log.info("Trying to select domain: "+sort);
		Select select = new Select(sortType);
		select.selectByVisibleText(sort);
		Log.info("sortType selected successfully.");
	}
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("userName")){
			errormessage=userNameMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("zoneList")){
			errormessage=zoneListMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("domainList")){
			errormessage=domainListMsg.getText();
		}
		else if(attribute.equalsIgnoreCase("parentCategoryList")){
			errormessage=parentCategoryListMsg.getText();
		}
		
		else if(attribute.equalsIgnoreCase("userStatus")){
			errormessage=userStatusMsg.getText();
		}	
		else if(attribute.equalsIgnoreCase("sortType")){
			errormessage=sortType.getText();
		}
else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	
	
	public void clickInetBtn(){
		Log.info("Trying to click Inet report button.");
		iNETReport.click();
		Log.info("inet report button clicked successfuly.");
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
