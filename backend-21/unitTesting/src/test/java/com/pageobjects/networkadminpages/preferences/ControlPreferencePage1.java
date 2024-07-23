package com.pageobjects.networkadminpages.preferences;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ControlPreferencePage1 {

	@FindBy(name="controlCode")
	private WebElement controlCode;
	
	@FindBy(name="submitButton")
	private WebElement submit;
	
    WebDriver driver = null;
	
	public ControlPreferencePage1(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectControlType(String controlType) {
		Log.info("Trying to Select Control Type");
		Select select1 = new Select(controlCode);
		select1.selectByVisibleText(controlType);
		Log.info("Control Type selected successfully");
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click confirm button.");
		submit.click();
		Log.info("Confirm button clicked successfuly.");		
	}
	
	
	
}
