package com.pageobjects.channeladminpages.channelreportO2C;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class O2CTransferAcknowledgementSpring {

	@FindBy(xpath="//a[@href [contains(.,'pageCode=RPTO2CDD01')]]")
	private WebElement o2cTransferdetailsLink;
	
	@FindBy(id="transferNum")
	private WebElement transferNum;
	
	
	@FindBy(id="submitUserSearchButton")
	private WebElement submit;
	
	@FindBy(id="iNETReport")
	private WebElement report;
	
	@FindBy(xpath="//input[@id='transferNum']/following-sibling::label")
	private WebElement transferNumMsg;
	
	
	
	
	@FindBy(xpath="//*[@id='submitUserSearchButton' and @class='submit btn btn-primary  enabled']")
	private WebElement submitbtnenabled;
	
	WebDriver driver=null;
	
	public O2CTransferAcknowledgementSpring(WebDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectTransferNumber(String transferNumber){
		Log.info("Trying to select transferNumber: "+transferNumber);
		Select select = new Select(transferNum);
		select.selectByVisibleText(transferNumber);
		Log.info("TransferNumber selected successfully.");
	}

	public void clicksubmitBtn(){
		Log.info("Trying to click submit button.");
		submit.click();
		Log.info("Submit button clicked successfuly.");
	}
	
	public void clickreportBtn(){
		Log.info("Trying to click report button.");
		report.click();
		Log.info("Report button clicked successfuly.");
	}
	
	public void clickO2CTransferDetailslink() {
		Log.info("Trying to click O2C Transfer Details link");
		o2cTransferdetailsLink.click();
		Log.info("O2C Transfer Details link clicked successfully");
	}
	
	public String fetcherrormessage(String attribute){
		String errormessage = null;
		Log.info("Trying to get error message from screen for: "+attribute );
		if(attribute.equalsIgnoreCase("transferNum")){
			errormessage=transferNumMsg.getText();
		}
		
		else {Log.info("Issue with attribute ["+attribute+"] passed in method.");}
		
		Log.info("Message successfuly fetched for : "+attribute);
		return errormessage;
	}
	
	public boolean submitBtnenabled(){
		boolean enabled=false;
		try{if(submitbtnenabled.isDisplayed()){
			enabled=true;
			Log.info("Submit button is enabled.");
		}}catch(Exception e){enabled = false;
		Log.info("Submit button is not enabled.");}
		return enabled;
	}
}

