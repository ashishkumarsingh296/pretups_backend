package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyActiveProfile {

	WebDriver driver=null;
	public ModifyActiveProfile(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name ="activeProductID")
	private WebElement activeProfileDropDown;
	
	@FindBy(name = "selAppFromSubmit")
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
	
	public void selectActiveProfile(String value) {
		Log.info("Trying to Select Active Profile");
		try {
		Select select = new Select(activeProfileDropDown);
		select.selectByVisibleText(value);
		}
		catch (Exception ex) {
			Log.info("Active Profile dropdown not found");
		}
		Log.info("Active Profile selected  successfully as:"+ value);
	}
	
}
