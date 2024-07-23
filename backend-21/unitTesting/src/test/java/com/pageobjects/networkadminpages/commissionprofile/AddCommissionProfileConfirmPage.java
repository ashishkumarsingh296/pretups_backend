package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddCommissionProfileConfirmPage {
	@ FindBy(name = "confirm")
	private WebElement confirmButton;

	@ FindBy(name = "cancel")
	private WebElement cancelButton;

	@ FindBy(name = "back")
	private WebElement backButton;
	
	@FindBy(xpath = "//table/tbody/tr[2]/td[2]/ol/li")
	private WebElement message;
	
	WebDriver driver= null;

	public AddCommissionProfileConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked Confirm Button.");
	}

	public void clickCancelButton() {
		cancelButton.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The Error message is" +msg);
		return msg;
	}
	
}
