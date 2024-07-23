package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherDownloadSubCategories {
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=ENBVODmm')]]")
	private WebElement createBatchForVoucherDownload;

	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VOMSD001')]]")
	private WebElement vomsVoucherDownload;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VOUCHD001')]]")
	private WebElement voucherDownload;
	
	WebDriver driver = null;

	public VoucherDownloadSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickCreateBatchForVoucherDownload() {
		Log.info("Trying to click Create Batch For Voucher Download link");
		createBatchForVoucherDownload.click();
    	Log.info("Add Voucher Denomination link clicked successfully");
	}
	
	public void clickVomsVoucherDownload() {
		Log.info("Trying to click Voms Voucher Download link");
		vomsVoucherDownload.click();
    	Log.info("Voms Voucher Download link clicked successfully");
	}

	public void clickVoucherDownload() {
		Log.info("Trying to click Voucher Download link");
		voucherDownload.click();
    	Log.info("Voucher Download link clicked successfully");
	}


}
