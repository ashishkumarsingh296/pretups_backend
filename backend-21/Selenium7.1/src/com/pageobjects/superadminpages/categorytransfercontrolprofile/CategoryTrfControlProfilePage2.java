package com.pageobjects.superadminpages.categorytransfercontrolprofile;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class CategoryTrfControlProfilePage2 {

	@FindBy(name = "update")
	private WebElement modifyButton;

	@FindBy(name = "btnDel")
	private WebElement deleteButton;

	@FindBy(name = "back")
	private WebElement backButton;

	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;

	@FindBy(xpath = "//table[3]/tbody/tr/td/form/table/tbody/tr[4]/td[3]")
	private WebElement catProfile;

	WebDriver driver = null;

	public CategoryTrfControlProfilePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickModifyButton() {
		modifyButton.click();
		Log.info("User clicked Modify Button.");
	}

	public void clickDeleteButton() {
		deleteButton.click();
		Log.info("User clicked Delete Button.");
	}

	public void clickBackButton() {
		backButton.click();
		Log.info("User clicked Back Button.");
	}

	public boolean isModifyButtonPresent() {
		boolean result = false;
		try {
			if (modifyButton.isDisplayed())
				result = true;
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}

	public String getMessage() {
		return message.getText();
	}

	public String getCatTCPName() {
		return catProfile.getText();
	}
	
	
	public String getErrorMessage() {
		
		String msg = ErrorMessage.getText();
		
		Log.info("The Error Message is:" +msg);
		return msg;
	}
}
