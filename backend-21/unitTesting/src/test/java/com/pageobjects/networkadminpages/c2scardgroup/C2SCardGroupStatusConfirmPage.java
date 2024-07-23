package com.pageobjects.networkadminpages.c2scardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2SCardGroupStatusConfirmPage {
	
	
	WebDriver driver;

	public C2SCardGroupStatusConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	@FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement message;
	
	
	


	public String getMessage() {
		String msg =message.getText();
		Log.info("Message: "+msg);
		return msg;
	}
}
