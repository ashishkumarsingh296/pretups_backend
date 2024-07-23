package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyVoucherDenomination {

	WebDriver driver = null;
	public ModifyVoucherDenomination(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy (name = "voucherType")
	private WebElement voucherType;
	
	@FindBy (name ="service")
	private WebElement serviceType;
	
	@FindBy (name ="type")
	private WebElement subServiceType;
	
	@FindBy(name = "modifySubCatSubmit" )
	private WebElement submit;
	
	public void clickSubmit() {
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
	}
	
	
	public void selectVoucherType(String value) {
		Log.info("Trying to check if Voucher Type drop down available");
		try {
			Select select = new Select(voucherType);
			select.selectByValue(value);
		}
		catch(Exception ex) {
			Log.info("Voucher Type dropdown not found");
		}
		Log.info("Voucher Type selected  successfully:" +value);
	}
	
	
	public void selectService(String value) {
		Log.info("Trying to check service type drop down available");
		try {
			Select select = new Select(serviceType);
			select.selectByValue(value);
		}
		catch(Exception e) {
			Log.info("Service type dropdown not found");
		}
		
		Log.info("Service Type selected successfully: "+value);
	}
	
	public void selectSubService(String value) {
		Log.info("Trying to check sub service type drop down available");
		try {
			Select select = new Select(subServiceType);
			select.selectByValue(value);
		}
		catch (Exception e) {
			Log.info("Sub Service drop down was not visible");
		}
		Log.info("Sub Service Type selected successfully: " +value);
		
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
