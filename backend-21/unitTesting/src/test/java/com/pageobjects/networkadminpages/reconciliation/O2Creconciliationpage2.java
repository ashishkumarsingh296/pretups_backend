package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2Creconciliationpage2 {

	WebDriver driver;

	public O2Creconciliationpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "submitButton")
	private WebElement btnSubmit;

	@FindBy(name = "backButton")
	private WebElement btnBack;
	
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
	public boolean checkTransaction(String validationValue) {
		boolean elementDisplayed = false;
		Log.info("Trying to check transactions. ");
		WebElement element = null;
		String xpath = "";
		try{xpath="//td/input[@type='radio']";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed = element.isDisplayed();}
		catch(Exception e){xpath = "//li[contains(text(),'"+validationValue+"')]";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed=false;}
		
		return elementDisplayed;
	}
	
	
	
	public void clickonradioButton(String transactionID) {
		Log.info("Trying to select transaction "+transactionID);
		WebElement element = null;
		String xpath = "";	
		xpath = "//td/a[normalize-space()='"+transactionID+"']/parent::td/preceding-sibling::td/input[@type='radio']";
		
		System.out.println(xpath);
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Transaction selected successfully");
	}
}
