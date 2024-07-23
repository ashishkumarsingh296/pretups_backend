package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelDomainSubCategories {

	@FindBy(linkText = "Grade management")
	private WebElement gradeManagement;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=DOMAIN001')]]")
	private WebElement channelDomainMgmt;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CATEGORY01')]]")
	private WebElement channelCategoryMgmt;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;

	WebDriver driver = null;

	public void clickGradeManagement() {
		gradeManagement.click();
		Log.info("User clicked Grade Management.");
	}

	public ChannelDomainSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickChannelDomainMgmt(){
		Log.info("Trying to click Channel domain management link");
		channelDomainMgmt.click();
		Log.info("Channel domain management link clicked.");
	}
	
	public String getMessage() {
		Log.info("Trying to fetch Success Message");
		String message1 = message.getText();
		Log.info("Success Message Returned: " + message1);
		return message1;
	}
	
	public void clickChannelCategoryMgmt(){
		Log.info("Trying to click Channel category management link");
		channelCategoryMgmt.click();
		Log.info("Channel category management link clicked.");
	}
	
}
