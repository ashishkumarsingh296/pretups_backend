package com.pageobjects.channeluserspages.c2cwithdraw;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CWithdrawConfirmPage {

	@FindBy(xpath="//input[@name='submitButton'][@value='Confirm']")//(name = "submitButton")
	public WebElement confirm;

	@FindBy(name = "backButton")
	private WebElement backButton;

	WebDriver driver = null;

	public C2CWithdrawConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickConfirm() {
		confirm.click();
		Log.info("User clicked Confirm");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button");
	}

}
