/**
 * 
 */
package com.pageobjects.customercarepages.privaterecharge;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.dbrepository.DBHandler;
import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class PrivateRecahrgeDeactivationpage {
	
	@FindBy(name="subscriberMsisdn")
	private WebElement subsMSISDN;
	
	@FindBy(name="subscriberSID")
	private WebElement subscriberSID;
	
	@FindBy(name="ssubmitDeactivation")
	private WebElement deactivateBtn;
	
	@FindBy(name="confirmSubscriberDelete")
	private WebElement confirmdeactivateBtn;
	
	@FindBy(xpath="//td[text()[contains(.,'Subscriber MSISDN')]]/following-sibling::td[1]")
	
	
	WebDriver driver=null;
	
	public PrivateRecahrgeDeactivationpage(WebDriver driver){
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterSubscriberMSISDN(String msisdn){
		Log.info("Trying to eneter subscriber msisdn.");
		subsMSISDN.clear();
		subsMSISDN.sendKeys(msisdn);
		Log.info("Subscriber MSISDN entered as: "+msisdn);
	}
	
	public void enterSubscriberSID(String msisdn){
		Log.info("Trying to eneter subscriber SID.");
		subscriberSID.clear();
		String actualSID = DBHandler.AccessHandler.getsubscriberSIDviaMSISDN(msisdn);
		if(actualSID!=null)
		subscriberSID.sendKeys(actualSID);
		else
			subscriberSID.sendKeys("null");
		Log.info("Subscriber SID entered as: "+actualSID);
	}
	
	public void clickdeactivatebtn(){
		Log.info("Trying to click deactivate button.");
		deactivateBtn.click();
		Log.info("Deactivate button clicked successfully.");
	}
	
	public void clickconfirmdeactivatebtn(){
		Log.info("Trying to click confirm deactivate button.");
		confirmdeactivateBtn.click();
		Log.info("Confirm deactivate button clicked successfully");
	}
	
}
