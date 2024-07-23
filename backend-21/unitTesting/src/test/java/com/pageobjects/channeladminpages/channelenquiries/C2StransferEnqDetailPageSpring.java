package com.pageobjects.channeladminpages.channelenquiries;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.asserts.SoftAssert;

import com.utils.Log;

public class C2StransferEnqDetailPageSpring {
	SoftAssert sAssert = new SoftAssert();
	
	@FindBy(xpath = "//a[@id='transferID']")
	public WebElement transferID;
	
	@FindBy(xpath = "//a[@id='msisdn_0']")
	public WebElement senderMsisdn;
	
	@FindBy(xpath = "//a[@id='msisdn_1']")
	public WebElement receiverMsisdn;
	
	WebDriver driver = null;

	public C2StransferEnqDetailPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void prepareC2SEnquiryValuesBy(String transferId,String senderMSISDN,String receiverMSISDN) {
		Log.info("prepareC2SEnquiryValues"+transferId);
		Map<String, String> c2SEnquiryValues= new HashMap<String, String>();
		c2SEnquiryValues.put("transferID", transferID.getText());
		c2SEnquiryValues.put("senderMSISDN", senderMsisdn.getText());
		c2SEnquiryValues.put("receiverMSISDN",senderMsisdn.getText());
		sAssert.assertEquals(c2SEnquiryValues.get("transferID"),transferId);
		sAssert.assertEquals(c2SEnquiryValues.get("senderMSISDN"),senderMSISDN);
		sAssert.assertEquals(c2SEnquiryValues.get("receiverMSISDN"),receiverMSISDN);
		sAssert.assertAll();
	}

}
