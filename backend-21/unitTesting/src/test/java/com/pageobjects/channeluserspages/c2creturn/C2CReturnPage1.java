/**
 * 
 */
package com.pageobjects.channeluserspages.c2creturn;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class C2CReturnPage1 {

	@FindBy(name="userCode")
	private WebElement msisdn;
	
	@FindBy(name="toCategoryCode")
	private WebElement categoryName;
	
	@FindBy(name="toUserName")
	private WebElement userName;
	
	@FindBy(name="submitButton")
	private WebElement submitBtn;
	
	@FindBy(name="resetButton")
	private WebElement resetBtn;
	
	WebDriver driver=null;
	
	public C2CReturnPage1(WebDriver driver){
	this.driver=driver;
	PageFactory.initElements(driver, this);
	}
	
	public boolean msisdnVisible=false;
	public void enterMSISDN(String MSISDN){
		try{
			Log.info("Looking for MSISDN textfield.");
			msisdnVisible = msisdn.isDisplayed();}
		catch(Exception e){
			Log.info("MSISDN field not found.");}
		
		if(msisdnVisible){
			Log.info("Trying to enter MSISDN");
			msisdn.sendKeys(MSISDN);
			Log.info("MSISDN entered as : "+MSISDN);}
	}
	
	public void clickSubmitBtn(){
		if(msisdnVisible){
			Log.info("Trying to click submit button.");
			submitBtn.click();
			Log.info("Submit button clicked successfuly.");}
	}
	
}
