package com.pageobjects.superadminpages.grouprolemanagement;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class GroupRoleManagementPage1 {
	WebDriver driver;
	@ FindBy(name = "domainCode")
	private WebElement domain;

	@ FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@ FindBy(name = "submit")
	private WebElement submit;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;

	public GroupRoleManagementPage1(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void selectDomain(String Domain) throws InterruptedException {
		Select select1 = new Select(domain);
		select1.selectByVisibleText(Domain);
		Log.info("User selected Domain.");
	}

	public void selectCategory(String Category) throws InterruptedException {
		Select select1 = new Select(categoryCode);
		select1.selectByVisibleText(Category);
		Log.info("User selected Category.");
	}

	public void clickSubmitButton() throws InterruptedException {
		submit.click();
		Log.info("User clicked submit button.");
	}
	
	public void countDomain() throws InterruptedException {
		Select select1 = new Select(domain);
		List<WebElement> list = new ArrayList<WebElement>();
		
		list = select1.getOptions();
		System.out.println(list);
		Log.info("User selected Domain.");
	}
	
	
	public String getMessage(){
		
		String msg = message.getText();
		
		Log.info("The message fetched as:" +msg);
		
		return msg;
	}
}
