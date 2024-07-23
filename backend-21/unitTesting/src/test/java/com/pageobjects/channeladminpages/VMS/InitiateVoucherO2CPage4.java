package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateVoucherO2CPage4 {
	   
		WebDriver driver = null;
		public InitiateVoucherO2CPage4(WebDriver driver) {
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}
		
		@FindBy(xpath = "//td/b[text()='Total']/parent::td/parent::tr/td[10]/b" )
		private WebElement netPayableAmtNegCommission;

		@FindBy(xpath = "//td/b[text()='Total']/parent::td/parent::tr/td[15]/b" )
		private WebElement receiverCreditAmtPosCommission;
		
		@FindBy(name = "confirmO2CVoucherProdButton" )
		private WebElement confirmO2CVoucherProdButton;
		
		@FindBy(name = "backButton" )
		private WebElement backButton;
		
		@ FindBy(xpath = "//ul/li")
		private WebElement message;
		
		@FindBy(xpath = "//ol/li")
		private WebElement errorMessage;
		
		public String fetchNetPayableAmount() {
			String amount = netPayableAmtNegCommission.getText();
			Log.info("Fetched Net Payable Amt."+amount);
			return amount;
		}
		
		public String fetchreceiverCreditQtyPosCommission() {
			String amount = receiverCreditAmtPosCommission.getText();
			Log.info("Fetched Receiver credit quantity: "+amount);
			return amount;
		}
		
		
		public void ClickonConfirm(){
			Log.info("Trying to click on Confirm Button");
			confirmO2CVoucherProdButton.click();
			Log.info("Clicked on Confirm Button successfully");
			}
		
		public void ClickonBack(){
			Log.info("Trying to click on Back Button");
			backButton.click();
			Log.info("Clicked on Back Button successfully");
			}
		
		
		public String getMessage(){
			String Message = null;
			Log.info("Trying to fetch Message");
			try {
			Message = message.getText();
			Log.info("Message fetched successfully as: " + Message);
			} catch (Exception e) {
				Log.info("No Message found");
			}
			return Message;
		}
		
		public String getErrorMessage() {
			String Message = null;
			Log.info("Trying to fetch Error Message");
			try {
			Message = errorMessage.getText();
			Log.info("Error Message fetched successfully");
			}
			catch (org.openqa.selenium.NoSuchElementException e) {
				Log.info("Error Message Not Found");
			}
			return Message;
		}



}
