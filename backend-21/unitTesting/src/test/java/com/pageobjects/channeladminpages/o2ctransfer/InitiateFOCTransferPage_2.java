package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateFOCTransferPage_2 {
	
	@ FindBy(name = "refrenceNum")
	private WebElement refrenceNumber;

	@ FindBy(name = "dataListIndexed[0].requestedQuantity")
	private WebElement quantity;
	
	@ FindBy(name = "remarks")
	private WebElement remarks;
	
	@ FindBy(name="defaultLang")
	private WebElement Language1;
	
	@ FindBy(name="secondLang")
	private WebElement Language2;
		
	@ FindBy(id = "smsPin")
	private WebElement pin;

	@ FindBy(name = "saveProductButton")
	private WebElement submitButton;
	
	@ FindBy(name = "reset")
	private WebElement resetButton;
	
	@ FindBy(name = "back")
	private WebElement backButton;
	
	WebDriver driver= null;
	
	public InitiateFOCTransferPage_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterRefNumber(String RefNumber) {
		Log.info("Trying to enter Reference Number");
		refrenceNumber.sendKeys(RefNumber);
		Log.info("Reference Number entered successfully as: "+RefNumber);
	}
	
	public void enterQuantity(String Quantity) {
		Log.info("Trying to enter Quantity");
		quantity.sendKeys(Quantity);
		Log.info("Quantity entered successfully: " + Quantity);
	}
	
	public void enterQuantitywithname(String Quantity, String productName) {
		String sf1=String.format("//tr//td[text()='%s']/following-sibling::td/input",productName);  
		driver.findElement(By.xpath(sf1)).sendKeys(Quantity);
		Log.info("User entered Quantity: "+Quantity);
	}
	
	public void enterRemarks(String Remarks) {
		Log.info("Trying to enter Remarks");
		remarks.sendKeys(Remarks);
		Log.info("Remarks entered successfully as: "+Remarks);
	}
	
	public void enterPin(String Pin) {
		try {
		Log.info("Trying to enter PIN");
		pin.sendKeys(Pin);
		Log.info("PIN entered successfully as: "+Pin);
		}
		catch (NoSuchElementException e) {
			Log.writeStackTrace(e);
		}
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void clickResetButton() {
		Log.info("Trying to click reset Button");
		resetButton.click();
		Log.info("Reset Button clicked successfully");
	}
	
	public void clickBackButton() {
		Log.info("Trying to click back button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
	
}
