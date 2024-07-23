package com.pageobjects.networkadminpages.geographicaldomain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

/**
 * @author Ayush Abhijeet
 * This class Contains the Page Objects for View Page of Geographical Domain Management
 **/

public class ViewGeographicalDomainPage {

	@ FindBy(name = "btnAdd")
	private WebElement btnAdd;

	@ FindBy(name = "btnModify")
	private WebElement btnModify;
	
	@ FindBy(name = "btnDel")
	private WebElement btnDel;

	@ FindBy(name = "btnBack")
	private WebElement btnBack;
	
	@FindBy(xpath = "//ul/li")
	WebElement UIMessage;

	@FindBy(xpath = "//ol/li")
	WebElement errorMessage;
	
	public String getActualMsg() {

		String UIMsg = null;
		String errorMsg = null;
		try{
		errorMsg = errorMessage.getText();
		}catch(Exception e){
			Log.info("No error Message found: "+e);
		}
		try{
		UIMsg = UIMessage.getText();
		}catch(Exception e){
			Log.info("No Success Message found: "+e);
		}
		if (errorMsg == null)
			return UIMsg;
		else
			return errorMsg;
	}
	
	WebDriver driver= null;

	public ViewGeographicalDomainPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	

	public void clickAddButton() {
		btnAdd.click();
		Log.info("User clicked Add Button.");
	}

	public void clickModifyButton() {
		btnModify.click();
		Log.info("User clicked Modify Button.");
	}
	
	public void clickDeleteButton() {
		btnDel.click();
		Log.info("User clicked Delete Button.");
	}
	
	public void clickBackButton() {
		btnBack.click();
		Log.info("User clicked Back Button.");
	}
	
	public void clickOnRadioButton(String domainName) {
		Log.info("Trying to click on xpath ");
		WebElement element = null;
		String xpath = "";	
		xpath = "//td[text()='"+domainName+"']/..//input[@type='radio']";
		element = driver.findElement(By.xpath(xpath));
		element.click();
		Log.info("Clicked on Xpath successfully");
	}

}
