package com.pageobjects.networkadminpages.accesscontrolmgmt;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class PinorPasswordhistoryreportpage2 {
	WebDriver driver;

	public PinorPasswordhistoryreportpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
}
