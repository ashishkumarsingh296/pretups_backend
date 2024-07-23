package com.pageobjects.superadminpages.VMS;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.utils.Log;

public class VomsOrderApproval2Page3 {

    WebDriver driver = null;
	public VomsOrderApproval2Page3(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(name = "noOfVoucher" )
	private WebElement noOfVoucher;
	
	@FindBy(name = "remarks" )
	private WebElement remarks;
	
	@FindBy(name = "submitApprv1" )
	private WebElement submitApprv1;
	
	@FindBy(name = "backApprv1" )
	private WebElement backApprv1;
	
	@ FindBy(xpath = "//ul/li")
	private WebElement message;
	
	@FindBy(xpath = "//ol/li")
	private WebElement errorMessage;
	
	public void EnterQuantity(String value){
		Log.info("Trying to enter Quantity");
		noOfVoucher.clear();
		noOfVoucher.sendKeys(value);
		Log.info("Quantity entered  successfully as:"+ value);
		}
	
	public void EnterRemarks(String value){
		Log.info("Trying to enter Remarks");
		remarks.clear();
		remarks.sendKeys(value);
		Log.info("Remarks entered  successfully as:"+ value);
		}
	
	public void ClickonApprove(){
		Log.info("Trying to click on Approve Button");
		submitApprv1.click();
		Log.info("Clicked on Approve Button successfully");
		}
	
	public void ClickonBack(){
		Log.info("Trying to click on Back Button");
		backApprv1.click();
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
