package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VomsOrderApproval3Page2 {

    WebDriver driver = null;
	public VomsOrderApproval3Page2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "submitApprv1" )
	private WebElement submitApprv1;
	
	@FindBy(name = "backApprv1" )
	private WebElement backApprv1;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitApprv1.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backApprv1.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	public boolean checkParticularProfileAvailable(String profileName, String mrp) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("(//td[@class='tabcol' and text()= '" + profileName);
		TransferRuleX.append("']/following-sibling::td[@class='tabcol' and text()='" + mrp + "'])/preceding-sibling::td/input");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
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





}
