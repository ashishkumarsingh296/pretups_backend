package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewedituserPage2 {
	WebDriver driver;

	public ViewedituserPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "back")
	private WebElement back;

	public void ClickOnback() {
		Log.info("Trying to click on button  Back ");
		back.click();
		Log.info("Clicked on  Back successfully");
	}
}
