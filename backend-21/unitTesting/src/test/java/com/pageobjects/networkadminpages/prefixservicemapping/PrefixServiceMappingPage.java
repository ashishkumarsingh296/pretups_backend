package com.pageobjects.networkadminpages.prefixservicemapping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class PrefixServiceMappingPage {

	WebDriver driver;

	public PrefixServiceMappingPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "prepaidSeries")
	private WebElement prepaidSeries;

	@FindBy(name = "postpaidSeries")
	private WebElement postpaidSeries;

	@FindBy(name = "save")
	private WebElement save;

	@FindBy(name = "reset")
	private WebElement reset;

	public String getPrefixData(String prefixType) {
		String data = null;
		if (prefixType.equals("Postpaid"))
			data = postpaidSeries.getText();
		if (prefixType.equals("Prepaid"))
			data = prepaidSeries.getText();
		return data;
	}

	public void clickSaveButton() {
		save.click();
		Log.info("User clicked Save Button.");
	}

	public void clickResetButton() {
		reset.click();
		Log.info("User clicked Reset Button.");
	}

}
