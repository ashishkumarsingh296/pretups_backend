package com.pageobjects.channeluserspages.channelenquiry;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CEnquiryViewDetailsSpring {
	
	WebDriver driver;
	
	@FindBy(name = "backFromDetails")
	private WebElement backButton;
	
	@FindBy(xpath = "//input[@id='tmpToUserCode']")
	private WebElement receiverMSISDN;
	
	@FindBy(xpath = "//input[@id='transferNumber']")
	private WebElement transferNumber;
	
	public void clickBackButton() {
		Log.info("Trying to click Back Button");
		backButton.click();
		Log.info("User clicked Back Button");
	}

	public C2CEnquiryViewDetailsSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public Map<String, String> prepareC2CEnquiryDataTransferNumber(){
		Map<String, String> c2cEnquiryValues= new HashMap<String, String>();
		c2cEnquiryValues.put("receiverMSISDN", receiverMSISDN.getAttribute("value"));
		c2cEnquiryValues.put("transferNum", transferNumber.getAttribute("value"));
		return c2cEnquiryValues;
	}
	
	public Map<String, String> prepareC2CEnquiryDataSenderMsisdn(){
		Map<String, String> c2cEnquiryValues= new HashMap<String, String>();
		c2cEnquiryValues.put("receiverMSISDN", receiverMSISDN.getAttribute("value"));
		c2cEnquiryValues.put("transferNum", transferNumber.getAttribute("value"));
		return c2cEnquiryValues;
	}
	
	public Map<String, String> prepareC2CEnquiryDataReceiverrMsisdn(){
		Map<String, String> c2cEnquiryValues= new HashMap<String, String>();
		c2cEnquiryValues.put("receiverMSISDN", receiverMSISDN.getAttribute("value"));
		c2cEnquiryValues.put("transferNum", transferNumber.getAttribute("value"));
		return c2cEnquiryValues;
	}
}
