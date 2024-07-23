package com.pageobjects.networkadminpages.c2stransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyC2STransferRulePage2 {

	WebDriver driver;
	String MasterSheetPath;

	public ModifyC2STransferRulePage2(WebDriver driver) {
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

	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
	public void clickOnbtnMod() {
		Log.info("Trying to click on Modify");
		btnModify.click();
		Log.info("Clicked on  Modify successfully");
	}

	public void clickOnbtnDelete() {
		Log.info("Trying to click on Delete");
		btnDeleteRule.click();
		Log.info("Clicked on  Delete successfully");
	}

	public void clickOnbtnReset() {
		Log.info("Trying to click on Reset");
		btnReset.click();
		Log.info("Clicked on  Reset successfully");
	}

	public void clickOnbtnBack() {
		Log.info("Trying to click on Back");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}

	public void changedStatus(String Status) {
		Select select1 = new Select(status);
		select1.selectByValue(Status);
		Log.info("User selected Status.");
	}

	public void changedCardGroup(String cardGroup) {
		Log.info("Trying to Select C2S Card Group: " + cardGroup);
		Select select1 = new Select(cardGroupSet);
		select1.selectByVisibleText(cardGroup);
		Log.info("Card Group " + cardGroup + " selected successfully.");
	}
	
	public void clickOK() {
		driver.switchTo().alert().accept();
		Log.info("Clicked on OK Button in Alert.");
	}

	
}
