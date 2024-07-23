package com.pageobjects.channeladminpages.barunbar;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class UnBarUserPage {

	@FindBy(name = "module")
	private WebElement module;

	@FindBy(name = "userType")
	private WebElement userType;
	
	@FindBy(name = "msisdn")
	private WebElement mobileNum;

	@FindBy(name = "barredReason")
	private WebElement unBarredReason;

	@FindBy(name = "submit1")
	private WebElement submitBtn;
	
	WebDriver driver = null;

	public UnBarUserPage(WebDriver driver) {
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

	public void enterMobileNumber(String MobileNumber) {
		Log.info("Trying to enter Mobile Number");
		mobileNum.sendKeys(MobileNumber);
		Log.info("Mobile Number entered as: " + MobileNumber);
	}

	public void enterBarredReason(String UnBarredReason) {
		Log.info("Trying to enter Un-Barred Reason");
		unBarredReason.sendKeys(UnBarredReason);
		Log.info("Un-Barred Reason entered as: " + UnBarredReason);
	}

	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}

}
