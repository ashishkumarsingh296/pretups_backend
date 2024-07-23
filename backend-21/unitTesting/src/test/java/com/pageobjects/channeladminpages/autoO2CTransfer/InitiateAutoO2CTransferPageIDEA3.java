package com.pageobjects.channeladminpages.autoO2CTransfer;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateAutoO2CTransferPageIDEA3 {

	@ FindBy(name = "maxTxnAmount")
	private WebElement maxTxnAmount;
	
	@ FindBy(name = "dailyInCount")
	private WebElement dailyCount;
	
	@ FindBy(name = "weeklyInCount")
	private WebElement weeklyCount;
	
	@ FindBy(name = "monthlyInCount")
	private WebElement monthlyCount;
	
	@ FindBy(name = "submit")
	private WebElement AddModify;

	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	WebDriver driver= null;
	
	public InitiateAutoO2CTransferPageIDEA3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	 public void selectIsAutoO2CAllowed(String autoO2CAllowed) {
			Log.info("Trying to click on radio button");
			List<WebElement> radioList = driver
					.findElements(By.xpath("//td[text()[contains(.,'')]]/input[@type='radio']"));
			if(autoO2CAllowed.equalsIgnoreCase("true"))
			 radioList.get(0).click();
			else
			radioList.get(1).click();
		}
		
		public void enterMaxAmt(String amount) throws InterruptedException {
			    maxTxnAmount.clear();
			    if(!amount.equals(""))
			    maxTxnAmount.sendKeys(amount);
				Log.info("User entered Max TXN Amount: "+amount);
			}
		
		public void enterDailyCount(String count) throws InterruptedException {
			dailyCount.clear();
		    if(!count.equals(""))
		    	dailyCount.sendKeys(count);
			Log.info("User entered Daily Count: "+count);
		}
		
		public void enterWeeklyCount(String count) throws InterruptedException {
			weeklyCount.clear();
		    if(!count.equals(""))
		    	weeklyCount.sendKeys(count);
			Log.info("User entered Weekly Count: "+count);
		}
		
		public void enterMonthlyCount(String count) throws InterruptedException {
			monthlyCount.clear();
		    if(!count.equals(""))
		    	monthlyCount.sendKeys(count);
			Log.info("User entered Monthly Count: "+count);
		}
		
		public void clickAddModifyButton() {
			AddModify.click();
			Log.info("User clicked Add/Modify button");
		}
}
