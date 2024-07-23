package com.pageobjects.loginpages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
	
	WebDriver driver;
	@ FindBy(name = "language")
	private WebElement language;

	
	@ FindBy(id = "loginID")
	private WebElement loginID;
	
	@ FindBy(name = "loginID")
	private WebElement loginIDname;
	
	@ FindBy(id = "password")
	private WebElement password;
	
	@ FindBy(name = "password")
	private WebElement passwordByName;

	@ FindBy(name = "submit1")
	private WebElement submitButton;

	@ FindBy(name = "reset1")
	private WebElement resetButton;

	@ FindBy(name = "close1")
	private WebElement closeButton;

	@ FindBy(name = "relogin")
	private WebElement relogin;

	@ FindBy(name = "exit")
	private WebElement exit;
	
	@FindBy(xpath = "//td/div/li")
	private WebElement errorMessage;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectLanguage(String Language) {
		try {
		Log.info("Trying to select Language");
		WebElement element = driver.findElement(By.name("language"));
		Select select = new Select(element);
		select.selectByVisibleText(Language);
		Log.info("Language selected successfully as: "+Language);
		}
		catch(Exception e){ Log.debug("<b>Language Selector Not Found:</b>"); }
	}


	public void enterLoginID(String LoginID) {
		Log.info("Trying to enter Login ID");
		try{

			WebDriverWait wait=new WebDriverWait(driver,10);
			wait.until(ExpectedConditions.visibilityOf(loginID));
			loginID.clear();
		loginID.sendKeys(LoginID);
		}
		catch(Exception e){loginIDname.clear();
			loginIDname.sendKeys(LoginID);	
		}
		
		Log.info("Login ID entered successfully as: "+LoginID);
	}

	public void enterPassword(String Password) {
		Log.info("Trying to enter Password");
		try{password.clear();
		password.sendKeys(Password);
		}
		catch(Exception e){passwordByName.clear();
			passwordByName.sendKeys(Password);	
		}
		Log.info("Password entered successfully as: "+Password);
	}

	public void clickSubmitButton() {
		Log.info("Trying to click Submit Button");
		submitButton.click();
		Log.info("Submit button clicked successfully");
	}

	public void clickResetButton() {
		Log.info("Trying to click Reset Button");
		resetButton.click();
		Log.info("Reset Button clicked successfully");
	}

	public void clickCloseButton() {
		Log.info("Trying to click Close Button");
		closeButton.click();
		Log.info("Close Button clicked successfully");
	}

	public void clickReloginButton() {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try{
			Log.info("Trying to click Relogin Button");
			driver.switchTo().frame(0);
			relogin.click();
			driver.switchTo().frame(0);
			Log.info("Relogin Button clicked successfully");
		}
		catch(Exception e){
			Log.info("Relogin Button Not Found");
			//Log.writeStackTrace(e);
		}

	}

	public void clickExitButton() {
		Log.info("Trying to click Exit Button");
		exit.click();
		Log.info("Exit Button clicked successfully");
	}

	public boolean visibilityOfReloginButton() {
		Log.info("Trying to check if Relogin Button exists");
		boolean result = false;
		if(relogin.isDisplayed())
			Log.info("Relogin Button found");
			result = true;
		return result;
	}

	public String getErrorMessage() {
		String ErrorMessage = null;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try {
			ErrorMessage = errorMessage.getText();
			Log.info("Error Message Found on Login Screen: " + ErrorMessage);
		}
		catch (Exception e) {
			//Log.writeStackTrace(e);
		}
		return ErrorMessage;
	}
}
