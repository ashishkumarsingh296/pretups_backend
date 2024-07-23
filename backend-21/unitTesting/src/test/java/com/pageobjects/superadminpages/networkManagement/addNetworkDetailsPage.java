package com.pageobjects.superadminpages.networkManagement;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class addNetworkDetailsPage {

	@FindBy (name = "networkCode")
	private WebElement  networkCode;

	@FindBy (name = "networkName")
	private WebElement  networkName;

	@FindBy (name = "networkShortName")
	private WebElement  networkShortName;

	@FindBy (name = "companyName")
	private WebElement  companyName;

	@FindBy (name = "reportHeaderName")
	private WebElement  reportHeaderName;

	@FindBy (name = "erpNetworkCode")
	private WebElement  erpNetworkCode;

	@FindBy (name = "language1Message")
	private WebElement  language1Message;

	@FindBy (name = "language2Message")
	private WebElement  language2Message;

	@FindBy (name = "countryPrefixCode")
	private WebElement  countryPrefixCode;

	@FindBy (name = "serviceSetID")
	private WebElement  serviceSetID;

	@FindBy (name = "save")
	private WebElement  saveButton;

	@FindBy (name = "reset")
	private WebElement  resetButton;




	WebDriver driver;

	public addNetworkDetailsPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}





	public void clickSave(){
		Log.info("User trying to click save button");

		saveButton.click();
		Log.info("User clicked save button");
	}



	public void enternetworkCode(String networkCode1){

		networkCode.sendKeys(networkCode1);
		Log.info("User entered networkCode as:" +networkCode1);
	}



	public void enternetworkName(String networkName1){

		networkName.sendKeys(networkName1);
		Log.info("User entered networkName as:" +networkName1);
	}


	public void enternetworkShortName(String ShortName){

		networkShortName.sendKeys(ShortName);
		Log.info("User entered ShortName as:" +ShortName);
	}
	
	
	public void enterCompanyName(String CompanyName){

		companyName.sendKeys(CompanyName);
		Log.info("User entered CompanyName as:" +CompanyName);
	}

	
	public void enterreportHeaderName(String HeaderName){

		reportHeaderName.sendKeys(HeaderName);
		Log.info("User entered reportHeaderName as:" +HeaderName);
	}
	
	
	public void entererpNetworkCode(String ErpCode){

		erpNetworkCode.sendKeys(ErpCode);
		Log.info("User entered ErpCode as:" +ErpCode);
	}
	
	public void entercountryPrefixCode(String PrefixCode){

		countryPrefixCode.sendKeys(PrefixCode);
		Log.info("User entered countryPrefixCode as:" +PrefixCode);
	}
	
public void selectserviceSetID(){
		String SetID="1";
		Select select = new Select(serviceSetID);
		select.selectByValue(SetID);
		
		Log.info("User selected Service set id ");
	}
	
	
}
