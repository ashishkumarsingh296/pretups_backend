package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VoucherBurnRateIndicator {

	WebDriver driver = null;
	public VoucherBurnRateIndicator(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "categoryCodeForProduct" )
	private WebElement categoryCodeForProduct;
	
	@FindBy(name = "productID" )
	private WebElement productID;
	
	@FindBy(name = "distributedFromDate" )
	private WebElement distributedFromDate;
	
	@FindBy(name = "distributedToDate" )
	private WebElement distributedToDate;
	
	@FindBy(name = "consumedFromDate" )
	private WebElement consumedFromDate;
	
	@FindBy(name = "consumedToDate" )
	private WebElement consumedToDate;
	
	@FindBy(name = "burnRateSubmit" )
	private WebElement burnRateSubmit;
	
	@ FindBy(xpath = "//td[@class='tabcol' and contains(text(),'Voucher Profile')]/following-sibling::td")
	private WebElement voucherProfile;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectVoucherDenomination(String value){
		Log.info("Trying to Select Denomination");
		Select select = new Select(categoryCodeForProduct);
		select.selectByVisibleText(value);
		Log.info("Denomination selected  successfully as:"+ value);
		}
	
	public void SelectVoucherProfile(String value){
		Log.info("Trying to Select Voucher Profile");
		Select select = new Select(productID);
		select.selectByVisibleText(value);
		Log.info("Voucher Profile selected  successfully as:"+ value);
		}
	
	public void EnterDistributedFromDate(String value){
		Log.info("Trying to enter Distributed From Date");
		distributedFromDate.sendKeys(value);
		Log.info("Distributed From Date entered  successfully as:"+ value);
		}
	
	public void EnterDistributedToDate(String value){
		Log.info("Trying to enter Distributed To Date");
		distributedToDate.sendKeys(value);
		Log.info("Distributed To Date entered  successfully as:"+ value);
		}
	
	public void EnterConsumedFromDate(String value){
		Log.info("Trying to enter Consumed From Date");
		consumedFromDate.sendKeys(value);
		Log.info("Consumed From Date entered  successfully as:"+ value);
		}
	
	public void EnterConsumedToDate(String value){
		Log.info("Trying to enter Consumed To Date");
		consumedToDate.sendKeys(value);
		Log.info("Consumed To Date entered  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit button ");
		burnRateSubmit.click();
		Log.info("Clicked on Submit successfully");
		}
	
	public String fetchDisplayedProfile() {
		String profile = voucherProfile.getText();
		Log.info("Displayed Voucher Profile: "+profile);
		return profile;
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
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	
	
	
}
