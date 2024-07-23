package com.pageobjects.superadminpages.divisionmanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class modifyDivisionPage {
	
	
	@ FindBy(name = "divDeptName")
	private WebElement divisionName;

	@ FindBy(name = "divDeptShortCode")
	private WebElement divisionShortCode;
	
	@ FindBy(name = "status")
	private WebElement status;

	@ FindBy(name = "modifyConfirm")
	private WebElement submitButton;

	@ FindBy(name = "reset")
	private WebElement resetButton;
	
	@ FindBy(name = "backFrmModify")
	private WebElement backButton;
	
	@ FindBy(name = "confirm")
	private WebElement confirm;
	
	WebDriver driver= null;
	public modifyDivisionPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back button clicked successfully");
	}
	
	public void clickResetButton() {
		Log.info("Trying to click Reset Button");
		resetButton.click();
		Log.info("Reset Button clicked successfully");
	}
	
	public void clickConfirmButton() {
		Log.info("Trying to click confirm Button");
		confirm.click();
		Log.info("Confirm Button clicked successfully");
	}
	
	
	
	public void selectStatus(String Status) {
		Log.info("Trying to select Status");
		Select select = new Select(status);
		select.selectByValue(Status);
		Log.info("Status selected successfully");
	}
	
	public void enterDivisionName(String DivisionName) {
		Log.info("Trying to enter Division Name");
		divisionName.sendKeys(DivisionName);
		Log.info("Division Name entered as: "+DivisionName);
	}
	
	public void enterDivisionShortCode(String DivisionShortCode) {
		Log.info("Trying to enter Division Short Code");
		divisionShortCode.sendKeys(DivisionShortCode);
		Log.info("Division Short Code entered as: "+DivisionShortCode);
	}

}
