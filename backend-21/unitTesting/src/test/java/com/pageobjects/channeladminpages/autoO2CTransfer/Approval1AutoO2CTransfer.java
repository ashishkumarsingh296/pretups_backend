package com.pageobjects.channeladminpages.autoO2CTransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class Approval1AutoO2CTransfer {
	
	
	@ FindBy(name = "geographicDomainCode")
	private WebElement geographyDomain;
	
	@ FindBy(name = "domainCode")
	private WebElement domain;

	@ FindBy(name = "categoryCode")
	private WebElement category;
	
	@ FindBy(name = "submitBtnL1")
	private WebElement submitButton;

	@ FindBy(xpath = "resetbutton")
	private WebElement resetbutton;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
    WebDriver driver= null;
	
	public Approval1AutoO2CTransfer(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectGeographyDomain(String GeographyDomain) {
		int size = driver.findElements(By.name("geoDomainCode")).size();
		if(size>1){
		Select select = new Select(geographyDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("User selected Geography Domain." +GeographyDomain);}
		else{Log.info("Only single geography exist and no drop down to select: "+GeographyDomain);}
	}
	public void selectDomain(String Domain) {
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("User selected Domain." +Domain);
	}
	public void selectCategory(String Category) {
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("User selected Category." +Category);
	}
	
	public void clickSubmitButton() {
		submitButton.click();
		Log.info("User clicked Submit button");
	}
	
	public void clickResetButton() {
		resetbutton.click();
		Log.info("User clicked Reset button");
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
		Log.info("Error Message fetched successfully");
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	

}
