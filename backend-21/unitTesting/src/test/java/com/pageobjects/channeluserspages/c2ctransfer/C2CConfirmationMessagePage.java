package com.pageobjects.channeluserspages.c2ctransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2CConfirmationMessagePage {
	
	@FindBy(xpath="//table/tbody/tr[2]/td[2]/ul/li")
	private WebElement confirmationMessage;
	
	
WebDriver driver= null;
	
	public C2CConfirmationMessagePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	
	public String c2cStatus(){		
		driver.switchTo().defaultContent();
		driver.switchTo().frame(0);
		
		String StatusText = driver.findElement(By.xpath("//table/tbody/tr[1]/td/table/tbody/tr[2]/td[2]/ul/li")).getText();
		Log.info("Status Text is:"+StatusText);
		//Assert.assertTrue("Text Found!",StatusText.contains("Transfer is successful"));
		return StatusText;
	
	}
}
