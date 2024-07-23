package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.utils.Log;

public class VoucherDenomination {
	WebDriver driver;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMADCT001')]]")
    private WebElement addDenomination;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMMODN001')]]")
    private WebElement modifyDenomination;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VMVWDE001')]]")
    private WebElement viewDenomination;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHGSTAT001')]]")
    private WebElement changeGeneratedStatus;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=CHGOTST001')]]")
    private WebElement changeOtherStatus;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VIEWBCH001')]]")
    private WebElement viewBatchList;
	
	  public VoucherDenomination(WebDriver driver) {
	    	this.driver = driver;
	    	PageFactory.initElements(driver, this);
	        }
	  
	  public void clickAddVoucherDenomination() {
	    	Log.info("Trying to click Add Voucher Denomination link");
	    	new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(addDenomination));
	    	addDenomination.click();
	    	Log.info("Add Voucher Denomination link clicked successfully");
	        }
	  
	  public void clickModifyVoucherDenomination() {
	    	Log.info("Trying to click Modify Voucher Denomination link");
	    	modifyDenomination.click();
	    	Log.info("Modify Voucher Denomination link clicked successfully");
	        }
	  
	  public void clickViewVoucherDenomination() {
	    	Log.info("Trying to click View Voucher Denomination link");
	    	viewDenomination.click();
	    	Log.info("View Voucher Denomination link clicked successfully");
	        }
	  
	  public void clickChangeGeneratedStatus() {
	    	Log.info("Trying to click Change Generated Status link");
	    	changeGeneratedStatus.click();
	    	Log.info("Change Generated Status link clicked successfully");
	        }
	  
	  public void clickChangeOtherStatus() {
	    	Log.info("Trying to click Change Other Status link");
	    	changeOtherStatus.click();
	    	Log.info("Change Other Status link clicked successfully");
	        }
	  
	  public void clickViewBatchList() {
	    	Log.info("Trying to click View Batch List link");
	    	viewBatchList.click();
	    	Log.info("View Batch List link clicked successfully");
	        }
}
