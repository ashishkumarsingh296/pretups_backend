package com.pageobjects.networkadminpages.c2stransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AddC2STransferRulePage2 {

	@FindBy(name = "btnAddSubmit")
	WebElement confirm;

	@FindBy(name = "btnC2SAddCncl")
	WebElement cancel;

	@FindBy(name = "btnAddBack")
	WebElement back;
	
	@FindBy(xpath = "//tr/td/ul/li")
	WebElement UIMessage;
	
	WebDriver driver = null;

	public AddC2STransferRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void confirm() {
		confirm.click();
		Log.info("User clicked confirm.");
	}

	public void cancel() {
		cancel.click();
		Log.info("User clicked cancel.");
	}

	public void back() {
		back.click();
		Log.info("User clicked back.");
	}

	public String getActualMsg() {

		String UIMsg = null;
		UIMessage.isDisplayed();
		UIMsg = UIMessage.getText();
		return UIMsg;
	}
}
