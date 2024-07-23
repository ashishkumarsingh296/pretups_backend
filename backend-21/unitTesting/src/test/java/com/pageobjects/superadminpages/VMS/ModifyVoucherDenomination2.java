package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyVoucherDenomination2 {

	WebDriver driver= null;
	public ModifyVoucherDenomination2(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (name ="categoryID")
	private WebElement denominationName;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(name = "modifySubCatSubmit" )
	private WebElement submit;
	
	public void clickSubmit() {
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
	}
	
	public void selectDenominationName(String value)  {
		Log.info("trying to check denomination name dropdown available");
		try {
			Select select = new Select(denominationName);
			select.selectByVisibleText(value);
			Log.info("Denomination name Type selected successfully: " +value);
		}
		catch (Exception e) {
			Log.info("Denomination name drop down was not visible");
			throw e;
		}
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
