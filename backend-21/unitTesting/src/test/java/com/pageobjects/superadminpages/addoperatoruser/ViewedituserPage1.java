package com.pageobjects.superadminpages.addoperatoruser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ViewedituserPage1 {
	WebDriver driver;

	public ViewedituserPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	@FindBy(name = "userType" )
	private WebElement userType;

	@FindBy(name = "domainCode" )
	private WebElement domainCode;

	@FindBy(name = "categoryCode" )
	private WebElement categoryCode;

	@FindBy(name = "submit1" )
	private WebElement submit1;

	public void SelectuserType(String value){
	Log.info("Trying to Select   userType ");
	Select select = new Select(userType);
	select.selectByVisibleText(value);
	Log.info("Data selected  successfully");
	}

	public void SelectdomainCode(String value){
	Log.info("Trying to Select   domainCode ");
	Select select = new Select(domainCode);
	select.selectByVisibleText(value);
	Log.info("Data selected  successfully");
	}

	public void SelectcategoryCode(String value){
	Log.info("Trying to Select   categoryCode ");
	Select select = new Select(categoryCode);
	select.selectByVisibleText(value);
	Log.info("Data selected  successfully");
	}

	public void ClickOnsubmit1(){
	Log.info("Trying to click on button  Submit ");
	submit1.click();
	Log.info("Clicked on  Submit successfully");
	}
}
