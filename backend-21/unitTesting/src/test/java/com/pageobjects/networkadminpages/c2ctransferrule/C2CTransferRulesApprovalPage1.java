package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTransferRulesApprovalPage1 {
	@FindBy(name = "btnSubmit")
	public WebElement submitButton;
	
	WebDriver driver= null;

	public C2CTransferRulesApprovalPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectTransferRuleForApproval(String fromcategory, String tocategory) {
		Log.info("Trying to click on Radio Button for from_category '"+fromcategory+"' and to_category '"+tocategory+"'");
		try{
		driver.findElement(By.xpath("//form//td[2][contains(.,'"+fromcategory+"')]/following-sibling::td[text()='"+tocategory+"']")).click();
		Log.info("Radio Button for from_category '"+fromcategory+"' and to_category "+tocategory+" clicked successfully");
		}
		catch(Exception e){
			Log.info("Elements not found");
		}
		}
	
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Sumit Button clicked successfully");
	}
	

}
