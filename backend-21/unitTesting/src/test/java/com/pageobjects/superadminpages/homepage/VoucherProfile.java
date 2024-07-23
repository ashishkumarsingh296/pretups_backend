package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class VoucherProfile {
	WebDriver driver;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMADPR001')]]")
    private WebElement addProfile;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMMDPR001')]]")
    private WebElement modifyProfile;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMVWPR001')]]")
    private WebElement viewProfile;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMACPR001')]]")
    private WebElement addActiveProfileDetails;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMMOAP001')]]")
    private WebElement modifyActiveProfileDetails;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMVWAC001')]]")
    private WebElement viewActiveProfileDetails;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VTYPE001')]]")
    private WebElement voucherTypeManagementDetails;
	
	public VoucherProfile(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
  
  public void clickAddVoucherProfile() {
    	Log.info("Trying to click Add Voucher Profile link");
    	new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(addProfile));
    	addProfile.click();
    	Log.info("Add Voucher Profile link clicked successfully");
        }
  
  public void clickModifyVoucherProfile() {
  	Log.info("Trying to click Modify Voucher Profile link");
  	modifyProfile.click();
  	Log.info("Modify Voucher Profile link clicked successfully");
      }
  
  public void clickViewVoucherProfile() {
  	Log.info("Trying to click View Voucher Profile link");
  	viewProfile.click();
  	Log.info("View Voucher Profile link clicked successfully");
      }
  
  public void clickAddActiveProfileDetails() {
  	Log.info("Trying to click Add Active Profile Details link");
  	addActiveProfileDetails.click();
  	Log.info("Add Active Profile Details link clicked successfully");
      }
  
  public void clickModifyActiveProfileDetails() {
	  	Log.info("Trying to click Modify Active Profile Details link");
	  	modifyActiveProfileDetails.click();
	  	Log.info("Modify Active Profile Details link clicked successfully");
	      }
  
  public void clickViewActiveProfileDetails() {
	  	Log.info("Trying to click View Active Profile Details link");
	  	viewActiveProfileDetails.click();
	  	Log.info("View Active Profile Details link clicked successfully");
	      }
  
  public void clickVoucherTypeManagementDetails() {
	  	Log.info("Trying to click Voucher Type Management Details link");
	  	voucherTypeManagementDetails.click();
	  	Log.info("Voucher Type Management Details clicked successfully");
	      }
  
}
