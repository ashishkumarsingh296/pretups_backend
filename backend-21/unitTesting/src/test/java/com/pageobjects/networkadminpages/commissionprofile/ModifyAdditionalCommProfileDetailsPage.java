package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyAdditionalCommProfileDetailsPage {
	
	@FindBy(name = "applicableFromAdditional")
	private WebElement applicableFromDate;

	@FindBy(name = "applicableToAdditional")
	private WebElement applicableToDate;
	
	@FindBy(name="deleteAdditional")
	private WebElement deleteSlab;
	
	@FindBy(name="suspendAdditional")
	private WebElement suspendSlab;
	
	@FindBy(name="resumeAdditional")
	private WebElement resumeSlab;
	
	WebDriver driver= null;

	public ModifyAdditionalCommProfileDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void enterApplicableToDate(String ApplicableToDate) {
		applicableToDate.clear();
		applicableToDate.sendKeys(ApplicableToDate);
		Log.info("User entered Applicable To Date: " + ApplicableToDate);
	}

	public void enterApplicableFromDate(String ApplicableFromDate) {
		applicableFromDate.clear();
		applicableFromDate.sendKeys(ApplicableFromDate);
		Log.info("User entered Applicable From Date: " + ApplicableFromDate);
	}
	
	public void clickDeleteSlab(){
		deleteSlab.click();
	}
	
	public void clickSuspendAdditionalComm(){
		suspendSlab.click();
		Log.info("User clicked suspend button");
	}
	
	public void clickResumeAdditionalComm(){
		resumeSlab.click();
		Log.info("User clicked resume button");
	}
	

}
