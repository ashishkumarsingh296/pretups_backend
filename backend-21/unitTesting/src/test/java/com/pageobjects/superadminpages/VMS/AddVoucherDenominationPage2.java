package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddVoucherDenominationPage2 {

	WebDriver driver = null;
	public AddVoucherDenominationPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "confirmAddSubCat" )
	private WebElement confirm;
	
	@FindBy(name = "cancelAddSubCat" )
	private WebElement cancel;
	
	@FindBy(name = "backAddSubCat" )
	private WebElement back;
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm button ");
		confirm.click();
		Log.info("Clicked on Confirm successfully");
		}
	
	public void ClickonCancel(){
		Log.info("Trying to click on Cancel button ");
		cancel.click();
		Log.info("Clicked on Cancel successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back button ");
		back.click();
		Log.info("Clicked on Back successfully");
		}
}
