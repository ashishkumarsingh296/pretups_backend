package com.pageobjects.networkadminpages.p2ptransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddP2PTransferRulePage2 {
	
	@FindBy(name = "btnAddSubmit")
	WebElement confirm;
	
	@FindBy(name = "btnAddCncl")
	WebElement cancel;
	
	@FindBy(name = "btnAddBack")
	WebElement back;
	
	WebDriver driver= null;

	public AddP2PTransferRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void confirm(){
		confirm.click();
		Log.info("User clicked confirm.");
	}
	
	public void cancel(){
		cancel.click();
		Log.info("User clicked cancel.");
	}
	
	public void back(){
		back.click();
		Log.info("User clicked back.");
	}
	

}
