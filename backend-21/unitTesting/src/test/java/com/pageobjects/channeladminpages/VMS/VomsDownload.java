package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class VomsDownload {

    WebDriver driver = null;
	public VomsDownload(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "batchesType" )
	private WebElement batchesType;
	
	@FindBy(name = "fromDate" )
	private WebElement fromDate;
	
	@FindBy(name = "toDate" )
	private WebElement toDate;
	
	@FindBy(name = "submitVoucherDown" )
	private WebElement submitVoucherDown;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectBatchType(String value){
		Log.info("Trying to Select Batch Type");
		Select select = new Select(batchesType);
		select.selectByValue(value);
		Log.info("Batch Type selected  successfully as:"+ value);
		}
	
	public void EnterFromDate(String value){
		Log.info("Trying to enter From Date");
		fromDate.sendKeys(value);
		Log.info("From Date entered  successfully as:"+ value);
		}
	
	public void EnterToDate(String value){
		Log.info("Trying to enter To Date");
		toDate.sendKeys(value);
		Log.info("To Date entered  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitVoucherDown.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public String getMessage(){
		String Message = null;
		Log.info("Trying to fetch Message");
		try {
		Message = message.getText();
		Log.info("Message fetched successfully as: " + Message);
		} catch (Exception e) {
			Log.info("No Message found");
		}
		return Message;
	}
	
	public String getErrorMessage() {
		String Message = null;
		Log.info("Trying to fetch Error Message");
		try {
		Message = errorMessage.getText();
		Log.info("Error Message fetched successfully as:"+ Message);
		}
		catch (org.openqa.selenium.NoSuchElementException e) {
			Log.info("Error Message Not Found");
		}
		return Message;
	}







}
