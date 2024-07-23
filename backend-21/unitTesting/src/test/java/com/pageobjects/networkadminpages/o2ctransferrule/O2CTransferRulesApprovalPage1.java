package com.pageobjects.networkadminpages.o2ctransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTransferRulesApprovalPage1 {
	@FindBy(name = "btnSubmit")
	public WebElement submitButton;
	
	WebDriver driver= null;

	public O2CTransferRulesApprovalPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectTransferRuleForApproval(String categoryName) {
		Log.info("Trying to click on Radio Button for "+ categoryName);
		driver.findElement(By.xpath("//tr/td[3][text()='"+ categoryName +"']/preceding::input[@name='radioIndex'][1]")).click();
		Log.info("Radio Button for " + categoryName + " clicked successfully");
	}
	
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Sumit Button clicked successfully");
	}
	

}
