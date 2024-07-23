package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewC2SCardGroupPage2 {
	@ FindBy(name = "view")
	private WebElement submitBtn;

	@ FindBy(name = "back")
	private WebElement backBtn;

	WebDriver driver= null;
	
	public ViewC2SCardGroupPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitButton() {
		submitBtn.click();
		Log.info("User clicked Submit Button.");
	}
	
	public void clickBackButton() {
		backBtn.click();
		Log.info("User clicked Back Button.");
	}
}
