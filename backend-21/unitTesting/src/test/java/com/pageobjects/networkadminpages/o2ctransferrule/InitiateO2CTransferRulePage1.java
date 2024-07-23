package com.pageobjects.networkadminpages.o2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class InitiateO2CTransferRulePage1 {
	@FindBy(name = "domainCode")
	public WebElement domain;
	
	@FindBy(name = "submitButton")
	public WebElement submit;
	
	WebDriver driver= null;

	public InitiateO2CTransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectDomainName(String domainname) {
		Log.info("Trying to Select Domain Name");
		Select select1 = new Select(domain);
		select1.selectByVisibleText(domainname);
		Log.info("Domain Name selected successfully");
	}
	
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		submit.click();
		Log.info("Sumit Button clicked successfully");
	}
	
}
