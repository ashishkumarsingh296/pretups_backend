package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2STransferSubCategoriesPage {

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SRECHR01')]]")
	private WebElement c2sRecharge;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=C2SREV001')]]")
	private WebElement c2sReversal;

	WebDriver driver = null;

	public C2STransferSubCategoriesPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickC2SRecharge() {
		c2sRecharge.click();
		Log.info("User clicked C2S Recharge Link.");
	}

	public void clickc2sReversal() {
		c2sReversal.click();
		Log.info("User clicked C2S Reversal Link");
	}

}
