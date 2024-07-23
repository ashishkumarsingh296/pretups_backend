package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewVoucherProfile {
	
	WebDriver driver = null;
	public ViewVoucherProfile(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy( name = "voucherType")
	private WebElement voucherType;
	
	@FindBy (name = "selectViewForGreater")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void selectVoucherType(String type) throws Exception{
		Log.info("Trying to check if Voucher Type drop down available");
		try {
			Select select = new Select(voucherType);
			select.selectByValue(type);
		}
		catch(Exception e) {
			Log.info("Voucher Type not available in dropdown");
		}
		Log.info("Vocuher Type successfully selected:" +type);
		
	}
	
	public void clickSubmitButton() {
		Log.info("trying to click Submit Button");
		submit.click();
		Log.info("Submit Button Clicked Successfully");
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
