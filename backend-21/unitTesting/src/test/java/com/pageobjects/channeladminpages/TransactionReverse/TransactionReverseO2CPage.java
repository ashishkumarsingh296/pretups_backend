package com.pageobjects.channeladminpages.TransactionReverse;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class TransactionReverseO2CPage {

	@FindBy (name = "transferNum")
	private WebElement TransferNum;

	@FindBy(name = "userCode")
	private WebElement MobileNumber;

	@FindBy(name = "trfCatForUserCode")
	private WebElement transferCategory;

	@FindBy(name = "fromDateForUserCode")
	private WebElement FromDateForMSISDN;

	@FindBy(name = "toDateForUserCode")
	private WebElement ToDateForMSISDN;

	@FindBy (name = "geoDomainCode")
	private WebElement geoDomain;

	@FindBy (name = "channelDomain")
	private WebElement channelDomain;

	@FindBy (name = "productType")
	private WebElement productType;


	@FindBy (name = "categoryCode")
	private WebElement userCategory;

	@FindBy (name = "transferCategoryCode")
	private WebElement transferCategory2;



	@FindBy(name = "fromDate")
	private WebElement FromDate;

	@FindBy(name = "toDate")
	private WebElement ToDate;


	@FindBy(name = "submitButton")
	private WebElement submitButton;


	@FindBy(name = "resetbutton")
	private WebElement resetButton;
	
	@FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement Errormessage;
	
	WebDriver driver = null;

	public TransactionReverseO2CPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	
	public void EnterTransferNum(String transferId){
		TransferNum.sendKeys(transferId);
		
		Log.info("User entered transfer number as :" +transferId);
		
		
	}
	
	
	public void EnterMobileNumber(String mobileNum){
		MobileNumber.sendKeys(mobileNum);
		
		Log.info("User entered Mobile number as :" +mobileNum);
		
		
	}
	
	
	public void selectTransferCategory(String TransferCategory) {
		Log.info("Trying to select selectTransferCategory");
		try {
		Select select = new Select(this.transferCategory);
		select.selectByValue(TransferCategory);
		Log.info("TransferCategory selected successfully as: " + TransferCategory);
		}
		catch (Exception e) {
			Log.info("TransferCategory Dropdown not found");
		}
	}
	
	
	public void EnterFromDateForMSISDN(String date){
		FromDateForMSISDN.clear();
		FromDateForMSISDN.sendKeys(date);
		
		Log.info("User entered From Date as :" +date);
		
		
	}
	
	
	public void EnterToDateForMSISDN(String date){
		ToDateForMSISDN.clear();
		ToDateForMSISDN.sendKeys(date);
		
		Log.info("User entered To date as :" +date);
		
		
	}
	
	
	
	public void EnterFromDate(String date){
		FromDate.clear();
		FromDate.sendKeys(date);
		
		Log.info("User entered From Date as :" +date);
		
		
	}
	
	
	public void EnterToDate(String date){
		ToDate.clear();
		ToDate.sendKeys(date);
		
		Log.info("User entered To Date as :" +date);
		
		
	}
	
	
	
	public void selectProductType1(String product) {
		Log.info("Trying to select Product");
		try {
		Select select = new Select(productType);
		select.selectByValue(product);
		Log.info("User selected product type as:" +product);
		}
		catch (Exception e) {
			Log.info("Only one Product exists");
		}
	}
	
	public void selectGeographyDomain(String GeographyDomain) {
		
		Log.info("Trying to select Geography");
		try {
		Select select = new Select(geoDomain);
		select.selectByVisibleText(GeographyDomain);
		Log.info("User selected Geography Domain." +GeographyDomain);
		}
		catch (Exception e) {
			Log.info("Geography Dropdown not found");
		}
	}
	
	public void selectDomain(String Domain1) {
		Select select = new Select(channelDomain);
		select.selectByVisibleText(Domain1);
		Log.info("User selected Domain." +Domain1);
	}
	public void selectCategory(String Category) {
		Select select = new Select(userCategory);
		select.selectByVisibleText(Category);
		Log.info("User selected Category." +Category);
	}
	
	
	public void selectTransferCategory2(String TransferCategory) {
		Log.info("Trying to select selectTransferCategory");
		try {
		Select select = new Select(this.transferCategory2);
		select.selectByValue(TransferCategory);
		Log.info("TransferCategory selected successfully as: " + TransferCategory);
		}
		catch (Exception e) {
			Log.info("TransferCategory Dropdown not found");
		}
	}
	
	
	public void clickSubmit(){
		Log.info("User is trying to click submit button");
		submitButton.click();
		Log.info("User click submit button");
	}
	
	
	public String getMessage(){
		String msg = message.getText();
		Log.info("The message is:" +msg);
		
		return msg;
		
	}
	
	public String getErrorMessage(){
		String msg = Errormessage.getText();
		Log.info("The Error message is:" +msg);
		
		return msg;
		
	}
	
	

}


