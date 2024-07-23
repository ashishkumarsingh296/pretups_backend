package com.pageobjects.networkadminpages.o2ctransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class InitiateO2CTransferRulePage2 {
	
	@FindBy(name = "btnAdd")
	 WebElement Add;
	
	@FindBy(name = "btnBack")
	 WebElement Back;
	
	@FindBy(name = "btnModify")
	 WebElement Modify;
	
	
	
	WebDriver driver= null;

	public InitiateO2CTransferRulePage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickAdd()
	{
		Log.info("Trying to click Add Button");
		Add.click();
		Log.info("Add Button clicked successfully");
	}
	
	public void clickModify()
	{
		Log.info("Trying to click Modify Button");
		Modify.click();
		Log.info("Modify Button clicked successfully");
	}
	
	
	public void clickBack()
	{
		Log.info("Trying to click Back Button");
		Back.click();
		Log.info("Back Button clicked successfully");
	}
	
	public boolean checkIfTransferRuleExists(String category) {
		boolean TransferRuleStatus;
		Log.info("Trying to check if O2C Transfer Rule Already exists for category: " + category);
		try {
			TransferRuleStatus = driver.findElement(By.xpath("//tr[*]/td[3][text()='"+category+"']")).isDisplayed();
			Log.info("O2C Transfer Rule for " + category + " already exists");
		}
		catch (NoSuchElementException e)
		{
			Log.info("O2C Transfer Rule for " + category + " does not exists");
			TransferRuleStatus = false;
		}
		
		return TransferRuleStatus;
	}
	
	public void selectTransferRule(String category) {
		Log.info("Trying to select Transfer Rule for category: " + category);
		driver.findElement(By.xpath("//tr[*]/td[3][text()='"+category+"']")).click();
		Log.info("O2C Transfer Rule for " + category + " selected");
	}

}
