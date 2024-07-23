package com.pageobjects.superadminpages.networkManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class networkManagementPage {
	
	@FindBy(name = "add")
	private WebElement addButton;
	
	@FindBy(name = "edit")
	private WebElement editButton;
	
	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;
	
	
WebDriver driver;
	
	public networkManagementPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	public void clickAdd(){
		Log.info("User trying to click add button");
		
		addButton.click();
		Log.info("User clicked add button");
	}
	
	
	public void clickModify(){
		Log.info("User trying to click edit button");
		
		editButton.click();
		Log.info("User clicked edit button");
	}
	
	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}
	
	
	public void selectnetworkRadioButton(String networkCode) throws InterruptedException {
		String networkCode1= networkCode.toUpperCase();
		System.out.println(networkCode1);
		WebElement SelectNetwork = driver.findElement(By.xpath("//input[@value='"+networkCode1+"']"));
		SelectNetwork.click();
		System.out.println(networkCode);
		Log.info("User selected networkName" + networkCode);
	}
	

}
