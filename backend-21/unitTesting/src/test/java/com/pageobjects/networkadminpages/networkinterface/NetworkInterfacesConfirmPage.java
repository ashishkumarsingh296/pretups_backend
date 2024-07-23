package com.pageobjects.networkadminpages.networkinterface;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkInterfacesConfirmPage {
	WebDriver driver;

	public NetworkInterfacesConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "confirm")
	private WebElement confirm;

	@FindBy(name = "cancel")
	private WebElement cancel;

	@FindBy(name = "back")
	private WebElement back;

	public void clickOnConfirm() {
		try {
			Log.info("Trying to click on Confirm button ");
			confirm.click();
			Log.info("Clicked on Confirm successfully");
		} catch (Exception e) {
			Log.info("Exception: " + e);
		}
	}

	public void clickOnCancel() {
		Log.info("Trying to click on Cancel button ");
		cancel.click();
		Log.info("Clicked on Cancel successfully");
	}

	public void clickOnBack() {
		Log.info("Trying to click on Back button ");
		back.click();
		Log.info("Clicked on Back successfully");
	}
}
