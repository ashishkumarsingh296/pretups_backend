package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class OperatorUsersSubCategories {

	WebDriver driver= null;
	
	@FindBy (xpath = "//a[@href[contains(.,'pageCode=APRLUSR001')]]")
	private WebElement approveOperatorUsers;
	
	
	@FindBy (xpath = "//a[@href[contains(.,'pageCode=ADDUSR001')]]")
	private WebElement addOperatorUsers;
	
	@FindBy (xpath = "//a[@href[contains(.,'pageCode=EDITUSR001')]]")
	private WebElement modifyOperatorUsers;
	
	@FindBy (xpath = "//a[@href[contains(.,'pageCode=VIEWUSR001')]]")
	private WebElement viewOperatorUsers;
	
	@FindBy (xpath = "//a[@href[contains(.,'pageCode=VIEWUSRS01')]]")
	private WebElement viewSelfDetails;
	
	
	public OperatorUsersSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickApproveOperatorUsers() {
		Log.info("Trying to click Operator User Approval Link");
		approveOperatorUsers.click();
		Log.info("Operator User approval link clicked successfully");
	}
	
	public void clickAddOperatorUsers() {
		Log.info("Trying to click add Operator User Link");
		addOperatorUsers.click();
		Log.info("Add Operator User link clicked successfully");
	}
	
	public void clickModifyOperatorUsers() {
		Log.info("Trying to click modify Operator User Link");
		modifyOperatorUsers.click();
		Log.info("Modify Operator User link clicked successfully");
	}
	
	public void clickViewOperatorUsers() {
		Log.info("Trying to click view Operator User Link");
		viewOperatorUsers.click();
		Log.info("View Operator User link clicked successfully");
	}
	
	public void clickViewSelfDetails() {
		Log.info("Trying to click view self details Link");
		viewSelfDetails.click();
		Log.info("View self details link clicked successfully");
	}
}
