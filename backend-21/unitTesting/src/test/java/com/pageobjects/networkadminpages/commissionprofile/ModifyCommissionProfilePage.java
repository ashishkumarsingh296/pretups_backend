package com.pageobjects.networkadminpages.commissionprofile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyCommissionProfilePage {

	@ FindBy(name = "selectCommProfileSetID")
	private WebElement commissionProfileSet;

	@ FindBy(name = "selectCommProifleVersionID")
	private WebElement version;

	@ FindBy(name = "edit")
	private WebElement modifyButton;

	@ FindBy(name = "delete")
	private WebElement deleteButton;

	@ FindBy(name = "back")
	private WebElement backButton;
	
	@FindBy(xpath = "//ol/li")
	private WebElement error;
	
	WebDriver driver= null;

	public ModifyCommissionProfilePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void selectCommissionProfileSet(String CommissionProfileSet) {
		Log.info("Trying to select Commission Profile with value: " + CommissionProfileSet);
		Select select = new Select(commissionProfileSet);
		select.selectByVisibleText(CommissionProfileSet);
		//select.selectByValue(CommissionProfileSet);
		Log.info("User selected Commission Profile Set");
	}
	
	public void selectVersion(String Version) throws InterruptedException {
		Select select = new Select(version);
		select.selectByVisibleText(Version);
		Log.info("User selected Version:"+Version);
	}
	
	public void clickModifyButton(){
		modifyButton.click();
		Log.info("User clicked Modify Button.");
	}
	
	public void clickDeleteButton() throws InterruptedException {
		deleteButton.click();
		Log.info("User clicked Delete Button.");
	}
	
	public void clickBackButton() throws InterruptedException {
		backButton.click();
		Log.info("User clicked Back Button.");
	}
	
	public boolean verifyTheExistenceOfCommissionProfile(String CommissionProfile) throws InterruptedException {
		boolean result= true;
	try{
		Select select = new Select(commissionProfileSet);
		select.selectByVisibleText(CommissionProfile);
		return result;
	}
	catch(Exception NoSuchElementFound){
		result = false;
		return result;
	}
	}
	
	
	public String getErrorMessage(){
		String msg = error.getText();
	return msg;
	}
}
