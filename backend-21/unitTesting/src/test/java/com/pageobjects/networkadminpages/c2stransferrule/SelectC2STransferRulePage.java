package com.pageobjects.networkadminpages.c2stransferrule;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class SelectC2STransferRulePage {
	WebDriver driver;
	
	public SelectC2STransferRulePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	@FindBy(name = "orderType")
	private WebElement orderType;
	
	@FindBy(name = "submit")
	private WebElement submit;

	public void selectOrderBy(String orderByValue) {
		try{
		Select select = new Select(orderType);
		select.selectByVisibleText(orderByValue);
		Log.info("User selected Order By: "+orderByValue);
		}catch(Exception e){
			Log.info("Couldn't find order by drop down");
		}
	}

	public void clickOnSubmitButton() {
		try{
		Log.info("Trying to click on Submit");
		submit.click();
		Log.info("Clicked on Submit successfully");
		}catch(Exception e){
			Log.info("Couldn't find Submit button on Order by page");
		}
	}
}
