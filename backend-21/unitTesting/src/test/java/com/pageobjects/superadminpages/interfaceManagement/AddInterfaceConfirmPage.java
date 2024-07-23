package com.pageobjects.superadminpages.interfaceManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AddInterfaceConfirmPage {
	
	@FindBy(name = "confirm")
	private WebElement confirmButton;
	
	@FindBy(name = "cancel")
	private WebElement cancel;
	
	
WebDriver driver;
	
	public AddInterfaceConfirmPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void clickConfirm(){
		
		confirmButton.click();
	}
	

}
