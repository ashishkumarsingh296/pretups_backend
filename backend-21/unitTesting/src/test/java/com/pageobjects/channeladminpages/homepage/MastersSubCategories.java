package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class MastersSubCategories {
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=BAR01')]]")
	private WebElement barUser;

	@FindBy(xpath = "//a[@href [contains(.,'pageCode=UNBAR01')]]")
	private WebElement unBarUser;
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=VIEWBAR01')]]")
	private WebElement viewBarredList;
	
	WebDriver driver = null;

	public MastersSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickBarUser() {
		Log.info("Trying to click Bar User link");
		barUser.click();
		Log.info("Bar User link clicked successfully");
	}
	
	public void clickUnBarUser() {
		Log.info("Trying to click Un-Bar User link");
		unBarUser.click();
		Log.info("Un-Bar User link clicked successfully");
}

	public void clickViewBarredlist() {
		Log.info("Trying to click View Barred list link");
		viewBarredList.click();
		Log.info("View Barred list link clicked successfully");
	}
}
