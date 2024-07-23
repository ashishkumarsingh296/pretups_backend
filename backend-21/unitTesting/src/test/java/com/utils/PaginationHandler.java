package com.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PaginationHandler {

	public void getToLastPage(WebDriver driver) {
		Log.info("Entered PaginationHandler@getToLastPage()");
		int enteriesPerPage = Integer.parseInt(_masterVO.getProperty("enteriesPerPage"));
		boolean isNextExists=isNextDisplayed(driver);
		
		while(isNextExists == true)	{
			getLastPage(enteriesPerPage, driver);
			isNextExists = isNextDisplayed(driver);
		}
		Log.info("Reached to Last page successfully, Exiting getToLastPage()");
	}
	
	public boolean checkTransferRule(String requestGatewayCode, String domain, String category,
			String grade, String receiverType, String receiverServiceClass, String status, String serviceType, String subService, WebDriver driver) {
		boolean elementDisplayed = false;
		Log.info("Trying to check xpath ");
		String xpath = "";
		xpath = "//tr/td[contains(text(),'" + requestGatewayCode + "')]//following-sibling::td[contains(text(),'"
				+ domain + "')]/following-sibling::td[contains(text(),'" + category
				+ "')]/following-sibling::td[contains(text(),'"
				+ grade + "')]/following-sibling::td[contains(text(),'" + receiverType
				+ "')]/following-sibling::td[contains(text(),'" + receiverServiceClass
				+ "')]/following-sibling::td[contains(text(),'" + status + "')]/following-sibling::td[contains(text(),'"
				+ serviceType + "')]/following-sibling::td[contains(text(),'" + subService
				+ "')]";
		elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
		return elementDisplayed;	
	}
	
	public String getTotalEnteries(WebDriver driver) {
		String xpath = "";
		xpath = "//div[contains(text(),'of') and contains(text(),'-')]";
		String str = driver.findElement(By.xpath(xpath)).getText();
		str = str.trim();
		String[] wordList = str.split("\\s+");
		String lastWord = wordList[wordList.length-1];
		return lastWord;
	}
	
	public void getLastPage(int enteries, WebDriver driver) {
		WebElement element = null;
		String xpath = "";
		xpath = "//a/b[contains(text(),'[Next ]')]/ancestor::a/preceding-sibling::a[1]";
		element = driver.findElement(By.xpath(xpath));
		element.click();
	}
	
	public boolean isNextDisplayed(WebDriver driver) {
		boolean elementDisplayed = false;
		try {
			String xpath = "";
			xpath = "//a/b[contains(text(),'[Next ]')]";
			elementDisplayed = driver.findElement(By.xpath(xpath)).isDisplayed();
			return elementDisplayed;
		} catch(Exception e) {
			elementDisplayed = false;
		}
		return elementDisplayed;
	}
}
