package com.pageobjects.networkadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProfileManagementSubCategories {

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=COMMP001')]]")
	private WebElement commissionProfile;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=COMMPS001')]]")
	private WebElement commissionProfileStatus;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=TFCTRPRF01')]]")
	private WebElement transferControlProfile;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=UCONFMGT01')]]")
	private WebElement userDefaultConfigurationManagement;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=OCOMP001')]]")
	private WebElement otherCommissionProfile;

	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=OCOMP001')]]")
	private WebElement viewProfile;

	WebDriver driver= null;

	public ProfileManagementSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickCommissionProfile() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(commissionProfile));
		commissionProfile.click();
		Log.info("User clicked Commission Profile.");
	}
	
	public void clickCommissionProfileStatus() {

		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(commissionProfileStatus));
		commissionProfileStatus.click();
		Log.info("User clicked Commission Profile Status.");
	}
	
	public void clickTransferControlProfile() {
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(transferControlProfile));

		transferControlProfile.click();
		Log.info("User clicked Transfer Control Profile.");
	}
	
	public void clickUserDefaultConfigurationManagement() {
		userDefaultConfigurationManagement.click();
		Log.info("User clicked User Default Configuration Management.");
	}
	
	public void clickOtherCommProfile() {
		otherCommissionProfile.click();
		Log.info("Clicked on Other Commission Profile");
	}

	public void clickviewLoanprofile(){
		viewProfile.click();
		Log.info("Clicked on View Loan Profile..");
	}
}
