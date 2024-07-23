package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_3_Page_2 {

	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "///ul/li")
	private WebElement message;

	WebDriver driver= null;

	public FOC_Approval_3_Page_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectTransactionID(String TransactionID){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td/a[contains(text(),'" + TransactionID + "')]/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransactionID + " clicked successfully");
	}
	
	public String getMessage(){
		Log.info("Trying to fetch Success Message from WEB");
		String Message = message.getText();
		Log.info("Success Message fetched successfully as: " + Message);
		return message.getText();
	}

	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickBackBtn() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
}
