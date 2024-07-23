package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class FOC_Approval_2_Page_2 {

	@ FindBy(name = "submit")
	private WebElement submitButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;

	WebDriver driver= null;

	public FOC_Approval_2_Page_2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectTransactionID(String TransactionID){
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td/a[contains(text(),'" + TransactionID + "')]/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransactionID + " clicked successfully");
	}
	
	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public String getMessage(){
		Log.info("Trying to get Sucess Message from WEB");
		String Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		return Message;
	}

	public void clickBackBtn() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("Back Button clicked successfully");
	}
}
