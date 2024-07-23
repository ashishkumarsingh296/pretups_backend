package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewBatchList2 {
	
	WebDriver driver = null;

	public ViewBatchList2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "backBtn")
	private WebElement backButton;
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Back Button");
		backButton.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	public boolean checkParticularBatchNumberAvailable(String batchNumber) {
		boolean elementDisplayed = false;
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//a[text()= '" + batchNumber + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		elementDisplayed = element.isDisplayed();
		return elementDisplayed;
	}

	
	public void selectBatchNumberAvailable(String batchNumber) {
		try {
			Log.info("Trying to click on Batch Number");
		WebElement element = null;
		StringBuilder TransferRuleX = new StringBuilder();
		TransferRuleX.append("//a[text()= '" + batchNumber + "']");
		element= driver.findElement(By.xpath(TransferRuleX.toString()));
		element.click();
		}
		catch(Exception e) {
			Log.info("Batch Number is not clickable");
		}
		Log.info("Batch Number clicked successfully");
		
	}

}
