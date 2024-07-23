package com.pageobjects.channeluserspages.changePin;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChangePinPage {

	
	
	@FindBy(id = "submitPinChange")
	private WebElement submitButton;
	
	@FindBy(xpath = "//p[contains(@class,'alertify-message')]")
	private WebElement alertifyError;
	
	@FindBy(xpath = "//button[@id='alertify-ok']")
	private WebElement alertifyOK;
	
	@FindBy(xpath = "//span[@class='errorClass']")
	private WebElement formMessage;
	
	
	WebDriver driver = null;

	public ChangePinPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickCheckBox(String msisdn){
		Log.info("Trying to click on checkbox corresponding: "+msisdn);
		WebElement element = null;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//../td/input[@type='checkbox']";
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on checkbox corresponding: "+msisdn+" successfully");
	}
	
	public void clickOnSubmitButton(){
		Log.info("Trying to click on Submit button");
		submitButton.click();
		Log.info("Submit button clicked successfully");
	}
	
	public void clickOKButton(){
		Log.info("Trying to click OK button");
		alertifyOK.click();
		Log.info("Clicked OK button");
	}
	
	
	public void enterOldSmsPin(String msisdn, String oldPin){
		Log.info("Trying to enter Old Pin value corresponding: "+msisdn);
		WebElement element = null;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//../td/input[contains(@id,'oldSmsPin')]";
		element = driver.findElement(By.xpath(xpath));
		element.clear();
		element.sendKeys(oldPin);
		Log.info("Entered Old Pin Value corresponding: "+msisdn+" successfully");
	}
	
	public void enterNewSmsPin(String msisdn, String newPin){
		Log.info("Trying to enter New Pin value corresponding: "+msisdn);
		WebElement element = null;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//../td/input[contains(@id,'showSmsPin')]";
		element = driver.findElement(By.xpath(xpath));
		element.clear();
		element.sendKeys(newPin);
		Log.info("Entered Old Pin Value corresponding: "+msisdn+" successfully");
	}
	
	public void enterConfirmSmsPin(String msisdn, String confirmPin){
		Log.info("Trying to enter New Pin value corresponding: "+msisdn);
		WebElement element = null;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//../td/input[contains(@id,'confirmSmsPin')]";
		element = driver.findElement(By.xpath(xpath));
		element.clear();
		element.sendKeys(confirmPin);
		Log.info("Entered Old Pin Value corresponding: "+msisdn+" successfully");
	}
	
	public void enterRemarks(String msisdn, String remarks){
		Log.info("Trying to enter remarks corresponding: "+msisdn);
		WebElement element = null;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//../td/textarea[contains(@name,'eventRemarks')]";
		element = driver.findElement(By.xpath(xpath));
		element.clear();
		element.sendKeys(remarks);
		Log.info("Entered remarks corresponding: "+msisdn+" successfully");
	}
	
	public String getFieldError(){
		Log.info("Trying to get field error");
		List<WebElement> element = null;
		String xpath = "//div[contains(@class,'jqueryError')]";
		element = driver.findElements(By.xpath(xpath));
		String errorMessage = element.get(0).getText();
		Log.info("Field error: "+errorMessage);
		return errorMessage;
	}
	
	public String getAlertifyError(){
		Log.info("Trying to get Alertify error");
		String errorMessage = alertifyError.getText();
		Log.info("Alertify error: "+errorMessage);
		return errorMessage;
	}
	
	public String getFormMessage(){
		Log.info("Trying to get form message");
		String message = formMessage.getText();
		Log.info("Message fetched as: "+message);
		return message;
	}
	

}
