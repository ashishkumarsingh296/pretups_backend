package com.pageobjects.channeluserspages.c2cwithdraw;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class C2CWithdrawUserSearchPageSpring {
	@FindBy(name = "userCode")
	public WebElement mobileNumber;

	@FindBy(name = "toCategoryCode")
	public WebElement categoryCode;

	@FindBy(name = "toUserName")
	public WebElement toUserName;

	@FindBy(name = "submitMsisdn")
	public WebElement submitMsisdn;

	@FindBy(name = "submitUsrSearch")
	public WebElement submitUsrSearch;
	
	@FindBy(xpath="//label[@for='msisdn']")
	private WebElement fieldErrorMSISDN;
	
	
	@FindBy(xpath="//label[@for='toUserName']")
	private WebElement fieldErrorUserName;
	
	@FindBy(xpath="//label[@for='category']")
	private WebElement fieldErrorCategory;
	
	@FindBy(xpath="//a[@href='#collapseTwo']")
	public WebElement byUserName;
	
	@FindBy(xpath="//a[@href='#collapseOne']")
	public WebElement byMobileNumber;
	

	public void clickByUserName(){
		Log.info("Trying to click ByUserName link");
		byUserName.click();
		Log.info("ByUserName link clicked successfuly.");
	}
	public void clickByMobileNumber(){
		Log.info("Trying to click ByMobileNumber link");
		byMobileNumber.click();
		Log.info("ByMobileNumber link clicked successfuly.");
	}
	

	WebDriver driver = null;

	public C2CWithdrawUserSearchPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public String enterMobileNo(String msisdn) {
		Log.info("Trying to enter mobile number"+msisdn);
		mobileNumber.sendKeys(msisdn);
		Log.info("User entered Mobile Number");
		return msisdn;
	}

	public void selectCategoryCode(String Category) {
		Log.info("Trying to select category"+Category);
		Select CategoryCode = new Select(categoryCode);
		CategoryCode.selectByVisibleText(Category);
		Log.info("User selected Category.");
	}

	public void enterToUserName(String ToUser) {
		Log.info("Trying to enter user"+ToUser);
		toUserName.sendKeys(ToUser);
		Log.info("User selected To user: "+ ToUser);
	}

	public void clickSubmitMsisdn() {
		Log.info("Trying to click submit button");
		submitMsisdn.click();
		Log.info("User clicked submit button");
	}
	
	public void clickSubmitSearch() {
		Log.info("Trying to click submit button");
		submitUsrSearch.click();
		Log.info("User clicked submit button");
	}
	
	public String getFieldErrorMSISDN() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorMSISDN.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	
	public String getFieldErrorUserName() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorUserName.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
	public String getFieldErrorCategory() {
		Log.info("Trying to get Field Error");
		String message=fieldErrorCategory.getText();
		Log.info("Error fetched successfuly: "+message);
		return message;
	}
}
