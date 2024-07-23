package com.pageobjects.channeladminpages.channelUserTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ResumeChannelUserHierarchy {

	
	WebDriver driver = null;
	public ResumeChannelUserHierarchy(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "msisdn" )
	private WebElement msisdn;
	
	@FindBy(name = "loginID" )
	private WebElement loginID;
	
	
	
	@FindBy(name = "domainCode" )
	private WebElement domainCode;
	
	@FindBy(name = "ownerName" )
	private WebElement ownerName;
	
	@FindBy(name = "parentCategoryCode" )
	private WebElement parentCategoryCode;
	
	@FindBy(name = "parentUserName" )
	private WebElement parentUserName;
	
	@FindBy(name = "transferUserCategoryCode" )
	private WebElement transferUserCategoryCode;
	
	@FindBy(name = "userTransferMode" )
	private WebElement userTransferMode;
	
	@FindBy(name = "submitButton" )
	private WebElement submitButton;
	
	@FindBy(name = "btnCnf" )
	private WebElement confirmButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void EnterMSISDN(String MSISDN){
		Log.info("Trying to enter MSISDN");
		msisdn.sendKeys(MSISDN);
		Log.info("MSISDN entered  successfully as:"+ MSISDN);
		}
	
	public void EnterLoginID(String loginId){
		Log.info("Trying to enter loginId");
		loginID.sendKeys(loginId);
		Log.info("loginId entered  successfully as:"+ loginId);
		}
	
	
	public void SelectDomain(String domain){
		Log.info("Trying to Select Domain");
		Select select = new Select(domainCode);
		select.selectByValue(domain);
		Log.info("Domain selected  successfully as:"+ domain);
		}
	
	public void EnterOwner(String owner){
		Log.info("Trying to enter Owner");
		ownerName.sendKeys(owner);
		Log.info("Owner entered  successfully as:"+ owner);
		}
	
	public void SelectParentCategoryCode(String parentCode){
		Log.info("Trying to Select parent Category Code");
		Select select = new Select(parentCategoryCode);
		select.selectByValue(parentCode);
		Log.info("Parent Category Code selected  successfully as:"+ parentCode);
		}
	
	public void EnterParentUser(String parentName){
		Log.info("Trying to enter parentName");
		parentUserName.sendKeys(parentName);
		Log.info("parentUserName entered  successfully as:"+ parentName);
		}
	
	public void SelectUserCategory(String userCategory){
		Log.info("Trying to Select user Category");
		Select select = new Select(transferUserCategoryCode);
		select.selectByValue(userCategory);
		Log.info("user Category Code selected  successfully as:"+ userCategory);
		}
	
	public void SelectUserSelectionMode(String userSelectionMode){
		Log.info("Trying to Select User Selection Mode");
		Select select = new Select(userTransferMode);
		select.selectByValue(userSelectionMode);
		Log.info("User Selection Mode selected  successfully as:"+ userSelectionMode);
		}
	
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitButton.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	

	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		confirmButton.click();
		Log.info("Clicked on Confirm Button successfully");
		}
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}



	
}
