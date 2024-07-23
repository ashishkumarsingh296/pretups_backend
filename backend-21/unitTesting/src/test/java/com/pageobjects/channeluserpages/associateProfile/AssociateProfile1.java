package com.pageobjects.channeluserpages.associateProfile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AssociateProfile1{
	
	
	@FindBy(name = "submitAssociate")
	private WebElement submitAssociate;
	
	@FindBy(name = "searchMsisdn")
	private WebElement searchMsisdn;
	
	@FindBy(name = "searchLoginId")
	private WebElement searchLoginId;
	
	@FindBy(name = "domainCode")
	private WebElement domainCode;
	
	@FindBy(name = "channelCategoryCode")
	private WebElement channelCategoryCode;
	
	@FindBy(name = "parentDomainCode")
	private WebElement parentDomainCode;
	
	@FindBy(name = "user")
	private WebElement user;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	
	WebDriver driver = null;

	public AssociateProfile1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void enterSearchMsisdn(String msisdn){
		Log.info("Trying to enter Search MSISDN");
		searchMsisdn.clear();
		searchMsisdn.sendKeys(msisdn);
		Log.info("Entered Search MSISDN: "+msisdn);
	}
	
	public void clickSubmit(){
		Log.info("Trying to click Submit button for MSISDN");
		submitAssociate.click();
		Log.info("Clicked Submit button for MSISDN");
	}
	
	public void enterSearchLoginId(String loginId){
		Log.info("Trying to enter Search LoginId");
		searchLoginId.clear();
		searchLoginId.sendKeys(loginId);
		Log.info("Entered Search LoginId: "+loginId);
	}
	
	
	

	
	public void enterSearchUser(String userValue){
		Log.info("Trying to enter Search User");
		user.clear();
		user.sendKeys(userValue);
		Log.info("Entered Search User: "+userValue);
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
