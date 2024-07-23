package com.pageobjects.networkadminpages.c2ctransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class AssociateC2CTransferRulePage2 {
	
	@FindBy(name = "btnAdd")
	 WebElement Add;
	
	@FindBy(name = "btnBack")
	 WebElement Back;
	
	@FindBy(name = "btnModify")
	 WebElement Modify;
	
	
	
	WebDriver driver= null;

	public AssociateC2CTransferRulePage2(WebDriver driver) {
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
	
	public boolean checkIfTransferRuleExists(String fromCategory, String toCategory) {
		boolean TransferRuleStatus;
		Log.info("Trying to check if C2C Transfer Rule Already exists for from category '" + fromCategory+"' to category '"+toCategory+"'");
		try { 
			TransferRuleStatus = driver.findElement(By.xpath("//td[3][text()='"+toCategory+"']/preceding-sibling::td[text()='"+fromCategory+"']")).isDisplayed();
		
			if(TransferRuleStatus==true)
			{
				Log.info("C2C Transfer Rule from '"+ fromCategory+"' to '"+toCategory+"' already exists");
			}
			
		}
		catch (NoSuchElementException e)
		{
			Log.info("C2C Transfer Rule from '"+ fromCategory+"' to '"+toCategory+"' not exist.");
			TransferRuleStatus = false;
		}
		return TransferRuleStatus;
		}

}
