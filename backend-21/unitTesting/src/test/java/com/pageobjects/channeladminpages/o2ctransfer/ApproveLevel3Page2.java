package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ApproveLevel3Page2 {

	@ FindBy(name = "submitButton")
	private WebElement submitButton;

	@ FindBy(name = "backButton")
	private WebElement backButton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;

	WebDriver driver= null;

	public ApproveLevel3Page2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectTransferNum(String TransferNumber) {
		Log.info("Trying to click on Radio Button for specific Transaction ID");
		driver.findElement(By.xpath("//tr/td[normalize-space() = '"+ TransferNumber +"']/ancestor::tr/td/input[@type='radio']")).click();
		Log.info("Radio Button for Transaction ID: " + TransferNumber + " clicked successfully");
	}
	
	public String getMessage(){
		return message.getText();
	}

	public void clickSubmitBtn() {
		submitButton.click();
		Log.info("User clicked submit Button.");
	}

	public void clickBackBtn() {
		backButton.click();
		Log.info("User clicked back Button.");
	}
}
