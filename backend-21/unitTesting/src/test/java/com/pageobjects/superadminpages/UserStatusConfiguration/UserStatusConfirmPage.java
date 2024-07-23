package com.pageobjects.superadminpages.UserStatusConfiguration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class UserStatusConfirmPage {
	
	
	@FindBy(name="Confirm")
	private WebElement Confirm;
	
	
	WebDriver driver = null;
	
	public UserStatusConfirmPage(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirm(){
		Confirm.click();
	}
}
