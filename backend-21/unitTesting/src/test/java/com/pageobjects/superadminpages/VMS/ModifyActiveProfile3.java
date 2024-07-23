package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyActiveProfile3 {

	WebDriver driver = null;
	public ModifyActiveProfile3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name ="confirmModifyActiveProduct")
	private WebElement confirmButton;
	
	@FindBy(name = "cancelModifyActiveProduct")
	private WebElement cancel;
	
	@FindBy(name = "backModifyActiveProduct")
	private WebElement backButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	
	public void clickConfirm() {
		Log.info("Trying to click on Confirm button ");
		confirmButton.click();
		Log.info("Clicked on Confirm Button successfully");
	}
	public void clickBackButton() {
		Log.info("Trying to click on back button ");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
	}
	public void clickCancel() {
		Log.info("Trying to click on Cancel button ");
		cancel.click();
		Log.info("Clicked on Cancel successfully");
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
	
	public boolean checkParticularActiveProfileAvailable(String denominationName, String profileName) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//td[@class='tabcol' and text()= '" + denominationName);
		TransferRuleX.append("']/following-sibling::td[@class='tabcol' and text()='" + profileName + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
	}

}
