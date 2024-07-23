package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateAutoO2CTransferPageIDEA4 {
	
	@ FindBy(name = "submit1")
	private WebElement Confirm;
	
	@ FindBy(name = "btnCncl")
	private WebElement Cancel;
	
	@ FindBy(name = "btnBack1")
	private WebElement Back;
	
	WebDriver driver= null;
	
	public InitiateAutoO2CTransferPageIDEA4(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirmButton() {
		Confirm.click();
		Log.info("User clicked Confirm button");
	}
	
	public void clickCancelButton() {
		Cancel.click();
		Log.info("User clicked Cancel button");
	}
	
	public void clickBackButton() {
		Back.click();
		Log.info("User clicked Back button");
	}

}
