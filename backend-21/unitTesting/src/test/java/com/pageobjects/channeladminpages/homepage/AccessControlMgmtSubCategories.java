/**
 * 
 */
package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class AccessControlMgmtSubCategories {

	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2SRSTP001')]]")
	private WebElement userpasswordMgmt;
	
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=PINPWD001')]]")
	private WebElement pinpasswordhistoryReport;
	
	WebDriver driver;
	public AccessControlMgmtSubCategories(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickUserPasswordMgmtLink(){
		Log.info("Trying to click user password management link.");
		userpasswordMgmt.click();
		Log.info("user password management link clicked successfully.");
	}
	
	public void clickPinPasswordHistoryLink(){
		Log.info("Trying to click Pin and Password history report link.");
		pinpasswordhistoryReport.click();
		Log.info("PIN/Password History report link clicked successfully.");
	}
	
}
