package com.pageobjects.channeluserspages.c2cwithdraw;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CWithdrawConfirmPageSpring {

	@FindBy(id = "submitWithdrawReturn")
	public WebElement confirm;

	@FindBy(name = "withdrawBackSecond")
	private WebElement backButton;

	WebDriver driver = null;

	public C2CWithdrawConfirmPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirm() {
		Log.info("Trying to click Confirm Button");
			WebElement confirmButton = driver.findElement(By.id("submitWithdrawReturn"));
			 ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
			    confirmButton.click();
			    Log.info("User clicked Confirm Button");
		} 
	

	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("User clicked Back Button");
	}
	
}
