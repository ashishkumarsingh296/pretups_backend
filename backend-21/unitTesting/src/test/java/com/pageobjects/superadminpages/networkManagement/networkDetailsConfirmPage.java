package com.pageobjects.superadminpages.networkManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class networkDetailsConfirmPage {
	
	@FindBy (name = "confirm")
	private WebElement  confirmButton;

	@FindBy (name = "back")
	private WebElement  backButton;
	
	WebDriver driver;

	public networkDetailsConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirm(){
		Log.info("User trying to click confirm button");

		confirmButton.click();
		Log.info("User clicked confirm button");
	}
	
	


}
