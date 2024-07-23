package com.pageobjects.networkadminpages.p2pcardgroup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class P2PCardGroupStatusConfirmPage {
	
	
	WebDriver driver;

	public P2PCardGroupStatusConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	//@FindBy(xpath = "//table/tbody/tr[2]/td[2]/ul/li")
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	
	


	public String getMessage() {
		String msg =message.getText();
		Log.info("Message: "+msg);
		return msg;
	}
}
