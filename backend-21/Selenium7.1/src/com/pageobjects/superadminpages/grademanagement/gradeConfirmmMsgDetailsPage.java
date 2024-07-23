package com.pageobjects.superadminpages.grademanagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class gradeConfirmmMsgDetailsPage {
	
	@FindBy(xpath="//ul/li")
	private WebElement message;
	
	@FindBy(xpath="//ol/li")
	private WebElement ErrorMessage;
	
	WebDriver driver = null;

	public gradeConfirmmMsgDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public String getMessage() {
		String msg = message.getText();
		Log.info("The message is:" +msg);
		return msg;
	}


	public String getErrorMessage() {
		String msg = ErrorMessage.getText();
		Log.info("The Error message is:" +msg);
		return msg;
	}
	
	
	
}
