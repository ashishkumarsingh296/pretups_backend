package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class FOC_Approval_3_Page_1 {

	@ FindBy(name = "userCode")
	private WebElement mobileNumber;

	@ FindBy(name = "geographicDomainCode")
	private WebElement geographicDomain;

	@ FindBy(name = "domainCode")
	private WebElement domain;

	@ FindBy(name = "categoryCode")
	private WebElement category;

	@ FindBy(xpath = "//input[@value='A']")
	private WebElement allRadioBtn;

	@ FindBy(name = "//input[@value='S']")
	private WebElement singleRadioBtn;

	@ FindBy(name = "submitButton")
	private WebElement submitBtn;

	@ FindBy(name = "resetbutton")
	private WebElement resetBtn;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement SuccessMessage;

	WebDriver driver= null;

	public FOC_Approval_3_Page_1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterMobileNumber(String MobileNumber) {
		Log.info("Trying to enter Mobile Number");
		mobileNumber.sendKeys(MobileNumber);
		Log.info("Mobile Number entered successfully as: "+MobileNumber);
	}
	
	public void selectGeographyDomain(String GeographyDomain) {
		Log.info("Trying to select Geographical Domain");
		Select select = new Select(geographicDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("Geographical Domain selected successfully as: "+GeographyDomain);
	}
	
	public void selectDomain(String Domain) {
		Log.info("Trying to select Domain");
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("Domain selected successfully as: "+Domain);
	}
	
	public void selectCategory(String Category) {
		Log.info("Trying to select Category");
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("Category selected successfully as: "+Category);
	}
	
	public void clickAllRadioBtn() {
		allRadioBtn.click();
		Log.info("User clicked All button");
	}
	
	public void clickSingleButton() {
		singleRadioBtn.click();
		Log.info("User clicked Single button");
	}
	
	public void clickSubmitBtn() {
		Log.info("Trying to click Submit Button");
		submitBtn.click();
		Log.info("Submit Button clicked successfully");
	}
	
	public void clickResetBtn() {
		Log.info("Trying to click Reset Button");
		resetBtn.click();
		Log.info("Reset Button clicked successfully");
	}
	
	public String getMessage() {
		Log.info("Trying to fetch Success Message from WEB");
		String Message = SuccessMessage.getText();
		Log.info("Success Message fetched successfully as: " + Message);
		return Message;
	}
}
