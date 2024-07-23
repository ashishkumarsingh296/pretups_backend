package com.pageobjects.superadminpages.interfaceManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ModifyInterfacePage {

	@FindBy(name ="language1Message")
	private WebElement language1Message;
	
	@FindBy(name ="language2Message")
	private WebElement language2Message;

	@FindBy(name = "addInterface")
	private WebElement addInterfaceButton;

	@FindBy(name = "confirm")
	private WebElement confirmButton;

	WebDriver driver;

	public ModifyInterfacePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);

	}


	public void enterlanguage1Message(){
		
		language1Message.sendKeys("RC Interface added");;
			Log.info("User entered message as:RC Interface modified");
		}
		
		
	public void enterlanguage2Message(){
		
		language2Message.sendKeys("Msg 2 RC Interface added");;
			Log.info("User entered message as:Msg 2 RC Interface modified");
		}
	

	
	public void clicksubmitButton(){
		
		addInterfaceButton.click();
	}
	
public void clickConfirmButton(){
		
		confirmButton.click();
	}

}
