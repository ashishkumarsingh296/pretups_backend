package com.pageobjects.channeladminpages.barunbar;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class BarUserPage {
	
	@FindBy(name = "module")
	private WebElement module;

	@FindBy(name = "userType")
	private WebElement userType;
	
	@FindBy(name = "barredType")
	private WebElement barringType;

	@FindBy(name = "msisdn")
	private WebElement mobileNum;

	@FindBy(name = "name")
	private WebElement name;
	
	@FindBy(name = "barredReason")
	private WebElement barredReason;

	@FindBy(name = "submit1")
	private WebElement submitBtn;
	
	@FindBy(name = "confirm")
	private WebElement confirmBtn;
		
	WebDriver driver = null;

	public BarUserPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectModule(String Module) {
		Log.info("Trying to select Module");
		Select select = new Select(module);
		select.selectByValue(Module);
		Log.info("Module selected successfully");
	}

	public void selectUserType(String UserType) {
		Log.info("Trying to select User Type");
		Select select = new Select(userType);
		select.selectByValue(UserType);
		Log.info("User Type selected successfully");
	}

	public void selectBarringType() {
		Log.info("Trying to select Barring Type.");
		Select select = new Select(barringType);
		select.selectByIndex(1);
		Log.info("Barring Type selected successfully");
	}

	public void enterMobileNumber(String MobileNumber) {
		Log.info("Trying to enter Mobile Number");
		mobileNum.sendKeys(MobileNumber);
		Log.info("Mobile Number entered as: " + MobileNumber);
	}

	public void enterName(String Name) {
		Log.info("Trying to enter Name");
		name.sendKeys(Name);
		Log.info("Name entered as: " +Name);
	}

	public void enterBarredReason(String BarredReason) {
		Log.info("Trying to enter Barred Reason");
		barredReason.sendKeys(BarredReason);
		Log.info("Barred Reason entered as: " + BarredReason);
	}

	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}

	public void clickConfirmBtn() {
		Log.info("Trying to click Confirm Button");
		confirmBtn.click();
		Log.info("Confirm Button clicked successfully");
	}
}
