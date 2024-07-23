package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AssociateC2CTransferRulePage1 {
	@FindBy(name = "domainCode")
	public WebElement fromdomain;
	
	@FindBy(name = "toDomainCode")
	public WebElement todomain;
	
	@FindBy(name = "submitButton")
	public WebElement submit;
	
	WebDriver driver= null;

	public AssociateC2CTransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectFromDomainName(String domainname) {
		Log.info("Trying to Select FromDomain Name");
		try {
			Select select1 = new Select(fromdomain);
			select1.selectByVisibleText(domainname);
			Log.info(domainname+" FromDomain Name selected successfully");
		}catch (NoSuchElementException noSuchElementException){
				noSuchElementException.printStackTrace();
				Log.info(domainname+" FromDomain Name selected successfully");
		}
	}
	
	public void selectToDomainName(String domainname) {
		Log.info("Trying to Select ToDomain Name");
		Select select1 = new Select(todomain);
		select1.selectByVisibleText(domainname);
		Log.info("ToDomain Name selected successfully");
	}
	
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		submit.click();
		Log.info("Sumit Button clicked successfully");
	}
	
}
