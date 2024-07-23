package com.pageobjects.channeluserspages.channelenquiry;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UserBalanceSpring {
	
WebDriver driver = null;
	
	@FindBy(name = "searchLoginId")
	private WebElement loginID;
	
	@FindBy(name = "searchMsisdn")
	private WebElement msisdn;
	
	@FindBy(name = "submitMsisdn")
	private WebElement submitMsisdn;
	
	@FindBy(name = "submitLoginId")
	private WebElement submitLoginId;

	@FindBy(name = "channelCategoryCode")
	private WebElement channelCategoryCode;
	
	@FindBy(name = "userId")
	private WebElement userId;
	
	@FindBy(name = "submitUser")
	private WebElement submitUser;
	
	
	
	public UserBalanceSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterLoginID(String login) {
		Log.info("Trying to enter Login ID");
		loginID.sendKeys(login);
		Log.info("Login ID entered successfully as " + login);
	}
	
	public void clickSubmitLoginIdButton() {
		Log.info("Trying to click Submit Button For LoginId");
		submitLoginId.click();
		Log.info("Submit Button clicked successfully");
	}
	
	
	public void enterMSISDN(String _msisdn) {
		Log.info("Trying to enter MSISDN");
		msisdn.sendKeys(_msisdn);
		Log.info("MSISDN Entered successfully as: " + _msisdn);
	}
	
	public void clickSubmitMsisdnButton() {
		Log.info("Trying to click Submit Button For Mobile No");
		submitMsisdn.click();
		Log.info("Submit Button clicked successfully");
	}
	

	
	public void enterChannelCategoryCode(String channelCategoryCodeValue) {
		Log.info("Trying to enter channelCategoryCode");
		channelCategoryCode.sendKeys(channelCategoryCodeValue);
		Log.info("channelCategoryCode Entered successfully as: " + channelCategoryCodeValue);
	}

	public void enterUserId(String userIdValue) {
		Log.info("Trying to enter userIdValue");
		userId.sendKeys(userIdValue);
		Log.info("userIdValue Entered successfully as: " + userIdValue);
	}
	
	public void clickSubmitUser() {
		Log.info("Trying to click Submit Button For UserName");
		submitUser.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickPanelTwo() {
		Log.info("Trying to click panel two For loginID");
		driver.findElement(By.xpath("//a[@href='#collapseTwo']")).click();
		Log.info("Panel Two Button clicked successfully");
	}
	
	public void clickPanelThree() {
		Log.info("Trying to click panel three For Category/Domain");
		driver.findElement(By.xpath("//a[@href='#collapseThree']")).click();
		Log.info("Panel three Button clicked successfully");
	}
	
	public String getFieldError(){
        Log.info("Trying to get field error");
        List<WebElement> element = null;
        String xpath = "//label[@class='error']";
        element = driver.findElements(By.xpath(xpath));
        String errorMessage = element.get(0).getText();
        Log.info("Field error: "+errorMessage);
        return errorMessage;
 }
	
	public String getMsisdnFieldError(){
		Log.info("Trying to get MSISDN field error");
		WebElement element = null;
		String xpath = "//label[@for='searchMsisdn']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("MSISDN field error: ");
		return errorMessage;
	}
	
	public String getLoginIdFieldError(){
		Log.info("Trying to get Login ID field error");
		WebElement element = null;
		String xpath = "//label[@for='searchLoginId' and @class='error']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Login ID field error: ");
		return errorMessage;
	}
	
	public String getCategoryFieldError(){
		Log.info("Trying to get Login ID field error");
		WebElement element = null;
		String xpath = "//label[@for='categoryList']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Login ID field error: ");
		return errorMessage;
	}
	
	public String getUserFieldError(){
		Log.info("Trying to get Login ID field error");
		WebElement element = null;
		String xpath = "//label[@for='user']";
		element = driver.findElement(By.xpath(xpath));
		String errorMessage = element.getText();
		Log.info("Login ID field error: ");
		return errorMessage;
	}

	
}
