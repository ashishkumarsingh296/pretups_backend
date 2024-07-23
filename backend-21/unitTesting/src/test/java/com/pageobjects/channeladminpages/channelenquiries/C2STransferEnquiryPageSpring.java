package com.pageobjects.channeladminpages.channelenquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.asserts.SoftAssert;

import com.utils.Log;

public class C2STransferEnquiryPageSpring {
	SoftAssert sAssert = new SoftAssert();
	C2StransferEnqDetailPageSpring c2stransferEnqDetailPageSpring;

	@FindBy(xpath = "//a[@id='back']")
	public WebElement backButton;

	@FindBy(xpath = "//a[@id='save']")
	public WebElement downloadButton;

	WebDriver driver = null;

	public C2STransferEnquiryPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		c2stransferEnqDetailPageSpring = new C2StransferEnqDetailPageSpring(driver);
	}

	public void clickBack() {
		Log.info("Trying to click back button");
		backButton.click();
		Log.info("User clicked back");
	}

	public void clickDownload() {
		Log.info("Trying to click Download button");
		downloadButton.click();
		Log.info("User clicked Download");
	}
	
	public void clicktransferId() {
		Log.info("Trying to click Transfer ID");
		String xpath ="";
		//click link
		Log.info("User clicked Download");
	}
	
	
	public void prepareC2SEnquiryValuesByTransferID(String transferID,String senderMsisdn,String receiverMsisdn) {
		Log.info("prepareC2SEnquiryValuesByTransferID"+transferID);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		String senderMSISDN = "//td[contains(text(),'"+transferID+"')]/following-sibling::td[5]";
		String receiverMSISDN = "//td[contains(text(),'"+transferID+"')]/following-sibling::td[7]";
		c2SEnquiryValues.put("senderMSISDN", driver.findElement(By.xpath(senderMSISDN)).getText());
		c2SEnquiryValues.put("receiverMSISDN", driver.findElement(By.xpath(receiverMSISDN)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("senderMSISDN"),senderMsisdn);
		sAssert.assertEquals(c2SEnquiryValues.get("receiverMSISDN"),receiverMsisdn);
		sAssert.assertAll();
	}
	
	public void prepareC2SEnquiryValuesBySenderMSISDN(String senderMSISDN,String tferID,String receiverMsisdn) {
		Log.info("prepareC2SEnquiryValuesBySenderMSISDN"+senderMSISDN);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		
		String transferID = "//td[contains(text(),'"+senderMSISDN+"')]/preceding-sibling::td";
		String receiverMSISDN = "//td[contains(text(),'"+senderMSISDN+"')]/following-sibling::td/following-sibling::td";
		c2SEnquiryValues.put("transferID", driver.findElement(By.xpath(transferID)).getText());
		c2SEnquiryValues.put("receiverMSISDN", driver.findElement(By.xpath(receiverMSISDN)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("transferID"),tferID);
		sAssert.assertEquals(c2SEnquiryValues.get("receiverMSISDN"),receiverMsisdn);
		//sAssert.assertAll();
	}
	
	public void prepareC2SEnquiryValuesByReceiverID(String receiverMSISDN,String senderMsisdn,String tferID) {
		Log.info("prepareC2SEnquiryValuesBySenderMSISDN"+receiverMSISDN);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		String senderMSISDN = "//td[contains(text(),'"+receiverMSISDN+"')]/preceding-sibling::td[2]";
		String transferID = "//td[contains(text(),'"+receiverMSISDN+"')]/preceding-sibling::td";
		c2SEnquiryValues.put("senderMSISDN", driver.findElement(By.xpath(senderMSISDN)).getText());
		c2SEnquiryValues.put("transferID", driver.findElement(By.xpath(transferID)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("senderMSISDN"),senderMsisdn);
		sAssert.assertEquals(c2SEnquiryValues.get("transferID"),tferID);
		//sAssert.assertAll();
	}
	
	public void prepareC2SEnquiryValuesByTransferIDforOpUser(String tferID,String senderMsisdn,String receiverMsisdn) throws InterruptedException {
		  Log.info("Trying to click transfer ID: "+tferID);
		  WebElement element = null; 
			Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
			
	//	String xpath = "//td/a[contains(text(),'"+tferID+"')]";
		 /* String xpath = "//td[contains(text(),"+tferID+")]";
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		element=driver.findElement(By.xpath(xpath));*/
		String senderMSISDN = "//td[contains(text(),"+tferID+")]/following-sibling::td[5]";
		String receiverMSISDN = "//td[contains(text(),"+tferID+")]/following-sibling::td[7]";
		c2SEnquiryValues.put("senderMSISDN", driver.findElement(By.xpath(senderMSISDN)).getText());
		c2SEnquiryValues.put("receiverMSISDN", driver.findElement(By.xpath(receiverMSISDN)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("senderMSISDN"),senderMsisdn);
		sAssert.assertEquals(c2SEnquiryValues.get("receiverMSISDN"),receiverMsisdn);
		/*Thread.sleep(1000);
        element.click(); */

		//c2stransferEnqDetailPageSpring.prepareC2SEnquiryValuesBy(tferID, senderMsisdn, receiverMsisdn);
		
	}
	
	public void prepareC2SEnquiryValuesBySenderMSISDNforOpUser(String senderMSISDN,String tferID,String receiverMsisdn) {
		Log.info("prepareC2SEnquiryValuesBySenderMSISDN"+senderMSISDN);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		
		/* WebElement element = null; 
		String xpath = "//td[contains(text(),'"+senderMSISDN+"')]/preceding-sibling::td";
		element=driver.findElement(By.xpath(xpath)); 
        element.click(); 
		c2stransferEnqDetailPageSpring.prepareC2SEnquiryValuesBy(tferID, senderMSISDN, receiverMsisdn);*/
		String transferID = "//td[contains(text(),'"+senderMSISDN+"')]/preceding-sibling::td";
		String receiverMSISDN = "//td[contains(text(),'"+senderMSISDN+"')]/following-sibling::td/following-sibling::td";
		c2SEnquiryValues.put("transferID", driver.findElement(By.xpath(transferID)).getText());
		c2SEnquiryValues.put("receiverMSISDN", driver.findElement(By.xpath(receiverMSISDN)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("transferID"),tferID);
		sAssert.assertEquals(c2SEnquiryValues.get("receiverMSISDN"),receiverMsisdn);
	}
	
	public void prepareC2SEnquiryValuesByReceiverIDforOpUser(String receiverMSISDN,String senderMsisdn,String tferID) {
		Log.info("prepareC2SEnquiryValuesBySenderMSISDN"+receiverMSISDN);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		
		 /*WebElement element = null; 
		String xpath = "//td[contains(text(),'"+receiverMSISDN+"')]/preceding-sibling::td";
		element=driver.findElement(By.xpath(xpath)); 
        element.click(); 
		c2stransferEnqDetailPageSpring.prepareC2SEnquiryValuesBy(tferID, senderMsisdn, receiverMSISDN);*/
		String senderMSISDN = "//td[contains(text(),'"+receiverMSISDN+"')]/preceding-sibling::td[2]";
		String transferID = "//td[contains(text(),'"+receiverMSISDN+"')]/preceding-sibling::td";
		c2SEnquiryValues.put("senderMSISDN", driver.findElement(By.xpath(senderMSISDN)).getText());
		c2SEnquiryValues.put("transferID", driver.findElement(By.xpath(transferID)).getText());
		sAssert.assertEquals(c2SEnquiryValues.get("senderMSISDN"),senderMsisdn);
		sAssert.assertEquals(c2SEnquiryValues.get("transferID"),tferID);
		
	}

}
