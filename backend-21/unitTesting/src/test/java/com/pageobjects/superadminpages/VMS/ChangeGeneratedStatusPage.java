package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ChangeGeneratedStatusPage {

	WebDriver driver = null;
	public ChangeGeneratedStatusPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "voucherType" )
	private WebElement voucherType;
	
	@FindBy(name = "fromSerial" )
	private WebElement fromSerial;
	
	@FindBy(name = "toSerial" )
	private WebElement toSerial;
	
	@FindBy(name = "totalNoOfVouchStr" )
	private WebElement totalNoOfVouchStr;
	
	@FindBy(name = "mrpStr" )
	private WebElement mrpStr;
	
	@FindBy(name = "productID" )
	private WebElement productID;
	
	@FindBy(name = "voucherStatus" )
	private WebElement voucherStatus;
	
	@FindBy(name = "changeGeneratedStatus" )
	private WebElement changeGeneratedStatus;
	
	@FindBy(name = "confirm")
	private WebElement confirmBtn;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public boolean isVoucherTypeAvailable(){
		Log.info("Trying to check if Voucher Type drop down available");
		if(voucherType.isDisplayed())
		return true;
		else
			return false;
		}
	
	public void SelectVoucherType(String value){
		Log.info("Trying to Select Voucher Type");
		Select select = new Select(voucherType);
		select.selectByValue(value);
		Log.info("Voucher Type selected  successfully as:"+ value);
		}
	
	public void EnterFromSerial(String value){
		Log.info("Trying to enter From Serial Number");
		fromSerial.sendKeys(value);
		Log.info("From Serial Number entered  successfully as:"+ value);
		}
	
	public void EnterToSerial(String value){
		Log.info("Trying to enter To Serial Number");
		toSerial.sendKeys(value);
		Log.info("To Serial Number entered  successfully as:"+ value);
		}
	
	public void EnterNumberOfVouchers(String value){
		Log.info("Trying to enter Number Of Vouchers");
		totalNoOfVouchStr.sendKeys(value);
		Log.info("Number Of Vouchers entered  successfully as:"+ value);
		}
	
	public void EnterMRP(String value){
		Log.info("Trying to enter MRP");
		mrpStr.sendKeys(value);
		Log.info("MRP entered  successfully as:"+ value);
		}
	
	public void SelectProductID(String value){
		Log.info("Trying to Select ProductID");
		Select select = new Select(productID);
		select.selectByVisibleText(value);
		Log.info("ProductID selected  successfully as:"+ value);
		}
	
	public void SelectVoucherStatus(String value){
		Log.info("Trying to Select Voucher Status");
		Select select = new Select(voucherStatus);
		select.selectByValue(value);
		Log.info("Voucher Status selected  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		changeGeneratedStatus.click();
		Log.info("Clicked on Submit Button successfully");
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
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		confirmBtn.click();
		Log.info("Clicked on Confirm Button successfully");
		}
}
