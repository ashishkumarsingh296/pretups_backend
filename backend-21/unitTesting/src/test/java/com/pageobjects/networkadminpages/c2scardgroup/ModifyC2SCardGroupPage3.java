package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyC2SCardGroupPage3 {

	@FindBy(name = "addCard")
	private WebElement addCardBtn;

	@FindBy(name = "status")
	private WebElement suspendBtn;
	
	@FindBy(name = "status")
	private WebElement resumeBtn;

	@FindBy(name = "deleteCard")
	private WebElement deleteCardBtn;
	
	@FindBy(name = "reset")
	private WebElement resetBtn;
	
	
	
	WebDriver driver = null;

	public ModifyC2SCardGroupPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickAddBtn() {
		addCardBtn.click();
		Log.info("User clicked add button.");
	}

	public void clickSuspendBtn() {
		suspendBtn.click();
		Log.info("User clicked suspend button.");
	}
	
	public void clickResumeBtn() {
		resumeBtn.click();
		Log.info("User clicked resume button.");
	}
	

	public void clickdeleteBtn() {
		deleteCardBtn.click();
		Log.info("User clicked delete button.");
	}

	public void clickResetBtn() {
		resetBtn.click();
		Log.info("User clicked reset button.");
	}
	
	

}
