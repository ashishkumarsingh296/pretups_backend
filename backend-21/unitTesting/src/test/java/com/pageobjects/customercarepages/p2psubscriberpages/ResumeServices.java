package com.pageobjects.customercarepages.p2psubscriberpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ResumeServices {

	@FindBy(name="msisdn")
	private WebElement msisdn;
	
	@FindBy(name="btnSubmit")
	private WebElement submitBtn;
	
	@FindBy(name="btnCnf")
	private WebElement resumeBtn;
	
	WebDriver driver =null;
	
	public ResumeServices(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String subsMsisdn){
		Log.info("Trying to enter MSISDN: "+subsMsisdn);
		msisdn.sendKeys(subsMsisdn);
		Log.info("MSISDN entered successfully.");
	}
	
	public void clickResumeBtn(){
		Log.info("Trying to click resume button.");
		resumeBtn.click();
		Log.info("Resume button clicked successfully.");
	}
	
	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfully");
	}
	
}
