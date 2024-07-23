package com.pageobjects.networkadminpages.promotionaltransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyPromotionalTransferRulePage4 {
	
	WebDriver driver;

	public ModifyPromotionalTransferRulePage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "btnModSubmit")
	private WebElement btnModSubmit;
	
	@FindBy(name = "btnBackModConfirm")
	private WebElement btnBackModConfirm;
	
	public void ClickOnModify() {
		Log.info("Trying to click on Modify Button ");
		btnModSubmit.click();
		Log.info("Clicked on  Modify successfully");
	}
	
	public void ClickOnReset() {
		Log.info("Trying to click on Reset Button ");
		btnBackModConfirm.click();
		Log.info("Clicked on Reset successfully");
	}

}
