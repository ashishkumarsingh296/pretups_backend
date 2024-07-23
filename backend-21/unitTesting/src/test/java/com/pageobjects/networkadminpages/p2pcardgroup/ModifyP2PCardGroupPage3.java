package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyP2PCardGroupPage3 {

	@FindBy(name = "confirm")
	private WebElement confirmBtn;

	@FindBy(name = "cancel")
	private WebElement cancelBtn;
	
	@FindBy(name = "back")
	private WebElement backBtn;
	
	@FindBy(name = "status")
	private WebElement suspendBtn;
	
	@FindBy(name = "status")
	private WebElement resumeBtn;

	@FindBy(name = "deleteCard")
	private WebElement deleteCardBtn;
		
	WebDriver driver = null;

	public ModifyP2PCardGroupPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	
	
	
	public void clickSuspendBtn() {
		suspendBtn.click();
		Log.info("User clicked suspend button.");
	}
	
	public void clickResumeBtn() {
		resumeBtn.click();
		Log.info("User clicked resume button.");
	}

	public void clickConfirmButton() {
		confirmBtn.click();
		Log.info("User clicked Confirm Button.");
	}

	public void clickCancelButton() {
		cancelBtn.click();
		Log.info("User clicked Cancel Button.");
	}

	public void clickBackButton() {
		backBtn.click();
		Log.info("User clicked Back Button.");
	}

}
