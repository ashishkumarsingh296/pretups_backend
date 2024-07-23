package com.pageobjects.channeladminpages.channelreportO2C;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VoucherTransactionReport {

	
	@ FindBy(name = "fromDate")
	private WebElement fromdate;
	
	@ FindBy(xpath = "//input[@type='submit']")
	private WebElement submitButton;
	
	@ FindBy(xpath = "//a[normalize-space(text())='DOWNLOAD VOUCHER AVAILABILITY REPORT']")
	private WebElement link;
	
	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;
	
	WebDriver driver= null;
	
	public VoucherTransactionReport(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterDate(String date) {
		fromdate.sendKeys(date);
		Log.info("User entered Date "+date);
	}

	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickreportLink() {
		link.click();
		Log.info("User clicked Report Link");
	}
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
}
