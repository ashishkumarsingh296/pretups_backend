package com.pageobjects.superadminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VoucherGeneration {
	WebDriver driver;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VOMSI009')]]")
    private WebElement vomsOrderInitiate;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VOMSA1001')]]")
    private WebElement vomsOrderApproval1;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VOMSA2001')]]")
    private WebElement vomsOrderApproval2;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=VOMSA3001')]]")
    private WebElement vomsOrderApproval3;
	
	public VoucherGeneration(WebDriver driver) {
    	this.driver = driver;
    	PageFactory.initElements(driver, this);
        }
  
  public void clickVomsOrderInitiate() {
    	Log.info("Trying to click Voms Order Initiate link");
    	vomsOrderInitiate.click();
    	Log.info("Voms Order Initiate link clicked successfully");
        }
  
  public void clickVomsOrderApproval1() {
  	Log.info("Trying to click Voms Order Approval1 link");
  	vomsOrderApproval1.click();
  	Log.info("Voms Order Approval1 link clicked successfully");
      }
  
  public void clickVomsOrderApproval2() {
  	Log.info("Trying to click Voms Order Approval2 link");
  	vomsOrderApproval2.click();
  	Log.info("Voms Order Approval2 link clicked successfully");
      }
  
  public void clickVomsOrderApproval3() {
  	Log.info("Trying to click Voms Order Approval3 link");
  	vomsOrderApproval3.click();
  	Log.info("Voms Order Approval3 link clicked successfully");
      }
  
}
