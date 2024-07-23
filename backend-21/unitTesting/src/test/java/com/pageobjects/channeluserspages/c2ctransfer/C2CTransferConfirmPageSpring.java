package com.pageobjects.channeluserspages.c2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTransferConfirmPageSpring {
	
	@FindBy(id="confirmtransfer")
	public WebElement confirmbutton;
	
	@FindBy(id="resetNetworkStatus")
	public WebElement backButton;

	WebDriver driver = null;
	

	public C2CTransferConfirmPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirm() {
		//Actions actions = new Actions(driver);
		Log.info("User trying to click confirm button");
		WebElement confirmButton = driver.findElement(By.id("confirmtransfer"));
		 ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
		    confirmButton.click();
	Log.info("User clicked confirm button");
	}

	public void clickBackButton() {
		Log.info("User trying to click back button");
		backButton.click();
		Log.info("User clicked Back Button");
	}
}
