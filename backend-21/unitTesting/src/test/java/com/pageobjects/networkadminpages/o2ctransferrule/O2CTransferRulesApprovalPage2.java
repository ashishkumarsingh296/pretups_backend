package com.pageobjects.networkadminpages.o2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTransferRulesApprovalPage2 {
	@FindBy(name = "btnApprove")
	public WebElement approveButton;
	
	@FindBy(xpath = "//ul/li")
	public WebElement SuccessMessage;
	
	WebDriver driver= null;

	public O2CTransferRulesApprovalPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickApproveButton() {
		Log.info("Trying to click Submit Button");
		approveButton.click();
		Log.info("Sumit Button clicked successfully");
	}
	
	public String getSuccessMessage() {
		String Message;
		Log.info("Trying to fetch Success Message");
		Message = SuccessMessage.getText();
		Log.info("Success Message found as: " + SuccessMessage);
		return Message;
	}
	
	public void PressOkOnConfirmDialog() {
		Log.info("Alert: "+driver.switchTo().alert().getText());
		Log.info("Trying to click OK on Alert");
		driver.switchTo().alert().accept();
		Log.info("Alert accepted successfully");
	}
}
