package com.pageobjects.channeluserspages.homepages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChannelUserHomePage {

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=C2STRF')]]")
	private WebElement c2sTransfer;

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=CHNL2CHNL')]]")
	private WebElement c2cTransfer;

	@FindBy(xpath="//a[@id='BC2CIN001CHNL2CHNL']")
	public WebElement initiatec2cBatch;

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=WITHDRAW')]]")
	private WebElement c2cWithdraw;

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=C2SENQ')]]")
	private WebElement channelEnquiry;

	@FindBy(xpath = "//a[@href[contains(.,'moduleCode=OPT2CHNL')]]")
	private WebElement o2cTransfer;
    
	@FindBy(xpath="//a[@href[contains(.,'moduleCode=CHRPTC2C')]]")
	private WebElement channelreportC2C;
	
	@FindBy(xpath="//a[@href[contains(.,'moduleCode=CHRPTUSR')]]")
	private WebElement UserBalanceMovBal;
	
	@FindBy(xpath="//a[@href [contains(.,'moduleCode=CUSERS')]]")
	private WebElement changeNotificationLanguageLink;
	
	@FindBy(xpath="//a[@href [contains(.,'moduleCode=VOMSOREQ')]]")
	private WebElement voucherOrderRequest;
	
	@FindBy(xpath="//a[@href [contains(.,'pageCode=VOMSRQO001')]]")
	private WebElement voucherOrderRequestInitiate;
	
	@FindBy(linkText = "Logout")
	private WebElement logout;
	
	@ FindBy(xpath = "//table/tbody/tr/td/div/span")
	private WebElement loginDateAndTime;

	WebDriver driver = null;

	public ChannelUserHomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void clickC2STransfer() {
		c2sTransfer.click();
		Log.info("User clicked C2S Transfer Link.");
	}
	
	public void clickVoucherOrderRequestInitiate() {
		Log.info("Trying to click Voucher Order Request Inititate link.");
		voucherOrderRequestInitiate.click();
		Log.info(" Voucher Order Request Inititate link clicked successfuly.");
	}
	
	public void clickVoucherOrderRequest() {
		Log.info("Trying to click Voucher Order Request link.");
		voucherOrderRequest.click();
		Log.info(" Voucher Order Request link clicked successfuly.");
	}
	
	public String getDate() throws InterruptedException {
		String[] dateTime= loginDateAndTime.getText().split(" ");
		System.out.println(loginDateAndTime.getText());
		System.out.println(dateTime);
		String date = dateTime[11];
		System.out.println(date);
		Log.info("Server date: "+date);
		return date;
	}

	public void clickC2CTransfer() {
		Log.info("Trying to click Channel to Channel link.");
		WebDriverWait wait=new WebDriverWait(driver,10);
		wait.until(ExpectedConditions.visibilityOf(c2cTransfer));
		c2cTransfer.click();
		Log.info("Channel to Channel link clicked successfuly.");
	}

	public void clickInitiateC2CBatch(){
		Log.info("Trying to click Initiate C2C Batch link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		initiatec2cBatch.click();
		Log.info("Initiate C2C Batch link clicked successfuly.");
	}

	public void clickLogout() {
		logout.click();
		Log.info("User clicked logout button");
	}

	public void clickWithdrawalLink() {
		Log.info("Trying to click Withdrawal link");
		c2cWithdraw.click();
		Log.info("Withdrawal link clicked successfuly.");
	}

	public void clickChannelEnquiry() {
		Log.info("Trying to click Channel Enquiry Link");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		channelEnquiry.click();
		Log.info("Channel Enquiry Link clicked successfully");
	}

	public void clickO2CTransfer() {
		Log.info("Trying to click O2C Transfer link");
		o2cTransfer.click();
		Log.info("O2C Transfer link clicked successfully");
	}

	public boolean C2STransferLinkVisibility(){
		Log.info("Trying to check C2S Transfer Link exists");
		boolean result = false;
		try {
			if (c2sTransfer.isDisplayed()) {
				Log.info("C2S Transfer Link exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("C2S Transfer Link does not exist");
		}
		return result;
	}


	public boolean C2CTransferLinkVisibility(){

		Log.info("Trying to check C2C Transfer Link exists");
		boolean result = false;
		try {
			if (c2cTransfer.isDisplayed()) {
				Log.info("C2C Transfer Link exists");
				result = true;
			}
		} catch (NoSuchElementException e) {
			result = false;
			Log.info("C2C Transfer Link does not exist");
		}
		return result;

	}
	
	public void clickChannelTrfC2CReport() {
		Log.info("Trying to click Channel Transfer-C2C link");
		channelreportC2C.click();
		Log.info("Channel Transfer-C2C link clicked successfully");
	}
	
	public void clickUserBalMov() {
		Log.info("Trying to click User Balance Movement Summary link");
		UserBalanceMovBal.click();
		Log.info("User Balance Movement Summary clicked successfully");
	}
	
	public void clickChannelUsers() {
		Log.info("Trying to click Channel Users link");
		changeNotificationLanguageLink.click();
		Log.info("Channel Users link clicked successfully");
	}

}
