package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateVoucherO2CPage2 {

    WebDriver driver = null;
	public InitiateVoucherO2CPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "voucherType" )
	private WebElement voucherType;
	
	@FindBy(name = "segment" )
	private WebElement segment;
	
	@FindBy(name = "slabsListIndexed[0].denomination" )
	private WebElement denomination0;
	
	@FindBy(name = "slabsListIndexed[0].fromSerialNo" )
	private WebElement fromSerialNo0;
	
	@FindBy(name = "slabsListIndexed[0].toSerialNo" )
	private WebElement toSerialNo0;
	
	@FindBy(name = "remarks" )
	private WebElement remarks;
	
	@FindBy(name = "submitO2CVoucherButton" )
	private WebElement submitO2CVoucherButton;
	
	@FindBy(name = "resetButton" )
	private WebElement resetButton;
	
	@FindBy(name = "backButton" )
	private WebElement backButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectVoucherType(String value){
		Log.info("Trying to Select Voucher Type");
		Select select = new Select(voucherType);
		select.selectByValue(value);
		Log.info("Voucher Type selected  successfully");
		}
	
	
	public void SelectVoucherSegment(String value){
		Log.info("Trying to Select Voucher Segment");
		try
		{
			Select select = new Select(segment);
			select.selectByValue(value);
			Log.info("Voucher Segment selected  successfully:"+value);
		}
		catch (Exception ex) {
			Log.info("Voucher Segment dropdown not found");
		}
	
		}
	
	public void SelectDenomination(String value){
		Log.info("Trying to Select Denomination");
		try {
		Select select = new Select(denomination0);
		select.selectByVisibleText(value);
		Log.info("Denomaination selected  successfully");
		}
		catch (Exception e) {
			Log.info("Denomination not visible in dropdown");
			throw e;
		}
		}
	
	public void EnterFromSerialNumber(String value){
		Log.info("Trying to enter From Serial Number");
		fromSerialNo0.sendKeys(value);
		Log.info("From Serial Number entered  successfully");
		}
	
	public void EnterToSerialNumber(String value){
		Log.info("Trying to enter To Serial Number");
		toSerialNo0.sendKeys(value);
		Log.info("To Serial Number entered  successfully");
		}
	
	public void EnterRemarks(String value){
		Log.info("Trying to enter Remarks");
		remarks.sendKeys(value);
		Log.info("Remarks entered  successfully");
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitO2CVoucherButton.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonReset(){
		Log.info("Trying to click on Reset Button");
		resetButton.click();
		Log.info("Clicked on Reset Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	
	public String getMessage(){
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
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}



}
