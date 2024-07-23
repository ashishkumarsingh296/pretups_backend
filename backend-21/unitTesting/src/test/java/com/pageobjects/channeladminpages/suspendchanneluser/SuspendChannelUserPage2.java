/**
 * 
 */
package com.pageobjects.channeladminpages.suspendchanneluser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import com.utils.SwitchWindow;

/**
 * @author lokesh.kontey
 *
 */
public class SuspendChannelUserPage2 {

	
	@FindBy(name="saveDelete")
	private WebElement submit;
	
	@FindBy(name="back")
	private WebElement backBtn;
	
	@FindBy(name = "searchTextArrayIndexed[0]")
	private WebElement ownerName;

	@FindBy(name = "searchTextArrayIndexed[1]")
	private WebElement parentName;

	@FindBy(xpath = "//input[@type='submit' and @name='submitParent']")
	private WebElement submitParentBtn;

	@FindBy(xpath = "//a[@href [contains(.,'searchParentUser')]]")
	private WebElement searchParentUser;
	
	@FindBy(xpath="//ul/li")
	private WebElement message;
	
	@FindBy(xpath="//ol")
	private WebElement errorMessage;
	
	WebDriver driver=null;
	boolean w1,w2;
	
	public SuspendChannelUserPage2(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickSubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfully.");
	}
	
	public void clickBackBtn(){
		Log.info("Trying to click back button.");
		backBtn.click();
		Log.info("Back button clicked successfully.");
	}
	
	public void enterOwnerUser() {
		try {
			Log.info("Trying to check if Owner User Search field exists");
			w1 = ownerName.isDisplayed();
			} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Owner user search field not found");
		}
	}

	public void selectOwnerName(String UserID) throws InterruptedException {
		if (w1 == true) {
			Log.info("Trying to select owner Name");
			ownerName.sendKeys(UserID);
			Log.info("Owner Name selected successfully");
			SwitchWindow.switchwindow(driver);
			Log.info("Trying to click submit button and return to main window");
			SwitchWindow.backwindow(driver);
			Log.info("Submit button clicked.");
		} else {
			Log.info("Owner User Name link not found");
		}
	}

	public void enterChannelUser() {
		try {
			Log.info("Trying to check if Parent User field exists");
			w2 = parentName.isDisplayed();
			Log.info("Parent User link found");
		} catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Parent user search field not found");
		}
	}

	public void selectChannelUserName(String UserID) throws InterruptedException{
		if (w2 == true) {
			Log.info("Trying to select parent Name");
			parentName.sendKeys(UserID);
			Log.info("Parent Name selected successfully");
			SwitchWindow.switchwindow(driver);
			Log.info("Trying to click submit button and return to main window");
			SwitchWindow.backwindow(driver);
			Log.info("Submit button clicked.");
		} else {
			Log.info("Parent user name not found");
		}
	}

	public void clickPrntSubmitBtn() {
		if (w1 == true) {
			Log.info("Trying to click submit button on search user screen");
			submitParentBtn.click();
			Log.info("Submit button clicked successfully");
		}

	}
	
	public String fetchMessage() {
			
			String fetchedmessage=null;
			try{
			Log.info("Trying to fetch success message.");
			fetchedmessage=message.getText();
			Log.info("Message fetched as :: "+fetchedmessage);
			}
			catch(Exception e){
				Log.info("Success message not found.");
				fetchedmessage=errorMessage.getText();
				Log.writeStackTrace(e);
			}
			return fetchedmessage;
			
		}
	
	
}
