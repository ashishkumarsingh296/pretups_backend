package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyActiveProfile2 {
	
	WebDriver driver=null;
	public ModifyActiveProfile2(WebDriver driver) {
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name ="modifyActiveProductBack")
	private WebElement backButton;
	
	@FindBy(name = "modifyActiveProductSubmit")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(name = "vomsActPrdItemVOForDelIndexed[0].checkBoxVal")
	private WebElement checkbox;
	
	
	
	public void clickSubmit() {
		Log.info("Trying to click on Submit button ");
		submit.click();
		Log.info("Clicked on Submit successfully");
	}
	public void clickBackButton() {
		Log.info("Trying to click on back button ");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
	}
	
	public void selectActiveProfile(String profile){
		Log.info("Trying to select Voucher active profile to modify");
		WebElement xPath = driver.findElement(By.xpath("//td[text()='"+profile+"']/preceding-sibling::td/input"));
		xPath.click();
		Log.info("Active Profile selected successfully as:"+ profile);
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
