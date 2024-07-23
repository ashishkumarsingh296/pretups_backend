package com.pageobjects.networkadminpages.o2ctransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AssociateO2CTransferRulePage1 {
	@FindBy(name = "domainCode")
	public WebElement domain;
	
	@FindBy(name = "submitButton")
	public WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
	
	WebDriver driver= null;

	public AssociateO2CTransferRulePage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void selectDomainName(String domainname) {
		Log.info("Trying to Select Domain Name");
		Select select1 = new Select(domain);
		select1.selectByVisibleText(domainname);
		Log.info("Domain Name selected successfully: "+domainname);
	}
	
	
	public void clickSubmit() {
		Log.info("Trying to click Submit Button");
		submit.click();
		Log.info("Sumit Button clicked successfully");
	}
	
}
