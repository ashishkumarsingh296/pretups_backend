/**
 * 
 */
package com.pageobjects.channeladminpages.suspendchanneluser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ApproveSuspendChannelUserPage2 {

	@FindBy(xpath="//input[@type='radio'][@value='A']")
	private WebElement approveRadioBtn;
	
	@FindBy(xpath="//input[@type='radio'][@value='R']")
	private WebElement rejectRadioBtn;
	
	@FindBy(xpath="//input[@type='radio'][@value='D']")
	private WebElement discardRadioBtn;
	
	@FindBy(name="saveDeleteSuspend")
	private WebElement submitBtn;
	
	@FindBy(name="back")
	private WebElement backBtn;
	
	@FindBy(name="confirm")
	private WebElement confirmBtn;
	
	WebDriver driver=null;
	
	public ApproveSuspendChannelUserPage2(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectToApprove(){
		Log.info("Trying to select Approve.");
		approveRadioBtn.click();
		Log.info("Approve selected successfully.");
	}
	
	public void selectToReject(){
		Log.info("Trying to select Reject.");
		rejectRadioBtn.click();
		Log.info("Reject selected successfully.");
	}
	
	public void selectToDiscard(){
		Log.info("Trying to select Discard.");
		discardRadioBtn.click();
		Log.info("Discard selected successfully.");
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submitBtn.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public void clickConfirmBtn(){
		Log.info("Trying to click confirm button.");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully.");
	}
}
