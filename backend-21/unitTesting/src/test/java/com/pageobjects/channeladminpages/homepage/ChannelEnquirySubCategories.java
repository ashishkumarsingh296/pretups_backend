package com.pageobjects.channeladminpages.homepage;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ChannelEnquirySubCategories {
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CUSRBALV01')]]")
	private WebElement userBalance;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CENQ001')]]")
	private WebElement O2CTransfers;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=VIEWCUSR01')]]")
	private WebElement ViewChannelUser;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CUSRBALV01')]]")
	private WebElement userBalances;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CUSRBALCU')]]")
	private WebElement userBalanceSpring;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=CUSRBALV03')]]")
	private WebElement selfBalance;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=C2STENQ001')]]")
	private WebElement C2STransfers;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=C2CENQ001')]]")
	private WebElement C2CTransfers;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=O2CENQ006')]]")
	private WebElement O2CTransfersSpring;
	
	@FindBy(xpath = "//a[@href[contains(.,'pageCode=ZBALDET001')]]")
	private WebElement zeroBalanceCounterDetailSpring;
	
	WebDriver driver= null;

	public ChannelEnquirySubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickUserBalance() {
		Log.info("Trying to click User Balances link");
		userBalance.click();
		Log.info("User Balances link clicked successfully");
	}
	
	public void clickUserBalanceSpring() {
		Log.info("Trying to click User Balances link");
		userBalanceSpring.click();
		Log.info("User Balances link clicked successfully");
	}
	
	public void clickSelfBalance() {
		Log.info("Trying to click Self Balance link");
		selfBalance.click();
		Log.info("Self Balance link clicked successfully");
	}
	
	public void clickO2CTransfers() {
		Log.info("Trying to click O2C Transfers Link");
		O2CTransfers.click();
		Log.info("O2C Transfers Link clicked successfully");
	}
	
	public void clickViewChannelUser() {
		Log.info("Trying to click View Channel User link");
		ViewChannelUser.click();
		Log.info("View Channel User link clicked successfully");
	}
	
	public void clickUserBalanceEnquiry() {
		Log.info("Trying to click User Balance link");
		userBalances.click();
		Log.info("User Balance link clicked successfully");
	}
	
	public void clickC2STransfersEnquiry() {
		Log.info("Trying to click C2S Transfers Enquiry link");
		C2STransfers.click();
		Log.info("C2S Transfers Enquiry link clicked successfully");
	}
	
	public void clickC2CTransfersEnquiry() {
		Log.info("Trying to click C2C Transfers Enquiry link");
		C2CTransfers.click();
		Log.info("C2C Transfers Enquiry link clicked successfully");
	}
	
	public void clickZeroBalanceCounterSpring()
	{
		Log.info("Trying to click Zero Balance Counter Spring");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		zeroBalanceCounterDetailSpring.click();
		Log.info("Zero Balance Counter Spring Link clicked successfully");
	}
	
	public void clickO2CTransfersSpring()
	{
		Log.info("Trying to click O2C Transfer Spring");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		O2CTransfersSpring.click();
		Log.info("O2C Transfer Spring Link clicked successfully");
	}
}
