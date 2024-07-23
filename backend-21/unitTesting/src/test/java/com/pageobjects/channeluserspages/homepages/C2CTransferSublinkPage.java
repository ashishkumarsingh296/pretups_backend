package com.pageobjects.channeluserspages.homepages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTransferSublinkPage {

	@FindBy(xpath = "//table/tbody/tr[3]/td/table[3]/tbody/tr/td/span/a")
	private WebElement c2cTransfer;

	WebDriver driver = null;

	public C2CTransferSublinkPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickC2CTransfer() {
		c2cTransfer.click();
		Log.info("User clicked C2C transfer");
	}

}
