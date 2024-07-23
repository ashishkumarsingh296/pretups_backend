package com.pageobjects.superadminpages.subLookUps;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class modifyLookUps {
	
	@FindBy(name = "lookupCode")
	private WebElement modifyLookUpName;
	
	@FindBy(name = "subLookupCode")
	private WebElement selectSubLName;
	
	@FindBy(name = "submit")
	private WebElement submitButton;
	
	@FindBy(name = "subLookupName")
	private WebElement subLName;
	
	@FindBy(name = "submit1")
	private WebElement submit1;
	
	@FindBy(name = "reset")
	private WebElement resetButton;
	
	@FindBy(name = "delete")
	private WebElement delete;
	
	@FindBy(name = "back")
	private WebElement backButton;
	
	@FindBy(name = "confirm")
	private WebElement confirmButton;
	
WebDriver driver;
	
	public modifyLookUps(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public int getLookUpCodeIndex() {
		Select select = new Select(modifyLookUpName);
		ArrayList<WebElement> lookUpName = (ArrayList<WebElement>) select.getOptions();
		int size = lookUpName.size();
		System.out.println(size);
		Log.info("List of LookUpCodes." + size);
		return --size;
	}
	
	public String selectLookUpName(int index) {
		Select select = new Select(modifyLookUpName);
		select.selectByIndex(index);
		String Name = driver.findElement(By.xpath("//select[@name='lookupCode']/option["+(index + 1)+"]")).getText();
		Log.info("User selected LookUpName: ["+Name+"]");
		
		return Name;
	}
	
	public void selectLookUpNamebyVisibleText(String LookUpName) {
		Select select = new Select(modifyLookUpName);
		select.selectByVisibleText(LookUpName);
		
		Log.info("User selected LookUpName: ["+LookUpName+"]");
		
	
	}
	
	public void selectSubLookupName(String subLName){
		Log.info("User trying to select subLookupName");
		Select selectSubName = new Select(selectSubLName);
		selectSubName.selectByVisibleText(subLName);
		Log.info("User selected Sub-LookUp name as" +subLName);
	}
	
	
	public void clickSubmit(){
		Log.info("User trying to click submit button");
		
		submitButton.click();
		Log.info("User clicked submit button");
	}
	
	public void EnterModifiedName(String NewName){
		subLName.clear();
		
		Log.info("User is trying to enter a modified name");
		
		subLName.sendKeys(NewName);
		Log.info("User entered New Name");
		
	}
	
	
	public void clickSubmit1(){
		Log.info("User trying to click submit1 button");
		
		submit1.click();
		Log.info("User clicked submit button");
	}
	
	
	public void clickDelete(){
		Log.info("User trying to click delete button");
		
		delete.click();
		Log.info("User clicked delete button");
	}
	
	
	public void clickConfirm(){
		Log.info("User trying to click confirm button");
		
		confirmButton.click();
		Log.info("User clicked confirm button");
	}

}
