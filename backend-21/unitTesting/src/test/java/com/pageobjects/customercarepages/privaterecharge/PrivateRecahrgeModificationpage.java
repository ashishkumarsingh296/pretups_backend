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
public class PrivateRecahrgeModificationpage {
	
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
	
	@FindBy(name="modifySID")
	private WebElement modifyBtn;
	
	@FindBy(name="submitModifyDetails")
	private WebElement submitmodifyBtn;
	
	@FindBy(name="reset")
	private WebElement resetBtn;
	
	@FindBy(name="back")
	private WebElement backBtn;
	
	@FindBy(name="confrmModify")
	private WebElement confirmModifyBtn;
	
	WebDriver driver=null;
	
	public PrivateRecahrgeModificationpage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterSubscriberMSISDN(String msisdn){
		Log.info("Trying to eneter subscriber msisdn.");
		subsMSISDN.clear();
		subsMSISDN.sendKeys(msisdn);
		Log.info("Subscriber MSISDN entered as: "+msisdn);
	}
	
	public void enterSubsriberName(String Name){
		Log.info("Trying to eneter subscriber name.");
		subsName.clear();
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
		subsSID.clear();
		subsSID.sendKeys(SID);
		Log.info("Subscriber SID entered as: "+SID);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		modifyBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickResetBtn(){
		Log.info("Trying to click reset button.");
		resetBtn.click();
		Log.info("Reset button clicked successfuly.");
	}
	
	
	public void clickSubmitnextBtn(){
		Log.info("Trying to click submit button.");
		submitmodifyBtn.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickRegisterBtn(){
		Log.info("Trying to click register button.");
		confirmModifyBtn.click();
		Log.info("Register button clicked successfuly.");
	}
	
	public void clickBackBtn(){
		Log.info("Trying to click back button.");
		backBtn.click();
		Log.info("Back button clicked successfuly.");
	}
	
	public void clickModifyBtn(){
		Log.info("Trying to click modify button.");
		confirmModifyBtn.click();
		Log.info("Modify button clicked successfuly.");
	}
}
