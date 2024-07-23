package com.pageobjects.superadminpages.VMS.VoucherExpiry;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChangeVoucherExpiryPage {

	@FindBy(name = "voucherType" )
	private WebElement voucherType;
	
	@FindBy(name = "fromSerial" )
	private WebElement fromSerial;
	
	@FindBy(name = "toSerial" )
	private WebElement toSerial;
	
	@FindBy(name = "totalNoOfVouchStr" )
	private WebElement totalNoOfVouchStr;
	
	@FindBy(name = "expiryDateString" )
	private WebElement expiryDateString;
	
	@FindBy(xpath = "//input[@value='Submit']")
	private WebElement submitBtn;
	
	@FindBy(xpath = "//input[@value='Confirm']")
	private WebElement confirmBtn;
	
	@FindBy(xpath = "//tr[3]/td[4]")
	private WebElement dateformat;
	
	WebDriver driver = null;
	public ChangeVoucherExpiryPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterFromSerialNo(String serialno){
		Log.info("Trying to enter from serial no." +serialno);
		fromSerial.sendKeys(serialno);
		Log.info("From serial no entered successfully");
	}
	
	public void enterToSerialNo(String serialno){
		Log.info("Trying to enter to serial no." +serialno);
		toSerial.sendKeys(serialno);
		Log.info("To serial no entered successfully");
	}
	
	public void enterNoOfVouchers(String noOfvouchers){
		Log.info("Trying to enter no. of vouchers: " +noOfvouchers);
		totalNoOfVouchStr.sendKeys(noOfvouchers);
		Log.info("No. of vouchers entered successfully");
	}
	
	public void enterExpiryDate(String date){
		Log.info("Trying to enter ExpiryDate: " +date);
		expiryDateString.sendKeys(date);
		Log.info("ExpiryDate entered successfully");
	}
	
	public void clickSubmitbutton(){
		Log.info("Trying to click submit btn");
		submitBtn.click();
		Log.info("Submit button clicked.");
	}
	
	public void clickConfirmbutton(){
		Log.info("Trying to click confirm btn");
		confirmBtn.click();
		Log.info("Confirm button clicked.");
	}
	
	public String getDateFormat(){
		Log.info("Trying to get date format from screen.");
		String format  = dateformat.getText().trim();
		Log.info("Format returned as : " + format);
		return format;
	}
}
