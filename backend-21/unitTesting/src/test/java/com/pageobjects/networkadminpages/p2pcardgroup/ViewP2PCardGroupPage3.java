package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewP2PCardGroupPage3 {
	
	@FindBy(xpath = "//table[2]/tbody/tr[2]/td/div")
	private WebElement message;
	
	
	WebDriver driver = null;
	
	public ViewP2PCardGroupPage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public String getMessage() {
		String msg =message.getText();
		Log.info("Message: "+msg);
		return msg;
	}
	
}
