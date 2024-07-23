package com.pageobjects.channeluserpages.changeNotificationLanguage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ChangeNotificationLanguageNextPageSpring {
	
	WebDriver driver = null;

	public ChangeNotificationLanguageNextPageSpring(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//select[@class='form-control']")
	private WebElement selectLanguage;
	
	@FindBy(xpath="//input[@onclick='checkclick()']")
	private WebElement selectCheck;
	
	public void selectchannelCategory(String msisdn, String language)
	{
		Log.info("Trying to select Language : "+language);
		WebElement element;
		String xpath = "//tr/td[contains(text(),'"+msisdn+"')]//..//select[@class='form-control']";
		element = driver.findElement(By.xpath(xpath));
		Select select = new Select(element);
		
		select.selectByVisibleText(language);
		Log.info("Language selected successfully");
	}
	
	
}
