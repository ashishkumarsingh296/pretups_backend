package com.pageobjects.channeladminpages.o2cwithdraw;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CWithdrawPage4 {
	@ FindBy(name = "confirmButton")
	private WebElement confirmButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
		
	@ FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
		
	WebDriver driver= null;
	
	public O2CWithdrawPage4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmButton() {
		confirmButton.click();
		Log.info("User clicked confirm Button.");
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}

	public String getMessage(){
		return message.getText();
	}
}
