package com.pageobjects.networkadminpages.preferences;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ControlPreferencePage2 {
	
	@FindBy(name="preferenceCode")
	private WebElement preferenceCode;
	
	@FindBy(name="submitButton")
	private WebElement submit;
	
	 WebDriver driver = null;
		
	public ControlPreferencePage2(WebDriver driver){
			this.driver=driver;
			PageFactory.initElements(driver, this);
		}
	
	public void selectControlType(String preference) {
		Log.info("Trying to Select Preference Code");
		Select select1 = new Select(preferenceCode);
		select1.selectByVisibleText(preference);
		Log.info("Preference Code selected successfully");
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click confirm button.");
		submit.click();
		Log.info("Confirm button clicked successfuly.");		
	}

}
