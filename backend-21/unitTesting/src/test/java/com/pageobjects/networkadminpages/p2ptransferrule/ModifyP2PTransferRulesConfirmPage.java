package com.pageobjects.networkadminpages.p2ptransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyP2PTransferRulesConfirmPage {
	WebDriver driver;

	public ModifyP2PTransferRulesConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "btnModSubmit")
	private WebElement btnModSubmit;

	@FindBy(name = "btnC2SModCncl")
	private WebElement btnC2SModCncl;

	@FindBy(name = "btnModBack")
	private WebElement btnModBack;

	public void clickOnSubmitButton() {
		Log.info("Trying to click on Submit ");
		btnModSubmit.click();
		Log.info("Clicked on  Submit successfully");
	}

	public void clickOnCancelButton() {
		Log.info("Trying to click on Cancel ");
		btnC2SModCncl.click();
		Log.info("Clicked on  Cancel successfully");
	}

	public void clickOnBackButton() {
		Log.info("Trying to click on Back ");
		btnModBack.click();
		Log.info("Clicked on  Back successfully");
	}

}
