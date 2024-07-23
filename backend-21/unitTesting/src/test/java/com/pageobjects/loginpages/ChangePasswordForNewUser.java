/*
 * 
 */
package com.pageobjects.loginpages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 * This class is created to Change First Time Password of Users.
 */
public class ChangePasswordForNewUser {

	@FindBy(id="oldPassword")
	private WebElement oldPassword;
	
	@FindBy(name ="oldPassword")
	private WebElement oldPassword1;
	
	@FindBy(id="newPassword")
	private WebElement newPassword;
	
	@FindBy(id="confirmNewPassword")
	private WebElement confirmNewPassword;
	
	@FindBy(xpath="//input[@type='submit' and @name='changePassword']")
	private WebElement submitBtn;
	
	
	WebDriver driver=null;
	
	public ChangePasswordForNewUser(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void changePassword(String password, String NewPassword, String ConfirmPassword){
		Log.info("Trying to enter Old Password");	
		try{
			Thread.sleep(2000);
		oldPassword.sendKeys(password);
		Log.info("Old Password Entered successfully:"+password);
		}
		catch(Exception e){
			oldPassword1.sendKeys(password);
			Log.info("Old Password Entered successfully:"+password);	
		}
		Log.info("Trying to enter New Password");
		newPassword.sendKeys(NewPassword);
		Log.info("New Password Entered successfully:"+NewPassword);
		Log.info("Trying to enter Confirm Password");
		confirmNewPassword.sendKeys(ConfirmPassword);
		Log.info("Confirm Password Entered successfully:"+NewPassword);
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}
	
	
}
