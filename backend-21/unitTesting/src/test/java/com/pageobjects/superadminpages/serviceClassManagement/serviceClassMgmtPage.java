package com.pageobjects.superadminpages.serviceClassManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class serviceClassMgmtPage {


	@FindBy(name = "interfaceCategory")
	private WebElement InterfaceCatergory;

	@FindBy(name = "interfaceType")
	private WebElement interfaceType;

	@FindBy(name = "interfaceCode")
	private WebElement interfaceCode;

	@FindBy(name = "submit")
	private WebElement submitButton;

	@FindBy(xpath = "//ul/li")	
	private WebElement actualMessage;


	WebDriver driver;

	public serviceClassMgmtPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public String selectInterfaceCatergory(String IntCategory) {
		Select select = new Select(InterfaceCatergory);
		select.selectByValue(IntCategory);

		Log.info("User selected Interface Category: ["+IntCategory+"]");

		return IntCategory;
	}

	public void selectInterfaceCatergory1(int index) {
		Select select = new Select(InterfaceCatergory);
		select.selectByIndex(index);

		Log.info("User selected Interface Category " +InterfaceCatergory.getText());


	}

	public void selectInterfaceType(int index){

		Select select = new Select(interfaceType);
		select.selectByIndex(index);

		Log.info("User selected LookUpName: ["+interfaceType.getText()+"]");
	}



	public String selectInterfaceType(String IntType) {
		Select select = new Select(interfaceType);
		select.selectByValue(IntType);

		Log.info("User selected Interface Type: ["+IntType+"]");

		return IntType;
	}


	public void selectInterface(String Interface){
		Log.info("Trying to select Interface");
		Select select = new Select(interfaceCode);
		select.selectByVisibleText(Interface);
		Log.info("User selected Interface code as:" +Interface);
	}

	public void clickSubmit(){
		Log.info("User trying to click submit button");

		submitButton.click();
		Log.info("User clicked submit button");
	}

	public String getMsg(){
		String msg = actualMessage.getText();
		Log.info("Actual Message fetched as " +msg);
		return msg;
	}



}
