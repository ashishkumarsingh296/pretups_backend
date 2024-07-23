package com.pageobjects.superadminpages.UserStatusConfiguration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class UserStatusConfigurationPage {

	@FindBy(name = "gatewayType")
	private WebElement gatewayType;

	@FindBy(name = "userType")
	private WebElement userType;

	@FindBy(name = "domainCode")
	private WebElement domainCode;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "Add")
	private WebElement Add;

	@FindBy(name = "Modify")
	private WebElement Modify;

	@FindBy(name = "View")
	private WebElement View;
	
	@FindBy(xpath = "//ul/li")
	private WebElement actualMessage;

	WebDriver driver;

	public UserStatusConfigurationPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}



	public void SelectGateway(String gatewayType1){

		Log.info("Trying to select gateway");
		Select gateway = new Select(gatewayType);
		gateway.selectByValue(gatewayType1);
		Log.info("User selected gatewayType as:" +gatewayType1);
	}


	public void SelectUserType(String type){

		Log.info("Trying to select UserType");
		Select user = new Select(userType);
		user.selectByValue(type);
		Log.info("User selected UserType as:" +type);
	}



	public void SelectDomain(String dom){

		Log.info("Trying to select Domain Code");
		Select domain = new Select(domainCode);
		domain.selectByVisibleText(dom);
		Log.info("User selected Domain Code as:" +dom);
	}


	public void SelectCategory(String catCode){

		Log.info("Trying to select category Code");
		Select category = new Select(categoryCode);
		category.selectByVisibleText(catCode);
		Log.info("User selected category Code as:" +catCode);
	}


	public void clickAdd(){
		Add.click();
	}

	public void clickModify(){

		Modify.click();

	}

	public void clickView(){
		View.click();

	}
	
	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}



}


