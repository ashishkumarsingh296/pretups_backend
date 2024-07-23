package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ApproveLevel1Page {

	@ FindBy(name = "userCode")
	private WebElement mobileNumber;
	
	@FindBy(name="distributorMode")
	private WebElement distributionMode;

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

	@ FindBy(name = "submitBtnL1")
	private WebElement submitBtn;

	@ FindBy(name = "resetbutton")
	private WebElement resetBtn;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@ FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;

	public ApproveLevel1Page(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void enterMobileNumber(String MobileNumber) {
		mobileNumber.sendKeys(MobileNumber);
		Log.info("User entered Mobile Number: "+MobileNumber);
	}
	
	public void selectGeographyDomain(String GeographyDomain) {
		Select select = new Select(geographicDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("User selected Geography Domain: "+GeographyDomain);
	}
	
	public void selectDomain(String Domain) {
		Select select = new Select(domain);
		select.selectByVisibleText(Domain);
		Log.info("User selected Domain: "+Domain);
	}
	
	public void selectCategory(String Category) {
		Select select = new Select(category);
		select.selectByVisibleText(Category);
		Log.info("User selected Category: "+Category);
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
		submitBtn.click();
		Log.info("User clicked submit button");
	}
	
	public void clickResetBtn() {
		resetBtn.click();
		Log.info("User clicked Reset button");
	}
	
	public String getMessage(){
		return message.getText();
	}
	
	public String getErrorMessage(){
		return errorMessage.getText();
	}
	
	public boolean selectDistributionMode(String DistributionMode) {
		Log.info("Trying to select Distribution Type");
		boolean status = false;
		try {
		Select select = new Select(this.distributionMode);
		select.selectByValue(DistributionMode);
		Log.info("Distribution Mode selected successfully as: " + DistributionMode);
		status = true;
		}
		catch (Exception e) {
			Log.info("Distribution Mode Dropdown not found");
		}
		return status;
	}

}
