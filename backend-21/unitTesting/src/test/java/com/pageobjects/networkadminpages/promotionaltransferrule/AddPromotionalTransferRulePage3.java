package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddPromotionalTransferRulePage3 {
	
	WebDriver driver;

	public AddPromotionalTransferRulePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "btnAddSubmit")
	private WebElement btnAddSubmit;
	
	@FindBy(name = "btnC2SAddCncl")
	private WebElement btnC2SAddCncl;
	
	@FindBy(name = "btnAddBack")
	private WebElement btnAddBack;
	
	public void ClickOnConfirm() {
		Log.info("Trying to click on button  Confirm ");
		btnAddSubmit.click();
		Log.info("Clicked on  Confirm successfully");
	}
	
	public void ClickOnCancel() {
		Log.info("Trying to click on button  Cancel ");
		btnC2SAddCncl.click();
		Log.info("Clicked on  Cancel successfully");
	}
	
	public void ClickOnBack() {
		Log.info("Trying to click on button  Back ");
		btnAddBack.click();
		Log.info("Clicked on  Back successfully");
	}

}
