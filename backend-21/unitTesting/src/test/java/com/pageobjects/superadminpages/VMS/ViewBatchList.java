package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewBatchList {

	WebDriver driver = null;

	public ViewBatchList(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "userMsisdn")
	private WebElement userMsisdn;

	@FindBy(name = "batchNo")
	private WebElement batchNumber;

	@FindBy(name = "allBatches")
	private WebElement viewBatches;

	@FindBy(name = "voucherStatus")
	private WebElement voucherStatus;

	@FindBy(name = "voucherType")
	private WebElement voucherType;

	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "fromDate")
	private WebElement fromDate;

	@FindBy(name = "submit")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;

	public void enterMsisdn(String value) {
		Log.info("Trying to enter Msisdn");
		try {
			userMsisdn.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Msisdn not entered");
		}
		Log.info("Msisdn updated successfully as: "+value);
	}
	
	public void enterBatchNumber(String value) {
		Log.info("Trying to enter Batch Number");
		try {
			batchNumber.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Batch Number not entered");
		}
		Log.info("Batch Number updated successfully as: "+value);
	}
	
	public void entertoDate(String value) {
		Log.info("Trying to enter To date");
		try {
			toDate.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("To Date Not Entered");
		}
		Log.info("To Date entred successfully as: "+value);
	}
	
	public void enterfromDate(String value) {
		Log.info("Trying to enter From date");
		try {
			fromDate.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("From Date Not Entered");
		}
		Log.info("From Date entred successfully as: "+value);
	}
	
	public void selectViewBatches(String value) {
		Log.info("Trying to Select View Batches");
		Select select = new Select(viewBatches);
		select.selectByValue(value);
		Log.info("View Batches selected as:" + value);
	}

	public void selectViewStatus(String value) {
		Log.info("Trying to Select Voucher Status");
		Select select = new Select(voucherStatus);
		select.selectByValue(value);
		Log.info("Voucher Status selected as:" + value);
	}

	public void selectViewType(String value) {
		Log.info("Trying to Select Voucher Type");
		Select select = new Select(voucherType);
		select.selectByValue(value);
		Log.info("Voucher Type selected as:" + value);
	}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public String getSuccessMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String errormessage =null;
		Log.info("Trying to fetch Message");
		try {
			errormessage =errorMessage.getText();
			Log.info("Error Message fetched successfully as: " + errormessage);
		}
		catch(Exception e){
			Log.info("Error Message not found");
		}
		
		return errormessage;
	}
	
	
	

}
