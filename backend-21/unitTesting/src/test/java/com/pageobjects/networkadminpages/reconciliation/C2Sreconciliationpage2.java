package com.pageobjects.networkadminpages.reconciliation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class C2Sreconciliationpage2 {

	WebDriver driver;

	public C2Sreconciliationpage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "btnSubmit")
	private WebElement btnSubmit;

	@FindBy(name = "btnBack")
	private WebElement btnBack;
	
	public boolean checkTransaction(String validationValue) {
		boolean elementDisplayed = false;
		Log.info("Trying to check transactions. ");
		WebElement element = null;
		String xpath = "";
		try{xpath="//td/input[@type='radio']";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed = element.isDisplayed();}
		catch(Exception e){xpath = "//td[contains(text(),'"+validationValue+"')]";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed=false;}
		
		return elementDisplayed;
	}
	
	public String getTotalEnteries() {
		String xpath = "";
		xpath = "//div[contains(text(),'of') and contains(text(),'-')]";
		String str = driver.findElement(By.xpath(xpath)).getText();
		str = str.trim();
		String[] wordList = str.split("\\s+");
		String lastWord = wordList[wordList.length-1];
		return lastWord;
	}
	
	public void getLastPage(int enteries)
	{
		WebElement element = null;
		String xpath = "";
		xpath = "//a/b[contains(text(),'[Next ]')]/ancestor::a/preceding-sibling::a[1]";
		element = driver.findElement(By.xpath(xpath));
		element.click();
	}
	
	public boolean isNextDisplayed()
	{
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		try{
			String xpath = "";
			xpath = "//a/b[contains(text(),'[Next ]')]";
			elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
			return elementDisplayed;
		}catch(Exception e)
		{
			elementDisplayed = false;
		}
		return elementDisplayed;
	}
	public void clickonradioButton(String transactionID) {
		Log.info("Trying to select transaction "+transactionID);
		WebElement element = null;
		String xpath = "";	
		xpath = "//td[contains(text(),'"+transactionID+"')]/preceding::input[@type='radio']";
		
		System.out.println(xpath);
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Transaction selected successfully");
	}
	
	public void ClickOnbtnSubmit() {
		Log.info("Trying to click on button  Submit ");
		btnSubmit.click();
		Log.info("Clicked on Submit successfully");
	}


	public void ClickOnbtnBack() {
		Log.info("Trying to click on button  Back ");
		btnBack.click();
		Log.info("Clicked on  Back successfully");
	}
	

}
