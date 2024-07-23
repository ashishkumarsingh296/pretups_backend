package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CTxnReverseConfirmPage {
	
	@FindBy (name = "saveRevTrx")
	private WebElement confirm;
	
	@FindBy (name = "cancelButton")
	private WebElement cancelButton;
	
	@FindBy (name = "//ul/li")
	private WebElement message;
	
	
	WebDriver driver = null;

	public C2CTxnReverseConfirmPage(WebDriver driver) {
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
