package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CreateBatchForVoucherDownloadPage2 {

    WebDriver driver = null;
	public CreateBatchForVoucherDownloadPage2(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "slabsListIndexed[0].productid" )
	private WebElement productid0;
	
	@FindBy(name = "submitOrderInit" )
	private WebElement submitOrderInit;
	
	@FindBy(name = "back" )
	private WebElement back;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void SelectProductID(String value){
		Log.info("Trying to Select Product ID");
		Select select = new Select(productid0);
		select.selectByVisibleText(value);
		Log.info("ProductID selected  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitOrderInit.click();
		Log.info("Clicked on Submit Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		back.click();
		Log.info("Clicked on Back Button successfully");
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
