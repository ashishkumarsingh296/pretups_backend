package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class VoucherBundleManagement {
	
	WebDriver driver = null;
	public VoucherBundleManagement(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(id = "VMADVB001VOUBUNDLE")
	WebElement addVoucherBundle;
	
	@FindBy(id = "VMMOVB001VOUBUNDLE")
	WebElement modifyVoucherBundle;
	
	@FindBy(id = "VMVWVB001VOUBUNDLE")
	WebElement viewVoucherBundle;
	
	public void clickAddVoucherBundle() {
		Log.info("Trying to click Add Voucher Bundle");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(addVoucherBundle));
		addVoucherBundle.click();
		Log.info("Clicked Add Voucher Bundle successfully");
	}
	
	public void clickModifyVoucherBundle() {
		Log.info("Trying to click Add Voucher Bundle");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(modifyVoucherBundle));
		modifyVoucherBundle.click();
		Log.info("Clicked Add Voucher Bundle successfully");
	}
	
	public void clickViewVoucherBundle() {
		Log.info("Trying to click Add Voucher Bundle");
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(viewVoucherBundle));
		viewVoucherBundle.click();
		Log.info("Clicked Add Voucher Bundle successfully");
	}
}
