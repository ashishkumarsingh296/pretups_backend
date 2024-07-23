package com.pageobjects.channeladminpages.VMS;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class CreateBatchForVoucherDownload {

    WebDriver driver = null;
	public CreateBatchForVoucherDownload(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "voucherType" )
	private WebElement voucherType;
	
	@FindBy(name = "slabsListIndexed[0].denomination" )
	private WebElement denomination0;
	
	@FindBy(name = "slabsListIndexed[0].batchID" )
	private WebElement batchID;
	
	@FindBy(name = "slabsListIndexed[0].quantity" )
	private WebElement quantity0;
	
	@FindBy(name = "slabsListIndexed[0].remarks" )
	private WebElement remarks0;
	
	@FindBy(name = "submitOrderInit1" )
	private WebElement submitOrderInit1;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public boolean isVoucherTypeAvailable(){
		Log.info("Trying to check if Voucher Type drop down available");
		try {
		if(voucherType.isDisplayed())
		return true;
		else
			return false;
		}
		catch(NoSuchElementException e) {
			return false;
		}
	}
	
	public void SelectVoucherType(String value){
		Log.info("Trying to Select Voucher Type");
		try {
		Select select = new Select(voucherType);
		select.selectByValue(value);
		Log.info("Voucher Type selected  successfully as:"+ value);
		}catch(Exception ex)
		{
			Log.info("Voucher Type drop down not found.");
		}
		
		}
	
	public void SelectCreateBatchFor(String value){
		Log.info("Trying to Select Create Batch For");
		WebElement element = driver.findElement(By.xpath("//*[@type='radio' and @value='"+value+"']"));
		element.click();
		Log.info("Batch Type selected  successfully as:"+ value);
		}
	
	public void SelectDenomination(String value){
		Log.info("Trying to Select Denomination");
		Select select = new Select(denomination0);
		select.selectByVisibleText(value);
		Log.info("Denomaination selected  successfully as:"+ value);
		}
	
	public void SelectBatchID(String value){
		Log.info("Trying to Select BatchID");
		Select select = new Select(batchID);
		select.selectByVisibleText(value);
		Log.info("Batch ID selected  successfully as:"+ value);
		}
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter quantity");
		quantity0.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void EnterRemarks(String value){
		Log.info("Trying to enter Remarks");
		remarks0.sendKeys(value);
		Log.info("Remarks entered  successfully as:"+ value);
		}
	
	public void ClickonSubmit(){
		Log.info("Trying to click on Submit Button");
		submitOrderInit1.click();
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
