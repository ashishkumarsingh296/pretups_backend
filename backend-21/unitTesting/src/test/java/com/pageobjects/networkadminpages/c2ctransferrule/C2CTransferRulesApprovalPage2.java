package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTransferRulesApprovalPage2 {
	@FindBy(name = "btnApprove")
	public WebElement approveButton;
	
	WebDriver driver= null;

	public C2CTransferRulesApprovalPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickApproveButton() {
		Log.info("Trying to click Approve Button");
		approveButton.click();
		Log.info("Approve Button clicked successfully");
	}
	
	public void PressOkOnConfirmDialog() {
		try{
		Log.info("Alert: "+driver.switchTo().alert().getText());
		Log.info("Trying to click OK on Alert");
		driver.switchTo().alert().accept();
		Log.info("Alert accepted successfully");}
		catch(Exception e){
			Log.info("Not able to accept Alert");
		}
	}
}
