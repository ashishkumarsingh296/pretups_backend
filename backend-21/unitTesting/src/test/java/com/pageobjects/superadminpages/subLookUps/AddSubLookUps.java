package com.pageobjects.superadminpages.subLookUps;

import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class AddSubLookUps {
	
	@FindBy(name = "lookupCode")
	private WebElement  lookUpName;
	
	@FindBy(name = "subLookupName")
	private WebElement subLookupName;
	
	@FindBy(name = "submit")
	private WebElement submitButton;
	
	@FindBy(name = "reset")
	private WebElement resetButton;
	
	
	
	@FindBy(name = "confirm")
	private WebElement confirmButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(name = "back")
	private WebElement backButton;
	
	WebDriver driver;
	
	public AddSubLookUps(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public int getLookUpCodeIndex() {
		Select select = new Select(lookUpName);
		ArrayList<WebElement> lookUpName = (ArrayList<WebElement>) select.getOptions();
		int size = lookUpName.size();
		System.out.println(size);
		Log.info("List of LookUpCodes." + size);
		return --size;
	}
	
	public String selectLookUpName(int index) {
		Select select = new Select(lookUpName);
		select.selectByIndex(index);
		String Name = driver.findElement(By.xpath("//select[@name='lookupCode']/option["+(index + 1)+"]")).getText();
		Log.info("User selected LookUpName: ["+Name+"]");
		
		return Name;
	}
	
	public String selectLookUpName1(String name) {
		Select select = new Select(lookUpName);
		select.selectByValue(name);
		//String Name = driver.findElement(By.xpath("//select[@name='lookupCode']/option["+(index + 1)+"]")).getText();
		Log.info("User selected LookUpName: ["+name+"]");
		
		return name;
	}
	
	public void entersubLookupName(String subLName){
		Log.info("User trying to enter subLookupName");
		subLookupName.sendKeys(subLName);
		Log.info("User entered Sub-LookUp name as" +subLName);
	}
	
	
	public void clickSubmit(){
		Log.info("User trying to click submit button");
		
		submitButton.click();
		Log.info("User clicked submit button");
	}
	
	public void clickReset(){
		Log.info("User trying to click reset button");
		
		resetButton.click();
		Log.info("User clicked reset button");
	}
	
	public String getMessage(){
		Log.info("User is trying to get message");
		String ActualMessage = message.getText();
		Log.info("Message fetched as:" +ActualMessage);
		
		return ActualMessage;
	}
	
	
	
	public void clickConfirm(){
		Log.info("User trying to click confirm button");
		
		confirmButton.click();
		Log.info("User clicked confirm button");
	}
	
	
	
	
	
	
	
	
	

}
