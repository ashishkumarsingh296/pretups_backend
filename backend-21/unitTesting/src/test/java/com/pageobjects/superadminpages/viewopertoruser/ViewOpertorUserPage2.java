package com.pageobjects.superadminpages.viewopertoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewOpertorUserPage2 {
	
	WebDriver driver = null;
	public ViewOpertorUserPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "back" )
	private WebElement back;
	

	public void clickonback(){
	Log.info("Trying to click on button  back ");
	back.click();
	Log.info("Clicked on  back button successfully");
	}

}
