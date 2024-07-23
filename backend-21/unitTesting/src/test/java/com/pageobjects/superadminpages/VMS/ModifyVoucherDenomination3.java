package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyVoucherDenomination3 {
	WebDriver driver= null;
	public ModifyVoucherDenomination3(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (name ="categoryName")
	private WebElement denominationName;
	
	@FindBy (name ="categoryShortName")
	private WebElement shortName;
	
	@FindBy (name ="mrp")
	private WebElement mrp;
	
	@FindBy (name ="payAmount")
	private WebElement payAmount;
	
	@FindBy (name ="description")
	private WebElement description;
	
	@FindBy(name = "modifymrpSubmit" )
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void clickSubmit() {
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
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
	
	public void modifyDenominationName(String value) {
		Log.info("Trying to Modify Denomaintion Name");
		try {
			denominationName.clear();
			denominationName.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Denomination name not modified");
		}
		Log.info("Denomiantion name updated successfully as: "+value);
	}
	
	public void modifyShortName(String value) {
		Log.info("Trying to Modify Short Name");
		try {
			shortName.clear();
			shortName.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("short name not modified");
		}
		Log.info("Short name updated successfully as: "+value);
	}
	
	public void modifyMRP(String value) {
		Log.info("Trying to Modify MRP");
		try {
			mrp.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("MRP not modified");
		}
		Log.info("MRP updated successfully as: "+value);
	}
	
	public void modifyPayableAmount(String value) {
		Log.info("Trying to Modify Payable Amount");
		try {
			payAmount.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Payable Amount not modified");
		}
		Log.info("Payable Amount updated successfully as: "+value);
	}
	
	public void modifyDescription(String value) {
		Log.info("Trying to Modify Description");
		try {
			description.clear();
			description.sendKeys(value);
		}
		catch(Exception e) {
			Log.info("Description not modified");
		}
		Log.info("Description updated successfully as: "+value);
	}	
}
