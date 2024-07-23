package com.pageobjects.superadminpages.serviceClassManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class serviceClassConfirmPage {

	@FindBy(name = "addConf")
	private WebElement confirm;
	
	@FindBy(name = "modifyConf")
	private WebElement modifyConfirm;

	@FindBy(name = "cancel")
	private WebElement cancel;

	@FindBy(name = "addback")
	private WebElement backButton;

	WebDriver driver;

	public serviceClassConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void clickConfirm(){
		Log.info("Trying to click confirm button");
		confirm.click();
		Log.info("Confirm button clicked successfully");
		
	}
	
	public void clickModifyConfirm(){
		Log.info("Trying to click Modify confirm button");
		modifyConfirm.click();
		Log.info("Modify Confirm button clicked successfully");
		
	}
	
	
	public void clickCancel(){
		Log.info("Trying to click cancel button");
		cancel.click();
		Log.info("Cancel button clicked successfully");
		
	}
	
	
	public void clickBack(){
		Log.info("Trying to click back button");
		backButton.click();
		Log.info("Back button clicked successfully");
		
	}
}
