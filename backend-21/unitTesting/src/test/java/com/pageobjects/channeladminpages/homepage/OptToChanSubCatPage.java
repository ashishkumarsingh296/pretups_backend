package com.pageobjects.channeladminpages.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class OptToChanSubCatPage {


	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CTRF001')]]")
	private WebElement initiateTransfer;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CAPV101')]]")
	private WebElement approveLevel1;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CAPV201')]]")
	private WebElement approveLevel2;
	
	@ FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CAPV301')]]")
	private WebElement approveLevel3;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=FOCTRF001')]]")
	private WebElement initiateFOCTransfer;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=FOCAPP101')]]")
	private WebElement FOCApprovalLevel1;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=FOCAPP201')]]")
	private WebElement FOCApprovalLevel2;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=FOCAPP301')]]")
	private WebElement FOCApprovalLevel3;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=AUTOO2C001')]]")
	private WebElement InitiateAutoO2CTransfer;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=AO2CAP1001')]]")
	private WebElement AutoO2CTransferApproval1;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=AO2CAP2001')]]")
	private WebElement AutoO2CTransferApproval2;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=AO2CAP3001')]]")
	private WebElement AutoO2CTransferApproval3;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=ATOO2CM001')]]")
	private WebElement AutoO2CCreditLimit;
	
	WebDriver driver= null;

	public OptToChanSubCatPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickInitiateTransfer() {
		Log.info("Trying to click Initiate O2C Transfer link");
		initiateTransfer.click();
		Log.info("Initiate O2C Transfer link clicked successfully");
	}
	
	public void clickApproveLevel1() {
		Log.info("Trying to click O2C Transfer Approval Level 1 link");
		approveLevel1.click();
		Log.info("O2C Transfer Approval Level 1 link clicked successfully");
	}
	
	public void clickApproveLevel2() {
		Log.info("Trying to click O2C Transfer Approval Level 2 link");
		approveLevel2.click();
		Log.info("O2C Transfer Approval Level 2 link clicked successfully");
	}
	
	public void clickApproveLevel3() {
		Log.info("Trying to click O2C Transfer Approval Level 3 link");
		approveLevel3.click();
		Log.info("O2C Transfer Approval Level 3 link clicked successfully");
	}
	
	public void clickInitiateFOCTransfer() {
		Log.info("Trying to click Initiate FOC Transfer Link");
		initiateFOCTransfer.click();
		Log.info("Initiate FOC Transfer link clicked successfully");
	}
	
	public void clickFOCApprovalLevel1() {
		Log.info("Trying to click FOC Approval Level 1 link");
		FOCApprovalLevel1.click();
		Log.info("FOC Level 1 Approval Link clicked successfully");
	}
	
	public void clickFOCApprovalLevel2() {
		Log.info("Trying to click FOC Approval Level 2 link");
		FOCApprovalLevel2.click();
		Log.info("FOC Level 2 Approval Link clicked sucessfully");
	}
	
	public void clickFOCApprovalLevel3() {
		Log.info("Trying to click FOC Approval Level 3 link");
		FOCApprovalLevel3.click();
		Log.info("FOC Level 3 Approval Link clicked successfully");
	}
	
	public void clickInitiateAutoO2CTransfer() {
		Log.info("Trying to click Initiate Auto O2C link");
		InitiateAutoO2CTransfer.click();
		Log.info("Initiate Auto O2C Link clicked successfully");
	}
	
	public void clickAutoO2CApproveLevel1() {
		Log.info("Trying to click Auto O2C Transfer Approval Level 1 link");
		AutoO2CTransferApproval1.click();
		Log.info("Auto O2C Transfer Approval Level 1 link clicked successfully");
	}
	
	public void clickAutoO2CApproveLevel2() {
		Log.info("Trying to click Auto O2C Transfer Approval Level 2 link");
		AutoO2CTransferApproval2.click();
		Log.info("Auto O2C Transfer Approval Level 2 link clicked successfully");
	}
	
	public void clickAutoO2CApproveLevel3() {
		Log.info("Trying to click Auto O2C Transfer Approval Level 3 link");
		AutoO2CTransferApproval3.click();
		Log.info("Auto O2C Transfer Approval Level 3 link clicked successfully");
	}
	
	public void clickAutoO2CCreditLimit() {
		Log.info("Trying to click Auto O2C Credit Limit link");
		AutoO2CCreditLimit.click();
		Log.info("Auto O2C Credit Limit link clicked successfully");
	}
}
