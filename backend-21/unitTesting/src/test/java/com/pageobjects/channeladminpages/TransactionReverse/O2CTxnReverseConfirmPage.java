package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class O2CTxnReverseConfirmPage {
	
	@FindBy (name = "revTrx")
	private WebElement confirm;
	
	@FindBy (name = "backButton")
	private WebElement backButton;
	
	@FindBy (name = "//ul/li")
	private WebElement message;
	
	
	WebDriver driver = null;

	public O2CTxnReverseConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickConfirm(){
		confirm.click();
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message is:" +msg);
		
		return msg;
		
	}
	
	

}
