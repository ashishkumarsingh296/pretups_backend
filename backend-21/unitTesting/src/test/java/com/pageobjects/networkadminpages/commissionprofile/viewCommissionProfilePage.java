package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class viewCommissionProfilePage {
	
	@ FindBy(name = "selectCommProfileSetID")
	private WebElement commissionProfileSet;
	
	
	@ FindBy(name ="numberOfDays")
	private WebElement numberOfDays;

	@ FindBy(name = "selectCommProifleVersionID")
	private WebElement version;
	
	
	@ FindBy(name = "view")
	private WebElement submit;
	
	@ FindBy(name = "back")
	private WebElement backButton;
	
	WebDriver driver = null;
	
	public viewCommissionProfilePage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
		
	}
	
	
	
	public void selectCommissionProfileSet(String CommissionProfileSet) {
		Select select = new Select(commissionProfileSet);
		select.selectByVisibleText(CommissionProfileSet);
		Log.info("User selected Commission Profile Set.");
	}
	
	public void selectNoOfDays(String noOfDays) throws InterruptedException {
		
		numberOfDays.clear();
		numberOfDays.sendKeys(noOfDays);
	
		Log.info("User entered numberOfDays.");
	}
	
	public void clickSubmit(){
		submit.click();
		Log.info("User clicked Submit button");
		
	}
	

}
