package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewActiveProfile2 {

		WebDriver driver = null;
		public ViewActiveProfile2(WebDriver driver) {
			this.driver = driver;
			PageFactory.initElements(driver, this);
		}
			
		@FindBy(name = "backViewActProf" )
		private WebElement backButton;
		
		@FindBy(xpath = "//ul/li")
		private WebElement message;
		
		@FindBy(xpath = "//ol/li")
		private WebElement errorMessage;
		
		public boolean checkParticularActiveProfile(String mrp, String denominationName, String profileName, String talkTime, String validity, String voucherType) {
			boolean elementDisplayed = false;
			WebElement element = null;
			StringBuilder TransferRuleX = new StringBuilder();
			TransferRuleX.append("//tr/td[normalize-space() = '" + mrp);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + denominationName);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + profileName);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + talkTime);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + validity);
			TransferRuleX.append("']/following-sibling::td[normalize-space() = '" + voucherType + "']");
			try {
			element= driver.findElement(By.xpath(TransferRuleX.toString()));
			elementDisplayed = element.isDisplayed();
			}catch(NoSuchElementException e) {
				return false;
			}return elementDisplayed;
		}
		
		public String getSuccessMessage(){
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
			String errormessage =null;
			Log.info("Trying to fetch Message");
			try {
				errormessage =errorMessage.getText();
				Log.info("Error Message fetched successfully as: " + errormessage);
			}
			catch(Exception e){
				Log.info("Error Message not found");
			}
			
			return errormessage;
		}
		
		public void clickBack() {
			Log.info("Trying to click on Back button ");
			backButton.click();
			Log.info("Clicked on Back Button successfully");
		}
}
