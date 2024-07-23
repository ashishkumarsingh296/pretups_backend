package com.pageobjects.channeladminpages.restrictedList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class viewRestrictedList {


	@FindBy(name = "geoDomainCode")
	private WebElement geoDomain;

	@FindBy(name = "domainCode")
	private WebElement domainCode;

	@FindBy(name = "categoryCode")
	private WebElement categoryCode;

	@FindBy(name = "userName")
	private WebElement userName;

	@FindBy(name = "msisdn")
	private WebElement subscribersMSISDN;
	
	@FindBy(name = "fromDate")
	private WebElement fromDate;
	
		
	@FindBy(name = "toDate")
	private WebElement toDate;

	@FindBy(name = "submitBtn")
	private WebElement submit;

	@FindBy(xpath = "//table[2]/tbody/tr[2]/td/div")
	private WebElement message;

	@FindBy(xpath = "//ol/li")
	private WebElement ErrorMessage;
	
	@FindBy(xpath = "//ul/li")
	private WebElement msg;
	
	WebDriver driver = null;

	public viewRestrictedList(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}


	public void selectGeoDomain(String geo){
		Select select = new Select(geoDomain);
		select.selectByVisibleText(geo);
		Log.info("User selected Geographical Domain." +geo);
	}


	public void selectDomainCode(String domain){
		Select select = new Select(domainCode);
		select.selectByVisibleText(domain);
		Log.info("User selected Domain." +domain);
	}

	public void selectCategory(String category){
		Select select = new Select(categoryCode);
		select.selectByVisibleText(category);
		Log.info("User selected Category." +category);
	}


	public void enterUserName(String usr) {
		userName.sendKeys(usr);
		Log.info("User Name entered : "+usr);
	}

	public void clickSubmit(){
		submit.click();
	}


	public void enterMSISDN(String msisdn){

		subscribersMSISDN.sendKeys(msisdn);
	}

	
	public void enterFromDate(String Date){

		fromDate.sendKeys(Date);
	}
	
	public void enterToDate(String Date){
		toDate.sendKeys(Date);
	}
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message fetched as : " +msg);
		
		return msg;
	}
	

	public String getErrorMessage(){
		String msg = ErrorMessage.getText();
		Log.info("The error message fetched as : " +msg);
		
		return msg;
	}
	
	public String getMsg(){
		String message = msg.getText();
		Log.info("The message fetched as : " +msg);
		
		return message;
	}
	
	
	
}
