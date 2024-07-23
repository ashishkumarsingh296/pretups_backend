package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewVoucherProfile2 {
	
	WebDriver driver = null;
	
	public ViewVoucherProfile2(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (name = "selProForModSubmit")
	private WebElement submitButton;
	
	@FindBy (name ="selectBackForView")
	private WebElement backButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public boolean checkParticularProfileAvailable(String profileName, String mrp) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("(//td[@class='tabcol' and text()= '" + profileName);
		TransferRuleX.append("']/following-sibling::td[@class='tabcol' and text()='" + mrp + "'])/preceding-sibling::td/input");
		try {
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		}
		catch(NoSuchElementException e){
			return false;
		}
		return elementDisplayed;
	}
	
	public void selectRadioButtonProfile(String profileName, String mrp) {
		Log.info("Trying to Click on radio Button");
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("(//td[@class='tabcol' and text()= '" + profileName);
		TransferRuleX.append("']/following-sibling::td[@class='tabcol' and text()='" + mrp + "'])/preceding-sibling::td/input");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		element.click();
		Log.info("Radio Button selected successfully");
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit button clicked successfully");
	}
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back button clicked successfully");
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
