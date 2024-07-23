package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class TransactionReverseSubCategories {
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=REVTRX001')]]")
	private WebElement ReverseC2CTxn;
	
	
	@FindBy(xpath = "//a[@href [contains(.,'pageCode=RVO2C001')]]")
	private WebElement ReverseO2CTxn;
	
	
	WebDriver driver = null;

	public TransactionReverseSubCategories(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void click_C2C_ReverseTxnLink(){
		Log.info("Trying to click Reverse C2C Transaction Link");
		ReverseC2CTxn.click();
		Log.info("User clicked Reverse C2C Transaction Link successfully ");
		
	}
	
	public void click_O2C_ReverseTxnLink(){
		Log.info("Trying to click Reverse O2C Transaction Link");
		ReverseO2CTxn.click();
		Log.info("User clicked Reverse O2C Transaction Link successfully ");
		
	}
	
	
	

}
