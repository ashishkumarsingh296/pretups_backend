package com.pageobjects.networkadminpages.masters;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class Networkstatuspage2 {
	WebDriver driver;

	public Networkstatuspage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "confirm")
	private WebElement confirm;

	@FindBy(name = "cancel")
	private WebElement cancel;

	@FindBy(name = "back")
	private WebElement back;

	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;

	public void ClickOnconfirm() {
		Log.info("Trying to click confirm button");
		confirm.click();
		Log.info("Confirm button clicked successfully");
	}

	public void ClickOncancel() {
		Log.info("Trying to click on cancel button");
		cancel.click();
		Log.info("Cancel Button clicked successfully");
	}

	public void ClickOnback() {
		Log.info("Trying to click back button");
		back.click();
		Log.info("Back button clicked successfully");
	}

	public String getActualMsg() {
		String UIMsg = null;
		UIMessage.isDisplayed();
		UIMsg = UIMessage.getText();
		return UIMsg;
	}

}
