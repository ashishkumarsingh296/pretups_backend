package com.pageobjects.superadminpages.modifyoperatorusers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class modifyOperatorpage1 {
	
	WebDriver driver = null;
	public modifyOperatorpage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "userName" )
	private WebElement userName;

	@FindBy(name = "categoryCode" )
	private WebElement categoryCode;
	
	@FindBy(name = "edit" )
	private WebElement submit;
	
	public void EnteruserName(String value){
	Log.info("Trying to enter  value in userName ");
	userName.sendKeys(value);
	Log.info("Data entered  successfully");
	}

	public void SelectcategoryCode(String value){
	Log.info("Trying to Select value  categoryCode ");
	Select select = new Select(categoryCode);
	select.selectByVisibleText(value);
	Log.info("Data selected  successfully");
	}

	public void ClickonSubmit(String value){
	Log.info("Trying to click on button  value in Submit ");
	submit.click();
	Log.info("Clicked on  Submit successfully");
	}

}
