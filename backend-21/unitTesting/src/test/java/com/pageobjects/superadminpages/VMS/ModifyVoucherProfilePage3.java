package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import com.utils.Log;

public class ModifyVoucherProfilePage3 {

	WebDriver driver = null;
	public ModifyVoucherProfilePage3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "modifyProductSubmit" )
	private WebElement modifyProductSubmit;
	
	@FindBy(name = "modifyProductBack" )
	private WebElement modifyProductBack;
	
	@FindBy(name = "confirmModifyProduct" )
	private WebElement confirmModifyProduct;
	
	@FindBy(name = "voucherThreshold" )
	private WebElement voucherThreshold;
	
	@FindBy(name = "voucherGenerateQuantity" )
	private WebElement voucherGenerateQuantity;
	
	@FindBy(name = "productDelete" )
	private WebElement productDelete;
	
	@ FindBy(xpath = "//*[@type='radio' and @value='Y']")
	private WebElement autoVoucher;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	@FindBy(name = "status")
	private WebElement statusDropDown;
	
	public void selectStatus(String value) {
		Log.info("Trying to Select Status");
		try {
		Select select = new Select(statusDropDown);
		select.selectByValue(value);
		}
		catch (Exception ex) {
			Log.info("Status dropdown not found");
		}
		Log.info("Status selected  successfully as:"+ value);
	}
	
	public boolean ClickAutoGenerate(){
		Log.info("Trying to enable Auto Generate Button");
		try {
		autoVoucher.click();
		return true;
		}catch(Exception ex)
		{
			Log.info("Auto Generate Button not found");
			return false;
		}
		}
	
	public boolean isAutoVoucherDisplayed(){
		Log.info("Trying to check if Auto Voucher is getting displayed");
		if(autoVoucher.isDisplayed())
			return true;
		else
			return false;
		}
	
	public void EnterThreshold(String value){
		Log.info("Trying to enter Threshold");
		voucherThreshold.clear();
		voucherThreshold.sendKeys(value);
		Log.info("Threshold entered  successfully as:"+ value);
		}
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter Quantity");
		voucherGenerateQuantity.clear();
		voucherGenerateQuantity.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void ClickonModify(){
		Log.info("Trying to click on Modify Button");
		modifyProductSubmit.click();
		Log.info("Clicked on Modify Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		modifyProductBack.click();
		Log.info("Clicked on Back Button successfully");
		}
	
	public void ClickonDelete(){
		Log.info("Trying to click on Delete Button");
		productDelete.click();
		Log.info("Clicked on Delete Button successfully");
		}
	
	public void ClickonConfirm(){
		Log.info("Trying to click on Confirm Button");
		confirmModifyProduct.click();
		Log.info("Clicked on Confirm Button successfully");
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
	
	public void sendEnterKey() {
		
//		((JavascriptExecutor) driver).executeScript("window.confirm = function(msg) { return true; }");
		driver.switchTo().alert().accept();
	}

}
