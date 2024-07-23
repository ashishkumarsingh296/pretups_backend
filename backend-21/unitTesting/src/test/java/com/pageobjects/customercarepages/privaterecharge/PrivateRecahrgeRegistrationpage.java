/**
 * 
 */
package com.pageobjects.customercarepages.privaterecharge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class PrivateRecahrgeRegistrationpage {

	@FindBy(name="subscriberMsisdn")
	private WebElement subsMSISDN;
	
	@FindBy(name="subscriberName")
	private WebElement subsName;
	
	@FindBy(name="sidGenerationType")
	private WebElement sidGenType;
	
	@FindBy(xpath="//input[@type='radio'][@value='AUTO']")
	private WebElement genTypeAuto;
	
	@FindBy(xpath="//input[@type='radio'][@value='MANUAL']")
	private WebElement genTypeManual;
	
	@FindBy(name="subscriberSID")
	private WebElement subsSID;
	
	@FindBy(name="submitPrivateReg")
	private WebElement submitBtn;
	
	@FindBy(name="resetPrivateReg")
	private WebElement resetBtn;
	
	@FindBy(name="registerSubscriber")
	private WebElement regSubscriber;
	
	@FindBy(name="backButton")
	private WebElement backBtn;
	
	WebDriver driver=null;
	
	public PrivateRecahrgeRegistrationpage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterSubscriberMSISDN(String MSISDN){
		Log.info("Trying to enter subscriber MSISDN.");
		subsMSISDN.sendKeys(MSISDN);
		Log.info("MSISDN entered as : "+MSISDN);
	}
	
	public void enterSubsriberName(String Name){
		Log.info("Trying to eneter subscriber name.");
		subsName.sendKeys(Name);
		Log.info("Subscriber Name entered as: "+Name);
	}
	
	public void selectTypeOfGeneration(boolean genType){
		if(genType){Log.info("Trying to select generation type: manual.");
			genTypeManual.click();
			Log.info("Generation type selected as manual.");
		}
		else{Log.info("Trying to select generation type: auto.");
		genTypeAuto.click();
		Log.info("Generation type selected as auto.");
		}
	}
	
	public void enterSubsriberSID(String SID){
		Log.info("Trying to enter subscriber SID.");
		subsSID.sendKeys(SID);
		Log.info("Subscriber SID entered as: "+SID);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickResetBtn(){
		Log.info("Trying to click reset button.");
		resetBtn.click();
		Log.info("Reset button clicked successfuly.");
	}
	
	public void clickRegisterBtn(){
		Log.info("Trying to click register button.");
		regSubscriber.click();
		Log.info("Register button clicked successfuly.");
	}
	
	public void clickBackBtn(){
		Log.info("Trying to click back button.");
		backBtn.click();
		Log.info("Back button clicked successfuly.");
	}
}
