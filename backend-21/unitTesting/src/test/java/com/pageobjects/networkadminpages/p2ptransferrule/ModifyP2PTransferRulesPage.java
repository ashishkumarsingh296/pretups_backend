package com.pageobjects.networkadminpages.p2ptransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyP2PTransferRulesPage {
	WebDriver driver;

	public ModifyP2PTransferRulesPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//select[@name[contains(.,'.status')]]")
	private WebElement status;

	@FindBy(xpath = "//select[@name[contains(.,'.cardGroupSetID')]]")
	private WebElement cardGroupSet;

	@FindBy(name = "btnModify")
	private WebElement btnModify;

	@FindBy(name = "btnDeleteRule")
	private WebElement btnDeleteRule;

	@FindBy(name = "btnReset")
	private WebElement btnReset;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	public void clickOnModifyButton() {
		Log.info("Trying to click on Modify");
		btnModify.click();
		Log.info("Clicked on  Modify successfully");
	}

	public void clickOnDeleteButton() {
		Log.info("Trying to click on Delete");
		btnDeleteRule.click();
		Log.info("Clicked on  Delete successfully");
	}

	public void clickOnResetButton() {
		Log.info("Trying to click on Reset");
		btnReset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void clickOnBackButton() {
		Log.info("Trying to click on Back");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}

	public void changeStatus(String Status) {
		Select select1 = new Select(status);
		select1.selectByValue(Status);
		Log.info("User selected Status." + Status);
	}

	public void changeCardGroup(String cardGroup) {
		Select select1 = new Select(cardGroupSet);
		select1.selectByVisibleText(cardGroup);
		Log.info("User selected Card Group." + cardGroup);
	}
}
