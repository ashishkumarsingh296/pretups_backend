package com.pageobjects.networkadminpages.preferences;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ControlPreferencePage4 {
	
	@FindBy(name="btnCnf")
	private WebElement Confirm;
	
	@FindBy(name="btnCncl")
	private WebElement Cancel;
	
	@FindBy(name="btnBack")
	private WebElement Back;
	
	 WebDriver driver = null;
		
	public ControlPreferencePage4(WebDriver driver){
			this.driver=driver;
			PageFactory.initElements(driver, this);
		}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		Confirm.click();
		Log.info("Confirm button clicked successfuly.");		
	}
	
	public void clickBackBtn(){
		Log.info("Trying to click Back button.");
		Back.click();
		Log.info("Back button clicked successfuly.");		
	}
	
	public void clickCancelBtn(){
		Log.info("Trying to click Cancel button.");
		Cancel.click();
		Log.info("Cancel button clicked successfuly.");		
	}
	
	
	

}
