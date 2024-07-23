package com.pageobjects.channeladminpages.barunbar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewBarredListPage {
	

	@FindBy(name = "msisdn")
	private WebElement mobileNum;

	@FindBy(name = "submit")
	private WebElement submitBtn;
		
	WebDriver driver = null;

	public ViewBarredListPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterMobileNumber(String MobileNumber) {
		Log.info("Trying to enter Mobile Number");
		mobileNum.sendKeys(MobileNumber);
		Log.info("Mobile Number entered as: " + MobileNumber);
	}

	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}

	public void checkMsisdnExistinList(String MSISDN,boolean exist){
		Log.info("Searching for msisdn:"+MSISDN);
		if(exist){
			driver.findElement(By.xpath("//td[text()='"+MSISDN+"']"));
			Log.info("MSISDN "+MSISDN+" found, hence,provided user is barred.");}
		else if(!exist){
			try{driver.findElement(By.xpath("//td[text()='"+MSISDN+"']"));}
			catch(Exception e){Log.info("MSISDN not found in barred list and hence,user unbarred");}
			}
	}
}
