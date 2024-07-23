package com.pageobjects.channeladminpages.voucherdownload;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherDownloadPage2 {
	
	@FindBy(id = "screenLock")
	private WebElement pageHeading;
	
	WebDriver driver = null;

	public VoucherDownloadPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public String getPageHeading(){
		Log.info("Trying to fetch view page heading");
		WebElement element = null;
		
		element=pageHeading;
		String heading = element.getText();
		Log.info("View Page heading : "+heading);
		return heading;
	}
}
