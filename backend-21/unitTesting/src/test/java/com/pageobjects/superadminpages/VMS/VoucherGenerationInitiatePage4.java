package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherGenerationInitiatePage4 {

	WebDriver driver = null;
	public VoucherGenerationInitiatePage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "submitOrderInit" )
	private WebElement Confirm;
	
	@FindBy(name = "back" )
	private WebElement back;
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		Confirm.click();
		Log.info("Clicked on Confirm Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		back.click();
		Log.info("Clicked on Back Button successfully");
		}
	


}
