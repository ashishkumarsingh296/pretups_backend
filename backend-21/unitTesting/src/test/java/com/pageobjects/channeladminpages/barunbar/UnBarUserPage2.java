package com.pageobjects.channeladminpages.barunbar;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class UnBarUserPage2 {

	@FindBy(name = "btnUnBar")
	private WebElement submitBtn;
	
	@FindBy(name="back")
	private WebElement backBtn;

	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
	@FindBy(name="unbarAll")
	private WebElement unbarALLchkBox;
	
	WebDriver driver = null;

	public UnBarUserPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void checkUnBarALLUser(){
		Log.info("User is trying to click Unbar ALL checkbox");
		unbarALLchkBox.click();
		Log.info("User clicked Unbar ALL checkbox");
	}
	
	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickBackBtn() {
		Log.info("Trying to click Back Button");
		backBtn.click();
		Log.info("Back Button clicked successfully");
	}

	public void clickConfirmBtn() {
		Log.info("Trying to click Confirm Button");
		confirmBtn.click();
		Log.info("Confirm Button clicked successfully");
	}
}
