package com.pageobjects.channeladminpages.o2ctransfer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class FOC_Approval_1_Page_1 {

	@ FindBy(name = "userCode")
	private WebElement mobileNumber;

	@ FindBy(name = "geographicalDomainCode")
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
	
	@ FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	WebDriver driver= null;

	public FOC_Approval_1_Page_1(WebDriver driver) {
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

}
