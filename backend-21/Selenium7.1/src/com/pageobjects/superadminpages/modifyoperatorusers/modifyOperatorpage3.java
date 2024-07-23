package com.pageobjects.superadminpages.modifyoperatorusers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class modifyOperatorpage3 {
	
	WebDriver driver = null;
	public modifyOperatorpage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "confirm" )
	private WebElement confirm;



	public void clickconfirm(){
	Log.info("Trying to click on button  value in Submit ");
	confirm.click();
	Log.info("Clicked on  Submit successfully");
	}

}
