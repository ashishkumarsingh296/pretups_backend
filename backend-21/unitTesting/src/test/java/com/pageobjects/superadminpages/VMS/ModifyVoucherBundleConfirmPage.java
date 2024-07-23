package com.pageobjects.superadminpages.VMS;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.utils.Log;

//jj
public class ModifyVoucherBundleConfirmPage {
	
	WebDriver driver = null;
	public ModifyVoucherBundleConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	//WebDriverWait wait = new WebDriverWait(driver, 10).until(E);

	@FindBy(name = "confirmAddSubCat")
	WebElement confirm;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	
	
	
	public void clickConfirm() {
		Log.info("Trying to click confirm Submit button");
		confirm.click();
		Log.info("Confirm clicked successfully");
	}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(message));
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(errorMessage));
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}
	
	public void dismissAlert() {
		driver.switchTo().alert().dismiss();
	}

	public void waitDefault() {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	

}
