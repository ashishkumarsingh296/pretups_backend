package com.pageobjects.networkadminpages.networkservices;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class NetworkServicesConfirmPage {
	WebDriver driver;

	public NetworkServicesConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	@FindBy(name = "btnCnf")
	private WebElement btnCnf;

	@FindBy(name = "btnCncl")
	private WebElement btnCncl;

	@FindBy(name = "btnBack")
	private WebElement btnBack;

	public void clickOnConfirm() {
		Log.info("Trying to click on Confirm button ");
		btnCnf.click();
		Log.info("Clicked on Confirm button successfully");
	}
	public void ClickOnCancel() {
		Log.info("Trying to click on Cancel button ");
		btnCncl.click();
		Log.info("Clicked on Cancel button successfully");
	}
	public void ClickOnBack() {
		Log.info("Trying to click on Back button");
		btnBack.click();
		Log.info("Clicked on Back button successfully");
	}
	
	public boolean isActive(String network){
		boolean elementDisplayed = false;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + network + "')]/following-sibling::td[contains(text(),'Activate')]";
		elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
		if(elementDisplayed)
			return true;
		else
			return false;
		
	}
	
	public boolean isSuspend(String network){
		boolean elementDisplayed = false;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + network + "')]/following-sibling::td[contains(text(),'Suspend')]";
		elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
		if(elementDisplayed)
			return true;
		else
			return false;
		
	}

}
