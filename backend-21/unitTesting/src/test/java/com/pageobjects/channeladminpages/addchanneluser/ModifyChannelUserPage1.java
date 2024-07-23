/**
 * 
 */
package com.pageobjects.channeladminpages.addchanneluser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;


/**
 * @author lokesh.kontey
 *
 */
public class ModifyChannelUserPage1 {

	@FindBy(name="searchLoginId")
	private WebElement LoginID;
	
	@FindBy(name="submit1")
	private WebElement submitBtn;
	
	@FindBy(name="searchMsisdn")
	private WebElement MSISDN;
	
	WebDriver driver = null;
	
	public ModifyChannelUserPage1(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterMSISDN(String msisdn){
		Log.info("Trying to enter MSISDN.");
		MSISDN.sendKeys(msisdn);
		Log.info("MSISDN entered successfuly."+msisdn);
	}
	
	public void enterLoginID(String loginid){
		Log.info("Trying to enter LoginID.");
		LoginID.sendKeys(loginid);
		Log.info("MSISDN entered successfuly."+loginid);
	}
	
	public void clickSubmitButton(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfully.");
	}
}
