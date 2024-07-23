/**
 * 
 */
package com.pageobjects.channeluserspages.sublinks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author lokesh.kontey
 *
 */
public class ChannelUserSubLinkPages {

	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CWDR001')]]")
	public WebElement withdrawal;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CTRF001')]]")
	public WebElement c2cTransfer;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVINI001')]]")
	public WebElement c2cVocuherTransfer;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR1001')]]")
	public WebElement c2cTransferApr1;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR2001')]]")
	public WebElement c2cTransferApr2;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CAPR3001')]]")
	public WebElement c2cTransferApr3;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP101')]]")
	public WebElement c2cTransferVouApr1;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP201')]]")
	public WebElement c2cTransferVouApr2;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CVAP301')]]")
	public WebElement c2cTransferVouApr3;
	
	@FindBy(xpath="//a[@href[contains(.,'pageCode=C2CWDR004')]]")
	public WebElement c2cReturn;
	
	WebDriver driver = null;

	public ChannelUserSubLinkPages(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickC2CTransferLink(){
		Log.info("Trying to click C2CTransfer link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransfer.click();
		Log.info("C2CTransfer link clicked successfuly.");
	}
	
	
	public void clickC2CVoucherTransferLink(){
		Log.info("Trying to click C2CTransfer link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cVocuherTransfer.click();
		Log.info("C2CTransfer link clicked successfuly.");
	}
	
	public void clickC2CTransferApr1(){
		Log.info("Trying to click C2CTransfer Approval 1 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr1.click();
		Log.info("C2CTransfer Approval 1 link clicked successfuly.");
	}
	
	
	public void clickC2CTransferApr2(){
		Log.info("Trying to click C2CTransfer Approval 2 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr2.click();
		Log.info("C2CTransfer Approval 2 link clicked successfuly.");
	}
	
	public void clickC2CTransferApr3(){
		Log.info("Trying to click C2CTransfer Approval 3 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferApr3.click();
		Log.info("C2CTransfer Approval 3 link clicked successfuly.");
	}
	
	
	
	public void clickC2CTransferVoucApr1(){
		Log.info("Trying to click C2CTransfer Voucher Approval 1 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr1.click();
		Log.info("C2CTransfer Vocuher Approval 1 link clicked successfuly.");
	}
	
	
	public void clickC2CTransferVoucApr2(){
		Log.info("Trying to click C2CTransfer Voucher Approval 2 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr2.click();
		Log.info("C2CTransfer Voucher Approval 2 link clicked successfuly.");
	}
	
	public void clickC2CTransferVoucApr3(){
		Log.info("Trying to click C2CTransfer Voucher Approval 3 link");
		try {
			Thread.sleep(500);
		} catch(Exception e) { }
		c2cTransferVouApr3.click();
		Log.info("C2CTransfer Voucher Approval 3 link clicked successfuly.");
	}
	
	
	public void clickWithdrawLink(){
		Log.info("Trying to click Withdraw link");
		try {
			Thread.sleep(500);
		} catch (Exception e) { }
		withdrawal.click();
		Log.info("Withdraw link clicked successfuly.");
	}
	
	public void clickC2CReturnLink(){
		Log.info("Trying to click C2CReturn link");
		c2cReturn.click();
		Log.info("C2CReturn link clicked successfuly.");
	}
	
}
