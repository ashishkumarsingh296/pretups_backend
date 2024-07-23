package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherExpiry {
	
	WebDriver driver;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHGVEXP001')]]")
    private WebElement changeVoucherExpiry;
	
	
	public VoucherExpiry(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
	
	public void clickChangeVoucherExpiry(){
		Log.info("Trying to click Change voucher expiry link.");
		changeVoucherExpiry.click();
		Log.info("Change voucher expiry link clicked.");
	}
}
