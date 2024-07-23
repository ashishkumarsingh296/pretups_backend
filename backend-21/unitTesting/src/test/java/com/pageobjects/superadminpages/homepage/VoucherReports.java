package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherReports {
	WebDriver driver;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMBRATE001')]]")
    private WebElement voucherBurnRateIndicator;
	
	  public VoucherReports(WebDriver driver) {
	    	this.driver = driver;
	    	PageFactory.initElements(driver, this);
	        }
	  
	  public void clickVoucherBurnRateIndicator() {
	    	Log.info("Trying to click Voucher Burn Rate Indicator link");
	    	voucherBurnRateIndicator.click();
	    	Log.info("Voucher Burn Rate Indicator link clicked successfully");
	        }
}
