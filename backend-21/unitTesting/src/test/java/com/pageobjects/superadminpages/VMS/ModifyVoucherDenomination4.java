package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyVoucherDenomination4 {
	
	WebDriver driver= null;
	public ModifyVoucherDenomination4(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "modifySubCatSubmit" )
	private WebElement submit;
	@FindBy(name = "modifycnfrmCancel" )
	private WebElement cancel;
	@FindBy(name = "backModiSubCat" )
	private WebElement backButton;
	
	public void clickConfirm() {
		Log.info("Trying to click on Confirm button ");
		submit.click();
		Log.info("Clicked on Confirm Button successfully");
	}public void clickCancelButton() {
		Log.info("Trying to click on Cancel button ");
		cancel.click();
		Log.info("Clicked on Cancel Button successfully");
	}public void clickBackButton() {
		Log.info("Trying to click on Back button ");
		backButton.click();
		Log.info("Clicked on Back button successfully");
	}

}
