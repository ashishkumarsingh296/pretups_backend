package com.pageobjects.networkadminpages.p2ptransferrule;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class ViewP2PTransferRule {

	WebDriver driver;

	public ViewP2PTransferRule(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public boolean checkTransferRule(String senderType, String senderServiceClass, String receiverType,
			String receiverServiceClass, String serviceType, String subService, String cardGroupSet, String requestBearer) {
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		WebElement element = null;
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + requestBearer + "')]//following-sibling::td[contains(text(),'" + senderType + "')]//following-sibling::td[contains(text(),'"
				+ senderServiceClass + "')]/following-sibling::td[contains(text(),'" + receiverType
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				//+ "')]/following-sibling::td[contains(text(),'" + status + "')]/following-sibling::td[contains(text(),'"
				+ "')]/following-sibling::td[contains(text(),'"
				+ subService + "')]/following-sibling::td[contains(text(),'" + serviceType + "')]/following-sibling::td[contains(text(),'" + cardGroupSet
				+ "')]";
		element = driver.findElement(By.xpath(xpath));
		elementDisplayed = element.isDisplayed();
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

}
