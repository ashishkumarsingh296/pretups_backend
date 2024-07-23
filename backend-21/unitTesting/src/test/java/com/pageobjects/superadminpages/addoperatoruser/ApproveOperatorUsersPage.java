package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ApproveOperatorUsersPage {

	@FindBy(name = "searchLoginId")
	private WebElement searchLoginId;

	@FindBy(name = "aprlSubmit")
	private WebElement aprlSubmitBtn;

	@FindBy(name = "ok")
	private WebElement okSubmitBtn;

	@FindBy(name = "save")
	private WebElement ApproveBtn;

	@FindBy(name = "confirm")
	private WebElement confirmBtn;

	@FindBy(name = "reject")
	private WebElement RejectBtn;

	@FindBy(name = "reset")
	private WebElement ResetBtn;

	@FindBy(name = "back")
	private WebElement BackBtn;
	
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

	WebDriver driver = null;

	public ApproveOperatorUsersPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterLoginID(String LoginID) {
		Log.info("Trying to Enter intiated Login ID: " + LoginID);
		searchLoginId.sendKeys(LoginID);
		Log.info("Login ID entered successfully");
	}

	public void clickaprlSubmitBtn() {
		Log.info("Trying to click approval submit button");
		aprlSubmitBtn.click();
		Log.info("First Submit button clicked successfully");
	}

	public void clickOkSubmitBtn() {
		Log.info("Trying to click Submit button");
		okSubmitBtn.click();
		Log.info("Second Submit button clicked successfully");
	}

	public void approveBtn() {
		Log.info("Tring to click Approve button");
		ApproveBtn.click();
		Log.info("Approve button clicked successfully");
	}
	
	public void rejectBtn() {
		Log.info("Tring to click Reject button");
		RejectBtn.click();
		Log.info("Reject button clicked successfully");
	}

	public void confirmBtn() {
		Log.info("Tring to click Confirm button");
		confirmBtn.click();
		Log.info("Confirm button clicked successfully");
	}

}
