package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2Preconciliationpage2 {

	WebDriver driver;

	public P2Preconciliationpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	@FindBy(name = "btnBack")
	private WebElement btnBack;
	
	public boolean checkTransaction(String validationValue) {
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		WebElement element = null;
		String xpath = "";
		xpath = "//td[contains(text(),'"+validationValue+"')]";
		try{
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed = element.isDisplayed();}
		catch(Exception e){Log.info(xpath+": not found");};
		return elementDisplayed;
	}
	
	public void clickonradioButton(String transactionID) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";	
		xpath = "//td[contains(text(),'"+transactionID+"')]/preceding::input[@type='radio']";
		
		System.out.println(xpath);
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}
	
	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on Submit successfully");
	}


	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}
}
